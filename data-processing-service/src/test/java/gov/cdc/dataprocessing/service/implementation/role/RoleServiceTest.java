package gov.cdc.dataprocessing.service.implementation.role;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.role.RoleRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RoleServiceTest {
    @Mock
    private RoleRepository roleRepositoryMock;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelperMock;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(roleRepositoryMock);
        Mockito.reset(prepareAssocModelHelperMock);
    }

    @Test
    void findRoleScopedToPatient() {
        Role role = new Role();
        role.setRoleSeq(1L);
        role.setAddReasonCode("TEST_REASON_CODE");
        role.setAddTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        role.setAddUserId(10055282L);
        role.setCode("SF");
        role.setCodeDescription("TEST_DESCRIPTION");
        role.setEffectiveDurationAmount("TEST_DURATION_AMOUNT");
        role.setEffectiveDurationUnitCode("TEST_UNIT_CODE");
        role.setEffectiveFromTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        role.setEffectiveToTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        role.setLastChangeReasonCode("TEST_REASON_CODE");
        role.setLastChangeTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        role.setLastChangeUserId(10055282L);
        role.setRecordStatusCode("TEST_STATUS_CODE");
        role.setRecordStatusTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        role.setScopingRoleCode("TEST_SCOPING_ROLE_CODE");
        role.setStatusCode("TEST_STATUS_CODE");
        role.setStatusTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        role.setUserAffiliationText("TEST_USER_AFFINITY_TEXT");
        role.setScopingEntityUid(123L);
        role.setScopingRoleSeq(234);
        role.setSubjectEntityUid(1234L);
        role.setScopingClassCode("TEST_SCOPING_CLASS_CODE");
        role.setSubjectClassCode("TEST_SUBJECT_CLASS_CODE");

        Collection<Role> rolesList = new ArrayList<>();
        rolesList.add(role);
        Optional<Collection<Role>> roles = Optional.of(rolesList);

        when(roleRepositoryMock.findRoleScopedToPatient(123L)).thenReturn(roles);

        Collection<RoleDto> rolesResult = roleService.findRoleScopedToPatient(123L);
        assertEquals(1, rolesResult.size());
    }

    @Test
    void storeRoleDTCollection() throws DataProcessingException {
        RoleDto roleDto = getRoleDto();
        Collection<RoleDto> roleDTColl = new ArrayList<>();
        roleDTColl.add(roleDto);

        when(prepareAssocModelHelperMock.prepareAssocDTForRole(roleDto)).thenReturn(roleDto);
        roleService.storeRoleDTCollection(roleDTColl);
        verify(prepareAssocModelHelperMock).prepareAssocDTForRole(roleDto);
    }

    @Test
    void storeRoleDTCollection_throw_exp() throws DataProcessingException {
        RoleDto roleDto = getRoleDto();
        roleDto.setRecordStatusCd(null);

        Collection<RoleDto> roleDTColl = new ArrayList<>();
        roleDTColl.add(roleDto);

        when(prepareAssocModelHelperMock.prepareAssocDTForRole(roleDto)).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> roleService.storeRoleDTCollection(roleDTColl));
    }

    @Test
    void saveRole() {
        RoleDto roleDto = getRoleDto();
        roleDto.setItNew(true);
        var data = new Role(roleDto);
        when(roleRepositoryMock.save(data)).thenReturn(data);
        roleService.saveRole(roleDto);
        verify(roleRepositoryMock).save(data);
    }
    @Test
    void saveRole_forDelete() {
        RoleDto roleDto = getRoleDto();
        roleDto.setItDelete(true);
        doNothing().when(roleRepositoryMock).deleteRoleByPk(1234L,"SF",1L);
        roleService.saveRole(roleDto);
        verify(roleRepositoryMock).deleteRoleByPk(1234L,"SF",1L);
    }

    @Test
    void loadCountBySubjectCdComb() {
        RoleDto roleDto = new RoleDto();
        roleDto.setSubjectEntityUid(123L);
        roleDto.setCd("TEST");
        when(roleRepositoryMock.loadCountBySubjectCdComb(123L, "TEST")).thenReturn(Optional.of(2));
        Integer countResult = roleService.loadCountBySubjectCdComb(roleDto);
        assertEquals(2, countResult);
    }

    @Test
    void loadCountBySubjectScpingCdComb() {
        RoleDto roleDto = new RoleDto();
        roleDto.setSubjectEntityUid(123L);
        roleDto.setCd("TEST");
        roleDto.setScopingEntityUid(234L);
        when(roleRepositoryMock.loadCountBySubjectScpingCdComb(123L, "TEST",234L)).thenReturn(Optional.of(1));
        Integer countResult= roleService.loadCountBySubjectScpingCdComb(roleDto);
        assertEquals(1, countResult);
    }

    private RoleDto getRoleDto() {
        RoleDto roleDto = new RoleDto();
        roleDto.setRoleSeq(1L);
        roleDto.setAddReasonCd("TEST_REASON_CODE");
        roleDto.setAddTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        roleDto.setAddUserId(10055282L);
        roleDto.setCd("SF");
        roleDto.setCdDescTxt("TEST_DESCRIPTION");
        roleDto.setEffectiveDurationAmt("TEST_DURATION_AMOUNT");
        roleDto.setEffectiveDurationUnitCd("TEST_UNIT_CODE");
        roleDto.setEffectiveFromTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        roleDto.setEffectiveToTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        roleDto.setLastChgReasonCd("TEST_REASON_CODE");
        roleDto.setLastChgTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        roleDto.setLastChgUserId(10055282L);
        roleDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        roleDto.setRecordStatusTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        roleDto.setScopingRoleCd("TEST_SCOPING_ROLE_CODE");
        roleDto.setStatusCd("TEST_STATUS_CODE");
        roleDto.setStatusTime(new Timestamp(2024, 05, 27, 10, 12, 53, 693));
        roleDto.setUserAffiliationTxt("TEST_USER_AFFINITY_TEXT");
        roleDto.setScopingEntityUid(123L);
        roleDto.setScopingRoleSeq(234);
        roleDto.setSubjectEntityUid(1234L);
        roleDto.setScopingClassCd("TEST_SCOPING_CLASS_CODE");
        roleDto.setSubjectClassCd("TEST_SUBJECT_CLASS_CODE");
        return roleDto;
    }
}