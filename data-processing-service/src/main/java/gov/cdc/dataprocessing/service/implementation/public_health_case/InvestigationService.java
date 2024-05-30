package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.interfaces.ReportSummaryInterface;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.model.dto.notification.UpdatedNotificationDto;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Lab_Summary_ForWorkUp_New;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.LabTestRepository;
import gov.cdc.dataprocessing.service.implementation.act.ActRelationshipService;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.interfaces.material.IMaterialService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationSummaryService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IContactSummaryService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.ILdfService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class InvestigationService implements IInvestigationService {
    private static final Logger logger = LoggerFactory.getLogger(InvestigationService.class);

    private final PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;
    private final PatientRepositoryUtil patientRepositoryUtil;

    private final IMaterialService materialService;

    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final ObservationRepositoryUtil observationRepositoryUtil;
    private final ObservationRepository observationRepository;
    private final IRetrieveSummaryService retrieveSummaryService;
    private final ActRelationshipService actRelationshipService;

    private final INotificationService notificationService;
    private final IObservationSummaryService observationSummaryService;
    private final QueryHelper queryHelper;
    private final Observation_SummaryRepository observationSummaryRepository;
    private final IContactSummaryService contactSummaryService;
    private final CachingValueService cachingValueService;
    private final ILdfService ldfService;
    private final LabTestRepository labTestRepository;

    public InvestigationService(PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil,
                                OrganizationRepositoryUtil organizationRepositoryUtil,
                                PatientRepositoryUtil patientRepositoryUtil,
                                IMaterialService materialService,
                                PrepareAssocModelHelper prepareAssocModelHelper,
                                ObservationRepositoryUtil observationRepositoryUtil,
                                ObservationRepository observationRepository, IRetrieveSummaryService retrieveSummaryService,
                                ActRelationshipService actRelationshipService,
                                INotificationService notificationService,
                                IObservationSummaryService observationSummaryService,
                                QueryHelper queryHelper,
                                Observation_SummaryRepository observationSummaryRepository,
                                IContactSummaryService contactSummaryService,
                                CachingValueService cachingValueService,
                                ILdfService ldfService,
                                LabTestRepository labTestRepository) {
        this.publicHealthCaseRepositoryUtil = publicHealthCaseRepositoryUtil;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.materialService = materialService;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.observationRepositoryUtil = observationRepositoryUtil;
        this.observationRepository = observationRepository;
        this.retrieveSummaryService = retrieveSummaryService;
        this.actRelationshipService = actRelationshipService;
        this.notificationService = notificationService;
        this.observationSummaryService = observationSummaryService;
        this.queryHelper = queryHelper;
        this.observationSummaryRepository = observationSummaryRepository;
        this.contactSummaryService = contactSummaryService;
        this.cachingValueService = cachingValueService;
        this.ldfService = ldfService;
        this.labTestRepository = labTestRepository;
    }

    @Transactional
    public void setAssociations(Long investigationUID,
                                Collection<LabReportSummaryContainer>  reportSumVOCollection,
                                Collection<Object>  vaccinationSummaryVOCollection,
                                Collection<Object>  summaryDTColl,
                                Collection<Object> treatmentSumColl,
                                Boolean isNNDResendCheckRequired) throws DataProcessingException {
        InvestigationContainer invVO = new InvestigationContainer();
        try {
            if(reportSumVOCollection!=null && !reportSumVOCollection.isEmpty() ){
                setObservationAssociationsImpl(investigationUID, reportSumVOCollection);
            }

            // THESE WONT BE RELEVANT IN THIS FLOW
            /*
            if(vaccinationSummaryVOCollection!=null && !vaccinationSummaryVOCollection.isEmpty() ){
                manageAutoAssc.setVaccinationAssociationsImpl(investigationUID, vaccinationSummaryVOCollection);
            }
            if(summaryDTColl!=null && !summaryDTColl.isEmpty()){
                manageAutoAssc.setDocumentAssociationsImpl(investigationUID, summaryDTColl);
            }
            if(treatmentSumColl!=null && !treatmentSumColl.isEmpty()){
                manageAutoAssc.setTreatmentAssociationsImpl(investigationUID,NEDSSConstant.INVESTIGATION, treatmentSumColl);
            }
            */
            if(isNNDResendCheckRequired){
                 invVO = getInvestigationProxy(investigationUID);
                updateAutoResendNotificationsAsync(invVO);
            }
            if(reportSumVOCollection!=null && reportSumVOCollection.size()>0){
                retrieveSummaryService.checkBeforeCreateAndStoreMessageLogDTCollection(investigationUID, reportSumVOCollection);
            }


        }catch (Exception e) {
            NNDActivityLogDto nndActivityLogDT = new  NNDActivityLogDto();
            String phcLocalId = invVO.getThePublicHealthCaseContainer().getThePublicHealthCaseDto().getLocalId();
            nndActivityLogDT.setErrorMessageTxt(e.toString());
            if (phcLocalId!=null)
            {
                nndActivityLogDT.setLocalId(phcLocalId);
            }
            else
            {
                nndActivityLogDT.setLocalId("N/A");
            }
            //catch & store auto resend notifications exceptions in NNDActivityLog table

            //TODO: LOGGING PIPELINE
            //n1.persistNNDActivityLog(nndActivityLogDT);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    /**
     * This method Associates or disassociates the observation(LAb or MORB) to Investigation from
     * the manage observation page
     * @param investigationUID -- The UID for the investigation to which observation is to be associated or disassociates
     */

    private void setObservationAssociationsImpl(Long investigationUID, Collection<LabReportSummaryContainer> reportSumVOCollection) throws DataProcessingException {
        // false flag indicates that investigation already exists and LAB/MORB
        // is associated to it using manage associations page
        setObservationAssociationsImpl(investigationUID, reportSumVOCollection, false);
    }

    /**
     * This method Associates the observation(LAb or MORB) to Investigation 
     * @param investigationUID -- The UID for the investigation to which observation is to be associated or disassociates
     * @param invFromEvent - flag to indicates if lab or morb report is the reactor for investigation.
     */

    public void setObservationAssociationsImpl(Long investigationUID, Collection<LabReportSummaryContainer>  reportSumVOCollection, boolean invFromEvent) throws DataProcessingException {


        PublicHealthCaseDto phcDT =  publicHealthCaseRepositoryUtil.findPublicHealthCase(investigationUID);

        try
        {
            //For each report summary vo
            if(!reportSumVOCollection.isEmpty())
            {
                for (LabReportSummaryContainer reportSumVO : reportSumVOCollection) {
                    ActRelationshipDto actRelationshipDT = null;
                    RootDtoInterface rootDT = null;

                    //Gets and checks whether any association change; if changed, do something, else go next one
                    boolean isTouched = reportSumVO.getIsTouched();
                    if (!isTouched) {
                        continue;
                    }

                    actRelationshipDT = new ActRelationshipDto();
                    //Sets the properties of ActRelationshipDT object
                    actRelationshipDT.setTargetActUid(investigationUID);
                    actRelationshipDT.setSourceActUid(reportSumVO.getObservationUid());
                    actRelationshipDT.setFromTime(reportSumVO.getActivityFromTime());
                    actRelationshipDT.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
                    //Set from time same as investigation create time if act relationship is created while creating investigation from lab or morbidity report
                    if (invFromEvent) {
                        actRelationshipDT.setFromTime(phcDT.getAddTime());
                    }
                    actRelationshipDT.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
                    actRelationshipDT.setTargetClassCd(NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE);
                    //actRelationshipDT.setStatusTime(new Timestamp(new java.util.Date().getTime()));
                    boolean reportFromDoc = false;
                    if (reportSumVO instanceof LabReportSummaryContainer) {
                        actRelationshipDT.setTypeCd(NEDSSConstant.LAB_DISPALY_FORM);
                        if (reportSumVO.isLabFromDoc()) {
                            reportFromDoc = true;
                        }
                        if (invFromEvent) {
                            actRelationshipDT.setAddReasonCd(reportSumVO.getProcessingDecisionCd());
                        }
                    }
                    /*
                    if(reportSumVO instanceof MorbReportSummaryVO){
                        actRelationshipDT.setTypeCd(NEDSSConstant.DISPLAY_FORM);
                        if(((MorbReportSummaryVO)reportSumVO).isMorbFromDoc())
                        {
                            reportFromDoc=true;
                        }
                        if(invFromEvent)
                        {
                            actRelationshipDT.setAddReasonCd(((MorbReportSummaryVO)reportSumVO).getProcessingDecisionCd());
                        }
                    }
                    */


                    if (reportSumVO.getIsAssociated()) {
                        actRelationshipDT.setRecordStatusCd(NEDSSConstant.ACTIVE);
                        actRelationshipDT.setStatusCd(NEDSSConstant.A);
                    } else {
                        actRelationshipDT.setRecordStatusCd(NEDSSConstant.INACTIVE);
                        actRelationshipDT.setStatusCd(NEDSSConstant.I);
                    }

                    actRelationshipDT = prepareAssocModelHelper.prepareAssocDTForActRelationship(actRelationshipDT);
                    // needs to be done here as prepareAssocDT will always set dirty flag true
                    if (reportSumVO.getIsAssociated())
                    {
                        actRelationshipDT.setItNew(true);
                        actRelationshipDT.setItDirty(false);
                    }
                    else
                    {
                        actRelationshipDT.setItDelete(true);
                        actRelationshipDT.setItDirty(false);
                    }
                    observationRepositoryUtil.saveActRelationship(actRelationshipDT);

                    if (!reportFromDoc) {
                        //Obtains the core observation object
                        var obs = observationRepositoryUtil.loadObject(reportSumVO.getObservationUid());
                        ObservationDto obsDT = obs.getTheObservationDto();
                        //Starts persist observationDT
                        if (reportSumVO.getIsAssociated())
                        {
                            obsDT.setItDirty(true);
                            String businessObjLookupName = "";
                            String businessTriggerCd = "";
                            String tableName = NEDSSConstant.OBSERVATION;
                            String moduleCd = NEDSSConstant.BASE;
                            if (reportSumVO instanceof LabReportSummaryContainer)
                            {
                                businessObjLookupName = NEDSSConstant.OBSERVATIONLABREPORT;
                                businessTriggerCd = NEDSSConstant.OBS_LAB_ASC;
                            }
                            /*
                            if(reportSumVO instanceof MorbReportSummaryVO)
                            {
                                businessObjLookupName=NEDSSConstant.OBSERVATIONMORBIDITYREPORT;
                                businessTriggerCd=NEDSSConstant.OBS_MORB_ASC;
                            }
                            */

                            rootDT = prepareAssocModelHelper.prepareVO(obsDT, businessObjLookupName, businessTriggerCd, tableName, moduleCd, obsDT.getVersionCtrlNbr());
                        }

                        if (!reportSumVO.getIsAssociated())
                        {
                            obsDT.setItDirty(true);
                            String businessObjLookupName = "";
                            String businessTriggerCd = "";
                            String tableName = NEDSSConstant.OBSERVATION;
                            String moduleCd = NEDSSConstant.BASE;
                            if (reportSumVO instanceof LabReportSummaryContainer)
                            {
                                Collection<ActRelationshipDto> actRelColl = actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(reportSumVO.getObservationUid(), "LabReport");
                                businessObjLookupName = NEDSSConstant.OBSERVATIONLABREPORT;
                                if (actRelColl != null && actRelColl.size() > 0)
                                {
                                    businessTriggerCd = NEDSSConstant.OBS_LAB_DIS_ASC;
                                }
                                else
                                {
                                    businessTriggerCd = NEDSSConstant.OBS_LAB_UNPROCESS;
                                    // if  Lab does not have other associations it will be sent back into needing review queue
                                }
                            }
                            /*
                            if(reportSumVO instanceof MorbReportSummaryVO)
                            {
                                businessObjLookupName=NEDSSConstant.OBSERVATIONMORBIDITYREPORT;
                                businessTriggerCd=NEDSSConstant.OBS_MORB_UNPROCESS;
                            }
                            */
                            rootDT = prepareAssocModelHelper.prepareVO(obsDT, businessObjLookupName, businessTriggerCd, tableName, moduleCd, obsDT.getVersionCtrlNbr());
                        }
                        obsDT = (ObservationDto) rootDT;
                        //set the previous entered processing decision to null
                        obsDT.setProcessingDecisionCd(null);
                        observationRepositoryUtil.setObservationInfo(obsDT);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new DataProcessingException(e.getMessage());
        }
    }




    public void updateAutoResendNotificationsAsync(BaseContainer v)
    {
        try{
            updateAutoResendNotifications(v);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Nothing in here for LabResult Proxy Yet
     * */
    private void updateAutoResendNotifications(BaseContainer vo) throws DataProcessingException
    {
        try {
            logger.info("enter NNDMessageSenderHelper.updateAutoResendNotifications--------------");
            if(
                //!(vo instanceof VaccinationProxyVO)
                    !(vo instanceof LabResultProxyContainer)
                            //&&!(vo instanceof MorbidityProxyVO)
                            &&!(vo instanceof InvestigationContainer)
                            &&!(vo instanceof PageActProxyContainer)
                            &&!(vo instanceof PamProxyContainer)
//            &&!(vo instanceof SummaryReportProxyVO)
            )
            {
                throw new DataProcessingException("vo not instance of VaccinationProxyVO,LabResultProxyVO, or MorbidityProxyVO,PamProxyVO, SummaryReportProxyVO");
            }
            Collection<Object>  notSumVOColl =null;
            PublicHealthCaseDto phcDT = null;


            //TODO: LAB RESULT WONT HIT ANY OF THESE

            if(
                    vo instanceof InvestigationContainer
                            || vo instanceof PamProxyContainer
                            ||  vo instanceof PageActProxyContainer
//                ||  vo instanceof SummaryReportProxyVO
            ){
                if(vo instanceof InvestigationContainer)
                {
                    InvestigationContainer invVO = (InvestigationContainer)vo;
                    phcDT = invVO.thePublicHealthCaseContainer.getThePublicHealthCaseDto();
                    notSumVOColl = invVO.getTheNotificationSummaryVOCollection();
                }
//            else if(vo instanceof PamProxyContainer)
//            {
//                PamProxyVO pamVO = (PamProxyVO)vo;
//                phcDT = pamVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
//                notSumVOColl = pamVO.getTheNotificationSummaryVOCollection();
//            }
//            else if (vo instanceof LabResultProxyContainer)
//            {
//                NNDAutoResendDAOImpl nndAutoResendDAO = new NNDAutoResendDAOImpl();
//                Collection<Object>  theNotificationCollection  = nndAutoResendDAO.getAutoResendNotificationSummaries(getActClassCd(vo), getTypeCd(vo), getRootUid(vo));
//                Iterator<Object>  notIter = theNotificationCollection.iterator();
//                while(notIter.hasNext()){
//                    NotificationSummaryVO notSumVO = (NotificationSummaryVO)notIter.next();
//                    updateNotification(false, notSumVO.getNotificationUid(),notSumVO.getCd(),notSumVO.getCaseClassCd(),notSumVO.getProgAreaCd(),notSumVO.getJurisdictionCd(),notSumVO.getSharedInd(), false, nbsSecurityObj);
//                }
//            }
//            else if(vo instanceof PageActProxyContainer)
//            {
//                PageActProxyContainer pageActProxyVO= (PageActProxyContainer)vo;
//                phcDT = pageActProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
//                notSumVOColl = pageActProxyVO.getTheNotificationSummaryVOCollection();
//            }
//            else if (vo instanceof SummaryReportProxyVO)
//            {
//                SummaryReportProxyVO summaryReportProxyVO = (SummaryReportProxyVO)vo;
//                phcDT = summaryReportProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto();
//                notSumVOColl = summaryReportProxyVO.getTheNotificationVOCollection();
//                Iterator<Object>  notSumIter =  notSumVOColl.iterator();
//                while(notSumIter.hasNext()){
//                    NotificationContainer notVO = (NotificationContainer)notSumIter.next();
//                    Long notificationUid = notVO.getTheNotificationDT().getNotificationUid();
//                    String phcCd = phcDT.getCd();
//                    String phcClassCd = phcDT.getCaseClassCd();
//                    String progAreaCd = phcDT.getProgAreaCd();
//                    String jurisdictionCd = phcDT.getJurisdictionCd();
//                    String sharedInd = phcDT.getSharedInd();
//                    // retrieve the status change
//                    boolean caseStatusChange = phcDT.isCaseStatusDirty();
//                    updateNotification(true, notificationUid,phcCd,phcClassCd,progAreaCd,jurisdictionCd,sharedInd, caseStatusChange, nbsSecurityObj);
//                }
//            }
                if(
                        vo instanceof InvestigationContainer
                                || vo instanceof PamProxyContainer
                                || vo instanceof PageActProxyContainer
                )
                {
                    if(notSumVOColl!=null && notSumVOColl.size()>0){
                        Iterator<Object>  notSumIter =  notSumVOColl.iterator();
                        while(notSumIter.hasNext()){
                            NotificationSummaryContainer notSummaryVO = (NotificationSummaryContainer)notSumIter.next();
                            if(notSummaryVO.getIsHistory().equals("F") && !notSummaryVO.getAutoResendInd().equals("F")){
                                Long notificationUid = notSummaryVO.getNotificationUid();
                                String phcCd = phcDT.getCd();
                                String phcClassCd = phcDT.getCaseClassCd();
                                String progAreaCd = phcDT.getProgAreaCd();
                                String jurisdictionCd = phcDT.getJurisdictionCd();
                                String sharedInd = phcDT.getSharedInd();

                                // retrieve the status change
                                boolean caseStatusChange = phcDT.isCaseStatusDirty();
                                updateNotification(false, notificationUid,phcCd,phcClassCd,progAreaCd,jurisdictionCd,sharedInd, caseStatusChange);

                            }
                        }
                    }
                }

            }
//        else if(vo instanceof VaccinationProxyVO
//                || vo instanceof MorbidityProxyVO)
//        {
//            NNDAutoResendDAOImpl nndAutoResendDAO = new NNDAutoResendDAOImpl();
//            Collection<Object>  theNotificationCollection  = nndAutoResendDAO.getAutoResendNotificationSummaries(getActClassCd(vo), getTypeCd(vo), getRootUid(vo));
//            Iterator<Object>  notIter = theNotificationCollection.iterator();
//            while(notIter.hasNext()){
//                NotificationSummaryVO notSumVO = (NotificationSummaryVO)notIter.next();
//                updateNotification(false, notSumVO.getNotificationUid(),notSumVO.getCd(),notSumVO.getCaseClassCd(),notSumVO.getProgAreaCd(),notSumVO.getJurisdictionCd(),notSumVO.getSharedInd(), false, nbsSecurityObj);
//            }
//        }
//        logger.info("finish NNDMessageSenderHelper.updateAutoResendNotifications--------------");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private  void updateNotification(boolean isSummaryCase, Long notificationUid, String phcCd,
                                   String phcClassCd, String progAreaCd, String jurisdictionCd,
                                   String sharedInd, boolean caseStatusChange) throws DataProcessingException {
        //TODO: PERMISSION
        boolean checkNotificationPermission = true;//nbsSecurityObj.getPermission(NBSBOLookup.NOTIFICATION, NBSOperationLookup.CREATE,progAreaCd,jurisdictionCd,sharedInd);
        boolean checkNotificationPermission1 = true;//nbsSecurityObj.getPermission(NBSBOLookup.NOTIFICATION, NBSOperationLookup.CREATENEEDSAPPROVAL,progAreaCd,jurisdictionCd,sharedInd);
        String businessTriggerCd = null;
        if(isSummaryCase){
            businessTriggerCd = NEDSSConstant.NOT_CR_APR;
        }
        else if(!checkNotificationPermission && !checkNotificationPermission1){
            logger.info("No create notification permissions for updateNotification");
            throw new DataProcessingException("NO CREATE NOTIFICATION PERMISSIONS for updateNotification");
        }
        else
        {
            // In auto resend scenario, the change to investigation or
            // any associated object puts the notification in APPROVED queue

            businessTriggerCd = NEDSSConstant.NOT_CR_APR;
        }

        Collection<Object>  notificationVOCollection  = null;

        try
        {
            var notification = notificationService.getNotificationById(notificationUid);
            NotificationContainer notificationContainer = new NotificationContainer();
            if (notification != null) {
                notificationContainer.setTheNotificationDT(notification);
            }
            NotificationDto newNotificationDT = null;
            NotificationDto notificationDT = notificationContainer.getTheNotificationDT();
            notificationDT.setProgAreaCd(progAreaCd);
            notificationDT.setJurisdictionCd(jurisdictionCd);
            notificationDT.setCaseConditionCd(phcCd);
            notificationDT.setSharedInd(sharedInd);
            notificationDT.setCaseClassCd(phcClassCd);
            notificationContainer.setItDirty(true);
            notificationDT.setItDirty(true);

            //retreive the new NotificationDT generated by PrepareVOUtils
            newNotificationDT = (NotificationDto) prepareAssocModelHelper.prepareVO(
                    notificationDT, NBSBOLookup.NOTIFICATION, businessTriggerCd,
                    "Notification", NEDSSConstant.BASE, notificationDT.getVersionCtrlNbr());

            //replace old NotificationDT in NotificationContainer with the new NotificationDT
            notificationContainer.setTheNotificationDT(newNotificationDT);

            // If the user has "NEEDS APPROVAL" permissions and the notification
            // is in AUTO_RESEND status, new record is created for review.
            // This record is visible in Updated Notifications Queue

            if(checkNotificationPermission1 &&
                    (notificationDT.getAutoResendInd().equalsIgnoreCase("T"))){
                UpdatedNotificationDto updatedNotification = new UpdatedNotificationDto();

                updatedNotification.setAddTime(new Timestamp(System.currentTimeMillis()));
                updatedNotification.setAddUserId(AuthUtil.authUser.getAuthUserUid());
                updatedNotification.setCaseStatusChg(caseStatusChange);
                updatedNotification.setItNew(true);
                updatedNotification.setNotificationUid(notificationDT.getNotificationUid());
                updatedNotification.setStatusCd("A");
                updatedNotification.setCaseClassCd(notificationDT.getCaseClassCd());
                notificationContainer.setTheUpdatedNotificationDto(updatedNotification);
            }

            Long newNotficationUid = notificationService.saveNotification(notificationContainer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.error("Error calling ActController.setNotification() " + e.getMessage());
            throw new DataProcessingException("Error in calling ActControllerEJB.setNotification()" + e.toString(), e);
        }

        logger.info("updateNotification on NNDMessageSenderHelper complete");
    }//updateNotification




    private InvestigationContainer getInvestigationProxy(Long publicHealthCaseUID) throws DataProcessingException {

        return getInvestigationProxyLite(publicHealthCaseUID, false);
    }

    private InvestigationContainer getInvestigationProxyLite(Long publicHealthCaseUID, boolean lite) throws DataProcessingException {



        var investigationProxyVO = new InvestigationContainer();

        PublicHealthCaseDto thePublicHealthCaseDto = null;
        PublicHealthCaseContainer thePublicHealthCaseContainer = null;

        ArrayList<Object>  theParticipationDTCollection;
        ArrayList<Object> theRoleDTCollection;

        ArrayList<Object>  thePersonVOCollection  = new ArrayList<Object> (); // Person (VO)
        ArrayList<Object>  theOrganizationVOCollection  = new ArrayList<Object> (); // Organization (VO)
        ArrayList<Object>  theMaterialVOCollection  = new ArrayList<Object> (); // Material (VO)
        ArrayList<ObservationContainer>  theObservationVOCollection  = new ArrayList<> (); // Observation (VO)
        ArrayList<Object>  theInterventionVOCollection  = new ArrayList<Object> (); // Itervention (VO)
        ArrayList<Object>  theEntityGroupVOCollection  = new ArrayList<Object> (); // Group (VO)
        ArrayList<Object>  theNonPersonLivingSubjectVOCollection  = new ArrayList<Object> (); // NPLS (VO)
        ArrayList<Object>  thePlaceVOCollection  = new ArrayList<Object> (); // Place (VO)
        //ArrayList<Object> theNotificationVOCollection  = new ArrayList<Object> ();		// Notification (VO)
        ArrayList<Object>  theReferralVOCollection  = new ArrayList<Object> (); // Referral (VO)
        ArrayList<Object>  thePatientEncounterVOCollection  = new ArrayList<Object> (); // PatientEncounter (VO)
        ArrayList<Object>  theClinicalDocumentVOCollection  = new ArrayList<Object> (); // Clinical Document (VO)

        // Summary Collections
        ArrayList<Object>  theObservationSummaryVOCollection  = new ArrayList<Object> ();
        ArrayList<Object>  theVaccinationSummaryVOCollection  = new ArrayList<Object> ();
        ArrayList<Object>  theNotificationSummaryVOCollection  = new ArrayList<Object> ();
        ArrayList<StateDefinedFieldDataDto>  theStateDefinedFieldDTCollection  = new ArrayList<> ();
        ArrayList<Object>  theTreatmentSummaryVOCollection  = new ArrayList<Object> ();
        ArrayList<Object>  theDocumentSummaryVOCollection  = new ArrayList<Object> ();

        Object theLookedUpObject;

        try {
            
            // Step 1: Get the Pubic Health Case
            thePublicHealthCaseContainer = publicHealthCaseRepositoryUtil.loadObject(publicHealthCaseUID);

            // TODO: Get user name from PHC
            //thePublicHealthCaseContainer.getThePublicHealthCaseDto().setAddUserName(helper.getUserName(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getAddUserId()));
            thePublicHealthCaseContainer.getThePublicHealthCaseDto().setAddUserName(AuthUtil.authUser.getUserId());

            // TODO: Get user name from PHC
            //thePublicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgUserName(helper.getUserName(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getLastChgUserId()));
            thePublicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgUserName(AuthUtil.authUser.getUserId());

            thePublicHealthCaseDto = thePublicHealthCaseContainer.getThePublicHealthCaseDto();

            Long PatientGroupID = thePublicHealthCaseDto.getPatientGroupId();
            if (PatientGroupID != null) {
                var entityGrp = publicHealthCaseRepositoryUtil.getEntityGroup(thePublicHealthCaseDto.getPatientGroupId());
                if (entityGrp != null) {
                    EntityGroupContainer entityGroupContainer = new EntityGroupContainer();
                    entityGroupContainer.setTheEntityGroupDT(entityGrp);
                    theEntityGroupVOCollection.add(entityGroupContainer);
                }
            }
            logger.debug("PatientGroupID = " + PatientGroupID);
            String strTypeCd;
            String strClassCd;
            String recordStatusCd = "";
            Long nEntityID;
            ParticipationDto participationDT = null;

            Iterator<ParticipationDto>  participationIterator = thePublicHealthCaseContainer.
                    getTheParticipationDTCollection().iterator();
            logger.debug("ParticipationDTCollection() = " +
                    thePublicHealthCaseContainer.getTheParticipationDTCollection());

            // Populate the Entity collections with the results
            while (participationIterator.hasNext()) {
                participationDT = (ParticipationDto) participationIterator.next();
                nEntityID = participationDT.getSubjectEntityUid();
                strClassCd = participationDT.getSubjectClassCd();
                strTypeCd = participationDT.getTypeCd();
                recordStatusCd = participationDT.getRecordStatusCd();
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.PLACE) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) && !lite) {

                    var plc = publicHealthCaseRepositoryUtil.getPlace(nEntityID);
                    if (plc != null) {
                        PlaceContainer placeContainer = new PlaceContainer();
                        placeContainer.setThePlaceDT(plc);
                        thePlaceVOCollection.add(placeContainer);
                    }
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.
                                NONPERSONLIVINGSUBJECT) ==
                                0 && recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    var nonpDto = publicHealthCaseRepositoryUtil.getNonPersonLivingSubject(nEntityID);
                    if (nonpDto != null) {
                        NonPersonLivingSubjectContainer nonPersonLivingSubjectContainer = new NonPersonLivingSubjectContainer();
                        nonPersonLivingSubjectContainer.setTheNonPersonLivingSubjectDT(nonpDto);
                        theNonPersonLivingSubjectVOCollection.add(nonPersonLivingSubjectContainer);

                    }
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.ORGANIZATION) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    theOrganizationVOCollection.add(organizationRepositoryUtil.loadObject(nEntityID, null));
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.PERSON) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    thePersonVOCollection.add(patientRepositoryUtil.loadPerson(nEntityID));
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.MATERIAL) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    theMaterialVOCollection.add(materialService.loadMaterialObject(nEntityID));
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.ENTITYGROUP) == 0 &&
                        recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {

                    var entityGrp = publicHealthCaseRepositoryUtil.getEntityGroup(nEntityID);
                    if (entityGrp != null) {
                        EntityGroupContainer entityGroupContainer = new EntityGroupContainer();
                        entityGroupContainer.setTheEntityGroupDT(entityGrp);
                        theEntityGroupVOCollection.add(entityGroupContainer);
                    }


                    continue;
                }
                if (nEntityID == null || strClassCd == null || strClassCd.length() == 0) {
                    continue;
                }
            }

            ActRelationshipDto actRelationshipDT = null;
            //Get the Vaccinations for a PublicHealthCase/Investigation
            Iterator<ActRelationshipDto>  actRelationshipIterator = thePublicHealthCaseContainer.
                    getTheActRelationshipDTCollection().iterator();

            // Populate the ACT collections in the results
            while (actRelationshipIterator.hasNext()) {
                actRelationshipDT = (ActRelationshipDto) actRelationshipIterator.next();
                logger.debug("inside while actUid: " +
                        actRelationshipDT.getTargetActUid() + " observationUid: " +
                        actRelationshipDT.getSourceActUid());
                Long nSourceActID = actRelationshipDT.getSourceActUid();
                strClassCd = actRelationshipDT.getSourceClassCd();
                strTypeCd = actRelationshipDT.getTypeCd();
                recordStatusCd = actRelationshipDT.getRecordStatusCd();

                /*
                if (!lite && strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.
                                INTERVENTION_CLASS_CODE) ==
                                0
                        && recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)
                        && strTypeCd != null && !strTypeCd.equals("1180")) {
                    InterventionContainer interventionVO = actController.getIntervention(
                            nSourceActID, nbsSecurityObj);
                    theInterventionVOCollection.add(interventionVO);
                    InterventionDto intDT = interventionVO.getTheInterventionDto();

                    if (intDT.getCd() != null &&
                            intDT.getCd().compareToIgnoreCase("VACCINES/ANTISERA") == 0) {
                        Collection<Object>  intPartDTs = interventionVO.
                                getTheParticipationDTCollection();
                        Iterator<Object>  intPartIter = intPartDTs.iterator();
                        while (intPartIter.hasNext()) {
                            ParticipationDT dt = (ParticipationDT) intPartIter.next();

                            if (dt.getTypeCd() != null &&
                                    dt.getTypeCd() ==
                                            NEDSSConstant.VACCINATION_ADMINISTERED_TYPE_CODE) {
                                VaccinationSummaryVO vaccinationSummaryVO = new
                                        VaccinationSummaryVO();
                                vaccinationSummaryVO.setActivityFromTime(intDT.
                                        getActivityFromTime());
                                vaccinationSummaryVO.setInterventionUid(intDT.
                                        getInterventionUid());
                                vaccinationSummaryVO.setLocalId(intDT.getLocalId());
                                MaterialDT materialDT = entityController.getMaterialInfo(dt.
                                        getSubjectEntityUid(), nbsSecurityObj);
                                vaccinationSummaryVO.setVaccineAdministered(materialDT.getNm());
                                //theVaccinationSummaryVOCollection.add(vaccinationSummaryVO);
                            }
                        }
                    }
                    continue;
                }
                */
                if (strClassCd != null && strClassCd.compareToIgnoreCase(NEDSSConstant.CLINICAL_DOCUMENT_CLASS_CODE) == 0
                        && recordStatusCd != null && recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {

                    var doc = publicHealthCaseRepositoryUtil.getClinicalDocument(nSourceActID);
                    if (doc != null){
                        ClinicalDocumentContainer documentContainer = new ClinicalDocumentContainer();
                        documentContainer.setTheClinicalDocumentDT(doc);
                        theClinicalDocumentVOCollection.add(documentContainer);

                    }
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.REFERRAL_CLASS_CODE) == 0
                        && recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    var ref = publicHealthCaseRepositoryUtil.getReferral(nSourceActID);
                    if (ref != null) {
                        ReferralContainer referralContainer = new ReferralContainer();
                        referralContainer.setTheReferralDT(ref);
                        theReferralVOCollection.add(referralContainer);
                    }
                    continue;
                }
                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.
                                PATIENT_ENCOUNTER_CLASS_CODE) == 0
                        && recordStatusCd != null &&
                        recordStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    var encounter = publicHealthCaseRepositoryUtil.getPatientEncounter(nSourceActID);
                    if (encounter != null) {
                        PatientEncounterContainer patientEncounterContainer = new PatientEncounterContainer();
                        patientEncounterContainer.setThePatientEncounterDT(encounter);
                        thePatientEncounterVOCollection.add(patientEncounterContainer);

                    }
                    continue;
                }

                if (strClassCd != null &&
                        strClassCd.compareToIgnoreCase(NEDSSConstant.
                                OBSERVATION_CLASS_CODE) ==
                                0 && strTypeCd != null &&
                        strTypeCd.equals(NEDSSConstant.PHC_INV_FORM)) {
                    ObservationContainer parentObservationVO = observationRepositoryUtil.loadObject(nSourceActID);
                    theObservationVOCollection  = (ArrayList<ObservationContainer> ) observationRepositoryUtil.retrieveObservationQuestion(nSourceActID);
                    //  //##!! System.out.println("Size of VO in retrieveObservationQA : " +theObservationVOCollection.size() );
                    theObservationVOCollection.add(parentObservationVO);
                    continue;
                }
                if (nSourceActID == null || strClassCd == null) {
                    logger.debug(
                            "InvestigationProxyEJB.getInvestigation: check for nulls: SourceActUID" +
                                    nSourceActID + " classCd: " + strClassCd);
                    continue;
                }
            }

            investigationProxyVO.setThePublicHealthCaseContainer(thePublicHealthCaseContainer);
            investigationProxyVO.setThePlaceVOCollection(thePlaceVOCollection);
            investigationProxyVO.setTheNonPersonLivingSubjectVOCollection(
                    theNonPersonLivingSubjectVOCollection);
            investigationProxyVO.setTheOrganizationVOCollection(
                    theOrganizationVOCollection);
            investigationProxyVO.setThePersonVOCollection(thePersonVOCollection);
            investigationProxyVO.setTheMaterialVOCollection(theMaterialVOCollection);
            investigationProxyVO.setTheEntityGroupVOCollection(
                    theEntityGroupVOCollection);
            investigationProxyVO.setTheInterventionVOCollection(
                    theInterventionVOCollection);
            investigationProxyVO.setTheClinicalDocumentVOCollection(
                    theClinicalDocumentVOCollection);
            investigationProxyVO.setTheReferralVOCollection(theReferralVOCollection);
            investigationProxyVO.setThePatientEncounterVOCollection(
                    thePatientEncounterVOCollection);
            //investigationProxyVO.setTheNotificationVOCollection( theNotificationVOCollection  );
            investigationProxyVO.setTheObservationVOCollection(
                    theObservationVOCollection);

            //for LDFs
            // ArrayList<Object> ldfList = new ArrayList<Object> ();
            try {
                //code for new ldf back end
                if(!lite) {
                    //TODO: INVESTIGATE LDF
                    theStateDefinedFieldDTCollection  = new ArrayList<>(ldfService.getLDFCollection(publicHealthCaseUID, investigationProxyVO.getBusinessObjectName()));
                }
            }
            catch (Exception e) {
                logger.error("Exception occured while retrieving LDFCollection<Object>  = " +
                        e.toString());
            }

            if (theStateDefinedFieldDTCollection  != null) {
                logger.debug("Before setting LDFCollection<Object>  = " +
                        theStateDefinedFieldDTCollection.size());
                investigationProxyVO.setTheStateDefinedFieldDataDTCollection(theStateDefinedFieldDTCollection);
            }


            Collection<Object>  labSumVOCol = new ArrayList<Object> ();
            HashMap<Object,Object> labSumVOMap = new HashMap<Object,Object>();
            Date dtc = new Date();
            ////##!! System.out.println("the InvestigationProxyVO time before start getting associated reports is :" + (dtc.getTime()- dta.getTime()));

            //TODO: CHECK THIS PERM
            if (!lite
//                    && nbsSecurityObj.getPermission(NBSBOLookup.OBSERVATIONLABREPORT,
//                    "VIEW",
//                    "ANY",
//                    "ANY")
            )
            {
                String labReportViewClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.OBSERVATIONLABREPORT, "VIEW", "obs");
                labReportViewClause = labReportViewClause != null? " AND " + labReportViewClause:"";

                Collection<UidSummaryContainer>  LabReportUidSummarVOs = observationSummaryService.findAllActiveLabReportUidListForManage(publicHealthCaseUID,labReportViewClause);
                String uidType = "LABORATORY_UID";
                Collection<Object>  newLabReportSummaryVOCollection  = new ArrayList<Object> ();
                Collection<?>  labReportSummaryVOCollection  = new ArrayList<Object> ();
                LabReportSummaryContainer labReportSummaryVOs = new LabReportSummaryContainer();

                if(LabReportUidSummarVOs != null && LabReportUidSummarVOs.size() > 0)
                {
                    //labSumVOCol = new ObservationProcessor().
                    // retrieveLabReportSummary(LabReportUidSummarVOs, nbsSecurityObj);
                    labSumVOMap = retrieveLabReportSummaryRevisited(LabReportUidSummarVOs,false, uidType);
                    if(labSumVOMap !=null)
                    {
                        if(labSumVOMap.containsKey("labEventList"))
                        {
                            labReportSummaryVOCollection  = (ArrayList<?> )labSumVOMap.get("labEventList");
                            Iterator<?>  iterator = labReportSummaryVOCollection.iterator();
                            while( iterator.hasNext())
                            {
                                labReportSummaryVOs = (LabReportSummaryContainer) iterator. next();
                                labSumVOCol.add(labReportSummaryVOs);

                            }
                        }
                    }

                    logger.debug("Size of labreport Collection<Object>  :" + labSumVOCol.size());
                }
            }
            else {
                logger.debug(
                        "user has no permission to view ObservationSummaryVO collection");

            }

            if (labSumVOCol != null) {
                investigationProxyVO.setTheLabReportSummaryVOCollection(labSumVOCol);

            }


            Collection<Object>  morbSumVOCol = new ArrayList<Object> ();
            HashMap<Object,Object> morbSumVoMap = new HashMap<Object,Object>();

            //TODO CHECK THIS PERM ---- ALSO THIS IS MORBIDITY
            /*
            if (!lite && nbsSecurityObj.getPermission(NBSBOLookup.OBSERVATIONMORBIDITYREPORT,
                    "VIEW",
                    "ANY",
                    "ANY")
            )
            {
                String morbReportViewClause = getDataAccessWhereClause(NBSBOLookup.OBSERVATIONMORBIDITYREPORT, "VIEW", "obs");
                morbReportViewClause = morbReportViewClause != null? " AND " + morbReportViewClause : "";
                Collection<Object>  morbReportUidSummarVOs =new ObservationSummaryDAOImpl().findAllActiveMorbReportUidListForManage(publicHealthCaseUID, morbReportViewClause);
                String uidType = "MORBIDITY_UID";
                Collection<Object>  newMobReportSummaryVOCollection  = new ArrayList<Object> ();
                Collection<?>  mobReportSummaryVOCollection  = new ArrayList<Object> ();
                MorbReportSummaryVO mobReportSummaryVOs = new MorbReportSummaryVO();

                if(morbReportUidSummarVOs != null && morbReportUidSummarVOs.size() > 0)
                {
                    //morbSumVOCol = new ObservationProcessor().
                    // retrieveMorbReportSummaryRevisited(morbReportUidSummarVOs, nbsSecurityObj, uidType);
                    morbSumVoMap = new ObservationProcessor().
                            retrieveMorbReportSummaryRevisited(morbReportUidSummarVOs, false, nbsSecurityObj, uidType);

                    if(morbSumVoMap !=null)
                    {
                        if(morbSumVoMap.containsKey("MorbEventColl"))
                        {
                            mobReportSummaryVOCollection  = (ArrayList<?> )morbSumVoMap.get("MorbEventColl");
                            Iterator<?>  iterator = mobReportSummaryVOCollection.iterator();
                            while( iterator.hasNext())
                            {
                                mobReportSummaryVOs = (MorbReportSummaryVO) iterator. next();
                                morbSumVOCol.add(mobReportSummaryVOs);

                            }
                        }
                    }
                    logger.debug("Size of Morbidity Collection<Object>  :" + morbSumVOCol.size());
                }

            }*/
//            else {
//                logger.debug(
//                        "user has no permission to view ObservationSummaryVO collection");
//            }
//            if (morbSumVOCol != null) {
//                investigationProxyVO.setTheMorbReportSummaryVOCollection(morbSumVOCol);
//
//            }

            //TODO: THIS IS INTERVENTION
//            if (!lite && nbsSecurityObj.getPermission(NBSBOLookup.INTERVENTIONVACCINERECORD, "VIEW"))
//            {
//                RetrieveSummaryVO retrievePhcVaccinations = new RetrieveSummaryVO();
//                theVaccinationSummaryVOCollection  = new ArrayList<Object> (
//                        retrievePhcVaccinations.retrieveVaccinationSummaryVOForInv(
//                                publicHealthCaseUID, nbsSecurityObj).values());
//                investigationProxyVO.setTheVaccinationSummaryVOCollection(
//                        theVaccinationSummaryVOCollection);
//            }
//            else {
//                logger.debug("user has no permission to view VaccinationSummaryVO collection");
//            }


            if(!lite) {
                investigationProxyVO.setTheNotificationSummaryVOCollection(retrieveSummaryService.notificationSummaryOnInvestigation(thePublicHealthCaseContainer, investigationProxyVO));

                if(investigationProxyVO.getTheNotificationSummaryVOCollection()!=null){
                    Iterator<Object> it = investigationProxyVO.getTheNotificationSummaryVOCollection().iterator();
                    while(it.hasNext()){
                        NotificationSummaryContainer notifVO = (NotificationSummaryContainer)it.next();
                        Iterator<ActRelationshipDto> actIterator = investigationProxyVO.getThePublicHealthCaseContainer().getTheActRelationshipDTCollection().iterator();
                        while(actIterator.hasNext()){
                            ActRelationshipDto actRelationDT = (ActRelationshipDto)actIterator.next();
                            if((notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_SHARE_NOTF) ||
                                    notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_SHARE_NOTF_PHDC))
                                    && notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid())==0){
                                actRelationDT.setShareInd(true);
                            }
                            if ((notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_EXP_NOTF) ||
                                    notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC)) &&
                                    notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid())==0){
                                actRelationDT.setExportInd(true);
                            }
                            if(notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_NOTF) && notifVO.getNotificationUid().compareTo(actRelationDT.getSourceActUid())==0){
                                actRelationDT.setNNDInd(true);
                            }
                        }
                    }
                }
            }

            //Begin support for TreatmentSummary
            if (!lite
//                    && nbsSecurityObj.getPermission(NBSBOLookup.TREATMENT,
//                    "VIEW",
//                    "ANY",
//                    "ANY")
            ) {

                logger.debug("About to get TreatmentSummaryList for Investigation");
                //RetrieveSummaryVO rsvo = new RetrieveSummaryVO();
                theTreatmentSummaryVOCollection  = new ArrayList<Object> ((retrieveSummaryService.retrieveTreatmentSummaryVOForInv(publicHealthCaseUID)).values());
                logger.debug("Number of treatments found: " +
                        theTreatmentSummaryVOCollection.size());
                investigationProxyVO.setTheTreatmentSummaryVOCollection(
                        theTreatmentSummaryVOCollection);
            }
            else {
                logger.debug(
                        "user has no permission to view TreatmentSummaryVO collection");
            }
            // end treatment support

            // document support starts here
            if (!lite
//                    && nbsSecurityObj.getPermission(NBSBOLookup.DOCUMENT, "VIEW")
            )
            {
                theDocumentSummaryVOCollection  = new ArrayList<Object> (retrieveSummaryService.retrieveDocumentSummaryVOForInv(publicHealthCaseUID).values());
                investigationProxyVO.setTheDocumentSummaryVOCollection(theDocumentSummaryVOCollection);
            }
            else {
                logger.debug(
                        "user has no permission to view DocumentSummaryVO collection");
            }
            if (!lite
//                    && nbsSecurityObj.getPermission(NBSBOLookup.CT_CONTACT, "VIEW")
            )
            {
                Collection<Object> contactCollection= contactSummaryService.getContactListForInvestigation(publicHealthCaseUID);

                investigationProxyVO.setTheCTContactSummaryDTCollection(contactCollection);
            }
            else {
                logger.debug("user has no permission to view Contact Summary collection");
            }


        }
        catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return investigationProxyVO;
    }




    private HashMap<Object, Object> retrieveLabReportSummaryRevisited(Collection<UidSummaryContainer> labReportUids, boolean isCDCFormPrintCase, String uidType) throws DataProcessingException {
        //labReportUids = getLongArrayList(labReportUids);
        HashMap<Object, Object> labReportSummarMap = getObservationSummaryListForWorkupRevisited(labReportUids, isCDCFormPrintCase, uidType);

        return labReportSummarMap;
    }

    private HashMap<Object, Object> getObservationSummaryListForWorkupRevisited(Collection<UidSummaryContainer> uidList,boolean isCDCFormPrintCase, String uidType) throws DataProcessingException {
        ArrayList<Object>  labSummList = new ArrayList<Object> ();
        ArrayList<Object>  labEventList = new ArrayList<Object> ();
        int count = 0;


        Long providerUid=null;

        if (uidList != null) {
            String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.OBSERVATIONLABREPORT, "VIEW", "");
            if (dataAccessWhereClause == null) {
                dataAccessWhereClause = "";
            }
            else {
                dataAccessWhereClause = " AND " + dataAccessWhereClause;
            }



            LabReportSummaryContainer labVO = new LabReportSummaryContainer();


            Collection<Observation_Lab_Summary_ForWorkUp_New> labList = new ArrayList<> ();
            Long LabAsSourceForInvestigation = null;
            try {

                Timestamp fromTime = null;
                //   uidList = (ArrayList<Object> )getUidSummaryVOArrayList(uidList);
                Iterator<UidSummaryContainer> itLabId = uidList.iterator();
                while (itLabId.hasNext()) {
                    if(uidType.equals("PERSON_PARENT_UID")){
                        Long uid = itLabId.next().getUid();
                        var res = observationSummaryRepository.findLabSummaryForWorkupNew(uid, dataAccessWhereClause);
                        if (res.isPresent()) {
                            labList = res.get();
                            count = count + 1;

                        }
                    }
                    else if(uidType.equals("LABORATORY_UID"))
                    {
                        UidSummaryContainer vo = (UidSummaryContainer) itLabId.next();
                        Long observationUid = vo.getUid();
                        fromTime = vo.getAddTime();
                        if(vo.getStatusTime()!=null && vo.getStatusTime().compareTo(fromTime)==0){
                            LabAsSourceForInvestigation=vo.getUid();
                        }

                        var res = observationRepository.findById(observationUid);
                        if (res.isPresent()) {
                            var sum = new Observation_Lab_Summary_ForWorkUp_New(res.get());
                            labList.add(sum);
                            count = count + 1;
                        }
                    }
                    if(labList != null) {
                        Iterator<Observation_Lab_Summary_ForWorkUp_New> labIt = labList.iterator();
                        while (labIt.hasNext()) {
                            LabReportSummaryContainer labRepVO = new LabReportSummaryContainer(labIt.next());
                            labRepVO.setActivityFromTime(fromTime);
                            LabReportSummaryContainer labRepSumm = null;
                            LabReportSummaryContainer labRepEvent = null;
                            Map<Object,Object> uidMap = observationSummaryService.getLabParticipations(labRepVO.getObservationUid());
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR110_TYP_CD) ){
                                if (labRepVO.getRecordStatusCd()!=null && (labRepVO.getRecordStatusCd().equals("UNPROCESSED"))) {
                                    labRepSumm = labRepVO ;
                                    labRepSumm.setMPRUid((Long)uidMap.get(NEDSSConstant.PAR110_TYP_CD));
                                }
                                if(labRepVO.getRecordStatusCd()!=null && !labRepVO.getRecordStatusCd().equals("LOG_DEL")){
                                    labRepEvent = labRepVO;
                                    labRepEvent.setMPRUid((Long)uidMap.get(NEDSSConstant.PAR110_TYP_CD));
                                }
                            }

                            ArrayList<Object>  valList = observationSummaryService.getPatientPersonInfo(labRepVO.getObservationUid());
                            ArrayList<Object>  providerDetails = observationSummaryService.getProviderInfo(labRepVO.getObservationUid(),"ORD");
                            ArrayList<Object>  actIdDetails = observationSummaryService.getActIdDetails(labRepVO.getObservationUid());
                            Map<Object,Object> associationsMap = observationSummaryService.getAssociatedInvList(labRepVO.getObservationUid(), "OBS");
                            if(labRepEvent!=null){
                                labRepEvent.setAssociationsMap(associationsMap);
                            }
                            if(labRepSumm!=null){
                                labRepSumm.setAssociationsMap(associationsMap);
                            }
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR111_TYP_CD) && labRepEvent != null) {
                                labRepEvent.setReportingFacility(observationSummaryService.getReportingFacilityName((Long)uidMap.get(NEDSSConstant.PAR111_TYP_CD)));
                            }
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR111_TYP_CD) && labRepSumm != null) {
                                labRepSumm.setReportingFacility(observationSummaryService.getReportingFacilityName((Long)uidMap.get(NEDSSConstant.PAR111_TYP_CD)));
                            }

                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR101_TYP_CD) && labRepEvent != null) {
                                labRepEvent.setOrderingFacility(observationSummaryService.getReportingFacilityName((Long)uidMap.get(NEDSSConstant.PAR101_TYP_CD)));
                            }
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR101_TYP_CD) && labRepSumm != null) {
                                labRepSumm.setOrderingFacility(observationSummaryService.getReportingFacilityName((Long)uidMap.get(NEDSSConstant.PAR101_TYP_CD)));
                            }

                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR104_TYP_CD) && labRepEvent != null) {
                                var code = observationSummaryService.getSpecimanSource((Long)uidMap.get(NEDSSConstant.PAR104_TYP_CD));
                                var tree = cachingValueService.getCodedValues("SPECMN_SRC",code);
                                labRepEvent.setSpecimenSource(tree.get(code));
                            }
                            if (uidMap != null && uidMap.containsKey(NEDSSConstant.PAR104_TYP_CD) && labRepSumm != null) {
                                var code = observationSummaryService.getSpecimanSource((Long)uidMap.get(NEDSSConstant.PAR104_TYP_CD));
                                var tree = cachingValueService.getCodedValues("SPECMN_SRC",code);
                                labRepSumm.setSpecimenSource(tree.get(code));
                            }

                            providerUid = observationSummaryService.getProviderInformation(providerDetails, labRepEvent);

                            if(isCDCFormPrintCase && providerUid!=null && LabAsSourceForInvestigation!=null){
                                ProviderDataForPrintContainer providerDataForPrintVO =null;
                                if(labRepEvent.getProviderDataForPrintVO()==null){
                                    providerDataForPrintVO =new ProviderDataForPrintContainer();
                                    labRepEvent.setProviderDataForPrintVO(providerDataForPrintVO);
                                }
                                Long orderingFacilityUid = null;
                                if(uidMap.get(NEDSSConstant.PAR101_TYP_CD)!=null){
                                    orderingFacilityUid=(Long)uidMap.get(NEDSSConstant.PAR101_TYP_CD);
                                }
                                if(orderingFacilityUid!=null){
                                    var org = organizationRepositoryUtil.loadObject(orderingFacilityUid, null);
                                    if(org != null && !org.getTheOrganizationNameDtoCollection().isEmpty()) {
                                        OrganizationNameDto dt = null;
                                        for(var item : org.getTheOrganizationNameDtoCollection()) {
                                            dt = item;
                                            break;
                                        }

                                        providerDataForPrintVO.setFacilityName(dt.getNmTxt());
                                    }
                                    observationSummaryService.getOrderingFacilityAddress(providerDataForPrintVO, orderingFacilityUid);
                                    observationSummaryService.getOrderingFacilityPhone(providerDataForPrintVO, orderingFacilityUid);
                                }
                                if(providerUid!=null){
                                    observationSummaryService.getOrderingPersonAddress(providerDataForPrintVO, providerUid);
                                    observationSummaryService.getOrderingPersonPhone(providerDataForPrintVO, providerUid);
                                }
                            }

                            observationSummaryService.getProviderInformation(providerDetails, labRepSumm);


                            if (actIdDetails != null && actIdDetails.size() > 0 && labRepEvent != null) {
                                Object[] accessionNumber = actIdDetails.toArray();
                                if (accessionNumber[0] != null) {
                                    labRepEvent.setAccessionNumber((String) accessionNumber[0]);
                                }
                            }
                            if (actIdDetails != null && actIdDetails.size() > 0 && labRepSumm != null) {
                                Object[] accessionNumber = actIdDetails.toArray();
                                if (accessionNumber[0] != null) {
                                    labRepSumm.setAccessionNumber((String) accessionNumber[0]);
                                }
                            }

                            if(labRepEvent!= null)
                                labEventList.add(labRepEvent);
                            if(labRepSumm !=null)
                                labSummList.add(labRepSumm);

                            Long ObservationUID = labRepVO.getObservationUid();
                            observationSummaryService.getTestAndSusceptibilities("COMP", ObservationUID, labRepEvent, labRepSumm);
                        }

                    }
                }
            }
            catch (Exception ex) {
                throw new DataProcessingException(ex.toString());
            }
        }


        this.populateDescTxtFromCachedValues(labSummList);
        this.populateDescTxtFromCachedValues(labEventList);
        HashMap<Object, Object> returnMap = new HashMap<Object, Object>();
        returnMap.put("labSummList", labSummList);
        returnMap.put("labEventList", labEventList);
        return returnMap;
    } //end of getObservationSummaryVOCollectionForWorkup()



    private void populateDescTxtFromCachedValues(Collection<Object>
                                                         reportSummaryVOCollection) throws DataProcessingException {
        ReportSummaryInterface sumVO = null;
        LabReportSummaryContainer labVO = null;
        LabReportSummaryContainer labMorbVO = null;
      //  MorbReportSummaryVO morbVO = null;
        ResultedTestSummaryContainer resVO = null;
        Iterator<ResultedTestSummaryContainer> resItor = null;
        Iterator<Object> labMorbItor = null;
        ResultedTestSummaryContainer susVO = null;
        Iterator<Object> susItor = null;
        Collection<Object> susColl = null;
        Collection<Object> labMorbColl = null;
        String tempStr = null;

        Iterator<Object>  itor = reportSummaryVOCollection.iterator();
        while (itor.hasNext()) {
            sumVO = (LabReportSummaryContainer) itor.next();
            if (sumVO instanceof LabReportSummaryContainer) {
                labVO = (LabReportSummaryContainer) sumVO;
                labVO.setType(NEDSSConstant.LAB_REPORT_DESC);
                if (labVO.getProgramArea() != null) {
                    tempStr = SrteCache.programAreaCodesMap.get(labVO.getProgramArea());
                    labVO.setProgramArea(tempStr);
                }
                if (labVO.getJurisdiction() != null) {
                    tempStr = SrteCache.jurisdictionCodeMap.get(labVO.getJurisdiction());
                    if(!tempStr.isEmpty())
                        labVO.setJurisdiction(tempStr);
                }
                if (labVO.getStatus() != null) {
                    tempStr = cachingValueService.getCodeDescTxtForCd("ACT_OBJ_ST", labVO.getStatus());
                    if (tempStr != null)
                        labVO.setStatus(tempStr);
                }
                if (labVO.getTheResultedTestSummaryVOCollection() != null &&
                        labVO.getTheResultedTestSummaryVOCollection().size() > 0) {
                    resItor = labVO.getTheResultedTestSummaryVOCollection().iterator();
                    while (resItor.hasNext()) {
                        resVO = (ResultedTestSummaryContainer) resItor.next();


                        if (resVO.getCtrlCdUserDefined1() != null)
                        {
                            if (resVO.getCtrlCdUserDefined1() != null && resVO.getCtrlCdUserDefined1().equals("N"))
                            {
                                if (resVO.getCodedResultValue() != null &&
                                        !resVO.getCodedResultValue().equals("")) {
                                    tempStr =  SrteCache.labResultByDescMap.get(resVO.getCodedResultValue());
                                    resVO.setCodedResultValue(tempStr);
                                }
                            }
                            else if (resVO.getCtrlCdUserDefined1() == null || resVO.getCtrlCdUserDefined1().equals("Y"))
                            {
                                if (resVO.getOrganismName() != null && resVO.getOrganismCodeSystemCd()!=null ) {
                                    if (resVO.getOrganismCodeSystemCd() != null && resVO.getOrganismCodeSystemCd().equals("SNM")) {
                                        tempStr = SrteCache.snomedCodeByDescMap.get(resVO.getCodedResultValue());
                                        resVO.setOrganismName(tempStr);
                                    }
                                    else {
                                        tempStr = SrteCache.labResultWithOrganismNameIndMap.get(resVO.getCodedResultValue());
                                        resVO.setOrganismName(tempStr);
                                    }
                                }
                            }
                        }else if (resVO.getCtrlCdUserDefined1() == null ){
                            if (resVO.getOrganismName() != null){
//System.out.println("got in with an org for elr");
                                if (resVO.getOrganismCodeSystemCd()!=null ) {
                                    if (resVO.getOrganismCodeSystemCd().equals("SNM")) {
                                        tempStr = SrteCache.snomedCodeByDescMap.get(resVO.getCodedResultValue());
                                        if (tempStr == null)
                                        {
                                            resVO.setOrganismName(resVO.getOrganismName());
                                        }
                                        else
                                        {
                                            resVO.setOrganismName(tempStr);
                                        }
                                    }
                                    else
                                    {
                                        tempStr = SrteCache.labResultWithOrganismNameIndMap.get(resVO.getCodedResultValue());
                                        if (tempStr == null)
                                        {
                                            resVO.setOrganismName(resVO.getOrganismName());
                                        }
                                        else
                                        {
                                            resVO.setOrganismName(tempStr);
                                        }

                                    }
                                }
                                else
                                {
                                    resVO.setOrganismName(resVO.getOrganismName());
                                }
                            }
                            else
                            {
                                tempStr = SrteCache.labResultWithOrganismNameIndMap.get(resVO.getCodedResultValue());
                                if (tempStr == null)
                                {
                                    resVO.setOrganismName(resVO.getCodedResultValue());
                                }
                                else
                                {
                                    resVO.setOrganismName(tempStr);
                                }
                            }
                        }

                        if ( (resVO.getCdSystemCd() != null) &&
                                (! (resVO.getCdSystemCd().equals("")))) {
                            if (resVO.getCdSystemCd().equals("LN")) {
                                if (resVO.getResultedTestCd() != null &&
                                        !resVO.getResultedTestCd().equals("")) {

                                    tempStr = SrteCache.loinCodeWithComponentNameMap.get(resVO.getResultedTestCd());
                                    // System.out.println("\n The temStr for resVO" + tempStr);
                                    if (tempStr != null && !tempStr.equals(""))
                                        resVO.setResultedTest(tempStr);
                                }
                            }
                            else if (!resVO.getCdSystemCd().equals("LN")) {
                                if (resVO.getResultedTestCd() != null &&
                                        !resVO.getResultedTestCd().equals("")) {
                                    var res = labTestRepository.findLabTestByLabIdAndLabTestCode(resVO.getCdSystemCd(),resVO.getResultedTestCd());
                                    if (res.isPresent()) {
                                        tempStr =  res.get().get(0).getLabResultDescTxt();
                                    }
                                    if (tempStr != null && !tempStr.equals(""))
                                        resVO.setResultedTest(tempStr);

                                }
                            }
                        }
                        // Added this for ER16368
                        if ((resVO.getResultedTestStatusCd() != null) &&(! (resVO.getResultedTestStatusCd().equals("")))){
                            tempStr = cachingValueService.getCodeDescTxtForCd("ACT_OBJ_ST",resVO.getResultedTestStatusCd());
                            if (tempStr != null && !tempStr.equals(""))
                                resVO.setResultedTestStatus(tempStr);
                        }
                        // End  ER16368
                        susColl = resVO.getTheSusTestSummaryVOColl();
                        if (susColl != null && susColl.size() > 0) {
                            susItor = susColl.iterator();
                            while (susItor.hasNext()) {
                                susVO = (ResultedTestSummaryContainer) susItor.next();

                                if (susVO.getCodedResultValue() != null &&
                                        !susVO.getCodedResultValue().equals("")) {
                                    tempStr = SrteCache.labResultByDescMap.get(susVO.getCodedResultValue());
                                    if (tempStr != null && !tempStr.equals(""))
                                        susVO.setCodedResultValue(tempStr);
                                }
                                if (susVO.getCdSystemCd() != null &&
                                        !susVO.getCdSystemCd().equals("")) {
                                    if (susVO.getCdSystemCd().equals("LN")) {
                                        if (susVO.getResultedTestCd() != null &&
                                                !susVO.getResultedTestCd().equals("")) {
                                            tempStr = SrteCache.loinCodeWithComponentNameMap.get(susVO.getResultedTestCd());

                                            if (tempStr != null && !tempStr.equals("")) {
                                                susVO.setResultedTest(tempStr);
                                            }
                                        }
                                    }
                                    else if (!susVO.getCdSystemCd().equals("LN")) {
                                        if (susVO.getResultedTestCd() != null &&
                                                !susVO.getResultedTestCd().equals("")) {
//                                            tempStr = cdv.getResultedTestDescLab(susVO.getCdSystemCd(),
//                                                    susVO.getResultedTestCd());

                                            var res = labTestRepository.findLabTestByLabIdAndLabTestCode(resVO.getCdSystemCd(),resVO.getResultedTestCd());
                                            if (res.isPresent()) {
                                                tempStr =  res.get().get(0).getLabResultDescTxt();
                                            }

                                            if (tempStr != null && !tempStr.equals("")) {
                                                susVO.setResultedTest(tempStr);
                                            }
                                        }
                                    }
                                }

                            } // inner while
                        }
                    } //outer while
                } //if
            }
            //TODO: MORBIDITY
            /*
            else if (sumVO instanceof MorbReportSummaryVO)
            {
                morbVO = (MorbReportSummaryVO) sumVO;
                if (morbVO.getCondition() != null) {
                    tempStr = cdv.getConditionDesc(morbVO.getCondition());
                    morbVO.setConditionDescTxt(tempStr);
                }
                if (morbVO.getProgramArea() != null) {
                    tempStr = cdv.getProgramAreaDesc(morbVO.getProgramArea());
                    morbVO.setProgramArea(tempStr);
                }
                if (morbVO.getJurisdiction() != null) {
                    tempStr = cdv.getJurisdictionDesc(morbVO.getJurisdiction());
                    morbVO.setJurisdiction(tempStr);
                }
                morbVO.setType(NEDSSConstant.MORB_REPORT_DESC);
                if (morbVO.getReportType() != null) {
                    tempStr = cdv.getDescForCode("MORB_RPT_TYPE", morbVO.getReportType());
                    morbVO.setReportTypeDescTxt(tempStr);
                }
                labMorbColl = morbVO.getTheLabReportSummaryVOColl();
                if (labMorbColl != null) {
                    //morb has collection of labsumvo
                    labMorbItor = labMorbColl.iterator();
                    while (labMorbItor.hasNext()) {
                        labMorbVO = (LabReportSummaryVO) labMorbItor.next();
                        if (labMorbVO.getTheResultedTestSummaryVOCollection() != null) {

                            //lab has collection of ResultedTestSummaryVO
                            resItor = labMorbVO.getTheResultedTestSummaryVOCollection().
                                    iterator();
                            while (resItor.hasNext()) {
                                resVO = (ResultedTestSummaryVO) resItor.next();
                                if (resVO.getCodedResultValue() != null &&
                                        !resVO.getCodedResultValue().equals("")) {
                                    //tempStr = cdv.getCodedResultDesc(resVO.getCodedResultValue());
                                    //resVO.setCodedResultValue(tempStr);
                                }
                                if (resVO.getResultedTest() != null &&
                                        !resVO.getResultedTest().equals("")) {
                                    //tempStr = cdv.getResultedTestDesc(resVO.getResultedTestCd());
                                    //resVO.setResultedTest(tempStr);

                                }

                                susColl = resVO.getTheSusTestSummaryVOColl();
                                if (susColl != null && susColl.size() > 0) {
                                    //ResultedTestSummaryVO has collection of SusTestSummaryVO
                                    susItor = susColl.iterator();
                                    while (susItor.hasNext()) {
                                        susVO = (ResultedTestSummaryVO) susItor.next();
                                        if (susVO.getCodedResultValue() != null &&
                                                !susVO.getCodedResultValue().equals("")) {
                                            tempStr = cdv.getCodedResultDesc(susVO.
                                                    getCodedResultValue());
                                            susVO.setCodedResultValue(tempStr);
                                        }

                                    } // inner while
                                }
                            } //outer while
                        } //if lab
                    } // while labmorb
                } //if labmorbcoll

            }
            */

        }
    }



    public PageActProxyContainer getPageProxyVO(String typeCd, Long publicHealthCaseUID) throws DataProcessingException {

//        if (!nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                NBSOperationLookup.VIEW)) {
//            logger
//                    .info("nbsSecurityObj.getPermission(NedssBOLookup.INVESTIGATION,NBSOperationLookup.VIEW) is false");
//            throw new NEDSSSystemException("NO PERMISSIONS");
//        }
//        logger.info("nbsSecurityObj.getPermission(NedssBOLookup.INVESTIGATION,NBSOperationLookup.VIEW) is true");
        PageActProxyContainer pageProxyVO = new PageActProxyContainer();

        PublicHealthCaseContainer thePublicHealthCaseContainer = null;

        ArrayList<PersonContainer> thePersonVOCollection = new ArrayList<>();
        ArrayList<OrganizationContainer> theOrganizationVOCollection = new ArrayList<>();
        ArrayList<MaterialContainer> theMaterialVOCollection = new ArrayList<>();
        ArrayList<Object> theInterventionVOCollection = new ArrayList<Object>();

        // Summary Collections
        ArrayList<Object> theVaccinationSummaryVOCollection = new ArrayList<Object>();
        ArrayList<Object> theTreatmentSummaryVOCollection = new ArrayList<Object>();
        ArrayList<Object> theInvestigationAuditLogSummaryVOCollection = new ArrayList<Object>(); // civil00014862

        ArrayList<Object> theDocumentSummaryVOCollection = new ArrayList<Object>();

        Object theLookedUpObject;

        try {


            // Step 1: Get the Public Health Case
            thePublicHealthCaseContainer = publicHealthCaseRepositoryUtil.loadObject(publicHealthCaseUID);

            // before returning PublicHealthCaseContainer check security permissions -
            // if no permissions - terminate
//            if (!nbsSecurityObj.checkDataAccess(thePublicHealthCaseContainer
//                            .getThePublicHealthCaseDto(), NBSBOLookup.INVESTIGATION,
//                    NBSOperationLookup.VIEW)) {
//                logger
//                        .info("nbsSecurityObj.checkDataAccess(thePublicHealthCaseContainer.getThePublicHealthCaseDto(), NedssBOLookup.INVESTIGATION, NBSOperationLookup.VIEW) is false");
//                throw new NEDSSSystemException("NO ACCESS PERMISSIONS");
//            }
//            logger
//                    .info("nbsSecurityObj.checkDataAccess(thePublicHealthCaseContainer.getThePublicHealthCaseDto(), NedssBOLookup.INVESTIGATION, NBSOperationLookup.VIEW) is true");



            //TODO: Auth
//            NBSAuthHelper helper = new NBSAuthHelper();
//            thePublicHealthCaseContainer.getThePublicHealthCaseDto().setAddUserName(helper.getUserName(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getAddUserId()));
//            thePublicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgUserName(helper.getUserName(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getLastChgUserId()));
            thePublicHealthCaseContainer.getThePublicHealthCaseDto().setAddUserName(AuthUtil.authUser.getUserId());
            thePublicHealthCaseContainer.getThePublicHealthCaseDto().setLastChgUserName(AuthUtil.authUser.getUserId());


            BasePamContainer pageVO = publicHealthCaseRepositoryUtil.getPamVO(publicHealthCaseUID);
            pageProxyVO.setPageVO(pageVO);
            String strTypeCd;
            String strClassCd;
            String recordStatusCd = "";
            Long nEntityID;
            ParticipationDto participationDT = null;

            Iterator<ParticipationDto> participationIterator = thePublicHealthCaseContainer
                    .getTheParticipationDTCollection().iterator();
            logger.debug("ParticipationDTCollection() = "
                    + thePublicHealthCaseContainer.getTheParticipationDTCollection());

            // Populate the Entity collections with the results
            while (participationIterator.hasNext()) {
                participationDT = (ParticipationDto) participationIterator
                        .next();
                nEntityID = participationDT.getSubjectEntityUid();
                strClassCd = participationDT.getSubjectClassCd();
                strTypeCd = participationDT.getTypeCd();
                recordStatusCd = participationDT.getRecordStatusCd();
                if (strClassCd != null
                        && strClassCd
                        .compareToIgnoreCase(NEDSSConstant.ORGANIZATION) == 0
                        && recordStatusCd != null
                        && recordStatusCd
                        .equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    theOrganizationVOCollection.add(organizationRepositoryUtil.loadObject(nEntityID, null));

                    continue;
                }
                if (strClassCd != null
                        && strClassCd
                        .compareToIgnoreCase(NEDSSConstant.PERSON) == 0
                        && recordStatusCd != null
                        && recordStatusCd
                        .equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    thePersonVOCollection.add(patientRepositoryUtil.loadPerson(nEntityID));
                    continue;
                }
                if (strClassCd != null
                        && strClassCd
                        .compareToIgnoreCase(NEDSSConstant.MATERIAL) == 0
                        && recordStatusCd != null
                        && recordStatusCd
                        .equals(NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                    theMaterialVOCollection.add(materialService.loadMaterialObject(nEntityID));

                    continue;
                }
                if (nEntityID == null || strClassCd == null
                        || strClassCd.length() == 0) {
                    continue;
                }
            }

            pageProxyVO.setTheOrganizationContainerCollection(theOrganizationVOCollection);
            pageProxyVO.setPublicHealthCaseContainer(thePublicHealthCaseContainer);
            pageProxyVO.setThePersonContainerCollection(thePersonVOCollection);

            pageProxyVO.setTheNotificationSummaryVOCollection(retrieveSummaryService.notificationSummaryOnInvestigation(thePublicHealthCaseContainer, pageProxyVO));

            if (pageProxyVO.getTheNotificationSummaryVOCollection() != null) {
                Iterator<Object> it = pageProxyVO
                        .getTheNotificationSummaryVOCollection().iterator();
                while (it.hasNext()) {
                    NotificationSummaryContainer notifVO = (NotificationSummaryContainer) it
                            .next();
                    Iterator<ActRelationshipDto> actIterator = pageProxyVO
                            .getPublicHealthCaseContainer()
                            .getTheActRelationshipDTCollection().iterator();
                    while (actIterator.hasNext()) {
                        ActRelationshipDto actRelationDT = (ActRelationshipDto) actIterator
                                .next();
                        if ((notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_SHARE_NOTF) ||
                                notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_SHARE_NOTF_PHDC))
                                && notifVO.getNotificationUid().compareTo(
                                actRelationDT.getSourceActUid()) == 0) {
                            actRelationDT.setShareInd(true);
                        }
                        if ( (notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_EXP_NOTF) ||
                                notifVO.getCdNotif().equalsIgnoreCase(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC))
                                && notifVO.getNotificationUid().compareTo(
                                actRelationDT.getSourceActUid()) == 0) {
                            actRelationDT.setExportInd(true);
                        }
                        if ((notifVO.getCdNotif().equalsIgnoreCase(
                                NEDSSConstant.CLASS_CD_NOTF))
                                && notifVO.getNotificationUid().compareTo(
                                actRelationDT.getSourceActUid()) == 0) {
                            actRelationDT.setNNDInd(true);
                        }
                    }
                }
            }

            if(typeCd!=null && !typeCd.equals(NEDSSConstant.CASE_LITE)) {
                ActRelationshipDto actRelationshipDT = null;
                // Get the Vaccinations for a PublicHealthCase/Investigation
                Iterator<ActRelationshipDto> actRelationshipIterator = thePublicHealthCaseContainer
                        .getTheActRelationshipDTCollection().iterator();

                // Populate the ACT collections in the results
                while (actRelationshipIterator.hasNext()) {
                    actRelationshipDT = (ActRelationshipDto) actRelationshipIterator
                            .next();
                    logger.debug("inside while actUid: "
                            + actRelationshipDT.getTargetActUid()
                            + " observationUid: "
                            + actRelationshipDT.getSourceActUid());
                    Long nSourceActID = actRelationshipDT.getSourceActUid();
                    strClassCd = actRelationshipDT.getSourceClassCd();
                    strTypeCd = actRelationshipDT.getTypeCd();
                    recordStatusCd = actRelationshipDT.getRecordStatusCd();

                    //TODO INTERVENTION
//                    if (strClassCd != null
//                            && strClassCd
//                            .compareToIgnoreCase(NEDSSConstant.INTERVENTION_CLASS_CODE) == 0
//                            && recordStatusCd != null
//                            && recordStatusCd
//                            .equals(NEDSSConstant.RECORD_STATUS_ACTIVE)
//                            && strTypeCd != null && !strTypeCd.equals("1180")) {
//                        InterventionContainer interventionVO = actController
//                                .getIntervention(nSourceActID, nbsSecurityObj);
//                        theInterventionVOCollection.add(interventionVO);
//                        InterventionDto intDT = interventionVO
//                                .getTheInterventionDto();
//
//                        if (intDT.getCd() != null
//                                && intDT.getCd().compareToIgnoreCase(
//                                "VACCINES/ANTISERA") == 0) {
//                            Collection<Object> intPartDTs = interventionVO
//                                    .getTheParticipationDTCollection();
//                            Iterator<Object> intPartIter = intPartDTs.iterator();
//                            while (intPartIter.hasNext()) {
//                                ParticipationDT dt = (ParticipationDT) intPartIter
//                                        .next();
//
//                                if (dt.getTypeCd() != null
//                                        && dt.getTypeCd() == NEDSSConstant.VACCINATION_ADMINISTERED_TYPE_CODE) {
//                                    VaccinationSummaryVO vaccinationSummaryVO = new VaccinationSummaryVO();
//                                    vaccinationSummaryVO.setActivityFromTime(intDT
//                                            .getActivityFromTime());
//                                    vaccinationSummaryVO.setInterventionUid(intDT
//                                            .getInterventionUid());
//                                    vaccinationSummaryVO.setLocalId(intDT
//                                            .getLocalId());
//                                    MaterialDT materialDT = entityController
//                                            .getMaterialInfo(dt
//                                                            .getSubjectEntityUid(),
//                                                    nbsSecurityObj);
//                                    vaccinationSummaryVO
//                                            .setVaccineAdministered(materialDT
//                                                    .getNm());
//                                    // theVaccinationSummaryVOCollection.add(vaccinationSummaryVO);
//                                }
//                            }
//                        }
//                        continue;
//                    }

                    if (nSourceActID == null || strClassCd == null) {
                        logger
                                .debug("PageProxyEJB.getInvestigation: check for nulls: SourceActUID"
                                        + nSourceActID + " classCd: " + strClassCd);
                        continue;
                    }
                }



                Collection<Object> labSumVOCol = new ArrayList<Object>();
                HashMap<Object, Object> labSumVOMap = new HashMap<Object, Object>();

//                if (nbsSecurityObj.getPermission(NBSBOLookup.OBSERVATIONLABREPORT,
//                        NBSOperationLookup.VIEW,
//                        ProgramAreaJurisdictionUtil.ANY_PROGRAM_AREA,
//                        ProgramAreaJurisdictionUtil.ANY_JURISDICTION)) {

                    String labReportViewClause = queryHelper
                            .getDataAccessWhereClause(
                                    NBSBOLookup.OBSERVATIONLABREPORT,
                                    "VIEW", "obs");
                    labReportViewClause = labReportViewClause != null ? " AND "
                            + labReportViewClause : "";

                    Collection<UidSummaryContainer> LabReportUidSummarVOs =  observationSummaryService
                            .findAllActiveLabReportUidListForManage(
                                    publicHealthCaseUID, labReportViewClause);

                    String uidType = "LABORATORY_UID";
                    Collection<?> labReportSummaryVOCollection = new ArrayList<Object>();
                    LabReportSummaryContainer labReportSummaryVOs = new LabReportSummaryContainer();
                    if (LabReportUidSummarVOs != null
                            && LabReportUidSummarVOs.size() > 0) {
                        // labSumVOCol = new
                        // ObservationProcessor().retrieveLabReportSummary(LabReportUidSummarVOs,
                        // nbsSecurityObj);
                        boolean isCDCFormPrintCase= false;
                        if(typeCd.equalsIgnoreCase(NEDSSConstant.PRINT_CDC_CASE)){
                            isCDCFormPrintCase = true;
                            if(LabReportUidSummarVOs!=null && LabReportUidSummarVOs.size()>0){
                                Iterator it = LabReportUidSummarVOs.iterator();
                                while(it.hasNext()){
                                    UidSummaryContainer uidSummaryVO = (UidSummaryContainer)it.next();
                                    uidSummaryVO.setStatusTime(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getAddTime());
                                }
                            }

                        }else{
                            isCDCFormPrintCase= false;
                        }
                        labSumVOMap = retrieveLabReportSummaryRevisited(
                                        LabReportUidSummarVOs,isCDCFormPrintCase,
                                        uidType);

                        if (labSumVOMap != null) {
                            if (labSumVOMap.containsKey("labEventList")) {
                                labReportSummaryVOCollection = (ArrayList<?>) labSumVOMap
                                        .get("labEventList");
                                Iterator<?> iterator = labReportSummaryVOCollection
                                        .iterator();
                                while (iterator.hasNext()) {
                                    labReportSummaryVOs = (LabReportSummaryContainer) iterator
                                            .next();
                                    labSumVOCol.add(labReportSummaryVOs);
                                }
                            }
                        }

                        logger.debug("Size of labreport Collection<Object>  :"
                                + labSumVOCol.size());

                        logger.debug("Size of labreport Collection<Object>  :"
                                + labSumVOCol.size());
                    }
                    //Add the associated labs from PHDC document
                //TODO: CDA EVENT SUMMARY PARSER
//                    Map<String, EDXEventProcessDto> edxEventsMap = nbsDAO.getEDXEventProcessMapByCaseId(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
//                    CDAEventSummaryParser cdaParser = new CDAEventSummaryParser();
//                    Map<Long, LabReportSummaryContainer> labMapfromDOC = cdaParser.getLabReportMapByPHCUid(edxEventsMap, nbsSecurityObj);

                Map<Long, LabReportSummaryContainer> labMapfromDOC =  new HashMap<>();

                if(labMapfromDOC!=null && labMapfromDOC.size()>0)
                        labSumVOCol.addAll(labMapfromDOC.values());
//                } else {
//                    logger
//                            .debug("user has no permission to view ObservationSummaryVO collection");
//                }

                if (labSumVOCol != null) {
                    pageProxyVO.setTheLabReportSummaryVOCollection(labSumVOCol);

                }

//                TODO: MORBIDITY
//                Collection<Object> morbSumVOCol = new ArrayList<Object>();
//                if (nbsSecurityObj.getPermission(
//                        NBSBOLookup.OBSERVATIONMORBIDITYREPORT,
//                        NBSOperationLookup.VIEW,
//                        ProgramAreaJurisdictionUtil.ANY_PROGRAM_AREA,
//                        ProgramAreaJurisdictionUtil.ANY_JURISDICTION)) {
//                    String morbReportViewClause = nbsSecurityObj
//                            .getDataAccessWhereClause(
//                                    NBSBOLookup.OBSERVATIONMORBIDITYREPORT,
//                                    NBSOperationLookup.VIEW, "obs");
//                    morbReportViewClause = morbReportViewClause != null ? " AND "
//                            + morbReportViewClause : "";
//                    Collection<Object> morbReportUidSummarVOs = new ObservationSummaryDAOImpl()
//                            .findAllActiveMorbReportUidListForManage(
//                                    publicHealthCaseUID, morbReportViewClause);
//
//                    String uidType = "MORBIDITY_UID";
//                    Collection<?> mobReportSummaryVOCollection = new ArrayList<Object>();
//                    MorbReportSummaryVO mobReportSummaryVOs = new MorbReportSummaryVO();
//                    HashMap<Object, Object> morbSumVoMap = new HashMap<Object, Object>();
//                    if (morbReportUidSummarVOs != null
//                            && morbReportUidSummarVOs.size() > 0) {
//                        // morbSumVOCol = new
//                        // ObservationProcessor().retrieveMorbReportSummary(morbReportUidSummarVOs,
//                        // nbsSecurityObj);
//                        boolean isCDCFormPrintCase= false;
//                        if(typeCd.equalsIgnoreCase(NEDSSConstant.PRINT_CDC_CASE)){
//                            if(morbReportUidSummarVOs!=null && morbReportUidSummarVOs.size()>0){
//                                Iterator it = morbReportUidSummarVOs.iterator();
//                                while(it.hasNext()){
//                                    UidSummaryVO uidSummaryVO = (UidSummaryVO)it.next();
//                                    uidSummaryVO.setStatusTime(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getAddTime());
//                                }
//                            }
//
//                            isCDCFormPrintCase = true;
//                        }else{
//                            isCDCFormPrintCase= false;
//                        }
//                        morbSumVoMap = new ObservationProcessor()
//                                .retrieveMorbReportSummaryRevisited(
//                                        morbReportUidSummarVOs, isCDCFormPrintCase, nbsSecurityObj,
//                                        uidType);
//                        if (morbSumVoMap != null) {
//
//                            if (morbSumVoMap.containsKey("MorbEventColl")) {
//                                mobReportSummaryVOCollection = (ArrayList<?>) morbSumVoMap
//                                        .get("MorbEventColl");
//                                Iterator<?> iterator = mobReportSummaryVOCollection
//                                        .iterator();
//                                while (iterator.hasNext()) {
//                                    mobReportSummaryVOs = (MorbReportSummaryVO) iterator
//                                            .next();
//                                    morbSumVOCol.add(mobReportSummaryVOs);
//
//                                }
//                            }
//                        }
//                        logger.debug("Size of Morbidity Collection<Object>  :"
//                                + morbSumVOCol.size());
//                    }
//                    //Add the associated morbs from PHDC document
//                    NbsDocumentDAOImpl nbsDAO = new NbsDocumentDAOImpl();
//                    Map<String, EDXEventProcessDto> edxEventsMap = nbsDAO.getEDXEventProcessMapByCaseId(thePublicHealthCaseContainer.getThePublicHealthCaseDto().getPublicHealthCaseUid());
//                    CDAEventSummaryParser cdaParser = new CDAEventSummaryParser();
//                    Map<Long, MorbReportSummaryVO> morbMapfromDOC = cdaParser.getMorbReportMapByPHCUid(edxEventsMap, nbsSecurityObj);
//                    if(morbMapfromDOC!=null && morbMapfromDOC.size()>0)
//                        morbSumVOCol.addAll(morbMapfromDOC.values());
//                } else {
//                    logger
//                            .debug("user has no permission to view ObservationSummaryVO collection");
//                }
//                if (morbSumVOCol != null) {
//                    pageProxyVO.setTheMorbReportSummaryVOCollection(morbSumVOCol);
//
//                }

//                TODO: INVERVENTION
//                if (nbsSecurityObj.getPermission(
//                        NBSBOLookup.INTERVENTIONVACCINERECORD,
//                        NBSOperationLookup.VIEW)) {
//                    RetrieveSummaryVO retrievePhcVaccinations = new RetrieveSummaryVO();
//                    theVaccinationSummaryVOCollection = new ArrayList<Object>(
//                            retrievePhcVaccinations
//                                    .retrieveVaccinationSummaryVOForInv(
//                                            publicHealthCaseUID, nbsSecurityObj)
//                                    .values());
//                    pageProxyVO
//                            .setTheVaccinationSummaryVOCollection(theVaccinationSummaryVOCollection);
//                } else {
//                    logger
//                            .debug("user has no permission to view VaccinationSummaryVO collection");
//                }

                //TODO: TREATMENT
                // Begin support for TreatmentSummary
//                if (nbsSecurityObj.getPermission(NBSBOLookup.TREATMENT,
//                        NBSOperationLookup.VIEW,
//                        ProgramAreaJurisdictionUtil.ANY_PROGRAM_AREA,
//                        ProgramAreaJurisdictionUtil.ANY_JURISDICTION)) {
//
//                    logger
//                            .debug("About to get TreatmentSummaryList for Investigation");
//                    RetrieveSummaryVO rsvo = new RetrieveSummaryVO();
//                    theTreatmentSummaryVOCollection = new ArrayList<Object>((rsvo
//                            .retrieveTreatmentSummaryVOForInv(publicHealthCaseUID,
//                                    nbsSecurityObj)).values());
//                    logger.debug("Number of treatments found: "
//                            + theTreatmentSummaryVOCollection.size());
//                    pageProxyVO
//                            .setTheTreatmentSummaryVOCollection(theTreatmentSummaryVOCollection);
//                } else {
//                    logger
//                            .debug("user has no permission to view TreatmentSummaryVO collection");
//                }

                // Added this for Investigation audit log summary on the RVCT
                // Page(civil00014862)

//                if (nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                        NBSOperationLookup.VIEW)) {

                    logger.debug("About to get AuditLogSummary for Investigation");

                //TODO: AUDIT COLLECTION
                    theInvestigationAuditLogSummaryVOCollection = new ArrayList<>();
//                            new ArrayList<Object>(
//                            (summaryVO.retrieveInvestigationAuditLogSummaryVO(publicHealthCaseUID)));

                    logger.debug("Number of Investigation Auditlog summary found: "
                            + theInvestigationAuditLogSummaryVOCollection.size());
                    pageProxyVO
                            .setTheInvestigationAuditLogSummaryVOCollection(theInvestigationAuditLogSummaryVOCollection);
//                } else {
//                    logger
//                            .debug("user has no permission to view InvestigationAuditLogSummaryVO collection");
//                }

                // End (civil00014862)

                // Begin support for Document Summary Section
//                if (nbsSecurityObj.getPermission(NBSBOLookup.DOCUMENT, NBSOperationLookup.VIEW)) {
                    theDocumentSummaryVOCollection = new ArrayList<Object>(
                            retrieveSummaryService.retrieveDocumentSummaryVOForInv(publicHealthCaseUID).values());
                    pageProxyVO
                            .setTheDocumentSummaryVOCollection(theDocumentSummaryVOCollection);
//                } else {
//                    logger
//                            .debug("user has no permission to view DocumentSummaryVO collection");
//                }

                //TODO: INTERVIEW
//                if (nbsSecurityObj.getPermission(NBSBOLookup.INTERVIEW,
//                        NBSOperationLookup.VIEW)) {
//                    InterviewSummaryDAO interviewSummaryDAO = new InterviewSummaryDAO();
//                    Collection<Object> interviewCollection = interviewSummaryDAO
//                            .getInterviewListForInvestigation(publicHealthCaseUID,
//                                    pageProxyVO.getPublicHealthCaseContainer().getThePublicHealthCaseDto().getProgAreaCd(),
//                                    nbsSecurityObj);
//
//                    pageProxyVO
//                            .setTheInterviewSummaryDTCollection(interviewCollection);
//                } else {
//                    logger.debug("User has no permission to view Interview Summary collection");
//                }


//                if (nbsSecurityObj.getPermission(NBSBOLookup.CT_CONTACT,
//                        NBSOperationLookup.VIEW)) {
                    Collection<Object> contactCollection = contactSummaryService.getContactListForInvestigation(publicHealthCaseUID);

                    pageProxyVO
                            .setTheCTContactSummaryDTCollection(contactCollection);
//                } else {
//                    logger
//                            .debug("user has no permission to view Contact Summary collection");
//                }


//                if (nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                        NBSOperationLookup.VIEW)) {

  //TODO: INVESTIGATION
//                    Collection<Object> nbsCaseAttachmentDTColl = nbsAttachmentDAO.getNbsAttachmentCollection(publicHealthCaseUID);
//                    pageProxyVO.setNbsAttachmentDTColl(nbsCaseAttachmentDTColl);
//                    Collection<NbsNoteDto>  nbsCaseNotesColl = nbsAttachmentDAO.getNbsNoteCollection(publicHealthCaseUID);
//                    pageProxyVO.setNbsNoteDTColl(nbsCaseNotesColl);


//                } else {
//                    logger
//                            .debug("user has no permission to view Investigation : Attachments and Notes are secured by Investigation View Permission");
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new DataProcessingException(e.getMessage(), e);
        }
        return pageProxyVO;
    }


}
