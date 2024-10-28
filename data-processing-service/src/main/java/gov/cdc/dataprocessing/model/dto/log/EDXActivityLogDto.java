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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
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
    private Map<Object,Object> newaddedCodeSets = new HashMap<Object,Object>();
    private boolean logDetailAllStatus = false;
    private String algorithmAction;
    private String actionId;
    private String messageId;
    private String entityNm;
    private String accessionNbr;
    private String algorithmName;
}
