package gov.cdc.nbs.deduplication.duplicates.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import gov.cdc.nbs.deduplication.duplicates.model.LinkResult;
import gov.cdc.nbs.deduplication.duplicates.model.MatchRequest;
import gov.cdc.nbs.deduplication.duplicates.model.MatchResponse;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class DuplicateCheckServiceTest {

  @Mock
  private RestClient recordLinkageClient;

  @Mock
  private RequestBodyUriSpec uriSpec;

  @Mock
  private RequestBodySpec bodySpec;

  @Mock
  private ResponseSpec responseSpec;

  @InjectMocks
  private DuplicateCheckService duplicateCheckService;


  @Test
  void findDuplicateRecordsTest() {
    MpiPerson personRecord = new MpiPerson(null, null, null, null,
        null, null, null, null, null);

    LinkResult linkResult1 = new LinkResult(UUID.randomUUID(), 0.85);
    LinkResult linkResult2 = new LinkResult(UUID.randomUUID(), 0.90);
    MatchResponse expectedResponse = new MatchResponse(
        MatchResponse.Prediction.POSSIBLE_MATCH,
        UUID.randomUUID(),
        List.of(linkResult1, linkResult2)
    );

    when(recordLinkageClient.post()).thenReturn(uriSpec);
    when(uriSpec.uri("/match")).thenReturn(bodySpec);
    when(bodySpec.accept(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodySpec);
    when(bodySpec.body(any(MatchRequest.class))).thenReturn(bodySpec);
    when(bodySpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.body(MatchResponse.class)).thenReturn(expectedResponse);

    MatchResponse actualResponse = duplicateCheckService.findDuplicateRecords(personRecord);

    assertThat(actualResponse).isEqualTo(expectedResponse);
  }

}
