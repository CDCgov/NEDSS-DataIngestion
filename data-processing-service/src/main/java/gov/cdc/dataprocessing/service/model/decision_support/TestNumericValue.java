package gov.cdc.dataprocessing.service.model.decision_support;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TestNumericValue {
        private String testCode;
        private String testCodeDesc;
        private String comparatorCode;
        private String comparatorCodeDesc;
        private BigDecimal value1;
        private String separatorCode;
        private BigDecimal value2;
        private String unitCode;
        private String unitCodeDesc;




}
