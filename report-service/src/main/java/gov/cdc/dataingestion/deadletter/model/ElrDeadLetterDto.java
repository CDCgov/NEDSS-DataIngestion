package gov.cdc.dataingestion.deadletter.model;


import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ElrDeadLetterDto {
    private String id;

    private String errorMessageId;

    private String errorMessageSource;

    private String errorStackTrace;

    private Integer dltOccurrence;

    private String dltStatus;

    private Timestamp createdOn;

    private Timestamp updatedOn;

    private String createdBy;

    private String updatedBy;

    public ElrDeadLetterDto(ElrDeadLetterModel model) {
        this.id = model.getId();
        this.errorMessageSource = model.getErrorMessageSource();
        this.errorStackTrace = model.getErrorStackTrace();
        this.dltOccurrence = model.getDltOccurrence();
        this.dltStatus = model.getDltStatus();
        this.createdOn = model.getCreatedOn();
        this.updatedOn = model.getUpdatedOn();
        this.createdBy = model.getCreatedBy();
        this.updatedBy = model.getUpdatedBy();
    }

}
