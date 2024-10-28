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
 * */
@SuppressWarnings({"java:S1118",""})
public class ValueMapper {
    private String colName;
    private String value;
}
