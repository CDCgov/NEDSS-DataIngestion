package gov.cdc.dataprocessing.service.interfaces.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;

import java.util.Collection;
import java.util.List;


public interface IEdxDocumentService {
    Collection<EDXDocumentDto> selectEdxDocumentCollectionByActUid(Long uid);
    EDXDocumentDto saveEdxDocument(EDXDocumentDto edxDocumentDto) throws DataProcessingException;
    List<EDXDocumentDto> saveEdxDocumentBatch(List<EDXDocumentDto> dtos);
}
