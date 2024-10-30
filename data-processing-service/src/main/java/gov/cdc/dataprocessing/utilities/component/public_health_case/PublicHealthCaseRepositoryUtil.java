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
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.CaseManagement;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ConfirmationMethod;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PublicHealthCase;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.NbsActEntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsCaseAnswerRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.phc.*;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.component.act.ActIdRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActLocatorParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.EPILINK;

@Component
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class PublicHealthCaseRepositoryUtil {
    private final PublicHealthCaseRepository publicHealthCaseRepository;
    private final EntityGroupRepository entityGroupRepository;
    private final PlaceRepository placeRepository;
    private final NonPersonLivingSubjectRepository nonPersonLivingSubjectRepository;
    private final ClinicalDocumentRepository clinicalDocumentRepository;
    private final ReferralRepository referralRepository;
    private final PatientEncounterRepository patientEncounterRepository;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;
    private final ActRepository actRepository;
    private final ActIdRepository actIdRepository;
    private final ConfirmationMethodRepository confirmationMethodRepository;
    private final ActLocatorParticipationRepository actLocatorParticipationRepository;
    private final CaseManagementRepository caseManagementRepository;
    private final ConfirmationMethodRepositoryUtil confirmationMethodRepositoryUtil;
    private final CaseManagementRepositoryUtil caseManagementRepositoryUtil;
    private final ActIdRepositoryUtil actIdRepositoryUtil;
    private final ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    private final ParticipationRepositoryUtil participationRepositoryUtil;
    private final NbsCaseAnswerRepository nbsCaseAnswerRepository;
    private final NbsActEntityRepository actEntityRepository;

    public PublicHealthCaseRepositoryUtil(PublicHealthCaseRepository publicHealthCaseRepository,
                                          EntityGroupRepository entityGroupRepository,
                                          PlaceRepository placeRepository,
                                          NonPersonLivingSubjectRepository nonPersonLivingSubjectRepository,
                                          ClinicalDocumentRepository clinicalDocumentRepository,
                                          ReferralRepository referralRepository,
                                          PatientEncounterRepository patientEncounterRepository,
                                          IOdseIdGeneratorWCacheService odseIdGeneratorService1, ActRepository actRepository,
                                          ActIdRepository actIdRepository,
                                          ConfirmationMethodRepository confirmationMethodRepository,
                                          ActLocatorParticipationRepository actLocatorParticipationRepository,
                                          CaseManagementRepository caseManagementRepository,
                                          ConfirmationMethodRepositoryUtil confirmationMethodRepositoryUtil,
                                          CaseManagementRepositoryUtil caseManagementRepositoryUtil,
                                          ActIdRepositoryUtil actIdRepositoryUtil,
                                          ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil,
                                          ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                                          ParticipationRepositoryUtil participationRepositoryUtil,
                                          NbsCaseAnswerRepository nbsCaseAnswerRepository,
                                          NbsActEntityRepository actEntityRepository) {
        this.publicHealthCaseRepository = publicHealthCaseRepository;
        this.entityGroupRepository = entityGroupRepository;
        this.placeRepository = placeRepository;
        this.nonPersonLivingSubjectRepository = nonPersonLivingSubjectRepository;
        this.clinicalDocumentRepository = clinicalDocumentRepository;
        this.referralRepository = referralRepository;
        this.patientEncounterRepository = patientEncounterRepository;
        this.odseIdGeneratorService = odseIdGeneratorService1;
        this.actRepository = actRepository;
        this.actIdRepository = actIdRepository;
        this.confirmationMethodRepository = confirmationMethodRepository;
        this.actLocatorParticipationRepository = actLocatorParticipationRepository;
        this.caseManagementRepository = caseManagementRepository;
        this.confirmationMethodRepositoryUtil = confirmationMethodRepositoryUtil;
        this.caseManagementRepositoryUtil = caseManagementRepositoryUtil;
        this.actIdRepositoryUtil = actIdRepositoryUtil;
        this.actLocatorParticipationRepositoryUtil = actLocatorParticipationRepositoryUtil;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.nbsCaseAnswerRepository = nbsCaseAnswerRepository;
        this.actEntityRepository = actEntityRepository;
    }


    @Transactional
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

    @Transactional
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

    private void insertActivityLocatorParticipations(Long phcUid, Collection<ActivityLocatorParticipationDto> activityIDs) throws DataProcessingException {
        ArrayList<ActivityLocatorParticipationDto> activityLocatorArray =  (ArrayList<ActivityLocatorParticipationDto> )activityIDs;
        Iterator<ActivityLocatorParticipationDto>  iterator = activityLocatorArray.iterator();
        try{
            while (iterator.hasNext())
            {
                ActivityLocatorParticipationDto  activityLocatorVO = iterator.next();

                if (activityLocatorVO.getLocatorUid() != null && activityLocatorVO.getEntityUid() != null)
                {
                    ActLocatorParticipation data = new ActLocatorParticipation(activityLocatorVO);
                    data.setActUid(phcUid);
                    actLocatorParticipationRepository.save(data);
                }
            }
        }catch(Exception ex){
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }

    private void insertActivityIDs(Long phcUid, Collection<ActIdDto> activityIDs) throws DataProcessingException {
        Iterator<ActIdDto> anIterator;
        ArrayList<ActIdDto>  activityList = (ArrayList<ActIdDto> )activityIDs;

        try
        {
            anIterator = activityList.iterator();

            while(anIterator.hasNext())
            {
                ActIdDto activityID = anIterator.next();

                if (activityID != null)
                {
                    ActId data = new ActId(activityID);
                    data.setActUid(phcUid);
                    actIdRepository.save(data);

                    activityID.setItDirty(false);
                    activityID.setItNew(false);
                    activityID.setItDelete(false);
                    activityID.setActUid(phcUid);
                }
            }
        }
        catch(Exception ex)
        {
            throw new DataProcessingException( ex.getMessage() );
        }
    }

    private void insertCaseManagementDT(Long phcUid, CaseManagementDto caseManagementDto) throws DataProcessingException {
        updateCaseManagementWithEPIIDandFRNum(caseManagementDto);
        CaseManagement data = new CaseManagement(caseManagementDto);
        data.setPublicHealthCaseUid(phcUid);

        caseManagementRepository.save(data);

    }

    protected void updateCaseManagementWithEPIIDandFRNum(CaseManagementDto caseManagementDto) throws DataProcessingException {
        // generate EPI Link Id (Lot Nbr) and field record number if not present

        try {
            if (caseManagementDto.getEpiLinkId() == null && caseManagementDto.getFieldRecordNumber() == null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yy"); // Just the year, with 2 digits
                String twoDigitYear = sdf.format(Calendar.getInstance()
                        .getTime());
                var epicUid = odseIdGeneratorService.getValidLocalUid(EPILINK, false);
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
                var epicUid = odseIdGeneratorService.getValidLocalUid(EPILINK, false);
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

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private Long insertPublicHealthCase(PublicHealthCaseContainer phcVO) throws DataProcessingException {
        var uid = odseIdGeneratorService.getValidLocalUid(LocalIdClass.PUBLIC_HEALTH_CASE, true);
        var phcDT = phcVO.getThePublicHealthCaseDto();
        if (phcDT.getCaseTypeCd().equals(NEDSSConstant.I) && (phcDT.getInvestigationStatusCd() == null
                || phcDT.getInvestigationStatusCd().trim().equals("") || phcDT.getProgAreaCd() == null
                || phcDT.getProgAreaCd().trim().equals("") || phcDT.getJurisdictionCd() == null
                || phcDT.getJurisdictionCd().equals(""))) {

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

        actRepository.save(act);

        PublicHealthCase phc = new PublicHealthCase(phcDT);
        phc.setPublicHealthCaseUid(phcUid);
        phc.setLocalId(phcLocalUid);

        String coInfectionGroupID;
        if (phcDT.getCoinfectionId() != null
                && phcDT.getCoinfectionId().equalsIgnoreCase(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE)) {
            var coInfectUid = odseIdGeneratorService.getValidLocalUid(LocalIdClass.COINFECTION_GROUP, false);
            coInfectionGroupID = coInfectUid.getClassTypeUid().getSeedValueNbr().toString();
            phcDT.setCoinfectionId(coInfectionGroupID);
            phc.setCoinfectionId(coInfectionGroupID);
        }

        publicHealthCaseRepository.save(phc);
        phcDT.setItNew(false);
        phcDT.setItDirty(false);
        phcDT.setItDelete(false);

        phcVO.setThePublicHealthCaseDto(phcDT);
        return phcUid;
    }

    private void insertConfirmationMethods(Long phcUid, Collection<ConfirmationMethodDto> coll) throws DataProcessingException {
        if(!coll.isEmpty())
        {
            Iterator<ConfirmationMethodDto> anIterator;
            ArrayList<ConfirmationMethodDto> methodList = (ArrayList<ConfirmationMethodDto> )coll;

            try
            {
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
                throw new DataProcessingException( ex.getMessage(), ex);
            }
        }
    }

    public PublicHealthCaseContainer loadObject(Long phcUid) throws DataProcessingException {
        try {
            var container = new PublicHealthCaseContainer();

            var phcDt = publicHealthCaseRepository.findById(phcUid);
            if (phcDt.isEmpty()) {
                throw new DataProcessingException("Public Health Case Not Exist");
            }


            container.setThePublicHealthCaseDto(new PublicHealthCaseDto(phcDt.get()));

            //  phcDt.setStdHivProgramAreaCode(isStdHivProgramAreaCode);
            container.getThePublicHealthCaseDto().setStdHivProgramAreaCode(false);

            var confirmLst = confirmationMethodRepositoryUtil.getConfirmationMethodByPhc(phcUid);
            container.setTheConfirmationMethodDTCollection(confirmLst);

            var caseMag = caseManagementRepositoryUtil.getCaseManagementPhc(phcUid);
            container.setTheCaseManagementDto(caseMag);

            var actIdLst = actIdRepositoryUtil.GetActIdCollection(phcUid);
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
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

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
        var phc = publicHealthCaseRepository.findById(publicHealthCaseUid);
        // ADD CODE TO FIND PHC RELATED OBJECT HERE
        return phc.map(PublicHealthCaseDto::new).orElse(null);
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

    public BasePamContainer getPamVO(Long publicHealthCaseUID) throws DataProcessingException {
        BasePamContainer pamVO = new BasePamContainer();
        try{
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
        }catch(Exception ex){
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return pamVO;
    }

    private Collection<NbsActEntityDto>  getActEntityDTCollection(Long actUid){
        Collection<NbsActEntityDto> lst = new ArrayList<>();
        var res = actEntityRepository.getNbsActEntitiesByActUid(actUid);
        if (res.isEmpty()) {
            return new ArrayList<>();
        }
        for (var item : res.get()) {
            NbsActEntityDto data = new NbsActEntityDto(item);
            lst.add(data);
        }
        return lst;
    }
    @SuppressWarnings("java:S3776")
    private Map<Object, Object> getPamAnswerDTMaps(Long publicHealthCaseUID) throws DataProcessingException {
        ArrayList<Object> PamAnswerDTCollection;
        Map<Object, Object> nbsReturnAnswerMap = new HashMap<>();
        Map<Object, Object> nbsAnswerMap = new HashMap<>();
        Map<Object, Object> nbsRepeatingAnswerMap = new HashMap<>();
        try
        {

            var pamAnsCol = nbsCaseAnswerRepository.getNbsCaseAnswerByActUid(publicHealthCaseUID);
            if (pamAnsCol.isEmpty()) {
                return new HashMap<>();
            }

            PamAnswerDTCollection = new ArrayList(pamAnsCol.get());

            Iterator<Object> it = PamAnswerDTCollection.iterator();
            Long nbsQuestionUid = 0L;
            Collection<Object> coll = new ArrayList<>();
            while (it.hasNext())
            {
                NbsCaseAnswerDto pamAnsDT = new NbsCaseAnswerDto ((NbsCaseAnswer) it.next());

                if (pamAnsDT.getNbsQuestionUid() != null
                        && nbsQuestionUid != 0
                        && pamAnsDT.getNbsQuestionUid().longValue() != nbsQuestionUid
                        .longValue() && coll.size() > 0) {
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
                    if (coll.size() > 0)
                    {
                        nbsAnswerMap.put(nbsQuestionUid, coll);
                        coll = new ArrayList<>();
                    }
                    coll.add(pamAnsDT);
                }
                else
                {
                    if (coll.size() > 0)
                    {
                        nbsAnswerMap.put(nbsQuestionUid, coll);
                    }
                    nbsAnswerMap.put(pamAnsDT.getNbsQuestionUid(), pamAnsDT);
                    coll = new ArrayList<>();
                }
                nbsQuestionUid = pamAnsDT.getNbsQuestionUid();
                if (!it.hasNext() && coll.size() > 0)
                {
                    nbsAnswerMap.put(pamAnsDT.getNbsQuestionUid(), coll);
                }
            }
        }
        catch (Exception ex)
        {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        nbsReturnAnswerMap.put(NEDSSConstant.NON_REPEATING_QUESTION, nbsAnswerMap);
        nbsReturnAnswerMap.put(NEDSSConstant.REPEATING_QUESTION, nbsRepeatingAnswerMap);

        return nbsReturnAnswerMap;
    }

}
