package gov.cdc.dataingestion.nbs.repository.model.dto.lookup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class ConstantLookUpDto {
    private String id;
    private String subjectArea;
    private String questionIdentifier;
    private String questionDisplayName;
    private String sampleValue;
    private String usage;
}
