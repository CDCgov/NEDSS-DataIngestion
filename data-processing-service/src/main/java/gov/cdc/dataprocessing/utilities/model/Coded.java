package gov.cdc.dataprocessing.utilities.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
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


