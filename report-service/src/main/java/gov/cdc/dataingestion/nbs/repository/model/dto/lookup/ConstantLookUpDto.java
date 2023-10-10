package gov.cdc.dataingestion.nbs.repository.model.dto.lookup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ConstantLookUpDto {
    private String id;
    private String subjectArea;
    private String questionIdentifier;
    private String questionDisplayName;
    private String sampleValue;
    private String usage;
}
