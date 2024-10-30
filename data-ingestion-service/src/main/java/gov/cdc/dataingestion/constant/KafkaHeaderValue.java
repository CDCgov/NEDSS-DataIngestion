package gov.cdc.dataingestion.constant;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class KafkaHeaderValue {
    private KafkaHeaderValue() {

    }
    public static final String MESSAGE_TYPE = "MESSAGE-TYPE";
    public static final String MESSAGE_VALIDATION_ACTIVE = "MESSAGE-VALIDATION-APPLIED";
    public static final String DLT_OCCURRENCE = "DLT-OCCURRENCE";
    public static final String ORIGINAL_TOPIC = "kafka_original-topic";
    public static final String MESSAGE_TYPE_HL7 = "HL7";
    public static final String MESSAGE_TYPE_CSV = "CSV";

    public static final String MESSAGE_VERSION = "MESSAGE_VERSION";

    public static final String MESSAGE_OPERATION = "MESSAGE_OPERATION";
    public static final String DATA_PROCESSING_ENABLE = "DATA_PROCESSING_ENABLE";
    public static final String DATA_TYPE = "DATA_TYPE";
}