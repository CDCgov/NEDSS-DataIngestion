package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.CodeValueJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CachingValueDpDpService implements ICatchingValueDpService {

    private final CodeValueJdbcRepository codeValueJdbcRepository;
    private final ICacheApiService cacheApiService;


    public CachingValueDpDpService(
            CodeValueJdbcRepository codeValueJdbcRepository,
            @Lazy ICacheApiService cacheApiService) {
        this.codeValueJdbcRepository = codeValueJdbcRepository;
        this.cacheApiService = cacheApiService;
    }

    private static final String separator = "~";
    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException {
        String key = fromCodeSetNm + separator + fromCode + separator + toCodeSetNm;
        return cacheApiService.getSrteCacheString(ObjectName.FIND_TO_CODE.name(), key);
    }

    public String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException {
        String key = code + separator + codeSetNm;
        return cacheApiService.getSrteCacheString(ObjectName.GET_CODE_DESC_TXT_FOR_CD.name(), key);
    }

    public String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException {
        String key = county + separator + stateCd;
        return cacheApiService.getSrteCacheString(ObjectName.GET_COUNTY_CD_BY_DESC.name(), key);

    }

    public StateCode findStateCodeByStateNm(String stateNm) {
        return (StateCode) cacheApiService.getSrteCacheObject(ObjectName.FIND_STATE_CODE_BY_STATE_NM.name(), stateNm);
    }

    public String getCodedValue(String pType, String pKey) throws DataProcessingException {
        return cacheApiService.getSrteCacheString(String.valueOf(ObjectName.CODED_VALUE), pType + separator + pKey);
    }

    public boolean checkCodedValue(String pType, String pKey) throws DataProcessingException {
        return cacheApiService.getSrteCacheBool(String.valueOf(ObjectName.CODED_VALUE), pType + separator + pKey);
    }

    public String getCodedValuesCallRepos(String pType) throws DataProcessingException {
        return cacheApiService.getSrteCacheString(String.valueOf(ObjectName.GET_CODED_VALUES_CALL_REPOS), pType);
    }

    public List<CodeValueGeneral> getGeneralCodedValue(String code) {
        var res = codeValueJdbcRepository.findCodeValuesByCodeSetNm(code);
        if (res != null && !res.isEmpty()) {
            return res;
        }
        else {
            return new ArrayList<>();
        }
    }

    public String getCodedValue(String code) throws DataProcessingException {
        return cacheApiService.getSrteCacheString(ObjectName.GET_CODED_VALUE.name(), code);
    }
}
