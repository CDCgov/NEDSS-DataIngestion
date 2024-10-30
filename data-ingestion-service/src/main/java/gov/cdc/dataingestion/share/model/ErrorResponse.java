package gov.cdc.dataingestion.share.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String details;
}
