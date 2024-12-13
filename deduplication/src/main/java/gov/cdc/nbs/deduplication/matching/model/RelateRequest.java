package gov.cdc.nbs.deduplication.matching.model;

import gov.cdc.nbs.deduplication.matching.model.MatchResponse.MatchType;

public record RelateRequest(
    Long nbsPerson,
    Long nbsPersonParent,
    MatchType matchType,
    LinkResponse linkResponse) {

}
