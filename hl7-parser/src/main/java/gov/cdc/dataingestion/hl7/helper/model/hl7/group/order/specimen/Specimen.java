package gov.cdc.dataingestion.hl7.helper.model.hl7.group.order.specimen;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.*;
import static gov.cdc.dataingestion.hl7.helper.helper.ModelListHelper.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Specimen {
    String setIdSpm;
    Eip specimenId = new Eip();
    List<Eip> specimenParentId = new ArrayList<>();
    Cwe  specimenType = new Cwe();
    List<Cwe> specimenTypeModifier = new ArrayList<>();
    List<Cwe> specimenAdditives= new ArrayList<>();
    Cwe specimenCollectionMethod = new Cwe();
    Cwe specimenSourceSite = new Cwe();
    List<Cwe> specimenSourceSiteModifier = new ArrayList<>();
    Cwe specimenCollectionSite = new Cwe();
    List<Cwe> specimenRole = new ArrayList<>();
    Cq specimenCollectionAmount = new Cq();
    String groupedSpecimenCount;
    List<String> specimenDescription= new ArrayList<>();
    List<Cwe> specimenHandlingCode= new ArrayList<>();
    List<Cwe> specimenRiskCode= new ArrayList<>();
    Dr specimenCollectionDateTime = new Dr();
    Ts specimenReceivedDateTime = new Ts();
    Ts specimenExpirationDateTime = new Ts();
    String specimenAvailability;
    List<Cwe> specimenRejectReason = new ArrayList<>();
    Cwe specimenQuality = new Cwe();
    Cwe specimenAppropriateness = new Cwe();
    List<Cwe> specimenCondition = new ArrayList<>();
    Cq specimenCurrentQuantity = new Cq();
    String numberOfSpecimenContainers;
    Cwe containerType = new Cwe();
    Cwe containerCondition = new Cwe();
    Cwe specimenChildRole = new Cwe();

    public Specimen(ca.uhn.hl7v2.model.v251.segment.SPM spm) {
        this.setIdSpm = spm.getSetIDSPM().getValue();
        this.specimenId = new Eip(spm.getSpecimenID());
        this.specimenParentId = getEipList(spm.getSpecimenParentIDs());
        this.specimenType = new Cwe(spm.getSpecimenType());
        this.specimenTypeModifier = getCweList(spm.getSpecimenTypeModifier());
        this.specimenAdditives = getCweList(spm.getSpecimenAdditives());
        this.specimenCollectionMethod = new Cwe(spm.getSpecimenCollectionMethod());
        this.specimenSourceSite = new Cwe(spm.getSpecimenSourceSite());
        this.specimenSourceSiteModifier = getCweList(spm.getSpecimenSourceSiteModifier());
        this.specimenCollectionSite = new Cwe(spm.getSpecimenCollectionSite());
        this.specimenRole = getCweList(spm.getSpecimenRole());
        this.specimenCollectionAmount = new Cq(spm.getSpecimenCollectionAmount());
        this.groupedSpecimenCount = spm.getGroupedSpecimenCount().getValue();
        this.specimenDescription = getStStringList(spm.getSpecimenDescription());
        this.specimenHandlingCode = getCweList(spm.getSpecimenHandlingCode());
        this.specimenRiskCode = getCweList(spm.getSpecimenRiskCode());
        this.specimenCollectionDateTime = new Dr(spm.getSpecimenCollectionDateTime());
        this.specimenReceivedDateTime = new Ts(spm.getSpecimenReceivedDateTime());
        this.specimenExpirationDateTime = new Ts(spm.getSpecimenExpirationDateTime());
        this.specimenAvailability = spm.getSpecimenAvailability().getValue();
        this.specimenRejectReason = getCweList(spm.getSpecimenRejectReason());
        this.specimenQuality = new Cwe(spm.getSpecimenQuality());
        this.specimenAppropriateness = new Cwe(spm.getSpecimenAppropriateness());
        this.specimenCondition = getCweList(spm.getSpecimenCondition());
        this.specimenCurrentQuantity = new Cq(spm.getSpecimenCurrentQuantity());
        this.numberOfSpecimenContainers = spm.getNumberOfSpecimenContainers().getValue();
        this.containerType = new Cwe(spm.getContainerType());
        this.containerCondition = new Cwe(spm.getContainerCondition());
        this.specimenChildRole = new Cwe(spm.getSpecimenChildRole());
    }

    public Specimen() {

    }
}
