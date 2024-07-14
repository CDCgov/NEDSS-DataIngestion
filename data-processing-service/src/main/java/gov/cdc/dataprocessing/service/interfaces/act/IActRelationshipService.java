package gov.cdc.dataprocessing.service.interfaces.act;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;

import java.util.Collection;

public interface IActRelationshipService {
    Collection<ActRelationshipDto> loadActRelationshipBySrcIdAndTypeCode(Long uid, String type);

    void saveActRelationship(ActRelationshipDto actRelationshipDto) throws DataProcessingException;
}
