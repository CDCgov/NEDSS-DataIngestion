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
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class PatientRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(PatientRepositoryUtil.class);

    private final PersonRepository personRepository;
    private final EntityRepositoryUtil entityRepositoryUtil;
    private final PersonNameRepository personNameRepository;
    private final PersonRaceRepository personRaceRepository;
    private final PersonEthnicRepository personEthnicRepository;
    private final EntityIdRepository entityIdRepository;

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
            RoleRepository roleRepository,
            IOdseIdGeneratorWCacheService odseIdGeneratorService1,
            IEntityLocatorParticipationService entityLocatorParticipationService) {
        this.personRepository = personRepository;
        this.entityRepositoryUtil = entityRepositoryUtil;
        this.personNameRepository = personNameRepository;
        this.personRaceRepository = personRaceRepository;
        this.personEthnicRepository = personEthnicRepository;
        this.entityIdRepository = entityIdRepository;
        this.roleRepository = roleRepository;
        this.odseIdGeneratorService = odseIdGeneratorService1;
        this.entityLocatorParticipationService = entityLocatorParticipationService;
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

        entityRepositoryUtil.preparingEntityReposCallForPerson(personContainer.getThePersonDto(), personUid, NEDSSConstant.PERSON, NEDSSConstant.UPDATE);


        //NOTE: Create Person
        Person person = new Person(personContainer.getThePersonDto());
        person.setBirthCntryCd(null);
        personRepository.save(person);

        //NOTE: Create Person Name
        if  (personContainer.getThePersonNameDtoCollection() != null && !personContainer.getThePersonNameDtoCollection().isEmpty()) {
            createPersonName(personContainer);
        }
        //NOTE: Create Person Race
        if  (personContainer.getThePersonRaceDtoCollection() != null && !personContainer.getThePersonRaceDtoCollection().isEmpty()) {
            createPersonRace(personContainer);
        }
        //NOTE: Create Person Ethnic
        if  (personContainer.getThePersonEthnicGroupDtoCollection() != null && !personContainer.getThePersonEthnicGroupDtoCollection().isEmpty()) {
            createPersonEthnic(personContainer);
        }
        //NOTE: Create EntityID
        if  (personContainer.getTheEntityIdDtoCollection() != null && !personContainer.getTheEntityIdDtoCollection().isEmpty()) {
            createEntityId(personContainer);
        }
        //NOTE: Create Entity Locator Participation
        if  (personContainer.getTheEntityLocatorParticipationDtoCollection() != null && !personContainer.getTheEntityLocatorParticipationDtoCollection().isEmpty()) {
            entityLocatorParticipationService.createEntityLocatorParticipation(personContainer.getTheEntityLocatorParticipationDtoCollection(), personContainer.getThePersonDto().getPersonUid());
        }
        //NOTE: Create Role
        if  (personContainer.getTheRoleDtoCollection() != null && !personContainer.getTheRoleDtoCollection().isEmpty()) {
            createRole(personContainer);
        }

        return person;
    }

    @SuppressWarnings("java:S3776")
    @Transactional
    public void updateExistingPerson(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<Object>  arrayList = new ArrayList<>();

        arrayList.add(NEDSSConstant.PERSON);

        //NOTE: Update Person
        Person person = new Person(personContainer.getThePersonDto());
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
            createRole(personContainer);
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
        var personResult = personRepository.findById(personUid);
        if (personResult.isPresent()) {
            personDto = new PersonDto(personResult.get());
            personDto.setItDirty(false);
            personDto.setItNew(false);
        }
        personContainer.setThePersonDto(personDto);

        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();
        var personNameResult = personNameRepository.findByParentUid(personUid);
        if (personResult.isPresent() && personNameResult.isPresent()) {
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
                                personNameRepository.updatePersonNameStatus(personNameDto.getPersonUid(), seqId);

                                seqId++;
                                if (personName.getStatusCd() == null) {
                                    personName.setStatusCd("A");
                                }
                                if (personName.getStatusTime() == null) {
                                    personName.setStatusTime(new Timestamp(new Date().getTime()));
                                }

                                personName.setPersonNameSeq(seqId);
                                personName.setRecordStatusCd("ACTIVE");
                                personName.setAddReasonCd("Add");
                                personNameRepository.save(new PersonName(personName));

                                var mprRecord =  SerializationUtils.clone(personName);
                                mprRecord.setPersonUid(personContainer.getThePersonDto().getPersonParentUid());
                                personNameRepository.save(new PersonName(mprRecord));
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
                    personNameDto.setStatusTime(new Timestamp(new Date().getTime()));
                }
                personNameDto.setRecordStatusCd("ACTIVE");
                personNameDto.setAddReasonCd("Add");
                personNameRepository.save(new PersonName(personNameDto));
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
                    entityIdRepository.save(new EntityId(mprRecord));


                    var pUid = personContainer.getThePersonDto().getPersonUid();
                    entityIdDto.setEntityUid(pUid);
                    entityIdDto.setAddReasonCd("Add");
                    entityIdRepository.save(new EntityId(entityIdDto));
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
                        personRaceRepository.deletePersonRaceByUidAndCode(personRaceDto.getPersonUid(), personRaceDto.getRaceCd());
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
                    personRaceRepository.save(new PersonRace(mprRecord));

                    personRaceDto.setPersonUid(pUid);
                    personRaceDto.setAddReasonCd("Add");
                    personRaceRepository.save(new PersonRace(personRaceDto));


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


    private void createPersonRace(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<PersonRaceDto>  personList = (ArrayList<PersonRaceDto> ) personContainer.getThePersonRaceDtoCollection();
        try {
            for (PersonRaceDto personRaceDto : personList) {
                var pUid = personContainer.getThePersonDto().getPersonUid();
                personRaceDto.setPersonUid(pUid);
                personRaceDto.setAddReasonCd("Add");
                personRaceRepository.save(new PersonRace(personRaceDto));
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
                personEthnicRepository.save(new PersonEthnicGroup(personEthnicGroupDto));
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
        ArrayList<EntityIdDto>  personList = (ArrayList<EntityIdDto> ) personContainer.getTheEntityIdDtoCollection();
        try {
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
                entityIdRepository.save(new EntityId(entityIdDto));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }



    private void createRole(PersonContainer personContainer) throws DataProcessingException {
        ArrayList<RoleDto>  personList = (ArrayList<RoleDto> ) personContainer.getTheRoleDtoCollection();
        try {
            for (RoleDto obj : personList) {
                roleRepository.save(new Role(obj));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public List<Person> findPersonByParentUid(Long parentUid) {
        var res = personRepository.findByParentUid(parentUid);
        return res.orElseGet(ArrayList::new);
    }


    @SuppressWarnings({"java:S1871","java:S3776"})
    @Transactional
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
