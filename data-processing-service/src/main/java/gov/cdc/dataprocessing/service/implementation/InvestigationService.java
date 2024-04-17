package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.*;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.service.implementation.act.ActRelationshipService;
import gov.cdc.dataprocessing.service.interfaces.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.material.IMaterialService;
import gov.cdc.dataprocessing.utilities.component.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

@Service
public class InvestigationService implements IInvestigationService {
    private static final Logger logger = LoggerFactory.getLogger(InvestigationService.class);

    private final PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;
    private final PatientRepositoryUtil patientRepositoryUtil;

    private final IMaterialService materialService;

    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final ObservationRepositoryUtil observationRepositoryUtil;
    private final IRetrieveSummaryService retrieveSummaryService;
    private final ActRelationshipService actRelationshipService;

    public InvestigationService(PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil,
                                OrganizationRepositoryUtil organizationRepositoryUtil,
                                PatientRepositoryUtil patientRepositoryUtil,
                                IMaterialService materialService,
                                PrepareAssocModelHelper prepareAssocModelHelper,
                                ObservationRepositoryUtil observationRepositoryUtil,
                                IRetrieveSummaryService retrieveSummaryService,
                                ActRelationshipService actRelationshipService) {
        this.publicHealthCaseRepositoryUtil = publicHealthCaseRepositoryUtil;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.materialService = materialService;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.observationRepositoryUtil = observationRepositoryUtil;
        this.retrieveSummaryService = retrieveSummaryService;
        this.actRelationshipService = actRelationshipService;
    }

    @Transactional
    public void setAssociations(Long investigationUID,
                                Collection<LabReportSummaryContainer>  reportSumVOCollection,
                                Collection<Object>  vaccinationSummaryVOCollection,
                                Collection<Object>  summaryDTColl,
                                Collection<Object> treatmentSumColl,
                                Boolean isNNDResendCheckRequired) throws DataProcessingException {
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
                InvestigationContainer invVO = getInvestigationProxy(investigationUID);
                updateAutoResendNotificationsAsync(invVO);
            }
            if(reportSumVOCollection!=null && reportSumVOCollection.size()>0){
                retrieveSummaryService.checkBeforeCreateAndStoreMessageLogDTCollection(investigationUID, reportSumVOCollection);
            }


        }catch (Exception e) {
            NNDActivityLogDto nndActivityLogDT = new  NNDActivityLogDto();
            String phcLocalId = investigationProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getLocalId();
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

    private void setObservationAssociationsImpl(Long investigationUID, Collection<LabReportSummaryContainer>  reportSumVOCollection, boolean invFromEvent) throws DataProcessingException {



        PublicHealthCaseDT phcDT =  publicHealthCaseRepositoryUtil.findPublicHealthCase(investigationUID);

        try
        {
            //For each report summary vo
            if(!reportSumVOCollection.isEmpty())
            {
                Iterator<LabReportSummaryContainer> theIterator = reportSumVOCollection.iterator();
                while( theIterator.hasNext() )
                {
                    LabReportSummaryContainer reportSumVO = (LabReportSummaryContainer)theIterator.next();
                    ActRelationshipDto actRelationshipDT = null;
                    RootDtoInterface rootDT=null;

                    //Gets and checks whether any association change; if changed, do something, else go next one
                    boolean isTouched = reportSumVO.isTouched();
                    if(!isTouched) {
                        continue;
                    }

                    actRelationshipDT = new ActRelationshipDto();
                    //Sets the properties of ActRelationshipDT object
                    actRelationshipDT.setTargetActUid(investigationUID);
                    actRelationshipDT.setSourceActUid(reportSumVO.getObservationUid());
                    actRelationshipDT.setFromTime(reportSumVO.getActivityFromTime());
                    actRelationshipDT.setLastChgUserId(Long.parseLong("21212121"));
                    //Set from time same as investigation create time if act relationship is created while creating investigation from lab or morbidity report
                    if (invFromEvent)
                    {
                        actRelationshipDT.setFromTime(phcDT.getAddTime());
                    }
                    actRelationshipDT.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
                    actRelationshipDT.setTargetClassCd(NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE);
                    //actRelationshipDT.setStatusTime(new Timestamp(new java.util.Date().getTime()));
                    boolean reportFromDoc = false;
                    if(reportSumVO instanceof LabReportSummaryContainer)
                    {
                        actRelationshipDT.setTypeCd(NEDSSConstant.LAB_DISPALY_FORM);
                        if(((LabReportSummaryContainer)reportSumVO).isLabFromDoc())
                        {
                            reportFromDoc=true;
                        }
                        if(invFromEvent)
                        {
                            actRelationshipDT.setAddReasonCd(((LabReportSummaryContainer)reportSumVO).getProcessingDecisionCd());
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


                    if(reportSumVO.isAssociated()){
                        //actRelationshipDT.setItNew(true);
                        actRelationshipDT.setRecordStatusCd(NEDSSConstant.ACTIVE);
                        actRelationshipDT.setStatusCd(NEDSSConstant.A);
                    }
                    else
                    {
                        // actRelationshipDT.setItDelete(true);
                        actRelationshipDT.setRecordStatusCd(NEDSSConstant.INACTIVE);
                        actRelationshipDT.setStatusCd(NEDSSConstant.I);
                        //    actRelationshipDAOImpl.store(actRelationshipDT);
                    }

                    actRelationshipDT= prepareAssocModelHelper.prepareAssocDTForActRelationship(actRelationshipDT);
                    // needs to be done here as prepareAssocDT will always set dirty flag true
                    if(reportSumVO.isAssociated()){
                        actRelationshipDT.setItNew(true);
                        actRelationshipDT.setItDirty(false);
                    }
                    else{
                        actRelationshipDT.setItDelete(true);
                        actRelationshipDT.setItDirty(false);
                    }
                    observationRepositoryUtil.saveActRelationship(actRelationshipDT);

                    if(!reportFromDoc){
                        //Obtains the core observation object
                        var obs = observationRepositoryUtil.loadObject(reportSumVO.getObservationUid());
                        ObservationDto  obsDT = obs.getTheObservationDto();
                        //Starts persist observationDT
                        if(reportSumVO.isAssociated())
                        {
                            obsDT.setItDirty(true);
                            String businessObjLookupName="";
                            String businessTriggerCd="";
                            String tableName=NEDSSConstant.OBSERVATION;
                            String moduleCd=NEDSSConstant.BASE;
                            if(reportSumVO instanceof LabReportSummaryContainer)
                            {
                                businessObjLookupName=NEDSSConstant.OBSERVATIONLABREPORT;
                                businessTriggerCd=NEDSSConstant.OBS_LAB_ASC;
                            }
                            /*
                            if(reportSumVO instanceof MorbReportSummaryVO)
                            {
                                businessObjLookupName=NEDSSConstant.OBSERVATIONMORBIDITYREPORT;
                                businessTriggerCd=NEDSSConstant.OBS_MORB_ASC;
                            }
                            */

                            rootDT =  prepareAssocModelHelper.prepareVO(obsDT,businessObjLookupName, businessTriggerCd,tableName, moduleCd, obsDT.getVersionCtrlNbr());
                        } // End if(observationSumVO.getIsAssociated()==true)

                        if(!reportSumVO.isAssociated())
                        {
                            obsDT.setItDirty(true);
                            String businessObjLookupName="";
                            String businessTriggerCd="";
                            String tableName=NEDSSConstant.OBSERVATION;
                            String moduleCd=NEDSSConstant.BASE;
                            if(reportSumVO instanceof LabReportSummaryContainer)
                            {
                                Collection<ActRelationshipDto> actRelColl = actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(reportSumVO.getObservationUid(),"LabReport");
                                businessObjLookupName=NEDSSConstant.OBSERVATIONLABREPORT;
                                if(actRelColl!=null && actRelColl.size()>0)
                                    businessTriggerCd= NEDSSConstant.OBS_LAB_DIS_ASC;
                                else
                                    businessTriggerCd=NEDSSConstant.OBS_LAB_UNPROCESS;// if  Lab does not have other associations it will be sent back into needing review queue
                            }
                            /*
                            if(reportSumVO instanceof MorbReportSummaryVO)
                            {
                                businessObjLookupName=NEDSSConstant.OBSERVATIONMORBIDITYREPORT;
                                businessTriggerCd=NEDSSConstant.OBS_MORB_UNPROCESS;
                            }
                            */
                            rootDT =  prepareAssocModelHelper.prepareVO(obsDT,businessObjLookupName, businessTriggerCd,tableName, moduleCd, obsDT.getVersionCtrlNbr());
                        } // End Of if(observationSumVO.getIsAssociated()==false)
                        obsDT = (ObservationDto)rootDT;
                        //set the previous entered processing decision to null
                        obsDT.setProcessingDecisionCd(null);
                        observationRepositoryUtil.setObservationInfo(obsDT);
                    } // End Of while(theIterator.hasNext())
                } // END Of if(!observationSumVOCollection.isEmpty())
            }
        }// End of try
        catch(Exception e)
        {
            e.printStackTrace();
            throw new DataProcessingException(e.getMessage());
        }

    }//end of setObservationAssociations()




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
        logger.info("enter NNDMessageSenderHelper.updateAutoResendNotifications--------------");
        if(
            //!(vo instanceof VaccinationProxyVO)
                !(vo instanceof LabResultProxyContainer)
            //&&!(vo instanceof MorbidityProxyVO)
            //&&!(vo instanceof InvestigationProxyVO)
            //&&!(vo instanceof PageActProxyVO)
            //&&!(vo instanceof PamProxyVO)
            //&&!(vo instanceof SummaryReportProxyVO)
        )
        {
            throw new DataProcessingException("vo not instance of VaccinationProxyVO,LabResultProxyVO, or MorbidityProxyVO,PamProxyVO, SummaryReportProxyVO");
        }
        Collection<Object>  notSumVOColl =null;
        PublicHealthCaseDT phcDT = null;


        //TODO: LAB RESULT WONT HIT ANY OF THESE

        if(
                vo instanceof InvestigationContainer
                || vo instanceof PamProxyContainer
                ||  vo instanceof PageActProxyVO
//                ||  vo instanceof SummaryReportProxyVO
        ){
            if(vo instanceof InvestigationContainer)
            {
                InvestigationProxyVO invVO = (InvestigationProxyVO)vo;
                phcDT = invVO.thePublicHealthCaseVO.getThePublicHealthCaseDT();
                notSumVOColl = invVO.getTheNotificationSummaryVOCollection();
            }
            else if(vo instanceof PamProxyContainer)
            {
                PamProxyVO pamVO = (PamProxyVO)vo;
                phcDT = pamVO.getPublicHealthCaseVO().getThePublicHealthCaseDT();
                notSumVOColl = pamVO.getTheNotificationSummaryVOCollection();
            }
            else if (vo instanceof LabResultProxyContainer)
            {
                NNDAutoResendDAOImpl nndAutoResendDAO = new NNDAutoResendDAOImpl();
                Collection<Object>  theNotificationCollection  = nndAutoResendDAO.getAutoResendNotificationSummaries(getActClassCd(vo), getTypeCd(vo), getRootUid(vo));
                Iterator<Object>  notIter = theNotificationCollection.iterator();
                while(notIter.hasNext()){
                    NotificationSummaryVO notSumVO = (NotificationSummaryVO)notIter.next();
                    updateNotification(false, notSumVO.getNotificationUid(),notSumVO.getCd(),notSumVO.getCaseClassCd(),notSumVO.getProgAreaCd(),notSumVO.getJurisdictionCd(),notSumVO.getSharedInd(), false, nbsSecurityObj);
                }
            }
            else if(vo instanceof PageActProxyVO)
            {
                PageActProxyVO pageActProxyVO= (PageActProxyVO)vo;
                phcDT = pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT();
                notSumVOColl = pageActProxyVO.getTheNotificationSummaryVOCollection();
            }
//            else if (vo instanceof SummaryReportProxyVO)
//            {
//                SummaryReportProxyVO summaryReportProxyVO = (SummaryReportProxyVO)vo;
//                phcDT = summaryReportProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT();
//                notSumVOColl = summaryReportProxyVO.getTheNotificationVOCollection();
//                Iterator<Object>  notSumIter =  notSumVOColl.iterator();
//                while(notSumIter.hasNext()){
//                    NotificationVO notVO = (NotificationVO)notSumIter.next();
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
                    || vo instanceof PageActProxyVO)
            {
                if(notSumVOColl!=null && notSumVOColl.size()>0){
                    Iterator<Object>  notSumIter =  notSumVOColl.iterator();
                    while(notSumIter.hasNext()){
                        NotificationSummaryVO notSummaryVO = (NotificationSummaryVO)notSumIter.next();
                        if(notSummaryVO.getIsHistory().equals("F") && !notSummaryVO.getAutoResendInd().equals("F")){
                            Long notificationUid = notSummaryVO.getNotificationUid();
                            String phcCd = phcDT.getCd();
                            String phcClassCd = phcDT.getCaseClassCd();
                            String progAreaCd = phcDT.getProgAreaCd();
                            String jurisdictionCd = phcDT.getJurisdictionCd();
                            String sharedInd = phcDT.getSharedInd();

                            // retrieve the status change
                            boolean caseStatusChange = phcDT.isCaseStatusDirty();
                            updateNotification(false, notificationUid,phcCd,phcClassCd,progAreaCd,jurisdictionCd,sharedInd, caseStatusChange, nbsSecurityObj);

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


    }


    private InvestigationContainer getInvestigationProxy(Long publicHealthCaseUID) throws DataProcessingException {

        return getInvestigationProxyLite(publicHealthCaseUID, false);
    }

    private InvestigationContainer getInvestigationProxyLite(Long publicHealthCaseUID, boolean lite) throws DataProcessingException {



        var investigationProxyVO = new InvestigationContainer();

        PublicHealthCaseDT thePublicHealthCaseDT = null;
        PublicHealthCaseVO thePublicHealthCaseVO = null;

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
        ArrayList<Object>  theStateDefinedFieldDTCollection  = new ArrayList<Object> ();
        ArrayList<Object>  theTreatmentSummaryVOCollection  = new ArrayList<Object> ();
        ArrayList<Object>  theDocumentSummaryVOCollection  = new ArrayList<Object> ();

        Object theLookedUpObject;

        try {
            
            // Step 1: Get the Pubic Health Case
            thePublicHealthCaseVO = publicHealthCaseRepositoryUtil.getPublicHealthCaseContainer(publicHealthCaseUID);

            // TODO: Get user name from PHC
            //thePublicHealthCaseVO.getThePublicHealthCaseDT().setAddUserName(helper.getUserName(thePublicHealthCaseVO.getThePublicHealthCaseDT().getAddUserId()));
            thePublicHealthCaseVO.getThePublicHealthCaseDT().setAddUserName("212121");

            // TODO: Get user name from PHC
            //thePublicHealthCaseVO.getThePublicHealthCaseDT().setLastChgUserName(helper.getUserName(thePublicHealthCaseVO.getThePublicHealthCaseDT().getLastChgUserId()));
            thePublicHealthCaseVO.getThePublicHealthCaseDT().setLastChgUserName("212121");

            thePublicHealthCaseDT = thePublicHealthCaseVO.getThePublicHealthCaseDT();

            Long PatientGroupID = thePublicHealthCaseDT.getPatientGroupId();
            if (PatientGroupID != null) {
                var entityGrp = publicHealthCaseRepositoryUtil.getEntityGroup(thePublicHealthCaseDT.getPatientGroupId());
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

            Iterator<Object>  participationIterator = thePublicHealthCaseVO.
                    getTheParticipationDTCollection().iterator();
            logger.debug("ParticipationDTCollection() = " +
                    thePublicHealthCaseVO.getTheParticipationDTCollection());

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
            Iterator<Object>  actRelationshipIterator = thePublicHealthCaseVO.
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
                    InterventionVO interventionVO = actController.getIntervention(
                            nSourceActID, nbsSecurityObj);
                    theInterventionVOCollection.add(interventionVO);
                    InterventionDT intDT = interventionVO.getTheInterventionDT();

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

            investigationProxyVO.setThePublicHealthCaseVO(thePublicHealthCaseVO);
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
//                    LDFHelper ldfHelper = LDFHelper.getInstance();
//                    theStateDefinedFieldDTCollection  = (ArrayList<Object> ) ldfHelper.getLDFCollection(publicHealthCaseUID, investigationProxyVO.getBusinessObjNm(),nbsSecurityObj);
                }
            }
            catch (Exception e) {
                logger.error("Exception occured while retrieving LDFCollection<Object>  = " +
                        e.toString());
            }

            if (theStateDefinedFieldDTCollection  != null) {
                logger.debug("Before setting LDFCollection<Object>  = " +
                        theStateDefinedFieldDTCollection.size());
                investigationProxyVO.setTheStateDefinedFieldDataDTCollection(
                        theStateDefinedFieldDTCollection);
            }


            Collection<Object>  labSumVOCol = new ArrayList<Object> ();
            HashMap<Object,Object> labSumVOMap = new HashMap<Object,Object>();
            java.util.Date dtc = new java.util.Date();
            ////##!! System.out.println("the InvestigationProxyVO time before start getting associated reports is :" + (dtc.getTime()- dta.getTime()));

            //TODO: CHECK THIS PERM
//            if (!lite && nbsSecurityObj.getPermission(NBSBOLookup.OBSERVATIONLABREPORT,
//                    "VIEW",
//                    "ANY",
//                    "ANY"))
            if(!lite)
            {
                String labReportViewClause = nbsSecurityObj.getDataAccessWhereClause(
                        NBSBOLookup.OBSERVATIONLABREPORT, NBSOperationLookup.VIEW, "obs");
                labReportViewClause = labReportViewClause != null? " AND " + labReportViewClause:"";

                Collection<Object>  LabReportUidSummarVOs =new ObservationSummaryDAOImpl().findAllActiveLabReportUidListForManage(publicHealthCaseUID,labReportViewClause);
                String uidType = "LABORATORY_UID";
                Collection<Object>  newLabReportSummaryVOCollection  = new ArrayList<Object> ();
                Collection<?>  labReportSummaryVOCollection  = new ArrayList<Object> ();
                LabReportSummaryContainer labReportSummaryVOs = new LabReportSummaryContainer();

                if(LabReportUidSummarVOs != null && LabReportUidSummarVOs.size() > 0)
                {
                    //labSumVOCol = new ObservationProcessor().
                    // retrieveLabReportSummary(LabReportUidSummarVOs, nbsSecurityObj);
                    labSumVOMap = new ObservationProcessor().retrieveLabReportSummaryRevisited(LabReportUidSummarVOs,false, nbsSecurityObj, uidType);
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

            //TODO CHECK THIS PERM
//            if (!lite && nbsSecurityObj.getPermission(NBSBOLookup.OBSERVATIONMORBIDITYREPORT,
//                    NBSOperationLookup.VIEW,
//                    ProgramAreaJurisdictionUtil.
//                            ANY_PROGRAM_AREA,
//                    ProgramAreaJurisdictionUtil.
//                            ANY_JURISDICTION))
            if (!lite)
            {
                String morbReportViewClause = nbsSecurityObj.getDataAccessWhereClause(
                        NBSBOLookup.OBSERVATIONMORBIDITYREPORT, NBSOperationLookup.VIEW, "obs");
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
            }
            else {
                logger.debug(
                        "user has no permission to view ObservationSummaryVO collection");
            }
            if (morbSumVOCol != null) {
                investigationProxyVO.setTheMorbReportSummaryVOCollection(morbSumVOCol);

            }

            if (!lite && nbsSecurityObj.getPermission(NBSBOLookup.INTERVENTIONVACCINERECORD,
                    NBSOperationLookup.VIEW)) {
                RetrieveSummaryVO retrievePhcVaccinations = new RetrieveSummaryVO();
                theVaccinationSummaryVOCollection  = new ArrayList<Object> (
                        retrievePhcVaccinations.retrieveVaccinationSummaryVOForInv(
                                publicHealthCaseUID, nbsSecurityObj).values());
                investigationProxyVO.setTheVaccinationSummaryVOCollection(
                        theVaccinationSummaryVOCollection);
            }
            else {
                logger.debug(
                        "user has no permission to view VaccinationSummaryVO collection");
            }


            if(!lite) {
                investigationProxyVO.setTheNotificationSummaryVOCollection(RetrieveSummaryVO.
                        notificationSummaryOnInvestigation(thePublicHealthCaseVO, investigationProxyVO,
                                nbsSecurityObj));

                if(investigationProxyVO.getTheNotificationSummaryVOCollection()!=null){
                    Iterator<Object> it = investigationProxyVO.getTheNotificationSummaryVOCollection().iterator();
                    while(it.hasNext()){
                        NotificationSummaryVO notifVO = (NotificationSummaryVO)it.next();
                        Iterator<Object> actIterator = investigationProxyVO.getPublicHealthCaseVO().getTheActRelationshipDTCollection().iterator();
                        while(actIterator.hasNext()){
                            ActRelationshipDT actRelationDT = (ActRelationshipDT)actIterator.next();
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
            if (!lite && nbsSecurityObj.getPermission(NBSBOLookup.TREATMENT,
                    NBSOperationLookup.VIEW,
                    ProgramAreaJurisdictionUtil.
                            ANY_PROGRAM_AREA,
                    ProgramAreaJurisdictionUtil.
                            ANY_JURISDICTION)) {

                logger.debug("About to get TreatmentSummaryList for Investigation");
                RetrieveSummaryVO rsvo = new RetrieveSummaryVO();
                theTreatmentSummaryVOCollection  = new ArrayList<Object> ((rsvo.
                        retrieveTreatmentSummaryVOForInv(publicHealthCaseUID,
                                nbsSecurityObj)).values());
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
            if (!lite && nbsSecurityObj.getPermission(NBSBOLookup.DOCUMENT,
                    NBSOperationLookup.VIEW)) {
                RetrieveSummaryVO retrievePhcVaccinations = new RetrieveSummaryVO();
                theDocumentSummaryVOCollection  = new ArrayList<Object> (
                        retrievePhcVaccinations.retrieveDocumentSummaryVOForInv(
                                publicHealthCaseUID, nbsSecurityObj).values());
                investigationProxyVO.setTheDocumentSummaryVOCollection(theDocumentSummaryVOCollection);
            }
            else {
                logger.debug(
                        "user has no permission to view DocumentSummaryVO collection");
            }
            if (!lite && nbsSecurityObj.getPermission(NBSBOLookup.CT_CONTACT,
                    NBSOperationLookup.VIEW)) {
                CTContactSummaryDAO cTContactSummaryDAO = new CTContactSummaryDAO();
                Collection<Object> contactCollection= cTContactSummaryDAO.getContactListForInvestigation(publicHealthCaseUID, nbsSecurityObj);

                investigationProxyVO.setTheCTContactSummaryDTCollection(contactCollection);
            }
            else {
                logger.debug(
                        "user has no permission to view Contact Summary collection");
            }


        }
        catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        java.util.Date dtb = new java.util.Date();

        return investigationProxyVO;
    }


}
