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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;


@Service
@Slf4j

public class ObservationService implements IObservationService {

    protected static final Logger logger = LoggerFactory.getLogger(ObservationService.class);

    protected final INNDActivityLogService nndActivityLogService;
    protected final IMessageLogService messageLogService;

    protected final ObservationRepositoryUtil observationRepositoryUtil;

    protected final INotificationService notificationService;

    protected final IMaterialService materialService;

    protected final PatientRepositoryUtil patientRepositoryUtil;

    protected final IRoleService roleService;

    protected final IActRelationshipService actRelationshipService;

    protected final IEdxDocumentService edxDocumentService;
    protected final IAnswerService answerService;

    protected final IParticipationService participationService;

    protected final ObservationRepository observationRepository;
    protected final PersonRepository personRepository;
    protected final IJurisdictionService jurisdictionService;

    protected final OrganizationRepositoryUtil organizationRepositoryUtil;

    protected final IObservationCodeService observationCodeService;

    protected final ObservationUtil observationUtil;
    protected final PersonUtil personUtil;

    protected final IProgramAreaService programAreaService;

    protected final PrepareAssocModelHelper prepareAssocModelHelper;

    protected final IUidService uidService;

    protected final IInvestigationService investigationService;
    @Value("${service.timezone}")
    protected String tz = "UTC";

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


    public boolean processObservation(Long observationUid) throws DataProcessingException {
        return processObservationWithProcessingDecision(observationUid,
                null, null);

    }

    public void setLabInvAssociation(Long labUid, Long investigationUid) throws DataProcessingException {
        LabReportSummaryContainer labReportSummaryVO = new LabReportSummaryContainer();
        labReportSummaryVO.setTouched(true);
        labReportSummaryVO.setAssociated(true);
        labReportSummaryVO.setObservationUid(labUid);
        labReportSummaryVO.setActivityFromTime(getCurrentTimeStamp(tz));
        Collection<LabReportSummaryContainer> labReportSummaryVOColl = new ArrayList<>();
        labReportSummaryVOColl.add(labReportSummaryVO);

        setObservationAssociations(investigationUid, labReportSummaryVOColl);
    }

    /**
     * Loading Existing Either Observation or Intervention
     * was: getActVO
     * */
    protected BaseContainer getAbstractObjectForObservationOrIntervention(String actType, Long anUid) throws DataProcessingException
    {
        BaseContainer obj = null;
        if (anUid != null && actType.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE))
        {
            obj = observationRepositoryUtil.loadObject(anUid);
        }
        return obj;
    }


    /**
     * Retrieving assoc data from Participation: PERSON, ORG, MATERIAL, ROLE
     * */
    @SuppressWarnings("java:S1640")
    protected Map<DataProcessingMapKey, Object>  retrieveEntityFromParticipationForContainer(Collection<ParticipationDto> partColl)  {
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
    protected Collection<Object>  retrieveOrganizationFromParticipation(Collection<ParticipationDto> partColl) {
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
            )
            {
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
    @SuppressWarnings({"java:S3776","java:S1640"})
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
                    if (scopedPersons != null && !scopedPersons.isEmpty()) {
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
    @SuppressWarnings("java:S1640")
    Map<DataProcessingMapKey, Object> retrieveActForLabResultContainer(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException {
        Map<DataProcessingMapKey, Object> mapper = new HashMap<>();

        //Retrieve associated interventions
        mapper.put(DataProcessingMapKey.INTERVENTION, retrieveInterventionFromActRelationship(actRelColl));

        //Retrieve associated observations and performing labs of any resulted tests
        Map<DataProcessingMapKey, Object> obsOrg = retrieveObservationFromActRelationship(actRelColl);
        mapper.put(DataProcessingMapKey.OBSERVATION, obsOrg.get(DataProcessingMapKey.OBSERVATION));
        mapper.put(DataProcessingMapKey.ORGANIZATION, obsOrg.get(DataProcessingMapKey.ORGANIZATION));


        return mapper;
    }

    /**
     * Retrieving Observation and the assoc Organization
     * was: retrieveObservationVOsForProxyVO
     * */
    @SuppressWarnings({"java:S3776", "java:S1640", "java:S135"})
    protected Map<DataProcessingMapKey, Object>  retrieveObservationFromActRelationship(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException
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
    @SuppressWarnings({"java:S3776", "java:S135"})

    protected Collection<ObservationContainer>  retrieveReflexObservationsFromActRelationship(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException
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
    @SuppressWarnings("java:S135")
    protected Collection<ObservationContainer>  retrieveReflexRTsAkaObservationFromActRelationship(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException
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
    @SuppressWarnings("java:S135")
    protected OrganizationContainer retrievePerformingLabAkaOrganizationFromParticipation(Collection<ParticipationDto> partColl)
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
    protected Collection<Object>  retrieveInterventionFromActRelationship(Collection<ActRelationshipDto> actRelColl) throws DataProcessingException
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
            if (col != null && !col.isEmpty())
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
            if (labColl != null && !labColl.isEmpty())
            {
                lrProxyVO.getTheOrganizationContainerCollection().addAll(labColl);
            }
        }
    }
    /**
     *  LabResultProxyVO getLabResultProxyVO(Long observationId,  boolean isELR, NBSSecurityObj nbsSecurityObj)
     * */
    protected LabResultProxyContainer loadingObservationToLabResultContainer(Long observationId,  boolean isELR) throws DataProcessingException {
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
            logger.error(e.getMessage());
        }

        return lrProxyVO;
    }

    protected Map<Object, Object> setLabResultProxy(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {

            Map<Object, Object> returnVal = setLabResultProxyWithoutNotificationAutoResend(labResultProxyVO);

            updateLabResultWithAutoResendNotification(labResultProxyVO);

            if(labResultProxyVO.getMessageLogDCollection()!=null
                    && !labResultProxyVO.getMessageLogDCollection().isEmpty()){
                try {
                    messageLogService.saveMessageLog(labResultProxyVO.getMessageLogDCollection());
                } catch (Exception e) {
                    logger.error("Unable to store the Error message for = {}", labResultProxyVO.getMessageLogDCollection());
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
            {
                nndActivityLogDto.setLocalId("N/A");
            }
            //catch & store auto resend notifications exceptions in NNDActivityLog table
            nndActivityLogService.saveNddActivityLog(nndActivityLogDto);
            logger.error("Exception occurred while calling nndMessageSenderHelper.updateAutoResendNotificationsAsync");
            logger.info(e.getMessage());
        }

        return nndActivityLogDto;
    }

    protected Map<Object, Object> setLabResultProxyWithoutNotificationAutoResend(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {
        Map<Object, Object> returnVal = new HashMap<>();
        Long patientMprUid = processPatient(labResultProxyVO, returnVal);
        Map<Object, Object> obsResults = processObservations(labResultProxyVO, returnVal, patientMprUid);
        processOrganizations(labResultProxyVO);
        processMaterials(labResultProxyVO);
        processParticipations(labResultProxyVO);
        processActRelationships(labResultProxyVO);
        processRoles(labResultProxyVO);
        processEdxDocuments(labResultProxyVO, (Long) obsResults.get(NEDSSConstant.SETLAB_RETURN_OBS_UID));
        persistPageAnswers(labResultProxyVO, (Long) obsResults.get(NEDSSConstant.SETLAB_RETURN_OBS_UID));
        return returnVal;
    }

    protected Long processPatient(LabResultProxyContainer proxy, Map<Object, Object> returnVal) throws DataProcessingException {
        Long mprUid = personUtil.processLabPersonContainerCollection(proxy.getThePersonContainerCollection(), false, proxy);
        if (mprUid != null) {
            returnVal.put(NEDSSConstant.SETLAB_RETURN_MPR_UID, mprUid);
        }
        return mprUid;
    }

    protected Map<Object, Object> processObservations(LabResultProxyContainer proxy, Map<Object, Object> returnVal, Long mprUid) throws DataProcessingException {
        Map<Object, Object> obsResults = processObservationContainerCollection(proxy, true);
        if (!obsResults.isEmpty()) {
            returnVal.putAll(obsResults);
        }

        if (mprUid != null && mprUid < 0) {
            mprUid = participationService.findPatientMprUidByObservationUid(
                    NEDSSConstant.PERSON, NEDSSConstant.PAR110_TYP_CD,
                    (Long) obsResults.get(NEDSSConstant.SETLAB_RETURN_OBS_UID)
            );
            if (mprUid == null) {
                throw new DataProcessingException("Expected this observation to be associated with a patient, observation uid = " + obsResults.get(NEDSSConstant.SETLAB_RETURN_OBS_UID));
            }
            returnVal.put(NEDSSConstant.SETLAB_RETURN_MPR_UID, mprUid);
        }

        Long obsUid = (Long) obsResults.get(NEDSSConstant.SETLAB_RETURN_OBS_UID);
        returnVal.putAll(findLocalUidsFor(mprUid, obsUid));
        return obsResults;
    }

    protected void processOrganizations(LabResultProxyContainer proxy) throws DataProcessingException {
        if (proxy.getTheOrganizationContainerCollection() == null) return;

        for (OrganizationContainer orgContainer : proxy.getTheOrganizationContainerCollection()) {
            if (orgContainer == null) continue;
            OrganizationDto dto = orgContainer.getTheOrganizationDto();
            var loaded = organizationRepositoryUtil.loadObject(dto.getOrganizationUid(), null);
            Integer version = (loaded != null && loaded.getTheOrganizationDto() != null) ? loaded.getTheOrganizationDto().getVersionCtrlNbr() : null;

            if (orgContainer.isItNew()) {
                dto = (OrganizationDto) prepareAssocModelHelper.prepareVO(dto, NBSBOLookup.ORGANIZATION, NEDSSConstant.ORG_CR, "ORGANIZATION", NEDSSConstant.BASE, version);
                orgContainer.setTheOrganizationDto(dto);
                Long falseUid = dto.getOrganizationUid();
                Long realUid = organizationRepositoryUtil.setOrganization(orgContainer, null);
                if (falseUid < 0) uidService.setFalseToNewForObservation(proxy, falseUid, realUid);
            } else if (orgContainer.isItDirty()) {
                dto = (OrganizationDto) prepareAssocModelHelper.prepareVO(dto, NBSBOLookup.ORGANIZATION, NEDSSConstant.ORG_EDIT, "ORGANIZATION", NEDSSConstant.BASE, version);
                orgContainer.setTheOrganizationDto(dto);
                organizationRepositoryUtil.setOrganization(orgContainer, null);
            }
        }
    }

    @SuppressWarnings("java:S3776")
    protected void processMaterials(LabResultProxyContainer proxy) throws DataProcessingException {
        if (proxy.getTheMaterialContainerCollection() == null) return;

        for (MaterialContainer matContainer : proxy.getTheMaterialContainerCollection()) {
            if (matContainer == null) continue;
            MaterialDto dto = matContainer.getTheMaterialDto();
            Integer version = null;

            if (dto.getMaterialUid() > 0) {
                var existing = materialService.loadMaterialObject(dto.getMaterialUid());
                if (existing != null && existing.getTheMaterialDto() != null) {
                    version = existing.getTheMaterialDto().getVersionCtrlNbr();
                }
            }

            if (matContainer.isItNew()) {
                dto = (MaterialDto) prepareAssocModelHelper.prepareVO(dto, NBSBOLookup.MATERIAL, NEDSSConstant.MAT_MFG_CR, "MATERIAL", NEDSSConstant.BASE, version);
                matContainer.setTheMaterialDto(dto);
                Long falseUid = dto.getMaterialUid();
                Long realUid = materialService.saveMaterial(matContainer);
                if (falseUid < 0) uidService.setFalseToNewForObservation(proxy, falseUid, realUid);
            } else if (matContainer.isItDirty()) {
                dto = (MaterialDto) prepareAssocModelHelper.prepareVO(dto, NBSBOLookup.MATERIAL, NEDSSConstant.MAT_MFG_EDIT, "MATERIAL", NEDSSConstant.BASE, version);
                matContainer.setTheMaterialDto(dto);
                materialService.saveMaterial(matContainer);
            }
        }
    }

    protected void processParticipations(LabResultProxyContainer proxy) throws DataProcessingException {
        if (proxy.getTheParticipationDtoCollection() == null) return;

        List<ParticipationDto> toSave = new ArrayList<>();
        List<ParticipationDto> toDelete = new ArrayList<>();

        for (ParticipationDto dto : proxy.getTheParticipationDtoCollection()) {
            if (dto != null) {
                if (dto.isItDelete()) toDelete.add(dto);
                else toSave.add(dto);
            }
        }

        if (!toDelete.isEmpty()) participationService.saveParticipationHistBatch(toDelete);
        if (!toSave.isEmpty()) participationService.saveParticipationByBatch(toSave);
    }

    protected void processActRelationships(LabResultProxyContainer proxy) throws DataProcessingException {
        if (proxy.getTheActRelationshipDtoCollection() == null) return;

        for (ActRelationshipDto dto : proxy.getTheActRelationshipDtoCollection()) {
            if (dto != null) actRelationshipService.saveActRelationship(dto);
        }
    }

    protected void processRoles(LabResultProxyContainer proxy) throws DataProcessingException {
        Collection<RoleDto> roles = proxy.getTheRoleDtoCollection();
        if (roles != null && !roles.isEmpty()) {
            roleService.storeRoleDTCollection(roles);
        }
    }

    protected void processEdxDocuments(LabResultProxyContainer proxy, Long observationUid) throws DataProcessingException {
        Collection<EDXDocumentDto> docs = proxy.getEDXDocumentCollection();
        ObservationDto root = observationUtil.getRootObservationDto(proxy);

        if (docs == null || docs.isEmpty() || root.getElectronicInd() == null || !root.getElectronicInd().equals(NEDSSConstant.YES)) return;

        List<EDXDocumentDto> toSave = new ArrayList<>();
        for (EDXDocumentDto dto : docs) {
            if (dto.getPayload() != null) {
                int index = dto.getPayload().indexOf("<Container");
                if (index != -1) dto.setPayload(dto.getPayload().substring(index));
            }
            if (dto.isItNew()) dto.setActUid(observationUid);
            toSave.add(dto);
        }

        if (!toSave.isEmpty()) edxDocumentService.saveEdxDocumentBatch(toSave);
    }

    protected void persistPageAnswers(LabResultProxyContainer proxy, Long observationUid) throws DataProcessingException {
        ObservationDto root = observationUtil.getRootObservationDto(proxy);
        root.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        root.setObservationUid(observationUid);

        PageContainer page = (PageContainer) proxy.getPageVO();
        if (proxy.isItDirty()) {
            answerService.storePageAnswer(page, root);
        } else {
            answerService.insertPageVO(page, root);
        }
    }

    @SuppressWarnings("java:S135")
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
    protected Map<Object, Object> processObservationContainerCollection(BaseContainer proxyVO, boolean elrProcessing) throws DataProcessingException {
        if (proxyVO instanceof LabResultProxyContainer labResultProxyContainer)
        {
            return processLabReportObsContainerCollection( labResultProxyContainer, elrProcessing);
        }
        else
        {
            throw new DataProcessingException("Expected a valid observation proxy vo, it is: " + proxyVO.getClass().getName());
        }

    }

    /**
     * Original Name: processLabReportObsVOCollection
     * */
    protected Map<Object, Object> processLabReportObsContainerCollection(LabResultProxyContainer labResultProxyVO, boolean elrProcessing) throws DataProcessingException {
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
            }
        }

        //Process the ordered test further
        returnObsVal = processLabReportOrderTest(labResultProxyVO, elrProcessing);

        //Then, persist the observations
        Long observationUid = storeObservationVOCollection(labResultProxyVO);

        //Return the order test uid
        if (observationUid != null)
        {
            returnObsVal.put(NEDSSConstant.SETLAB_RETURN_OBS_UID, observationUid);
        }
        return returnObsVal;

    }

    protected Map<Object, Object> processLabReportOrderTest(LabResultProxyContainer labResultProxyVO, boolean isELR) throws DataProcessingException {
        ObservationContainer orderTest = observationUtil.getRootObservationContainer(labResultProxyVO);
        ObservationDto observationDto = orderTest.getTheObservationDto();

        overrideRptToStateTimeIfExternalUser(observationDto);
        Map<Object, Object> returnErrors = new HashMap<>();

        handleProgramAreaCode(labResultProxyVO, orderTest, returnErrors);
        handleJurisdictionCode(labResultProxyVO, observationDto, returnErrors);
        normalizeJurisdictionCode(observationDto);

        performOrderTestStateTransition(labResultProxyVO, orderTest, isELR);

        return returnErrors;
    }

    protected void overrideRptToStateTimeIfExternalUser(ObservationDto dto) {
        String userType = AuthUtil.authUser.getUserType();
        if (userType != null && userType.equalsIgnoreCase(NEDSSConstant.SEC_USERTYPE_EXTERNAL)) {
            dto.setRptToStateTime(getCurrentTimeStamp(tz));
        }
    }

    protected void handleProgramAreaCode(LabResultProxyContainer proxy, ObservationContainer orderTest, Map<Object, Object> errors) throws DataProcessingException {
        String paCd = orderTest.getTheObservationDto().getProgAreaCd();
        if (paCd != null && paCd.equalsIgnoreCase(ProgramAreaJurisdiction.ANY_PROGRAM_AREA)) {
            String error = programAreaService.deriveProgramAreaCd(proxy, orderTest);
            if (error != null) {
                errors.put(NEDSSConstant.SETLAB_RETURN_PROGRAM_AREA_ERRORS, error);
            }
        }
    }

    protected void handleJurisdictionCode(LabResultProxyContainer proxy, ObservationDto dto, Map<Object, Object> errors) throws DataProcessingException {
        String code = dto.getJurisdictionCd();
        if (code != null &&
                (code.equalsIgnoreCase(ProgramAreaJurisdiction.ANY_JURISDICTION)
                        || code.equalsIgnoreCase(ProgramAreaJurisdiction.JURISDICTION_NONE))) {

            String error = jurisdictionService.deriveJurisdictionCd(proxy, dto);
            if (error != null) {
                errors.put(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS, code);
            }
        }
    }

    protected void normalizeJurisdictionCode(ObservationDto dto) {
        String code = dto.getJurisdictionCd();
        if (code != null &&
                (code.trim().isEmpty() || code.equalsIgnoreCase("ANY") || code.equalsIgnoreCase("NONE"))) {
            dto.setJurisdictionCd(null);
        }
    }


    protected Map<Object, Object> findLocalUidsFor(Long personMprUid, Long observationUid)  {
        Map<Object, Object> localIds;

        //Find observation local id
        localIds = new HashMap<>();
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

        return localIds;
    }

    protected String processingOrderTestStateTransition(LabResultProxyContainer labResultProxyVO,
                                                      ObservationContainer orderTest,
                                                      String businessTriggerCd,
                                                      boolean isELR) {
        if (labResultProxyVO.isItNew() && orderTest.getTheObservationDto().getProcessingDecisionCd()!=null && !orderTest.getTheObservationDto().getProcessingDecisionCd().trim().isEmpty())
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

    protected Long storeObservationVOCollection(BaseContainer proxyVO) throws DataProcessingException {
        Collection<ObservationContainer> obsVOColl = extractObservationCollection(proxyVO);
        boolean isLabResultProxyVO = proxyVO instanceof LabResultProxyContainer;

        if (obsVOColl == null || obsVOColl.isEmpty()) {
            return null;
        }

        Long returnObsVal = null;

        for (ObservationContainer observationContainer : obsVOColl) {
            if (observationContainer == null) continue;

            boolean isRootObs = isRootObservation(observationContainer, isLabResultProxyVO);

            Long observationUid = observationRepositoryUtil.saveObservation(observationContainer);
            handleFalseUidReplacement(proxyVO, observationContainer, observationUid);

            if (observationUid != null && isRootObs) {
                returnObsVal = observationUid;
            }
        }

        return returnObsVal;
    }

    @SuppressWarnings("java:S1168")
    protected Collection<ObservationContainer> extractObservationCollection(BaseContainer proxyVO) {
        if (proxyVO instanceof LabResultProxyContainer labResultProxyContainer) {
            return labResultProxyContainer.getTheObservationContainerCollection();
        }
        return null;
    }

    protected boolean isRootObservation(ObservationContainer observationContainer, boolean isLabResultProxyVO) {
        ObservationDto dto = observationContainer.getTheObservationDto();

        String obsDomain = dto.getObsDomainCdSt1();
        if (isLabResultProxyVO && NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD.equalsIgnoreCase(obsDomain)) {
            return true;
        }

        String ctrlCd = dto.getCtrlCdDisplayForm();
        return NEDSSConstant.MOB_CTRLCD_DISPLAY.equalsIgnoreCase(ctrlCd);
    }

    protected void handleFalseUidReplacement(BaseContainer proxyVO, ObservationContainer observationContainer, Long realUid) {
        if (observationContainer.isItNew()) {
            Long falseUid = observationContainer.getTheObservationDto().getObservationUid();
            if (falseUid != null && falseUid < 0) {
                uidService.setFalseToNewForObservation(proxyVO, falseUid, realUid);
            }
        }
    }



    protected boolean processObservationWithProcessingDecision(Long observationUid, String processingDecisionCd, String processingDecisionTxt) throws DataProcessingException {

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
            observationVO.setItDirty(true);
            observationVO.setItNew(false);
            observationRepositoryUtil.saveObservation(observationVO);
            return true;
        }
        else
        {
            return false;
        }

    }


    protected void setObservationAssociations(Long investigationUid, Collection<LabReportSummaryContainer>  observationSummaryVOColl) throws DataProcessingException {
        investigationService.setAssociations(investigationUid, observationSummaryVOColl,
                null, null,null, true);

    }

}



