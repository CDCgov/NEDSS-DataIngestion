package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
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
import org.mockito.Spy;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class PrepareAssocModelHelperTest {
    @InjectMocks
    @Spy
    private PrepareAssocModelHelper prepareAssocModelHelper;

    @Mock
    private PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;

    @Mock
    private ProgAreaJurisdictionUtil progAreaJurisdictionUtil;

    @Mock
    private ConcurrentCheck concurrentCheck;
    @Mock
    private ActivityLocatorParticipationDto activityLocatorParticipationInterface;


    @Mock
    private AuthUtil authUtil;

    @Mock
    private ActRelationshipDto assocDTInterface;
    @Mock
    private ParticipationDto participationInterface;

    @Mock
    private ActRelationshipDto actInterface;
    @Mock
    private RoleDto roleInterface;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        user.setNedssEntryId(1L);
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


    @Test
    void testPrepareAssocDTForActivityLocatorParticipation_RecordStatusCdNull() {
        // Arrange
        ActivityLocatorParticipationDto assocDTInterface = new ActivityLocatorParticipationDto();
        assocDTInterface.setRecordStatusCd(null);

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForActivityLocatorParticipation(assocDTInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd -----2----null   statusCode--------null"));
    }


    @Test
    void testPrepareAssocDTForActivityLocatorParticipation_RecordStatusCdNotActiveOrInactive() {
        // Arrange
        ActivityLocatorParticipationDto assocDTInterface = new ActivityLocatorParticipationDto();
        assocDTInterface.setRecordStatusCd("UNKNOWN_STATUS");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForActivityLocatorParticipation(assocDTInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd is not active or inactive"));
    }


    @Test
    void testPrepareAssocDTForActivityLocatorParticipation_ValidRecordStatusCd() throws DataProcessingException {
        // Arrange
        ActivityLocatorParticipationDto assocDTInterface = new ActivityLocatorParticipationDto();
        assocDTInterface.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        assocDTInterface.setStatusCd("STATUS");
        assocDTInterface.setItDirty(true);

        // Act
        ActivityLocatorParticipationDto result = prepareAssocModelHelper.prepareAssocDTForActivityLocatorParticipation(assocDTInterface);

        // Assert
        assertNotNull(result);
        assertNull(result.getAddUserId());
        assertNull(result.getAddTime());
        assertNotNull(result.getRecordStatusTime());
        assertNotNull(result.getStatusTime());
        assertNotNull(result.getLastChgTime());
        assertEquals(AuthUtil.authUser.getNedssEntryId(), result.getLastChgUserId());
        assertNull(result.getLastChgReasonCd());
    }


    @Test
    void testPrepareAssocDTForActivityLocatorParticipation_NotDirty() throws DataProcessingException {
        // Arrange
        ActivityLocatorParticipationDto assocDTInterface = new ActivityLocatorParticipationDto();
        assocDTInterface.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        assocDTInterface.setStatusCd("STATUS");
        assocDTInterface.setItDirty(false);

        // Act
        ActivityLocatorParticipationDto result = prepareAssocModelHelper.prepareAssocDTForActivityLocatorParticipation(assocDTInterface);

        // Assert
        assertNotNull(result);
        assertFalse(result.isItDirty());
        assertEquals(AuthUtil.authUser.getNedssEntryId(), result.getLastChgUserId());
    }


    @Test
    void testPrepareDirtyEntityVO_ExceptionHandling() {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        when(theRootDTInterface.getUid()).thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareDirtyEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertTrue(exception.getMessage().contains("Test Exception"));
    }

    @Test
    void testPrepareDirtyEntityVO_PatientNoMerge() throws DataProcessingException {
        // Arrange
        PersonDto theRootDTInterface = new PersonDto();
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "PAT_NO_MERGE";
        String tableName = NEDSSConstant.PATIENT;
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareDirtyEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof PersonDto);
        assertNull(((PersonDto) result).getGroupNbr());
        assertNull(((PersonDto) result).getGroupTime());
        verify(prepareEntityStoredProcRepository, times(1)).getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName);
    }

    @Test
    void testPrepareDirtyEntityVO_PatientNotNoMerge() throws DataProcessingException {
        // Arrange
        PersonDto theRootDTInterface = new PersonDto();
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "NOT_PAT_NO_MERGE";
        String tableName = NEDSSConstant.PATIENT;
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareDirtyEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof PersonDto);
        assertNull(((PersonDto) result).getDedupMatchInd());
        assertNull(((PersonDto) result).getGroupNbr());
        assertNull(((PersonDto) result).getGroupTime());
        verify(prepareEntityStoredProcRepository, times(1)).getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName);
    }


    @Test
    void testPrepareDirtyActVO_ExceptionHandling() {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        when(theRootDTInterface.getUid()).thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareDirtyActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertTrue(exception.getMessage().contains("Test Exception"));
    }

    @Test
    void testPrepareDirtyActVO_RecordStatusStateNull() throws DataProcessingException {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn(null);

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareDirtyActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertTrue(exception.getMessage().contains("NEDSSConcurrentDataException"));
    }

    @Test
    void testPrepareDirtyActVO_ValidProgAreaAndJurisdiction() throws DataProcessingException {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        when(theRootDTInterface.getProgAreaCd()).thenReturn("progAreaCd");
        when(theRootDTInterface.getJurisdictionCd()).thenReturn("jurisdictionCd");

        long pajHash = 123456L;
        when(progAreaJurisdictionUtil.getPAJHash("progAreaCd", "jurisdictionCd")).thenReturn(pajHash);

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareDirtyActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        verify(theRootDTInterface, times(1)).setProgramJurisdictionOid(pajHash);
        verify(theRootDTInterface, times(1)).setAddUserId(1L);
        verify(theRootDTInterface, times(1)).setAddTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setRecordStatusCd("recordStatusState");
        verify(theRootDTInterface, times(1)).setRecordStatusTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setLastChgTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setLastChgUserId(1L);
        verify(theRootDTInterface, times(1)).setLastChgReasonCd(null);
    }

    @Test
    void testPrepareDirtyActVO_ValidWithoutProgAreaAndJurisdiction() throws DataProcessingException {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        when(theRootDTInterface.getProgAreaCd()).thenReturn(null);
        when(theRootDTInterface.getJurisdictionCd()).thenReturn(null);

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareDirtyActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        verify(theRootDTInterface, times(0)).setProgramJurisdictionOid(anyLong());
        verify(theRootDTInterface, times(1)).setAddUserId(1L);
        verify(theRootDTInterface, times(1)).setAddTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setRecordStatusCd("recordStatusState");
        verify(theRootDTInterface, times(1)).setRecordStatusTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setLastChgTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setLastChgUserId(1L);
        verify(theRootDTInterface, times(1)).setLastChgReasonCd(null);
    }

    @Test
    void testPrepareNewEntityVO_ExceptionHandling_1() {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        when(theRootDTInterface.getUid()).thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertTrue(exception.getMessage().contains("Test Exception"));
    }

    @Test
    void testPrepareNewEntityVO_RecordStatusStateNull_1() throws DataProcessingException {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn(null);

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertTrue(exception.getMessage().contains("NEDSSConcurrentDataException"));
    }

    @Test
    void testPrepareNewEntityVO_PatientNoMerge_1() throws DataProcessingException {
        // Arrange
        PersonDto theRootDTInterface = new PersonDto();
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "PAT_NO_MERGE";
        String tableName = NEDSSConstant.PATIENT;
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof PersonDto);
        assertNull(((PersonDto) result).getGroupNbr());
        assertNull(((PersonDto) result).getGroupTime());
        verify(prepareEntityStoredProcRepository, times(1)).getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName);
    }

    @Test
    void testPrepareNewEntityVO_PatientNotNoMerge() throws DataProcessingException {
        // Arrange
        PersonDto theRootDTInterface = new PersonDto();
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "NOT_PAT_NO_MERGE";
        String tableName = NEDSSConstant.PATIENT;
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof PersonDto);
        assertNull(((PersonDto) result).getDedupMatchInd());
        assertNull(((PersonDto) result).getGroupNbr());
        assertNull(((PersonDto) result).getGroupTime());
        verify(prepareEntityStoredProcRepository, times(1)).getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName);
    }

    @Test
    void testPrepareNewActVO_ExceptionHandling() {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        when(theRootDTInterface.getUid()).thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareNewActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertTrue(exception.getMessage().contains("Test Exception"));
    }

    @Test
    void testPrepareNewActVO_RecordStatusStateNull() throws DataProcessingException {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn(null);
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareNewActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertNotNull(exception);

    }

    @Test
    void testPrepareNewActVO_ValidProgAreaAndJurisdiction() throws DataProcessingException {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        when(theRootDTInterface.getProgAreaCd()).thenReturn("progAreaCd");
        when(theRootDTInterface.getJurisdictionCd()).thenReturn("jurisdictionCd");

        long pajHash = 123456L;
        when(progAreaJurisdictionUtil.getPAJHash("progAreaCd", "jurisdictionCd")).thenReturn(pajHash);

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareNewActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        verify(theRootDTInterface, times(1)).setProgramJurisdictionOid(pajHash);
        verify(theRootDTInterface, times(1)).setLocalId(null);
        verify(theRootDTInterface, times(1)).setRecordStatusCd("recordStatusState");
        verify(theRootDTInterface, times(1)).setRecordStatusTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setLastChgTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setAddTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setLastChgUserId(1L);
        verify(theRootDTInterface, times(1)).setAddUserId(1L);
        verify(theRootDTInterface, times(1)).setLastChgReasonCd(null);
    }

    @Test
    void testPrepareNewActVO_ValidWithoutProgAreaAndJurisdiction() throws DataProcessingException {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        when(theRootDTInterface.getProgAreaCd()).thenReturn(null);
        when(theRootDTInterface.getJurisdictionCd()).thenReturn(null);

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareNewActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        verify(theRootDTInterface, times(0)).setProgramJurisdictionOid(anyLong());
        verify(theRootDTInterface, times(1)).setLocalId(null);
        verify(theRootDTInterface, times(1)).setRecordStatusCd("recordStatusState");
        verify(theRootDTInterface, times(1)).setRecordStatusTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setLastChgTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setAddTime(any(Timestamp.class));
        verify(theRootDTInterface, times(1)).setLastChgUserId(1L);
        verify(theRootDTInterface, times(1)).setAddUserId(1L);
        verify(theRootDTInterface, times(1)).setLastChgReasonCd(null);
    }

    @Test
    void testPrepareNewEntityVO_ExceptionHandling() {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        when(theRootDTInterface.getUid()).thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertTrue(exception.getMessage().contains("Test Exception"));
    }

    @Test
    void testPrepareNewEntityVO_RecordStatusStateNull() throws DataProcessingException {
        // Arrange
        RootDtoInterface theRootDTInterface = mock(RootDtoInterface.class);
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "businessTriggerCd";
        String tableName = "tableName";
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn(null);
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
        });

        assertTrue(exception.getMessage().contains("NEDSSConcurrentDataException: The data has been modified by other user, please verify!"));
    }

    @Test
    void testPrepareNewEntityVO_PatientNoMerge() throws DataProcessingException {
        // Arrange
        PersonDto theRootDTInterface = new PersonDto();
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "PAT_NO_MERGE";
        String tableName = NEDSSConstant.PATIENT;
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof PersonDto);
        assertNull(((PersonDto) result).getGroupNbr());
        assertNull(((PersonDto) result).getGroupTime());
        verify(prepareEntityStoredProcRepository, times(1)).getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName);
    }

    @Test
    void testPrepareNewEntityVO_PatientNotNoMerge_1() throws DataProcessingException {
        // Arrange
        PersonDto theRootDTInterface = new PersonDto();
        String businessObjLookupName = "businessObjLookupName";
        String businessTriggerCd = "NOT_PAT_NO_MERGE";
        String tableName = NEDSSConstant.PATIENT;
        String moduleCd = "moduleCd";

        PrepareEntity prepareVOUtilsHelper = mock(PrepareEntity.class);
        when(prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName))
                .thenReturn(prepareVOUtilsHelper);
        when(prepareVOUtilsHelper.getLocalId()).thenReturn("localId");
        when(prepareVOUtilsHelper.getAddUserId()).thenReturn(1L);
        when(prepareVOUtilsHelper.getAddUserTime()).thenReturn(new Timestamp(new Date().getTime()));
        when(prepareVOUtilsHelper.getRecordStatusState()).thenReturn("recordStatusState");
        when(prepareVOUtilsHelper.getObjectStatusState()).thenReturn("objectStatusState");

        // Act
        RootDtoInterface result = prepareAssocModelHelper.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof PersonDto);
        assertNull(((PersonDto) result).getDedupMatchInd());
        assertNull(((PersonDto) result).getGroupNbr());
        assertNull(((PersonDto) result).getGroupTime());
        verify(prepareEntityStoredProcRepository, times(1)).getPrepareEntity(businessTriggerCd, moduleCd, theRootDTInterface.getUid(), tableName);
    }


    @Test
    void testPrepareAssocDTForEntityLocatorParticipation_RecordStatusCdNull() {
        // Arrange
        EntityLocatorParticipationDto assocDTInterface = mock(EntityLocatorParticipationDto.class);
        when(assocDTInterface.getRecordStatusCd()).thenReturn(null);
        when(assocDTInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForEntityLocatorParticipation(assocDTInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd -----2----null   statusCode--------ACTIVE"));
    }

    @Test
    void testPrepareAssocDTForEntityLocatorParticipation_RecordStatusCdNotActiveOrInactive() {
        // Arrange
        EntityLocatorParticipationDto assocDTInterface = mock(EntityLocatorParticipationDto.class);
        when(assocDTInterface.getRecordStatusCd()).thenReturn("INVALID_STATUS");
        when(assocDTInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForEntityLocatorParticipation(assocDTInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd is not active or inactive"));
    }

    @Test
    void testPrepareAssocDTForEntityLocatorParticipation_UnhandledException() {
        // Arrange
        EntityLocatorParticipationDto assocDTInterface = mock(EntityLocatorParticipationDto.class);
        when(assocDTInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(assocDTInterface.getStatusCd()).thenReturn("ACTIVE");
        doThrow(new RuntimeException("Test Exception")).when(assocDTInterface).setAddUserId(null);

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForEntityLocatorParticipation(assocDTInterface);
        });

        assertTrue(exception.getMessage().contains("Test Exception"));
    }

    @Test
    void testPrepareAssocDTForActRelationship_RecordStatusCdNull() {
        // Arrange
        when(assocDTInterface.getRecordStatusCd()).thenReturn(null);
        when(assocDTInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForActRelationship(assocDTInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd -----2----null   statusCode--------ACTIVE"));
    }

    @Test
    void testPrepareAssocDTForActRelationship_RecordStatusCdNotActiveOrInactive() {
        // Arrange
        when(assocDTInterface.getRecordStatusCd()).thenReturn("INVALID_STATUS");
        when(assocDTInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForActRelationship(assocDTInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd is not active or inactive"));
    }



    @Test
    void testPrepareAssocDTForActRelationship_InnerTryBlock() throws DataProcessingException {
        // Arrange
        when(assocDTInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(assocDTInterface.getStatusCd()).thenReturn("ACTIVE");
        when(assocDTInterface.isItDirty()).thenReturn(true);

        // Act
        ActRelationshipDto result = prepareAssocModelHelper.prepareAssocDTForActRelationship(assocDTInterface);

        // Assert
        assertNotNull(result);
        verify(assocDTInterface).setAddUserId(null);
        verify(assocDTInterface).setAddTime(null);
        verify(assocDTInterface).setRecordStatusTime(any(Timestamp.class));
        verify(assocDTInterface).setStatusTime(any(Timestamp.class));
        verify(assocDTInterface).setLastChgTime(any(Timestamp.class));
        verify(assocDTInterface).setLastChgUserId(anyLong());
        verify(assocDTInterface).setLastChgReasonCd(null);
        verify(assocDTInterface, never()).setItDirty(false); // Since isRealDirty is true
    }


    @Test
    void testPrepareAssocDTForRole_RecordStatusCdNull() {
        // Arrange
        when(roleInterface.getRecordStatusCd()).thenReturn(null);
        when(roleInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForRole(roleInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd -----2----null   statusCode--------ACTIVE"));
    }

    @Test
    void testPrepareAssocDTForRole_RecordStatusCdNotActiveOrInactive() {
        // Arrange
        when(roleInterface.getRecordStatusCd()).thenReturn("INVALID_STATUS");
        when(roleInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForRole(roleInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd is not active or inactive"));
    }
    

    @Test
    void testPrepareAssocDTForRole_InnerTryBlock() throws DataProcessingException {
        // Arrange
        when(roleInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(roleInterface.getStatusCd()).thenReturn("ACTIVE");
        when(roleInterface.isItDirty()).thenReturn(true);

        // Act
        RoleDto result = prepareAssocModelHelper.prepareAssocDTForRole(roleInterface);

        // Assert
        assertNotNull(result);
        verify(roleInterface).setAddUserId(null);
        verify(roleInterface).setAddTime(null);
        verify(roleInterface).setRecordStatusTime(any(Timestamp.class));
        verify(roleInterface).setStatusTime(any(Timestamp.class));
        verify(roleInterface).setLastChgTime(any(Timestamp.class));
        verify(roleInterface).setLastChgReasonCd(null);
        verify(roleInterface, never()).setItDirty(false); // Since isRealDirty is true
    }

    @Test
    void testPrepareAssocDTForRole_NotDirty() throws DataProcessingException {
        // Arrange
        when(roleInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(roleInterface.getStatusCd()).thenReturn("ACTIVE");
        when(roleInterface.isItDirty()).thenReturn(false);

        // Act
        RoleDto result = prepareAssocModelHelper.prepareAssocDTForRole(roleInterface);

        // Assert
        assertNotNull(result);
        verify(roleInterface).setAddUserId(null);
        verify(roleInterface).setAddTime(null);
        verify(roleInterface).setRecordStatusTime(any(Timestamp.class));
        verify(roleInterface).setStatusTime(any(Timestamp.class));
        verify(roleInterface).setLastChgTime(any(Timestamp.class));
        verify(roleInterface).setLastChgReasonCd(null);
        verify(roleInterface).setItDirty(false); // Since isRealDirty is false
    }

    @Test
    void testPrepareAssocDTForParticipation_RecordStatusCdNull() {
        // Arrange
        when(participationInterface.getRecordStatusCd()).thenReturn(null);
        when(participationInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForParticipation(participationInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd -----2----null   statusCode--------ACTIVE"));
    }

    @Test
    void testPrepareAssocDTForParticipation_RecordStatusCdNotActiveOrInactive() {
        // Arrange
        when(participationInterface.getRecordStatusCd()).thenReturn("INVALID_STATUS");
        when(participationInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareAssocDTForParticipation(participationInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd is not active or inactive"));
    }

    @Test
    void testPrepareAssocDTForParticipation_InnerTryBlock() throws DataProcessingException {
        // Arrange
        when(participationInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(participationInterface.getStatusCd()).thenReturn("ACTIVE");
        when(participationInterface.isItDirty()).thenReturn(true);

        // Act
        ParticipationDto result = prepareAssocModelHelper.prepareAssocDTForParticipation(participationInterface);

        // Assert
        assertNotNull(result);
        verify(participationInterface).setAddUserId(null);
        verify(participationInterface).setAddTime(null);
        verify(participationInterface).setRecordStatusTime(any(Timestamp.class));
        verify(participationInterface).setStatusTime(any(Timestamp.class));
        verify(participationInterface).setLastChgTime(any(Timestamp.class));
        verify(participationInterface).setLastChgReasonCd(null);
        verify(participationInterface, never()).setItDirty(false); // Since isRealDirty is true
    }

    @Test
    void testPrepareAssocDTForParticipation_NotDirty() throws DataProcessingException {
        // Arrange
        when(participationInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(participationInterface.getStatusCd()).thenReturn("ACTIVE");
        when(participationInterface.isItDirty()).thenReturn(false);

        // Act
        ParticipationDto result = prepareAssocModelHelper.prepareAssocDTForParticipation(participationInterface);

        // Assert
        assertNotNull(result);
        verify(participationInterface).setAddUserId(null);
        verify(participationInterface).setAddTime(null);
        verify(participationInterface).setRecordStatusTime(any(Timestamp.class));
        verify(participationInterface).setStatusTime(any(Timestamp.class));
        verify(participationInterface).setLastChgTime(any(Timestamp.class));
        verify(participationInterface).setLastChgReasonCd(null);
        verify(participationInterface).setItDirty(false); // Since isRealDirty is false
    }

    @Test
    void testPrepareActRelationshipDT_RecordStatusCdNull() {
        // Arrange
        when(actInterface.getRecordStatusCd()).thenReturn(null);
        when(actInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareActRelationshipDT(actInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd -----2----null   statusCode--------ACTIVE"));
    }

    @Test
    void testPrepareActRelationshipDT_RecordStatusCdNotActiveOrInactive() {
        // Arrange
        when(actInterface.getRecordStatusCd()).thenReturn("INVALID_STATUS");
        when(actInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareActRelationshipDT(actInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd is not active or inactive"));
    }

    @Test
    void testPrepareActRelationshipDT_UnhandledException() {
        // Arrange
        when(actInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(actInterface.getStatusCd()).thenReturn("ACTIVE");
        doThrow(new RuntimeException("Test Exception")).when(actInterface).setAddUserId(null);

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareActRelationshipDT(actInterface);
        });

        assertTrue(exception.getMessage().contains("Test Exception"));
    }

    @Test
    void testPrepareActRelationshipDT_InnerTryBlock() throws DataProcessingException {
        // Arrange
        when(actInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(actInterface.getStatusCd()).thenReturn("ACTIVE");
        when(actInterface.isItDirty()).thenReturn(true);

        // Act
        ActRelationshipDto result = prepareAssocModelHelper.prepareActRelationshipDT(actInterface);

        // Assert
        assertNotNull(result);
        verify(actInterface).setAddUserId(null);
        verify(actInterface).setAddTime(null);
        verify(actInterface).setRecordStatusTime(any(Timestamp.class));
        verify(actInterface).setStatusTime(any(Timestamp.class));
        verify(actInterface).setLastChgTime(any(Timestamp.class));
        verify(actInterface).setLastChgUserId(anyLong());
        verify(actInterface).setLastChgReasonCd(null);
        verify(actInterface, never()).setItDirty(false); // Since isRealDirty is true
    }

    @Test
    void testPrepareActRelationshipDT_NotDirty() throws DataProcessingException {
        // Arrange
        when(actInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(actInterface.getStatusCd()).thenReturn("ACTIVE");
        when(actInterface.isItDirty()).thenReturn(false);

        // Act
        ActRelationshipDto result = prepareAssocModelHelper.prepareActRelationshipDT(actInterface);

        // Assert
        assertNotNull(result);
        verify(actInterface).setAddUserId(null);
        verify(actInterface).setAddTime(null);
        verify(actInterface).setRecordStatusTime(any(Timestamp.class));
        verify(actInterface).setStatusTime(any(Timestamp.class));
        verify(actInterface).setLastChgTime(any(Timestamp.class));
        verify(actInterface).setLastChgUserId(anyLong());
        verify(actInterface).setLastChgReasonCd(null);
        verify(actInterface).setItDirty(false); // Since isRealDirty is false
    }

    @Test
    void testPrepareActivityLocatorParticipationDT_RecordStatusCdNull() {
        // Arrange
        when(activityLocatorParticipationInterface.getRecordStatusCd()).thenReturn(null);
        when(activityLocatorParticipationInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareActivityLocatorParticipationDT(activityLocatorParticipationInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd -----2----null   statusCode--------ACTIVE"));
    }

    @Test
    void testPrepareActivityLocatorParticipationDT_RecordStatusCdNotActiveOrInactive() {
        // Arrange
        when(activityLocatorParticipationInterface.getRecordStatusCd()).thenReturn("INVALID_STATUS");
        when(activityLocatorParticipationInterface.getStatusCd()).thenReturn("ACTIVE");

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareActivityLocatorParticipationDT(activityLocatorParticipationInterface);
        });

        assertTrue(exception.getMessage().contains("RecordStatusCd is not active or inactive"));
    }

    @Test
    void testPrepareActivityLocatorParticipationDT_UnhandledException() {
        // Arrange
        when(activityLocatorParticipationInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(activityLocatorParticipationInterface.getStatusCd()).thenReturn("ACTIVE");
        doThrow(new RuntimeException("Test Exception")).when(activityLocatorParticipationInterface).setAddUserId(null);

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            prepareAssocModelHelper.prepareActivityLocatorParticipationDT(activityLocatorParticipationInterface);
        });

        assertTrue(exception.getMessage().contains("Test Exception"));
    }

    @Test
    void testPrepareActivityLocatorParticipationDT_InnerTryBlock() throws DataProcessingException {
        // Arrange
        when(activityLocatorParticipationInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(activityLocatorParticipationInterface.getStatusCd()).thenReturn("ACTIVE");
        when(activityLocatorParticipationInterface.isItDirty()).thenReturn(true);

        // Act
        ActivityLocatorParticipationDto result = prepareAssocModelHelper.prepareActivityLocatorParticipationDT(activityLocatorParticipationInterface);

        // Assert
        assertNotNull(result);
        verify(activityLocatorParticipationInterface).setAddUserId(null);
        verify(activityLocatorParticipationInterface).setAddTime(null);
        verify(activityLocatorParticipationInterface).setRecordStatusTime(any(Timestamp.class));
        verify(activityLocatorParticipationInterface).setStatusTime(any(Timestamp.class));
        verify(activityLocatorParticipationInterface).setLastChgTime(any(Timestamp.class));
        verify(activityLocatorParticipationInterface).setLastChgUserId(anyLong());
        verify(activityLocatorParticipationInterface).setLastChgReasonCd(null);
        verify(activityLocatorParticipationInterface, never()).setItDirty(false); // Since isRealDirty is true
    }

    @Test
    void testPrepareActivityLocatorParticipationDT_NotDirty() throws DataProcessingException {
        // Arrange
        when(activityLocatorParticipationInterface.getRecordStatusCd()).thenReturn(NEDSSConstant.RECORD_STATUS_ACTIVE);
        when(activityLocatorParticipationInterface.getStatusCd()).thenReturn("ACTIVE");
        when(activityLocatorParticipationInterface.isItDirty()).thenReturn(false);

        // Act
        ActivityLocatorParticipationDto result = prepareAssocModelHelper.prepareActivityLocatorParticipationDT(activityLocatorParticipationInterface);

        // Assert
        assertNotNull(result);
        verify(activityLocatorParticipationInterface).setAddUserId(null);
        verify(activityLocatorParticipationInterface).setAddTime(null);
        verify(activityLocatorParticipationInterface).setRecordStatusTime(any(Timestamp.class));
        verify(activityLocatorParticipationInterface).setStatusTime(any(Timestamp.class));
        verify(activityLocatorParticipationInterface).setLastChgTime(any(Timestamp.class));
        verify(activityLocatorParticipationInterface).setLastChgUserId(anyLong());
        verify(activityLocatorParticipationInterface).setLastChgReasonCd(null);
        verify(activityLocatorParticipationInterface).setItDirty(false); // Since isRealDirty is false
    }

}
