package gov.cdc.nbsDedup.model;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.model.container.base.BasePamContainer;
import gov.cdc.nbsDedup.model.container.interfaces.InterviewContainer;
import gov.cdc.nbsDedup.model.container.model.PersonContainer;
import gov.cdc.nbsDedup.model.dto.act.ActRelationshipDto;
import gov.cdc.nbsDedup.model.dto.log.MessageLogDto;
import gov.cdc.nbsDedup.model.dto.nbs.NbsNoteDto;
import gov.cdc.nbsDedup.model.dto.participation.ParticipationDto;
import gov.cdc.nbsDedup.model.dto.phc.ExportReceivingFacilityDto;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PageActProxyContainer extends BaseContainer {
  private static final long serialVersionUID = 1L;

  public String pageProxyTypeCd = "";
  // page business type INV, IXS, etc.
  private PublicHealthCaseContainer publicHealthCaseContainer;
  private InterviewContainer interviewContainer;
  private NotificationContainer theNotificationContainer;
  private InterventionContainer interventionContainer;

  private Long patientUid;
  private String currentInvestigator;
  private String fieldSupervisor;
  private String caseSupervisor;
  private boolean isSTDProgramArea = false;
  private Collection<PersonContainer> thePersonContainerCollection;

  private BasePamContainer pageVO;
  // contains answer maps

  private Collection<Object> theVaccinationSummaryVOCollection;
  private Collection<Object> theNotificationSummaryVOCollection;
  private Collection<Object> theTreatmentSummaryVOCollection;
  private Collection<Object> theLabReportSummaryVOCollection;
  private Collection<Object> theMorbReportSummaryVOCollection;
  protected Collection<ParticipationDto> theParticipationDtoCollection;

  private Collection<ActRelationshipDto> theActRelationshipDtoCollection;
  private Collection<Object> theInvestigationAuditLogSummaryVOCollection;
  protected Collection<OrganizationContainer> theOrganizationContainerCollection;
  private Collection<Object> theCTContactSummaryDTCollection;
  private Collection<Object> theInterviewSummaryDTCollection;
  private Collection<Object> theNotificationVOCollection;
  private Collection<Object> theCSSummaryVOCollection;
  private Collection<Object> nbsAttachmentDTColl;
  private Collection<NbsNoteDto> nbsNoteDTColl;
  private Collection<Object> theDocumentSummaryVOCollection;
  private boolean isOOSystemInd;
  private boolean isOOSystemPendInd;
  private boolean associatedNotificationsInd;
  private boolean isUnsavedNote;
  private boolean isMergeCase;
  private Collection<Object> theEDXDocumentDTCollection;
  private boolean isRenterant;
  private boolean isConversionHasModified;
  private ExportReceivingFacilityDto exportReceivingFacilityDto;
  private Map<String, MessageLogDto> messageLogDTMap = new HashMap<String, MessageLogDto>();
}
