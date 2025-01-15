package gov.cdc.nbs.deduplication.seed.model;

import java.util.List;

public record NbsPerson(
    String personId,
    String personParentId,
    String birth_date,
    String sex,
    String mrn,
    List<SeedRequest.Address> address,
    List<SeedRequest.Name> name,
    List<SeedRequest.Telecom> telecom,
    String ssn,
    String race,
    String gender,
    SeedRequest.DriversLicense drivers_license) {
}
