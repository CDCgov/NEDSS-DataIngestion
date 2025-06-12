package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.notification.NotificationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.cdc.dataprocessing.constant.ComplexQueries.*;
import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.AND_UPPERCASE;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.CASE_CLASS_CODE_SET_NM;

@Service

public class RetrieveSummaryService implements IRetrieveSummaryService {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveSummaryService.class); // NOSONAR

    private final QueryHelper queryHelper;
    private final CustomRepository customRepository;
    private final ICatchingValueDpService catchingValueService;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final NotificationRepositoryUtil notificationRepositoryUtil;

    public RetrieveSummaryService(
                                  QueryHelper queryHelper,
                                  CustomRepository customRepository,
                                  ICatchingValueDpService catchingValueService,
                                  PrepareAssocModelHelper prepareAssocModelHelper,
                                  NotificationRepositoryUtil notificationRepositoryUtil) {
        this.queryHelper = queryHelper;
        this.customRepository = customRepository;
        this.catchingValueService = catchingValueService;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.notificationRepositoryUtil = notificationRepositoryUtil;
    }

    /**
     * This method will access the HashMap<Object,Object> of TreatmentSummaryVO for passed investigationUID
     * to papulate the Treatment summary on Investigation page
     * @param publicHealthUID -- UID  for investigation to Access Treatment related to it
     * @return HashMap<Object,Object> -- HashMap<Object,Object> of TreatmentSummaryVO for the passed investigationUID
     */
    @SuppressWarnings("java:S1135")
    public Map<Object,Object> retrieveTreatmentSummaryVOForInv(Long publicHealthUID) {
        //TODO: DIFFER FLOW
        return new HashMap<>();
    } // retrieveTreatmentSummaryList


    public Map<Object,Object> retrieveDocumentSummaryVOForInv(Long publicHealthUID)  {
        return customRepository.retrieveDocumentSummaryVOForInv(publicHealthUID);
    } // retrieveDocumentSummaryList

    @SuppressWarnings({"java:S3776","java:S1066"})

    public Collection<Object>  notificationSummaryOnInvestigation(PublicHealthCaseContainer publicHealthCaseContainer, Object object) throws DataProcessingException {

        Collection<Object>  theNotificationSummaryVOCollection;
        Long publicHealthCaseUID = null;
        NotificationSummaryContainer notificationSummaryVO = null;

        if (publicHealthCaseContainer != null) {
            publicHealthCaseUID = publicHealthCaseContainer.getThePublicHealthCaseDto().
                    getPublicHealthCaseUid();
        }

        if (publicHealthCaseContainer != null && publicHealthCaseContainer.getThePublicHealthCaseDto().getCaseClassCd() != null) {
            theNotificationSummaryVOCollection  =retrieveNotificationSummaryListForInvestigation(publicHealthCaseUID);
        }
        else {
            theNotificationSummaryVOCollection  =retrieveNotificationSummaryListForInvestigation1(publicHealthCaseUID);
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
                        if(object instanceof InvestigationContainer investigationProxyVO){
                            investigationProxyVO.setAssociatedNotificationsInd(true);
                        } else if(object instanceof PamProxyContainer pamProxy) {
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
                }
            }
        }

        theNotificationSummaryVOCollection = notificationSummaryOnInvestigationProcessingNotificationCol(theNotificationSummaryVOCollection,
                 notificationSummaryVO,
                 object);
        return theNotificationSummaryVOCollection;
    } //end of observationAssociates()

    @SuppressWarnings("java:S3776")
    protected Collection<Object> notificationSummaryOnInvestigationProcessingNotificationCol(Collection<Object> theNotificationSummaryVOCollection,
                                                                               NotificationSummaryContainer notificationSummaryVO,
                                                                               Object object) {
        if (theNotificationSummaryVOCollection  != null) {
            for (Object o : theNotificationSummaryVOCollection) {
                notificationSummaryVO = (NotificationSummaryContainer) o; //NOSONAR
                if (object instanceof InvestigationContainer investigationProxyVO) {
                    if (notificationSummaryVO.isCaseReport()) {
                        if (notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_APPROVED_CODE)) {
                            investigationProxyVO.setOOSystemInd(true);
                        }

                        if (notificationSummaryVO.isHistory != null &&
                                !notificationSummaryVO.isHistory.equals("T") &&
                                notificationSummaryVO.getCdNotif() != null &&
                                (notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF)
                                        || notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC))
                                && !(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_REJECTED_CODE) ||
                                        notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_MESSAGE_FAILED))) {
                            investigationProxyVO.setOOSystemPendInd(true);
                        }
                    }

                }
                else if (object instanceof PamProxyContainer pamProxy) // NOSONAR
                {
                    if (notificationSummaryVO.isCaseReport()) {
                        if (notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_APPROVED_CODE)) {
                            pamProxy.setOOSystemInd(true);
                        }

                        if (notificationSummaryVO.isHistory != null &&
                                !notificationSummaryVO.isHistory.equals("T") &&
                                notificationSummaryVO.getCdNotif() != null &&
                                (notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF) || notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC)) &&
                                !(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_REJECTED_CODE)
                                        || notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_MESSAGE_FAILED))) {
                            pamProxy.setOOSystemPendInd(true);
                        }
                    }
                }
                else if (object instanceof PageActProxyContainer pageProxy && notificationSummaryVO.isCaseReport()) // NOSONAR
                {
                    if (notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_APPROVED_CODE)) {
                        pageProxy.setOOSystemInd(true);
                    }

                    if (notificationSummaryVO.isHistory != null &&
                            !notificationSummaryVO.isHistory.equals("T") &&
                            notificationSummaryVO.getCdNotif() != null &&
                            (notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF) || notificationSummaryVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_EXP_NOTF_PHDC)) &&
                            !(notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_REJECTED_CODE)
                                    || notificationSummaryVO.getRecordStatusCd().trim().equals(NEDSSConstant.NOTIFICATION_MESSAGE_FAILED))) {
                        pageProxy.setOOSystemPendInd(true);
                    }
                }
            }
        }

        return theNotificationSummaryVOCollection;
    }


    /**
     * This method will access the Collection<Object>  of NotificationSummaryList for passed
     * investigationUID to papulate the Notification summary on investigation page
     * @return Collection<Object>  -- Collection<Object>  of NotificationSummaryVO for the passed publicHealthCaseDT
     */
    @SuppressWarnings({"java:S3776","java:S1197"})
    protected Collection<Object>  retrieveNotificationSummaryListForInvestigation(Long publicHealthUID) throws DataProcessingException {
        ArrayList<Object> theNotificationSummaryVOCollection  = new ArrayList<> ();
        if (publicHealthUID != null) {
            String statement[] = new String[2];
            statement[0] = SELECT_NOTIFICATION_FOR_INVESTIGATION_SQL;
            statement[1] = SELECT_NOTIFICATION_HIST_FOR_INVESTIGATION_SQL +" ORDER BY notHist.version_ctrl_nbr DESC";
            for (String s : statement) {
                List<NotificationSummaryContainer> retval;

                retval = customRepository.retrieveNotificationSummaryListForInvestigation(publicHealthUID, s);
                if (retval.isEmpty()) {
                    break;
                }
                for (NotificationSummaryContainer newVO : retval) {
                    if (newVO.getCaseClassCd() != null
                            && !newVO.getCaseClassCd().trim().isEmpty()) {
                        catchingValueService.getCodedValuesCallRepos(CASE_CLASS_CODE_SET_NM);
                        newVO.setCaseClassCdTxt(catchingValueService.getCodedValuesCallRepos(newVO.getCaseClassCd()));
                    }
                    if (newVO.getCd() != null
                            && !newVO.getCd().trim().isEmpty()) {
                        catchingValueService.getCodedValuesCallRepos(CASE_CLASS_CODE_SET_NM);
                        newVO.setCdTxt(catchingValueService.getCodedValuesCallRepos(newVO.getCd()));
                    }

                    if (newVO.getCdNotif() != null
                            && !newVO.getCdNotif().trim().isEmpty()) {
                        newVO.setCdNotif(newVO.getCdNotif());
                        newVO.setNotificationSrtDescCd(catchingValueService.getCodeDescTxtForCd(newVO.getCdNotif(), "NBS_DOC_PURPOSE"));
                    }
                    if (newVO.getRecipient() != null
                            && !newVO.getRecipient().trim().isEmpty()) {
                        newVO.setRecipient(newVO.getRecipient());
                    } else if (newVO.getRecipient() == null) {
                        if (newVO.getNndInd() != null && newVO.getNndInd().equals(NEDSSConstant.YES))
                            newVO.setRecipient(NEDSSConstant.ADMINFLAGCDC);
                        else
                            newVO.setRecipient(NEDSSConstant.LOCAl_DESC);
                    }

                    if (newVO.getCdNotif() != null && !newVO.getCdNotif().equals(NEDSSConstant.CLASS_CD_NOTF))
                        newVO.setCaseReport(true);

                    newVO.setItNew(false);
                    newVO.setItDirty(false);
                    theNotificationSummaryVOCollection.add(newVO);
                }
            }
        }
        return theNotificationSummaryVOCollection;
    }
    @SuppressWarnings({"unchecked","java:S3776","java:S1197"})
    protected Collection<Object>  retrieveNotificationSummaryListForInvestigation1(Long publicHealthUID) throws DataProcessingException {
        ArrayList<Object> theNotificationSummaryVOCollection  = new ArrayList<> ();
        if (publicHealthUID != null) {
            String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(
                    NBSBOLookup.INVESTIGATION, "VIEW",
                    "Notification");

            if (dataAccessWhereClause == null) {
                dataAccessWhereClause = "";
            }
            else {
                dataAccessWhereClause = AND_UPPERCASE + dataAccessWhereClause;

            }
            String statement[] = new String[2];

            statement[0] = SELECT_NOTIFICATION_FOR_INVESTIGATION_SQL1 +
                    dataAccessWhereClause;
            statement[1] = SELECT_NOTIFICATION_HIST_FOR_INVESTIGATION_SQL1 +
                    dataAccessWhereClause + " ORDER BY notHist.version_ctrl_nbr DESC";


            catchingValueService.getCodedValuesCallRepos(CASE_CLASS_CODE_SET_NM);
            catchingValueService.getCodedValuesCallRepos("PHC_TYPE");

            for (String s : statement) {
                List<Object> inputArg = new ArrayList<>();
                inputArg.add(publicHealthUID);
                List<NotificationSummaryContainer> retval;
                retval = customRepository.retrieveNotificationSummaryListForInvestigation(publicHealthUID, s);


                //break out of loop if there is no existing Notification
                if (retval.isEmpty()) {
                    break;
                }
                for (NotificationSummaryContainer newVO : retval) {
                    if (newVO.getCaseClassCd() != null
                            && !newVO.getCaseClassCd().trim().isEmpty()) {
                        newVO.setCaseClassCdTxt(catchingValueService.getCodedValuesCallRepos(newVO.getCaseClassCd()));
                    }
                    if (newVO.getCd() != null
                            && !newVO.getCd().trim().isEmpty()) {
                        newVO.setCdTxt(catchingValueService.getCodedValuesCallRepos(newVO.getCd()));
                    }

                    if (newVO.getCdNotif() != null
                            && !newVO.getCdNotif().trim().isEmpty()) {
                        newVO.setCdNotif(newVO.getCdNotif());
                        newVO.setNotificationSrtDescCd(catchingValueService.getCodeDescTxtForCd(newVO.getCdNotif(), "NBS_DOC_PURPOSE"));
                    }
                    if (newVO.getRecipient() != null
                            && !newVO.getRecipient().trim().isEmpty()) {
                        newVO.setRecipient(newVO.getRecipient());
                    } else {
                        if (newVO.getNndInd() != null && newVO.getNndInd().equals(NEDSSConstant.YES))
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
        return theNotificationSummaryVOCollection;
    }



    /*
     * getAssociatedInvList - from the act relationship retrieve any associated cases for the passed in class code
     * Note: This was modified for STD to also retrieve the Processing Decision stored in the add_reason_cd
     * Processing Decision is only stored for STD and only when associating a lab or morb to a closed case.
     */
    public Map<Object,Object> getAssociatedDocumentList(Long uid, String targetClassCd, String sourceClassCd)
    {
        Map<Object,Object> assocoiatedDocMap;

        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(
                NBSBOLookup.DOCUMENT, "VIEW", "");
        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = AND_UPPERCASE + dataAccessWhereClause;
        }

        String assocQuery =
                "select nbs_document.local_id \"localId\", " +
                        "nbs_document.nbs_document_uid \"uid\" " +
                        "from nbs_document  with (nolock) " +
                        "inner join act_relationship  with (nolock) on " +
                        "nbs_document.nbs_document_uid = act_relationship.source_act_uid " +
                        "and act_relationship.target_act_uid = :TargetActUid " +
                        "and act_relationship.source_class_cd = :SourceClassCd " +
                        "and target_class_cd = :TargetClassCd " +
                        "and nbs_document.record_status_cd!='LOG_DEL' ";

        assocQuery=assocQuery+dataAccessWhereClause;

        assocoiatedDocMap = customRepository.getAssociatedDocumentList(uid, targetClassCd, sourceClassCd, assocQuery);


        return assocoiatedDocMap;
    }

    public void updateNotification(Long notificationUid,
                                           String businessTriggerCd,
                                           String phcCd,
                                           String phcClassCd,
                                           String progAreaCd,
                                           String jurisdictionCd,
                                           String sharedInd) throws DataProcessingException {


        NotificationContainer notificationContainer = notificationRepositoryUtil.getNotificationContainer(notificationUid);
        NotificationDto newNotificationDT;
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

        notificationRepositoryUtil.setNotification(notificationContainer);



    }


}
