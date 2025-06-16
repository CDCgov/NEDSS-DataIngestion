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

import static gov.cdc.dataprocessing.constant.DpConstant.OPERATION_CREATE;
import static gov.cdc.dataprocessing.constant.DpConstant.OPERATION_UPDATE;

@Component

public class PatientRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(PatientRepositoryUtil.class);
    @Value("${service.timezone}")
    private String tz = "UTC";

    private final EntityRepositoryUtil entityRepositoryUtil;
    private final PersonJdbcRepository personJdbcRepository;
    private final RoleJdbcRepository roleJdbcRepository;
    private final EntityIdJdbcRepository entityIdJdbcRepository;
    private final DataModifierReposJdbc dataModifierReposJdbc;
    private final IEntityLocatorParticipationService entityLocatorParticipationService;

    private final UidPoolManager uidPoolManager;

    private static final String ERROR_DELETE_MSG = "Error Delete Patient Entity: ";
    private static final String ERROR_UPDATE_MSG = "Error Updating Existing Patient Entity: ";


    public PatientRepositoryUtil(
            EntityRepositoryUtil entityRepositoryUtil,
            PersonJdbcRepository personJdbcRepository,
            RoleJdbcRepository roleJdbcRepository,
            EntityIdJdbcRepository entityIdJdbcRepository,
            DataModifierReposJdbc dataModifierReposJdbc,
            IEntityLocatorParticipationService entityLocatorParticipationService,
            UidPoolManager uidPoolManager) {
        this.entityRepositoryUtil = entityRepositoryUtil;
        this.personJdbcRepository = personJdbcRepository;
        this.roleJdbcRepository = roleJdbcRepository;
        this.entityIdJdbcRepository = entityIdJdbcRepository;
        this.dataModifierReposJdbc = dataModifierReposJdbc;
        this.entityLocatorParticipationService = entityLocatorParticipationService;
        this.uidPoolManager = uidPoolManager;
    }

    public Long updateExistingPersonEdxIndByUid(Long uid) {
        return (long) dataModifierReposJdbc.updateExistingPersonEdxIndByUid(uid);
    }

    public Person findExistingPersonByUid(Long personUid) {
        return personJdbcRepository.selectByPersonUid(personUid);
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
        entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, personUid, NEDSSConstant.PERSON, OPERATION_UPDATE);

        Person person = new Person(personDto, tz);
        person.setBirthCntryCd(null);

        personJdbcRepository.createPerson(person);



        if (!personContainer.getThePersonNameDtoCollection().isEmpty()) {
            createPersonName(personContainer);
        }

        if (!personContainer.getThePersonRaceDtoCollection().isEmpty()) {
            createPersonRace(personContainer);
        }

        if (!personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
            createPersonEthnic(personContainer);
        }

        if (!personContainer.getTheEntityIdDtoCollection().isEmpty()) {
            createEntityId(personContainer);
        }

        if (!personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            // 104ms on this one
            entityLocatorParticipationService.createEntityLocatorParticipation(
                    personContainer.getTheEntityLocatorParticipationDtoCollection(),
                    personDto.getPersonUid());
        }

        if (!personContainer.getTheRoleDtoCollection().isEmpty()) {
            createRole(personContainer, OPERATION_CREATE);
        }


        return person;
    }

    @SuppressWarnings("java:S3776")
    public void updateExistingPerson(PersonContainer personContainer) throws DataProcessingException {
        //NOTE: Update Person
        Person person = new Person(personContainer.getThePersonDto(), tz);
        person.setVersionCtrlNbr(person.getVersionCtrlNbr() + 1);
        person.setBirthCntryCd(null);
        personJdbcRepository.updatePerson(person);

        // Cache UID for reuse
        Long personUid = person.getPersonUid();

        // NOTE: Create Person Name
        var nameCollection = personContainer.getThePersonNameDtoCollection();
        if (!nameCollection.isEmpty()) {
            updatePersonName(personContainer);
        }

        // NOTE: Create Person Race
        var raceCollection = personContainer.getThePersonRaceDtoCollection();
        if (!raceCollection.isEmpty()) {
            updatePersonRace(personContainer);
        }

        // NOTE: Create Person Ethnic
        var ethnicCollection = personContainer.getThePersonEthnicGroupDtoCollection();
        if (!ethnicCollection.isEmpty()) {
            updatePersonEthnic(personContainer);
        }

        // NOTE: Upsert EntityID
        var entityIdCollection = personContainer.getTheEntityIdDtoCollection();
        if (!entityIdCollection.isEmpty()) {
            updateEntityId(personContainer);
        }

        // NOTE: Create Entity Locator Participation
        var elpCollection = personContainer.getTheEntityLocatorParticipationDtoCollection();
        if (!elpCollection.isEmpty()) {
            entityLocatorParticipationService.updateEntityLocatorParticipation(elpCollection, personUid);
        }

        // NOTE: Upsert Role
        var roleCollection = personContainer.getTheRoleDtoCollection();
        if (!roleCollection.isEmpty()) {
            createRole(personContainer, OPERATION_UPDATE);
        }


        // NOTE: Updating MPR
        if (!Objects.equals(person.getPersonUid(), person.getPersonParentUid())) {
            var mpr = personJdbcRepository.selectByPersonUid(person.getPersonParentUid());
            mpr.setVersionCtrlNbr(person.getVersionCtrlNbr() + 1);
            if (person.getEthnicGroupInd() != null) {
                mpr.setEthnicGroupInd(person.getEthnicGroupInd());
            }
            mpr.setBirthCntryCd(null);
            personJdbcRepository.updatePerson(mpr);
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
    protected void updatePersonName(PersonContainer personContainer)   {
        List<PersonNameDto> personList = new ArrayList<>(personContainer.getThePersonNameDtoCollection());

        Long personUid = personContainer.getThePersonDto().getPersonUid();
        List<PersonName> existingNames = personJdbcRepository.findBySeqIdByParentUid(personUid);

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
                        var pPersonName = new PersonName(newNameDto, tz);
                        personJdbcRepository.mergePersonName(pPersonName);

                        // Clone for MPR
                        var mprCopy = SerializationUtils.clone(newNameDto);
                        mprCopy.setPersonUid(personContainer.getThePersonDto().getPersonParentUid());
                        var mprPersonName = new PersonName(mprCopy, tz);
                        personJdbcRepository.mergePersonName(mprPersonName);


                    } catch (Exception ex) {
                        logger.error("{} {}", ERROR_UPDATE_MSG, ex.getMessage()); // NOSONAR
                    }
                }
            }
        }
    }

    protected String defaultStr(String s) {
        return s != null ? s : "";
    }

    protected void createPersonName(PersonContainer personContainer)   {
        var personNameDtos = personContainer.getThePersonNameDtoCollection();
        if (personNameDtos == null || personNameDtos.isEmpty()) {
            return;
        }

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


    }

    @SuppressWarnings({"java:S3776", "java:S1141"})
    protected void updateEntityId(PersonContainer personContainer)   {
        var entityIdDtos = personContainer.getTheEntityIdDtoCollection();
        if (entityIdDtos == null || entityIdDtos.isEmpty()) {
            return;
        }

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
                entityIdJdbcRepository.mergeEntityId(new EntityId(mprCopy, tz));

                // Save for person
                dto.setEntityUid(personUid);
                dto.setAddReasonCd("Add");
                entityIdJdbcRepository.mergeEntityId(new EntityId(dto, tz));
            }
        }

    }


    @SuppressWarnings({"java:S1141","java:S3776"})
    protected void updatePersonRace(PersonContainer personContainer)   {
        var personRaceDtos = personContainer.getThePersonRaceDtoCollection();
        if (personRaceDtos == null || personRaceDtos.isEmpty()) {
            return;
        }

        Long parentUid = personContainer.getThePersonDto().getPersonParentUid();
        Long personUid = personContainer.getThePersonDto().getPersonUid();
        Long patientUid = -1L;

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
                var mprPersonRace = new PersonRace(mprRecord, tz);
                personJdbcRepository.mergePersonRace(mprPersonRace);

                // Save to actual person
                dto.setPersonUid(personUid);
                dto.setAddReasonCd("Add");
                var pPersonRace = new PersonRace(dto, tz);
                personJdbcRepository.mergePersonRace(pPersonRace);

            }
        }

        // Cleanup inactive races
        deleteInactivePersonRace(retainingRaceCodeList, patientUid, parentUid);

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
            if (parentUid != null && parentUid > 0) {
                assert patientUid != null;
                if (!patientUid.equals(parentUid)) {
                    var raceParent = personJdbcRepository.findByPersonRaceUid(parentUid);
                    if (raceParent != null && !raceParent.isEmpty()) {
                        dataModifierReposJdbc.deletePersonRaceByUid(parentUid, retainingRaceCodeList);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("{} {}", ERROR_UPDATE_MSG, e.getMessage()); // NOSONAR
        }
    }



    protected void createPersonRace(PersonContainer personContainer)   {
        var personRaceDtos = personContainer.getThePersonRaceDtoCollection();
        if (personRaceDtos == null || personRaceDtos.isEmpty()) {
            return;
        }

        Long personUid = personContainer.getThePersonDto().getPersonUid();

        for (PersonRaceDto dto : personRaceDtos) {
            dto.setPersonUid(personUid);
            dto.setAddReasonCd("Add");

            PersonRace entity = new PersonRace(dto, tz);

            personJdbcRepository.createPersonRace(entity);
        }

    }


    protected void createPersonEthnic(PersonContainer personContainer)   {
        var ethnicGroupDtos = personContainer.getThePersonEthnicGroupDtoCollection();
        if (ethnicGroupDtos == null || ethnicGroupDtos.isEmpty()) {
            return;
        }

        Long personUid = personContainer.getThePersonDto().getPersonUid();

        for (PersonEthnicGroupDto dto : ethnicGroupDtos) {
            dto.setPersonUid(personUid);
            PersonEthnicGroup entity = new PersonEthnicGroup(dto);

            personJdbcRepository.createPersonEthnicGroup(entity);
        }

    }


    protected void updatePersonEthnic(PersonContainer personContainer)   {
        var ethnicDtos = personContainer.getThePersonEthnicGroupDtoCollection();
        if (ethnicDtos == null || ethnicDtos.isEmpty()) {
            return;
        }

        Long personUid = personContainer.getThePersonDto().getPersonUid();
        Long parentUid = personContainer.getThePersonDto().getPersonParentUid();

        for (PersonEthnicGroupDto dto : ethnicDtos) {
            // Save to MPR
            var mprCopy = SerializationUtils.clone(dto);
            mprCopy.setPersonUid(parentUid);
            var mprPersonEthnic = new PersonEthnicGroup(mprCopy);
            personJdbcRepository.mergePersonEthnicGroup(mprPersonEthnic);

            // Save to actual person
            dto.setPersonUid(personUid);
            var pPersonEthnic = new PersonEthnicGroup(dto);
            personJdbcRepository.mergePersonEthnicGroup(pPersonEthnic);

        }

    }

    protected void createEntityId(PersonContainer personContainer)   {
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

        entityIdJdbcRepository.batchCreateEntityIds(entityIdEntities);

    }




    protected void createRole(PersonContainer personContainer, String operation)   {
        var roleDtos = personContainer.getTheRoleDtoCollection();
        if (roleDtos == null || roleDtos.isEmpty()) return;

        boolean isCreateOp = OPERATION_CREATE.equalsIgnoreCase(operation);

        for (RoleDto dto : roleDtos) {
            Role role = new Role(dto);
            if (isCreateOp) {
                roleJdbcRepository.createRole(role);
            } else {
                roleJdbcRepository.updateRole(role);
            }
        }

    }


    public List<Person> findPersonByParentUid(Long parentUid) {
        var res = personJdbcRepository.findPersonsByParentUid(parentUid);
        return Objects.requireNonNullElse(res, Collections.emptyList());
    }


    @SuppressWarnings({"java:S1871","java:S3776"})
    public PersonContainer preparePersonNameBeforePersistence(PersonContainer personContainer)   {
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

    }

    protected boolean isAfter(Date a, Date b) {
        return a != null && (b == null || a.after(b));
    }



}
