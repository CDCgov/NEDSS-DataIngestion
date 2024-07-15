package gov.cdc.dataprocessing.service.model.wds;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class WdsValueNumericReport {
    private String codeType;
    private String inputCode1;
    private String inputCode2;
    private String operator;
    private String wdsCode;
    private boolean matchedFound;
}
