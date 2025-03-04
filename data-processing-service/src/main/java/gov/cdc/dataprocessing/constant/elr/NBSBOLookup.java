package gov.cdc.dataprocessing.constant.elr;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class NBSBOLookup {
    public static final String ASSOCIATEINTERVENTIONVACCINERECORDS = "ASSOCIATEINTERVENTIONVACCINERECORDS";
    public static final String ORGANIZATION = "ORGANIZATION";
    public static final String PATIENT = "PATIENT";
    public static final String OBSERVATIONGENERICOBSERVATION  = "OBSERVATIONGENERICOBSERVATION";
    public static final String OBSERVATIONMORBIDITYREPORT = "OBSERVATIONMORBIDITYREPORT";
    public static final String OBSERVATIONLABREPORT = "OBSERVATIONLABREPORT";
    public static final String OBSERVATIONMORBREPORT = "OBSERVATIONMORBREPORT";
    public static final String REPORTING = "REPORTING";
    public static final String INTERVENTIONVACCINERECORD = "INTERVENTIONVACCINERECORD";
    public static final String SYSTEM = "SYSTEM";
    public static final String NOTIFICATION = "NOTIFICATION";
    public static final String CASEREPORTING = "CASEREPORTING";
    public static final String DOCUMENT = "DOCUMENT";
    public static final String INVESTIGATION = "INVESTIGATION";
    public static final String SUMMARYREPORT = "SUMMARYREPORT";
    public static final String MATERIAL = "material";
    public static final String SRT = "SRT";
    public static final String PROVIDER = "PROVIDER";
    public static final String TREATMENT = "TREATMENT";
    public static final String CT_CONTACT= "CT_CONTACT";
    public static final String QUEUES= "QUEUES";
    public static final String PUBLICQUEUES= "PUBLICQUEUES";

    public static final String GLOBAL= "GLOBAL";
    public static final String PLACE= "PLACE";
    public static final String INTERVIEW= "INTERVIEW";
}
