package gov.cdc.dataprocessing.model.container.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class InvestigationContainer extends LdfBaseContainer {
    private static final long serialVersionUID = 1L;
    public PublicHealthCaseContainer thePublicHealthCaseContainer;

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
    public NotificationContainer theNotificationContainer;
    private boolean associatedNotificationsInd;
    private String businessObjectName;
    private boolean isOOSystemInd;
    private boolean isOOSystemPendInd;
    private Collection<Object>  theContactVOColl;
    private Collection<Object> theCTContactSummaryDTCollection;

    public Collection<Object> theDocumentSummaryVOCollection;
}
