package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class StateDefinedFieldDataDT extends AbstractVO
{
    private Long ldfUid;
    private String businessObjNm;
    private Timestamp addTime;
    private Long businessObjUid;
    private Timestamp lastChgTime;
    private String ldfValue;
    private Integer versionCtrlNbr;
    private String conditionCd;
    private boolean itDirty = false;
    private String codeSetNm;
    private String fieldSize;
    private String dataType;
}
