package gov.cdc.dataprocessing.model.dto.edx;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class EdxRuleManageDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private String value;
    private Collection<Object> defaultCodedValueColl;
    private String defaultNumericValue;
    private String defaultStringValue;
    private String behavior;
    private Long dsmAlgorithmUid;
    private String defaultCommentValue;
    private String logic;
    private String questionId;
    private boolean isAdvanceCriteria;
    private String type;
    private String participationTypeCode;
    private String participationClassCode;
    private Long participationUid;
}
