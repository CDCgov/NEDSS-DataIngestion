package gov.cdc.dataprocessing.model.dto.edx;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public  class EDXEventProcessCaseSummaryDto extends EDXEventProcessDto {
    private static final long serialVersionUID = 1L;

    private String conditionCd;
    private Long personParentUid;
    private Long personUid;
    private String personLocalId;
}
