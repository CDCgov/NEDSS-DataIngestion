package gov.cdc.dataprocessing.utilities.component.organization;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.PrepareEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.Organization;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizationRepositoryUtilTest {
    @Mock
    private OrganizationJdbcRepository organizationRepository;
    @Mock
    private OrganizationNameJdbcRepository organizationNameRepository;
    @Mock
    private EntityJdbcRepository entityRepository;
    @Mock
    private EntityIdJdbcRepository entityIdRepository;
    @Mock
    private EntityLocatorJdbcRepository entityLocatorParticipationRepository;
    @Mock
    private RoleJdbcRepository roleRepository;

    @Mock
    private IOdseIdGeneratorWCacheService odseIdGeneratorService;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private ParticipationJdbcRepository participationRepository;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;
    @Mock
    AuthUtil authUtil;
    @InjectMocks
    private OrganizationRepositoryUtil organizationRepositoryUtil;

    @Mock
    private UidPoolManager uidPoolManager;

    @BeforeEach
    void setUp() throws DataProcessingException {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo authUserProfileInfo=new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setNedssEntryId(1L);
        authUserProfileInfo.setAuthUser(user);
        authUtil.setGlobalAuthUser(authUserProfileInfo);

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
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(organizationRepository);
        Mockito.reset(organizationNameRepository);
        Mockito.reset(entityRepository);
        Mockito.reset(entityIdRepository);
        Mockito.reset(entityLocatorParticipationRepository);
        Mockito.reset(roleRepository);
        Mockito.reset(odseIdGeneratorService);
        Mockito.reset(entityHelper);
        Mockito.reset(participationRepository);
        Mockito.reset(prepareAssocModelHelper);
        Mockito.reset(prepareEntityStoredProcRepository);
    }

    @Test
    void findOrganizationByUid() {
        Long orgUid = 123L;
        Organization organization = new Organization();
        organization.setOrganizationUid(orgUid);
        organization.setAddReasonCode("TEST");
        when(organizationRepository.findById(orgUid)).thenReturn(organization);
        Organization organizationResult = organizationRepositoryUtil.findOrganizationByUid(orgUid);
        assertNotNull(organizationResult);
    }

    @Test
    void createOrganization() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);

        OrganizationNameDto orgNameDto = new OrganizationNameDto("UTC");
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setOrganizationNameSeq(1234);
        orgNameDto.setNmTxt("TEST_ORG_NAME");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);

        LocalUidModel localIdModel = new LocalUidModel();
        localIdModel.setGaTypeUid(new LocalUidGeneratorDto());
        localIdModel.setClassTypeUid(new LocalUidGeneratorDto());

        localIdModel.getGaTypeUid().setSeedValueNbr(1234L);
        localIdModel.getGaTypeUid().setUidPrefixCd("TEST_PX");
        localIdModel.getGaTypeUid().setUidSuffixCd("TEST_SX");

        localIdModel.getClassTypeUid().setSeedValueNbr(1234L);
        localIdModel.getClassTypeUid().setUidPrefixCd("TEST_PX");
        localIdModel.getClassTypeUid().setUidSuffixCd("TEST_SX");

        when(odseIdGeneratorService.getValidLocalUid(LocalIdClass.ORGANIZATION, true)).thenReturn(localIdModel);
        //Insert Organization
        Organization organization = new Organization(organizationDto);
//        when(organizationRepository.save(organization)).thenReturn(organization);
        //Insert OrganizationName
        OrganizationName orgName = new OrganizationName(orgNameDto);
//        when(organizationNameRepository.save(orgName)).thenReturn(orgName);

        //EntityID
        EntityIdDto entityIdDto = getEntityIdDto();
        ArrayList<EntityIdDto> entityList = new ArrayList<>();
        entityList.add(entityIdDto);
        organizationContainer.setTheEntityIdDtoCollection(entityList);

        EntityId entityId = new EntityId(entityIdDto, "UTC");
//        when(entityIdRepository.save(entityId)).thenReturn(entityId);

        //Entity Locator Participation
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoPh);
        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoPo);
        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoTel);

        LocalUidModel  localUidGenerator = getLocalUidGenerator();
        when(odseIdGeneratorService.getValidLocalUid(LocalIdClass.ORGANIZATION, true)).thenReturn(localUidGenerator);
//
//        when(entityLocatorParticipationRepository.createPhysicalLocator(new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto()))).thenReturn((new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto())));
//        when(entityLocatorParticipationRepository.createPostalLocator(new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto()))).thenReturn((new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto())));
//        when(entityLocatorParticipationRepository.createTeleLocator(new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto()))).thenReturn((new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto())));

        //Role
        RoleDto roleDto = getRoleDto();
        Collection<RoleDto> theRoleDTCollection = new ArrayList<>();
        theRoleDTCollection.add(roleDto);
        organizationContainer.setTheRoleDTCollection(theRoleDTCollection);
//        when(roleRepository.save(new Role(roleDto))).thenReturn(new Role(roleDto));

        organizationRepositoryUtil.createOrganization(organizationContainer);
        verify(roleRepository, times(1)).createRole(new Role(roleDto));
    }

    @Test
    void createOrganization_with_null_dependencies() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);

        LocalUidModel localIdModel = new LocalUidModel();
        localIdModel.setGaTypeUid(new LocalUidGeneratorDto());
        localIdModel.setClassTypeUid(new LocalUidGeneratorDto());

        localIdModel.getGaTypeUid().setSeedValueNbr(1234L);
        localIdModel.getGaTypeUid().setUidPrefixCd("TEST_PX");
        localIdModel.getGaTypeUid().setUidSuffixCd("TEST_SX");

        localIdModel.getClassTypeUid().setSeedValueNbr(1234L);
        localIdModel.getClassTypeUid().setUidPrefixCd("TEST_PX");
        localIdModel.getClassTypeUid().setUidSuffixCd("TEST_SX");

        when(odseIdGeneratorService.getValidLocalUid(LocalIdClass.ORGANIZATION, true)).thenReturn(localIdModel);
        long orgIdResult = organizationRepositoryUtil.createOrganization(organizationContainer);
        assertEquals(1, orgIdResult);
    }

    @Test
    void updateOrganization() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);

        OrganizationNameDto orgNameDto = new OrganizationNameDto("UTC");
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setNmTxt("TEST_ORG_NAME");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);

        LocalUidModel localIdModel = new LocalUidModel();
        localIdModel.setGaTypeUid(new LocalUidGeneratorDto());
        localIdModel.setClassTypeUid(new LocalUidGeneratorDto());

        localIdModel.getGaTypeUid().setSeedValueNbr(1234L);
        localIdModel.getGaTypeUid().setUidPrefixCd("TEST_PX");
        localIdModel.getGaTypeUid().setUidSuffixCd("TEST_SX");

        localIdModel.getClassTypeUid().setSeedValueNbr(1234L);
        localIdModel.getClassTypeUid().setUidPrefixCd("TEST_PX");
        localIdModel.getClassTypeUid().setUidSuffixCd("TEST_SX");

        when(odseIdGeneratorService.getValidLocalUid(LocalIdClass.ORGANIZATION, true)).thenReturn(localIdModel);
        //Insert Organization
        Organization organization = new Organization(organizationDto);
//        when(organizationRepository.save(organization)).thenReturn(organization);
        //Insert OrganizationName
        OrganizationName orgName = new OrganizationName(orgNameDto);
//        when(organizationNameRepository.save(orgName)).thenReturn(orgName);

        //EntityID
        EntityIdDto entityIdDto = getEntityIdDto();
        ArrayList<EntityIdDto> entityList = new ArrayList<>();
        entityList.add(entityIdDto);
        organizationContainer.setTheEntityIdDtoCollection(entityList);

        EntityId entityId = new EntityId(entityIdDto, "UTC");
//        when(entityIdRepository.save(entityId)).thenReturn(entityId);

        //Entity Locator Participation
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoPh);
        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoPo);
        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocatorParticipationDtoTel);

        var localUidGenerator = getLocalUidGenerator();
        when(odseIdGeneratorService.getValidLocalUid(LocalIdClass.ORGANIZATION, true)).thenReturn(localUidGenerator);

//        when(physicalLocatorRepository.save(new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto()))).thenReturn((new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto())));
//        when(postalLocatorRepository.save(new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto()))).thenReturn((new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto())));
//        when(teleLocatorRepository.save(new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto()))).thenReturn((new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto())));

        //Role
        RoleDto roleDto = getRoleDto();
        Collection<RoleDto> theRoleDTCollection = new ArrayList<>();
        theRoleDTCollection.add(roleDto);
        organizationContainer.setTheRoleDTCollection(theRoleDTCollection);
//        when(roleRepository.save(new Role(roleDto))).thenReturn(new Role(roleDto));

        organizationRepositoryUtil.updateOrganization(organizationContainer);
        verify(roleRepository, times(1)).updateRole(new Role(roleDto));
    }

    @Test
    void updateOrganization_with_null_dependencies() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);
        organizationRepositoryUtil.updateOrganization(organizationContainer);
        verify(organizationRepository, times(1)).updateOrganization(any(Organization.class));
    }

    @Test
    void updateOrganization_with_throw_exp() {
        OrganizationContainer organizationContainer = null;
        assertThrows(DataProcessingException.class, () -> organizationRepositoryUtil.updateOrganization(organizationContainer));
    }

    @Test
    void setOrganization() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);
        Long orgIdResult=organizationRepositoryUtil.setOrganization(organizationContainer, "TEST");
        assertEquals(123L,orgIdResult);
    }

    @Test
    void setOrganization_throw_exp() {
        OrganizationContainer organizationContainer = null;
        assertThrows(NullPointerException.class, () -> organizationRepositoryUtil.setOrganization(organizationContainer, "TEST"));
    }

    @Test
    void setOrganization_for_loadObj() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);
        organizationDto.setVersionCtrlNbr(1);
        organizationContainer.setItNew(false);
        organizationContainer.setItDirty(true);

        Collection<RoleDto> rDTCol = new ArrayList<>();
        Collection<ParticipationDto> pDTCol = new ArrayList<>();
        organizationContainer.setTheRoleDTCollection(rDTCol);
        organizationContainer.setTheParticipationDtoCollection(pDTCol);
        //OrganizationName
        OrganizationNameDto orgNameDto = new OrganizationNameDto("UTC");
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setOrganizationNameSeq(1234);
        orgNameDto.setNmTxt("TEST_ORG_NAME");
        orgNameDto.setNmUseCd("L");

        OrganizationNameDto orgNameDto1 = new OrganizationNameDto("UTC");
        orgNameDto1.setOrganizationUid(123L);
        orgNameDto1.setOrganizationNameSeq(1234);
        orgNameDto1.setNmTxt("TEST_ORG_NAME");
        orgNameDto1.setNmUseCd("TEST");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        theOrganizationNameDtoCollection.add(orgNameDto1);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);
        //Select Org
        Organization organization = new Organization();
        organization.setOrganizationUid(123L);
        organization.setAddReasonCode("TEST");

        when(organizationRepository.findById(123L)).thenReturn(organization);
        //Select Org
        List<OrganizationName> organizationNameList = new ArrayList<>();
        OrganizationName orgName = new OrganizationName();
        orgName.setOrganizationUid(123L);
        orgName.setOrganizationNameSeq(1234);
        orgName.setNameText("Test org name");
        organizationNameList.add(orgName);
        when(organizationNameRepository.findByOrganizationUid(123L)).thenReturn(organizationNameList);
        //select EntityId
        List<EntityId> entityIdList = new ArrayList<>();
        EntityId entityId = new EntityId();
        entityId.setEntityUid(123L);
        entityId.setAddReasonCode("Test");
        entityIdList.add(entityId);
        when(entityIdRepository.findEntityIds(123L)).thenReturn(entityIdList);
        //select EntityLocatorParticipations
        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        EntityLocatorParticipation entityLocatorParticipationPh = new EntityLocatorParticipation(entityLocatorParticipationDtoPh, "UTC");
        EntityLocatorParticipation entityLocatorParticipationPo = new EntityLocatorParticipation(entityLocatorParticipationDtoPo, "UTC");
        EntityLocatorParticipation entityLocatorParticipationTel = new EntityLocatorParticipation(entityLocatorParticipationDtoTel, "UTC");
        entityLocatorParticipations.add(entityLocatorParticipationPh);
        entityLocatorParticipations.add(entityLocatorParticipationPo);
        entityLocatorParticipations.add(entityLocatorParticipationTel);
        when(entityLocatorParticipationRepository.findByEntityUid(123L)).thenReturn(entityLocatorParticipations);
        //
        ArrayList<Participation> participationList = new ArrayList<>();
        Participation participation = new Participation();
        participation.setCode("TEST");
        participation.setSubjectEntityUid(123L);
        participationList.add(participation);

        when(participationRepository.findBySubjectUid(123L)).thenReturn(participationList);
        //select Role
        List<Role> roleList = new ArrayList<>();
        Role roleModel = new Role();
        roleModel.setRoleSeq(1234L);
        roleList.add(roleModel);
        when(roleRepository.findActiveBySubjectEntityUid(Long.valueOf(123L))).thenReturn(roleList);
        //
        OrganizationDto newOrganizationDto = new OrganizationDto();
        newOrganizationDto.setOrganizationUid(1234L);
        newOrganizationDto.setVersionCtrlNbr(2);

        when(prepareAssocModelHelper
                .prepareVO(any(),
                        any(), any(),
                        any(),
                        any(),
                        any()
                )).thenReturn(newOrganizationDto);
        //PrepareEntity
        PrepareEntity prepareEntity = new PrepareEntity();
        prepareEntity.setLocalId("123");
        prepareEntity.setRecordStatusState("TEST_STATUS_CD");
        prepareEntity.setObjectStatusState("TEST_OBJ_STATUS_CD");

        when(prepareEntityStoredProcRepository.getPrepareEntity(any(), any(), any(), any())).thenReturn(prepareEntity);

        Long orgIdResult=organizationRepositoryUtil.setOrganization(organizationContainer, "TEST");
        assertEquals(1234L,orgIdResult);
    }

    @Test
    void setOrganization_for_new_org() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        OrganizationDto organizationDto = organizationContainer.getTheOrganizationDto();
        organizationDto.setOrganizationUid(123L);
        organizationDto.setVersionCtrlNbr(1);
        organizationContainer.setItNew(true);
        organizationContainer.setItDirty(false);

        Collection<RoleDto> rDTCol = new ArrayList<>();
        Collection<ParticipationDto> pDTCol = new ArrayList<>();
        organizationContainer.setTheRoleDTCollection(rDTCol);
        organizationContainer.setTheParticipationDtoCollection(pDTCol);
        //OrganizationName
        OrganizationNameDto orgNameDto = new OrganizationNameDto("UTC");
        orgNameDto.setOrganizationUid(123L);
        orgNameDto.setOrganizationNameSeq(1234);
        orgNameDto.setNmTxt("TEST_ORG_NAME");
        orgNameDto.setNmUseCd("L");

        OrganizationNameDto orgNameDto1 = new OrganizationNameDto("UTC");
        orgNameDto1.setOrganizationUid(123L);
        orgNameDto1.setOrganizationNameSeq(1234);
        orgNameDto1.setNmTxt("TEST_ORG_NAME");
        orgNameDto1.setNmUseCd("TEST");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(orgNameDto);
        theOrganizationNameDtoCollection.add(orgNameDto1);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);
        //for localid

        LocalUidModel localIdModel = new LocalUidModel();
        localIdModel.setGaTypeUid(new LocalUidGeneratorDto());
        localIdModel.setClassTypeUid(new LocalUidGeneratorDto());

        localIdModel.getGaTypeUid().setSeedValueNbr(1234L);
        localIdModel.getGaTypeUid().setUidPrefixCd("TEST_PX");
        localIdModel.getGaTypeUid().setUidSuffixCd("TEST_SX");

        localIdModel.getClassTypeUid().setSeedValueNbr(1234L);
        localIdModel.getClassTypeUid().setUidPrefixCd("TEST_PX");
        localIdModel.getClassTypeUid().setUidSuffixCd("TEST_SX");

        when(odseIdGeneratorService.getValidLocalUid(LocalIdClass.ORGANIZATION, true)).thenReturn(localIdModel);
        //Select Org
        Organization organization = new Organization();
        organization.setOrganizationUid(123L);
        organization.setAddReasonCode("TEST");

        when(organizationRepository.findById(123L)).thenReturn(organization);
        //Select Org
        List<OrganizationName> organizationNameList = new ArrayList<>();
        OrganizationName orgName = new OrganizationName();
        orgName.setOrganizationUid(123L);
        orgName.setOrganizationNameSeq(1234);
        orgName.setNameText("Test org name");
        organizationNameList.add(orgName);
        when(organizationNameRepository.findByOrganizationUid(123L)).thenReturn(organizationNameList);
        //select EntityId
        List<EntityId> entityIdList = new ArrayList<>();
        EntityId entityId = new EntityId();
        entityId.setEntityUid(123L);
        entityId.setAddReasonCode("Test");
        entityIdList.add(entityId);
        when(entityIdRepository.findEntityIds(123L)).thenReturn(entityIdList);
        //select EntityLocatorParticipations
        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        EntityLocatorParticipation entityLocatorParticipationPh = new EntityLocatorParticipation(entityLocatorParticipationDtoPh, "UTC");
        EntityLocatorParticipation entityLocatorParticipationPo = new EntityLocatorParticipation(entityLocatorParticipationDtoPo, "UTC");
        EntityLocatorParticipation entityLocatorParticipationTel = new EntityLocatorParticipation(entityLocatorParticipationDtoTel, "UTC");
        entityLocatorParticipations.add(entityLocatorParticipationPh);
        entityLocatorParticipations.add(entityLocatorParticipationPo);
        entityLocatorParticipations.add(entityLocatorParticipationTel);
        when(entityLocatorParticipationRepository.findByEntityUid(123L)).thenReturn(entityLocatorParticipations);
        //select Role
        List<Role> roleList = new ArrayList<>();
        Role roleModel = new Role();
        roleModel.setRoleSeq(1234L);
        roleList.add(roleModel);
        when(roleRepository.findActiveBySubjectEntityUid(Long.valueOf(123L))).thenReturn(roleList);
        //
        OrganizationDto newOrganizationDto = new OrganizationDto();
        newOrganizationDto.setOrganizationUid(1234L);
        newOrganizationDto.setVersionCtrlNbr(2);

        when(prepareAssocModelHelper
                .prepareVO(any(),
                        any(), any(),
                        any(),
                        any(),
                        any()
                )).thenReturn(newOrganizationDto);
        //PrepareEntity
        PrepareEntity prepareEntity = new PrepareEntity();
        prepareEntity.setLocalId("123");
        prepareEntity.setRecordStatusState("TEST_STATUS_CD");
        prepareEntity.setObjectStatusState("TEST_OBJ_STATUS_CD");

        when(prepareEntityStoredProcRepository.getPrepareEntity(any(), any(), any(), any())).thenReturn(prepareEntity);

        Long orgIdResult=organizationRepositoryUtil.setOrganization(organizationContainer, "TEST");
        assertEquals(1,orgIdResult);
    }

    @Test
    void loadObject() throws DataProcessingException {
        //Select Org
        Organization organization = new Organization();
        organization.setOrganizationUid(123L);
        organization.setAddReasonCode("TEST");

        when(organizationRepository.findById(123L)).thenReturn(organization);
        //Select Org
        List<OrganizationName> organizationNameList = new ArrayList<>();
        OrganizationName orgName = new OrganizationName();
        orgName.setOrganizationUid(123L);
        orgName.setOrganizationNameSeq(1234);
        orgName.setNameText("Test org name");
        organizationNameList.add(orgName);
        when(organizationNameRepository.findByOrganizationUid(123L)).thenReturn(organizationNameList);
        //select EntityId
        List<EntityId> entityIdList = new ArrayList<>();
        EntityId entityId = new EntityId();
        entityId.setEntityUid(123L);
        entityId.setAddReasonCode("Test");
        entityIdList.add(entityId);
        when(entityIdRepository.findEntityIds(123L)).thenReturn(entityIdList);
        //select EntityLocatorParticipations
        List<EntityLocatorParticipation> entityLocatorParticipations = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPh = getEntityLocatorParticipationDto_ph();
        EntityLocatorParticipationDto entityLocatorParticipationDtoPo = getEntityLocatorParticipationDto_po();
        EntityLocatorParticipationDto entityLocatorParticipationDtoTel = getEntityLocatorParticipationDto_tel();

        EntityLocatorParticipation entityLocatorParticipationPh = new EntityLocatorParticipation(entityLocatorParticipationDtoPh, "UTC");
        EntityLocatorParticipation entityLocatorParticipationPo = new EntityLocatorParticipation(entityLocatorParticipationDtoPo, "UTC");
        EntityLocatorParticipation entityLocatorParticipationTel = new EntityLocatorParticipation(entityLocatorParticipationDtoTel, "UTC");
        entityLocatorParticipations.add(entityLocatorParticipationPh);
        entityLocatorParticipations.add(entityLocatorParticipationPo);
        entityLocatorParticipations.add(entityLocatorParticipationTel);

        PhysicalLocator physicalLocator = new PhysicalLocator(entityLocatorParticipationDtoPh.getThePhysicalLocatorDto());
        PostalLocator postalLocator = new PostalLocator(entityLocatorParticipationDtoPo.getThePostalLocatorDto());
        TeleLocator teleLocator = new TeleLocator(entityLocatorParticipationDtoTel.getTheTeleLocatorDto());

        List<PhysicalLocator> physicalLocatorList = new ArrayList<>();
        physicalLocatorList.add(physicalLocator);
        List<PostalLocator> postalLocatorList = new ArrayList<>();
        postalLocatorList.add(postalLocator);
        List<TeleLocator> teleLocatorList = new ArrayList<>();
        teleLocatorList.add(teleLocator);

        when(entityLocatorParticipationRepository.findByEntityUid(123L)).thenReturn(entityLocatorParticipations);

        when(entityLocatorParticipationRepository.findByPhysicalLocatorUids(any())).thenReturn(physicalLocatorList);
        when(entityLocatorParticipationRepository.findByPostalLocatorUids(any())).thenReturn(postalLocatorList);
        when(entityLocatorParticipationRepository.findByTeleLocatorUids(any())).thenReturn(teleLocatorList);

        //select Role
        List<Role> roleList = new ArrayList<>();
        Role roleModel = new Role();
        roleModel.setRoleSeq(1234L);
        roleList.add(roleModel);
        when(roleRepository.findActiveBySubjectEntityUid(Long.valueOf(123L))).thenReturn(roleList);
        //
        List<Participation> participationList = new ArrayList<>();
        Participation participation = new Participation();
        participation.setActUid(123L);
        participation.setSubjectEntityUid(123L);
        participation.setCode("TEST");
        participationList.add(participation);

        when(participationRepository.selectParticipationBySubjectAndActUid(123L, 123L)).thenReturn(participationList);

        OrganizationContainer organizationContainer = organizationRepositoryUtil.loadObject(123L, 123L);
        assertNotNull(organizationContainer);
    }

    @Test
    void prepareVO_thow_exp() {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setItNew(false);
        organizationDto.setItDirty(false);
        organizationDto.setItDelete(false);
        assertThrows(DataProcessingException.class, () -> organizationRepositoryUtil.prepareVO(organizationDto, "TEST_BTCD", "ORG", "TEST_MODULE"));
    }

    @Test
    void getPrepareEntityForOrganization() throws DataProcessingException {
        when(organizationRepositoryUtil.getPrepareEntityForOrganization("TEST", "TEST", 123L, "TEST")).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> organizationRepositoryUtil.getPrepareEntityForOrganization("TEST", "TEST", 123L, "TEST"));
    }

    private EntityIdDto getEntityIdDto() {
        EntityIdDto entityIdDto = new EntityIdDto();

        entityIdDto.setEntityUid(123L);
        entityIdDto.setEntityIdSeq(1234);
        entityIdDto.setAddReasonCd("TEST_REASON_CD");
        entityIdDto.setAddTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setAddUserId(123L);
        entityIdDto.setAssigningAuthorityCd("TEST");
        entityIdDto.setAssigningAuthorityDescTxt("TEST");
        entityIdDto.setDurationAmt("123");
        entityIdDto.setDurationUnitCd("TEST");
        entityIdDto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setLastChgReasonCd("TEST");
        entityIdDto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setLastChgUserId(123L);
        entityIdDto.setRecordStatusCd("TEST");
        entityIdDto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));

        entityIdDto.setRootExtensionTxt("TEST");
        entityIdDto.setStatusCd("TEST");
        entityIdDto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setTypeCd("TEST");
        entityIdDto.setTypeDescTxt("TEST");
        entityIdDto.setUserAffiliationTxt("TEST");
        entityIdDto.setValidFromTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setValidToTime(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        entityIdDto.setAssigningAuthorityIdType("TEST");
        return entityIdDto;
    }

    private EntityLocatorParticipationDto getEntityLocatorParticipationDto_ph() {
        //PHYSICAL
        EntityLocatorParticipationDto entityLocatorDTPh = new EntityLocatorParticipationDto();
        entityLocatorDTPh.setClassCd(NEDSSConstant.PHYSICAL);
        entityLocatorDTPh.setAddUserId(12345L);

        PhysicalLocatorDto physicalLocatorDto = new PhysicalLocatorDto();
        physicalLocatorDto.setLocatorTxt("TEST_TXT");
        physicalLocatorDto.setAddReasonCd("TEST_REASON_CD");
        physicalLocatorDto.setAddUserId(12345L);
        entityLocatorDTPh.setThePhysicalLocatorDto(physicalLocatorDto);
        return entityLocatorDTPh;
    }

    private EntityLocatorParticipationDto getEntityLocatorParticipationDto_po() {
        //POSTAL
        EntityLocatorParticipationDto entityLocatorDTPo = new EntityLocatorParticipationDto();
        entityLocatorDTPo.setClassCd(NEDSSConstant.POSTAL);
        entityLocatorDTPo.setAddUserId(12345L);

        PostalLocatorDto thePostalLocatorDto = new PostalLocatorDto();
        thePostalLocatorDto.setAddReasonCd("TEST_REASON_CD");
        thePostalLocatorDto.setAddUserId(12345L);
        entityLocatorDTPo.setThePostalLocatorDto(thePostalLocatorDto);
        return entityLocatorDTPo;
    }

    private EntityLocatorParticipationDto getEntityLocatorParticipationDto_tel() {
        //TELE
        EntityLocatorParticipationDto entityLocatorDTTel = new EntityLocatorParticipationDto();
        entityLocatorDTTel.setClassCd(NEDSSConstant.TELE);
        entityLocatorDTTel.setAddUserId(12345L);

        TeleLocatorDto teleLocatorDto = new TeleLocatorDto();
        teleLocatorDto.setAddReasonCd("TEST_REASON_CD");
        teleLocatorDto.setAddUserId(12345L);
        entityLocatorDTTel.setTheTeleLocatorDto(teleLocatorDto);
        return entityLocatorDTTel;
    }

    private LocalUidModel  getLocalUidGenerator() {
        LocalUidModel newLocalId = new LocalUidModel();
        newLocalId.setGaTypeUid(new LocalUidGeneratorDto());
        newLocalId.setClassTypeUid(new LocalUidGeneratorDto());
        newLocalId.getClassTypeUid().setUidSuffixCd("TEST_SFCD");
        newLocalId.getClassTypeUid().setUidPrefixCd("TEST_SFCD");
        newLocalId.getClassTypeUid().setTypeCd("TYPE_CD");
        newLocalId.getClassTypeUid().setClassNameCd("TEST_CL_CD");
        newLocalId.getClassTypeUid().setSeedValueNbr(123456L);

        newLocalId.getGaTypeUid().setUidSuffixCd("TEST_SFCD");
        newLocalId.getGaTypeUid().setUidPrefixCd("TEST_SFCD");
        newLocalId.getGaTypeUid().setTypeCd("TYPE_CD");
        newLocalId.getGaTypeUid().setClassNameCd("TEST_CL_CD");
        newLocalId.getGaTypeUid().setSeedValueNbr(123456L);
        return newLocalId;
    }

    private RoleDto getRoleDto() {
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleSeq(12345L);
        return roleDto;
    }
}