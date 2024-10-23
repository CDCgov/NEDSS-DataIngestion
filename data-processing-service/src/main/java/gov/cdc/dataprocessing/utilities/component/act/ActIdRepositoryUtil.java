package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class ActIdRepositoryUtil {
    private final ActIdRepository actIdRepository;

    public ActIdRepositoryUtil(ActIdRepository actIdRepository) {
        this.actIdRepository = actIdRepository;
    }

    public Collection<ActIdDto> GetActIdCollection(Long actUid) {
        var actIds = actIdRepository.findRecordsById(actUid);
        Collection<ActIdDto> actIdCollection = new ArrayList<>();
        if (actIds.isPresent()) {
            for(var item : actIds.get()) {
                var dto  = new ActIdDto(item);
                dto.setItNew(false);
                dto.setItDirty(false);
                actIdCollection.add(dto);
            }
        }
        return actIdCollection;
    }


    public void insertActIdCollection(Long uid, Collection<ActIdDto> actIdDtoCollection) {
        for(var item: actIdDtoCollection){
            ActId data = new ActId(item);
            data.setActUid(uid);
            actIdRepository.save(data);
            item.setItDirty(false);
            item.setItNew(false);
            item.setItDelete(false);
        }
    }


}
