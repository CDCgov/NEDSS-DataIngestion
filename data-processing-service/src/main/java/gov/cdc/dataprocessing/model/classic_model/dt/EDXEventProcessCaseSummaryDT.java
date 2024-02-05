package gov.cdc.dataprocessing.model.classic_model.dt;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EDXEventProcessCaseSummaryDT extends EDXEventProcessDT{
    private static final long serialVersionUID = 1L;

    private String conditionCd;
    private Long personParentUid;
    private Long personUid;
    private String personLocalId;
}
