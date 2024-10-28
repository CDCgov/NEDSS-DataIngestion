package gov.cdc.dataprocessing.service.interfaces.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;

import java.util.Collection;

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
public interface IEdxDocumentService {
    Collection<EDXDocumentDto> selectEdxDocumentCollectionByActUid(Long uid);
    EDXDocumentDto saveEdxDocument(EDXDocumentDto edxDocumentDto) throws DataProcessingException;
}
