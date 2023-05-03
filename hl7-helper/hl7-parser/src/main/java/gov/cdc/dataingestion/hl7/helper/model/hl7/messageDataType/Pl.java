package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pl {
    String pointOfCare;
    String room;
    String bed;
    Hd facility;
    String locationStatus;
    String personLocationType;
    String building;
    String floor;
    String locationDescription;
    Ei comprehensiveLocationIdentifier;
    Hd assignAuthorityForLocation;

    public Pl(ca.uhn.hl7v2.model.v251.datatype.PL pl) {
        this.pointOfCare = pl.getPointOfCare().getValue();
        this.room = pl.getRoom().getValue();
        this.bed = pl.getBed().getValue();
        this.facility = new Hd(pl.getFacility());
        this.locationStatus = pl.getLocationStatus().getValue();
        this.personLocationType = pl.getPersonLocationType().getValue();
        this.building = pl.getBuilding().getValue();
        this.floor = pl.getFloor().getValue();
        this.locationDescription = pl.getLocationDescription().getValue();
        this.comprehensiveLocationIdentifier = new Ei(pl.getComprehensiveLocationIdentifier());
        this.assignAuthorityForLocation = new Hd(pl.getAssigningAuthorityForLocation());
    }
}
