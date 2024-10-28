package gov.cdc.dataingestion.exception;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class EcrCdaXmlException  extends Exception {
    public EcrCdaXmlException(String message) {
        super(message);
    }
}
