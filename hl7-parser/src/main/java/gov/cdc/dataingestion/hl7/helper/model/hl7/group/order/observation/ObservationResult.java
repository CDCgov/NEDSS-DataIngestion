package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ObservationResult {
    String setIdObx;
    String valueType;
    Ce observationIdentifier = new Ce();
    String observationSubId;
    List<String> observationValue = new ArrayList<>();
    Ce units = new Ce();
    String referencesRange;
    List<String> abnormalFlag = new ArrayList<>();
    String probability;
    List<String> natureOfAbnormalTest = new ArrayList<>();
    String observationResultStatus;
    Ts effectiveDateOfReferenceRange = new Ts();
    String userDefinedAccessChecks;
    Ts dateTimeOfTheObservation = new Ts();
    Ce producerId = new Ce();
    List<Xcn> responsibleObserver = new ArrayList<>();
    List<Ce> observationMethod = new ArrayList<>();
    List<Ei> equipmentInstanceIdentifier = new ArrayList<>();
    Ts dateTimeOfTheAnalysis = new Ts();
    String reservedForHarmonizationWithV261;
    String reservedForHarmonizationWithV262;
    String reservedForHarmonizationWithV263;
    Xon performingOrganizationName = new Xon();
    Xad performingOrganizationAddress = new Xad();
    Xcn performingOrganizationMedicalDirector = new Xcn();

    public ObservationResult() {

    }

    public ObservationResult(ca.uhn.hl7v2.model.v251.segment.OBX obx) {
        this.setIdObx = obx.getSetIDOBX().getValue();
        this.valueType = obx.getValueType().getValue();
        this.observationIdentifier = new Ce(obx.getObservationIdentifier());
        this.observationSubId = obx.getObservationSubID().getValue();
        this.observationValue = getVariesStringList(obx.getObservationValue());
        this.units = new Ce(obx.getUnits());
        this.referencesRange = obx.getReferencesRange().getValue();
        this.abnormalFlag = getIsStringList(obx.getAbnormalFlags());
        this.probability = obx.getProbability().getValue();
        this.natureOfAbnormalTest = getIdStringList(obx.getNatureOfAbnormalTest());
        this.observationResultStatus = obx.getObservationResultStatus().getValue();
        this.effectiveDateOfReferenceRange = new Ts(obx.getEffectiveDateOfReferenceRangeValues());
        this.userDefinedAccessChecks = obx.getUserDefinedAccessChecks().getValue();
        this.dateTimeOfTheObservation = new Ts(obx.getDateTimeOfTheObservation());
        this.producerId = new Ce(obx.getProducerSReference());
        this.responsibleObserver = getXcnList(obx.getResponsibleObserver());
        this.observationMethod = getCeList(obx.getObservationMethod());
        this.equipmentInstanceIdentifier = getEiList(obx.getEquipmentInstanceIdentifier());
        this.dateTimeOfTheAnalysis = new Ts(obx.getDateTimeOfTheAnalysis());
        this.reservedForHarmonizationWithV261 = obx.getReservedForHarmonizationWithV26().toString();
        this.reservedForHarmonizationWithV262 = obx.getReservedForHarmonizationWithV26Number2().toString();
        this.reservedForHarmonizationWithV263 = obx.getReservedForHarmonizationWithV26Number3().toString();
        this.performingOrganizationName = new Xon(obx.getPerformingOrganizationName());
        this.performingOrganizationAddress = new Xad(obx.getPerformingOrganizationAddress());
        this.performingOrganizationMedicalDirector = new Xcn(obx.getPerformingOrganizationMedicalDirector());
    }
}

