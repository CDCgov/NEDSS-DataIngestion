package gov.cdc.dataprocessing.service.interfaces.lookup_data;

import gov.cdc.dataprocessing.exception.DataProcessingException;

import java.util.TreeMap;

public interface ILookupService {
    TreeMap<Object, Object> getToPrePopFormMapping(String formCd) throws DataProcessingException;

    TreeMap<Object, Object> getQuestionMap();

    TreeMap<Object, Object> getDMBQuestionMapAfterPublish();

    void fillPrePopMap();
}
