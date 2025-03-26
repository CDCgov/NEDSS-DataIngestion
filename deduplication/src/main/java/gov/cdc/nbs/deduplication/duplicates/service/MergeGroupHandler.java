package gov.cdc.nbs.deduplication.duplicates.service;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
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
          personUids.add(possibleMatchGroup.personUid());
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
    MpiPerson oldestPersonInTheGroup = (mpiPersonList.isEmpty()) ? null : mpiPersonList.getFirst();
    if (oldestPersonInTheGroup != null) {
      MpiPerson.Name mostRecentName = oldestPersonInTheGroup.name().getFirst();
      String givenName = mostRecentName.given().isEmpty() ? "" : mostRecentName.given().getFirst();
      String familyName = mostRecentName.family() == null ? "" : mostRecentName.family();
      return givenName.concat(" ").concat(familyName);
    }
    return "";
  }

  private List<String> getPersonIdsByMpiIds(List<String> mpiIds) {
    return deduplicationTemplate.query(
        QueryConstants.PERSON_UIDS_BY_MPI_PATIENT_IDS,
        new MapSqlParameterSource("mpiPersonIds", mpiIds),
        (rs, rowNum) -> rs.getString("person_uid")
    );
  }

  private List<String> getMpiIdsByPersonIds(List<String> personIds) {
    return deduplicationTemplate.query(
        QueryConstants.PATIENT_IDS_BY_PERSON_UIDS,
        new MapSqlParameterSource("personIds", personIds),
        (rs, rowNum) -> rs.getString("mpi_person")
    );
  }

  public void updateMergeStatusForGroup(Long personOfTheGroup) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("personUid", personOfTheGroup);
    parameters.addValue("isMerge", false);
    deduplicationTemplate.update(QueryConstants.UPDATE_MERGE_STATUS_FOR_GROUP, parameters);
  }

  public void updateMergeStatusForPatients(String survivorPersonId, List<String> personIds) {
    markMergedRecordAsMerge(survivorPersonId, personIds);
    markNonActiveRecordAsNoMerge(survivorPersonId, personIds);
    markSingleRemainingRecordAsNoMergeIfExists(survivorPersonId);
  }

  private void markMergedRecordAsMerge(String survivorPersonId, List<String> personIds) {
    List<String> mpiPersonIds = getMpiIdsByPersonIds(personIds);
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("mpiIds", mpiPersonIds);
    parameters.addValue("personId", survivorPersonId);
    deduplicationTemplate.update(QueryConstants.UPDATE_MERGE_STATUS_FOR_PATIENTS, parameters);
  }

  private void markNonActiveRecordAsNoMerge(String survivorPersonId, List<String> personIds) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    List<String> mpiPersonIds = getMpiIdsByPersonIds(personIds);
    parameters.addValue("mpiIds", mpiPersonIds);
    parameters.addValue("personId", survivorPersonId);
    parameters.addValue("personIds", personIds);
    deduplicationTemplate.update(QueryConstants.UPDATE_MERGE_STATUS_FOR_NON_PATIENTS, parameters);
  }

  private void markSingleRemainingRecordAsNoMergeIfExists(String survivorPersonId) {
    deduplicationTemplate.update(QueryConstants.UPDATE_SINGLE_RECORD,
        new MapSqlParameterSource("personUid", survivorPersonId));
  }

}
