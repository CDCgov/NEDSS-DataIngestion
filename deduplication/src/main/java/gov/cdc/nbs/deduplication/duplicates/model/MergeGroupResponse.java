package gov.cdc.nbs.deduplication.duplicates.model;

import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

import java.util.List;

public record MergeGroupResponse(
    String dateIdentified,
    String mostRecentPersonName,
    List<MpiPerson> patients
) {
}
