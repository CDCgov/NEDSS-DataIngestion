package gov.cdc.nbs.deduplication.duplicates.step;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MatchCandidateWriterTest {

  @Mock
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @InjectMocks
  private MatchCandidateWriter writer;

  @Test
  void initializes() {
    MatchCandidateWriter newWriter = new MatchCandidateWriter(namedParameterJdbcTemplate);
    assertThat(newWriter).isNotNull();
  }

  @Test
  void writesChunkWithPossibleMatches() {
    List<MatchCandidate> candidates = new ArrayList<>();
    candidates.add(new MatchCandidate("nbsId1", List.of("possibleMatch1")));
    candidates.add(new MatchCandidate("nbsId2", List.of("possibleMatch2", "possibleMatch3")));
    var chunk = new Chunk<>(candidates);

    writer.write(chunk);

    verify(namedParameterJdbcTemplate, times(1)).batchUpdate(Mockito.anyString(),
        Mockito.any(MapSqlParameterSource[].class));
    verify(namedParameterJdbcTemplate, times(1)).update(Mockito.anyString(), any(MapSqlParameterSource.class));
  }


  @Test
  void writesChunkWithNoPossibleMatches() {
    List<MatchCandidate> candidates = new ArrayList<>();
    candidates.add(new MatchCandidate("nbsId1", null)); // No possible matches
    var chunk = new Chunk<>(candidates);

    writer.write(chunk);

    verify(namedParameterJdbcTemplate, never()).batchUpdate(Mockito.anyString(),
        Mockito.any(MapSqlParameterSource[].class));
    verify(namedParameterJdbcTemplate, times(1)).update(Mockito.anyString(), any(MapSqlParameterSource.class));
  }

}
