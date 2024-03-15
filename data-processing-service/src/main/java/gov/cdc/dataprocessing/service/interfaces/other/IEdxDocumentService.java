package gov.cdc.dataprocessing.service.interfaces.other;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;

import java.util.Collection;

public interface IEdxDocumentService {
    Collection<EDXDocumentDto> selectEdxDocumentCollectionByActUid(Long uid);
    EDXDocumentDto saveEdxDocument(EDXDocumentDto edxDocumentDto) throws DataProcessingException;
}
