package gov.cdc.nbs.mpidatasyncer.model;


import java.io.Serializable;
import java.util.List;

public record LinkerSeedRequest(List<Cluster> clusters) implements Serializable {

  public record Cluster(List<Record> records, String external_person_id) implements Serializable {}

  public record Record(
      String external_id,
      String birth_date,
      String sex,
      String mrn,
      List<Address> address,
      List<Name> name,
      List<Telecom> telecom,
      String ssn,
      String race,
      String gender,
      DriversLicense drivers_license
  ) implements Serializable {}

  public record Address(String street) implements Serializable {}

  public record Name(List<String> given, String family) implements Serializable {}

  public record Telecom(String value) implements Serializable {}

  public record DriversLicense(String value, String authority) implements Serializable {}

}
