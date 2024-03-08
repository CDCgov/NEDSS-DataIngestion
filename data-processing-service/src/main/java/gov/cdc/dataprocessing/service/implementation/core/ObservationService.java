package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.constant.elr.*;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NNDActivityLogDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
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
import gov.cdc.dataprocessing.service.interfaces.matching.IObservationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.matching.IPatientMatchingService;
import gov.cdc.dataprocessing.service.interfaces.matching.IProviderMatchingService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ObservationService implements IObservationService {


    private final int RETRIEVED_PERSONS_FOR_PROXY = 0;
    private final int RETRIEVED_ORGANIZATIONS_FOR_PROXY = 1;
    private final int RETRIEVED_MATERIALS_FOR_PROXY = 2;
    private final int RETRIEVED_PATIENT_ROLES = 3;

    private final int RETRIEVED_INTERVENTIONS_FOR_PROXY = 0;
    private final int RETRIEVED_OBSERVATIONS_FOR_PROXY = 1;
    private final int RETRIEVED_LABS_FOR_RT = 2;

    private static final Logger logger = LoggerFactory.getLogger(ObservationService.class);

    private final IObservationMatchingService observationMatchingService;
    private final INNDActivityLogService nndActivityLogService;
    private final IMessageLogService messageLogService;

    private final ObservationUtil observationUtil;

    private final INotificationService notificationService;

    private final IMaterialService materialService;

    private final PatientRepositoryUtil patientRepositoryUtil;

    private final IRoleService roleService;

    private final IActRelationshipService actRelationshipService;

    private final IEdxDocumentService edxDocumentService;
    private final IAnswerService answerService;
    private final ISrteCodeObsService srteCodeObsService;

    private final IParticipationService participationService;

    private final IProviderMatchingService providerMatchingService;
    private final IPatientMatchingService patientMatchingService;

    private final ObservationRepository observationRepository;
    private final PersonRepository personRepository;
    private final IJurisdictionService jurisdictionService;

    private final EntityHelper entityHelper;

    private final OdseIdGeneratorService odseIdGeneratorService;

    private final ActRepository  actRepository;
    private final ObservationReasonRepository observationReasonRepository;
    private final ActIdRepository actIdRepository;
    private final ObservationInterpRepository observationInterpRepository;
    private final ObsValueCodedRepository obsValueCodedRepository;
    private final ObsValueTxtRepository obsValueTxtRepository;
    private final ObsValueDateRepository obsValueDateRepository;
    private final ObsValueNumericRepository obsValueNumericRepository;
    private final ActLocatorParticipationRepository actLocatorParticipationRepository;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;



    public ObservationService(IObservationMatchingService observationMatchingService,
                              INNDActivityLogService nndActivityLogService,
                              IMessageLogService messageLogService,
                              ObservationUtil observationUtil,
                              INotificationService notificationService,
                              IMaterialService materialService,
                              PatientRepositoryUtil patientRepositoryUtil,
                              IRoleService roleService,
                              IActRelationshipService actRelationshipService,
                              IEdxDocumentService edxDocumentService,
                              IAnswerService answerService,
                              ISrteCodeObsService srteCodeObsService,
                              IParticipationService participationService,
                              IProviderMatchingService providerMatchingService,
                              IPatientMatchingService patientMatchingService,
                              ObservationRepository observationRepository,
                              PersonRepository personRepository,
                              IJurisdictionService jurisdictionService,
                              EntityHelper entityHelper,
                              OdseIdGeneratorService odseIdGeneratorService,
                              ActRepository actRepository,
                              ObservationReasonRepository observationReasonRepository,
                              ActIdRepository actIdRepository,
                              ObservationInterpRepository observationInterpRepository,
                              ObsValueCodedRepository obsValueCodedRepository,
                              ObsValueTxtRepository obsValueTxtRepository,
                              ObsValueDateRepository obsValueDateRepository,
                              ObsValueNumericRepository obsValueNumericRepository,
                              ActLocatorParticipationRepository actLocatorParticipationRepository, OrganizationRepositoryUtil organizationRepositoryUtil) {

        this.observationMatchingService = observationMatchingService;
        this.nndActivityLogService = nndActivityLogService;
        this.messageLogService = messageLogService;
        this.observationUtil = observationUtil;
        this.notificationService = notificationService;
        this.materialService = materialService;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.roleService = roleService;
        this.actRelationshipService = actRelationshipService;
        this.edxDocumentService = edxDocumentService;
        this.answerService = answerService;
        this.srteCodeObsService = srteCodeObsService;
        this.participationService = participationService;
        this.providerMatchingService = providerMatchingService;
        this.patientMatchingService = patientMatchingService;
        this.observationRepository = observationRepository;
        this.personRepository = personRepository;
        this.jurisdictionService = jurisdictionService;
        this.entityHelper = entityHelper;
        this.odseIdGeneratorService = odseIdGeneratorService;
        this.actRepository = actRepository;
        this.observationReasonRepository = observationReasonRepository;
        this.actIdRepository = actIdRepository;
        this.observationInterpRepository = observationInterpRepository;
        this.obsValueCodedRepository = obsValueCodedRepository;
        this.obsValueTxtRepository = obsValueTxtRepository;
        this.obsValueDateRepository = obsValueDateRepository;
        this.obsValueNumericRepository = obsValueNumericRepository;
        this.actLocatorParticipationRepository = actLocatorParticipationRepository;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
    }

    private void checkMethodArgs(Long uid) throws DataProcessingException {
        if (uid == null)
        {
            throw new DataProcessingException("Method arguements of getXXXProxy() cannot be null, however," + "\n Act/Entity uid is: " + uid);
        }
    }


    public LabResultProxyContainer getLabResultToProxy(Long observationUid) throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = null;
        if (observationUid == null) {
            logger.error("HL7CommonLabUtil.getLabResultToProxy observationUid is null ");
            throw new DataProcessingException("LabResultProxyVO is null");
        }
        try {
            labResultProxyVO = getLabResultProxyVO(observationUid, true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new DataProcessingException("HL7CommonLabUtil.getLabResultToProxy The labResultProxyVO could not be retrieved. Please check.:" + e);
        }
        logger.info("HL7CommonLabUtil.getLabResultToProxy result returned.");
        return labResultProxyVO;
    }


    /**
     * Loading Existing Either Observation or Intervention
     * */
    private AbstractVO getActVO(String actType, Long anUid) throws DataProcessingException
    {
        AbstractVO obj = null;
        try
        {
            if (anUid != null)
            {
                if (actType.equalsIgnoreCase(NEDSSConstant.INTERVENTION_CLASS_CODE))
                {
                    //TODO: LOAD INTERVENTION
                   // obj = interventionRootDAOImpl.loadObject(anUid.longValue());
                }
                else if (actType.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE))
                {
                    obj = observationUtil.loadObject(anUid);
                }
            }
        }
        catch (Exception ex)
        {
            throw new DataProcessingException("Error while retrieving a " + actType + " value object. " + ex.toString());
        }

        return obj;
    }


    private List<Object> retrieveEntityForProxyVO(Collection<ParticipationDT> partColl) 
    {
        List<Object> allEntityHolder = new ArrayList<Object> ();

        //Retrieve associated persons
        Object[] obj = retrievePersonVOsForProxyVO(partColl);
        allEntityHolder.add(this.RETRIEVED_PERSONS_FOR_PROXY, (Collection<?>)obj[0]);

        //Retrieve associated organizations
        allEntityHolder.add(this.RETRIEVED_ORGANIZATIONS_FOR_PROXY, retrieveOrganizationVOsForProxyVO(partColl));

        //Retrieve associated materials
        allEntityHolder.add(this.RETRIEVED_MATERIALS_FOR_PROXY, retrieveMaterialVOsForProxyVO(partColl));
        Collection<?>  coll = (Collection<?>)obj[1];
        allEntityHolder.add(this.RETRIEVED_PATIENT_ROLES, coll);

        return allEntityHolder;
    }

    private Collection<Object>  retrieveOrganizationVOsForProxyVO(Collection<ParticipationDT> partColl)
    {
        Collection<Object>  theOrganizationVOCollection  = null;

        for (Iterator<ParticipationDT> it = partColl.iterator(); it.hasNext(); )
        {
            ParticipationDT partDT = (ParticipationDT) it.next();

            if (partDT == null)
            {
                continue;
            }

            String subjectClassCd = partDT.getSubjectClassCd();
            String recordStatusCd = partDT.getRecordStatusCd();
            String typeCd = partDT.getTypeCd();
            //If organization...
            if (subjectClassCd != null &&
                    subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR102_SUB_CD)
                    && recordStatusCd != null &&
                    recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE))
            {
                Long organizationUid = partDT.getSubjectEntityUid();
                if (theOrganizationVOCollection  == null)
                {
                    theOrganizationVOCollection  = new ArrayList<Object> ();
                }
                //TODO: LOAD ORGANIZATION
//                theOrganizationVOCollection.add(getEntityVO(NEDSSConstant.ORGANIZATION,
//                        organizationUid, partDT.getActUid(),
//                        securityObj));

                theOrganizationVOCollection.add(new OrganizationDT());
            }
        }
        return theOrganizationVOCollection;
    }

    private Collection<Object>  retrieveMaterialVOsForProxyVO(Collection<ParticipationDT> partColl)
    {
        Collection<Object>  theMaterialVOCollection  = null;

        for (Iterator<ParticipationDT> it = partColl.iterator(); it.hasNext(); )
        {
            ParticipationDT partDT = (ParticipationDT) it.next();

            if (partDT == null)
            {
                continue;
            }

            String subjectClassCd = partDT.getSubjectClassCd();
            String recordStatusCd = partDT.getRecordStatusCd();

            //If material...
            if (subjectClassCd != null &&
                    subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR104_SUB_CD)
                    && recordStatusCd != null &&
                    recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE))
            {
                Long materialUid = partDT.getSubjectEntityUid();
                if (theMaterialVOCollection  == null)
                {
                    theMaterialVOCollection  = new ArrayList<Object> ();
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
     * */
    private Object[] retrievePersonVOsForProxyVO(Collection<ParticipationDT> partColl)
    {
        Object[] obj = new Object[2];
        Collection<Object>  thePersonVOCollection  = new ArrayList<> ();
        Collection<Object>  patientRollCollection  = new ArrayList<> ();
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
                    && recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE)) {
                PersonContainer vo = patientRepositoryUtil.loadPerson(partDT.getSubjectEntityUid());
                thePersonVOCollection.add(vo);

                //If the person is a patient, do more...
                if (typeCd != null && typeCd.equalsIgnoreCase(NEDSSConstant.PAR110_TYP_CD)) {
                    if (vo.getTheRoleDtoCollection().size() > 0) {
                        patientRollCollection.addAll(vo.getTheRoleDtoCollection());
                    }
                    Collection<Object> scopedPersons = retrieveScopedPersons(vo.getThePersonDto().getPersonUid());
                    if (scopedPersons != null && scopedPersons.size() > 0) {
                        for (Object person : scopedPersons) {
                            PersonContainer scopedPerson = (PersonContainer) person;
                            if (scopedPerson.getTheRoleDtoCollection() != null && scopedPerson.getTheRoleDtoCollection().size() > 0) {
                                patientRollCollection.addAll(scopedPerson.getTheRoleDtoCollection());
                            }
                            thePersonVOCollection.add(scopedPerson);
                        }
                    }
                }
            }
        }
        obj[1] = patientRollCollection;
        obj[0] = thePersonVOCollection;
        return obj;
    }


    /**
     * Getting Person Role Giving the UID
     * */
    private Collection<Object>  retrieveScopedPersons(Long scopingUid)
    {
        Collection<RoleDto>  roleDTColl = roleService.findRoleScopedToPatient(scopingUid);
        Collection<Object>  scopedPersons = null;

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

    private List<Object> retrieveActForProxyVO(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException {
        List<Object> allActHolder = new ArrayList<Object> ();

        //Retrieve associated interventions
        allActHolder.add(this.RETRIEVED_INTERVENTIONS_FOR_PROXY, retrieveInterventionVOsForProxyVO(actRelColl));

        //Retrieve associated observations and performing labs of any resulted tests
        List<Object> obs_org = (List<Object>) retrieveObservationVOsForProxyVO(actRelColl);
        allActHolder.add(this.RETRIEVED_OBSERVATIONS_FOR_PROXY, obs_org.get(0));
        allActHolder.add(this.RETRIEVED_LABS_FOR_RT, obs_org.get(1));

        return allActHolder;
    }

    private Collection<Object>  retrieveObservationVOsForProxyVO(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException
    {
        List<Object> obs_org = new ArrayList<Object> ();
        Collection<Object>  theObservationVOCollection  = new ArrayList<Object> ();
        Collection<Object>  performingLabColl = new ArrayList<Object> ();

        for (Iterator<ActRelationshipDT> it = actRelColl.iterator(); it.hasNext(); )
        {
            ActRelationshipDT actRelDT = (ActRelationshipDT) it.next();

            if (actRelDT == null)
            {
                continue;
            }

            String typeCd = actRelDT.getTypeCd();
            String sourceClassCd = actRelDT.getSourceClassCd();
            String targetClassCd = actRelDT.getTargetClassCd();
            String recordStatusCd = actRelDT.getRecordStatusCd();

            //If observation...
            if (sourceClassCd != null &&
                    sourceClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && targetClassCd != null &&
                    targetClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && recordStatusCd != null &&
                    recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE))
            {
                Long observationUid = actRelDT.getSourceActUid();

                //If a processing decision observation
                if(typeCd!=null && typeCd.equals(NEDSSConstant.ACT_TYPE_PROCESSING_DECISION)){
                    ObservationVO processingDecObservationVO = (ObservationVO) getActVO(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);
                    theObservationVOCollection.add(processingDecObservationVO);
                }
                //If a Comments observation
                if (typeCd != null && typeCd.equalsIgnoreCase("APND"))
                {
                    ObservationVO ordTestCommentVO = (ObservationVO)getActVO(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);

                    theObservationVOCollection.add(ordTestCommentVO);
                    Collection<ActRelationshipDT>  arColl = ordTestCommentVO.getTheActRelationshipDTCollection();
                    if(arColl != null){
                        Iterator<ActRelationshipDT>  arCollIter = arColl.iterator();
                        while(arCollIter.hasNext()){
                            ActRelationshipDT ordTestDT = (ActRelationshipDT)arCollIter.next();
                            if(ordTestDT.getTypeCd().equals("COMP")){
                                //add the resulted test to the collection
                                ObservationVO resTestVO = (ObservationVO) getActVO(NEDSSConstant.OBSERVATION_CLASS_CODE, ordTestDT.getSourceActUid());

                                //BB - civil0012298 - Retrieve User Name to be displayed instead of ID
                                resTestVO.getTheObservationDT().setAddUserName("TEST123");

                                theObservationVOCollection.add(resTestVO);

                            }
                        }

                    }
                }
                //If a Resulted Test observation
                else if (typeCd != null &&
                        typeCd.equalsIgnoreCase(NEDSSConstant.ACT108_TYP_CD))
                {
                    ObservationVO rtObservationVO = (ObservationVO) getActVO(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);
                    if (rtObservationVO == null)
                    {
                        continue;
                    }
                    theObservationVOCollection.add(rtObservationVO); //The Resulted Test itself
                    //Retrieve the RT's lab
                    OrganizationVO rtPerformingLab = retrievePerformingLab(rtObservationVO.getTheParticipationDTCollection());
                    if (rtPerformingLab != null)
                    {
                        performingLabColl.add(rtPerformingLab);
                    }


                    //Retrieves all reflex observations, including each ordered and its resulted
                    Collection<Object>  reflexObsColl = retrieveReflexObservations(rtObservationVO.getTheActRelationshipDTCollection());
                    if (reflexObsColl == null || reflexObsColl.size() <= 0)
                    {
                        continue;
                    }
                    theObservationVOCollection.addAll(reflexObsColl);
                }
            }
        }

        obs_org.add(0, theObservationVOCollection);
        obs_org.add(1, performingLabColl);
        return obs_org;
    }

    /**
     * Retrieves the Reflex Observations
     */
    private Collection<Object>  retrieveReflexObservations(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException
    {
        Collection<Object>  reflexObsVOCollection  = null;

        for (Iterator<ActRelationshipDT> it = actRelColl.iterator(); it.hasNext(); )
        {
            ActRelationshipDT actRelDT = (ActRelationshipDT) it.next();

            if (actRelDT == null)
            {
                continue;
            }

            String typeCd = actRelDT.getTypeCd();
            String sourceClassCd = actRelDT.getSourceClassCd();
            String targetClassCd = actRelDT.getTargetClassCd();
            String recordStatusCd = actRelDT.getRecordStatusCd();

            //If reflex ordered test observation...
            if (typeCd != null &&
                    typeCd.equalsIgnoreCase(NEDSSConstant.ACT109_TYP_CD)
                    && sourceClassCd != null &&
                    sourceClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && targetClassCd != null &&
                    targetClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && recordStatusCd != null &&
                    recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE))
            {
                Long observationUid = actRelDT.getSourceActUid();

                ObservationVO reflexObs = (ObservationVO) getActVO(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);

                if (reflexObs == null)
                {
                    continue;
                }
                else
                {
                    if(reflexObsVOCollection  == null) reflexObsVOCollection  = new ArrayList<Object> ();
                    reflexObsVOCollection.add(reflexObs);
                }

                //Retrieves its associated reflex resulted tests
                Collection<Object>  reflexRTs = retrieveReflexRTs(reflexObs.getTheActRelationshipDTCollection());
                if (reflexRTs == null || reflexRTs.size() < 0)
                {
                    continue;
                }
                reflexObsVOCollection.addAll(reflexRTs);
            }
        }
        return reflexObsVOCollection;
    }


    /**
     * Retrieves the Reflex Result Test
     */
    private Collection<Object>  retrieveReflexRTs(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException
    {
        Collection<Object>  reflexRTCollection  = null;

        for (Iterator<ActRelationshipDT> it = actRelColl.iterator(); it.hasNext(); )
        {
            ActRelationshipDT actRelDT = (ActRelationshipDT) it.next();

            if (actRelDT == null)
            {
                continue;
            }

            String typeCd = actRelDT.getTypeCd();
            String sourceClassCd = actRelDT.getSourceClassCd();
            String targetClassCd = actRelDT.getTargetClassCd();
            String recordStatusCd = actRelDT.getRecordStatusCd();

            //If reflex resulted test observation...
            if (typeCd != null &&
                    typeCd.equalsIgnoreCase(NEDSSConstant.ACT110_TYP_CD)
                    && sourceClassCd != null &&
                    sourceClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && targetClassCd != null &&
                    targetClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && recordStatusCd != null &&
                    recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE))
            {
                Long observationUid = actRelDT.getSourceActUid();

                ObservationVO reflexObs = (ObservationVO) getActVO(NEDSSConstant.OBSERVATION_CLASS_CODE, observationUid);

                if (reflexObs == null)
                {
                    continue;
                }
                if(reflexRTCollection  == null) reflexRTCollection  = new ArrayList<Object> ();
                reflexRTCollection.add(reflexObs);
            }
        }
        return reflexRTCollection;
    }



    // LOAD the performing  lab
    private OrganizationVO retrievePerformingLab(Collection<ParticipationDT> partColl) throws DataProcessingException
    {
        OrganizationVO lab = null;

        for (Iterator<ParticipationDT> it = partColl.iterator(); it.hasNext(); )
        {
            ParticipationDT partDT = (ParticipationDT) it.next();

            if (partDT == null)
            {
                continue;
            }

            String typeCd = partDT.getTypeCd();
            String subjectClassCd = partDT.getSubjectClassCd();
            String actClassCd = partDT.getActClassCd();
            String recordStatusCd = partDT.getRecordStatusCd();

            //If performing lab...
            if (typeCd != null && typeCd.equals(NEDSSConstant.PAR122_TYP_CD)
                    && subjectClassCd != null &&
                    subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR122_SUB_CD)
                    && actClassCd != null &&
                    actClassCd.equals(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && recordStatusCd != null &&
                    recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE))
            {
                Long organizationUid = partDT.getSubjectEntityUid();

                // TODO LOAD ORG and it assoc

                /*
                lab = (OrganizationVO) getEntityVO(NEDSSConstant.ORGANIZATION,
                        organizationUid, partDT.getActUid(),
                        securityObj);
                */

                lab = new OrganizationVO();
                break; //only one lab for each RT
            }
        }
        return lab;
    }




    private Collection<Object>  retrieveInterventionVOsForProxyVO(Collection<ActRelationshipDT> actRelColl) throws DataProcessingException
    {
        Collection<Object>  theInterventionVOCollection  = null;

        for (Iterator<ActRelationshipDT> it = actRelColl.iterator(); it.hasNext(); )
        {
            ActRelationshipDT actRelDT = (ActRelationshipDT) it.next();

            if (actRelDT == null)
            {
                continue;
            }

            String sourceClassCd = actRelDT.getSourceClassCd();
            String targetClassCd = actRelDT.getTargetClassCd();
            String recordStatusCd = actRelDT.getRecordStatusCd();

            //If intervention...
            if (sourceClassCd != null &&
                    sourceClassCd.equalsIgnoreCase(NEDSSConstant.INTERVENTION_CLASS_CODE)
                    && targetClassCd != null &&
                    targetClassCd.equalsIgnoreCase(NEDSSConstant.OBSERVATION_CLASS_CODE)
                    && recordStatusCd != null &&
                    recordStatusCd.equalsIgnoreCase(NEDSSConstant.ACTIVE))
            {
                Long interventionUid = actRelDT.getSourceActUid();
                if (theInterventionVOCollection  == null)
                {
                    theInterventionVOCollection  = new ArrayList<Object> ();
                }
                theInterventionVOCollection.add(getActVO(NEDSSConstant.INTERVENTION_CLASS_CODE, interventionUid));
            }
        }
        return theInterventionVOCollection;
    }




    // public LabResultProxyVO getLabResultProxyVO(Long observationId,  boolean isELR, NBSSecurityObj nbsSecurityObj)throws Exceptio
    public LabResultProxyContainer getLabResultProxyVO(Long observationId,  boolean isELR) throws DataProcessingException {
        LabResultProxyContainer lrProxyVO =  new LabResultProxyContainer();

        checkMethodArgs(observationId);

        // LOADING EXISTING Observation
        ObservationVO orderedTest = (ObservationVO) getActVO(NEDSSConstant.OBSERVATION_CLASS_CODE, observationId);

        /**Check permission*/
        // checkPermissionToGetProxy(orderedTest);

        Collection<ParticipationDT>  partColl = orderedTest.getTheParticipationDTCollection();
        if (partColl != null && partColl.size() > 0)
        {
            List<Object> allEntity = retrieveEntityForProxyVO(partColl);
            if (allEntity != null && allEntity.size() > 0)
            {
                lrProxyVO.setThePersonContainerCollection( (Collection<PersonContainer>) allEntity.get(this.RETRIEVED_PERSONS_FOR_PROXY));
                lrProxyVO.setTheOrganizationVOCollection( (Collection<OrganizationVO>) allEntity.get(this.RETRIEVED_ORGANIZATIONS_FOR_PROXY));
                lrProxyVO.setTheMaterialVOCollection( (Collection<MaterialVO>) allEntity.get(this.RETRIEVED_MATERIALS_FOR_PROXY));

                Object obj = allEntity.get(this.RETRIEVED_PATIENT_ROLES);
                Collection<RoleDto>  coll = null;
                if(obj == null)
                    coll = new ArrayList<> ();//do not want to place a null object in  for role collection
                else
                    coll = (Collection<RoleDto>)obj;

                lrProxyVO.setTheRoleDtoCollection(coll);
            }
        }

        Collection<ActRelationshipDT>  actRelColl = orderedTest.getTheActRelationshipDTCollection();

        if (actRelColl != null && actRelColl.size() > 0)
        {
            List<Object> allAct = retrieveActForProxyVO(actRelColl);
            if (allAct != null && allAct.size() > 0)
            {
                //Set intervention collection
                lrProxyVO.setTheInterventionVOCollection( (Collection<Object>) allAct.get(this.RETRIEVED_INTERVENTIONS_FOR_PROXY));

                //Set observation collection
                Collection<ObservationVO>  obsColl = (Collection<ObservationVO>) allAct.get(this.RETRIEVED_OBSERVATIONS_FOR_PROXY);
                if (obsColl == null)
                {
                    obsColl = new ArrayList<ObservationVO> ();
                }

                //BB - civil0012298 - Retrieve User Name to b displayed instead of ID!
                if(!isELR) {
                    orderedTest.getTheObservationDT().setAddUserName("TEST123");
                    orderedTest.getTheObservationDT().setLastChgUserName("TEST123");
                }

                obsColl.add(orderedTest);
                lrProxyVO.setTheObservationVOCollection(obsColl);

                //Adds the performing lab(if any) to the organization cellection
                Collection<OrganizationVO>  labColl = (Collection<OrganizationVO>) allAct.get(this.RETRIEVED_LABS_FOR_RT);
                if (labColl != null && labColl.size() > 0)
                {
                    lrProxyVO.getTheOrganizationVOCollection().addAll(labColl);
                }
            }
        }

        if (!isELR) {
            try {

                boolean exists = notificationService.checkForExistingNotification(lrProxyVO);
                lrProxyVO.setAssociatedNotificationInd(exists);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Collection<ActRelationshipDT> col = actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(observationId, NEDSSConstant.LAB_REPORT);
                if (col != null && col.size() > 0)
                {
                    lrProxyVO.setAssociatedInvInd(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // get EDX Document data
            try {
                Collection<EDXDocumentDT> documentList = edxDocumentService.selectEdxDocumentCollectionByActUid(observationId);
                if (documentList != null) {
                    lrProxyVO.setEDXDocumentCollection(documentList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // get the list of conditions associated with this Lab
            try {
                ArrayList<String> conditionList = deriveTheConditionCodeList(lrProxyVO, orderedTest);
                if (conditionList != null && !conditionList.isEmpty()) {
                    lrProxyVO.setTheConditionsList(conditionList);
                }

            } catch (Exception e) {
                e.printStackTrace();
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

    /**
     * deriveTheConditionCodeList - used by Associate to Investigations
     *    when associating an STD lab to a closed investigation.
     *    Condition list determines the Processing Decision to show.
     */
    private ArrayList<String> deriveTheConditionCodeList(
            LabResultProxyContainer labResultProxyVO, ObservationVO orderTest) throws DataProcessingException {
        ArrayList<String> derivedConditionList = new ArrayList<String>();

        //if this is not an STD Program Area - we can skip this overhead
        //TODO: CACHING
//        String programAreaCd = orderTest.getTheObservationDT().getProgAreaCd();
//        if ((programAreaCd == null) || (!PropertyUtil.isStdOrHivProgramArea(programAreaCd))) {
//            return derivedConditionList;
//        }

        // Get the result tests
        Collection<ObservationVO> resultTests = new ArrayList<ObservationVO>();
        for (ObservationVO obsVO : labResultProxyVO.getTheObservationVOCollection()) {
            String obsDomainCdSt1 = obsVO.getTheObservationDT().getObsDomainCdSt1();
            if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD)) {
                resultTests.add(obsVO);
            }
        }

        // Get the reporting lab clia
        String reportingLabCLIA = "";
        if (labResultProxyVO.getLabClia() != null && labResultProxyVO.isManualLab()){
            reportingLabCLIA = labResultProxyVO.getLabClia();
        }
        else {
            if (orderTest.getTheParticipationDTCollection() != null) {
                reportingLabCLIA = getReportingLabCLIAId(orderTest.getTheParticipationDTCollection());
            }
        }
        if (reportingLabCLIA == null || reportingLabCLIA.trim().equals(""))
            reportingLabCLIA = NEDSSConstant.DEFAULT;

        // If there are resulted tests, call obs processor for the list of
        // associated conditions
        // found in the various lab test SRT tables
        if (resultTests.size() > 0) {
            derivedConditionList = getDerivedConditionList(reportingLabCLIA, resultTests, orderTest.getTheObservationDT().getElectronicInd());
        }

        return derivedConditionList;
    }

    private ArrayList<String> getDerivedConditionList(String reportingLabCLIA,
                                                     Collection<ObservationVO> observationVOCollection,
                                                      String electronicInd) throws DataProcessingException {
        int noConditionFoundForResultedTestCount = 0;
        ArrayList<String> returnList =  new ArrayList<String> ();

        Iterator<ObservationVO> obsIt = observationVOCollection.iterator();
        // iterator through each resultTest
        while (obsIt.hasNext()) {
            ArrayList<String> resultedTestConditionList =  new ArrayList<String> ();
            ObservationVO obsVO = (ObservationVO) obsIt.next();
            ObservationDT obsDt = obsVO.getTheObservationDT();

            String obsDomainCdSt1 = obsDt.getObsDomainCdSt1();
            String obsDTCode = obsDt.getCd();

            // make sure you are dealing with a resulted test here.
            if ((obsDomainCdSt1 != null)
                    && obsDomainCdSt1.equals(ELRConstant.ELR_OBSERVATION_RESULT)
                    && (obsDTCode != null)
                    && (!obsDTCode.equals(NEDSSConstant.ACT114_TYP_CD))) {

                // Retrieve Condition List using SNM Lab Result --> SNOMED code mapping
                // If ELR, use actual CLIA - if manual use "DEFAULT" as CLIA
                if (electronicInd.equals(NEDSSConstant.ELECTRONIC_IND_ELR))
                {
                    resultedTestConditionList = getConditionsFromSNOMEDCodes(reportingLabCLIA, obsVO.getTheObsValueCodedDTCollection());
                }
                else
                {
                    resultedTestConditionList = getConditionsFromSNOMEDCodes(NEDSSConstant.DEFAULT, obsVO.getTheObsValueCodedDTCollection());
                }

                // if no conditions found - try LN to retrieve Condition using Resulted Test --> LOINC mapping
                if (resultedTestConditionList.isEmpty()) {
                    String loincCondition = getConditionForLOINCCode(reportingLabCLIA, obsVO);
                    if (loincCondition!= null && !loincCondition.isEmpty())
                    {
                        resultedTestConditionList.add(loincCondition);
                    }
                }

                // none - try LR to retrieve default Condition using Local Result Code to condition mapping
                if (resultedTestConditionList.isEmpty()) {
                    String localResultDefaultConditionCd = getConditionCodeForLocalResultCode(reportingLabCLIA, obsVO.getTheObsValueCodedDTCollection());
                    if (localResultDefaultConditionCd != null && !localResultDefaultConditionCd.isEmpty())
                    {
                        resultedTestConditionList.add(localResultDefaultConditionCd);
                    }
                }
                // none - try LT to retrieve default Condition using Local Test Code to condition mapping
                if (resultedTestConditionList.isEmpty()) {
                    String localTestDefaultConditionCd = getConditionCodeForLocalTestCode(reportingLabCLIA, obsVO);
                    if (localTestDefaultConditionCd != null && !localTestDefaultConditionCd.isEmpty())
                    {
                        resultedTestConditionList.add(localTestDefaultConditionCd);
                    }
                }
                // none - see if default condition code exists for the resulted lab test
                if (resultedTestConditionList.isEmpty()) {
                    String defaultLabTestConditionCd = getDefaultConditionForLabTestCode(obsDTCode, reportingLabCLIA);
                    if (defaultLabTestConditionCd != null && !defaultLabTestConditionCd.isEmpty())
                    {
                        resultedTestConditionList.add(defaultLabTestConditionCd);
                    }
                }
                if (resultedTestConditionList.isEmpty()) {
                    noConditionFoundForResultedTestCount = noConditionFoundForResultedTestCount + 1;
                }
                //if we found conditions add them to the return list
                if (!resultedTestConditionList.isEmpty()) {
                    Set<String> hashset = new HashSet<String>();
                    hashset.addAll(returnList);
                    hashset.addAll(resultedTestConditionList);
                    //get rid of dups..
                    returnList = new ArrayList<String>(hashset);
                } //resulted test condition list not empty
            } //end of if valid resulted test
        } // end of while more resulted tests
        //if we couldn't derive a condition for a test, return no conditions
        if (noConditionFoundForResultedTestCount > 0)
        {
            returnList.clear(); //incomplete list - return empty list
        }

        return returnList;
    } // end of ConditionList

    /**
     * Returns a List of Condition Codes associated with the passed Snomed codes.
     *
     * @param reportingLabCLIA : String
     * @param obsValueCodedDtColl : Collection
     * @return ArrayList<string>
     */
    // AK - 7/25/04
    private ArrayList<String> getConditionsFromSNOMEDCodes(
            String reportingLabCLIA, Collection<ObsValueCodedDT> obsValueCodedDtColl) throws DataProcessingException {

        ArrayList<String> snomedConditionList = new ArrayList<String>();

        if (obsValueCodedDtColl != null) {
            Iterator<ObsValueCodedDT> codedDtIt = obsValueCodedDtColl.iterator();
            while (codedDtIt.hasNext()) {
                String snomedCd = "";
                String conditionCd = "";
                ObsValueCodedDT codedDt = (ObsValueCodedDT) codedDtIt.next();
                String codeSystemCd = codedDt.getCodeSystemCd();

                if (codeSystemCd == null || codeSystemCd.trim().equals(""))
                {
                    continue;
                }

                String obsCode = codedDt.getCode();
                if (obsCode == null || obsCode.trim().equals(""))
                {
                    continue;
                }

                /* If the code is not a Snomed code, try to get the snomed code.
                 * Check if ObsValueCodedDT.codeSystemCd='L' and CLIA for
                 * Reporting Lab is available, find the Snomed code for
                 * ObsValueCodedDT.code(Local Result to Snomed lookup)
                 */
                if (!codeSystemCd.equals(ELRConstant.ELR_SNOMED_CD)) {
                    Map<String, Object> snomedMap =  srteCodeObsService.getSnomed(codedDt.getCode(), ELRConstant.TYPE, reportingLabCLIA);

                    if(snomedMap.containsKey("COUNT") && (Integer) snomedMap.get("COUNT") == 1) {
                        snomedCd = (String) snomedMap.get("LOINC");
                    }
                    else
                    {
                        continue;
                    }
                }

                /*
                 * If already coded using SNOMED code, just add it to the return
                 * array. check if ObsValueCodedDT.codeSystemCd="SNM", use
                 * ObsValueCodedDT.code for Snomed
                 */
                else if (codeSystemCd.equals("SNM")) {
                    snomedCd = obsCode;
                }

                //if these is a Snomed code, see if we can get a corresponding condition for it
                try {
                    conditionCd = srteCodeObsService.getConditionForSnomedCode(snomedCd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (conditionCd != null && !conditionCd.isEmpty())
                {
                    snomedConditionList.add(conditionCd);
                }
            } // end of while has next
        } // end if collection not null
        return snomedConditionList;
    } //getConditionsFromSNOMEDCodes()

    private String getConditionForLOINCCode(String reportingLabCLIA, ObservationVO resultTestVO) throws DataProcessingException {

        String loincCd = "";
        ObservationDT obsDt = resultTestVO.getTheObservationDT();
        if (obsDt == null || reportingLabCLIA == null)
        {
            return null;
        }

        String cdSystemCd = obsDt.getCdSystemCd();
        if (cdSystemCd == null || cdSystemCd.trim().equals(""))
        {
            return null;
        }

        String obsCode = obsDt.getCd();
        if (obsCode == null || obsCode.trim().equals(""))
        {
            return null;
        }

        if (cdSystemCd.equals(ELRConstant.ELR_OBSERVATION_LOINC)) {
            loincCd = obsCode;
        }
        else
        {
            Map<String, Object> snomedMap =  srteCodeObsService.getSnomed(obsCode, "LT", reportingLabCLIA);

            if(snomedMap.containsKey("COUNT") && (Integer) snomedMap.get("COUNT") == 1) {
                loincCd = (String) snomedMap.get("LOINC");
            }
        }

        // If we have resolved the LOINC code, try to derive the condition
        if (loincCd == null || loincCd.isEmpty())
        {
            return loincCd;
        }

        String conditionCd = "";
        try {
            conditionCd = srteCodeObsService.getConditionForLoincCode(loincCd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (conditionCd);

    } //end of getConditionForLoincCode()

    /**
     * Gets the default condition for a Local Result code.
     * If we find that it maps to more than one condition code, return nothing.
     * @param reportingLabCLIA : String
     * @param obsValueCodedDtColl: Collection
     * @return conditionCd : String
     */
    private String getConditionCodeForLocalResultCode(String reportingLabCLIA, Collection<ObsValueCodedDT> obsValueCodedDtColl) {
        String conditionCd = "";
        HashMap<String, String> conditionMap = new HashMap<String,String>();
        if (obsValueCodedDtColl == null || reportingLabCLIA == null)
        {
            return null;
        }

        Iterator<ObsValueCodedDT> codedDtIt = obsValueCodedDtColl.iterator();
        while (codedDtIt.hasNext()) {
            ObsValueCodedDT obsValueCodedDT = (ObsValueCodedDT) codedDtIt.next();
            String code = obsValueCodedDT.getCode();
            //String codeSystemCd = obsValueCodedDT.getCodeSystemCd();
            if (code != null) {
                String defaultCondition = srteCodeObsService.getDefaultConditionForLocalResultCode(code, reportingLabCLIA);
                if (defaultCondition != null && !defaultCondition.isEmpty()) {
                    conditionCd = defaultCondition;
                    conditionMap.put(defaultCondition, code);
                }
            }
        }
        if (conditionMap.size() > 1 || conditionMap.isEmpty())
        {
            return("");
        }
        else {
            return(conditionCd);
        }
    }

    /**
     * Gets the default condition for the Local Test code.
     * @param resultTestVO : Collection
     * @param reportingLabCLIA : String
     * @return conditionCd : String
     */
    private String getConditionCodeForLocalTestCode(String reportingLabCLIA,
                                                   ObservationVO resultTestVO) {

        //edit checks
        if (reportingLabCLIA == null || resultTestVO == null)
        {
            return null;
        }
        ObservationDT obsDt = resultTestVO.getTheObservationDT();
        if (obsDt.getCd() == null || obsDt.getCd().equals("") || obsDt.getCd().equals(" ") || obsDt.getCdSystemCd() == null)
        {
            return null;
        }

        String testCd = obsDt.getCd();
        String conditionCd = srteCodeObsService.getDefaultConditionForLocalResultCode(testCd, reportingLabCLIA);
        return (conditionCd);
    } //getConditionCodeForLocalTestCode()

    /**
     * Gets the default condition for the Lab Test code.
     * @return conditionCd : String
     */
    private String getDefaultConditionForLabTestCode(String labTestCd, String reportingLabCLIA) {
        String conditionCd = srteCodeObsService.getDefaultConditionForLabTest(labTestCd, reportingLabCLIA );
        //see if the DEFAULT is set for the lab test if still not found..
        if ((conditionCd == null || conditionCd.isEmpty()) && !reportingLabCLIA.equals(NEDSSConstant.DEFAULT))
        {
            conditionCd = srteCodeObsService.getDefaultConditionForLabTest(labTestCd, NEDSSConstant.DEFAULT);
        }
        return(conditionCd);
    }


    private String getReportingLabCLIAId(Collection<ParticipationDT> partColl) throws DataProcessingException {


        // Get the reporting lab
        Long reportingLabUid = this.getUid(
                partColl,
                null,
                NEDSSConstant.ENTITY_UID_LIST_TYPE,
                NEDSSConstant.ORGANIZATION, NEDSSConstant.PAR111_TYP_CD,
                NEDSSConstant.PART_ACT_CLASS_CD,
                NEDSSConstant.RECORD_STATUS_ACTIVE);

        OrganizationVO reportingLabVO = null;
        try {
            if (reportingLabUid != null) {
                reportingLabVO = organizationRepositoryUtil.loadObject(reportingLabUid, null);
            }
        } catch (Exception rex) {
            throw new DataProcessingException("Error while retriving reporting organization vo, its uid is: " + reportingLabUid);
        }

        // Get the CLIA
        String reportingLabCLIA = null;

        if (reportingLabVO != null) {

            Collection<EntityIdDto> entityIdColl = reportingLabVO.getTheEntityIdDtoCollection();

            if (entityIdColl != null && entityIdColl.size() > 0) {
                for (Iterator<EntityIdDto> it = entityIdColl.iterator(); it.hasNext();) {
                    EntityIdDto idDT = (EntityIdDto) it.next();
                    if (idDT == null) {
                        continue;
                    }

                    String authoCd = idDT.getAssigningAuthorityCd();
                    String idTypeCd = idDT.getTypeCd();
                    if (authoCd == null || idTypeCd == null) {
                        continue;
                    }
                    if (authoCd.trim().contains(NEDSSConstant.REPORTING_LAB_CLIA) &&
                            idTypeCd.trim().equalsIgnoreCase(NEDSSConstant.REPORTING_LAB_FI_TYPE)) {
                        reportingLabCLIA = idDT.getRootExtensionTxt();
                        break;
                    }
                }
            } 
        }
        return reportingLabCLIA;
    }

    private Long getUid(Collection<ParticipationDT> participationDTCollection, Collection<ActRelationshipDT> actRelationshipDTCollection,
                        String uidListType, String uidClassCd, String uidTypeCd,
                        String uidActClassCd, String uidRecordStatusCd)
            throws DataProcessingException {
        Long anUid = null;
        try {
            if (participationDTCollection != null) {
                Iterator<ParticipationDT> assocIter = participationDTCollection.iterator();
                while (assocIter.hasNext()) {
                    ParticipationDT partDT = (ParticipationDT) assocIter.next();
                    if (
                            ((partDT.getSubjectClassCd() != null
                                    && partDT.getSubjectClassCd().equalsIgnoreCase(uidClassCd))
                                    && (partDT.getTypeCd() != null
                                    && partDT.getTypeCd().equalsIgnoreCase(uidTypeCd))
                                    && (partDT.getActClassCd() != null
                                    && partDT.getActClassCd().equalsIgnoreCase(uidActClassCd))
                                    && (partDT.getRecordStatusCd() != null
                                    && partDT.getRecordStatusCd().equalsIgnoreCase(uidRecordStatusCd)))
                    ) {
                        anUid = partDT.getSubjectEntityUid();
                    }
                }
            }
            else if (actRelationshipDTCollection != null) {
                Iterator<ActRelationshipDT> assocIter = actRelationshipDTCollection.iterator();
                while (assocIter.hasNext()) {
                    ActRelationshipDT actRelDT = (ActRelationshipDT) assocIter.next();
                    if (((actRelDT.getSourceClassCd() != null
                            && actRelDT.getSourceClassCd().equalsIgnoreCase(uidClassCd))
                            && (actRelDT.getTypeCd() != null
                            && actRelDT.getTypeCd().equalsIgnoreCase(uidTypeCd))
                            && (actRelDT.getTargetClassCd() != null
                            && actRelDT.getTargetClassCd().equalsIgnoreCase(uidActClassCd))
                            && (actRelDT.getRecordStatusCd() != null
                            && actRelDT.getRecordStatusCd().equalsIgnoreCase(uidRecordStatusCd))))
                    {
                        if (uidListType.equalsIgnoreCase(NEDSSConstant.ACT_UID_LIST_TYPE)) {
                            anUid = actRelDT.getTargetActUid();
                        } else if (uidListType.equalsIgnoreCase(NEDSSConstant.SOURCE_ACT_UID_LIST_TYPE)) {
                            anUid = actRelDT.getSourceActUid();
                        }
                    }
                }
            }

        } catch (Exception ex) {
            throw new DataProcessingException("Error while retrieving a " + uidListType + " uid. " + ex.toString());
        }

        return anUid;
    }

    @Transactional
    public ObservationDT processingObservation(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        String fillerNumber = "";
        try {
            ObservationVO observationVO = edxLabInformationDto.getRootObservationVO();
            fillerNumber = edxLabInformationDto.getFillerNumber();

            ObservationDT obsDT = null;

            if (edxLabInformationDto.getRootObservationVO() != null) {
                obsDT = observationMatchingService.matchingObservation(edxLabInformationDto);
            } else {
                logger.error("Error!! masterObsVO not available for fillerNbr:" + edxLabInformationDto.getFillerNumber());
                return null;
            }

            if (obsDT != null) // find a match is it a correction?
            {
                String msgStatus = observationVO.getTheObservationDT().getStatusCd();
                String odsStatus = obsDT.getStatusCd();
                if (msgStatus == null || odsStatus == null) {
                    logger.error("Error!! null status cd: msgInObs status=" + msgStatus + " odsObs status=" + odsStatus);
                    return null;
                }
                if ((odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_NEW)
                        && (msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_NEW)
                        || msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
                        || msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)))
                        || (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
                        && (msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
                        || msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)))
                        || (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)
                        && msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED))
                ) {
                    if (obsDT.getActivityToTime() != null && obsDT.getActivityToTime().after(edxLabInformationDto.getRootObservationVO().getTheObservationDT().getActivityToTime())) {
                        edxLabInformationDto.setActivityTimeOutOfSequence(true);
                        edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                        edxLabInformationDto.setLocalId(obsDT.getLocalId());
                        throw new DataProcessingException("An Observation Lab test match was found for Accession # " + fillerNumber + ", but the activity time is out of sequence.");
                    }
                    return obsDT;
                } else if (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)
                        && msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)) {
                    edxLabInformationDto.setFinalPostCorrected(true);
                    edxLabInformationDto.setLocalId(obsDT.getLocalId());
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                    throw new DataProcessingException("Lab report " + obsDT.getLocalId() + " was not updated. Final report with Accession # " + fillerNumber + " was sent after a corrected report was received.");
                } else if (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
                        && msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_NEW)) {
                    edxLabInformationDto.setPreliminaryPostFinal(true);
                    edxLabInformationDto.setLocalId(obsDT.getLocalId());
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                    throw new DataProcessingException("Lab report " + obsDT.getLocalId() + " was not updated. Preliminary report with Accession # " + fillerNumber + " was sent after a final report was received.");
                } else if (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)
                        && msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_NEW)) {
                    edxLabInformationDto.setPreliminaryPostCorrected(true);
                    edxLabInformationDto.setLocalId(obsDT.getLocalId());
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                    throw new DataProcessingException("Lab report " + obsDT.getLocalId() + " was not updated. Preliminary report with Accession # " + fillerNumber + " was sent after a corrected report was received.");
                } else {
                    edxLabInformationDto.setFinalPostCorrected(true);
                    edxLabInformationDto.setLocalId(obsDT.getLocalId());
                    logger.error(" Error!! Invalid status combination: msgInObs status=" + msgStatus + " odsObs status=" + odsStatus);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                    throw new DataProcessingException("Lab report " + obsDT.getLocalId() + " was not updated. Final report with Accession # " + fillerNumber + " was sent after a corrected report was received.");
                }
            }
        } catch(Exception e){
            throw new DataProcessingException("ERROR");
        }
        return null;
    }

    @Transactional
    public ObservationDT sendLabResultToProxy(LabResultProxyContainer labResultProxyContainer) throws DataProcessingException {
        ObservationDT obsDT = null;
        if (labResultProxyContainer == null) {
            logger.error("HL7CommonLabUtil.sendLabResultToProxy labResultProxyVO is null ");
            throw new DataProcessingException("LabResultProxyVO is null");
        } else {
            labResultProxyContainer.setItNew(true);
            labResultProxyContainer.setItDirty(false);
        }

        try {


            Map<Object, Object> returnMap = setLabResultProxy(labResultProxyContainer);

            obsDT = (ObservationDT)returnMap.get(NEDSSConstant.SETLAB_RETURN_OBSDT);
            logger.info("odsObsLocalId: " + obsDT.getLocalId());

        } catch (Exception e) {
            throw new DataProcessingException("HL7CommonLabUtil.sendLabResultToProxy The labResultProxyVO could not be saved"+ e.getMessage());
        }
        logger.info("Sent LabResultProxyVO to Observation Proxy - ODS Observation uid=" + obsDT.getObservationUid());
        return obsDT;

    }

    private Map<Object, Object> setLabResultProxy(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {
        try {
            NNDActivityLogDT nndActivityLogDT = null;

            //saving LabResultProxyVO before updating auto resend notifications
            Map<Object, Object> returnVal = this.setLabResultProxyWithoutNotificationAutoResend(labResultProxyVO);



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
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private Map<Object, Object> setLabResultProxyWithoutNotificationAutoResend(LabResultProxyContainer labResultProxyVO) throws DataProcessingException {
        //Before doing anything
        //TODO: Verify this check
       // checkMethodArgs(labResultProxyVO);

        //Set flag for type of processing
        boolean ELR_PROCESSING = false;

        //TODO: Assigned whatever user DI will be using here
        if (AuthUtil.authUser != null && AuthUtil.authUser.getUserId().equals(NEDSSConstant.ELR_LOAD_USER_ACCOUNT))
        {
            ELR_PROCESSING = true;
        }

        //TODO: Verify this check lab result perm
        //Check permission to proceed
        //checkPermissionToSetProxy(labResultProxyVO, securityObj, ELR_PROCESSING);

        //All well to proceed
        Map<Object, Object> returnVal = null;
        Long falseUid = null;
        Long realUid = null;
        boolean valid = false;


        try
        {

            //Process PersonVOCollection  and adds the patient mpr uid to the return
            Long patientMprUid = processLabPersonVOCollection(labResultProxyVO);
            if (patientMprUid != null)
            {
                if (returnVal == null)
                {
                    returnVal = new HashMap<Object, Object>();
                }
                returnVal.put(NEDSSConstant.SETLAB_RETURN_MPR_UID, patientMprUid);
            }

            //ObservationVOCollection
            Map<Object, Object> obsResults = processObservationVOCollection(labResultProxyVO, ELR_PROCESSING);
            if (obsResults != null)
            {
                if (returnVal == null)
                {
                    returnVal = new HashMap<Object, Object>();
                }
                returnVal.putAll(obsResults);
            }

            //TODO: REVALUATING THIS, this one try to find Patient UID, the code above is also doing this
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
            OrganizationVO organizationVO = null;
            if (labResultProxyVO.getTheOrganizationVOCollection() != null)
            {
                for (Iterator<OrganizationVO> anIterator = labResultProxyVO.getTheOrganizationVOCollection().iterator(); anIterator.hasNext(); )
                {
                    organizationVO = (OrganizationVO) anIterator.next();
                    OrganizationDT newOrganizationDT = null;

                    if (organizationVO.isItNew())
                    {
                        //TODO: EVALUDATE THIS prepare method
                        /*
                        newOrganizationDT = (OrganizationDT) prepareVOUtils.prepareVO(
                                organizationVO.getTheOrganizationDT(), NBSBOLookup.ORGANIZATION,
                                NEDSSConstant.ORG_CR, DataTables.ORGANIZATION_TABLE,
                                NEDSSConstant.BASE, securityObj);
                        */

//                        logger.debug("new organizationUID: " + newOrganizationDT.getOrganizationUid());
//                        organizationVO.setTheOrganizationDT(newOrganizationDT);
                        falseUid = organizationVO.getTheOrganizationDT().getOrganizationUid();
                        logger.debug("false organizationUID: " + falseUid);

                        realUid = organizationRepositoryUtil.setOrganization(organizationVO, null);
                        if (falseUid.intValue() < 0)
                        {
                            setFalseToNew(labResultProxyVO, falseUid, realUid);
                        }
                    }
                    else if (organizationVO.isItDirty())
                    {
                        //TODO: EVALUATE
//                        newOrganizationDT = (OrganizationDT) prepareVOUtils.prepareVO(
//                                organizationVO.getTheOrganizationDT(), NBSBOLookup.ORGANIZATION,
//                                NEDSSConstant.ORG_EDIT, DataTables.ORGANIZATION_TABLE,
//                                NEDSSConstant.BASE);

                        organizationVO.setTheOrganizationDT(newOrganizationDT);

                        realUid = organizationRepositoryUtil.setOrganization(organizationVO, null);
                        logger.debug("exisiting but updated organization's UID: " + realUid);
                    }
                }
            }

            //MaterialCollection
            MaterialVO materialVO = null;
            if (labResultProxyVO.getTheMaterialVOCollection() != null)
            {
                for (Iterator<MaterialVO> anIterator = labResultProxyVO.getTheMaterialVOCollection().iterator(); anIterator.hasNext(); )
                {
                    materialVO = (MaterialVO) anIterator.next();
                    MaterialDT newMaterialDT = null;
                    logger.debug("materialUID: " + materialVO.getTheMaterialDT().getMaterialUid());

                    if (materialVO.isItNew())
                    {
                        //TODO: EVALUATE
//                        newMaterialDT = (MaterialDT) prepareVOUtils.prepareVO(materialVO.
//                                        getTheMaterialDT(), NBSBOLookup.MATERIAL,
//                                NEDSSConstant.MAT_MFG_CR, DataTables.MATERIAL_TABLE,
//                                NEDSSConstant.BASE);
//                        logger.debug("new materialUID: " + newMaterialDT.getMaterialUid());
//                        materialVO.setTheMaterialDT(newMaterialDT);
                        falseUid = materialVO.getTheMaterialDT().getMaterialUid();
                        logger.debug("false materialUID: " + falseUid);
                        //TODO: INSERTION
                        realUid = materialService.saveMaterial(materialVO);
                        if (falseUid.intValue() < 0)
                        {
                            setFalseToNew(labResultProxyVO, falseUid, realUid);
                        }
                    }
                    else if (materialVO.isItDirty())
                    {
                        //TODO: EVALUATE
//                        newMaterialDT = (MaterialDT) prepareVOUtils.prepareVO(materialVO.
//                                        getTheMaterialDT(), NBSBOLookup.MATERIAL,
//                                NEDSSConstant.MAT_MFG_EDIT, DataTables.MATERIAL_TABLE,
//                                NEDSSConstant.BASE);
//                        materialVO.setTheMaterialDT(newMaterialDT);

                        //TODO: INSERTION
                        realUid = materialService.saveMaterial(materialVO);
                        logger.debug("exisiting but updated material's UID: " + realUid);
                    }
                }
            }

            //ParticipationCollection
            if (labResultProxyVO.getTheParticipationDTCollection() != null)
            {
                logger.debug("Iniside participation Collection<Object>  Loop - Lab");
                for (Iterator<ParticipationDT> anIterator = labResultProxyVO.
                        getTheParticipationDTCollection().iterator(); anIterator.hasNext(); )
                {

                    logger.debug("Inside loop size of participations: " +
                            labResultProxyVO.getTheParticipationDTCollection().size());
                    ParticipationDT participationDT = (ParticipationDT) anIterator.next();
                    try
                    {
                        if (participationDT != null)
                        {
                            if (participationDT.isItDelete())
                            {
                                //TODO: INSERTION
                                participationService.saveParticipationHist(participationDT);
                            }
                            //TODO: INSERTION
                            participationService.saveParticipation(participationDT);

                            logger.debug("got the participationDT, the ACTUID is " +
                                    participationDT.getActUid());
                            logger.debug("got the participationDT, the subjectEntityUid is " +
                                    participationDT.getSubjectEntityUid());
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw new DataProcessingException(e.getMessage());
                    }
                }
            }

            //ActRelationship Collection
            if (labResultProxyVO.getTheActRelationshipDTCollection() != null)
            {
                logger.debug("Act relationship size: " + labResultProxyVO.getTheActRelationshipDTCollection().size());
                for (Iterator<ActRelationshipDT> anIterator = labResultProxyVO.
                        getTheActRelationshipDTCollection().iterator(); anIterator.hasNext(); )
                {
                    ActRelationshipDT actRelationshipDT = (ActRelationshipDT) anIterator.
                            next();
                    try
                    {
                        if (actRelationshipDT != null)
                        {
                            //TODO: INSERTION
                            actRelationshipService.saveActRelationship(actRelationshipDT);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw new DataProcessingException(e.getMessage());
                    }
                }
            }

            //Processes roleDT collection
            Collection<RoleDto>  roleDTColl = labResultProxyVO.getTheRoleDtoCollection();
            if (roleDTColl != null && !roleDTColl.isEmpty())
            {
                //TODO: INSERTION
                storeRoleDTCollection(roleDTColl);
            }


            //add LDF data
            /**
             * @TBD Release 6.0, Commented out as LDF will be planned out as new type of answers
            LDFHelper ldfHelper = LDFHelper.getInstance();
            ldfHelper.setLDFCollection(labResultProxyVO.getTheStateDefinedFieldDataDTCollection(), labResultProxyVO.getLdfUids(),
            NEDSSConstant.LABREPORT_LDF,null,observationUid,securityObj);
             */

            //EDX Document

            Collection<EDXDocumentDT> edxDocumentCollection = labResultProxyVO.getEDXDocumentCollection();
            ObservationDT rootDT = this.getRootDT(labResultProxyVO);
            if (edxDocumentCollection != null && edxDocumentCollection.size() > 0) {
                if (rootDT.getElectronicInd() != null && rootDT.getElectronicInd().equals(NEDSSConstant.YES)) {
                    Iterator<EDXDocumentDT> ite = edxDocumentCollection.iterator();
                    while (ite.hasNext()) {
                        EDXDocumentDT eDXDocumentDt = (EDXDocumentDT) ite.next();
                        if(eDXDocumentDt.getPayload()!=null){
                            String payload = eDXDocumentDt.getPayload();
                            int containerIndex = payload.indexOf("<Container");
                            eDXDocumentDt.setPayload(payload.substring(containerIndex));
                        }
                        if (eDXDocumentDt.isItNew())
                        {
                            eDXDocumentDt.setActUid(observationUid);
                        }
                        //TODO: INSERTION
                        edxDocumentService.saveEdxDocument(eDXDocumentDt);
                    }
                }
            }
            rootDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
            rootDT.setObservationUid(observationUid);
            if(labResultProxyVO.isItDirty()) {
                PageVO pageVO=(PageVO)labResultProxyVO.getPageVO();
                //TODO: INSERTION
                answerService.storePageAnswer(pageVO, rootDT);
            }else {
                PageVO pageVO=(PageVO)labResultProxyVO.getPageVO();
                //TODO: INSERTION
                answerService.insertPageVO(pageVO, rootDT);
            }
        }
        catch (Exception e)
        {
            throw new DataProcessingException(e.getMessage(), e);
        }
        finally
        {
            try
            {

            }
            catch (Exception rex)
            {
                rex.printStackTrace();
                throw new DataProcessingException(rex.getMessage(), rex);
            }
        }


        return returnVal;
    }

    private void updateAutoResendNotificationsAsync(AbstractVO v)
    {
        logger.info("enter NNDMessageSenderHelper.updateAutoResendNotificationsAsync--------------");
        try{
            updateAutoResendNotifications(v);
            logger.info("finish NNDMessageSenderHelper.updateAutoResendNotificationsAsync()---------------------");
        }
        catch(Exception e){
            logger.error("Exception occurred while calling the updateAutoResendNotificationsAsync"+e.getMessage());
            e.printStackTrace();
        }
    }

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

        Iterator<ObservationVO>  itor = coll.iterator();

        while (itor.hasNext())
        {
            ObservationVO obsVO = (ObservationVO) itor.next();
            ObservationDT obsDT = obsVO.getTheObservationDT();

            if (obsDT == null)
            {
                continue;
            }

            if (obsDT.getCd() == null)
            {
                continue;
            }
            if (obsDT.getCd().trim().equalsIgnoreCase(strCode.trim()))
            {
                return obsVO; // found it!
            }
        }

        // didn't find one
        return null;
    }



    private Long processLabPersonVOCollection(AbstractVO proxyVO) throws DataProcessingException {
        try {
            Collection<PersonContainer>  personVOColl = null;
            boolean isMorbReport = false;

            //TODO: MORBIDITY
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                personVOColl = ( (MorbidityProxyVO) proxyVO).getThePersonVOCollection();
//                isMorbReport = true;
//            }
            if (proxyVO instanceof LabResultProxyContainer)
            {
                personVOColl = ( (LabResultProxyContainer) proxyVO).getThePersonContainerCollection();
            }

            if (personVOColl == null)
            {
                throw new IllegalArgumentException(
                        "PersonVO collection is null");
            }

            PersonContainer personVO = null;
            Long patientMprUid = null;

            if (personVOColl != null && personVOColl.size() > 0)
            {
                for (Iterator<PersonContainer> anIterator = personVOColl.iterator(); anIterator.hasNext(); )
                {
                    personVO = (PersonContainer) anIterator.next();

                    if (personVO == null)
                    {
                        continue;
                    }

                    logger.debug("personUID: " + personVO.getThePersonDto().getPersonUid());

                    //Finds out the type of person being processed and if it is a new person object,
                    //and abort the processing if the parameters not provided or provided incorrectly
                    String personType = personVO.getThePersonDto().getCd();
                    boolean isNewVO = personVO.isItNew();

                    if (personType == null)
                    {
                        throw new DataProcessingException("Expected a non-null person type cd for this person uid: " + personVO.getThePersonDto().getPersonUid());
                    }

                    ObservationDT rootDT = getRootDT(proxyVO);

                    //Persists the person object
                    boolean isExternal = false;
                    String electronicInd = rootDT.getElectronicInd();
                    if(electronicInd != null
                            && ((isMorbReport && electronicInd.equals(NEDSSConstant.EXTERNAL_USER_IND))
                            || electronicInd.equals(NEDSSConstant.YES)))
                    {
                        isExternal = true;
                    }
                    Long realUid = null;
                    if(personVO.getRole()==null){
                        //TODO: INSERTION
                        realUid = setPerson(personType, personVO, isNewVO, isExternal);
                    }else{
                        realUid =personVO.getThePersonDto().getPersonUid();
                    }


                    //If it is a new person object, updates the associations with the newly created uid
                    if (isNewVO && realUid != null)
                    {
                        Long falseUid = personVO.getThePersonDto().getPersonUid();
                        logger.debug("false personUID: " + falseUid);
                        logger.debug("real personUID: " + realUid);

                        if (falseUid.intValue() < 0)
                        {
                            //TODO: FALSE TO NEW METHOD
                            this.setFalseToNew(proxyVO, falseUid, realUid);
                            //set the realUid to person after it has been set to participation
                            //this will help for jurisdiction derivation, this is only local to this call
                            personVO.getThePersonDto().setPersonUid(realUid);
                        }
                    }
                    else if (!isNewVO)
                    {
                        logger.debug("exisiting but updated person's UID: " + realUid);
                    }

                    //If it is patient, return the mpr uid, assuming only one patient in this processing
                    if (personType.equalsIgnoreCase(NEDSSConstant.PAT))
                    {
                        patientMprUid = patientRepositoryUtil.findPatientParentUidByUid(realUid);
                    }
                }
            }
            return patientMprUid;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private Map<Object, Object> processObservationVOCollection(AbstractVO proxyVO, boolean ELR_PROCESSING) throws DataProcessingException {
        try {
            //If coming from lab report, processing this way
            if (proxyVO instanceof LabResultProxyContainer)
            {
                return processLabReportObsVOCollection( (LabResultProxyContainer) proxyVO, ELR_PROCESSING);
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
                throw new IllegalArgumentException(
                        "Expected a valid observation proxy vo, it is: " +
                                proxyVO.getClass().getName());
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private Map<Object, Object> processLabReportObsVOCollection(LabResultProxyContainer labResultProxyVO, boolean ELR_PROCESSING) throws DataProcessingException {
        try {
            Collection<ObservationVO>  obsVOColl = labResultProxyVO.getTheObservationVOCollection();
            ObservationVO observationVO = null;
            Map<Object, Object> returnObsVal = null;
            boolean isMannualLab = false;

            //Find out if it is mannual lab
            String electronicInd = getRootDT(labResultProxyVO).getElectronicInd();
            if(electronicInd != null && !electronicInd.equals(NEDSSConstant.YES))
                isMannualLab = true;

            if (obsVOColl != null && obsVOColl.size() > 0)
            {
                for (Iterator<ObservationVO> anIterator = obsVOColl.iterator(); anIterator.hasNext(); )
                {
                    observationVO = (ObservationVO) anIterator.next();

                    if (observationVO == null)
                    {
                        continue;
                    }

                    //For ordered test and resulted tests
                    ObservationDT currentDT = observationVO.getTheObservationDT();
                    String obsDomainCdSt1 = currentDT.getObsDomainCdSt1();
                    boolean isOrderedTest = (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD))
                            && currentDT.getCd().equalsIgnoreCase("LAB112");
                    boolean isResultedTest = obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);


                    if(isMannualLab)
                    {
                        // Removed for Rel 1.1.3 - as we are not doing a reverse translation for ORdered test and Resulted Test
                        if ( isOrderedTest || isResultedTest) {
                            //Retrieve lab test code

                            //Do loinc and snomed lookups for oredered and resulted tests
                            //String labClia = this.getReportingLabCLIA(labResultProxyVO, securityObj);
                            observationVO = srteCodeObsService.labLoincSnomedLookup(observationVO, labResultProxyVO.getLabClia());
                        }
                        logger.debug("observationUID: " + observationVO.getTheObservationDT().getObservationUid());
                    }
                }
            }

            //Process the ordered test further
            if (returnObsVal == null)
            {
                returnObsVal = new HashMap<Object, Object>();
            }
            returnObsVal.putAll(processLabReportOrderTest(labResultProxyVO, ELR_PROCESSING));

            //Then, persist the observations
            //TODO: INSERTION
            Long observationUid = storeObservationVOCollection(labResultProxyVO);

            //Return the order test uid
            if (observationUid != null)
            {
                if (returnObsVal == null)
                {
                    returnObsVal = new HashMap<Object, Object>();
                }
                returnObsVal.put(NEDSSConstant.SETLAB_RETURN_OBS_UID, observationUid);
            }
            return returnObsVal;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private Map<Object, Object> processLabReportOrderTest(LabResultProxyContainer labResultProxyVO, boolean isELR) throws DataProcessingException {
            //Retrieve the ordered test
            ObservationVO orderTest = this.getRootObservationVO(labResultProxyVO);

            //Overrides rptToStateTime to current date/time for external user
            if (AuthUtil.authUser.getUserType() != null && AuthUtil.authUser.getUserType().equalsIgnoreCase(NEDSSConstant.SEC_USERTYPE_EXTERNAL))
            {
                orderTest.getTheObservationDT().setRptToStateTime(new java.sql.Timestamp( (new java.util.Date()).getTime()));
            }

            //Assign program area cd if necessary, and return any errors to the client
            Map<Object, Object> returnErrors = new HashMap<Object, Object>();
            String paCd = orderTest.getTheObservationDT().getProgAreaCd();
            if (paCd != null && paCd.equalsIgnoreCase(ProgramAreaJurisdiction.ANY_PROGRAM_AREA))
            {
                String paError = deriveProgramAreaCd(labResultProxyVO, orderTest);
                if (paError != null)
                {
                    returnErrors.put(NEDSSConstant.SETLAB_RETURN_PROGRAM_AREA_ERRORS, paError);
                }
            }

            //Assign jurisdiction cd if necessary
            String jurisdictionCd = orderTest.getTheObservationDT().getJurisdictionCd();
            if (jurisdictionCd != null &&
                    (jurisdictionCd.equalsIgnoreCase(ProgramAreaJurisdiction.ANY_JURISDICTION)
                            || jurisdictionCd.equalsIgnoreCase(ProgramAreaJurisdiction.JURISDICTION_NONE)))
            {
                String jurisdictionError = deriveJurisdictionCd(labResultProxyVO, orderTest.getTheObservationDT());
                if (jurisdictionCd != null)
                {
                    returnErrors.put(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS, jurisdictionCd);
                }
            }

            //Manipulate jurisdiction for preparing vo
            jurisdictionCd = orderTest.getTheObservationDT().getJurisdictionCd();
            if(jurisdictionCd != null && (jurisdictionCd.trim().equals("") || jurisdictionCd.equals("ANY") || jurisdictionCd.equals("NONE")))
            {
                orderTest.getTheObservationDT().setJurisdictionCd(null);
            }

            //Do observation object state transition accordingly
            performOrderTestStateTransition(labResultProxyVO, orderTest, isELR);

            return returnErrors;

    }

    private String deriveProgramAreaCd(LabResultProxyContainer labResultProxyVO, ObservationVO orderTest) throws DataProcessingException {
            //Gathering the result tests
            Collection<ObservationVO>  resultTests = new ArrayList<> ();
            for (Iterator<ObservationVO> it = labResultProxyVO.getTheObservationVOCollection().iterator(); it.hasNext(); )
            {
                ObservationVO obsVO = (ObservationVO) it.next();
                String obsDomainCdSt1 = obsVO.getTheObservationDT().getObsDomainCdSt1();
                if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD))
                {
                    resultTests.add(obsVO);
                }
            }

            //Get the reporting lab clia
            String reportingLabCLIA = "";
            if(labResultProxyVO.getLabClia()!=null && labResultProxyVO.isManualLab())
            {
                reportingLabCLIA =labResultProxyVO.getLabClia();
            }
            else
            {
                reportingLabCLIA = getReportingLabCLIA(labResultProxyVO);
            }

            if(reportingLabCLIA == null || reportingLabCLIA.trim().equals(""))
            {
                reportingLabCLIA = NEDSSConstant.DEFAULT;
            }

            //Get program area
            if(!orderTest.getTheObservationDT().getElectronicInd().equals(NEDSSConstant.ELECTRONIC_IND_ELR)){
                Map<Object, Object> paResults = null;
                if (resultTests.size() > 0)
                {
                    paResults = srteCodeObsService.getProgramArea(reportingLabCLIA, resultTests, orderTest.getTheObservationDT().getElectronicInd());
                }

                //set program area for order test
                if (paResults != null && paResults.containsKey(ELRConstant.PROGRAM_AREA_HASHMAP_KEY))
                {
                    orderTest.getTheObservationDT().setProgAreaCd( (String) paResults.get(ELRConstant.PROGRAM_AREA_HASHMAP_KEY));
                }
                else
                {
                    orderTest.getTheObservationDT().setProgAreaCd(null);
                }

                //Return errors if any
                if (paResults != null &&
                        paResults.containsKey("ERROR"))
                {
                    return (String) paResults.get("ERROR");
                }
                else
                {
                    return null;
                }
            }
            return null;
    }

    private String getReportingLabCLIA(AbstractVO proxy) throws DataProcessingException {
            Collection<ParticipationDT>  partColl = null;
            if (proxy instanceof LabResultProxyContainer)
            {
                partColl = ( (LabResultProxyContainer) proxy).getTheParticipationDTCollection();
            }
//            if (proxy instanceof MorbidityProxyVO)
//            {
//                partColl = ( (MorbidityProxyVO) proxy).getTheParticipationDTCollection();
//            }

            //Get the reporting lab
            Long reportingLabUid = this.getUid(partColl,
                    null,
                    NEDSSConstant.ENTITY_UID_LIST_TYPE,
                    NEDSSConstant.ORGANIZATION,
                    NEDSSConstant.PAR111_TYP_CD,
                    NEDSSConstant.PART_ACT_CLASS_CD,
                    NEDSSConstant.RECORD_STATUS_ACTIVE);

            OrganizationVO reportingLabVO = null;
            if (reportingLabUid != null)
            {
                reportingLabVO = organizationRepositoryUtil.loadObject(reportingLabUid, null);
            }

            //Get the CLIA
            String reportingLabCLIA = null;

            if(reportingLabVO != null)
            {
                Collection<EntityIdDto>  entityIdColl = reportingLabVO.getTheEntityIdDtoCollection();

                if (entityIdColl != null && entityIdColl.size() > 0) {
                    for (Iterator<EntityIdDto> it = entityIdColl.iterator(); it.hasNext(); ) {
                        EntityIdDto idDT = (EntityIdDto) it.next();
                        if (idDT == null) {
                            continue;
                        }

                        String authoCd = idDT.getAssigningAuthorityCd();
                        String idTypeCd = idDT.getTypeCd();
                        if (authoCd != null && idTypeCd != null
                                && authoCd.equalsIgnoreCase(NEDSSConstant.REPORTING_LAB_CLIA)
                                && idTypeCd.equalsIgnoreCase(NEDSSConstant.REPORTING_LAB_FI_TYPE)) { //civil00011659
                            reportingLabCLIA = idDT.getRootExtensionTxt();
                            break;
                        }
                    }
                }
            }
            return reportingLabCLIA;
    }


    private ObservationDT getRootDT(AbstractVO proxyVO) throws DataProcessingException {
        try {
            ObservationVO rootVO = getRootObservationVO(proxyVO);
            if (rootVO != null)
            {
                return rootVO.getTheObservationDT();
            }
            return null;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);

        }
    }

    private ObservationVO getRootObservationVO(AbstractVO proxy) throws DataProcessingException
    {
        try {
            Collection<ObservationVO>  obsColl = null;
            boolean isLabReport = false;

            if (proxy instanceof LabResultProxyContainer)
            {
                obsColl = ( (LabResultProxyContainer) proxy).getTheObservationVOCollection();
                isLabReport = true;
            }
//            if (proxy instanceof MorbidityProxyVO)
//            {
//                obsColl = ( (MorbidityProxyVO) proxy).getTheObservationVOCollection();
//            }

            ObservationVO rootVO = getRootObservationVO(obsColl, isLabReport);

            if( rootVO != null)
            {
                return rootVO;
            }
            throw new IllegalArgumentException("Expected the proxyVO containing a root observation(e.g., ordered test)");
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private ObservationVO getRootObservationVO(Collection<ObservationVO> obsColl, boolean isLabReport) throws DataProcessingException {
        try {
            if(obsColl == null){
                return null;
            }

            logger.debug("ObservationVOCollection  is not null");
            Iterator<ObservationVO>  iterator = null;
            for (iterator = obsColl.iterator(); iterator.hasNext(); )
            {
                ObservationVO observationVO = (ObservationVO) iterator.next();
                if (observationVO.getTheObservationDT() != null &&
                        ( (observationVO.getTheObservationDT().getCtrlCdDisplayForm() != null &&
                                observationVO.getTheObservationDT().getCtrlCdDisplayForm().
                                        equalsIgnoreCase(NEDSSConstant.LAB_CTRLCD_DISPLAY))
                                ||
                                (observationVO.getTheObservationDT().getObsDomainCdSt1() != null &&
                                        observationVO.getTheObservationDT().getObsDomainCdSt1().
                                                equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD) && isLabReport)
                                ||
                                (observationVO.getTheObservationDT().getCtrlCdDisplayForm() != null &&
                                        observationVO.getTheObservationDT().getCtrlCdDisplayForm().
                                                equalsIgnoreCase(NEDSSConstant.MOB_CTRLCD_DISPLAY))))
                {
                    logger.debug("found root vo !!");
                    return observationVO;
                }
                else
                {
                    continue;
                }
            }
            return null;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);

        }
    }

    private Map<Object, Object> findLocalUidsFor(Long personMprUid, Long observationUid) throws DataProcessingException {
        Map<Object, Object> localIds = null;

        try
        {
            //Find observation local id
            if(localIds == null) localIds = new HashMap<Object, Object> ();
            //TODO: SELECTION
            var resObs = observationRepository.findById(observationUid);
            ObservationDT obsDT = new ObservationDT();
            if (resObs.isPresent()) {
                obsDT = new ObservationDT(resObs.get());
            }
            localIds.put(NEDSSConstant.SETLAB_RETURN_OBS_LOCAL, obsDT.getLocalId());
            localIds.put(NEDSSConstant.SETLAB_RETURN_OBSDT, obsDT);
            //Find mpr local id
            //TODO: SELECTION

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

    /**
     * This method checks for the negative uid value for any ACT & ENTITY DT then compare them
     * with respective negative values in ActRelationshipDT and ParticipationDT as received from
     * the investigationProxyVO(determined in the addInvestigation method).
     * As it has also got the actualUID (determined in the addInvestigation method) it replaces them accordingly.
     */
    private void setFalseToNew(AbstractVO proxyVO, Long falseUid, Long actualUid)
    {
        Iterator<ParticipationDT>  participationDTIterator = null;
        Iterator<ActRelationshipDT>  actRelationshipDTIterator = null;
        Iterator<RoleDto>  roleDtoIterator = null;


        ParticipationDT participationDT = null;
        ActRelationshipDT actRelationshipDT = null;
        RoleDto roleDT = null;

        Collection<ParticipationDT>  participationColl = null;
        Collection<ActRelationshipDT>  actRelationShipColl = null;
        Collection<RoleDto>  roleColl = null;

        if (proxyVO instanceof LabResultProxyContainer)
        {
            participationColl = (ArrayList<ParticipationDT> ) ( (LabResultProxyContainer) proxyVO).getTheParticipationDTCollection();
            actRelationShipColl = (ArrayList<ActRelationshipDT> ) ( (LabResultProxyContainer) proxyVO).getTheActRelationshipDTCollection();
            roleColl = (ArrayList<RoleDto> ) ( (LabResultProxyContainer) proxyVO).getTheRoleDtoCollection();
        }

        //TODO: MORBIDITY
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                participationColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheParticipationDTCollection();
//                actRelationShipColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheActRelationshipDTCollection();
//                roleColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheRoleDTCollection();
//            }

        if (participationColl != null)
        {
            for (participationDTIterator = participationColl.iterator(); participationDTIterator.hasNext(); )
            {
                participationDT = (ParticipationDT) participationDTIterator.next();
                if (participationDT != null && falseUid != null)
                {
                    if (participationDT.getActUid().compareTo(falseUid) == 0)
                    {
                        participationDT.setActUid(actualUid);
                    }
                    if (participationDT.getSubjectEntityUid().compareTo(falseUid) == 0)
                    {
                        participationDT.setSubjectEntityUid(actualUid);
                    }
                }
            }
        }

        if (actRelationShipColl != null)
        {
            for (actRelationshipDTIterator = actRelationShipColl.iterator(); actRelationshipDTIterator.hasNext(); )
            {
                actRelationshipDT = (ActRelationshipDT) actRelationshipDTIterator.next();
                if (actRelationshipDT.getTargetActUid().compareTo(falseUid) == 0)
                {
                    actRelationshipDT.setTargetActUid(actualUid);
                }
                if (actRelationshipDT.getSourceActUid().compareTo(falseUid) == 0)
                {
                    actRelationshipDT.setSourceActUid(actualUid);
                }
            }

        }

        if (roleColl != null && roleColl.size() != 0)
        {
            for (roleDtoIterator = roleColl.iterator(); roleDtoIterator.hasNext(); )
            {
                roleDT =  roleDtoIterator.next();

                if (roleDT.getSubjectEntityUid().compareTo(falseUid) == 0)
                {
                    roleDT.setSubjectEntityUid(actualUid);

                }
                if (roleDT.getScopingEntityUid() != null)
                {
                    if (roleDT.getScopingEntityUid().compareTo(falseUid) == 0)
                    {
                        roleDT.setScopingEntityUid(actualUid);
                    }
                }

            }
        }
    }



    private String deriveJurisdictionCd(AbstractVO proxyVO, ObservationDT rootObsDT) throws DataProcessingException {
        try {
            //Retieve provider uid and patient uid
            Collection<ParticipationDT>  partColl = null;
            boolean isLabReport = false, isMorbReport = false;
            String jurisdictionDerivationInd = AuthUtil.authUser.getJurisdictionDerivationInd();
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                isMorbReport = true;
//                partColl = ( (MorbidityProxyVO) proxyVO).getTheParticipationDTCollection();
//            }


            if (proxyVO instanceof LabResultProxyContainer)
            {
                isLabReport = true;
                partColl = ( (LabResultProxyContainer) proxyVO).getTheParticipationDTCollection();
            }
            if (partColl == null || partColl.size() <= 0)
            {
                throw new DataProcessingException("Participation collection is null or empty, it is: " + partColl);
            }

            Long providerUid = null;
            Long patientUid = null;
            Long orderingFacilityUid = null;
            Long reportingFacilityUid = null;

            for (Iterator<ParticipationDT> it = partColl.iterator(); it.hasNext(); )
            {
                ParticipationDT partDT = (ParticipationDT) it.next();
                if (partDT == null)
                {
                    continue;
                }

                String typeCd = partDT.getTypeCd();
                String subjectClassCd = partDT.getSubjectClassCd();
                if (typeCd != null && (typeCd.equalsIgnoreCase(NEDSSConstant.PAR101_TYP_CD)
                        || typeCd.equalsIgnoreCase(NEDSSConstant.MOB_PHYSICIAN_OF_MORB_REPORT))
                        && subjectClassCd != null && subjectClassCd.equalsIgnoreCase(NEDSSConstant.PERSON_CLASS_CODE))
                {
                    providerUid = partDT.getSubjectEntityUid();
                }
                else if (typeCd != null
                        && (typeCd.equalsIgnoreCase(NEDSSConstant.PAR110_TYP_CD)
                        || typeCd.equalsIgnoreCase(NEDSSConstant.MOB_SUBJECT_OF_MORB_REPORT)))
                {
                    patientUid = partDT.getSubjectEntityUid();
                }
                else if (typeCd != null
                        && (typeCd.equalsIgnoreCase(NEDSSConstant.PAR102_TYP_CD)))
                {
                    orderingFacilityUid = partDT.getSubjectEntityUid();
                }
                else if(jurisdictionDerivationInd!=null
                        && jurisdictionDerivationInd.equals(NEDSSConstant.YES)
                        && typeCd != null
                        && typeCd.equalsIgnoreCase(NEDSSConstant.PAR111_TYP_CD)
                        && subjectClassCd != null
                        && subjectClassCd.equalsIgnoreCase(NEDSSConstant.PAR111_SUB_CD)
                        && rootObsDT!=null
                        && rootObsDT.getCtrlCdDisplayForm()!=null
                        && rootObsDT.getCtrlCdDisplayForm().equalsIgnoreCase(NEDSSConstant.LAB_REPORT)
                        && rootObsDT.getElectronicInd()!=null
                        && rootObsDT.getElectronicInd().equals(NEDSSConstant.EXTERNAL_USER_IND))
                    reportingFacilityUid=partDT.getSubjectEntityUid();
            }

            //Get the provider vo from db
            PersonContainer providerVO = null;
            OrganizationVO orderingFacilityVO = null;
            OrganizationVO reportingFacilityVO = null;
            try
            {
                if (providerUid != null)
                {
                    providerVO = patientRepositoryUtil.loadPerson(providerUid);
                }
                if (orderingFacilityUid != null)
                {
                    // orderingFacilityVO = getOrganization(orderingFacilityUid);
                    orderingFacilityVO = organizationRepositoryUtil.loadObject(orderingFacilityUid, null);
                }
                if(reportingFacilityUid!=null)
                {
                    // reportingFacilityVO = getOrganization(reportingFacilityUid);
                    orderingFacilityVO = organizationRepositoryUtil.loadObject(orderingFacilityUid, null);
                }
            }
            catch (Exception rex)
            {
                throw new DataProcessingException("Error retieving provider with UID:"+ providerUid +" OR Ordering Facility, its uid is: " + orderingFacilityUid);
            }

            //Get the patient subject
            PersonContainer patientVO = null;
            Collection<PersonContainer>  personVOColl = null;
            if (isLabReport)
            {
                personVOColl = ( (LabResultProxyContainer) proxyVO).getThePersonContainerCollection();
            }
//            if (isMorbReport)
//            {
//                personVOColl = ( (MorbidityProxyVO) proxyVO).getThePersonVOCollection();
//
//            }
            if (patientUid != null && personVOColl != null && personVOColl.size() > 0)
            {
                for (Iterator<PersonContainer> it = personVOColl.iterator(); it.hasNext(); )
                {
                    PersonContainer pVO = (PersonContainer) it.next();
                    if (pVO == null || pVO.getThePersonDto() == null)
                    {
                        continue;
                    }
                    if (pVO.getThePersonDto().getPersonUid().compareTo(patientUid) == 0)
                    {
                        patientVO = pVO;
                        break;
                    }
                }
            }

            //Derive the jurisdictionCd
            Map<Object, Object> jMap = null;
            if (patientVO != null)
            {

                try
                {
                    //TODO: JURISDICTION
                    jMap = jurisdictionService.resolveLabReportJurisdiction(patientVO, providerVO, orderingFacilityVO, reportingFacilityVO);
                }
                catch (Exception cex)
                {
                    throw new DataProcessingException("Error creating jurisdiction services.");
                }
            }

            //set jurisdiction for order test
            if (jMap != null && jMap.containsKey(ELRConstant.JURISDICTION_HASHMAP_KEY))
            {
                rootObsDT.setJurisdictionCd( (String) jMap.get(ELRConstant.JURISDICTION_HASHMAP_KEY));
            }
            else
            {
                rootObsDT.setJurisdictionCd(null);
            }

            //Return errors if any
            if (jMap != null && jMap.containsKey("ERROR"))
            {
                return (String) jMap.get("ERROR");
            }
            else
            {
                return null;
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

    private void performOrderTestStateTransition(LabResultProxyContainer labResultProxyVO, ObservationVO orderTest, boolean isELR) throws DataProcessingException
    {
        try {
            String businessTriggerCd = null;
            ObservationDT newObservationDT = null;
            logger.debug("order test UID: " +
                    orderTest.getTheObservationDT().getObservationUid());

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
//            newObservationDT = (ObservationDT) prepareVOUtils.prepareVO(
//                    orderTest.getTheObservationDT(), NBSBOLookup.OBSERVATIONLABREPORT,
//                    businessTriggerCd, DataTables.OBSERVATION_TABLE, NEDSSConstant.BASE,
//                    securityObj);
//            orderTest.setTheObservationDT(newObservationDT);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private void storeRoleDTCollection(Collection<RoleDto> roleDTColl) throws DataProcessingException {
        try {
            if(roleDTColl == null || roleDTColl.isEmpty()) return;

            for (Iterator<RoleDto> anIterator = roleDTColl.iterator(); anIterator.hasNext(); )
            {
                RoleDto roleDT = anIterator.next();
                if(roleDT == null){
                    continue;
                }

                //TODO: EVALUATE
                //roleDT = (RoleDto)new PrepareVOUtils().prepareAssocDT(roleDT);
                roleService.saveRole(roleDT);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private Long setPerson(String personType, PersonContainer personVO, boolean isNew, boolean isExternal) throws DataProcessingException
    {
        try
        {
            if (personType.equalsIgnoreCase(NEDSSConstant.PAT))
            {
                return patientMatchingService.updateExistingPerson(personVO, isNew ? NEDSSConstant.PAT_CR : NEDSSConstant.PAT_EDIT);
            }
            else if (personType.equalsIgnoreCase(NEDSSConstant.PRV) && (!isNew || (isNew && isExternal)))
            {
                return providerMatchingService.setProvider(personVO, isNew ? NEDSSConstant.PRV_CR : NEDSSConstant.PRV_EDIT);
            }
            else
            {
                throw new IllegalArgumentException("Expected a valid person type: " + personType);
            }
        }
        catch (Exception rex)
        {
            throw new DataProcessingException(rex.getMessage(), rex);
        }
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
                for (Iterator<ObservationVO> anIterator = obsVOColl.iterator(); anIterator.hasNext(); )
                {
                    observationVO = (ObservationVO) anIterator.next();

                    if (observationVO == null)
                    {
                        continue;
                    }

                    //If lab report's order test, set a flag
                    boolean isRootObs = false;

                    String obsDomainCdSt1 = observationVO.getTheObservationDT().
                            getObsDomainCdSt1();
                    if (isLabResultProxyVO && obsDomainCdSt1 != null &&
                            obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD))
                    {
                        isRootObs = true;
                    }

                    //If a root morbidity, set a flag so to return the observation uid
                    String ctrlCdDisplayForm = observationVO.getTheObservationDT().
                            getCtrlCdDisplayForm();
                    if (ctrlCdDisplayForm != null &&
                            ctrlCdDisplayForm.equalsIgnoreCase(NEDSSConstant.MOB_CTRLCD_DISPLAY))
                    {
                        isRootObs = true;
                    }

                    //TODO INSERTION
                    //Persist the observation vo
                    Long observationUid = saveObservation(observationVO);

                    //Update associations with real uid if new
                    if (observationVO.isItNew())
                    {
                        Long falseUid = observationVO.getTheObservationDT().getObservationUid();
                        logger.debug("false observationUID: " + falseUid);
                        if (falseUid.intValue() < 0) {
                            this.setFalseToNew(proxyVO, falseUid, observationUid);
                        }
                    }


                    //Return the order test uid
                    if (observationUid != null && isRootObs)
                    {
                        returnObsVal = observationUid;
                    }
                } //end of for loop
            } //end of main if
            return returnObsVal;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    @Transactional
    public Long saveObservation(ObservationVO observationVO) throws DataProcessingException {
        Long observationUid = -1L;


        try {
            Observation observation = null;

            Collection<ActivityLocatorParticipationDT> alpDTCol = observationVO.getTheActivityLocatorParticipationDTCollection();
            Collection<ActRelationshipDT> arDTCol = observationVO.getTheActRelationshipDTCollection();
            Collection<ParticipationDT> pDTCol = observationVO.getTheParticipationDTCollection();
            Collection<ActRelationshipDT> colAct = null;
            Collection<ParticipationDT> colParticipation = null;
            Collection<ActivityLocatorParticipationDT> colActLocatorParticipation = null;


            if (alpDTCol != null)
            {
                colActLocatorParticipation = entityHelper.iterateActivityParticipation(alpDTCol);
                observationVO.setTheActivityLocatorParticipationDTCollection(colActLocatorParticipation);
            }

            if (arDTCol != null)
            {
                colAct = entityHelper.iterateActRelationship(arDTCol);
                observationVO.setTheActRelationshipDTCollection(colAct);
            }

            if (pDTCol != null)
            {
                colParticipation = entityHelper.iteratePDTForParticipation(pDTCol);
                observationVO.setTheParticipationDTCollection(colParticipation);
            }

            if (observationVO.isItNew())
            {
                //observation = home.create(observationVO);
                var obsUid =  createNewObservation(observationVO);
                observationUid = obsUid;
            }
            else
            {
                if (observationVO.getTheObservationDT() != null) // make sure it is not null
                {
                    updateObservation(observationVO);
                    observationUid = observationVO.getTheObservationDT().getObservationUid();
                }
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return observationUid;

    }

    @Transactional
    public Long createNewObservation(ObservationVO observationVO) {
        Long obsId = saveNewObservation(observationVO.getTheObservationDT());
        observationVO.getTheObservationDT().setItNew(false);
        observationVO.getTheObservationDT().setItDirty(false);

        addObservationReasons(obsId, observationVO.getTheObservationReasonDTCollection());
        addActivityId(obsId, observationVO.getTheActIdDTCollection());
        addObservationInterps(obsId, observationVO.getTheObservationInterpDTCollection());
        addObsValueCoded(obsId, observationVO.getTheObsValueCodedDTCollection());
        addObsValueTxts(obsId, observationVO.getTheObsValueTxtDTCollection());
        addObsValueDates(obsId, observationVO.getTheObsValueDateDTCollection());
        addObsValueNumeric(obsId, observationVO.getTheObsValueNumericDTCollection());
        addActivityLocatorParticipations(obsId, observationVO.getTheActivityLocatorParticipationDTCollection());
        return obsId;
    }

    @Transactional
    public Long updateObservation(ObservationVO observationVO) {
        Long uid = -1L;
        if (observationVO.getTheObservationDT().getObservationUid() == null) {
            uid = saveNewObservation(observationVO.getTheObservationDT());
            observationVO.getTheObservationDT().setItNew(false);
            observationVO.getTheObservationDT().setItDirty(false);
        } else {
            uid = saveObservation(observationVO.getTheObservationDT());
            observationVO.getTheObservationDT().setItNew(false);
            observationVO.getTheObservationDT().setItDirty(false);
        }

        updateObservationReason(uid, observationVO.getTheObservationReasonDTCollection());
        addActivityId(uid, observationVO.getTheActIdDTCollection());
        updateObservationInterps(uid, observationVO.getTheObservationInterpDTCollection());
        updateObsValueCoded(uid, observationVO.getTheObsValueCodedDTCollection());
        updateObsValueTxts(uid, observationVO.getTheObsValueTxtDTCollection());
        updateObsValueDates(uid, observationVO.getTheObsValueDateDTCollection());
        updateObsValueNumerics(uid, observationVO.getTheObsValueNumericDTCollection());
        addActivityLocatorParticipations(uid, observationVO.getTheActivityLocatorParticipationDTCollection());
        return uid;
    }

    private Long saveNewObservation(ObservationDT observationDT) {
        var uid = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.OBSERVATION);

        Act act = new Act();
        act.setActUid(uid.getSeedValueNbr());
        act.setClassCode(NEDSSConstant.OBSERVATION_CLASS_CODE);
        act.setMoodCode(NEDSSConstant.EVENT_MOOD_CODE);

        actRepository.save(act);

        observationDT.setObservationUid(uid.getSeedValueNbr());
        //TODO EVALUATE
        // Local uid
        observationDT.setLocalId("TEST123");
        Observation observation = new Observation(observationDT);
        observationRepository.save(observation);
        return uid.getSeedValueNbr();
    }
    private Long saveObservation(ObservationDT observationDT) {
        Observation observation = new Observation(observationDT);
        observationRepository.save(observation);
        return observation.getObservationUid();
    }

    // private void insertObservationReasons(ObservationVO obVO) throws  NEDSSSystemException
    private void  addObservationReasons(Long obsUid, Collection<ObservationReasonDT> observationReasonDTCollection) {
        ArrayList<ObservationReasonDT> arr = new ArrayList<>(observationReasonDTCollection);
        for(var item: arr) {
            item.setObservationUid(obsUid);
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
            saveObservationReason(item);
        }
    }

    private void saveObservationReason(ObservationReasonDT item) {
        var data = new ObservationReason(item);
        observationReasonRepository.save(data);
    }

    private void updateObservationReason(Long obsUid, Collection<ObservationReasonDT> observationReasonDTCollection) {
        ArrayList<ObservationReasonDT> arr = new ArrayList<>(observationReasonDTCollection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObservationReason(item);
            } else {
                observationReasonRepository.delete(new ObservationReason(item));
            }
        }
    }
    private void addActivityId(Long obsUid, Collection<ActIdDT> actIdDTCollection) {
        ArrayList<ActIdDT> arr = new ArrayList<>(actIdDTCollection);
        for(var item: arr) {
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
            item.setActUid(obsUid);
            var reason = new ActId(item);
            actIdRepository.save(reason);
        }
    }

    private void addObservationInterps(Long obsUid, Collection<ObservationInterpDT> observationInterpDTCollection) {
        ArrayList<ObservationInterpDT> arr = new ArrayList<>(observationInterpDTCollection);
        for(var item: arr) {
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
            item.setObservationUid(obsUid);
            saveObservationInterp(item);
        }
    }

    private void saveObservationInterp(ObservationInterpDT item) {
        var reason = new ObservationInterp(item);
        observationInterpRepository.save(reason);
    }

    private void updateObservationInterps(Long obsUid, Collection<ObservationInterpDT> collection) {
        ArrayList<ObservationInterpDT> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObservationInterp(item);
            } else {
                observationInterpRepository.delete(new ObservationInterp(item));
            }
        }
    }

    private void addObsValueCoded(Long obsUid, Collection<ObsValueCodedDT> obsValueCodedDTCollection) {
        ArrayList<ObsValueCodedDT> arr = new ArrayList<>(obsValueCodedDTCollection);
        for(var item: arr) {
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
            item.setObservationUid(obsUid);
            saveObsValueCoded(item);
        }
    }

    private void saveObsValueCoded(ObsValueCodedDT item) {
        var reason = new ObsValueCoded(item);
        obsValueCodedRepository.save(reason);
    }

    private void updateObsValueCoded(Long obsUid, Collection<ObsValueCodedDT> collection) {
        ArrayList<ObsValueCodedDT> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObsValueCoded(item);

            } else {
                obsValueCodedRepository.delete(new ObsValueCoded(item));
            }
        }
    }


    private void addObsValueTxts(Long obsUid, Collection<ObsValueTxtDT> obsValueTxtDTCollection) {
        ArrayList<ObsValueTxtDT> arr = new ArrayList<>(obsValueTxtDTCollection);
        for(var item: arr) {
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
            item.setObservationUid(obsUid);
            saveObsValueTxt(item);
        }
    }

    private void saveObsValueTxt(ObsValueTxtDT item) {
        var reason = new ObsValueTxt(item);
        obsValueTxtRepository.save(reason);
    }

    private void updateObsValueTxts(Long obsUid, Collection<ObsValueTxtDT> collection) {
        ArrayList<ObsValueTxtDT> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObsValueTxt(item);

            } else {
                obsValueTxtRepository.delete(new ObsValueTxt(item));
            }
        }
    }

    private void addObsValueDates(Long obsUid, Collection<ObsValueDateDT> obsValueDateDTCollection) {
        ArrayList<ObsValueDateDT> arr = new ArrayList<>(obsValueDateDTCollection);
        for(var item: arr) {
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
            item.setObservationUid(obsUid);
            saveObsValueDate(item);
        }
    }

    private void saveObsValueDate(ObsValueDateDT item) {
        var reason = new ObsValueDate(item);
        obsValueDateRepository.save(reason);
    }

    private void updateObsValueDates(Long obsUid, Collection<ObsValueDateDT> collection) {
        ArrayList<ObsValueDateDT> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObsValueDate(item);

            } else {
                obsValueDateRepository.delete(new ObsValueDate(item));
            }
        }
    }

    private void addObsValueNumeric(Long obsUid, Collection<ObsValueNumericDT> obsValueNumericDTCollection) {
        ArrayList<ObsValueNumericDT> arr = new ArrayList<>(obsValueNumericDTCollection);
        for(var item: arr) {
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
            item.setObservationUid(obsUid);
            saveObsValueNumeric(item);
        }
    }

    private void saveObsValueNumeric(ObsValueNumericDT item) {
        var reason = new ObsValueNumeric(item);
        obsValueNumericRepository.save(reason);
    }

    private void updateObsValueNumerics(Long obsUid, Collection<ObsValueNumericDT> collection) {
        ArrayList<ObsValueNumericDT> arr = new ArrayList<>(collection);
        for(var item: arr) {
            if (!item.isItDelete()) {
                item.setObservationUid(obsUid);
                saveObsValueNumeric(item);

            } else {
                obsValueNumericRepository.delete(new ObsValueNumeric(item));
            }
        }
    }

    private void addActivityLocatorParticipations(Long obsUid, Collection<ActivityLocatorParticipationDT> activityLocatorParticipationDTCollection) {
        ArrayList<ActivityLocatorParticipationDT> arr = new ArrayList<>(activityLocatorParticipationDTCollection);
        for(var item: arr) {
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
            item.setActUid(obsUid);
            var reason = new ActLocatorParticipation(item);
            actLocatorParticipationRepository.save(reason);
        }
    }

}



