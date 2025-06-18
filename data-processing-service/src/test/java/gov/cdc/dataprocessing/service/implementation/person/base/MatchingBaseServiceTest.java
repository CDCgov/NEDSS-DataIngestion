package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueDpDpService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchingBaseServiceTest {
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
    @Spy
    private MatchingBaseService matchingBaseService;
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
    @Mock
    private PersonContainer personContainer;
    @Mock
    private Coded coded;

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtil, coded, entityHelper,patientRepositoryUtil, cachingValueDpService,prepareAssocModelHelper, personContainer, authUtil);
    }


    @Test
    void testGetIdentifier_NoEntityIdDtoCollection() throws DataProcessingException {
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(null);

        List<String> identifiers = matchingBaseService.getIdentifier(personContainer);

        assertEquals(0, identifiers.size());
    }

    @Test
    void testGetIdentifier_EmptyEntityIdDtoCollection() throws DataProcessingException {
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(Arrays.asList());

        List<String> identifiers = matchingBaseService.getIdentifier(personContainer);

        assertEquals(0, identifiers.size());
    }

    @Test
    void getIdentifier_Test() throws DataProcessingException {
        var entityCol = new ArrayList<EntityIdDto>();
        var entity = new EntityIdDto();
        entity.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        entity.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        entity.setRootExtensionTxt("ROOT");
        entity.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        entity.setAssigningAuthorityCd("TEST");
        entityCol.add(entity);
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(entityCol);

        matchingBaseService.getIdentifier(personContainer);

        verify(personContainer, times(3)).getTheEntityIdDtoCollection();
    }

    @Test
    void getIdentifier_Test_2()  {
        when(personContainer.getTheEntityIdDtoCollection()).thenThrow(new RuntimeException());

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            matchingBaseService.getIdentifier(personContainer);
        });

        assertNotNull(thrown);
    }

    @Test
    void getNameStr_Test() {
        PersonContainer personContainer1 = new PersonContainer();

        var nameCol = new ArrayList<PersonNameDto>();
        var name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        name.setAsOfDate(TimeStampUtil.getCurrentTimeStampPlusOneDay("UTC"));
        name.setLastNm("TEST");
        name.setFirstNm("TEST");
        nameCol.add(name);
        name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        name.setAsOfDate(TimeStampUtil.getCurrentTimeStamp("UTC"));
        name.setLastNm("TEST");
        name.setFirstNm("TEST");
        nameCol.add(name);

        personContainer1.setThePersonNameDtoCollection(nameCol);
        personContainer1.getThePersonDto().setCd(NEDSSConstant.PAT);


        var res = matchingBaseService.getNamesStr(personContainer1);

        assertNotNull(res);


        personContainer1.setThePersonNameDtoCollection(nameCol);
    }

    @Test
    void testProcessingPersonNameBasedOnAsOfDate_ValidNames() {
        PersonNameDto dto = new PersonNameDto();
        dto.setFirstNm("John");
        dto.setLastNm("Doe");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        dto.setAsOfDate(timestamp);

        String result = matchingBaseService.processingPersonNameBasedOnAsOfDate(dto, "original", null);

        assertEquals("Doe^John", result);
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testProcessingPersonNameBasedOnAsOfDate_LastNameNull() {
        PersonNameDto dto = new PersonNameDto();
        dto.setFirstNm("John");
        dto.setLastNm(null);

        String result = matchingBaseService.processingPersonNameBasedOnAsOfDate(dto, "original", null);

        assertEquals("original", result);
    }

    @Test
    void testProcessingPersonNameBasedOnAsOfDate_LastNameEmpty() {
        PersonNameDto dto = new PersonNameDto();
        dto.setFirstNm("John");
        dto.setLastNm("   "); // whitespace

        String result = matchingBaseService.processingPersonNameBasedOnAsOfDate(dto, "original", null);

        assertEquals("original", result);
    }

    @Test
    void testProcessingPersonNameBasedOnAsOfDate_FirstNameNull() {
        PersonNameDto dto = new PersonNameDto();
        dto.setFirstNm(null);
        dto.setLastNm("Doe");

        String result = matchingBaseService.processingPersonNameBasedOnAsOfDate(dto, "original", null);

        assertEquals("original", result);
    }

    @Test
    void testProcessingPersonNameBasedOnAsOfDate_FirstNameEmpty() {
        PersonNameDto dto = new PersonNameDto();
        dto.setFirstNm("   ");
        dto.setLastNm("Doe");

        String result = matchingBaseService.processingPersonNameBasedOnAsOfDate(dto, "original", null);

        assertEquals("original", result);
    }

    @Test
    void testGetNamesStr_AllConditionsPass() {
        mockPersonWithName("L", NEDSSConstant.RECORD_STATUS_ACTIVE);
        String result = matchingBaseService.getNamesStr(personContainer);
        assertNotNull(result); // Name processed
    }

    @Test
    void testGetNamesStr_NmUseCdIsNull() {
        mockPersonWithName(null, NEDSSConstant.RECORD_STATUS_ACTIVE);
        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result); // Skipped
    }

    @Test
    void testGetNamesStr_NmUseCdNotL() {
        mockPersonWithName("X", NEDSSConstant.RECORD_STATUS_ACTIVE);
        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result); // Skipped
    }

    @Test
    void testGetNamesStr_RecordStatusCdIsNull() {
        mockPersonWithName("L", null);
        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result); // Skipped
    }

    @Test
    void testGetNamesStr_RecordStatusCdNotActive() {
        mockPersonWithName("L", "INACTIVE");
        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result); // Skipped
    }

    private void mockPersonWithName(String nmUseCd, String recordStatusCd) {
        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PAT);

        PersonNameDto nameDto = new PersonNameDto();
        nameDto.setFirstNm("Test");
        nameDto.setLastNm("User");
        nameDto.setAsOfDate(Timestamp.valueOf("2021-01-01 00:00:00"));
        nameDto.setNmUseCd(nmUseCd);
        nameDto.setRecordStatusCd(recordStatusCd);

        Collection<PersonNameDto> nameColl = new ArrayList<>();
        nameColl.add(nameDto);

        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.getThePersonNameDtoCollection()).thenReturn(nameColl);

        doReturn("User^Test").when(matchingBaseService).processingPersonNameBasedOnAsOfDate(any(), any(), any());
    }

    @Test
    void testGetNamesStr_NameCollectionIsNull() {
        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PAT);

        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.getThePersonNameDtoCollection()).thenReturn(null);

        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result);
    }


    @Test
    void testGetNamesStr_NameCollectionIsEmpty() {
        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PAT);

        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.getThePersonNameDtoCollection()).thenReturn(Collections.emptyList());

        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result);
    }

    @Test
    void testGetNamesStr_PersonCdIsNull() {
        PersonDto personDto = new PersonDto();
        personDto.setCd(null); // null

        when(personContainer.getThePersonDto()).thenReturn(personDto);

        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result);
    }


    @Test
    void testGetNamesStr_PersonCdIsNotPAT() {
        PersonDto personDto = new PersonDto();
        personDto.setCd("DOC"); // not "PAT"

        when(personContainer.getThePersonDto()).thenReturn(personDto);

        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result);
    }

    @Test
    void testGetNamesStr_PersonDtoIsNull() {
        when(personContainer.getThePersonDto()).thenReturn(null);

        String result = matchingBaseService.getNamesStr(personContainer);
        assertNull(result);
    }


    @SuppressWarnings("java:S1117")
    @Test
    void testGetIdentifier_TriggersElseBlock() throws DataProcessingException {
        EntityIdDto idDto = new EntityIdDto();
        idDto.setStatusCd(NEDSSConstant.STATUS_ACTIVE); // pass status check
        idDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE); // pass record status
        idDto.setRootExtensionTxt("123456");
        idDto.setTypeCd("SS");
        idDto.setAssigningAuthorityCd("AUTH_CD");

        // assign null to one of these to skip the main 'if' block
        idDto.setAssigningAuthorityDescTxt(null);
        idDto.setAssigningAuthorityIdType(null); // triggers 'else'

        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PAT);

        // required by getNamesStr
        PersonNameDto nameDto = new PersonNameDto();
        nameDto.setFirstNm("John");
        nameDto.setLastNm("Doe");
        nameDto.setAsOfDate(Timestamp.valueOf("2022-01-01 00:00:00"));
        nameDto.setNmUseCd("L");
        nameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);

        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.getThePersonNameDtoCollection()).thenReturn(List.of(nameDto));
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(List.of(idDto));

        // Mock getNamesStr to return something so it reaches identifierList.add()
        doReturn("Doe^John").when(matchingBaseService).getNamesStr(any());

        // Also mock Coded if needed to simulate non-null values
        Coded coded = new Coded();
        coded.setCode("AUTH_CD");
        coded.setCodesetName(NEDSSConstant.EI_AUTH);
        coded.setCodeDescription("Desc");
        coded.setCodeSystemCd("SYS_CD");

        // Simulate coded behavior (if needed, could also use a factory or refactor for testability)

        List<String> result = matchingBaseService.getIdentifier(personContainer);

        assertNotNull(result);
    }


    @Test
    void testGetIdentifier_statusCdIsNull_shouldNotMatch() throws Exception {
        EntityIdDto idDto = buildIdDto(null, "ACTIVE", "ELR_SS"); // statusCd null
        testConditionFalse(idDto);
    }

    @Test
    void testGetIdentifier_statusCdIsInactive_shouldNotMatch() throws Exception {
        EntityIdDto idDto = buildIdDto("INACTIVE", "ACTIVE", "ELR_SS"); // wrong status
        testConditionFalse(idDto);
    }

    @Test
    void testGetIdentifier_recordStatusCdIsNull_shouldNotMatch() throws Exception {
        EntityIdDto idDto = buildIdDto("ACTIVE", null, "ELR_SS");
        testConditionFalse(idDto);
    }

    @Test
    void testGetIdentifier_typeCdIsNull_shouldNotMatch() throws Exception {
        EntityIdDto idDto = buildIdDto(null, "ACTIVE", null); // ELR_SS_TYPE check fails
        testConditionFalse(idDto);
    }

    @Test
    void testGetIdentifier_typeCdNotMatching_shouldNotMatch() throws Exception {
        EntityIdDto idDto = buildIdDto(null, "ACTIVE", "NON_ELR");
        testConditionFalse(idDto);
    }


    private EntityIdDto buildIdDto(String statusCd, String recordStatusCd, String typeCd) {
        EntityIdDto dto = new EntityIdDto();
        dto.setStatusCd(statusCd);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setTypeCd(typeCd);

        dto.setRootExtensionTxt("123");
        dto.setAssigningAuthorityCd("AUTH");
        dto.setAssigningAuthorityDescTxt("DESC");
        dto.setAssigningAuthorityIdType("TYPE");

        return dto;
    }

    private void testConditionFalse(EntityIdDto idDto) throws Exception {
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(List.of(idDto));
        when(personContainer.getThePersonDto()).thenReturn(null); // skip name lookup
        List<String> result = matchingBaseService.getIdentifier(personContainer);
        assertTrue(result.isEmpty(), "Expected no identifiers to be matched");
    }


    @Test
    void testGetIdentifier_AssigningAuthorityIdTypeIsNull_ShouldNotEnterIfBlock() throws Exception {
        EntityIdDto dto = baseValidIdDto();
        dto.setAssigningAuthorityIdType(null);
        runExpectingEmptyIdentifier(dto);
    }

    @Test
    void testGetIdentifier_AssigningAuthorityDescTxtIsNull_ShouldNotEnterIfBlock() throws Exception {
        EntityIdDto dto = baseValidIdDto();
        dto.setAssigningAuthorityDescTxt(null);
        runExpectingEmptyIdentifier(dto);
    }

    @Test
    void testGetIdentifier_AssigningAuthorityCdIsNull_ShouldNotEnterIfBlock() throws Exception {
        EntityIdDto dto = baseValidIdDto();
        dto.setAssigningAuthorityCd(null);
        runExpectingEmptyIdentifier(dto);
    }

    @Test
    void testGetIdentifier_TypeCdIsNull_ShouldNotEnterIfBlock() throws Exception {
        EntityIdDto dto = baseValidIdDto();
        dto.setTypeCd(null);
        runExpectingEmptyIdentifier(dto);
    }

    @Test
    void testGetIdentifier_RootExtensionTxtIsNull_ShouldNotEnterIfBlock() throws Exception {
        EntityIdDto dto = baseValidIdDto();
        dto.setRootExtensionTxt(null);
        runExpectingEmptyIdentifier(dto);
    }

    private void runExpectingEmptyIdentifier(EntityIdDto dto) throws Exception {
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(List.of(dto));
        when(personContainer.getThePersonDto()).thenReturn(null); // skip name block
        List<String> result = matchingBaseService.getIdentifier(personContainer);
        assertTrue(result.isEmpty(), "Expected empty result due to null in mandatory field");
    }


    private EntityIdDto baseValidIdDto() {
        EntityIdDto dto = new EntityIdDto();
        dto.setRootExtensionTxt("123");
        dto.setTypeCd("SS");
        dto.setAssigningAuthorityCd("AUTH_CD");
        dto.setAssigningAuthorityDescTxt("DESC");
        dto.setAssigningAuthorityIdType("TYPE");
        dto.setStatusCd("ACTIVE");
        dto.setRecordStatusCd("ACTIVE");

        return dto;
    }



}
