package gov.cdc.nbs.mpidatasyncer.enums;

import lombok.Getter;

@Getter
public enum LogLevel {
  SUCCESS("SUCCESS"),
  ERROR("ERROR"),
  WARN("WARN"),
  INFO("INFO");

  private final String status;

  LogLevel(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return this.status;
  }
}
