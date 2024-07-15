package gov.cdc.dataprocessing.service.model.decision_support;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class TestTextValue {
    private String testCode;
    private String testCodeDesc;
    private String comparatorCode;
    private String comparatorCodeDesc;
    private String textValue;

}
