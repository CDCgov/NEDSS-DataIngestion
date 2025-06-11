package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActRelationshipJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationshipHistory;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component

public class ActRelationshipRepositoryUtil {
    private final ActRelationshipJdbcRepository actRelationshipJdbcRepository;
    @Value("${service.timezone}")
    private String tz = "UTC";
    public ActRelationshipRepositoryUtil(ActRelationshipJdbcRepository actRelationshipJdbcRepository) {
        this.actRelationshipJdbcRepository = actRelationshipJdbcRepository;
    }

    public Collection<ActRelationshipDto> getActRelationshipCollectionFromSourceId(Long actUid) {
        var res = actRelationshipJdbcRepository.findBySourceActUid(actUid);
        Collection<ActRelationshipDto> dtoCollection = new ArrayList<>();
        if (res != null && !res.isEmpty()) {
            for(var item : res) {
                var dto  = new ActRelationshipDto(item);
                dto.setItNew(false);
                dto.setItDirty(false);
                dtoCollection.add(dto);
            }
        }
        return dtoCollection;
    }

    public Collection<ActRelationshipDto> selectActRelationshipDTCollectionFromActUid(long aUID)
    {
        var col = actRelationshipJdbcRepository.findByTargetActUid(aUID);
        Collection<ActRelationshipDto> dtCollection = new ArrayList<>();
        if (col != null && !col.isEmpty()) {
            for (var item : col) {
                ActRelationshipDto dt = new ActRelationshipDto(item);
                dt.setItNew(false);
                dt.setItDirty(false);
                dtCollection.add(dt);
            }
        }
        return dtCollection;
    }


    public void insertActRelationshipHist(ActRelationshipDto actRelationshipDto) {
        var hst = new ActRelationshipHistory(actRelationshipDto);
        actRelationshipJdbcRepository.insertActRelationshipHistory(hst);
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
            data.setLastChgTime(TimeStampUtil.getCurrentTimeStamp(tz)); // TODO: CHECK_LAST_TS
            actRelationshipJdbcRepository.insertActRelationship(data);
        }
        else if (dt.isItDelete())
        {
            actRelationshipJdbcRepository.deleteActRelationship(data);
        }
        else if (dt.isItDirty() &&
                dt.getTargetActUid() != null &&
                dt.getSourceActUid() != null && dt.getTypeCd() != null)
        {
            actRelationshipJdbcRepository.updateActRelationship(data);
        }
    }
}
