package gov.cdc.dataprocessing.service.implementation.manager;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.cache.PropertyUtilCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.DpConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.RtiCacheException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.NbsInterfaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsInterfaceJdbcRepository;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.data_extraction.IDataExtractionService;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerAggregationService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPageService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IDecisionSupportService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationNotificationService;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import gov.cdc.dataprocessing.service.model.wds.WdsTrackerView;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.ManagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataprocessing.utilities.GsonUtil.GSON;
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

    private final KafkaManagerProducer kafkaManagerProducer;

    private final IManagerAggregationService managerAggregationService;
    private final ILabReportProcessing labReportProcessing;
    private final IPageService pageService;
    private final IPamService pamService;
    private final IInvestigationNotificationService investigationNotificationService;

    private final NbsInterfaceJdbcRepository nbsInterfaceJdbcRepository;

    private static final String LOG_EXCEPTION_MESSAGE = "Exception while formatting exception message for Activity Log: ";
    @Autowired
    public ManagerService(@Lazy ICacheApiService cacheApiService, IObservationService observationService,
                          IEdxLogService edxLogService,
                          IDataExtractionService dataExtractionService,
                          NbsInterfaceRepository nbsInterfaceRepository,
                          IDecisionSupportService decisionSupportService,
                          ManagerUtil managerUtil,
                          KafkaManagerProducer kafkaManagerProducer,
                          IManagerAggregationService managerAggregationService,
                          ILabReportProcessing labReportProcessing,
                          IPageService pageService,
                          IPamService pamService,
                          IInvestigationNotificationService investigationNotificationService, NbsInterfaceJdbcRepository nbsInterfaceJdbcRepository) {
        this.cacheApiService = cacheApiService;
        this.observationService = observationService;
        this.edxLogService = edxLogService;
        this.dataExtractionService = dataExtractionService;
        this.nbsInterfaceRepository = nbsInterfaceRepository;
        this.decisionSupportService = decisionSupportService;
        this.managerUtil = managerUtil;
        this.kafkaManagerProducer = kafkaManagerProducer;
        this.managerAggregationService = managerAggregationService;
        this.labReportProcessing = labReportProcessing;
        this.pageService = pageService;
        this.pamService = pamService;
        this.investigationNotificationService = investigationNotificationService;
        this.nbsInterfaceJdbcRepository = nbsInterfaceJdbcRepository;
    }

    @Deprecated
    public void processDistribution(Integer data) throws DataProcessingConsumerException, DataProcessingException {
        if (AuthUtil.authUser != null) {
            processingELR(data);
        } else {
            throw new DataProcessingConsumerException("Invalid User");
        }
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    @Transactional()
    public PublicHealthCaseFlowContainer initiatingInvestigationAndPublicHealthCase(PublicHealthCaseFlowContainer phcContainer) throws DataProcessingException, RtiCacheException {
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

//        Gson gson = new Gson();
//        String jsonString = gson.toJson(phcContainer);
//        kafkaManagerProducer.sendDataLabHandling(jsonString);

        return phcContainer;
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})

//    @Transactional
    public void initiatingLabProcessing(PublicHealthCaseFlowContainer phcContainer) throws DataProcessingException {
        NbsInterfaceModel interfaceModel = phcContainer.getNbsInterfaceModel();
        EdxLabInformationDto edxDto = phcContainer.getEdxLabInformationDto();
        ObservationDto obsDto = phcContainer.getObservationDto();

        PageActProxyContainer pageAct = edxDto.getPageActContainer();
        PamProxyContainer pamProxy = edxDto.getPamContainer();
        PublicHealthCaseContainer phcContainerModel;
        Long phcUid;

        String action = edxDto.getAction();
        if (DecisionSupportConstants.MARK_AS_REVIEWED.equalsIgnoreCase(action)) {
            labReportProcessing.markAsReviewedHandler(obsDto.getObservationUid(), edxDto);
            Long associatedPhcUid = edxDto.getAssociatedPublicHealthCaseUid();
            if (associatedPhcUid != null && associatedPhcUid > 0) {
                edxDto.setPublicHealthCaseUid(associatedPhcUid);
                edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_21);
                edxDto.setLabAssociatedToInv(true);
            } else {
                edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_11);
            }
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
                phcUid = pageService.setPageProxyWithAutoAssoc(
                        NEDSSConstant.CASE,
                        pageAct,
                        edxDto.getRootObserbationUid(),
                        NEDSSConstant.LABRESULT_CODE,
                        null);

                pageAct.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setPublicHealthCaseUid(phcUid);
            } else if (pamProxy != null && obsDto.getJurisdictionCd() != null && obsDto.getProgAreaCd() != null) {
                phcUid = pamService.setPamProxyWithAutoAssoc(
                        pamProxy,
                        edxDto.getRootObserbationUid(),
                        NEDSSConstant.LABRESULT_CODE);

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
//                handleNndNotification(phcContainerModel, edxDto);
                EDXActivityDetailLogDto detailLog = investigationNotificationService.sendNotification(phcContainerModel, edxDto.getNndComment());
                detailLog.setRecordType(EdxELRConstant.ELR_RECORD_TP);
                detailLog.setRecordName(EdxELRConstant.ELR_RECORD_NM);

                ArrayList<EDXActivityDetailLogDto> details = (ArrayList<EDXActivityDetailLogDto>) edxDto.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails();
                if (details == null) {
                    details = new ArrayList<>();
                }
                details.add(detailLog);
                edxDto.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(details);

                if (EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name().equals(detailLog.getLogType())) {
                    String comment = detailLog.getComment();
                    if (comment != null && comment.contains(EdxELRConstant.MISSING_NOTF_REQ_FIELDS)) {
                        edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_8);
                        edxDto.setNotificationMissingFields(true);
                    } else {
                        edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_10);
                    }
                    throw new DataProcessingException("MISSING NOTI REQUIRED: " + comment);
                } else {
                    edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_6);
                }
            }
        }

        interfaceModel.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_3);
        interfaceModel.setRecordStatusTime(getCurrentTimeStamp(tz));
        nbsInterfaceRepository.save(interfaceModel);
        logger.info("Completed");
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNndNotification(PublicHealthCaseContainer phcContainerModel, EdxLabInformationDto edxDto) throws DataProcessingException {
        EDXActivityDetailLogDto detailLog = investigationNotificationService.sendNotification(phcContainerModel, edxDto.getNndComment());
        detailLog.setRecordType(EdxELRConstant.ELR_RECORD_TP);
        detailLog.setRecordName(EdxELRConstant.ELR_RECORD_NM);

        ArrayList<EDXActivityDetailLogDto> details = (ArrayList<EDXActivityDetailLogDto>) edxDto.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails();
        if (details == null) {
            details = new ArrayList<>();
        }
        details.add(detailLog);
        edxDto.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(details);

        if (EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name().equals(detailLog.getLogType())) {
            String comment = detailLog.getComment();
            if (comment != null && comment.contains(EdxELRConstant.MISSING_NOTF_REQ_FIELDS)) {
                edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_8);
                edxDto.setNotificationMissingFields(true);
            } else {
                edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_10);
            }
            throw new DataProcessingException("MISSING NOTI REQUIRED: " + comment);
        } else {
            edxDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_6);
        }
    }


    @SuppressWarnings({"java:S6541", "java:S3776"})
    @Transactional()
    @Retryable(
            value = { LockAcquisitionException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public PublicHealthCaseFlowContainer processingELR(Integer data) {
        logger.info("Interface Id: {}", data);
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

            // Return case container
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
            e.printStackTrace();
            logger.error("DP ERROR: {}", e.getMessage());
            if (nbsInterfaceModel != null) {
                nbsInterfaceModel.setRecordStatusCd(DpConstant.DP_FAILURE_STEP_1);
                nbsInterfaceModel.setRecordStatusTime(getCurrentTimeStamp(tz));
                nbsInterfaceRepository.save(nbsInterfaceModel);
            }
            String accessionNumberToAppend = "Accession Number:" + edxLabInformationDto.getFillerNumber();
            edxLabInformationDto.setStatus(NbsInterfaceStatus.Failure);
            edxLabInformationDto.setSystemException(true);

            if (e.toString().contains("Invalid XML")) {
                edxLabInformationDto.setInvalidXML(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
            }

            if ((edxLabInformationDto.getPageActContainer() != null
                    || edxLabInformationDto.getPamContainer() != null)
                    && !edxLabInformationDto.isInvestigationSuccessfullyCreated()) {
                if (edxLabInformationDto.isInvestigationMissingFields()) {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_5);
                } else {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_9);
                }
            }
            else if (
                    (edxLabInformationDto.getPageActContainer() != null
                    || edxLabInformationDto.getPamContainer() != null)
                    && edxLabInformationDto.isInvestigationSuccessfullyCreated())
            {
                if (edxLabInformationDto.isNotificationMissingFields()) {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_8);
                } else {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_10);
                }
            }


            // error check function in here to create the details message

                logger.error("Exception EdxLabHelper.getUnProcessedELR processing exception: {}", e.getMessage());

                if(edxLabInformationDto.getErrorText()==null){
                    //if error text is null, that means lab was not created due to unexpected error.
                    if(e.getMessage().contains(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG)
                            || e.getMessage().contains(EdxELRConstant.ORACLE_FIELD_TRUNCATION_ERROR_MSG))
                    {
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_18);
                        edxLabInformationDto.setFieldTruncationError(true);
                        edxLabInformationDto.setSystemException(false);
                        try{
                            // Extract table name from Exception message, first find table name and ignore text after it.
                            StringWriter errors = new StringWriter();
                            e.printStackTrace(new PrintWriter(errors));
                            String exceptionMessage = errors.toString();
                            //Patient is not created so setting patient_parent_id to 0
                            edxLabInformationDto.setPersonParentUid(0);
                            //No need to create success message "The Ethnicity code provided in the message is not found in the SRT database. The code is saved to the NBS." in case of exception scenario
                            edxLabInformationDto.setEthnicityCodeTranslated(true);
                            String textToLookFor = "Table Name : ";
                            String tableName = exceptionMessage.substring(exceptionMessage.indexOf(textToLookFor)+textToLookFor.length());
                            tableName = tableName.substring(0, tableName.indexOf(" "));
                            detailedMsg = "SQLException while inserting into "+tableName+"\n "+accessionNumberToAppend+"\n "+exceptionMessage;
                            detailedMsg = detailedMsg.substring(0,Math.min(detailedMsg.length(), 2000));
                        }catch(Exception ex){
                            logger.error("{} {}", LOG_EXCEPTION_MESSAGE, ex.getMessage());
                        }
                    } else if (e.getMessage().contains(EdxELRConstant.DATE_VALIDATION)) {
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_20);
                        edxLabInformationDto.setInvalidDateError(true);
                        edxLabInformationDto.setSystemException(false);

                        //Patient is not created so setting patient_parent_id to 0
                        edxLabInformationDto.setPersonParentUid(0);
                        //No need to create success message for Ethnic code
                        edxLabInformationDto.setEthnicityCodeTranslated(true);
                        try {
                            // Extract problem date from Exception message
                            String problemDateInfoSubstring = e.getMessage().substring(e.getMessage().indexOf(EdxELRConstant.DATE_VALIDATION));
                            problemDateInfoSubstring = problemDateInfoSubstring.substring(0,problemDateInfoSubstring.indexOf(EdxELRConstant.DATE_VALIDATION_END_DELIMITER1));
                            detailedMsg = problemDateInfoSubstring+"\n "+accessionNumberToAppend+"\n"+e.getMessage();
                            detailedMsg = detailedMsg.substring(0,Math.min(detailedMsg.length(), 2000));
                        }catch(Exception ex){
                            logger.error("Exception while formatting date exception message for Activity Log: {}", ex.getMessage());
                        }
                    }else{
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_16);
                        try{
                            //Patient is not created so setting patient_parent_id to 0
                            edxLabInformationDto.setPersonParentUid(0);
                            //No need to create success message for Ethnicity code provided in the message is not found in the SRT database. The code is saved to the NBS." in case of exception scenario
                            edxLabInformationDto.setEthnicityCodeTranslated(true);
                            StringWriter errors = new StringWriter();
                            e.printStackTrace(new PrintWriter(errors));
                            String exceptionMessage = accessionNumberToAppend+"\n"+errors;
                            detailedMsg = exceptionMessage.substring(0,Math.min(exceptionMessage.length(), 2000));
                        }catch(Exception ex){
                            logger.error("{} {}", LOG_EXCEPTION_MESSAGE, ex.getMessage());
                        }
                    }
                }
                if( edxLabInformationDto.isInvestigationMissingFields() || edxLabInformationDto.isNotificationMissingFields() || (edxLabInformationDto.getErrorText()!=null && edxLabInformationDto.getErrorText().equals(EdxELRConstant.ELR_MASTER_LOG_ID_10))){
                    edxLabInformationDto.setSystemException(false);
                }

                if(edxLabInformationDto.isReflexResultedTestCdMissing()
                        || edxLabInformationDto.isResultedTestNameMissing()
                        || edxLabInformationDto.isOrderTestNameMissing()
                        || edxLabInformationDto.isReasonforStudyCdMissing()){
                    try{
                        String exceptionMsg = e.getMessage();
                        String textToLookFor = "XMLElementName: ";
                        detailedMsg = "Blank identifiers in segments "+exceptionMsg.substring(exceptionMsg.indexOf(textToLookFor)+textToLookFor.length())+"\n\n"+accessionNumberToAppend;
                        detailedMsg = detailedMsg.substring(0,Math.min(detailedMsg.length(), 2000));
                    }catch(Exception ex){
                        logger.error("{} {}", LOG_EXCEPTION_MESSAGE, ex.getMessage());
                    }
                }
        }
        finally
        {
            if (nbsInterfaceModel != null && !kafkaFailedCheck) {
                edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto);
                edxLogService.addActivityDetailLogs(edxLabInformationDto, detailedMsg);
                kafkaManagerProducer.sendDataEdxActivityLog(GSON.toJson(edxLabInformationDto.getEdxActivityLogDto()));
            }
        }

        return null;
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
