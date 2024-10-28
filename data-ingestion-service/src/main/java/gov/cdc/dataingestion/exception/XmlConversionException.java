package gov.cdc.dataingestion.exception;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class XmlConversionException extends Exception {
    public XmlConversionException(String message) {
        super(message);
    }
}
