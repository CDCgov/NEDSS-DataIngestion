package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;

import java.util.Collection;

public interface IActRelationshipService {
    Collection<ActRelationshipDT> loadActRelationshipBySrcIdAndTypeCode(Long uid, String type);
}
