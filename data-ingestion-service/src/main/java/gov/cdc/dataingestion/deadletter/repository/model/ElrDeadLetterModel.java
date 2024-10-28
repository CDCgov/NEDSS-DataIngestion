package gov.cdc.dataingestion.deadletter.repository.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "elr_dlt")
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class ElrDeadLetterModel {

    @Id
    @Column(name = "error_message_id",  columnDefinition="uniqueidentifier")
    private String errorMessageId;

    @Column(name = "error_message_source")
    private String errorMessageSource;

    @Column(name = "error_stack_trace")
    private String errorStackTrace;

    @Column(name = "error_stack_trace_short")
    private String errorStackTraceShort;

    @Column(name = "dlt_occurrence")
    private int dltOccurrence;

    @Column(name="dlt_status")
    private String dltStatus;

    @Column(name="message")
    private String message;

    @Basic(optional = false)
    @Column(name = "created_on",insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdOn;

    @Column(name = "updated_on")
    private Timestamp updatedOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

}
