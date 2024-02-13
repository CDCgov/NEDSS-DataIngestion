package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.*;
import gov.cdc.dataprocessing.utilities.UniqueIdGenerator;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

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
    private final TeleLocatorRepository teleLocatorRepository;
    private final PostalLocatorRepository postalLocatorRepository;
    private final PhysicalLocatorRepository physicalLocatorRepository;


    public PatientRepositoryUtil(
            PersonRepository personRepository,
            EntityRepositoryUtil entityRepositoryUtil,
            PersonNameRepository personNameRepository,
            PersonRaceRepository personRaceRepository,
            PersonEthnicRepository personEthnicRepository,
            EntityIdRepository entityIdRepository,
            EntityLocatorParticipationRepository entityLocatorParticipationRepository,
            RoleRepository roleRepository,
            TeleLocatorRepository teleLocatorRepository,
            PostalLocatorRepository postalLocatorRepository,
            PhysicalLocatorRepository physicalLocatorRepository) {
        this.personRepository = personRepository;
        this.entityRepositoryUtil = entityRepositoryUtil;
        this.personNameRepository = personNameRepository;
        this.personRaceRepository = personRaceRepository;
        this.personEthnicRepository = personEthnicRepository;
        this.entityIdRepository = entityIdRepository;
        this.entityLocatorParticipationRepository = entityLocatorParticipationRepository;
        this.roleRepository = roleRepository;
        this.teleLocatorRepository = teleLocatorRepository;
        this.postalLocatorRepository = postalLocatorRepository;
        this.physicalLocatorRepository = physicalLocatorRepository;
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

        if(personVO.getThePersonDT().getLocalId() == null || personVO.getThePersonDT().getLocalId().trim().length() == 0) {
            personVO.getThePersonDT().setLocalId("PSN" + localUid + "GA01");
        }

        if(personVO.getThePersonDT().getPersonParentUid() == null) {
            personVO.getThePersonDT().setPersonParentUid(personUid);
        }

        // set new person uid in entity table
        personVO.getThePersonDT().setPersonUid(personUid);

        arrayList.add(personUid);
        arrayList.add(NEDSSConstant.PERSON);

        //NOTE: Create Entitty
        try {
            //NOTE: OK
            entityRepositoryUtil.preparingEntityReposCallForPerson(personVO.getThePersonDT(), personUid, NEDSSConstant.PERSON, NEDSSConstant.UPDATE);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        //NOTE: Create Person
        Person person = new Person(personVO.getThePersonDT());
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

    public void updateExistingPerson(PersonVO personVO) throws DataProcessingException {
        //TODO: Implement unique id generator here


        ArrayList<Object>  arrayList = new ArrayList<>();

        arrayList.add(NEDSSConstant.PERSON);


        //NOTE: Create Person Name
        if  (personVO.getThePersonNameDTCollection() != null && !personVO.getThePersonNameDTCollection().isEmpty()) {
            try {
                updatePersonName(personVO);
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


        //NOTE: Upsert EntityID
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
        //NOTE: Upsert Role
        if  (personVO.getTheRoleDTCollection() != null && !personVO.getTheRoleDTCollection().isEmpty()) {
            try {
                createRole(personVO);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }

    }

    private void updatePersonName(PersonVO personVO) throws DataProcessingException {
        ArrayList<PersonNameDT>  personList = (ArrayList<PersonNameDT> ) personVO.getThePersonNameDTCollection();
        try {
            var pUid = personVO.getThePersonDT().getPersonUid();
            List<Integer> personNameSeqId = personNameRepository.findBySeqIdByParentUid(pUid);
            Integer seqId = 0;
            if (!personNameSeqId.isEmpty()) {
                seqId = personNameSeqId.get(0);
            }

            for(int i = 0; i < personList.size(); i++) {
                seqId++;
                personList.get(i).setPersonUid(pUid);
                if (personList.get(i).getStatusCd() == null) {
                    personList.get(i).setStatusCd("A");
                }
                if (personList.get(i).getStatusTime() == null) {
                    personList.get(i).setStatusTime(new Timestamp(new Date().getTime()));
                }
                personList.get(i).setPersonNameSeq(seqId);
                personNameRepository.save(new PersonName( personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createPersonName(PersonVO personVO) throws DataProcessingException {
        ArrayList<PersonNameDT>  personList = (ArrayList<PersonNameDT> ) personVO.getThePersonNameDTCollection();
        try {
            var pUid = personVO.getThePersonDT().getPersonUid();
            for(int i = 0; i < personList.size(); i++) {
                personList.get(i).setPersonUid(pUid);
                if (personList.get(i).getStatusCd() == null) {
                    personList.get(i).setStatusCd("A");
                }
                if (personList.get(i).getStatusTime() == null) {
                    personList.get(i).setStatusTime(new Timestamp(new Date().getTime()));
                }
                personNameRepository.save(new PersonName( personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createPersonRace(PersonVO personVO) throws DataProcessingException {
        ArrayList<PersonRaceDT>  personList = (ArrayList<PersonRaceDT> ) personVO.getThePersonRaceDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                var pUid = personVO.getThePersonDT().getPersonUid();
                personList.get(i).setPersonUid(pUid);
                personRaceRepository.save(new PersonRace(personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createPersonEthnic(PersonVO personVO) throws DataProcessingException {
        ArrayList<PersonEthnicGroupDT>  personList = (ArrayList<PersonEthnicGroupDT> ) personVO.getThePersonEthnicGroupDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                var pUid = personVO.getThePersonDT().getPersonUid();
                personList.get(i).setPersonUid(pUid);
                personEthnicRepository.save(new PersonEthnicGroup(personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityId(PersonVO personVO) throws DataProcessingException {
        ArrayList<EntityIdDT>  personList = (ArrayList<EntityIdDT> ) personVO.getTheEntityIdDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                var pUid = personVO.getThePersonDT().getPersonUid();
                personList.get(i).setEntityUid(pUid);
                entityIdRepository.save(new EntityId(personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityLocatorParticipation(PersonVO personVO) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDT>  personList = (ArrayList<EntityLocatorParticipationDT> ) personVO.getTheEntityLocatorParticipationDTCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {

                Long uniqueId = UniqueIdGenerator.generateUniqueId();
                if (personList.get(i).getClassCd().equals(NEDSSConstant.PHYSICAL) && personList.get(i).getThePhysicalLocatorDT() != null) {
                    personList.get(i).getThePhysicalLocatorDT().setPhysicalLocatorUid(uniqueId);
                    physicalLocatorRepository.save(new PhysicalLocator(personList.get(i).getThePhysicalLocatorDT()));
                }
                if (personList.get(i).getClassCd().equals(NEDSSConstant.POSTAL) && personList.get(i).getThePostalLocatorDT() != null) {
                    personList.get(i).getThePostalLocatorDT().setPostalLocatorUid(uniqueId);
                    postalLocatorRepository.save(new PostalLocator(personList.get(i).getThePostalLocatorDT()));
                }
                if (personList.get(i).getClassCd().equals(NEDSSConstant.TELE) && personList.get(i).getTheTeleLocatorDT() != null) {
                    personList.get(i).getTheTeleLocatorDT().setTeleLocatorUid(uniqueId);
                    teleLocatorRepository.save(new TeleLocator(personList.get(i).getTheTeleLocatorDT()));
                }
                personList.get(i).setEntityUid(personVO.getThePersonDT().getPersonUid());
                personList.get(i).setLocatorUid(uniqueId);

                if (personList.get(i).getVersionCtrlNbr() == null) {
                    personList.get(i).setVersionCtrlNbr(1);
                }
                entityLocatorParticipationRepository.save(new EntityLocatorParticipation(personList.get(i)));
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
    public PersonVO preparePersonNameBeforePersistence(PersonVO personVO) throws DataProcessingException {
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

        return personVO;
    }




}
