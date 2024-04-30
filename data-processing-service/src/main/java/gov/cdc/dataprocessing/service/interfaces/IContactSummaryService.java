package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;

import java.util.Collection;

public interface IContactSummaryService {
    Collection<Object> getContactListForInvestigation(Long publicHealthCaseUID) throws DataProcessingException;
}
