package gov.cdc.dataprocessing.service.implementation.cache;


import gov.cdc.dataprocessing.cache.cache_model.SrteCache;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.CodeValueJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class CachingValueService implements ICatchingValueService {
    private final CodeValueJdbcRepository codeValueJdbcRepository;

    private final ElrXrefRepository elrXrefRepository;
    private final RaceCodeRepository raceCodeRepository;
    private final StateCountyCodeValueRepository stateCountyCodeValueRepository;
    private final StateCodeRepository stateCodeRepository;
    private final LOINCCodeRepository loincCodeRepository;

    private final ConditionCodeRepository conditionCodeRepository; //LEAVE THIS ONE BE - COMPLEX

    private final LabResultRepository labResultRepository;
    private final SnomedCodeRepository snomedCodeRepository;

    private final JurisdictionCodeRepository jurisdictionCodeRepository;
    private final SrteCustomRepository srteCustomRepository;

    private final IProgramAreaService programAreaService;
    private final IJurisdictionService jurisdictionService;

    public CachingValueService(CodeValueJdbcRepository codeValueJdbcRepository,
                               JurisdictionCodeRepository jurisdictionCodeRepository,
                               ElrXrefRepository elrXrefRepository,
                               RaceCodeRepository raceCodeRepository,
                               StateCountyCodeValueRepository stateCountyCodeValueRepository,
                               StateCodeRepository stateCodeRepository,
                               LOINCCodeRepository loincCodeRepository,
                               IProgramAreaService programAreaService,
                               IJurisdictionService jurisdictionService,
                               ConditionCodeRepository conditionCodeRepository,
                               LabResultRepository labResultRepository,
                               SnomedCodeRepository snomedCodeRepository,
                               SrteCustomRepository srteCustomRepository) {
        this.codeValueJdbcRepository = codeValueJdbcRepository;
        this.jurisdictionCodeRepository = jurisdictionCodeRepository;
        this.elrXrefRepository = elrXrefRepository;
        this.raceCodeRepository = raceCodeRepository;
        this.stateCountyCodeValueRepository = stateCountyCodeValueRepository;
        this.stateCodeRepository = stateCodeRepository;
        this.loincCodeRepository = loincCodeRepository;
        this.programAreaService = programAreaService;
        this.jurisdictionService = jurisdictionService;
        this.conditionCodeRepository = conditionCodeRepository;
        this.labResultRepository = labResultRepository;
        this.snomedCodeRepository = snomedCodeRepository;
        this.srteCustomRepository = srteCustomRepository;
    }

    public HashMap<String, String> getAllLoinCodeWithComponentName() {
        HashMap<String, String> result = new HashMap<>();

        List<LOINCCode> loincCodes = loincCodeRepository.findAll();

        for (LOINCCode loincCode : loincCodes) {
            result.put(loincCode.getLoincCode(), loincCode.getComponentName());
        }

        return result;
    }

    public HashMap<String, String> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd()  {
        HashMap<String, String> result = new HashMap<>();

        List<LabResult> labResults = srteCustomRepository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();

        for (LabResult labResult : labResults) {
            result.put(labResult.getLabResultCd(), labResult.getLabResultDescTxt());
        }

        return result;
    }

    public HashMap<String, String> getAllSnomedCode()  {
        HashMap<String, String> result = new HashMap<>();

        List<SnomedCode> snomedCodes = snomedCodeRepository.findAll();

        for (SnomedCode snomedCode : snomedCodes) {
            result.put(snomedCode.getSnomedCd(), snomedCode.getSnomedDescTxt());
        }

        return result;
    }

    public HashMap<String, String> getLabResultDesc()  {
        HashMap<String, String> result = new HashMap<>();

        List<LabResult> labResults = labResultRepository.findLabResultByDefaultLabAndOrgNameN()
                .orElse(Collections.emptyList());

        for (LabResult labResult : labResults) {
            result.put(labResult.getLabResultCd(), labResult.getLabResultDescTxt());
        }

        return result;
    }

    public HashMap<String, String> getAOELOINCCodes()  {
        HashMap<String, String> result = new HashMap<>();

        List<LOINCCode> loincCodes = loincCodeRepository.findLoincCodes()
                .orElse(Collections.emptyList());

        for (LOINCCode loincCode : loincCodes) {
            result.put(loincCode.getLoincCode(), loincCode.getLoincCode());
        }

        return result;
    }

    public HashMap<String, String> getRaceCodes()  {
        HashMap<String, String> result = new HashMap<>();

        List<RaceCode> raceCodes = raceCodeRepository.findAllActiveRaceCodes()
                .orElse(Collections.emptyList());

        for (RaceCode raceCode : raceCodes) {
            result.put(raceCode.getCode(), raceCode.getCodeShortDescTxt());
        }

        return result;
    }

    public HashMap<String, String> getAllProgramAreaCodes()  {
        HashMap<String, String> result = new HashMap<>();
        List<ProgramAreaCode> programAreaCodes = programAreaService.getAllProgramAreaCode();
        for (ProgramAreaCode programAreaCode : programAreaCodes) {
            result.put(programAreaCode.getProgAreaCd(), programAreaCode.getProgAreaDescTxt());
        }

        return result;
    }

    public HashMap<String, Integer> getAllProgramAreaCodesWithNbsUid()  {
        HashMap<String, Integer> result = new HashMap<>();
        List<ProgramAreaCode> programAreaCodes = programAreaService.getAllProgramAreaCode();
        for (ProgramAreaCode programAreaCode : programAreaCodes) {
            result.put(programAreaCode.getProgAreaCd(), programAreaCode.getNbsUid());
        }

        return result;
    }

    public HashMap<String, String> getAllJurisdictionCode()  {
        HashMap<String, String> result = new HashMap<>();
        List<JurisdictionCode> jurisdictionCodes = jurisdictionService.getJurisdictionCode();
        for (JurisdictionCode jurisdictionCode : jurisdictionCodes) {
            String key = jurisdictionCode.getCode();         // Generate the key
            String value = jurisdictionCode.getCodeDescTxt(); // Generate the value
            result.put(key, value);
        }

        return result;
    }

    public HashMap<String, Integer> getAllJurisdictionCodeWithNbsUid()  {
        HashMap<String, Integer> result = new HashMap<>();
        List<JurisdictionCode> jurisdictionCodes = jurisdictionService.getJurisdictionCode();
        for (JurisdictionCode jurisdictionCode : jurisdictionCodes) {
            String key = jurisdictionCode.getCode();  // Generate the key
            Integer value = jurisdictionCode.getNbsUid(); // Generate the value
            result.put(key, value);
        }

        return result;
    }

    public List<ElrXref> getAllElrXref()  {
        return elrXrefRepository.findAll();
    }

    public HashMap<String, String> getAllOnInfectionConditionCode()  {
        HashMap<String, String> result = new HashMap<>();
        List<ConditionCode> conditionCodes = conditionCodeRepository.findCoInfectionConditionCode().orElse(Collections.emptyList());
        for (ConditionCode conditionCode : conditionCodes) {
            String key = conditionCode.getConditionCd();      // Generate the key
            String value = conditionCode.getCoinfectionGrpCd(); // Generate the value
            result.put(key, value);
        }
        return result;
    }

    public List<ConditionCode> getAllConditionCode()  {
        return conditionCodeRepository.findAllConditionCode().orElse(Collections.emptyList());
    }

    @SuppressWarnings("java:S2696")
    public HashMap<String, String> getCodedValues(String pType, String key)  {
        if (!SrteCache.codedValuesMap.containsKey(key) || SrteCache.codedValuesMap.get(key).isEmpty()) {
            SrteCache.codedValuesMap.putAll(getCodedValuesCallRepos(pType));
        }
        return SrteCache.codedValuesMap;
    }

    @SuppressWarnings("java:S2696")
    public String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException {
        if ( SrteCache.codeDescTxtMap.get(code) == null || SrteCache.codeDescTxtMap.get(code).isEmpty()) {
            SrteCache.codeDescTxtMap.putAll(getCodedValuesCallRepos(codeSetNm));
        }
        return SrteCache.codeDescTxtMap.get(code);
    }



    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException {
        return elrXrefRepository.findToCodeByConditions(fromCodeSetNm, fromCode, toCodeSetNm).map(ElrXref::getToCode).orElse("");
    }

    public String getCountyCdByDesc(String county, String stateCd)  {
        if (county == null || stateCd == null) {
            return null;
        }

        // Normalize county input
        String cnty = county.toUpperCase();
        if (!cnty.endsWith("COUNTY")) {
            cnty += " COUNTY";
        }

        // Check if the static map is initialized and contains the required value
        if (SrteCache.countyCodeByDescMap.get(cnty) == null || SrteCache.countyCodeByDescMap.get(cnty).isEmpty()) {
            SrteCache.countyCodeByDescMap.putAll(getCountyCdByDescCallRepos(stateCd));
        }

        // Return the county code
        return SrteCache.countyCodeByDescMap.get(cnty);
    }

    public List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code) {
        var res = codeValueJdbcRepository.findCodeValuesByCodeSetNmAndCode(codeSetNm, code);
        if (res != null && !res.isEmpty()) {
            return res;
        }
        else {
            return Collections.emptyList();
        }
    }

    public StateCode findStateCodeByStateNm(String stateNm) {
        return stateCodeRepository.findStateCdByStateName(stateNm).orElseGet(StateCode::new);
    }



    public HashMap<String, String> getCodedValuesCallRepos(String pType)  {
        if ("S_JURDIC_C".equals(pType)) {
            return getJurisdictionCode();
        } else {
            return getCodedValue(pType);
        }
    }

    public HashMap<String, String> getCodedValue(String code)  {
        HashMap<String, String> map = new HashMap<>();
        List<CodeValueGeneral> codeValueGeneralList;
        if (ELRConstant.ELR_LOG_PROCESS.equals(code)) {
            codeValueGeneralList = codeValueJdbcRepository.findCodeDescriptionsByCodeSetNm(code);
            if (codeValueGeneralList == null) {
                codeValueGeneralList = Collections.emptyList();
            }
            for (CodeValueGeneral obj : codeValueGeneralList) {
                map.put(obj.getCode(), obj.getCodeDescTxt());
            }
        } else {
            codeValueGeneralList = codeValueJdbcRepository.findCodeValuesByCodeSetNm(code);
            if (codeValueGeneralList == null) {
                codeValueGeneralList = Collections.emptyList();
            }
            for (CodeValueGeneral obj : codeValueGeneralList) {
                map.put(obj.getCode(), obj.getCodeShortDescTxt());
            }
        }
        return map;
    }

    private HashMap<String, String> getJurisdictionCode()  {
        HashMap<String, String> result = new HashMap<>();
        List<JurisdictionCode> jurisdictionCodes = jurisdictionCodeRepository.findJurisdictionCodeValues()
                .orElse(Collections.emptyList());

        for (JurisdictionCode code : jurisdictionCodes) {
            String key = code.getCode();         // Generate the key
            String value = code.getCodeDescTxt(); // Generate the value
            result.put(key, value);
        }

        return result;
    }

    protected HashMap<String, String> getCountyCdByDescCallRepos(String stateCd)  {
        HashMap<String, String> result = new HashMap<>();

        List<StateCountyCodeValue> stateCountyCodeValues;
        if (stateCd == null || stateCd.trim().isEmpty()) {
            stateCountyCodeValues = stateCountyCodeValueRepository.findByIndentLevelNbr().orElse(Collections.emptyList());
        } else {
            stateCountyCodeValues = stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd).orElse(Collections.emptyList());
        }

        // Populate the result map
        for (StateCountyCodeValue value : stateCountyCodeValues) {
            String key = value.getCode() + " COUNTY"; // Generate the key
            String assigningAuthorityDescTxt = value.getAssigningAuthorityDescTxt(); // Generate the value
            result.put(key, assigningAuthorityDescTxt);
        }

        return result;
    }

}