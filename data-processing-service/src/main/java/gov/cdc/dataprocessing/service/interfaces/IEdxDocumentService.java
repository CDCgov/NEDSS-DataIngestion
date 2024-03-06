package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXDocumentDT;

import java.util.Collection;

public interface IEdxDocumentService {
    Collection<EDXDocumentDT> selectEdxDocumentCollectionByActUid(Long uid);
}
