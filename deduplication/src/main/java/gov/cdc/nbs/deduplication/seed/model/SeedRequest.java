package gov.cdc.nbs.deduplication.seed.model;

import java.util.List;

public record SeedRequest(List<Cluster> clusters) {

  public SeedRequest(MpiPerson mpiPerson) {
    this(List.of(new Cluster(List.of(mpiPerson), mpiPerson.parent_id())));
  }

  public record Cluster(
      List<MpiPerson> records,
      String external_person_id // person_parent_uid
  ) {
  }
}
