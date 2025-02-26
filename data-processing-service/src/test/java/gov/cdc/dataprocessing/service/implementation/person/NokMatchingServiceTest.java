package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class NokMatchingServiceTest {
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
    private NokMatchingService nokMatchingService;

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
    void getMatchingNextOfKin_name_addr_street() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.POSTAL);
        entLocPartDT.setCd(NEDSSConstant.OFFICE_CD);
        entLocPartDT.setUseCd(NEDSSConstant.WORK_PLACE);
        entLocPartDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);

        PostalLocatorDto postLocDT = new PostalLocatorDto();
        postLocDT.setStreetAddr1("STREET_ADDR1");
        postLocDT.setCityDescTxt("CITYDESCTXT");
        postLocDT.setStateCd("TEST_STATE_CD");
        postLocDT.setZipCd("12345");
        entLocPartDT.setThePostalLocatorDto(postLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);

        EdxPatientMatchDto edxPatientMatchResult =nokMatchingService.getMatchingNextOfKin(personContainer);
        assertNotNull(edxPatientMatchResult);
    }
    @Test
    void getMatchingNextOfKin_name_addr_street_throw_exp() {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.POSTAL);
        entLocPartDT.setCd(NEDSSConstant.OFFICE_CD);
        entLocPartDT.setUseCd(NEDSSConstant.WORK_PLACE);
        entLocPartDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);

        PostalLocatorDto postLocDT = new PostalLocatorDto();
        postLocDT.setStreetAddr1("STREET_ADDR1");
        postLocDT.setCityDescTxt("CITYDESCTXT");
        postLocDT.setStateCd("TEST_STATE_CD");
        postLocDT.setZipCd("12345");
        entLocPartDT.setThePostalLocatorDto(postLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        assertThrows(NullPointerException.class, () -> nokMatchingService.getMatchingNextOfKin(personContainer));
    }

    @Test
    void getMatchingNextOfKin_telephone() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd(NEDSSConstant.PAT);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);
        entLocPartDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");

        entLocPartDT.setTheTeleLocatorDto(teleLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        //for getNamesStr
        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        //for NEW NOK
        EntityIdDto entityIdDto=new EntityIdDto();
        entityIdDto.setEntityUid(123L);
        entityIdDto.setTypeCd("TEST");
        personContainer.getTheEntityIdDtoCollection().add(entityIdDto);

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

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);

        EdxPatientMatchDto edxPatientMatchResult =nokMatchingService.getMatchingNextOfKin(personContainer);
        assertNotNull(edxPatientMatchResult);
    }
    @Test
    void getMatchingNextOfKin_telephone_throw_exp() {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd(NEDSSConstant.PAT);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);
        entLocPartDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");

        entLocPartDT.setTheTeleLocatorDto(teleLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        //for getNamesStr
        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        assertThrows(NullPointerException.class, () -> nokMatchingService.getMatchingNextOfKin(personContainer));
    }
    @Test
    void getMatchingNextOfKin_entityId_throw_exp() throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.thePersonDto.setCd(NEDSSConstant.PAT);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);
        entLocPartDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");

        entLocPartDT.setTheTeleLocatorDto(teleLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        //for getNamesStr
        PersonNameDto personNameDto = new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(), any())).thenReturn(edxPatientMatchFoundDT);

        assertThrows(NullPointerException.class, () -> nokMatchingService.getMatchingNextOfKin(personContainer));
    }
    @Test
    void getMatchingNextOfKin_throw_exp() {
        PersonContainer personContainer = new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        assertThrows(NullPointerException.class, () -> nokMatchingService.getMatchingNextOfKin(personContainer));
    }
}