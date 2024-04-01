package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonEthnicRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonNameRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.service.interfaces.entity.IEntityLocatorParticipationService;
import gov.cdc.dataprocessing.service.interfaces.other.IOdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
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

    private final PrepareAssocModelHelper prepareAssocModelHelper;
//    private final EntityLocatorParticipationRepository entityLocatorParticipationRepository;
    private final RoleRepository roleRepository;
//    private final TeleLocatorRepository teleLocatorRepository;
//    private final PostalLocatorRepository postalLocatorRepository;
//    private final PhysicalLocatorRepository physicalLocatorRepository;
    private final IOdseIdGeneratorService odseIdGeneratorService;

    private final IEntityLocatorParticipationService entityLocatorParticipationService;


    public PatientRepositoryUtil(
            PersonRepository personRepository,
            EntityRepositoryUtil entityRepositoryUtil,
            PersonNameRepository personNameRepository,
            PersonRaceRepository personRaceRepository,
            PersonEthnicRepository personEthnicRepository,
            EntityIdRepository entityIdRepository,
            PrepareAssocModelHelper prepareAssocModelHelper,
            RoleRepository roleRepository,
            IOdseIdGeneratorService odseIdGeneratorService,
            IEntityLocatorParticipationService entityLocatorParticipationService) {
        this.personRepository = personRepository;
        this.entityRepositoryUtil = entityRepositoryUtil;
        this.personNameRepository = personNameRepository;
        this.personRaceRepository = personRaceRepository;
        this.personEthnicRepository = personEthnicRepository;
        this.entityIdRepository = entityIdRepository;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.roleRepository = roleRepository;
        this.odseIdGeneratorService = odseIdGeneratorService;
        this.entityLocatorParticipationService = entityLocatorParticipationService;
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
    public Person createPerson(PersonContainer personContainer) throws DataProcessingException {
        Long personUid;
        String localUid;
        //var localIdModel = localUidGeneratorRepository.findById(PERSON);
        var localIdModel = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.PERSON);
        personUid = localIdModel.getSeedValueNbr();
        localUid = localIdModel.getUidPrefixCd() + personUid + localIdModel.getUidSuffixCd();


        ArrayList<Object>  arrayList = new ArrayList<>();

        if(personContainer.getThePersonDto().getLocalId() == null || personContainer.getThePersonDto().getLocalId().trim().length() == 0) {
            personContainer.getThePersonDto().setLocalId(localUid);
        }

        if(personContainer.getThePersonDto().getPersonParentUid() == null) {
            personContainer.getThePersonDto().setPersonParentUid(personUid);
        }

        // set new person uid in entity table
        personContainer.getThePersonDto().setPersonUid(personUid);

        arrayList.add(personUid);
        arrayList.add(NEDSSConstant.PERSON);

//        prepareAssocModelHelper.prepareVO(
//                personContainer.getThePersonDto(),
//                NEDSSConstant.PATIENT,
//                NEDSSConstant.PER_CR,
//                "Person",
//                NEDSSConstant.BASE);

        //NOTE: Create Entitty
        try {
            //NOTE: OK
            entityRepositoryUtil.preparingEntityReposCallForPerson(personContainer.getThePersonDto(), personUid, NEDSSConstant.PERSON, NEDSSConstant.UPDATE);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        //NOTE: Create Person
        Person person = new Person(personContainer.getThePersonDto());
        try {
            personRepository.save(person);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        //NOTE: Create Person Name
        if  (personContainer.getThePersonNameDtoCollection() != null && !personContainer.getThePersonNameDtoCollection().isEmpty()) {
            try {
                createPersonName(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Person Race
        if  (personContainer.getThePersonRaceDtoCollection() != null && !personContainer.getThePersonRaceDtoCollection().isEmpty()) {
            try {
                createPersonRace(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Person Ethnic
        if  (personContainer.getThePersonEthnicGroupDtoCollection() != null && !personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
            try {
                createPersonEthnic(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create EntityID
        if  (personContainer.getTheEntityIdDtoCollection() != null && !personContainer.getTheEntityIdDtoCollection().isEmpty()) {
            try {
                createEntityId(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Entity Locator Participation
        if  (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            try {
                entityLocatorParticipationService.createEntityLocatorParticipation(personContainer.getTheEntityLocatorParticipationDtoCollection(), personContainer.getThePersonDto().getPersonUid());
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Role
        if  (personContainer.getTheRoleDtoCollection() != null && !personContainer.getTheRoleDtoCollection().isEmpty()) {
            try {
                createRole(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        logger.debug("Person Uid\t" + person.getPersonUid());
        logger.debug("Person Parent Uid\t" + person.getPersonParentUid());

        return person;
    }

    @Transactional
    public void updateExistingPerson(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<Object>  arrayList = new ArrayList<>();

        arrayList.add(NEDSSConstant.PERSON);

//        prepareAssocModelHelper.prepareVO(
//                personContainer.getThePersonDto(),
//                NEDSSConstant.PATIENT,
//                NEDSSConstant.PER_CR,
//                "Person",
//                NEDSSConstant.BASE);

        //NOTE: Update Person
        Person person = new Person(personContainer.getThePersonDto());
        try {
            personRepository.save(person);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        //NOTE: Create Person Name
        if  (personContainer.getThePersonNameDtoCollection() != null && !personContainer.getThePersonNameDtoCollection().isEmpty()) {
            try {
                updatePersonName(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Person Race
        if  (personContainer.getThePersonRaceDtoCollection() != null && !personContainer.getThePersonRaceDtoCollection().isEmpty()) {
            try {
                createPersonRace(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Create Person Ethnic
        if  (personContainer.getThePersonEthnicGroupDtoCollection() != null && !personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
            try {
                createPersonEthnic(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }


        //NOTE: Upsert EntityID
        if  (personContainer.getTheEntityIdDtoCollection() != null && !personContainer.getTheEntityIdDtoCollection().isEmpty()) {
            try {
                createEntityId(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }


        //NOTE: Create Entity Locator Participation
        if  (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            try {
                entityLocatorParticipationService.updateEntityLocatorParticipation(personContainer.getTheEntityLocatorParticipationDtoCollection(), personContainer.getThePersonDto().getPersonUid());
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        //NOTE: Upsert Role
        if  (personContainer.getTheRoleDtoCollection() != null && !personContainer.getTheRoleDtoCollection().isEmpty()) {
            try {
                createRole(personContainer);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        }

    }

    public PersonContainer loadPerson(Long personUid) {
        PersonContainer personContainer = new PersonContainer();

        PersonDto personDto = null;
        var personResult = personRepository.findById(personUid);
        if (personResult.isPresent()) {
            personDto = new PersonDto(personResult.get());
            personDto.setItDirty(false);
            personDto.setItNew(false);
        }
        personContainer.setThePersonDto(personDto);

        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();
        var personNameResult = personNameRepository.findByParentUid(personUid);
        if (personResult.isPresent()) {
            for(var item : personNameResult.get()) {
                var elem = new PersonNameDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                personNameDtoCollection.add(elem);
            }
        }
        personContainer.setThePersonNameDtoCollection(personNameDtoCollection);

        Collection<PersonRaceDto> personRaceDtoCollection = new ArrayList<>();
        var personRaceResult = personRaceRepository.findByParentUid(personUid);
        if (personRaceResult.isPresent()) {
            for(var item : personRaceResult.get()) {
                var elem = new PersonRaceDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                personRaceDtoCollection.add(elem);
            }
        }
        personContainer.setThePersonRaceDtoCollection(personRaceDtoCollection);

        Collection<PersonEthnicGroupDto> personEthnicGroupDtoCollection = new ArrayList<>();
        var personEthnic = personEthnicRepository.findByParentUid(personUid);
        if (personEthnic.isPresent()) {
            for(var item : personEthnic.get()) {
                var elem = new PersonEthnicGroupDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                personEthnicGroupDtoCollection.add(elem);
            }
        }
        personContainer.setThePersonEthnicGroupDtoCollection(personEthnicGroupDtoCollection);

        Collection<EntityIdDto> entityIdDtoCollection = new ArrayList<>();
        var entityIdResult = entityIdRepository.findByParentUid(personUid);
        if (entityIdResult.isPresent()) {
            for(var item : entityIdResult.get()) {
                var elem = new EntityIdDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                entityIdDtoCollection.add(elem);
            }
        }
        personContainer.setTheEntityIdDtoCollection(entityIdDtoCollection);

        Collection<EntityLocatorParticipationDto> entityLocatorParticipationDtoCollection = new ArrayList<>();
        var entityLocatorResult = entityLocatorParticipationService.findEntityLocatorById(personUid);
        for(var item : entityLocatorResult) {
            var elem = new EntityLocatorParticipationDto(item);
            elem.setItDirty(false);
            elem.setItNew(false);
            entityLocatorParticipationDtoCollection.add(elem);
        }

        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorParticipationDtoCollection);


        Collection<RoleDto> roleDtoCollection = new ArrayList<>();
        var roleResult = roleRepository.findByParentUid(personUid);
        if (roleResult.isPresent()) {
            for(var item : roleResult.get()) {
                var elem = new RoleDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                roleDtoCollection.add(elem);
            }
        }
        personContainer.setTheRoleDtoCollection(roleDtoCollection);


        personContainer.setItDirty(false);
        personContainer.setItNew(false);
        return personContainer;
    }

    public Long findPatientParentUidByUid(Long personUid) {
        var result = personRepository.findPatientParentUidByUid(personUid);
        return result.map(longs -> longs.get(0)).orElse(null);
    }
    private void updatePersonName(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonNameDto>  personList = (ArrayList<PersonNameDto> ) personContainer.getThePersonNameDtoCollection();
        try {
            var pUid = personContainer.getThePersonDto().getPersonUid();
            List<PersonName> persons = personNameRepository.findBySeqIdByParentUid(pUid);

            Integer seqId = 0;

            StringBuilder sbFromInput = new StringBuilder();
            sbFromInput.append(personContainer.getThePersonDto().getFirstNm());
            sbFromInput.append(personContainer.getThePersonDto().getLastNm());
            sbFromInput.append(personContainer.getThePersonDto().getMiddleNm());
            sbFromInput.append(personContainer.getThePersonDto().getNmPrefix());
            sbFromInput.append(personContainer.getThePersonDto().getNmSuffix());


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

                for (PersonNameDto personNameDto : personList) {
                    seqId++;
                    personNameDto.setPersonUid(pUid);
                    if (personNameDto.getStatusCd() == null) {
                        personNameDto.setStatusCd("A");
                    }
                    if (personNameDto.getStatusTime() == null) {
                        personNameDto.setStatusTime(new Timestamp(new Date().getTime()));
                    }
                    personNameDto.setPersonNameSeq(seqId);
                    personNameDto.setRecordStatusCd("ACTIVE");
                    personNameDto.setAddReasonCd("Add");
                    personNameRepository.save(new PersonName(personNameDto));
                }
            }


        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createPersonName(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonNameDto>  personList = (ArrayList<PersonNameDto> ) personContainer.getThePersonNameDtoCollection();
        try {
            var pUid = personContainer.getThePersonDto().getPersonUid();
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

    private void createPersonRace(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonRaceDto>  personList = (ArrayList<PersonRaceDto> ) personContainer.getThePersonRaceDtoCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                var pUid = personContainer.getThePersonDto().getPersonUid();
                personList.get(i).setPersonUid(pUid);
                personList.get(i).setAddReasonCd("Add");
                personRaceRepository.save(new PersonRace(personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createPersonEthnic(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonEthnicGroupDto>  personList = (ArrayList<PersonEthnicGroupDto> ) personContainer.getThePersonEthnicGroupDtoCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                var pUid = personContainer.getThePersonDto().getPersonUid();
                personList.get(i).setPersonUid(pUid);
                personEthnicRepository.save(new PersonEthnicGroup(personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityId(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<EntityIdDto>  personList = (ArrayList<EntityIdDto> ) personContainer.getTheEntityIdDtoCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                var pUid = personContainer.getThePersonDto().getPersonUid();
                personList.get(i).setEntityUid(pUid);
                personList.get(i).setAddReasonCd("Add");
                if (personList.get(i).getAddUserId() == null) {
                    personList.get(i).setAddUserId(AuthUtil.authUser.getAuthUserUid());
                }
                if (personList.get(i).getLastChgUserId() == null) {
                    personList.get(i).setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
                }
                entityIdRepository.save(new EntityId(personList.get(i)));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private void createRole(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<RoleDto>  personList = (ArrayList<RoleDto> ) personContainer.getTheRoleDtoCollection();
        try {
            for(int i = 0; i < personList.size(); i++) {
                RoleDto obj = personList.get(i);
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
    public PersonContainer preparePersonNameBeforePersistence(PersonContainer personContainer) throws DataProcessingException {
        try {
            Collection<PersonNameDto> namesCollection = personContainer
                    .getThePersonNameDtoCollection();
            if (namesCollection != null && namesCollection.size() > 0) {

                Iterator<PersonNameDto> namesIter = namesCollection.iterator();
                PersonNameDto selectedNameDT = null;
                while (namesIter.hasNext()) {
                    PersonNameDto thePersonNameDto = (PersonNameDto) namesIter.next();
                    if (thePersonNameDto.getNmUseCd() != null
                            && !thePersonNameDto.getNmUseCd().trim().equals("L"))
                        continue;
                    if (thePersonNameDto.getAsOfDate() != null) {
                        if (selectedNameDT == null)
                            selectedNameDT = thePersonNameDto;
                        else if (selectedNameDT.getAsOfDate()!=null && thePersonNameDto.getAsOfDate()!=null  && thePersonNameDto.getAsOfDate().after(
                                selectedNameDT.getAsOfDate())) {
                            selectedNameDT = thePersonNameDto;
                        }
                    } else {
                        if (selectedNameDT == null)
                            selectedNameDT = thePersonNameDto;
                    }
                }
                if (selectedNameDT != null) {
                    personContainer.getThePersonDto().setLastNm(selectedNameDT.getLastNm());
                    personContainer.getThePersonDto().setFirstNm(
                            selectedNameDT.getFirstNm());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return personContainer;
    }




}
