package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.cache.PropertyUtilCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.DpConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Service
@Slf4j
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
                          KafkaManagerProducer kafkaManagerProducer) {
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
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public PublicHealthCaseFlowContainer processingELR(Integer data) throws EdxLogException {
        logger.debug("Interface Id: {}", data);
        NbsInterfaceModel nbsInterfaceModel = null;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String detailedMsg = "";
        boolean kafkaFailedCheck = false;
        try {

            // Load interface model
            nbsInterfaceModel = nbsInterfaceJdbcRepository.getNbsInterfaceByUid(data);
            if (nbsInterfaceModel == null) {
                throw new DataProcessingException("NBS Interface Not Exist");
            }

            // Short-circuit if already processed
            synchronized (PropertyUtilCache.class) {
                String status = nbsInterfaceModel.getRecordStatusCd();
                if (status != null && status.toUpperCase().contains("SUCCESS")) {
                    if (PropertyUtilCache.kafkaFailedCheckStep1 == 100000) {
                        PropertyUtilCache.kafkaFailedCheckStep1 = 0;
                    }
                    ++PropertyUtilCache.kafkaFailedCheckStep1;
                    logger.info("Kafka failed check : {}", PropertyUtilCache.kafkaFailedCheckStep1);
                    kafkaFailedCheck = true;
                    return null;
                }
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

//            nbsInterfaceModel.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_1);
//            nbsInterfaceModel.setRecordStatusTime(getCurrentTimeStamp(tz));
//            nbsInterfaceRepository.save(nbsInterfaceModel);

            return phcContainer;
        }
        catch (Exception e)
        {
            if (e instanceof CannotAcquireLockException || e instanceof QueryTimeoutException || e instanceof TransientDataAccessException) {
                // DEAD LOCK -- HANDLE THIS,
                // transaction will roll back once hitting this -- shoot these trouble to another topic - handling it sequentially
            } else {
                detailedMsg = handleProcessingELRError(e, edxLabInformationDto, nbsInterfaceModel);
            }
        }
        finally
        {
            if (nbsInterfaceModel != null && !kafkaFailedCheck) {
                edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto);
                edxLogService.addActivityDetailLogs(edxLabInformationDto, detailedMsg);
                edxLogService.saveEdxActivityLogs(edxLabInformationDto.getEdxActivityLogDto());
            }
        }

        return null;
    }

    @Transactional()
    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handlingWdsAndLab(PublicHealthCaseFlowContainer phcContainer) throws DataProcessingException {
        PublicHealthCaseFlowContainer wds;
        try {
            wds = initiatingInvestigationAndPublicHealthCase(phcContainer);
        } catch (Exception e) {
            // TODO SEND TO DLT QUEUE HERE && ISOLATE LOCK EXCEPTION and push it to sequence queue
            phcContainer.getNbsInterfaceModel().setRecordStatusCd(DpConstant.DP_FAILURE_STEP_2);
            phcContainer.getNbsInterfaceModel().setRecordStatusTime(getCurrentTimeStamp(tz));
            nbsInterfaceRepository.save(phcContainer.getNbsInterfaceModel());
            throw new DataProcessingException(e.getMessage(), e);
        }

        try {
            initiatingLabProcessing(wds);
        } catch (Exception e) {
            // TODO SEND TO DLT QUEUE HERE && ISOLATE LOCK EXCEPTION and push it to sequence queue
            phcContainer.getNbsInterfaceModel().setRecordStatusCd(DpConstant.DP_FAILURE_STEP_3);
            phcContainer.getNbsInterfaceModel().setRecordStatusTime(getCurrentTimeStamp(tz));
            nbsInterfaceRepository.save(phcContainer.getNbsInterfaceModel());
            throw new DataProcessingException(e.getMessage(), e);
        }
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
        if (edxDto.getPageActContainer() != null) {
            tracker.setPublicHealthCase(edxDto.getPageActContainer().getPublicHealthCaseContainer().getThePublicHealthCaseDto());
        } else if (edxDto.getPamContainer() != null) {
            tracker.setPublicHealthCase(edxDto.getPamContainer().getPublicHealthCaseContainer().getThePublicHealthCaseDto());
        }


//        interfaceModel.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_2);
//        interfaceModel.setRecordStatusTime(getCurrentTimeStamp(tz));
//        nbsInterfaceRepository.save(interfaceModel);
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
        if (DecisionSupportConstants.MARK_AS_REVIEWED.equalsIgnoreCase(action)) {
            labService.handleMarkAsReviewed(obsDto, edxDto);
        } else if (pageAct != null || pamProxy != null) {
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
        String detailedMsg = "";
        e.printStackTrace();
        logger.error("DP ERROR: {}", e.getMessage());

        if (model != null) {
            model.setRecordStatusCd(DpConstant.DP_FAILURE_STEP_1);
            model.setRecordStatusTime(getCurrentTimeStamp(tz));
            nbsInterfaceRepository.save(model);
        }

        String accessionNumber = "Accession Number:" + dto.getFillerNumber();
        dto.setStatus(NbsInterfaceStatus.Failure);
        dto.setSystemException(true);

        try {
            if (e.toString().contains("Invalid XML")) {
                dto.setInvalidXML(true);
                dto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
            }

            if ((dto.getPageActContainer() != null || dto.getPamContainer() != null) && !dto.isInvestigationSuccessfullyCreated()) {
                dto.setErrorText(dto.isInvestigationMissingFields()
                        ? EdxELRConstant.ELR_MASTER_LOG_ID_5
                        : EdxELRConstant.ELR_MASTER_LOG_ID_9);
            } else if (dto.getPageActContainer() != null || dto.getPamContainer() != null) {
                dto.setErrorText(dto.isNotificationMissingFields()
                        ? EdxELRConstant.ELR_MASTER_LOG_ID_8
                        : EdxELRConstant.ELR_MASTER_LOG_ID_10);
            }

            logger.error("Exception EdxLabHelper.getUnProcessedELR processing exception: {}", e.getMessage());

            if (dto.getErrorText() == null) {
                String msg = e.getMessage();
                if (msg.contains(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG) || msg.contains(EdxELRConstant.ORACLE_FIELD_TRUNCATION_ERROR_MSG)) {
                    dto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_18);
                    dto.setFieldTruncationError(true);
                    dto.setSystemException(false);
                    dto.setPersonParentUid(0);
                    dto.setEthnicityCodeTranslated(true);

                    String exceptionMessage = new StringWriter().toString();
                    e.printStackTrace(new PrintWriter(new StringWriter()));
                    String tableName = exceptionMessage.substring(exceptionMessage.indexOf("Table Name : ") + 13).split(" ")[0];
                    detailedMsg = String.format("SQLException while inserting into %s\n %s\n %s", tableName, accessionNumber, exceptionMessage);
                } else if (msg.contains(EdxELRConstant.DATE_VALIDATION)) {
                    dto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_20);
                    dto.setInvalidDateError(true);
                    dto.setSystemException(false);
                    dto.setPersonParentUid(0);
                    dto.setEthnicityCodeTranslated(true);

                    String substring = msg.substring(msg.indexOf(EdxELRConstant.DATE_VALIDATION));
                    substring = substring.substring(0, substring.indexOf(EdxELRConstant.DATE_VALIDATION_END_DELIMITER1));
                    detailedMsg = String.format("%s\n %s\n%s", substring, accessionNumber, msg);
                } else {
                    dto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_16);
                    dto.setPersonParentUid(0);
                    dto.setEthnicityCodeTranslated(true);
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    detailedMsg = accessionNumber + "\n" + sw;
                }
            }

            if (dto.isInvestigationMissingFields() || dto.isNotificationMissingFields()
                    || EdxELRConstant.ELR_MASTER_LOG_ID_10.equals(dto.getErrorText())) {
                dto.setSystemException(false);
            }

            if (dto.isReflexResultedTestCdMissing() || dto.isResultedTestNameMissing()
                    || dto.isOrderTestNameMissing() || dto.isReasonforStudyCdMissing()) {
                String text = e.getMessage();
                if (text.contains("XMLElementName: ")) {
                    detailedMsg = "Blank identifiers in segments " + text.substring(text.indexOf("XMLElementName: ") + 16) + "\n\n" + accessionNumber;
                }
            }
        } catch (Exception ex) {
            logger.error("Exception while formatting detailed error: {}", ex.getMessage());
        }

        return detailedMsg.length() > 2000 ? detailedMsg.substring(0, 2000) : detailedMsg;
    }

    private void requiredFieldError(String errorTxt, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        if (errorTxt != null) {
            edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_5);
            if (edxLabInformationDT.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails() == null)
            {
                edxLabInformationDT.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(
                        new ArrayList<>());
            }

            //TODO: LOGGING
//            setActivityDetailLog((ArrayList<Object>) edxLabInformationDT.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails(),
//                    String.valueOf(edxLabInformationDT.getLocalId()),
//                    EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, errorTxt);

            edxLabInformationDT.setInvestigationMissingFields(true);
            throw new DataProcessingException("MISSING REQUIRED FIELDS: "+errorTxt);
        }
    }

}
