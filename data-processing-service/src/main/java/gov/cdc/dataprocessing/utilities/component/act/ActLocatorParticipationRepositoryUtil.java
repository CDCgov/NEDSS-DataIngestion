package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ActLocatorParticipationRepositoryUtil {
    private final ActLocatorParticipationRepository actLocatorParticipationRepository;

    public ActLocatorParticipationRepositoryUtil(ActLocatorParticipationRepository actLocatorParticipationRepository) {
        this.actLocatorParticipationRepository = actLocatorParticipationRepository;
    }

    public Collection<ActivityLocatorParticipationDto> getActLocatorParticipationCollection(Long actUid) {
        var res = actLocatorParticipationRepository.findRecordsById(actUid);
        Collection<ActivityLocatorParticipationDto> dtoCollection = new ArrayList<>();
        if (!res.isEmpty()) {
            for (var item : res) {
                var dto = new ActivityLocatorParticipationDto(item);
                dto.setItNew(false);
                dto.setItDirty(false);
                dtoCollection.add(dto);
            }
        }
        return dtoCollection;
    }


    public void insertActLocatorParticipationCollection(Long uid, Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection) {
        for (var item : activityLocatorParticipationDtoCollection) {
            ActLocatorParticipation data = new ActLocatorParticipation(item);
            data.setActUid(uid);
            actLocatorParticipationRepository.save(data);
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
        }
    }
}
