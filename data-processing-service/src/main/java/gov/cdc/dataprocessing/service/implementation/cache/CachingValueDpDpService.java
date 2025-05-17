package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.RtiCacheException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.utilities.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CachingValueDpDpService implements ICatchingValueDpService {
    private final CodeValueGeneralRepository codeValueGeneralRepository;
    private final ICacheApiService cacheApiService;


    public CachingValueDpDpService(
                                   CodeValueGeneralRepository codeValueGeneralRepository,
                                   @Lazy ICacheApiService cacheApiService) {
        this.codeValueGeneralRepository = codeValueGeneralRepository;
        this.cacheApiService = cacheApiService;
    }

    private String separator = "~";
    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws RtiCacheException {
        String key = fromCodeSetNm + separator + fromCode + separator + toCodeSetNm;
        return cacheApiService.getSrteCacheString(ObjectName.FIND_TO_CODE.name(), key);
    }

    public String getCodeDescTxtForCd(String code, String codeSetNm) throws RtiCacheException {
        String key = code + separator + codeSetNm;
        return cacheApiService.getSrteCacheString(ObjectName.GET_CODE_DESC_TXT_FOR_CD.name(), key);
    }

    public String getCountyCdByDesc(String county, String stateCd) throws RtiCacheException {
        String key = county + separator + stateCd;
        return cacheApiService.getSrteCacheString(ObjectName.GET_COUNTY_CD_BY_DESC.name(), key);

    }

    public StateCode findStateCodeByStateNm(String stateNm) {
//        var result = GsonUtil.GSON.fromJson(cacheApiService.getSrteCacheObject(ObjectName.FIND_STATE_CODE_BY_STATE_NM.name(), stateNm), StateCode.class);

        return (StateCode) cacheApiService.getSrteCacheObject(ObjectName.FIND_STATE_CODE_BY_STATE_NM.name(), stateNm);
    }

    public String getCodedValue(String pType, String pKey) throws RtiCacheException {
        return cacheApiService.getSrteCacheString(String.valueOf(ObjectName.CODED_VALUE), pType + separator + pKey);
    }

    public boolean checkCodedValue(String pType, String pKey) throws RtiCacheException {
        return cacheApiService.getSrteCacheBool(String.valueOf(ObjectName.CODED_VALUE), pType + separator + pKey);
    }

    public String getCodedValuesCallRepos(String pType) throws RtiCacheException {
        return cacheApiService.getSrteCacheString(String.valueOf(ObjectName.GET_CODED_VALUES_CALL_REPOS), pType);
    }

    public List<CodeValueGeneral> getGeneralCodedValue(String code) {
        return codeValueGeneralRepository.findCodeValuesByCodeSetNm(code).orElseGet(ArrayList::new);
    }

    public String getCodedValue(String code) throws RtiCacheException {
        return cacheApiService.getSrteCacheString(ObjectName.GET_CODED_VALUE.name(), code);
    }
}
