package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.MessageConstants;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.CaseManagementDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXEventProcessDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PageActProxyVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.NbsDocumentContainer;
import gov.cdc.dataprocessing.model.container.NotificationSummaryContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.interfaces.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.IPageService;
import gov.cdc.dataprocessing.service.interfaces.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.other.IUidService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.utilities.component.*;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@Service
public class PageService implements IPageService {
    private static final Logger logger = LoggerFactory.getLogger(PageService.class);
    private final IInvestigationService investigationService;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final IUidService uidService;
    private final PamRepositoryUtil pamRepositoryUtil;
    private final IPublicHealthCaseService publicHealthCaseService;
    private final IRetrieveSummaryService retrieveSummaryService;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    private final EdxEventProcessRepositoryUtil edxEventProcessRepositoryUtil;
    private final ParticipationRepositoryUtil participationRepositoryUtil;
    private final CustomRepository customRepository;
    private final NbsDocumentRepositoryUtil nbsDocumentRepositoryUtil;
    private final NbsNoteRepositoryUtil nbsNoteRepositoryUtil;

    private final AssociatedInvestigationUtil associatedInvestigationUtil;



    public PageService(IInvestigationService investigationService,
                       PatientRepositoryUtil patientRepositoryUtil,
                       PrepareAssocModelHelper prepareAssocModelHelper,
                       IUidService uidService,
                       PamRepositoryUtil pamRepositoryUtil,
                       IPublicHealthCaseService publicHealthCaseService,
                       IRetrieveSummaryService retrieveSummaryService,
                       ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                       EdxEventProcessRepositoryUtil edxEventProcessRepositoryUtil,
                       ParticipationRepositoryUtil participationRepositoryUtil,
                       CustomRepository customRepository,
                       NbsDocumentRepositoryUtil nbsDocumentRepositoryUtil,
                       NbsNoteRepositoryUtil nbsNoteRepositoryUtil,
                       AssociatedInvestigationUtil associatedInvestigationUtil) {
        this.investigationService = investigationService;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.uidService = uidService;
        this.pamRepositoryUtil = pamRepositoryUtil;
        this.publicHealthCaseService = publicHealthCaseService;
        this.retrieveSummaryService = retrieveSummaryService;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.edxEventProcessRepositoryUtil = edxEventProcessRepositoryUtil;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.customRepository = customRepository;
        this.nbsDocumentRepositoryUtil = nbsDocumentRepositoryUtil;
        this.nbsNoteRepositoryUtil = nbsNoteRepositoryUtil;
        this.associatedInvestigationUtil = associatedInvestigationUtil;
    }

    public Long setPageProxyWithAutoAssoc(String typeCd, PageActProxyVO pageProxyVO, Long observationUid,
                                          String observationTypeCd, String processingDecision) throws DataProcessingException {
        Long publicHealthCaseUID=null;
        try {
            if(typeCd.equalsIgnoreCase(NEDSSConstant.CASE)){
                publicHealthCaseUID= setPageProxyWithAutoAssoc(pageProxyVO,observationUid,observationTypeCd, processingDecision);
            }
        }
        catch (Exception re) {
            throw new DataProcessingException(re.getMessage());
        }
        return publicHealthCaseUID;
    }

    public Long setPageProxyWithAutoAssoc(PageActProxyVO pageProxyVO, Long observationUid, 
                                          String observationTypeCd, String processingDecision) throws  DataProcessingException {
        Long publicHealthCaseUID;
        try {
            publicHealthCaseUID = setPageActProxyVO(pageProxyVO);
            Collection<LabReportSummaryContainer> observationColl = new ArrayList<>();
            if (observationTypeCd.equalsIgnoreCase(NEDSSConstant.LAB_DISPALY_FORM))
            {
                LabReportSummaryContainer labSumVO = new LabReportSummaryContainer();
                labSumVO.setItTouched(true);
                labSumVO.setItAssociated(true);
                labSumVO.setObservationUid(observationUid);
                //set the add_reason_code(processing decision) for act_relationship  from initial follow-up(pre-populated from Lab report processing decision) field in case management
                if(pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT()!=null && pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().getInitFollUp()!=null)
                    labSumVO.setProcessingDecisionCd(pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().getInitFollUp());
                else
                    labSumVO.setProcessingDecisionCd(processingDecision);
                observationColl.add(labSumVO);

            }
            // TODO: MORBIDITY
            else
            {
//                MorbReportSummaryVO morbSumVO = new MorbReportSummaryVO();
//                morbSumVO.setItTouched(true);
//                morbSumVO.setItAssociated(true);
//                morbSumVO.setObservationUid(observationUid);
//                //set the add_reason_code(processing decision) for act_relationship  from initial follow-up(pre-populated from Morb report processing decision) field in case management
//                if(pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT()!=null && pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().getInitFollUp()!=null)
//                    morbSumVO.setProcessingDecisionCd(pageProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT().getInitFollUp());
//                else
//                    morbSumVO.setProcessingDecisionCd(processingDecision);
//                observationColl.add(morbSumVO);

            }

            investigationService.setObservationAssociationsImpl(publicHealthCaseUID, observationColl, true);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
        }
        return publicHealthCaseUID;
    }

    public Long setPageActProxyVO(PageActProxyVO pageProxyVO) throws  DataProcessingException {
        Long phcPatientRevisionUid=null;
        try {
            PageActProxyVO pageActProxyVO = (PageActProxyVO) pageProxyVO;
            PublicHealthCaseDT phcDT = pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT();
            boolean isCoInfectionCondition =pageActProxyVO.getPublicHealthCaseVO().isCoinfectionCondition();
            Long mprUid = null;

            // if both are false throw exception
            if ((!pageActProxyVO.isItNew()) && (!pageActProxyVO.isItDirty())) {
                throw new DataProcessingException("pageProxyVO.isItNew() = "
                        + pageActProxyVO.isItNew()
                        + " and pageProxyVO.isItDirty() = "
                        + pageActProxyVO.isItDirty() + " for setPageProxy");
            }
            logger.info("pageProxyVO.isItNew() = " + pageActProxyVO.isItNew()
                    + " and pageProxyVO.isItDirty() = "
                    + pageActProxyVO.isItDirty());

            if (pageActProxyVO.isItNew()) {
                logger.info("pageProxyVO.isItNew() = " + pageActProxyVO.isItNew()
                        + " and pageProxyVO.isItDirty() = "
                        + pageActProxyVO.isItDirty());

                //TODO: PERM
//                boolean checkInvestigationAutoCreatePermission = nbsSecurityObj
//                        .getPermission(NBSBOLookup.INVESTIGATION,
//                                NBSOperationLookup.AUTOCREATE, phcDT
//                                        .getProgAreaCd(),
//                                ProgramAreaJurisdictionUtil.ANY_JURISDICTION, phcDT
//                                        .getSharedInd());
//
//                if (!nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                        NBSOperationLookup.ADD, phcDT.getProgAreaCd(),
//                        ProgramAreaJurisdictionUtil.ANY_JURISDICTION, phcDT
//                                .getSharedInd())
//                        && !(checkInvestigationAutoCreatePermission)) {
//                    logger.info("no add permissions for setPageProxy");
//                    throw new NEDSSSystemException(
//                            "NO ADD PERMISSIONS for setPageProxy");
//                }
//                logger.info("user has add permissions for setPageProxy");
            } 
            else if (pageActProxyVO.isItDirty()) {
                //TODO: PERM
//                if (!nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                        NBSOperationLookup.EDIT, phcDT.getProgAreaCd(), phcDT
//                                .getJurisdictionCd(), phcDT.getSharedInd())) {
//                    logger.info("no edit permissions for setPageProxy");
//                    throw new NEDSSSystemException(
//                            "NO EDIT PERMISSIONS for setPageProxy");
//                }
            }


            // uncomment to test autoresend notification
            if (pageActProxyVO.isItDirty() && !pageActProxyVO.isConversionHasModified()) { //If conversion has modified pageActProxyVO then no need to re-queue notifications.
                try {
                    // update auto resend notifications
                    investigationService.updateAutoResendNotificationsAsync(pageActProxyVO);
                } catch (Exception e) {
                    //TODO: LOG NND LOG
//                    NNDActivityLogDto nndActivityLogDT = new NNDActivityLogDto();
//                    String phcLocalId = pageActProxyVO.getPublicHealthCaseVO()
//                            .getThePublicHealthCaseDT().getLocalId();
//                    nndActivityLogDT.setErrorMessageTxt(e.toString());
//                    if (phcLocalId != null)
//                        nndActivityLogDT.setLocalId(phcLocalId);
//                    else
//                        nndActivityLogDT.setLocalId("N/A");
//                    // catch & store auto resend notifications exceptions in
//                    // NNDActivityLog table
//                    nndMessageSenderHelper.persistNNDActivityLog(nndActivityLogDT);
//                    e.printStackTrace();
                }
            }
            if (pageActProxyVO.isItNew() && (!pageActProxyVO.isItDirty())) {
                // changes according to new Analysis
                String classCd;
                Long entityUID;
                String recordStatusCd;
                ParticipationDto partDT = null;
                Iterator<ParticipationDto> partIter = pageActProxyVO
                        .getTheParticipationDtoCollection().iterator();

                while (partIter.hasNext()) {
                    partDT = (ParticipationDto) partIter.next();
                    entityUID = partDT.getSubjectEntityUid();

                    if (entityUID != null && entityUID.intValue() > 0) {
                        classCd = partDT.getSubjectClassCd();
                        if (classCd != null
                                && classCd
                                .compareToIgnoreCase(NEDSSConstant.PERSON) == 0) {
                            // Now, get PersonVO from Entity Controller and check if
                            // Person is active, if not throw
                            // DataConcurrenceException
           

                            PersonContainer personVO = patientRepositoryUtil.loadPerson(entityUID);
                            recordStatusCd = personVO.getThePersonDto().getRecordStatusCd();

                            if (recordStatusCd != null && recordStatusCd.trim().compareToIgnoreCase(NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE) == 0) {
                                throw new DataProcessingException("The Person you are trying to create Investigation no Longer exists !!");
                            }
                        } // if
                    } // entityUID > 0
                } // while
            } // if

            Long actualUid = null;
            PersonContainer personVO = null;

            Iterator<PersonContainer> anIterator = null;
            Long falsePublicHealthCaseUid = null;

            try {
                Long falseUid = null;
                Long realUid = null;
                Long patientRevisionUid=null;
                Long phcUid=null;
                if (pageActProxyVO.getThePersonContainerCollection() != null) {
                    for (anIterator = pageActProxyVO.getThePersonContainerCollection()
                            .iterator(); anIterator.hasNext();) {
                        personVO = (PersonContainer) anIterator.next();
                        if (personVO.getThePersonDto().getCd()!=null
                                && personVO.getThePersonDto().getCd().equals(NEDSSConstant.PAT)) {
                            mprUid=personVO.getThePersonDto().getPersonParentUid();
                        }

                        if (personVO.isItNew()) {
                            if (personVO.getThePersonDto().getCd().equals(
                                    NEDSSConstant.PAT)) { // Patient
                                String businessTriggerCd = NEDSSConstant.PAT_CR;
                                try {
                                    //realUid = patientRepositoryUtil.createPerson(personVO, businessTriggerCd);
                                    var data = patientRepositoryUtil.createPerson(personVO);
                                    patientRevisionUid= data.getPersonParentUid();
                                    realUid = patientRevisionUid;
                                    phcPatientRevisionUid = patientRevisionUid;
                                } catch (Exception ex) {
                                    throw new DataProcessingException("Error in entityController.setPatientRevision : " + ex.toString());
                                }
                            } else if (personVO.getThePersonDto().getCd().equals(NEDSSConstant.PRV)) { // Provider
                                String businessTriggerCd = NEDSSConstant.PRV_CR;
                                try {
                                    //realUid = entityController.setProvider(personVO, businessTriggerCd);
                                    var data = patientRepositoryUtil.createPerson(personVO);
                                    realUid = data.getPersonParentUid();
                                } catch (Exception ex) {
                                    throw new DataProcessingException("Error in entityController.setProvider : " + ex.toString());
                                }

                            } // end of else if

                            falseUid = personVO.getThePersonDto().getPersonUid();

                            // replace the falseId with the realId
                            if (falseUid.intValue() < 0) {
                                uidService.setFalseToNewForPageAct(pageActProxyVO, falseUid, realUid);
                            }
                        } else if (personVO.isItDirty()) {
                            if (personVO.getThePersonDto().getCd().equals(
                                    NEDSSConstant.PAT)) {
                                String businessTriggerCd = NEDSSConstant.PAT_EDIT;
                                try {
                                   // realUid = entityController.setPatientRevision(personVO, businessTriggerCd);
                                    var data = patientRepositoryUtil.createPerson(personVO);
                                    realUid = data.getPersonParentUid();
                                    patientRevisionUid= realUid;
                                }  catch (Exception ex) {
                                    throw new DataProcessingException("Error in entityController.setPatientRevision : " + ex.toString());
                                }
                            } else if (personVO.getThePersonDto().getCd().equals(
                                    NEDSSConstant.PRV)) { // Provider
                                String businessTriggerCd = NEDSSConstant.PRV_EDIT;
                                try {
                                    var data = patientRepositoryUtil.createPerson(personVO);
                                    realUid = data.getPersonParentUid();
//                                    realUid = entityController.setProvider(
//                                            personVO, businessTriggerCd);
                                }  catch (Exception ex) {
                                    throw new DataProcessingException("Error in entityController.setProvider : " + ex.toString());
                                }

                            } // end of else

                        }
                    } // end of for
                    phcDT.setCurrentPatientUid(patientRevisionUid);
                } // end of if(pageProxyVO.getThePersonVOCollection() != null)

                if (pageActProxyVO.getPublicHealthCaseVO() != null) {
                    String businessTriggerCd = null;
                    PublicHealthCaseVO publicHealthCaseVO = pageActProxyVO.getPublicHealthCaseVO();
                    publicHealthCaseVO.getThePublicHealthCaseDT().setPageCase(true);
                    if(pageActProxyVO.isItDirty())
                    {
                        pamRepositoryUtil.getPamHistory(pageActProxyVO.getPublicHealthCaseVO());
                    }
                    PublicHealthCaseDT publicHealthCaseDT = publicHealthCaseVO
                            .getThePublicHealthCaseDT();
                    if(publicHealthCaseVO.getNbsAnswerCollection()!=null)
                        logger.debug("********#publicHealthCaseVO.getNbsAnswerCollection() size from history table: "+publicHealthCaseVO.getNbsAnswerCollection().size());
                    if(publicHealthCaseDT.getPublicHealthCaseUid()!=null && publicHealthCaseDT.getVersionCtrlNbr()!=null)
                        logger.debug("********#Public Health Case Uid: "+publicHealthCaseDT.getPublicHealthCaseUid().longValue()+"" +" Version: "+publicHealthCaseDT.getVersionCtrlNbr().intValue()+"");

                    RootDtoInterface rootDTInterface = publicHealthCaseDT;
                    String businessObjLookupName = NBSBOLookup.INVESTIGATION;
                    if (pageActProxyVO.isItNew()) {
                        businessTriggerCd = "INV_CR";
                        if(isCoInfectionCondition && pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCoinfectionId()==null) {
                            logger.debug("AssociatedInvestigationUpdateUtil.updatForConInfectionId created an new coinfection id for the case");
                            pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().setCoinfectionId(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE);
                        }
                    } else if (pageActProxyVO.isItDirty()) {
                        businessTriggerCd = "INV_EDIT";

                    }
                    String tableName = "PUBLIC_HEALTH_CASE";
                    String moduleCd = "BASE";
                    publicHealthCaseDT = (PublicHealthCaseDT)
                            prepareAssocModelHelper.prepareVO(rootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd, rootDTInterface.getVersionCtrlNbr());
                    publicHealthCaseVO.setThePublicHealthCaseDT(publicHealthCaseDT);

                    falsePublicHealthCaseUid = publicHealthCaseVO
                            .getThePublicHealthCaseDT().getPublicHealthCaseUid();
                    actualUid = publicHealthCaseService.setPublicHealthCase(publicHealthCaseVO);
                    phcUid= actualUid;
                    logger.debug("actualUid.intValue() = " + actualUid.intValue());
                    if (falsePublicHealthCaseUid.intValue() < 0) {
                        logger.debug("falsePublicHealthCaseUid.intValue() = "
                                + falsePublicHealthCaseUid.intValue());
                        uidService.setFalseToNewForPageAct(pageActProxyVO, falsePublicHealthCaseUid, actualUid);
                        publicHealthCaseVO.getThePublicHealthCaseDT()
                                .setPublicHealthCaseUid(actualUid);
                    }

                    logger.debug("falsePublicHealthCaseUid.intValue() = "
                            + falsePublicHealthCaseUid.intValue());
                }

                if (pageActProxyVO.getMessageLogDTMap() != null && !pageActProxyVO.getMessageLogDTMap().isEmpty()) {


                    Set<String> set = pageActProxyVO.getMessageLogDTMap().keySet();
                    for (Iterator<String> aIterator = set.iterator(); aIterator.hasNext();) {
                        String key =(String)aIterator.next();
                        if (key.contains(MessageConstants.DISPOSITION_SPECIFIED_KEY))
                            //Investigator of Named by contact will get message for Named by contact and contact's investigation id.
                            continue;
                        MessageLogDto messageLogDT =(MessageLogDto)pageActProxyVO.getMessageLogDTMap().get(key);

                        messageLogDT.setPersonUid(patientRevisionUid);
                        if(messageLogDT.getEventUid()!=null && messageLogDT.getEventUid().longValue()>0)
                            continue;
                        else
                            messageLogDT.setEventUid(phcUid);



                    }
                    try {
                        //TODO: Message Log
                        //messageLogDAOImpl.storeMessageLogDTCollection(pageActProxyVO.getMessageLogDTMap().values());
                    } catch (Exception e) {
                        logger.error("Unable to store the Error message for = "
                                + falsePublicHealthCaseUid.intValue());
                    }
                }

                // this collection should only be populated in edit scenario, xz
                // defect 11861 (10/01/04)
                if (pageActProxyVO.getTheNotificationSummaryVOCollection() != null) {
                    Collection<Object> notSumVOColl = pageActProxyVO
                            .getTheNotificationSummaryVOCollection();
                    Iterator<Object> notSumIter = notSumVOColl.iterator();
                    while (notSumIter.hasNext()) {
                        NotificationSummaryContainer notSummaryVO = (NotificationSummaryContainer) notSumIter
                                .next();
                        // Only handles notifications that are not history and not
                        // in auto-resend status.
                        // for auto resend, it'll be handled separately. xz defect
                        // 11861 (10/07/04)
                        if (notSummaryVO.getIsHistory().equals("F")
                                && notSummaryVO.getAutoResendInd().equals("F")) {
                            Long notificationUid = notSummaryVO
                                    .getNotificationUid();
                            String phcCd = phcDT.getCd();
                            String phcClassCd = phcDT.getCaseClassCd();
                            String progAreaCd = phcDT.getProgAreaCd();
                            String jurisdictionCd = phcDT.getJurisdictionCd();
                            String sharedInd = phcDT.getSharedInd();
                            String notificationRecordStatusCode = notSummaryVO
                                    .getRecordStatusCd();
                            if (notificationRecordStatusCode != null) {
                                String trigCd = null;

                                /*
                                 * The notification status remains same when the
                                 * Investigation or Associated objects are changed
                                 */
                                if (notificationRecordStatusCode
                                        .equalsIgnoreCase(NEDSSConstant.APPROVED_STATUS)) {
                                    trigCd = NEDSSConstant.NOT_CR_APR;
                                }

                                // change from pending approval to approved
                                if (notificationRecordStatusCode
                                        .equalsIgnoreCase(NEDSSConstant.PENDING_APPROVAL_STATUS)) {
                                    trigCd = NEDSSConstant.NOT_CR_PEND_APR;
                                }
                                if (trigCd != null) {
                                    // we only need to update notification when
                                    // trigCd is not null
                                    retrieveSummaryService.updateNotification(
                                            notificationUid, trigCd, phcCd,
                                            phcClassCd, progAreaCd, jurisdictionCd,
                                            sharedInd);
                                }

                            }
                        }
                    }
                }
                Long docUid = null;

                Iterator<ActRelationshipDto> anIteratorActRelationship = null;
                if (pageActProxyVO.getPublicHealthCaseVO().getTheActRelationshipDTCollection() != null) {
                    for (anIteratorActRelationship = pageActProxyVO.getPublicHealthCaseVO()
                            .getTheActRelationshipDTCollection().iterator(); anIteratorActRelationship
                                 .hasNext();) {
                        ActRelationshipDto actRelationshipDT = (ActRelationshipDto) anIteratorActRelationship
                                .next();
                        if (actRelationshipDT.getTypeCd() != null
                                && actRelationshipDT.getTypeCd().equals(
                                NEDSSConstant.DocToPHC))
                            docUid = actRelationshipDT.getSourceActUid();
                        logger.debug("the actRelationshipDT statusTime is "
                                + actRelationshipDT.getStatusTime());
                        logger.debug("the actRelationshipDT statusCode is "
                                + actRelationshipDT.getStatusCd());
                        logger.debug("Got into The ActRelationship loop");
                        try {
                            if (actRelationshipDT.isItDelete()) {
                                actRelationshipRepositoryUtil.insertActRelationshipHist(actRelationshipDT);

                            }
                            actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDT);
                            logger
                                    .debug("Got into The ActRelationship, The ActUid is "
                                            + actRelationshipDT.getTargetActUid());
                        } catch (Exception e) {
                            throw new DataProcessingException(e.getMessage());
                        }
                    }
                }

                if (pageActProxyVO.getPublicHealthCaseVO()
                        .getEdxEventProcessDTCollection() != null) {
                    for (EDXEventProcessDT processDT : pageActProxyVO
                            .getPublicHealthCaseVO()
                            .getEdxEventProcessDTCollection()) {
                        if(processDT.getDocEventTypeCd()!=null && processDT.getDocEventTypeCd().equals(NEDSSConstant.CASE))
                            processDT.setNbsEventUid(phcUid);
                        edxEventProcessRepositoryUtil.insertEventProcess(processDT);
                        logger.debug("Inserted the event Process for sourceId: "
                                + processDT.getSourceEventId());
                    }
                }

                /*
                 * Updating the Document table
                 */
                // Getting the DocumentEJB reference
                if (docUid != null) {
                    try {

                        // get the
                        NbsDocumentContainer nbsDocVO = nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(docUid);
                        if (nbsDocVO.getNbsDocumentDT().getJurisdictionCd() == null
                                || (nbsDocVO.getNbsDocumentDT().getJurisdictionCd() != null && nbsDocVO
                                .getNbsDocumentDT().getJurisdictionCd()
                                .equals("")))
                            nbsDocVO.getNbsDocumentDT().setJurisdictionCd(
                                    pageActProxyVO.getPublicHealthCaseVO()
                                            .getThePublicHealthCaseDT()
                                            .getJurisdictionCd());
                        nbsDocumentRepositoryUtil.updateDocumentWithOutthePatient(nbsDocVO);
                    } catch (Exception e) {
                        logger.error("Error while updating the Document table", e
                                .getMessage(), e);
                        e.printStackTrace();
                        throw new DataProcessingException(e.getMessage());
                    }
                }

                Iterator<ParticipationDto> anIteratorPat = null;
                if (pageActProxyVO.getTheParticipationDtoCollection() != null) {
                    for (anIteratorPat = pageActProxyVO.getTheParticipationDtoCollection().iterator(); anIteratorPat
                                 .hasNext();) {

                        ParticipationDto participationDT = (ParticipationDto) anIteratorPat
                                .next();
                        try {
                            if (participationDT.isItDelete()) {
                                participationRepositoryUtil.insertParticipationHist(participationDT);

                            }
                            participationRepositoryUtil.storeParticipation(participationDT);
                        } catch (Exception e) {
                            throw new DataProcessingException(e.getMessage());
                        }
                    }
                }
                if( pageActProxyVO.isUnsavedNote() && pageActProxyVO.getNbsNoteDTColl()!=null && pageActProxyVO.getNbsNoteDTColl().size()>0){
                    nbsNoteRepositoryUtil.storeNotes(actualUid, pageActProxyVO.getNbsNoteDTColl());

                }
                if (pageActProxyVO.getPageVO() != null && pageActProxyVO.isItNew()) {
                    pamRootDAO.insertPamVO(pageActProxyVO.getPageVO(), pageActProxyVO.getPublicHealthCaseVO());
                } else if (pageActProxyVO.getPageVO() != null && pageActProxyVO.isItDirty()) {
                    pamRootDAO.editPamVO(pageActProxyVO.getPageVO(), pageActProxyVO.getPublicHealthCaseVO());

                } else
                {
                    logger.error("There is error in setPageActProxyVO as pageProxyVO.getPageVO() is null");
                }


            } catch (Exception e) {
                throw new DataProcessingException("ActControllerEJB Create : "+e.getMessage() + e.toString());
            }

            if( !pageActProxyVO.isRenterant() && pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCoinfectionId()!=null
                    && !pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getCoinfectionId().
                    equalsIgnoreCase(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE) && mprUid!=null
                    && !pageActProxyVO.isMergeCase() && !NEDSSConstant.INVESTIGATION_STATUS_CODE_CLOSED.equals(pageActProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getInvestigationStatusCd())) {
                associatedInvestigationUtil.updatForConInfectionId(pageActProxyVO, mprUid, actualUid);
            }

            if(pageActProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT()!=null) {
                boolean isStdHivProgramAreaCode =PropertyUtil.isStdOrHivProgramArea(pageProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getProgAreaCd());
                if(isStdHivProgramAreaCode)
                {
                    updateNamedAsContactDisposition(pageActProxyVO.getPublicHealthCaseVO().getTheCaseManagementDT());
                }

            }
            return actualUid;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void updateNamedAsContactDisposition(CaseManagementDT caseManagementDT) throws DataProcessingException {
        if (caseManagementDT.getPublicHealthCaseUid() == null)  //auto field followup create in progress..
            return;
        try {

            String dispositionCd =caseManagementDT.getFldFollUpDispo();
            if(dispositionCd!=null && dispositionCd.equalsIgnoreCase(NEDSSConstant.FROM1_A_PREVENTATIVE_TREATMENT)) {
                dispositionCd = NEDSSConstant.TO1_Z_PREVIOUS_PREVENTATIVE_TREATMENT;
            }
            else if(dispositionCd!=null && dispositionCd.equalsIgnoreCase(NEDSSConstant.FROM2_C_INFECTED_BROUGHT_TO_TREATMENT)) {
                dispositionCd = NEDSSConstant.TO2_E_PREVIOUSLY_TREATED_FOR_THIS_INFECTION;
            }
            Timestamp fldFollowUpDispDate=caseManagementDT.getFldFollUpDispoDate();


            int numbersOfAssociatedContactRecords= ctContactDAO.countNamedAsContactDispoInvestigations(caseManagementDT.getPublicHealthCaseUid());
            logger.debug("numbersOfAssociatedContactRecords is "+numbersOfAssociatedContactRecords);

            if(numbersOfAssociatedContactRecords>0) {
                ctContactDAO.updateNamedAsContactDispoInvestigation(dispositionCd,fldFollowUpDispDate, caseManagementDT.getPublicHealthCaseUid());
                logger.debug("updateNamedAsContactDisposition update was successful for "+numbersOfAssociatedContactRecords+" numbers of associated investigations.");
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
        }
    }





}


