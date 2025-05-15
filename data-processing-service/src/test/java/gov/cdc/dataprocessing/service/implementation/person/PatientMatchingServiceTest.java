package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.person.matching.DeduplicationService;
import gov.cdc.dataprocessing.service.implementation.person.matching.MatchResponse;
import gov.cdc.dataprocessing.service.implementation.person.matching.MatchResponse.MatchType;
import gov.cdc.dataprocessing.service.implementation.person.matching.PersonMatchRequest;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.ObjectProvider;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PatientMatchingServiceTest {

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
    @Mock
    private ObjectProvider<DeduplicationService> serviceProvider;
    @Mock
    private DeduplicationService deduplicationService;

    private PatientMatchingService patientMatchingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(serviceProvider.getIfAvailable()).thenReturn(null);
        patientMatchingService = new PatientMatchingService(
                edxPatientMatchRepositoryUtil,
                entityHelper,
                patientRepositoryUtil,
                cachingValueService,
                prepareAssocModelHelper,
                false,
                serviceProvider);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtil);
        Mockito.reset(entityHelper);
        Mockito.reset(patientRepositoryUtil);
        Mockito.reset(cachingValueService);
        Mockito.reset(prepareAssocModelHelper);
    }

    @Test
    void shouldReturnNullIfRoleIsNotPat() throws DataProcessingException {
        PersonContainer container = new PersonContainer();
        container.setRole("NonPat");
        EdxPatientMatchDto matchDto = patientMatchingService.getMatchingPatient(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void shouldPerformModernizedMatching() throws DataProcessingException {
        when(serviceProvider.getIfAvailable()).thenReturn(deduplicationService);
        patientMatchingService = new PatientMatchingService(
                edxPatientMatchRepositoryUtil,
                entityHelper,
                patientRepositoryUtil,
                cachingValueService,
                prepareAssocModelHelper,
                true,
                serviceProvider);
        when(deduplicationService.match(Mockito.any(PersonMatchRequest.class))).thenReturn(new MatchResponse(
                1l,
                MatchType.EXACT));

        PersonContainer mpr = new PersonContainer();
        mpr.getThePersonDto().setLocalId("4444");
        mpr.setExt(true);
        mpr.setItNew(false);
        mpr.setItDirty(false);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(mpr);

        PersonContainer container = new PersonContainer();
        container.setRole("");
        patientMatchingService.getMatchingPatient(container);
        verify(deduplicationService, times(1)).match(Mockito.any());
    }

    @Test
    void modernizedMatchingThrowsException() {
      when(serviceProvider.getIfAvailable()).thenReturn(deduplicationService);
        patientMatchingService = new PatientMatchingService(
                edxPatientMatchRepositoryUtil,
                entityHelper,
                patientRepositoryUtil,
                cachingValueService,
                prepareAssocModelHelper,
                true,
                serviceProvider);
        when(deduplicationService.match(Mockito.any(PersonMatchRequest.class)))
                .thenReturn(null);
        PersonContainer container = new PersonContainer();
        container.setRole("PAT");
        DataProcessingException exception = assertThrows(
                DataProcessingException.class,
                () -> patientMatchingService.getMatchingPatient(container));
        assertThat(exception.getMessage()).isEqualTo("Null response returned from deduplication service");
        verify(deduplicationService, times(1)).match(Mockito.any());
    }

    @Test
    void tryMatchByLocalId_noLocalId() throws DataProcessingException {
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(null, null)).thenReturn(null);
        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByLocalId(new PersonContainer());
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByLocalId_LocalId() throws DataProcessingException {
        PersonContainer container = new PersonContainer();
        container.setLocalIdentifier("localId");
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(null, "LOCALID")).thenReturn(null);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByLocalId(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByLocalId_multiMatch() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setMultipleMatch(true);

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(null, null)).thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByLocalId(new PersonContainer());
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByLocalId_nullPatientUid() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(null);

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(null, null)).thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByLocalId(new PersonContainer());
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByLocalId_zeroPatientUid() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(0L);

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(null, null)).thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByLocalId(new PersonContainer());
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByLocalId_validPatientUid() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(1L);

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(null, null)).thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByLocalId(new PersonContainer());
        assertThat(matchDto).isNotNull();
        assertThat(matchDto.getPatientUid()).isEqualTo(1L);

    }

    @Test
    void tryMatchByIdentifier_noIdentifier() throws DataProcessingException {
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(null, null)).thenReturn(null);
        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByIdentifier(new PersonContainer());
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByIdentifier_null() throws DataProcessingException {
        PersonContainer container = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd("PAT");
        container.setThePersonDto(personDto);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        EntityIdDto id1 = new EntityIdDto();
        id1.setStatusCd("A");
        id1.setRecordStatusCd("ACTIVE");
        id1.setAssigningAuthorityIdType("IDType");
        id1.setTypeCd("SS");
        id1.setRootExtensionTxt("ssn-value");
        id1.setAssigningAuthorityCd("SSA");
        id1.setAssigningAuthorityDescTxt("Social Security Administration");

        container.setTheEntityIdDtoCollection(List.of(id1));
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                null,
                "SSN-VALUE^SS^SSA^SOCIAL SECURITY ADMINISTRATION^IDTYPE^LASTNAME^FIRSTNAME"))
                .thenReturn(null);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByIdentifier(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByIdentifier_multiMatch() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setMultipleMatch(true);

        PersonContainer container = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd("PAT");
        container.setThePersonDto(personDto);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        EntityIdDto id1 = new EntityIdDto();
        id1.setStatusCd("A");
        id1.setRecordStatusCd("ACTIVE");
        id1.setAssigningAuthorityIdType("IDType");
        id1.setTypeCd("SS");
        id1.setRootExtensionTxt("ssn-value");
        id1.setAssigningAuthorityCd("SSA");
        id1.setAssigningAuthorityDescTxt("Social Security Administration");

        container.setTheEntityIdDtoCollection(List.of(id1));
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "SSN-VALUE^SS^SSA^SOCIAL SECURITY ADMINISTRATION^IDTYPE^LASTNAME^FIRSTNAME"))
                .thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByIdentifier(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByIdentifier_nullPatient() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(null);

        PersonContainer container = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd("PAT");
        container.setThePersonDto(personDto);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        EntityIdDto id1 = new EntityIdDto();
        id1.setStatusCd("A");
        id1.setRecordStatusCd("ACTIVE");
        id1.setAssigningAuthorityIdType("IDType");
        id1.setTypeCd("SS");
        id1.setRootExtensionTxt("ssn-value");
        id1.setAssigningAuthorityCd("SSA");
        id1.setAssigningAuthorityDescTxt("Social Security Administration");

        container.setTheEntityIdDtoCollection(List.of(id1));
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "SSN-VALUE^SS^SSA^SOCIAL SECURITY ADMINISTRATION^IDTYPE^LASTNAME^FIRSTNAME"))
                .thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByIdentifier(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByIdentifier_zeroPatientUid() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(0L);

        PersonContainer container = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd("PAT");
        container.setThePersonDto(personDto);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        EntityIdDto id1 = new EntityIdDto();
        id1.setStatusCd("A");
        id1.setRecordStatusCd("ACTIVE");
        id1.setAssigningAuthorityIdType("IDType");
        id1.setTypeCd("SS");
        id1.setRootExtensionTxt("ssn-value");
        id1.setAssigningAuthorityCd("SSA");
        id1.setAssigningAuthorityDescTxt("Social Security Administration");

        container.setTheEntityIdDtoCollection(List.of(id1));
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "SSN-VALUE^SS^SSA^SOCIAL SECURITY ADMINISTRATION^IDTYPE^LASTNAME^FIRSTNAME"))
                .thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByIdentifier(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByIdentifier_validPatientUid() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(2L);

        PersonContainer container = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd("PAT");
        container.setThePersonDto(personDto);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        EntityIdDto id1 = new EntityIdDto();
        id1.setStatusCd("A");
        id1.setRecordStatusCd("ACTIVE");
        id1.setAssigningAuthorityIdType("IDType");
        id1.setTypeCd("SS");
        id1.setRootExtensionTxt("ssn-value");
        id1.setAssigningAuthorityCd("SSA");
        id1.setAssigningAuthorityDescTxt("Social Security Administration");

        container.setTheEntityIdDtoCollection(List.of(id1));
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "SSN-VALUE^SS^SSA^SOCIAL SECURITY ADMINISTRATION^IDTYPE^LASTNAME^FIRSTNAME"))
                .thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByIdentifier(container);
        assertThat(matchDto).isNotNull();
        assertThat(matchDto.getPatientUid()).isEqualTo(2L);

    }

    @Test
    void tryMatchByDemographics_noDemographics() throws DataProcessingException {
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(null, null)).thenReturn(null);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByDemographics(new PersonContainer());
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByDemographics_null() throws DataProcessingException {
        PersonContainer container = new PersonContainer();

        PersonDto person = new PersonDto();
        person.setCd("PAT");
        person.setBirthTime(Timestamp.valueOf("2024-12-17 12:00:00"));
        person.setCurrSexCd("U");
        container.setThePersonDto(person);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "LASTNAME^FIRSTNAME^2024-12-17 12:00:00.0^U"))
                .thenReturn(null);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByDemographics(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByDemographics_multimatch() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setMultipleMatch(true);

        PersonContainer container = new PersonContainer();

        PersonDto person = new PersonDto();
        person.setCd("PAT");
        person.setBirthTime(Timestamp.valueOf("2024-12-17 12:00:00"));
        person.setCurrSexCd("U");
        container.setThePersonDto(person);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "LASTNAME^FIRSTNAME^2024-12-17 12:00:00.0^U"))
                .thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByDemographics(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByDemographics_nullPatientUid() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(null);

        PersonContainer container = new PersonContainer();

        PersonDto person = new PersonDto();
        person.setCd("PAT");
        person.setBirthTime(Timestamp.valueOf("2024-12-17 12:00:00"));
        person.setCurrSexCd("U");
        container.setThePersonDto(person);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "LASTNAME^FIRSTNAME^2024-12-17 12:00:00.0^U"))
                .thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByDemographics(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByDemographics_zeroPatientUid() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(0L);

        PersonContainer container = new PersonContainer();

        PersonDto person = new PersonDto();
        person.setCd("PAT");
        person.setBirthTime(Timestamp.valueOf("2024-12-17 12:00:00"));
        person.setCurrSexCd("U");
        container.setThePersonDto(person);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "LASTNAME^FIRSTNAME^2024-12-17 12:00:00.0^U"))
                .thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByDemographics(container);
        assertThat(matchDto).isNull();
    }

    @Test
    void tryMatchByDemographics_validPatientUid() throws DataProcessingException {
        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(3L);

        PersonContainer container = new PersonContainer();

        PersonDto person = new PersonDto();
        person.setCd("PAT");
        person.setBirthTime(Timestamp.valueOf("2024-12-17 12:00:00"));
        person.setCurrSexCd("U");
        container.setThePersonDto(person);

        PersonNameDto name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd("ACTIVE");
        name.setLastNm("lastName");
        name.setFirstNm("firstName");
        container.setThePersonNameDtoCollection(List.of(name));

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(
                "PAT",
                "LASTNAME^FIRSTNAME^2024-12-17 12:00:00.0^U"))
                .thenReturn(edxPatientMatchDto);

        EdxPatientMatchDto matchDto = patientMatchingService.tryMatchByDemographics(container);
        assertThat(matchDto).isNotNull();
        assertThat(matchDto.getPatientUid()).isEqualTo(3L);
    }

    @Test
    void tryMatchingString_null() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd("TEST");
        personContainer.setLocalIdentifier("123");
        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(false);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(null);
        // call test method
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_localid() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd("TEST");
        personContainer.setLocalIdentifier("123");
        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(false);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);
        // call test method
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_localid_throw_exp() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd("TEST");
        personContainer.setLocalIdentifier("123");
        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(anyString(), anyString()))
                .thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> patientMatchingService.getMatchingPatient(personContainer));
    }

    @Test
    void getMatchingPatient_localid_MultipleMatch_true() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd("TEST");
        personContainer.setLocalIdentifier("123");
        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(false);
        edxPatientMatchFoundDT.setPatientUid(222L);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);
        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");

        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        PersonContainer mpr = new PersonContainer();
        mpr.getThePersonDto().setLocalId("4444");
        mpr.setExt(true);
        mpr.setItNew(false);
        mpr.setItDirty(false);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(mpr);
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_throw_exp_nullpointer() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");
        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(true);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);
        assertThrows(NullPointerException.class, () -> patientMatchingService.getMatchingPatient(personContainer));
    }

    @Test
    void getMatchingPatient_identifier() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setItNew(false);
        personContainer.setItDirty(false);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);
        // for getIdentifier
        EntityIdDto entityIdDto = new EntityIdDto();
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

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(true);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);

        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");

        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        PersonContainer mpr = new PersonContainer();
        mpr.getThePersonDto().setLocalId("4444");
        mpr.setExt(true);
        mpr.setItNew(false);
        mpr.setItDirty(false);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(mpr);
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_identifier_multimatch_false() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setItNew(false);
        personContainer.setItDirty(false);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);
        // for getIdentifier
        EntityIdDto entityIdDto = new EntityIdDto();
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

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(false);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);

        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");

        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        PersonContainer mpr = new PersonContainer();
        mpr.getThePersonDto().setLocalId("4444");
        mpr.setExt(true);
        mpr.setItNew(false);
        mpr.setItDirty(false);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(mpr);
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_identifier_null_uid() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setItNew(false);
        personContainer.setItDirty(false);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);
        // for getIdentifier
        EntityIdDto entityIdDto = new EntityIdDto();
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

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(false);
        edxPatientMatchFoundDT.setPatientUid(null);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);

        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");

        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        PersonContainer mpr = new PersonContainer();
        mpr.getThePersonDto().setLocalId("4444");
        mpr.setExt(true);
        mpr.setItNew(false);
        mpr.setItDirty(false);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(mpr);
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_identifier_0() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setItNew(false);
        personContainer.setItDirty(false);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);
        // for getIdentifier
        EntityIdDto entityIdDto = new EntityIdDto();
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

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(false);
        edxPatientMatchFoundDT.setPatientUid(0L);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);

        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");

        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        PersonContainer mpr = new PersonContainer();
        mpr.getThePersonDto().setLocalId("4444");
        mpr.setExt(true);
        mpr.setItNew(false);
        mpr.setItDirty(false);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(mpr);
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_LNmFnmDobCurSexStr() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd(NEDSSConstant.PAT);
        personContainer.thePersonDto.setBirthTime(new Timestamp(System.currentTimeMillis()));
        personContainer.thePersonDto.setCurrSexCd("TEST");

        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(false);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);

        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");

        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        PersonContainer mpr = new PersonContainer();
        mpr.getThePersonDto().setLocalId("4444");
        mpr.setExt(true);
        mpr.setItNew(false);
        mpr.setItDirty(false);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(mpr);
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_LNmFnmDobCurSexStr_multimatch_true() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd(NEDSSConstant.PAT);
        personContainer.thePersonDto.setBirthTime(new Timestamp(System.currentTimeMillis()));
        personContainer.thePersonDto.setCurrSexCd("TEST");

        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(true);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);

        Person person = new Person();
        person.setPersonUid(222L);
        person.setPersonParentUid(222L);
        person.setLocalId("333");

        when(patientRepositoryUtil.createPerson(any())).thenReturn(person);

        PersonContainer mpr = new PersonContainer();
        mpr.getThePersonDto().setLocalId("4444");
        mpr.setExt(true);
        mpr.setItNew(false);
        mpr.setItDirty(false);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(mpr);
        EdxPatientMatchDto edxPatientMatchDtoResult = patientMatchingService.getMatchingPatient(personContainer);
        assertNotNull(edxPatientMatchDtoResult);
    }

    @Test
    void getMatchingPatient_LNmFnmDobCurSexStr_throwExp() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd(NEDSSConstant.PAT);
        personContainer.thePersonDto.setBirthTime(new Timestamp(System.currentTimeMillis()));
        personContainer.thePersonDto.setCurrSexCd("TEST");

        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any()))
                .thenReturn(edxPatientMatchFoundDT);

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(anyString(), anyString()))
                .thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(NullPointerException.class, () -> patientMatchingService.getMatchingPatient(personContainer));
    }

    @Test
    void updateExistingPerson() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setPersonParentUid(123L);
        Long personIdActual = patientMatchingService.updateExistingPerson(personContainer, "TEST");
        assertNull(personIdActual);
    }
}