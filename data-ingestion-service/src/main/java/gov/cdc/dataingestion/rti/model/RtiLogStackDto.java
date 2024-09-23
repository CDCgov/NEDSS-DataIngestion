package gov.cdc.dataingestion.rti.model;

import gov.cdc.dataingestion.rti.repository.model.RtiLog;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RtiLogStackDto {
    private Integer nbsInterfaceId;
    private String step;
    private String stackTrace;

    public RtiLogStackDto() {

    }

    public RtiLogStackDto(RtiLog rtiLog) {
        nbsInterfaceId = rtiLog.getNbsInterfaceId();
        step = rtiLog.getRtiStep();
        stackTrace = rtiLog.getStackTrace();
    }
}
