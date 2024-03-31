package gov.cdc.dataingestion.odse.repository.model;

/**
 * This is JPA Projection class for the
 * getEdxActivityLogDetailsBySourceId in IEdxActivityLogRepository.java
 */
public interface EdxActivityLogModelView {
    String getRecordId();
    String getRecordType();
    String getLogType();
    String getLogComment();
}