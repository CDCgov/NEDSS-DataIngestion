package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.*;
import gov.cdc.dataprocessing.repository.nbs.srte.model.*;
import gov.cdc.dataprocessing.service.interfaces.ICheckingValueService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Slf4j
public class CheckingValueService implements ICheckingValueService {
    private static final Logger logger = LoggerFactory.getLogger(CheckingValueService.class);

    private final JurisdictionCodeRepository jurisdictionCodeRepository;
    private final CodeValueGeneralRepository codeValueGeneralRepository;
    private final ElrXrefRepository elrXrefRepository;
    private  final RaceCodeRepository raceCodeRepository;
    private final StateCountyCodeValueRepository stateCountyCodeValueRepository;

    private final LOINCCodeRepository loincCodeRepository;

    private final CacheManager cacheManager;

    public CheckingValueService(JurisdictionCodeRepository jurisdictionCodeRepository,
                                CodeValueGeneralRepository codeValueGeneralRepository,
                                ElrXrefRepository elrXrefRepository,
                                RaceCodeRepository raceCodeRepository,
                                StateCountyCodeValueRepository stateCountyCodeValueRepository,
                                LOINCCodeRepository loincCodeRepository,
                                CacheManager cacheManager) {
        this.jurisdictionCodeRepository = jurisdictionCodeRepository;
        this.codeValueGeneralRepository = codeValueGeneralRepository;
        this.elrXrefRepository = elrXrefRepository;
        this.raceCodeRepository = raceCodeRepository;
        this.stateCountyCodeValueRepository = stateCountyCodeValueRepository;
        this.loincCodeRepository = loincCodeRepository;
        this.cacheManager = cacheManager;
    }

    //TODO: CACHED
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

    //TODO: CACHED
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


    //TODO: CACHED
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
        if (
                (SrteCache.codedValuesMap.get(key) != null && SrteCache.codedValuesMap.get(key).isEmpty())
                    || SrteCache.codedValuesMap.get(key) == null
        ) {
            SrteCache.codedValuesMap.putAll(getCodedValuesCallRepos(pType));
            cache.put("codedValues", SrteCache.codedValuesMap);
        }


        return SrteCache.codedValuesMap;
    }

    //TODO: CACHED
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
        if ((SrteCache.codeDescTxtMap.get(code) != null && SrteCache.codeDescTxtMap.get(code).isEmpty())
                        || SrteCache.codeDescTxtMap.get(code) == null
        ) {
            SrteCache.codeDescTxtMap.putAll(getCodedValuesCallRepos(codeSetNm));
            cache.put("codeDescTxt", SrteCache.codeDescTxtMap);
        }

        return SrteCache.codeDescTxtMap.get(code);
    }
//    public  String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException {
//        var map = getCodedValuesCallRepos(codeSetNm);
//        String codeDesc = "";
//
//        if (map.containsKey(code)) {
//            codeDesc = map.get(code);
//        }
//        return codeDesc;
//    }
    //TODO: NO CACHED
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

    //TODO: CACHED
    public String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException {
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

        if ((SrteCache.countyCodeByDescMap.get(cnty) != null && SrteCache.countyCodeByDescMap.get(cnty).isEmpty())
                || SrteCache.countyCodeByDescMap.get(cnty) == null
        ) {
            SrteCache.countyCodeByDescMap.putAll(getCountyCdByDescCallRepos(stateCd));
            cache.put("countyCodeByDesc", SrteCache.countyCodeByDescMap);
        }

        return SrteCache.countyCodeByDescMap.get(cnty);
    }

    private TreeMap<String, String> getCountyCdByDescCallRepos(String stateCd) throws DataProcessingException {
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


//    public String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException {
//        try {
//            String code = "";
//            String cnty = county.toUpperCase();
//            if (!cnty.endsWith("COUNTY")) {
//                cnty = cnty + " COUNTY";
//            }
//            Optional<List<StateCountyCodeValue>> result;
//            if( stateCd==null || stateCd.trim().equals("")) {
//                result = stateCountyCodeValueRepository.findByIndentLevelNbr();
//            } else {
//                result = stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd);
//            }
//
//            if (result.isPresent()) {
//                String comparer = cnty;
//                var res = result.get().stream().filter(x -> x.getCode().equals(comparer)).findFirst();
//                code = res.get().getCode();
//            }
//            return code;
//        } catch (Exception e) {
//            throw new DataProcessingException(e.getMessage());
//        }
//
//    }

    private TreeMap<String, String> getCodedValuesCallRepos(String pType) throws DataProcessingException {
        TreeMap<String, String> map;
        if (pType.equals("S_JURDIC_C")) {
            map = getJurisdictionCode();
        } else {
            map = getCodedValue(pType);
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

    private TreeMap<String, String> getCodedValue(String code) throws DataProcessingException {
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



}
