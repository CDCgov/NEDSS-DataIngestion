package gov.cdc.nbs.deduplication.seed.model;

import java.util.List;

public record SeedRequest(List<Cluster> clusters) {

  public record Cluster(
      List<MpiPerson> records,
      String external_person_id // person_parent_uid
  ) {
  }

}
