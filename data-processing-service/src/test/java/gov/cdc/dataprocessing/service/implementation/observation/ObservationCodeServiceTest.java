package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ISrteCodeObsService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestData;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.SELECT_COUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ObservationCodeServiceTest {
    @Mock
    private ISrteCodeObsService srteCodeObsService;
    @Mock
    private OrganizationRepositoryUtil organizationRepositoryUtil;
    @Mock
    private ObservationUtil observationUtil;
    @InjectMocks
    private ObservationCodeService observationCodeService;
    @Mock
    AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(srteCodeObsService, organizationRepositoryUtil, observationUtil, authUtil);
    }


    @Test
    void getReportingLabCLIA_Success() throws DataProcessingException {
        TestData.createLabResultContainer();
        LabResultProxyContainer labResultProxyContainer = TestData.labResultProxyContainer;

        when(observationUtil.getUid(labResultProxyContainer.getTheParticipationDtoCollection(),
                null,
                NEDSSConstant.ENTITY_UID_LIST_TYPE,
                NEDSSConstant.ORGANIZATION,
                NEDSSConstant.PAR111_TYP_CD,
                NEDSSConstant.PART_ACT_CLASS_CD,
                NEDSSConstant.RECORD_STATUS_ACTIVE)).thenReturn(10L);

        var orgCon = new OrganizationContainer();
        var orgDto = new OrganizationDto();
        orgDto.setOrganizationUid(10L);

        var entityIdCol = new ArrayList<EntityIdDto>();
        var entityId = new EntityIdDto();
        entityId.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        entityId.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        entityId.setRootExtensionTxt("TEST");
        entityIdCol.add(entityId);
        orgCon.setTheEntityIdDtoCollection(entityIdCol);
        orgCon.setTheOrganizationDto(orgDto);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgCon);


        var test = observationCodeService.getReportingLabCLIA(labResultProxyContainer);

        assertNotNull(test);
        assertEquals("TEST", test);

    }

    @Test
    void deriveTheConditionCodeList_Test() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsConn.setTheObservationDto(obsDt);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia("CLIA");
        labResultProxyVO.setManualLab(true);

        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertNotNull(test);
    }

    @Test
    void deriveTheConditionCodeList_Test_2() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsConn.setTheObservationDto(obsDt);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertNotNull(test);
    }

    @Test
    void deriveTheConditionCodeList_Test_3() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsConn.setTheObservationDto(obsDt);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        orderTest.setTheParticipationDtoCollection(patCol);

        when(observationUtil.getUid(any(), any(), any(), any(),
                any(), any(), any())).thenReturn(10L);

        OrganizationContainer orgConn =new OrganizationContainer();
        var enCol = new ArrayList<EntityIdDto>();
        var en = new EntityIdDto();
        en.setAssigningAuthorityCd(null);
        en.setTypeCd(null);
        enCol.add(en);

        en = new EntityIdDto();
        en.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        en.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        en.setRootExtensionTxt("ROOT");
        enCol.add(en);
        orgConn.setTheEntityIdDtoCollection(enCol);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgConn);


        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertNotNull(test);
    }

    @Test
    void deriveTheConditionCodeList_Test_4() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsConn.setTheObservationDto(obsDt);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        orderTest.setTheParticipationDtoCollection(patCol);

        when(observationUtil.getUid(any(), any(), any(), any(),
                any(), any(), any())).thenReturn(-1L);

        OrganizationContainer orgConn =new OrganizationContainer();
        var enCol = new ArrayList<EntityIdDto>();
        var en = new EntityIdDto();
        en.setAssigningAuthorityCd(null);
        en.setTypeCd(null);
        enCol.add(en);


        orgConn.setTheEntityIdDtoCollection(enCol);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgConn);


        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);


        assertNotNull(test);
    }


    @Test
    void deriveTheConditionCodeList_Test_5() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsDt.setCd("CODE");
        obsConn.setTheObservationDto(obsDt);

        var codedCol = new ArrayList<ObsValueCodedDto>();
        var codedObs = new ObsValueCodedDto();
        codedCol.add(codedObs);
        codedObs = new ObsValueCodedDto();
        codedObs.setCodeSystemCd("CODE");
        codedCol.add(codedObs);

        codedObs = new ObsValueCodedDto();
        codedObs.setCodeSystemCd("CODE");
        codedObs.setCode("CODE");
        codedCol.add(codedObs);

        codedObs = new ObsValueCodedDto();
        codedObs.setCodeSystemCd("CODE_2");
        codedObs.setCode("CODE_2");
        codedCol.add(codedObs);

        codedObs = new ObsValueCodedDto();
        codedObs.setCodeSystemCd("SNM");
        codedObs.setCode("SNM");
        codedCol.add(codedObs);

        var mapSnomed = new HashMap<String, Object>();
        mapSnomed.put(SELECT_COUNT, 1);
        mapSnomed.put("LOINC", "LOINC");
        when(srteCodeObsService.getSnomed(eq("CODE_2"), any(), any())).thenReturn(mapSnomed);
        when(srteCodeObsService.getConditionForSnomedCode("LOINC")).thenReturn("LOINC");


        obsConn.setTheObsValueCodedDtoCollection(codedCol);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        orderTest.setTheParticipationDtoCollection(patCol);

        var obsTestDt = new ObservationDto();
        obsTestDt.setElectronicInd(NEDSSConstant.ELECTRONIC_IND_ELR);
        orderTest.setTheObservationDto(obsTestDt);

        when(observationUtil.getUid(any(), any(), any(), any(),
                any(), any(), any())).thenReturn(10L);

        OrganizationContainer orgConn =new OrganizationContainer();
        var enCol = new ArrayList<EntityIdDto>();
        var en = new EntityIdDto();
        en.setAssigningAuthorityCd(null);
        en.setTypeCd(null);
        enCol.add(en);

        en = new EntityIdDto();
        en.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        en.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        en.setRootExtensionTxt("ROOT");
        enCol.add(en);
        orgConn.setTheEntityIdDtoCollection(enCol);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgConn);


        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertEquals(1, test.size());
    }

    @Test
    void deriveTheConditionCodeList_Test_Empty_1() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsDt.setCd("CODE");
        obsDt.setCdSystemCd("SYS");
        obsConn.setTheObservationDto(obsDt);

        var mapSnomed = new HashMap<String, Object>();
        mapSnomed.put(SELECT_COUNT, 1);
        mapSnomed.put("LOINC", "LOINC");
        when(srteCodeObsService.getSnomed(eq("CODE"), any(), any())).thenReturn(mapSnomed);
        when(srteCodeObsService.getConditionForLoincCode("LOINC")).thenReturn("LOINC");


        var codedCol = new ArrayList<ObsValueCodedDto>();
        var codedObs = new ObsValueCodedDto();
        codedCol.add(codedObs);


        obsConn.setTheObsValueCodedDtoCollection(codedCol);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        orderTest.setTheParticipationDtoCollection(patCol);

        var obsTestDt = new ObservationDto();
        obsTestDt.setElectronicInd("N");
        orderTest.setTheObservationDto(obsTestDt);

        when(observationUtil.getUid(any(), any(), any(), any(),
                any(), any(), any())).thenReturn(10L);

        OrganizationContainer orgConn =new OrganizationContainer();
        var enCol = new ArrayList<EntityIdDto>();
        var en = new EntityIdDto();
        en.setAssigningAuthorityCd(null);
        en.setTypeCd(null);
        enCol.add(en);

        en = new EntityIdDto();
        en.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        en.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        en.setRootExtensionTxt("ROOT");
        enCol.add(en);
        orgConn.setTheEntityIdDtoCollection(enCol);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgConn);


        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertEquals(1, test.size());
    }

    @Test
    void deriveTheConditionCodeList_Test_Empty_2() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsDt.setCd("CODE");
        obsConn.setTheObservationDto(obsDt);

        var codedCol = new ArrayList<ObsValueCodedDto>();
        var codedObs = new ObsValueCodedDto();
        codedObs.setCode("CODE");
        codedCol.add(codedObs);

        when(srteCodeObsService.getDefaultConditionForLocalResultCode(eq("CODE"), any())).thenReturn("CODE");


        obsConn.setTheObsValueCodedDtoCollection(codedCol);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        orderTest.setTheParticipationDtoCollection(patCol);

        var obsTestDt = new ObservationDto();
        obsTestDt.setElectronicInd("N");
        orderTest.setTheObservationDto(obsTestDt);

        when(observationUtil.getUid(any(), any(), any(), any(),
                any(), any(), any())).thenReturn(10L);

        OrganizationContainer orgConn =new OrganizationContainer();
        var enCol = new ArrayList<EntityIdDto>();
        var en = new EntityIdDto();
        en.setAssigningAuthorityCd(null);
        en.setTypeCd(null);
        enCol.add(en);

        en = new EntityIdDto();
        en.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        en.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        en.setRootExtensionTxt("ROOT");
        enCol.add(en);
        orgConn.setTheEntityIdDtoCollection(enCol);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgConn);


        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertEquals(1, test.size());
    }

    @Test
    void deriveTheConditionCodeList_Test_Empty_3() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsDt.setCd("CODE");
        obsDt.setCdSystemCd("CODE");
        obsConn.setTheObservationDto(obsDt);

        when(srteCodeObsService.getDefaultConditionForLocalResultCode(eq("CODE"), any())).thenReturn("BLAH");


        var codedCol = new ArrayList<ObsValueCodedDto>();
        var codedObs = new ObsValueCodedDto();
        codedCol.add(codedObs);



        obsConn.setTheObsValueCodedDtoCollection(codedCol);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        orderTest.setTheParticipationDtoCollection(patCol);

        var obsTestDt = new ObservationDto();
        obsTestDt.setElectronicInd("N");
        orderTest.setTheObservationDto(obsTestDt);

        when(observationUtil.getUid(any(), any(), any(), any(),
                any(), any(), any())).thenReturn(10L);

        OrganizationContainer orgConn =new OrganizationContainer();
        var enCol = new ArrayList<EntityIdDto>();
        var en = new EntityIdDto();
        en.setAssigningAuthorityCd(null);
        en.setTypeCd(null);
        enCol.add(en);

        en = new EntityIdDto();
        en.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        en.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        en.setRootExtensionTxt("ROOT");
        enCol.add(en);
        orgConn.setTheEntityIdDtoCollection(enCol);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgConn);


        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertEquals(1, test.size());
    }

    @Test
    void deriveTheConditionCodeList_Test_Empty_4() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsDt.setCd("CODE");
        obsConn.setTheObservationDto(obsDt);

        when(srteCodeObsService.getDefaultConditionForLabTest(eq("CODE"),any())).thenReturn("BLAH");

        var codedCol = new ArrayList<ObsValueCodedDto>();
        var codedObs = new ObsValueCodedDto();
        codedCol.add(codedObs);



        obsConn.setTheObsValueCodedDtoCollection(codedCol);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        orderTest.setTheParticipationDtoCollection(patCol);

        var obsTestDt = new ObservationDto();
        obsTestDt.setElectronicInd("N");
        orderTest.setTheObservationDto(obsTestDt);

        when(observationUtil.getUid(any(), any(), any(), any(),
                any(), any(), any())).thenReturn(10L);

        OrganizationContainer orgConn =new OrganizationContainer();
        var enCol = new ArrayList<EntityIdDto>();
        var en = new EntityIdDto();
        en.setAssigningAuthorityCd(null);
        en.setTypeCd(null);
        enCol.add(en);

        en = new EntityIdDto();
        en.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        en.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        en.setRootExtensionTxt("ROOT");
        enCol.add(en);
        orgConn.setTheEntityIdDtoCollection(enCol);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgConn);


        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertEquals(1, test.size());
    }

    @Test
    void deriveTheConditionCodeList_Test_Empty_5_NONE() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        ObservationContainer orderTest = new ObservationContainer();

        var obsConnCol = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsDt.setCd("CODE");
        obsConn.setTheObservationDto(obsDt);

        var codedCol = new ArrayList<ObsValueCodedDto>();
        var codedObs = new ObsValueCodedDto();
        codedCol.add(codedObs);



        obsConn.setTheObsValueCodedDtoCollection(codedCol);
        obsConnCol.add(obsConn);
        labResultProxyVO.setTheObservationContainerCollection(obsConnCol);
        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(true);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        orderTest.setTheParticipationDtoCollection(patCol);

        var obsTestDt = new ObservationDto();
        obsTestDt.setElectronicInd("N");
        orderTest.setTheObservationDto(obsTestDt);

        when(observationUtil.getUid(any(), any(), any(), any(),
                any(), any(), any())).thenReturn(10L);

        OrganizationContainer orgConn =new OrganizationContainer();
        var enCol = new ArrayList<EntityIdDto>();
        var en = new EntityIdDto();
        en.setAssigningAuthorityCd(null);
        en.setTypeCd(null);
        enCol.add(en);

        en = new EntityIdDto();
        en.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        en.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        en.setRootExtensionTxt("ROOT");
        enCol.add(en);
        orgConn.setTheEntityIdDtoCollection(enCol);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgConn);


        var test = observationCodeService.deriveTheConditionCodeList(labResultProxyVO, orderTest);

        assertEquals(0, test.size());
    }
}
