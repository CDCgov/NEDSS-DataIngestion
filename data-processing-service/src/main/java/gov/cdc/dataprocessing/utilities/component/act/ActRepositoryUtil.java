package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import org.springframework.stereotype.Component;

@Component
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
