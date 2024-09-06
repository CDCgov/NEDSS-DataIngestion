package gov.cdc.nbsDedup.service.interfaces.entity.cache;


import gov.cdc.nbsDedup.exception.DataProcessingException;
import java.util.TreeMap;

public interface ICatchingValueService {
    TreeMap<String, String> getCodedValue(String code) throws DataProcessingException;
    TreeMap<String, String> getCodedValuesCallRepos(String pType) throws DataProcessingException;
}
