package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
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
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientRepositoryUtilTest {
    @Mock
    private PersonRepository personRepository;
    @Mock
    private EntityRepositoryUtil entityRepositoryUtil;
    @Mock
    private PersonNameRepository personNameRepository;
    @Mock
    private PersonRaceRepository personRaceRepository;
    @Mock
    private PersonEthnicRepository personEthnicRepository;
    @Mock
    private EntityIdRepository entityIdRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private IOdseIdGeneratorService odseIdGeneratorService;
    @Mock
    private IEntityLocatorParticipationService entityLocatorParticipationService;
    @InjectMocks
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(personRepository, entityRepositoryUtil, personNameRepository, personRaceRepository,
                personEthnicRepository, entityIdRepository, roleRepository,
                odseIdGeneratorService, entityLocatorParticipationService, authUtil);
    }

    @Test
    void updateExistingPersonEdxIndByUid_Test() {
        when(personRepository.findById(10L)).thenReturn(Optional.of(new Person()));
        var res = patientRepositoryUtil.updateExistingPersonEdxIndByUid(10L);
        assertNotNull(res);
    }

    @Test
    void findExistingPersonByUid_Null() {
        when(personRepository.findById(10L)).thenReturn(Optional.empty());
        var res = patientRepositoryUtil.findExistingPersonByUid(10L);
        assertNull(res);
    }

    @Test
    void findExistingPersonByUid_Test() {
        when(personRepository.findById(10L)).thenReturn(Optional.of(new Person()));
        var res = patientRepositoryUtil.findExistingPersonByUid(10L);
        assertNotNull(res);
    }

    @Test
    void createPerson_Test() throws DataProcessingException {
        var perCon = new PersonContainer();
        var id = new LocalUidGenerator();
        id.setSeedValueNbr(10L);
        id.setUidPrefixCd("TEST");
        id.setUidSuffixCd("TEST");
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(any())).thenReturn(id);

        var perDt = new PersonDto();
        perCon.setThePersonDto(perDt);

        var patNameCol = new ArrayList<PersonNameDto>();
        var patName = new PersonNameDto();
        patNameCol.add(patName);
        perCon.setThePersonNameDtoCollection(patNameCol);

        var patRaceCol = new ArrayList<PersonRaceDto>();
        var patRace = new PersonRaceDto();
        patRaceCol.add(patRace);
        perCon.setThePersonRaceDtoCollection(patRaceCol);

        var patEthCol = new ArrayList<PersonEthnicGroupDto>();
        var patEth = new PersonEthnicGroupDto();
        patEthCol.add(patEth);
        perCon.setThePersonEthnicGroupDtoCollection(patEthCol);

        var entityCol = new ArrayList<EntityIdDto>();
        var entity = new EntityIdDto();
        entityCol.add(entity);
        perCon.setTheEntityIdDtoCollection(entityCol);

        var patLocaCol = new ArrayList<EntityLocatorParticipationDto>();
        var patLoca = new EntityLocatorParticipationDto();
        patLocaCol.add(patLoca);
        perCon.setTheEntityLocatorParticipationDtoCollection(patLocaCol);


        var roleCol = new ArrayList<RoleDto>();
        var role = new RoleDto();
        roleCol.add(role);
        perCon.setTheRoleDtoCollection(roleCol);

        patientRepositoryUtil.createPerson(perCon);

        verify(entityLocatorParticipationService, times(1)).createEntityLocatorParticipation(any(), any());


    }


    @Test
    void updateExistingPerson_Test() throws DataProcessingException {
        var perCon = new PersonContainer();

        var perDt = new PersonDto();
        perDt.setVersionCtrlNbr(1);
        perDt.setPersonUid(10L);
        perDt.setLocalId("TEST");
        perDt.setFirstNm("TEST-1");
        perDt.setLastNm("TEST-1");
        perDt.setMiddleNm("TEST-1");
        perDt.setNmPrefix("TEST-1");
        perDt.setNmSuffix("TEST-1");
        perCon.setThePersonDto(perDt);


        var patNameCol = new ArrayList<PersonNameDto>();
        var patName = new PersonNameDto();
        patName.setFirstNm("TEST-1");
        patName.setLastNm("TEST-1");
        patName.setMiddleNm("TEST-1");
        patName.setNmPrefix("TEST-1");
        patName.setNmSuffix("TEST-1");
        patName.setItDelete(false);
        patNameCol.add(patName);
        patName = new PersonNameDto();
        patName.setFirstNm("TEST-1");
        patName.setLastNm("TEST-1");
        patName.setMiddleNm("TEST-1");
        patName.setNmPrefix("TEST-1");
        patName.setNmSuffix("TEST-1");
        patName.setItDelete(true);
        patNameCol.add(patName);
        patName = new PersonNameDto();
        patName.setFirstNm("TEST-1");
        patName.setLastNm("TEST-1");
        patName.setMiddleNm("TEST-1");
        patName.setNmPrefix("TEST-1");
        patName.setNmSuffix("TEST-1");
        patName.setItDelete(true);
        patNameCol.add(patName);
        perCon.setThePersonNameDtoCollection(patNameCol);

        var nameCol = new ArrayList<PersonName>();
        var name = new PersonName();
        name.setFirstNm("TEST");
        name.setLastNm("TEST");
        name.setMiddleNm("TEST");
        name.setNmPrefix("TEST");
        name.setNmSuffix("TEST");
        name.setPersonNameSeq(1);
        nameCol.add(name);
        when(personNameRepository.findBySeqIdByParentUid(10L)).thenReturn(nameCol);


        var patRaceCol = new ArrayList<PersonRaceDto>();
        var patRace = new PersonRaceDto();
        patRace.setPersonUid(1L);
        patRace.setRaceCd("CODE");
        patRace.setItDelete(true);
        patRaceCol.add(patRace);
        patRace = new PersonRaceDto();
        patRace.setPersonUid(1L);
        patRace.setRaceCd("CODE");
        patRace.setItDelete(false);
        patRaceCol.add(patRace);
        perCon.setThePersonRaceDtoCollection(patRaceCol);



        var patEthCol = new ArrayList<PersonEthnicGroupDto>();
        var patEth = new PersonEthnicGroupDto();
        patEthCol.add(patEth);
        perCon.setThePersonEthnicGroupDtoCollection(patEthCol);

        var entityCol = new ArrayList<EntityIdDto>();
        var entity = new EntityIdDto();
        entity.setEntityUid(1L);
        entity.setEntityIdSeq(1);
        entity.setItDelete(true);
        entityCol.add(entity);
        entity = new EntityIdDto();
        entity.setEntityUid(1L);
        entity.setEntityIdSeq(1);
        entity.setItDelete(false);
        entityCol.add(entity);
        perCon.setTheEntityIdDtoCollection(entityCol);

        var patLocaCol = new ArrayList<EntityLocatorParticipationDto>();
        var patLoca = new EntityLocatorParticipationDto();
        patLocaCol.add(patLoca);
        perCon.setTheEntityLocatorParticipationDtoCollection(patLocaCol);


        var roleCol = new ArrayList<RoleDto>();
        var role = new RoleDto();
        roleCol.add(role);
        perCon.setTheRoleDtoCollection(roleCol);

        patientRepositoryUtil.updateExistingPerson(perCon);

        verify(personRepository, times(1)).save(any());


    }


    @Test
    void findPersonByParentUid_Test() {
        Long parentUid = 10L;
        var perCol = new ArrayList<Person>();
        var per = new Person();
        perCol.add(per);
        when(personRepository.findByParentUid(parentUid)).thenReturn(Optional.of(perCol));

        var res = patientRepositoryUtil.findPersonByParentUid(parentUid);

        assertNotNull(res);

    }


    @Test
    void loadPerson_Test() {
        Long uid = 10L;
        var person = new Person();
        when(personRepository.findById(uid)).thenReturn(Optional.of(person));

        var perNameCol = new ArrayList<PersonName>();
        var perName = new PersonName();
        perNameCol.add(perName);
        when(personNameRepository.findByParentUid(uid)).thenReturn(Optional.of(perNameCol));

        var perRaceCol = new ArrayList<PersonRace>();
        var perRace = new PersonRace();
        perRaceCol.add(perRace);
        when(personRaceRepository.findByParentUid(uid)).thenReturn(Optional.of(perRaceCol));

        var perEthCol = new ArrayList<PersonEthnicGroup>();
        var perEth = new PersonEthnicGroup();
        perEthCol.add(perEth);
        when(personEthnicRepository.findByParentUid(uid)).thenReturn(Optional.of(perEthCol));

        var entiCol =new ArrayList<EntityId>();
        var enti = new EntityId();
        entiCol.add(enti);
        when(entityIdRepository.findByParentUid(uid)).thenReturn(Optional.of(entiCol));

        var loCol = new ArrayList<EntityLocatorParticipation>();
        var lo = new EntityLocatorParticipation();
        loCol.add(lo);
        when(entityLocatorParticipationService.findEntityLocatorById(uid)).thenReturn(loCol);

        var rolCol = new ArrayList<Role>();
        var role =new Role();
        rolCol.add(role);
        when(roleRepository.findByParentUid(uid)).thenReturn(Optional.of(rolCol));

        var res = patientRepositoryUtil.loadPerson(uid);

        assertNotNull(res);



    }


    @Test
    void findPatientParentUidByUid_Test() {
        var uid = 10L;
        var ids = new ArrayList<Long>();
        ids.add(10L);
        when(personRepository.findPatientParentUidByUid(uid)).thenReturn(Optional.of(ids));

        var res = patientRepositoryUtil.findPatientParentUidByUid(uid);

        assertNotNull(res);

    }

    @Test
    void findPatientParentUidByUid_Test_2() {
        var uid = 10L;
        when(personRepository.findPatientParentUidByUid(uid)).thenReturn(Optional.empty());

        var res = patientRepositoryUtil.findPatientParentUidByUid(uid);

        assertNull(res);

    }

    @Test
    void preparePersonNameBeforePersistence_Test() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var perNameCol = new ArrayList<PersonNameDto>();
        var perName = new PersonNameDto();
        perName.setNmUseCd("TEST");
        perNameCol.add(perName);
        perName = new PersonNameDto();
        perName.setNmUseCd("L");
        perName.setAsOfDate(TimeStampUtil.getCurrentTimeStamp());
        perNameCol.add(perName);
        perName = new PersonNameDto();
        perName.setNmUseCd("L");
        perName.setAsOfDate(TimeStampUtil.getCurrentTimeStamp());
        perNameCol.add(perName);
        perName = new PersonNameDto();
        perName.setNmUseCd("L");
        perNameCol.add(perName);
        personContainer.setThePersonNameDtoCollection(perNameCol);

        var res = patientRepositoryUtil.preparePersonNameBeforePersistence(personContainer);

        assertNotNull(res);
    }
}
