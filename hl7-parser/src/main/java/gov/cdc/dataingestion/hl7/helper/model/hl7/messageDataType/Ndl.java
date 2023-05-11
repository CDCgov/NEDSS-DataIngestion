package gov.cdc.dataingestion.hl7.helper.model.hl7.messageDataType;

import ca.uhn.hl7v2.model.v251.datatype.NDL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ndl {
    Cnn name = new Cnn();
    Ts startDateTime = new Ts();
    Ts endDateTime = new Ts();
    String pointOfCare;
    String room;
    String bed;
    Hd facility = new Hd();
    String locationStatus;
    String patientLocationType;
    String building;
    String floor;
    public Ndl(NDL ndl) {
        this.name = new Cnn(ndl.getNDLName());
        this.startDateTime = new Ts(ndl.getStartDateTime());
        this.endDateTime = new Ts(ndl.getEndDateTime());
        this.pointOfCare = ndl.getPointOfCare().getValue();
        this.room = ndl.getRoom().getValue();
        this.bed = ndl.getBed().getValue();
        this.facility= new Hd(ndl.getFacility());
        this.locationStatus = ndl.getLocationStatus().getValue();
        this.patientLocationType = ndl.getPatientLocationType().getValue();
        this.building = ndl.getBuilding().getValue();
        this.floor = ndl.getFloor().getValue();
    }

    public Ndl() {

    }
}
