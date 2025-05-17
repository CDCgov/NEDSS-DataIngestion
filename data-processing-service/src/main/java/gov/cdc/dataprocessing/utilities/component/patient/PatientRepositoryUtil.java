package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
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
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.entity.IEntityLocatorParticipationService;
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
    private static final Logger logger = LoggerFactory.getLogger(PatientRepositoryUtil.class);
    @Value("${service.timezone}")
    private String tz = "UTC";

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

    private final IEntityLocatorParticipationService entityLocatorParticipationService;

    private final UidPoolManager uidPoolManager;

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
            IEntityLocatorParticipationService entityLocatorParticipationService,
            UidPoolManager uidPoolManager) {
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
        this.entityLocatorParticipationService = entityLocatorParticipationService;
        this.uidPoolManager = uidPoolManager;
    }

    public Long updateExistingPersonEdxIndByUid(Long uid) {
        return (long) dataModifierReposJdbc.updateExistingPersonEdxIndByUid(uid);
    }

    public Person findExistingPersonByUid(Long personUid) {
        var result = personRepository.findById(personUid);
        return result.orElse(null);
    }

    public Person createPerson(PersonContainer personContainer) throws DataProcessingException {
        Long personUid;
        String localUid;

        var localIdModel = uidPoolManager.getNextUid(LocalIdClass.PERSON,true);

        personUid = localIdModel.getGaTypeUid().getSeedValueNbr();
        localUid = localIdModel.getClassTypeUid().getUidPrefixCd()
                + localIdModel.getClassTypeUid().getSeedValueNbr()
                + localIdModel.getClassTypeUid().getUidSuffixCd();

        var personDto = personContainer.getThePersonDto();
        if (personDto.getLocalId() == null || personDto.getLocalId().trim().isEmpty()) {
            personDto.setLocalId(localUid);
        }

        if (personDto.getPersonParentUid() == null) {
            personDto.setPersonParentUid(personUid);
        }

        personDto.setPersonUid(personUid);
        entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, personUid, NEDSSConstant.PERSON, NEDSSConstant.UPDATE);

        Person person = new Person(personDto, tz);
        person.setBirthCntryCd(null);

        personJdbcRepository.createPerson(person);



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
            // 104ms on this one
            entityLocatorParticipationService.createEntityLocatorParticipation(
                    personContainer.getTheEntityLocatorParticipationDtoCollection(),
                    personDto.getPersonUid());
        }

        if (personContainer.getTheRoleDtoCollection() != null && !personContainer.getTheRoleDtoCollection().isEmpty()) {
            createRole(personContainer, "CREATE");
        }


        return person;
    }

    @SuppressWarnings("java:S3776")
    public void updateExistingPerson(PersonContainer personContainer) throws DataProcessingException {
        //NOTE: Update Person
        Person person = new Person(personContainer.getThePersonDto(), tz);
        person.setVersionCtrlNbr(person.getVersionCtrlNbr() + 1);
        person.setBirthCntryCd(null);
        personRepository.save(person);

        // Cache UID for reuse
        Long personUid = person.getPersonUid();

        // NOTE: Create Person Name
        var nameCollection = personContainer.getThePersonNameDtoCollection();
        if (nameCollection != null && !nameCollection.isEmpty()) {
            updatePersonName(personContainer);
        }

        // NOTE: Create Person Race
        var raceCollection = personContainer.getThePersonRaceDtoCollection();
        if (raceCollection != null && !raceCollection.isEmpty()) {
            updatePersonRace(personContainer);
        }

        // NOTE: Create Person Ethnic
        var ethnicCollection = personContainer.getThePersonEthnicGroupDtoCollection();
        if (ethnicCollection != null && !ethnicCollection.isEmpty()) {
            updatePersonEthnic(personContainer);
        }

        // NOTE: Upsert EntityID
        var entityIdCollection = personContainer.getTheEntityIdDtoCollection();
        if (entityIdCollection != null && !entityIdCollection.isEmpty()) {
            updateEntityId(personContainer);
        }

        // NOTE: Create Entity Locator Participation
        var elpCollection = personContainer.getTheEntityLocatorParticipationDtoCollection();
        if (elpCollection != null && !elpCollection.isEmpty()) {
            entityLocatorParticipationService.updateEntityLocatorParticipation(elpCollection, personUid);
        }

        // NOTE: Upsert Role
        var roleCollection = personContainer.getTheRoleDtoCollection();
        if (roleCollection != null && !roleCollection.isEmpty()) {
            createRole(personContainer, "UPDATE");
        }


        // NOTE: Updating MPR
        if (!Objects.equals(person.getPersonUid(), person.getPersonParentUid())) {
            personRepository.findById(person.getPersonParentUid()).ifPresent(mpr -> {
                mpr.setVersionCtrlNbr(person.getVersionCtrlNbr() + 1);
                if (person.getEthnicGroupInd() != null) {
                    mpr.setEthnicGroupInd(person.getEthnicGroupInd());
                }
                mpr.setBirthCntryCd(null);
                personRepository.save(mpr);
            });
        }
    }

    @SuppressWarnings("java:S3776")
    public PersonContainer loadPerson(Long personUid) {
        PersonContainer personContainer = new PersonContainer();

        // Load Person DTO
        var personResult = personJdbcRepository.findByPersonUid(personUid);
        if (personResult != null) {
            PersonDto personDto = new PersonDto(personResult);
            personDto.setItDirty(false);
            personDto.setItNew(false);
            personContainer.setThePersonDto(personDto);
        }

        // Load Person Name DTOs
        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();
        var personNameResult = personJdbcRepository.findPersonNameByPersonUid(personUid);
        if (!personNameResult.isEmpty()) {
            for (var item : personNameResult) {
                var dto = new PersonNameDto(item);
                dto.setItDirty(false);
                dto.setItNew(false);
                personNameDtoCollection.add(dto);
            }
        }
        personContainer.setThePersonNameDtoCollection(personNameDtoCollection);

        // Load Person Race DTOs
        Collection<PersonRaceDto> personRaceDtoCollection = new ArrayList<>();
        var personRaceResult = personJdbcRepository.findPersonRaceByPersonUid(personUid);
        if (!personRaceResult.isEmpty()) {
            for (var item : personRaceResult) {
                var dto = new PersonRaceDto(item);
                dto.setItDirty(false);
                dto.setItNew(false);
                personRaceDtoCollection.add(dto);
            }
        }
        personContainer.setThePersonRaceDtoCollection(personRaceDtoCollection);

        // Load Person Ethnic Group DTOs
        Collection<PersonEthnicGroupDto> personEthnicGroupDtoCollection = new ArrayList<>();
        var personEthnicResult = personJdbcRepository.findPersonEthnicByPersonUid(personUid);
        if (!personEthnicResult.isEmpty()) {
            for (var item : personEthnicResult) {
                var dto = new PersonEthnicGroupDto(item);
                dto.setItDirty(false);
                dto.setItNew(false);
                personEthnicGroupDtoCollection.add(dto);
            }
        }
        personContainer.setThePersonEthnicGroupDtoCollection(personEthnicGroupDtoCollection);

        // Load Entity ID DTOs
        Collection<EntityIdDto> entityIdDtoCollection = new ArrayList<>();
        var entityIdResult = entityIdJdbcRepository.findEntityIds(personUid);
        if (!entityIdResult.isEmpty()) {
            for (var item : entityIdResult) {
                var dto = new EntityIdDto(item);
                dto.setItDirty(false);
                dto.setItNew(false);
                entityIdDtoCollection.add(dto);
            }
        }
        personContainer.setTheEntityIdDtoCollection(entityIdDtoCollection);

        // Load Entity Locator Participation DTOs
        Collection<EntityLocatorParticipationDto> entityLocatorParticipationDtoCollection = new ArrayList<>();
        var entityLocatorResult = entityLocatorParticipationService.findEntityLocatorById(personUid);
        if (!entityLocatorResult.isEmpty()) {
            for (var item : entityLocatorResult) {
                var dto = new EntityLocatorParticipationDto(item);
                dto.setItDirty(false);
                dto.setItNew(false);
                entityLocatorParticipationDtoCollection.add(dto);
            }
        }
        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorParticipationDtoCollection);

        // Load Role DTOs
        Collection<RoleDto> roleDtoCollection = new ArrayList<>();
        var roleResult = roleJdbcRepository.findRolesByParentUid(personUid);
        if (!roleResult.isEmpty()) {
            for (var item : roleResult) {
                var dto = new RoleDto(item);
                dto.setItDirty(false);
                dto.setItNew(false);
                roleDtoCollection.add(dto);
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
        List<PersonNameDto> personList = new ArrayList<>(personContainer.getThePersonNameDtoCollection());

        try {
            Long personUid = personContainer.getThePersonDto().getPersonUid();
            List<PersonName> existingNames = personNameRepository.findBySeqIdByParentUid(personUid);

            // Build current input name key
            String inputNameKey = (
                    defaultStr(personContainer.getThePersonDto().getFirstNm()) +
                            defaultStr(personContainer.getThePersonDto().getLastNm()) +
                            defaultStr(personContainer.getThePersonDto().getMiddleNm()) +
                            defaultStr(personContainer.getThePersonDto().getNmPrefix()) +
                            defaultStr(personContainer.getThePersonDto().getNmSuffix())
            ).toUpperCase();

            // Build list of existing name keys
            Set<String> existingNameKeys = new HashSet<>();
            for (PersonName name : existingNames) {
                String existingKey = (
                        defaultStr(name.getFirstNm()) +
                                defaultStr(name.getLastNm()) +
                                defaultStr(name.getMiddleNm()) +
                                defaultStr(name.getNmPrefix()) +
                                defaultStr(name.getNmSuffix())
                ).toUpperCase();
                existingNameKeys.add(existingKey);
            }

            // Only save if it's a truly new name
            if (!existingNameKeys.contains(inputNameKey)) {
                int nextSeqId = existingNames.stream()
                        .map(PersonName::getPersonNameSeq)
                        .max(Comparator.naturalOrder())
                        .orElse(0);

                PersonNameDto newNameDto = null;

                for (PersonNameDto dto : personList) {
                    if (!dto.isItDelete()) {
                        newNameDto = dto;
                    } else if (newNameDto != null) {
                        try {
                            // Inactivate existing record
                            dataModifierReposJdbc.updatePersonNameStatus(dto.getPersonUid(), nextSeqId);

                            nextSeqId++;
                            if (newNameDto.getStatusCd() == null) {
                                newNameDto.setStatusCd("A");
                            }
                            if (newNameDto.getStatusTime() == null) {
                                newNameDto.setStatusTime(TimeStampUtil.getCurrentTimeStamp(tz));
                            }

                            newNameDto.setPersonNameSeq(nextSeqId);
                            newNameDto.setRecordStatusCd("ACTIVE");
                            newNameDto.setAddReasonCd("Add");

                            // Save to person
                            personNameRepository.save(new PersonName(newNameDto, tz));

                            // Clone for MPR
                            var mprCopy = SerializationUtils.clone(newNameDto);
                            mprCopy.setPersonUid(personContainer.getThePersonDto().getPersonParentUid());
                            personNameRepository.save(new PersonName(mprCopy, tz));

                        } catch (Exception ex) {
                            logger.error("{} {}", ERROR_UPDATE_MSG, ex.getMessage()); // NOSONAR
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private String defaultStr(String s) {
        return s != null ? s : "";
    }

    private void createPersonName(PersonContainer personContainer) throws DataProcessingException {
        var personNameDtos = personContainer.getThePersonNameDtoCollection();
        if (personNameDtos == null || personNameDtos.isEmpty()) {
            return;
        }

        try {
            Long personUid = personContainer.getThePersonDto().getPersonUid();

            for (PersonNameDto dto : personNameDtos) {
                dto.setPersonUid(personUid);

                if (dto.getStatusCd() == null) {
                    dto.setStatusCd("A");
                }
                if (dto.getStatusTime() == null) {
                    dto.setStatusTime(TimeStampUtil.getCurrentTimeStamp(tz));
                }

                dto.setRecordStatusCd("ACTIVE");
                dto.setAddReasonCd("Add");

                PersonName entity = new PersonName(dto, tz);
                personJdbcRepository.createPersonName(entity);
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @SuppressWarnings({"java:S3776", "java:S1141"})
    private void updateEntityId(PersonContainer personContainer) throws DataProcessingException {
        var entityIdDtos = personContainer.getTheEntityIdDtoCollection();
        if (entityIdDtos == null || entityIdDtos.isEmpty()) {
            return;
        }

        try {
            var personDto = personContainer.getThePersonDto();
            Long personUid = personDto.getPersonUid();
            Long parentUid = personDto.getPersonParentUid();

            for (EntityIdDto dto : entityIdDtos) {
                if (dto.isItDelete()) {
                    try {
                        dataModifierReposJdbc.deleteEntityIdAndSeq(dto.getEntityUid(), dto.getEntityIdSeq());
                        if (parentUid != null) {
                            dataModifierReposJdbc.deleteEntityIdAndSeq(parentUid, dto.getEntityIdSeq());
                        }
                    } catch (Exception e) {
                        logger.error("{} {}", ERROR_DELETE_MSG, e.getMessage()); // NOSONAR
                    }
                } else {
                    Long userId = AuthUtil.authUser.getNedssEntryId();
                    if (dto.getAddUserId() == null) {
                        dto.setAddUserId(userId);
                    }
                    if (dto.getLastChgUserId() == null) {
                        dto.setLastChgUserId(userId);
                    }

                    // Save for parent
                    var mprCopy = SerializationUtils.clone(dto);
                    mprCopy.setEntityUid(parentUid);
                    mprCopy.setAddReasonCd("Add");
                    entityIdRepository.save(new EntityId(mprCopy, tz));

                    // Save for person
                    dto.setEntityUid(personUid);
                    dto.setAddReasonCd("Add");
                    entityIdRepository.save(new EntityId(dto, tz));
                }
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    @SuppressWarnings({"java:S1141","java:S3776"})
    private void updatePersonRace(PersonContainer personContainer) throws DataProcessingException {
        var personRaceDtos = personContainer.getThePersonRaceDtoCollection();
        if (personRaceDtos == null || personRaceDtos.isEmpty()) {
            return;
        }

        Long parentUid = personContainer.getThePersonDto().getPersonParentUid();
        Long personUid = personContainer.getThePersonDto().getPersonUid();
        Long patientUid = -1L;

        try {
            List<String> retainingRaceCodeList = new ArrayList<>();

            for (PersonRaceDto dto : personRaceDtos) {
                if (dto.isItDelete()) {
                    try {
                        dataModifierReposJdbc.deletePersonRaceByUidAndCode(dto.getPersonUid(), dto.getRaceCd());
                    } catch (Exception e) {
                        logger.error("{} {}", ERROR_DELETE_MSG, e.getMessage()); // NOSONAR
                    }
                } else {
                    // Handle race update edge case
                    if (dto.isItDirty() && !Objects.equals(dto.getPersonUid(), parentUid)) {
                        retainingRaceCodeList.add(dto.getRaceCd());
                        patientUid = dto.getPersonUid();
                    }

                    // Save to MPR
                    var mprRecord = SerializationUtils.clone(dto);
                    mprRecord.setPersonUid(parentUid);
                    mprRecord.setAddReasonCd("Add");
                    personRaceRepository.save(new PersonRace(mprRecord, tz));

                    // Save to actual person
                    dto.setPersonUid(personUid);
                    dto.setAddReasonCd("Add");
                    personRaceRepository.save(new PersonRace(dto, tz));
                }
            }

            // Cleanup inactive races
            deleteInactivePersonRace(retainingRaceCodeList, patientUid, parentUid);

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    protected void deleteInactivePersonRace(List<String> retainingRaceCodeList, Long patientUid, Long parentUid) {
        // Executes after update process: delete races not in retained list and not directly tied to parent UID
        if (retainingRaceCodeList == null || retainingRaceCodeList.isEmpty()) return;

        try {
            if (patientUid != null && patientUid > 0) {
                dataModifierReposJdbc.deletePersonRaceByUid(patientUid, retainingRaceCodeList);
            }
        } catch (Exception e) {
            logger.error("{} {}", ERROR_DELETE_MSG, e.getMessage()); // NOSONAR
        }

        try {
            if (parentUid != null && parentUid > 0 && !patientUid.equals(parentUid)) {
                var raceParent = personRaceRepository.findByParentUid(parentUid);
                if (raceParent.isPresent() && raceParent.get().size() > 1) {
                    dataModifierReposJdbc.deletePersonRaceByUid(parentUid, retainingRaceCodeList);
                }
            }
        } catch (Exception e) {
            logger.error("{} {}", ERROR_UPDATE_MSG, e.getMessage()); // NOSONAR
        }
    }



    private void createPersonRace(PersonContainer personContainer) throws DataProcessingException {
        var personRaceDtos = personContainer.getThePersonRaceDtoCollection();
        if (personRaceDtos == null || personRaceDtos.isEmpty()) {
            return;
        }

        try {
            Long personUid = personContainer.getThePersonDto().getPersonUid();

            for (PersonRaceDto dto : personRaceDtos) {
                dto.setPersonUid(personUid);
                dto.setAddReasonCd("Add");

                PersonRace entity = new PersonRace(dto, tz);

                personJdbcRepository.createPersonRace(entity);
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private void createPersonEthnic(PersonContainer personContainer) throws DataProcessingException {
        var ethnicGroupDtos = personContainer.getThePersonEthnicGroupDtoCollection();
        if (ethnicGroupDtos == null || ethnicGroupDtos.isEmpty()) {
            return;
        }

        try {
            Long personUid = personContainer.getThePersonDto().getPersonUid();

            for (PersonEthnicGroupDto dto : ethnicGroupDtos) {
                dto.setPersonUid(personUid);
                PersonEthnicGroup entity = new PersonEthnicGroup(dto);

                personJdbcRepository.createPersonEthnicGroup(entity);
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    private void updatePersonEthnic(PersonContainer personContainer) throws DataProcessingException {
        var ethnicDtos = personContainer.getThePersonEthnicGroupDtoCollection();
        if (ethnicDtos == null || ethnicDtos.isEmpty()) {
            return;
        }

        try {
            Long personUid = personContainer.getThePersonDto().getPersonUid();
            Long parentUid = personContainer.getThePersonDto().getPersonParentUid();

            for (PersonEthnicGroupDto dto : ethnicDtos) {
                // Save to MPR
                var mprCopy = SerializationUtils.clone(dto);
                mprCopy.setPersonUid(parentUid);
                personEthnicRepository.save(new PersonEthnicGroup(mprCopy));

                // Save to actual person
                dto.setPersonUid(personUid);
                personEthnicRepository.save(new PersonEthnicGroup(dto));
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private void createEntityId(PersonContainer personContainer) throws DataProcessingException {
        var entityIdDtos = personContainer.getTheEntityIdDtoCollection();
        if (entityIdDtos == null || entityIdDtos.isEmpty()) return;

        Long personUid = personContainer.getThePersonDto().getPersonUid();
        Long authUserId = AuthUtil.authUser.getNedssEntryId();

        List<EntityId> entityIdEntities = entityIdDtos.stream().map(dto -> {
            dto.setEntityUid(personUid);
            dto.setAddReasonCd("Add");

            if (dto.getAddUserId() == null) dto.setAddUserId(authUserId);
            if (dto.getLastChgUserId() == null) dto.setLastChgUserId(authUserId);

            return new EntityId(dto, tz);
        }).toList();

        try {
            entityIdJdbcRepository.batchCreateEntityIds(entityIdEntities);
        } catch (Exception e) {
            throw new DataProcessingException("Error during batch entity ID creation", e);
        }
    }




    private void createRole(PersonContainer personContainer, String operation) throws DataProcessingException {
        var roleDtos = personContainer.getTheRoleDtoCollection();
        if (roleDtos == null || roleDtos.isEmpty()) return;

        boolean isCreateOp = "CREATE".equalsIgnoreCase(operation);

        try {
            for (RoleDto dto : roleDtos) {
                Role role = new Role(dto);
                if (isCreateOp) {
                    roleJdbcRepository.createRole(role);
                } else {
                    roleRepository.save(role);
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException("Error creating roles", e);
        }
    }


    public List<Person> findPersonByParentUid(Long parentUid) {
        var res = personJdbcRepository.findPersonsByParentUid(parentUid);
        return Objects.requireNonNullElse(res, Collections.emptyList());
    }


    @SuppressWarnings({"java:S1871","java:S3776"})
    public PersonContainer preparePersonNameBeforePersistence(PersonContainer personContainer) throws DataProcessingException {
        try {
            var nameDtos = personContainer.getThePersonNameDtoCollection();
            if (nameDtos == null || nameDtos.isEmpty()) {
                return personContainer;
            }

            PersonNameDto selected = null;

            for (PersonNameDto dto : nameDtos) {
                // Skip if name use code is not 'L'
                if (!"L".equalsIgnoreCase(dto.getNmUseCd())) {
                    continue;
                }

                if (selected == null || isAfter(dto.getAsOfDate(), selected.getAsOfDate())) {
                    selected = dto;
                }
            }

            if (selected != null) {
                var personDto = personContainer.getThePersonDto();
                personDto.setLastNm(selected.getLastNm());
                personDto.setFirstNm(selected.getFirstNm());
            }

            return personContainer;

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private boolean isAfter(Date a, Date b) {
        return a != null && (b == null || a.after(b));
    }



}
