
package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.cache.cache_model.SrteCache;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.CodeValueJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CachingValueServiceTest {

    @Mock
    private CodeValueJdbcRepository codeValueJdbcRepository;
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
    private ConditionCodeRepository conditionCodeRepository;
    @Mock
    private LabResultRepository labResultRepository;
    @Mock
    private SnomedCodeRepository snomedCodeRepository;
    @Mock
    private JurisdictionCodeRepository jurisdictionCodeRepository;
    @Mock
    private SrteCustomRepository srteCustomRepository;
    @Mock
    private IProgramAreaService programAreaService;
    @Mock
    private IJurisdictionService jurisdictionService;

    @InjectMocks
    private CachingValueService cachingValueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllLoinCodeWithComponentName() {
        LOINCCode loinc = new LOINCCode();
        loinc.setLoincCode("123");
        loinc.setComponentName("Comp");
        when(loincCodeRepository.findAll()).thenReturn(List.of(loinc));
        var result = cachingValueService.getAllLoinCodeWithComponentName();
        assertEquals("Comp", result.get("123"));
    }

    @Test
    void testGetAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() {
        LabResult lab = new LabResult();
        lab.setLabResultCd("code");
        lab.setLabResultDescTxt("desc");
        when(srteCustomRepository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd())
                .thenReturn(List.of(lab));
        var result = cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
        assertEquals("desc", result.get("code"));
    }

    @Test
    void testGetAllSnomedCode() {
        SnomedCode snomed = new SnomedCode();
        snomed.setSnomedCd("001");
        snomed.setSnomedDescTxt("sdesc");
        when(snomedCodeRepository.findAll()).thenReturn(List.of(snomed));
        var result = cachingValueService.getAllSnomedCode();
        assertEquals("sdesc", result.get("001"));
    }

    @Test
    void testFindToCode() throws DataProcessingException {
        ElrXref xref = new ElrXref();
        xref.setToCode("tgt");
        when(elrXrefRepository.findToCodeByConditions("a", "b", "c")).thenReturn(Optional.of(xref));
        assertEquals("tgt", cachingValueService.findToCode("a", "b", "c"));
    }

    @Test
    void testGetAOELOINCCodes() {
        LOINCCode loinc = new LOINCCode();
        loinc.setLoincCode("xyz");
        when(loincCodeRepository.findLoincCodes()).thenReturn(Optional.of(List.of(loinc)));
        var result = cachingValueService.getAOELOINCCodes();
        assertEquals("xyz", result.get("xyz"));
    }

    @Test
    void testGetAllProgramAreaCodes() {
        ProgramAreaCode pac = new ProgramAreaCode();
        pac.setProgAreaCd("abc");
        pac.setProgAreaDescTxt("desc");
        when(programAreaService.getAllProgramAreaCode()).thenReturn(List.of(pac));
        var result = cachingValueService.getAllProgramAreaCodes();
        assertEquals("desc", result.get("abc"));
    }

    @Test
    void testGetAllProgramAreaCodesWithNbsUid() {
        ProgramAreaCode pac = new ProgramAreaCode();
        pac.setProgAreaCd("abc");
        pac.setNbsUid(100);
        when(programAreaService.getAllProgramAreaCode()).thenReturn(List.of(pac));
        var result = cachingValueService.getAllProgramAreaCodesWithNbsUid();
        assertEquals(100, result.get("abc"));
    }

    @Test
    void testGetAllJurisdictionCode() {
        JurisdictionCode jc = new JurisdictionCode();
        jc.setCode("J1");
        jc.setCodeDescTxt("JDesc");
        when(jurisdictionService.getJurisdictionCode()).thenReturn(List.of(jc));
        var result = cachingValueService.getAllJurisdictionCode();
        assertEquals("JDesc", result.get("J1"));
    }

    @Test
    void testGetAllJurisdictionCodeWithNbsUid() {
        JurisdictionCode jc = new JurisdictionCode();
        jc.setCode("J2");
        jc.setNbsUid(200);
        when(jurisdictionService.getJurisdictionCode()).thenReturn(List.of(jc));
        var result = cachingValueService.getAllJurisdictionCodeWithNbsUid();
        assertEquals(200, result.get("J2"));
    }

    @Test
    void testGetAllElrXref() {
        ElrXref x = new ElrXref();
        when(elrXrefRepository.findAll()).thenReturn(List.of(x));
        var result = cachingValueService.getAllElrXref();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllOnInfectionConditionCode() {
        ConditionCode c = new ConditionCode();
        c.setConditionCd("cond");
        c.setCoinfectionGrpCd("grp");
        when(conditionCodeRepository.findCoInfectionConditionCode()).thenReturn(Optional.of(List.of(c)));
        var result = cachingValueService.getAllOnInfectionConditionCode();
        assertEquals("grp", result.get("cond"));
    }

    @Test
    void testFindCodeValuesByCodeSetNmAndCode() {
        CodeValueGeneral cv = new CodeValueGeneral();
        cv.setCode("C1");
        when(codeValueJdbcRepository.findCodeValuesByCodeSetNmAndCode("set", "C1")).thenReturn(List.of(cv));
        var result = cachingValueService.findCodeValuesByCodeSetNmAndCode("set", "C1");
        assertEquals(1, result.size());
    }

    @Test
    void testFindStateCodeByStateNm() {
        StateCode stateCode = new StateCode();
        when(stateCodeRepository.findStateCdByStateName("NY")).thenReturn(Optional.of(stateCode));
        var result = cachingValueService.findStateCodeByStateNm("NY");
        assertNotNull(result);
    }

    @Test
    void testGetLabResultDesc() {
        LabResult lab = new LabResult();
        lab.setLabResultCd("001");
        lab.setLabResultDescTxt("Description");
        when(labResultRepository.findLabResultByDefaultLabAndOrgNameN()).thenReturn(Optional.of(List.of(lab)));

        HashMap<String, String> result = cachingValueService.getLabResultDesc();
        assertEquals("Description", result.get("001"));
    }

    @Test
    void testGetRaceCodes() {
        RaceCode race = new RaceCode();
        race.setCode("A");
        race.setCodeShortDescTxt("RaceA");
        when(raceCodeRepository.findAllActiveRaceCodes()).thenReturn(Optional.of(List.of(race)));

        HashMap<String, String> result = cachingValueService.getRaceCodes();
        assertEquals("RaceA", result.get("A"));
    }

    @Test
    void testGetAllConditionCode() {
        ConditionCode cc = new ConditionCode();
        cc.setConditionCd("COND1");
        when(conditionCodeRepository.findAllConditionCode()).thenReturn(Optional.of(List.of(cc)));

        List<ConditionCode> result = cachingValueService.getAllConditionCode();
        assertEquals(1, result.size());
        assertEquals("COND1", result.get(0).getConditionCd());
    }

    @Test
    void testGetCodedValues() {
        CodeValueGeneral codeValue = new CodeValueGeneral();
        codeValue.setCode("key1");
        codeValue.setCodeShortDescTxt("desc1");

        when(codeValueJdbcRepository.findCodeValuesByCodeSetNm("type1"))
                .thenReturn(List.of(codeValue));

        HashMap<String, String> codedMap = cachingValueService.getCodedValues("type1", "key1");
        assertNotNull(codedMap);
        assertEquals("desc1", codedMap.get("key1"));
    }

    @Test
    void testGetCountyCdByDescFromCache() {
        SrteCache.countyCodeByDescMap.put("MARICOPA COUNTY", "001");

        String result = cachingValueService.getCountyCdByDesc("Maricopa", "AZ");
        assertEquals("001", result);
    }

    @Test
    void testGetCountyCdByDescFromRepo() {
        SrteCache.countyCodeByDescMap.clear();

        StateCountyCodeValue scv = new StateCountyCodeValue();
        scv.setCode("PIMA"); // Must match input county for proper key: "PIMA COUNTY"
        scv.setAssigningAuthorityDescTxt("002");

        when(stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt("AZ"))
                .thenReturn(Optional.of(List.of(scv)));

        String result = cachingValueService.getCountyCdByDesc("Pima", "AZ");

        assertEquals("002", result); // Now will pass
    }


    @Test
    void testFindCodeValuesByCodeSetNmAndCodeEmpty() {
        when(codeValueJdbcRepository.findCodeValuesByCodeSetNmAndCode("set", "X"))
                .thenReturn(Collections.emptyList());

        List<CodeValueGeneral> result = cachingValueService.findCodeValuesByCodeSetNmAndCode("set", "X");
        assertTrue(result.isEmpty());
    }


    @Test
    void testGetCodedValuesCallReposJurisdiction() {
        JurisdictionCode code = new JurisdictionCode();
        code.setCode("J1");
        code.setCodeDescTxt("Jurisdiction Desc");
        when(jurisdictionCodeRepository.findJurisdictionCodeValues()).thenReturn(Optional.of(List.of(code)));

        HashMap<String, String> result = cachingValueService.getCodedValuesCallRepos("S_JURDIC_C");
        assertEquals("Jurisdiction Desc", result.get("J1"));
    }

    @Test
    void testGetCodedValuesCallReposOther() {
        CodeValueGeneral codeValue = new CodeValueGeneral();
        codeValue.setCode("C1");
        codeValue.setCodeShortDescTxt("Short Desc");
        when(codeValueJdbcRepository.findCodeValuesByCodeSetNm("SOME")).thenReturn(List.of(codeValue));

        HashMap<String, String> result = cachingValueService.getCodedValuesCallRepos("SOME");
        assertEquals("Short Desc", result.get("C1"));
    }

    @Test
    void testGetCodedValueForLogProcess() {
        CodeValueGeneral codeValue = new CodeValueGeneral();
        codeValue.setCode("LOG1");
        codeValue.setCodeDescTxt("Log Desc");
        when(codeValueJdbcRepository.findCodeDescriptionsByCodeSetNm(ELRConstant.ELR_LOG_PROCESS))
                .thenReturn(List.of(codeValue));

        HashMap<String, String> result = cachingValueService.getCodedValue(ELRConstant.ELR_LOG_PROCESS);
        assertEquals("Log Desc", result.get("LOG1"));
    }

    @Test
    void testGetCountyCdByDescCallReposWithState() {
        StateCountyCodeValue value = new StateCountyCodeValue();
        value.setCode("101");
        value.setAssigningAuthorityDescTxt("Desc101");
        when(stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt("AZ"))
                .thenReturn(Optional.of(List.of(value)));

        HashMap<String, String> result = cachingValueService.getCountyCdByDescCallRepos("AZ");
        assertEquals("Desc101", result.get("101 COUNTY"));
    }

    @Test
    void testGetCountyCdByDescCallReposWithoutState() {
        StateCountyCodeValue value = new StateCountyCodeValue();
        value.setCode("102");
        value.setAssigningAuthorityDescTxt("Desc102");
        when(stateCountyCodeValueRepository.findByIndentLevelNbr())
                .thenReturn(Optional.of(List.of(value)));

        HashMap<String, String> result = cachingValueService.getCountyCdByDescCallRepos(null);
        assertEquals("Desc102", result.get("102 COUNTY"));
    }
}
