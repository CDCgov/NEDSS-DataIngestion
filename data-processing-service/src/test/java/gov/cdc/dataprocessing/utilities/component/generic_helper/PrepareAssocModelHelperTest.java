package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.PrepareEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.jurisdiction.ProgAreaJurisdictionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
class PrepareAssocModelHelperTest {
    @InjectMocks
    private PrepareAssocModelHelper prepareAssocModelHelper;

    @Mock
    private PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;

    @Mock
    private ProgAreaJurisdictionUtil progAreaJurisdictionUtil;

    @Mock
    private ConcurrentCheck concurrentCheck;

    @Mock
    private AuthUtil authUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @Test
    void testPrepareAssocDTForEntityLocatorParticipation() throws DataProcessingException {
        EntityLocatorParticipationDto dto = mock(EntityLocatorParticipationDto.class);
        when(dto.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(dto.getStatusCd()).thenReturn("A");
        when(dto.isItDirty()).thenReturn(true);

        EntityLocatorParticipationDto result = prepareAssocModelHelper.prepareAssocDTForEntityLocatorParticipation(dto);

        verify(dto).setRecordStatusTime(any(Timestamp.class));
        verify(dto).setStatusTime(any(Timestamp.class));
        verify(dto).setLastChgTime(any(Timestamp.class));
        assertTrue(result.isItDirty());
    }

    @Test
    void testPrepareAssocDTForActRelationship() throws DataProcessingException {
        ActRelationshipDto dto = mock(ActRelationshipDto.class);
        when(dto.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(dto.getStatusCd()).thenReturn("A");
        when(dto.isItDirty()).thenReturn(true);

        ActRelationshipDto result = prepareAssocModelHelper.prepareAssocDTForActRelationship(dto);

        verify(dto).setRecordStatusTime(any(Timestamp.class));
        verify(dto).setStatusTime(any(Timestamp.class));
        verify(dto).setLastChgTime(any(Timestamp.class));
        assertTrue(result.isItDirty());
    }

    @Test
    void testPrepareAssocDTForRole() throws DataProcessingException {
        RoleDto dto = mock(RoleDto.class);
        when(dto.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(dto.getStatusCd()).thenReturn("A");
        when(dto.isItDirty()).thenReturn(true);

        RoleDto result = prepareAssocModelHelper.prepareAssocDTForRole(dto);

        verify(dto).setRecordStatusTime(any(Timestamp.class));
        verify(dto).setStatusTime(any(Timestamp.class));
        verify(dto).setLastChgTime(any(Timestamp.class));
        assertTrue(result.isItDirty());
    }

    @Test
    void testPrepareAssocDTForParticipation() throws DataProcessingException {
        ParticipationDto dto = mock(ParticipationDto.class);
        when(dto.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(dto.getStatusCd()).thenReturn("A");
        when(dto.isItDirty()).thenReturn(true);

        ParticipationDto result = prepareAssocModelHelper.prepareAssocDTForParticipation(dto);

        verify(dto).setRecordStatusTime(any(Timestamp.class));
        verify(dto).setStatusTime(any(Timestamp.class));
        verify(dto).setLastChgTime(any(Timestamp.class));
        assertTrue(result.isItDirty());
    }

    @Test
    void testPrepareActivityLocatorParticipationDT() throws DataProcessingException {
        ActivityLocatorParticipationDto dto = mock(ActivityLocatorParticipationDto.class);
        when(dto.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(dto.getStatusCd()).thenReturn("A");
        when(dto.isItDirty()).thenReturn(true);

        ActivityLocatorParticipationDto result = prepareAssocModelHelper.prepareActivityLocatorParticipationDT(dto);

        verify(dto).setRecordStatusTime(any(Timestamp.class));
        verify(dto).setStatusTime(any(Timestamp.class));
        verify(dto).setLastChgTime(any(Timestamp.class));
        assertTrue(result.isItDirty());
    }

    @Test
    void testPrepareActRelationshipDT() throws DataProcessingException {
        ActRelationshipDto dto = mock(ActRelationshipDto.class);
        when(dto.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(dto.getStatusCd()).thenReturn("A");
        when(dto.isItDirty()).thenReturn(true);

        ActRelationshipDto result = prepareAssocModelHelper.prepareActRelationshipDT(dto);

        verify(dto).setRecordStatusTime(any(Timestamp.class));
        verify(dto).setStatusTime(any(Timestamp.class));
        verify(dto).setLastChgTime(any(Timestamp.class));
        assertTrue(result.isItDirty());
    }

    @Test
    void testPrepareVO_NewAct() throws DataProcessingException {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        when(rootDto.isItNew()).thenReturn(true);
        when(rootDto.getSuperclass()).thenReturn(NEDSSConstant.CLASSTYPE_ACT);

        PrepareEntity prepareEntity = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(anyString(), anyString(), anyLong(), anyString())).thenReturn(prepareEntity);
        when(prepareEntity.getLocalId()).thenReturn("localId");
        when(prepareEntity.getRecordStatusState()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(prepareEntity.getObjectStatusState()).thenReturn("A");

        RootDtoInterface result = prepareAssocModelHelper.prepareVO(rootDto, "businessObjLookupName", "businessTriggerCd", "tableName", "moduleCd", 1);

        verify(rootDto).setLocalId(null);
        verify(rootDto).setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        verify(rootDto).setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        verify(rootDto).setAddUserId(AuthUtil.authUser.getNedssEntryId());
        assertEquals(result, rootDto);
    }

    @Test
    void testPrepareVO_NewEntity() throws DataProcessingException {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        when(rootDto.isItNew()).thenReturn(true);
        when(rootDto.getSuperclass()).thenReturn(NEDSSConstant.CLASSTYPE_ENTITY);

        PrepareEntity prepareEntity = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(anyString(), anyString(), anyLong(), anyString())).thenReturn(prepareEntity);
        when(prepareEntity.getLocalId()).thenReturn("localId");
        when(prepareEntity.getRecordStatusState()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(prepareEntity.getObjectStatusState()).thenReturn("A");

        RootDtoInterface result = prepareAssocModelHelper.prepareVO(rootDto, "businessObjLookupName", "businessTriggerCd", "tableName", "moduleCd", 1);

        verify(rootDto).setLocalId("localId");
        verify(rootDto).setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        verify(rootDto).setStatusCd("A");
        verify(rootDto).setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        verify(rootDto).setAddUserId(AuthUtil.authUser.getNedssEntryId());
        assertEquals(result, rootDto);
    }

    @Test
    void testPrepareVO_DirtyAct() throws DataProcessingException {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        when(rootDto.isItDirty()).thenReturn(true);
        when(rootDto.getSuperclass()).thenReturn(NEDSSConstant.CLASSTYPE_ACT);

        PrepareEntity prepareEntity = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(anyString(), anyString(), anyLong(), anyString())).thenReturn(prepareEntity);
        when(prepareEntity.getLocalId()).thenReturn("localId");
        when(prepareEntity.getRecordStatusState()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(prepareEntity.getObjectStatusState()).thenReturn("A");

        when(concurrentCheck.dataConcurrenceCheck(any(), anyString(), anyInt())).thenReturn(true);

        RootDtoInterface result = prepareAssocModelHelper.prepareVO(rootDto, "businessObjLookupName", "businessTriggerCd", "tableName", "moduleCd", 1);

        verify(rootDto).setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        verify(rootDto).setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        assertEquals(result, rootDto);
    }

    @Test
    void testPrepareVO_DirtyEntity() throws DataProcessingException {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        when(rootDto.isItDirty()).thenReturn(true);
        when(rootDto.getSuperclass()).thenReturn(NEDSSConstant.CLASSTYPE_ENTITY);

        PrepareEntity prepareEntity = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(anyString(), anyString(), anyLong(), anyString())).thenReturn(prepareEntity);
        when(prepareEntity.getLocalId()).thenReturn("localId");
        when(prepareEntity.getRecordStatusState()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(prepareEntity.getObjectStatusState()).thenReturn("A");

        when(concurrentCheck.dataConcurrenceCheck(any(), anyString(), anyInt())).thenReturn(true);

        RootDtoInterface result = prepareAssocModelHelper.prepareVO(rootDto, "businessObjLookupName", "businessTriggerCd", "tableName", "moduleCd", 1);

        verify(rootDto).setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        verify(rootDto).setStatusCd("A");
        verify(rootDto).setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        assertEquals(result, rootDto);
    }

    @Test
    void testPrepareNewActVO() throws DataProcessingException {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        when(rootDto.isItNew()).thenReturn(true);
        when(rootDto.getSuperclass()).thenReturn(NEDSSConstant.CLASSTYPE_ACT);

        PrepareEntity prepareEntity = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(anyString(), anyString(), anyLong(), anyString())).thenReturn(prepareEntity);
        when(prepareEntity.getLocalId()).thenReturn("localId");
        when(prepareEntity.getRecordStatusState()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);

        RootDtoInterface result = prepareAssocModelHelper.prepareNewActVO(rootDto, "businessObjLookupName", "businessTriggerCd", "tableName", "moduleCd");

        verify(rootDto).setLocalId(null);
        verify(rootDto).setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        verify(rootDto).setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        verify(rootDto).setAddUserId(AuthUtil.authUser.getNedssEntryId());
        assertEquals(result, rootDto);
    }

    @Test
    void testPrepareNewEntityVO() throws DataProcessingException {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        when(rootDto.isItNew()).thenReturn(true);
        when(rootDto.getSuperclass()).thenReturn(NEDSSConstant.CLASSTYPE_ENTITY);

        PrepareEntity prepareEntity = mock(PrepareEntity.class);
        when(prepareEntity.getRecordStatusState()).thenReturn("TEST");
        when(prepareEntity.getObjectStatusState()).thenReturn("TEST");

        when(prepareEntityStoredProcRepository.getPrepareEntity(anyString(), anyString(), anyLong(), anyString())).thenReturn(prepareEntity);
        when(prepareEntity.getLocalId()).thenReturn("localId");
        when(prepareEntity.getRecordStatusState()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);

        RootDtoInterface result = prepareAssocModelHelper.prepareNewEntityVO(rootDto, "businessObjLookupName", "businessTriggerCd", "tableName", "moduleCd");

        verify(rootDto).setLocalId("localId");
        verify(rootDto).setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        verify(rootDto).setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        verify(rootDto).setAddUserId(AuthUtil.authUser.getNedssEntryId());
        assertEquals(result, rootDto);
    }

    @Test
    void testPrepareDirtyActVO() throws DataProcessingException {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        when(rootDto.isItDirty()).thenReturn(true);
        when(rootDto.getSuperclass()).thenReturn(NEDSSConstant.CLASSTYPE_ACT);

        PrepareEntity prepareEntity = mock(PrepareEntity.class);
        when(prepareEntity.getRecordStatusState()).thenReturn("TEST");
        when(prepareEntity.getObjectStatusState()).thenReturn("TEST");

        when(prepareEntityStoredProcRepository.getPrepareEntity(anyString(), anyString(), anyLong(), anyString())).thenReturn(prepareEntity);
        when(prepareEntity.getLocalId()).thenReturn("localId");
        when(prepareEntity.getRecordStatusState()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);

        RootDtoInterface result = prepareAssocModelHelper.prepareDirtyActVO(rootDto, "businessObjLookupName", "businessTriggerCd", "tableName", "moduleCd");

        verify(rootDto).setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        verify(rootDto).setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        assertEquals(result, rootDto);
    }

    @Test
    void testPrepareDirtyEntityVO() throws DataProcessingException {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        when(rootDto.isItDirty()).thenReturn(true);
        when(rootDto.getSuperclass()).thenReturn(NEDSSConstant.CLASSTYPE_ENTITY);

        PrepareEntity prepareEntity = mock(PrepareEntity.class);
        when(prepareEntity.getRecordStatusState()).thenReturn("TEST");
        when(prepareEntity.getObjectStatusState()).thenReturn("TEST");

        when(prepareEntityStoredProcRepository.getPrepareEntity(anyString(), anyString(), anyLong(), anyString())).thenReturn(prepareEntity);
        when(prepareEntity.getLocalId()).thenReturn("localId");
        when(prepareEntity.getRecordStatusState()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);

        RootDtoInterface result = prepareAssocModelHelper.prepareDirtyEntityVO(rootDto, "businessObjLookupName", "businessTriggerCd", "tableName", "moduleCd");

        verify(rootDto).setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        verify(rootDto).setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
        assertEquals(result, rootDto);
    }


}
