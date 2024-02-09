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


    public PatientRepositoryUtil(
            PersonRepository personRepository,
            EntityRepositoryUtil entityRepositoryUtil,
            PersonNameRepository personNameRepository,
            PersonRaceRepository personRaceRepository,
            PersonEthnicRepository personEthnicRepository,
            EntityIdRepository entityIdRepository,
            EntityLocatorParticipationRepository entityLocatorParticipationRepository,
            RoleRepository roleRepository
             ) {
        this.personRepository = personRepository;
        this.entityRepositoryUtil = entityRepositoryUtil;
        this.personNameRepository = personNameRepository;
        this.personRaceRepository = personRaceRepository;
        this.personEthnicRepository = personEthnicRepository;
        this.entityIdRepository = entityIdRepository;
        this.entityLocatorParticipationRepository = entityLocatorParticipationRepository;
        this.roleRepository = roleRepository;
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




    /**
     * @roseuid 3E7B17250186
     * @J2EE_METHOD -- preparePersonNameBeforePersistence
     */
    public void preparePersonNameBeforePersistence(PersonVO personVO) throws DataProcessingException {
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




}
