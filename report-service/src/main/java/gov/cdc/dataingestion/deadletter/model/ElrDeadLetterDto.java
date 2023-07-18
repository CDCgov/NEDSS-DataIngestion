package gov.cdc.dataingestion.deadletter.model;


import gov.cdc.dataingestion.constant.enums.EnumElrServiceOperation;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

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

    private String errorStackTraceShort;

    private Integer dltOccurrence;

    private String dltStatus;

    private Timestamp createdOn;

    private Timestamp updatedOn;

    private String createdBy;

    private String updatedBy;

    public ElrDeadLetterDto() {};

    // Constructor for DLT in Kafka Consumer Service
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
        this.errorStackTraceShort = processingSourceStackTrace(errorStackTrace);
    }

    // Constructor for DLT service, get all error messages
    public ElrDeadLetterDto(ElrDeadLetterModel model, EnumElrServiceOperation operation) {
        this.errorMessageId = model.getErrorMessageId();
        this.errorMessageSource = model.getErrorMessageSource();
        this.errorStackTraceShort = model.getErrorStackTraceShort();
        this.dltOccurrence = model.getDltOccurrence();
        this.dltStatus = model.getDltStatus();
        this.createdOn = model.getCreatedOn();
        this.updatedOn = model.getUpdatedOn();
        this.createdBy = model.getCreatedBy();
        this.updatedBy = model.getUpdatedBy();

        if (operation == EnumElrServiceOperation.GET_DLT_BY_ID) {
            this.errorStackTrace = model.getErrorStackTrace();
            this.message = model.getMessage();
        }
    }

    /**
     * Description: this method take in the full stackTrace message and narrow down to root cause.
     * Stacktrace that is coming back from Kafka is appended with kafka listener stacktrace,
     * hence, right now we do this to extract the root message
     * */
    @NotNull
    private String processingSourceStackTrace(String stackTrace) {
        // Leading ^ and tailing [^\n]*+
        String regexCleanUpFirstLevel = "org\\.springframework\\.kafka\\.listener[^\\n]*+$";
        String regexCleanUpSecondLevel = "Caused by: gov.cdc.dataingestion.(.*+)";
        if (stackTrace == null) {
            return "";
        }
        Pattern pattern = Pattern.compile(regexCleanUpFirstLevel, Pattern.MULTILINE);
        String result = pattern.matcher(stackTrace).replaceAll("");
        pattern = Pattern.compile(regexCleanUpSecondLevel);
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            String extractedString = matcher.group(1).trim();
            return extractCustomExceptionMessage(extractedString);
        } else {
            return extractGenericExceptionMessage(result ,stackTrace);
        }
    }

    private String extractCustomExceptionMessage(String extractedString) {
        int colonCount = extractedString.split(":").length - 1;
        return extractCustomMessageAfterColon(extractedString, colonCount);
    }

    private String extractGenericExceptionMessage(String message, String originalMessage) {
        String regexCleanUpSecondLevelForGenericException = "Caused by: java.lang.Exception.(.*+)";
        var pattern = Pattern.compile(regexCleanUpSecondLevelForGenericException);
        var matcher = pattern.matcher(message);
        if (matcher.find()) {
            var extractedStringRoot = matcher.group(1);
            return extractedStringRoot.trim();
        } else {
            return originalMessage;
        }
    }

    private String extractCustomMessageAfterColon(String message, int numberColon) {
        String regex = ":(.*+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            var extractedStringRoot = matcher.group(1);
            if (numberColon <= 0) {
                return message.trim();
            }
            numberColon--;
            return extractCustomMessageAfterColon(extractedStringRoot,numberColon);
        } else {
            return message.trim();
        }
    }



}
