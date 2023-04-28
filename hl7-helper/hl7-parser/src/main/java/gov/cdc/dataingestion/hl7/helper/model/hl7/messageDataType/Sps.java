package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.SPS;
import lombok.Getter;

@Getter
public class Sps {
    Cwe specimenSourceNameOrCode;
    Cwe additives;
    String specimenCollectionMethod;
    Cwe bodySite;
    Cwe siteModifier;
    Cwe collectionMethodModifierCode;
    Cwe specimenRole;
    public Sps(SPS sps) {
        this.specimenSourceNameOrCode = new Cwe(sps.getSpecimenSourceNameOrCode());
        this.additives = new Cwe(sps.getAdditives());
        this.specimenCollectionMethod = sps.getSpecimenCollectionMethod().getValue();
        this.bodySite = new Cwe(sps.getBodySite());
        this.siteModifier = new Cwe(sps.getSiteModifier());
        this.collectionMethodModifierCode = new Cwe(sps.getCollectionMethodModifierCode());
        this.specimenRole = new Cwe(sps.getSpecimenRole());
    }
}
