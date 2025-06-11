package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ActRepositoryUtilTest {
    @InjectMocks
    private ActRepositoryUtil actRepositoryUtil;

    @Mock
    private ActJdbcRepository actRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertActivityId() {
        Long uid = 1L;
        String classCode = "classCode";
        String moodCode = "moodCode";

        actRepositoryUtil.insertActivityId(uid, classCode, moodCode);

        verify(actRepository, times(1)).insertAct(any());
    }

    @Test
    void testInsertActivityIdWithNullValues() {
        Long uid = null;
        String classCode = null;
        String moodCode = null;

        actRepositoryUtil.insertActivityId(uid, classCode, moodCode);

        verify(actRepository, times(1)).insertAct(any());
    }

    @Test
    void testInsertActivityIdWithEmptyValues() {
        Long uid = 1L;
        String classCode = "";
        String moodCode = "";

        actRepositoryUtil.insertActivityId(uid, classCode, moodCode);

        verify(actRepository, times(1)).insertAct(any());
    }
}
