package gov.cdc.dataprocessing.utilities.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coded {
    private String code;
    private String codeDescription;
    private String codeSystemCd;
    private String localCode;
    private String localCodeDescription;
    private String localCodeSystemCd;

    private Long codesetGroupId;
    private String codesetName;
    private String codesetTableName;
    private boolean flagNotFound;//this has been created as a fix for eicr ND-11745
}


