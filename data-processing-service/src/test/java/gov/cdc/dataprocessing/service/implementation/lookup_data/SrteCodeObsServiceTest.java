package gov.cdc.dataprocessing.service.implementation.lookup_data;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.ProgAreaSnomeCodeStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LOINCCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResult;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LabTest;
import gov.cdc.dataprocessing.repository.nbs.srte.model.SnomedCode;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class SrteCodeObsServiceTest {
    @Mock
    private ProgAreaSnomeCodeStoredProcRepository progAreaSnomeCodeStoredProcRepository;
    @Mock
    private SnomedConditionRepository snomedConditionRepository;
    @Mock
    private LOINCCodeRepository loincCodeRepository;
    @Mock
    private LabResultRepository labResultRepository;
    @Mock
    private LabTestRepository labTestRepository;
    @Mock
    private LabTestLoincRepository labTestLoincRepository;
    @Mock
    private LabResultSnomedRepository labResultSnomedRepository;
    @Mock
    private SnomedCodeRepository snomedCodeRepository;
    @Mock
    private ConditionCodeRepository conditionCodeRepository;

    @InjectMocks
    private SrteCodeObsService srteCodeObsService;
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
        Mockito.reset(progAreaSnomeCodeStoredProcRepository, snomedConditionRepository,
                loincCodeRepository, labResultRepository,
                labTestRepository, labTestLoincRepository,
                labResultSnomedRepository, snomedCodeRepository,
                conditionCodeRepository, authUtil);
    }

    @Test
    void getSnomed_Success() throws DataProcessingException {
        String code = "code";
        String type = "type";
        String clia = "clia";

        when(progAreaSnomeCodeStoredProcRepository.getSnomed(code, type, clia)).thenReturn(
                new HashMap<>()
        );

        var res = srteCodeObsService.getSnomed(code, type, clia);
        assertNotNull(res);

    }

    @Test
    void getConditionForSnomedCode_Success_1() {
        String snomedCd = "code";
        var lst = new ArrayList<String>();
        lst.add("TEST");
        when(snomedConditionRepository.getConditionForSnomedCode(snomedCd)).thenReturn(Optional.of(lst));
        var res = srteCodeObsService.getConditionForSnomedCode(snomedCd);
        assertNotNull(res);
        assertEquals("TEST", res);
    }

    @Test
    void getConditionForSnomedCode_Success_2() {
        String snomedCd = "code";
        when(snomedConditionRepository.getConditionForSnomedCode(snomedCd)).thenReturn(Optional.empty());
        var res = srteCodeObsService.getConditionForSnomedCode(snomedCd);
        assertNotNull(res);
        assertEquals("", res);
    }

    @Test
    void getConditionForLoincCode_Success_1() {
        String snomedCd = "code";
        var lst = new ArrayList<String>();
        lst.add("TEST");
        when(loincCodeRepository.findConditionForLoincCode(snomedCd)).thenReturn(Optional.of(lst));
        var res = srteCodeObsService.getConditionForLoincCode(snomedCd);
        assertNotNull(res);
        assertEquals("TEST", res);
    }

    @Test
    void getConditionForLoincCode_Success_2() {
        String snomedCd = "code";
        when(loincCodeRepository.findConditionForLoincCode(snomedCd)).thenReturn(Optional.empty());
        var res = srteCodeObsService.getConditionForLoincCode(snomedCd);
        assertNotNull(res);
        assertEquals("", res);
    }

    @Test
    void getDefaultConditionForLocalResultCodeSuccess_1() {
        String snomedCd = "code";
        var lst = new ArrayList<String>();
        lst.add("TEST");
        when(labResultRepository.findDefaultConditionCdByLabResultCdAndLaboratoryId(snomedCd , "TEST")).thenReturn(Optional.of(lst));
        var res = srteCodeObsService.getDefaultConditionForLocalResultCode(snomedCd, "TEST");
        assertNotNull(res);
        assertEquals("TEST", res);
    }

    @Test
    void getDefaultConditionForLocalResultCode_Success_2() {
        String snomedCd = "code";
        when(labResultRepository.findDefaultConditionCdByLabResultCdAndLaboratoryId(snomedCd, "TEST")).thenReturn(Optional.empty());
        var res = srteCodeObsService.getDefaultConditionForLocalResultCode(snomedCd, "TEST");
        assertNotNull(res);
        assertEquals("", res);
    }

    @Test
    void getDefaultConditionForLabTest_Success_1() {
        String snomedCd = "code";
        var lst = new ArrayList<String>();
        lst.add("TEST");
        when(labTestRepository.findDefaultConditionForLabTest(snomedCd , "TEST")).thenReturn(Optional.of(lst));
        var res = srteCodeObsService.getDefaultConditionForLabTest(snomedCd, "TEST");
        assertNotNull(res);
        assertEquals("TEST", res);
    }

    @Test
    void getDefaultConditionForLabTest_Success_2() {
        String snomedCd = "code";
        when(labTestRepository.findDefaultConditionForLabTest(snomedCd, "TEST")).thenReturn(Optional.empty());
        var res = srteCodeObsService.getDefaultConditionForLabTest(snomedCd, "TEST");
        assertNotNull(res);
        assertEquals("", res);
    }

    @Test
    void labLoincSnomedLookup_Success_null() {
        ObservationContainer observationContainer = new ObservationContainer();
        ObservationDto obsDt = new ObservationDto();
        obsDt.setCdSystemCd(NEDSSConstant.DEFAULT);
        observationContainer.setTheObservationDto(obsDt);
        String labClia = null;

        var res = srteCodeObsService.labLoincSnomedLookup(observationContainer,labClia);

        assertNotNull(res);
    }

    @Test
    void labLoincSnomedLookup_Success_null_2() {
        ObservationContainer observationContainer = new ObservationContainer();
        ObservationDto obsDt = new ObservationDto();
        obsDt.setCdSystemCd("TEST");
        observationContainer.setTheObservationDto(obsDt);
        String labClia = null;

        var res = srteCodeObsService.labLoincSnomedLookup(observationContainer,labClia);

        assertNotNull(res);
    }


    @Test
    void labLoincSnomedLookup_Success() {
        ObservationContainer observationContainer = new ObservationContainer();
        ObservationDto obsDt = new ObservationDto();
        obsDt.setCdSystemCd(NEDSSConstant.DEFAULT);
        obsDt.setAltCdSystemCd(null);
        obsDt.setCd("CODE");
        String labClia = "CLIA";

        var valueList = new ArrayList<ObsValueCodedDto>();
        ObsValueCodedDto value = new ObsValueCodedDto();
        value.setCodeSystemCd("CODE");
        value.setAltCdSystemCd(null);
        value.setCode("CODE");
        valueList.add(value);
        value = null;
        valueList.add(value);

        observationContainer.setTheObsValueCodedDtoCollection(valueList);
        observationContainer.setTheObservationDto(obsDt);



        var lst = new ArrayList<String>();
        lst.add("TEST");
        when(labTestLoincRepository.findLoincCds(labClia, "CODE")).thenReturn(Optional.of(lst));

        when(labResultSnomedRepository.findSnomedCds(labClia, "CODE")).thenReturn(Optional.of(lst));

        var res = srteCodeObsService.labLoincSnomedLookup(observationContainer,labClia);

        assertNotNull(res);
        assertEquals(2, res.getTheObsValueCodedDtoCollection().size());
    }

    @Test
    void getProgramArea_Success_Null() throws DataProcessingException {
        String reportingLabCLIA = null;
        var observationContainerCollection = new ArrayList<ObservationContainer>();
        String electronicInd = "Y";
        var res = srteCodeObsService.getProgramArea(reportingLabCLIA,
                observationContainerCollection, electronicInd);
        assertNotNull(res);
    }

    @Test
    void getProgramArea_Success_Electronic_Found_1() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        var observationContainerCollection = new ArrayList<ObservationContainer>();
        String electronicInd = NEDSSConstant.ELECTRONIC_IND_ELR;

        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCd("TEST");
        obsDt.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsConn.setTheObservationDto(obsDt);
        var obsCodedValueCol = new ArrayList<ObsValueCodedDto>();
        var obsCodedValue = new ObsValueCodedDto();

        // (!codeSystemCd.equals(ELRConstant.ELR_SNOMED_CD))
        obsCodedValue.setCodeSystemCd("CODE");
        obsCodedValue.setCode("CODE");
        obsCodedValueCol.add(obsCodedValue);
        var labResultLst = new ArrayList<LabResult>();
        var labResult = new LabResult();
        labResult.setPaDerivationExcludeCd(NEDSSConstant.NO);
        labResultLst.add(labResult);
        when(labResultRepository.findLabResultProgramAreaExclusion(any(), eq(reportingLabCLIA)))
                .thenReturn(Optional.of(labResultLst));

        var snomedMap = new HashMap<String, Object>();
        snomedMap.put("COUNT", 1);
        snomedMap.put("LOINC", "TEST");

        when(progAreaSnomeCodeStoredProcRepository.getSnomed("CODE", "LR" ,"CLIA"))
                .thenReturn(snomedMap);


        obsCodedValue = new ObsValueCodedDto();
        obsCodedValue.setCodeSystemCd("SNM");
        obsCodedValue.setCode("SNM");
        obsCodedValueCol.add(obsCodedValue);

        var snomedLst = new ArrayList<SnomedCode>();
        var snomed = new SnomedCode();
        snomed.setPaDerivationExcludeCd("BLAH");
        snomedLst.add(snomed);
        when(snomedCodeRepository.findSnomedProgramAreaExclusion("SNM"))
                .thenReturn(Optional.of(snomedLst));

        var progMap = new HashMap<String, Object>();
        progMap.put("COUNT", 1);
        progMap.put("PROGRAM", "TEST");
        when(progAreaSnomeCodeStoredProcRepository.getProgAreaCd(any(), any(), any()))
                .thenReturn(progMap);


        obsConn.setTheObsValueCodedDtoCollection(obsCodedValueCol);
        observationContainerCollection.add(obsConn);

        var res = srteCodeObsService.getProgramArea(reportingLabCLIA,
                observationContainerCollection, electronicInd);

        assertNotNull(res);
        assertEquals(1, res.size());

    }

    @Test
    void getProgramArea_Success_Electronic_Found_2_LOINC() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        var observationContainerCollection = new ArrayList<ObservationContainer>();
        String electronicInd = NEDSSConstant.ELECTRONIC_IND_ELR;

        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCd("TEST");
        obsDt.setCdSystemCd(ELRConstant.ELR_OBSERVATION_LOINC);
        obsDt.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsConn.setTheObservationDto(obsDt);
        var obsCodedValueCol = new ArrayList<ObsValueCodedDto>();
        var obsCodedValue = new ObsValueCodedDto();

        // (!codeSystemCd.equals(ELRConstant.ELR_SNOMED_CD))
        obsCodedValue.setCodeSystemCd("CODE");
        obsCodedValue.setCode("CODE");
        obsCodedValueCol.add(obsCodedValue);
        var labResultLst = new ArrayList<LabResult>();
        var labResult = new LabResult();
        labResult.setPaDerivationExcludeCd(NEDSSConstant.NO);
        labResultLst.add(labResult);
        when(labResultRepository.findLabResultProgramAreaExclusion(any(), eq(reportingLabCLIA)))
                .thenReturn(Optional.of(labResultLst));

        var snomedMap = new HashMap<String, Object>();
        snomedMap.put("NULL", 1);
        snomedMap.put("LOINC", "TEST");

        when(progAreaSnomeCodeStoredProcRepository.getSnomed("CODE", "LR" ,"CLIA"))
                .thenReturn(snomedMap);


        obsCodedValue = new ObsValueCodedDto();
        obsCodedValue.setCodeSystemCd("SNM");
        obsCodedValue.setCode("SNM");
        obsCodedValueCol.add(obsCodedValue);

        var snomedLst = new ArrayList<SnomedCode>();
        var snomed = new SnomedCode();
        snomed.setPaDerivationExcludeCd(NEDSSConstant.YES);
        snomedLst.add(snomed);
        when(snomedCodeRepository.findSnomedProgramAreaExclusion("SNM"))
                .thenReturn(Optional.of(snomedLst));

        var progMap = new HashMap<String, Object>();
        progMap.put("COUNT", 1);
        progMap.put("PROGRAM", "TEST");
        when(progAreaSnomeCodeStoredProcRepository.getProgAreaCd(any(), any(), any()))
                .thenReturn(progMap);


        obsConn.setTheObsValueCodedDtoCollection(obsCodedValueCol);
        observationContainerCollection.add(obsConn);


        //FOUND 2

        var res = srteCodeObsService.getProgramArea(reportingLabCLIA,
                observationContainerCollection, electronicInd);

        assertNotNull(res);
        assertEquals(1, res.size());

    }


    @Test
    void getPAFromSNOMEDCodes_ContinuesCase_Null() throws DataProcessingException {
        String reportingLabCLIA = null;
        var res = srteCodeObsService.getPAFromSNOMEDCodes(reportingLabCLIA, new ArrayList<>());
        assertNull(res);
    }

    @Test
    void getPAFromSNOMEDCodes_ContinuesCase_1() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        var lstObsValue = new ArrayList<ObsValueCodedDto>();
        var obsValue = new ObsValueCodedDto();
        obsValue.setCodeSystemCd(null);
        lstObsValue.add(obsValue);
        var res = srteCodeObsService.getPAFromSNOMEDCodes(reportingLabCLIA, lstObsValue);
        assertNull(res);
    }

    @Test
    void getPAFromSNOMEDCodes_ContinuesCase_2() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        var lstObsValue = new ArrayList<ObsValueCodedDto>();
        var obsValue = new ObsValueCodedDto();
        obsValue.setCodeSystemCd("CODE");
        obsValue.setCode(null);
        lstObsValue.add(obsValue);
        var res = srteCodeObsService.getPAFromSNOMEDCodes(reportingLabCLIA, lstObsValue);
        assertNull(res);
    }

    @Test
    void getPAFromSNOMEDCodes_ProgExclude() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        var lstObsValue = new ArrayList<ObsValueCodedDto>();
        var obsValue = new ObsValueCodedDto();
        obsValue.setCodeSystemCd("CODE");
        obsValue.setCode("CODE");
        lstObsValue.add(obsValue);
        var labResultLst = new ArrayList<LabResult>();
        var labResult = new LabResult();
        labResult.setPaDerivationExcludeCd((NEDSSConstant.YES));
        labResultLst.add(labResult);
        when(labResultRepository.findLabResultProgramAreaExclusion(any(), any())).thenReturn(Optional.of(labResultLst));

        var res = srteCodeObsService.getPAFromSNOMEDCodes(reportingLabCLIA, lstObsValue);
        assertNull(res);
    }


    @Test
    void getProgAreaCd_Test_Null() {
        Vector<Object> codeVector = null;
        String reportingLabCLIA = null;
        String nextLookUp = null;
        String type = null;

        var res = srteCodeObsService.getProgAreaCd(codeVector, reportingLabCLIA, nextLookUp, type);
        assertNull(res);

    }

    @Test
    void getProgAreaCd_Test_Null_2() throws DataProcessingException {
        Vector<Object> codeVector = new Vector<>();
        String reportingLabCLIA = null;
        String nextLookUp = null;
        String type = null;

        codeVector.add("TEST");

        when(progAreaSnomeCodeStoredProcRepository.getProgAreaCd(any(), any(), any()))
                .thenReturn(new HashMap<>());

        var res = srteCodeObsService.getProgAreaCd(codeVector, reportingLabCLIA, nextLookUp, type);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_Null() throws DataProcessingException {
        String reportingLabCLIA = null;
        ObservationContainer resultTestVO = new ObservationContainer();

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_Null_2() throws DataProcessingException {
        String reportingLabCLIA = null;
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        resultTestVO.setTheObservationDto(obsDt);

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_Null_3() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        resultTestVO.setTheObservationDto(obsDt);

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_Null_4() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd("");
        resultTestVO.setTheObservationDto(obsDt);

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_Null_5() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd("CODE");
        resultTestVO.setTheObservationDto(obsDt);

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_Null_6() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd("CODE");
        obsDt.setCd("");
        resultTestVO.setTheObservationDto(obsDt);

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_Null_7() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd(ELRConstant.ELR_OBSERVATION_LOINC);
        obsDt.setCd("CODE");
        resultTestVO.setTheObservationDto(obsDt);

        var loincLst = new ArrayList<LOINCCode>();
        var loin = new LOINCCode();
        loin.setPaDerivationExcludeCode(NEDSSConstant.YES);
        loincLst.add(loin);
        when(loincCodeRepository.findLoinCCodeExclusion("CODE")).thenReturn(Optional.of(loincLst));

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_9_Null() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd(ELRConstant.ELR_OBSERVATION_LOINC);
        obsDt.setCd("CODE");
        resultTestVO.setTheObservationDto(obsDt);


        when(loincCodeRepository.findLoinCCodeExclusion("CODE")).thenReturn(Optional.empty());

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLOINCCode_Null_8() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd("BLAH");
        obsDt.setCd("CODE");
        resultTestVO.setTheObservationDto(obsDt);

        var loincLst = new ArrayList<LabTest>();
        var loin = new LabTest();
        loin.setPaDerivationExcludeCd(NEDSSConstant.YES);
        loincLst.add(loin);
        when(labTestRepository.findLabTestForExclusion(any(), any())).thenReturn(Optional.of(loincLst));

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }



    @Test
    void getPAFromLOINCCode_Null_9() throws DataProcessingException {
        String reportingLabCLIA = "CLIA";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd("BLAH");
        obsDt.setCd("CODE");
        resultTestVO.setTheObservationDto(obsDt);


        when(labTestRepository.findLabTestForExclusion(any(), any())).thenReturn(Optional.empty());

        var res = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, resultTestVO);
        assertNull(res);
    }

    @Test
    void getPAFromLocalResultCode_Null() {
        String reportingLabCLIA = "TEST";
        Collection<ObsValueCodedDto> obsValueCodedDtoColl = null;


        var res = srteCodeObsService.getPAFromLocalResultCode(reportingLabCLIA, obsValueCodedDtoColl);
        assertNull(res);
    }

    @Test
    void getPAFromLocalResultCode_Null_2() {
        String reportingLabCLIA = null;
        Collection<ObsValueCodedDto> obsValueCodedDtoColl = new ArrayList<>();

        var res = srteCodeObsService.getPAFromLocalResultCode(reportingLabCLIA, obsValueCodedDtoColl);
        assertNull(res);
    }

    @Test
    void getPAFromLocalResultCode_Success_Null() {
        String reportingLabCLIA = "CLIA";
        Collection<ObsValueCodedDto> obsValueCodedDtoColl = new ArrayList<>();
        var obsValue = new ObsValueCodedDto();
        obsValue.setCode("CODE");
        obsValue.setCodeSystemCd("CODE");
        obsValueCodedDtoColl.add(obsValue);

        var loincLst = new ArrayList<LabResult>();
        var loin = new LabResult();
        loin.setPaDerivationExcludeCd(NEDSSConstant.YES);
        loincLst.add(loin);
        when(labResultRepository.findLabResultProgramAreaExclusion(any(), any())).thenReturn(Optional.of(loincLst));


        var res = srteCodeObsService.getPAFromLocalResultCode(reportingLabCLIA, obsValueCodedDtoColl);
        assertNull(res);
    }



    @Test
    void getPAFromLocalResultCode_Success_NotNull() {
        String reportingLabCLIA = "CLIA";
        Collection<ObsValueCodedDto> obsValueCodedDtoColl = new ArrayList<>();
        var obsValue = new ObsValueCodedDto();
        obsValue.setCode("CODE");
        obsValue.setCodeSystemCd("CODE");
        obsValueCodedDtoColl.add(obsValue);

        var loincLst = new ArrayList<LabResult>();
        var loin = new LabResult();
        loin.setPaDerivationExcludeCd(null);
        loincLst.add(loin);
        when(labResultRepository.findLabResultProgramAreaExclusion(any(), any())).thenReturn(Optional.of(loincLst));

        var strLst = new ArrayList<String>();
        var str = "TEST";
        strLst.add(str);
        when(conditionCodeRepository.findConditionCodeByLabResultLabIdAndCd(any(), any()))
                .thenReturn(Optional.of(strLst));


        var res = srteCodeObsService.getPAFromLocalResultCode(reportingLabCLIA, obsValueCodedDtoColl);
        assertEquals("TEST", res);
    }

    @Test
    void getPAFromLocalResultCode_Success_NotNull_2() {
        String reportingLabCLIA = "CLIA";
        Collection<ObsValueCodedDto> obsValueCodedDtoColl = new ArrayList<>();
        var obsValue = new ObsValueCodedDto();
        obsValue.setCode("CODE");
        obsValue.setCodeSystemCd("CODE");
        obsValueCodedDtoColl.add(obsValue);

        var loincLst = new ArrayList<LabResult>();
        var loin = new LabResult();
        loin.setPaDerivationExcludeCd(null);
        loincLst.add(loin);
        when(labResultRepository.findLabResultProgramAreaExclusion(any(), any())).thenReturn(Optional.of(loincLst));

        var strLst = new ArrayList<String>();
        var str = "TEST";
        strLst.add(str);
        when(labResultRepository.findLocalResultDefaultProgramAreaCd(any(), any()))
                .thenReturn(Optional.of(strLst));


        var res = srteCodeObsService.getPAFromLocalResultCode(reportingLabCLIA, obsValueCodedDtoColl);
        assertEquals("TEST", res);
    }


    @Test
    void getPAFromLocalTestCode_Null() {
        String reportingLabCLIA = null;
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        resultTestVO.setTheObservationDto(obsDt);

        var res = srteCodeObsService.getPAFromLocalTestCode(reportingLabCLIA, resultTestVO);

        assertNull(res);
    }

    @Test
    void getPAFromLocalTestCode_Null_2() {
        String reportingLabCLIA = "TEST";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        resultTestVO.setTheObservationDto(obsDt);

        var res = srteCodeObsService.getPAFromLocalTestCode(reportingLabCLIA, resultTestVO);

        assertNull(res);
    }

    @Test
    void getPAFromLocalTestCode_Null_3() {
        String reportingLabCLIA = "TEST";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd("TEST");
        obsDt.setCd("TEST");
        resultTestVO.setTheObservationDto(obsDt);


        var loincLst = new ArrayList<LabTest>();
        var loin = new LabTest();
        loin.setPaDerivationExcludeCd(NEDSSConstant.YES);
        loincLst.add(loin);
        when(labTestRepository.findLabTestForExclusion(any(), any())).thenReturn(Optional.of(loincLst));


        var res = srteCodeObsService.getPAFromLocalTestCode(reportingLabCLIA, resultTestVO);

        assertNull(res);
    }

    @Test
    void getPAFromLocalTestCode_NotNull_1() {
        String reportingLabCLIA = "TEST";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd("TEST");
        obsDt.setCd("TEST");
        resultTestVO.setTheObservationDto(obsDt);


        var loincLst = new ArrayList<LabTest>();
        var loin = new LabTest();
        loin.setPaDerivationExcludeCd(NEDSSConstant.NO);
        loincLst.add(loin);
        when(labTestRepository.findLabTestForExclusion(any(), any())).thenReturn(Optional.of(loincLst));

        var strLst = new ArrayList<String>();
        var str = "TEST";
        strLst.add(str);
        when(conditionCodeRepository.findLocalTestDefaultConditionProgramAreaCd(any(), any()))
                .thenReturn(Optional.of(strLst));

        var res = srteCodeObsService.getPAFromLocalTestCode(reportingLabCLIA, resultTestVO);

        assertEquals("TEST",res);
    }

    @Test
    void getPAFromLocalTestCode_NotNull_2() {
        String reportingLabCLIA = "TEST";
        ObservationContainer resultTestVO = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCdSystemCd("TEST");
        obsDt.setCd("TEST");
        resultTestVO.setTheObservationDto(obsDt);


        var loincLst = new ArrayList<LabTest>();
        var loin = new LabTest();
        loin.setPaDerivationExcludeCd(NEDSSConstant.NO);
        loincLst.add(loin);
        when(labTestRepository.findLabTestForExclusion(any(), any())).thenReturn(Optional.of(loincLst));

        var strLst = new ArrayList<String>();
        var str = "TEST";
        strLst.add(str);
        when(conditionCodeRepository.findLocalTestDefaultConditionProgramAreaCd(any(), any()))
                .thenReturn(Optional.empty());

        when(labTestRepository.findLocalTestDefaultProgramAreaCd(any(), any()))
                .thenReturn(Optional.of(strLst));

        var res = srteCodeObsService.getPAFromLocalTestCode(reportingLabCLIA, resultTestVO);

        assertEquals("TEST",res);
    }






}
