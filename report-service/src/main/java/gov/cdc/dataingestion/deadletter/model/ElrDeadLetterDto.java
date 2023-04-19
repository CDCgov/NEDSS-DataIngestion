package gov.cdc.dataingestion.deadletter.model;


import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
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

    public ElrDeadLetterDto(String errorMessageId, String errorMessageSource,
                            String errorStackTrace,
                            Integer dltOccurrence, String dltStatus,
                            String createdBy, String updatedBy) {
        this.errorMessageId = errorMessageId;
        this.errorMessageSource = errorMessageSource;
        this.dltOccurrence = dltOccurrence;
        this.dltStatus = dltStatus;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.errorStackTrace = errorStackTrace;
    }

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
