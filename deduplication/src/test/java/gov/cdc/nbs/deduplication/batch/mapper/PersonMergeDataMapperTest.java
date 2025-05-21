package gov.cdc.nbs.deduplication.batch.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PersonMergeDataMapperTest {

  private final PersonMergeDataMapper mapper = new PersonMergeDataMapper();

  // Constants for testing
  private static final String PERSON_UID = "1000001";
  private static final String COMMENT_DATE = "2023-01-10T00:00";
  private static final Timestamp COMMENT_DATE_TIMESTAMP = Timestamp.valueOf("2023-01-10 00:00:00");
  private static final String COMMENTS = "Admin comments here";

  private static final String ETHNICITY_AS_OF_DATE = "2023-01-01";
  private static final String ETHNIC_GROUP_DESC_TXT = "Hispanic or Latino";
  private static final String SPANISH_ORIGIN = "Yes";
  private static final String ETHNIC_UNKNOWN_REASON = "Unknown";

  private static final String SEX_AND_BIRTH_AS_OF_DATE = "2023-02-01";
  private static final String BIRTH_TIME = "1990-01-01T00:00:00Z";
  private static final String CURRENT_SEX_CODE = "M";
  private static final String SEX_UNKNOWN_REASON = "Not applicable";
  private static final String ADDITIONAL_GENDER_CODE = "";
  private static final String BIRTH_GENDER_CODE = "Male";
  private static final Boolean MULTIPLE_BIRTH_INDICATOR = true;
  private static final Integer BIRTH_ORDER_NUMBER = 1;
  private static final String BIRTH_CITY_CODE = "12345";
  private static final String BIRTH_STATE_CODE = "GA";
  private static final String BIRTH_COUNTRY_CODE = "US";
  private static final String PREFERRED_GENDER = "Male";

  private static final String MORTALITY_AS_OF_DATE = "2023-03-01";
  private static final String DECEASED_INDICATOR_CODE = "Y";
  private static final String DECEASED_TIME = "2023-04-01T00:00:00Z";
  private static final String DEATH_CITY = "Atlanta";
  private static final String DEATH_STATE = "Georgia";
  private static final String DEATH_COUNTY = "Fulton";
  private static final String DEATH_COUNTRY = "US";

  private static final String GENERAL_PATIENT_INFO_AS_OF_DATE = "2023-05-01";
  private static final String MARITAL_STATUS_DESCRIPTION = "Married";
  private static final String MOTHERS_MAIDEN_NAME = "Jane Doe";
  private static final Integer ADULTS_IN_HOUSEHOLD_NUMBER = 2;
  private static final Integer CHILDREN_IN_HOUSEHOLD_NUMBER = 1;
  private static final String OCCUPATION_CODE = "Engineer";
  private static final String EDUCATION_LEVEL_DESCRIPTION = "Bachelor's Degree";
  private static final String PRIMARY_LANGUAGE_DESCRIPTION = "English";
  private static final String SPEAKS_ENGLISH_CODE = "Y";
  private static final String STATE_HIV_CASE_ID = "123456789";

  private static final String INVESTIGATIONS_STRING = """
      [
          {"investigationId": "1", "started_on": "2023-06-01T00:00:00Z", "condition": "Condition A"},
          {"investigationId": "2", "started_on": "2023-07-01T00:00:00Z", "condition": "Condition B"}
      ]
      """;

  private static final String ADDRESS_STRING = """
      [
          {"address1": "123 Main st", "city": "Atlanta", "state": "Georgia", "zip": "12345"},
          {"address1": "456 Elm st", "city": "Nashville", "state": "Tennessee", "zip": "67890"}
      ]
      """;

  private static final String PHONE_STRING = """
      [
          {"phone_number": "1234567890"},
          {"phone_number": "9876543210"}
      ]
      """;

  private static final String NAME_STRING = """
      [
          {"first": "John", "last": "Doe"},
          {"first": "Jane", "last": "Smith"}
      ]
      """;

  private static final String IDENTIFIER_STRING = """
      [
           {
               "type": "DL",
               "id": "1",
               "as_of_date_identifier": "2023-01-01",
               "value": "1234567",
               "assigning_authority": "Test"
           }
       ]
      """;

  private static final String RACE_STRING = """
      [
          {"race_category_cd": "2106-3"},
          {"race_category_cd": "2054-5"}
      ]
      """;

  @Test
  void testMapRow() throws Exception {
    ResultSet rs = Mockito.mock(ResultSet.class);
    // Mocking
    when(rs.getString("person_parent_uid")).thenReturn(PERSON_UID);
    mockGeneralFields(rs);
    mockEthnicityFields(rs);
    mockSexAndBirthFields(rs);
    mockMortalityFields(rs);
    mockGeneralPatientInformationFields(rs);
    mockInvestigationsField(rs);
    mockNestedFields(rs);

    // Perform mapping
    PersonMergeData personMergeData = mapper.mapRow(rs, 0);

    // Assertions
    assertThat(personMergeData.personUid()).isEqualTo(PERSON_UID);
    assertAdminComments(personMergeData);
    assertEthnicity(personMergeData);
    assertSexAndBirth(personMergeData);
    assertMortality(personMergeData);
    assertGeneralPatientInformation(personMergeData);
    assertInvestigations(personMergeData);
    assertNestedFields(personMergeData);
  }

  // Mocking Methods
  private void mockGeneralFields(ResultSet rs) throws SQLException {
    when(rs.getTimestamp("comment_date")).thenReturn(COMMENT_DATE_TIMESTAMP);
    when(rs.getString("admin_comments")).thenReturn(COMMENTS);
  }

  private void mockEthnicityFields(ResultSet rs) throws SQLException {
    when(rs.getString("as_of_date_ethnicity")).thenReturn(ETHNICITY_AS_OF_DATE);
    when(rs.getString("ethnic_group_desc_txt")).thenReturn(ETHNIC_GROUP_DESC_TXT);
    when(rs.getString("spanish_origin")).thenReturn(SPANISH_ORIGIN);
    when(rs.getString("ethnic_unknown_reason")).thenReturn(ETHNIC_UNKNOWN_REASON);
  }

  private void mockSexAndBirthFields(ResultSet rs) throws SQLException {
    when(rs.getString("as_of_date_sex")).thenReturn(SEX_AND_BIRTH_AS_OF_DATE);
    when(rs.getString("birth_time")).thenReturn(BIRTH_TIME);
    when(rs.getString("curr_sex_cd")).thenReturn(CURRENT_SEX_CODE);
    when(rs.getString("sex_unknown_reason")).thenReturn(SEX_UNKNOWN_REASON);
    when(rs.getString("additional_gender_cd")).thenReturn(ADDITIONAL_GENDER_CODE);
    when(rs.getString("birth_gender_cd")).thenReturn(BIRTH_GENDER_CODE);
    when(rs.getBoolean("multiple_birth_ind")).thenReturn(MULTIPLE_BIRTH_INDICATOR);
    when(rs.getInt("birth_order_nbr")).thenReturn(BIRTH_ORDER_NUMBER);
    when(rs.getString("birth_city_cd")).thenReturn(BIRTH_CITY_CODE);
    when(rs.getString("birth_state_cd")).thenReturn(BIRTH_STATE_CODE);
    when(rs.getString("birth_cntry_cd")).thenReturn(BIRTH_COUNTRY_CODE);
    when(rs.getString("preferred_gender")).thenReturn(PREFERRED_GENDER);
  }

  private void mockMortalityFields(ResultSet rs) throws SQLException {
    when(rs.getString("as_of_date_morbidity")).thenReturn(MORTALITY_AS_OF_DATE);
    when(rs.getString("deceased_ind_cd")).thenReturn(DECEASED_INDICATOR_CODE);
    when(rs.getString("deceased_time")).thenReturn(DECEASED_TIME);
    when(rs.getString("death_city")).thenReturn(DEATH_CITY);
    when(rs.getString("death_state")).thenReturn(DEATH_STATE);
    when(rs.getString("death_county")).thenReturn(DEATH_COUNTY);
    when(rs.getString("death_country")).thenReturn(DEATH_COUNTRY);
  }

  private void mockGeneralPatientInformationFields(ResultSet rs) throws SQLException {
    when(rs.getString("as_of_date_general")).thenReturn(GENERAL_PATIENT_INFO_AS_OF_DATE);
    when(rs.getString("marital_status_desc_txt")).thenReturn(MARITAL_STATUS_DESCRIPTION);
    when(rs.getString("mothers_maiden_nm")).thenReturn(MOTHERS_MAIDEN_NAME);
    when(rs.getInt("adults_in_house_nbr")).thenReturn(ADULTS_IN_HOUSEHOLD_NUMBER);
    when(rs.getInt("children_in_house_nbr")).thenReturn(CHILDREN_IN_HOUSEHOLD_NUMBER);
    when(rs.getString("occupation_cd")).thenReturn(OCCUPATION_CODE);
    when(rs.getString("education_level_desc_txt")).thenReturn(EDUCATION_LEVEL_DESCRIPTION);
    when(rs.getString("prim_lang_desc_txt")).thenReturn(PRIMARY_LANGUAGE_DESCRIPTION);
    when(rs.getString("speaks_english_cd")).thenReturn(SPEAKS_ENGLISH_CODE);
    when(rs.getString("State_HIV_Case_ID")).thenReturn(STATE_HIV_CASE_ID);
  }

  private void mockInvestigationsField(ResultSet rs) throws SQLException {
    when(rs.getString("investigations")).thenReturn(INVESTIGATIONS_STRING);
  }

  private void mockNestedFields(ResultSet rs) throws SQLException {
    when(rs.getString("address")).thenReturn(ADDRESS_STRING);
    when(rs.getString("phone")).thenReturn(PHONE_STRING);
    when(rs.getString("name")).thenReturn(NAME_STRING);
    when(rs.getString("identifiers")).thenReturn(IDENTIFIER_STRING);
    when(rs.getString("race")).thenReturn(RACE_STRING);
  }

  // Assertion Methods
  private void assertAdminComments(PersonMergeData personMergeData) {
    assertThat(personMergeData.adminComments().date()).isEqualTo(COMMENT_DATE);
    assertThat(personMergeData.adminComments().comment()).isEqualTo(COMMENTS);
  }

  private void assertEthnicity(PersonMergeData personMergeData) {
    assertThat(personMergeData.ethnicity().asOfDate()).isEqualTo(ETHNICITY_AS_OF_DATE);
    assertThat(personMergeData.ethnicity().ethnicGroupDescription()).isEqualTo(ETHNIC_GROUP_DESC_TXT);
    assertThat(personMergeData.ethnicity().spanishOrigin()).isEqualTo(SPANISH_ORIGIN);
    assertThat(personMergeData.ethnicity().ethnicUnknownReason()).isEqualTo(ETHNIC_UNKNOWN_REASON);
  }

  private void assertSexAndBirth(PersonMergeData personMergeData) {
    assertThat(personMergeData.sexAndBirth().asOfDate()).isEqualTo(SEX_AND_BIRTH_AS_OF_DATE);
    assertThat(personMergeData.sexAndBirth().birthTime()).isEqualTo(BIRTH_TIME);
    assertThat(personMergeData.sexAndBirth().currentSexCode()).isEqualTo(CURRENT_SEX_CODE);
    assertThat(personMergeData.sexAndBirth().sexUnknownReason()).isEqualTo(SEX_UNKNOWN_REASON);
    assertThat(personMergeData.sexAndBirth().additionalGenderCode()).isEqualTo(ADDITIONAL_GENDER_CODE);
    assertThat(personMergeData.sexAndBirth().birthGenderCode()).isEqualTo(BIRTH_GENDER_CODE);
    assertThat(personMergeData.sexAndBirth().multipleBirthIndicator()).isEqualTo(MULTIPLE_BIRTH_INDICATOR);
    assertThat(personMergeData.sexAndBirth().birthOrderNumber()).isEqualTo(BIRTH_ORDER_NUMBER);
    assertThat(personMergeData.sexAndBirth().birthCityCode()).isEqualTo(BIRTH_CITY_CODE);
    assertThat(personMergeData.sexAndBirth().birthStateCode()).isEqualTo(BIRTH_STATE_CODE);
    assertThat(personMergeData.sexAndBirth().birthCountryCode()).isEqualTo(BIRTH_COUNTRY_CODE);
    assertThat(personMergeData.sexAndBirth().preferredGender()).isEqualTo(PREFERRED_GENDER);
  }

  private void assertMortality(PersonMergeData personMergeData) {
    assertThat(personMergeData.mortality().asOfDate()).isEqualTo(MORTALITY_AS_OF_DATE);
    assertThat(personMergeData.mortality().deceasedIndicatorCode()).isEqualTo(DECEASED_INDICATOR_CODE);
    assertThat(personMergeData.mortality().deceasedTime()).isEqualTo(DECEASED_TIME);
    assertThat(personMergeData.mortality().deathCity()).isEqualTo(DEATH_CITY);
    assertThat(personMergeData.mortality().deathState()).isEqualTo(DEATH_STATE);
    assertThat(personMergeData.mortality().deathCounty()).isEqualTo(DEATH_COUNTY);
    assertThat(personMergeData.mortality().deathCountry()).isEqualTo(DEATH_COUNTRY);
  }

  private void assertGeneralPatientInformation(PersonMergeData personMergeData) {
    assertThat(personMergeData.generalPatientInformation().asOfDate()).isEqualTo(GENERAL_PATIENT_INFO_AS_OF_DATE);
    assertThat(personMergeData.generalPatientInformation().maritalStatusDescription())
        .isEqualTo(MARITAL_STATUS_DESCRIPTION);
    assertThat(personMergeData.generalPatientInformation().mothersMaidenName()).isEqualTo(MOTHERS_MAIDEN_NAME);
    assertThat(personMergeData.generalPatientInformation().adultsInHouseholdNumber())
        .isEqualTo(ADULTS_IN_HOUSEHOLD_NUMBER);
    assertThat(personMergeData.generalPatientInformation().childrenInHouseholdNumber())
        .isEqualTo(CHILDREN_IN_HOUSEHOLD_NUMBER);
    assertThat(personMergeData.generalPatientInformation().occupationCode()).isEqualTo(OCCUPATION_CODE);
    assertThat(personMergeData.generalPatientInformation().educationLevelDescription())
        .isEqualTo(EDUCATION_LEVEL_DESCRIPTION);
    assertThat(personMergeData.generalPatientInformation().primaryLanguageDescription())
        .isEqualTo(PRIMARY_LANGUAGE_DESCRIPTION);
    assertThat(personMergeData.generalPatientInformation().speaksEnglishCode()).isEqualTo(SPEAKS_ENGLISH_CODE);
    assertThat(personMergeData.generalPatientInformation().stateHivCaseId()).isEqualTo(STATE_HIV_CASE_ID);
  }

  private void assertInvestigations(PersonMergeData personMergeData) {
    assertThat(personMergeData.investigations()).hasSize(2);
    assertThat(personMergeData.investigations().getFirst().investigationId()).isEqualTo("1");
    assertThat(personMergeData.investigations().getFirst().startedOn()).isEqualTo("2023-06-01T00:00:00Z");
    assertThat(personMergeData.investigations().getFirst().condition()).isEqualTo("Condition A");
    assertThat(personMergeData.investigations().get(1).investigationId()).isEqualTo("2");
    assertThat(personMergeData.investigations().get(1).startedOn()).isEqualTo("2023-07-01T00:00:00Z");
    assertThat(personMergeData.investigations().get(1).condition()).isEqualTo("Condition B");
  }

  private void assertNestedFields(PersonMergeData personMergeData) {
    assertThat(personMergeData.address()).hasSize(2);
    assertThat(personMergeData.telecom()).hasSize(2);
    assertThat(personMergeData.name()).hasSize(2);
    assertThat(personMergeData.identifiers()).hasSize(1);
    assertThat(personMergeData.race()).hasSize(2);
  }

  @Test
  void testMapInvestigations() {
    List<PersonMergeData.Investigation> investigations = mapper.mapInvestigations(INVESTIGATIONS_STRING);
    assertThat(investigations).hasSize(2);
    assertThat(investigations.getFirst().investigationId()).isEqualTo("1");
    assertThat(investigations.getFirst().startedOn()).isEqualTo("2023-06-01T00:00:00Z");
    assertThat(investigations.getFirst().condition()).isEqualTo("Condition A");
    assertThat(investigations.get(1).investigationId()).isEqualTo("2");
    assertThat(investigations.get(1).startedOn()).isEqualTo("2023-07-01T00:00:00Z");
    assertThat(investigations.get(1).condition()).isEqualTo("Condition B");
  }

  @Test
  void testMapInvestigationsEmptyString() {
    final String investigationString = "";
    List<PersonMergeData.Investigation> investigations = mapper.mapInvestigations(investigationString);
    assertThat(investigations).isEmpty();
  }

  @Test
  void testMapInvestigationsNull() {
    List<PersonMergeData.Investigation> investigations = mapper.mapInvestigations(null);
    assertThat(investigations).isEmpty();
  }

  @Test
  void testAsRaceNullRaceMap() {
    PersonMergeData.Race race = mapper.asRace(null);
    assertThat(race).isNull();
  }

  @Test
  void testAsRaceUnknownCategory() {
    Map<String, Object> raceMap = Map.of(
        "personUid", "12345",
        "Id", "1",
        "as_of_date_race", "2023-01-01",
        "race_category_cd", "BAD_CATEGORY" // This category does not exist in RACE_MAP
    );
    PersonMergeData.Race race = mapper.asRace(raceMap);
    assertThat(race).isNull();
  }
}
