package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.locator.PhysicalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.service.interfaces.person.INokMatchingService;
import gov.cdc.dataprocessing.service.interfaces.person.IProviderMatchingService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PersonServiceTest {
    @Mock
    private PatientMatchingService patientMatchingServiceMock;
    @Mock
    private INokMatchingService nokMatchingServiceMock;
    @Mock
    private IProviderMatchingService providerMatchingServiceMock;
    @Mock
    private IUidService uidServiceMock;
    @InjectMocks
    private PersonService personService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @AfterEach
    void tearDown() {
        Mockito.reset(patientMatchingServiceMock);
        Mockito.reset(nokMatchingServiceMock);
        Mockito.reset(providerMatchingServiceMock);
        Mockito.reset(uidServiceMock);
    }

    @Test
    void processingNextOfKin() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();

        PersonContainer personContainerResult = personService.processingNextOfKin(labResultProxyContainer, personContainer);
        assertNotNull(personContainerResult);
    }

    @Test
    void processingNextOfKin_personid_null() {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(null);
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        assertThrows(DataProcessingException.class, () -> personService.processingNextOfKin(labResultProxyContainer, personContainer));
    }

    @Test
    void processingPatient() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setFirstNm("TestFirstName");
        personNameDto.setLastNm("TestLastName");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setPatientUid(123L);
        PersonContainer personContainerResult = personService.processingPatient(labResultProxyContainer, edxLabInformationDto, personContainer);
        assertNotNull(personContainerResult);
    }

    @Test
    void processingPatient_with_personNameCollection() {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.setThePersonNameDtoCollection(new ArrayList<>());

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setPatientUid(123L);

        assertThrows(DataProcessingException.class, () -> personService.processingPatient(labResultProxyContainer, edxLabInformationDto, personContainer));
    }

    @Test
    void processingPatient_personid_zero() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(0L);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setFirstNm("TestFirstName");
        personNameDto.setLastNm("TestLastName");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setPatientUid(0L);
        PersonContainer personContainerResult = personService.processingPatient(labResultProxyContainer, edxLabInformationDto, personContainer);
        assertNotNull(personContainerResult);
    }

    @Test
    void processingPatient_personid_PatientMatchedFound() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setPersonParentUid(123L);
        personContainer.setPatientMatchedFound(true);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setFirstNm("TestFirstName");
        personNameDto.setLastNm("TestLastName");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setPatientUid(0L);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setPatientUid(0L);
        edxPatientMatchFoundDT.setMultipleMatch(false);

        when(patientMatchingServiceMock.getMatchingPatient(personContainer)).thenReturn(edxPatientMatchFoundDT);
        when(patientMatchingServiceMock.getMultipleMatchFound()).thenReturn(false);

        PersonContainer personContainerResult = personService.processingPatient(labResultProxyContainer, edxLabInformationDto, personContainer);
        assertNotNull(personContainerResult);
    }

    @Test
    void processingProvider() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(111L);
        personContainer.setRole("testrole");

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        boolean orderingProviderIndicator = false;

        EDXActivityDetailLogDto eDXActivityDetailLogDto = new EDXActivityDetailLogDto();
        eDXActivityDetailLogDto.setRecordId("123");

        when(providerMatchingServiceMock.getMatchingProvider(personContainer)).thenReturn(eDXActivityDetailLogDto);

        PersonContainer personContainerResult = personService.processingProvider(labResultProxyContainer, edxLabInformationDto, personContainer, orderingProviderIndicator);
        assertNull(personContainerResult);

        /// Role null
        personContainer.setRole(null);
        eDXActivityDetailLogDto.setRecordId(null);
        when(providerMatchingServiceMock.getMatchingProvider(personContainer)).thenReturn(eDXActivityDetailLogDto);
        PersonContainer personContainerResult1 = personService.processingProvider(labResultProxyContainer, edxLabInformationDto, personContainer, orderingProviderIndicator);
        assertNull(personContainerResult1);
    }

    @Test
    void processingProvider_with_role() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(111L);
        personContainer.setRole(EdxELRConstant.ELR_OP_CD);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        boolean orderingProviderIndicator = false;

        EDXActivityDetailLogDto eDXActivityDetailLogDto = new EDXActivityDetailLogDto();
        eDXActivityDetailLogDto.setRecordId(null);

        when(providerMatchingServiceMock.getMatchingProvider(personContainer)).thenReturn(eDXActivityDetailLogDto);
        PersonContainer personContainerResult = personService.processingProvider(labResultProxyContainer, edxLabInformationDto, personContainer, orderingProviderIndicator);
        assertNotNull(personContainerResult);
    }

    @Test
    void getMatchedPersonUID() {
        Collection<PersonContainer> personCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(111L);
        personContainer.getThePersonDto().setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
        personCollection.add(personContainer);

        LabResultProxyContainer matchedlabResultProxyVO = new LabResultProxyContainer();
        matchedlabResultProxyVO.setThePersonContainerCollection(personCollection);
        Long personUidAcutual = personService.getMatchedPersonUID(matchedlabResultProxyVO);
        assertEquals(111L, personUidAcutual);
    }

    @Test
    void getMatchedPersonUID_with_cdDescText_null() {
        Collection<PersonContainer> personCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(111L);
        personCollection.add(personContainer);

        LabResultProxyContainer matchedlabResultProxyVO = new LabResultProxyContainer();
        matchedlabResultProxyVO.setThePersonContainerCollection(personCollection);
        Long personUidAcutual = personService.getMatchedPersonUID(matchedlabResultProxyVO);
        assertNull(personUidAcutual);
    }

    @Test
    void updatePersonELRUpdate() {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        LabResultProxyContainer matchedLabResultProxyVO = new LabResultProxyContainer();

        //matchedLabResultProxyVO
        Collection<PersonContainer> personCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        personContainer.getThePersonDto().setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
        personContainer.getThePersonDto().setPersonUid(123L);
        personContainer.getThePersonDto().setPersonParentUid(234L);
        personContainer.getThePersonDto().setLocalId("111");
        personContainer.getThePersonDto().setVersionCtrlNbr(456);
        personCollection.add(personContainer);

        //person name
        PersonNameDto personNameDT = new PersonNameDto();
        personNameDT.setPersonNameSeq(1);
        personContainer.getThePersonNameDtoCollection().add(personNameDT);

        //person race
        PersonRaceDto personRaceDT = new PersonRaceDto();
        personRaceDT.setRaceCd("TEST_RACE_CD");
        personContainer.getThePersonRaceDtoCollection().add(personRaceDT);
        //person ethnic group
        PersonEthnicGroupDto personEthnicGroupDT = new PersonEthnicGroupDto();
        personEthnicGroupDT.setEthnicGroupCd("TEST_ETHNICGROUP_CD");
        personContainer.getThePersonEthnicGroupDtoCollection().add(personEthnicGroupDT);

        //Entity Id
        EntityIdDto entityIDDT = new EntityIdDto();
        entityIDDT.setEntityIdSeq(1);
        personContainer.getTheEntityIdDtoCollection().add(entityIDDT);

        //Entity Locator Participation
        EntityLocatorParticipationDto entityLocPartDT = new EntityLocatorParticipationDto();

        PostalLocatorDto thePostalLocatorDto = new PostalLocatorDto();
        thePostalLocatorDto.setPostalLocatorUid(111L);

        PhysicalLocatorDto thePhysicalLocatorDto = new PhysicalLocatorDto();
        thePhysicalLocatorDto.setPhysicalLocatorUid(222L);

        TeleLocatorDto theTeleLocatorDto = new TeleLocatorDto();
        theTeleLocatorDto.setTeleLocatorUid(333L);

        entityLocPartDT.setThePostalLocatorDto(thePostalLocatorDto);
        entityLocPartDT.setThePhysicalLocatorDto(thePhysicalLocatorDto);
        entityLocPartDT.setTheTeleLocatorDto(theTeleLocatorDto);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocPartDT);

        matchedLabResultProxyVO.setThePersonContainerCollection(personCollection);

        //labResultProxyVO
        Collection<PersonContainer> personCollection1 = new ArrayList<>();

        PersonContainer personContainer1 = new PersonContainer();
        personContainer1.getThePersonDto().setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
        personCollection1.add(personContainer1);
        //person name
        PersonNameDto personNameDT1 = new PersonNameDto();
        personNameDT1.setPersonNameSeq(1);
        personContainer1.getThePersonNameDtoCollection().add(personNameDT1);

        //person race
        PersonRaceDto personRaceDT1 = new PersonRaceDto();
        personRaceDT1.setRaceCd("TEST_RACE_CD");
        personContainer1.getThePersonRaceDtoCollection().add(personRaceDT1);

        //person ethnic group
        PersonEthnicGroupDto personEthnicGroupDT1 = new PersonEthnicGroupDto();
        personEthnicGroupDT1.setEthnicGroupCd("TEST_ETHNICGROUP_CD");
        personContainer1.getThePersonEthnicGroupDtoCollection().add(personEthnicGroupDT1);

        //Entity Id
        EntityIdDto entityIDDT1 = new EntityIdDto();
        entityIDDT1.setEntityIdSeq(1);
        personContainer1.getTheEntityIdDtoCollection().add(entityIDDT1);

        //Entity Locator Participation
        EntityLocatorParticipationDto entityLocPartDT1 = new EntityLocatorParticipationDto();

        PostalLocatorDto thePostalLocatorDto1 = new PostalLocatorDto();
        thePostalLocatorDto1.setPostalLocatorUid(111L);

        PhysicalLocatorDto thePhysicalLocatorDto1 = new PhysicalLocatorDto();
        thePhysicalLocatorDto1.setPhysicalLocatorUid(222L);

        TeleLocatorDto theTeleLocatorDto1 = new TeleLocatorDto();
        theTeleLocatorDto1.setTeleLocatorUid(333L);

        entityLocPartDT1.setThePostalLocatorDto(thePostalLocatorDto1);
        entityLocPartDT1.setThePhysicalLocatorDto(thePhysicalLocatorDto1);
        entityLocPartDT1.setTheTeleLocatorDto(theTeleLocatorDto1);
        personContainer1.getTheEntityLocatorParticipationDtoCollection().add(entityLocPartDT1);

        labResultProxyVO.setThePersonContainerCollection(personCollection1);

        personService.updatePersonELRUpdate(labResultProxyVO, matchedLabResultProxyVO);
        assertNotNull(labResultProxyVO);
    }

    @Test
    void updatePersonELRUpdate_with_empty_proxy_collections() {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        LabResultProxyContainer matchedLabResultProxyVO = new LabResultProxyContainer();

        //matchedLabResultProxyVO
        Collection<PersonContainer> personCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        personContainer.getThePersonDto().setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
        personContainer.getThePersonDto().setPersonUid(123L);
        personContainer.getThePersonDto().setPersonParentUid(234L);
        personContainer.getThePersonDto().setLocalId("111");
        personContainer.getThePersonDto().setVersionCtrlNbr(456);
        personCollection.add(personContainer);

        //person name
        PersonNameDto personNameDT = new PersonNameDto();
        personNameDT.setPersonNameSeq(1); //condition 2
        personContainer.getThePersonNameDtoCollection().add(personNameDT);

        //person race
        PersonRaceDto personRaceDT = new PersonRaceDto();
        personRaceDT.setRaceCd("TEST_RACE_CD");
        personContainer.getThePersonRaceDtoCollection().add(personRaceDT);
        //person ethnic group
        PersonEthnicGroupDto personEthnicGroupDT = new PersonEthnicGroupDto();
        personEthnicGroupDT.setEthnicGroupCd("TEST_ETHNICGROUP_CD");
        personContainer.getThePersonEthnicGroupDtoCollection().add(personEthnicGroupDT);

        //Entity Id
        EntityIdDto entityIDDT = new EntityIdDto();
        entityIDDT.setEntityIdSeq(1);
        personContainer.getTheEntityIdDtoCollection().add(entityIDDT);

        //Entity Locator Participation
        EntityLocatorParticipationDto entityLocPartDT = new EntityLocatorParticipationDto();

        PostalLocatorDto thePostalLocatorDto = new PostalLocatorDto();
        thePostalLocatorDto.setPostalLocatorUid(111L);

        PhysicalLocatorDto thePhysicalLocatorDto = new PhysicalLocatorDto();
        thePhysicalLocatorDto.setPhysicalLocatorUid(222L);

        TeleLocatorDto theTeleLocatorDto = new TeleLocatorDto();
        theTeleLocatorDto.setTeleLocatorUid(333L);

        entityLocPartDT.setThePostalLocatorDto(thePostalLocatorDto);
        entityLocPartDT.setThePhysicalLocatorDto(thePhysicalLocatorDto);
        entityLocPartDT.setTheTeleLocatorDto(theTeleLocatorDto);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entityLocPartDT);

        matchedLabResultProxyVO.setThePersonContainerCollection(personCollection);

        //labResultProxyVO
        Collection<PersonContainer> personCollection1 = new ArrayList<>();

        PersonContainer personContainer1 = new PersonContainer();
        personContainer1.getThePersonDto().setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
        personCollection1.add(personContainer1);
        //person name
        personContainer1.setThePersonNameDtoCollection(null);

        //Person race
        PersonRaceDto personRaceDT1 = new PersonRaceDto();
        personContainer1.getThePersonRaceDtoCollection().add(personRaceDT1);

        //person ethnic group
        PersonEthnicGroupDto personEthnicGroupDT1 = new PersonEthnicGroupDto();
        personContainer1.getThePersonEthnicGroupDtoCollection().add(personEthnicGroupDT1);

        //Entity Id
        personContainer1.setTheEntityIdDtoCollection(null);

        //Entity Locator Participation
        personContainer1.setTheEntityLocatorParticipationDtoCollection(null);

        labResultProxyVO.setThePersonContainerCollection(personCollection1);

        personService.updatePersonELRUpdate(labResultProxyVO, matchedLabResultProxyVO);
        assertNotNull(labResultProxyVO);
    }
}