package gov.cdc.dataingestion.rti.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RtiLogStackDto {
    private Integer nbsInterfaceId;
    private String step;
    private String stackTrace;
}
