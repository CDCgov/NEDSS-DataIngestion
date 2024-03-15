package gov.cdc.dataprocessing.model.dto.edx;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXEventProcessDT;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EDXEventProcessCaseSummaryDto extends EDXEventProcessDT {
    private static final long serialVersionUID = 1L;

    private String conditionCd;
    private Long personParentUid;
    private Long personUid;
    private String personLocalId;
}
