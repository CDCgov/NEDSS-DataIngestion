package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.*;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.IPamService;
import gov.cdc.dataprocessing.service.interfaces.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.service.interfaces.other.IUidService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsNoteRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Service
public class PamService implements IPamService {
    private static final Logger logger = LoggerFactory.getLogger(PamService.class);

    private final IInvestigationService investigationService;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final IRetrieveSummaryService retrieveSummaryService;
    private final IPublicHealthCaseService publicHealthCaseService;

    private final IUidService uidService;
    private final ParticipationRepositoryUtil participationRepositoryUtil;

    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;

    private final NbsNoteRepositoryUtil nbsNoteRepositoryUtil;
    private final IAnswerService answerService;

    private final PatientMatchingBaseService patientMatchingBaseService;

    public PamService(IInvestigationService investigationService,
                      PatientRepositoryUtil patientRepositoryUtil,
                      PrepareAssocModelHelper prepareAssocModelHelper,
                      IRetrieveSummaryService retrieveSummaryService,
                      IPublicHealthCaseService publicHealthCaseService,
                      IUidService uidService,
                      ParticipationRepositoryUtil participationRepositoryUtil,
                      ActRelationshipRepositoryUtil actRelationshipRepositoryUtil, NbsNoteRepositoryUtil nbsNoteRepositoryUtil, IAnswerService answerService,
                      PatientMatchingBaseService patientMatchingBaseService) {
        this.investigationService = investigationService;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.retrieveSummaryService = retrieveSummaryService;
        this.publicHealthCaseService = publicHealthCaseService;
        this.uidService = uidService;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.nbsNoteRepositoryUtil = nbsNoteRepositoryUtil;
        this.answerService = answerService;
        this.patientMatchingBaseService = patientMatchingBaseService;
    }

    public Long setPamProxyWithAutoAssoc(PamProxyContainer pamProxyVO, Long observationUid, String observationTypeCd) throws DataProcessingException {
        try {
            Long investigationUID = setPamProxy(pamProxyVO);

            Collection<LabReportSummaryContainer> observationColl = new ArrayList<>();
            if (observationTypeCd.equalsIgnoreCase(NEDSSConstant.LAB_DISPALY_FORM)) {
                LabReportSummaryContainer labSumVO = new LabReportSummaryContainer();
                labSumVO.setItTouched(true);
                labSumVO.setItAssociated(true);
                labSumVO.setObservationUid(observationUid);
                observationColl.add(labSumVO);

            }
            // TODO: MORBIDITY
            else
            {
//                MorbReportSummaryVO morbSumVO = new MorbReportSummaryVO();
//                morbSumVO.setItTouched(true);
//                morbSumVO.setItAssociated(true);
//                morbSumVO.setObservationUid(observationUid);
//                observationColl.add(morbSumVO);

            }
            investigationService.setObservationAssociationsImpl(investigationUID, observationColl, true);
            return investigationUID;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public void insertPamVO(BasePamContainer pamVO,PublicHealthCaseVO publichHealthCaseVO)
            throws DataProcessingException{
        try {
            Collection<Object>  pamDTCollection  =new ArrayList<Object> ();
            Collection<Object>  repeatingAnswerDTCollection  =new ArrayList<Object> ();
            if(pamVO.getPamAnswerDTMap()!=null){
                pamDTCollection= pamVO.getPamAnswerDTMap().values();
            }

            //NOTE: PAM IS EMPTY IN RELEVANT FLOW
            //storePamAnswerDTCollection(pamDTCollection, publichHealthCaseVO);
            if(pamVO.getPageRepeatingAnswerDTMap()!=null){
                repeatingAnswerDTCollection= pamVO.getPageRepeatingAnswerDTMap().values();
            }
            //NOTE: PAM IS EMPTY IN RELEVANT FLOW
           // storePamAnswerDTCollection(repeatingAnswerDTCollection, publichHealthCaseVO);
            answerService.storeActEntityDTCollectionWithPublicHealthCase(pamVO.getActEntityDTCollection(),  publichHealthCaseVO.getThePublicHealthCaseDT());
        } catch (Exception e) {
            throw new DataProcessingException(e.toString());
        }
    }


    private Long setPamProxy(PamProxyContainer pamProxyVO) throws DataProcessingException {

        PublicHealthCaseDT phcDT = pamProxyVO.getPublicHealthCaseVO()
                .getThePublicHealthCaseDT();

        // if both are false throw exception
        if ((!pamProxyVO.isItNew()) && (!pamProxyVO.isItDirty())) {
            throw new DataProcessingException("pamProxyVO.isItNew() = "
                    + pamProxyVO.isItNew() + " and pamProxyVO.isItDirty() = "
                    + pamProxyVO.isItDirty() + " for setPamProxy");
        }
        if (pamProxyVO.isItNew()) {
            
            //TODO: PERM
//            boolean checkInvestigationAutoCreatePermission = nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,
//                    NBSOperationLookup.AUTOCREATE, phcDT.getProgAreaCd(),	ProgramAreaJurisdictionUtil.ANY_JURISDICTION, phcDT.getSharedInd());
//
//            if (!nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION,	NBSOperationLookup.ADD, phcDT.getProgAreaCd(), ProgramAreaJurisdictionUtil.ANY_JURISDICTION, phcDT	.getSharedInd())
//                    && !(checkInvestigationAutoCreatePermission)) {
//                logger.info("no add permissions for setPamProxy");
//                throw new NEDSSSystemException("NO ADD PERMISSIONS for setPamProxy");
//            }
//            logger.info("user has add permissions for setPamProxy");
        } else if (pamProxyVO.isItDirty()) {
            //TODO: PERM
//            if (!nbsSecurityObj.getPermission(NBSBOLookup.INVESTIGATION, NBSOperationLookup.EDIT, phcDT.getProgAreaCd(), phcDT.getJurisdictionCd(), phcDT.getSharedInd())) {
//                logger.info("no edit permissions for setPamProxy");
//                throw new NEDSSSystemException("NO EDIT PERMISSIONS for setPamProxy");
//            }
        }


        if(pamProxyVO.isItDirty()){
            try {
                //update auto resend notifications
                investigationService.updateAutoResendNotificationsAsync(pamProxyVO);
            }
            catch (Exception e) {
                NNDActivityLogDto nndActivityLogDT = new NNDActivityLogDto();
                String phcLocalId = pamProxyVO.getPublicHealthCaseVO().
                        getThePublicHealthCaseDT().getLocalId();
                nndActivityLogDT.setErrorMessageTxt(e.toString());
                if (phcLocalId != null)
                    nndActivityLogDT.setLocalId(phcLocalId);
                else
                    nndActivityLogDT.setLocalId("N/A");
                //catch & store auto resend notifications exceptions in NNDActivityLog table
                //TODO: LOGGING
                //nndMessageSenderHelper.persistNNDActivityLog(nndActivityLogDT);
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        if (pamProxyVO.isItNew() && (!pamProxyVO.isItDirty())) {
            // changes according to new Analysis
            String classCd;
            Long entityUID;
            String recordStatusCd;
            ParticipationDto partDT = null;
            Iterator<ParticipationDto> partIter = pamProxyVO	.getTheParticipationDTCollection().iterator();

            while (partIter.hasNext()) {
                partDT = (ParticipationDto) partIter.next();
                entityUID = partDT.getSubjectEntityUid();

                if (entityUID != null && entityUID.intValue() > 0) {
                    classCd = partDT.getSubjectClassCd();
                    if (classCd != null
                            && classCd
                            .compareToIgnoreCase(NEDSSConstant.PERSON) == 0) {
                        // Now, get PersonContainer from Entity Controller and check if
                        // Person is active, if not throw
                        // DataConcurrenceException


                        PersonContainer personVO = patientRepositoryUtil.loadPerson(entityUID);
                        recordStatusCd = personVO.getThePersonDto().getRecordStatusCd();

                        if (recordStatusCd != null
                                && recordStatusCd
                                .trim()
                                .compareToIgnoreCase(
                                        NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE) == 0) {
                            throw new DataProcessingException(
                                    "The Person you are trying to create Investigation no Longer exists !!");
                        }
                    } // if
                } // entityUID > 0
            } // while
        } // if

        Long actualUid = null;
        PersonContainer personVO = null;

        Iterator<Object>  anIterator = null;
        Long falsePublicHealthCaseUid = null;

        try {



            Long falseUid = null;
            Long realUid = null;

            Iterator<PersonContainer>  anIteratorPerson = null;
            if (pamProxyVO.getThePersonVOCollection() != null) {
                for (anIteratorPerson = pamProxyVO.getThePersonVOCollection()
                        .iterator(); anIteratorPerson.hasNext();) {

                    personVO = (PersonContainer) anIteratorPerson.next();
                    logger.debug("The Base personDT is :"
                            + personVO.getThePersonDto());
                    logger.debug("The personUID is :"
                            + personVO.getThePersonDto().getPersonUid());

                    if (personVO.isItNew()) {
                        if (personVO.getThePersonDto().getCd().equals(
                                NEDSSConstant.PAT)) { // Patient
                            String businessTriggerCd = NEDSSConstant.PAT_CR;
                            try {
//                                realUid = entityController.setPatientRevision(
//                                        personVO, businessTriggerCd,
//                                        nbsSecurityObj);


//                                var data = patientRepositoryUtil.createPerson(personVO);
//                                realUid = data.getPersonParentUid();

                                var data = patientMatchingBaseService.setPatientRevision(personVO, businessTriggerCd);
                                realUid = data;
                            }  catch (Exception ex) {
                                throw new DataProcessingException(ex.getMessage(),ex);
                            }
                        } else if (personVO.getThePersonDto().getCd().equals(
                                NEDSSConstant.PRV)) { // Provider
                            String businessTriggerCd = NEDSSConstant.PRV_CR;
                            try {
//                                realUid = entityController.setProvider(
//                                        personVO, businessTriggerCd,
//                                        nbsSecurityObj);
                                    var data = patientRepositoryUtil.createPerson(personVO);
                                    realUid = data.getPersonParentUid();
                            } catch (Exception ex) {
                                throw new DataProcessingException(ex.getMessage(),ex);
                            }

                        } // end of else if

                        falseUid = personVO.getThePersonDto().getPersonUid();
                        // replace the falseId with the realId
                        if (falseUid.intValue() < 0) {
                           uidService.setFalseToNewForPam(pamProxyVO, falseUid, realUid);
                        }
                    } else if (personVO.isItDirty()) {
                        if (personVO.getThePersonDto().getCd().equals(
                                NEDSSConstant.PAT)) {
                            String businessTriggerCd = NEDSSConstant.PAT_EDIT;
                            try {
//                                var data = patientRepositoryUtil.createPerson(personVO);
//                                realUid = data.getPersonParentUid();

                                var data = patientMatchingBaseService.setPatientRevision(personVO, businessTriggerCd);
                                realUid = data;

                            }  catch (Exception ex) {
                                throw new DataProcessingException(ex.getMessage(),ex);
                            }
                        } else if (personVO.getThePersonDto().getCd().equals(
                                NEDSSConstant.PRV)) { // Provider
                            String businessTriggerCd = NEDSSConstant.PRV_EDIT;
                            try {
//                                realUid = entityController.setProvider(
//                                        personVO, businessTriggerCd,
//                                        nbsSecurityObj);
                                var data = patientRepositoryUtil.createPerson(personVO);
                                realUid = data.getPersonParentUid();
                            } catch (Exception ex) {
                                throw new DataProcessingException(ex.getMessage(),ex);
                            }

                        } // end of else
                        logger.debug("The realUid for the Patient/Provider is: "
                                + realUid);

                    }
                } // end of for
            } // end of if(pamProxyVO.getThePersonVOCollection() != null)

            if (pamProxyVO.getPublicHealthCaseVO() != null) {
                String businessTriggerCd = null;
                PublicHealthCaseVO publicHealthCaseVO = pamProxyVO.getPublicHealthCaseVO();
                publicHealthCaseVO.getThePublicHealthCaseDT().setPamCase(true);
                //TODO: PAM HISTORY
//                nbsHistoryDAO.getPamHistory(pamProxyVO.getPublicHealthCaseVO());
                PublicHealthCaseDT publicHealthCaseDT = publicHealthCaseVO.getThePublicHealthCaseDT();
                RootDtoInterface rootDTInterface = publicHealthCaseDT;
                String businessObjLookupName = NBSBOLookup.INVESTIGATION;
                if (pamProxyVO.isItNew()) {
                    businessTriggerCd = "INV_CR";
                } else if (pamProxyVO.isItDirty()) {
                    businessTriggerCd = "INV_EDIT";
                }
                String tableName = "PUBLIC_HEALTH_CASE";
                String moduleCd = "BASE";
                publicHealthCaseDT = (PublicHealthCaseDT) prepareAssocModelHelper.prepareVO(rootDTInterface, businessObjLookupName,
                                businessTriggerCd, tableName, moduleCd, rootDTInterface.getVersionCtrlNbr());
                publicHealthCaseVO.setThePublicHealthCaseDT(publicHealthCaseDT);

                falsePublicHealthCaseUid = publicHealthCaseVO
                        .getThePublicHealthCaseDT().getPublicHealthCaseUid();
                actualUid = publicHealthCaseService.setPublicHealthCase(
                        publicHealthCaseVO);
                logger.debug("actualUid.intValue() = " + actualUid.intValue());
                if (falsePublicHealthCaseUid.intValue() < 0) {
                    uidService.setFalseToNewForPam(pamProxyVO, falsePublicHealthCaseUid, actualUid);
                    publicHealthCaseVO.getThePublicHealthCaseDT()
                            .setPublicHealthCaseUid(actualUid);
                }
                Long publicHealthCaseUid =publicHealthCaseVO.getThePublicHealthCaseDT().getPublicHealthCaseUid();

                logger.debug("falsePublicHealthCaseUid.intValue() = "
                        + falsePublicHealthCaseUid.intValue());
            }
            if( pamProxyVO.isUnsavedNote() && pamProxyVO.getNbsNoteDTColl()!=null && pamProxyVO.getNbsNoteDTColl().size()>0){
                nbsNoteRepositoryUtil.storeNotes(actualUid, pamProxyVO.getNbsNoteDTColl());

            }
            // this collection should only be populated in edit scenario, xz defect 11861 (10/01/04)
            if (pamProxyVO.getTheNotificationSummaryVOCollection() != null) {
                Collection<Object>  notSumVOColl = pamProxyVO.getTheNotificationSummaryVOCollection();
                Iterator<Object>  notSumIter =  notSumVOColl.iterator();
                while(notSumIter.hasNext()){
                    NotificationSummaryContainer notSummaryVO = (NotificationSummaryContainer)notSumIter.next();
                    // Only handles notifications that are not history and not in auto-resend status.
                    // for auto resend, it'll be handled separately.  xz defect 11861 (10/07/04)
                    if(notSummaryVO.getIsHistory().equals("F") && notSummaryVO.getAutoResendInd().equals("F")) {
                        Long notificationUid = notSummaryVO.getNotificationUid();
                        String phcCd = phcDT.getCd();
                        String phcClassCd = phcDT.getCaseClassCd();
                        String progAreaCd = phcDT.getProgAreaCd();
                        String jurisdictionCd = phcDT.getJurisdictionCd();
                        String sharedInd = phcDT.getSharedInd();
                        String notificationRecordStatusCode = notSummaryVO.getRecordStatusCd();
                        if(notificationRecordStatusCode != null){
                            String trigCd = null;

                            /* The notification status remains same when the
                             * Investigation or Associated objects are changed
                             */
                            if (notificationRecordStatusCode
                                    .equalsIgnoreCase(NEDSSConstant.APPROVED_STATUS)){
                                trigCd = NEDSSConstant.NOT_CR_APR;
                            }

                            // change from pending approval to approved
                            if (notificationRecordStatusCode
                                    .equalsIgnoreCase(NEDSSConstant.PENDING_APPROVAL_STATUS)){
                                trigCd = NEDSSConstant.NOT_CR_PEND_APR;
                            }
                            if(trigCd != null){
                                // we only need to update notification when trigCd is not null
                                retrieveSummaryService.updateNotification(notificationUid,trigCd,phcCd,phcClassCd,progAreaCd,jurisdictionCd,sharedInd);
                            }

                        }
                    }
                }
            }
            Long docUid = null;
            if (pamProxyVO.getPublicHealthCaseVO()
                    .getTheActRelationshipDTCollection() != null) {
                Iterator<ActRelationshipDto>  anIteratorAct = null;
                for (anIteratorAct = pamProxyVO.getPublicHealthCaseVO()
                        .getTheActRelationshipDTCollection().iterator(); anIteratorAct
                             .hasNext();) {
                    ActRelationshipDto actRelationshipDT = (ActRelationshipDto) anIteratorAct
                            .next();
                    if(actRelationshipDT.getTypeCd() != null && actRelationshipDT.getTypeCd().equals(NEDSSConstant.DocToPHC))
                        docUid  = actRelationshipDT.getSourceActUid();
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
                    } catch (Exception e) {
                        throw new DataProcessingException(e.getMessage(),e);
                    }
                }
            }
            /*
             * Updating the Document table
             */
            //Getting the DocumentEJB reference
            if(docUid != null){
                try{

                    //TODO: NBS DOCUMENT
//                    NbsDocumentContainer nbsDocVO = nbsDocument.getNBSDocumentWithoutActRelationship(docUid);
//                    if(nbsDocVO.getNbsDocumentDT().getJurisdictionCd()==null || (nbsDocVO.getNbsDocumentDT().getJurisdictionCd()!=null && nbsDocVO.getNbsDocumentDT().getJurisdictionCd().equals("")))
//                    {
//                        nbsDocVO.getNbsDocumentDT().setJurisdictionCd(pamProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT().getJurisdictionCd());
//                    }
//                    Long nbsDocumentUid = nbsDocument.updateDocumentWithOutthePatient(nbsDocVO);
                }catch(Exception e){
                    throw new DataProcessingException(e.getMessage(),e);
                }
            }

            Iterator<ParticipationDto>  anIteratorPat = null;
            if (pamProxyVO.getTheParticipationDTCollection() != null) {
                for (anIteratorPat = pamProxyVO.getTheParticipationDTCollection().iterator(); anIteratorPat
                        .hasNext();) {
                    ParticipationDto participationDT = (ParticipationDto) anIteratorPat
                            .next();
                    try {
                        if (participationDT.isItDelete()) {
                            participationRepositoryUtil.insertParticipationHist(participationDT);

                        }
                        participationRepositoryUtil.storeParticipation(participationDT);

                    } catch (Exception e) {
                        throw new DataProcessingException(e.getMessage(),e);
                    }
                }
            }
            //TODO: NBS PAM
//            if (pamProxyVO.getPamVO() != null && pamProxyVO.isItNew()) {
//                pamRootDAO.insertPamVO(pamProxyVO.getPamVO(), pamProxyVO.getPublicHealthCaseVO());
//            } else if (pamProxyVO.getPamVO() != null && pamProxyVO.isItDirty()) {
//                pamRootDAO.editPamVO(pamProxyVO.getPamVO(), pamProxyVO.getPublicHealthCaseVO());
//
//            }
//            else
//                logger.error("There is error in setPamProxyVO as pamProxyVO.getPamVO() is null");
//
//            logger.debug("the actual Uid for PamProxy Publichealthcase is "
//                    + actualUid);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
        }
        return actualUid;
    }




}
