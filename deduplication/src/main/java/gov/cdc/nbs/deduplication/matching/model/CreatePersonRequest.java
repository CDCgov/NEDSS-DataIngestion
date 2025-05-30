package gov.cdc.nbs.deduplication.matching.model;

import java.util.List;

public record CreatePersonRequest(List<String> patients) {

}
