package gov.cdc.dataprocessing.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ServicePropertiesProvider {
    @Value("${service.timezone:UTC}")
    private String tz;

    @Value("${nedss.nbs-state-code:13}")
    private String nbsStateCode;

}
