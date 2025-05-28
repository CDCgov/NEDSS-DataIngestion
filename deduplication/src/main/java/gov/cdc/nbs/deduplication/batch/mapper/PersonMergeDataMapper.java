package gov.cdc.nbs.deduplication.batch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.*;

public class PersonMergeDataMapper implements RowMapper<PersonMergeData> {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  @Nullable
  public PersonMergeData mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    String personUid = rs.getString("person_parent_uid");
    // General Fields
    AdminComments adminComments = mapAdminComments(rs);

    // Ethnicity Mapping
    Ethnicity ethnicity = mapEthnicity(rs);

    // Sex & Birth Mapping
    SexAndBirth sexAndBirth = mapSexAndBirth(rs);

    // Mortality Mapping
    Mortality mortality = mapMortality(rs);

    // General Patient Information Mapping
    GeneralPatientInformation generalPatientInformation = mapGeneralPatientInformation(rs);

    // Investigations Mapping
    List<Investigation> investigations = mapInvestigations(String.valueOf(rs.getString("investigations")));

    // Nested Fields
    List<Address> addresses = mapAddresses(String.valueOf(rs.getString("address")));
    List<PhoneEmail> phones = mapPhones(String.valueOf(rs.getString("phone")));
    List<Name> names = mapNames(String.valueOf(rs.getString("name")));
    List<Identification> identifiers = mapIdentifiers(String.valueOf(rs.getString("identifiers")));
    List<Race> races = mapRaces(String.valueOf(rs.getString("race")));

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

  // ETHNICITY Mapping
  Ethnicity mapEthnicity(ResultSet rs) throws SQLException {
    String asOfDate = String.valueOf(rs.getString("as_of_date_ethnicity"));
    String ethnicGroupDescription = String.valueOf(rs.getString("ethnic_group_desc_txt"));
    String spanishOrigin = String.valueOf(rs.getString("spanish_origin"));
    String ethnicUnknownReason = String.valueOf(rs.getString("ethnic_unknown_reason"));
    return new Ethnicity(
        asOfDate,
        ethnicGroupDescription,
        spanishOrigin,
        ethnicUnknownReason);
  }

  // SEX & BIRTH Mapping
  SexAndBirth mapSexAndBirth(ResultSet rs) throws SQLException {
    String asOfDate = String.valueOf(rs.getString("as_of_date_sex"));
    String birthTime = String.valueOf(rs.getString("birth_time"));
    String currentSexCode = String.valueOf(rs.getString("curr_sex_cd"));
    String sexUnknownReason = String.valueOf(rs.getString("sex_unknown_reason"));
    String additionalGenderCode = String.valueOf(rs.getString("additional_gender_cd"));
    String birthGenderCode = String.valueOf(rs.getString("birth_gender_cd"));
    Boolean multipleBirthIndicator = rs.getBoolean("multiple_birth_ind");
    Integer birthOrderNumber = rs.getInt("birth_order_nbr");
    String birthCityCode = String.valueOf(rs.getString("birth_city_cd"));
    String birthStateCode = String.valueOf(rs.getString("birth_state_cd"));
    String birthCountryCode = String.valueOf(rs.getString("birth_cntry_cd"));
    String preferredGender = String.valueOf(rs.getString("preferred_gender"));
    return new SexAndBirth(
        asOfDate,
        birthTime,
        currentSexCode,
        sexUnknownReason,
        additionalGenderCode,
        birthGenderCode,
        multipleBirthIndicator,
        birthOrderNumber,
        birthCityCode,
        birthStateCode,
        birthCountryCode,
        preferredGender);
  }

  // MORTALITY Mapping
  Mortality mapMortality(ResultSet rs) throws SQLException {
    String asOfDate = String.valueOf(rs.getString("as_of_date_morbidity"));
    String deceasedIndicatorCode = String.valueOf(rs.getString("deceased_ind_cd"));
    String deceasedTime = String.valueOf(rs.getString("deceased_time"));
    String deathCity = String.valueOf(rs.getString("death_city"));
    String deathState = String.valueOf(rs.getString("death_state"));
    String deathCounty = String.valueOf(rs.getString("death_county"));
    String deathCountry = String.valueOf(rs.getString("death_country"));
    return new Mortality(
        asOfDate,
        deceasedIndicatorCode,
        deceasedTime,
        deathCity,
        deathState,
        deathCounty,
        deathCountry);
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

  // RACE Mapping (unchanged)
  List<Race> mapRaces(String raceString) {
    return tryParse(raceString, new TypeReference<List<Map<String, Object>>>() {
    })
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::asRace)
        .filter(Objects::nonNull)
        .toList();
  }

  private static final Map<String, String> RACE_MAP = Map.ofEntries(
      Map.entry("1002-5", "AMERICAN_INDIAN"),
      Map.entry("2028-9", "ASIAN"),
      Map.entry("2054-5", "BLACK"),
      Map.entry("2076-8", "HAWAIIAN"),
      Map.entry("2106-3", "WHITE"),
      Map.entry("2131-1", "OTHER"),
      Map.entry("U", "UNKNOWN"));

  Race asRace(Map<String, Object> raceMap) {
    if (raceMap == null) {
      return null;
    }
    String personUid = String.valueOf(raceMap.get("personUid"));
    String id = String.valueOf(raceMap.get("Id"));
    String asOfDate = String.valueOf(raceMap.get("as_of_date_race"));
    String category = String.valueOf(raceMap.get("race_category_cd"));
    String mappedCategory = RACE_MAP.getOrDefault(category, null);
    if (mappedCategory == null) {
      return null;
    }
    return new Race(
        personUid,
        id,
        asOfDate,
        mappedCategory);
  }
}
