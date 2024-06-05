package gov.cdc.dataprocessing.service.implementation.jurisdiction;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.srte.model.JurisdictionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.JurisdictionCodeRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.JurisdictionParticipationRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JurisdictionServiceTest {
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private OrganizationRepositoryUtil organizationRepositoryUtil;
    @Mock
    private JurisdictionParticipationRepository jurisdictionParticipationRepository;
    @Mock
    private JurisdictionCodeRepository jurisdictionCodeRepository;
    @Mock
    AuthUtil authUtil;
    @InjectMocks
    private JurisdictionService jurisdictionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo authUserProfileInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        authUserProfileInfo.setAuthUser(user);
        authUtil.setGlobalAuthUser(authUserProfileInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(patientRepositoryUtil);
        Mockito.reset(organizationRepositoryUtil);
        Mockito.reset(jurisdictionParticipationRepository);
        Mockito.reset(jurisdictionCodeRepository);
    }

    @Test
    void getJurisdictionCode() {
        List<JurisdictionCode> jurisdictionCodeList = new ArrayList<>();
        JurisdictionCode jurisdictionCode = getJurisdictionCodeObj();
        jurisdictionCodeList.add(jurisdictionCode);
        when(jurisdictionCodeRepository.findAll()).thenReturn(jurisdictionCodeList);
        List<JurisdictionCode> jurisdictionCodeListRslt = jurisdictionService.getJurisdictionCode();
        assertEquals(1, jurisdictionCodeListRslt.size());
    }

    @Test
    void getJurisdictionCode_emptylist() {
        List<JurisdictionCode> jurisdictionCodeList = new ArrayList<>();
        when(jurisdictionCodeRepository.findAll()).thenReturn(jurisdictionCodeList);
        List<JurisdictionCode> jurisdictionCodeListRslt = jurisdictionService.getJurisdictionCode();
        assertEquals(0, jurisdictionCodeListRslt.size());
    }

    @Test
    void assignJurisdiction_patient() throws DataProcessingException {
        PersonContainer patientContainer = new PersonContainer();
        PersonContainer providerContainer = new PersonContainer();
        OrganizationContainer organizationContainer = new OrganizationContainer();
        ObservationContainer observationRequest = new ObservationContainer();

        Collection<EntityLocatorParticipationDto> entityLocatorPartColl = new ArrayList<>();

        EntityLocatorParticipationDto entityLocatorDT_po_1 = new EntityLocatorParticipationDto();
        entityLocatorDT_po_1.setClassCd(NEDSSConstant.POSTAL);
        entityLocatorDT_po_1.setUseCd("H");
        PostalLocatorDto postalDt1 = new PostalLocatorDto();
        postalDt1.setZipCd("22275-9998");
        postalDt1.setAddReasonCd("TEST_REASON_CD");
        postalDt1.setAddUserId(12345L);
        postalDt1.setCityDescTxt("test_city_desc_text");
        entityLocatorDT_po_1.setThePostalLocatorDto(postalDt1);
        entityLocatorPartColl.add(entityLocatorDT_po_1);
        patientContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorPartColl);

        Collection<String> jurisdictionsList = new ArrayList<>();
        jurisdictionsList.add("111");
        when(jurisdictionParticipationRepository.findJurisdictionForCity(any(), any(), any())).thenReturn(Optional.of(jurisdictionsList));

        jurisdictionService.assignJurisdiction(patientContainer, providerContainer, organizationContainer, observationRequest);
        verify(jurisdictionParticipationRepository).findJurisdictionForCity(any(), any(), any());
    }

    @Test
    void assignJurisdiction_patient_zipcode_empty() throws DataProcessingException {
        PersonContainer patientContainer = new PersonContainer();
        PersonContainer providerContainer = new PersonContainer();
        OrganizationContainer organizationContainer = new OrganizationContainer();
        ObservationContainer observationRequest = new ObservationContainer();

        Collection<EntityLocatorParticipationDto> entityLocatorPartColl = new ArrayList<>();

        EntityLocatorParticipationDto entityLocatorDT_po_1 = new EntityLocatorParticipationDto();
        entityLocatorDT_po_1.setClassCd(NEDSSConstant.POSTAL);
        entityLocatorDT_po_1.setUseCd("H");
        PostalLocatorDto postalDt1 = new PostalLocatorDto();

        postalDt1.setAddReasonCd("TEST_REASON_CD");
        postalDt1.setAddUserId(12345L);
        postalDt1.setCityDescTxt("test_city_desc_text");
        entityLocatorDT_po_1.setThePostalLocatorDto(postalDt1);
        entityLocatorPartColl.add(entityLocatorDT_po_1);
        patientContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorPartColl);

        Collection<String> jurisdictionsList = new ArrayList<>();
        jurisdictionsList.add("111");
        when(jurisdictionParticipationRepository.findJurisdictionForCity(any(), any(), any())).thenReturn(Optional.of(jurisdictionsList));

        jurisdictionService.assignJurisdiction(patientContainer, providerContainer, organizationContainer, observationRequest);
        verify(jurisdictionParticipationRepository).findJurisdictionForCity(any(), any(), any());
    }

    @Test
    void assignJurisdiction_provider() throws DataProcessingException {
        PersonContainer patientContainer = new PersonContainer();
        PersonContainer providerContainer = new PersonContainer();
        OrganizationContainer organizationContainer = new OrganizationContainer();
        ObservationContainer observationRequest = new ObservationContainer();

        Collection<EntityLocatorParticipationDto> entityLocatorPartColl = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorDT_po_2 = new EntityLocatorParticipationDto();
        entityLocatorDT_po_2.setClassCd(NEDSSConstant.POSTAL);
        entityLocatorDT_po_2.setUseCd("WP");
        PostalLocatorDto postalDt2 = new PostalLocatorDto();
        postalDt2.setZipCd("22275");
        postalDt2.setAddReasonCd("TEST_REASON_CD");
        postalDt2.setAddUserId(12345L);
        postalDt2.setCityDescTxt("test_city_desc_text");
        entityLocatorDT_po_2.setThePostalLocatorDto(postalDt2);
        entityLocatorPartColl.add(entityLocatorDT_po_2);
        providerContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorPartColl);

        Collection<String> jurisdictionsList = new ArrayList<>();
        jurisdictionsList.add("111");
        when(jurisdictionParticipationRepository.findJurisdictionForCity(any(), any(), any())).thenReturn(Optional.of(jurisdictionsList));

        jurisdictionService.assignJurisdiction(patientContainer, providerContainer, organizationContainer, observationRequest);
        verify(jurisdictionParticipationRepository).findJurisdictionForCity(any(), any(), any());
    }

    @Test
    void assignJurisdiction_org1() throws DataProcessingException {
        PersonContainer patientContainer = new PersonContainer();
        PersonContainer providerContainer = new PersonContainer();
        OrganizationContainer organizationContainer = new OrganizationContainer();
        ObservationContainer observationRequest = new ObservationContainer();

        Collection<EntityLocatorParticipationDto> entityLocatorPartColl = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorDT_po = new EntityLocatorParticipationDto();
        entityLocatorDT_po.setClassCd(NEDSSConstant.POSTAL);
        entityLocatorDT_po.setUseCd("WP");
        PostalLocatorDto postalDt = new PostalLocatorDto();
        postalDt.setZipCd("22275");
        postalDt.setAddReasonCd("TEST_REASON_CD");
        postalDt.setAddUserId(12345L);
        postalDt.setCityDescTxt("test_city_desc_text");
        entityLocatorDT_po.setThePostalLocatorDto(postalDt);
        entityLocatorPartColl.add(entityLocatorDT_po);
        organizationContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorPartColl);

        Collection<String> jurisdictionsList = new ArrayList<>();
        jurisdictionsList.add("111");
        when(jurisdictionParticipationRepository.findJurisdictionForCity(any(), any(), any())).thenReturn(Optional.of(jurisdictionsList));

        jurisdictionService.assignJurisdiction(patientContainer, providerContainer, organizationContainer, observationRequest);
        verify(jurisdictionParticipationRepository).findJurisdictionForCity(any(), any(), any());
    }

    @Test
    void assignJurisdiction_findJuris_empty_jurislist() throws DataProcessingException {
        PersonContainer patientContainer = new PersonContainer();
        PersonContainer providerContainer = new PersonContainer();
        OrganizationContainer organizationContainer = new OrganizationContainer();
        ObservationContainer observationRequest = new ObservationContainer();

        Collection<EntityLocatorParticipationDto> entityLocatorPartColl = new ArrayList<>();

        EntityLocatorParticipationDto entityLocatorDT_po = new EntityLocatorParticipationDto();
        entityLocatorDT_po.setClassCd(NEDSSConstant.POSTAL);
        entityLocatorDT_po.setUseCd("H");

        PostalLocatorDto postalDt = new PostalLocatorDto();
        postalDt.setZipCd("22275-9998");
        postalDt.setAddReasonCd("TEST_REASON_CD");
        postalDt.setAddUserId(12345L);
        postalDt.setCityDescTxt("test_city_desc_text");
        entityLocatorDT_po.setThePostalLocatorDto(postalDt);
        entityLocatorPartColl.add(entityLocatorDT_po);
        patientContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorPartColl);

        Collection<String> jurisdictionsList = new ArrayList<>();//Empty
        when(jurisdictionParticipationRepository.findJurisdictionForCity(any(), any(), any())).thenReturn(Optional.of(jurisdictionsList));

        jurisdictionService.assignJurisdiction(patientContainer, providerContainer, organizationContainer, observationRequest);
        verify(jurisdictionParticipationRepository).findJurisdictionForCity(any(), any(), any());
    }

    @Test
    void deriveJurisdictionCd_throw_exp_on_empty_obj() {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto rootObsDT = new ObservationDto();
        assertThrows(DataProcessingException.class, () -> jurisdictionService.deriveJurisdictionCd(labResultProxyContainer, rootObsDT));
    }

    @Test
    void deriveJurisdictionCd_all() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto rootObsDT = new ObservationDto();
        AuthUtil.authUser.setJurisdictionDerivationInd(NEDSSConstant.YES);
        //provider
        ParticipationDto partDT = new ParticipationDto();
        partDT.setTypeCd(NEDSSConstant.PAR101_TYP_CD);
        partDT.setSubjectClassCd(NEDSSConstant.PERSON_CLASS_CODE);
        partDT.setSubjectEntityUid(123L);
        //patient
        ParticipationDto partDT_patient = new ParticipationDto();
        partDT_patient.setTypeCd(NEDSSConstant.PAR110_TYP_CD);
        partDT_patient.setSubjectEntityUid(123L);
        //ordering facility
        ParticipationDto partDT_of = new ParticipationDto();
        partDT_of.setTypeCd(NEDSSConstant.PAR102_TYP_CD);
        partDT_of.setSubjectEntityUid(123L);
        //reporting facility
        ParticipationDto partDT_rf = new ParticipationDto();
        partDT_rf.setTypeCd(NEDSSConstant.PAR111_TYP_CD);
        partDT_rf.setSubjectClassCd(NEDSSConstant.PAR111_SUB_CD);
        partDT_rf.setSubjectEntityUid(123L);
        rootObsDT.setCtrlCdDisplayForm(NEDSSConstant.LAB_REPORT);
        rootObsDT.setElectronicInd(NEDSSConstant.EXTERNAL_USER_IND);

        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(partDT);
        partColl.add(partDT_patient);
        partColl.add(partDT_of);
        partColl.add(partDT_rf);
        labResultProxyContainer.setTheParticipationDtoCollection(partColl);

        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto1 = new PersonDto();
        personDto1.setPersonUid(123L);
        personContainer.setThePersonDto(personDto1);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(personContainer);
        //patient
        PersonContainer personContainer_patent = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(123L);
        personContainer_patent.setThePersonDto(personDto);
        Collection<PersonContainer> personContainerCol = new ArrayList<>();
        personContainerCol.add(personContainer_patent);
        labResultProxyContainer.setThePersonContainerCollection(personContainerCol);
        //ordering facility
        OrganizationContainer organizationContainer2 = new OrganizationContainer();
        Collection<EntityLocatorParticipationDto> entityLocatorPartColl = new ArrayList<>();
        EntityLocatorParticipationDto entityLocatorDT_po = new EntityLocatorParticipationDto();
        entityLocatorDT_po.setClassCd(NEDSSConstant.POSTAL);
        entityLocatorDT_po.setUseCd("WP");
        PostalLocatorDto postalDt = new PostalLocatorDto();
        postalDt.setZipCd("22275");
        postalDt.setAddReasonCd("TEST_REASON_CD");
        postalDt.setAddUserId(12345L);
        postalDt.setCityDescTxt("test_city_desc_text");
        entityLocatorDT_po.setThePostalLocatorDto(postalDt);
        entityLocatorPartColl.add(entityLocatorDT_po);
        organizationContainer2.setTheEntityLocatorParticipationDtoCollection(entityLocatorPartColl);
        when(organizationRepositoryUtil.loadObject(any(), any())).thenReturn(organizationContainer2);

        jurisdictionService.deriveJurisdictionCd(labResultProxyContainer, rootObsDT);
        verify(organizationRepositoryUtil,times(2)).loadObject(any(), any());
    }

    @Test
    void deriveJurisdictionCd_provider() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto rootObsDT = new ObservationDto();
        //
        ParticipationDto partDT = new ParticipationDto();
        partDT.setTypeCd(NEDSSConstant.PAR101_TYP_CD);
        partDT.setSubjectClassCd(NEDSSConstant.PERSON_CLASS_CODE);
        partDT.setSubjectEntityUid(123L);
        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(partDT);
        labResultProxyContainer.setTheParticipationDtoCollection(partColl);

        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(123L);
        personContainer.setThePersonDto(personDto);
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(personContainer);

        jurisdictionService.deriveJurisdictionCd(labResultProxyContainer, rootObsDT);
        verify(patientRepositoryUtil,times(1)).loadPerson(123L);
    }

    @Test
    void deriveJurisdictionCd_patient() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto rootObsDT = new ObservationDto();
        //
        ParticipationDto partDT = new ParticipationDto();
        partDT.setTypeCd(NEDSSConstant.PAR110_TYP_CD);
        partDT.setSubjectEntityUid(123L);

        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(partDT);
        partColl.add(null);
        labResultProxyContainer.setTheParticipationDtoCollection(partColl);

        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(123L);
        personContainer.setThePersonDto(personDto);
        Collection<PersonContainer> personContainerCol = new ArrayList<>();
        personContainerCol.add(personContainer);
        personContainerCol.add(new PersonContainer());
        labResultProxyContainer.setThePersonContainerCollection(personContainerCol);

        String result=jurisdictionService.deriveJurisdictionCd(labResultProxyContainer, rootObsDT);
        assertNull(result);
    }

    @Test
    void deriveJurisdictionCd_orderingFacility() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto rootObsDT = new ObservationDto();
        //
        ParticipationDto partDT = new ParticipationDto();
        partDT.setTypeCd(NEDSSConstant.PAR102_TYP_CD);
        partDT.setSubjectEntityUid(123L);

        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(partDT);
        labResultProxyContainer.setTheParticipationDtoCollection(partColl);

        OrganizationContainer organizationContainer = new OrganizationContainer();

        when(organizationRepositoryUtil.loadObject(any(), any())).thenReturn(organizationContainer);

        jurisdictionService.deriveJurisdictionCd(labResultProxyContainer, rootObsDT);
        verify(organizationRepositoryUtil).loadObject(any(), any());
    }

    @Test
    void deriveJurisdictionCd_orderingFacility_throw_exp() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto rootObsDT = new ObservationDto();

        ParticipationDto partDT = new ParticipationDto();
        partDT.setTypeCd(NEDSSConstant.PAR102_TYP_CD);
        partDT.setSubjectEntityUid(123L);

        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(partDT);
        labResultProxyContainer.setTheParticipationDtoCollection(partColl);

        when(organizationRepositoryUtil.loadObject(any(), any())).thenThrow(Mockito.mock(DataProcessingException.class));
        assertThrows(DataProcessingException.class, () -> jurisdictionService.deriveJurisdictionCd(labResultProxyContainer, rootObsDT));
    }

    @Test
    void deriveJurisdictionCd_reportingFacility() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto rootObsDT = new ObservationDto();
        AuthUtil.authUser.setJurisdictionDerivationInd(NEDSSConstant.YES);

        ParticipationDto partDT = new ParticipationDto();
        partDT.setTypeCd(NEDSSConstant.PAR111_TYP_CD);
        partDT.setSubjectClassCd(NEDSSConstant.PAR111_SUB_CD);

        rootObsDT.setCtrlCdDisplayForm(NEDSSConstant.LAB_REPORT);
        rootObsDT.setElectronicInd(NEDSSConstant.EXTERNAL_USER_IND);
        partDT.setSubjectEntityUid(123L);

        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(partDT);
        labResultProxyContainer.setTheParticipationDtoCollection(partColl);

        OrganizationContainer organizationContainer = new OrganizationContainer();

        when(organizationRepositoryUtil.loadObject(any(), any())).thenReturn(organizationContainer);

        jurisdictionService.deriveJurisdictionCd(labResultProxyContainer, rootObsDT);
        verify(organizationRepositoryUtil).loadObject(any(), any());
    }

    private JurisdictionCode getJurisdictionCodeObj() {
        JurisdictionCode jurisdictionCode = new JurisdictionCode();
        jurisdictionCode.setCode("CD");
        jurisdictionCode.setTypeCd("TYPE_CD");
        jurisdictionCode.setAssigningAuthorityCd("TEST_AA");
        jurisdictionCode.setAssigningAuthorityDescTxt("TEST_AA_DESC");
        jurisdictionCode.setCodeDescTxt("TEST_CD_TXT");
        jurisdictionCode.setCodeShortDescTxt("CD_DESC_TXT");
        jurisdictionCode.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        jurisdictionCode.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        jurisdictionCode.setIndentLevelNbr(1);
        jurisdictionCode.setIsModifiableInd("test");
        jurisdictionCode.setParentIsCd("test_prnt_cd");
        jurisdictionCode.setStateDomainCd("Test_state_domain_cd");
        jurisdictionCode.setStatusCd("test_status_cd");
        jurisdictionCode.setStatusTime(new Timestamp(System.currentTimeMillis()));
        jurisdictionCode.setCodeSetNm("test_cd_set_name");
        jurisdictionCode.setCodeSeqNum(123);
        jurisdictionCode.setNbsUid(123);
        jurisdictionCode.setSourceConceptId("test_src_id");
        jurisdictionCode.setCodeSystemCd("test_cd_sys");
        jurisdictionCode.setCodeSystemDescTxt("test_cd_sys_txt");
        jurisdictionCode.setExportInd("test_I");
        return jurisdictionCode;
    }
}