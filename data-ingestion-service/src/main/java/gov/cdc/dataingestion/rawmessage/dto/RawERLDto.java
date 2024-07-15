package gov.cdc.dataingestion.rawmessage.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RawERLDto {

    private String id;
    private String type;
    private String payload;
    private Boolean validationActive = false;

}
