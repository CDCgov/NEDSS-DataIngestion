package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.phc.*;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.CaseManagement;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ConfirmationMethod;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PublicHealthCase;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.utilities.component.act.ActIdRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActLocatorParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component

public class PublicHealthCaseRepositoryUtil {
    private final PublicHealthCaseJdbcRepository publicHealthCaseJdbcRepository;
    private final SupportForPhcJdbcRepository supportForPhcJdbcRepository;

    private final ActJdbcRepository actJdbcRepository;
    private final ActIdJdbcRepository actIdJdbcRepository;

    private final ConfirmationMethodJdbcRepository confirmationMethodJdbcRepository;
    private final CaseManagementJdbcRepository caseManagementJdbcRepository;
    private final NbsCaseAnswerJdbcRepository nbsCaseAnswerJdbcRepository;
    private final NbsActJdbcRepository nbsActJdbcRepository;
    private final ActLocatorParticipationJdbcRepository actLocatorParticipationJdbcRepository;

    private final ConfirmationMethodRepositoryUtil confirmationMethodRepositoryUtil;
    private final CaseManagementRepositoryUtil caseManagementRepositoryUtil;
    private final ActIdRepositoryUtil actIdRepositoryUtil;
    private final ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    private final ParticipationRepositoryUtil participationRepositoryUtil;
    private final UidPoolManager uidPoolManager;

    public PublicHealthCaseRepositoryUtil(PublicHealthCaseJdbcRepository publicHealthCaseJdbcRepository,
                                          SupportForPhcJdbcRepository supportForPhcJdbcRepository,
                                          ActJdbcRepository actJdbcRepository,
                                          ActIdJdbcRepository actIdJdbcRepository,
                                          ConfirmationMethodJdbcRepository confirmationMethodJdbcRepository,
                                          CaseManagementJdbcRepository caseManagementJdbcRepository,
                                          NbsCaseAnswerJdbcRepository nbsCaseAnswerJdbcRepository,
                                          NbsActJdbcRepository nbsActJdbcRepository,
                                          ActLocatorParticipationJdbcRepository actLocatorParticipationJdbcRepository,
                                          ConfirmationMethodRepositoryUtil confirmationMethodRepositoryUtil,
                                          CaseManagementRepositoryUtil caseManagementRepositoryUtil,
                                          ActIdRepositoryUtil actIdRepositoryUtil,
                                          ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil,
                                          ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                                          ParticipationRepositoryUtil participationRepositoryUtil,
                                          @Lazy UidPoolManager uidPoolManager) {
        this.publicHealthCaseJdbcRepository = publicHealthCaseJdbcRepository;
        this.supportForPhcJdbcRepository = supportForPhcJdbcRepository;
        this.actJdbcRepository = actJdbcRepository;
        this.actIdJdbcRepository = actIdJdbcRepository;
        this.confirmationMethodJdbcRepository = confirmationMethodJdbcRepository;
        this.caseManagementJdbcRepository = caseManagementJdbcRepository;
        this.nbsCaseAnswerJdbcRepository = nbsCaseAnswerJdbcRepository;
        this.nbsActJdbcRepository = nbsActJdbcRepository;
        this.actLocatorParticipationJdbcRepository = actLocatorParticipationJdbcRepository;
        this.confirmationMethodRepositoryUtil = confirmationMethodRepositoryUtil;
        this.caseManagementRepositoryUtil = caseManagementRepositoryUtil;
        this.actIdRepositoryUtil = actIdRepositoryUtil;
        this.actLocatorParticipationRepositoryUtil = actLocatorParticipationRepositoryUtil;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.uidPoolManager = uidPoolManager;
    }


    public PublicHealthCaseContainer update(PublicHealthCaseContainer phcVO) throws DataProcessingException {
        if (phcVO == null) {
            return null;
        }
        /**
         * Inserts ConfirmationMethodDT collection
         */

        if (phcVO.getTheConfirmationMethodDTCollection() != null)
        {
            insertConfirmationMethods(phcVO.getThePublicHealthCaseDto().getUid(), phcVO.getTheConfirmationMethodDTCollection());
        }
        /**
         * Inserts CaseManagementDto
         */

        if (phcVO.getTheCaseManagementDto() != null
                && phcVO.getTheCaseManagementDto().isCaseManagementDTPopulated)
        {
            insertCaseManagementDT(phcVO.getThePublicHealthCaseDto().getUid(), phcVO.getTheCaseManagementDto());
        }
        /**
         * Inserts ActIdDT collection
         */

        if (phcVO.getTheActIdDTCollection() != null)
        {
            insertActivityIDs(phcVO.getThePublicHealthCaseDto().getUid(), phcVO.getTheActIdDTCollection());
        }

        /**
         * Inserts ActivityLocatorParticipationDT collection
         */

        if (phcVO.getTheActivityLocatorParticipationDTCollection() != null)
        {
            insertActivityLocatorParticipations(phcVO.getThePublicHealthCaseDto().getUid() ,phcVO.getTheActivityLocatorParticipationDTCollection());
        }

        phcVO.setItNew(false);
        phcVO.setItDirty(false);

        return phcVO;
    }

    public PublicHealthCaseContainer create(PublicHealthCaseContainer phcVO) throws DataProcessingException {
        long phcUid;
        phcVO.getThePublicHealthCaseDto().setVersionCtrlNbr(1);
        if(phcVO.getThePublicHealthCaseDto().getSharedInd() == null)
        {
            phcVO.getThePublicHealthCaseDto().setSharedInd("T");
        }
        phcUid = insertPublicHealthCase(phcVO);


        phcVO.getThePublicHealthCaseDto().setPublicHealthCaseUid(phcUid);


        /**
         * Inserts ConfirmationMethodDT collection
         */

        if (phcVO.getTheConfirmationMethodDTCollection() != null)
        {
            insertConfirmationMethods(phcUid, phcVO.getTheConfirmationMethodDTCollection());
        }
        /**
         * Inserts CaseManagementDto
         */

        if (phcVO.getTheCaseManagementDto() != null && phcVO.getTheCaseManagementDto().isCaseManagementDTPopulated)
        {
            insertCaseManagementDT(phcUid, phcVO.getTheCaseManagementDto());
        }
        /**
         * Inserts ActIdDT collection
         */

        if (phcVO.getTheActIdDTCollection() != null)
        {
            insertActivityIDs(phcUid, phcVO.getTheActIdDTCollection());
        }

        /**
         * Inserts ActivityLocatorParticipationDT collection
         */

        if (phcVO.getTheActivityLocatorParticipationDTCollection() != null)
        {
            insertActivityLocatorParticipations(phcUid ,phcVO.getTheActivityLocatorParticipationDTCollection());
        }

        phcVO.setItNew(false);
        phcVO.setItDirty(false);

        return phcVO;
    }

    private void insertActivityLocatorParticipations(Long phcUid, Collection<ActivityLocatorParticipationDto> activityIDs)   {
        ArrayList<ActivityLocatorParticipationDto> activityLocatorArray =  (ArrayList<ActivityLocatorParticipationDto> )activityIDs;
        for (ActivityLocatorParticipationDto activityLocatorVO : activityLocatorArray) {
            if (activityLocatorVO.getLocatorUid() != null && activityLocatorVO.getEntityUid() != null) {
                ActLocatorParticipation data = new ActLocatorParticipation(activityLocatorVO);
                data.setActUid(phcUid);
                actLocatorParticipationJdbcRepository.mergeActLocatorParticipation(data);
            }
        }

    }

    private void insertActivityIDs(Long phcUid, Collection<ActIdDto> activityIDs)   {
        Iterator<ActIdDto> anIterator;
        ArrayList<ActIdDto>  activityList = (ArrayList<ActIdDto> )activityIDs;


        anIterator = activityList.iterator();

        while(anIterator.hasNext())
        {
            ActIdDto activityID = anIterator.next();

            if (activityID != null)
            {
                ActId data = new ActId(activityID);
                data.setActUid(phcUid);
                actIdJdbcRepository.mergeActId(data);

                activityID.setItDirty(false);
                activityID.setItNew(false);
                activityID.setItDelete(false);
                activityID.setActUid(phcUid);
            }
        }

    }

    private void insertCaseManagementDT(Long phcUid, CaseManagementDto caseManagementDto) throws DataProcessingException {
        updateCaseManagementWithEPIIDandFRNum(caseManagementDto);
        CaseManagement data = new CaseManagement(caseManagementDto);
        data.setPublicHealthCaseUid(phcUid);

        caseManagementJdbcRepository.mergeCaseManagement(data);

    }

    protected void updateCaseManagementWithEPIIDandFRNum(CaseManagementDto caseManagementDto) throws DataProcessingException {
        // generate EPI Link Id (Lot Nbr) and field record number if not present


        if (caseManagementDto.getEpiLinkId() == null && caseManagementDto.getFieldRecordNumber() == null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yy"); // Just the year, with 2 digits
            String twoDigitYear = sdf.format(Calendar.getInstance()
                    .getTime());
            var epicUid = uidPoolManager.getNextUid(LocalIdClass.EPILINK, false);
            String epiLinkId =  epicUid.getClassTypeUid().getUidPrefixCd() + epicUid.getClassTypeUid().getSeedValueNbr() + epicUid.getClassTypeUid().getUidSuffixCd();
//                TODO: ENV VARIABLE
//                String lotNum = PropertyUtil.getInstance().getNBS_STATE_CODE()
//                        + epiLinkId.substring(2, epiLinkId.length()-2)
//                        + twoDigitYear;
            String lotNum = "NBS_STATE_CODE"
                    + epiLinkId.substring(2, epiLinkId.length()-2)
                    + twoDigitYear;
            caseManagementDto.setEpiLinkId(lotNum);
            caseManagementDto.setFieldRecordNumber(lotNum);
        }
        else if (caseManagementDto.getEpiLinkId() != null && caseManagementDto.getFieldRecordNumber() == null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yy"); // Just the year, with 2 digits
            String twoDigitYear = sdf.format(Calendar.getInstance()
                    .getTime());
            var epicUid = uidPoolManager.getNextUid(LocalIdClass.EPILINK, false);
            String epiLinkId =  epicUid.getClassTypeUid().getUidPrefixCd() + epicUid.getClassTypeUid().getSeedValueNbr() + epicUid.getClassTypeUid().getUidSuffixCd();
            //                TODO: ENV VARIABLE
//                String lotNum = PropertyUtil.getInstance().getNBS_STATE_CODE()
//                        + epiLinkId.substring(2, epiLinkId.length()-2)
//                        + twoDigitYear;
            String fieldRecordNumber =  "NBS_STATE_CODE"
                    + epiLinkId.substring(2, epiLinkId.length()-2)
                    + twoDigitYear;
            caseManagementDto.setFieldRecordNumber(fieldRecordNumber);
        }


    }

    private Long insertPublicHealthCase(PublicHealthCaseContainer phcVO) throws DataProcessingException {
        var uid = uidPoolManager.getNextUid(LocalIdClass.PUBLIC_HEALTH_CASE, true);
        var phcDT = phcVO.getThePublicHealthCaseDto();
        if (phcDT.getCaseTypeCd().equals(NEDSSConstant.I) && (phcDT.getInvestigationStatusCd() == null
                || phcDT.getInvestigationStatusCd().trim().isEmpty() || phcDT.getProgAreaCd() == null
                || phcDT.getProgAreaCd().trim().isEmpty() || phcDT.getJurisdictionCd() == null
                || phcDT.getJurisdictionCd().isEmpty())) {

            String error = "********#Investigation canot be inserted with partial information for these fields : Program Area Cd = "
                    + phcDT.getProgAreaCd() + " Jurisdiction Code = " + phcDT.getJurisdictionCd()
                    + " Investigation Status = " + phcDT.getInvestigationStatusCd();
            throw new DataProcessingException(error);
        }
        var phcUid = uid.getGaTypeUid().getSeedValueNbr();
        var phcLocalUid = uid.getClassTypeUid().getUidPrefixCd() + uid.getClassTypeUid().getSeedValueNbr() + uid.getClassTypeUid().getUidSuffixCd();

        Act act = new Act();
        act.setActUid(phcUid);
        act.setMoodCode(NEDSSConstant.EVENT_MOOD_CODE);
        act.setClassCode(NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE);

        actJdbcRepository.insertAct(act);

        PublicHealthCase phc = new PublicHealthCase(phcDT);
        phc.setPublicHealthCaseUid(phcUid);
        phc.setLocalId(phcLocalUid);

        String coInfectionGroupID;
        if (phcDT.getCoinfectionId() != null
                && phcDT.getCoinfectionId().equalsIgnoreCase(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE)) {
            var coInfectUid = uidPoolManager.getNextUid(LocalIdClass.COINFECTION_GROUP, false);
            coInfectionGroupID = coInfectUid.getClassTypeUid().getSeedValueNbr().toString();
            phcDT.setCoinfectionId(coInfectionGroupID);
            phc.setCoinfectionId(coInfectionGroupID);
        }

        publicHealthCaseJdbcRepository.insertPublicHealthCase(phc);
        phcDT.setItNew(false);
        phcDT.setItDirty(false);
        phcDT.setItDelete(false);

        phcVO.setThePublicHealthCaseDto(phcDT);
        return phcUid;
    }

    private void insertConfirmationMethods(Long phcUid, Collection<ConfirmationMethodDto> coll)   {
        if(!coll.isEmpty())
        {
            Iterator<ConfirmationMethodDto> anIterator;
            ArrayList<ConfirmationMethodDto> methodList = (ArrayList<ConfirmationMethodDto> )coll;

            /**
             * Inserts confirmation methods
             */
            anIterator = methodList.iterator();

            while(anIterator.hasNext())
            {
                ConfirmationMethodDto confirmationMethod = anIterator.next();

                if (confirmationMethod != null)
                {
                    ConfirmationMethod data = new ConfirmationMethod(confirmationMethod);
                    data.setPublicHealthCaseUid(phcUid);
                    if(confirmationMethod.getConfirmationMethodCd() == null)
                    {
                        data.setConfirmationMethodCd("Unknown");
                    }
                    confirmationMethodJdbcRepository.upsertConfirmationMethod(data);

                    confirmationMethod.setPublicHealthCaseUid(phcUid);
                    confirmationMethod.setItNew(false);
                    confirmationMethod.setItDirty(false);
                }
            }

        }
    }

    public PublicHealthCaseContainer loadObject(Long phcUid) throws DataProcessingException {
        var container = new PublicHealthCaseContainer();

        var phcDt = publicHealthCaseJdbcRepository.findById(phcUid);
        if (phcDt == null) {
            throw new DataProcessingException("Public Health Case Not Exist");
        }


        container.setThePublicHealthCaseDto(new PublicHealthCaseDto(phcDt));

        //  phcDt.setStdHivProgramAreaCode(isStdHivProgramAreaCode);
        container.getThePublicHealthCaseDto().setStdHivProgramAreaCode(false);

        var confirmLst = confirmationMethodRepositoryUtil.getConfirmationMethodByPhc(phcUid);
        container.setTheConfirmationMethodDTCollection(confirmLst);

        var caseMag = caseManagementRepositoryUtil.getCaseManagementPhc(phcUid);
        container.setTheCaseManagementDto(caseMag);

        var actIdLst = actIdRepositoryUtil.getActIdCollection(phcUid);
        container.setTheActIdDTCollection(actIdLst);

        var actLoc = actLocatorParticipationRepositoryUtil.getActLocatorParticipationCollection(phcUid);
        container.setTheActivityLocatorParticipationDTCollection(actLoc);

        var actRe = actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(phcUid);
        container.setTheActRelationshipDTCollection(actRe);

        var pat = participationRepositoryUtil.getParticipationsByActUid(phcUid);
        container.setTheParticipationDTCollection(pat);

        container.setItNew(false);
        container.setItDirty(false);

        return container;


    }

    public PublicHealthCaseContainer getPublicHealthCaseContainer(long publicHealthCaseUid) throws DataProcessingException {
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

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        publicHealthCaseContainer.setThePublicHealthCaseDto(phc);

        return publicHealthCaseContainer;
    }

    public PublicHealthCaseDto findPublicHealthCase(long publicHealthCaseUid) {
        var phc = publicHealthCaseJdbcRepository.findById(publicHealthCaseUid);
        if (phc == null) {
            return null;
        }
        return new PublicHealthCaseDto(phc);
    }


    public EntityGroupDto getEntityGroup(long entityGroupUid) {
        var entityGrp = supportForPhcJdbcRepository.findEntityGroupById(entityGroupUid);
        if (entityGrp == null) {
            return null;
        }
        else {
            return new EntityGroupDto(entityGrp);
        }
    }

    public PlaceDto getPlace(long placeUid) {
        var place = supportForPhcJdbcRepository.findPlaceById(placeUid);
        if (place == null) {
            return null;
        }
        else {
            return new PlaceDto(place);
        }
    }

    public NonPersonLivingSubjectDto getNonPersonLivingSubject(long uid) {
        var entity = supportForPhcJdbcRepository.findNonPersonLivingSubjectById(uid);
        return entity != null ? new NonPersonLivingSubjectDto(entity) : null;
    }

    public ClinicalDocumentDto getClinicalDocument(long uid) {
        var entity = supportForPhcJdbcRepository.findClinicalDocumentById(uid);
        return entity != null ? new ClinicalDocumentDto(entity) : null;
    }

    public ReferralDto getReferral(long uid) {
        var entity = supportForPhcJdbcRepository.findReferralById(uid);
        return entity != null ? new ReferralDto(entity) : null;
    }

    public PatientEncounterDto getPatientEncounter(long uid) {
        var entity = supportForPhcJdbcRepository.findPatientEncounterById(uid);
        return entity != null ? new PatientEncounterDto(entity) : null;
    }


    public BasePamContainer getPamVO(Long publicHealthCaseUID) throws DataProcessingException {
        BasePamContainer pamVO = new BasePamContainer();
        Map<Object,Object> pamAnswerDTReturnMap = getPamAnswerDTMaps(publicHealthCaseUID);
        Map<Object, Object> nbsAnswerMap =new HashMap<>();
        Map<Object, Object> nbsRepeatingAnswerMap =new HashMap<>();
        if(pamAnswerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION)!=null){
            nbsAnswerMap=(HashMap<Object, Object>)pamAnswerDTReturnMap.get(NEDSSConstant.NON_REPEATING_QUESTION);
        }
        if(pamAnswerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION)!=null){
            nbsRepeatingAnswerMap=(HashMap<Object, Object>)pamAnswerDTReturnMap.get(NEDSSConstant.REPEATING_QUESTION);
        }
        pamVO.setPamAnswerDTMap(nbsAnswerMap);
        pamVO.setPageRepeatingAnswerDTMap(nbsRepeatingAnswerMap);

        Collection<NbsActEntityDto>  pamCaseEntityDTCollection= getActEntityDTCollection(publicHealthCaseUID);
        pamVO.setActEntityDTCollection(pamCaseEntityDTCollection);

        return pamVO;
    }

    private Collection<NbsActEntityDto>  getActEntityDTCollection(Long actUid){
        Collection<NbsActEntityDto> lst = new ArrayList<>();
        var res = nbsActJdbcRepository.getNbsActEntitiesByActUid(actUid);
        if (res.isEmpty()) {
            return new ArrayList<>();
        }
        for (var item : res) {
            NbsActEntityDto data = new NbsActEntityDto(item);
            lst.add(data);
        }
        return lst;
    }
    @SuppressWarnings("java:S3776")
    private Map<Object, Object> getPamAnswerDTMaps(Long publicHealthCaseUID)   {
        ArrayList<Object> PamAnswerDTCollection;
        Map<Object, Object> nbsReturnAnswerMap = new HashMap<>();
        Map<Object, Object> nbsAnswerMap = new HashMap<>();
        Map<Object, Object> nbsRepeatingAnswerMap = new HashMap<>();

        var pamAnsCol = nbsCaseAnswerJdbcRepository.getNbsCaseAnswerByActUid(publicHealthCaseUID);

        if (pamAnsCol.isEmpty()) {
            return new HashMap<>();
        }

        PamAnswerDTCollection = new ArrayList<>(pamAnsCol);

        Iterator<Object> it = PamAnswerDTCollection.iterator();
        Long nbsQuestionUid = 0L;
        Collection<Object> coll = new ArrayList<>();
        while (it.hasNext())
        {
            NbsCaseAnswerDto pamAnsDT = new NbsCaseAnswerDto ((NbsCaseAnswer) it.next());

            if (pamAnsDT.getNbsQuestionUid() != null
                    && nbsQuestionUid != 0
                    && pamAnsDT.getNbsQuestionUid().longValue() != nbsQuestionUid
                    .longValue() && !coll.isEmpty()) {
                nbsAnswerMap.put(nbsQuestionUid, coll);
                coll = new ArrayList<>();
            }

            if (pamAnsDT.getAnswerGroupSeqNbr() != null && pamAnsDT.getAnswerGroupSeqNbr() > -1)
            {
                if (nbsRepeatingAnswerMap.get(pamAnsDT.getNbsQuestionUid()) == null)
                {
                    Collection collection = new ArrayList();
                    collection.add(pamAnsDT);
                    nbsRepeatingAnswerMap.put(pamAnsDT.getNbsQuestionUid(), collection);
                }
                else
                {
                    Collection collection = (Collection) nbsRepeatingAnswerMap.get(pamAnsDT.getNbsQuestionUid());
                    collection.add(pamAnsDT);
                    nbsRepeatingAnswerMap.put(pamAnsDT.getNbsQuestionUid(), collection);
                }
            }
            else if (
                    (pamAnsDT.getNbsQuestionUid() != null
                            && pamAnsDT.getNbsQuestionUid().compareTo(nbsQuestionUid) == 0
                    )
                    && pamAnsDT.getSeqNbr() != null
                    && pamAnsDT.getSeqNbr() > 0
            )
            {
                coll.add(pamAnsDT);
            }
            else if (pamAnsDT.getSeqNbr() != null && pamAnsDT.getSeqNbr() > 0)
            {
                if (!coll.isEmpty())
                {
                    nbsAnswerMap.put(nbsQuestionUid, coll);
                    coll = new ArrayList<>();
                }
                coll.add(pamAnsDT);
            }
            else
            {
                if (!coll.isEmpty())
                {
                    nbsAnswerMap.put(nbsQuestionUid, coll);
                }
                nbsAnswerMap.put(pamAnsDT.getNbsQuestionUid(), pamAnsDT);
                coll = new ArrayList<>();
            }
            nbsQuestionUid = pamAnsDT.getNbsQuestionUid();
            if (!it.hasNext() && !coll.isEmpty())
            {
                nbsAnswerMap.put(pamAnsDT.getNbsQuestionUid(), coll);
            }
        }

        nbsReturnAnswerMap.put(NEDSSConstant.NON_REPEATING_QUESTION, nbsAnswerMap);
        nbsReturnAnswerMap.put(NEDSSConstant.REPEATING_QUESTION, nbsRepeatingAnswerMap);

        return nbsReturnAnswerMap;
    }

}
