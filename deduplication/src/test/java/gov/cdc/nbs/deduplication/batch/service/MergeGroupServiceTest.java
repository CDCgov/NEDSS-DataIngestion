package gov.cdc.nbs.deduplication.batch.service;

import com.lowagie.text.pdf.PdfReader;

import gov.cdc.nbs.deduplication.batch.model.MatchCandidateData;
import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse;
import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.MergeGroupService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MergeGroupServiceTest {

  @Mock
  private NamedParameterJdbcTemplate deduplicationTemplate;

  @InjectMocks
  private MergeGroupService mergeGroupService;

  @Test
  @SuppressWarnings("unchecked")
  void fetchAllMatchesRequiringReview_ReturnsListOfMatchCandidateData() throws SQLException {
    // Given
    String personUid = "person-123";
    long numOfMatching = 3L;
    String dateIdentified = "2023-09-15";

    ResultSet mockResultSet = mock(ResultSet.class);
    when(mockResultSet.getString("person_uid")).thenReturn(personUid);
    when(mockResultSet.getLong("num_of_matching")).thenReturn(numOfMatching);
    when(mockResultSet.getString("date_identified")).thenReturn(dateIdentified);

    when(deduplicationTemplate.query(
        eq(QueryConstants.FETCH_ALL_MATCH_CANDIDATES_REQUIRING_REVIEW),
        any(MapSqlParameterSource.class),
        any(RowMapper.class))).thenAnswer(invocation -> {
          RowMapper<MatchCandidateData> rowMapper = invocation.getArgument(2);
          MatchCandidateData data = rowMapper.mapRow(mockResultSet, 0);
          return Collections.singletonList(data);
        });

    // When
    List<MatchCandidateData> result = mergeGroupService.fetchAllMatchesRequiringReview();

    // Then
    assertThat(result).hasSize(1);
    MatchCandidateData data = result.get(0);
    assertThat(data.personUid()).isEqualTo(personUid);
    assertThat(data.numOfMatches()).isEqualTo(numOfMatching);
    assertThat(data.dateIdentified()).isEqualTo(dateIdentified);
  }

  @Test
  void writeMatchesRequiringReviewPDF_writesValidPDFToResponse() throws Exception {
    // Arrange
    List<MatchesRequireReviewResponse.MatchRequiringReview> matches = List.of(
        new MatchesRequireReviewResponse.MatchRequiringReview(
            "123456", "John Doe", "2023-09-10T10:00:00", "2023-09-11T11:30:00", 2));

    ByteArrayOutputStream pdfContent = new ByteArrayOutputStream();

    HttpServletResponse response = mock(HttpServletResponse.class);
    ServletOutputStream outputStream = new DelegatingServletOutputStream(pdfContent);
    when(response.getOutputStream()).thenReturn(outputStream);

    // Act
    mergeGroupService.writeMatchesRequiringReviewPDF(response, matches, "20250101_123456", "April 29, 2025");

    // Assert
    verify(response).setContentType("application/pdf");
    verify(response).setHeader(eq("Content-Disposition"), contains("matches_requiring_review_20250101_123456.pdf"));
    assertThat(pdfContent.size()).isPositive();

    // Validate it's a valid PDF
    PdfReader reader = new PdfReader(pdfContent.toByteArray());
    assertThat(reader.getNumberOfPages()).isPositive();
    reader.close();
  }

  // Helper class to bridge ServletOutputStream and ByteArrayOutputStream
  static class DelegatingServletOutputStream extends ServletOutputStream {
    private final OutputStream targetStream;

    public DelegatingServletOutputStream(OutputStream targetStream) {
      this.targetStream = targetStream;
    }

    @Override
    public void write(int b) {
      try {
        targetStream.write(b);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public boolean isReady() {
      return true; // Always ready in this mocked context
    }

    @Override
    public void setWriteListener(WriteListener listener) {
      // No-op for testing
    }
  }
}
