package gov.cdc.nbs.deduplication.batch.step;

import gov.cdc.nbs.deduplication.batch.model.LinkResult;
import gov.cdc.nbs.deduplication.batch.model.MatchCandidate;
import gov.cdc.nbs.deduplication.batch.model.MatchResponse;
import gov.cdc.nbs.deduplication.batch.service.DuplicateCheckService;
import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;
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

    // Both "possible" and "EXACT" matches should be flagged for review.
    // We do not auto merge exact matches as part of the batch prcoessing of
    // existing records
    if (MatchResponse.Prediction.NO_MATCH != response.prediction()) {
      List<String> possibleMatchList = response.results().stream()
          .map(LinkResult::personReferenceId)
          .map(UUID::toString)
          .toList();

      return new MatchCandidate(personUid, possibleMatchList);
    }
    return new MatchCandidate(personUid, null);
  }
}
