package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxEventProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.constant.query.EdxEventProcessQuery.MERGE_EDX_EVENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EdxEventProcessJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private EdxEventProcessJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMergeEdxEventProcess_shouldCallUpdate() {
        EdxEventProcess edx = new EdxEventProcess();
        edx.setEdxEventProcessUid(1L);
        edx.setNbsDocumentUid(2L);
        edx.setNbsEventUid(3L);
        edx.setSourceEventId("srcEvent123");
        edx.setDocEventTypeCd("typeCd");
        edx.setAddUserId(101L);
        edx.setAddTime(new Timestamp(System.currentTimeMillis()));
        edx.setParsedInd("Y");
        edx.setEdxDocumentUid(4L);

        repository.mergeEdxEventProcess(edx);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_EDX_EVENT), any(MapSqlParameterSource.class));
    }
}
