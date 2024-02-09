package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.*;
import gov.cdc.dataprocessing.utilities.UniqueIdGenerator;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Component
public class PatientRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(PatientRepositoryUtil.class);
    private final PersonRepository personRepository;
    private final EntityRepositoryUtil entityRepositoryUtil;
    private final PersonNameRepository personNameRepository;
    private final PersonRaceRepository personRaceRepository;
    private final PersonEthnicRepository personEthnicRepository;
    private final EntityIdRepository entityIdRepository;
    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    private final RoleRepository roleRepository;

    private final PreparingPersonUtil preparingPersonUtil;

    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;

    private final EntityHelper entityHelper;

    public PatientRepositoryUtil(
            PersonRepository personRepository,
            EntityRepositoryUtil entityRepositoryUtil,
            PersonNameRepository personNameRepository,
            PersonRaceRepository personRaceRepository,
            PersonEthnicRepository personEthnicRepository,
            EntityIdRepository entityIdRepository,
            EntityLocatorParticipationRepository entityLocatorParticipationRepository,
            RoleRepository roleRepository,
            PreparingPersonUtil preparingPersonUtil,
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper
             ) {
        this.personRepository = personRepository;
        this.entityRepositoryUtil = entityRepositoryUtil;
        this.personNameRepository = personNameRepository;
        this.personRaceRepository = personRaceRepository;
        this.personEthnicRepository = personEthnicRepository;
        this.entityIdRepository = entityIdRepository;
        this.entityLocatorParticipationRepository = entityLocatorParticipationRepository;
        this.roleRepository = roleRepository;
        this.preparingPersonUtil = preparingPersonUtil;
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
        this.entityHelper = entityHelper;
    }

    public Person findExistingPersonByUid(PersonVO personVO) {
        PersonDT personDT = personVO.getThePersonDT();
        var result = personRepository.findById(personDT.getPersonUid());
        return result.get();
    }

    public Person findExistingPersonByUid(Long personUid) {
        var result = personRepository.findById(personUid);
        return result.get();
    }

    public Person createPerson(PersonVO personVO) throws DataProcessingException {
        //TODO: Implement unique id generator here
        Long personUid = 212121L;
        personUid = UniqueIdGenerator.generateUniqueId();

        String localUid = "Unique Id here";
        localUid = UniqueIdGenerator.generateUniqueStringId();

        ArrayList<Object>  arrayList = new ArrayList<>();
        PersonDT personDT = personVO.getThePersonDT();

        if(personDT.getLocalId() == null || personDT.getLocalId().trim().length() == 0) {
            personDT.setLocalId(localUid);
        }

        if(personDT.getPersonParentUid() == null) {
            personDT.setPersonParentUid(personUid);
        }

        // set new person uid in entity table
        personDT.setPersonUid(personUid);

        arrayList.add(personUid);
        arrayList.add(NEDSSConstant.PERSON);



        //NOTE: Create Entitty
        try {
            entityRepositoryUtil.preparingEntityReposCallForPerson(personDT, personUid, NEDSSConstant.PERSON, NEDSSConstant.UPDATE);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        //NOTE: Create Person
        Person person = new Person(personDT);
        try {
            personRepository.save(person);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        //NOTE: Create Person Name
        if  (personVO.getThePersonNameDTCollection() != null && !personVO.getThePersonNameDTCollection().isEmpty()) {
            try {
                createPersonName(personVO);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Person Race
        if  (personVO.getThePersonRaceDTCollection() != null && !personVO.getThePersonRaceDTCollection().isEmpty()) {
            try {
                createPersonRace(personVO);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Person Ethnic
        if  (personVO.getThePersonEthnicGroupDTCollection() != null && !personVO.getThePersonEthnicGroupDTCollection().isEmpty()) {
            try {
                createPersonEthnic(personVO);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create EntityID
        if  (personVO.getTheEntityIdDTCollection() != null && !personVO.getTheEntityIdDTCollection().isEmpty()) {
            try {
                createEntityId(personVO);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Entity Locator Participation
        if  (personVO.getTheEntityLocatorParticipationDTCollection() != null && !personVO.getTheEntityLocatorParticipationDTCollection().isEmpty()) {
            try {
                createEntityLocatorParticipation(personVO);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Role
        if  (personVO.getTheRoleDTCollection() != null && !personVO.getTheRoleDTCollection().isEmpty()) {
            try {
                createRole(personVO);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        return person;
    }

    private void createPersonName(PersonVO personVO) throws DataProcessingException {
        ArrayList<PersonNameDT>  personList = (ArrayList<PersonNameDT> ) personVO.getThePersonNameDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                PersonNameDT personNameDT = personList.get(i);
                personNameRepository.save(new PersonName(personNameDT));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createPersonRace(PersonVO personVO) throws DataProcessingException {
        ArrayList<PersonRaceDT>  personList = (ArrayList<PersonRaceDT> ) personVO.getThePersonRaceDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                PersonRaceDT obj = personList.get(i);
                personRaceRepository.save(new PersonRace(obj));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createPersonEthnic(PersonVO personVO) throws DataProcessingException {
        ArrayList<PersonEthnicGroupDT>  personList = (ArrayList<PersonEthnicGroupDT> ) personVO.getThePersonEthnicGroupDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                PersonEthnicGroupDT obj = personList.get(i);
                personEthnicRepository.save(new PersonEthnicGroup(obj));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityId(PersonVO personVO) throws DataProcessingException {
        ArrayList<EntityIdDT>  personList = (ArrayList<EntityIdDT> ) personVO.getTheEntityIdDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                EntityIdDT obj = personList.get(i);
                entityIdRepository.save(new EntityId(obj));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityLocatorParticipation(PersonVO personVO) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDT>  personList = (ArrayList<EntityLocatorParticipationDT> ) personVO.getTheEntityLocatorParticipationDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                EntityLocatorParticipationDT obj = personList.get(i);
                entityLocatorParticipationRepository.save(new EntityLocatorParticipation(obj));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createRole(PersonVO personVO) throws DataProcessingException {
        ArrayList<RoleDT>  personList = (ArrayList<RoleDT> ) personVO.getTheRoleDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                RoleDT obj = personList.get(i);
                roleRepository.save(new Role(obj));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public boolean updateWithRevision(PersonVO newRevision) throws DataProcessingException {
        //TODO: Logic to update revison
        Object theLookedUpObject;
//        theLookedUpObject = nedssUtils.lookupBean(JNDINames.MPR_UPDATE_ENGINE_EJB);
//        MPRUpdateEngineHome mprHome = (MPRUpdateEngineHome) PortableRemoteObject.narrow(theLookedUpObject, MPRUpdateEngineHome.class);
//        MPRUpdateEngine mprUpdateEngine = mprHome.create();
//        mprUpdateEngine.updateWithRevision(personVO, nbsSecurityObj);

        Long mprUID = newRevision.getThePersonDT().getPersonParentUid();;
        PersonVO mpr = getMPR(mprUID);
        mpr.setMPRUpdateValid(newRevision.isMPRUpdateValid());
        if(mpr != null) //With the MPR, update...
        {
            //localId need to be same for MPR and Revision and it need to be set at backend
            newRevision.getThePersonDT().setLocalId(mpr.getThePersonDT().getLocalId());
            return update(mpr, newRevision);
        }
        else //No MPR.
        {
            throw new DataProcessingException("Cannot get a mpr for this person parent uid: "+ mprUID);
        }

    }

    private PersonVO getMPR(Long personUid) throws DataProcessingException {
        try
        {
            return getPersonInternal(personUid);
        }
        catch(Exception e)
        {
            logger.error("CreateException: cannot create an EntityController object."+e.getMessage(),e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private boolean update(PersonVO mpr, PersonVO newRevision)
            throws DataProcessingException
    {
        try {
            logger.debug("Starts update mpr, person uid = " + mpr.getThePersonDT().getPersonUid());
            logger.debug("Starts update mpr, person parent uid = " + mpr.getThePersonDT().getPersonParentUid());
            Collection<PersonVO> aNewRevisionList = new ArrayList<> ();
            aNewRevisionList.add(newRevision);

//            MPRUpdateVO mprUpdateVO = new MPRUpdateVO(mpr, aNewRevisionList);
//            logger.debug("Try to get a " + MPRUpdateEngineConstants.DEFAULT_HANDLER + " handler.");
//            MPRUpdateHandler handler = getHandler(MPRUpdateEngineConstants.DEFAULT_HANDLER);
//            logger.debug("The handler is: " + handler);

            if(process(mpr, aNewRevisionList))
            {
                return saveMPR(mpr);
            }else
            {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * Process MPR
     * */
    private boolean process(PersonVO personVO, Collection<PersonVO> aNewRevisionList ) {
        return true;
    }

    private boolean saveMPR(PersonVO mpr) throws DataProcessingException {
        return storeMPR(mpr, NEDSSConstant.PAT_EDIT);
    }

    private boolean storeMPR(PersonVO mpr, String businessTriggerCd) throws DataProcessingException {
        if(setMPR(mpr, businessTriggerCd) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Long setMPR(PersonVO personVO,  String businessTriggerCd) throws DataProcessingException {
        try {
            Long personUID = null;
            personVO.getThePersonDT().setEdxInd(NEDSSConstant.EDX_IND);
            if(personVO.isExt()){
                personVO.setItNew(false);
                personVO.setItDirty(false);
                if(personVO.isExt()){
                    if(personVO.getThePersonNameDTCollection()!=null){
                        Collection<PersonNameDT> coll = personVO.getThePersonNameDTCollection();
                        Iterator<PersonNameDT> itName = coll.iterator();
                        while (itName.hasNext()) {
                            PersonNameDT personNameDT = (PersonNameDT) itName.next();
                            personNameDT.setItDirty(true);
                            personNameDT.setItNew(false);

                        }
                    }
                }
            }
            if(personVO.isMPRUpdateValid()){
                personUID = setPersonInternal(personVO, NBSBOLookup.PATIENT, businessTriggerCd);

                try {

                    if(personVO.getThePersonDT().getPersonParentUid()==null)
                    {
                        personVO.getThePersonDT().setPersonParentUid(personUID);
                    }
                    setPatientHashCd(personVO);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return personUID;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("EntityControllerEJB.setMPR: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * @roseuid 3E7B380C036B
     * @J2EE_METHOD -- setPersonInternal
     */
    public Long setPersonInternal(PersonVO personVO,
                                  String businessObjLookupName, String businessTriggerCd
    ) throws  DataProcessingException {
        Long personUID = -1L;
        String localId = "";
        boolean isELRCase = false;
        try {
            if (personVO.isItNew() || personVO.isItDirty()) {

                // changed as per shannon and chase, keep the temp localid and
                // set it back to personDT after prepareVOUtils
                if (personVO.getThePersonDT().isItNew() && !(businessObjLookupName.equalsIgnoreCase(NEDSSConstant.businessObjLookupNamePROVIDER))) {
                    localId = personVO.getThePersonDT().getLocalId();
                }

                if(localId==null){
                    personVO.getThePersonDT().setEdxInd("Y");
                    isELRCase= true;
                }

                //TODO: Check this prep function out
                PersonDT personDT = preparingPersonUtil.prepareVO(personVO.getThePersonDT(), businessObjLookupName,
                        businessTriggerCd, "PERSON", "BASE");
//                PersonDT personDT = personVO.getThePersonDT();

                if (personVO.getThePersonDT().isItNew()
                        && !(businessObjLookupName
                        .equalsIgnoreCase(NEDSSConstant.businessObjLookupNamePROVIDER)))
                    personDT.setLocalId(localId);

                personVO.setThePersonDT((PersonDT) personDT);
                Collection<EntityLocatorParticipationDT> collEntityLocatorPar = null;
                Collection<RoleDT> colRole= null;
                Collection<ParticipationDT> colParticipation= null;


                collEntityLocatorPar = personVO.getTheEntityLocatorParticipationDTCollection();
                colRole = personVO.getTheRoleDTCollection();
                colParticipation = personVO.getTheParticipationDTCollection();

                if (collEntityLocatorPar != null) {
                    entityHelper.iterateELPDTForEntityLocatorParticipation(collEntityLocatorPar);

                    personVO.setTheEntityLocatorParticipationDTCollection(collEntityLocatorPar);
                }

                if (colRole != null) {
                    entityHelper.iterateRDT(colRole);

                    personVO.setTheRoleDTCollection(colRole);
                }

                if (colParticipation != null) {
                    entityHelper.iteratePDTForParticipation(colParticipation);
                    personVO.setTheParticipationDTCollection(colParticipation);
                }

                preparePersonNameBeforePersistence(personVO);


                if (personVO.isItNew()) {

                    Person person = createPerson(personVO);
                    personUID = person.getPersonUid();
                    logger.debug(" EntityControllerEJB.setProvider() Person Created");
                } else {
                    // TODO: Check this Update DB
                    //TODO: Check Legacy - DOES THIS DO ANYTHING?
                    //                person.setPersonVO(personVO);
//                    Person person = home.findByPrimaryKey(personVO
//                            .getThePersonDT().getPersonUid());
//
                    Person person = findExistingPersonByUid(personVO);
                    // person.setPersonVO(personVO);
//                    personUID = person.getPersonVO().getThePersonDT()
//                            .getPersonUid();
                    personUID = person.getPersonUid();
                    logger.debug(" EntityControllerEJB.setProvider() Person Updated");
                }
                if(isELRCase){
                    try {
                        personVO.getThePersonDT().setPersonUid(personUID);
                        personVO.getThePersonDT().setPersonParentUid(personUID);
                        setPatientHashCd(personVO);
                    } catch (Exception e) {
                        logger.error("RemoteException thrown while creating hashcode for the ELR patient."+e);
                        throw new DataProcessingException(e.getMessage(), e);

                    }
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(),e);
        }
        return personUID;
    }


    /**
     * @roseuid 3E7B17250186
     * @J2EE_METHOD -- preparePersonNameBeforePersistence
     */
    private void preparePersonNameBeforePersistence(PersonVO personVO ) throws DataProcessingException {
        try {
            Collection<PersonNameDT> namesCollection = personVO
                    .getThePersonNameDTCollection();
            if (namesCollection != null && namesCollection.size() > 0) {

                Iterator<PersonNameDT> namesIter = namesCollection.iterator();
                PersonNameDT selectedNameDT = null;
                while (namesIter.hasNext()) {
                    PersonNameDT thePersonNameDT = (PersonNameDT) namesIter.next();
                    if (thePersonNameDT.getNmUseCd() != null
                            && !thePersonNameDT.getNmUseCd().trim().equals("L"))
                        continue;
                    if (thePersonNameDT.getAsOfDate() != null) {
                        if (selectedNameDT == null)
                            selectedNameDT = thePersonNameDT;
                        else if (selectedNameDT.getAsOfDate()!=null && thePersonNameDT.getAsOfDate()!=null  && thePersonNameDT.getAsOfDate().after(
                                selectedNameDT.getAsOfDate())) {
                            selectedNameDT = thePersonNameDT;
                        }
                    } else {
                        if (selectedNameDT == null)
                            selectedNameDT = thePersonNameDT;
                    }
                }
                if (selectedNameDT != null) {
                    personVO.getThePersonDT().setLastNm(selectedNameDT.getLastNm());
                    personVO.getThePersonDT().setFirstNm(
                            selectedNameDT.getFirstNm());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("EntityControllerEJB.preparePersonNameBeforePersistence: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    public void setPatientHashCd(PersonVO personVO) throws DataProcessingException {

        try {
            long personUid = personVO.getThePersonDT().getPersonParentUid();
            edxPatientMatchRepositoryUtil.deleteEdxPatientMatchDTColl(personUid);
            try {
                if(personVO.getThePersonDT().getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)){
                    personVO.getThePersonDT().setPersonUid(personUid);
                    edxPatientMatchingCriteriaUtil.setPatientToEntityMatch(personVO);
                }
            } catch (Exception e) {
                //per defect #1836 change to warning..
                logger.warn("Unable to setPatientHashCd for personUid: "+personUid);
                logger.warn("Exception in setPatientToEntityMatch -> unhandled exception: " +e.getMessage());
            }
        } catch (Exception e) {
            logger.error("EntityControllerEJB.setPatientHashCd: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }

    }


    /**
     * @roseuid 3E7B38140232
     * @J2EE_METHOD -- getPersonInternal
     */
    public PersonVO getPersonInternal(Long personUID) throws DataProcessingException {
        PersonVO personVO = null;

        try {
            Person person = null;
            if (personUID != null)
                person = findExistingPersonByUid(personUID);
            // for LDFs
            if (person != null && (person.getElectronicInd() != null
                    && !personVO.getThePersonDT().getElectronicInd().equals(NEDSSConstant.ELECTRONIC_IND_ELR))) {
                ArrayList<Object> ldfList = new ArrayList<Object>();
                try {
                    //TODO: THis seem related to version control
//                    LDFHelper ldfHelper = LDFHelper.getInstance();
//                    ldfList = (ArrayList<Object>) ldfHelper.getLDFCollection(personUID, null, nbsSecurityObj);
                } catch (Exception e) {
                    logger.error("Exception occured while retrieving LDFCollection<Object>  = "
                            + e.toString());
                }

                if (ldfList != null) {
                    logger.debug("Before setting LDFCollection<Object>  = " + ldfList.size());
                    personVO.setTheStateDefinedFieldDataDTCollection(ldfList);
                }
            }

            logger.debug("Ent Controller past the find - person = " + person.toString());
            logger.debug("Ent Controllerpast the find - person.getPrimaryKey = " + person.getPersonUid());

        } catch (Exception e) {
            logger.error("EntityControllerEJB.getPersonInternal: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        return personVO;

    }


}
