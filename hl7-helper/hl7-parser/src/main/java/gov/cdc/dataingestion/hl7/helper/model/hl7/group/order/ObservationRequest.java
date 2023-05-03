package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import lombok.Setter;

import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

import java.util.List;

@Getter
@Setter
public class ObservationRequest {
    String setIdObr;
    Ei placerOrderNumber;
    Ei fillerOrderNumber;
    Ce universalServiceIdentifier;
    String priorityObr;
    Ts requestedDateTime;
    Ts observationDateTime;
    Ts observationEndDateTime;
    Cq collectionVolume;
    List<Xcn> collectorIdentifier;
    String specimenActionCode;
    Ce dangerCode;
    String relevantClinicalInformation;
    Ts specimenReceivedDateTime;
    Sps specimenSource;
    List<Xcn> orderingProvider;
    List<Xtn> orderCallbackPhoneNumber;
    String placerField1;
    String placerField2;
    String fillerField1;
    String fillerField2;
    Ts resultRptStatusChngDateTime;
    Moc chargeToPractice;
    String diagnosticServSectId;
    String resultStatus;
    Prl parentResult;
    List<Tq> quantityTiming;
    List<Xcn> resultCopiesTo;
    Eip parent;
    String transportationMode;
    List<Ce> reasonForStudy;
    Ndl principalResultInterpreter;
    List<Ndl> assistantResultInterpreter;
    List<Ndl> technician;
    List<Ndl> transcriptionist;
    Ts scheduledDateTime;
    String numberOfSampleContainers;
    List<Ce> transportLogisticsOfCollectedSample;
    List<Ce> collectorComment;
    Ce transportArrangementResponsibility;
    String transportArranged;
    String escortRequired;
    List<Ce> plannedPatientTransportComment;
    Ce procedureCode;
    List<Ce> procedureCodeModifier;
    List<Ce> placerSupplementalServiceInformation;
    List<Ce> fillerSupplementalServiceInformation;
    Cwe medicallyNecessaryDuplicateProcedureReason;
    String resultHandling;
    Cwe parentUniversalServiceIdentifier;

    public ObservationRequest(ca.uhn.hl7v2.model.v251.segment.OBR obr) {
        this.setIdObr = obr.getSetIDOBR().getValue();
        this.placerOrderNumber = new Ei(obr.getPlacerOrderNumber());
        this.fillerOrderNumber = new Ei(obr.getFillerOrderNumber());
        this.universalServiceIdentifier = new Ce(obr.getUniversalServiceIdentifier());
        this.priorityObr = obr.getPriorityOBR().getValue();
        this.requestedDateTime = new Ts(obr.getRequestedDateTime());
        this.observationDateTime = new Ts(obr.getObservationDateTime());
        this.observationEndDateTime = new Ts(obr.getObservationEndDateTime());
        this.collectionVolume = new Cq(obr.getCollectionVolume());
        this.collectorIdentifier = GetXcnList(obr.getCollectorIdentifier());
        this.specimenActionCode = obr.getSpecimenActionCode().getValue();
        this.dangerCode = new Ce(obr.getDangerCode());
        this.relevantClinicalInformation = obr.getRelevantClinicalInformation().getValue();
        this.specimenReceivedDateTime = new Ts(obr.getSpecimenReceivedDateTime());
        this.specimenSource = new Sps(obr.getSpecimenSource());
        this.orderingProvider = GetXcnList(obr.getOrderingProvider());
        this.orderCallbackPhoneNumber = GetXtnList(obr.getOrderCallbackPhoneNumber());
        this.placerField1 = obr.getPlacerField1().getValue();
        this.placerField2 = obr.getPlacerField2().getValue();
        this.fillerField1 = obr.getFillerField1().getValue();
        this.fillerField2 = obr.getFillerField2().getValue();
        this.resultRptStatusChngDateTime = new Ts(obr.getResultsRptStatusChngDateTime());
        this.chargeToPractice = new Moc(obr.getChargeToPractice());
        this.diagnosticServSectId = obr.getDiagnosticServSectID().getValue();
        this.resultStatus = obr.getResultStatus().getValue();
        this.parentResult = new Prl(obr.getParentResult());
        this.quantityTiming = GetTqList(obr.getQuantityTiming());
        this.resultCopiesTo = GetXcnList(obr.getResultCopiesTo());
        this.parent = new Eip(obr.getOBRParent());
        this.transportationMode = obr.getTransportationMode().getValue();
        this.reasonForStudy = GetCeList(obr.getReasonForStudy());
        this.principalResultInterpreter = new Ndl(obr.getPrincipalResultInterpreter());
        this.assistantResultInterpreter = GetNdlList(obr.getAssistantResultInterpreter());
        this.technician = GetNdlList(obr.getTechnician());
        this.transcriptionist = GetNdlList(obr.getTranscriptionist());
        this.scheduledDateTime = new Ts(obr.getScheduledDateTime());
        this.numberOfSampleContainers = obr.getNumberOfSampleContainers().getValue();
        this.transportLogisticsOfCollectedSample = GetCeList(obr.getTransportLogisticsOfCollectedSample());
        this.collectorComment = GetCeList(obr.getCollectorSComment());
        this.transportArrangementResponsibility = new Ce(obr.getTransportArrangementResponsibility());
        this.transportArranged = obr.getTransportArranged().getValue();
        this.escortRequired = obr.getEscortRequired().getValue();
        this.plannedPatientTransportComment = GetCeList(obr.getPlannedPatientTransportComment());
        this.procedureCode = new Ce(obr.getProcedureCode());
        this.procedureCodeModifier = GetCeList(obr.getProcedureCodeModifier());
        this.placerSupplementalServiceInformation = GetCeList(obr.getPlacerSupplementalServiceInformation());
        this.fillerSupplementalServiceInformation = GetCeList(obr.getFillerSupplementalServiceInformation());
        this.medicallyNecessaryDuplicateProcedureReason = new Cwe(obr.getMedicallyNecessaryDuplicateProcedureReason());
        this.resultHandling = obr.getResultHandling().getValue();
        this.parentUniversalServiceIdentifier = new Cwe(obr.getParentUniversalServiceIdentifier());
    }
}
