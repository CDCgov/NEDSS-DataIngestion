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
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.EntityIdJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.PersonJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.RoleJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.entity.IEntityLocatorParticipationService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientRepositoryUtilTest {
    @Mock
    private PersonJdbcRepository personRepository;
    @Mock
    private EntityRepositoryUtil entityRepositoryUtil;

    @Mock
    private DataModifierReposJdbc dataModifierReposJdbc;

    @Mock
    private EntityIdJdbcRepository entityIdRepository;
    @Mock
    private RoleJdbcRepository roleRepository;
    @Mock
    private IOdseIdGeneratorWCacheService odseIdGeneratorService;
    @Mock
    private IEntityLocatorParticipationService entityLocatorParticipationService;
    @InjectMocks
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    AuthUtil authUtil;

    @Mock
    UidPoolManager uidPoolManager;

    @BeforeEach
    void setUp() throws DataProcessingException {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        var model = new LocalUidModel();
        LocalUidGeneratorDto dto = new LocalUidGeneratorDto();
        dto.setClassNameCd("TEST");
        dto.setTypeCd("TEST");
        dto.setUidPrefixCd("TEST");
        dto.setUidSuffixCd("TEST");
        dto.setSeedValueNbr(1L);
        dto.setCounter(3);
        dto.setUsedCounter(2);
        model.setClassTypeUid(dto);
        model.setGaTypeUid(dto);
        model.setPrimaryClassName("TEST");
        when(uidPoolManager.getNextUid(any(), anyBoolean())).thenReturn(model);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(personRepository, entityRepositoryUtil, entityIdRepository, roleRepository,
                odseIdGeneratorService, entityLocatorParticipationService, authUtil, dataModifierReposJdbc);
    }

    @Test
    void updateExistingPersonEdxIndByUid_Test() {
        when(dataModifierReposJdbc.updateExistingPersonEdxIndByUid(10L)).thenReturn(1);
        var res = patientRepositoryUtil.updateExistingPersonEdxIndByUid(10L);
        assertNotNull(res);
    }

    @Test
    void findExistingPersonByUid_Null() {
        when(personRepository.selectByPersonUid(10L)).thenReturn(null);
        var res = patientRepositoryUtil.findExistingPersonByUid(10L);
        assertNull(res);
    }

    @Test
    void findExistingPersonByUid_Test() {
        when(personRepository.selectByPersonUid(10L)).thenReturn(new Person());
        var res = patientRepositoryUtil.findExistingPersonByUid(10L);
        assertNotNull(res);
    }

    @Test
    void createPerson_Test() throws DataProcessingException {
        var perCon = new PersonContainer();
        var id = new LocalUidModel();
        id.setGaTypeUid(new LocalUidGeneratorDto());
        id.setClassTypeUid(new LocalUidGeneratorDto());
        id.getGaTypeUid().setSeedValueNbr(10L);
        id.getGaTypeUid().setUidPrefixCd("TEST");
        id.getGaTypeUid().setUidSuffixCd("TEST");

        id.getClassTypeUid().setSeedValueNbr(10L);
        id.getClassTypeUid().setUidPrefixCd("TEST");
        id.getClassTypeUid().setUidSuffixCd("TEST");
        when(odseIdGeneratorService.getValidLocalUid(any(), anyBoolean())).thenReturn(id);

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
        when(personRepository.findBySeqIdByParentUid(10L)).thenReturn(nameCol);


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

        when(personRepository.selectByPersonUid(any())).thenReturn(new Person(perCon.getThePersonDto(), "UTC"));

        patientRepositoryUtil.updateExistingPerson(perCon);

        verify(personRepository, times(2)).updatePerson(any());


    }


    @Test
    void findPersonByParentUid_Test() {
        Long parentUid = 10L;
        var perCol = new ArrayList<Person>();
        var per = new Person();
        perCol.add(per);
        when(personRepository.findPersonsByParentUid(parentUid)).thenReturn(perCol);

        var res = patientRepositoryUtil.findPersonByParentUid(parentUid);

        assertNotNull(res);

    }


    @Test
    void loadPerson_Test() {
        Long uid = 10L;
        var person = new Person();
        when(personRepository.findByPersonUid(uid)).thenReturn(person);

        var perNameCol = new ArrayList<PersonName>();
        var perName = new PersonName();
        perNameCol.add(perName);
        when(personRepository.findPersonNameByPersonUid(uid)).thenReturn(perNameCol);

        var perRaceCol = new ArrayList<PersonRace>();
        var perRace = new PersonRace();
        perRaceCol.add(perRace);
        when(personRepository.findPersonRaceByPersonUid(uid)).thenReturn(perRaceCol);

        var perEthCol = new ArrayList<PersonEthnicGroup>();
        var perEth = new PersonEthnicGroup();
        perEthCol.add(perEth);
        when(personRepository.findPersonEthnicByPersonUid(uid)).thenReturn(perEthCol);

        var entiCol =new ArrayList<EntityId>();
        var enti = new EntityId();
        entiCol.add(enti);
        when(entityIdRepository.findEntityIds(uid)).thenReturn(entiCol);

        var loCol = new ArrayList<EntityLocatorParticipation>();
        var lo = new EntityLocatorParticipation();
        loCol.add(lo);
        when(entityLocatorParticipationService.findEntityLocatorById(uid)).thenReturn(loCol);

        var rolCol = new ArrayList<Role>();
        var role =new Role();
        rolCol.add(role);
        when(roleRepository.findRolesByParentUid(uid)).thenReturn(rolCol);

        var res = patientRepositoryUtil.loadPerson(uid);

        assertNotNull(res);



    }


    @Test
    void findPatientParentUidByUid_Test() {
        var uid = 10L;
        var ids = new ArrayList<Long>();
        ids.add(10L);
        when(personRepository.findMprUid(uid)).thenReturn(uid);

        var res = patientRepositoryUtil.findPatientParentUidByUid(uid);

        assertNotNull(res);

    }

    @Test
    void findPatientParentUidByUid_Test_2() {
        var uid = 10L;
        when(personRepository.findMprUid(uid)).thenReturn(null);

        var res = patientRepositoryUtil.findPatientParentUidByUid(uid);

        assertNull(res);

    }

    @Test
    void preparePersonNameBeforePersistence_Test()  {
        PersonContainer personContainer = new PersonContainer();
        var perNameCol = new ArrayList<PersonNameDto>();
        var perName = new PersonNameDto();
        perName.setNmUseCd("TEST");
        perNameCol.add(perName);
        perName = new PersonNameDto();
        perName.setNmUseCd("L");
        perName.setAsOfDate(TimeStampUtil.getCurrentTimeStamp("UTC"));
        perNameCol.add(perName);
        perName = new PersonNameDto();
        perName.setNmUseCd("L");
        perName.setAsOfDate(TimeStampUtil.getCurrentTimeStamp("UTC"));
        perNameCol.add(perName);
        perName = new PersonNameDto();
        perName.setNmUseCd("L");
        perNameCol.add(perName);
        personContainer.setThePersonNameDtoCollection(perNameCol);

        var res = patientRepositoryUtil.preparePersonNameBeforePersistence(personContainer);

        assertNotNull(res);
    }

    @Test
    void deleteInactivePersonRace_Test() {
        List<String> retainingRaceCodeList = new ArrayList<>();
        Long patientUid = 10L;
        Long parentUid = 11L;

        retainingRaceCodeList.add("TEST");

        var personRaceCol = new ArrayList<PersonRace>();
        personRaceCol.add(new PersonRace());
        personRaceCol.add(new PersonRace());
        when(personRepository.findByPersonRaceUid(11L)).thenReturn(personRaceCol);


        patientRepositoryUtil.deleteInactivePersonRace(retainingRaceCodeList, patientUid, parentUid);

        verify(dataModifierReposJdbc, times(1)).deletePersonRaceByUid(eq(10L),any());
    }

    @Test
    void deleteInactivePersonRace_Test_Exp() {
        List<String> retainingRaceCodeList = new ArrayList<>();
        Long patientUid = 10L;
        Long parentUid = 11L;

        retainingRaceCodeList.add("TEST");


        doThrow(new RuntimeException("TEST")).when(dataModifierReposJdbc).deletePersonRaceByUid(eq(10L), any());
        when(personRepository.findByPersonRaceUid(11L)).thenThrow(new RuntimeException("TEST"));


        patientRepositoryUtil.deleteInactivePersonRace(retainingRaceCodeList, patientUid, parentUid);

        verify(dataModifierReposJdbc, times(0)).deletePersonRaceByUid(eq(11L),any());
    }

    @Test
    void deleteInactivePersonRace_Test_2() {
        List<String> retainingRaceCodeList = new ArrayList<>();
        Long patientUid = 10L;
        Long parentUid = 11L;

        retainingRaceCodeList.add("TEST");

        var personRaceCol = new ArrayList<PersonRace>();
        personRaceCol.add(new PersonRace());
        when(personRepository.findByPersonRaceUid(11L)).thenReturn(personRaceCol);


        patientRepositoryUtil.deleteInactivePersonRace(retainingRaceCodeList, patientUid, parentUid);

        verify(dataModifierReposJdbc, times(1)).deletePersonRaceByUid(eq(11L),any());
    }



    @Test
    void deleteInactivePersonRace_Test_3() {
        List<String> retainingRaceCodeList = new ArrayList<>();
        Long patientUid = -10L;
        Long parentUid = -11L;




        patientRepositoryUtil.deleteInactivePersonRace(retainingRaceCodeList, patientUid, parentUid);

        verify(dataModifierReposJdbc, times(0)).deletePersonRaceByUid(eq(10L),any());
    }
}
