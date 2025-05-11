package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.EntityIdJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.PersonJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.RoleJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonEthnicRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonNameRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.service.interfaces.entity.IEntityLocatorParticipationService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class PatientRepositoryUtil {
    @Value("${feature.jdbc-flag}")
    private boolean jdbcFlag = true;
    private static final Logger logger = LoggerFactory.getLogger(PatientRepositoryUtil.class);
    @Value("${service.timezone}")
    private String tz = "UTC";

    @Value("${feature.thread-enabled}")
    private boolean threadEnabled = false;

    private final PersonRepository personRepository;
    private final EntityRepositoryUtil entityRepositoryUtil;
    private final PersonNameRepository personNameRepository;
    private final PersonRaceRepository personRaceRepository;
    private final PersonEthnicRepository personEthnicRepository;
    private final EntityIdRepository entityIdRepository;

    private final PersonJdbcRepository personJdbcRepository;
    private final RoleJdbcRepository roleJdbcRepository;
    private final EntityIdJdbcRepository entityIdJdbcRepository;


    private final DataModifierReposJdbc dataModifierReposJdbc;

    private final RoleRepository roleRepository;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;

    private final IEntityLocatorParticipationService entityLocatorParticipationService;

    private static final String ERROR_DELETE_MSG = "Error Delete Patient Entity: ";
    private static final String ERROR_UPDATE_MSG = "Error Updating Existing Patient Entity: ";


    public PatientRepositoryUtil(
            PersonRepository personRepository,
            EntityRepositoryUtil entityRepositoryUtil,
            PersonNameRepository personNameRepository,
            PersonRaceRepository personRaceRepository,
            PersonEthnicRepository personEthnicRepository,
            EntityIdRepository entityIdRepository,
            PersonJdbcRepository personJdbcRepository,
            RoleJdbcRepository roleJdbcRepository,
            EntityIdJdbcRepository entityIdJdbcRepository,
            DataModifierReposJdbc dataModifierReposJdbc,
            RoleRepository roleRepository,
            IOdseIdGeneratorWCacheService odseIdGeneratorService1,
            IEntityLocatorParticipationService entityLocatorParticipationService) {
        this.personRepository = personRepository;
        this.entityRepositoryUtil = entityRepositoryUtil;
        this.personNameRepository = personNameRepository;
        this.personRaceRepository = personRaceRepository;
        this.personEthnicRepository = personEthnicRepository;
        this.entityIdRepository = entityIdRepository;
        this.personJdbcRepository = personJdbcRepository;
        this.roleJdbcRepository = roleJdbcRepository;
        this.entityIdJdbcRepository = entityIdJdbcRepository;
        this.dataModifierReposJdbc = dataModifierReposJdbc;
        this.roleRepository = roleRepository;
        this.odseIdGeneratorService = odseIdGeneratorService1;
        this.entityLocatorParticipationService = entityLocatorParticipationService;
    }

    public Long updateExistingPersonEdxIndByUid(Long uid) {
        return (long) dataModifierReposJdbc.updateExistingPersonEdxIndByUid(uid);
    }

    public Person findExistingPersonByUid(Long personUid) {
        var result = personRepository.findById(personUid);
        return result.orElse(null);
    }

//    public Person createPerson(PersonContainer personContainer) throws DataProcessingException {
//        Long personUid;
//        String localUid;
//        var localIdModel = odseIdGeneratorService.getValidLocalUid(LocalIdClass.PERSON, true);
//        personUid = localIdModel.getGaTypeUid().getSeedValueNbr();
//        localUid = localIdModel.getClassTypeUid().getUidPrefixCd() + localIdModel.getClassTypeUid().getSeedValueNbr() + localIdModel.getClassTypeUid().getUidSuffixCd();
//
//
//        ArrayList<Object>  arrayList = new ArrayList<>();
//
//        if(personContainer.getThePersonDto().getLocalId() == null || personContainer.getThePersonDto().getLocalId().trim().isEmpty()) {
//            personContainer.getThePersonDto().setLocalId(localUid);
//        }
//
//        if(personContainer.getThePersonDto().getPersonParentUid() == null) {
//            personContainer.getThePersonDto().setPersonParentUid(personUid);
//        }
//
//        // set new person uid in entity table
//        personContainer.getThePersonDto().setPersonUid(personUid);
//
//        arrayList.add(personUid);
//        arrayList.add(NEDSSConstant.PERSON);
//
//        entityRepositoryUtil.preparingEntityReposCallForPerson(personContainer.getThePersonDto(), personUid, NEDSSConstant.PERSON, NEDSSConstant.UPDATE);
//
//
//        //NOTE: Create Person
//        Person person = new Person(personContainer.getThePersonDto(), tz);
//        person.setBirthCntryCd(null);
//        if (jdbcFlag) {
//            personJdbcRepository.createPerson(person);
//        }
//        else {
//            personRepository.save(person);
//        }
//        createPersonName(personContainer);
//        createPersonRace(personContainer);
//        createPersonEthnic(personContainer);
//        createEntityId(personContainer);
//        entityLocatorParticipationService.createEntityLocatorParticipation(personContainer.getTheEntityLocatorParticipationDtoCollection(), personContainer.getThePersonDto().getPersonUid());
//        createRole(personContainer, "CREATE");
//
//        return person;
//    }


    public Person createPerson(PersonContainer personContainer) throws DataProcessingException {
        Long personUid;
        String localUid;

        var localIdModel = odseIdGeneratorService.getValidLocalUid(LocalIdClass.PERSON, true);
        personUid = localIdModel.getGaTypeUid().getSeedValueNbr();
        localUid = localIdModel.getClassTypeUid().getUidPrefixCd()
                + localIdModel.getClassTypeUid().getSeedValueNbr()
                + localIdModel.getClassTypeUid().getUidSuffixCd();

        if (personContainer.getThePersonDto().getLocalId() == null || personContainer.getThePersonDto().getLocalId().trim().isEmpty()) {
            personContainer.getThePersonDto().setLocalId(localUid);
        }

        if (personContainer.getThePersonDto().getPersonParentUid() == null) {
            personContainer.getThePersonDto().setPersonParentUid(personUid);
        }

        personContainer.getThePersonDto().setPersonUid(personUid);
        entityRepositoryUtil.preparingEntityReposCallForPerson(personContainer.getThePersonDto(), personUid, NEDSSConstant.PERSON, NEDSSConstant.UPDATE);

        Person person = new Person(personContainer.getThePersonDto(), tz);
        person.setBirthCntryCd(null);

        if (jdbcFlag) {
            personJdbcRepository.createPerson(person);
        } else {
            personRepository.save(person);
        }

        if (threadEnabled) {
            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                List<CompletableFuture<Void>> tasks = new ArrayList<>();

                if (personContainer.getThePersonNameDtoCollection() != null && !personContainer.getThePersonNameDtoCollection().isEmpty()) {
                    tasks.add(CompletableFuture.runAsync(() -> {
                        try {
                            createPersonName(personContainer);
                        } catch (DataProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor));
                }

                if (personContainer.getThePersonRaceDtoCollection() != null && !personContainer.getThePersonRaceDtoCollection().isEmpty()) {
                    tasks.add(CompletableFuture.runAsync(() -> {
                        try {
                            createPersonRace(personContainer);
                        } catch (DataProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor));
                }

                if (personContainer.getThePersonEthnicGroupDtoCollection() != null && !personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
                    tasks.add(CompletableFuture.runAsync(() -> {
                        try {
                            createPersonEthnic(personContainer);
                        } catch (DataProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor));
                }

                if (personContainer.getTheEntityIdDtoCollection() != null && !personContainer.getTheEntityIdDtoCollection().isEmpty()) {
                    tasks.add(CompletableFuture.runAsync(() -> {
                        try {
                            createEntityId(personContainer);
                        } catch (DataProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor));
                }

                if (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
                    tasks.add(CompletableFuture.runAsync(() -> {
                        try {
                            entityLocatorParticipationService.createEntityLocatorParticipation(
                                    personContainer.getTheEntityLocatorParticipationDtoCollection(),
                                    personContainer.getThePersonDto().getPersonUid());
                        } catch (DataProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor));
                }

                if (personContainer.getTheRoleDtoCollection() != null && !personContainer.getTheRoleDtoCollection().isEmpty()) {
                    tasks.add(CompletableFuture.runAsync(() -> {
                        try {
                            createRole(personContainer, "CREATE");
                        } catch (DataProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor));
                }

                for (CompletableFuture<Void> task : tasks) {
                    task.join();
                }

            } catch (CompletionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof DataProcessingException dpe) {
                    throw dpe;
                } else {
                    throw new DataProcessingException("Unexpected error during person creation", cause);
                }
            }

        } else {
            if (personContainer.getThePersonNameDtoCollection() != null && !personContainer.getThePersonNameDtoCollection().isEmpty()) {
                createPersonName(personContainer);
            }

            if (personContainer.getThePersonRaceDtoCollection() != null && !personContainer.getThePersonRaceDtoCollection().isEmpty()) {
                createPersonRace(personContainer);
            }

            if (personContainer.getThePersonEthnicGroupDtoCollection() != null && !personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
                createPersonEthnic(personContainer);
            }

            if (personContainer.getTheEntityIdDtoCollection() != null && !personContainer.getTheEntityIdDtoCollection().isEmpty()) {
                createEntityId(personContainer);
            }

            if (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
                entityLocatorParticipationService.createEntityLocatorParticipation(
                        personContainer.getTheEntityLocatorParticipationDtoCollection(),
                        personContainer.getThePersonDto().getPersonUid());
            }

            if (personContainer.getTheRoleDtoCollection() != null && !personContainer.getTheRoleDtoCollection().isEmpty()) {
                createRole(personContainer, "CREATE");
            }
        }

        return person;
    }


    @SuppressWarnings("java:S3776")
    public void updateExistingPerson(PersonContainer personContainer) throws DataProcessingException {
        //NOTE: Update Person
        Person person = new Person(personContainer.getThePersonDto(), tz);
        var ver = person.getVersionCtrlNbr();
        person.setVersionCtrlNbr(++ver);
        person.setBirthCntryCd(null);
        personRepository.save(person);



        //NOTE: Create Person Name
        if  (personContainer.getThePersonNameDtoCollection() != null && !personContainer.getThePersonNameDtoCollection().isEmpty()) {
            updatePersonName(personContainer);
        }
        //NOTE: Create Person Race
        if  (personContainer.getThePersonRaceDtoCollection() != null && !personContainer.getThePersonRaceDtoCollection().isEmpty()) {
            updatePersonRace(personContainer);
        }
        //NOTE: Create Person Ethnic
        if  (personContainer.getThePersonEthnicGroupDtoCollection() != null && !personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
            updatePersonEthnic(personContainer);
        }


        //NOTE: Upsert EntityID
        if  (personContainer.getTheEntityIdDtoCollection() != null && !personContainer.getTheEntityIdDtoCollection().isEmpty()) {
            updateEntityId(personContainer);

        }


        //NOTE: Create Entity Locator Participation
        if  (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            entityLocatorParticipationService.updateEntityLocatorParticipation(personContainer.getTheEntityLocatorParticipationDtoCollection(), personContainer.getThePersonDto().getPersonUid());
        }
        //NOTE: Upsert Role
        if  (personContainer.getTheRoleDtoCollection() != null && !personContainer.getTheRoleDtoCollection().isEmpty()) {
            createRole(personContainer, "UPDATE");
        }


        // Updating MPR
        if (!Objects.equals(person.getPersonUid(), person.getPersonParentUid())) {
            var mprRes = personRepository.findById(person.getPersonParentUid());
            if (mprRes.isPresent()) {
                var version = person.getVersionCtrlNbr();
                mprRes.get().setVersionCtrlNbr(++version);
                if (person.getEthnicGroupInd() != null) {
                    mprRes.get().setEthnicGroupInd(person.getEthnicGroupInd());
                }
                mprRes.get().setBirthCntryCd(null);
                personRepository.save(mprRes.get());
            }

        }

    }
    @SuppressWarnings("java:S3776")
    public PersonContainer loadPerson(Long personUid) {
        PersonContainer personContainer = new PersonContainer();

        PersonDto personDto = null;
        var personResult = personJdbcRepository.findByPersonUid(personUid);
        if (personResult != null) {
            personDto = new PersonDto(personResult);
            personDto.setItDirty(false);
            personDto.setItNew(false);
        }
        personContainer.setThePersonDto(personDto);

        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();
        List<PersonName> personNameResult = null;

        personNameResult = personJdbcRepository.findPersonNameByPersonUid(personUid);

        if (personNameResult != null && !personNameResult.isEmpty()) {
            for(var item : personNameResult) {
                var elem = new PersonNameDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                personNameDtoCollection.add(elem);
            }
        }
        personContainer.setThePersonNameDtoCollection(personNameDtoCollection);

        Collection<PersonRaceDto> personRaceDtoCollection = new ArrayList<>();
        var personRaceResult = personJdbcRepository.findPersonRaceByPersonUid(personUid);
        if (personRaceResult != null && !personRaceResult.isEmpty()) {
            for(var item : personRaceResult) {
                var elem = new PersonRaceDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                personRaceDtoCollection.add(elem);
            }
        }
        personContainer.setThePersonRaceDtoCollection(personRaceDtoCollection);

        Collection<PersonEthnicGroupDto> personEthnicGroupDtoCollection = new ArrayList<>();
        var personEthnic = personJdbcRepository.findPersonEthnicByPersonUid(personUid);
        if (personEthnic!= null && !personEthnic.isEmpty()) {
            for(var item : personEthnic) {
                var elem = new PersonEthnicGroupDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                personEthnicGroupDtoCollection.add(elem);
            }
        }
        personContainer.setThePersonEthnicGroupDtoCollection(personEthnicGroupDtoCollection);


        Collection<EntityIdDto> entityIdDtoCollection = new ArrayList<>();
        var entityIdResult = entityIdJdbcRepository.findEntityIds(personUid);
        if (entityIdResult != null && !entityIdResult.isEmpty()) {
            for(var item : entityIdResult) {
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
        var roleResult = roleJdbcRepository.findRolesByParentUid(personUid);
        if (roleResult != null && !roleResult.isEmpty()) {
            for(var item : roleResult) {
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
        return personJdbcRepository.findMprUid(personUid);
    }

    @SuppressWarnings({"java:S3776","java:S1141"})
    private void updatePersonName(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonNameDto>  personList = (ArrayList<PersonNameDto> ) personContainer.getThePersonNameDtoCollection();
        try {
            var pUid = personContainer.getThePersonDto().getPersonUid();
            List<PersonName> persons = personNameRepository.findBySeqIdByParentUid(pUid);

            Integer seqId = 1;

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

                PersonNameDto personName = null;
                for (PersonNameDto personNameDto : personList) {
                    if (!personNameDto.isItDelete()) {
                        personName = personNameDto;
                    } else {
                        try {
                            if (personName != null) {
                                // set existing record status to inactive
                                dataModifierReposJdbc.updatePersonNameStatus(personNameDto.getPersonUid(), seqId);

                                seqId++;
                                if (personName.getStatusCd() == null) {
                                    personName.setStatusCd("A");
                                }
                                if (personName.getStatusTime() == null) {
                                    personName.setStatusTime(TimeStampUtil.getCurrentTimeStamp(tz));
                                }

                                personName.setPersonNameSeq(seqId);
                                personName.setRecordStatusCd("ACTIVE");
                                personName.setAddReasonCd("Add");
                                personNameRepository.save(new PersonName(personName, tz));

                                var mprRecord =  SerializationUtils.clone(personName);
                                mprRecord.setPersonUid(personContainer.getThePersonDto().getPersonParentUid());
                                personNameRepository.save(new PersonName(mprRecord, tz));
                            }
                        } catch (Exception e) {
                            logger.error("{} {}", ERROR_UPDATE_MSG, e.getMessage()); //NOSONAR
                        }
                    }

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
            for (PersonNameDto personNameDto : personList) {
                personNameDto.setPersonUid(pUid);
                if (personNameDto.getStatusCd() == null) {
                    personNameDto.setStatusCd("A");
                }
                if (personNameDto.getStatusTime() == null) {
                    personNameDto.setStatusTime(TimeStampUtil.getCurrentTimeStamp(tz));
                }
                personNameDto.setRecordStatusCd("ACTIVE");
                personNameDto.setAddReasonCd("Add");
                if (jdbcFlag) {
                    personJdbcRepository.createPersonName(new PersonName(personNameDto,tz));
                }
                else {
                    personNameRepository.save(new PersonName(personNameDto,tz));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @SuppressWarnings({"java:S3776", "java:S1141"})
    private void updateEntityId(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<EntityIdDto>  personList = (ArrayList<EntityIdDto> ) personContainer.getTheEntityIdDtoCollection();
        try {
            for (EntityIdDto entityIdDto : personList) {

                if (entityIdDto.isItDelete()) {
                    try {
                        dataModifierReposJdbc.deleteEntityIdAndSeq(entityIdDto.getEntityUid(), entityIdDto.getEntityIdSeq());
                        if (personContainer.getThePersonDto().getPersonParentUid() != null) {
                            dataModifierReposJdbc.deleteEntityIdAndSeq(personContainer.getThePersonDto().getPersonParentUid(), entityIdDto.getEntityIdSeq());
                        }
                    } catch (Exception e) {
                        logger.error("{} {}", ERROR_DELETE_MSG, e.getMessage()); //NOSONAR
                    }
                }
                else {
                    if (entityIdDto.getAddUserId() == null) {
                        entityIdDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());
                    }
                    if (entityIdDto.getLastChgUserId() == null) {
                        entityIdDto.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                    }

                    var mprRecord =  SerializationUtils.clone(entityIdDto);
                    mprRecord.setEntityUid(personContainer.getThePersonDto().getPersonParentUid());
                    mprRecord.setAddReasonCd("Add");
                    entityIdRepository.save(new EntityId(mprRecord, tz));


                    var pUid = personContainer.getThePersonDto().getPersonUid();
                    entityIdDto.setEntityUid(pUid);
                    entityIdDto.setAddReasonCd("Add");
                    entityIdRepository.save(new EntityId(entityIdDto, tz));
                }


            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @SuppressWarnings({"java:S1141","java:S3776"})
    private void updatePersonRace(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonRaceDto>  personList = (ArrayList<PersonRaceDto> ) personContainer.getThePersonRaceDtoCollection();
        var parentUid = personContainer.getThePersonDto().getPersonParentUid();
        Long patientUid = -1L;
        try {
            List<String> retainingRaceCodeList = new ArrayList<>();
            for (PersonRaceDto personRaceDto : personList) {
                var pUid = personContainer.getThePersonDto().getPersonUid();
                if (personRaceDto.isItDelete()) {
                    try {
                        dataModifierReposJdbc.deletePersonRaceByUidAndCode(personRaceDto.getPersonUid(), personRaceDto.getRaceCd());
                    } catch (Exception e) {
                        logger.error("{} {}", ERROR_DELETE_MSG, e.getMessage()); //NOSONAR

                    }
                }
                else {
                    // Edge case, happen when there are race exist, and we try to remove the second race from the list
                    if (personRaceDto.isItDirty() && !Objects.equals(personRaceDto.getPersonUid(), parentUid)) {
                        retainingRaceCodeList.add(personRaceDto.getRaceCd());
                        patientUid = personRaceDto.getPersonUid();
                    }
                    var mprRecord = SerializationUtils.clone(personRaceDto);
                    mprRecord.setPersonUid(personContainer.getThePersonDto().getPersonParentUid());
                    mprRecord.setAddReasonCd("Add");
                    personRaceRepository.save(new PersonRace(mprRecord, tz));

                    personRaceDto.setPersonUid(pUid);
                    personRaceDto.setAddReasonCd("Add");
                    personRaceRepository.save(new PersonRace(personRaceDto, tz));


                }
            }

            // Theses executes after the update process, whatever race not it the retain list and not direct assoc with parent uid will be deleted
            deleteInactivePersonRace(retainingRaceCodeList, patientUid, parentUid);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    protected void deleteInactivePersonRace(List<String> retainingRaceCodeList, Long patientUid, Long parentUid) {
        // Theses executes after the update process, whatever race not it the retain list and not direct assoc with parent uid will be deleted
        if (!retainingRaceCodeList.isEmpty() && patientUid > 0) {
            try {
                dataModifierReposJdbc.deletePersonRaceByUid(patientUid,retainingRaceCodeList);
            } catch (Exception e) {
                logger.error("{} {}", ERROR_DELETE_MSG, e.getMessage()); //NOSONAR

            }
        }
        if (!retainingRaceCodeList.isEmpty() && parentUid > 0 && !patientUid.equals(parentUid)) {
            try {
                var raceParent = personRaceRepository.findByParentUid(parentUid);
                if (raceParent.isPresent() && raceParent.get().size() > 1) {
                    dataModifierReposJdbc.deletePersonRaceByUid(parentUid,retainingRaceCodeList);
                }
            } catch (Exception e) {
                logger.error("{} {}", ERROR_UPDATE_MSG, e.getMessage()); //NOSONAR

            }
        }
    }


    private void createPersonRace(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonRaceDto>  personList = (ArrayList<PersonRaceDto> ) personContainer.getThePersonRaceDtoCollection();
        try {
            for (PersonRaceDto personRaceDto : personList) {
                var pUid = personContainer.getThePersonDto().getPersonUid();
                personRaceDto.setPersonUid(pUid);
                personRaceDto.setAddReasonCd("Add");
                if (jdbcFlag) {
                    personJdbcRepository.createPersonRace(new PersonRace(personRaceDto, tz));
                }
                else {
                    personRaceRepository.save(new PersonRace(personRaceDto, tz));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createPersonEthnic(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonEthnicGroupDto>  personList = (ArrayList<PersonEthnicGroupDto> ) personContainer.getThePersonEthnicGroupDtoCollection();
        try {
            for (PersonEthnicGroupDto personEthnicGroupDto : personList) {
                var pUid = personContainer.getThePersonDto().getPersonUid();
                personEthnicGroupDto.setPersonUid(pUid);
                if (jdbcFlag) {
                    personJdbcRepository.createPersonEthnicGroup(new PersonEthnicGroup(personEthnicGroupDto));
                }
                else {
                    personEthnicRepository.save(new PersonEthnicGroup(personEthnicGroupDto));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void updatePersonEthnic(PersonContainer personContainer) throws DataProcessingException {
        try {
            var parentUid = personContainer.getThePersonDto().getPersonParentUid();
            for (PersonEthnicGroupDto personEthnicGroupDto : personContainer.getThePersonEthnicGroupDtoCollection()) {

                var mprRecord =  SerializationUtils.clone(personEthnicGroupDto);
                mprRecord.setPersonUid(parentUid);
                personEthnicRepository.save(new PersonEthnicGroup(mprRecord));

                var pUid = personContainer.getThePersonDto().getPersonUid();
                personEthnicGroupDto.setPersonUid(pUid);
                personEthnicRepository.save(new PersonEthnicGroup(personEthnicGroupDto));


            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityId(PersonContainer personContainer) throws DataProcessingException {
        Collection<EntityIdDto> entityIds = personContainer.getTheEntityIdDtoCollection();
        if (entityIds == null || entityIds.isEmpty()) return;
        List<EntityId> entityIdEntities = entityIds.stream().map(dto -> {
            dto.setEntityUid(personContainer.getThePersonDto().getPersonUid());
            dto.setAddReasonCd("Add");

            if (dto.getAddUserId() == null) dto.setAddUserId(AuthUtil.authUser.getNedssEntryId());
            if (dto.getLastChgUserId() == null) dto.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());

            return new EntityId(dto, tz);
        }).toList();
        try {
            if (jdbcFlag) {
                entityIdJdbcRepository.batchCreateEntityIds(entityIdEntities);
            } else {
                entityIdRepository.saveAll(entityIdEntities);
            }
        } catch (Exception e) {
            throw new DataProcessingException("Error during batch entity ID creation", e);
        }
    }



    private void createRole(PersonContainer personContainer, String operation) throws DataProcessingException {
        ArrayList<RoleDto>  personList = (ArrayList<RoleDto> ) personContainer.getTheRoleDtoCollection();
        try {
            for (RoleDto obj : personList) {
                if (jdbcFlag && operation.equalsIgnoreCase("CREATE")) {
                    roleJdbcRepository.createRole(new Role(obj));
                }
                else {
                    roleRepository.save(new Role(obj));
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public List<Person> findPersonByParentUid(Long parentUid) {
        var res = personJdbcRepository.findPersonsByParentUid(parentUid);
        return Objects.requireNonNullElse(res, Collections.emptyList());
    }


    @SuppressWarnings({"java:S1871","java:S3776"})
    public PersonContainer preparePersonNameBeforePersistence(PersonContainer personContainer) throws DataProcessingException {
        try {
            Collection<PersonNameDto> namesCollection = personContainer
                    .getThePersonNameDtoCollection();
            if (namesCollection != null && !namesCollection.isEmpty()) {

                Iterator<PersonNameDto> namesIter = namesCollection.iterator();
                PersonNameDto selectedNameDT = null;
                while (namesIter.hasNext()) {
                    PersonNameDto thePersonNameDto =  namesIter.next();
                    if (thePersonNameDto.getNmUseCd() != null
                            && !thePersonNameDto.getNmUseCd().trim().equals("L"))
                    {
                        continue;
                    }
                    if (thePersonNameDto.getAsOfDate() != null) {
                        if (selectedNameDT == null)
                        {
                            selectedNameDT = thePersonNameDto;
                        }
                        else if (selectedNameDT.getAsOfDate()!=null
                                && thePersonNameDto.getAsOfDate()!=null
                                && thePersonNameDto.getAsOfDate().after(selectedNameDT.getAsOfDate()))
                        {
                            selectedNameDT = thePersonNameDto;
                        }
                    } else {
                        if (selectedNameDT == null)
                        {
                            selectedNameDT = thePersonNameDto;
                        }
                    }
                }
                if (selectedNameDT != null) {
                    personContainer.getThePersonDto().setLastNm(selectedNameDT.getLastNm());
                    personContainer.getThePersonDto().setFirstNm(selectedNameDT.getFirstNm());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

        return personContainer;
    }


}
