package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import org.springframework.stereotype.Component;

@Component

public class ActRepositoryUtil {
    private final ActJdbcRepository actJdbcRepository;

    public ActRepositoryUtil(ActJdbcRepository actJdbcRepository) {
        this.actJdbcRepository = actJdbcRepository;
    }

    public void insertActivityId(Long uid, String classCode, String moodCode) {
        Act act = new Act();
        act.setActUid(uid);
        act.setClassCode(classCode);
        act.setMoodCode(moodCode);
        actJdbcRepository.insertAct(act);
    }

    public void updateActivityId(Long uid, String classCode, String moodCode) {
        Act act = new Act();
        act.setActUid(uid);
        act.setClassCode(classCode);
        act.setMoodCode(moodCode);
        actJdbcRepository.updateAct(act);
    }
}
