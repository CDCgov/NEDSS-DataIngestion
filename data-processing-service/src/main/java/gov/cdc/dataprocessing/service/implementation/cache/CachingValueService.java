package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Slf4j
public class CachingValueService implements ICatchingValueService {
    private final JurisdictionCodeRepository jurisdictionCodeRepository;
    private final CodeValueGeneralRepository codeValueGeneralRepository;
    private final ElrXrefRepository elrXrefRepository;
    private  final RaceCodeRepository raceCodeRepository;
    private final StateCountyCodeValueRepository stateCountyCodeValueRepository;
    private final StateCodeRepository stateCodeRepository;
    private final LOINCCodeRepository loincCodeRepository;

    private final CacheManager cacheManager;

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
                               CacheManager cacheManager,
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
        this.cacheManager = cacheManager;
        this.programAreaService = programAreaService;
        this.jurisdictionService = jurisdictionService;
        this.conditionCodeRepository = conditionCodeRepository;
        this.labResultRepository = labResultRepository;
        this.snomedCodeRepository = snomedCodeRepository;
        this.srteCustomRepository = srteCustomRepository;
    }


    @Cacheable(cacheNames = "srte", key = "'loinCodeWithComponentName'")
    public TreeMap<String, String> getAllLoinCodeWithComponentName() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = loincCodeRepository.findAll();
            if (!result.isEmpty()) {
                for (LOINCCode obj : result) {
                    map.put(obj.getLoincCode(), obj.getComponentName());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }

    @Cacheable(cacheNames = "srte", key = "'labResulDescWithOrgnismName'")
    public TreeMap<String, String> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = srteCustomRepository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
            if (!result.isEmpty()) {
                for (LabResult obj : result) {
                    map.put(obj.getLabResultCd(), obj.getLabResultDescTxt());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }



    @Cacheable(cacheNames = "srte", key = "'snomedCodeByDesc'")
    public TreeMap<String, String> getAllSnomedCode() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = snomedCodeRepository.findAll();
            if (!result.isEmpty()) {
                for (SnomedCode obj : result) {
                    map.put(obj.getSnomedCd(), obj.getSnomedDescTxt());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }



    // getCodedResultDesc
    @Cacheable(cacheNames = "srte", key = "'labResulDesc'")
    public TreeMap<String, String> getLabResultDesc() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = labResultRepository.findLabResultByDefaultLabAndOrgNameN();
            if (result.isPresent()) {
                var data = result.get();
                for (LabResult obj :data) {
                    map.put(obj.getLabResultCd(), obj.getLabResultDescTxt());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }


    @Cacheable(cacheNames = "srte", key = "'loincCodes'")
    public TreeMap<String, String>  getAOELOINCCodes() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = loincCodeRepository.findLoincCodes();
            if (result.isPresent()) {
                for (LOINCCode obj :result.get()) {
                    map.put(obj.getLoincCode(), obj.getLoincCode());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return map;
    }

    @Cacheable(cacheNames = "srte", key = "'raceCodes'")
    public TreeMap<String, String> getRaceCodes() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = raceCodeRepository.findAllActiveRaceCodes();
            if (result.isPresent()) {
                var raceCode = result.get();
                for (RaceCode obj :raceCode) {
                    map.put(obj.getCode(), obj.getCodeShortDescTxt());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }

    @Cacheable(cacheNames = "srte", key = "'programAreaCodes'")
    public TreeMap<String, String> getAllProgramAreaCodes() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = programAreaService.getAllProgramAreaCode();
            for (ProgramAreaCode obj :result) {
                map.put(obj.getProgAreaCd(), obj.getProgAreaDescTxt());
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }

    @Cacheable(cacheNames = "srte", key = "'programAreaCodesWithNbsUid'")
    public TreeMap<String, Integer> getAllProgramAreaCodesWithNbsUid() throws DataProcessingException {
        TreeMap<String, Integer> map = new TreeMap<>();
        try {
            var result = programAreaService.getAllProgramAreaCode();
            for (ProgramAreaCode obj :result) {
                map.put(obj.getProgAreaCd(), obj.getNbsUid());
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }

    @Cacheable(cacheNames = "srte", key = "'jurisdictionCode'")
    public TreeMap<String, String> getAllJurisdictionCode() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = jurisdictionService.getJurisdictionCode();
            for (JurisdictionCode obj :result) {
                map.put(obj.getCode(), obj.getCodeDescTxt());
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }

    @Cacheable(cacheNames = "srte", key = "'jurisdictionCodeWithNbsUid'")
    public TreeMap<String, Integer> getAllJurisdictionCodeWithNbsUid() throws DataProcessingException {
        TreeMap<String, Integer> map = new TreeMap<>();
        try {
            var result = jurisdictionService.getJurisdictionCode();
            for (JurisdictionCode obj :result) {
                map.put(obj.getCode(), obj.getNbsUid());
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }

    @Cacheable(cacheNames = "srte", key = "'elrXref'")
    public List<ElrXref> getAllElrXref() throws DataProcessingException {
        try {
            return elrXrefRepository.findAll();
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }

    }

    @Cacheable(cacheNames = "srte", key = "'coInfectionConditionCode'")
    public TreeMap<String, String> getAllOnInfectionConditionCode() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var result = conditionCodeRepository.findCoInfectionConditionCode();
            if (result.isPresent()) {
                for (ConditionCode obj :result.get()) {
                    map.put(obj.getConditionCd(), obj.getCoinfectionGrpCd());
                }

            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  map;
    }

    @Cacheable(cacheNames = "srte", key = "'conditionCode'")
    public List<ConditionCode> getAllConditionCode() throws DataProcessingException {
        try {
            var result = conditionCodeRepository.findAllConditionCode();
            if (result.isPresent()) {
                return result.get();
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return  new ArrayList<>();
    }

    /**
     * Retrieve value from Cache
     * */
    public TreeMap<String, String> getCodedValues(String pType, String key) throws DataProcessingException {
        var cache = cacheManager.getCache("srte");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper;
            valueWrapper = cache.get("codedValues");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.codedValuesMap = (TreeMap<String, String>) cachedObject;
                }
            }
        }
        if ( cache != null && (
                (SrteCache.codedValuesMap.get(key) != null && SrteCache.codedValuesMap.get(key).isEmpty())
                    || SrteCache.codedValuesMap.get(key) == null)
        ) {
            SrteCache.codedValuesMap.putAll(getCodedValuesCallRepos(pType));
            cache.put("codedValues", SrteCache.codedValuesMap);
        }


        return SrteCache.codedValuesMap;
    }

    /**
     * Retrieve value from Cache
     * */
    public  String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException {
        var cache = cacheManager.getCache("srte");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper;
            valueWrapper = cache.get("codeDescTxt");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.codeDescTxtMap = (TreeMap<String, String>) cachedObject;
                }
            }
        }
        if (
                cache != null && (
                (SrteCache.codeDescTxtMap.get(code) != null && SrteCache.codeDescTxtMap.get(code).isEmpty())
                        || SrteCache.codeDescTxtMap.get(code) == null)
        ) {
            SrteCache.codeDescTxtMap.putAll(getCodedValuesCallRepos(codeSetNm));
            cache.put("codeDescTxt", SrteCache.codeDescTxtMap);
        }

        return SrteCache.codeDescTxtMap.get(code);
    }

    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException {
        try {
            var result = elrXrefRepository.findToCodeByConditions(fromCodeSetNm, fromCode, toCodeSetNm);
            if (result.isPresent()) {
                return result.get().getToCode();
            }
            else {
                return "";
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }

    }

    public String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException {

        if (county == null || stateCd == null) {
          return null;
        }
        String cnty = county.toUpperCase();
        if (!cnty.endsWith("COUNTY")) {
            cnty = cnty + " COUNTY";
        }

        var cache = cacheManager.getCache("srte");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper;
            valueWrapper = cache.get("countyCodeByDesc");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof TreeMap) {
                    SrteCache.countyCodeByDescMap = (TreeMap<String, String>) cachedObject;
                }
            }
        }

        if ( cache != null &&
                ((SrteCache.countyCodeByDescMap.get(cnty) != null && SrteCache.countyCodeByDescMap.get(cnty).isEmpty())
                || SrteCache.countyCodeByDescMap.get(cnty) == null)
        ) {
            SrteCache.countyCodeByDescMap.putAll(getCountyCdByDescCallRepos(stateCd));
            cache.put("countyCodeByDesc", SrteCache.countyCodeByDescMap);
        }

        return SrteCache.countyCodeByDescMap.get(cnty);
    }

    public List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code) {
        var result = codeValueGeneralRepository.findCodeValuesByCodeSetNmAndCode(codeSetNm, code);
        return result.orElseGet(ArrayList::new);

    }

    public StateCode findStateCodeByStateNm(String stateNm) {
        var res = stateCodeRepository.findStateCdByStateName(stateNm);
        return res.orElseGet(StateCode::new);
    }

    public List<CodeValueGeneral> getGeneralCodedValue(String code) {
        var res = codeValueGeneralRepository.findCodeValuesByCodeSetNm(code);
        return res.orElseGet(ArrayList::new);
    }

    public TreeMap<String, String> getCodedValuesCallRepos(String pType) throws DataProcessingException {
        TreeMap<String, String> map;
        if (pType.equals("S_JURDIC_C")) {
            map = getJurisdictionCode();
        } else {
            map = getCodedValue(pType);
        }
        return map;
    }

    public TreeMap<String, String> getCodedValue(String code) throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            List<CodeValueGeneral> codeValueGeneralList;
            if (code.equals(ELRConstant.ELR_LOG_PROCESS)) {
                 var result = codeValueGeneralRepository.findCodeDescriptionsByCodeSetNm(code);
                 if (result.isPresent()) {
                     codeValueGeneralList = result.get();
                     for (CodeValueGeneral obj : codeValueGeneralList) {
                         map.put(obj.getCode(), obj.getCodeDescTxt());
                     }
                 }
            }
            else {
                var result = codeValueGeneralRepository.findCodeValuesByCodeSetNm(code);
                if (result.isPresent()) {
                    codeValueGeneralList = result.get();
                    for (CodeValueGeneral obj : codeValueGeneralList) {
                        map.put(obj.getCode(), obj.getCodeShortDescTxt());
                    }
                }
            }

        } catch (Exception e) {
            throw  new DataProcessingException(e.getMessage());
        }
        return map;
    }


    private TreeMap<String, String> getJurisdictionCode() throws DataProcessingException {
        TreeMap<String, String> map = new TreeMap<>();
        try {
            var codes = jurisdictionCodeRepository.findJurisdictionCodeValues();
            if (codes.isPresent()) {
                for (JurisdictionCode obj : codes.get()) {
                    map.put(obj.getCode(), obj.getCodeDescTxt());
                }
            }
        } catch (Exception e) {
            throw  new DataProcessingException(e.getMessage());
        }
        return map;
    }

    protected TreeMap<String, String> getCountyCdByDescCallRepos(String stateCd) throws DataProcessingException {
        TreeMap<String, String> codeMap = new TreeMap<>();
        try {
            Optional<List<StateCountyCodeValue>> result;
            if( stateCd==null || stateCd.trim().equals("")) {
                result = stateCountyCodeValueRepository.findByIndentLevelNbr();
            } else {
                result = stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd);
            }

            if (result.isPresent()) {
                var res  = result.get();
                for (StateCountyCodeValue obj : res) {
                    codeMap.put(obj.getCode() + " COUNTY", obj.getAssigningAuthorityDescTxt());
                }
            }
            return codeMap;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }

    }




}
