package gov.cdc.dataprocessing.model.container.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public class ProgramAreaContainer implements Serializable, Comparable
{

    private String conditionCd;
    private String conditionShortNm;
    private String stateProgAreaCode;
    private String stateProgAreaCdDesc;
    private String investigationFormCd;

    @Override
    public int compareTo(Object o) {
        return getConditionShortNm().compareTo( ((ProgramAreaContainer) o).getConditionShortNm() );
    }
}
