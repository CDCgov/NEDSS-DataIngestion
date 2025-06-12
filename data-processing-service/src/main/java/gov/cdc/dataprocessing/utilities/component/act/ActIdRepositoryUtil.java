package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActIdJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component

public class ActIdRepositoryUtil {
    private final ActIdJdbcRepository actIdJdbcRepository;

    public ActIdRepositoryUtil(
                               ActIdJdbcRepository actIdJdbcRepository) {
        this.actIdJdbcRepository = actIdJdbcRepository;
    }

    public Collection<ActIdDto> getActIdCollection(Long actUid) {
        var actIds = actIdJdbcRepository.findRecordsByActUid(actUid);
        Collection<ActIdDto> actIdCollection = new ArrayList<>();
        if (actIds != null && !actIds.isEmpty()) {
            for(var item : actIds) {
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
            actIdJdbcRepository.mergeActId(data);
            item.setItDirty(false);
            item.setItNew(false);
            item.setItDelete(false);
        }
    }


}
