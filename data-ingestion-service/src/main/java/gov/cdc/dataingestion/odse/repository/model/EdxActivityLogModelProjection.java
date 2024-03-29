package gov.cdc.dataingestion.odse.repository.model;

public interface EdxActivityLogModelProjection {
    String getRecordId();
    String getRecordType();
    String getLogType();
    String getLogComment();
}