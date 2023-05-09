package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.observation;

import gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType.*;
import static gov.cdc.dataingestion.hl7.helper.helper.modelListHelper.*;
import lombok.Getter;

import java.util.List;

@Getter
public class ObservationResult {
    String setIdObx;
    String valueType;
    Ce observationIdentifier;
    String observationSubId;
    List<String> observationValue;
    Ce units;
    String referencesRange;
    List<String> abnormalFlag;
    String probability;
    List<String> natureOfAbnormalTest;
    String observationResultStatus;
    Ts effectiveDateOfReferenceRange;
    String userDefinedAccessChecks;
    Ts DateTimeOfTheObservation;
    Ce producerId;
    List<Xcn> responsibleObserver;
    List<Ce> observationMethod;
    List<Ei> equipmentInstanceIdentifier;
    Ts dateTimeOfTheAnalysis;
    String reservedForHarmonizationWithV261;
    String reservedForHarmonizationWithV262;
    String reservedForHarmonizationWithV263;
    Xon performingOrganizationName;
    Xad performingOrganizationAddress;
    Xcn performingOrganizationMedicalDirector;


    public ObservationResult(ca.uhn.hl7v2.model.v251.segment.OBX obx) {
        this.setIdObx = obx.getSetIDOBX().getValue();
        this.valueType = obx.getValueType().getValue();
        this.observationIdentifier = new Ce(obx.getObservationIdentifier());
        this.observationSubId = obx.getObservationSubID().getValue();
        this.observationValue = GetVariesStringList(obx.getObservationValue());
        this.units = new Ce(obx.getUnits());
        this.referencesRange = obx.getReferencesRange().getValue();
        this.abnormalFlag = GetIsStringList(obx.getAbnormalFlags());
        this.probability = obx.getProbability().getValue();
        this.natureOfAbnormalTest = GetIdStringList(obx.getNatureOfAbnormalTest());
        this.observationResultStatus = obx.getObservationResultStatus().getValue();
        this.effectiveDateOfReferenceRange = new Ts(obx.getEffectiveDateOfReferenceRangeValues());
        this.userDefinedAccessChecks = obx.getUserDefinedAccessChecks().getValue();
        this.DateTimeOfTheObservation = new Ts(obx.getDateTimeOfTheObservation());
        this.producerId = new Ce(obx.getProducerSReference());
        this.responsibleObserver = GetXcnList(obx.getResponsibleObserver());
        this.observationMethod = GetCeList(obx.getObservationMethod());
        this.equipmentInstanceIdentifier = GetEiList(obx.getEquipmentInstanceIdentifier());
        this.dateTimeOfTheAnalysis = new Ts(obx.getDateTimeOfTheAnalysis());
        this.reservedForHarmonizationWithV261 = obx.getReservedForHarmonizationWithV26().toString();
        this.reservedForHarmonizationWithV262 = obx.getReservedForHarmonizationWithV26Number2().toString();
        this.reservedForHarmonizationWithV263 = obx.getReservedForHarmonizationWithV26Number3().toString();
        this.performingOrganizationName = new Xon(obx.getPerformingOrganizationName());
        this.performingOrganizationAddress = new Xad(obx.getPerformingOrganizationAddress());
        this.performingOrganizationMedicalDirector = new Xcn(obx.getPerformingOrganizationMedicalDirector());
    }
}

