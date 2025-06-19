package gov.cdc.nbs.deduplication.patient;

import java.sql.Timestamp;

public record PatientName(
    Timestamp asof,
    Type type,
    Prefix prefix,
    String first,
    String middle,
    String last,
    Suffix suffix,
    Degree degree) {

  public interface CodedValue {
    public String code();
  }

  public enum Type implements CodedValue {
    ADOPTED_NAME("AD"),
    ALIAS_NAME("AL"),
    ARTIST("A"),
    CODED_PSEUDO("S"),
    INDIGENOUS_TRIBAL("I"),
    LEGAL("L"),
    LICENSE("C"),
    MAIDEN_NAME("M"),
    MOTHERS_NAME("MO"),
    NAME_AT_BIRTH("BR"),
    NAME_OF_PARTNER_SPOUSE("P"),
    RELIGIOUS("R");

    private final String code;

    Type(String code) {
      this.code = code;
    }

    public String code() {
      return code;
    }

  }

  public enum Prefix implements CodedValue {
    BISHOP("BSHP"),
    BROTHER("BRO"),
    CARDINAL("CARD"),
    DOCTOR("DR"),
    FATHER("FATH"),
    HONORABLE("HON"),
    MISS("MISS"),
    MONSIGNOR("MON"),
    MOTHER("MOTH"),
    MR("MR"),
    MRS("MRS"),
    MS("MS"),
    PASTOR("PAST"),
    PROFESSOR("PROF"),
    RABBI("RAB"),
    REVEREND("REV"),
    SISTER("SIS"),
    SWAMI("SWM");

    private final String code;

    Prefix(String code) {
      this.code = code;
    }

    public String code() {
      return code;
    }

  }

  public enum Suffix implements CodedValue {
    ESQUIRE("ESQ"),
    THE_SECOND("II"),
    THE_THIRD("III"),
    THE_FOURTH("IV"),
    THE_FIFTH("V"),
    JUNIOR("JR"),
    SENIOR("SR"),
    ;

    private final String code;

    Suffix(String code) {
      this.code = code;
    }

    public String code() {
      return code;
    }

  }

  public enum Degree implements CodedValue {
    APN("APN"),
    BA("BA"),
    BS("BS"),
    BSN("BSN"),
    CNM("CNM"),
    CNP("CNP"),
    CPA("CPA"),
    DD("DD"),
    DDS("DDS"),
    DMD("DMD"),
    DO("DO"),
    DRN("DRN"),
    DVM("DVM"),
    JD("JD"),
    LLB("LLB"),
    LLD("LLD"),
    LPN("LPN"),
    MA("MA"),
    MBA("MBA"),
    MD("MD"),
    MED("MED"),
    MPH("MPH"),
    MS("MS"),
    MSN("MSN"),
    NP("NP"),
    PA("PA"),
    PHD("PHD"),
    RN("RN"),
    ;

    private final String code;

    Degree(String code) {
      this.code = code;
    }

    public String code() {
      return code;
    }

  }

}
