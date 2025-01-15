package gov.cdc.nbs.deduplication.seed.model;

import java.util.List;

public record SeedRequest(List<Cluster> clusters) {

  public record Cluster(
      List<MpiPerson> records,
      String external_person_id // person_parent_uid
  ) {
  }

  public record MpiPerson(
      String external_id, // person_uid
      String birth_date,
      String sex,
      String mrn,
      List<Address> address,
      List<Name> name,
      List<Telecom> telecom,
      String ssn,
      String race,
      String gender,
      DriversLicense drivers_license) {
  }

  public record Address(
      List<String> line,
      String city,
      String state,
      String postal_code,
      String county) {
  }

  public record Name(
      List<String> given,
      String family,
      List<String> suffix) {
  }

  public record Telecom(String value) {
  }

  public record DriversLicense(
      String value,
      String authority) {
  }

}
