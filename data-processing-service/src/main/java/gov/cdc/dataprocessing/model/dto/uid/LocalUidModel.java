package gov.cdc.dataprocessing.model.dto.uid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LocalUidModel {
    private LocalUidGeneratorDto classTypeUid;
    private LocalUidGeneratorDto gaTypeUid;
    private String primaryClassName;
}
