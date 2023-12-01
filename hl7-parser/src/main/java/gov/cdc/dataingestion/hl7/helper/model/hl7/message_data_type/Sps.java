package gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type;

import ca.uhn.hl7v2.model.v251.datatype.SPS;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sps {
    Cwe specimenSourceNameOrCode = new Cwe();
    Cwe additives = new Cwe();
    String specimenCollectionMethod;
    Cwe bodySite = new Cwe();
    Cwe siteModifier = new Cwe();
    Cwe collectionMethodModifierCode = new Cwe();
    Cwe specimenRole = new Cwe();
    public Sps(SPS sps) {
        this.specimenSourceNameOrCode = new Cwe(sps.getSpecimenSourceNameOrCode());
        this.additives = new Cwe(sps.getAdditives());
        this.specimenCollectionMethod = sps.getSpecimenCollectionMethod().getValue();
        this.bodySite = new Cwe(sps.getBodySite());
        this.siteModifier = new Cwe(sps.getSiteModifier());
        this.collectionMethodModifierCode = new Cwe(sps.getCollectionMethodModifierCode());
        this.specimenRole = new Cwe(sps.getSpecimenRole());
    }

    public Sps() {

    }
}
