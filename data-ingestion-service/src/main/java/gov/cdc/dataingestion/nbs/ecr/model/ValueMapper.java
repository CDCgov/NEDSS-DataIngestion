package gov.cdc.dataingestion.nbs.ecr.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/// TO USE LATER FOR REFACTOR
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class ValueMapper {
    private String colName;
    private String value;
}
