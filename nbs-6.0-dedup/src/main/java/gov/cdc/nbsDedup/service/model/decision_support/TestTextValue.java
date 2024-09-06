package gov.cdc.nbsDedup.service.model.decision_support;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestTextValue {
    private String testCode;
    private String testCodeDesc;
    private String comparatorCode;
    private String comparatorCodeDesc;
    private String textValue;

}
