package gov.cdc.srtedataservice.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalUidModel {
    private LocalUidGeneratorDto classTypeUid;
    private LocalUidGeneratorDto gaTypeUid;
    private String primaryClassName;
}
