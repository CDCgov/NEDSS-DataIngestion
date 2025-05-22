package gov.cdc.dataprocessing.service.interfaces.cache;

import gov.cdc.dataprocessing.exception.RtiCacheException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;

import java.util.List;

public interface ICatchingValueDpService {
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws RtiCacheException;
    String getCodeDescTxtForCd(String code, String codeSetNm) throws RtiCacheException;
    String getCountyCdByDesc(String county, String stateCd) throws RtiCacheException;
    StateCode findStateCodeByStateNm(String stateNm);
    String getCodedValue(String pType, String pKey) throws RtiCacheException;
    boolean checkCodedValue(String pType, String pKey) throws RtiCacheException;
    String getCodedValuesCallRepos(String pType) throws RtiCacheException;
    List<CodeValueGeneral> getGeneralCodedValue(String code);
    String getCodedValue(String code) throws RtiCacheException;
}
