package gov.cdc.nbs.deduplication.duplicates.service;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MergePatientHandlerTest {

  @Mock
  private NamedParameterJdbcTemplate nbsNamedTemplate;

  @Mock
  private MergeGroupHandler mergeGroupHandler;

  @InjectMocks
  private MergePatientHandler mergePatientHandler;



  @Test
  void testPerformMerge() {
    String survivorPersonId = "survivor1";
    List<String> supersededPersonIds = Arrays.asList("superseded1", "superseded2");

    mergePatientHandler.performMerge(survivorPersonId, supersededPersonIds);

    // Verify that the superseded records are marked
    verify(nbsNamedTemplate, times(1)).update(eq(QueryConstants.MARK_SUPERSEDED_RECORDS),
        any(MapSqlParameterSource.class));

    // Verify that merge metadata is created
    verify(nbsNamedTemplate, times(1)).batchUpdate(eq(QueryConstants.CREATE_MERGE_METADATA),
        any(MapSqlParameterSource[].class));
  }
}
