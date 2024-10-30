package gov.cdc.dataprocessing.service.implementation.page_and_pam;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsNoteRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Service
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
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
        Long investigationUID = setPamProxy(pamProxyVO);

        Collection<LabReportSummaryContainer> observationColl = new ArrayList<>();
        if (observationTypeCd.equalsIgnoreCase(NEDSSConstant.LAB_DISPALY_FORM)) {
            LabReportSummaryContainer labSumVO = new LabReportSummaryContainer();
            labSumVO.setItTouched(true);
            labSumVO.setItAssociated(true);
            labSumVO.setObservationUid(observationUid);
            observationColl.add(labSumVO);
        }
        investigationService.setObservationAssociationsImpl(investigationUID, observationColl, true);
        return investigationUID;
    }

    public void insertPamVO(BasePamContainer pamVO, PublicHealthCaseContainer publichHealthCaseVO)
            throws DataProcessingException{
        //NOTE: PAM IS EMPTY IN RELEVANT FLOW
        answerService.storeActEntityDTCollectionWithPublicHealthCase(pamVO.getActEntityDTCollection(),  publichHealthCaseVO.getThePublicHealthCaseDto());
    }


    @SuppressWarnings({"java:S1135", "java:S3776", "java:S1871"})
    private Long setPamProxy(PamProxyContainer pamProxyVO) throws DataProcessingException {

        PublicHealthCaseDto phcDT = pamProxyVO.getPublicHealthCaseContainer()
                .getThePublicHealthCaseDto();

        // if both are false throw exception
        if ((!pamProxyVO.isItNew()) && (!pamProxyVO.isItDirty())) {
            throw new DataProcessingException("pamProxyVO.isItNew() = "
                    + pamProxyVO.isItNew() + " and pamProxyVO.isItDirty() = "
                    + pamProxyVO.isItDirty() + " for setPamProxy");
        }
        if (pamProxyVO.isItNew()) {
            // TODO: Not relevant for ELR
        } else if (pamProxyVO.isItDirty()) {
            // TODO: Not relevant for ELR
        }


        if(pamProxyVO.isItDirty()){
            try {
                //update auto resend notifications
                investigationService.updateAutoResendNotificationsAsync(pamProxyVO);
            }
            catch (Exception e) {
                NNDActivityLogDto nndActivityLogDT = new NNDActivityLogDto();
                String phcLocalId = pamProxyVO.getPublicHealthCaseContainer().
                        getThePublicHealthCaseDto().getLocalId();
                nndActivityLogDT.setErrorMessageTxt(e.getMessage());
                if (phcLocalId != null)
                {
                    nndActivityLogDT.setLocalId(phcLocalId);
                }
                else
                {
                    nndActivityLogDT.setLocalId("N/A");
                }
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
            ParticipationDto partDT;

            for (ParticipationDto participationDto : pamProxyVO.getTheParticipationDTCollection()) {
                partDT = participationDto;
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
        PersonContainer personVO;

        Long falsePublicHealthCaseUid;

        try {



            Long falseUid;
            Long realUid = null;

            Iterator<PersonContainer>  anIteratorPerson;
            if (pamProxyVO.getThePersonVOCollection() != null) {
                for (anIteratorPerson = pamProxyVO.getThePersonVOCollection()
                        .iterator(); anIteratorPerson.hasNext();) {
                    personVO =  anIteratorPerson.next();
                    if (personVO.isItNew())
                    {
                        if (personVO.getThePersonDto().getCd().equals(
                                NEDSSConstant.PAT)) { // Patient
                            String businessTriggerCd = NEDSSConstant.PAT_CR;
                            realUid = patientMatchingBaseService.setPatientRevision(personVO, businessTriggerCd, NEDSSConstant.PAT);
                        }
                        else if (personVO.getThePersonDto().getCd().equals(
                                NEDSSConstant.PRV))
                        { // Provider
                            var data = patientRepositoryUtil.createPerson(personVO);
                            realUid = data.getPersonParentUid();
                        } // end of else if

                        falseUid = personVO.getThePersonDto().getPersonUid();
                        // replace the falseId with the realId
                        if (falseUid.intValue() < 0) {
                           uidService.setFalseToNewForPam(pamProxyVO, falseUid, realUid);
                        }
                    }
                    else if (personVO.isItDirty())
                    {
                        if (personVO.getThePersonDto().getCd().equals(
                                NEDSSConstant.PAT))
                        {
                            String businessTriggerCd = NEDSSConstant.PAT_EDIT;
                            realUid = patientMatchingBaseService.setPatientRevision(personVO, businessTriggerCd, NEDSSConstant.PAT);
                        }
                        else if (personVO.getThePersonDto().getCd().equals(
                                NEDSSConstant.PRV))
                        { // Provider
                            var data = patientRepositoryUtil.createPerson(personVO);
                            realUid = data.getPersonParentUid();
                        } // end of else
                    }
                } // end of for
            } // end of if(pamProxyVO.getThePersonVOCollection() != null)

            if (pamProxyVO.getPublicHealthCaseContainer() != null) {
                String businessTriggerCd = null;
                PublicHealthCaseContainer publicHealthCaseContainer = pamProxyVO.getPublicHealthCaseContainer();
                publicHealthCaseContainer.getThePublicHealthCaseDto().setPamCase(true);
                //TODO: PAM HISTORY
//                nbsHistoryDAO.getPamHistory(pamProxyVO.getPublicHealthCaseContainer());
                PublicHealthCaseDto publicHealthCaseDto = publicHealthCaseContainer.getThePublicHealthCaseDto();
                RootDtoInterface rootDTInterface = publicHealthCaseDto;
                String businessObjLookupName = NBSBOLookup.INVESTIGATION;
                if (pamProxyVO.isItNew()) {
                    businessTriggerCd = "INV_CR";
                } else if (pamProxyVO.isItDirty()) {
                    businessTriggerCd = "INV_EDIT";
                }
                String tableName = "PUBLIC_HEALTH_CASE";
                String moduleCd = "BASE";
                publicHealthCaseDto = (PublicHealthCaseDto) prepareAssocModelHelper.prepareVO(rootDTInterface, businessObjLookupName,
                                businessTriggerCd, tableName, moduleCd, rootDTInterface.getVersionCtrlNbr());
                publicHealthCaseContainer.setThePublicHealthCaseDto(publicHealthCaseDto);

                falsePublicHealthCaseUid = publicHealthCaseContainer
                        .getThePublicHealthCaseDto().getPublicHealthCaseUid();
                actualUid = publicHealthCaseService.setPublicHealthCase(
                        publicHealthCaseContainer);
                logger.debug("actualUid.intValue() = {}", actualUid.intValue());
                if (falsePublicHealthCaseUid.intValue() < 0) {
                    uidService.setFalseToNewForPam(pamProxyVO, falsePublicHealthCaseUid, actualUid);
                    publicHealthCaseContainer.getThePublicHealthCaseDto()
                            .setPublicHealthCaseUid(actualUid);
                }
                logger.debug("falsePublicHealthCaseUid.intValue() = {}", falsePublicHealthCaseUid.intValue());
            }
            if( pamProxyVO.isUnsavedNote() && pamProxyVO.getNbsNoteDTColl()!=null && !pamProxyVO.getNbsNoteDTColl().isEmpty()){
                nbsNoteRepositoryUtil.storeNotes(actualUid, pamProxyVO.getNbsNoteDTColl());

            }
            // this collection should only be populated in edit scenario, xz defect 11861 (10/01/04)
            if (pamProxyVO.getTheNotificationSummaryVOCollection() != null) {
                Collection<Object>  notSumVOColl = pamProxyVO.getTheNotificationSummaryVOCollection();
                for (Object o : notSumVOColl) {
                    NotificationSummaryContainer notSummaryVO = (NotificationSummaryContainer) o;
                    // Only handles notifications that are not history and not in auto-resend status.
                    // for auto resend, it'll be handled separately.  xz defect 11861 (10/07/04)
                    if (notSummaryVO.getIsHistory().equals("F") && notSummaryVO.getAutoResendInd().equals("F")) {
                        Long notificationUid = notSummaryVO.getNotificationUid();
                        String phcCd = phcDT.getCd();
                        String phcClassCd = phcDT.getCaseClassCd();
                        String progAreaCd = phcDT.getProgAreaCd();
                        String jurisdictionCd = phcDT.getJurisdictionCd();
                        String sharedInd = phcDT.getSharedInd();
                        String notificationRecordStatusCode = notSummaryVO.getRecordStatusCd();
                        if (notificationRecordStatusCode != null) {
                            String trigCd = null;

                            /* The notification status remains same when the
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
                                // we only need to update notification when trigCd is not null
                                retrieveSummaryService.updateNotification(notificationUid, trigCd, phcCd, phcClassCd, progAreaCd, jurisdictionCd, sharedInd);
                            }

                        }
                    }
                }
            }
            Long docUid = null;
            if (pamProxyVO.getPublicHealthCaseContainer() != null && pamProxyVO.getPublicHealthCaseContainer()
                    .getTheActRelationshipDTCollection() != null) {
                Iterator<ActRelationshipDto>  anIteratorAct = null;
                for (anIteratorAct = pamProxyVO.getPublicHealthCaseContainer()
                        .getTheActRelationshipDTCollection().iterator(); anIteratorAct
                             .hasNext();) {
                    ActRelationshipDto actRelationshipDT = anIteratorAct
                            .next();
                    if(actRelationshipDT.getTypeCd() != null && actRelationshipDT.getTypeCd().equals(NEDSSConstant.DocToPHC))
                    {
                        docUid  = actRelationshipDT.getSourceActUid();
                    }
                    if (actRelationshipDT.isItDelete()) {
                        actRelationshipRepositoryUtil.insertActRelationshipHist(actRelationshipDT);

                    }
                    actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDT);
                }
            }
            /*
             * Updating the Document table
             */
            //Getting the DocumentEJB reference
            if(docUid != null){
                try{

                    //TODO: NBS DOCUMENT, not relevant for ELR
                }catch(Exception e){
                    throw new DataProcessingException(e.getMessage(),e);
                }
            }

            Iterator<ParticipationDto>  anIteratorPat;
            if (pamProxyVO.getTheParticipationDTCollection() != null) {
                for (anIteratorPat = pamProxyVO.getTheParticipationDTCollection().iterator(); anIteratorPat
                        .hasNext();) {
                    ParticipationDto participationDT = anIteratorPat
                            .next();
                    if (participationDT.isItDelete()) {
                        participationRepositoryUtil.insertParticipationHist(participationDT);

                    }
                    participationRepositoryUtil.storeParticipation(participationDT);
                }
            }
            //TODO: NBS PAM
//            if (pamProxyVO.getPamVO() != null && pamProxyVO.isItNew()) {
//                pamRootDAO.insertPamVO(pamProxyVO.getPamVO(), pamProxyVO.getPublicHealthCaseContainer());
//            } else if (pamProxyVO.getPamVO() != null && pamProxyVO.isItDirty()) {
//                pamRootDAO.editPamVO(pamProxyVO.getPamVO(), pamProxyVO.getPublicHealthCaseContainer());
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
