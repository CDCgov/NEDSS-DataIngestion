package gov.cdc.dataingestion.nbs.repository.model.dto.lookup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class ConstantLookUpDto {
    private String id;
    private String subjectArea;
    private String questionIdentifier;
    private String questionDisplayName;
    private String sampleValue;
    private String usage;
}
