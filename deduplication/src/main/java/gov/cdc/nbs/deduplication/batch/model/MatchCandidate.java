package gov.cdc.nbs.deduplication.batch.model;

import java.util.List;

public record MatchCandidate(String personUid, List<String> possibleMatchList) {
}
