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
import gov.cdc.dataprocessing.service.interfaces.uid_generator.localUid.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.PersonEthnicUpdate;
import gov.cdc.dataprocessing.utilities.model.PersonNameUpdate;
import gov.cdc.dataprocessing.utilities.model.PersonRaceUpdate;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private static final Logger logger = LoggerFactory.getLogger(PatientRepositoryUtil.class);
    @Value("${service.timezone}")
    private String tz = "UTC";
    private final PersonRepository personRepository;
    private final EntityRepositoryUtil entityRepositoryUtil;
    private final PersonNameRepository personNameRepository;
    private final PersonRaceRepository personRaceRepository;
    private final PersonEthnicRepository personEthnicRepository;
    private final EntityIdRepository entityIdRepository;

    private final RoleRepository roleRepository;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;

    private final IEntityLocatorParticipationService entityLocatorParticipationService;
    private final PatientRepositoryUtilJdbc patientRepositoryUtilJdbc;

    private static final String ERROR_DELETE_MSG = "Error Delete Patient Entity: ";
    private static final String ERROR_UPDATE_MSG = "Error Updating Existing Patient Entity: ";


    public PatientRepositoryUtil(
            PersonRepository personRepository,
            EntityRepositoryUtil entityRepositoryUtil,
            PersonNameRepository personNameRepository,
            PersonRaceRepository personRaceRepository,
            PersonEthnicRepository personEthnicRepository,
            EntityIdRepository entityIdRepository,
            RoleRepository roleRepository,
            IOdseIdGeneratorWCacheService odseIdGeneratorService1,
            IEntityLocatorParticipationService entityLocatorParticipationService, PatientRepositoryUtilJdbc patientRepositoryUtilJdbc) {
        this.personRepository = personRepository;
        this.entityRepositoryUtil = entityRepositoryUtil;
        this.personNameRepository = personNameRepository;
        this.personRaceRepository = personRaceRepository;
        this.personEthnicRepository = personEthnicRepository;
        this.entityIdRepository = entityIdRepository;
        this.roleRepository = roleRepository;
        this.odseIdGeneratorService = odseIdGeneratorService1;
        this.entityLocatorParticipationService = entityLocatorParticipationService;
        this.patientRepositoryUtilJdbc = patientRepositoryUtilJdbc;
    }

    @Transactional
    public Long updateExistingPersonEdxIndByUid(Long uid) {
        return (long) personRepository.updateExistingPersonEdxIndByUid(uid);
    }

    @Transactional
    public Person findExistingPersonByUid(Long personUid) {
        var result = personRepository.findById(personUid);
        return result.orElse(null);
    }

    @Transactional
    public Person createPerson(PersonContainer personContainer) throws DataProcessingException {
        Long personUid;
        String localUid;
        var localIdModel = odseIdGeneratorService.getValidLocalUid(LocalIdClass.PERSON, true);
        personUid = localIdModel.getGaTypeUid().getSeedValueNbr();
        localUid = localIdModel.getClassTypeUid().getUidPrefixCd() + localIdModel.getClassTypeUid().getSeedValueNbr() + localIdModel.getClassTypeUid().getUidSuffixCd();


        if(personContainer.getThePersonDto().getLocalId() == null || personContainer.getThePersonDto().getLocalId().trim().isEmpty()) {
            personContainer.getThePersonDto().setLocalId(localUid);
        }

        if(personContainer.getThePersonDto().getPersonParentUid() == null) {
            personContainer.getThePersonDto().setPersonParentUid(personUid);
        }

        // set new person uid in entity table
        personContainer.getThePersonDto().setPersonUid(personUid);
        List<Role> roles = null;
        List<EntityId> entityIds = null;

        // Create Entity
        var entity = entityRepositoryUtil.preparingEntityReposCallForPersonV2(personUid, NEDSSConstant.PERSON);
        //NOTE: Create Role
        if  (personContainer.getTheRoleDtoCollection() != null && !personContainer.getTheRoleDtoCollection().isEmpty()) {
            roles = createRoleV2(personContainer);
        }
        if (roles != null && !roles.isEmpty()) {
            entity.setRoles(roles);
        }
        //NOTE: Create EntityID
        if  (personContainer.getTheEntityIdDtoCollection() != null && !personContainer.getTheEntityIdDtoCollection().isEmpty()) {
            entityIds = createEntityIdV2(personContainer);
        }
        if (entityIds != null && !entityIds.isEmpty()) {
            entity.setEntityIds(entityIds);
        }
//        entityRepositoryUtil.saveEntity(entity);
        patientRepositoryUtilJdbc.insertEntity(entity);

        //NOTE: Create Person
        Person person = new Person(personContainer.getThePersonDto(), tz);
        person.setBirthCntryCd(null);

        List<PersonName> personNames = null;
        List<PersonRace> personRaces = null;
        List<PersonEthnicGroup> personEthnicGroups = null;



        //NOTE: Create Person Name
        if  (personContainer.getThePersonNameDtoCollection() != null && !personContainer.getThePersonNameDtoCollection().isEmpty()) {
            personNames = createPersonNameV2(personContainer);
        }
        //NOTE: Create Person Race
        if  (personContainer.getThePersonRaceDtoCollection() != null && !personContainer.getThePersonRaceDtoCollection().isEmpty()) {
            personRaces = createPersonRaceV2(personContainer);
        }
        //NOTE: Create Person Ethnic
        if  (personContainer.getThePersonEthnicGroupDtoCollection() != null && !personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
            personEthnicGroups = createPersonEthnicV2(personContainer);
        }

        person.setPersonRaces(personRaces);
        person.setPersonEthnicGroups(personEthnicGroups);
        person.setPersonNames(personNames);

//        personRepository.save(person);
        patientRepositoryUtilJdbc.savePersonWithDetails(person);
        //NOTE: Create Entity Locator Participation
        if  (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            entityLocatorParticipationService.createEntityLocatorParticipation(personContainer.getTheEntityLocatorParticipationDtoCollection(), personContainer.getThePersonDto().getPersonUid());
        }

        return person;
    }

    @SuppressWarnings("java:S3776")
    @Transactional
    public void updateExistingPerson(PersonContainer personContainer) throws DataProcessingException {
        //NOTE: Update Person
        Person person = new Person(personContainer.getThePersonDto(), tz);
        var ver = person.getVersionCtrlNbr();
        person.setVersionCtrlNbr(++ver);
        person.setBirthCntryCd(null);

        List<PersonName>  existingPersonName = personNameRepository.findBySeqIdByParentUid(personContainer.getThePersonDto().getPersonUid());

        // Consist both Revision and MPR personName Entities
        PersonNameUpdate personNameUpdate = null;
        PersonRaceUpdate personRaceUpdate = null;
        PersonEthnicUpdate personEthnicUpdate = null;
        //NOTE: Create Person Name
        if  (personContainer.getThePersonNameDtoCollection() != null && !personContainer.getThePersonNameDtoCollection().isEmpty()) {
            personNameUpdate = updatePersonName(personContainer, existingPersonName);
        }

        //NOTE: Create Person Race
        if  (personContainer.getThePersonRaceDtoCollection() != null && !personContainer.getThePersonRaceDtoCollection().isEmpty()) {
            personRaceUpdate = updatePersonRace(personContainer);
        }
        //NOTE: Create Person Ethnic
        if  (personContainer.getThePersonEthnicGroupDtoCollection() != null && !personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
            personEthnicUpdate = updatePersonEthnic(personContainer);
        }

        person.setPersonNames(null);
        person.setPersonRaces(null);
        person.setPersonEthnicGroups(null);
        personRepository.save(person);
        if (personNameUpdate != null) {
            var comList = new ArrayList<PersonName>();
            comList.addAll(personNameUpdate.getDomainList());
            comList.addAll(personNameUpdate.getDomainListMpr());
            personNameRepository.saveAll(comList);
        }

        if (personRaceUpdate != null) {
            var comList = new ArrayList<PersonRace>();
            comList.addAll(personRaceUpdate.getPersonRaceList());
            comList.addAll(personRaceUpdate.getPersonRaceMprList());
            personRaceRepository.saveAll(comList);
            personRaceRepository.deleteAll(personRaceUpdate.getPersonRaceMprList());
            deleteInactivePersonRace(personRaceUpdate.getRetainingRaceCodeListForDeletion(),
                    personRaceUpdate.getPatientUidForDeletion(),
                    personRaceUpdate.getParentUidForDeletion());
        }

        if (personEthnicUpdate != null) {
            var comList = new ArrayList<PersonEthnicGroup>();
            comList.addAll(personEthnicUpdate.getPersonEthnicGroupList());
            comList.addAll(personEthnicUpdate.getPersonEthnicGroupMprList());
            personEthnicRepository.saveAll(comList);
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
            var roles = createRoleV2(personContainer);
            if (!roles.isEmpty()) {
                roleRepository.saveAll(roles);
            }
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
        var personResult = patientRepositoryUtilJdbc.findById(personUid);
        if (personResult.isPresent()) {
            personDto = new PersonDto(personResult.get());
            personDto.setItDirty(false);
            personDto.setItNew(false);
        }
        personContainer.setThePersonDto(personDto);

        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();

        if (personResult.isPresent() && personResult.get().getPersonNames() != null) {
            for(var item : personResult.get().getPersonNames()) {
                var elem = new PersonNameDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                personNameDtoCollection.add(elem);
            }
        }
        personContainer.setThePersonNameDtoCollection(personNameDtoCollection);

        Collection<PersonRaceDto> personRaceDtoCollection = new ArrayList<>();
        if (personResult.isPresent() && personResult.get().getPersonRaces() != null) {
            for(var item : personResult.get().getPersonRaces()) {
                var elem = new PersonRaceDto(item);
                elem.setItDirty(false);
                elem.setItNew(false);
                personRaceDtoCollection.add(elem);
            }
        }
        personContainer.setThePersonRaceDtoCollection(personRaceDtoCollection);

        Collection<PersonEthnicGroupDto> personEthnicGroupDtoCollection = new ArrayList<>();
        if (personResult.isPresent() && personResult.get().getPersonEthnicGroups() != null) {
            for(var item : personResult.get().getPersonEthnicGroups()) {
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
        return result.map(List::getFirst).orElse(null);
    }
    @SuppressWarnings({"java:S3776","java:S1141"})
    private PersonNameUpdate updatePersonName(PersonContainer personContainer, List<PersonName> existingPersonNames) throws DataProcessingException {
        ArrayList<PersonNameDto>  personList = (ArrayList<PersonNameDto> ) personContainer.getThePersonNameDtoCollection();
        PersonNameUpdate personNameUpdate = new PersonNameUpdate();
        List<PersonName> domainList = new ArrayList<>();
        List<PersonName> domainListMpr = new ArrayList<>();
        if (existingPersonNames == null ) {
            existingPersonNames = new ArrayList<>();
        }

        Integer seqId = 1;

        StringBuilder sbFromInput = new StringBuilder();
        sbFromInput.append(personContainer.getThePersonDto().getFirstNm());
        sbFromInput.append(personContainer.getThePersonDto().getLastNm());
        sbFromInput.append(personContainer.getThePersonDto().getMiddleNm());
        sbFromInput.append(personContainer.getThePersonDto().getNmPrefix());
        sbFromInput.append(personContainer.getThePersonDto().getNmSuffix());


        List<String> personNameForComparing = new ArrayList<>();
        for(PersonName item : existingPersonNames) {
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
            existingPersonNames = existingPersonNames.stream().sorted(Comparator.comparing(PersonName::getPersonNameSeq).reversed()).toList();
            if (!existingPersonNames.isEmpty()) {
                seqId = existingPersonNames.getFirst().getPersonNameSeq();
                domainList.add(existingPersonNames.getFirst());
            }

            PersonNameDto personName = null;
            for (PersonNameDto personNameDto : personList) {
                if (!personNameDto.isItDelete()) {
                    personName = personNameDto;
                } else {
                    try {
                        if (personName != null)
                        {
                            // set existing record status to inactive
                            // personNameRepository.updatePersonNameStatus(personNameDto.getPersonUid(), seqId);
                            domainList.getFirst().setStatusCd("I");
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
                            domainList.add(new PersonName(personName, tz));

                            var mprRecord =  SerializationUtils.clone(personName);
                            mprRecord.setPersonUid(personContainer.getThePersonDto().getPersonParentUid());
                            domainListMpr.add(new PersonName(mprRecord, tz));
                        }
                    } catch (Exception e) {
                        logger.error("{} {}", ERROR_UPDATE_MSG, e.getMessage()); //NOSONAR
                    }
                }

            }
        }
        personNameUpdate.setDomainList(domainList);
        personNameUpdate.setDomainListMpr(domainListMpr);

        return personNameUpdate;
    }

    private List<PersonName> createPersonNameV2(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonNameDto>  personList = (ArrayList<PersonNameDto> ) personContainer.getThePersonNameDtoCollection();
        var domainList = new ArrayList<PersonName>();
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

            var domain = new PersonName(personNameDto,tz);
            domainList.add(domain);
        }

        return domainList;
    }

    @SuppressWarnings({"java:S3776", "java:S1141"})
    private void updateEntityId(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<EntityIdDto>  personList = (ArrayList<EntityIdDto> ) personContainer.getTheEntityIdDtoCollection();
        for (EntityIdDto entityIdDto : personList) {

            if (entityIdDto.isItDelete()) {
                try {
                    entityIdRepository.deleteEntityIdAndSeq(entityIdDto.getEntityUid(), entityIdDto.getEntityIdSeq());
                    if (personContainer.getThePersonDto().getPersonParentUid() != null) {
                        entityIdRepository.deleteEntityIdAndSeq(personContainer.getThePersonDto().getPersonParentUid(), entityIdDto.getEntityIdSeq());
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
    }

    @SuppressWarnings({"java:S1141","java:S3776"})
    private PersonRaceUpdate updatePersonRace(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonRaceDto>  personList = (ArrayList<PersonRaceDto> ) personContainer.getThePersonRaceDtoCollection();
        var parentUid = personContainer.getThePersonDto().getPersonParentUid();
        Long patientUid = -1L;
        PersonRaceUpdate personRaceUpdate = new PersonRaceUpdate();
        List<String> retainingRaceCodeList = new ArrayList<>();
        for (PersonRaceDto personRaceDto : personList) {
            var pUid = personContainer.getThePersonDto().getPersonUid();
            if (personRaceDto.isItDelete()) {
                personRaceUpdate.getPersonRaceDeleteList().add(new PersonRace(personRaceDto, tz));
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
                personRaceUpdate.getPersonRaceList().add(new PersonRace(mprRecord, tz));

                personRaceDto.setPersonUid(pUid);
                personRaceDto.setAddReasonCd("Add");
                personRaceUpdate.getPersonRaceList().add(new PersonRace(personRaceDto, tz));


            }
        }

        // Theses executes after the update process, whatever race not it the retain list and not direct assoc with parent uid will be deleted

        personRaceUpdate.setPatientUidForDeletion(patientUid);
        personRaceUpdate.setParentUidForDeletion(parentUid);
        personRaceUpdate.setRetainingRaceCodeListForDeletion(retainingRaceCodeList);

        return personRaceUpdate;
    }

    protected void deleteInactivePersonRace(List<String> retainingRaceCodeList, Long patientUid, Long parentUid) {
        // Theses executes after the update process, whatever race not it the retain list and not direct assoc with parent uid will be deleted
        if (!retainingRaceCodeList.isEmpty() && patientUid > 0) {
            try {
                personRaceRepository.deletePersonRaceByUid(patientUid,retainingRaceCodeList);
            } catch (Exception e) {
                logger.error("{} {}", ERROR_DELETE_MSG, e.getMessage()); //NOSONAR

            }
        }
        if (!retainingRaceCodeList.isEmpty() && parentUid > 0 && !patientUid.equals(parentUid)) {
            try {
                var raceParent = personRaceRepository.findByParentUid(parentUid);
                if (raceParent.isPresent() && raceParent.get().size() > 1) {
                    personRaceRepository.deletePersonRaceByUid(parentUid,retainingRaceCodeList);
                }
            } catch (Exception e) {
                logger.error("{} {}", ERROR_UPDATE_MSG, e.getMessage()); //NOSONAR

            }
        }
    }


    private List<PersonRace> createPersonRaceV2(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonRaceDto>  personList = (ArrayList<PersonRaceDto> ) personContainer.getThePersonRaceDtoCollection();
        var domainList = new ArrayList<PersonRace>();
        for (PersonRaceDto personRaceDto : personList) {
            var pUid = personContainer.getThePersonDto().getPersonUid();
            personRaceDto.setPersonUid(pUid);
            personRaceDto.setAddReasonCd("Add");
            domainList.add(new PersonRace(personRaceDto, tz));

        }
        return domainList;
    }

    private List<PersonEthnicGroup> createPersonEthnicV2(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonEthnicGroupDto>  personList = (ArrayList<PersonEthnicGroupDto> ) personContainer.getThePersonEthnicGroupDtoCollection();
        var domainList = new ArrayList<PersonEthnicGroup>();
        for (PersonEthnicGroupDto personEthnicGroupDto : personList) {
            var pUid = personContainer.getThePersonDto().getPersonUid();
            personEthnicGroupDto.setPersonUid(pUid);
            domainList.add(new PersonEthnicGroup(personEthnicGroupDto));
        }
        return domainList;
    }

    private PersonEthnicUpdate updatePersonEthnic(PersonContainer personContainer) throws DataProcessingException {
        PersonEthnicUpdate personEthnicUpdate = new PersonEthnicUpdate();
        var parentUid = personContainer.getThePersonDto().getPersonParentUid();
        for (PersonEthnicGroupDto personEthnicGroupDto : personContainer.getThePersonEthnicGroupDtoCollection()) {

            var mprRecord =  SerializationUtils.clone(personEthnicGroupDto);
            mprRecord.setPersonUid(parentUid);
            personEthnicUpdate.getPersonEthnicGroupMprList().add(new PersonEthnicGroup(mprRecord));

            var pUid = personContainer.getThePersonDto().getPersonUid();
            personEthnicGroupDto.setPersonUid(pUid);
            personEthnicUpdate.getPersonEthnicGroupList().add(new PersonEthnicGroup(personEthnicGroupDto));


        }
        return personEthnicUpdate;
    }

    private List<EntityId> createEntityIdV2(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<EntityIdDto>  personList = (ArrayList<EntityIdDto> ) personContainer.getTheEntityIdDtoCollection();
        var domainList = new ArrayList<EntityId>();
        for (EntityIdDto entityIdDto : personList) {
            var pUid = personContainer.getThePersonDto().getPersonUid();
            entityIdDto.setEntityUid(pUid);
            entityIdDto.setAddReasonCd("Add");
            if (entityIdDto.getAddUserId() == null) {
                entityIdDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());
            }
            if (entityIdDto.getLastChgUserId() == null) {
                entityIdDto.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
            }
            domainList.add(new EntityId(entityIdDto, tz));
        }

        return domainList;
    }



    private List<Role> createRoleV2(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<RoleDto>  personList = (ArrayList<RoleDto> ) personContainer.getTheRoleDtoCollection();
        var domainList = new ArrayList<Role>();
        for (RoleDto obj : personList) {
            domainList.add(new Role(obj));
        }
        return domainList;
    }

    public List<Person> findPersonByParentUid(Long parentUid) {
        var res = personRepository.findByParentUid(parentUid);
        return res.orElseGet(ArrayList::new);
    }


    @SuppressWarnings({"java:S1871","java:S3776"})
    @Transactional
    public PersonContainer preparePersonNameBeforePersistence(PersonContainer personContainer) throws DataProcessingException {
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
                    else if (selectedNameDT.getAsOfDate() != null && thePersonNameDto.getAsOfDate().after(selectedNameDT.getAsOfDate()))
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
        return personContainer;
    }


}
