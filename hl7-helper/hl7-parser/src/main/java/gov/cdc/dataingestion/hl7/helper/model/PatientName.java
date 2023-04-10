package gov.cdc.dataingestion.hl7.helper.model;

public class PatientName {

    // PID.5.1
    String familyName;

    // PID.5.2
    String givenName;

    // PID.5.3
    String furtherGivenName;

    // PID.5.4
    String suffix;

    // PID.5.5
    String prefix;

    // PID.5.6
    String degree;

    //..MORE


    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFurtherGivenName() {
        return furtherGivenName;
    }

    public void setFurtherGivenName(String furtherGivenName) {
        this.furtherGivenName = furtherGivenName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}
