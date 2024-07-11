package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.service.implementation.auth_user.AuthUserService;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProviderMatchingBaseServiceTest {
    @InjectMocks
    private ProviderMatchingBaseService providerMatchingBaseService;

    @Mock
    private EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private CachingValueService cachingValueService;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testProcessingProvider_ValidData() throws Exception {
        // Mock the PersonContainer and PersonDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonDto personDto = mock(PersonDto.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personDto.isItNew()).thenReturn(true);
        when(personDto.getElectronicInd()).thenReturn("Y");
        when(personDto.isCaseInd()).thenReturn(false);
        when(personDto.getPersonUid()).thenReturn(1L);

        // Use a spy to mock the private method
        ProviderMatchingBaseService spyService = spy(providerMatchingBaseService);

        // Mock the private persistingProvider method
        doReturn(1L).when(spyService).persistingProvider(any(PersonContainer.class), anyString(), anyString());

        // Call the method under test
        Long result = spyService.processingProvider(personContainer, "PROVIDER", "BUSINESS_TRIGGER");

        // Verify interactions and results
        assertEquals(1L, result);
    }

    @Test
    void testProcessingProvider_ExceptionHandling() throws Exception {
        // Mock the PersonContainer and PersonDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonDto personDto = mock(PersonDto.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personDto.isItNew()).thenReturn(true);
        when(personDto.getElectronicInd()).thenReturn("Y");
        when(personDto.isCaseInd()).thenReturn(false);

        // Use a spy to mock the private method
        ProviderMatchingBaseService spyService = spy(providerMatchingBaseService);

        // Mock the private persistingProvider method to throw an exception
        doThrow(new RuntimeException("Error")).when(spyService).persistingProvider(any(PersonContainer.class), anyString(), anyString());

        // Call the method under test and verify exception is thrown
        assertThrows(DataProcessingException.class, () -> spyService.processingProvider(personContainer, "PROVIDER", "BUSINESS_TRIGGER"));
    }

    @Test
    void testProcessingProvider_CallOrgHashCodeTrue() throws Exception {
        // Mock the PersonContainer and PersonDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonDto personDto = mock(PersonDto.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personDto.isItNew()).thenReturn(true);
        when(personDto.getElectronicInd()).thenReturn("Y");
        when(personDto.isCaseInd()).thenReturn(false);
        when(personDto.getPersonUid()).thenReturn(1L);
        when(personContainer.isItNew()).thenReturn(true);

        // Use a spy to mock the private methods
        ProviderMatchingBaseService spyService = spy(providerMatchingBaseService);

        // Mock the private persistingProvider method
        doReturn(1L).when(spyService).persistingProvider(any(PersonContainer.class), anyString(), anyString());

        // Mock the private setProvidertoEntityMatch method
        doNothing().when(spyService).setProvidertoEntityMatch(any(PersonContainer.class));

        // Call the method under test
        Long result = spyService.processingProvider(personContainer, "PROVIDER", "BUSINESS_TRIGGER");

        // Verify interactions and results
        assertEquals(1L, result);
        verify(personDto).setEdxInd("Y");
        verify(personDto).setPersonUid(1L);
        verify(spyService, times(1)).setProvidertoEntityMatch(personContainer);
    }

    @Test
    void testProcessingProvider_CallOrgHashCodeTrue_Exception() throws Exception {
        // Mock the PersonContainer and PersonDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonDto personDto = mock(PersonDto.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personDto.isItNew()).thenReturn(true);
        when(personDto.getElectronicInd()).thenReturn("Y");
        when(personDto.isCaseInd()).thenReturn(false);
        when(personDto.getPersonUid()).thenReturn(1L);
        when(personContainer.isItNew()).thenReturn(true);

        // Use a spy to mock the private methods
        ProviderMatchingBaseService spyService = spy(providerMatchingBaseService);

        // Mock the private persistingProvider method
        doReturn(1L).when(spyService).persistingProvider(any(PersonContainer.class), anyString(), anyString());

        // Mock the private setProvidertoEntityMatch method to throw an exception
        doThrow(new RuntimeException("Error")).when(spyService).setProvidertoEntityMatch(any(PersonContainer.class));

        // Call the method under test and verify exception is thrown
        assertThrows(DataProcessingException.class, () -> spyService.processingProvider(personContainer, "PROVIDER", "BUSINESS_TRIGGER"));

        // Verify interactions
        verify(personDto).setEdxInd("Y");
        verify(personDto).setPersonUid(1L);
        verify(spyService, times(1)).setProvidertoEntityMatch(personContainer);
    }

    @Test
    void testPersistingProvider_NewPerson() throws Exception {
        // Mock the PersonContainer and PersonDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonDto personDto = mock(PersonDto.class);
        Person person = mock(Person.class);

        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.isItNew()).thenReturn(true);
        when(person.getPersonUid()).thenReturn(1L);

        // Mock the PatientRepositoryUtil
        when(patientRepositoryUtil.createPerson(any(PersonContainer.class))).thenReturn(person);

        // Call the method under test
        Long result = providerMatchingBaseService.persistingProvider(personContainer, "PROVIDER", "BUSINESS_TRIGGER");

        // Verify interactions and results
        assertEquals(1L, result);
        verify(patientRepositoryUtil, times(1)).createPerson(personContainer);
    }

    @Test
    void testSetProvidertoEntityMatch_Success() throws Exception {
        // Mock the PersonContainer and PersonDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonDto personDto = mock(PersonDto.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personDto.getPersonUid()).thenReturn(1L);

        // Mock the methods called within setProvidertoEntityMatch
        List<String> identifierList = new ArrayList<>();
        identifierList.add("identifier1");
        identifierList.add("identifier2");

        ProviderMatchingBaseService spyService = spy(providerMatchingBaseService);
        doReturn(identifierList).when(spyService).getIdentifierForProvider(any(PersonContainer.class));
        doReturn("address").when(spyService).nameAddressStreetOneProvider(any(PersonContainer.class));
        doReturn("telephone").when(spyService).telePhoneTxtProvider(any(PersonContainer.class));

        // Call the method under test
        spyService.setProvidertoEntityMatch(personContainer);

        // Verify interactions and results
        verify(edxPatientMatchRepositoryUtil, times(4)).saveEdxEntityMatch(any(EdxEntityMatchDto.class));
        verify(patientRepositoryUtil, times(1)).updateExistingPersonEdxIndByUid(anyLong());
    }


    @Test
    void testSetProvidertoEntityMatch_SaveEdxEntityMatchException_First() throws Exception {
        // Mock the PersonContainer and PersonDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonDto personDto = mock(PersonDto.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personDto.getPersonUid()).thenReturn(1L);

        // Mock the methods called within setProvidertoEntityMatch
        List<String> identifierList = new ArrayList<>();
        identifierList.add("identifier1");

        ProviderMatchingBaseService spyService = spy(providerMatchingBaseService);
        doReturn(identifierList).when(spyService).getIdentifierForProvider(any(PersonContainer.class));
        doReturn("address").when(spyService).nameAddressStreetOneProvider(any(PersonContainer.class));
        doReturn("telephone").when(spyService).telePhoneTxtProvider(any(PersonContainer.class));

        // Mock the saveEdxEntityMatch method to throw an exception
        doThrow(new RuntimeException("Error")).when(edxPatientMatchRepositoryUtil).saveEdxEntityMatch(any(EdxEntityMatchDto.class));

        // Call the method under test and verify exception is thrown
        assertThrows(DataProcessingException.class, () -> spyService.setProvidertoEntityMatch(personContainer));

        // Verify interactions
        verify(edxPatientMatchRepositoryUtil, times(1)).saveEdxEntityMatch(any(EdxEntityMatchDto.class));
    }

    @Test
    void testSetProvidertoEntityMatch_SaveEdxEntityMatchException_Second() throws Exception {
        // Mock the PersonContainer and PersonDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonDto personDto = mock(PersonDto.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personDto.getPersonUid()).thenReturn(1L);

        // Mock the methods called within setProvidertoEntityMatch
        List<String> identifierList = new ArrayList<>();
        identifierList.add("identifier1");

        ProviderMatchingBaseService spyService = spy(providerMatchingBaseService);
        doReturn(identifierList).when(spyService).getIdentifierForProvider(any(PersonContainer.class));
        doReturn("address").when(spyService).nameAddressStreetOneProvider(any(PersonContainer.class));
        doReturn("telephone").when(spyService).telePhoneTxtProvider(any(PersonContainer.class));

        // Mock the saveEdxEntityMatch method to throw an exception on the second call
        doNothing().doThrow(new RuntimeException("Error")).when(edxPatientMatchRepositoryUtil).saveEdxEntityMatch(any(EdxEntityMatchDto.class));

        // Call the method under test and verify exception is thrown
        assertThrows(DataProcessingException.class, () -> spyService.setProvidertoEntityMatch(personContainer));

        // Verify interactions
        verify(edxPatientMatchRepositoryUtil, times(2)).saveEdxEntityMatch(any(EdxEntityMatchDto.class));
    }



    @Test
    void testGetIdentifierForProvider_FullIdentifiers() throws DataProcessingException {
        // Mock the PersonContainer and EntityIdDto
        PersonContainer personContainer = mock(PersonContainer.class);
        EntityIdDto entityIdDto1 = mock(EntityIdDto.class);
        EntityIdDto entityIdDto2 = mock(EntityIdDto.class);

        Collection<EntityIdDto> entityIdDtoCollection = new ArrayList<>();
        entityIdDtoCollection.add(entityIdDto1);
        entityIdDtoCollection.add(entityIdDto2);

        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(entityIdDtoCollection);
        when(entityIdDto1.getStatusCd()).thenReturn(NEDSSConstant.STATUS_ACTIVE);
        when(entityIdDto1.getRootExtensionTxt()).thenReturn("root1");
        when(entityIdDto1.getTypeCd()).thenReturn("type1");
        when(entityIdDto1.getAssigningAuthorityCd()).thenReturn("auth1");
        when(entityIdDto1.getAssigningAuthorityDescTxt()).thenReturn("desc1");
        when(entityIdDto1.getAssigningAuthorityIdType()).thenReturn("idType1");
        when(entityIdDto2.getStatusCd()).thenReturn(NEDSSConstant.STATUS_ACTIVE);
        when(entityIdDto2.getRootExtensionTxt()).thenReturn("root2");
        when(entityIdDto2.getTypeCd()).thenReturn("type2");
        when(entityIdDto2.getAssigningAuthorityCd()).thenReturn("auth2");
        when(entityIdDto2.getAssigningAuthorityDescTxt()).thenReturn("desc2");
        when(entityIdDto2.getAssigningAuthorityIdType()).thenReturn("idType2");

        // Call the method under test
        List<String> result = providerMatchingBaseService.getIdentifierForProvider(personContainer);

        // Verify interactions and results
        assertEquals(2, result.size());
        assertTrue(result.contains("root1^type1^auth1^desc1^idType1"));
        assertTrue(result.contains("root2^type2^auth2^desc2^idType2"));
    }


    @Test
    void testGetIdentifierForProvider_PartialIdentifiers() throws DataProcessingException {
        // Mock the PersonContainer and EntityIdDto
        PersonContainer personContainer = mock(PersonContainer.class);
        EntityIdDto entityIdDto1 = mock(EntityIdDto.class);
        EntityIdDto entityIdDto2 = mock(EntityIdDto.class);

        Collection<EntityIdDto> entityIdDtoCollection = new ArrayList<>();
        entityIdDtoCollection.add(entityIdDto1);
        entityIdDtoCollection.add(entityIdDto2);

        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(entityIdDtoCollection);
        when(entityIdDto1.getStatusCd()).thenReturn(NEDSSConstant.STATUS_ACTIVE);
        when(entityIdDto1.getRootExtensionTxt()).thenReturn("root1");
        when(entityIdDto1.getTypeCd()).thenReturn("type1");
        when(entityIdDto1.getAssigningAuthorityCd()).thenReturn("auth1");
        when(entityIdDto1.getAssigningAuthorityDescTxt()).thenReturn(null); // Partial
        when(entityIdDto1.getAssigningAuthorityIdType()).thenReturn("idType1");
        when(entityIdDto2.getStatusCd()).thenReturn(NEDSSConstant.STATUS_ACTIVE);
        when(entityIdDto2.getRootExtensionTxt()).thenReturn("root2");
        when(entityIdDto2.getTypeCd()).thenReturn("type2");
        when(entityIdDto2.getAssigningAuthorityCd()).thenReturn("auth2");
        when(entityIdDto2.getAssigningAuthorityDescTxt()).thenReturn("desc2");
        when(entityIdDto2.getAssigningAuthorityIdType()).thenReturn("idType2");

        // Mock Coded to simulate code value lookup
        Coded coded = mock(Coded.class);
        when(coded.getCode()).thenReturn("auth1");
        when(coded.getCodeDescription()).thenReturn("desc1");
        when(coded.getCodeSystemCd()).thenReturn("sysCd1");
     //    when(cachingValueService.findCodeValuesByCodeSetNmAndCode(anyString(), anyString())).thenReturn(List.of(coded));

        // Call the method under test
        List<String> result = providerMatchingBaseService.getIdentifierForProvider(personContainer);

        // Verify interactions and results
        assertEquals(1, result.size());
        assertTrue(result.contains("root2^type2^auth2^desc2^idType2"));
    }


    @Test
    void testGetIdentifierForProvider_ExceptionHandling() throws DataProcessingException {
        // Mock the PersonContainer and EntityIdDto
        PersonContainer personContainer = mock(PersonContainer.class);
        EntityIdDto entityIdDto1 = mock(EntityIdDto.class);

        Collection<EntityIdDto> entityIdDtoCollection = new ArrayList<>();
        entityIdDtoCollection.add(entityIdDto1);

        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(entityIdDtoCollection);
        when(entityIdDto1.getStatusCd()).thenReturn(NEDSSConstant.STATUS_ACTIVE);
        when(entityIdDto1.getRootExtensionTxt()).thenReturn("root1");
        when(entityIdDto1.getTypeCd()).thenReturn("type1");
        when(entityIdDto1.getAssigningAuthorityCd()).thenReturn("auth1");
        when(entityIdDto1.getAssigningAuthorityDescTxt()).thenReturn(null); // Partial
        when(entityIdDto1.getAssigningAuthorityIdType()).thenReturn("idType1");

        // Mock Coded to simulate code value lookup
        Coded coded = mock(Coded.class);
        when(coded.getCode()).thenReturn("auth1");
        when(coded.getCodeDescription()).thenReturn("desc1");
        when(coded.getCodeSystemCd()).thenThrow(new RuntimeException("Error"));


        // Call the method under test and verify exception is thrown
       var res =  providerMatchingBaseService.getIdentifierForProvider(personContainer);
        assertNotNull(res);
    }


    @Test
    void testGetIdentifierForProvider_ExceptionHandling_2() throws DataProcessingException {
        // Mock the PersonContainer and EntityIdDto
        PersonContainer personContainer = mock(PersonContainer.class);
        EntityIdDto entityIdDto1 = mock(EntityIdDto.class);


        when(personContainer.getTheEntityIdDtoCollection()).thenThrow(new RuntimeException("TEST"));
        when(entityIdDto1.getStatusCd()).thenReturn(NEDSSConstant.STATUS_ACTIVE);
        when(entityIdDto1.getRootExtensionTxt()).thenReturn("root1");
        when(entityIdDto1.getTypeCd()).thenReturn("type1");
        when(entityIdDto1.getAssigningAuthorityCd()).thenReturn("auth1");
        when(entityIdDto1.getAssigningAuthorityDescTxt()).thenReturn(null); // Partial
        when(entityIdDto1.getAssigningAuthorityIdType()).thenThrow(new RuntimeException("TEST"));

        // Mock Coded to simulate code value lookup
        Coded coded = mock(Coded.class);
        when(coded.getCode()).thenReturn("auth1");
        when(coded.getCodeDescription()).thenReturn("desc1");
        when(coded.getCodeSystemCd()).thenReturn("sysCd1");


        // Call the method under test and verify exception is thrown
        assertThrows(DataProcessingException.class, () -> providerMatchingBaseService.getIdentifierForProvider(personContainer));
    }


    @Test
    void testGetNameStringForProvider_Success() {
        // Mock the PersonContainer and PersonNameDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonNameDto personNameDto1 = mock(PersonNameDto.class);
        PersonNameDto personNameDto2 = mock(PersonNameDto.class);

        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();
        personNameDtoCollection.add(personNameDto1);
        personNameDtoCollection.add(personNameDto2);

        when(personContainer.getThePersonNameDtoCollection()).thenReturn(personNameDtoCollection);
        when(personNameDto1.getNmUseCd()).thenReturn(NEDSSConstant.LEGAL);
        when(personNameDto1.getLastNm()).thenReturn("Doe");
        when(personNameDto1.getFirstNm()).thenReturn("John");
        when(personNameDto2.getNmUseCd()).thenReturn(NEDSSConstant.LEGAL);
        when(personNameDto2.getLastNm()).thenReturn("Smith");
        when(personNameDto2.getFirstNm()).thenReturn("Jane");

        // Call the method under test
        String result = providerMatchingBaseService.getNameStringForProvider(personContainer);

        // Verify the result
        assertEquals("SmithJane", result);  // Only the first matching name is returned
    }

    @Test
    void testGetNameStringForProvider_NullNmUseCd() {
        // Mock the PersonContainer and PersonNameDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonNameDto personNameDto1 = mock(PersonNameDto.class);

        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();
        personNameDtoCollection.add(personNameDto1);

        when(personContainer.getThePersonNameDtoCollection()).thenReturn(personNameDtoCollection);
        when(personNameDto1.getNmUseCd()).thenReturn(null);

        // Call the method under test
        String result = providerMatchingBaseService.getNameStringForProvider(personContainer);

        // Verify the result
        assertNull(result);
    }


    @Test
    void testGetNameStringForProvider_NoMatchingNmUseCd() {
        // Mock the PersonContainer and PersonNameDto
        PersonContainer personContainer = mock(PersonContainer.class);
        PersonNameDto personNameDto1 = mock(PersonNameDto.class);

        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();
        personNameDtoCollection.add(personNameDto1);

        when(personContainer.getThePersonNameDtoCollection()).thenReturn(personNameDtoCollection);
        when(personNameDto1.getNmUseCd()).thenReturn("OTHER");

        // Call the method under test
        String result = providerMatchingBaseService.getNameStringForProvider(personContainer);

        // Verify the result
        assertNull(result);
    }

    @Test
    void testGetNameStringForProvider_EmptyCollection() {
        // Mock the PersonContainer
        PersonContainer personContainer = mock(PersonContainer.class);

        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();

        when(personContainer.getThePersonNameDtoCollection()).thenReturn(personNameDtoCollection);

        // Call the method under test
        String result = providerMatchingBaseService.getNameStringForProvider(personContainer);

        // Verify the result
        assertNull(result);
    }

    @Test
    void testGetNameStringForProvider_NullCollection() {
        // Mock the PersonContainer
        PersonContainer personContainer = mock(PersonContainer.class);

        when(personContainer.getThePersonNameDtoCollection()).thenReturn(null);

        // Call the method under test
        String result = providerMatchingBaseService.getNameStringForProvider(personContainer);

        // Verify the result
        assertNull(result);
    }


}
