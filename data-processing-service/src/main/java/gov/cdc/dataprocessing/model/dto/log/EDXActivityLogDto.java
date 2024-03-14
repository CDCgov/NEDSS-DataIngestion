package gov.cdc.dataprocessing.model.dto.log;

import gov.cdc.dataprocessing.model.container.BaseContainer;
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
public class EDXActivityLogDto extends BaseContainer implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long edxActivityLogUid;
    private Long sourceUid;
    private Long targetUid;
    private String docType;
    private String recordStatusCd;
    private String recordStatusCdHtml;
    private Timestamp recordStatusTime;
    private String exception;
    private String impExpIndCd;
    private String impExpIndCdDesc;
    private String sourceTypeCd;
    private String targetTypeCd;
    private String businessObjLocalId;
    private String docName;
    private String srcName;
    private String viewLink;
    private String exceptionShort;
    private Collection<Object> EDXActivityLogDTWithVocabDetails;
    private Collection<Object> EDXActivityLogDTWithQuesDetails;
    private Collection<Object> EDXActivityLogDTDetails = new ArrayList();;
    private Map<Object,Object> newaddedCodeSets = new HashMap<Object,Object>();
    private boolean logDetailAllStatus = false;
    private String algorithmAction;
    private String actionId;
    private String messageId;
    private String entityNm;
    private String accessionNbr;



    private String algorithmName;
}
