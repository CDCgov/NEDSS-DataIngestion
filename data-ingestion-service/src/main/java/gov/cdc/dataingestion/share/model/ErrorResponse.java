package gov.cdc.dataingestion.share.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String details;
}
