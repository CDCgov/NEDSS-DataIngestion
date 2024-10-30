package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public class ActLocatorParticipationRepositoryUtil {
    private final ActLocatorParticipationRepository actLocatorParticipationRepository;

    public ActLocatorParticipationRepositoryUtil(ActLocatorParticipationRepository actLocatorParticipationRepository) {
        this.actLocatorParticipationRepository = actLocatorParticipationRepository;
    }

    public Collection<ActivityLocatorParticipationDto> getActLocatorParticipationCollection(Long actUid) {
        var res = actLocatorParticipationRepository.findRecordsById(actUid);
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
            actLocatorParticipationRepository.save(data);
            item.setItNew(false);
            item.setItDirty(false);
            item.setItDelete(false);
        }
    }
}
