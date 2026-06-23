package gov.cdc.dataingestion.reportstatus.model;

import java.util.List;
import java.util.UUID;

public record ElrStatus(UUID id, String status, List<Detail> details) {

  public record Detail(long messageId, String status) {}
}
