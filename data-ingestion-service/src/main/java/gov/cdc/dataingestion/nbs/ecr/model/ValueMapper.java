package gov.cdc.dataingestion.nbs.ecr.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ValueMapper {
    private String colName;
    private String value;
}
