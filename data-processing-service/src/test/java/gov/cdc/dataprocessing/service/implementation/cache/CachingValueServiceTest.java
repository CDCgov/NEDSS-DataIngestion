
package gov.cdc.dataprocessing.service.implementation.cache;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
}
