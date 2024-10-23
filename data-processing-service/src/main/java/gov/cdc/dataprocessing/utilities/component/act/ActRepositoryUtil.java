package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import org.springframework.stereotype.Component;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class ActRepositoryUtil {
    private final ActRepository actRepository;

    public ActRepositoryUtil(ActRepository actRepository) {
        this.actRepository = actRepository;
    }

    public void insertActivityId(Long uid, String classCode, String moodCode) {
        Act act = new Act();
        act.setActUid(uid);
        act.setClassCode(classCode);
        act.setMoodCode(moodCode);

        actRepository.save(act);
    }
}
