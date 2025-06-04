package gov.cdc.nbs.deduplication.batch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    String personUid = rs.getString("person_parent_uid");
    // General Fields
    AdminComments adminComments = mapAdminComments(rs);

    // Ethnicity Mapping
    Ethnicity ethnicity = mapEthnicity(rs.getString("ethnicity"));

    // Sex & Birth Mapping
    SexAndBirth sexAndBirth = mapSexAndBirth(rs.getString("sexAndBirth"));

    // Mortality Mapping
    Mortality mortality = mapMortality(rs.getString("mortality"));

    // General Patient Information Mapping
    GeneralPatientInformation generalPatientInformation = mapGeneralPatientInformation(rs);

    // Investigations Mapping
    List<Investigation> investigations = mapInvestigations(String.valueOf(rs.getString("investigations")));

    // Nested Fields
    List<Address> addresses = mapAddresses(rs.getString("address"));
    List<PhoneEmail> phones = mapPhones(rs.getString("phone"));
    List<Name> names = mapNames(rs.getString("name"));
    List<Identification> identifiers = mapIdentifiers(rs.getString("identifiers"));
    List<Race> races = mapRaces(rs.getString("race"));

    return new PersonMergeData(
        personUid,
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

  <T> Optional<T> tryParse(String stringValue, TypeReference<T> reference) {
    if (stringValue == null || stringValue.isBlank()) {
      return Optional.empty();
    }
    try {
      return Optional.of(mapper.readValue(stringValue, reference));
    } catch (Exception e) {
      return Optional.empty();
    }
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

  // GENERAL PATIENT INFORMATION Mapping
  GeneralPatientInformation mapGeneralPatientInformation(ResultSet rs) throws SQLException {
    String asOfDate = String.valueOf(rs.getString("as_of_date_general"));
    String maritalStatusDescription = String.valueOf(rs.getString("marital_status_desc_txt"));
    String mothersMaidenName = String.valueOf(rs.getString("mothers_maiden_nm"));
    Integer adultsInHouseholdNumber = rs.getInt("adults_in_house_nbr");
    Integer childrenInHouseholdNumber = rs.getInt("children_in_house_nbr");
    String occupationCode = String.valueOf(rs.getString("occupation_cd"));
    String educationLevelDescription = String.valueOf(rs.getString("education_level_desc_txt"));
    String primaryLanguageDescription = String.valueOf(rs.getString("prim_lang_desc_txt"));
    String speaksEnglishCode = String.valueOf(rs.getString("speaks_english_cd"));
    String stateHivCaseId = String.valueOf(rs.getString("State_HIV_Case_ID"));
    return new GeneralPatientInformation(
        asOfDate,
        maritalStatusDescription,
        mothersMaidenName,
        adultsInHouseholdNumber,
        childrenInHouseholdNumber,
        occupationCode,
        educationLevelDescription,
        primaryLanguageDescription,
        speaksEnglishCode,
        stateHivCaseId);
  }

  // INVESTIGATIONS Mapping
  List<PersonMergeData.Investigation> mapInvestigations(String investigationString) {
    return tryParse(investigationString, new TypeReference<List<Map<String, Object>>>() {
    })
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::asInvestigation)
        .filter(Objects::nonNull)
        .toList();
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
