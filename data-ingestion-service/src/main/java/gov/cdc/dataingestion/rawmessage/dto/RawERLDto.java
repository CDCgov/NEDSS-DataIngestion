package gov.cdc.dataingestion.rawmessage.dto;

import lombok.Data;

@Data
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class RawERLDto {

    private String id;
    private String type;
    private String payload;
    private Boolean validationActive = false;

}
