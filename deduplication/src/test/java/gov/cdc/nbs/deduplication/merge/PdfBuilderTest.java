package gov.cdc.nbs.deduplication.merge;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.lowagie.text.pdf.PdfReader;

import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

class PdfBuilderTest {

  private final PdfBuilder pdfBuilder = new PdfBuilder();

  @Test
  void createsPdf() throws Exception {
    // Arrange
    List<MatchesRequireReviewResponse.MatchRequiringReview> matches = List.of(
        new MatchesRequireReviewResponse.MatchRequiringReview(
            "123456",
            "444",
            "John Doe",
            "2023-09-10T10:00:00",
            "2023-09-11T11:30:00",
            2));

    ByteArrayOutputStream pdfContent = new ByteArrayOutputStream();

    HttpServletResponse response = mock(HttpServletResponse.class);
    ServletOutputStream outputStream = new DelegatingServletOutputStream(pdfContent);
    when(response.getOutputStream()).thenReturn(outputStream);

    // Act
    pdfBuilder.build(response, matches, "20250101_123456", "April 29, 2025");

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
