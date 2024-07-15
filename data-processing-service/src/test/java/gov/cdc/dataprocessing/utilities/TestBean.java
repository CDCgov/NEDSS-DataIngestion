package gov.cdc.dataprocessing.utilities;


import java.math.BigDecimal;
import java.sql.Timestamp;

public class TestBean {
    private String stringValue;
    private Long longValue;
    private Integer integerValue;
    private BigDecimal bigDecimalValue;
    private Timestamp timestampValue;
    private boolean booleanValue;

    // Getters and Setters
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue = bigDecimalValue;
    }

    public Timestamp getTimestampValue() {
        return timestampValue;
    }

    public void setTimestampValue(Timestamp timestampValue) {
        this.timestampValue = timestampValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }
}
