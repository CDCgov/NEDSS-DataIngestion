package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomNbsQuestionRepository;
import gov.cdc.dataprocessing.service.implementation.person.ProviderMatchingService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestigationNotificationServiceTest {

    @Mock
    private IInvestigationService investigationService;
    
    @Mock
    private INotificationService notificationService;
    
    @Mock
    private CustomNbsQuestionRepository customNbsQuestionRepository;
    
    @Mock
    private ICacheApiService cacheApiService;

    @InjectMocks
    private InvestigationNotificationService service;

    @BeforeEach
    void setUp() {
        service = new InvestigationNotificationService(
            investigationService,
            notificationService,
            customNbsQuestionRepository,
            cacheApiService
        );
    }

    @Test
    void testValidatePAMNotficationRequiredFieldsGivenPageProxy_WithNBSAnswer() throws DataProcessingException {
        // Arrange
        PamProxyContainer pageObj = new PamProxyContainer();
        BasePamContainer pamVO = new BasePamContainer();
        Map<Object, Object> answerMap = new HashMap<>();
        pamVO.setPamAnswerDTMap(answerMap);
        pageObj.setPamVO(pamVO);

        Map<Object, Object> reqFields = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setDataLocation("NBS_Answer.testField");
        metadata.setQuestionIdentifier("TEST_FIELD");
        metadata.setQuestionLabel("Test Field");
        reqFields.put(1L, metadata);

        // Act
        Map<Object, Object> result = service.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, 1L, reqFields, NEDSSConstant.INV_FORM_RVCT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Field", result.get("TEST_FIELD"));
    }

    @Test
    void testValidatePAMNotficationRequiredFieldsGivenPageProxy_WithPublicHealthCase() throws DataProcessingException {
        // Arrange
        PamProxyContainer pageObj = new PamProxyContainer();
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(1L);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        pageObj.setPublicHealthCaseContainer(phcContainer);

        Map<Object, Object> reqFields = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setDataLocation("public_health_case.cd");
        metadata.setQuestionIdentifier("PHC_CD");
        metadata.setQuestionLabel("PHC Code");
        reqFields.put(1L, metadata);

        // Act
        Map<Object, Object> result = service.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, 1L, reqFields, NEDSSConstant.INV_FORM_RVCT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PHC Code", result.get("PHC_CD"));
    }

    @Test
    void testValidatePAMNotficationRequiredFieldsGivenPageProxy_WithPerson() throws DataProcessingException {
        // Arrange
        PamProxyContainer pageObj = new PamProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personContainer.setThePersonDto(personDto);

        Collection<PersonContainer> personCollection = new ArrayList<>();
        personCollection.add(personContainer);

        Collection<ParticipationDto> participationCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd(NEDSSConstant.PHC_PATIENT);
        participation.setSubjectEntityUid(1L);
        participationCollection.add(participation);

        pageObj.setThePersonVOCollection(personCollection);
        pageObj.setTheParticipationDTCollection(participationCollection);

        Map<Object, Object> reqFields = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setDataLocation("person.firstNm");
        metadata.setQuestionIdentifier("FIRST_NAME");
        metadata.setQuestionLabel("First Name");
        reqFields.put(1L, metadata);

        // Act
        Map<Object, Object> result = service.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, 1L, reqFields, NEDSSConstant.INV_FORM_RVCT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("First Name", result.get("FIRST_NAME"));
    }

    @Test
    void testValidatePAMNotficationRequiredFieldsGivenPageProxy_WithPostalLocator() throws DataProcessingException {
        // Arrange
        PamProxyContainer pageObj = new PamProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personContainer.setThePersonDto(personDto);

        Collection<EntityLocatorParticipationDto> locatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto locator = new EntityLocatorParticipationDto();
        locator.setUseCd("HOME");
        PostalLocatorDto postalLocator = new PostalLocatorDto();
        locator.setThePostalLocatorDto(postalLocator);
        locatorCollection.add(locator);
        personContainer.setTheEntityLocatorParticipationDtoCollection(locatorCollection);

        Collection<PersonContainer> personCollection = new ArrayList<>();
        personCollection.add(personContainer);

        Collection<ParticipationDto> participationCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd(NEDSSConstant.PHC_PATIENT);
        participation.setSubjectEntityUid(1L);
        participationCollection.add(participation);

        pageObj.setThePersonVOCollection(personCollection);
        pageObj.setTheParticipationDTCollection(participationCollection);

        Map<Object, Object> reqFields = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setDataLocation("postal_locator.streetaddr1");
        metadata.setQuestionIdentifier("ADDRESS");
        metadata.setQuestionLabel("Address");
        metadata.setDataUseCd("HOME");
        reqFields.put(1L, metadata);

        // Act
        Map<Object, Object> result = service.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, 1L, reqFields, NEDSSConstant.INV_FORM_RVCT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Address", result.get("ADDRESS"));
    }

    @Test
    void testValidatePAMNotficationRequiredFieldsGivenPageProxy_WithPersonRace() throws DataProcessingException {
        // Arrange
        PamProxyContainer pageObj = new PamProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personContainer.setThePersonDto(personDto);

        Collection<PersonRaceDto> raceCollection = new ArrayList<>();
        PersonRaceDto race = new PersonRaceDto();
        raceCollection.add(race);
        personContainer.setThePersonRaceDtoCollection(raceCollection);

        Collection<PersonContainer> personCollection = new ArrayList<>();
        personCollection.add(personContainer);

        Collection<ParticipationDto> participationCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd(NEDSSConstant.PHC_PATIENT);
        participation.setSubjectEntityUid(1L);
        participationCollection.add(participation);

        pageObj.setThePersonVOCollection(personCollection);
        pageObj.setTheParticipationDTCollection(participationCollection);

        Map<Object, Object> reqFields = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setDataLocation("person_race.raceCd");
        metadata.setQuestionIdentifier("RACE");
        metadata.setQuestionLabel("Race");
        reqFields.put(1L, metadata);

        // Act
        Map<Object, Object> result = service.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, 1L, reqFields, NEDSSConstant.INV_FORM_RVCT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Race", result.get("RACE"));
    }

    @Test
    void testValidatePAMNotficationRequiredFieldsGivenPageProxy_WithActId() throws DataProcessingException {
        // Arrange
        PamProxyContainer pageObj = new PamProxyContainer();
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        Collection<ActIdDto> actIdCollection = new ArrayList<>();
        ActIdDto actId = new ActIdDto();
        actId.setTypeCd(NEDSSConstant.ACT_ID_STATE_TYPE_CD);
        actIdCollection.add(actId);
        phcContainer.setTheActIdDTCollection(actIdCollection);
        pageObj.setPublicHealthCaseContainer(phcContainer);

        Map<Object, Object> reqFields = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setDataLocation("act_id.rootExtensionTxt");
        metadata.setQuestionIdentifier("STATE");
        metadata.setQuestionLabel("State");
        reqFields.put(1L, metadata);

        // Act
        Map<Object, Object> result = service.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, 1L, reqFields, NEDSSConstant.INV_FORM_RVCT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("State", result.get("STATE"));
    }



    @Test
    void testValidatePAMNotficationRequiredFieldsGivenPageProxy_WithNoMissingFields() throws DataProcessingException {
        // Arrange
        PamProxyContainer pageObj = new PamProxyContainer();
        BasePamContainer pamVO = new BasePamContainer();
        Map<Object, Object> answerMap = new HashMap<>();
        answerMap.put(1L, "test value");
        pamVO.setPamAnswerDTMap(answerMap);
        pageObj.setPamVO(pamVO);

        Map<Object, Object> reqFields = new HashMap<>();
        NbsQuestionMetadata metadata = new NbsQuestionMetadata();
        metadata.setDataLocation("NBS_Answer.testField");
        metadata.setQuestionIdentifier("TEST_FIELD");
        metadata.setQuestionLabel("Test Field");
        reqFields.put(1L, metadata);

        // Act
        Map<Object, Object> result = service.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, 1L, reqFields, NEDSSConstant.INV_FORM_RVCT);

        // Assert
        assertNull(result);
    }

    @Test
    void validatePAM_shouldProcess_whenDLocationIsNotEmpty() throws Exception {
        // Arrange
        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setDataLocation("");  // Not empty
        meta.setQuestionLabel("Some label");

        Map<Object, Object> reqFields = new HashMap<>();
        reqFields.put(100L, meta);

        InvestigationNotificationService.ValidationContext mockContext = new InvestigationNotificationService.ValidationContext();
        mockContext.publicHealthCaseDto = new PublicHealthCaseDto();

        InvestigationNotificationService spyService = spy(service);

        doReturn(mockContext).when(spyService).buildValidationContext(any(), any(), anyString());

        // Act
        Map<Object, Object> result = spyService.validatePAMNotficationRequiredFieldsGivenPageProxy(
                new Object(), 123L, reqFields, "FORM_CD"
        );

        // Assert
        // should not be null if checkObject adds to missingFields, or null if nothing missing
        assertNull(result);
    }


    @Test
    void validatePAM_shouldThrow_whenReflectionFails() throws Exception {
        // Arrange
        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setDataLocation("person.badField");
        meta.setQuestionLabel("Label");

        Map<Object, Object> reqFields = new HashMap<>();
        reqFields.put(200L, meta);

        InvestigationNotificationService.ValidationContext mockContext = new InvestigationNotificationService.ValidationContext();
        mockContext.personDto = new PersonDto();

        InvestigationNotificationService spyService = spy(service);

        doReturn(mockContext).when(spyService).buildValidationContext(any(), any(), anyString());
        doThrow(new RuntimeException("reflection failure"))
                .when(spyService).reflectGet(any(), eq("person.badField"));

        // Act & Assert
        DataProcessingException ex = assertThrows(DataProcessingException.class, () -> {
            spyService.validatePAMNotficationRequiredFieldsGivenPageProxy(
                    new Object(), 123L, reqFields, "FORM_CD"
            );
        });

        assertTrue(ex.getMessage().contains("Validation error at: person.badField"));
        assertTrue(ex.getCause() instanceof RuntimeException);
    }


    @Test
    void validateNbsAnswer_shouldNotAddMissing_whenAnswerExists() {
        // Arrange
        Long key = 123L;
        Object answerValue = "someValue";
        NbsQuestionMetadata meta = mock(NbsQuestionMetadata.class);

        Map<Object, Object> answerMap = new HashMap<>();
        answerMap.put(key, answerValue);

        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();
        context.answerMap = answerMap;

        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);

        // Act
        spyService.validateNbsAnswer(context, key, meta, missingFields);

        // Assert: addMissing should NOT be called
        verify(spyService, never()).addMissing(any(), anyMap());
        assertTrue(missingFields.isEmpty());
    }

    @Test
    void validatePostalLocator_shouldCheckObjectWhenPersonVOAndCollectionIsNull() throws Exception {
        // Arrange
        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();
        context.personVO = null; // triggers the final "else"

        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setDataLocation("postal_locator.streetAddr1");

        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);

        doReturn("getStreetAddr1").when(spyService).createGetterMethod("streetAddr1");
        doReturn(PostalLocatorDto.class.getMethod("getStreetAddr1")).when(spyService).getMethod(eq(PostalLocatorDto.class), eq("getStreetAddr1"));
        doNothing().when(spyService).checkObject(null, missingFields, meta);

        // Act
        spyService.validatePostalLocator(context, "postal_locator.streetAddr1", meta, missingFields);

        // Assert
        verify(spyService).checkObject(null, missingFields, meta);
    }

    @Test
    void validatePostalLocator_shouldCheckObjectWhenPostalLocatorAndDataUseMatch() throws Exception {
        // Arrange
        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();

        PostalLocatorDto postalLocatorDto = new PostalLocatorDto();
        postalLocatorDto.setStreetAddr1("123 Main");

        EntityLocatorParticipationDto elp = new EntityLocatorParticipationDto();
        elp.setUseCd("WORK");
        elp.setThePostalLocatorDto(postalLocatorDto);

        PersonContainer personVO = new PersonContainer();
        personVO.setTheEntityLocatorParticipationDtoCollection(List.of(elp));
        context.personVO = personVO;

        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setDataLocation("postal_locator.streetAddr1");
        meta.setDataUseCd("WORK");

        Method getter = PostalLocatorDto.class.getMethod("getStreetAddr1");

        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);
        doReturn("getStreetAddr1").when(spyService).createGetterMethod("streetAddr1");
        doReturn(getter).when(spyService).getMethod(eq(PostalLocatorDto.class), eq("getStreetAddr1"));
        doNothing().when(spyService).checkObject(any(), any(), any());

        // Act
        spyService.validatePostalLocator(context, "postal_locator.streetAddr1", meta, missingFields);

        // Assert
        verify(spyService).checkObject(any(), eq(missingFields), eq(meta));
    }


    @Test
    void validatePersonRace_shouldCheckNull_whenPersonVOorCollectionIsNull() throws Exception {
        // Arrange
        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setDataLocation("person_race.raceCd");

        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();
        context.personVO = null; // triggers the else

        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);
        doReturn("getRaceCd").when(spyService).createGetterMethod("raceCd");
        doReturn(PersonRaceDto.class.getMethod("getRaceCd")).when(spyService).getMethod(eq(PersonRaceDto.class), eq("getRaceCd"));
        doNothing().when(spyService).checkObject(null, missingFields, meta);

        // Act
        spyService.validatePersonRace(context, "person_race.raceCd", meta, missingFields);

        // Assert
        verify(spyService).checkObject(null, missingFields, meta);
    }


    @Test
    void validateActId_shouldCheckObject_whenFormCdIsRvctAndLabelContainsState() throws Exception {
        ActIdDto actId = new ActIdDto();
        actId.setTypeCd("STATE");
        actId.setRootExtensionTxt("someValue");

        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();
        context.actIdColl = List.of(actId);

        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setQuestionLabel("State name");

        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);
        doReturn("getRootExtensionTxt").when(spyService).createGetterMethod("rootExtensionTxt");
        doReturn(ActIdDto.class.getMethod("getRootExtensionTxt")).when(spyService).getMethod(eq(ActIdDto.class), eq("getRootExtensionTxt"));
        doReturn("STATE").when(spyService).safe("STATE");
        doReturn("someValue").when(spyService).safe("someValue");

        doNothing().when(spyService).checkObject(any(), eq(missingFields), eq(meta));

        spyService.validateActId(context, "act_id.rootExtensionTxt", "State name", NEDSSConstant.INV_FORM_RVCT, meta, missingFields);

        verify(spyService).checkObject(any(), eq(missingFields), eq(meta));
    }


    @Test
    void validateActId_shouldAddMissing_whenActIdCollIsNullAndFormCdRvctAndLabelHasState() throws Exception {
        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();

        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setQuestionLabel("State Name");

        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);
        doNothing().when(spyService).addMissing(meta, missingFields);

        spyService.validateActId(context, "act_id.rootExtensionTxt", "State Name", NEDSSConstant.INV_FORM_RVCT, meta, missingFields);

        verify(spyService).addMissing(meta, missingFields);
    }

    @Test
    void validateActId_shouldNotCheckObject_whenShouldValidateIsFalse() throws Exception {
        ActIdDto actId = new ActIdDto();
        actId.setTypeCd("OTHER");
        actId.setRootExtensionTxt("value");

        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();
        context.actIdColl = List.of(actId);

        NbsQuestionMetadata meta = new NbsQuestionMetadata();
        meta.setQuestionLabel("Zip Code");

        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);
        doReturn("getRootExtensionTxt").when(spyService).createGetterMethod("rootExtensionTxt");
        doReturn("OTHER").when(spyService).safe("OTHER");
        doReturn("value").when(spyService).safe("value");

        spyService.validateActId(context, "act_id.rootExtensionTxt", "Zip Code", "ANY_FORM", meta, missingFields);

        // No validation should occur
        verify(spyService, never()).checkObject(any(), eq(missingFields), eq(meta));
        verify(spyService, never()).addMissing(any(), any());
    }

    @Test
    void validateNbsCaseAnswer_shouldAddMissing_whenAnswerMapIsNull() {
        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();
        context.answerMap = null;

        NbsQuestionMetadata metaData = mock(NbsQuestionMetadata.class);
        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);
        doNothing().when(spyService).addMissing(metaData, missingFields);

        spyService.validateNbsCaseAnswer(context, 101L, metaData, missingFields);

        verify(spyService).addMissing(metaData, missingFields);
    }

    @Test
    void validateNbsCaseAnswer_shouldAddMissing_whenAnswerMapIsEmpty() {
        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();
        context.answerMap = new HashMap<>();

        NbsQuestionMetadata metaData = mock(NbsQuestionMetadata.class);
        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);
        doNothing().when(spyService).addMissing(metaData, missingFields);

        spyService.validateNbsCaseAnswer(context, 101L, metaData, missingFields);

        verify(spyService).addMissing(metaData, missingFields);
    }


    @Test
    void validateNbsCaseAnswer_shouldAddMissing_whenBothKeysMissing() {
        InvestigationNotificationService.ValidationContext context = new InvestigationNotificationService.ValidationContext();
        context.answerMap = new HashMap<>();

        NbsQuestionMetadata metaData = mock(NbsQuestionMetadata.class);
//        when(metaData.getQuestionIdentifier()).thenReturn("QID_123");

        Map<Object, Object> missingFields = spy(new HashMap<>());

        InvestigationNotificationService spyService = spy(service);
        doNothing().when(spyService).addMissing(metaData, missingFields);

        spyService.validateNbsCaseAnswer(context, 200L, metaData, missingFields);

        verify(spyService).addMissing(metaData, missingFields);
    }


} 