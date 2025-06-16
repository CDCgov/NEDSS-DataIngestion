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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings({"java:S6068", "java:S2699"})
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

    private void mockUidPool(long uidSeed, String prefix, long mid, String suffix) throws DataProcessingException {
        LocalUidGeneratorDto classType = new LocalUidGeneratorDto();
        classType.setUidPrefixCd(prefix);
        classType.setUidSuffixCd(suffix);
        classType.setSeedValueNbr(mid);

        LocalUidGeneratorDto gaType = new LocalUidGeneratorDto();
        gaType.setSeedValueNbr(uidSeed);

        LocalUidModel uidModel = new LocalUidModel();
        uidModel.setClassTypeUid(classType);
        uidModel.setGaTypeUid(gaType);

        when(uidPoolManager.getNextUid(eq(LocalIdClass.PERSON), eq(true))).thenReturn(uidModel);
    }

    @Test
    void testCreatePerson_allFieldsNullOrEmpty() throws Exception {
        mockUidPool(123L, "PX", 456L, "SUF");

        PersonDto personDto = new PersonDto();
        personDto.setLocalId(null);
        personDto.setPersonParentUid(null);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);

        Person person = patientRepositoryUtil.createPerson(container);

        assertEquals("PX456SUF", person.getLocalId());
        assertEquals(123L, person.getPersonUid());
        verify(personRepository).createPerson(any());
    }

    @Test
    void testCreatePerson_allCollectionsEmpty() throws Exception {
        mockUidPool(1L, "P", 100L, "X");

        PersonDto personDto = new PersonDto();
        personDto.setLocalId(" ");
        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);

        container.setThePersonNameDtoCollection(Collections.emptyList());
        container.setThePersonRaceDtoCollection(Collections.emptyList());
        container.setThePersonEthnicGroupDtoCollection(Collections.emptyList());
        container.setTheEntityIdDtoCollection(Collections.emptyList());
        container.setTheEntityLocatorParticipationDtoCollection(Collections.emptyList());
        container.setTheRoleDtoCollection(Collections.emptyList());

        Person person = patientRepositoryUtil.createPerson(container);

        assertNotNull(person);
        verify(personRepository).createPerson(any());
    }

    @Test
    void testCreatePerson_withEachSubEntityCollection() throws Exception {
        mockUidPool(10L, "P", 20L, "Z");

        PersonDto dto = new PersonDto();
        dto.setLocalId("EXISTING");
        PersonContainer container = new PersonContainer();
        container.setThePersonDto(dto);

        container.setThePersonNameDtoCollection(List.of(new PersonNameDto()));
        container.setThePersonRaceDtoCollection(List.of(new PersonRaceDto()));
        container.setThePersonEthnicGroupDtoCollection(List.of(new PersonEthnicGroupDto()));
        container.setTheEntityIdDtoCollection(List.of(new EntityIdDto()));
        container.setTheEntityLocatorParticipationDtoCollection(List.of(new EntityLocatorParticipationDto()));
        container.setTheRoleDtoCollection(List.of(new RoleDto()));

        patientRepositoryUtil.createPerson(container);

        verify(personRepository).createPerson(any());
        verify(entityLocatorParticipationService).createEntityLocatorParticipation(any(), anyLong());
    }


    @Test
    void testCreatePerson_allFieldsNullOrEmpty2() throws Exception {
        mockUidPool(123L, "PX", 456L, "SUF");

        PersonDto personDto = new PersonDto();
        personDto.setLocalId(null);
        personDto.setPersonParentUid(1L);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);

        Person person = patientRepositoryUtil.createPerson(container);

        assertEquals("PX456SUF", person.getLocalId());
        assertEquals(123L, person.getPersonUid());
        verify(personRepository).createPerson(any());
    }

    @Test
    void testUpdateExistingPerson_allCollectionsEmpty() throws Exception {
        PersonDto dto = new PersonDto();
        dto.setPersonUid(1L);
        dto.setPersonParentUid(1L);
        dto.setVersionCtrlNbr(1);
        PersonContainer container = new PersonContainer();
        container.setThePersonDto(dto);
        container.setThePersonNameDtoCollection(Collections.emptyList());
        container.setThePersonRaceDtoCollection(Collections.emptyList());
        container.setThePersonEthnicGroupDtoCollection(Collections.emptyList());
        container.setTheEntityIdDtoCollection(Collections.emptyList());
        container.setTheEntityLocatorParticipationDtoCollection(Collections.emptyList());
        container.setTheRoleDtoCollection(Collections.emptyList());

        when(personRepository.selectByPersonUid(anyLong())).thenReturn(new Person());

        patientRepositoryUtil.updateExistingPerson(container);

        verify(personRepository).updatePerson(any(Person.class));
    }

    @Test
    void testUpdateExistingPerson_withCollections() throws Exception {
        PersonDto dto = new PersonDto();
        dto.setPersonUid(1L);
        dto.setPersonParentUid(1L);
        dto.setVersionCtrlNbr(1);
        PersonContainer container = new PersonContainer();
        container.setThePersonDto(dto);
        container.setThePersonNameDtoCollection(List.of(new PersonNameDto()));
        container.setThePersonRaceDtoCollection(List.of(new PersonRaceDto()));
        container.setThePersonEthnicGroupDtoCollection(List.of(new PersonEthnicGroupDto()));
        container.setTheEntityIdDtoCollection(List.of(new EntityIdDto()));
        container.setTheEntityLocatorParticipationDtoCollection(List.of(new EntityLocatorParticipationDto()));
        container.setTheRoleDtoCollection(List.of(new RoleDto()));

        when(personRepository.selectByPersonUid(anyLong())).thenReturn(new Person());

        patientRepositoryUtil.updateExistingPerson(container);

        verify(personRepository).updatePerson(any(Person.class));
        verify(entityLocatorParticipationService).updateEntityLocatorParticipation(anyList(), anyLong());
    }

    @Test
    void testUpdateExistingPerson_personNotMpr() throws Exception {
        PersonDto dto = new PersonDto();
        dto.setPersonUid(2L);
        dto.setPersonParentUid(1L);
        dto.setEthnicGroupInd("Hispanic");

        Person mpr = new Person();
        mpr.setPersonUid(1L);
        dto.setVersionCtrlNbr(1);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(dto);
        container.setThePersonNameDtoCollection(Collections.emptyList());
        container.setThePersonRaceDtoCollection(Collections.emptyList());
        container.setThePersonEthnicGroupDtoCollection(Collections.emptyList());
        container.setTheEntityIdDtoCollection(Collections.emptyList());
        container.setTheEntityLocatorParticipationDtoCollection(Collections.emptyList());
        container.setTheRoleDtoCollection(Collections.emptyList());

        when(personRepository.selectByPersonUid(1L)).thenReturn(mpr);

        patientRepositoryUtil.updateExistingPerson(container);

        verify(personRepository, times(2)).updatePerson(any(Person.class));
        assertEquals("Hispanic", mpr.getEthnicGroupInd());
    }

    @Test
    void testLoadPerson_personNotFound() {
        Long personUid = 100L;

        when(personRepository.findByPersonUid(personUid)).thenReturn(null);
        when(personRepository.findPersonNameByPersonUid(personUid)).thenReturn(Collections.emptyList());
        when(personRepository.findPersonRaceByPersonUid(personUid)).thenReturn(Collections.emptyList());
        when(personRepository.findPersonEthnicByPersonUid(personUid)).thenReturn(Collections.emptyList());
        when(entityIdRepository.findEntityIds(personUid)).thenReturn(Collections.emptyList());
        when(entityLocatorParticipationService.findEntityLocatorById(personUid)).thenReturn(Collections.emptyList());
        when(roleRepository.findRolesByParentUid(personUid)).thenReturn(Collections.emptyList());

        PersonContainer result = patientRepositoryUtil.loadPerson(personUid);

        assertTrue(result.getThePersonNameDtoCollection().isEmpty());
        assertTrue(result.getThePersonRaceDtoCollection().isEmpty());
        assertTrue(result.getThePersonEthnicGroupDtoCollection().isEmpty());
        assertTrue(result.getTheEntityIdDtoCollection().isEmpty());
        assertTrue(result.getTheEntityLocatorParticipationDtoCollection().isEmpty());
        assertTrue(result.getTheRoleDtoCollection().isEmpty());
    }

    @Test
    void testLoadPerson_allRepositoriesReturnEmpty() {
        Long personUid = 101L;

        when(personRepository.findByPersonUid(personUid)).thenReturn(new Person());
        when(personRepository.findPersonNameByPersonUid(personUid)).thenReturn(Collections.emptyList());
        when(personRepository.findPersonRaceByPersonUid(personUid)).thenReturn(Collections.emptyList());
        when(personRepository.findPersonEthnicByPersonUid(personUid)).thenReturn(Collections.emptyList());
        when(entityIdRepository.findEntityIds(personUid)).thenReturn(Collections.emptyList());
        when(entityLocatorParticipationService.findEntityLocatorById(personUid)).thenReturn(Collections.emptyList());
        when(roleRepository.findRolesByParentUid(personUid)).thenReturn(Collections.emptyList());

        PersonContainer result = patientRepositoryUtil.loadPerson(personUid);

        assertNotNull(result.getThePersonDto());
        assertTrue(result.getThePersonNameDtoCollection().isEmpty());
        assertTrue(result.getThePersonRaceDtoCollection().isEmpty());
        assertTrue(result.getThePersonEthnicGroupDtoCollection().isEmpty());
        assertTrue(result.getTheEntityIdDtoCollection().isEmpty());
        assertTrue(result.getTheEntityLocatorParticipationDtoCollection().isEmpty());
        assertTrue(result.getTheRoleDtoCollection().isEmpty());
    }

    @Test
    void testLoadPerson_someCollectionsPopulated() {
        Long personUid = 102L;

        when(personRepository.findByPersonUid(personUid)).thenReturn(new Person());
        when(personRepository.findPersonNameByPersonUid(personUid)).thenReturn(List.of(new PersonName()));
        when(personRepository.findPersonRaceByPersonUid(personUid)).thenReturn(List.of(new PersonRace()));
        when(personRepository.findPersonEthnicByPersonUid(personUid)).thenReturn(Collections.emptyList());
        when(entityIdRepository.findEntityIds(personUid)).thenReturn(Collections.emptyList());
        when(entityLocatorParticipationService.findEntityLocatorById(personUid)).thenReturn(Collections.emptyList());
        when(roleRepository.findRolesByParentUid(personUid)).thenReturn(Collections.emptyList());

        PersonContainer result = patientRepositoryUtil.loadPerson(personUid);

        assertEquals(1, result.getThePersonNameDtoCollection().size());
        assertEquals(1, result.getThePersonRaceDtoCollection().size());
        assertTrue(result.getThePersonEthnicGroupDtoCollection().isEmpty());
        assertTrue(result.getTheEntityIdDtoCollection().isEmpty());
        assertTrue(result.getTheEntityLocatorParticipationDtoCollection().isEmpty());
        assertTrue(result.getTheRoleDtoCollection().isEmpty());
    }

    @Test
    void testLoadPerson_allCollectionsHaveOneItem() {
        Long personUid = 103L;

        when(personRepository.findByPersonUid(personUid)).thenReturn(new Person());
        when(personRepository.findPersonNameByPersonUid(personUid)).thenReturn(List.of(new PersonName()));
        when(personRepository.findPersonRaceByPersonUid(personUid)).thenReturn(List.of(new PersonRace()));
        when(personRepository.findPersonEthnicByPersonUid(personUid)).thenReturn(List.of(new PersonEthnicGroup()));
        when(entityIdRepository.findEntityIds(personUid)).thenReturn(List.of(new EntityId()));
        when(entityLocatorParticipationService.findEntityLocatorById(personUid)).thenReturn(List.of(new EntityLocatorParticipation()));
        when(roleRepository.findRolesByParentUid(personUid)).thenReturn(List.of(new Role()));

        PersonContainer result = patientRepositoryUtil.loadPerson(personUid);

        assertEquals(1, result.getThePersonNameDtoCollection().size());
        assertEquals(1, result.getThePersonRaceDtoCollection().size());
        assertEquals(1, result.getThePersonEthnicGroupDtoCollection().size());
        assertEquals(1, result.getTheEntityIdDtoCollection().size());
        assertEquals(1, result.getTheEntityLocatorParticipationDtoCollection().size());
        assertEquals(1, result.getTheRoleDtoCollection().size());
    }

    @Test
    void testUpdatePersonName_newNameAndInactivateExisting() {
        Long personUid = 200L;
        Long parentUid = 300L;

        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(personUid);
        personDto.setPersonParentUid(parentUid);
        personDto.setFirstNm("John");
        personDto.setLastNm("Doe");
        personDto.setMiddleNm("M");
        personDto.setNmPrefix("Mr");
        personDto.setNmSuffix("Sr");

        PersonName existingName = new PersonName();
        existingName.setFirstNm("Jane");
        existingName.setLastNm("Smith");
        existingName.setMiddleNm("A");
        existingName.setNmPrefix("Ms");
        existingName.setNmSuffix("");
        existingName.setPersonNameSeq(1);

        PersonNameDto dto = new PersonNameDto();
        dto.setItDelete(true);
        dto.setPersonUid(personUid);

        PersonNameDto newDto = new PersonNameDto();
        newDto.setItDelete(false);
        newDto.setPersonUid(personUid);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setThePersonNameDtoCollection(List.of(dto, newDto));

        when(personRepository.findBySeqIdByParentUid(personUid)).thenReturn(List.of(existingName));
        doNothing().when(dataModifierReposJdbc).updatePersonNameStatus(anyLong(), anyInt());
        doNothing().when(personRepository).mergePersonName(any());

        patientRepositoryUtil.updatePersonName(container);

        verify(personRepository, times(0)).mergePersonName(any());
    }

    @Test
    void testUpdatePersonName_exceptionDuringProcessing() {
        Long personUid = 201L;
        Long parentUid = 301L;

        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(personUid);
        personDto.setPersonParentUid(parentUid);
        personDto.setFirstNm("John");
        personDto.setLastNm("Doe");

        PersonName existingName = new PersonName();
        existingName.setFirstNm("Jane");
        existingName.setLastNm("Smith");
        existingName.setPersonNameSeq(5);

        PersonNameDto dto = new PersonNameDto();
        dto.setItDelete(true);
        dto.setPersonUid(personUid);

        PersonNameDto newDto = new PersonNameDto();
        newDto.setItDelete(false);
        newDto.setPersonUid(personUid);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setThePersonNameDtoCollection(List.of(dto, newDto));

        when(personRepository.findBySeqIdByParentUid(personUid)).thenReturn(List.of(existingName));
        doThrow(new RuntimeException("DB error")).when(dataModifierReposJdbc).updatePersonNameStatus(anyLong(), anyInt());

        patientRepositoryUtil.updatePersonName(container);

    }



    @Test
    void testCreatePersonName_emptyCollection() {
        PersonContainer container = new PersonContainer();
        container.setThePersonNameDtoCollection(Collections.emptyList());

        patientRepositoryUtil.createPersonName(container);

        verify(personRepository, never()).createPersonName(any());
    }

    @Test
    void testCreatePersonName_validDtoWithStatusCdAndTime() {
        Long personUid = 123L;
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(personUid);

        PersonNameDto dto = new PersonNameDto();
        dto.setStatusCd("A");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setItNew(true);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setThePersonNameDtoCollection(List.of(dto));

        patientRepositoryUtil.createPersonName(container);

        verify(personRepository, times(1)).createPersonName(any(PersonName.class));
    }

    @Test
    void createPersonNameDtosEmpty() {
        PersonContainer container = new PersonContainer();
        patientRepositoryUtil.createPersonName(container);
        verify(personRepository, times(0)).createPersonName(any(PersonName.class));

    }

    @Test
    void updateEntityIdDtsEmpty() {
        PersonContainer container = new PersonContainer();
        patientRepositoryUtil.updateEntityId(container);
        verify(entityIdRepository, times(0)).mergeEntityId(any());

    }

    @Test
    void testUpdateEntityId_deletePath_noException() {
        EntityIdDto dto = new EntityIdDto();
        dto.setItDelete(true);
        dto.setEntityUid(100L);
        dto.setEntityIdSeq(1);

        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(100L);
        personDto.setPersonParentUid(200L);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setTheEntityIdDtoCollection(List.of(dto));

        patientRepositoryUtil.updateEntityId(container);

        verify(dataModifierReposJdbc).deleteEntityIdAndSeq(100L, 1);
        verify(dataModifierReposJdbc).deleteEntityIdAndSeq(200L, 1);
    }

    @Test
    void testUpdateEntityId_deletePath_withException() {
        EntityIdDto dto = new EntityIdDto();
        dto.setItDelete(true);
        dto.setEntityUid(100L);
        dto.setEntityIdSeq(1);

        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(100L);
        personDto.setPersonParentUid(200L);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setTheEntityIdDtoCollection(List.of(dto));

        doThrow(new RuntimeException("delete error"))
                .when(dataModifierReposJdbc).deleteEntityIdAndSeq(anyLong(), anyInt());

        patientRepositoryUtil.updateEntityId(container);

        verify(dataModifierReposJdbc, atLeastOnce()).deleteEntityIdAndSeq(anyLong(), anyInt());
    }


    @Test
    void testUpdateEntityId_mergePath_withNullUserIds() {
        EntityIdDto dto = new EntityIdDto();
        dto.setItDelete(false);
        dto.setAddUserId(null);
        dto.setLastChgUserId(null);
        dto.setEntityIdSeq(1);

        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(101L);
        personDto.setPersonParentUid(201L);

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setTheEntityIdDtoCollection(List.of(dto));

        AuthUser authUser = mock(AuthUser.class);
        when(authUser.getNedssEntryId()).thenReturn(12345L);

        patientRepositoryUtil.updateEntityId(container);

        verify(entityIdRepository, times(2)).mergeEntityId(any(EntityId.class));
    }

    @Test
    void testPreparePersonNameBeforePersistence_whenNameDtosNullOrEmpty() {
        PersonContainer container = new PersonContainer();
        container.setThePersonNameDtoCollection(null);

        PersonContainer result = patientRepositoryUtil.preparePersonNameBeforePersistence(container);

        assertSame(container, result);

        // Now test with empty list
        container.setThePersonNameDtoCollection(Collections.emptyList());
        result = patientRepositoryUtil.preparePersonNameBeforePersistence(container);
        assertSame(container, result);
    }


    @Test
    void testUpdatePersonRace_whenCollectionIsNullOrEmpty() {
        PersonContainer container = new PersonContainer();
        container.setThePersonRaceDtoCollection(null); // null case
        container.setThePersonDto(new PersonDto());

        patientRepositoryUtil.updatePersonRace(container); // should not throw or do anything

        // now with empty list
        container.setThePersonRaceDtoCollection(Collections.emptyList());

        patientRepositoryUtil.updatePersonRace(container); // again, should not throw
    }

    @Test
    void testUpdatePersonRace_whenDtoMarkedForDelete_callsDeleteMethod() {
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personDto.setPersonParentUid(2L);

        PersonRaceDto raceDto = new PersonRaceDto();
        raceDto.setItDelete(true);
        raceDto.setPersonUid(1L);
        raceDto.setRaceCd("2054-5");

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setThePersonRaceDtoCollection(List.of(raceDto));

        patientRepositoryUtil.updatePersonRace(container);

        verify(dataModifierReposJdbc).deletePersonRaceByUidAndCode(1L, "2054-5");
    }


    @Test
    void testUpdatePersonRace_whenDtoDirtyAndUidNotEqualParent_addsToRetainList() {
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(3L);
        personDto.setPersonParentUid(2L);

        PersonRaceDto raceDto = new PersonRaceDto();
        raceDto.setItDelete(false);
        raceDto.setItDirty(true);
        raceDto.setPersonUid(3L);
        raceDto.setRaceCd("1002-5");

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setThePersonRaceDtoCollection(List.of(raceDto));

        patientRepositoryUtil.updatePersonRace(container);

        verify(personRepository, times(2)).mergePersonRace(any(PersonRace.class));
    }

    @Test
    void testUpdatePersonRace_whenDeleteThrowsException_logsError() {
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personDto.setPersonParentUid(2L);

        PersonRaceDto raceDto = new PersonRaceDto();
        raceDto.setItDelete(true);
        raceDto.setPersonUid(1L);
        raceDto.setRaceCd("2054-5");

        PersonContainer container = new PersonContainer();
        container.setThePersonDto(personDto);
        container.setThePersonRaceDtoCollection(List.of(raceDto));

        // Simulate exception
        doThrow(new RuntimeException("Simulated deletion failure"))
                .when(dataModifierReposJdbc).deletePersonRaceByUidAndCode(1L, "2054-5");

        // Run
        assertDoesNotThrow(() -> patientRepositoryUtil.updatePersonRace(container));

        // Verify that the method was called (even though it failed)
        verify(dataModifierReposJdbc).deletePersonRaceByUidAndCode(1L, "2054-5");

    }


    @Test
    void test_retainingList_null() {
        patientRepositoryUtil.deleteInactivePersonRace(null, 10L, 20L);
        verifyNoInteractions(dataModifierReposJdbc);
    }

    @Test
    void test_retainingList_empty() {
        patientRepositoryUtil.deleteInactivePersonRace(Collections.emptyList(), 10L, 20L);
        verifyNoInteractions(dataModifierReposJdbc);
    }

    @Test
    void test_patientUid_null() {
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), null, 20L);
        verify(dataModifierReposJdbc, never()).deletePersonRaceByUid(any(), any());
    }

    @Test
    void test_patientUid_zero() {
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), 0L, 20L);
        verify(dataModifierReposJdbc, never()).deletePersonRaceByUid(eq(0L), any());
    }

    @Test
    void test_patientUid_valid_callsDelete() {
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), 10L, 20L);
        verify(dataModifierReposJdbc).deletePersonRaceByUid(10L, List.of("A"));
    }

    @Test
    void test_patientUid_throwsException_logsError() {
        doThrow(new RuntimeException("Simulated")).when(dataModifierReposJdbc).deletePersonRaceByUid(eq(10L), any());
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), 10L, 20L);
        verify(dataModifierReposJdbc).deletePersonRaceByUid(eq(10L), any());
    }

    @Test
    void test_parentUid_null() {
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), 10L, null);
        verify(personRepository, never()).findByPersonRaceUid(any());
        verify(dataModifierReposJdbc, never()).deletePersonRaceByUid(eq(null), any());
    }

    @Test
    void test_parentUid_sameAsPatientUid_shouldNotDeleteAgain() {
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), 10L, 10L);
        verify(personRepository, never()).findByPersonRaceUid(any());
    }

    @Test
    void test_parentUid_diffAndRecordFound_shouldDelete() {
        when(personRepository.findByPersonRaceUid(20L)).thenReturn(List.of(new PersonRace()));
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), 10L, 20L);
        verify(dataModifierReposJdbc).deletePersonRaceByUid(20L, List.of("A"));
    }

    @Test
    void test_parentUid_diffAndRecordEmpty_shouldNotDelete() {
        when(personRepository.findByPersonRaceUid(20L)).thenReturn(Collections.emptyList());
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), 10L, 20L);
        verify(dataModifierReposJdbc, never()).deletePersonRaceByUid(20L, List.of("A"));
    }

    @Test
    void test_parentUid_exception_logsError() {
        when(personRepository.findByPersonRaceUid(20L)).thenThrow(new RuntimeException("Error"));
        patientRepositoryUtil.deleteInactivePersonRace(List.of("A"), 10L, 20L);
        verify(personRepository).findByPersonRaceUid(20L);
    }
}
