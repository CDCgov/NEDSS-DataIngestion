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
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonEthnicRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonNameRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
import gov.cdc.dataprocessing.service.implementation.auth_user.AuthUserService;
import gov.cdc.dataprocessing.service.interfaces.entity.IEntityLocatorParticipationService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PatientRepositoryUtilTest {
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
        perDt.setFirstNm("TEST");
        perDt.setLastNm("TEST");
        perDt.setMiddleNm("TEST");
        perDt.setNmPrefix("TEST");
        perDt.setNmSuffix("TEST");
        perCon.setThePersonDto(perDt);


        var patNameCol = new ArrayList<PersonNameDto>();
        var patName = new PersonNameDto();
        patNameCol.add(patName);
        perCon.setThePersonNameDtoCollection(patNameCol);

        var nameCol = new ArrayList<PersonName>();
        var name = new PersonName();
        name.setFirstNm("TEST");
        name.setLastNm("TEST");
        name.setMiddleNm("TEST");
        name.setNmPrefix("TEST");
        name.setNmSuffix("TEST");
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


}
