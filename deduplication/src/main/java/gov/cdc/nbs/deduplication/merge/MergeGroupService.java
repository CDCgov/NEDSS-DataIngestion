package gov.cdc.nbs.deduplication.merge;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import gov.cdc.nbs.deduplication.batch.service.PatientRecordService;

@Component
public class MergeGroupService {

  private final JdbcClient jdbcClient;
  private final PatientRecordService patientRecordService;

  public MergeGroupService(
      @Qualifier("deduplicationJdbcClient") final JdbcClient jdbcClient,
      PatientRecordService patientRecordService) {
    this.jdbcClient = jdbcClient;
    this.patientRecordService = patientRecordService;
  }

}
