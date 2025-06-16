package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueDpDpService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProviderMatchingServiceTest {
    @Mock
    private EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private CachingValueDpDpService cachingValueDpService;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;

    @InjectMocks
    private ProviderMatchingService providerMatchingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtil);
        Mockito.reset(entityHelper);
        Mockito.reset(patientRepositoryUtil);
        Mockito.reset(cachingValueDpService);
        Mockito.reset(prepareAssocModelHelper);
    }

    @Test
    void getMatchingProvider_local_id_entityMatch() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");
        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        edxEntityMatchingDT.setEntityUid(123L);
        edxEntityMatchingDT.setTypeCd("TYPE_CD");
        edxEntityMatchingDT.setMatchString("MATCH_STRING");
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(edxEntityMatchingDT.getEntityUid()),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_local_id_with_entityMatch_null_throw_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");
        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);
        assertThrows(NullPointerException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_local_id_throws_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_entityMatch_Identifier() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);

        //for getIdentifier
        EntityIdDto entityIdDto=new EntityIdDto();
        entityIdDto.setEntityIdSeq(1);
        entityIdDto.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        entityIdDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        entityIdDto.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        entityIdDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        entityIdDto.setRootExtensionTxt("TEST");
        entityIdDto.setAssigningAuthorityCd("TEST_ASSING_AUTHTY");
        entityIdDto.setAssigningAuthorityDescTxt("TEST_ASSING_AUTHTY_DESC");
        entityIdDto.setAssigningAuthorityIdType("TEST");
        personContainer.getTheEntityIdDtoCollection().add(entityIdDto);

        PersonNameDto personNameDto=new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        edxEntityMatchingDT.setEntityUid(123L);
        edxEntityMatchingDT.setTypeCd("TYPE_CD");
        edxEntityMatchingDT.setMatchString("MATCH_STRING");
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_entityMatch_Identifier_thorws_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);

        //for getIdentifier
        EntityIdDto entityIdDto=new EntityIdDto();
        entityIdDto.setEntityIdSeq(1);
        entityIdDto.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        entityIdDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        entityIdDto.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        entityIdDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        entityIdDto.setRootExtensionTxt("TEST");
        entityIdDto.setAssigningAuthorityCd("TEST_ASSING_AUTHTY");
        entityIdDto.setAssigningAuthorityDescTxt("TEST_ASSING_AUTHTY_DESC");
        entityIdDto.setAssigningAuthorityIdType("TEST");
        personContainer.getTheEntityIdDtoCollection().add(entityIdDto);

        PersonNameDto personNameDto=new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_entityMatch_Identifier_null_edxentity() throws DataProcessingException {
        // Setup PersonContainer
        PersonContainer personContainer = new PersonContainer();
        var personDto = new PersonDto();
        personDto.setPersonUid(123L);
        personDto.setCd(NEDSSConstant.PAT);
        personContainer.setThePersonDto(personDto);

        // Setup identifier (ELR_SS_TYPE)
        EntityIdDto entityIdDto = new EntityIdDto();
        entityIdDto.setEntityIdSeq(1);
        entityIdDto.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        entityIdDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        entityIdDto.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        entityIdDto.setRootExtensionTxt("TEST");
        entityIdDto.setAssigningAuthorityCd("TEST_AUTH");
        entityIdDto.setAssigningAuthorityDescTxt("TEST_DESC");
        entityIdDto.setAssigningAuthorityIdType("TEST");
        personContainer.getTheEntityIdDtoCollection().add(entityIdDto);

        // Setup name
        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        // Mock return value of repository
        EdxEntityMatchDto emptyMatch = new EdxEntityMatchDto(); // entityUid is null here intentionally
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(), any())).thenReturn(emptyMatch);

        // Spy the service to stub address and phone methods
        ProviderMatchingService spyService = spy(providerMatchingService);
        doReturn("123 MAIN ST").when(spyService).nameAddressStreetOneProvider(any());
        doReturn("5551234567").when(spyService).telePhoneTxtProvider(any());
        doReturn(123L).when(spyService).processingProvider(any(), any(), any());

        // Execute
        EDXActivityDetailLogDto result = spyService.getMatchingProvider(personContainer);

        // Assert
        assertEquals("123", result.getRecordId());
    }

    @Test
    void getMatchingProvider_name_addr() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        edxEntityMatchDto.setEntityUid(123L);
        edxEntityMatchDto.setTypeCd(NEDSSConstant.ORGANIZATION_CLASS_CODE);
        edxEntityMatchDto.setMatchString("TEST_MATCHSTRING");

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.POSTAL);
        entLocPartDT.setCd(NEDSSConstant.OFFICE_CD);
        entLocPartDT.setUseCd(NEDSSConstant.WORK_PLACE);

        PostalLocatorDto postLocDT = new PostalLocatorDto();
        postLocDT.setStreetAddr1("STREET_ADDR1");
        postLocDT.setCityDescTxt("CITYDESCTXT");
        postLocDT.setStateCd("STATE_CD");
        postLocDT.setZipCd("ZIP_CD");

        entLocPartDT.setThePostalLocatorDto(postLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        edxEntityMatchingDT.setEntityUid(123L);
        edxEntityMatchingDT.setTypeCd("TYPE_CD");
        edxEntityMatchingDT.setMatchString("MATCH_STRING");
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_name_addr_with_empty_entity() throws DataProcessingException {
        // Arrange
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(123L);
        personDto.setCd(NEDSSConstant.PRV);
        personContainer.setThePersonDto(personDto);

        // Setup postal locator
        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.POSTAL);
        entLocPartDT.setCd(NEDSSConstant.OFFICE_CD);
        entLocPartDT.setUseCd(NEDSSConstant.WORK_PLACE);

        PostalLocatorDto postLocDT = new PostalLocatorDto();
        postLocDT.setStreetAddr1("STREET_ADDR1");
        postLocDT.setCityDescTxt("CITYDESCTXT");
        postLocDT.setStateCd("STATE_CD");
        postLocDT.setZipCd("ZIP_CD");

        entLocPartDT.setThePostalLocatorDto(postLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        // Mock return value of repository
        EdxEntityMatchDto edxEntityMatchingDT = new EdxEntityMatchDto(); // entityUid is null (simulate no match)
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(), any())).thenReturn(edxEntityMatchingDT);

        // Spy the service and mock phone to avoid NPE
        ProviderMatchingService spyService = spy(providerMatchingService);
        doReturn("1234567890").when(spyService).telePhoneTxtProvider(any()); // prevent phone == null
        doReturn("STREET_ADDR1 CITYDESCTXT STATE_CD ZIP_CD").when(spyService).nameAddressStreetOneProvider(any());
        doReturn(123L).when(spyService).processingProvider(any(), any(), any());

        // Act
        EDXActivityDetailLogDto result = spyService.getMatchingProvider(personContainer);

        // Assert
        assertEquals("123", result.getRecordId());
    }


    @Test
    void getMatchingProvider_name_addr_throws_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.POSTAL);
        entLocPartDT.setCd(NEDSSConstant.OFFICE_CD);
        entLocPartDT.setUseCd(NEDSSConstant.WORK_PLACE);

        PostalLocatorDto postLocDT = new PostalLocatorDto();
        postLocDT.setStreetAddr1("STREET_ADDR1");
        postLocDT.setCityDescTxt("CITYDESCTXT");
        postLocDT.setStateCd("STATE_CD");
        postLocDT.setZipCd("ZIP_CD");

        entLocPartDT.setThePostalLocatorDto(postLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_telephone() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        edxEntityMatchDto.setEntityUid(123L);
        edxEntityMatchDto.setTypeCd(NEDSSConstant.ORGANIZATION_CLASS_CODE);
        edxEntityMatchDto.setMatchString("TEST_MATCHSTRING");

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");

        entLocPartDT.setTheTeleLocatorDto(teleLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        edxEntityMatchingDT.setEntityUid(123L);
        edxEntityMatchingDT.setTypeCd("TYPE_CD");
        edxEntityMatchingDT.setMatchString("MATCH_STRING");
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);
        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_telephone_empty_entity() throws DataProcessingException {
        // Arrange
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(123L);
        personDto.setCd(NEDSSConstant.PRV);
        personContainer.setThePersonDto(personDto);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");
        entLocPartDT.setTheTeleLocatorDto(teleLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        EdxEntityMatchDto edxEntityMatchingDT = new EdxEntityMatchDto(); // entityUid is null
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(), any()))
                .thenReturn(edxEntityMatchingDT);

        // Spy the service and stub address to avoid NPE
        ProviderMatchingService spyService = spy(providerMatchingService);
        doReturn("123 Main St").when(spyService).nameAddressStreetOneProvider(any()); // avoid nameAddr1 == null
        doReturn("1234567890").when(spyService).telePhoneTxtProvider(any());
        doReturn(123L).when(spyService).processingProvider(any(), any(), any());

        // Act
        EDXActivityDetailLogDto result = spyService.getMatchingProvider(personContainer);

        // Assert
        assertEquals("123", result.getRecordId());
    }


    @Test
    void getMatchingProvider_telephone_throws_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");

        entLocPartDT.setTheTeleLocatorDto(teleLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_provider() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var personDto = new PersonDto();
        personDto.setPersonUid(123L);
        personDto.setCd(NEDSSConstant.PRV);
        personContainer.setThePersonDto(personDto);

        ProviderMatchingService spyService = spy(providerMatchingService);

        // Provide non-null values to avoid null.hashCode() crash
        doReturn(null).when(spyService).getLocalId(any());
        doReturn(null).when(spyService).getIdentifier(any());
        doReturn("123 Main Street").when(spyService).nameAddressStreetOneProvider(any());
        doReturn("5551234567").when(spyService).telePhoneTxtProvider(any());
        doReturn(123L).when(spyService).processingProvider(any(), any(), any());

        EDXActivityDetailLogDto result = spyService.getMatchingProvider(personContainer);

        assertEquals("123", result.getRecordId());
    }

    @Test
    void setProvider() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        Long idResult=providerMatchingService.setProvider(personContainer,"TEST");
        assertEquals(123L,idResult);
    }

    @Test
    void attemptMatch_shouldPersistWhenMatchFoundAndRoleIsNull() throws DataProcessingException {
        // Arrange
        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getRole()).thenReturn(null);

        List<EdxEntityMatchDto> matchList = new ArrayList<>();
        EDXActivityDetailLogDto logDto = new EDXActivityDetailLogDto();

        String matchString = "TEST_MATCH_STRING";

        // Prepare mocked match with entityUid
        EdxEntityMatchDto matchedDto = new EdxEntityMatchDto();
        matchedDto.setEntityUid(456L);
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, matchString))
                .thenReturn(matchedDto);

        // Spy on service to verify internal calls
        ProviderMatchingService spyService = spy(providerMatchingService);
        doReturn(edxPatientMatchRepositoryUtil).when(spyService).getEdxPatientMatchRepositoryUtil();
        doNothing().when(spyService).persistIfNoRole(any(), eq(personContainer), eq(456L));

        // Act
        boolean result = spyService.attemptMatch(matchString, personContainer, matchList, logDto);

        // Assert
        assertTrue(result);
        assertEquals("456", logDto.getRecordId());
        assertTrue(logDto.getComment().contains("Provider entity found"));
        verify(spyService, times(1)).persistIfNoRole(any(), eq(personContainer), eq(456L));
    }

    @Test
    void persistMatchIfNotNull_shouldNotPersist_whenMatchStringIsNull() throws DataProcessingException {
        // Arrange
        String matchString = null;
        int hashCode = 0;
        Long entityUid = 123L;

        PersonContainer personContainer = mock(PersonContainer.class);

        ProviderMatchingService spyService = spy(providerMatchingService);

        // Spy to ensure persistIfNoRole is not called
        doNothing().when(spyService).persistIfNoRole(any(), any());

        // Act
        spyService.persistMatchIfNotNull(matchString, hashCode, entityUid, personContainer);

        // Assert
        verify(spyService, never()).persistIfNoRole(any(), any());
    }

    @Test
    void persistIfNoRole_shouldCallSaveEdxEntityMatch_whenRoleIsNull() throws DataProcessingException {
        // Arrange
        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getRole()).thenReturn(null); // role == null

        EdxEntityMatchDto dto = new EdxEntityMatchDto();

        ProviderMatchingService spyService = spy(providerMatchingService);
        doReturn(edxPatientMatchRepositoryUtil).when(spyService).getEdxPatientMatchRepositoryUtil();

        // Act
        spyService.persistIfNoRole(dto, personContainer);

        // Assert
        verify(edxPatientMatchRepositoryUtil, times(1)).saveEdxEntityMatch(dto);
    }


    @Test
    void testPersistIfNoRoleIsNull() {
        var person = new PersonContainer();
        person.setRole("PAT");
        providerMatchingService.persistIfNoRole(null, person);
        verify(edxPatientMatchRepositoryUtil, times(0)).saveEdxEntityMatch(any());
    }

    @Test
    void attemptMatch_shouldNotPersist_whenRoleIsNotNull() throws DataProcessingException {
        // Arrange
        String matchString = "MATCH_ID";
        Long entityUid = 789L;

        PersonContainer container = mock(PersonContainer.class);
        when(container.getRole()).thenReturn("NOT_NULL_ROLE");

        EdxEntityMatchDto matchedDto = new EdxEntityMatchDto();
        matchedDto.setEntityUid(entityUid);

        List<EdxEntityMatchDto> matchList = new ArrayList<>();
        EDXActivityDetailLogDto logDto = new EDXActivityDetailLogDto();

        // Spy on service to verify internal behavior
        ProviderMatchingService spyService = spy(providerMatchingService);
        doReturn(edxPatientMatchRepositoryUtil).when(spyService).getEdxPatientMatchRepositoryUtil();
        doReturn(matchedDto).when(edxPatientMatchRepositoryUtil)
                .getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, matchString);

        // Prevent persistIfNoRole from being executed (we verify it shouldn't)
        doNothing().when(spyService).persistIfNoRole(any(), any(), any());

        // Act
        boolean result = spyService.attemptMatch(matchString, container, matchList, logDto);

        // Assert
        assertTrue(result);
        assertEquals("789", logDto.getRecordId());
        assertTrue(logDto.getComment().contains("Provider entity found"));

        // Ensure persistIfNoRole is never called
        verify(spyService, never()).persistIfNoRole(any(), any(), any());
    }


}