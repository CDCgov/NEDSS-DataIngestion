package gov.cdc.dataprocessing.service.implementation.manager;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.DpConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingDBException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.NbsInterfaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsInterfaceJdbcRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.data_extraction.IDataExtractionService;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerAggregationService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IDecisionSupportService;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import gov.cdc.dataprocessing.service.model.wds.WdsTrackerView;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.ManagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Service
@Slf4j

public class ManagerService implements IManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);
    @Value("${service.timezone}")
    private String tz = "UTC";
    private final ICacheApiService cacheApiService;
    private final IObservationService observationService;

    private final IEdxLogService edxLogService;

    private final IDataExtractionService dataExtractionService;

    private final NbsInterfaceRepository nbsInterfaceRepository;

    private final IDecisionSupportService decisionSupportService;

    private final ManagerUtil managerUtil;

    private final IManagerAggregationService managerAggregationService;
    private final LabService labService;

    private final NbsInterfaceJdbcRepository nbsInterfaceJdbcRepository;

    private final KafkaManagerProducer kafkaManagerProducer;

    @Autowired
    public ManagerService(@Lazy ICacheApiService cacheApiService,
                          IObservationService observationService,
                          IEdxLogService edxLogService,
                          IDataExtractionService dataExtractionService,
                          NbsInterfaceRepository nbsInterfaceRepository,
                          IDecisionSupportService decisionSupportService,
                          ManagerUtil managerUtil,
                          IManagerAggregationService managerAggregationService,
                          LabService labService,
                          NbsInterfaceJdbcRepository nbsInterfaceJdbcRepository,
                          KafkaManagerProducer kafkaManagerProducer)
    {
        this.cacheApiService = cacheApiService;
        this.observationService = observationService;
        this.edxLogService = edxLogService;
        this.dataExtractionService = dataExtractionService;
        this.nbsInterfaceRepository = nbsInterfaceRepository;
        this.decisionSupportService = decisionSupportService;
        this.managerUtil = managerUtil;
        this.managerAggregationService = managerAggregationService;
        this.labService = labService;
        this.nbsInterfaceJdbcRepository = nbsInterfaceJdbcRepository;
        this.kafkaManagerProducer = kafkaManagerProducer;
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    @Transactional()
    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {DataProcessingDBException.class}
    )
    public PublicHealthCaseFlowContainer processingELR(Integer data) throws EdxLogException, DataProcessingDBException {
        logger.debug("Interface Id: {}", data);
        NbsInterfaceModel nbsInterfaceModel = null;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String detailedMsg = "";
        boolean dltLockError = false;
        try
        {

            // Load interface model
            nbsInterfaceModel = nbsInterfaceJdbcRepository.getNbsInterfaceByUid(data);
            if (nbsInterfaceModel == null) {
                throw new DataProcessingException("NBS Interface Not Exist");
            }

            // Prepare DTO
            edxLabInformationDto.setStatus(NbsInterfaceStatus.Success);
            edxLabInformationDto.setUserName(AuthUtil.authUser.getUserId());
            edxLabInformationDto.setNbsInterfaceUid(nbsInterfaceModel.getNbsInterfaceUid());


            LabResultProxyContainer labResultProxyContainer = dataExtractionService.parsingDataToObject(nbsInterfaceModel, edxLabInformationDto);
            edxLabInformationDto.setLabResultProxyContainer(labResultProxyContainer);

            if (nbsInterfaceModel.getObservationUid() != null && nbsInterfaceModel.getObservationUid() > 0) {
                edxLabInformationDto.setRootObserbationUid(nbsInterfaceModel.getObservationUid());
            }

            // Observation matching and service aggregation
            edxLabInformationDto = managerAggregationService.processingObservationMatching(edxLabInformationDto, labResultProxyContainer, null);
            labResultProxyContainer.setMatchedObservationFound(edxLabInformationDto.isMatchedObservationFound());
            managerAggregationService.serviceAggregation(labResultProxyContainer, edxLabInformationDto);

            // Set update UID if matched
            if (edxLabInformationDto.isLabIsUpdateDRRQ() || edxLabInformationDto.isLabIsUpdateDRSA()) {
                managerUtil.setPersonUIDOnUpdate(null, labResultProxyContainer);
            }

            // Process observation
            ObservationDto observationDto = observationService.processingLabResultContainer(labResultProxyContainer);
            edxLabInformationDto.setLabResultProxyContainer(labResultProxyContainer);
            edxLabInformationDto.setLocalId(observationDto.getLocalId());
            edxLabInformationDto.getEdxActivityLogDto().setBusinessObjLocalId(observationDto.getLocalId());
            edxLabInformationDto.setRootObserbationUid(observationDto.getObservationUid());

            // Populate program area & jurisdiction from cache
            if (observationDto.getProgAreaCd() != null && cacheApiService.getSrteCacheBool(ObjectName.PROGRAM_AREA_CODES.name(), observationDto.getProgAreaCd())) {
                edxLabInformationDto.setProgramAreaName(cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES.name(), observationDto.getProgAreaCd()));
            }

            if (observationDto.getJurisdictionCd() != null && cacheApiService.getSrteCacheBool(ObjectName.JURISDICTION_CODES.name(), observationDto.getJurisdictionCd())) {
                edxLabInformationDto.setJurisdictionName(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODES.name(), observationDto.getJurisdictionCd()));
            }

            if (edxLabInformationDto.isLabIsCreate()) {
                edxLabInformationDto.setLabIsCreateSuccess(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_2);
            }

            if (edxLabInformationDto.isLabIsCreateSuccess() && (edxLabInformationDto.getProgramAreaName() == null || edxLabInformationDto.getJurisdictionName() == null)) {
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_1);
            }

            nbsInterfaceModel.setObservationUid(observationDto.getObservationUid().intValue());

            PublicHealthCaseFlowContainer phcContainer = new PublicHealthCaseFlowContainer();
            phcContainer.setLabResultProxyContainer(labResultProxyContainer);
            phcContainer.setEdxLabInformationDto(edxLabInformationDto);
            phcContainer.setObservationDto(observationDto);
            phcContainer.setNbsInterfaceId(nbsInterfaceModel.getNbsInterfaceUid());
            phcContainer.setNbsInterfaceModel(nbsInterfaceModel);

            return phcContainer;
        }
        catch (Exception e)
        {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (e instanceof CannotAcquireLockException ||
                    e instanceof QueryTimeoutException ||
                    e instanceof TransientDataAccessException ||
                    e instanceof DataAccessException ||
                    rootCause instanceof java.sql.SQLException) {
                log.warn("DB-related exception caught: {}", e.getMessage(), e);
                if (e instanceof CannotAcquireLockException) {
                    dltLockError = true;
                }
                else {
                    throw new DataProcessingDBException(e.getMessage(), e);
                }
            }
            else
            {
                detailedMsg = handleProcessingELRError(e, edxLabInformationDto, nbsInterfaceModel);
            }
        }
        finally
        {
            if (dltLockError)
            {
                composeDlt(String.valueOf(data));
            }
            edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto);
            edxLogService.addActivityDetailLogs(edxLabInformationDto, detailedMsg);
            edxLogService.saveEdxActivityLogs(edxLabInformationDto.getEdxActivityLogDto());
        }

        return null;
    }

    @Transactional()
    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {DataProcessingDBException.class}
    )
    @SuppressWarnings("java:S1135")
    public void handlingWdsAndLab(PublicHealthCaseFlowContainer phcContainer) throws DataProcessingException, DataProcessingDBException, EdxLogException {
        PublicHealthCaseFlowContainer wds;
        boolean dltLockError = false;
        try
        {
            wds = initiatingInvestigationAndPublicHealthCase(phcContainer);
            initiatingLabProcessing(wds);
        }
        catch (Exception e)
        {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (e instanceof CannotAcquireLockException ||
                    e instanceof QueryTimeoutException ||
                    e instanceof TransientDataAccessException ||
                    e instanceof DataAccessException ||
                    rootCause instanceof java.sql.SQLException) {

                log.warn("DB-related exception caught: {}", e.getMessage(), e);
                if (e instanceof CannotAcquireLockException) {
                    dltLockError = true;
                }
                else {
                    throw new DataProcessingDBException(e.getMessage(), e);
                }
            }
            else
            {
                // TODO SEND TO DLT QUEUE HERE && ISOLATE LOCK EXCEPTION and push it to sequence queue
                phcContainer.getNbsInterfaceModel().setRecordStatusCd(DpConstant.DP_FAILURE_STEP_2);
                phcContainer.getNbsInterfaceModel().setRecordStatusTime(getCurrentTimeStamp(tz));
                nbsInterfaceRepository.save(phcContainer.getNbsInterfaceModel());
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        finally
        {
            if (dltLockError) {
                var localGson = new Gson();
                composeDlt(localGson.toJson(phcContainer));
            }
            else
            {
                edxLogService.updateActivityLogDT(phcContainer.getNbsInterfaceModel(), phcContainer.getEdxLabInformationDto());
                edxLogService.addActivityDetailLogs(phcContainer.getEdxLabInformationDto(), "");
                edxLogService.saveEdxActivityLogs(phcContainer.getEdxLabInformationDto().getEdxActivityLogDto());
            }
        }
    }

    public void updateNbsInterfaceStatus(List<Integer> ids) {
        nbsInterfaceJdbcRepository.updateRecordStatusToRtiProcess(ids);
    }

    protected void composeDlt(String message) {
        kafkaManagerProducer.sendDltForLocking(message);
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    protected PublicHealthCaseFlowContainer initiatingInvestigationAndPublicHealthCase(PublicHealthCaseFlowContainer phcContainer) throws DataProcessingException {
        EdxLabInformationDto edxDto = phcContainer.getEdxLabInformationDto();
        ObservationDto observationDto = phcContainer.getObservationDto();
        LabResultProxyContainer labProxy = phcContainer.getLabResultProxyContainer();
        NbsInterfaceModel interfaceModel = phcContainer.getNbsInterfaceModel();

        if (edxDto.isLabIsUpdateDRRQ()) {
            edxDto.setLabIsUpdateSuccess(true);
            edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_15);
        } else if (edxDto.isLabIsUpdateDRSA()) {
            edxDto.setLabIsUpdateSuccess(true);
            edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_22);
        }

        decisionSupportService.validateProxyContainer(labProxy, edxDto);

        WdsTrackerView tracker = new WdsTrackerView();
        tracker.setWdsReport(edxDto.getWdsReports());

        // Extract patient info
        for (var person : labProxy.getThePersonContainerCollection()) {
            var dto = person.getThePersonDto();
            if ("PAT".equals(dto.getCd())) {
                tracker.setPatientUid(dto.getUid());
                tracker.setPatientParentUid(dto.getPersonParentUid());
                tracker.setPatientFirstName(dto.getFirstNm());
                tracker.setPatientLastName(dto.getLastNm());
                break;
            }
        }

        phcContainer.setNbsInterfaceId(interfaceModel.getNbsInterfaceUid());
        phcContainer.setLabResultProxyContainer(labProxy);
        phcContainer.setEdxLabInformationDto(edxDto);
        phcContainer.setObservationDto(observationDto);
        phcContainer.setWdsTrackerView(tracker);
        phcContainer.setNbsInterfaceModel(interfaceModel);

        // Set public health case in tracker view
        if (edxDto.getPageActContainer() != null)
        {
            tracker.setPublicHealthCase(edxDto.getPageActContainer().getPublicHealthCaseContainer().getThePublicHealthCaseDto());
        }
        else if (edxDto.getPamContainer() != null)
        {
            tracker.setPublicHealthCase(edxDto.getPamContainer().getPublicHealthCaseContainer().getThePublicHealthCaseDto());
        }

        return phcContainer;
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    protected void initiatingLabProcessing(PublicHealthCaseFlowContainer phcContainer) throws DataProcessingException {
        NbsInterfaceModel interfaceModel = phcContainer.getNbsInterfaceModel();
        EdxLabInformationDto edxDto = phcContainer.getEdxLabInformationDto();
        ObservationDto obsDto = phcContainer.getObservationDto();

        PageActProxyContainer pageAct = edxDto.getPageActContainer();
        PamProxyContainer pamProxy = edxDto.getPamContainer();
        PublicHealthCaseContainer phcContainerModel;
        Long phcUid;

        String action = edxDto.getAction();
        if (DecisionSupportConstants.MARK_AS_REVIEWED.equalsIgnoreCase(action))
        {
            labService.handleMarkAsReviewed(obsDto, edxDto);
        }
        else if (pageAct != null || pamProxy != null)
        {
            if (pageAct != null) {
                phcContainerModel = pageAct.getPublicHealthCaseContainer();
            } else {
                phcContainerModel = pamProxy.getPublicHealthCaseContainer();
            }

            if (phcContainerModel.getErrorText() != null) {
                requiredFieldError(phcContainerModel.getErrorText(), edxDto);
            }

            if (pageAct != null && obsDto.getJurisdictionCd() != null && obsDto.getProgAreaCd() != null) {
                phcUid = labService.handlePageContainer(pageAct, edxDto);
                pageAct.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setPublicHealthCaseUid(phcUid);
            } else if (pamProxy != null && obsDto.getJurisdictionCd() != null && obsDto.getProgAreaCd() != null) {
                phcUid = labService.handlePamContainer(pamProxy, edxDto);
                pamProxy.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setPublicHealthCaseUid(phcUid);
            } else {
                phcUid = null;
            }

            if (phcUid != null) {
                edxDto.setInvestigationSuccessfullyCreated(true);
                edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_3);
                edxDto.setPublicHealthCaseUid(phcUid);
                edxDto.setLabAssociatedToInv(true);
            }

            if (DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE.equalsIgnoreCase(action)) {
                labService.handleNndNotification(phcContainerModel, edxDto);
            }
        }

        interfaceModel.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_3);
        interfaceModel.setRecordStatusTime(getCurrentTimeStamp(tz));
        nbsInterfaceRepository.save(interfaceModel);
        logger.debug("Completed");
    }

    private String handleProcessingELRError(Exception e, EdxLabInformationDto dto, NbsInterfaceModel model) {
        logException(e);
        updateModelStatus(model);

        String accessionNumber = "Accession Number:" + dto.getFillerNumber();
        dto.setStatus(NbsInterfaceStatus.Failure);
        dto.setSystemException(true);

        try {
            processKnownErrorPatterns(e, dto);
            processFieldErrors(dto);
            return buildDetailedErrorMessage(e, dto, accessionNumber);
        } catch (Exception ex) {
            logger.error("Exception while formatting detailed error: {}", ex.getMessage());
        }

        return "";
    }

    private void logException(Exception e) {
        e.printStackTrace();
        logger.error("DP ERROR: {}", e.getMessage());
    }

    private void updateModelStatus(NbsInterfaceModel model) {
        if (model != null) {
            model.setRecordStatusCd(DpConstant.DP_FAILURE_STEP_1);
            model.setRecordStatusTime(getCurrentTimeStamp(tz));
            nbsInterfaceRepository.save(model);
        }
    }

    private void processKnownErrorPatterns(Exception e, EdxLabInformationDto dto) {
        if (e.toString().contains("Invalid XML")) {
            dto.setInvalidXML(true);
            dto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
        }

        boolean hasContainers = dto.getPageActContainer() != null || dto.getPamContainer() != null;

        if (hasContainers && !dto.isInvestigationSuccessfullyCreated()) {
            dto.setErrorText(dto.isInvestigationMissingFields()
                    ? EdxELRConstant.ELR_MASTER_LOG_ID_5
                    : EdxELRConstant.ELR_MASTER_LOG_ID_9);
        } else if (hasContainers) {
            dto.setErrorText(dto.isNotificationMissingFields()
                    ? EdxELRConstant.ELR_MASTER_LOG_ID_8
                    : EdxELRConstant.ELR_MASTER_LOG_ID_10);
        }

        if (dto.isInvestigationMissingFields() || dto.isNotificationMissingFields()
                || EdxELRConstant.ELR_MASTER_LOG_ID_10.equals(dto.getErrorText())) {
            dto.setSystemException(false);
        }
    }

    private void processFieldErrors(EdxLabInformationDto dto) {
        if (dto.isReflexResultedTestCdMissing() || dto.isResultedTestNameMissing()
                || dto.isOrderTestNameMissing() || dto.isReasonforStudyCdMissing()) {
            String msg = dto.getErrorText();
            if (msg != null && msg.contains("XMLElementName: ")) {
                String segment = msg.substring(msg.indexOf("XMLElementName: ") + 16);
                dto.setErrorText("Blank identifiers in segments " + segment);
            }
        }
    }

    private String buildDetailedErrorMessage(Exception e, EdxLabInformationDto dto, String accessionNumber) {
        if (dto.getErrorText() != null) return "";

        String msg = e.getMessage();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        if (msg.contains(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG) || msg.contains(EdxELRConstant.ORACLE_FIELD_TRUNCATION_ERROR_MSG)) {
            dto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_18);
            dto.setFieldTruncationError(true);
            dto.setSystemException(false);
            dto.setPersonParentUid(0);
            dto.setEthnicityCodeTranslated(true);

            String trace = sw.toString();
            String tableName = "Unknown";
            if (trace.contains("Table Name : ")) {
                String[] parts = trace.substring(trace.indexOf("Table Name : ") + 13).split(" ");
                if (parts.length > 0 && parts[0] != null && !parts[0].isBlank()) {
                    tableName = parts[0];
                }
            }
            return truncateMessage(String.format("SQLException while inserting into %s %s %s", tableName, accessionNumber, trace));
        }

        if (msg.contains(EdxELRConstant.DATE_VALIDATION)) {
            dto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_20);
            dto.setInvalidDateError(true);
            dto.setSystemException(false);
            dto.setPersonParentUid(0);
            dto.setEthnicityCodeTranslated(true);

            String extracted = extractDateValidation(msg);
            return truncateMessage(String.format("%s %s %s", extracted, accessionNumber, msg));
        }

        dto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_16);
        dto.setPersonParentUid(0);
        dto.setEthnicityCodeTranslated(true);
        return truncateMessage(accessionNumber + "\n" + sw);
    }

    private String extractDateValidation(String msg) {
        int start = msg.indexOf(EdxELRConstant.DATE_VALIDATION);
        int end = msg.indexOf(EdxELRConstant.DATE_VALIDATION_END_DELIMITER1, start);
        return (start >= 0 && end > start) ? msg.substring(start, end) : msg;
    }

    private String truncateMessage(String message) {
        return message.length() > 2000 ? message.substring(0, 2000) : message;
    }

    private void requiredFieldError(String errorTxt, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        if (errorTxt != null) {
            edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_5);
            if (edxLabInformationDT.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails() == null)
            {
                edxLabInformationDT.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(
                        new ArrayList<>());
            }

            edxLabInformationDT.setInvestigationMissingFields(true);
            throw new DataProcessingException("MISSING REQUIRED FIELDS: "+errorTxt);
        }
    }

}
