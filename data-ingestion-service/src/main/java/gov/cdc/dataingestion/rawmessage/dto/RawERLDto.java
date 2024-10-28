package gov.cdc.dataingestion.rawmessage.dto;

import lombok.Data;

@Data
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class RawERLDto {

    private String id;
    private String type;
    private String payload;
    private Boolean validationActive = false;

}
