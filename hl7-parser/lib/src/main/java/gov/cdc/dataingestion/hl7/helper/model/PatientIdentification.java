package gov.cdc.dataingestion.hl7.helper.model;

public class PatientIdentification {

    // pid.1
    String pId;

    // pid.2
    String id;

    // pid.5
    PatientName patientName;

    // pid.6
    PatientName motherMaidenName;

    // pid.7
    String dateTimeOfBirth;

    PatientAddress patientAddress;

    //..MORE


    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PatientName getPatientName() {
        return patientName;
    }

    public void setPatientName(PatientName patientName) {
        this.patientName = patientName;
    }

    public PatientName getMotherMaidenName() {
        return motherMaidenName;
    }

    public void setMotherMaidenName(PatientName motherMaidenName) {
        this.motherMaidenName = motherMaidenName;
    }

    public String getDateTimeOfBirth() {
        return dateTimeOfBirth;
    }

    public void setDateTimeOfBirth(String dateTimeOfBirth) {
        this.dateTimeOfBirth = dateTimeOfBirth;
    }

    public PatientAddress getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(PatientAddress patientAddress) {
        this.patientAddress = patientAddress;
    }
}
