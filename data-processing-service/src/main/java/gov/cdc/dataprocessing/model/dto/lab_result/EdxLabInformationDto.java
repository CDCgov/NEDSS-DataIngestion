package gov.cdc.dataprocessing.model.dto.lab_result;

import gov.cdc.dataprocessing.constant.enums.NbsInterfaceStatus;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import gov.cdc.dataprocessing.model.dto.edx.EdxLabIdentiferDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.service.model.WdsReport;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

@Getter
@Setter
public class EdxLabInformationDto extends EdxRuleAlgorothmManagerDto implements Serializable {
    /**
     * Pradeep Kumar Sharma
     * Utility class to store the information that is being shared all across the ELR processing
     */
    private static final long serialVersionUID = 1L;
    private Timestamp addTime;
    private Timestamp OrderEffectiveDate;
    private String role;
    private long rootObserbationUid;
    private PersonContainer orderingProviderVO;
    private String sendingFacilityClia;
    private String sendingFacilityName;
    private long patientUid;
    private long userId;
    private int nextUid;
    private String fillerNumber;
    private String messageControlID;
    private long parentObservationUid;
    private boolean isOrderingProvider;
    private LabResultProxyContainer labResultProxyContainer;
    private String localId;
    private boolean isParentObsInd;
    private Collection<EdxLabIdentiferDto> edxLabIdentiferDTColl;
    private String entityName;
    private String reportingSourceName;
    private String userName;
    private String universalIdType;

    private Long associatedPublicHealthCaseUid;
    private long publicHealthCaseUid;
    private long notificationUid;
    private long originalAssociatedPHCUid;
    private long nbsInterfaceUid;

    private Timestamp specimenCollectionTime;

    private String jurisdictionName;
    private String programAreaName;
    private boolean jurisdictionAndProgramAreaSuccessfullyDerived;

    private boolean algorithmHasInvestigation;
    private boolean investigationSuccessfullyCreated;
    private boolean investigationMissingFields;

    private boolean algorithmHasNotification;
    private boolean notificationSuccessfullyCreated;
    private boolean notificationMissingFields;

    private boolean labIsCreate;
    private boolean labIsCreateSuccess;
    private boolean labIsUpdateDRRQ;
    private boolean labIsUpdateDRSA;

    private boolean labIsUpdateSuccess;
    private boolean labIsMarkedAsReviewed;
    private Map<Object,Object> resultedTest;
    private String conditionCode;
    private Object proxyVO;
    private Map<Object, Object> edxSusLabDTMap = new HashMap<Object, Object>();
    private String addReasonCd;
    private ObservationContainer rootObservationContainer;
    //Informational Variables
    private boolean multipleSubjectMatch;
    private boolean multipleOrderingProvider;
    private boolean multipleCollector;
    private boolean multiplePrincipalInterpreter;
    private boolean multipleOrderingFacility;
    private boolean multipleSpecimen;
    private boolean ethnicityCodeTranslated;
    private boolean obsMethodTranslated;
    private boolean raceTranslated;
    private boolean sexTranslated;
    private boolean ssnInvalid;
    private boolean nullClia;
    //Error Variables
    private boolean fillerNumberPresent;
    private boolean finalPostCorrected;
    private boolean preliminaryPostFinal;

    private boolean preliminaryPostCorrected;
    private boolean activityTimeOutOfSequence;
    private boolean multiplePerformingLab;
    private boolean orderTestNameMissing;
    private boolean reflexOrderedTestCdMissing;
    private boolean reflexResultedTestCdMissing;
    private boolean resultedTestNameMissing;
    private boolean drugNameMissing;
    private boolean obsStatusTranslated;
    private  String dangerCode;
    private  String relationship;
    private  String relationshipDesc;
    private boolean activityToTimeMissing;
    private boolean systemException;
    private boolean universalServiceIdMissing;
    private boolean missingOrderingProvider;
    private boolean missingOrderingFacility;
    private boolean multipleReceivingFacility;
    private long personParentUid;
    private boolean patientMatch;
    private boolean multipleOBR;
    private boolean multipleSubject;
    private boolean noSubject;
    private boolean orderOBRWithParent;
    private boolean childOBRWithoutParent;
    private boolean invalidXML;
    private boolean missingOrderingProviderandFacility;
    private boolean createLabPermission;
    private boolean updateLabPermission;
    private boolean markAsReviewPermission;
    private boolean createInvestigationPermission;
    private boolean createNotificationPermission;
    private boolean matchingAlgorithm;
    private boolean unexpectedResultType;
    private boolean childSuscWithoutParentResult;
    private boolean fieldTruncationError;
    private boolean invalidDateError;
    private String algorithmAndOrLogic;
    private boolean labAssociatedToInv;

    private boolean reasonforStudyCdMissing;
    private Collection<PublicHealthCaseDT> matchingPublicHealthCaseDTColl;
    private String investigationType;
    private NbsInterfaceStatus status;

    private List<WdsReport> wdsReports = new ArrayList<>();

    public int getNextUid() {
        nextUid--;
        return nextUid;
    }

}
