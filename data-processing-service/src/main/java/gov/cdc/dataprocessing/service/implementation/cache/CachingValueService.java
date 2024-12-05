package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.utilities.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
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
    private final CacheManager cacheManager;
    private final IProgramAreaService programAreaService;
    private final IJurisdictionService jurisdictionService;
    private final ConditionCodeRepository conditionCodeRepository;
    private final LabResultRepository labResultRepository;
    private final SnomedCodeRepository snomedCodeRepository;
    private final SrteCustomRepository srteCustomRepository;
    private final ICacheApiService cacheApiService;


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
                               SrteCustomRepository srteCustomRepository, ICacheApiService cacheApiService) {
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
        this.cacheApiService = cacheApiService;
    }

    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) {
        String key = fromCodeSetNm + "_" + fromCode + "_" + toCodeSetNm;
        return cacheApiService.getSrteCacheString(ObjectName.FIND_TO_CODE.name(), key);
    }

    public String getCodeDescTxtForCd(String code, String codeSetNm) {
        String key = code + "_" + codeSetNm;
        return cacheApiService.getSrteCacheString(ObjectName.GET_CODE_DESC_TXT_FOR_CD.name(), key);
    }

    public String getCountyCdByDesc(String county, String stateCd)  {
        String key = county + "_" + stateCd;
        return cacheApiService.getSrteCacheString(ObjectName.GET_COUNTY_CD_BY_DESC.name(), key);

    }

    public StateCode findStateCodeByStateNm(String stateNm) {
        var result = GsonUtil.GSON.fromJson(cacheApiService.getSrteCacheObject(ObjectName.FIND_STATE_CODE_BY_STATE_NM.name(), stateNm), StateCode.class);
        return result;
    }

    public String getCodedValue(String pType, String pKey) {
        return cacheApiService.getSrteCacheString(String.valueOf(ObjectName.CODED_VALUE), pType + "_" + pKey);
    }

    public boolean checkCodedValue(String pType, String pKey) {
        return cacheApiService.getSrteCacheBool(String.valueOf(ObjectName.CODED_VALUE), pType + "_" + pKey);
    }

    public String getCodedValuesCallRepos(String pType) {
        return cacheApiService.getSrteCacheString(String.valueOf(ObjectName.GET_CODED_VALUES_CALL_REPOS), pType);
    }

    public List<CodeValueGeneral> getGeneralCodedValue(String code) {
        return codeValueGeneralRepository.findCodeValuesByCodeSetNm(code).orElseGet(ArrayList::new);
    }

    public String getCodedValue(String code) {
        return cacheApiService.getSrteCacheString(ObjectName.GET_CODED_VALUE.name(), code);
    }
}
