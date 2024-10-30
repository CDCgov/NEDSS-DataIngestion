package gov.cdc.dataprocessing.model.dto.edx;

import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
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
public class EdxRuleAlgorothmManagerDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String updateAction;
    private String nndComment;
    private String onFailureToCreateNND;
    private String dsmAlgorithmName;
    private String conditionName;

    private Map<Object,Object> edxRuleApplyDTMap;
    private Map<Object,Object> edxRuleAdvCriteriaDTMap;
    private Long dsmAlgorithmUid;
    private String onFailureToCreateInv;
    private String action;

    // These two were named object before
    private PageActProxyContainer pageActContainer;
    private PamProxyContainer pamContainer;


    private Collection<Object> edxActivityDetailLogDTCollection;
    private String errorText;
    private Collection<Object> sendingFacilityColl;
    private Map<Object, Object> edxBasicCriteriaMap;
    public  enum STATUS_VAL {Success, Failure};
    private Timestamp lastChgTime;
    private Long PHCUid;
    private Long PHCRevisionUid;
    private NBSDocumentDto documentDT;
    private Long MPRUid;
    private boolean isContactRecordDoc;
    private Map<String, EDXEventProcessCaseSummaryDto> eDXEventProcessCaseSummaryDTMap = new HashMap<String, EDXEventProcessCaseSummaryDto>();
    private boolean isUpdatedDocument;
    private boolean isLabReportDoc;
    private boolean isMorbReportDoc;
    private boolean isCaseUpdated;
    private EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();

}
