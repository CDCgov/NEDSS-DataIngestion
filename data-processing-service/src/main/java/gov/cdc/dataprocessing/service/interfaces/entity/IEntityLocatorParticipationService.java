package gov.cdc.dataprocessing.service.interfaces.entity;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;

import java.util.Collection;
import java.util.List;

public interface IEntityLocatorParticipationService {
    void updateEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long uid) throws DataProcessingException;
    void createEntityLocatorParticipation(Collection<EntityLocatorParticipationDto> locatorCollection, Long uid) throws DataProcessingException;
    List<EntityLocatorParticipation> findEntityLocatorById(Long uid);
}
