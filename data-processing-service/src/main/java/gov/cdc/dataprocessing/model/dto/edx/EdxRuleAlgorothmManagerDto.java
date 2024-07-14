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
@SuppressWarnings("all")
public class EdxRuleAlgorothmManagerDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String updateAction;
    private String nndComment;
    private String onFailureToCreateNND;
    private String dsmAlgorithmName;
    private String conditionName;

    private Map<Object, Object> edxRuleApplyDTMap;
    private Map<Object, Object> edxRuleAdvCriteriaDTMap;
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
    private Timestamp lastChgTime;

    ;
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
    public enum STATUS_VAL {Success, Failure}

}
