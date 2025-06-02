package gov.cdc.nbs.deduplication.merge;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import gov.cdc.nbs.deduplication.batch.model.MatchesRequireReviewResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PdfBuilder {

  public void build(HttpServletResponse response,
                    List<MatchesRequireReviewResponse.MatchRequiringReview> matches, String timestampForFilename,
                    String timestampForFooter) throws IOException {
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition",
            "attachment; filename=matches_requiring_review_" + timestampForFilename + ".pdf");

    try (Document document = new Document(PageSize.A4)) {
      PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
      writer.setPageEvent(new FooterWithTimestamp(timestampForFooter));
      document.open();

      Font headingFont = new Font(Font.HELVETICA, 16, Font.BOLD);
      Paragraph heading = new Paragraph("Matches Requiring Review", headingFont);
      heading.setAlignment(Element.ALIGN_CENTER);
      heading.setSpacingAfter(20f);
      document.add(heading);

      Font tableFont = new Font(Font.HELVETICA, 9);
      PdfPTable table = new PdfPTable(5);
      table.setWidthPercentage(100);
      table.setSpacingBefore(10f);
      table.setWidths(new float[]{2f, 3f, 2.5f, 2.5f, 3.5f});

      Stream.of("Patient ID", "Person Name", "Date Created", "Date Identified", "Number of Matching Records")
              .forEach(header -> {
                PdfPCell cell = new PdfPCell(new Phrase(header, tableFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
              });

      for (MatchesRequireReviewResponse.MatchRequiringReview match : matches) {
        table.addCell(createCenteredCell(String.valueOf(match.patientId()), tableFont));
        table.addCell(createCenteredCell(match.patientName(), tableFont));
        table.addCell(createCenteredCell(formatDateTime(match.createdDate()), tableFont));
        table.addCell(createCenteredCell(formatDateTime(match.identifiedDate()), tableFont));
        table.addCell(createCenteredCell(String.valueOf(match.numOfMatchingRecords()), tableFont));
      }

      document.add(table);
    } catch (DocumentException e) {
      throw new IOException("Error generating PDF", e);
    }
  }

  private PdfPCell createCenteredCell(String text, Font font) {
    PdfPCell cell = new PdfPCell(new Phrase(text, font));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    return cell;
  }

  public String formatDateTime(String rawDateTime) {
    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
    List<DateTimeFormatter> inputFormatters = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));

    for (DateTimeFormatter formatter : inputFormatters) {
      try {
        LocalDateTime parsed = LocalDateTime.parse(rawDateTime, formatter);
        return parsed.format(outputFormatter);
      } catch (DateTimeParseException ignored) {
        // Intentionally ignored — will try next formatter
      }
    }
    return rawDateTime;
  }

  private static class FooterWithTimestamp extends PdfPageEventHelper {
    private final String timestamp;

    public FooterWithTimestamp(String timestamp) {
      this.timestamp = timestamp;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
      Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);
      Phrase footer = new Phrase("Generated on: " + timestamp + " | Page " + writer.getPageNumber(), footerFont);

      ColumnText.showTextAligned(
              writer.getDirectContent(),
              Element.ALIGN_LEFT,
              footer,
              document.leftMargin(),
              document.bottomMargin() - 10,
              0);
    }
  }
}

