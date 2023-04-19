package gov.cdc.dataingestion.deadletter.model;


import gov.cdc.dataingestion.deadletter.repository.model.DeadLetterELRModel;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.sql.Timestamp;

public class DeadLetterELRDto {
    private String id;

    private String rawId;

    private String rawMessage;

    private String errorStackTrace;

    private Integer dltOccurrence;

    private String dltStatus;

    private Timestamp createdOn;

    private Timestamp updatedOn;

    private String createdBy;

    private String updatedBy;

    public DeadLetterELRDto(DeadLetterELRModel model) {
        this.id = model.getId();
        this.rawMessage = model.getRawMessage();
        this.errorStackTrace = model.getErrorStackTrace();
        this.dltOccurrence = model.getDltOccurrence();
        this.dltStatus = model.getDltStatus();
        this.createdOn = model.getCreatedOn();
        this.updatedOn = model.getUpdatedOn();
        this.createdBy = model.getCreatedBy();
        this.updatedBy = model.getUpdatedBy();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRawId() {
        return rawId;
    }

    public void setRawId(String rawId) {
        this.rawId = rawId;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public String getErrorStackTrace() {
        return errorStackTrace;
    }

    public void setErrorStackTrace(String errorStackTrace) {
        this.errorStackTrace = errorStackTrace;
    }

    public Integer getDltOccurrence() {
        return dltOccurrence;
    }

    public void setDltOccurrence(Integer dltOccurrence) {
        this.dltOccurrence = dltOccurrence;
    }

    public String getDltStatus() {
        return dltStatus;
    }

    public void setDltStatus(String dltStatus) {
        this.dltStatus = dltStatus;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
