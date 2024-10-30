package gov.cdc.dataprocessing.model.dto.log;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class EDXActivityLogDto extends BaseContainer implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long edxActivityLogUid;
    private Long sourceUid;
    private Long targetUid;
    private String docType;
    private String recordStatusCd;
    private String recordStatusCdHtml;
    private Timestamp recordStatusTime;
    private String exceptionTxt;
    private String impExpIndCd;
    private String impExpIndCdDesc;
    private String sourceTypeCd;
    private String targetTypeCd;
    private String businessObjLocalId;
    private String docName;
    private String srcName;
    private String viewLink;
    private String exceptionShort;
    private Collection<EDXActivityDetailLogDto> EDXActivityLogDTWithVocabDetails;
    private Collection<Object> EDXActivityLogDTWithQuesDetails;
    private Collection<EDXActivityDetailLogDto> EDXActivityLogDTDetails = new ArrayList();
    private Map<Object,Object> newaddedCodeSets = new HashMap<>();
    private boolean logDetailAllStatus = false;
    private String algorithmAction;
    private String actionId;
    private String messageId;
    private String entityNm;
    private String accessionNbr;
    private String algorithmName;
}
