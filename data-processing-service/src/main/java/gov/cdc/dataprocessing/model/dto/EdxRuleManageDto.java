package gov.cdc.dataprocessing.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
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
