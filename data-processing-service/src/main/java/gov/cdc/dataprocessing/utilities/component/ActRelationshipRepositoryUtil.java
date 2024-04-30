package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationshipHistory;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipHistoryRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ActRelationshipRepositoryUtil {
    private final ActRelationshipRepository actRelationshipRepository;
    private final ActRelationshipHistoryRepository actRelationshipHistoryRepository;

    public ActRelationshipRepositoryUtil(ActRelationshipRepository actRelationshipRepository,
                                         ActRelationshipHistoryRepository actRelationshipHistoryRepository) {
        this.actRelationshipRepository = actRelationshipRepository;
        this.actRelationshipHistoryRepository = actRelationshipHistoryRepository;
    }

    public Collection<ActRelationshipDto> getActRelationshipCollectionFromSourceId(Long actUid) {
        var res = actRelationshipRepository.findRecordsBySourceId(actUid);
        Collection<ActRelationshipDto> dtoCollection = new ArrayList<>();
        if (!res.isEmpty()) {
            for(var item : res) {
                var dto  = new ActRelationshipDto(item);
                dto.setItNew(false);
                dto.setItDirty(false);
                dtoCollection.add(dto);
            }
        }
        return dtoCollection;
    }

    public void insertActRelationshipHist(ActRelationshipDto actRelationshipDto) {
        var hst = new ActRelationshipHistory(actRelationshipDto);
        actRelationshipHistoryRepository.save(hst);
    }

    public void storeActRelationship(ActRelationshipDto dt) throws DataProcessingException {
        if (dt == null)
        {
            throw new DataProcessingException("Error: try to store null ActRelationshipDT object.");
        }
        ActRelationship data = new ActRelationship(dt);
        if (dt.isItNew())
        {
            actRelationshipRepository.save(data);
        }
        else if (dt.isItDelete())
        {
            actRelationshipRepository.delete(data);
        }
        else if (dt.isItDirty())
        {
            if (dt.getTargetActUid() != null &&
                    dt.getSourceActUid() != null && dt.getTypeCd() != null)
            {
                actRelationshipRepository.save(data);
            }
        }
    }
}
