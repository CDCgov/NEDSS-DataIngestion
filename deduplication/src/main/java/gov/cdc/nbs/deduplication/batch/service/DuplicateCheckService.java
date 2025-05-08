package gov.cdc.nbs.deduplication.batch.service;

import gov.cdc.nbs.deduplication.batch.model.MatchRequest;
import gov.cdc.nbs.deduplication.batch.model.MatchResponse;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

@Service
public class DuplicateCheckService {

  private final RestClient recordLinkageClient;

  public DuplicateCheckService(@Qualifier("recordLinkerRestClient") RestClient recordLinkageClient) {
    this.recordLinkageClient = recordLinkageClient;
  }

  public MatchResponse findDuplicateRecords(MpiPerson personRecord) {
    return recordLinkageClient.post()
        .uri("/match")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(new MatchRequest(personRecord, personRecord.parent_id(), null))
        .retrieve()
        .body(MatchResponse.class);
  }
}
