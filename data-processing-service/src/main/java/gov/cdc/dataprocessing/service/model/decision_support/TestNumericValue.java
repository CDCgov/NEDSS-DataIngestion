package gov.cdc.dataprocessing.service.model.decision_support;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

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
