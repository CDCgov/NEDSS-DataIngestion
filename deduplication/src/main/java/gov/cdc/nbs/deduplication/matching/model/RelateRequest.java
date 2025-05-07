package gov.cdc.nbs.deduplication.matching.model;

import gov.cdc.nbs.deduplication.matching.model.MatchResponse.MatchType;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

public record RelateRequest(
        Long nbsPerson,
        Long nbsPersonParent,
        MatchType matchType,
        LinkResponse linkResponse,
        MpiPerson mpiPerson) {

}
