package gov.cdc.nbs.deduplication.duplicates.model;


import java.util.List;

public record PossibleMatchGroup(
    List<String> mpiIds,
    String dateIdentified
) {
}
