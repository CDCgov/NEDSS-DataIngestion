package gov.cdc.dataprocessing.service.model.log;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogStack {
    private Integer nbsInterfaceId;
    private String step;
    private String stackTrace;
}
