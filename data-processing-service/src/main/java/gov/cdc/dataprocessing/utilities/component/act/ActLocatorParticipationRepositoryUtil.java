package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActLocatorParticipationJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component

public class ActLocatorParticipationRepositoryUtil {
    private final ActLocatorParticipationJdbcRepository actLocatorParticipationJdbcRepository;

    public ActLocatorParticipationRepositoryUtil(
            ActLocatorParticipationJdbcRepository actLocatorParticipationJdbcRepository) {
        this.actLocatorParticipationJdbcRepository = actLocatorParticipationJdbcRepository;
    }

    public Collection<ActivityLocatorParticipationDto> getActLocatorParticipationCollection(Long actUid) {
        //var res = actLocatorParticipationRepository.findRecordsById(actUid);
        var res = actLocatorParticipationJdbcRepository.findByActUid(actUid);
        Collection<ActivityLocatorParticipationDto> dtoCollection = new ArrayList<>();
        if (!res.isEmpty()) {
            for(var item : res) {
                var dto  = new ActivityLocatorParticipationDto(item);
                dto.setItNew(false);
                dto.setItDirty(false);
                dtoCollection.add(dto);
            }
        }
        return dtoCollection;
    }


    public void insertActLocatorParticipationCollection(Long uid, Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection) {
        for(var item : activityLocatorParticipationDtoCollection) {
            ActLocatorParticipation data = new ActLocatorParticipation(item);
            data.setActUid(uid);
//            actLocatorParticipationRepository.save(data);
            actLocatorParticipationJdbcRepository.mergeActLocatorParticipation(data);
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
        }
    }
}
