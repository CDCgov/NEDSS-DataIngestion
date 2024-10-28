package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.elr.ProgramAreaJurisdiction;
import gov.cdc.dataprocessing.constant.enums.DataProcessingMapKey;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.service.interfaces.act.IActRelationshipService;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.log.IMessageLogService;
import gov.cdc.dataprocessing.service.interfaces.log.INNDActivityLogService;
import gov.cdc.dataprocessing.service.interfaces.material.IMaterialService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IEdxDocumentService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationCodeService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.paticipation.IParticipationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.role.IRoleService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PersonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;


@Service
@Slf4j
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
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

    private final IInvestigationService investigationService;


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
                              IUidService uidService, IInvestigationService investigationService) {

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
        this.investigationService = investigationService;
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

    /**
     * Origin: sendLabResultToProxy
     * */
    @Transactional
    public ObservationDto processingLabResultContainer(LabResultProxyContainer labResultProxyContainer) throws DataProcessingException {
        if (labResultProxyContainer == null) {
            throw new DataProcessingException("Lab Result Container Is Null");
        }
        ObservationDto obsDT;
        labResultProxyContainer.setItNew(true);
        labResultProxyContainer.setItDirty(false);
        Map<Object, Object> returnMap = setLabResultProxy(labResultProxyContainer);
        obsDT = (ObservationDto)returnMap.get(NEDSSConstant.SETLAB_RETURN_OBSDT);
        return obsDT;

    }


    @Transactional
    public boolean processObservation(Long observationUid) throws DataProcessingException {
        return processObservationWithProcessingDecision(observationUid,
                null, null);

    }

    public void setLabInvAssociation(Long labUid, Long investigationUid) throws DataProcessingException {
        LabReportSummaryContainer labReportSummaryVO = new LabReportSummaryContainer();

        try {

            labReportSummaryVO.setTouched(true);
            labReportSummaryVO.setAssociated(true);
            labReportSummaryVO.setObservationUid(labUid);
            labReportSummaryVO.setActivityFromTime(new Timestamp(new java.util.Date().getTime()));

            Collection<LabReportSummaryContainer> labReportSummaryVOColl = new ArrayList<>();
            labReportSummaryVOColl.add(labReportSummaryVO);

            setObservationAssociations(investigationUid, labReportSummaryVOColl);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    /**
     * Loading Existing Either Observation or Intervention
     * was: getActVO
     * */
    private BaseContainer getAbstractObjectForObservationOrIntervention(String actType, Long anUid) throws DataProcessingException
    {
        BaseContainer obj = null;
        if (anUid != null)
        {
            if (actType.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE))
            {
                obj = observationRepositoryUtil.loadObject(anUid);
            }
        }
        return obj;
    }


    /**
     * Retrieving assoc data from Participation: PERSON, ORG, MATERIAL, ROLE
     * */
    private Map<DataProcessingMapKey, Object>  retrieveEntityFromParticipationForContainer(Collection<ParticipationDto> partColl) throws DataProcessingException {
        Map<DataProcessingMapKey, Object> entityHolder = new HashMap<>();

        //Retrieve associated persons
        Map<DataProcessingMapKey, Object> assocMapper = retrievePersonAndRoleFromParticipation(partColl);

        if (assocMapper.containsKey(DataProcessingMapKey.PERSON)) {
            entityHolder.put(DataProcessingMapKey.PERSON,  assocMapper.get(DataProcessingMapKey.PERSON));
        }

        //Retrieve associated organizations
        entityHolder.put(DataProcessingMapKey.ORGANIZATION,  retrieveOrganizationFromParticipation(partColl));


        //Retrieve associated materials
        entityHolder.put(DataProcessingMapKey.MATERIAL,  retrieveMaterialFromParticipation(partColl));


        if (assocMapper.containsKey(DataProcessingMapKey.ROLE)) {
            entityHolder.put(DataProcessingMapKey.ROLE, assocMapper.get(DataProcessingMapKey.ROLE));
        }

        return entityHolder;
    }

    /**
     * Was: retrieveOrganizationVOsForProxyVO
     * */
    Collection<Object>  retrieveOrganizationFromParticipation(Collection<ParticipationDto> partColl) throws DataProcessingException {
        Collection<Object>  theOrganizationVOCollection  = null;
        for (ParticipationDto partDT : partColl) {
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
    protected Collection<Object>  retrieveMaterialFromParticipation(Collection<ParticipationDto> partColl)
    {
        Collection<Object>  theMaterialVOCollection  = null;
        for (ParticipationDto partDT : partColl) {
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
                MaterialContainer materialContainer = materialService.loadMaterialObject(materialUid);
                theMaterialVOCollection.add(materialContainer);
            }
        }
        return theMaterialVOCollection;
    }

    /**
     * Mapping Person and Role into Object Array
     * Values from Participation
     * Was: retrievePersonVOsForProxyVO
     * */
    @SuppressWarnings("java:S3776")
    protected Map<DataProcessingMapKey, Object> retrievePersonAndRoleFromParticipation(Collection<ParticipationDto> partColl)
    {
        Map<DataProcessingMapKey, Object> mapper = new HashMap<>();
        Collection<PersonContainer>  thePersonVOCollection  = new ArrayList<> ();
        Collection<RoleDto>  patientRollCollection  = new ArrayList<> ();
        for (ParticipationDto partDT : partColl) {
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
                    if (!vo.getTheRoleDtoCollection().isEmpty()) {
                        patientRollCollection.addAll(vo.getTheRoleDtoCollection());
                    }
                    Collection<PersonContainer> scopedPersons = retrieveScopedPersons(vo.getThePersonDto().getPersonUid());
                    if (scopedPersons != null && scopedPersons.size() > 0) {
                        for (var person : scopedPersons) {
                            if (person.getTheRoleDtoCollection() != null && !person.getTheRoleDtoCollection().isEmpty()) {
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
    protected Collection<PersonContainer>  retrieveScopedPersons(Long scopingUid)
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
    Map<DataProcessingMapKey, Object> retrieveActForLabResultContainer(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException {
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
    @SuppressWarnings("java:S3776")
    private Map<DataProcessingMapKey, Object>  retrieveObservationFromActRelationship(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException
    {
        Map<DataProcessingMapKey, Object> mapper = new HashMap<>();
        Collection<ObservationContainer> theObservationContainerCollection = new ArrayList<> ();
        Collection<OrganizationContainer>  performingLabColl = new ArrayList<> ();

        for (ActRelationshipDto actRelDT : actRelColl) {
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
                    ObservationContainer processingDecObservationContainer = (ObservationContainer) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);
                    theObservationContainerCollection.add(processingDecObservationContainer);
                }
                //If a Comments observation
                if (typeCd != null && typeCd.equalsIgnoreCase("APND")) {
                    ObservationContainer ordTestCommentVO = (ObservationContainer) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);
                    theObservationContainerCollection.add(ordTestCommentVO);
                    Collection<ActRelationshipDto> arColl = ordTestCommentVO.getTheActRelationshipDtoCollection(); //NOSONAR
                    if (arColl != null) {
                        for (ActRelationshipDto ordTestDT : arColl) {
                            if (ordTestDT.getTypeCd().equals("COMP")) {
                                //add the resulted test to the collection
                                ObservationContainer resTestVO = (ObservationContainer) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, ordTestDT.getSourceActUid());

                                //BB - civil0012298 - Retrieve User Name to be displayed instead of ID
                                resTestVO.getTheObservationDto().setAddUserName(AuthUtil.authUser.getUserId()); //NOSONAR
                                theObservationContainerCollection.add(resTestVO);

                            }
                        }

                    }
                }
                //If a Resulted Test observation
                else if (typeCd != null && typeCd.equalsIgnoreCase(NEDSSConstant.ACT108_TYP_CD))
                {
                    ObservationContainer rtObservationContainer = (ObservationContainer) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);
                    if (rtObservationContainer == null) {
                        continue;
                    }
                    theObservationContainerCollection.add(rtObservationContainer); //The Resulted Test itself
                    //Retrieve the RT's lab
                    OrganizationContainer rtPerformingLab = retrievePerformingLabAkaOrganizationFromParticipation(rtObservationContainer.getTheParticipationDtoCollection());
                    if (rtPerformingLab != null) {
                        performingLabColl.add(rtPerformingLab);
                    }

                    //Retrieves all reflex observations, including each ordered and its resulted
                    Collection<ObservationContainer> reflexObsColl = retrieveReflexObservationsFromActRelationship(rtObservationContainer.getTheActRelationshipDtoCollection());
                    if (reflexObsColl == null || reflexObsColl.isEmpty()) {
                        continue;
                    }
                    theObservationContainerCollection.addAll(reflexObsColl);
                }
            }
        }

        mapper.put(DataProcessingMapKey.OBSERVATION, theObservationContainerCollection);
        mapper.put(DataProcessingMapKey.ORGANIZATION, performingLabColl);
        return mapper;
    }

    /**
     * was: retrieveReflexObservations
     */
    @SuppressWarnings("java:S3776")

    private Collection<ObservationContainer>  retrieveReflexObservationsFromActRelationship(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException
    {
        Collection<ObservationContainer>  reflexObsVOCollection  = null;

        for (ActRelationshipDto actRelDT : actRelColl) {
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
                ObservationContainer reflexObs = (ObservationContainer) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);

                if (reflexObs == null) {
                    continue;
                } 
                else {
                    if (reflexObsVOCollection == null) {
                        reflexObsVOCollection = new ArrayList<>();
                    }
                    reflexObsVOCollection.add(reflexObs);
                }

                //Retrieves its associated reflex resulted tests
                Collection<ObservationContainer> reflexRTs = retrieveReflexRTsAkaObservationFromActRelationship(reflexObs.getTheActRelationshipDtoCollection());
                if (reflexRTs == null || reflexRTs.isEmpty()
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
    private Collection<ObservationContainer>  retrieveReflexRTsAkaObservationFromActRelationship(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException
    {
        Collection<ObservationContainer>  reflexRTCollection  = null;

        for (ActRelationshipDto actRelDT : actRelColl) {
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
                ObservationContainer reflexObs = (ObservationContainer) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);

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
    private OrganizationContainer retrievePerformingLabAkaOrganizationFromParticipation(Collection<ParticipationDto> partColl) throws DataProcessingException
    {
        OrganizationContainer lab = null;

        for (ParticipationDto partDT : partColl) {
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
    private Collection<Object>  retrieveInterventionFromActRelationship(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException
    {
        Collection<Object>  theInterventionVOCollection  = null;

        for (ActRelationshipDto actRelDT : actRelColl) {
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

    protected void processingNotELRLab(boolean isELR, LabResultProxyContainer lrProxyVO,
                                       ObservationContainer orderedTest, long observationId) throws DataProcessingException {
        if (!isELR) {
            boolean exists = notificationService.checkForExistingNotification(lrProxyVO);
            lrProxyVO.setAssociatedNotificationInd(exists);


            Collection<ActRelationshipDto> col = actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(observationId, NEDSSConstant.LAB_REPORT);
            if (col != null && col.size() > 0)
            {
                lrProxyVO.setAssociatedInvInd(true);
            }


            // get EDX Document data
            Collection<EDXDocumentDto> documentList = edxDocumentService.selectEdxDocumentCollectionByActUid(observationId);
            if (documentList != null) {
                lrProxyVO.setEDXDocumentCollection(documentList);
            }

            // get the list of conditions associated with this Lab
            ArrayList<String> conditionList = observationCodeService.deriveTheConditionCodeList(lrProxyVO, orderedTest);
            if (conditionList != null && !conditionList.isEmpty()) {
                lrProxyVO.setTheConditionsList(conditionList);
            }
        }
    }

    protected  void loadingObservationToLabResultContainerActHelper(LabResultProxyContainer lrProxyVO,
                                                                    boolean isELR,
                                                                    Map<DataProcessingMapKey, Object> allAct,
                                                                    ObservationContainer orderedTest)  {
        if (!allAct.isEmpty())
        {
            //Set intervention collection
            lrProxyVO.setTheInterventionVOCollection( (Collection<Object>) allAct.get(DataProcessingMapKey.INTERVENTION));

            //Set observation collection
            Collection<ObservationContainer> obsColl = (Collection<ObservationContainer>) allAct.get(DataProcessingMapKey.OBSERVATION);
            if (obsColl == null)
            {
                obsColl = new ArrayList<>();
            }

            //BB - civil0012298 - Retrieve User Name to b displayed instead of ID!
            if(!isELR) {
                orderedTest.getTheObservationDto().setAddUserName(AuthUtil.authUser.getUserId());
                orderedTest.getTheObservationDto().setLastChgUserName(AuthUtil.authUser.getUserId());
            }

            obsColl.add(orderedTest);
            lrProxyVO.setTheObservationContainerCollection(obsColl);

            //Adds the performing lab(if any) to the organization cellection
            Collection<OrganizationContainer>  labColl = (Collection<OrganizationContainer>) allAct.get(DataProcessingMapKey.ORGANIZATION);
            if (labColl != null && labColl.size() > 0)
            {
                lrProxyVO.getTheOrganizationContainerCollection().addAll(labColl);
            }
        }
    }
    /**
     *  LabResultProxyVO getLabResultProxyVO(Long observationId,  boolean isELR, NBSSecurityObj nbsSecurityObj)
     * */
    private LabResultProxyContainer loadingObservationToLabResultContainer(Long observationId,  boolean isELR) throws DataProcessingException {
        LabResultProxyContainer lrProxyVO =  new LabResultProxyContainer();

        // LOADING EXISTING Observation
        ObservationContainer orderedTest = (ObservationContainer) getAbstractObjectForObservationOrIntervention(NEDSSConstant.OBSERVATION_CLASS_CODE, observationId);
        Collection<ParticipationDto>  partColl = orderedTest.getTheParticipationDtoCollection(); //NOSONAR

        if (partColl != null && !partColl.isEmpty())
        {
            Map<DataProcessingMapKey, Object> allEntity = retrieveEntityFromParticipationForContainer(partColl);
            if (!allEntity.isEmpty())
            {
                lrProxyVO.setThePersonContainerCollection((Collection<PersonContainer>) allEntity.get(DataProcessingMapKey.PERSON));
                lrProxyVO.setTheOrganizationContainerCollection((Collection<OrganizationContainer>) allEntity.get(DataProcessingMapKey.ORGANIZATION));
                lrProxyVO.setTheMaterialContainerCollection((Collection<MaterialContainer>) allEntity.get(DataProcessingMapKey.MATERIAL));
                Collection<RoleDto> roleObjs = (Collection<RoleDto>)  allEntity.get(DataProcessingMapKey.ROLE);
                Collection<RoleDto> roleDtoCollection;
                roleDtoCollection = Objects.requireNonNullElseGet(roleObjs, ArrayList::new);
                lrProxyVO.setTheRoleDtoCollection(roleDtoCollection);
            }
        }

        Collection<ActRelationshipDto>  actRelColl = orderedTest.getTheActRelationshipDtoCollection();

        if (actRelColl != null && !actRelColl.isEmpty())
        {
            Map<DataProcessingMapKey, Object> allAct = retrieveActForLabResultContainer(actRelColl);
            loadingObservationToLabResultContainerActHelper( lrProxyVO, isELR, allAct, orderedTest);
        }

        processingNotELRLab( isELR,  lrProxyVO,
                 orderedTest,  observationId);
        try {
            PageContainer pageContainer = answerService.getNbsAnswerAndAssociation(observationId);
            lrProxyVO.setPageVO(pageContainer);
        } catch (Exception e) {
            logger.error("Exception while getting data from NBS Answer for Lab");
            e.printStackTrace();
        }

        return lrProxyVO;
    }

    private Map<Object, Object> setLabResultProxy(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {

            //saving LabResultProxyVO before updating auto resend notifications
            Map<Object, Object> returnVal = setLabResultProxyWithoutNotificationAutoResend(labResultProxyVO);

            updateLabResultWithAutoResendNotification(labResultProxyVO);

            //TODO: EMAIL NOTIFICATION IS FLAGGED HERE

            if(labResultProxyVO.getMessageLogDCollection()!=null && labResultProxyVO.getMessageLogDCollection().size()>0){
                try {
                    messageLogService.saveMessageLog(labResultProxyVO.getMessageLogDCollection());
                } catch (Exception e) {
                    logger.error("Unable to store the Error message for = "+ labResultProxyVO.getMessageLogDCollection());
                }
            }
            return returnVal;


    }

    protected NNDActivityLogDto updateLabResultWithAutoResendNotification(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {
        NNDActivityLogDto nndActivityLogDto = null;
        try
        {
            //update auto resend notifications
            if(labResultProxyVO.associatedNotificationInd)
            {
                investigationService.updateAutoResendNotificationsAsync(labResultProxyVO);
            }
        }
        catch(Exception e)
        {
            nndActivityLogDto = new NNDActivityLogDto();
            nndActivityLogDto.setErrorMessageTxt(e.toString());
            Collection<ObservationContainer> observationCollection  = labResultProxyVO.getTheObservationContainerCollection();
            ObservationContainer obsVO = findObservationByCode(observationCollection, NEDSSConstant.LAB_REPORT);
            String localId = obsVO.getTheObservationDto().getLocalId(); //NOSONAR
            if (localId!=null)
            {
                nndActivityLogDto.setLocalId(localId);
            }
            else
                nndActivityLogDto.setLocalId("N/A");
            //catch & store auto resend notifications exceptions in NNDActivityLog table
            nndActivityLogService.saveNddActivityLog(nndActivityLogDto);
            logger.error("Exception occurred while calling nndMessageSenderHelper.updateAutoResendNotificationsAsync");
            e.printStackTrace();
        }

        return nndActivityLogDto;
    }
    @SuppressWarnings("java:S3776")
    private Map<Object, Object> setLabResultProxyWithoutNotificationAutoResend(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {

        //Set flag for type of processing
        boolean ELR_PROCESSING = false;

        // We need specific auth User for elr processing, but probably wont applicable for data processing
        if (AuthUtil.authUser != null || (AuthUtil.authUser != null && AuthUtil.authUser.getUserId().equals(NEDSSConstant.ELR_LOAD_USER_ACCOUNT)))
        {
            ELR_PROCESSING = true;
        }

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

            //For ELR update, mpr uid may be not available
            if(patientMprUid < 0)
            {
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
            OrganizationContainer organizationContainer;
            if (labResultProxyVO.getTheOrganizationContainerCollection() != null)
            {
                for (OrganizationContainer vo : labResultProxyVO.getTheOrganizationContainerCollection()) {
                    organizationContainer = vo;
                    OrganizationDto newOrganizationDto;

                    var orgCheck = organizationRepositoryUtil.loadObject(organizationContainer.getTheOrganizationDto().getOrganizationUid(), null);
                    Integer existingVer = null;
                    if (orgCheck != null && orgCheck.getTheOrganizationDto() != null) {
                        existingVer = orgCheck.getTheOrganizationDto().getVersionCtrlNbr();
                    }
                    if (organizationContainer.isItNew())
                    {
                        newOrganizationDto = (OrganizationDto) prepareAssocModelHelper.prepareVO(
                                organizationContainer.getTheOrganizationDto(), NBSBOLookup.ORGANIZATION,
                                NEDSSConstant.ORG_CR, "ORGANIZATION",
                                NEDSSConstant.BASE,
                                existingVer
                        );
                        organizationContainer.setTheOrganizationDto(newOrganizationDto);
                        falseUid = organizationContainer.getTheOrganizationDto().getOrganizationUid();
                        logger.debug("false organizationUID: " + falseUid);

                        realUid = organizationRepositoryUtil.setOrganization(organizationContainer, null);
                        if (falseUid.intValue() < 0) {
                            uidService.setFalseToNewForObservation(labResultProxyVO, falseUid, realUid);
                        }
                    }
                    else if (organizationContainer.isItDirty())
                    {
                        newOrganizationDto = (OrganizationDto) prepareAssocModelHelper.prepareVO(
                                organizationContainer.getTheOrganizationDto(), NBSBOLookup.ORGANIZATION,
                                NEDSSConstant.ORG_EDIT, "ORGANIZATION",
                                NEDSSConstant.BASE,
                                existingVer
                        );

                        organizationContainer.setTheOrganizationDto(newOrganizationDto);
                        organizationRepositoryUtil.setOrganization(organizationContainer, null);
                    }
                }
            }

            //MaterialCollection

            MaterialContainer materialContainer;
            if (labResultProxyVO.getTheMaterialContainerCollection() != null)
            {
                for (MaterialContainer vo : labResultProxyVO.getTheMaterialContainerCollection()) {
                    materialContainer = vo;
                    MaterialDto newMaterialDto;
                    logger.debug("materialUID: " + materialContainer.getTheMaterialDto().getMaterialUid());

                    Integer eixstVerNum = null;
                    if(materialContainer.getTheMaterialDto().getMaterialUid() > 0) {
                        var existMat = materialService.loadMaterialObject(materialContainer.getTheMaterialDto().getMaterialUid());
                        if (existMat != null && existMat.getTheMaterialDto() != null) {
                            eixstVerNum = existMat.getTheMaterialDto().getVersionCtrlNbr();
                        }
                    }


                    if (materialContainer.isItNew()) {
                        newMaterialDto = (MaterialDto) prepareAssocModelHelper.prepareVO(materialContainer.
                                        getTheMaterialDto(), NBSBOLookup.MATERIAL,
                                NEDSSConstant.MAT_MFG_CR, "MATERIAL",
                                NEDSSConstant.BASE,
                                eixstVerNum
                        );
                        materialContainer.setTheMaterialDto(newMaterialDto);
                        falseUid = materialContainer.getTheMaterialDto().getMaterialUid();
                        realUid = materialService.saveMaterial(materialContainer);
                        if (falseUid.intValue() < 0) {
                            uidService.setFalseToNewForObservation(labResultProxyVO, falseUid, realUid);
                        }
                    } else if (materialContainer.isItDirty()) {
                        newMaterialDto = (MaterialDto) prepareAssocModelHelper.prepareVO(materialContainer.
                                        getTheMaterialDto(), NBSBOLookup.MATERIAL,
                                NEDSSConstant.MAT_MFG_EDIT, "MATERIAL",
                                NEDSSConstant.BASE,
                                eixstVerNum
                        );
                        materialContainer.setTheMaterialDto(newMaterialDto);

                        realUid = materialService.saveMaterial(materialContainer);
                        logger.debug("exisiting but updated material's UID: " + realUid);
                    }
                }
            }

            //ParticipationCollection

            if (labResultProxyVO.getTheParticipationDtoCollection() != null)
            {
                logger.debug("Iniside participation Collection<Object>  Loop - Lab");
                for (ParticipationDto dt : labResultProxyVO.getTheParticipationDtoCollection()) {

                    logger.debug("Inside loop size of participations: " + labResultProxyVO.getTheParticipationDtoCollection().size());
                    try {
                        if (dt != null) {
                            if (dt.isItDelete()) {
                                participationService.saveParticipationHist(dt);
                            }

                            participationService.saveParticipation(dt);


                            logger.debug("got the participationDto, the ACTUID is " +
                                    dt.getActUid());
                            logger.debug("got the participationDto, the subjectEntityUid is " +
                                    dt.getSubjectEntityUid());
                        }
                    } catch (Exception e) {
                        throw new DataProcessingException(e.getMessage(),e);
                    } 
                }
            }


            //ActRelationship Collection

            if (labResultProxyVO.getTheActRelationshipDtoCollection() != null)
            {
                logger.debug("Act relationship size: " + labResultProxyVO.getTheActRelationshipDtoCollection().size());
                for (ActRelationshipDto actRelationshipDto : labResultProxyVO.getTheActRelationshipDtoCollection()) {
                    try {
                        if (actRelationshipDto != null) {
                            actRelationshipService.saveActRelationship(actRelationshipDto);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new DataProcessingException(e.getMessage(), e);
                    }
                }
            }


            Collection<RoleDto>  roleDTColl = labResultProxyVO.getTheRoleDtoCollection();
            if (roleDTColl != null && !roleDTColl.isEmpty())
            {
                roleService.storeRoleDTCollection(roleDTColl);
            }



            //add LDF data
            /**
             * @TBD Release 6.0, Commented out as LDF will be planned out as new type of answers
            LDFHelper ldfHelper = LDFHelper.getInstance();
            ldfHelper.setLDFCollection(labResultProxyVO.getTheStateDefinedFieldDataDTCollection(), labResultProxyVO.getLdfUids(),
            NEDSSConstant.LABREPORT_LDF,null,observationUid,securityObj);
             */

            //EDX Document


            Collection<EDXDocumentDto> edxDocumentCollection = labResultProxyVO.getEDXDocumentCollection();
            ObservationDto rootDT = observationUtil.getRootObservationDto(labResultProxyVO);
            if (edxDocumentCollection != null && edxDocumentCollection.size() > 0) {
                if (rootDT.getElectronicInd() != null && rootDT.getElectronicInd().equals(NEDSSConstant.YES)) {
                    for (EDXDocumentDto eDXDocumentDto : edxDocumentCollection) {
                        if (eDXDocumentDto.getPayload() != null) {
                            String payload = eDXDocumentDto.getPayload();
                            int containerIndex = payload.indexOf("<Container");
                            eDXDocumentDto.setPayload(payload.substring(containerIndex));
                        }
                        if (eDXDocumentDto.isItNew()) {
                            eDXDocumentDto.setActUid(observationUid);
                        }
                        edxDocumentService.saveEdxDocument(eDXDocumentDto);
                    }
                }
            }
            rootDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
            rootDT.setObservationUid(observationUid);


            // Does not seem to hit this case
            PageContainer pageContainer =(PageContainer)labResultProxyVO.getPageVO();
            if(labResultProxyVO.isItDirty())
            {
                answerService.storePageAnswer(pageContainer, rootDT);
            }
            else {
                    answerService.insertPageVO(pageContainer, rootDT);
            }


        }
        return returnVal;
    }

    protected ObservationContainer findObservationByCode(Collection<ObservationContainer> coll, String strCode)
    {
        if (coll == null)
        {
            return null;
        }

        for (ObservationContainer obsVO : coll) {
            ObservationDto obsDT = obsVO.getTheObservationDto();

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
    private Map<Object, Object> processObservationContainerCollection(BaseContainer proxyVO, boolean ELR_PROCESSING) throws DataProcessingException {
        if (proxyVO instanceof LabResultProxyContainer)
        {
            return processLabReportObsContainerCollection( (LabResultProxyContainer) proxyVO, ELR_PROCESSING);
        }

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
        Collection<ObservationContainer>obsContainerCollection = labResultProxyVO.getTheObservationContainerCollection();
        ObservationContainer observationContainer;
        Map<Object, Object> returnObsVal;
        boolean isMannualLab = false;

        //Find out if it is mannual lab
        String electronicInd = observationUtil.getRootObservationDto(labResultProxyVO).getElectronicInd();
        if(electronicInd != null && !electronicInd.equals(NEDSSConstant.YES))
        {
            isMannualLab = true;
        }

        if (obsContainerCollection != null && !obsContainerCollection.isEmpty())
        {
            for (ObservationContainer item : obsContainerCollection) {
                observationContainer = item;
                if (observationContainer == null) {
                    continue;
                }

                //For ordered test and resulted tests
                ObservationDto currentDT = observationContainer.getTheObservationDto();
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
                        observationContainer = srteCodeObsService.labLoincSnomedLookup(observationContainer, labResultProxyVO.getLabClia());
                    }
                    logger.debug("observationUID: " + observationContainer.getTheObservationDto().getObservationUid());
                     **/
                }
            }
        }

        //Process the ordered test further
        returnObsVal = processLabReportOrderTest(labResultProxyVO, ELR_PROCESSING);

        //Then, persist the observations
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
            ObservationContainer orderTest = observationUtil.getRootObservationContainer(labResultProxyVO);

            //Overrides rptToStateTime to current date/time for external user
            if (AuthUtil.authUser.getUserType() != null && AuthUtil.authUser.getUserType().equalsIgnoreCase(NEDSSConstant.SEC_USERTYPE_EXTERNAL))
            {
                orderTest.getTheObservationDto().setRptToStateTime(getCurrentTimeStamp());
            }

            //Assign program area cd if necessary, and return any errors to the client
            Map<Object, Object> returnErrors = new HashMap<>();
            String paCd = orderTest.getTheObservationDto().getProgAreaCd();
            if (paCd != null && paCd.equalsIgnoreCase(ProgramAreaJurisdiction.ANY_PROGRAM_AREA))
            {
                String paError = programAreaService.deriveProgramAreaCd(labResultProxyVO, orderTest);
                if (paError != null)
                {
                    returnErrors.put(NEDSSConstant.SETLAB_RETURN_PROGRAM_AREA_ERRORS, paError);
                }
            }

            //Assign jurisdiction cd if necessary
            String jurisdictionCd = orderTest.getTheObservationDto().getJurisdictionCd();
            if (jurisdictionCd != null &&
                    (jurisdictionCd.equalsIgnoreCase(ProgramAreaJurisdiction.ANY_JURISDICTION)
                    || jurisdictionCd.equalsIgnoreCase(ProgramAreaJurisdiction.JURISDICTION_NONE)
                    )
            )
            {
                String jurisdictionError = jurisdictionService.deriveJurisdictionCd(labResultProxyVO, orderTest.getTheObservationDto());
                if (jurisdictionError != null)
                {
                    returnErrors.put(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS, jurisdictionCd);
                }
            }

            //Manipulate jurisdiction for preparing vo
            jurisdictionCd = orderTest.getTheObservationDto().getJurisdictionCd();
            if(jurisdictionCd != null
                && (jurisdictionCd.trim().equals("")
                    || jurisdictionCd.equals("ANY")
                    || jurisdictionCd.equals("NONE")
                )
            )
            {
                orderTest.getTheObservationDto().setJurisdictionCd(null);
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
            if(localIds == null) {
                localIds = new HashMap<> ();
            }
            var resObs = observationRepository.findById(observationUid);
            ObservationDto obsDT = new ObservationDto();
            if (resObs.isPresent()) {
                obsDT = new ObservationDto(resObs.get());
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

    protected String processingOrderTestStateTransition(LabResultProxyContainer labResultProxyVO,
                                                      ObservationContainer orderTest,
                                                      String businessTriggerCd,
                                                      boolean isELR) {
        if (labResultProxyVO.isItNew() && orderTest.getTheObservationDto().getProcessingDecisionCd()!=null && !orderTest.getTheObservationDto().getProcessingDecisionCd().trim().equals(""))
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

        return businessTriggerCd;
    }
    protected void performOrderTestStateTransition(LabResultProxyContainer labResultProxyVO, ObservationContainer orderTest, boolean isELR) throws DataProcessingException
    {
        String businessTriggerCd = null;
        ObservationDto newObservationDto;

        businessTriggerCd = processingOrderTestStateTransition(labResultProxyVO,
                orderTest,
                businessTriggerCd,
                isELR);
        Integer existObsVer = null;
        if (orderTest.getTheObservationDto().getUid() > 0) {
            var existObs = observationRepositoryUtil.loadObject(orderTest.getTheObservationDto().getUid());
            if (existObs != null && existObs.getTheObservationDto() != null) {
                existObsVer = existObs.getTheObservationDto().getVersionCtrlNbr();
            }
        }

         newObservationDto = (ObservationDto) prepareAssocModelHelper.prepareVO(
                orderTest.getTheObservationDto(), NBSBOLookup.OBSERVATIONLABREPORT,
                businessTriggerCd, "OBSERVATION", NEDSSConstant.BASE,
                 existObsVer
         );
         orderTest.setTheObservationDto(newObservationDto);


    }
    @SuppressWarnings("java:S3776")
    private Long storeObservationVOCollection(BaseContainer proxyVO) throws DataProcessingException {
        try {
            //Iterates the observation collection and process each observation vo
            Collection<ObservationContainer>  obsVOColl = null;
            boolean isLabResultProxyVO = false;
            if (proxyVO instanceof LabResultProxyContainer)
            {
                obsVOColl = ( (LabResultProxyContainer) proxyVO).getTheObservationContainerCollection();
                isLabResultProxyVO = true;
            }

//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                obsVOColl = ( (MorbidityProxyVO) proxyVO).getTheObservationContainerCollection();
//            }

            ObservationContainer observationContainer ;
            Long returnObsVal = null;

            if (obsVOColl != null && obsVOColl.size() > 0)
            {
                for (ObservationContainer item : obsVOColl) {
                    observationContainer = item;

                    if (observationContainer == null) {
                        continue;
                    }

                    //If lab report's order test, set a flag
                    boolean isRootObs = false;

                    String obsDomainCdSt1 = observationContainer.getTheObservationDto().
                            getObsDomainCdSt1();
                    if (isLabResultProxyVO && obsDomainCdSt1 != null &&
                            obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD)) {
                        isRootObs = true;
                    }

                    //If a root morbidity, set a flag so to return the observation uid
                    String ctrlCdDisplayForm = observationContainer.getTheObservationDto().
                            getCtrlCdDisplayForm();
                    if (ctrlCdDisplayForm != null &&
                            ctrlCdDisplayForm.equalsIgnoreCase(NEDSSConstant.MOB_CTRLCD_DISPLAY)) {
                        isRootObs = true;
                    }

                    //Persist the observation vo
                    Long observationUid = observationRepositoryUtil.saveObservation(observationContainer);

                    //Update associations with real uid if new
                    if (observationContainer.isItNew()) {
                        Long falseUid = observationContainer.getTheObservationDto().getObservationUid();
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




    protected boolean processObservationWithProcessingDecision(Long observationUid, String processingDecisionCd, String processingDecisionTxt) throws DataProcessingException {

        try
        {
            ObservationContainer observationVO = observationRepositoryUtil.loadObject(observationUid);

            ObservationDto observationDT = observationVO.getTheObservationDto();
            observationDT.setProcessingDecisionCd(processingDecisionCd);
            if(processingDecisionTxt!=null && !processingDecisionTxt.isEmpty())
            {
                observationDT.setProcessingDecisionTxt(processingDecisionTxt);
            }

            String observationType = observationDT.getCtrlCdDisplayForm();
            String businessTrigger;
            String businessObjLookupName;

            if(observationType.equalsIgnoreCase(NEDSSConstant.LABRESULT_CODE)){
                businessTrigger = NEDSSConstant.OBS_LAB_PROCESS;
                businessObjLookupName = NBSBOLookup.OBSERVATIONLABREPORT;

            }
            else{
                throw new DataProcessingException("This is not a Lab Report OR a Morbidity Report! MarkAsReviewed only applies to Lab Report or Morbidity Report ");
            }

            if (observationDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.OBS_UNPROCESSED))
            {
                observationDT.setItNew(false);
                observationDT.setItDirty(true);

                RootDtoInterface rootDTInterface =  prepareAssocModelHelper.prepareVO(
                        observationDT,
                        businessObjLookupName,
                        businessTrigger,
                        "OBSERVATION",
                        NEDSSConstant.BASE,
                        observationDT.getVersionCtrlNbr()
                );

                observationVO.setTheObservationDto((ObservationDto) rootDTInterface);
                observationRepositoryUtil.saveObservation(observationVO);
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (Exception ex)
        {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }


    private void setObservationAssociations(Long investigationUid, Collection<LabReportSummaryContainer>  observationSummaryVOColl) throws DataProcessingException {
        investigationService.setAssociations(investigationUid, observationSummaryVOColl,
                null, null,null, true);

    }

}



