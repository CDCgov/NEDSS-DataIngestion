package gov.cdc.nbsDedup.service.interfaces.entity;


import gov.cdc.nbsDedup.exception.DataProcessingException;
import gov.cdc.nbsDedup.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.nbsDedup.nbs.odse.model.entity.EntityLocatorParticipation;

import java.util.Collection;
import java.util.List;

public interface IEntityLocatorParticipationService {
    void updateEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long uid) throws
        DataProcessingException;
    void createEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long uid) throws DataProcessingException;
    List<EntityLocatorParticipation> findEntityLocatorById(Long uid);
}
