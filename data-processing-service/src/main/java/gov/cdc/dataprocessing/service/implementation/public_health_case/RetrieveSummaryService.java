package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.utilities.component.notification.NotificationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.cdc.dataprocessing.constant.ComplexQueries.*;

@Service
public class RetrieveSummaryService implements IRetrieveSummaryService {
    private final PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    private final QueryHelper queryHelper;
    private final CustomRepository customRepository;
    private final ICatchingValueService catchingValueService;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final NotificationRepositoryUtil notificationRepositoryUtil;

    public RetrieveSummaryService(PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil,
                                  QueryHelper queryHelper,
                                  CustomRepository customRepository,
                                  ICatchingValueService catchingValueService,
                                  PrepareAssocModelHelper prepareAssocModelHelper,
                                  NotificationRepositoryUtil notificationRepositoryUtil) {
        this.publicHealthCaseRepositoryUtil = publicHealthCaseRepositoryUtil;
        this.queryHelper = queryHelper;
        this.customRepository = customRepository;
        this.catchingValueService = catchingValueService;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.notificationRepositoryUtil = notificationRepositoryUtil;
    }

    public void checkBeforeCreateAndStoreMessageLogDTCollection(Long investigationUID,
                                                                Collection<LabReportSummaryContainer> reportSumVOCollection){

        try {
            PublicHealthCaseDto publicHealthCaseDto = null;

            publicHealthCaseDto = publicHealthCaseRepositoryUtil.findPublicHealthCase(investigationUID);

            if(publicHealthCaseDto.isStdHivProgramAreaCode()){
                //TODO: LOGGING PIPELINE
                createAndStoreMessageLogDTCollection( reportSumVOCollection, publicHealthCaseDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will access the HashMap<Object,Object> of TreatmentSummaryVO for passed investigationUID
     * to papulate the Treatment summary on Investigation page
     * @param publicHealthUID -- UID  for investigation to Access Treatment related to it
     * @return HashMap<Object,Object> -- HashMap<Object,Object> of TreatmentSummaryVO for the passed investigationUID
     */
    public Map<Object,Object> retrieveTreatmentSummaryVOForInv(Long publicHealthUID) throws DataProcessingException {
        String aQuery = null;

        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.TREATMENT, "VIEW", "Treatment");

        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = "AND " + dataAccessWhereClause;

        }

        aQuery = TREATMENTS_FOR_A_PHC_ORACLE + dataAccessWhereClause;

        Map<Object,Object> treatmentsSummaryVOHashMap = new HashMap<Object,Object>();
        //TreeMap<Object, Object> treatmentsSummaryVOTreeMap = new TreeMap<Object, Object>();
        Map<Object,Object> map = null;
        TreatmentContainer treatmentSummaryVO = new TreatmentContainer();
        try {


            //TODO: CDA -- IS THIS ECR?
//            treatmentsSummaryVOHashMap =customRepository.retrieveTreatmentSummaryVOForInv(publicHealthUID, aQuery);

//            Map<String, EDXEventProcessDto> edxEventsMap = getEDXEventProcessMapByCaseId(publicHealthUID);
//            CDAEventSummaryParser cdaParser = new CDAEventSummaryParser();
//            if(treatmentsSummaryVOHashMap==null)
//            {
//                treatmentsSummaryVOHashMap = new HashMap<Object, Object>();
//            }
//            treatmentsSummaryVOHashMap.putAll(cdaParser.getTreatmentMapByPHCUid(edxEventsMap));
        }


        catch (Exception ex) {
            throw new DataProcessingException(ex.toString());
        }

        return treatmentsSummaryVOHashMap;
    } // retrieveTreatmentSummaryList


    private Map<String, EDXEventProcessDto> getEDXEventProcessMapByCaseId(Long publicHealthCaseUid) throws DataProcessingException {
        Map<String, EDXEventProcessDto> eventProcessMap = new HashMap<String, EDXEventProcessDto>();
        try {

            eventProcessMap = customRepository.getEDXEventProcessMapByCaseId(publicHealthCaseUid);

        } catch (Exception e) {
            throw new DataProcessingException(e.toString(), e);
        }
        return eventProcessMap;
    }


    public Map<Object,Object> retrieveDocumentSummaryVOForInv(Long publicHealthUID) throws DataProcessingException {

        Map<Object,Object> documentSummaryVOColl = new HashMap<Object,Object>();
        ArrayList<Object> docList= new ArrayList<Object> ();
        Map<Object,Object> map = null;
        //SummaryDT summaryDT = new SummaryDT();
        try {

            documentSummaryVOColl = customRepository.retrieveDocumentSummaryVOForInv(publicHealthUID);
        }
        catch (Exception rsuex) {
            throw new DataProcessingException(rsuex.toString());
        }
        return documentSummaryVOColl;
    } // retrieveDocumentSummaryList


    public Collection<Object>  notificationSummaryOnInvestigation(PublicHealthCaseContainer publicHealthCaseContainer, Object object) throws DataProcessingException {

        Collection<Object>  theNotificationSummaryVOCollection  = null;
        Long publicHealthCaseUID = null;
        NotificationSummaryContainer notificationSummaryVO = null;

        try{
            if (publicHealthCaseContainer != null) {
                publicHealthCaseUID = publicHealthCaseContainer.getThePublicHealthCaseDto().
                        getPublicHealthCaseUid();
            }

            if (publicHealthCaseContainer.getThePublicHealthCaseDto().getCaseClassCd() != null) {
                theNotificationSummaryVOCollection  = (ArrayList<Object>) retrieveNotificationSummaryListForInvestigation(publicHealthCaseUID);
            }
            else {
                theNotificationSummaryVOCollection  = (ArrayList<Object> ) retrieveNotificationSummaryListForInvestigation1(publicHealthCaseUID);

            }
            if (theNotificationSummaryVOCollection  != null) {
                Iterator<Object> anIterator = theNotificationSummaryVOCollection.iterator();
                int count = 0;
                while (anIterator.hasNext()) {
                    notificationSummaryVO = (NotificationSummaryContainer) anIterator.next();

                    if (count == 0) { //check only for the current Notification record
                        if (notificationSummaryVO.getRecordStatusCd().trim().equals(
                                NEDSSConstant.NOTIFICATION_APPROVED_CODE) ||
                                notificationSummaryVO.getRecordStatusCd().trim().equals(
                                        NEDSSConstant.NOTIFICATION_PENDING_CODE) ||
                                (notificationSummaryVO.getAutoResendInd() != null && notificationSummaryVO.getAutoResendInd().equalsIgnoreCase("T"))) {
                            if(object instanceof InvestigationContainer){
                                InvestigationContainer investigationProxyVO = (InvestigationContainer)object;
                                investigationProxyVO.setAssociatedNotificationsInd(true);
                            } else if(object instanceof PamProxyContainer) {
                                PamProxyContainer pamProxy = (PamProxyContainer) object;
                                pamProxy.setAssociatedNotificationsInd(true);
                            }
                        }
                    }
                    count++;

                    if (notificationSummaryVO.getRecordStatusCd() != null &&
                            notificationSummaryVO.getRecordStatusCd().trim().equals(
                                    NEDSSConstant.PENDING_APPROVAL_STATUS)) {
                        notificationSummaryVO.setCd(publicHealthCaseContainer.
                                getThePublicHealthCaseDto().getCd());
                        notificationSummaryVO.setCdTxt(publicHealthCaseContainer.
                                getThePublicHealthCaseDto().
                                getCdDescTxt());

                        //The following lines of code were commented out for  notificationSummaryVO.setCaseClassCd as there was a bug openend 
                        //in release 3.0 where the notificationSummary was getting the caseClassCd from publicHealthCase for Pending approval cases only.
                        /**As this was not a true reflection of notification, and to fix the bog, this code was commented out.
                         /   notificationSummaryVO.setCaseClassCd(publicHealthCaseContainer.
                         /                                    getThePublicHealthCaseDto().
                         getCaseClassCd());
                         CachedDropDownValues cachedDropDownValues = new CachedDropDownValues();
                         String caseClassCdTxt = cachedDropDownValues.getDescForCode(
                         NEDSSConstant.CASE_CLASS_CODE_SET_NM,
                         publicHealthCaseContainer.getThePublicHealthCaseDto().getCaseClassCd());
                         notificationSummaryVO.setCaseClassCdTxt(caseClassCdTxt);
                         */ //!!##System.out.println("notificationSummaryVO.getCaseClassCd()" + notificationSummaryVO.getCaseClassCd());
                    }
                }
            }

            // TODO:Needs to be fixed to move to Action class.

            if (theNotificationSummaryVOCollection  != null) {
                Iterator<Object>  anIterator1 = theNotificationSummaryVOCollection.iterator();
                while (anIterator1.hasNext()) {
                    notificationSummaryVO = (NotificationSummaryContainer) anIterator1.next();
                    if(object instanceof InvestigationContainer){
                        InvestigationContainer investigationProxyVO = (InvestigationContainer)object;
                        if(notificationSummaryVO.isCaseReport()){
                            if(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_APPROVED_CODE) ){
                                investigationProxyVO.setOOSystemInd(true);
                            }

                            if(notificationSummaryVO.isHistory!=null &&
                                    !notificationSummaryVO.isHistory.equals("T") &&
                                    notificationSummaryVO.getCdNotif()!=null &&
                                    (notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF) || notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC)) &&
                                    !(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_REJECTED_CODE) ||
                                            notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_MESSAGE_FAILED))){
                                investigationProxyVO.setOOSystemPendInd(true);
                            }
                        }

                    }
                    else if(object instanceof PamProxyContainer) {
                        PamProxyContainer pamProxy = (PamProxyContainer) object;
                        if(notificationSummaryVO.isCaseReport()){
                            if(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_APPROVED_CODE) ){
                                pamProxy.setOOSystemInd(true);
                            }

                            if(notificationSummaryVO.isHistory!=null &&
                                    !notificationSummaryVO.isHistory.equals("T") &&
                                    notificationSummaryVO.getCdNotif()!=null &&
                                    (notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF) || notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC)) &&
                                    !(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_REJECTED_CODE)
                                            || notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_MESSAGE_FAILED))){
                                pamProxy.setOOSystemPendInd(true);
                            }
                        }
                    }
                    else if(object instanceof PageActProxyContainer) {
                        PageActProxyContainer pageProxy = (PageActProxyContainer) object;
                        if(notificationSummaryVO.isCaseReport()){
                            if(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_APPROVED_CODE) ){
                                pageProxy.setOOSystemInd(true);
                            }

                            if(notificationSummaryVO.isHistory!=null &&
                                    !notificationSummaryVO.isHistory.equals("T") &&
                                    notificationSummaryVO.getCdNotif()!=null &&
                                    (notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF) || notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC)) &&
                                    !(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_REJECTED_CODE)
                                            || notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_MESSAGE_FAILED))){
                                pageProxy.setOOSystemPendInd(true);
                            }
                        }
                    }
                }
            }
        }catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
        return theNotificationSummaryVOCollection;
    } //end of observationAssociates()


    /**
     * LOGGING
     * TODO: this need to move to logging pipeline
     * */
    private void createAndStoreMessageLogDTCollection(Collection<LabReportSummaryContainer> reportSumVOCollection, PublicHealthCaseDto publicHealthCaseDto){
//        try {
//            Collection<MessageLogDT> coll =  new ArrayList<MessageLogDT>();
//            java.util.Date dateTime = new java.util.Date();
//            Timestamp time = new Timestamp(dateTime.getTime());
//
//            if(!reportSumVOCollection.isEmpty())
//            {
//                logger.debug("Number of observation sum vo: " + reportSumVOCollection.size());
//                Iterator<Object>  theIterator = reportSumVOCollection.iterator();
//                while( theIterator.hasNext() )
//                {
//                    ReportSummaryInterface reportSumVO = (ReportSummaryInterface)theIterator.next();
//                    if(reportSumVO.getIsAssociated()== true && reportSumVO.getIsTouched()== true){
//                        PublicHealthCaseRootDAOImpl phc = new PublicHealthCaseRootDAOImpl();
//                        PublicHealthCaseDto phcDT =phc.getOpenPublicHealthCaseWithInvestigatorDT(publicHealthCaseDto.getPublicHealthCaseUid());
//                        Long providerUid=nbsSecurityObj.getTheUserProfile().getTheUser().getProviderUid();
//                        if( phcDT!=null
//                                && (providerUid==null
//                                || !(providerUid.compareTo(phcDT.getCurrentInvestigatorUid())==0))){
//                            MessageLogDT messageLogDT =createMessageLogDT(phcDT, nbsSecurityObj);
//                            coll.add(messageLogDT);
//                        }
//
//                    }
//                }
//                MessageLogDAOImpl messageLogDAOImpl =  new MessageLogDAOImpl();
//                try {
//                    messageLogDAOImpl.storeMessageLogDTCollection(coll);
//                } catch (Exception e) {
//                    logger.error("Unable to store the Error message in createAndStoreMesssageLogDTCollection for = "
//                            + publicHealthCaseDto.toString());
//                }
//            }
//        } catch (Exception e) {
//            logger.error("createAndStoreMesssageLogDTCollection error throw"+ e.getMessage(),e);
//        }
    }


    /**
     * This method will access the Collection<Object>  of NotificationSummaryList for passed
     * investigationUID to papulate the Notification summary on investigation page
     * @return Collection<Object>  -- Collection<Object>  of NotificationSummaryVO for the passed publicHealthCaseDT
     */
    public Collection<Object>  retrieveNotificationSummaryListForInvestigation(Long publicHealthUID) throws DataProcessingException {
        ArrayList<Object> theNotificationSummaryVOCollection  = new ArrayList<Object> ();
        if (publicHealthUID != null) {
//            if (!nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                    NBSOperationLookup.VIEW)) {
//                logger.info("INVESTIGATION = " + NBSBOLookup.INVESTIGATION +
//                        ",  VIEW = " + NBSOperationLookup.VIEW);
//                throw new NEDSSSystemException("no permissions to VIEW a notification");
//            }

            String statement[] = new String[2];
            
            statement[0] = SELECT_NOTIFICATION_FOR_INVESTIGATION_SQL;
            statement[1] = SELECT_NOTIFICATION_HIST_FOR_INVESTIGATION_SQL +" ORDER BY notHist.version_ctrl_nbr DESC";

            NotificationSummaryContainer notifVO = new NotificationSummaryContainer();
            try
            {
                for (int i = 0; i < statement.length; i++) {
                    List<NotificationSummaryContainer> retval = new ArrayList<> ();


                    retval = customRepository.retrieveNotificationSummaryListForInvestigation(publicHealthUID, statement[i]);
                    //break if there is no existing Notification
                    if (retval.size() == 0) {
                        break;
                    }
                    for (Iterator<NotificationSummaryContainer> anIterator = retval.iterator(); anIterator.hasNext(); ) {
                        NotificationSummaryContainer newVO = (NotificationSummaryContainer) anIterator.
                                next();
                        if (newVO.getCaseClassCd() != null
                                && newVO.getCaseClassCd().trim().length() != 0)
                        {
                            TreeMap<?, ?> map = catchingValueService.getCodedValuesCallRepos("PHC_CLASS");
                            newVO.setCaseClassCdTxt((String)map.get(newVO.getCaseClassCd()));
                        }
                        if (newVO.getCd() != null
                                && newVO.getCd().trim().length() != 0)
                        {
                            TreeMap<?, ?> map = catchingValueService.getCodedValuesCallRepos("PHC_CLASS");
                            newVO.setCdTxt((String)map.get(newVO.getCd()));
                        }

                        if (newVO.getCdNotif() != null
                                && newVO.getCdNotif().trim().length() != 0)
                        {
                            newVO.setCdNotif(newVO.getCdNotif());
                            newVO.setNotificationSrtDescCd(catchingValueService.getCodeDescTxtForCd(newVO.getCdNotif(),"NBS_DOC_PURPOSE"));
                        }
                        if (newVO.getRecipient() != null
                                && newVO.getRecipient().trim().length() != 0)
                        {
                            newVO.setRecipient(newVO.getRecipient());
                        }
                        else if(newVO.getRecipient()== null){
                            if(newVO.getNndInd()!=null && newVO.getNndInd().equals(NEDSSConstant.YES))
                                newVO.setRecipient(NEDSSConstant.ADMINFLAGCDC);
                            else
                                newVO.setRecipient(NEDSSConstant.LOCAl_DESC);
                        }

                        if (!newVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_NOTF))
                            newVO.setCaseReport(true);

                        newVO.setItNew(false);
                        newVO.setItDirty(false);
                        theNotificationSummaryVOCollection.add(newVO);
                    }
                }
            }
            catch (Exception e) {
                throw new DataProcessingException(e.getMessage());
            }

        }

        return theNotificationSummaryVOCollection;
    } //retrieveNotificationSummaryListForInvestigationo


    /**
     * This method will access the Collection<Object>  of NotificationSummaryList for passed
     * investigationUID to papulate the Notification summary on investigation page
     * @return Collection<Object>  -- Collection<Object>  of NotificationSummaryVO for the passed publicHealthCaseDT
     */

    @SuppressWarnings("unchecked")
    public Collection<Object>  retrieveNotificationSummaryListForInvestigation1(Long publicHealthUID) throws DataProcessingException {
        ArrayList<Object> theNotificationSummaryVOCollection  = new ArrayList<Object> ();
        try {
            if (publicHealthUID != null) {
                String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(
                        NBSBOLookup.INVESTIGATION, "VIEW",
                        "Notification");

                if (dataAccessWhereClause == null) {
                    dataAccessWhereClause = "";
                }
                else {
                    dataAccessWhereClause = " AND " + dataAccessWhereClause;

                }
                String statement[] = new String[2];

                statement[0] = SELECT_NOTIFICATION_FOR_INVESTIGATION_SQL1 +
                        dataAccessWhereClause;
                statement[1] = SELECT_NOTIFICATION_HIST_FOR_INVESTIGATION_SQL1 +
                        dataAccessWhereClause + " ORDER BY notHist.version_ctrl_nbr DESC";

                NotificationSummaryContainer notifVO = new NotificationSummaryContainer();
                TreeMap<?, ?> mapPhcClass =  catchingValueService.getCodedValuesCallRepos("PHC_CLASS");
                TreeMap<?, ?> mapPhcType =  catchingValueService.getCodedValuesCallRepos("PHC_TYPE");


                for (int i = 0; i < statement.length; i++) {
                    List<Object> inputArg = new ArrayList<Object> ();
                    inputArg.add(publicHealthUID);
                    List<NotificationSummaryContainer> retval = new ArrayList<> ();
                    retval = customRepository.retrieveNotificationSummaryListForInvestigation(publicHealthUID, statement[i]);


                    //break out of loop if there is no existing Notification
                    if (retval.size() == 0) {
                        break;
                    }
                    for (Iterator<NotificationSummaryContainer> anIterator = retval.iterator(); anIterator.hasNext(); ) {
                        NotificationSummaryContainer newVO = (NotificationSummaryContainer) anIterator.
                                next();
                        if (newVO.getCaseClassCd() != null
                                && newVO.getCaseClassCd().trim().length() != 0)
                        {
                            newVO.setCaseClassCdTxt((String)mapPhcClass.get(newVO.getCaseClassCd()));
                        }
                        if (newVO.getCd() != null
                                && newVO.getCd().trim().length() != 0)
                        {
                            newVO.setCdTxt((String)mapPhcType.get(newVO.getCd()));
                        }

                        if (newVO.getCdNotif() != null
                                && newVO.getCdNotif().trim().length() != 0)
                        {
                            newVO.setCdNotif(newVO.getCdNotif());
                            newVO.setNotificationSrtDescCd(catchingValueService.getCodeDescTxtForCd(newVO.getCdNotif(),"NBS_DOC_PURPOSE"));
                        }
                        if (newVO.getRecipient() != null
                                && newVO.getRecipient().trim().length() != 0)
                        {
                            newVO.setRecipient(newVO.getRecipient());
                        }
                        else{
                            if(newVO.getNndInd()!=null && newVO.getNndInd().equals(NEDSSConstant.YES))
                                newVO.setRecipient(NEDSSConstant.ADMINFLAGCDC);
                            else
                                newVO.setRecipient(NEDSSConstant.LOCAl_DESC);
                        }

                        if (!newVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_NOTF))
                            newVO.setCaseReport(true);

                        newVO.setItNew(false);
                        newVO.setItDirty(false);
                        theNotificationSummaryVOCollection.add(newVO);
                    }
                }

            }
        }catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return theNotificationSummaryVOCollection;
    }



    /*
     * getAssociatedInvList - from the act relationship retrieve any associated cases for the passed in class code
     * Note: This was modified for STD to also retrieve the Processing Decision stored in the add_reason_cd
     * Processing Decision is only stored for STD and only when associating a lab or morb to a closed case.
     */
    public Map<Object,Object> getAssociatedDocumentList(Long uid, String targetClassCd, String sourceClassCd) throws DataProcessingException
    {
        Map<Object,Object> assocoiatedDocMap= new HashMap<Object,Object>();
        try{
            String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(
                    NBSBOLookup.DOCUMENT, "VIEW", "");
            if (dataAccessWhereClause == null) {
                dataAccessWhereClause = "";
            }
            else {
                dataAccessWhereClause = " AND " + dataAccessWhereClause;
            }

            String ASSOCIATED_DOC_QUERY =
                    "select nbs_document.local_id \"localId\", " +
                            "nbs_document.nbs_document_uid \"uid\" " +
                            "from nbs_document  with (nolock) " +
                            "inner join act_relationship  with (nolock) on " +
                            "nbs_document.nbs_document_uid = act_relationship.source_act_uid " +
                            "and act_relationship.target_act_uid = :TargetActUid " +
                            "and act_relationship.source_class_cd = :SourceClassCd " +
                            "and target_class_cd = :TargetClassCd " +
                            "and nbs_document.record_status_cd!='LOG_DEL' ";

            ASSOCIATED_DOC_QUERY=ASSOCIATED_DOC_QUERY+dataAccessWhereClause;

            assocoiatedDocMap = customRepository.getAssociatedDocumentList(uid, targetClassCd, sourceClassCd, ASSOCIATED_DOC_QUERY);
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.toString());
        }

        return assocoiatedDocMap;
    }

    public void updateNotification(Long notificationUid,
                                           String businessTriggerCd,
                                           String phcCd,
                                           String phcClassCd,
                                           String progAreaCd,
                                           String jurisdictionCd,
                                           String sharedInd) throws DataProcessingException {

        Collection<Object>  notificationVOCollection  = null;
        try
        {

            var resNotification = notificationRepositoryUtil.getNotificationContainer(notificationUid);
            NotificationContainer notificationContainer = resNotification;
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

            //TODO: NOTIFICATION UPDATE NEED TO BE IT WON THING
            Long newNotficationUid = notificationRepositoryUtil.setNotification(notificationContainer);
        }catch (Exception e){
            throw new DataProcessingException("Error in calling ActControllerEJB.setNotification()" + e.toString());
        }


    }


}
