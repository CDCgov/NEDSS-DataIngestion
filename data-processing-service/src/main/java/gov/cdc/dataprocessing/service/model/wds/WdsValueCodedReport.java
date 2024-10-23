package gov.cdc.dataprocessing.service.model.wds;


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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class WdsValueCodedReport {
    private String codeType;
    private String inputCode;
    private String wdsCode;
    private boolean matchedFound;
}
