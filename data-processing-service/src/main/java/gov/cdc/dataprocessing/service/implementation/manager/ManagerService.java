package gov.cdc.dataprocessing.service.implementation.manager;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.cache.DpStatic;
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
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.RtiDlt;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.NbsInterfaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsInterfaceJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.RtiDltJdbcRepository;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static gov.cdc.dataprocessing.constant.DpConstant.*;
import static gov.cdc.dataprocessing.utilities.StringUtils.getRootStackTraceAsString;
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

    private final RtiDltJdbcRepository rtiDltJdbcRepository;



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
                          KafkaManagerProducer kafkaManagerProducer, RtiDltJdbcRepository rtiDltJdbcRepository)
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
        this.rtiDltJdbcRepository = rtiDltJdbcRepository;
    }



    @SuppressWarnings({"java:S6541", "java:S3776"})
    @Transactional()
    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {DataProcessingDBException.class}
    )
    public PublicHealthCaseFlowContainer processingELR(Integer data, boolean retryApplied) throws EdxLogException {
        logger.debug("Interface Id: {}", data);
        NbsInterfaceModel nbsInterfaceModel = null;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String detailedMsg = "";
        Exception exception = null;
        boolean dltLockError = false;
        boolean dataIntegrityError = false;
        boolean nonDltError = false;
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
            enrichProgramAreaAndJurisdiction(observationDto, edxLabInformationDto);

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
            exception = e;
            AtomicBoolean lockError = new AtomicBoolean(false);
            AtomicBoolean integrityError = new AtomicBoolean(false);
            AtomicBoolean otherError = new AtomicBoolean(false);

            AtomicReference<String> msgRef = new AtomicReference<>("");

            handleProcessingElrException(e, edxLabInformationDto, nbsInterfaceModel, lockError, otherError, integrityError, msgRef);

            dltLockError = lockError.get();
            nonDltError = otherError.get();
            detailedMsg = msgRef.get();
            dataIntegrityError = integrityError.get();
        }
        finally
        {
            finalizeProcessingElr(
                    dltLockError,
                    nonDltError,
                    dataIntegrityError,
                    exception,
                    data,
                    detailedMsg,
                    nbsInterfaceModel,
                    edxLabInformationDto,
                    retryApplied
            );

        }

        return null;
    }

    protected void handleProcessingElrException(
            Exception e,
            EdxLabInformationDto edxLabInformationDto,
            NbsInterfaceModel nbsInterfaceModel,
            AtomicBoolean dltLockError,
            AtomicBoolean nonDltError,
            AtomicBoolean dataIntegrityError,
            AtomicReference<String> detailedMsg
    ) {

        Throwable rootCause = ExceptionUtils.getRootCause(e);
        log.warn("DB-related exception caught: {}", e.getMessage(), e);

        if (e instanceof CannotAcquireLockException) {
            dltLockError.set(true);
        } else if (e instanceof QueryTimeoutException ||
                e instanceof TransientDataAccessException ||
                e instanceof DataAccessException ||
                rootCause instanceof java.sql.SQLException) {
//            throw new DataProcessingDBException(e.getMessage(), e);
            dataIntegrityError.set(true);
        } else {
            detailedMsg.set(handleProcessingELRError(e, edxLabInformationDto, nbsInterfaceModel));
            nonDltError.set(true);
        }
    }

    protected void finalizeProcessingElr(
            boolean dltLockError,
            boolean nonDltError,
            boolean integrityError,
            Exception exception,
            int interfaceId,
            String detailedMsg,
            NbsInterfaceModel nbsInterfaceModel,
            EdxLabInformationDto edxLabInformationDto,
            boolean retryApplied
    ) throws EdxLogException {
        if (dltLockError) {
            persistingRtiDlt(exception, (long) interfaceId, EMPTY, STEP_1, DP_FAILURE_STEP_1 + DASH + ERROR_DB_LOCKING);
            if (!retryApplied) {
                composeDltKafkaEvent(String.valueOf(interfaceId), ERROR_DB_LOCKING);
            }
        } else if (nonDltError) {
            persistingRtiDlt(exception, (long) interfaceId, EMPTY, STEP_1, DP_FAILURE_STEP_1 + DASH + "Other Error");
        } else if (integrityError) {
            DpStatic.setUuidPoolInitialized(true);
            persistingRtiDlt(exception, (long) interfaceId, EMPTY, STEP_1, DP_FAILURE_STEP_1 + DASH + ERROR_DB_DATA_INTEGERITY);
            if (!retryApplied) {
                composeDltKafkaEvent(String.valueOf(interfaceId), ERROR_DB_DATA_INTEGERITY);
            }
        }
        edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto);
        edxLogService.addActivityDetailLogs(edxLabInformationDto, detailedMsg);
        edxLogService.saveEdxActivityLogs(edxLabInformationDto.getEdxActivityLogDto());
    }

    protected void enrichProgramAreaAndJurisdiction(
            ObservationDto observationDto,
            EdxLabInformationDto edxLabInformationDto
    ) throws DataProcessingException {
        if (observationDto.getProgAreaCd() != null &&
                cacheApiService.getSrteCacheBool(ObjectName.PROGRAM_AREA_CODES.name(), observationDto.getProgAreaCd())) {
            edxLabInformationDto.setProgramAreaName(
                    cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES.name(), observationDto.getProgAreaCd())
            );
        }

        if (observationDto.getJurisdictionCd() != null &&
                cacheApiService.getSrteCacheBool(ObjectName.JURISDICTION_CODES.name(), observationDto.getJurisdictionCd())) {
            edxLabInformationDto.setJurisdictionName(
                    cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODES.name(), observationDto.getJurisdictionCd())
            );
        }

        if (edxLabInformationDto.isLabIsCreate()) {
            edxLabInformationDto.setLabIsCreateSuccess(true);
            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_2);
        }

        if (edxLabInformationDto.isLabIsCreateSuccess() &&
                (edxLabInformationDto.getProgramAreaName() == null || edxLabInformationDto.getJurisdictionName() == null)) {
            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_1);
        }
    }



    @Transactional()
    @Retryable(
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {DataProcessingDBException.class}
    )
    @SuppressWarnings("java:S1135")
    public void handlingWdsAndLab(PublicHealthCaseFlowContainer phcContainer, boolean retryApplied) throws DataProcessingException, DataProcessingDBException, EdxLogException {
        PublicHealthCaseFlowContainer wds;
        Exception exception = null;
        boolean dltLockError = false;
        boolean nonDltError = false;
        boolean integrityError = false;
        try
        {
            wds = initiatingInvestigationAndPublicHealthCase(phcContainer);
            initiatingLabProcessing(wds);
        }
        catch (Exception e)
        {
            exception = e;
            AtomicBoolean lockFlag = new AtomicBoolean(false);
            AtomicBoolean otherFlag = new AtomicBoolean(false);
            AtomicBoolean integrityFlag = new AtomicBoolean(false);


            handleWdsAndLabException(e, phcContainer, lockFlag, otherFlag, integrityFlag);

            dltLockError = lockFlag.get();
            nonDltError = otherFlag.get();
            integrityError = integrityFlag.get();
        }
        finally {
            finalizeWdsAndLabProcessing(phcContainer, exception, dltLockError, nonDltError, integrityError, retryApplied);
        }

    }

    protected void handleWdsAndLabException(
            Exception e,
            PublicHealthCaseFlowContainer phcContainer,
            AtomicBoolean dltLockError,
            AtomicBoolean nonDltError,
            AtomicBoolean interityError
    )  {

        Throwable rootCause = ExceptionUtils.getRootCause(e);
        log.warn("DB-related exception caught: {}", e.getMessage(), e);

        if (e instanceof CannotAcquireLockException)
        {
            dltLockError.set(true);
        }
        else if (e instanceof QueryTimeoutException ||
                e instanceof TransientDataAccessException ||
                e instanceof DataAccessException ||
                rootCause instanceof java.sql.SQLException)
        {
            interityError.set(true);
//            throw new DataProcessingDBException(e.getMessage(), e);
        }
        else
        {
            nonDltError.set(true);
            NbsInterfaceModel model = phcContainer.getNbsInterfaceModel();
            model.setRecordStatusCd(DP_FAILURE_STEP_2);
            model.setRecordStatusTime(getCurrentTimeStamp(tz));
            nbsInterfaceRepository.save(model);
        }
    }

    protected void finalizeWdsAndLabProcessing(
            PublicHealthCaseFlowContainer phcContainer,
            Exception exception,
            boolean dltLockError,
            boolean nonDltError,
            boolean integrityError,
            boolean retryApplied
    ) throws EdxLogException {
        String payload = new Gson().toJson(phcContainer);

        if (dltLockError)
        {
            persistingRtiDlt(exception, Long.valueOf(phcContainer.getNbsInterfaceId()), payload, STEP_2, DP_FAILURE_STEP_2 + DASH + ERROR_DB_LOCKING);
            if (!retryApplied) {
                composeDltKafkaEvent(payload, ERROR_DB_LOCKING);
            }
        }
        else if (integrityError) {
            DpStatic.setUuidPoolInitialized(true);
            persistingRtiDlt(exception, Long.valueOf(phcContainer.getNbsInterfaceId()), EMPTY, STEP_2, DP_FAILURE_STEP_2 + DASH + ERROR_DB_DATA_INTEGERITY);
            if (!retryApplied) {
                composeDltKafkaEvent(payload, ERROR_DB_DATA_INTEGERITY);
            }
        }
        else
        {
            if (nonDltError)
            {
                persistingRtiDlt(exception, Long.valueOf(phcContainer.getNbsInterfaceId()), payload, STEP_2, DP_FAILURE_STEP_2 + " - Other Non Error");
            }
            edxLogService.updateActivityLogDT(phcContainer.getNbsInterfaceModel(), phcContainer.getEdxLabInformationDto());
            edxLogService.addActivityDetailLogs(phcContainer.getEdxLabInformationDto(), EMPTY);
            edxLogService.saveEdxActivityLogs(phcContainer.getEdxLabInformationDto().getEdxActivityLogDto());
        }
    }


    public void updateNbsInterfaceStatus(List<Integer> ids) {
        nbsInterfaceJdbcRepository.updateRecordStatusToRtiProcess(ids);
    }

    protected void persistingRtiDlt(Exception exception, Long nbsInterfaceUid, String payload, String step, String status) {
        RtiDlt rtiDlt = new RtiDlt();
        rtiDlt.setNbsInterfaceId(nbsInterfaceUid);
        rtiDlt.setOrigin(step);
        rtiDlt.setStatus(status);
        rtiDlt.setPayload(payload);
        rtiDlt.setStackTrace(getRootStackTraceAsString(exception));
        rtiDltJdbcRepository.upsert(rtiDlt);
    }
    protected void composeDltKafkaEvent(String message, String dltType) {
        if (dltType.equalsIgnoreCase(ERROR_DB_LOCKING)) {
            kafkaManagerProducer.sendDltForLocking(message);
        }
        else if (dltType.equalsIgnoreCase(ERROR_DB_DATA_INTEGERITY)) {
            kafkaManagerProducer.sendDltForDataIntegrity(message);
        }
        else {
            //DO NOTHING HERE
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

    protected String handleProcessingELRError(Exception e, EdxLabInformationDto dto, NbsInterfaceModel model) {
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

    @SuppressWarnings("jav:S4507")
    private void logException(Exception e) {
        logger.error("DP ERROR: {}", e.getMessage());
    }

    private void updateModelStatus(NbsInterfaceModel model) {
        if (model != null) {
            model.setRecordStatusCd(DP_FAILURE_STEP_1);
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
