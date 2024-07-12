package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.srte.model.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CachingValueServiceTest {
    @Mock
    private JurisdictionCodeRepository jurisdictionCodeRepository;
    @Mock
    private CodeValueGeneralRepository codeValueGeneralRepository;
    @Mock
    private ElrXrefRepository elrXrefRepository;
    @Mock
    private RaceCodeRepository raceCodeRepository;
    @Mock
    private StateCountyCodeValueRepository stateCountyCodeValueRepository;
    @Mock
    private StateCodeRepository stateCodeRepository;
    @Mock
    private LOINCCodeRepository loincCodeRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private IProgramAreaService programAreaService;
    @Mock
    private IJurisdictionService jurisdictionService;
    @Mock
    private ConditionCodeRepository conditionCodeRepository;
    @Mock
    private LabResultRepository labResultRepository;
    @Mock
    private SnomedCodeRepository snomedCodeRepository;
    @Mock
    private SrteCustomRepository srteCustomRepository;

    @Mock
    private Cache cache;

    @Mock
    private Cache.ValueWrapper valueWrapper;

    @InjectMocks
    private CachingValueService cachingValueService;
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
        SrteCache.codedValuesMap.clear();
        SrteCache.codeDescTxtMap.clear();
        SrteCache.countyCodeByDescMap.clear();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(cache, valueWrapper,
                jurisdictionCodeRepository, codeValueGeneralRepository,elrXrefRepository, raceCodeRepository,
                stateCountyCodeValueRepository, stateCodeRepository, loincCodeRepository,cacheManager,
                programAreaService, jurisdictionService, conditionCodeRepository, labResultRepository,
                snomedCodeRepository, srteCustomRepository, authUtil);
    }

    @Test
    void getAllLoinCodeWithComponentName_1 () throws DataProcessingException {
        var lstRes = new ArrayList<LOINCCode>();
        var code = new LOINCCode();
        code.setLoincCode("TEST");
        code.setComponentName("TEST");
        lstRes.add(code);
        when(loincCodeRepository.findAll()).thenReturn(lstRes);
        var res = cachingValueService.getAllLoinCodeWithComponentName();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getAllLoinCodeWithComponentName_Exception () {
        when(loincCodeRepository.findAll()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllLoinCodeWithComponentName();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd_Success () throws DataProcessingException {
        var lstRes = new ArrayList<LabResult>();
        var code = new LabResult();
        code.setLabResultCd("TEST");
        code.setLabResultDescTxt("TEST");
        lstRes.add(code);
        when(srteCustomRepository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd()).thenReturn(lstRes);
        var res = cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd_Exception () {
        when(srteCustomRepository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getAllSnomedCode_Success () throws DataProcessingException {
        var lstRes = new ArrayList<SnomedCode>();
        var code = new SnomedCode();
        code.setSnomedCd("TEST");
        code.setSnomedDescTxt("TEST");
        lstRes.add(code);
        when(snomedCodeRepository.findAll()).thenReturn(lstRes);
        var res = cachingValueService.getAllSnomedCode();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getAllSnomedCode_Exception () {
        when(snomedCodeRepository.findAll()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllSnomedCode();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getLabResultDesc_Success () throws DataProcessingException {
        var lstRes = new ArrayList<LabResult>();
        var code = new LabResult();
        code.setLabResultCd("TEST");
        code.setLabResultDescTxt("TEST");
        lstRes.add(code);
        when(labResultRepository.findLabResultByDefaultLabAndOrgNameN()).thenReturn(Optional.of(lstRes));
        var res = cachingValueService.getLabResultDesc();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getLabResultDesc_Exception () {
        when(labResultRepository.findLabResultByDefaultLabAndOrgNameN()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getLabResultDesc();
        });
        assertEquals("TEST", thrown.getMessage());
    }


    @Test
    void getAOELOINCCodes_Success () throws DataProcessingException {
        var lstRes = new ArrayList<LOINCCode>();
        var code = new LOINCCode();
        code.setLoincCode("TEST");
        code.setLoincCode("TEST");
        lstRes.add(code);
        when(loincCodeRepository.findLoincCodes()).thenReturn(Optional.of(lstRes));
        var res = cachingValueService.getAOELOINCCodes();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getAOELOINCCodesException () {
        when(loincCodeRepository.findLoincCodes()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAOELOINCCodes();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getRaceCodes_Success () throws DataProcessingException {
        var lstRes = new ArrayList<RaceCode>();
        var code = new RaceCode();
        code.setCode("TEST");
        code.setCodeShortDescTxt("TEST");
        lstRes.add(code);
        when(raceCodeRepository.findAllActiveRaceCodes()).thenReturn(Optional.of(lstRes));
        var res = cachingValueService.getRaceCodes();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getRaceCodes_Exception () {
        when(raceCodeRepository.findAllActiveRaceCodes()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getRaceCodes();
        });
        assertEquals("TEST", thrown.getMessage());
    }


    @Test
    void getAllProgramAreaCodes_Success () throws DataProcessingException {
        var lstRes = new ArrayList<ProgramAreaCode>();
        var code = new ProgramAreaCode();
        code.setProgAreaCd("TEST");
        code.setProgAreaDescTxt("TEST");
        lstRes.add(code);
        when(programAreaService.getAllProgramAreaCode()).thenReturn(lstRes);
        var res = cachingValueService.getAllProgramAreaCodes();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getAllProgramAreaCodes_Exception () {
        when(programAreaService.getAllProgramAreaCode()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllProgramAreaCodes();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getAllProgramAreaCodesWithNbsUid_Success () throws DataProcessingException {
        var lstRes = new ArrayList<ProgramAreaCode>();
        var code = new ProgramAreaCode();
        code.setProgAreaCd("TEST");
        code.setNbsUid(1);
        lstRes.add(code);
        when(programAreaService.getAllProgramAreaCode()).thenReturn(lstRes);
        var res = cachingValueService.getAllProgramAreaCodesWithNbsUid();
        assertEquals(1, res.get("TEST"));
    }

    @Test
    void getAllProgramAreaCodesWithNbsUid_Exception () {
        when(programAreaService.getAllProgramAreaCode()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllProgramAreaCodesWithNbsUid();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getAllJurisdictionCode_Success () throws DataProcessingException {
        var lstRes = new ArrayList<JurisdictionCode>();
        var code = new JurisdictionCode();
        code.setCode("TEST");
        code.setCodeDescTxt("TEST");
        lstRes.add(code);
        when(jurisdictionService.getJurisdictionCode()).thenReturn(lstRes);
        var res = cachingValueService.getAllJurisdictionCode();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getAllJurisdictionCode_Exception () {
        when(jurisdictionService.getJurisdictionCode()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllJurisdictionCode();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getAllJurisdictionCodeWithNbsUid_Success () throws DataProcessingException {
        var lstRes = new ArrayList<JurisdictionCode>();
        var code = new JurisdictionCode();
        code.setCode("TEST");
        code.setNbsUid(1);
        lstRes.add(code);
        when(jurisdictionService.getJurisdictionCode()).thenReturn(lstRes);
        var res = cachingValueService.getAllJurisdictionCodeWithNbsUid();
        assertEquals(1, res.get("TEST"));
    }

    @Test
    void getAllJurisdictionCodeWithNbsUid_Exception () {
        when(jurisdictionService.getJurisdictionCode()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllJurisdictionCodeWithNbsUid();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getAllElrXref_Success () throws DataProcessingException {
        var lstRes = new ArrayList<ElrXref>();
        var code = new ElrXref();
        code.setToCode("TEST");
        code.setNbsUid(1);
        lstRes.add(code);
        when(elrXrefRepository.findAll()).thenReturn(lstRes);
        var res = cachingValueService.getAllElrXref();
        assertEquals(1, res.size());
    }

    @Test
    void getAllElrXref_Exception () {
        when(elrXrefRepository.findAll()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllElrXref();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getAllOnInfectionConditionCode_Success () throws DataProcessingException {
        var lstRes = new ArrayList<ConditionCode>();
        var code = new ConditionCode();
        code.setConditionCd("TEST");
        code.setCoinfectionGrpCd("TEST");
        lstRes.add(code);
        when(conditionCodeRepository.findCoInfectionConditionCode()).thenReturn(Optional.of(lstRes));
        var res = cachingValueService.getAllOnInfectionConditionCode();
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getAllOnInfectionConditionCode_Exception () {
        when(conditionCodeRepository.findCoInfectionConditionCode()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllOnInfectionConditionCode();
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getAllConditionCode_Success () throws DataProcessingException {
        var lstRes = new ArrayList<ConditionCode>();
        var code = new ConditionCode();
        code.setConditionCd("TEST");
        code.setCoinfectionGrpCd("TEST");
        lstRes.add(code);
        when(conditionCodeRepository.findAllConditionCode()).thenReturn(Optional.of(lstRes));
        var res = cachingValueService.getAllConditionCode();
        assertEquals(1, res.size());
    }

    @Test
    void getAllConditionCode_Exception () {
        when(conditionCodeRepository.findAllConditionCode()).thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getAllConditionCode();
        });
        assertEquals("TEST", thrown.getMessage());
    }


    @Test
    void getCodedValues_Cache_Found() throws DataProcessingException {
        String pType = "";
        String key = "key";

        TreeMap<String, String> codeMap = new TreeMap<>();
        codeMap.put("key", "value1");
        SrteCache.codedValuesMap.put("key", "value");

        when(cacheManager.getCache("srte")).thenReturn(cache);
        when(cache.get("codedValues")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(codeMap);

        var res = cachingValueService.getCodedValues(pType, key);

        assertEquals(1, res.size());
    }

    @Test
    void getCodedValues_Cache_Found_2() throws DataProcessingException {
        String pType = "";
        String key = "key";

        TreeMap<String, String> codeMap = new TreeMap<>();
        codeMap.put("DUMP", "value1");
        SrteCache.codedValuesMap.put("key-2", "value");

        when(cacheManager.getCache("srte")).thenReturn(cache);
        when(cache.get("codedValues")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(codeMap);

        var res = cachingValueService.getCodedValues(pType, key);

        assertEquals(1, res.size());
    }

    @Test
    void getCodeDescTxtForCd_Cache_Found() throws DataProcessingException {
        String code = "key";
        String codeSetNm = "key";

        TreeMap<String, String> codeMap = new TreeMap<>();
        codeMap.put("key", "value1");
        SrteCache.codeDescTxtMap.put("key", "value");

        when(cacheManager.getCache("srte")).thenReturn(cache);
        when(cache.get("codeDescTxt")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(codeMap);

        var res = cachingValueService.getCodeDescTxtForCd(code, codeSetNm);

        assertEquals("value1", res);
    }

    @Test
    void getCodeDescTxtForCd_Cache_Found_2() throws DataProcessingException {
        String code = "";
        String codeSetNm = "key";

        TreeMap<String, String> codeMap = new TreeMap<>();
        codeMap.put("DUMP", "value1");
        SrteCache.codeDescTxtMap.put("key-2", "value");

        when(cacheManager.getCache("srte")).thenReturn(cache);
        when(cache.get("codeDescTxt")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(codeMap);

        var res = cachingValueService.getCodeDescTxtForCd(code, codeSetNm);

        assertNull(res);
    }

    @Test
    void findToCode_1() throws DataProcessingException {
        String fromCodeSetNm = "";
        String fromCode = "";
        String toCodeSetNm = "";

        var resElr = new ElrXref();
        resElr.setToCode("TEST");
        when(elrXrefRepository.findToCodeByConditions(fromCodeSetNm, fromCode, toCodeSetNm))
                .thenReturn(Optional.of(resElr));
        var res = cachingValueService.findToCode(fromCodeSetNm, fromCode, toCodeSetNm);
        assertEquals("TEST", res);
    }

    @Test
    void findToCode_2() throws DataProcessingException {
        String fromCodeSetNm = "";
        String fromCode = "";
        String toCodeSetNm = "";

        when(elrXrefRepository.findToCodeByConditions(fromCodeSetNm, fromCode, toCodeSetNm))
                .thenReturn(Optional.empty());
        var res = cachingValueService.findToCode(fromCodeSetNm, fromCode, toCodeSetNm);
        assertEquals("", res);
    }

    @Test
    void findToCode_Exception() {
        String fromCodeSetNm = "";
        String fromCode = "";
        String toCodeSetNm = "";

        when(elrXrefRepository.findToCodeByConditions(fromCodeSetNm, fromCode, toCodeSetNm))
                .thenThrow(new RuntimeException("TEST"));
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.findToCode(fromCodeSetNm, fromCode, toCodeSetNm);
        });
        assertEquals("TEST",thrown.getMessage());
    }

    @Test
    void getCountyCdByDesc_null_1() throws DataProcessingException {
        String county = null;
        String stateCd = "";

        var res = cachingValueService.getCountyCdByDesc(county, stateCd);
        assertNull(res);
    }

    @Test
    void getCountyCdByDesc_null_2() throws DataProcessingException {
        String county = "";
        String stateCd = null;

        var res = cachingValueService.getCountyCdByDesc(county, stateCd);
        assertNull(res);
    }

    @Test
    void getCountyCdByDesc_1() throws DataProcessingException {
        String county = "MARICOPA";
        String stateCd = "ARIZONA";


        TreeMap<String, String> codeMap = new TreeMap<>();
        codeMap.put("MARICOPA COUNTY", "");
        SrteCache.countyCodeByDescMap.put("key-2", "value");

        when(cacheManager.getCache("srte")).thenReturn(cache);
        when(cache.get("countyCodeByDesc")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(codeMap);


        var res = cachingValueService.getCountyCdByDesc(county, stateCd);
        assertEquals("", res);
    }

    @Test
    void getCountyCdByDesc_2() throws DataProcessingException {
        String county = "MARICOPA";
        String stateCd = "ARIZONA";


        TreeMap<String, String> codeMap = new TreeMap<>();
        codeMap.put("MARICOPA COUNTY", "ARIZONA");
        SrteCache.countyCodeByDescMap.put("key-2", "value");

        when(cacheManager.getCache("srte")).thenReturn(cache);
        when(cache.get("countyCodeByDesc")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(codeMap);


        var res = cachingValueService.getCountyCdByDesc(county, stateCd);
        assertEquals("ARIZONA", res);
    }

    @Test
    void findCodeValuesByCodeSetNmAndCode_1() {
        String codeSetNm = "";
        String code = "";
        var lstCode = new ArrayList<CodeValueGeneral>();
        var codeValue = new CodeValueGeneral();
        lstCode.add(codeValue);
        when(codeValueGeneralRepository.findCodeValuesByCodeSetNmAndCode(codeSetNm, code)).thenReturn(Optional.of(lstCode));

        var res = cachingValueService.findCodeValuesByCodeSetNmAndCode(codeSetNm, code);
        assertEquals(1, res.size());
    }

    @Test
    void findCodeValuesByCodeSetNmAndCode_2() {
        String codeSetNm = "";
        String code = "";
        when(codeValueGeneralRepository.findCodeValuesByCodeSetNmAndCode(codeSetNm, code)).thenReturn(Optional.empty());

        var res = cachingValueService.findCodeValuesByCodeSetNmAndCode(codeSetNm, code);
        assertEquals(0, res.size());
    }

    @Test
    void findStateCodeByStateNm_1() {
        String stateNm = "";
        var codeValue = new StateCode();
        codeValue.setCodeDescTxt("TEST");
        when(stateCodeRepository.findStateCdByStateName(stateNm)).thenReturn(Optional.of(codeValue));

        var res = cachingValueService.findStateCodeByStateNm(stateNm);
        assertEquals("TEST",  res.getCodeDescTxt());
    }

    @Test
    void findStateCodeByStateNm_2() {
        String stateNm = "";
        when(stateCodeRepository.findStateCdByStateName(stateNm)).thenReturn(Optional.empty());

        var res = cachingValueService.findStateCodeByStateNm(stateNm);
        assertNull(res.getCodeDescTxt());
    }

    @Test
    void getGeneralCodedValue_1() {
        String code = "";
        var lstCode = new ArrayList<CodeValueGeneral>();
        var codeValue = new CodeValueGeneral();
        lstCode.add(codeValue);
        when(codeValueGeneralRepository.findCodeValuesByCodeSetNm(code)).thenReturn(Optional.of(lstCode));

        var res = cachingValueService.getGeneralCodedValue(code);
        assertEquals(1,  res.size());
    }

    @Test
    void getGeneralCodedValue_2() {
        String code = "";
        when(codeValueGeneralRepository.findCodeValuesByCodeSetNm(code)).thenReturn(Optional.empty());

        var res = cachingValueService.getGeneralCodedValue(code);
        assertEquals(0, res.size());
    }

    @Test
    void getCodedValuesCallRepos_Jus_1() throws DataProcessingException {
        String pType = "S_JURDIC_C";
        var justLst = new ArrayList<JurisdictionCode>();
        var just = new JurisdictionCode();
        just.setCode("TEST");
        just.setCodeDescTxt("TEST");
        justLst.add(just);
        when(jurisdictionCodeRepository.findJurisdictionCodeValues())
                .thenReturn(Optional.of(justLst));

        var res = cachingValueService.getCodedValuesCallRepos(pType);
        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getCodedValuesCallRepos_Jus_Expcetion() {
        String pType = "S_JURDIC_C";
        when(jurisdictionCodeRepository.findJurisdictionCodeValues())
                .thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getCodedValuesCallRepos(pType);
        });
        assertEquals("TEST", thrown.getMessage());
    }


    @Test
    void getCodedValue_1() throws DataProcessingException {
        String code = ELRConstant.ELR_LOG_PROCESS;

        var lstCode = new ArrayList<CodeValueGeneral>();
        var codeVa = new CodeValueGeneral();
        codeVa.setCode("TEST");
        codeVa.setCodeDescTxt("TEST");
        lstCode.add(codeVa);
        when(codeValueGeneralRepository.findCodeDescriptionsByCodeSetNm(code))
                .thenReturn(Optional.of(lstCode));

        var res = cachingValueService.getCodedValue(code);

        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getCodedValue_2() throws DataProcessingException {
        String code = "BLAH";

        var lstCode = new ArrayList<CodeValueGeneral>();
        var codeVa = new CodeValueGeneral();
        codeVa.setCode("TEST");
        codeVa.setCodeShortDescTxt("TEST");
        lstCode.add(codeVa);
        when(codeValueGeneralRepository.findCodeValuesByCodeSetNm(code))
                .thenReturn(Optional.of(lstCode));

        var res = cachingValueService.getCodedValue(code);

        assertEquals("TEST", res.get("TEST"));
    }

    @Test
    void getCodedValue_Exception()  {
        String code = ELRConstant.ELR_LOG_PROCESS;


        when(codeValueGeneralRepository.findCodeDescriptionsByCodeSetNm(code))
                .thenThrow(new RuntimeException("TEST"));


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getCodedValue(code);
        });

        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getCountyCdByDescCallRepos_1() throws DataProcessingException {
        String stateCd = "";

        var lstState = new ArrayList<StateCountyCodeValue>();
        var state = new StateCountyCodeValue();
        state.setCode("TEST");
        state.setAssigningAuthorityDescTxt("TEST");
        lstState.add(state);
        when(stateCountyCodeValueRepository.findByIndentLevelNbr())
                .thenReturn(Optional.of(lstState));

        var res=   cachingValueService.getCountyCdByDescCallRepos(stateCd);

        assertEquals("TEST", res.get("TEST COUNTY"));
    }

    @Test
    void getCountyCdByDescCallRepos_2() throws DataProcessingException {
        String stateCd = "TEST";

        var lstState = new ArrayList<StateCountyCodeValue>();
        var state = new StateCountyCodeValue();
        state.setCode("TEST");
        state.setAssigningAuthorityDescTxt("TEST");
        lstState.add(state);
        when(stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd))
                .thenReturn(Optional.of(lstState));

        var res=   cachingValueService.getCountyCdByDescCallRepos(stateCd);

        assertEquals("TEST", res.get("TEST COUNTY"));
    }

    @Test
    void getCountyCdByDescCallRepos_Exception() {
        String stateCd = "";

        when(stateCountyCodeValueRepository.findByIndentLevelNbr())
                .thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            cachingValueService.getCountyCdByDescCallRepos(stateCd);
        });
        assertEquals("TEST", thrown.getMessage());
    }


    @Test
    void testGetAllLabResultJoinWithLabCodingSystemWithOrganismNameInd_EmptyResult() throws DataProcessingException {
        // Mock the repository to return an empty list
        when(srteCustomRepository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd()).thenReturn(Collections.emptyList());

        // Call the method
        TreeMap<String, String> result = cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();

        // Verify that the result is an empty TreeMap
        assertTrue(result.isEmpty());

        // Verify that the repository method was called
        verify(srteCustomRepository, times(1)).getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
    }


    @Test
    void testGetAllSnomedCode_EmptyResult() throws DataProcessingException {
        when(snomedCodeRepository.findAll()).thenReturn(Collections.emptyList());

        TreeMap<String, String> result = cachingValueService.getAllSnomedCode();

        assertTrue(result.isEmpty());
        verify(snomedCodeRepository, times(1)).findAll();
    }

    @Test
    void testGetLabResultDesc_EmptyResult() throws DataProcessingException {
        when(labResultRepository.findLabResultByDefaultLabAndOrgNameN()).thenReturn(Optional.empty());

        TreeMap<String, String> result = cachingValueService.getLabResultDesc();

        assertTrue(result.isEmpty());
        verify(labResultRepository, times(1)).findLabResultByDefaultLabAndOrgNameN();
    }

    @Test
    void testGetAOELOINCCodes_EmptyResult() throws DataProcessingException {
        when(loincCodeRepository.findLoincCodes()).thenReturn(Optional.empty());

        TreeMap<String, String> result = cachingValueService.getAOELOINCCodes();

        assertTrue(result.isEmpty());
        verify(loincCodeRepository, times(1)).findLoincCodes();
    }

    @Test
    void testGetRaceCodes_EmptyResult() throws DataProcessingException {
        when(raceCodeRepository.findAllActiveRaceCodes()).thenReturn(Optional.empty());

        TreeMap<String, String> result = cachingValueService.getRaceCodes();

        assertTrue(result.isEmpty());
        verify(raceCodeRepository, times(1)).findAllActiveRaceCodes();
    }

    @Test
    void testGetAllLoinCodeWithComponentName_EmptyResult() throws DataProcessingException {
        when(loincCodeRepository.findAll()).thenReturn(Collections.emptyList());

        TreeMap<String, String> result = cachingValueService.getAllLoinCodeWithComponentName();

        assertTrue(result.isEmpty());
        verify(loincCodeRepository, times(1)).findAll();
    }

    @Test
    void testGetAllOnInfectionConditionCode_EmptyResult() throws DataProcessingException {
        when(conditionCodeRepository.findCoInfectionConditionCode()).thenReturn(Optional.of(Collections.emptyList()));

        TreeMap<String, String> result = cachingValueService.getAllOnInfectionConditionCode();

        assertTrue(result.isEmpty());
        verify(conditionCodeRepository, times(1)).findCoInfectionConditionCode();
    }

    @Test
    void testGetAllConditionCode_EmptyResult() throws DataProcessingException {
        when(conditionCodeRepository.findAllConditionCode()).thenReturn(Optional.of(Collections.emptyList()));

        List<ConditionCode> result = cachingValueService.getAllConditionCode();

        assertTrue(result.isEmpty());
        verify(conditionCodeRepository, times(1)).findAllConditionCode();
    }
}
