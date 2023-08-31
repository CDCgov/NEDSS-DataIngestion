package gov.cdc.dataingestion.rawmessage.dto;

import lombok.Data;

@Data
public class RawERLDto {

    private String id;
    private String type;
    private String payload;
    private Boolean validationActive = false;

}
