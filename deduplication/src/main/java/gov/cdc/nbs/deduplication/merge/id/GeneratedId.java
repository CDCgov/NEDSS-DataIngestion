package gov.cdc.nbs.deduplication.merge.id;

public record GeneratedId(
    Long id,
    String prefix,
    String suffix) {

  public String toLocalId() {
    return prefix + id.toString() + suffix;
  }

}
