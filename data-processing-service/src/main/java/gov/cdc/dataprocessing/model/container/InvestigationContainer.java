package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.LdfBaseVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.NotificationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class InvestigationContainer extends LdfBaseVO {
    private static final long serialVersionUID = 1L;
    public PublicHealthCaseVO thePublicHealthCaseVO;

    /**
     * DTs
     */
    public Collection<Object> theParticipationDTCollection;
    public Collection<Object> theRoleDTCollection;
    public Collection<Object> theActRelationshipDTCollection;

    /**
     * VOs
     */
    public Collection<Object> thePersonVOCollection;
    public Collection<Object> theOrganizationVOCollection;
    public Collection<Object> theMaterialVOCollection;
    public Collection<ObservationContainer> theObservationVOCollection;
    public Collection<Object> theInterventionVOCollection;
    public Collection<Object> theEntityGroupVOCollection;
    public Collection<Object> theNonPersonLivingSubjectVOCollection;
    public Collection<Object> thePlaceVOCollection;
    public Collection<Object> theNotificationVOCollection;
    public Collection<Object> theReferralVOCollection;
    public Collection<Object> thePatientEncounterVOCollection;
    public Collection<Object> theClinicalDocumentVOCollection;
    public Collection<Object> theObservationSummaryVOCollection;//Replaced by theLabReportSummaryVOCollection, theMorbReportSummaryVOCollection
    public Collection<Object> theVaccinationSummaryVOCollection;
    public Collection<Object> theNotificationSummaryVOCollection;
    public Collection<Object> theTreatmentSummaryVOCollection;
    public Collection<Object> theLabReportSummaryVOCollection;
    public Collection<Object> theMorbReportSummaryVOCollection;
    public NotificationVO theNotificationVO;
    private boolean associatedNotificationsInd;
    private String businessObjectName;
    private boolean isOOSystemInd;
    private boolean isOOSystemPendInd;
    private Collection<Object>  theContactVOColl;
    private Collection<Object> theCTContactSummaryDTCollection;
}
