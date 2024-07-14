package gov.cdc.dataprocessing.service.implementation.manager;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.DpConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
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
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.data_extraction.IDataExtractionService;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerAggregationService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerCacheService;
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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static gov.cdc.dataprocessing.constant.ManagerEvent.EVENT_ELR;

@Service
@Slf4j
public class ManagerService implements IManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

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

    private final IManagerCacheService managerCacheService;

    @Autowired
    public ManagerService(IObservationService observationService,
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
                          IInvestigationNotificationService investigationNotificationService,
                          IManagerCacheService managerCacheService) {
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
        this.managerCacheService = managerCacheService;
    }

    @Transactional
    public void processDistribution(String eventType, String data) throws DataProcessingConsumerException {
        if (AuthUtil.authUser != null) {
            if (eventType.equals(EVENT_ELR)) {
                processingELR(data);
            }
        } else {
            throw new DataProcessingConsumerException("Invalid User");
        }

    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    @Transactional
    public void initiatingInvestigationAndPublicHealthCase(PublicHealthCaseFlowContainer publicHealthCaseFlowContainer) {
        NbsInterfaceModel nbsInterfaceModel = null;
        EdxLabInformationDto edxLabInformationDto = null;
        String detailedMsg = "";
        try {
            edxLabInformationDto = publicHealthCaseFlowContainer.getEdxLabInformationDto();
            ObservationDto observationDto = publicHealthCaseFlowContainer.getObservationDto();
            LabResultProxyContainer labResultProxyContainer = publicHealthCaseFlowContainer.getLabResultProxyContainer();
            var res = nbsInterfaceRepository.findByNbsInterfaceUid(publicHealthCaseFlowContainer.getNbsInterfaceId());
            if (res.isPresent()) {
                nbsInterfaceModel = res.get();
            } else {
                throw new DataProcessingException("NBS Interface Data Not Exist");
            }

            if (edxLabInformationDto.isLabIsUpdateDRRQ()) {
                edxLabInformationDto.setLabIsUpdateSuccess(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_15);
            } else if (edxLabInformationDto.isLabIsUpdateDRSA()) {
                edxLabInformationDto.setLabIsUpdateSuccess(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_22);
            }
            decisionSupportService.validateProxyContainer(labResultProxyContainer, edxLabInformationDto);

            WdsTrackerView trackerView = new WdsTrackerView();
            trackerView.setWdsReport(edxLabInformationDto.getWdsReports());

            Long patUid = -1L;
            Long patParentUid = -1L;
            String patFirstName = null;
            String patLastName = null;
            for (var item : publicHealthCaseFlowContainer.getLabResultProxyContainer().getThePersonContainerCollection()) {
                if (item.getThePersonDto().getCd().equals("PAT")) {
                    patUid = item.getThePersonDto().getUid();
                    patParentUid = item.getThePersonDto().getPersonParentUid();
                    patFirstName = item.getThePersonDto().getFirstNm();
                    patLastName = item.getThePersonDto().getLastNm();
                    break;
                }
            }

            trackerView.setPatientUid(patUid);
            trackerView.setPatientParentUid(patParentUid);
            trackerView.setPatientFirstName(patFirstName);
            trackerView.setPatientLastName(patLastName);

            nbsInterfaceModel.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_2);
            nbsInterfaceRepository.save(nbsInterfaceModel);

            PublicHealthCaseFlowContainer phcContainer = new PublicHealthCaseFlowContainer();
            phcContainer.setNbsInterfaceId(nbsInterfaceModel.getNbsInterfaceUid());
            phcContainer.setLabResultProxyContainer(labResultProxyContainer);
            phcContainer.setEdxLabInformationDto(edxLabInformationDto);
            phcContainer.setObservationDto(observationDto);
            phcContainer.setWdsTrackerView(trackerView);

            if (edxLabInformationDto.getPageActContainer() != null
                    || edxLabInformationDto.getPamContainer() != null) {
                if (edxLabInformationDto.getPageActContainer() != null) {
                    var pageActProxyVO = (PageActProxyContainer) edxLabInformationDto.getPageActContainer();
                    trackerView.setPublicHealthCase(pageActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto());
                } else {
                    var pamProxyVO = (PamProxyContainer) edxLabInformationDto.getPamContainer();
                    trackerView.setPublicHealthCase(pamProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto());
                }
            }


            Gson gson = new Gson();
            String trackerString = gson.toJson(trackerView);
            kafkaManagerProducer.sendDataActionTracker(trackerString);

            gson = new Gson();
            String jsonString = gson.toJson(phcContainer);
            kafkaManagerProducer.sendDataLabHandling(jsonString);

        } catch (Exception e) {
            detailedMsg = e.getMessage();
            if (nbsInterfaceModel != null) {
                nbsInterfaceModel.setRecordStatusCd(DpConstant.DP_FAILURE_STEP_2);
                nbsInterfaceRepository.save(nbsInterfaceModel);
            }

        } finally {
            if (nbsInterfaceModel != null) {
                edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto);
                edxLogService.addActivityDetailLogs(edxLabInformationDto, detailedMsg);
                Gson gson = new Gson();
                String jsonString = gson.toJson(edxLabInformationDto.getEdxActivityLogDto());
                kafkaManagerProducer.sendDataEdxActivityLog(jsonString);
            }
        }

    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    @Transactional
    public void initiatingLabProcessing(PublicHealthCaseFlowContainer publicHealthCaseFlowContainer) {
        NbsInterfaceModel nbsInterfaceModel = null;
        EdxLabInformationDto edxLabInformationDto = null;
        try {
            edxLabInformationDto = publicHealthCaseFlowContainer.getEdxLabInformationDto();
            ObservationDto observationDto = publicHealthCaseFlowContainer.getObservationDto();
            var res = nbsInterfaceRepository.findByNbsInterfaceUid(publicHealthCaseFlowContainer.getNbsInterfaceId());
            if (res.isPresent()) {
                nbsInterfaceModel = res.get();
            } else {
                throw new DataProcessingException("NBS Interface Data Not Exist");
            }
            PageActProxyContainer pageActProxyContainer = null;
            PamProxyContainer pamProxyVO = null;
            PublicHealthCaseContainer publicHealthCaseContainer;
            Long phcUid;


            if (edxLabInformationDto.getAction() != null && edxLabInformationDto.getAction().equalsIgnoreCase(DecisionSupportConstants.MARK_AS_REVIEWED)) {
                labReportProcessing.markAsReviewedHandler(observationDto.getObservationUid(), edxLabInformationDto);
                if (edxLabInformationDto.getAssociatedPublicHealthCaseUid() != null && edxLabInformationDto.getAssociatedPublicHealthCaseUid() > 0) {
                    edxLabInformationDto.setPublicHealthCaseUid(edxLabInformationDto.getAssociatedPublicHealthCaseUid());
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_21);
                    edxLabInformationDto.setLabAssociatedToInv(true);
                } else {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_11);
                }

            } else if (edxLabInformationDto.getPageActContainer() != null || edxLabInformationDto.getPamContainer() != null) {
                //Check for user security to create investigation
                //checkSecurity(nbsSecurityObj, edxLabInformationDto, NBSBOLookup.INVESTIGATION, NBSOperationLookup.ADD, programAreaCd, jurisdictionCd);
                if (edxLabInformationDto.getPageActContainer() != null) {
                    pageActProxyContainer = edxLabInformationDto.getPageActContainer();
                    publicHealthCaseContainer = pageActProxyContainer.getPublicHealthCaseContainer();
                } else {
                    pamProxyVO = edxLabInformationDto.getPamContainer();
                    publicHealthCaseContainer = pamProxyVO.getPublicHealthCaseContainer();
                }

                if (publicHealthCaseContainer.getErrorText() != null) {
                    //TODO: LOGGING
                    requiredFieldError(publicHealthCaseContainer.getErrorText(), edxLabInformationDto);
                }


                if (pageActProxyContainer != null && observationDto.getJurisdictionCd() != null && observationDto.getProgAreaCd() != null) {
                    phcUid = pageService.setPageProxyWithAutoAssoc(NEDSSConstant.CASE, pageActProxyContainer,
                            edxLabInformationDto.getRootObserbationUid(),
                            NEDSSConstant.LABRESULT_CODE, null);

                    pageActProxyContainer.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setPublicHealthCaseUid(phcUid);
                    edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_3);
                    edxLabInformationDto.setPublicHealthCaseUid(phcUid);
                    edxLabInformationDto.setLabAssociatedToInv(true);
                } else if (observationDto.getJurisdictionCd() != null && observationDto.getProgAreaCd() != null) {
                    phcUid = pamService.setPamProxyWithAutoAssoc(pamProxyVO, edxLabInformationDto.getRootObserbationUid(), NEDSSConstant.LABRESULT_CODE);

                    pamProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setPublicHealthCaseUid(phcUid);
                    edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_3);
                    edxLabInformationDto.setPublicHealthCaseUid(phcUid);
                    edxLabInformationDto.setLabAssociatedToInv(true);
                }

                if (edxLabInformationDto.getAction() != null
                        && edxLabInformationDto.getAction().equalsIgnoreCase(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE)) {
                    //TODO: LOGGING
                    EDXActivityDetailLogDto edxActivityDetailLogDT = investigationNotificationService.sendNotification(publicHealthCaseContainer, edxLabInformationDto.getNndComment());
                    edxActivityDetailLogDT.setRecordType(EdxELRConstant.ELR_RECORD_TP);
                    edxActivityDetailLogDT.setRecordName(EdxELRConstant.ELR_RECORD_NM);
                    ArrayList<EDXActivityDetailLogDto> details = (ArrayList<EDXActivityDetailLogDto>) edxLabInformationDto.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails();
                    if (details == null) {
                        details = new ArrayList<>();
                    }
                    details.add(edxActivityDetailLogDT);
                    edxLabInformationDto.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(details);
                    if (edxActivityDetailLogDT.getLogType() != null && edxActivityDetailLogDT.getLogType().equals(EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name())) {
                        if (edxActivityDetailLogDT.getComment() != null && edxActivityDetailLogDT.getComment().contains(EdxELRConstant.MISSING_NOTF_REQ_FIELDS)) {
                            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_8);
                            edxLabInformationDto.setNotificationMissingFields(true);
                        } else {
                            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_10);
                        }
                        throw new DataProcessingException("MISSING NOTI REQUIRED: " + edxActivityDetailLogDT.getComment());
                    } else {
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_6);
                    }

                }
            }
            nbsInterfaceModel.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_3);
            nbsInterfaceRepository.save(nbsInterfaceModel);
        } catch (Exception e) {
            if (nbsInterfaceModel != null) {
                nbsInterfaceModel.setRecordStatusCd(DpConstant.DP_FAILURE_STEP_3);
                nbsInterfaceRepository.save(nbsInterfaceModel);
            }
            if ((edxLabInformationDto.getPageActContainer() != null || edxLabInformationDto.getPamContainer() != null) && !edxLabInformationDto.isInvestigationSuccessfullyCreated()) {
                if (edxLabInformationDto.isInvestigationMissingFields()) {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_5);
                } else {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_9);
                }
            } else if ((edxLabInformationDto.getPageActContainer() != null
                    || edxLabInformationDto.getPamContainer() != null)
                    && edxLabInformationDto.isInvestigationSuccessfullyCreated()) {
                if (edxLabInformationDto.isNotificationMissingFields()) {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_8);
                } else {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_10);
                }
            }
        } finally {
            if (nbsInterfaceModel != null) {
                edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto);
                edxLogService.addActivityDetailLogsForWDS(edxLabInformationDto, "");

                Gson gson = new Gson();
                String jsonString = gson.toJson(edxLabInformationDto.getEdxActivityLogDto());
                kafkaManagerProducer.sendDataEdxActivityLog(jsonString);
            }
        }
    }

    @SuppressWarnings("java:S6541")
    private void processingELR(String data) {
        NbsInterfaceModel nbsInterfaceModel = null;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String detailedMsg = "";
        Gson gson = new Gson();
        try {

            edxLabInformationDto.setStatus(NbsInterfaceStatus.Success);
            edxLabInformationDto.setUserName(AuthUtil.authUser.getUserId());

            nbsInterfaceModel = gson.fromJson(data, NbsInterfaceModel.class);

            edxLabInformationDto.setNbsInterfaceUid(nbsInterfaceModel.getNbsInterfaceUid());

            CompletableFuture<Void> cacheLoadingFuture = managerCacheService.loadAndInitCachedValueAsync();
            cacheLoadingFuture.join();


            LabResultProxyContainer labResultProxyContainer = dataExtractionService.parsingDataToObject(nbsInterfaceModel, edxLabInformationDto);

            edxLabInformationDto.setLabResultProxyContainer(labResultProxyContainer);

            if (nbsInterfaceModel.getObservationUid() != null && nbsInterfaceModel.getObservationUid() > 0) {
                edxLabInformationDto.setRootObserbationUid(nbsInterfaceModel.getObservationUid());
            }
            Long aPersonUid = null;

            ObservationDto observationDto;

            // Checking for matching observation
            edxLabInformationDto = managerAggregationService.processingObservationMatching(edxLabInformationDto, labResultProxyContainer, aPersonUid);


            // This process patient, provider, nok, and organization. Then it will update both parsedData and edxLabInformationDto accordingly
            managerAggregationService.serviceAggregationAsync(labResultProxyContainer, edxLabInformationDto);


            // Hit when Obs is matched
            if (edxLabInformationDto.isLabIsUpdateDRRQ() || edxLabInformationDto.isLabIsUpdateDRSA()) {
                managerUtil.setPersonUIDOnUpdate(aPersonUid, labResultProxyContainer);
            }
            edxLabInformationDto.setLabResultProxyContainer(labResultProxyContainer);

            observationDto = observationService.processingLabResultContainer(labResultProxyContainer);

            if (edxLabInformationDto.isLabIsCreate()) {
                edxLabInformationDto.setLabIsCreateSuccess(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_2);
            }

            edxLabInformationDto.setLocalId(observationDto.getLocalId());
            edxLabInformationDto.getEdxActivityLogDto().setBusinessObjLocalId(observationDto.getLocalId());
            edxLabInformationDto.setRootObserbationUid(observationDto.getObservationUid());

            if (observationDto.getProgAreaCd() != null && SrteCache.programAreaCodesMap.containsKey(observationDto.getProgAreaCd())) {
                edxLabInformationDto.setProgramAreaName(SrteCache.programAreaCodesMap.get(observationDto.getProgAreaCd()));
            }

            if (observationDto.getJurisdictionCd() != null && SrteCache.jurisdictionCodeMap.containsKey(observationDto.getJurisdictionCd())) {
                String jurisdictionName = SrteCache.jurisdictionCodeMap.get(observationDto.getJurisdictionCd());
                edxLabInformationDto.setJurisdictionName(jurisdictionName);
            }


            if (edxLabInformationDto.isLabIsCreateSuccess() && (edxLabInformationDto.getProgramAreaName() == null
                    || edxLabInformationDto.getJurisdictionName() == null)) {
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_1);
            }


            nbsInterfaceModel.setObservationUid(observationDto.getObservationUid().intValue());
            nbsInterfaceModel.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_1);
            nbsInterfaceRepository.save(nbsInterfaceModel);


            PublicHealthCaseFlowContainer phcContainer = new PublicHealthCaseFlowContainer();
            phcContainer.setLabResultProxyContainer(labResultProxyContainer);
            phcContainer.setEdxLabInformationDto(edxLabInformationDto);
            phcContainer.setObservationDto(observationDto);
            phcContainer.setNbsInterfaceId(nbsInterfaceModel.getNbsInterfaceUid());
            gson = new Gson();
            String jsonString = gson.toJson(phcContainer);
            kafkaManagerProducer.sendDataPhc(jsonString);

            //return result;
        } catch (Exception e) {
            if (nbsInterfaceModel != null) {
                nbsInterfaceModel.setRecordStatusCd(DpConstant.DP_FAILURE_STEP_1);
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
            } else if (
                    (edxLabInformationDto.getPageActContainer() != null
                            || edxLabInformationDto.getPamContainer() != null)
                            && edxLabInformationDto.isInvestigationSuccessfullyCreated()) {
                if (edxLabInformationDto.isNotificationMissingFields()) {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_8);
                } else {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_10);
                }
            }


            // error check function in here to create the details message

            logger.error("Exception EdxLabHelper.getUnProcessedELR processing exception: " + e, e);

            if (edxLabInformationDto.getErrorText() == null) {
                //if error text is null, that means lab was not created due to unexpected error.
                if (e.getMessage().contains(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG)
                        || e.getMessage().contains(EdxELRConstant.ORACLE_FIELD_TRUNCATION_ERROR_MSG)) {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_18);
                    edxLabInformationDto.setFieldTruncationError(true);
                    edxLabInformationDto.setSystemException(false);
                    try {
                        // Extract table name from Exception message, first find table name and ignore text after it.
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));
                        String exceptionMessage = errors.toString();
                        //Patient is not created so setting patient_parent_id to 0
                        edxLabInformationDto.setPersonParentUid(0);
                        //No need to create success message "The Ethnicity code provided in the message is not found in the SRT database. The code is saved to the NBS." in case of exception scenario
                        edxLabInformationDto.setEthnicityCodeTranslated(true);
                        String textToLookFor = "Table Name : ";
                        String tableName = exceptionMessage.substring(exceptionMessage.indexOf(textToLookFor) + textToLookFor.length());
                        tableName = tableName.substring(0, tableName.indexOf(" "));
                        detailedMsg = "SQLException while inserting into " + tableName + "\n " + accessionNumberToAppend + "\n " + exceptionMessage;
                        detailedMsg = detailedMsg.substring(0, Math.min(detailedMsg.length(), 2000));
                    } catch (Exception ex) {
                        logger.error("Exception while formatting exception message for Activity Log: " + ex.getMessage(), ex);
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
                        problemDateInfoSubstring = problemDateInfoSubstring.substring(0, problemDateInfoSubstring.indexOf(EdxELRConstant.DATE_VALIDATION_END_DELIMITER1));
                        detailedMsg = problemDateInfoSubstring + "\n " + accessionNumberToAppend + "\n" + e.getMessage();
                        detailedMsg = detailedMsg.substring(0, Math.min(detailedMsg.length(), 2000));
                    } catch (Exception ex) {
                        logger.error("Exception while formatting date exception message for Activity Log: " + ex.getMessage(), ex);
                    }
                } else {
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_16);
                    try {
                        //Patient is not created so setting patient_parent_id to 0
                        edxLabInformationDto.setPersonParentUid(0);
                        //No need to create success message for Ethnicity code provided in the message is not found in the SRT database. The code is saved to the NBS." in case of exception scenario
                        edxLabInformationDto.setEthnicityCodeTranslated(true);
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));
                        String exceptionMessage = accessionNumberToAppend + "\n" + errors;
                        detailedMsg = exceptionMessage.substring(0, Math.min(exceptionMessage.length(), 2000));
                    } catch (Exception ex) {
                        logger.error("Exception while formatting exception message for Activity Log: " + ex.getMessage(), ex);
                    }
                }
            }
            if (edxLabInformationDto.isInvestigationMissingFields() || edxLabInformationDto.isNotificationMissingFields() || (edxLabInformationDto.getErrorText() != null && edxLabInformationDto.getErrorText().equals(EdxELRConstant.ELR_MASTER_LOG_ID_10))) {
                edxLabInformationDto.setSystemException(false);
            }

            if (edxLabInformationDto.isReflexResultedTestCdMissing()
                    || edxLabInformationDto.isResultedTestNameMissing()
                    || edxLabInformationDto.isOrderTestNameMissing()
                    || edxLabInformationDto.isReasonforStudyCdMissing()) {
                try {
                    String exceptionMsg = e.getMessage();
                    String textToLookFor = "XMLElementName: ";
                    detailedMsg = "Blank identifiers in segments " + exceptionMsg.substring(exceptionMsg.indexOf(textToLookFor) + textToLookFor.length()) + "\n\n" + accessionNumberToAppend;
                    detailedMsg = detailedMsg.substring(0, Math.min(detailedMsg.length(), 2000));
                } catch (Exception ex) {
                    logger.error("Exception while formatting exception message for Activity Log: " + ex.getMessage(), ex);
                }
            }

            //throw new DataProcessingConsumerException(e.getMessage(), result);

        } finally {
            if (nbsInterfaceModel != null) {
                edxLogService.updateActivityLogDT(nbsInterfaceModel, edxLabInformationDto);
                edxLogService.addActivityDetailLogs(edxLabInformationDto, detailedMsg);
                gson = new Gson();
                String jsonString = gson.toJson(edxLabInformationDto.getEdxActivityLogDto());
                kafkaManagerProducer.sendDataEdxActivityLog(jsonString);
            }
        }
    }

    private void requiredFieldError(String errorTxt, EdxLabInformationDto edxLabInformationDT) throws DataProcessingException {
        if (errorTxt != null) {
            edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_5);
            if (edxLabInformationDT.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails() == null) {
                edxLabInformationDT.getEdxActivityLogDto().setEDXActivityLogDTWithVocabDetails(
                        new ArrayList<>());
            }

            //TODO: LOGGING
//            setActivityDetailLog((ArrayList<Object>) edxLabInformationDT.getEdxActivityLogDto().getEDXActivityLogDTWithVocabDetails(),
//                    String.valueOf(edxLabInformationDT.getLocalId()),
//                    EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure, errorTxt);

            edxLabInformationDT.setInvestigationMissingFields(true);
            throw new DataProcessingException("MISSING REQUIRED FIELDS: " + errorTxt);
        }
    }

}
