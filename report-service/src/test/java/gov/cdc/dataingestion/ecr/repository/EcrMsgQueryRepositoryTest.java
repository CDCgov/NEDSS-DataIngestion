package gov.cdc.dataingestion.ecr.repository;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.implementation.EcrMsgQueryRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class EcrMsgQueryRepositoryTest {
    @InjectMocks
    private EcrMsgQueryRepository target;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchMsgContainerForApplicableEcr_Test() throws Exception {
        // Arrange
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{
                0,
                "1",
                2,
                "3",
                "4",
                5,
                6});
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        EcrMsgContainerDto result = target.fetchMsgContainerForApplicableEcr();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getMsgContainerUid());
        assertEquals("1", result.getInvLocalId());
        assertEquals(2, result.getNbsInterfaceUid());
        assertEquals("3", result.getReceivingSystem());
        assertEquals("4", result.getOngoingCase());
        assertEquals(5, result.getVersionCtrNbr());
        assertEquals(6, result.getDataMigrationStatus());
    }


    @Test
    void fetchMsgPatientForApplicableEcr_Test() throws Exception {
        // Arrange
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{
                0, "1", "2", new Timestamp(System.currentTimeMillis()), "4", "5", "6",
                "7", "8", "9", "10", "11", "12", "13", "14", new Timestamp(System.currentTimeMillis()),
                "16", "17", "18", "19", "20", new Timestamp(System.currentTimeMillis()), "22", "23",
                "24", new Timestamp(System.currentTimeMillis()), "26", "27", "28", "29", "30", "31",
                new Timestamp(System.currentTimeMillis()), "33", "34", "35", "36", "37", "38", "39", "40",
                41, "42", "43", "44", "45", 46, "47", "48", "49", new Timestamp(System.currentTimeMillis()),
                "51", "52", 53
        });
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        List<EcrMsgPatientDto> results = target.fetchMsgPatientForApplicableEcr(1); // You can change the argument as needed

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());

        EcrMsgPatientDto resultDto = results.get(0);

        assertEquals(0, resultDto.getMsgContainerUid());
        assertEquals("1", resultDto.getPatLocalId());
        assertEquals("2", resultDto.getPatAuthorId());
        assertEquals("4", resultDto.getPatAddrCityTxt());
        assertEquals("5", resultDto.getPatAddrCommentTxt());
        assertEquals("6", resultDto.getPatAddrCountyCd());
        assertEquals("7", resultDto.getPatAddrCountryCd());
        assertEquals("8", resultDto.getPatAdditionalGenderTxt());
        assertEquals("9", resultDto.getPatAddrCensusTractTxt());
        assertEquals("10", resultDto.getPatAddrStateCd());
        assertEquals("11", resultDto.getPatAddrStreetAddr1Txt());

        fetchMsgPatientForApplicableEcr_TestAssertionCheck2(resultDto);
        fetchMsgPatientForApplicableEcr_TestAssertionCheck(resultDto);
    }

    private void fetchMsgPatientForApplicableEcr_TestAssertionCheck( EcrMsgPatientDto resultDto ) {
        assertEquals("30", resultDto.getPatHomePhoneNbrTxt());
        assertEquals("31", resultDto.getPatNameAliasTxt());
        assertEquals("33", resultDto.getPatNameDegreeCd());
        assertEquals("34", resultDto.getPatNameFirstTxt());
        assertEquals("35", resultDto.getPatNameLastTxt());
        assertEquals("36", resultDto.getPatNameMiddleTxt());
        assertEquals("37", resultDto.getPatNamePrefixCd());
        assertEquals("38", resultDto.getPatNameSuffixCd());
        assertEquals("39", resultDto.getPatMaritalStatusCd());
        assertEquals("40", resultDto.getPatPhoneCommentTxt());
        assertEquals(41, resultDto.getPatPhoneCountryCodeTxt());
        assertEquals("42", resultDto.getPatPrimaryLanguageCd());
        assertEquals("43", resultDto.getPatPreferredGenderCd());
        assertEquals("44", resultDto.getPatRaceCategoryCd());
        assertEquals("45", resultDto.getPatRaceDescTxt());
        assertEquals(46, resultDto.getPatReportedAge());
        assertEquals("47", resultDto.getPatReportedAgeUnitCd());
        assertEquals("48", resultDto.getPatSexUnkReasonCd());
        assertEquals("49", resultDto.getPatSpeaksEnglishIndCd());
        assertEquals("51", resultDto.getPatUrlAddressTxt());
        assertEquals("52", resultDto.getPatWorkPhoneNbrTxt());
        assertEquals(53, resultDto.getPatWorkPhoneExtensionTxt());
    }
    private void fetchMsgPatientForApplicableEcr_TestAssertionCheck2( EcrMsgPatientDto resultDto ) {
        assertEquals("12", resultDto.getPatAddrStreetAddr2Txt());
        assertEquals("13", resultDto.getPatAddrZipCodeTxt());
        assertEquals("14", resultDto.getPatBirthCountryCd());
        assertEquals("16", resultDto.getPatBirthSexCd());
        assertEquals("17", resultDto.getPatCellPhoneNbrTxt());
        assertEquals("18", resultDto.getPatCommentTxt());
        assertEquals("19", resultDto.getPatCurrentSexCd());
        assertEquals("20", resultDto.getPatDeceasedIndCd());
        assertEquals("22", resultDto.getPatEffectiveTime());
        assertEquals("23", resultDto.getPatIdMedicalRecordNbrTxt());
        assertEquals("24", resultDto.getPatIdStateHivCaseNbrTxt());
        assertEquals("26", resultDto.getPatIdSsnTxt());
        assertEquals("27", resultDto.getPatEmailAddressTxt());
        assertEquals("28", resultDto.getPatEthnicGroupIndCd());
        assertEquals("29", resultDto.getPatEthnicityUnkReasonCd());
    }

    @Test
    void fetchMsgCaseParticipantForApplicableEcr_Test() throws Exception {
        // Arrange
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{
                "1", "2", "3", "4", 5, "6", "7", 8, 9
        });
        when(mockQuery.getResultList()).thenReturn(mockResults);

        Integer containerId = 1;
        String invLocalId = "inv123";

        // Act
        List<EcrMsgCaseParticipantDto> results = target.fetchMsgCaseParticipantForApplicableEcr(containerId, invLocalId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());

        EcrMsgCaseParticipantDto resultDto = results.get(0);

        assertEquals("1", resultDto.getMsgEventId());
        assertEquals("2", resultDto.getMsgEventType());
        assertEquals("3", resultDto.getAnswerTxt());
        assertEquals("4", resultDto.getAnswerLargeTxt());
        assertEquals(5, resultDto.getAnswerGroupSeqNbr());
        assertEquals("6", resultDto.getPartTypeCd());
        assertEquals("7", resultDto.getQuestionIdentifier());
        assertEquals(8, resultDto.getQuestionGroupSeqNbr());
        assertEquals(9, resultDto.getSeqNbr());
    }

    @Test
    void fetchMsgCaseAnswerForApplicableEcr_Test() throws Exception {
        // Arrange
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{
                "questionIdentifierValue",
                1,
                "msgEventIdValue",
                "msgEventTypeValue",
                "ansCodeSystemCdValue",
                "ansCodeSystemDescTxtValue",
                "ansDisplayTxtValue",
                "answerTxtValue",
                "partTypeCdValue",
                "quesCodeSystemCdValue",
                "quesCodeSystemDescTxtValue",
                "quesDisplayTxtValue",
                "questionDisplayNameValue",
                "ansToCodeValue",
                "ansToCodeSystemCdValue",
                "ansToDisplayNmValue",
                "codeTranslationRequiredValue",
                "ansToCodeSystemDescTxtValue"
        });
        when(mockQuery.getResultList()).thenReturn(mockResults);

        Integer containerId = 123;
        String invLocalId = "inv456";

        // Act
        List<EcrMsgCaseAnswerDto> results = target.fetchMsgCaseAnswerForApplicableEcr(containerId, invLocalId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());

        EcrMsgCaseAnswerDto resultDto = results.get(0);

        assertEquals("questionIdentifierValue", resultDto.getQuestionIdentifier());
        assertEquals(1, resultDto.getMsgContainerUid());
        assertEquals("msgEventIdValue", resultDto.getMsgEventId());
        assertEquals("msgEventTypeValue", resultDto.getMsgEventType());
        assertEquals("ansCodeSystemCdValue", resultDto.getAnsCodeSystemCd());
        assertEquals("ansCodeSystemDescTxtValue", resultDto.getAnsCodeSystemDescTxt());
        assertEquals("ansDisplayTxtValue", resultDto.getAnsDisplayTxt());
        assertEquals("answerTxtValue", resultDto.getAnswerTxt());
        assertEquals("partTypeCdValue", resultDto.getPartTypeCd());
        assertEquals("quesCodeSystemCdValue", resultDto.getQuesCodeSystemCd());
        assertEquals("quesCodeSystemDescTxtValue", resultDto.getQuesCodeSystemDescTxt());
        assertEquals("quesDisplayTxtValue", resultDto.getQuesDisplayTxt());
        assertEquals("questionDisplayNameValue", resultDto.getQuestionDisplayName());
        assertEquals("ansToCodeValue", resultDto.getAnsToCode());
        assertEquals("ansToCodeSystemCdValue", resultDto.getAnsToCodeSystemCd());
        assertEquals("ansToDisplayNmValue", resultDto.getAnsToDisplayNm());
        assertEquals("codeTranslationRequiredValue", resultDto.getCodeTranslationRequired());
        assertEquals("ansToCodeSystemDescTxtValue", resultDto.getAnsToCodeSystemDescTxt());
    }

    @Test
    void fetchMsgCaseAnswerRepeatForApplicableEcr_Test() throws Exception {
        // Arrange
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{
                "questionIdentifierValue",
                1,
                "msgEventIdValue",
                "msgEventTypeValue",
                "ansCodeSystemCdValue",
                "ansCodeSystemDescTxtValue",
                "ansDisplayTxtValue",
                "answerTxtValue",
                "partTypeCdValue",
                "quesCodeSystemCdValue",
                "quesCodeSystemDescTxtValue",
                "quesDisplayTxtValue",
                "questionDisplayNameValue",
                "ansToCodeValue",
                "ansToCodeSystemCdValue",
                "ansToDisplayNmValue",
                "codeTranslationRequiredValue",
                "ansToCodeSystemDescTxtValue"
        });
        when(mockQuery.getResultList()).thenReturn(mockResults);

        Integer containerId = 123;
        String invLocalId = "inv456";

        // Act
        List<EcrMsgCaseAnswerDto> results = target.fetchMsgCaseAnswerRepeatForApplicableEcr(containerId, invLocalId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());

        EcrMsgCaseAnswerDto resultDto = results.get(0);

        assertEquals("questionIdentifierValue", resultDto.getQuestionIdentifier());
        assertEquals(1, resultDto.getMsgContainerUid());
        assertEquals("msgEventIdValue", resultDto.getMsgEventId());
        assertEquals("msgEventTypeValue", resultDto.getMsgEventType());
        assertEquals("ansCodeSystemCdValue", resultDto.getAnsCodeSystemCd());
        assertEquals("ansCodeSystemDescTxtValue", resultDto.getAnsCodeSystemDescTxt());
        assertEquals("ansDisplayTxtValue", resultDto.getAnsDisplayTxt());
        assertEquals("answerTxtValue", resultDto.getAnswerTxt());
        assertEquals("partTypeCdValue", resultDto.getPartTypeCd());
        assertEquals("quesCodeSystemCdValue", resultDto.getQuesCodeSystemCd());
        assertEquals("quesCodeSystemDescTxtValue", resultDto.getQuesCodeSystemDescTxt());
        assertEquals("quesDisplayTxtValue", resultDto.getQuesDisplayTxt());
        assertEquals("questionDisplayNameValue", resultDto.getQuestionDisplayName());
        assertEquals("ansToCodeValue", resultDto.getAnsToCode());
        assertEquals("ansToCodeSystemCdValue", resultDto.getAnsToCodeSystemCd());
        assertEquals("ansToDisplayNmValue", resultDto.getAnsToDisplayNm());
        assertEquals("codeTranslationRequiredValue", resultDto.getCodeTranslationRequired());
        assertEquals("ansToCodeSystemDescTxtValue", resultDto.getAnsToCodeSystemDescTxt());
    }

    @Test
    void testFetchMsgXmlAnswerForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Integer containerId = 1;
        String invLocalId = "123";

        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(
                Arrays.asList(new Object[]{"data1", "xml1"}, new Object[]{"data2", "xml2"})
        );

        // When
        List<EcrMsgXmlAnswerDto> result = target.fetchMsgXmlAnswerForApplicableEcr(containerId, invLocalId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("data1", result.get(0).getDataType());
        assertEquals("xml1", result.get(0).getAnswerXmlTxt());
        assertEquals("data2", result.get(1).getDataType());
        assertEquals("xml2", result.get(1).getAnswerXmlTxt());

    }

    @Test
    void testFetchMsgProviderForApplicableEcr() throws EcrCdaXmlException {
        // Arrange
        Query query = mock(Query.class);
        Integer containerId = 1;
        Object[] objArray = {
                "LocalId", "AuthorId", "CityTxt", "CommentTxt", "CountyCd", "CountryCd", "StreetAddr1Txt",
                "StreetAddr2Txt", "StateCd", "ZipCodeTxt", "CommentTxt", "AltIdNbrTxt", "QuickCodeTxt", "IdNbrTxt",
                "NpiTxt", new Timestamp(0), "EmailTxt", "DegreeCd", "FirstTxt", "LastTxt", "MiddleTxt",
                "PrefixCd", "SuffixCd", "PhoneCommentTxt", "CountryCodeTxt", 1, "PhoneNbrTxt", "RoleCd", "UrlAddressTxt"
        };

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Object[][]{ objArray }));

        // Act
        List<EcrMsgProviderDto> result = target.fetchMsgProviderForApplicableEcr(containerId);

        // Assert
        assertFalse(result.isEmpty());
        EcrMsgProviderDto dto = result.get(0);
        assertEquals("LocalId", dto.getPrvLocalId());
        assertEquals("AuthorId", dto.getPrvAuthorId());
        assertEquals("CityTxt", dto.getPrvAddrCityTxt());
        assertEquals("CommentTxt", dto.getPrvAddrCommentTxt());
        assertEquals("CountyCd", dto.getPrvAddrCountyCd());
        assertEquals("CountryCd", dto.getPrvAddrCountryCd());
        assertEquals("StreetAddr1Txt", dto.getPrvAddrStreetAddr1Txt());
        assertEquals("StreetAddr2Txt", dto.getPrvAddrStreetAddr2Txt());
        assertEquals("StateCd", dto.getPrvAddrStateCd());
        testFetchMsgProviderForApplicableEcrAssertion(dto);
    }

    private void testFetchMsgProviderForApplicableEcrAssertion(EcrMsgProviderDto dto) {
        assertEquals("ZipCodeTxt", dto.getPrvAddrZipCodeTxt());
        assertEquals("CommentTxt", dto.getPrvCommentTxt());
        assertEquals("AltIdNbrTxt", dto.getPrvIdAltIdNbrTxt());
        assertEquals("QuickCodeTxt", dto.getPrvIdQuickCodeTxt());
        assertEquals("IdNbrTxt", dto.getPrvIdNbrTxt());
        assertEquals("NpiTxt", dto.getPrvIdNpiTxt());
        assertNotNull(dto.getPrvEffectiveTime());
        assertEquals("EmailTxt", dto.getPrvEmailAddressTxt());
        assertEquals("DegreeCd", dto.getPrvNameDegreeCd());
        assertEquals("FirstTxt", dto.getPrvNameFirstTxt());
        assertEquals("LastTxt", dto.getPrvNameLastTxt());
        assertEquals("MiddleTxt", dto.getPrvNameMiddleTxt());
        assertEquals("PrefixCd", dto.getPrvNamePrefixCd());
        assertEquals("SuffixCd", dto.getPrvNameSuffixCd());
        assertEquals("PhoneCommentTxt", dto.getPrvPhoneCommentTxt());
        assertEquals("CountryCodeTxt", dto.getPrvPhoneCountryCodeTxt());
        assertEquals(1, dto.getPrvPhoneExtensionTxt());
        assertEquals("PhoneNbrTxt", dto.getPrvPhoneNbrTxt());
        assertEquals("RoleCd", dto.getPrvRoleCd());
        assertEquals("UrlAddressTxt", dto.getPrvUrlAddressTxt());
    }

    @Test
    void testFetchMsgOrganizationForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Query query = mock(Query.class);
        Integer containerId = 123;

        Object[] objArray = {
                "orgLocalId", "orgAuthorId", new Timestamp(System.currentTimeMillis()), "orgNameTxt", "orgAddrCityTxt",
                "orgAddrCommentTxt", "orgAddrCountyCd", "orgAddrCountryCd", "orgAddrStateCd", "orgAddrStreetAddr1Txt",
                "orgAddrStreetAddr2Txt", "orgAddrZipCodeTxt", "orgClassCd", "orgCommentTxt", "orgEmailAddressTxt",
                "orgIdCliaNbrTxt", "orgIdFacilityIdentifierTxt", "orgIdQuickCodeTxt", "orgPhoneCommentTxt", "orgPhoneCountryCodeTxt",
                1, "orgPhoneNbrTxt", "orgRoleCd", "orgUrlAddressTxt"
        };


        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Object[][]{ objArray }));

        // When
        List<EcrMsgOrganizationDto> result = target.fetchMsgOrganizationForApplicableEcr(containerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        EcrMsgOrganizationDto dto = result.get(0);
        assertEquals("orgLocalId", dto.getOrgLocalId());
        assertEquals("orgAuthorId", dto.getOrgAuthorId());
        assertNotNull(dto.getOrgEffectiveTime());
        assertEquals("orgNameTxt", dto.getOrgNameTxt());
        assertEquals("orgAddrCityTxt", dto.getOrgAddrCityTxt());
        assertEquals("orgAddrCommentTxt", dto.getOrgAddrCommentTxt());
        assertEquals("orgAddrCountyCd", dto.getOrgAddrCountyCd());
        assertEquals("orgAddrCountryCd", dto.getOrgAddrCountryCd());
        assertEquals("orgAddrStateCd", dto.getOrgAddrStateCd());
        assertEquals("orgAddrStreetAddr1Txt", dto.getOrgAddrStreetAddr1Txt());
        assertEquals("orgAddrStreetAddr2Txt", dto.getOrgAddrStreetAddr2Txt());
        testFetchMsgOrganizationForApplicableEcrAssertion(dto);

    }
    private void testFetchMsgOrganizationForApplicableEcrAssertion(EcrMsgOrganizationDto dto ) {
        assertEquals("orgAddrZipCodeTxt", dto.getOrgAddrZipCodeTxt());
        assertEquals("orgClassCd", dto.getOrgClassCd());
        assertEquals("orgCommentTxt", dto.getOrgCommentTxt());
        assertEquals("orgEmailAddressTxt", dto.getOrgEmailAddressTxt());
        assertEquals("orgIdCliaNbrTxt", dto.getOrgIdCliaNbrTxt());
        assertEquals("orgIdFacilityIdentifierTxt", dto.getOrgIdFacilityIdentifierTxt());
        assertEquals("orgIdQuickCodeTxt", dto.getOrgIdQuickCodeTxt());
        assertEquals("orgPhoneCommentTxt", dto.getOrgPhoneCommentTxt());
        assertEquals("orgPhoneCountryCodeTxt", dto.getOrgPhoneCountryCodeTxt());
        assertEquals(Integer.valueOf(1), dto.getOrgPhoneExtensionTxt());
        assertEquals("orgPhoneNbrTxt", dto.getOrgPhoneNbrTxt());
        assertEquals("orgRoleCd", dto.getOrgRoleCd());
        assertEquals("orgUrlAddressTxt", dto.getOrgUrlAddressTxt());
    }

    @Test
    void testFetchMsgPlaceForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Query query = mock(Query.class);
        Integer containerId = 123;

        Object[] objArray = {
                1, "plaLocalId", "plaAuthorId", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                "plaAddrCityTxt", "plaAddrCountyCd", "plaAddrCountryCd", "plaAddrStateCd", "plaAddrStreetAddr1Txt",
                "plaAddrStreetAddr2Txt", "plaAddrZipCodeTxt", "plaAddrCommentTxt", "plaCensusTractTxt", "plaCommentTxt",
                "plaEmailAddressTxt", "plaIdQuickCode", "plaNameTxt", new Timestamp(System.currentTimeMillis()), "plaPhoneCountryCodeTxt",
                "plaPhoneExtensionTxt", "plaPhoneNbrTxt", "plaPhoneCommentTxt", "plaTypeCd", "plaUrlAddressTxt"
        };

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Arrays.asList(new Object[][]{ objArray }));

        // When
        List<EcrMsgPlaceDto> result = target.fetchMsgPlaceForApplicableEcr(containerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        EcrMsgPlaceDto dto = result.get(0);
        assertEquals(Integer.valueOf(1), dto.getMsgContainerUid());
        assertEquals("plaLocalId", dto.getPlaLocalId());
        assertEquals("plaAuthorId", dto.getPlaAuthorId());
        assertNotNull(dto.getPlaEffectiveTime());
        assertNotNull(dto.getPlaAddrAsOfDt());
        assertEquals("plaAddrCityTxt", dto.getPlaAddrCityTxt());
        assertEquals("plaAddrCountyCd", dto.getPlaAddrCountyCd());
        assertEquals("plaAddrCountryCd", dto.getPlaAddrCountryCd());
        assertEquals("plaAddrStateCd", dto.getPlaAddrStateCd());
        assertEquals("plaAddrStreetAddr1Txt", dto.getPlaAddrStreetAddr1Txt());
        assertEquals("plaAddrStreetAddr2Txt", dto.getPlaAddrStreetAddr2Txt());
        assertEquals("plaAddrZipCodeTxt", dto.getPlaAddrZipCodeTxt());
        testFetchMsgPlaceForApplicableEcrAssertion(dto);
    }

    private void testFetchMsgPlaceForApplicableEcrAssertion(EcrMsgPlaceDto dto) {
        assertEquals("plaAddrCommentTxt", dto.getPlaAddrCommentTxt());
        assertEquals("plaCensusTractTxt", dto.getPlaCensusTractTxt());
        assertEquals("plaCommentTxt", dto.getPlaCommentTxt());
        assertEquals("plaEmailAddressTxt", dto.getPlaEmailAddressTxt());
        assertEquals("plaIdQuickCode", dto.getPlaIdQuickCode());
        assertEquals("plaNameTxt", dto.getPlaNameTxt());
        assertNotNull(dto.getPlaPhoneAsOfDt());
        assertEquals("plaPhoneCountryCodeTxt", dto.getPlaPhoneCountryCodeTxt());
        assertEquals("plaPhoneExtensionTxt", dto.getPlaPhoneExtensionTxt());
        assertEquals("plaPhoneNbrTxt", dto.getPlaPhoneNbrTxt());
        assertEquals("plaPhoneCommentTxt", dto.getPlaPhoneCommentTxt());
        assertEquals("plaTypeCd", dto.getPlaTypeCd());
        assertEquals("plaUrlAddressTxt", dto.getPlaUrlAddressTxt());
    }

    @Test
    void testFetchMsgInterviewForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Query query = mock(Query.class);

        Integer containerId = 123;
        Object[] interviewArray = {
                1, "ixsLocalId", "ixsIntervieweeId", "ixsAuthorId", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), "ixsInterviewLocCd", "ixsIntervieweeRoleCd", "ixsInterviewTypeCd", "ixsStatusCd"
        };

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn
                (
                        Arrays.asList(new Object[][]{ interviewArray })
                );

        // When
        List<EcrMsgInterviewDto> result = target.fetchMsgInterviewForApplicableEcr(containerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        EcrMsgInterviewDto dto = result.get(0);
        assertEquals(Integer.valueOf(1), dto.getMsgContainerUid());
        assertEquals("ixsLocalId", dto.getIxsLocalId());
        assertEquals("ixsIntervieweeId", dto.getIxsIntervieweeId());
        assertEquals("ixsAuthorId", dto.getIxsAuthorId());
        assertNotNull(dto.getIxsEffectiveTime());
        assertNotNull(dto.getIxsInterviewDt());
        assertEquals("ixsInterviewLocCd", dto.getIxsInterviewLocCd());
        assertEquals("ixsIntervieweeRoleCd", dto.getIxsIntervieweeRoleCd());
        assertEquals("ixsInterviewTypeCd", dto.getIxsInterviewTypeCd());
        assertEquals("ixsStatusCd", dto.getIxsStatusCd());
    }

    @Test
    void testFetchMsgInterviewProviderForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Integer containerId = 123;
        String ixsLocalId = "testIxsLocalId";
        Object[] providerArray = {
                "localId", "authorId", "city", "comment", "county", "country",
                "street1", "street2", "state", "zip", "comment2", "altId", "quickCode",
                "idNumber", "npi", new Timestamp(System.currentTimeMillis()), "email", "degree",
                "firstName", "lastName", "middleName", "prefix", "suffix", "phoneComment",
                "countryCode", 1234, "phoneNumber", "role", "url"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(
                Arrays.asList(new Object[][]{ providerArray })
        );

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        List<EcrMsgProviderDto> result = target.fetchMsgInterviewProviderForApplicableEcr(containerId, ixsLocalId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        EcrMsgProviderDto resultDto = result.get(0);
        assertEquals("localId", resultDto.getPrvLocalId());
        assertEquals("authorId", resultDto.getPrvAuthorId());
        assertEquals("city", resultDto.getPrvAddrCityTxt());

    }

    @Test
    void testFetchMsgInterviewAnswerForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Integer containerId = 123;
        String ixsLocalId = "testIxsLocalId";
        Object[] answerArray = {
                "questionId", 1234, "eventId", "eventType", "codeSystemCd", "codeSystemDesc",
                "displayTxt", "answerTxt", "partTypeCd", "quesCodeSystemCd", "quesCodeSystemDesc",
                "quesDisplayTxt", "quesDisplayName", "ansToCode", "ansToCodeSystemCd", "ansToDisplayNm",
                "translationRequired", "ansToCodeSystemDesc"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(
                Arrays.asList(new Object[][]{ answerArray })

        );

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        List<EcrMsgCaseAnswerDto> result = target.fetchMsgInterviewAnswerForApplicableEcr(containerId, ixsLocalId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        EcrMsgCaseAnswerDto resultDto = result.get(0);
        assertEquals("questionId", resultDto.getQuestionIdentifier());
        assertEquals(Integer.valueOf(1234), resultDto.getMsgContainerUid());
        assertEquals("eventId", resultDto.getMsgEventId());
    }

    @Test
    void fetchMsgInterviewAnswerRepeatForApplicableEcr_Test() throws EcrCdaXmlException {
        // Given
        Integer containerId = 123;
        String ixsLocalId = "testIxsLocalId";
        Object[] answerArray = {
                "questionId", 1234, "eventId", "eventType", "codeSystemCd", "codeSystemDesc",
                "displayTxt", "answerTxt", "partTypeCd", "quesCodeSystemCd", "quesCodeSystemDesc",
                "quesDisplayTxt", "quesDisplayName", "ansToCode", "ansToCodeSystemCd", "ansToDisplayNm",
                "translationRequired", "ansToCodeSystemDesc"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(
                Arrays.asList(new Object[][]{ answerArray })

        );

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        List<EcrMsgCaseAnswerDto> result = target.fetchMsgInterviewAnswerRepeatForApplicableEcr(containerId, ixsLocalId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        EcrMsgCaseAnswerDto resultDto = result.get(0);
        assertEquals("questionId", resultDto.getQuestionIdentifier());
        assertEquals(Integer.valueOf(1234), resultDto.getMsgContainerUid());
        assertEquals("eventId", resultDto.getMsgEventId());
    }

    @Test
    void testFetchMsgTreatmentForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Integer containerId = 123;
        Object[] treatmentArray = {
                "localId", "authorId", "compositeCd", "commentTxt", "customTreatmentTxt", 123,
                "dosageUnitCd", "drugCd", 456, "durationUnitCd", new Timestamp(System.currentTimeMillis()),
                "frequencyAmtCd", "routeCd", new Timestamp(System.currentTimeMillis())
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(
                Arrays.asList(new Object[][]{ treatmentArray })

        );

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        List<EcrMsgTreatmentDto> result = target.fetchMsgTreatmentForApplicableEcr(containerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        EcrMsgTreatmentDto resultDto = result.get(0);
        assertEquals("localId", resultDto.getTrtLocalId());
        assertEquals("authorId", resultDto.getTrtAuthorId());
        assertEquals("compositeCd", resultDto.getTrtCompositeCd());
        assertEquals("commentTxt", resultDto.getTrtCommentTxt());
        assertEquals("customTreatmentTxt", resultDto.getTrtCustomTreatmentTxt());
        assertEquals(Integer.valueOf(123), resultDto.getTrtDosageAmt());
        assertEquals("dosageUnitCd", resultDto.getTrtDosageUnitCd());
        assertEquals("drugCd", resultDto.getTrtDrugCd());
        assertEquals(Integer.valueOf(456), resultDto.getTrtDurationAmt());
        assertEquals("durationUnitCd", resultDto.getTrtDurationUnitCd());
        assertNotNull(resultDto.getTrtEffectiveTime());
        assertNotNull(resultDto.getTrtTreatmentDt());
        assertEquals("frequencyAmtCd", resultDto.getTrtFrequencyAmtCd());
        assertEquals("routeCd", resultDto.getTrtRouteCd());

    }

    @Test
    void testFetchMsgTreatmentProviderForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Integer containerId = 123;
        Object[] providerArray = {
                "localId", "authorId", "city", "commentAddress", "countyCd", "countryCd", "streetAddr1",
                "streetAddr2", "stateCd", "zipCode", "commentTxt", "altIdNbrTxt", "quickCodeTxt",
                "idNbrTxt", "npiTxt", new Timestamp(System.currentTimeMillis()), "emailTxt", "degreeCd",
                "firstTxt", "lastTxt", "middleTxt", "prefixCd", "suffixCd", "phoneCommentTxt", "phoneCountryCode",
                123, "phoneNbr", "roleCd", "urlTxt"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(
                Arrays.asList(new Object[][]{ providerArray })
        );

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        List<EcrMsgProviderDto> result = target.fetchMsgTreatmentProviderForApplicableEcr(containerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        EcrMsgProviderDto resultDto = result.get(0);
        assertEquals("localId", resultDto.getPrvLocalId());
        assertEquals("authorId", resultDto.getPrvAuthorId());
        assertEquals("city", resultDto.getPrvAddrCityTxt());
        assertEquals("commentAddress", resultDto.getPrvAddrCommentTxt());
        assertEquals("countyCd", resultDto.getPrvAddrCountyCd());
        assertEquals("countryCd", resultDto.getPrvAddrCountryCd());
        assertEquals("streetAddr1", resultDto.getPrvAddrStreetAddr1Txt());
        assertEquals("streetAddr2", resultDto.getPrvAddrStreetAddr2Txt());
        assertEquals("stateCd", resultDto.getPrvAddrStateCd());
        assertEquals("zipCode", resultDto.getPrvAddrZipCodeTxt());
        assertEquals("commentTxt", resultDto.getPrvCommentTxt());
        assertEquals("altIdNbrTxt", resultDto.getPrvIdAltIdNbrTxt());
        assertEquals("quickCodeTxt", resultDto.getPrvIdQuickCodeTxt());
        testFetchMsgTreatmentProviderForApplicableEcrAssertion(resultDto);
    }

    private void testFetchMsgTreatmentProviderForApplicableEcrAssertion( EcrMsgProviderDto resultDto) {
        assertEquals("idNbrTxt", resultDto.getPrvIdNbrTxt());
        assertEquals("npiTxt", resultDto.getPrvIdNpiTxt());
        assertNotNull(resultDto.getPrvEffectiveTime());
        assertEquals("emailTxt", resultDto.getPrvEmailAddressTxt());
        assertEquals("degreeCd", resultDto.getPrvNameDegreeCd());
        assertEquals("firstTxt", resultDto.getPrvNameFirstTxt());
        assertEquals("lastTxt", resultDto.getPrvNameLastTxt());
        assertEquals("middleTxt", resultDto.getPrvNameMiddleTxt());
        assertEquals("prefixCd", resultDto.getPrvNamePrefixCd());
        assertEquals("suffixCd", resultDto.getPrvNameSuffixCd());
        assertEquals("phoneCommentTxt", resultDto.getPrvPhoneCommentTxt());
        assertEquals("phoneCountryCode", resultDto.getPrvPhoneCountryCodeTxt());
        assertEquals(Integer.valueOf(123), resultDto.getPrvPhoneExtensionTxt());
        assertEquals("phoneNbr", resultDto.getPrvPhoneNbrTxt());
        assertEquals("roleCd", resultDto.getPrvRoleCd());
        assertEquals("urlTxt", resultDto.getPrvUrlAddressTxt());
    }

    @Test
    void testFetchMsgTreatmentOrganizationForApplicableEcr() throws EcrCdaXmlException {
        // Given
        Integer containerId = 123;
        Object[] organizationArray = {
                "localId", "authorId", new Timestamp(System.currentTimeMillis()), "nameTxt", "cityTxt", "commentAddr",
                "countyCd", "countryCd", "stateCd", "streetAddr1", "streetAddr2", "zipCode", "classCd", "commentTxt",
                "emailTxt", "cliaNbr", "facilityId", "quickCodeTxt", "phoneComment", "phoneCountryCode", 123,
                "phoneNbr", "roleCd", "urlTxt"
        };

        Query mockQuery = mock(Query.class);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(
                Arrays.asList(new Object[][]{ organizationArray })
        );

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        // When
        List<EcrMsgOrganizationDto> result = target.fetchMsgTreatmentOrganizationForApplicableEcr(containerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        EcrMsgOrganizationDto resultDto = result.get(0);
        assertEquals("localId", resultDto.getOrgLocalId());
        assertEquals("authorId", resultDto.getOrgAuthorId());
        assertNotNull(resultDto.getOrgEffectiveTime());
        assertEquals("nameTxt", resultDto.getOrgNameTxt());
        assertEquals("cityTxt", resultDto.getOrgAddrCityTxt());
        assertEquals("commentAddr", resultDto.getOrgAddrCommentTxt());
        assertEquals("countyCd", resultDto.getOrgAddrCountyCd());
        assertEquals("countryCd", resultDto.getOrgAddrCountryCd());
        assertEquals("stateCd", resultDto.getOrgAddrStateCd());
        assertEquals("streetAddr1", resultDto.getOrgAddrStreetAddr1Txt());
        assertEquals("streetAddr2", resultDto.getOrgAddrStreetAddr2Txt());
        testFetchMsgTreatmentOrganizationForApplicableEcrAssertion(resultDto);
    }

    private void testFetchMsgTreatmentOrganizationForApplicableEcrAssertion(EcrMsgOrganizationDto resultDto ) {
        assertEquals("zipCode", resultDto.getOrgAddrZipCodeTxt());
        assertEquals("classCd", resultDto.getOrgClassCd());
        assertEquals("commentTxt", resultDto.getOrgCommentTxt());
        assertEquals("emailTxt", resultDto.getOrgEmailAddressTxt());
        assertEquals("cliaNbr", resultDto.getOrgIdCliaNbrTxt());
        assertEquals("facilityId", resultDto.getOrgIdFacilityIdentifierTxt());
        assertEquals("quickCodeTxt", resultDto.getOrgIdQuickCodeTxt());
        assertEquals("phoneComment", resultDto.getOrgPhoneCommentTxt());
        assertEquals("phoneCountryCode", resultDto.getOrgPhoneCountryCodeTxt());
        assertEquals(Integer.valueOf(123), resultDto.getOrgPhoneExtensionTxt());
        assertEquals("phoneNbr", resultDto.getOrgPhoneNbrTxt());
        assertEquals("roleCd", resultDto.getOrgRoleCd());
        assertEquals("urlTxt", resultDto.getOrgUrlAddressTxt());
    }

    @Test
    void testUpdateMatchEcrRecordForProcessing() throws EcrCdaXmlException {
        // Given
        Integer containerUid = 123;

        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery); // Mocking any SQL that could be loaded.
        when(mockQuery.setParameter("MSG_CONTAINER_UID", containerUid)).thenReturn(mockQuery);

        // When
        target.updateMatchEcrRecordForProcessing(containerUid);

        // Then
        verify(entityManager).createNativeQuery(anyString()); // Verify that a query was created.
        verify(mockQuery).setParameter("MSG_CONTAINER_UID", containerUid); // Verify that the parameter was set correctly.
        verify(mockQuery).executeUpdate(); // Verify that the update was executed.
    }


    @Test
    void fetchMsgCaseForApplicableEcrTest() throws Exception {
        // Arrange
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{
                "1", // InvLocalId
                "2", // PatLocalId
                "3", // InvAuthorId
                "4", // InvCaseStatusCd
                "5", // InvCloseDt
                "6", // InvCommentTxt
                "7", // InvConditionCd
                "8", // InvContactInvCommentTxt
                "9", // InvContactInvPriorityCd
                "10", // InvContactInvStatusCd
                "11", // InvCurrProcessStateCd
                "12", // InvDaycareIndCd
                "13", // InvDetectionMethodCd
                "14", // InvDiagnosisDt
                "15", // InvDiseaseAcquiredLocCd
                new Timestamp(System.currentTimeMillis()), // InvEffectiveTime
                "17", // InvFoodhandlerIndCd
                "18", // InvHospitalizedAdmitDt
                "19", // InvHospitalizedDischargeDt
                "20", // InvHospitalizedIndCd
                21, // InvHospStayDuration
                "22", // InvIllnessStartDt
                "23", // InvIllnessEndDt
                24, // InvIllnessDuration
                "25", // InvIllnessDurationUnitCd
                26, // InvIllnessOnsetAge
                "27", // InvIllnessOnsetAgeUnitCd
                "28", // InvInvestigatorAssignedDt
                "29", // InvImportCityTxt
                "30", // InvImportCountyCd
                "31", // InvImportCountryCd
                "32", // InvImportStateCd
                "33", // InvInfectiousFromDt
                "34", // InvInfectiousToDt
                "35", // InvLegacyCaseId
                "36", // InvMmwrWeekTxt
                "37", // InvMmwrYearTxt
                "38", // InvOutbreakIndCd
                "39", // InvOutbreakNameCd
                "40", // InvPatientDeathDt
                "41", // InvPatientDeathIndCd
                "42", // InvPregnancyIndCd
                "43", // InvReferralBasisCd
                "44", // InvReportDt
                "45", // InvReportToCountyDt
                "46", // InvReportToStateDt
                "47", // InvReportingCountyCd
                "48", // InvSharedIndCd
                "49", // InvSourceTypeCd
                "50", // InvStartDt
                "51", // InvStateId
                "52",  // InvStatusCd,
                "53"  // InvStatusCd
        });
        when(mockQuery.getResultList()).thenReturn(mockResults);

        Integer containerId = 1;

        // Act
        List<EcrMsgCaseDto> results = target.fetchMsgCaseForApplicableEcr(containerId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());

        EcrMsgCaseDto resultDto = results.get(0);

        assertEquals("1", resultDto.getInvLocalId());
    }
}
