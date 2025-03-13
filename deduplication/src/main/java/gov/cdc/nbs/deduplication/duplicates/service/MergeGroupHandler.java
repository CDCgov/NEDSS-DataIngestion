package gov.cdc.nbs.deduplication.duplicates.service;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.duplicates.model.MergeStatusRequest;
import gov.cdc.nbs.deduplication.duplicates.model.PossibleMatchGroup;
import gov.cdc.nbs.deduplication.duplicates.model.MergeGroupResponse;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Component
public class MergeGroupHandler {

  private final NamedParameterJdbcTemplate deduplicationTemplate;
  private final PatientRecordService patientRecordService;

  public MergeGroupHandler(
      @Qualifier("deduplicationNamedTemplate") NamedParameterJdbcTemplate deduplicationTemplate,
      PatientRecordService patientRecordService
  ) {
    this.deduplicationTemplate = deduplicationTemplate;
    this.patientRecordService = patientRecordService;
  }

  public List<MergeGroupResponse> getMergeGroups(int page, int size) {
    int offset = page * size;
    return getPossibleMatchGroups(offset, size).stream()
        .map(possibleMatchGroup -> {
          List<String> personUids = getPersonIdsByMpiIds(possibleMatchGroup.mpiIds());
          List<MpiPerson> patientRecords = patientRecordService.fetchPersonRecords(personUids);
          String mostRecentName = getMostRecentNameOfTheGroup(patientRecords);
          return new MergeGroupResponse(possibleMatchGroup.personUid(), possibleMatchGroup.dateIdentified(),
              mostRecentName, patientRecords);
        })
        .toList();
  }

  private List<PossibleMatchGroup> getPossibleMatchGroups(int offset, int limit) {
    MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("limit", limit)
        .addValue("offset", offset);
    return deduplicationTemplate.query(
        QueryConstants.POSSIBLE_MATCH_GROUP,
        parameters,
        this::mapRowToPossibleMatchGroup);
  }

  private PossibleMatchGroup mapRowToPossibleMatchGroup(ResultSet rs, int rowNum) throws SQLException {
    String personUid = rs.getString("person_uid");
    String mpiPersonIds = rs.getString("mpi_person_ids");
    String dateIdentified = rs.getString("date_identified");
    return new PossibleMatchGroup(personUid, Arrays.asList(mpiPersonIds.split(", ")), dateIdentified);
  }

  private String getMostRecentNameOfTheGroup(List<MpiPerson> mpiPersonList) {
    MpiPerson oldestPersonInTheGroup = mpiPersonList.getFirst();
    MpiPerson.Name mostRecentName = oldestPersonInTheGroup.name().getFirst();
    String givenName = mostRecentName.given().isEmpty() ? "" : mostRecentName.given().getFirst();
    String familyName = mostRecentName.family() == null ? "" : mostRecentName.family();
    return givenName.concat(" ").concat(familyName);
  }

  private List<String> getPersonIdsByMpiIds(List<String> mpiIds) {
    return deduplicationTemplate.query(
        QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS,
        new MapSqlParameterSource("mpiIds", mpiIds),
        (rs, rowNum) -> rs.getString("person_uid")
    );
  }

  public void updateMergeStatus(MergeStatusRequest request) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("personUid", request.personUid());
    parameters.addValue("isMerge", request.isMerge());
    deduplicationTemplate.update(QueryConstants.UPDATE_MERGE_STATUS_FOR_GROUP, parameters);
  }

}
