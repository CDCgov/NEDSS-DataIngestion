package gov.cdc.nbs.deduplication.duplicates.model;

import java.util.List;

public record MatchCandidate(String personUid, List<String> possibleMatchList) {
}
