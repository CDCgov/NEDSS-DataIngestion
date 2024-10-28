package gov.cdc.dataprocessing.service.interfaces.cache;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;

import java.util.HashMap;
import java.util.List;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface ICatchingValueService {
   // HashMap<String, String> getCodedValues(String pType) throws DataProcessingException;
    HashMap<String, String> getRaceCodes() throws DataProcessingException;
    String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException;
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException;
    String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException;
    HashMap<String, String>  getAOELOINCCodes() throws DataProcessingException;
    HashMap<String, String> getCodedValues(String pType, String key) throws DataProcessingException;
    List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code);
    StateCode findStateCodeByStateNm(String stateNm);
    HashMap<String, String> getAllJurisdictionCode() throws DataProcessingException;
    HashMap<String, String> getAllProgramAreaCodes() throws DataProcessingException;
    HashMap<String, Integer> getAllProgramAreaCodesWithNbsUid() throws DataProcessingException;
    HashMap<String, Integer> getAllJurisdictionCodeWithNbsUid() throws DataProcessingException;
    List<ElrXref> getAllElrXref() throws DataProcessingException;
    HashMap<String, String> getAllOnInfectionConditionCode() throws DataProcessingException;
    List<ConditionCode> getAllConditionCode() throws DataProcessingException;
    HashMap<String, String> getCodedValue(String code) throws DataProcessingException;
    List<CodeValueGeneral> getGeneralCodedValue(String code);
    HashMap<String, String> getCodedValuesCallRepos(String pType) throws DataProcessingException;
    HashMap<String, String> getLabResultDesc() throws DataProcessingException;
    HashMap<String, String> getAllSnomedCode() throws DataProcessingException;
    HashMap<String, String> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() throws DataProcessingException;
    HashMap<String, String> getAllLoinCodeWithComponentName() throws DataProcessingException;
}
