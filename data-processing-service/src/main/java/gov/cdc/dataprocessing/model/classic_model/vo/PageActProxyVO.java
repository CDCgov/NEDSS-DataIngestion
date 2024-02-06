package gov.cdc.dataprocessing.model.classic_model.vo;

import gov.cdc.dataprocessing.model.classic_model.dto.ExportReceivingFacilityDT;
import gov.cdc.dataprocessing.model.classic_model.dto.ParticipationDT;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class PageActProxyVO  extends AbstractVO {
    private static final long         serialVersionUID = 1L;

    public String                     pageProxyTypeCd  = "";
    // page business type INV, IXS, etc.
    private PublicHealthCaseVO        publicHealthCaseVO;
    private InterviewVO               interviewVO;
    private NotificationVO            theNotificationVO;
    private InterventionVO			  interventionVO;

    private Long                      patientUid;
    private String                    currentInvestigator;
    private String                    fieldSupervisor;
    private String                    caseSupervisor;
    private boolean                   isSTDProgramArea = false;
    private Collection<Object>        thePersonVOCollection;

    private PamVO                     pageVO;
    // contains answer maps

    private Collection<Object>        theVaccinationSummaryVOCollection;
    private Collection<Object>        theNotificationSummaryVOCollection;
    private Collection<Object>        theTreatmentSummaryVOCollection;
    private Collection<Object>        theLabReportSummaryVOCollection;
    private Collection<Object>        theMorbReportSummaryVOCollection;
    private Collection<ParticipationDT>        theParticipationDTCollection;

    private Collection<Object>        theActRelationshipDTCollection;
    private Collection<Object>        theInvestigationAuditLogSummaryVOCollection;
    private Collection<OrganizationVO>        theOrganizationVOCollection;
    private Collection<Object>        theCTContactSummaryDTCollection;
    private Collection<Object>        theInterviewSummaryDTCollection;
    private Collection<Object>        theNotificationVOCollection;
    private Collection<Object>        theCSSummaryVOCollection;
    private Collection<Object>        nbsAttachmentDTColl;
    private Collection<Object>        nbsNoteDTColl;
    private Collection<Object>        theDocumentSummaryVOCollection;
    private boolean                   isOOSystemInd;
    private boolean                   isOOSystemPendInd;
    private boolean                   associatedNotificationsInd;
    private boolean                   isUnsavedNote;
    private boolean                   isMergeCase;


    private Collection<Object> theEDXDocumentDTCollection;


    private boolean                   isRenterant;
    private boolean					  isConversionHasModified;

    private ExportReceivingFacilityDT exportReceivingFacilityDT;
}
