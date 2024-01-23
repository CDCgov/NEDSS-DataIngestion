package gov.cdc.dataingestion.constant;

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

    public static final String INGESTION_MODE = "INGESTION_MODE";
}