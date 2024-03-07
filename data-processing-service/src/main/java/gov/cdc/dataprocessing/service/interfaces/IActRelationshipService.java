package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;

import java.util.Collection;

public interface IActRelationshipService {
    Collection<ActRelationshipDT> loadActRelationshipBySrcIdAndTypeCode(Long uid, String type);
    void saveActRelationship(ActRelationshipDT actRelationshipDT) throws DataProcessingException;
}
