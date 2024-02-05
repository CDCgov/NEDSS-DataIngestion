package gov.cdc.dataprocessing.model.classic_model.dt;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class EdxRuleAlgorothmManagerDT implements Serializable {
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
    private Object object;
    private Collection<Object> edxActivityDetailLogDTCollection;
    private String errorText;
    private Collection<Object> sendingFacilityColl;
    private Map<Object, Object> edxBasicCriteriaMap;
    public  enum STATUS_VAL {Success, Failure};
    private Timestamp lastChgTime;
    private Long PHCUid;
    private Long PHCRevisionUid;
    private NBSDocumentDT documentDT;
    private Long MPRUid;
    private boolean isContactRecordDoc;
    private Map<String, EDXEventProcessCaseSummaryDT> eDXEventProcessCaseSummaryDTMap = new HashMap<String, EDXEventProcessCaseSummaryDT>();
    private boolean isUpdatedDocument;
    private boolean isLabReportDoc;
    private boolean isMorbReportDoc;
    private boolean isCaseUpdated;

}
