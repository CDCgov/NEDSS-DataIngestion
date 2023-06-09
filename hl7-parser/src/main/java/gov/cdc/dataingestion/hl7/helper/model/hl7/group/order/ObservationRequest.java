package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import lombok.Getter;
import lombok.Setter;

import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ObservationRequest {
    String setIdObr;
    Ei placerOrderNumber = new Ei();
    Ei fillerOrderNumber = new Ei();
    Ce universalServiceIdentifier = new Ce();
    String priorityObr;
    Ts requestedDateTime = new Ts();
    Ts observationDateTime = new Ts();
    Ts observationEndDateTime = new Ts();
    Cq collectionVolume = new Cq();
    List<Xcn> collectorIdentifier = new ArrayList<>();
    String specimenActionCode;
    Ce dangerCode = new Ce();
    String relevantClinicalInformation;
    Ts specimenReceivedDateTime = new Ts();
    Sps specimenSource = new Sps();
    List<Xcn> orderingProvider = new ArrayList<>();
    List<Xtn> orderCallbackPhoneNumber = new ArrayList<>();
    String placerField1;
    String placerField2;
    String fillerField1;
    String fillerField2;
    Ts resultRptStatusChngDateTime = new Ts();
    Moc chargeToPractice = new Moc();
    String diagnosticServSectId;
    String resultStatus;
    Prl parentResult = new Prl();
    List<Tq> quantityTiming = new ArrayList<>();
    List<Xcn> resultCopiesTo = new ArrayList<>();
    Eip parent = new Eip();
    String transportationMode;
    List<Ce> reasonForStudy = new ArrayList<>();
    Ndl principalResultInterpreter = new Ndl();
    List<Ndl> assistantResultInterpreter = new ArrayList<>();
    List<Ndl> technician  = new ArrayList<>();
    List<Ndl> transcriptionist = new ArrayList<>();
    Ts scheduledDateTime = new Ts();
    String numberOfSampleContainers;
    List<Ce> transportLogisticsOfCollectedSample = new ArrayList<>();
    List<Ce> collectorComment = new ArrayList<>();
    Ce transportArrangementResponsibility = new Ce();
    String transportArranged;
    String escortRequired;
    List<Ce> plannedPatientTransportComment = new ArrayList<>();
    Ce procedureCode = new Ce();
    List<Ce> procedureCodeModifier = new ArrayList<>();
    List<Ce> placerSupplementalServiceInformation = new ArrayList<>();
    List<Ce> fillerSupplementalServiceInformation = new ArrayList<>();
    Cwe medicallyNecessaryDuplicateProcedureReason = new Cwe();
    String resultHandling;
    Cwe parentUniversalServiceIdentifier = new Cwe();

    public ObservationRequest() {

    }

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
