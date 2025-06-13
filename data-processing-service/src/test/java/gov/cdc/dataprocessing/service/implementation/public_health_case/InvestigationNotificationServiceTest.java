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
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


} 