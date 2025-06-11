package gov.cdc.srtedataservice.service;

import gov.cdc.srtedataservice.cache_model.SrteCache;
import gov.cdc.srtedataservice.constant.ELRConstant;
import gov.cdc.srtedataservice.exception.DataProcessingException;
import gov.cdc.srtedataservice.exception.RtiCacheException;
import gov.cdc.srtedataservice.repository.nbs.srte.model.*;
import gov.cdc.srtedataservice.repository.nbs.srte.repository.*;
import gov.cdc.srtedataservice.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.srtedataservice.service.interfaces.ICatchingValueService;
import gov.cdc.srtedataservice.service.interfaces.IJurisdictionService;
import gov.cdc.srtedataservice.service.interfaces.IProgramAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j

public class CachingValueService implements ICatchingValueService {
    private final JurisdictionCodeRepository jurisdictionCodeRepository;
    private final CodeValueGeneralRepository codeValueGeneralRepository;
    private final ElrXrefRepository elrXrefRepository;
    private final RaceCodeRepository raceCodeRepository;
    private final StateCountyCodeValueRepository stateCountyCodeValueRepository;
    private final StateCodeRepository stateCodeRepository;
    private final LOINCCodeRepository loincCodeRepository;
    private final IProgramAreaService programAreaService;
    private final IJurisdictionService jurisdictionService;
    private final ConditionCodeRepository conditionCodeRepository;
    private final LabResultRepository labResultRepository;
    private final SnomedCodeRepository snomedCodeRepository;
    private final SrteCustomRepository srteCustomRepository;

    public CachingValueService(JurisdictionCodeRepository jurisdictionCodeRepository,
                               CodeValueGeneralRepository codeValueGeneralRepository,
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
        this.jurisdictionCodeRepository = jurisdictionCodeRepository;
        this.codeValueGeneralRepository = codeValueGeneralRepository;
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

    public HashMap<String, String> getAllLoinCodeWithComponentName()  {
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
    public String getCodeDescTxtForCd(String code, String codeSetNm)  {
        if ( SrteCache.codeDescTxtMap.get(code) == null || SrteCache.codeDescTxtMap.get(code).isEmpty()) {
            SrteCache.codeDescTxtMap.putAll(getCodedValuesCallRepos(codeSetNm));
        }
        return SrteCache.codeDescTxtMap.get(code);
    }



    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm)  {
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
        return codeValueGeneralRepository.findCodeValuesByCodeSetNmAndCode(codeSetNm, code).orElseGet(ArrayList::new);
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
            codeValueGeneralList = codeValueGeneralRepository.findCodeDescriptionsByCodeSetNm(code).orElse(Collections.emptyList());
            for (CodeValueGeneral obj : codeValueGeneralList) {
                map.put(obj.getCode(), obj.getCodeDescTxt());
            }
        } else {
            codeValueGeneralList = codeValueGeneralRepository.findCodeValuesByCodeSetNm(code).orElse(Collections.emptyList());
            for (CodeValueGeneral obj : codeValueGeneralList) {
                map.put(obj.getCode(), obj.getCodeShortDescTxt());
            }
        }

        return map;
    }

    private HashMap<String, String> getJurisdictionCode()  {
//        return loadCache(() -> jurisdictionCodeRepository.findJurisdictionCodeValues().orElse(Collections.emptyList()), JurisdictionCode::getCode, JurisdictionCode::getCodeDescTxt);

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
//        return loadCache(() -> {
//            if (stateCd == null || stateCd.trim().isEmpty()) {
//                return stateCountyCodeValueRepository.findByIndentLevelNbr().orElse(Collections.emptyList());
//            } else {
//                return stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd).orElse(Collections.emptyList());
//            }
//        }, stateCountyCodeValue -> stateCountyCodeValue.getCode() + " COUNTY", StateCountyCodeValue::getAssigningAuthorityDescTxt);

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

//    private <T, K, V> HashMap<K, V> loadCache(CacheLoader<T> loader, KeyExtractor<T, K> keyExtractor, ValueExtractor<T, V> valueExtractor)  {
//        HashMap<K, V> map = new HashMap<>();
//        try {
//            List<T> result = loader.load();
//            for (T obj : result) {
//                map.put(keyExtractor.extract(obj), valueExtractor.extract(obj));
//            }
//        } catch (Exception e) {
//            throw new RtiCacheException(e.getMessage(), e);
//        }
//        return map;
//    }

//    @FunctionalInterface
//    private interface CacheLoader<T> {
//        List<T> load() ;
//    }
//
//    @FunctionalInterface
//    private interface KeyExtractor<T, K> {
//        K extract(T obj);
//    }
//
//    @FunctionalInterface
//    private interface ValueExtractor<T, V> {
//        V extract(T obj);
//    }
}
