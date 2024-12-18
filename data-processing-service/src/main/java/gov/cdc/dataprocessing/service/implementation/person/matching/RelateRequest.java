package gov.cdc.dataprocessing.service.implementation.person.matching;

import gov.cdc.dataprocessing.service.implementation.person.matching.MatchResponse.MatchType;

public record RelateRequest(
    Long nbsPerson,
    Long nbsPersonParent,
    MatchType matchType,
    LinkResponse linkResponse) {

}
