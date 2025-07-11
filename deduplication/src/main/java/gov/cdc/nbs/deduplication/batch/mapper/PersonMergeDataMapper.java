package gov.cdc.nbs.deduplication.batch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Address;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.AdminComments;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Ethnicity;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.GeneralPatientInformation;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Identification;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Investigation;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Mortality;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Name;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.PhoneEmail;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Race;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.SexAndBirth;

public class PersonMergeDataMapper implements RowMapper<PersonMergeData> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  @Nullable
  public PersonMergeData mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    String personId = rs.getString("personId");
    String personUid = rs.getString("person_parent_uid");
    String addTime = rs.getString("add_time");

    // General Fields
    AdminComments adminComments = mapAdminComments(rs);

    // Ethnicity Mapping
    Ethnicity ethnicity = mapEthnicity(rs.getString("ethnicity"));

    // Sex & Birth Mapping
    SexAndBirth sexAndBirth = mapSexAndBirth(rs.getString("sexAndBirth"));

    // Mortality Mapping
    Mortality mortality = mapMortality(rs.getString("mortality"));

    // General Patient Information Mapping
    GeneralPatientInformation generalPatientInformation = mapGeneralPatientInformation(rs.getString("general"));

    // Investigations Mapping
    List<Investigation> investigations = mapInvestigations(rs.getString("investigations"));

    // Nested Fields
    List<Address> addresses = mapAddresses(rs.getString("address"));
    List<PhoneEmail> phones = mapPhones(rs.getString("phone"));
    List<Name> names = mapNames(rs.getString("name"));
    List<Identification> identifiers = mapIdentifiers(rs.getString("identifiers"));
    List<Race> races = mapRaces(rs.getString("race"));

    return new PersonMergeData(
        personId,
        personUid,
        addTime,
        adminComments,
        ethnicity,
        sexAndBirth,
        mortality,
        generalPatientInformation,
        investigations,
        addresses,
        phones,
        names,
        identifiers,
        races);
  }

  AdminComments mapAdminComments(ResultSet rs) throws SQLException {
    Timestamp commentDate = rs.getTimestamp("comment_date");

    return new AdminComments(
        commentDate != null ? commentDate.toLocalDateTime().toString() : null,
        rs.getString("admin_comments"));
  }

  Ethnicity mapEthnicity(String ethnicityString) {
    if (ethnicityString == null) {
      return new Ethnicity();
    }
    try {
      return mapper.readValue(ethnicityString, Ethnicity.class);
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient ethnicity");
    }
  }

  SexAndBirth mapSexAndBirth(String sexAndBirthString) {
    if (sexAndBirthString == null) {
      return new SexAndBirth();
    }
    try {
      return mapper.readValue(sexAndBirthString, SexAndBirth.class);
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient sex and birth");
    }
  }

  Mortality mapMortality(String mortalityString) {
    if (mortalityString == null) {
      return new Mortality();
    }
    try {
      return mapper.readValue(mortalityString, Mortality.class);
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient mortality");
    }
  }

  GeneralPatientInformation mapGeneralPatientInformation(String generalInfoString) {
    if (generalInfoString == null) {
      return new GeneralPatientInformation();
    }
    try {
      return mapper.readValue(generalInfoString, GeneralPatientInformation.class);
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient general information");
    }
  }

  List<Investigation> mapInvestigations(String investigationString) {
    if (investigationString == null) {
      return new ArrayList<>();
    }
    try {
      return mapper.readValue(investigationString, new TypeReference<List<Investigation>>() {
      });
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient investigations");
    }
  }

  Investigation asInvestigation(Map<String, Object> investigationMap) {
    if (investigationMap == null) {
      return null;
    }
    String investigationId = String.valueOf(investigationMap.get("investigationId"));
    String startedOn = String.valueOf(investigationMap.get("started_on"));
    String condition = String.valueOf(investigationMap.get("condition"));
    return new Investigation(
        investigationId,
        startedOn,
        condition);
  }

  List<Address> mapAddresses(String addressString) {
    if (addressString == null) {
      return new ArrayList<>();
    }
    try {
      return mapper.readValue(addressString, new TypeReference<List<Address>>() {
      });
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient addresses");
    }
  }

  List<PhoneEmail> mapPhones(String phoneString) {
    if (phoneString == null) {
      return new ArrayList<>();
    }
    try {
      return mapper.readValue(phoneString, new TypeReference<List<PhoneEmail>>() {
      });
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient phone and email");
    }
  }

  List<Name> mapNames(String nameString) {
    if (nameString == null) {
      return new ArrayList<>();
    }
    try {
      return mapper.readValue(nameString, new TypeReference<List<Name>>() {
      });
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient names");
    }
  }

  List<Identification> mapIdentifiers(String identifierString) {
    if (identifierString == null) {
      return new ArrayList<>();
    }
    try {
      return mapper.readValue(identifierString, new TypeReference<List<Identification>>() {
      });
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient identification");
    }
  }

  List<Race> mapRaces(String raceString) {
    if (raceString == null) {
      return new ArrayList<>();
    }
    try {
      return mapper.readValue(raceString, new TypeReference<List<Race>>() {
      });
    } catch (JsonProcessingException e) {
      throw new PersonMapException("Failed to parse patient race");
    }
  }

}
