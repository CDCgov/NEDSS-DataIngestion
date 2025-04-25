package gov.cdc.nbs.deduplication.duplicates.step;

import gov.cdc.nbs.deduplication.duplicates.model.LinkResult;
import gov.cdc.nbs.deduplication.duplicates.model.MatchCandidate;
import gov.cdc.nbs.deduplication.duplicates.model.MatchResponse;
import gov.cdc.nbs.deduplication.duplicates.service.PatientRecordService;
import gov.cdc.nbs.deduplication.duplicates.service.DuplicateCheckService;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DuplicatesProcessor implements ItemProcessor<String, MatchCandidate> {

  private final PatientRecordService patientRecordService;
  private final DuplicateCheckService recordLinkerService;

  public DuplicatesProcessor(
      final PatientRecordService patientRecordService,
      final DuplicateCheckService recordLinkerService) {
    this.patientRecordService = patientRecordService;
    this.recordLinkerService = recordLinkerService;
  }

  @Override
  public MatchCandidate process(String personUid) {

    MpiPerson patientRecord = patientRecordService.fetchMostRecentPatient(personUid);
    MatchResponse response = recordLinkerService.findDuplicateRecords(patientRecord);

    // Process only "possible" responses for manual review
    if (MatchResponse.Prediction.POSSIBLE_MATCH == response.prediction()) {
      List<String> possibleMatchList = response.results().stream()
          .map(LinkResult::personReferenceId)
          .map(UUID::toString)
          .toList();

      return new MatchCandidate(personUid, possibleMatchList);
    }
    return new MatchCandidate(personUid, null);
  }
}
