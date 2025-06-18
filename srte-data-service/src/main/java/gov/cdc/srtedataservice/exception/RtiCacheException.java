package gov.cdc.srtedataservice.exception;


public class RtiCacheException extends Exception{

    public RtiCacheException(String message) {
        super(message);
    }

    public RtiCacheException(String message, Exception result) {
        super(message, result);
    }
}
