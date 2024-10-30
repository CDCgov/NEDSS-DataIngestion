package gov.cdc.dataprocessing.service.model.wds;

import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class WdsTrackerView {
    private List<WdsReport> wdsReport;
    PublicHealthCaseDto publicHealthCase;
    Long patientUid;
    Long patientParentUid;
    String patientFirstName;
    String patientLastName;
}
