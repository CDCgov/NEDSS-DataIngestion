package gov.cdc.dataingestion.deadletter.repository.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "elr_dlt")
public class ElrDeadLetterModel {

    @Id
    @Column(name = "error_message_id",  columnDefinition="uniqueidentifier")
    private String errorMessageId;

    @Column(name = "error_message_source")
    private String errorMessageSource;

    @Column(name = "error_stack_trace")
    private String errorStackTrace;

    @Column(name = "dlt_occurrence")
    private int dltOccurrence;

    @Column(name="dlt_status")
    private String dltStatus;

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
