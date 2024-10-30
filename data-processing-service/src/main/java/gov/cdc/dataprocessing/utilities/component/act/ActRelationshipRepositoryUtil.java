package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationshipHistory;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipHistoryRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
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
        if (res.isPresent()) {
            for(var item : res.get()) {
                var dto  = new ActRelationshipDto(item);
                dto.setItNew(false);
                dto.setItDirty(false);
                dtoCollection.add(dto);
            }
        }
        return dtoCollection;
    }

    public Collection<ActRelationshipDto> selectActRelationshipDTCollectionFromActUid(long aUID) throws DataProcessingException
    {
        try
        {
            var col = actRelationshipRepository.findRecordsByActUid(aUID);
            Collection<ActRelationshipDto> dtCollection = new ArrayList<>();
            if (col.isPresent()) {
                for (var item : col.get()) {
                    ActRelationshipDto dt = new ActRelationshipDto(item);
                    dt.setItNew(false);
                    dt.setItDirty(false);
                    dtCollection.add(dt);
                }
            }
            return dtCollection;
        }
        catch(Exception ndapex)
        {
            throw new DataProcessingException(ndapex.getMessage());
        }
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
            data.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
            data.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
            actRelationshipRepository.save(data);
        }
        else if (dt.isItDelete())
        {
            actRelationshipRepository.delete(data);
        }
        else if (dt.isItDirty() &&
                dt.getTargetActUid() != null &&
                dt.getSourceActUid() != null && dt.getTypeCd() != null)
        {
            actRelationshipRepository.save(data);
        }
    }
}
