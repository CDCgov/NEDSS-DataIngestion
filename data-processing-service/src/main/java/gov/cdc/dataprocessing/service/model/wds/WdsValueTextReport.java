package gov.cdc.dataprocessing.service.model.wds;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class WdsValueTextReport {
    private String codeType;
    private String inputCode;
    private String wdsCode;
    private boolean matchedFound;
}
