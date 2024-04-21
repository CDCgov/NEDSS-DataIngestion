package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.*;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.CaseManagementDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.dto.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.ConfirmationMethod;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PublicHealthCase;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.CaseManagement;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import gov.cdc.dataprocessing.service.implementation.other.OdseIdGeneratorService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.EPILINK;

@Component
public class PublicHealthCaseRepositoryUtil {
    private final PublicHealthCaseRepository publicHealthCaseRepository;
    private final EntityGroupRepository entityGroupRepository;
    private final PlaceRepository placeRepository;
    private final NonPersonLivingSubjectRepository nonPersonLivingSubjectRepository;
    private final ClinicalDocumentRepository clinicalDocumentRepository;
    private final ReferralRepository referralRepository;
    private final PatientEncounterRepository patientEncounterRepository;
    private final OdseIdGeneratorService odseIdGeneratorService;
    private final ActRepository actRepository;
    private final ActIdRepository actIdRepository;
    private final ConfirmationMethodRepository confirmationMethodRepository;
    private final ActLocatorParticipationRepository actLocatorParticipationRepository;
    private final CaseManagementRepository caseManagementRepository;

    public PublicHealthCaseRepositoryUtil(PublicHealthCaseRepository publicHealthCaseRepository,
                                          EntityGroupRepository entityGroupRepository,
                                          PlaceRepository placeRepository,
                                          NonPersonLivingSubjectRepository nonPersonLivingSubjectRepository,
                                          ClinicalDocumentRepository clinicalDocumentRepository,
                                          ReferralRepository referralRepository,
                                          PatientEncounterRepository patientEncounterRepository,
                                          OdseIdGeneratorService odseIdGeneratorService,
                                          ActRepository actRepository,
                                          ActIdRepository actIdRepository,
                                          ConfirmationMethodRepository confirmationMethodRepository,
                                          ActLocatorParticipationRepository actLocatorParticipationRepository,
                                          CaseManagementRepository caseManagementRepository) {
        this.publicHealthCaseRepository = publicHealthCaseRepository;
        this.entityGroupRepository = entityGroupRepository;
        this.placeRepository = placeRepository;
        this.nonPersonLivingSubjectRepository = nonPersonLivingSubjectRepository;
        this.clinicalDocumentRepository = clinicalDocumentRepository;
        this.referralRepository = referralRepository;
        this.patientEncounterRepository = patientEncounterRepository;
        this.odseIdGeneratorService = odseIdGeneratorService;
        this.actRepository = actRepository;
        this.actIdRepository = actIdRepository;
        this.confirmationMethodRepository = confirmationMethodRepository;
        this.actLocatorParticipationRepository = actLocatorParticipationRepository;
        this.caseManagementRepository = caseManagementRepository;
    }


    //TODO: EVALUATE THIS ONE
    public PublicHealthCaseVO update(PublicHealthCaseVO phcVO) throws DataProcessingException {
        /**
         * Inserts ConfirmationMethodDT collection
         */

        if (phcVO != null && phcVO.getTheConfirmationMethodDTCollection() != null)
        {
            insertConfirmationMethods(phcVO.getThePublicHealthCaseDT().getUid(), phcVO.getTheConfirmationMethodDTCollection());
        }
        /**
         * Inserts CaseManagementDT
         */

        if (phcVO != null && phcVO.getTheCaseManagementDT() != null
                && phcVO.getTheCaseManagementDT().isCaseManagementDTPopulated)
        {
            insertCaseManagementDT(phcVO.getThePublicHealthCaseDT().getUid(), phcVO.getTheCaseManagementDT());
        }
        /**
         * Inserts ActIdDT collection
         */

        if (phcVO != null && phcVO.getTheActIdDTCollection() != null)
        {
            insertActivityIDs(phcVO.getThePublicHealthCaseDT().getUid(), phcVO.getTheActIdDTCollection());
        }

        /**
         * Inserts ActivityLocatorParticipationDT collection
         */

        if (phcVO != null && phcVO.getTheActivityLocatorParticipationDTCollection() != null)
        {
            insertActivityLocatorParticipations(phcVO.getThePublicHealthCaseDT().getUid() ,phcVO.getTheActivityLocatorParticipationDTCollection());
        }

        phcVO.setItNew(false);
        phcVO.setItDirty(false);

        return phcVO;
    }

    @Transactional
    public PublicHealthCaseVO create(PublicHealthCaseVO phcVO) throws DataProcessingException {
        long phcUid;
        phcVO.getThePublicHealthCaseDT().setVersionCtrlNbr(1);
        if(phcVO.getThePublicHealthCaseDT().getSharedInd() == null)
        {
            phcVO.getThePublicHealthCaseDT().setSharedInd("T");
        }
        phcUid = insertPublicHealthCase(phcVO);


        phcVO.getThePublicHealthCaseDT().setPublicHealthCaseUid(phcUid);


        /**
         * Inserts ConfirmationMethodDT collection
         */

        if (phcVO != null && phcVO.getTheConfirmationMethodDTCollection() != null)
        {
            insertConfirmationMethods(phcUid, phcVO.getTheConfirmationMethodDTCollection());
        }
        /**
         * Inserts CaseManagementDT
         */

        if (phcVO != null && phcVO.getTheCaseManagementDT() != null
                && phcVO.getTheCaseManagementDT().isCaseManagementDTPopulated)
        {
            insertCaseManagementDT(phcUid, phcVO.getTheCaseManagementDT());
        }
        /**
         * Inserts ActIdDT collection
         */

        if (phcVO != null && phcVO.getTheActIdDTCollection() != null)
        {
            insertActivityIDs(phcUid, phcVO.getTheActIdDTCollection());
        }

        /**
         * Inserts ActivityLocatorParticipationDT collection
         */

        if (phcVO != null && phcVO.getTheActivityLocatorParticipationDTCollection() != null)
        {
            insertActivityLocatorParticipations(phcUid ,phcVO.getTheActivityLocatorParticipationDTCollection());
        }

        phcVO.setItNew(false);
        phcVO.setItDirty(false);
        phcUid = phcVO.getThePublicHealthCaseDT().getPublicHealthCaseUid();

        return phcVO;
    }

    private void insertActivityLocatorParticipations(Long phcUid, Collection<ActivityLocatorParticipationDto> activityIDs) throws DataProcessingException {
        ArrayList<ActivityLocatorParticipationDto> activityLocatorArray =  (ArrayList<ActivityLocatorParticipationDto> )activityIDs;
        Iterator<ActivityLocatorParticipationDto>  iterator = activityLocatorArray.iterator();
        try{
            while (iterator.hasNext())
            {
                ActivityLocatorParticipationDto  activityLocatorVO = (ActivityLocatorParticipationDto)iterator.next();

                if (activityLocatorVO.getLocatorUid() != null && activityLocatorVO.getEntityUid() != null)
                {
                    ActLocatorParticipation data = new ActLocatorParticipation(activityLocatorVO);
                    data.setActUid(phcUid);
                    actLocatorParticipationRepository.save(data);
                }
            }
        }catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }

    private void insertActivityIDs(Long phcUid, Collection<ActIdDto> activityIDs) throws DataProcessingException {
        Iterator<ActIdDto> anIterator = null;
        ArrayList<ActIdDto>  activityList = (ArrayList<ActIdDto> )activityIDs;

        try
        {
            /**
             * Inserts activity ids
             */
            anIterator = activityList.iterator();

            while(anIterator.hasNext())
            {
                ActIdDto activityID = (ActIdDto)anIterator.next();

                if (activityID != null)
                {
                    ActId data = new ActId(activityID);
                    data.setActUid(phcUid);
                    actIdRepository.save(data);

                    activityID.setItDirty(false);
                    activityID.setItNew(false);
                    activityID.setItDelete(false);
                }
                //else
                //throw new NEDSSObservationDAOAppException("Empty person name collection");
                activityID.setActUid(phcUid);
            }
        }
        catch(Exception ex)
        {
            throw new DataProcessingException( ex.toString() );
        }
    }

    private void insertCaseManagementDT(Long phcUid, CaseManagementDT caseManagementDT) throws DataProcessingException {
        updateCaseManagementWithEPIIDandFRNum(caseManagementDT);
        CaseManagement data = new CaseManagement(caseManagementDT);
        data.setPublicHealthCaseUid(phcUid);

        caseManagementRepository.save(data);

    }

    private void updateCaseManagementWithEPIIDandFRNum(CaseManagementDT caseManagementDT) throws DataProcessingException {
        // generate EPI Link Id (Lot Nbr) and field record number if not present

        try {
            if (caseManagementDT.getEpiLinkId() == null && caseManagementDT.getFieldRecordNumber() == null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yy"); // Just the year, with 2 digits
                String twoDigitYear = sdf.format(Calendar.getInstance()
                        .getTime());
                var epicUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(EPILINK);
                String epiLinkId =  epicUid.getUidPrefixCd() + epicUid.getSeedValueNbr() + epicUid.getUidSuffixCd();
//                TODO: ENV VARIABLE
//                String lotNum = PropertyUtil.getInstance().getNBS_STATE_CODE()
//                        + epiLinkId.substring(2, epiLinkId.length()-2)
//                        + twoDigitYear;
                String lotNum = "NBS_STATE_CODE"
                        + epiLinkId.substring(2, epiLinkId.length()-2)
                        + twoDigitYear;
                caseManagementDT.setEpiLinkId(lotNum);
                caseManagementDT.setFieldRecordNumber(lotNum);
            }
            else if (caseManagementDT.getEpiLinkId() != null && caseManagementDT.getFieldRecordNumber() == null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yy"); // Just the year, with 2 digits
                String twoDigitYear = sdf.format(Calendar.getInstance()
                        .getTime());
                var epicUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(EPILINK);
                String epiLinkId =  epicUid.getUidPrefixCd() + epicUid.getSeedValueNbr() + epicUid.getUidSuffixCd();
                //                TODO: ENV VARIABLE
//                String lotNum = PropertyUtil.getInstance().getNBS_STATE_CODE()
//                        + epiLinkId.substring(2, epiLinkId.length()-2)
//                        + twoDigitYear;
                String fieldRecordNumber =  "NBS_STATE_CODE"
                        + epiLinkId.substring(2, epiLinkId.length()-2)
                        + twoDigitYear;
                caseManagementDT.setFieldRecordNumber(fieldRecordNumber);
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    private Long insertPublicHealthCase(PublicHealthCaseVO phcVO) throws DataProcessingException {
        var uid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PUBLIC_HEALTH_CASE);
        var phcDT = phcVO.getThePublicHealthCaseDT();
        if (phcDT.getCaseTypeCd().equals(NEDSSConstant.I) && (phcDT.getInvestigationStatusCd() == null
                || phcDT.getInvestigationStatusCd().trim().equals("") || phcDT.getProgAreaCd() == null
                || phcDT.getProgAreaCd().trim().equals("") || phcDT.getJurisdictionCd() == null
                || phcDT.getJurisdictionCd().equals(""))) {

            String error = "********#Investigation canot be inserted with partial information for these fields : Program Area Cd = "
                    + phcDT.getProgAreaCd() + " Jurisdiction Code = " + phcDT.getJurisdictionCd()
                    + " Investigation Status = " + phcDT.getInvestigationStatusCd();
            throw new DataProcessingException(error);
        }
        var phcUid = uid.getSeedValueNbr();
        var phcLocalUid = uid.getUidPrefixCd() + phcUid + uid.getUidSuffixCd();

        Act act = new Act();
        act.setActUid(phcUid);
        act.setMoodCode(NEDSSConstant.EVENT_MOOD_CODE);
        act.setClassCode(NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE);

        actRepository.save(act);

        PublicHealthCase phc = new PublicHealthCase(phcDT);
        phc.setPublicHealthCaseUid(phcUid);
        phc.setLocalId(phcLocalUid);

        String coInfectionGroupID = null;
        if (phcDT.getCoinfectionId() != null
                && phcDT.getCoinfectionId().equalsIgnoreCase(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE)) {
            var coInfectUid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.COINFECTION_GROUP);
            coInfectionGroupID = coInfectUid.getSeedValueNbr().toString();
            phcDT.setCoinfectionId(coInfectionGroupID);
            phc.setCoinfectionId(coInfectionGroupID);
        }

        publicHealthCaseRepository.save(phc);
        phcDT.setItNew(false);
        phcDT.setItDirty(false);
        phcDT.setItDelete(false);

        phcVO.setThePublicHealthCaseDT(phcDT);
        return phcUid;
    }

    private void insertConfirmationMethods(Long phcUid, Collection<ConfirmationMethodDto> coll) throws DataProcessingException {
        if(!coll.isEmpty())
        {
            Iterator<ConfirmationMethodDto> anIterator = null;
            ArrayList<ConfirmationMethodDto> methodList = (ArrayList<ConfirmationMethodDto> )coll;

            try
            {
                /**
                 * Inserts confirmation methods
                 */
                anIterator = methodList.iterator();

                while(anIterator.hasNext())
                {
                    ConfirmationMethodDto confirmationMethod = (ConfirmationMethodDto)anIterator.next();

                    if (confirmationMethod != null)
                    {
                        ConfirmationMethod data = new ConfirmationMethod(confirmationMethod);
                        data.setPublicHealthCaseUid(phcUid);
                        if(confirmationMethod.getConfirmationMethodCd() == null)
                        {
                            data.setConfirmationMethodCd("Unknown");
                        }
                        confirmationMethodRepository.save(data);

                        confirmationMethod.setPublicHealthCaseUid(phcUid);
                        confirmationMethod.setItNew(false);
                        confirmationMethod.setItDirty(false);
                    }
                    else
                    {
                        continue;
                    }
                }
            }
            catch(Exception ex)
            {
                throw new DataProcessingException( ex.toString(), ex);
            }
        }
    }

    public PublicHealthCaseVO getPublicHealthCaseContainer(long publicHealthCaseUid) throws DataProcessingException {
        var phc = findPublicHealthCase(publicHealthCaseUid);
        if (phc == null) {
            throw new DataProcessingException("Public Health Case Not Exist");
        }

        boolean isStdHivProgramAreaCode= false;


        //TODO: ENV VARIABLE - STD_PROGRAM_AREAS = STD
//        if(properties.getSTDProgramAreas()!=null){
//            StringTokenizer st2 = new StringTokenizer(properties.getSTDProgramAreas(), ",");
//            if (st2 != null) {
//                while (st2.hasMoreElements()) {
//                    if (st2.nextElement().equals(phc.getProgAreaCd())) {
//                        isStdHivProgramAreaCode= true;
//                        break;
//                    }
//                }
//            }
//        }
        phc.setStdHivProgramAreaCode(isStdHivProgramAreaCode);

        PublicHealthCaseVO publicHealthCaseContainer = new PublicHealthCaseVO();
        publicHealthCaseContainer.setThePublicHealthCaseDT(phc);
        return publicHealthCaseContainer;
    }

    public PublicHealthCaseDT findPublicHealthCase(long publicHealthCaseUid) {
        var phc = publicHealthCaseRepository.findById(publicHealthCaseUid);
        return phc.map(PublicHealthCaseDT::new).orElse(null);
    }


    public EntityGroupDto getEntityGroup(long entityGroupUid) {
        var entityGrp = entityGroupRepository.findById(entityGroupUid);
        return entityGrp.map(EntityGroupDto::new).orElse(null);
    }

    public PlaceDto getPlace(long placeUid) {
        var place = placeRepository.findById(placeUid);
        return place.map(PlaceDto::new).orElse(null);
    }

    public NonPersonLivingSubjectDto getNonPersonLivingSubject(long uid) {
        var nonp = nonPersonLivingSubjectRepository.findById(uid);
        return nonp.map(NonPersonLivingSubjectDto::new).orElse(null);
    }

    public ClinicalDocumentDto getClinicalDocument(long uid) {
        var doc = clinicalDocumentRepository.findById(uid);
        return doc.map(ClinicalDocumentDto::new).orElse(null);
    }

    public ReferralDto getReferral(long uid) {
        var doc = referralRepository.findById(uid);
        return doc.map(ReferralDto::new).orElse(null);
    }

    public PatientEncounterDto getPatientEncounter(long uid) {
        var doc = patientEncounterRepository.findById(uid);
        return doc.map(PatientEncounterDto::new).orElse(null);
    }
}
