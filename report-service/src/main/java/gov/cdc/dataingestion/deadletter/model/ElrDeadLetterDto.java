package gov.cdc.dataingestion.deadletter.model;


import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class ElrDeadLetterDto {

    private String errorMessageId;

    private String errorMessageSource;

    private String message;

    private String errorStackTrace;

    private Integer dltOccurrence;

    private String dltStatus;

    private Timestamp createdOn;

    private Timestamp updatedOn;

    private String createdBy;

    private String updatedBy;

    public ElrDeadLetterDto() {};

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
        this.errorStackTrace = processingSourceStackTrace(errorStackTrace);
    }

    public ElrDeadLetterDto(ElrDeadLetterModel model, String errorMessage) {
        this.errorMessageId = model.getErrorMessageId();
        this.errorMessageSource = model.getErrorMessageSource();
        this.errorStackTrace = processingSourceStackTrace(model.getErrorStackTrace());
        this.dltOccurrence = model.getDltOccurrence();
        this.dltStatus = model.getDltStatus();
        this.createdOn = model.getCreatedOn();
        this.updatedOn = model.getUpdatedOn();
        this.createdBy = model.getCreatedBy();
        this.updatedBy = model.getUpdatedBy();
        this.message = errorMessage;
    }

    public ElrDeadLetterDto(ElrDeadLetterModel model) {
        this.errorMessageId = model.getErrorMessageId();
        this.errorMessageSource = model.getErrorMessageSource();
        this.errorStackTrace = processingSourceStackTrace(model.getErrorStackTrace());
        this.dltOccurrence = model.getDltOccurrence();
        this.dltStatus = model.getDltStatus();
        this.createdOn = model.getCreatedOn();
        this.updatedOn = model.getUpdatedOn();
        this.createdBy = model.getCreatedBy();
        this.updatedBy = model.getUpdatedBy();
    }

    private String processingSourceStackTrace(String stackTrace) {
        String regex = "RuntimeException:\\s*(.*?)(?=\\r|\\n|$)";
        if (stackTrace == null) {
            return "";
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(stackTrace);
        if (matcher.find()) {
            String extractedString = matcher.group(1).trim();
            return extractedString;
        } else {
            return stackTrace;
        }
    }



}
