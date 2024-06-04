package gov.cdc.dataprocessing.service.implementation.organization;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.matching.EdxEntityMatchRepository;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrganizationMatchingServiceTest {
    @Mock
    private EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    @Mock
    private EdxEntityMatchRepository edxEntityMatchRepositoryMock;
    @Mock
    private OrganizationRepositoryUtil organizationRepositoryUtilMock;

    @InjectMocks
    private OrganizationMatchingService organizationMatchingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtil);
        Mockito.reset(edxEntityMatchRepositoryMock);
        Mockito.reset(organizationRepositoryUtilMock);
    }

    @Test
    void getMatchingOrganization() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        organizationContainer.setLocalIdentifier("123");

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        edxEntityMatchDto.setEntityUid(123L);
        edxEntityMatchDto.setTypeCd(NEDSSConstant.ORGANIZATION_CLASS_CODE);
        edxEntityMatchDto.setMatchString("TEST_MATCHSTRING");

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
    }

    @Test
    void getMatchingOrganization_no_entitymatch_for_localid() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        organizationContainer.setLocalIdentifier("123");

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
    }

    @Test
    void getMatchingOrganization_for_setOrganization_throwExp() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        organizationContainer.setLocalIdentifier("123");

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);
        when(organizationRepositoryUtilMock.setOrganization(any(OrganizationContainer.class), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> organizationMatchingService.getMatchingOrganization(organizationContainer));
    }

    @Test
    void getMatchingOrganization_for_localid_throwExp() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        organizationContainer.setLocalIdentifier("123");

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));

        assertThrows(DataProcessingException.class, () -> organizationMatchingService.getMatchingOrganization(organizationContainer));
    }

    @Test
    void getMatchingOrganization_entitymatch_for_identifier() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        edxEntityMatchDto.setEntityUid(123L);
        edxEntityMatchDto.setTypeCd(NEDSSConstant.ORGANIZATION_CLASS_CODE);
        edxEntityMatchDto.setMatchString("TEST_MATCHSTRING");

        EntityIdDto entityIdDT = new EntityIdDto();
        entityIdDT.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        entityIdDT.setRootExtensionTxt("TEST_ROOT_EXTN");
        entityIdDT.setTypeCd("TEST_TYPE_CD");
        entityIdDT.setAssigningAuthorityCd("TEST_ASSIGNING_AUTHORITY_CD");
        entityIdDT.setAssigningAuthorityDescTxt("TEST_ASSIGNING_DESC_TXT");
        entityIdDT.setAssigningAuthorityIdType("TEST_ASSIGNING_ID_TYPE");

        organizationContainer.getTheEntityIdDtoCollection().add(entityIdDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
    }

    @Test
    void getMatchingOrganization_for_identifier_throwExp() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        edxEntityMatchDto.setEntityUid(123L);
        edxEntityMatchDto.setTypeCd(NEDSSConstant.ORGANIZATION_CLASS_CODE);
        edxEntityMatchDto.setMatchString("TEST_MATCHSTRING");

        EntityIdDto entityIdDT = new EntityIdDto();
        entityIdDT.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        entityIdDT.setRootExtensionTxt("TEST_ROOT_EXTN");
        entityIdDT.setTypeCd("TEST_TYPE_CD");
        entityIdDT.setAssigningAuthorityCd("TEST_ASSIGNING_AUTHORITY_CD");
        entityIdDT.setAssigningAuthorityDescTxt("TEST_ASSIGNING_DESC_TXT");
        entityIdDT.setAssigningAuthorityIdType("TEST_ASSIGNING_ID_TYPE");

        organizationContainer.getTheEntityIdDtoCollection().add(entityIdDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));

        assertThrows(DataProcessingException.class, () -> organizationMatchingService.getMatchingOrganization(organizationContainer));
    }

    @Test
    void getMatchingOrganization_entitymatch_for_identifier_coded() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        edxEntityMatchDto.setEntityUid(123L);
        edxEntityMatchDto.setTypeCd(NEDSSConstant.ORGANIZATION_CLASS_CODE);
        edxEntityMatchDto.setMatchString("TEST_MATCHSTRING");

        EntityIdDto entityIdDT = new EntityIdDto();
        entityIdDT.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        entityIdDT.setRootExtensionTxt("TEST_ROOT_EXTN");
        entityIdDT.setTypeCd("TEST_TYPE_CD");

        organizationContainer.getTheEntityIdDtoCollection().add(entityIdDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
    }

    @Test
    void getMatchingOrganization_entitymatch_for_identifier_throws_exception() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        edxEntityMatchDto.setEntityUid(123L);
        edxEntityMatchDto.setTypeCd(NEDSSConstant.ORGANIZATION_CLASS_CODE);
        edxEntityMatchDto.setMatchString("TEST_MATCHSTRING");

        EntityIdDto entityIdDT = new EntityIdDto();
        entityIdDT.setRootExtensionTxt("TEST_ROOT_EXTN");
        entityIdDT.setTypeCd("TEST_TYPE_CD");

        organizationContainer.getTheEntityIdDtoCollection().add(entityIdDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);
        assertThrows(DataProcessingException.class, () -> organizationMatchingService.getMatchingOrganization(organizationContainer));
    }

    @Test
    void getMatchingOrganization_entitymatch_for_identifier_throws_exception1() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
        edxEntityMatchDto.setEntityUid(123L);
        edxEntityMatchDto.setTypeCd(NEDSSConstant.ORGANIZATION_CLASS_CODE);
        edxEntityMatchDto.setMatchString("TEST_MATCHSTRING");

        EntityIdDto entityIdDT = new EntityIdDto();
        entityIdDT.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        entityIdDT.setRootExtensionTxt("TEST_ROOT_EXTN");
        entityIdDT.setTypeCd("TEST_TYPE_CD");
        entityIdDT.setAssigningAuthorityCd("TEST_ASSIGNING_AUTHORITY_CD");

        organizationContainer.getTheEntityIdDtoCollection().add(entityIdDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
    }

    @Test
    void getMatchingOrganization_no_entitymatch_for_identifier() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

        EntityIdDto entityIdDT = new EntityIdDto();
        entityIdDT.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        entityIdDT.setRootExtensionTxt("TEST_ROOT_EXTN");
        entityIdDT.setTypeCd("TEST_TYPE_CD");
        entityIdDT.setAssigningAuthorityCd("TEST_ASSIGNING_AUTHORITY_CD");
        entityIdDT.setAssigningAuthorityDescTxt("TEST_ASSIGNING_DESC_TXT");
        entityIdDT.setAssigningAuthorityIdType("TEST_ASSIGNING_ID_TYPE");

        organizationContainer.getTheEntityIdDtoCollection().add(entityIdDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
    }

    @Test
    void getMatchingOrganization_entitymatch_for_nameAddStr() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

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

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
    }

    @Test
    void getMatchingOrganization_no_entitymatch_for_nameAddStr() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

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

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        doNothing().when(edxPatientMatchRepositoryUtil).saveEdxEntityMatch(isA(EdxEntityMatchDto.class));

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
        verify(edxPatientMatchRepositoryUtil, times(1)).saveEdxEntityMatch(isA(EdxEntityMatchDto.class));
    }

    @Test
    void getMatchingOrganization_for_nameAddStr_throwExp() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

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

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));

        assertThrows(DataProcessingException.class, () -> organizationMatchingService.getMatchingOrganization(organizationContainer));
    }

    @Test
    void getMatchingOrganization_entitymatch_for_telephoneTxt() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        //Org name
        OrganizationNameDto organizationNameDto = new OrganizationNameDto();
        organizationNameDto.setNmUseCd(NEDSSConstant.LEGAL);
        organizationNameDto.setNmTxt("test_org_name");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(organizationNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);

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

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);
    }

    @Test
    void getMatchingOrganization_entitymatch_for_telephoneTxt_throwExp() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        //Org name
        OrganizationNameDto organizationNameDto = new OrganizationNameDto();
        organizationNameDto.setNmUseCd(NEDSSConstant.LEGAL);
        organizationNameDto.setNmTxt("test_org_name");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(organizationNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);

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

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));

        assertThrows(DataProcessingException.class, () -> organizationMatchingService.getMatchingOrganization(organizationContainer));
    }

    @Test
    void getMatchingOrganization_no_entitymatch_for_telephoneTxt() throws DataProcessingException {
        OrganizationContainer organizationContainer = new OrganizationContainer();
        //Org name
        OrganizationNameDto organizationNameDto = new OrganizationNameDto();
        organizationNameDto.setNmUseCd(NEDSSConstant.LEGAL);
        organizationNameDto.setNmTxt("test_org_name");

        Collection<OrganizationNameDto> theOrganizationNameDtoCollection = new ArrayList<>();
        theOrganizationNameDtoCollection.add(organizationNameDto);
        organizationContainer.setTheOrganizationNameDtoCollection(theOrganizationNameDtoCollection);

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");

        entLocPartDT.setTheTeleLocatorDto(teleLocDT);

        organizationContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenReturn(edxEntityMatchDto);

        doNothing().when(edxPatientMatchRepositoryUtil).saveEdxEntityMatch(isA(EdxEntityMatchDto.class));

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult = organizationMatchingService.getMatchingOrganization(organizationContainer);
        assertNotNull(edxActivityDetailLogDtoResult);

        verify(edxPatientMatchRepositoryUtil, times(1)).saveEdxEntityMatch(isA(EdxEntityMatchDto.class));
    }
}