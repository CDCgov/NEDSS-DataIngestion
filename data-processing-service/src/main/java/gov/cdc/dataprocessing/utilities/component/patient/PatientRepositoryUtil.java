package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.*;
import gov.cdc.dataprocessing.utilities.id_generator.UniqueIdGenerator;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
    private final LocalUidGeneratorRepository localUidGeneratorRepository;

    private final static String PERSON = "PERSON";

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
            PhysicalLocatorRepository physicalLocatorRepository, LocalUidGeneratorRepository localUidGeneratorRepository) {
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
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }

    @Transactional
    public Long updateExistingPersonEdxIndByUid(Long uid) {
        return (long) personRepository.updateExistingPersonEdxIndByUid(uid);
    }

    @Transactional
    public Person findExistingPersonByUid(Long personUid) {
        var result = personRepository.findById(personUid);
        return result.get();
    }

    @Transactional
    public Person createPerson(PersonVO personVO) throws DataProcessingException {
        //TODO: Implement unique id generator here
        Long personUid = 212121L;
        String localUid = "Unique Id here";
        var localIdModel = localUidGeneratorRepository.findById(PERSON);
        personUid = localIdModel.get().getSeedValueNbr();
        localUid = localIdModel.get().getUidPrefixCd() + personUid + localIdModel.get().getUidSuffixCd();

        LocalUidGenerator newGen = new LocalUidGenerator();
        newGen.setClassNameCd(localIdModel.get().getClassNameCd());
        newGen.setTypeCd(localIdModel.get().getTypeCd());
        newGen.setSeedValueNbr(localIdModel.get().getSeedValueNbr() + 1);
        newGen.setUidPrefixCd(localIdModel.get().getUidPrefixCd());
        newGen.setUidSuffixCd(localIdModel.get().getUidSuffixCd());
        localUidGeneratorRepository.save(newGen);

        ArrayList<Object>  arrayList = new ArrayList<>();

        if(personVO.getThePersonDT().getLocalId() == null || personVO.getThePersonDT().getLocalId().trim().length() == 0) {
            personVO.getThePersonDT().setLocalId(localUid);
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
        logger.debug("Person Uid\t" + person.getPersonUid());
        logger.debug("Person Parent Uid\t" + person.getPersonParentUid());

        return person;
    }

    @Transactional
    public void updateExistingPerson(PersonVO personVO) throws DataProcessingException {
        //TODO: Implement unique id generator here


        ArrayList<Object>  arrayList = new ArrayList<>();

        arrayList.add(NEDSSConstant.PERSON);

        //NOTE: Update Person
        Person person = new Person(personVO.getThePersonDT());
        try {
            personRepository.save(person);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

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
                updateEntityLocatorParticipation(personVO);
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
            List<PersonName> persons = personNameRepository.findBySeqIdByParentUid(pUid);

            Integer seqId = 0;

            StringBuilder sbFromInput = new StringBuilder();
            sbFromInput.append(personVO.getThePersonDT().getFirstNm());
            sbFromInput.append(personVO.getThePersonDT().getLastNm());
            sbFromInput.append(personVO.getThePersonDT().getMiddleNm());
            sbFromInput.append(personVO.getThePersonDT().getNmPrefix());
            sbFromInput.append(personVO.getThePersonDT().getNmSuffix());


            List<String> personNameForComparing = new ArrayList<>();
            for(PersonName item : persons) {
                StringBuilder sb = new StringBuilder();
                sb.append(item.getFirstNm());
                sb.append(item.getLastNm());
                sb.append(item.getMiddleNm());
                sb.append(item.getNmPrefix());
                sb.append(item.getNmSuffix());
                if(!personNameForComparing.contains(sb.toString().toUpperCase())) {
                    personNameForComparing.add(sb.toString().toUpperCase());
                }
            }


            //Only save new record if new name is actually new
            if (!personNameForComparing.contains(sbFromInput.toString().toUpperCase())) {
                persons = persons.stream().sorted(Comparator.comparing(PersonName::getPersonNameSeq).reversed()).collect(Collectors.toList());
                if (!persons.isEmpty()) {
                    seqId = persons.get(0).getPersonNameSeq();
                }

                for (PersonNameDT personNameDT : personList) {
                    seqId++;
                    personNameDT.setPersonUid(pUid);
                    if (personNameDT.getStatusCd() == null) {
                        personNameDT.setStatusCd("A");
                    }
                    if (personNameDT.getStatusTime() == null) {
                        personNameDT.setStatusTime(new Timestamp(new Date().getTime()));
                    }
                    personNameDT.setPersonNameSeq(seqId);
                    personNameDT.setRecordStatusCd("ACTIVE");
                    personNameDT.setAddReasonCd("Add");
                    personNameRepository.save(new PersonName(personNameDT));
                }
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
                personList.get(i).setRecordStatusCd("ACTIVE");
                personList.get(i).setAddReasonCd("Add");
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
                personList.get(i).setAddReasonCd("Add");
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
                personList.get(i).setAddReasonCd("Add");
                if (personList.get(i).getAddUserId() == null) {
                    personList.get(i).setAddUserId(123L);
                }
                if (personList.get(i).getLastChgUserId() == null) {
                    personList.get(i).setLastChgUserId(123L);
                }
                entityIdRepository.save(new EntityId(personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void updateEntityLocatorParticipation(PersonVO personVO) throws DataProcessingException {
        ArrayList<EntityLocatorParticipationDT>  personList = (ArrayList<EntityLocatorParticipationDT> ) personVO.getTheEntityLocatorParticipationDTCollection();
        List<EntityLocatorParticipation> entityLocatorParticipations = entityLocatorParticipationRepository.findByParentUid(personVO.getThePersonDT().getPersonUid()).get();

        if (!entityLocatorParticipations.isEmpty()) {
            List<EntityLocatorParticipation> physicalLocators = new ArrayList<>();
            List<EntityLocatorParticipation> postalLocators = new ArrayList<>();
            List<EntityLocatorParticipation> teleLocators = new ArrayList<>();

            physicalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                    .equalsIgnoreCase(NEDSSConstant.PHYSICAL))
                    .sorted(Comparator.comparing(EntityLocatorParticipation::getRecordStatusTime).reversed())
                    .collect(Collectors.toList());
            postalLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                    .equalsIgnoreCase(NEDSSConstant.POSTAL))
                    .sorted(Comparator.comparing(EntityLocatorParticipation::getRecordStatusTime).reversed())
                    .collect(Collectors.toList());
            teleLocators = entityLocatorParticipations.stream().filter(x -> x.getClassCd()
                    .equalsIgnoreCase(NEDSSConstant.TELE))
                    .sorted(Comparator.comparing(EntityLocatorParticipation::getRecordStatusTime).reversed())
                    .collect(Collectors.toList());


            EntityLocatorParticipation physicalLocator;
            EntityLocatorParticipation postalLocator;
            EntityLocatorParticipation teleLocator;

            StringBuilder comparingString = new StringBuilder();
            for(int i = 0; i < personList.size(); i++) {

                Long uniqueId = UniqueIdGenerator.generateUniqueId();
                boolean newLocator = true;
                if (personList.get(i).getClassCd().equals(NEDSSConstant.PHYSICAL) && personList.get(i).getThePhysicalLocatorDT() != null) {
                    newLocator = true;
                    if (!physicalLocators.isEmpty()) {
                       // physicalLocator = physicalLocators.get(0);
                       // var existingLocator = physicalLocatorRepository.findById(physicalLocator.getLocatorUid());

                        var existingLocator = physicalLocatorRepository.findByPhysicalLocatorUids(
                                physicalLocators.stream()
                                        .map(x -> x.getLocatorUid())
                                        .collect(Collectors.toList()));

                        List<String> compareStringList = new ArrayList<>();

                        if (existingLocator.isPresent()) {
                            for(int j = 0; j < existingLocator.get().size(); j++) {
                                comparingString.setLength(0);
                                comparingString.append(existingLocator.get().get(j).getImageTxt());
                                compareStringList.add(comparingString.toString().toUpperCase());
                            }


                            if (!compareStringList.contains(personList.get(i).getThePhysicalLocatorDT().getImageTxt().toString().toUpperCase())) {
                                personList.get(i).getThePhysicalLocatorDT().setPhysicalLocatorUid(uniqueId);
                                physicalLocatorRepository.save(new PhysicalLocator(personList.get(i).getThePhysicalLocatorDT()));
                            }
                            else {
                                newLocator = false;
                            }
                        }
                        else {
                            personList.get(i).getThePhysicalLocatorDT().setPhysicalLocatorUid(uniqueId);
                            physicalLocatorRepository.save(new PhysicalLocator(personList.get(i).getThePhysicalLocatorDT()));
                        }

                        comparingString.setLength(0);
                    }
                    else {
                        personList.get(i).getThePhysicalLocatorDT().setPhysicalLocatorUid(uniqueId);
                        physicalLocatorRepository.save(new PhysicalLocator(personList.get(i).getThePhysicalLocatorDT()));
                    }
                }
                if (personList.get(i).getClassCd().equals(NEDSSConstant.POSTAL) && personList.get(i).getThePostalLocatorDT() != null) {
                    newLocator = true;
                    if (!postalLocators.isEmpty()) {
                        var existingLocator = postalLocatorRepository.findByPostalLocatorUids(
                                postalLocators.stream()
                                        .map(x -> x.getLocatorUid())
                                        .collect(Collectors.toList()));

                        List<String> compareStringList = new ArrayList<>();
                        if (existingLocator.isPresent()) {
                            for(int j = 0; j < existingLocator.get().size(); j++) {
                                comparingString.setLength(0);
                                comparingString.append(existingLocator.get().get(j).getCityCd());
                                comparingString.append(existingLocator.get().get(j).getCityDescTxt());
                                comparingString.append(existingLocator.get().get(j).getCntryCd());
                                comparingString.append(existingLocator.get().get(j).getCntryDescTxt());
                                comparingString.append(existingLocator.get().get(j).getCntyCd());
                                comparingString.append(existingLocator.get().get(j).getCntyDescTxt());
                                comparingString.append(existingLocator.get().get(j).getStateCd());
                                comparingString.append(existingLocator.get().get(j).getStreetAddr1());
                                comparingString.append(existingLocator.get().get(j).getStreetAddr2());
                                comparingString.append(existingLocator.get().get(j).getZipCd());

                                compareStringList.add(comparingString.toString().toUpperCase());
                            }


                            StringBuilder existComparingLocator = new StringBuilder();
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getCityCd());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getCityDescTxt());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getCntryCd());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getCntryDescTxt());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getCntyCd());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getCntyDescTxt());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getStateCd());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getStreetAddr1());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getStreetAddr2());
                            existComparingLocator.append(personList.get(i).getThePostalLocatorDT().getZipCd());


                            if (!compareStringList.contains(existComparingLocator.toString().toUpperCase())) {
                                personList.get(i).getThePostalLocatorDT().setPostalLocatorUid(uniqueId);
                                postalLocatorRepository.save(new PostalLocator(personList.get(i).getThePostalLocatorDT()));
                            }
                            else {
                                newLocator = false;
                            }
                        }
                        else {
                            personList.get(i).getThePostalLocatorDT().setPostalLocatorUid(uniqueId);
                            postalLocatorRepository.save(new PostalLocator(personList.get(i).getThePostalLocatorDT()));
                        }
                        comparingString.setLength(0);
                    }
                    else {
                        personList.get(i).getThePostalLocatorDT().setPostalLocatorUid(uniqueId);
                        postalLocatorRepository.save(new PostalLocator(personList.get(i).getThePostalLocatorDT()));
                    }
                }
                if (personList.get(i).getClassCd().equals(NEDSSConstant.TELE) && personList.get(i).getTheTeleLocatorDT() != null) {
                    newLocator = true;
                    if (!teleLocators.isEmpty()) {
//                        teleLocator = teleLocators.get(0);
//                        var existingLocator = teleLocatorRepository.findById(teleLocator.getLocatorUid());
                        var existingLocator = teleLocatorRepository.findByTeleLocatorUids(
                                teleLocators.stream()
                                        .map(x -> x.getLocatorUid())
                                        .collect(Collectors.toList()));
                        List<String> compareStringList = new ArrayList<>();

                        if (existingLocator.isPresent()) {
                            for(int j = 0; j < existingLocator.get().size(); j++) {
                                comparingString.setLength(0);
                                comparingString.append(existingLocator.get().get(j).getCntryCd());
                                comparingString.append(existingLocator.get().get(j).getEmailAddress());
                                comparingString.append(existingLocator.get().get(j).getExtensionTxt());
                                comparingString.append(existingLocator.get().get(j).getPhoneNbrTxt());
                                comparingString.append(existingLocator.get().get(j).getUrlAddress());
                                compareStringList.add(comparingString.toString().toUpperCase());
                            }

                            StringBuilder existComparingLocator = new StringBuilder();
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDT().getCntryCd());
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDT().getEmailAddress());
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDT().getExtensionTxt());
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDT().getPhoneNbrTxt());
                            existComparingLocator.append(personList.get(i).getTheTeleLocatorDT().getUrlAddress());

                            if (!compareStringList.contains(existComparingLocator.toString().toUpperCase())) {
                                personList.get(i).getTheTeleLocatorDT().setTeleLocatorUid(uniqueId);
                                teleLocatorRepository.save(new TeleLocator(personList.get(i).getTheTeleLocatorDT()));
                            }
                            else {
                                newLocator = false;
                            }
                        }
                        else {
                            personList.get(i).getTheTeleLocatorDT().setTeleLocatorUid(uniqueId);
                            teleLocatorRepository.save(new TeleLocator(personList.get(i).getTheTeleLocatorDT()));
                        }

                        comparingString.setLength(0);
                    }
                    else {
                        personList.get(i).getTheTeleLocatorDT().setTeleLocatorUid(uniqueId);
                        teleLocatorRepository.save(new TeleLocator(personList.get(i).getTheTeleLocatorDT()));
                    }
                }

                // ONLY persist new participation locator if new locator actually exist
                if (newLocator) {
                    personList.get(i).setEntityUid(personVO.getThePersonDT().getPersonUid());
                    personList.get(i).setLocatorUid(uniqueId);

                    if (personList.get(i).getVersionCtrlNbr() == null) {
                        personList.get(i).setVersionCtrlNbr(1);
                    }
                    entityLocatorParticipationRepository.save(new EntityLocatorParticipation(personList.get(i)));
                }

            }
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
    @Transactional
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
