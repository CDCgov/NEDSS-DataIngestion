package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ProviderMatchingServiceTest {
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
    private ProviderMatchingService providerMatchingService;

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
    void getMatchingProvider_local_id_entityMatch() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");
        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        edxEntityMatchingDT.setEntityUid(123L);
        edxEntityMatchingDT.setTypeCd("TYPE_CD");
        edxEntityMatchingDT.setMatchString("MATCH_STRING");
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(edxEntityMatchingDT.getEntityUid()),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_local_id_with_entityMatch_null() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");
        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);
        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_local_id_throws_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_entityMatch_Identifier() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);

        //for getIdentifier
        EntityIdDto entityIdDto=new EntityIdDto();
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

        PersonNameDto personNameDto=new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        edxEntityMatchingDT.setEntityUid(123L);
        edxEntityMatchingDT.setTypeCd("TYPE_CD");
        edxEntityMatchingDT.setMatchString("MATCH_STRING");
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_entityMatch_Identifier_thorws_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);

        //for getIdentifier
        EntityIdDto entityIdDto=new EntityIdDto();
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

        PersonNameDto personNameDto=new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_entityMatch_Identifier_null_edxentity() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);

        //for getIdentifier
        EntityIdDto entityIdDto=new EntityIdDto();
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

        PersonNameDto personNameDto=new PersonNameDto();
        personNameDto.setNmUseCd("L");
        personNameDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        personNameDto.setAsOfDate(new Timestamp(System.currentTimeMillis()));
        personNameDto.setLastNm("TEST_LST_NM");
        personNameDto.setFirstNm("TEST_FIRST_NM");
        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_name_addr() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

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
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        edxEntityMatchingDT.setEntityUid(123L);
        edxEntityMatchingDT.setTypeCd("TYPE_CD");
        edxEntityMatchingDT.setMatchString("MATCH_STRING");
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_name_addr_with_empty_entity() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

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
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }

    @Test
    void getMatchingProvider_name_addr_throws_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

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
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_telephone() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

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
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();
        edxEntityMatchingDT.setEntityUid(123L);
        edxEntityMatchingDT.setTypeCd("TYPE_CD");
        edxEntityMatchingDT.setMatchString("MATCH_STRING");
        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);
        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void getMatchingProvider_telephone_empty_entity() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");

        entLocPartDT.setTheTeleLocatorDto(teleLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        EdxEntityMatchDto edxEntityMatchingDT=new EdxEntityMatchDto();

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(any(),any())).thenReturn(edxEntityMatchingDT);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }

    @Test
    void getMatchingProvider_telephone_throws_exp() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);

        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

        EntityLocatorParticipationDto entLocPartDT = new EntityLocatorParticipationDto();
        entLocPartDT.setEntityUid(123L);
        entLocPartDT.setClassCd(NEDSSConstant.TELE);
        entLocPartDT.setCd(NEDSSConstant.PHONE);

        TeleLocatorDto teleLocDT = new TeleLocatorDto();
        teleLocDT.setPhoneNbrTxt("1234567890");

        entLocPartDT.setTheTeleLocatorDto(teleLocDT);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(entLocPartDT);

        when(edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(anyString(), anyString())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> providerMatchingService.getMatchingProvider(personContainer));
    }
    @Test
    void getMatchingProvider_provider() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.getThePersonDto().setCd(NEDSSConstant.PRV);

        EDXActivityDetailLogDto edxActivityDetailLogDtoResult=providerMatchingService.getMatchingProvider(personContainer);
        assertEquals(String.valueOf(123L),edxActivityDetailLogDtoResult.getRecordId());
    }
    @Test
    void setProvider() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        Long idResult=providerMatchingService.setProvider(personContainer,"TEST");
        assertEquals(123L,idResult);
    }
}