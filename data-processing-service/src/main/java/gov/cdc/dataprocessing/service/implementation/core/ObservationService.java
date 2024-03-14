package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.constant.elr.*;
import gov.cdc.dataprocessing.constant.enums.DataProcessingMapKey;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NNDActivityLogDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.service.interfaces.*;
import gov.cdc.dataprocessing.service.interfaces.core.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.core.IUidService;
import gov.cdc.dataprocessing.service.interfaces.matching.IObservationMatchingService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PersonUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;


@Service
@Slf4j
public class ObservationService implements IObservationService {

    private static final Logger logger = LoggerFactory.getLogger(ObservationService.class);

    private final INNDActivityLogService nndActivityLogService;
    private final IMessageLogService messageLogService;

    private final ObservationRepositoryUtil observationRepositoryUtil;

    private final INotificationService notificationService;

    private final IMaterialService materialService;

    private final PatientRepositoryUtil patientRepositoryUtil;

    private final IRoleService roleService;

    private final IActRelationshipService actRelationshipService;

    private final IEdxDocumentService edxDocumentService;
    private final IAnswerService answerService;

    private final IParticipationService participationService;

    private final ObservationRepository observationRepository;
    private final PersonRepository personRepository;
    private final IJurisdictionService jurisdictionService;

    private final OrganizationRepositoryUtil organizationRepositoryUtil;

    private final IObservationCodeService observationCodeService;

    private final ObservationUtil observationUtil;
    private final PersonUtil personUtil;

    private final IProgramAreaService programAreaService;

    private final PrepareAssocModelHelper prepareAssocModelHelper;

    private final IUidService uidService;


    public ObservationService(INNDActivityLogService nndActivityLogService,
                              IMessageLogService messageLogService,
                              ObservationRepositoryUtil observationRepositoryUtil,
                              INotificationService notificationService,
                              IMaterialService materialService,
                              PatientRepositoryUtil patientRepositoryUtil,
                              IRoleService roleService,
                              IActRelationshipService actRelationshipService,
                              IEdxDocumentService edxDocumentService,
                              IAnswerService answerService,
                              IParticipationService participationService,
                              ObservationRepository observationRepository,
                              PersonRepository personRepository,
                              IJurisdictionService jurisdictionService,
                              OrganizationRepositoryUtil organizationRepositoryUtil,
                              IObservationCodeService observationCodeService,
                              ObservationUtil observationUtil,
                              PersonUtil personUtil,
                              IProgramAreaService programAreaService,
                              PrepareAssocModelHelper prepareAssocModelHelper,
                              IUidService uidService) {

        this.nndActivityLogService = nndActivityLogService;
        this.messageLogService = messageLogService;
        this.observationRepositoryUtil = observationRepositoryUtil;
        this.notificationService = notificationService;
        this.materialService = materialService;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.roleService = roleService;
        this.actRelationshipService = actRelationshipService;
        this.edxDocumentService = edxDocumentService;
        this.answerService = answerService;
        this.participationService = participationService;
        this.observationRepository = observationRepository;
        this.personRepository = personRepository;
        this.jurisdictionService = jurisdictionService;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
        this.observationCodeService = observationCodeService;
        this.observationUtil = observationUtil;
        this.personUtil = personUtil;
        this.programAreaService = programAreaService;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.uidService = uidService;
    }

    /**
     * Getting Observation Dto into LabResult Container
     * */
    public LabResultProxyContainer getObservationToLabResultContainer(Long observationUid) throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO;
        if (observationUid == null) {
            String errorMessage = "HL7CommonLabUtil.getLabResultToProxy observationUid is null";
            logger.error(errorMessage);
            throw new DataProcessingException(errorMessage);
        }
        labResultProxyVO = loadingObservationToLabResultContainer(observationUid, true);
        return labResultProxyVO;
    }

    @Transactional
    public ObservationDT sendLabResultToProxy(LabResultProxyContainer labResultProxyContainer) throws DataProcessingException {
        if (labResultProxyContainer == null) {
            throw new DataProcessingException("Lab Result Container Is Null");
        }
        ObservationDT obsDT;
        labResultProxyContainer.setItNew(true);
        labResultProxyContainer.setItDirty(false);
        Map<Object, Object> returnMap = setLabResultProxy(labResultProxyContainer);
        obsDT = (ObservationDT)returnMap.get(NEDSSConstant.SETLAB_RETURN_OBSDT);
        return obsDT;

    }



    /**
     * Loading Existing Either Observation or Intervention
     * was: getActVO
     * */
    private AbstractVO getAbstractObjectForObservationOrIntervention(String actType, Long anUid) throws DataProcessingException
    {
        AbstractVO obj = null;
        if (anUid != null)
        {
            if (actType.equalsIgnoreCase(NEDSSConstant.INTERVENTION_CLASS_CODE))
            {
                //TODO: LOAD INTERVENTION
               // obj = interventionRootDAOImpl.loadObject(anUid.longValue());
            }
            else if (actType.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE))
            {
                obj = observationRepositoryUtil.loadObject(anUid);
            }
        }
        return obj;
    }


    /**
     * Retrieving assoc data from Participation: PERSON, ORG, MATERIAL, ROLE
     * */
    private Map<DataProcessingMapKey, Object>  retrieveEntityFromParticipationForContainer(Collection<ParticipationDT> partColl) throws DataProcessingException {
        Map<DataProcessingMapKey, Object> entityHolder = new HashMap<>();

        //Retrieve associated persons
        Map<DataProcessingMapKey, Object> assocMapper = retrievePersonAndRoleFromParticipation(partColl);

        if (assocMapper.containsKey(DataProcessingMapKey.PERSON)) {
            //allEntityHolder.add(this.RETRIEVED_PERSONS_FOR_PROXY, assocMapper.get(DataProcessingMapKey.ROLE));
            entityHolder.put(DataProcessingMapKey.PERSON,  assocMapper.get(DataProcessingMapKey.PERSON));
        }

        //Retrieve associated organizations
        //allEntityHolder.add(this.RETRIEVED_ORGANIZATIONS_FOR_PROXY, retrieveOrganizationVOsForProxyVO(partColl));
        entityHolder.put(DataProcessingMapKey.ORGANIZATION,  retrieveOrganizationFromParticipation(partColl));


        //Retrieve associated materials
        //allEntityHolder.add(this.RETRIEVED_MATERIALS_FOR_PROXY, retrieveMaterialVOsForProxyVO(partColl));
        entityHolder.put(DataProcessingMapKey.MATERIAL,  retrieveMaterialFromParticipation(partColl));


        if (assocMapper.containsKey(DataProcessingMapKey.ROLE)) {
            //allEntityHolder.add(this.RETRIEVED_PATIENT_ROLES, assocMapper.get(DataProcessingMapKey.PERSON));
            entityHolder.put(DataProcessingMapKey.ROLE, assocMapper.get(DataProcessingMapKey.ROLE));
        }

        return entityHolder;
    }

    /**
     * Was: retrieveOrganizationVOsForProxyVO
     * */
    private Collection<Object>  retrieveOrganizationFromParticipation(Collection<ParticipationDT> partColl) throws DataProcessingException {
        Collection<Object>  theOrganizationVOCollection  = null;
        for (ParticipationDT partDT : partColl) {
            if (partDT == null) {
                continue;
            }

            String subjectClassCd = partDT.getSubjectClassCd();
            String recordStatusCd = partDT.getRecordStatusCd();

            if (subjectClassCd != null
                && subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR102_SUB_CD)
                && recordStatusCd != null
                && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)
            ) {
                Long organizationUid = partDT.getSubjectEntityUid();
                if (theOrganizationVOCollection == null) {
                    theOrganizationVOCollection = new ArrayList<>();
                }
                var orgContainer = organizationRepositoryUtil.loadObject(organizationUid, partDT.getActUid());
                theOrganizationVOCollection.add(orgContainer);
            }
        }
        return theOrganizationVOCollection;
    }

    /**
     * was: retrieveMaterialVOsForProxyVO
     * */
    private Collection<Object>  retrieveMaterialFromParticipation(Collection<ParticipationDT> partColl)
    {
        Collection<Object>  theMaterialVOCollection  = null;
        for (ParticipationDT partDT : partColl) {
            if (partDT == null) {
                continue;
            }

            String subjectClassCd = partDT.getSubjectClassCd();
            String recordStatusCd = partDT.getRecordStatusCd();

            if (subjectClassCd != null
                && subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR104_SUB_CD)
                && recordStatusCd != null
                && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)
            ) {
                Long materialUid = partDT.getSubjectEntityUid();
                if (theMaterialVOCollection == null) {
                    theMaterialVOCollection = new ArrayList<>();
                }
                MaterialVO materialVO = materialService.loadMaterialObject(materialUid);
                theMaterialVOCollection.add(materialVO);
            }
        }
        return theMaterialVOCollection;
    }

    /**
     * Mapping Person and Role into Object Array
     * Values from Participation
     * Was: retrievePersonVOsForProxyVO
     * */
    private Map<DataProcessingMapKey, Object> retrievePersonAndRoleFromParticipation(Collection<ParticipationDT> partColl)
    {
        Map<DataProcessingMapKey, Object> mapper = new HashMap<>();
        Collection<PersonContainer>  thePersonVOCollection  = new ArrayList<> ();
        Collection<RoleDto>  patientRollCollection  = new ArrayList<> ();
        for (ParticipationDT partDT : partColl) {
            if (partDT == null) {
                continue;
            }

            String subjectClassCd = partDT.getSubjectClassCd();
            String recordStatusCd = partDT.getRecordStatusCd();
            String typeCd = partDT.getTypeCd();

            //If person...
            if (subjectClassCd != null
                && subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR110_SUB_CD)
                && recordStatusCd != null
                && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)
            ) {
                PersonContainer vo = patientRepositoryUtil.loadPerson(partDT.getSubjectEntityUid());
                thePersonVOCollection.add(vo);

                //If hit, we will get list of person who is associated with the scoped id
                if (typeCd != null && typeCd.equalsIgnoreCase(NEDSSConstant.PAR110_TYP_CD)) {
                    if (vo.getTheRoleDtoCollection().size() > 0) {
                        patientRollCollection.addAll(vo.getTheRoleDtoCollection());
                    }
                    Collection<PersonContainer> scopedPersons = retrieveScopedPersons(vo.getThePersonDto().getPersonUid());
                    if (scopedPersons != null && scopedPersons.size() > 0) {
                        for (var person : scopedPersons) {
                            if (person.getTheRoleDtoCollection() != null && person.getTheRoleDtoCollection().size() > 0) {
                                patientRollCollection.addAll(person.getTheRoleDtoCollection());
                            }
                            thePersonVOCollection.add(person);
                        }
                    }
                }
            }
        }
        mapper.put(DataProcessingMapKey.ROLE, patientRollCollection);
        mapper.put(DataProcessingMapKey.PERSON, thePersonVOCollection);

        return mapper;
    }

    /**
     * Getting List of person given Entity Uid for Role
     * */
    private Collection<PersonContainer>  retrieveScopedPersons(Long scopingUid)
    {
        Collection<RoleDto>  roleDTColl = roleService.findRoleScopedToPatient(scopingUid);
        Collection<PersonContainer>  scopedPersons = null;

        for (RoleDto roleDT : roleDTColl) {
            if (roleDT == null) {
                continue;
            }
            //In this case the subjectEntityUid is not the patient
            Long scopingEntityUid = roleDT.getSubjectEntityUid();

            if (scopedPersons == null) {
                scopedPersons = new ArrayList<>();
            }
            if (scopingEntityUid != null) {
                scopedPersons.add(patientRepositoryUtil.loadPerson(scopingEntityUid));
            }
        }
        return scopedPersons;
    }

    /**
     * was: retrieveActForProxyVO
     * */
    private Map<DataProcessingMapKey, Object> retrieveActForLabResultContainer(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException {
        Map<DataProcessingMapKey, Object> mapper = new HashMap<>();

        //Retrieve associated interventions
        mapper.put(DataProcessingMapKey.INTERVENTION, retrieveInterventionFromActRelationship(actRelColl));

        //Retrieve associated observations and performing labs of any resulted tests
        Map<DataProcessingMapKey, Object> obs_org = retrieveObservationFromActRelationship(actRelColl);
        mapper.put(DataProcessingMapKey.OBSERVATION, obs_org.get(DataProcessingMapKey.OBSERVATION));
        mapper.put(DataProcessingMapKey.ORGANIZATION, obs_org.get(DataProcessingMapKey.ORGANIZATION));


        return mapper;
    }

    /**
     * Retrieving Observation and the assoc Organization
     * was: retrieveObservationVOsForProxyVO
     * */
    private Map<DataProcessingMapKey, Object>  retrieveObservationFromActRelationship(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException
    {
        List<Object> obs_org = new ArrayList<Object> ();
        Map<DataProcessingMapKey, Object> mapper = new HashMap<>();
        Collection<ObservationVO>  theObservationVOCollection  = new ArrayList<> ();
        Collection<OrganizationVO>  performingLabColl = new ArrayList<> ();

        for (ActRelationshipDT actRelDT : actRelColl) {
            if (actRelDT == null) {
                continue;
            }

            String typeCd = actRelDT.getTypeCd();
            String sourceClassCd = actRelDT.getSourceClassCd();
            String targetClassCd = actRelDT.getTargetClassCd();
            String recordStatusCd = actRelDT.getRecordStatusCd();

            //If observation...
            if (sourceClassCd != null
                    && sourceClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && targetClassCd != null
                    && targetClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && recordStatusCd != null
                    && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)
            ) {
                Long observationUid = actRelDT.getSourceActUid();

                //If a processing decision observation
                if (typeCd != null && typeCd.equals(NEDSSConstant.ACT_TYPE_PROCESSING_DECISION)) {
                    ObservationVO processingDecObservationVO = (ObservationVO) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);
                    theObservationVOCollection.add(processingDecObservationVO);
                }
                //If a Comments observation
                if (typeCd != null && typeCd.equalsIgnoreCase("APND")) {
                    ObservationVO ordTestCommentVO = (ObservationVO) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);
                    theObservationVOCollection.add(ordTestCommentVO);
                    Collection<ActRelationshipDT> arColl = ordTestCommentVO.getTheActRelationshipDTCollection();
                    if (arColl != null) {
                        for (ActRelationshipDT ordTestDT : arColl) {
                            if (ordTestDT.getTypeCd().equals("COMP")) {
                                //add the resulted test to the collection
                                ObservationVO resTestVO = (ObservationVO) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, ordTestDT.getSourceActUid());

                                //BB - civil0012298 - Retrieve User Name to be displayed instead of ID
                                resTestVO.getTheObservationDT().setAddUserName(AuthUtil.authUser.getUserId());
                                theObservationVOCollection.add(resTestVO);

                            }
                        }

                    }
                }
                //If a Resulted Test observation
                else if (typeCd != null
                        && typeCd.equalsIgnoreCase(NEDSSConstant.ACT108_TYP_CD)
                ) {
                    ObservationVO rtObservationVO = (ObservationVO) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);
                    if (rtObservationVO == null) {
                        continue;
                    }
                    theObservationVOCollection.add(rtObservationVO); //The Resulted Test itself
                    //Retrieve the RT's lab
                    OrganizationVO rtPerformingLab = retrievePerformingLabAkaOrganizationFromParticipation(rtObservationVO.getTheParticipationDTCollection());
                    if (rtPerformingLab != null) {
                        performingLabColl.add(rtPerformingLab);
                    }

                    //Retrieves all reflex observations, including each ordered and its resulted
                    Collection<ObservationVO> reflexObsColl = retrieveReflexObservationsFromActRelationship(rtObservationVO.getTheActRelationshipDTCollection());
                    if (reflexObsColl == null || reflexObsColl.size() <= 0) {
                        continue;
                    }
                    theObservationVOCollection.addAll(reflexObsColl);
                }
            }
        }

        mapper.put(DataProcessingMapKey.OBSERVATION, theObservationVOCollection);
        mapper.put(DataProcessingMapKey.ORGANIZATION, performingLabColl);
        return mapper;
    }

    /**
     * was: retrieveReflexObservations
     */
    private Collection<ObservationVO>  retrieveReflexObservationsFromActRelationship(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException
    {
        Collection<ObservationVO>  reflexObsVOCollection  = null;

        for (ActRelationshipDT actRelDT : actRelColl) {
            if (actRelDT == null) {
                continue;
            }

            String typeCd = actRelDT.getTypeCd();
            String sourceClassCd = actRelDT.getSourceClassCd();
            String targetClassCd = actRelDT.getTargetClassCd();
            String recordStatusCd = actRelDT.getRecordStatusCd();

            //If reflex ordered test observation...
            if (typeCd != null
                && typeCd.equalsIgnoreCase(NEDSSConstant.ACT109_TYP_CD)
                && sourceClassCd != null
                && sourceClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                && targetClassCd != null
                && targetClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                && recordStatusCd != null
                && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)
            ) {
                Long observationUid = actRelDT.getSourceActUid();
                ObservationVO reflexObs = (ObservationVO) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);

                if (reflexObs == null) {
                    continue;
                } else {
                    if (reflexObsVOCollection == null) {
                        reflexObsVOCollection = new ArrayList<>();
                    }
                    reflexObsVOCollection.add(reflexObs);
                }

                //Retrieves its associated reflex resulted tests
                Collection<ObservationVO> reflexRTs = retrieveReflexRTsAkaObservationFromActRelationship(reflexObs.getTheActRelationshipDTCollection());
                if (reflexRTs == null
                    || reflexRTs.size() < 0
                ) {
                    continue;
                }
                reflexObsVOCollection.addAll(reflexRTs);
            }
        }
        return reflexObsVOCollection;
    }

    /**
     * Retrieves the Reflex Result Test
     * was: retrieveReflexRTs
     */
    private Collection<ObservationVO>  retrieveReflexRTsAkaObservationFromActRelationship(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException
    {
        Collection<ObservationVO>  reflexRTCollection  = null;

        for (ActRelationshipDT actRelDT : actRelColl) {
            if (actRelDT == null) {
                continue;
            }

            String typeCd = actRelDT.getTypeCd();
            String sourceClassCd = actRelDT.getSourceClassCd();
            String targetClassCd = actRelDT.getTargetClassCd();
            String recordStatusCd = actRelDT.getRecordStatusCd();

            //If reflex resulted test observation...
            if (typeCd != null
                && typeCd.equalsIgnoreCase(NEDSSConstant.ACT110_TYP_CD)
                && sourceClassCd != null
                && sourceClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                && targetClassCd != null
                && targetClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                && recordStatusCd != null
                && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)
            ) {
                Long observationUid = actRelDT.getSourceActUid();
                ObservationVO reflexObs = (ObservationVO) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);

                if (reflexObs == null) {
                    continue;
                }
                if (reflexRTCollection == null) {
                    reflexRTCollection = new ArrayList<>();
                }
                reflexRTCollection.add(reflexObs);
            }
        }
        return reflexRTCollection;
    }

    // LOAD the performing  lab
    /**
     * was: retrievePerformingLab
     * */
    private OrganizationVO retrievePerformingLabAkaOrganizationFromParticipation(Collection<ParticipationDT> partColl) throws DataProcessingException
    {
        OrganizationVO lab = null;

        for (ParticipationDT partDT : partColl) {
            if (partDT == null) {
                continue;
            }

            String typeCd = partDT.getTypeCd();
            String subjectClassCd = partDT.getSubjectClassCd();
            String actClassCd = partDT.getActClassCd();
            String recordStatusCd = partDT.getRecordStatusCd();

            //If performing lab...
            if (typeCd != null
                && typeCd.equals(NEDSSConstant.PAR122_TYP_CD)
                && subjectClassCd != null
                && subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR122_SUB_CD)
                && actClassCd != null
                && actClassCd.equals(NEDSSConstant.OBSERVATION_CLASS_CODE)
                && recordStatusCd != null
                && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)
            ) {
                Long organizationUid = partDT.getSubjectEntityUid();
                lab = organizationRepositoryUtil.loadObject(organizationUid, partDT.getActUid());
                break; //only one lab for each RT
            }
        }
        return lab;
    }

    /**
     * was: retrieveInterventionVOsForProxyVO
     * */
    private Collection<Object>  retrieveInterventionFromActRelationship(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException
    {
        Collection<Object>  theInterventionVOCollection  = null;

        for (ActRelationshipDT actRelDT : actRelColl) {
            if (actRelDT == null) {
                continue;
            }

            String sourceClassCd = actRelDT.getSourceClassCd();
            String targetClassCd = actRelDT.getTargetClassCd();
            String recordStatusCd = actRelDT.getRecordStatusCd();

            if (sourceClassCd != null
                && sourceClassCd.equalsIgnoreCase(NEDSSConstant.INTERVENTION_CLASS_CODE)
                && targetClassCd != null
                && targetClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                && recordStatusCd != null
                && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)
            ) {
                Long interventionUid = actRelDT.getSourceActUid();
                if (theInterventionVOCollection == null) {
                    theInterventionVOCollection = new ArrayList<>();
                }
                var intervention = getAbstractObjectForObservationOrIntervention(NEDSSConstant.INTERVENTION_CLASS_CODE, interventionUid);
                theInterventionVOCollection.add(intervention);
            }
        }
        return theInterventionVOCollection;
    }

    /**
     *  public LabResultProxyVO getLabResultProxyVO(Long observationId,  boolean isELR, NBSSecurityObj nbsSecurityObj)
     * */
    private LabResultProxyContainer loadingObservationToLabResultContainer(Long observationId,  boolean isELR) throws DataProcessingException {
        LabResultProxyContainer lrProxyVO =  new LabResultProxyContainer();

        // LOADING EXISTING Observation
        ObservationVO orderedTest = (ObservationVO) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationId);
        Collection<ParticipationDT>  partColl = orderedTest.getTheParticipationDTCollection();

        if (partColl != null && !partColl.isEmpty())
        {
            Map<DataProcessingMapKey, Object> allEntity = retrieveEntityFromParticipationForContainer(partColl);
            if (!allEntity.isEmpty())
            {
                lrProxyVO.setThePersonContainerCollection((Collection<PersonContainer>) allEntity.get(DataProcessingMapKey.PERSON));
                lrProxyVO.setTheOrganizationVOCollection((Collection<OrganizationVO>) allEntity.get(DataProcessingMapKey.ORGANIZATION));
                lrProxyVO.setTheMaterialVOCollection((Collection<MaterialVO>) allEntity.get(DataProcessingMapKey.MATERIAL));
                Collection<RoleDto> roleObjs = (Collection<RoleDto>)  allEntity.get(DataProcessingMapKey.ROLE);
                Collection<RoleDto> roleDtoCollection;
                roleDtoCollection = Objects.requireNonNullElseGet(roleObjs, ArrayList::new);
                lrProxyVO.setTheRoleDtoCollection(roleDtoCollection);
            }
        }

        Collection<ActRelationshipDT>  actRelColl = orderedTest.getTheActRelationshipDTCollection();

        if (actRelColl != null && !actRelColl.isEmpty())
        {
            Map<DataProcessingMapKey, Object> allAct = retrieveActForLabResultContainer(actRelColl);
            if (!allAct.isEmpty())
            {
                //Set intervention collection
                lrProxyVO.setTheInterventionVOCollection( (Collection<Object>) allAct.get(DataProcessingMapKey.INTERVENTION));

                //Set observation collection
                Collection<ObservationVO> obsColl = (Collection<ObservationVO>) allAct.get(DataProcessingMapKey.OBSERVATION);
                if (obsColl == null)
                {
                    obsColl = new ArrayList<>();
                }

                //BB - civil0012298 - Retrieve User Name to b displayed instead of ID!
                if(!isELR) {
                    orderedTest.getTheObservationDT().setAddUserName(AuthUtil.authUser.getUserId());
                    orderedTest.getTheObservationDT().setLastChgUserName(AuthUtil.authUser.getUserId());
                }

                obsColl.add(orderedTest);
                lrProxyVO.setTheObservationVOCollection(obsColl);

                //Adds the performing lab(if any) to the organization cellection
                Collection<OrganizationVO>  labColl = (Collection<OrganizationVO>) allAct.get(DataProcessingMapKey.ORGANIZATION);
                if (labColl != null && labColl.size() > 0)
                {
                    lrProxyVO.getTheOrganizationVOCollection().addAll(labColl);
                }
            }
        }

        if (!isELR) {
            boolean exists = notificationService.checkForExistingNotification(lrProxyVO);
            lrProxyVO.setAssociatedNotificationInd(exists);


            Collection<ActRelationshipDT> col = actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(observationId, NEDSSConstant.LAB_REPORT);
            if (col != null && col.size() > 0)
            {
                lrProxyVO.setAssociatedInvInd(true);
            }


            // get EDX Document data
            Collection<EDXDocumentDT> documentList = edxDocumentService.selectEdxDocumentCollectionByActUid(observationId);
            if (documentList != null) {
                lrProxyVO.setEDXDocumentCollection(documentList);
            }

            // get the list of conditions associated with this Lab
            ArrayList<String> conditionList = observationCodeService.deriveTheConditionCodeList(lrProxyVO, orderedTest);
            if (conditionList != null && !conditionList.isEmpty()) {
                lrProxyVO.setTheConditionsList(conditionList);
            }
        }

        try {
            PageVO pageVO = answerService.getNbsAnswerAndAssociation(observationId);
            lrProxyVO.setPageVO(pageVO);
        } catch (Exception e) {
            logger.error("Exception while getting data from NBS Answer for Lab");
            e.printStackTrace();
        }

        return lrProxyVO;
    }

    private Map<Object, Object> setLabResultProxy(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {
            NNDActivityLogDT nndActivityLogDT = null;

            //saving LabResultProxyVO before updating auto resend notifications
            Map<Object, Object> returnVal = setLabResultProxyWithoutNotificationAutoResend(labResultProxyVO);


            nndActivityLogDT = updateLabResultWithAutoResendNotification(labResultProxyVO);


            //TODO: THIS ONE SEND OF EMAIL NOTIFICATION BASED ON THE FLAG IN ENV
            /*
            if(labResultProxyVO.isItNew() && propertyUtil.getEnableELRAlert()!=null && propertyUtil.getEnableELRAlert().equals(NEDSSConstant.TRUE))
            {
                try
                {
                    java.util.Date date = new java.util.Date();
                    long time1 = date.getTime();
                    logger.debug("time1 is :" + time1);
                    String LocalId= (String)returnVal.get(NEDSSConstant.SETLAB_RETURN_OBS_LOCAL);
                    alertLabsEmailMessage(labResultProxyVO,LocalId);
                    java.util.Date date2 = new java.util.Date();
                    long time2 = date2.getTime();
                    logger.debug("time2 is :" + time2);
                    logger.debug("Total alertfunctionality  time taken is:" + (time2-time1));
                }
                catch(Exception e){
                    logger.error("Alert message could not be captured" + e.getMessage());
                }
            }
            */

            if(labResultProxyVO.getMessageLogDCollection()!=null && labResultProxyVO.getMessageLogDCollection().size()>0){
                try {
                    messageLogService.saveMessageLog(labResultProxyVO.getMessageLogDCollection());
                } catch (Exception e) {
                    logger.error("Unable to store the Error message for = "+ labResultProxyVO.getMessageLogDCollection());
                }
            }
            return returnVal;


    }

    private NNDActivityLogDT updateLabResultWithAutoResendNotification(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {
        NNDActivityLogDT nndActivityLogDT = null;
        try
        {
            //update auto resend notifications
            if(labResultProxyVO.associatedNotificationInd)
            {
                updateAutoResendNotificationsAsync(labResultProxyVO);
            }
        }
        catch(Exception e)
        {
            nndActivityLogDT = new  NNDActivityLogDT();
            nndActivityLogDT.setErrorMessageTxt(e.toString());
            Collection<ObservationVO> observationCollection  = labResultProxyVO.getTheObservationVOCollection();
            ObservationVO obsVO = findObservationByCode(observationCollection, NEDSSConstant.LAB_REPORT);
            String localId = obsVO.getTheObservationDT().getLocalId();
            if (localId!=null)
            {
                nndActivityLogDT.setLocalId(localId);
            }
            else
                nndActivityLogDT.setLocalId("N/A");
            //catch & store auto resend notifications exceptions in NNDActivityLog table
            nndActivityLogService.saveNddActivityLog(nndActivityLogDT);
            logger.error("Exception occurred while calling nndMessageSenderHelper.updateAutoResendNotificationsAsync");
            e.printStackTrace();
        }

        return nndActivityLogDT;
    }

    private Map<Object, Object> setLabResultProxyWithoutNotificationAutoResend(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {
        //Before doing anything
        //TODO: Verify this check
        //checkMethodArgs(labResultProxyVO);

        //Set flag for type of processing
        boolean ELR_PROCESSING = false;

        // We need specific auth User for elr processing, but probably wont applicable for data processing
        if (AuthUtil.authUser != null || (AuthUtil.authUser != null && AuthUtil.authUser.getUserId().equals(NEDSSConstant.ELR_LOAD_USER_ACCOUNT)))
        {
            ELR_PROCESSING = true;
        }

        //TODO: Verify this check lab result perm
        //Check permission to proceed
        //checkPermissionToSetProxy(labResultProxyVO, securityObj, ELR_PROCESSING);

        //All well to proceed
        Map<Object, Object> returnVal = new HashMap<>();
        Long falseUid;
        Long realUid ;
        boolean valid = false;



            //Process PersonVOCollection  and adds the patient mpr uid to the return
            Long patientMprUid = personUtil.processLabPersonContainerCollection(
                    labResultProxyVO.getThePersonContainerCollection(),
                    false,
                    labResultProxyVO
            );
            if (patientMprUid != null)
            {
            {
                returnVal.put(NEDSSConstant.SETLAB_RETURN_MPR_UID, patientMprUid);
            }

            //ObservationVOCollection
            Map<Object, Object> obsResults;
            obsResults = processObservationContainerCollection(labResultProxyVO, ELR_PROCESSING);

            if (!obsResults.isEmpty())
            {
                returnVal.putAll(obsResults);
            }

            //TODO: EVALUATE THIS, this one try to find Patient UID, the code above is also doing this
            //For ELR update, mpr uid may be not available
            if(patientMprUid == null || patientMprUid.longValue() < 0)
            {
                /**
                 *       String aQuery = "SELECT subject_entity_uid FROM " +
                 *              DataTables.PARTICIPATION_TABLE + " WHERE subject_class_cd = ? AND type_cd = ? AND act_uid = ?";
                 *
                 * */
                patientMprUid = participationService.findPatientMprUidByObservationUid(
                        NEDSSConstant.PERSON,
                        NEDSSConstant.PAR110_TYP_CD,
                        (Long) obsResults.get(NEDSSConstant.SETLAB_RETURN_OBS_UID)
                );
                if(patientMprUid == null){
                    throw new DataProcessingException("Expected this observation to be associated with a patient, observation uid = " + obsResults.get(NEDSSConstant.SETLAB_RETURN_OBS_UID));
                }
                returnVal.put(NEDSSConstant.SETLAB_RETURN_MPR_UID, patientMprUid);
            }


            //Retrieve and return local ids for the patient and observation
            Long observationUid = (Long)obsResults.get(NEDSSConstant.SETLAB_RETURN_OBS_UID);
            returnVal.putAll(findLocalUidsFor(patientMprUid, observationUid));

            //OrganizationCollection
            OrganizationVO organizationVO;
            if (labResultProxyVO.getTheOrganizationVOCollection() != null)
            {
                for (OrganizationVO vo : labResultProxyVO.getTheOrganizationVOCollection()) {
                    organizationVO = vo;
                    OrganizationDT newOrganizationDT = null;

                    if (organizationVO.isItNew()) {
                        //TODO: EVALUATE THIS prepare method

                        newOrganizationDT = (OrganizationDT) prepareAssocModelHelper.prepareVO(
                                organizationVO.getTheOrganizationDT(), NBSBOLookup.ORGANIZATION,
                                NEDSSConstant.ORG_CR, "ORGANIZATION",
                                NEDSSConstant.BASE);
                        organizationVO.setTheOrganizationDT(newOrganizationDT);
                        falseUid = organizationVO.getTheOrganizationDT().getOrganizationUid();
                        logger.debug("false organizationUID: " + falseUid);

                        realUid = organizationRepositoryUtil.setOrganization(organizationVO, null);
                        if (falseUid.intValue() < 0) {
                            uidService.setFalseToNewForObservation(labResultProxyVO, falseUid, realUid);
                        }
                    } else if (organizationVO.isItDirty()) {
                        //TODO: EVALUATE
                        newOrganizationDT = (OrganizationDT) prepareAssocModelHelper.prepareVO(
                                organizationVO.getTheOrganizationDT(), NBSBOLookup.ORGANIZATION,
                                NEDSSConstant.ORG_EDIT, "ORGANIZATION",
                                NEDSSConstant.BASE);

                        organizationVO.setTheOrganizationDT(newOrganizationDT);
                        realUid = organizationRepositoryUtil.setOrganization(organizationVO, null);
                    }
                }
            }

            //MaterialCollection

            MaterialVO materialVO = null;
            if (labResultProxyVO.getTheMaterialVOCollection() != null)
            {
                for (MaterialVO vo : labResultProxyVO.getTheMaterialVOCollection()) {
                    materialVO = vo;
                    MaterialDT newMaterialDT = null;
                    logger.debug("materialUID: " + materialVO.getTheMaterialDT().getMaterialUid());

                    if (materialVO.isItNew()) {
                        //TODO: EVALUATE
                        newMaterialDT = (MaterialDT) prepareAssocModelHelper.prepareVO(materialVO.
                                        getTheMaterialDT(), NBSBOLookup.MATERIAL,
                                NEDSSConstant.MAT_MFG_CR, "MATERIAL",
                                NEDSSConstant.BASE);
                        materialVO.setTheMaterialDT(newMaterialDT);
                        falseUid = materialVO.getTheMaterialDT().getMaterialUid();
                        realUid = materialService.saveMaterial(materialVO);
                        if (falseUid.intValue() < 0) {
                            uidService.setFalseToNewForObservation(labResultProxyVO, falseUid, realUid);
                        }
                    } else if (materialVO.isItDirty()) {
                        //TODO: EVALUATE
                        newMaterialDT = (MaterialDT) prepareAssocModelHelper.prepareVO(materialVO.
                                        getTheMaterialDT(), NBSBOLookup.MATERIAL,
                                NEDSSConstant.MAT_MFG_EDIT, "MATERIAL",
                                NEDSSConstant.BASE);
                        materialVO.setTheMaterialDT(newMaterialDT);

                        realUid = materialService.saveMaterial(materialVO);
                        logger.debug("exisiting but updated material's UID: " + realUid);
                    }
                }
            }

            //ParticipationCollection

            if (labResultProxyVO.getTheParticipationDTCollection() != null)
            {
                logger.debug("Iniside participation Collection<Object>  Loop - Lab");
                for (ParticipationDT dt : labResultProxyVO.getTheParticipationDTCollection()) {

                    logger.debug("Inside loop size of participations: " + labResultProxyVO.getTheParticipationDTCollection().size());
                    ParticipationDT participationDT = dt;
                    try {
                        if (participationDT != null) {
                            if (participationDT.isItDelete()) {
                                participationService.saveParticipationHist(participationDT);
                            }
                            //TODO EVALUATE - use root obs uid for now
                            // participationDT.setActUid(observationUid);

                            participationService.saveParticipation(participationDT);


                            logger.debug("got the participationDT, the ACTUID is " +
                                    participationDT.getActUid());
                            logger.debug("got the participationDT, the subjectEntityUid is " +
                                    participationDT.getSubjectEntityUid());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new DataProcessingException(e.getMessage());
                    }
                }
            }


            //ActRelationship Collection

            if (labResultProxyVO.getTheActRelationshipDTCollection() != null)
            {
                logger.debug("Act relationship size: " + labResultProxyVO.getTheActRelationshipDTCollection().size());
                for (ActRelationshipDT actRelationshipDT : labResultProxyVO.getTheActRelationshipDTCollection()) {
                    try {
                        if (actRelationshipDT != null) {
                            //TODO: EVALUATE use Obs Root Uid for now
                            //actRelationshipDT.setTargetActUid(observationUid);
                            actRelationshipService.saveActRelationship(actRelationshipDT);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new DataProcessingException(e.getMessage());
                    }
                }
            }


            //TODO: EVALUATE - ROLE SERVICE
            //Processes roleDT collection

//            Collection<RoleDto>  roleDTColl = labResultProxyVO.getTheRoleDtoCollection();
//            if (roleDTColl != null && !roleDTColl.isEmpty())
//            {
//                roleService.storeRoleDTCollection(roleDTColl);
//            }



            //add LDF data
            /**
             * @TBD Release 6.0, Commented out as LDF will be planned out as new type of answers
            LDFHelper ldfHelper = LDFHelper.getInstance();
            ldfHelper.setLDFCollection(labResultProxyVO.getTheStateDefinedFieldDataDTCollection(), labResultProxyVO.getLdfUids(),
            NEDSSConstant.LABREPORT_LDF,null,observationUid,securityObj);
             */

            //EDX Document


            Collection<EDXDocumentDT> edxDocumentCollection = labResultProxyVO.getEDXDocumentCollection();
            ObservationDT rootDT = observationUtil.getRootObservationDto(labResultProxyVO);
            if (edxDocumentCollection != null && edxDocumentCollection.size() > 0) {
                if (rootDT.getElectronicInd() != null && rootDT.getElectronicInd().equals(NEDSSConstant.YES)) {
                    for (EDXDocumentDT eDXDocumentDt : edxDocumentCollection) {
                        if (eDXDocumentDt.getPayload() != null) {
                            String payload = eDXDocumentDt.getPayload();
                            int containerIndex = payload.indexOf("<Container");
                            eDXDocumentDt.setPayload(payload.substring(containerIndex));
                        }
                        if (eDXDocumentDt.isItNew()) {
                            eDXDocumentDt.setActUid(observationUid);
                        }
                        edxDocumentService.saveEdxDocument(eDXDocumentDt);
                    }
                }
            }
            rootDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
            rootDT.setObservationUid(observationUid);


            // Does not seem to hit this case
            if (labResultProxyVO.getPageVO() != null) {
                if(labResultProxyVO.isItDirty()) {
                    PageVO pageVO=(PageVO)labResultProxyVO.getPageVO();
                    //TODO: INSERTION
                    answerService.storePageAnswer(pageVO, rootDT);
                }
            else {
                    PageVO pageVO=(PageVO)labResultProxyVO.getPageVO();
                    //TODO: INSERTION
                    answerService.insertPageVO(pageVO, rootDT);
                }
            }

        }
        return returnVal;
    }

    private void updateAutoResendNotificationsAsync(AbstractVO v)
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
    private void updateAutoResendNotifications(AbstractVO vo) throws DataProcessingException
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
        /*
        if(
                vo instanceof InvestigationProxyVO
                || vo instanceof PamProxyVO
                ||  vo instanceof PageActProxyVO
                ||  vo instanceof SummaryReportProxyVO
        ){
            if(vo instanceof InvestigationProxyVO)
            {
                InvestigationProxyVO invVO = (InvestigationProxyVO)vo;
                phcDT = invVO.thePublicHealthCaseVO.getThePublicHealthCaseDT();
                notSumVOColl = invVO.getTheNotificationSummaryVOCollection();
            }
            else if(vo instanceof PamProxyVO)
            {
                PamProxyVO pamVO = (PamProxyVO)vo;
                phcDT = pamVO.getPublicHealthCaseVO().getThePublicHealthCaseDT();
                notSumVOColl = pamVO.getTheNotificationSummaryVOCollection();
            }
            else if (vo instanceof LabResultProxyVO)
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
            else if (vo instanceof SummaryReportProxyVO)
            {
                SummaryReportProxyVO summaryReportProxyVO = (SummaryReportProxyVO)vo;
                phcDT = summaryReportProxyVO.getPublicHealthCaseVO().getThePublicHealthCaseDT();
                notSumVOColl = summaryReportProxyVO.getTheNotificationVOCollection();
                Iterator<Object>  notSumIter =  notSumVOColl.iterator();
                while(notSumIter.hasNext()){
                    NotificationVO notVO = (NotificationVO)notSumIter.next();
                    Long notificationUid = notVO.getTheNotificationDT().getNotificationUid();
                    String phcCd = phcDT.getCd();
                    String phcClassCd = phcDT.getCaseClassCd();
                    String progAreaCd = phcDT.getProgAreaCd();
                    String jurisdictionCd = phcDT.getJurisdictionCd();
                    String sharedInd = phcDT.getSharedInd();
                    // retrieve the status change
                    boolean caseStatusChange = phcDT.isCaseStatusDirty();
                    updateNotification(true, notificationUid,phcCd,phcClassCd,progAreaCd,jurisdictionCd,sharedInd, caseStatusChange, nbsSecurityObj);
                }
            }
            if(
                    vo instanceof InvestigationProxyVO
                    || vo instanceof PamProxyVO
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
        else if(vo instanceof VaccinationProxyVO
                || vo instanceof MorbidityProxyVO)
        {
            NNDAutoResendDAOImpl nndAutoResendDAO = new NNDAutoResendDAOImpl();
            Collection<Object>  theNotificationCollection  = nndAutoResendDAO.getAutoResendNotificationSummaries(getActClassCd(vo), getTypeCd(vo), getRootUid(vo));
            Iterator<Object>  notIter = theNotificationCollection.iterator();
            while(notIter.hasNext()){
                NotificationSummaryVO notSumVO = (NotificationSummaryVO)notIter.next();
                updateNotification(false, notSumVO.getNotificationUid(),notSumVO.getCd(),notSumVO.getCaseClassCd(),notSumVO.getProgAreaCd(),notSumVO.getJurisdictionCd(),notSumVO.getSharedInd(), false, nbsSecurityObj);
            }
        }
        logger.info("finish NNDMessageSenderHelper.updateAutoResendNotifications--------------");

        */
    }

    private ObservationVO findObservationByCode(Collection<ObservationVO> coll, String strCode)
    {
        if (coll == null)
        {
            return null;
        }

        for (ObservationVO obsVO : coll) {
            ObservationDT obsDT = obsVO.getTheObservationDT();

            if (obsDT == null) {
                continue;
            }

            if (obsDT.getCd() == null) {
                continue;
            }
            if (obsDT.getCd().trim().equalsIgnoreCase(strCode.trim())) {
                return obsVO; // found it!
            }
        }

        // didn't find one
        return null;
    }


    /**
     * Processing observation collection
     * Original Name: processObservationVOCollection
     * */
    private Map<Object, Object> processObservationContainerCollection(AbstractVO proxyVO, boolean ELR_PROCESSING) throws DataProcessingException {
        if (proxyVO instanceof LabResultProxyContainer)
        {
            return processLabReportObsContainerCollection( (LabResultProxyContainer) proxyVO, ELR_PROCESSING);
        }

        //TODO: Morbidity is from a different flow
        //If coming from morbidity, processing this way
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                return processMorbObsVOCollection( (MorbidityProxyVO) proxyVO,
//                        securityObj);
//            }

        //If not above, abort the operation
        else
        {
            throw new DataProcessingException("Expected a valid observation proxy vo, it is: " + proxyVO.getClass().getName());
        }

    }

    /**
     * Original Name: processLabReportObsVOCollection
     * */
    private Map<Object, Object> processLabReportObsContainerCollection(LabResultProxyContainer labResultProxyVO, boolean ELR_PROCESSING) throws DataProcessingException {
        Collection<ObservationVO>obsContainerCollection = labResultProxyVO.getTheObservationVOCollection();
        ObservationVO observationVO;
        Map<Object, Object> returnObsVal = new HashMap<>();
        boolean isMannualLab = false;

        //Find out if it is mannual lab
        String electronicInd = observationUtil.getRootObservationDto(labResultProxyVO).getElectronicInd();
        if(electronicInd != null && !electronicInd.equals(NEDSSConstant.YES))
        {
            isMannualLab = true;
        }

        if (obsContainerCollection != null && !obsContainerCollection.isEmpty())
        {
            for (ObservationVO item : obsContainerCollection) {
                observationVO = item;
                if (observationVO == null) {
                    continue;
                }

                //For ordered test and resulted tests
                ObservationDT currentDT = observationVO.getTheObservationDT();
                String obsDomainCdSt1 = currentDT.getObsDomainCdSt1();
                boolean isOrderedTest = (obsDomainCdSt1 != null
                        && obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD))
                        && currentDT.getCd().equalsIgnoreCase("LAB112");
                boolean isResultedTest = obsDomainCdSt1 != null
                        && obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);

                // NOTE: data toward DP will never be manual lab
                if (isMannualLab) {
                    /**
                    // Removed for Rel 1.1.3 - as we are not doing a reverse translation for ORdered test and Resulted Test
                    if (isOrderedTest || isResultedTest) {
                        //Retrieve lab test code

                        //Do loinc and snomed lookups for oredered and resulted tests
                        observationVO = srteCodeObsService.labLoincSnomedLookup(observationVO, labResultProxyVO.getLabClia());
                    }
                    logger.debug("observationUID: " + observationVO.getTheObservationDT().getObservationUid());
                     **/
                }
            }
        }

        //Process the ordered test further
        returnObsVal = processLabReportOrderTest(labResultProxyVO, ELR_PROCESSING);

        //Then, persist the observations
        //TODO: INSERTION
        Long observationUid = storeObservationVOCollection(labResultProxyVO);

        //Return the order test uid
        if (observationUid != null)
        {
            returnObsVal.put(NEDSSConstant.SETLAB_RETURN_OBS_UID, observationUid);
        }
        return returnObsVal;

    }

    private Map<Object, Object> processLabReportOrderTest(LabResultProxyContainer labResultProxyVO, boolean isELR) throws DataProcessingException {
            //Retrieve the ordered test
            ObservationVO orderTest = observationUtil.getRootObservationContainer(labResultProxyVO);

            //Overrides rptToStateTime to current date/time for external user
            if (AuthUtil.authUser.getUserType() != null && AuthUtil.authUser.getUserType().equalsIgnoreCase(NEDSSConstant.SEC_USERTYPE_EXTERNAL))
            {
                orderTest.getTheObservationDT().setRptToStateTime(getCurrentTimeStamp());
            }

            //Assign program area cd if necessary, and return any errors to the client
            Map<Object, Object> returnErrors = new HashMap<>();
            String paCd = orderTest.getTheObservationDT().getProgAreaCd();
            if (paCd != null && paCd.equalsIgnoreCase(ProgramAreaJurisdiction.ANY_PROGRAM_AREA))
            {
                String paError = programAreaService.deriveProgramAreaCd(labResultProxyVO, orderTest);
                if (paError != null)
                {
                    returnErrors.put(NEDSSConstant.SETLAB_RETURN_PROGRAM_AREA_ERRORS, paError);
                }
            }

            //Assign jurisdiction cd if necessary
            String jurisdictionCd = orderTest.getTheObservationDT().getJurisdictionCd();
            if (jurisdictionCd != null &&
                    (jurisdictionCd.equalsIgnoreCase(ProgramAreaJurisdiction.ANY_JURISDICTION)
                    || jurisdictionCd.equalsIgnoreCase(ProgramAreaJurisdiction.JURISDICTION_NONE)
                    )
            )
            {
                String jurisdictionError = jurisdictionService.deriveJurisdictionCd(labResultProxyVO, orderTest.getTheObservationDT());
                if (jurisdictionCd != null)
                {
                    returnErrors.put(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS, jurisdictionCd);
                }
            }

            //Manipulate jurisdiction for preparing vo
            jurisdictionCd = orderTest.getTheObservationDT().getJurisdictionCd();
            if(jurisdictionCd != null
                && (jurisdictionCd.trim().equals("")
                    || jurisdictionCd.equals("ANY")
                    || jurisdictionCd.equals("NONE")
                )
            )
            {
                orderTest.getTheObservationDT().setJurisdictionCd(null);
            }

            //Do observation object state transition accordingly
            performOrderTestStateTransition(labResultProxyVO, orderTest, isELR);

            return returnErrors;

    }

    private Map<Object, Object> findLocalUidsFor(Long personMprUid, Long observationUid) throws DataProcessingException {
        Map<Object, Object> localIds = null;

        try
        {
            //Find observation local id
            if(localIds == null) localIds = new HashMap<Object, Object> ();
            var resObs = observationRepository.findById(observationUid);
            ObservationDT obsDT = new ObservationDT();
            if (resObs.isPresent()) {
                obsDT = new ObservationDT(resObs.get());
            }
            localIds.put(NEDSSConstant.SETLAB_RETURN_OBS_LOCAL, obsDT.getLocalId());
            localIds.put(NEDSSConstant.SETLAB_RETURN_OBSDT, obsDT);
            //Find mpr local id
            var resPat = personRepository.findById(personMprUid);
            if (resPat.isPresent()) {
                localIds.put(NEDSSConstant.SETLAB_RETURN_MPR_LOCAL, resPat.get().getLocalId());
            }
        }
        catch (Exception ex)
        {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return localIds;
    }

    private void performOrderTestStateTransition(LabResultProxyContainer labResultProxyVO, ObservationVO orderTest, boolean isELR) throws DataProcessingException
    {
        String businessTriggerCd = null;
        ObservationDT newObservationDT;
        if (labResultProxyVO.isItNew() && orderTest.getTheObservationDT().getProcessingDecisionCd()!=null && !orderTest.getTheObservationDT().getProcessingDecisionCd().trim().equals(""))
        {
            businessTriggerCd = NEDSSConstant.OBS_LAB_CR_MR;
        }
        else if (labResultProxyVO.isItNew())
        {
            businessTriggerCd = NEDSSConstant.OBS_LAB_CR;
        }
        else if (labResultProxyVO.isItDirty())
        {
            if (isELR)
            {
                businessTriggerCd = NEDSSConstant.OBS_LAB_CORRECT;
            }
            else
            {
                businessTriggerCd = NEDSSConstant.OBS_LAB_EDIT;
            }
        }
//            TODO: EVALUATE
         newObservationDT = (ObservationDT) prepareAssocModelHelper.prepareVO(
                orderTest.getTheObservationDT(), NBSBOLookup.OBSERVATIONLABREPORT,
                businessTriggerCd, "OBSERVATION", NEDSSConstant.BASE);
          orderTest.setTheObservationDT(newObservationDT);


    }

    private Long storeObservationVOCollection(AbstractVO proxyVO) throws DataProcessingException {
        try {
            //Iterates the observation collection and process each observation vo
            Collection<ObservationVO>  obsVOColl = null;
            boolean isLabResultProxyVO = false;
            if (proxyVO instanceof LabResultProxyContainer)
            {
                obsVOColl = ( (LabResultProxyContainer) proxyVO).getTheObservationVOCollection();
                isLabResultProxyVO = true;
            }

            //TODO: MORBIDITY
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                obsVOColl = ( (MorbidityProxyVO) proxyVO).getTheObservationVOCollection();
//            }

            ObservationVO observationVO = null;
            Long returnObsVal = null;

            if (obsVOColl != null && obsVOColl.size() > 0)
            {
                for (ObservationVO item : obsVOColl) {
                    observationVO = item;

                    if (observationVO == null) {
                        continue;
                    }

                    //If lab report's order test, set a flag
                    boolean isRootObs = false;

                    String obsDomainCdSt1 = observationVO.getTheObservationDT().
                            getObsDomainCdSt1();
                    if (isLabResultProxyVO && obsDomainCdSt1 != null &&
                            obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD)) {
                        isRootObs = true;
                    }

                    //If a root morbidity, set a flag so to return the observation uid
                    String ctrlCdDisplayForm = observationVO.getTheObservationDT().
                            getCtrlCdDisplayForm();
                    if (ctrlCdDisplayForm != null &&
                            ctrlCdDisplayForm.equalsIgnoreCase(NEDSSConstant.MOB_CTRLCD_DISPLAY)) {
                        isRootObs = true;
                    }

                    //TODO INSERTION
                    //Persist the observation vo
                    Long observationUid = observationRepositoryUtil.saveObservation(observationVO);

                    //Update associations with real uid if new
                    if (observationVO.isItNew()) {
                        Long falseUid = observationVO.getTheObservationDT().getObservationUid();
                        if (falseUid.intValue() < 0) {
                            uidService.setFalseToNewForObservation(proxyVO, falseUid, observationUid);
                        }
                    }


                    //Return the order test uid
                    if (observationUid != null && isRootObs) {
                        returnObsVal = observationUid;
                    }
                } //end of for loop
            } //end of main if
            return returnObsVal;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


}



