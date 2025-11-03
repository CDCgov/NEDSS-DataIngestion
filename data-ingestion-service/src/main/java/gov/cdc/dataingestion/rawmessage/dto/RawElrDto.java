package gov.cdc.dataingestion.rawmessage.dto;

import lombok.Data;

@Data
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class RawElrDto {

    private String id;
    private String type;
    private String payload;
    private String version;
    private String dataSource;
    private Boolean validationActive = false;
    private String customMapper;
}
