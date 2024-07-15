package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private PatientMatchingService patientMatchingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    void getMatchingPatient_localid() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd("TEST");
        personContainer.setLocalIdentifier("123");
        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(false);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);
        //call test method
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

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
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
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);
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
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);
        assertThrows(DataProcessingException.class, () -> patientMatchingService.getMatchingPatient(personContainer));
    }

    @Test
    void getMatchingPatient_identifier() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setItNew(false);
        personContainer.setItDirty(false);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);
        //for getIdentifier
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
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);

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
        //for getIdentifier
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
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);

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
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);

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
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);

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
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);

        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> patientMatchingService.getMatchingPatient(personContainer));
    }

    @Test
    void getMultipleMatchFound() {
        boolean multiMatchResult = patientMatchingService.getMultipleMatchFound();
        assertFalse(multiMatchResult);
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