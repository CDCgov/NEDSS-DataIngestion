package gov.cdc.dataprocessing.model.dto.generic_helper;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class StateDefinedFieldDataDto extends BaseContainer {
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
