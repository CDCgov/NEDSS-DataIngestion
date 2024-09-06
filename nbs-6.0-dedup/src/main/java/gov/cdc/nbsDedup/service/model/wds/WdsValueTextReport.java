package gov.cdc.nbsDedup.service.model.wds;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WdsValueTextReport {
    private String codeType;
    private String inputCode;
    private String wdsCode;
    private boolean matchedFound;
}
