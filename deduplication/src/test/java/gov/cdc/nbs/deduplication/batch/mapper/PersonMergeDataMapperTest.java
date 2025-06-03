package gov.cdc.nbs.deduplication.batch.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Address;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Ethnicity;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Identification;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Name;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.PhoneEmail;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Race;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

class PersonMergeDataMapperTest {

  private final PersonMergeDataMapper mapper = new PersonMergeDataMapper();

  // Constants for testing
  private static final String PERSON_UID = "1000001";
  private static final String COMMENT_DATE = "2023-01-10T00:00";
  private static final Timestamp COMMENT_DATE_TIMESTAMP = Timestamp.valueOf("2023-01-10 00:00:00");
  private static final String COMMENTS = "Admin comments here";

  private static final String ETHNICITY_AS_OF_DATE = "2025-05-30T00:00:00";
  private static final String ETHNIC_GROUP_DESC_TXT = "Hispanic or Latino";
  private static final String SPANISH_ORIGIN = "Central American | Cuban";

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
        {
          "id": 10055283,
          "asOf": "2025-05-27T00:00:00",
          "type": "Dormitory",
          "use": "Primary Business",
          "address": "1112 Another address",
          "city": "Atlanta",
          "state": "Georgia",
          "zipcode": "12345"
        },
        {
          "id": 10055283,
          "asOf": "2025-05-13T00:00:00",
          "type": "House",
          "use": "Home",
          "address": "111 Main st",
          "address2": "Block 2",
          "city": "City ",
          "state": "Georgia",
          "zipcode": "11111",
          "county": "Atkinson County",
          "censusTract": "0111",
          "country": "United States",
          "comments": "Address comment 1"
        }
      ]
            """;

  private static final String PHONE_STRING = """
      [
          {"phoneNumber": "1234567890"},
          {"phoneNumber": "9876543210"}
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
            "personUid": "1",
            "sequence": "1",
            "type": "Driver's License",
            "asOf": "2023-01-01",
            "value": "1234567",
            "assigningAuthority": "Test"
           }
       ]
      """;

  private static final String RACE_STRING = """
      [
          {"personUid": "1234", "raceCode": "2106-3", "race": "white", "detailedRaces": "European | Middle Eastern or North African"},
          {"personUid": "1234", "raceCode": "2028-9", "race": "asian", "detailedRaces": null}
      ]
      """;

  private static final String ETHNICITY_STRING = """
      {
      "asOf": "2025-05-30T00:00:00",
      "ethnicity": "Hispanic or Latino",
      "reasonUnknown": null,
      "spanishOrigin": "Central American | Cuban"
      }
      """;

  private static final String SEX_AND_BIRTH_STRING = """
        {
        "asOf": "2025-05-27T00:00:00",
        "dateOfBirth": "2025-05-12T00:00:00",
        "currentSex": "Male",
        "sexUnknown": null,
        "transgender": "Did not ask",
        "additionalGender": "Add Gender",
        "birthGender": "Male",
        "multipleBirth": "No",
        "birthOrder": 1,
        "birthCity": "Birth City",
        "birthState": "Tennessee",
        "birthCounty": "Some County",
        "birthCountry": "United States"
      }
            """;

  @Test
  void testMapRow() throws Exception {
    ResultSet rs = Mockito.mock(ResultSet.class);
    // Mocking
    when(rs.getString("person_parent_uid")).thenReturn(PERSON_UID);
    mockGeneralFields(rs);
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

  private void mockSexAndBirthFields(ResultSet rs) throws SQLException {
    when(rs.getString("sexAndBirth")).thenReturn(SEX_AND_BIRTH_STRING);
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
    when(rs.getString("ethnicity")).thenReturn(ETHNICITY_STRING);
  }

  // Assertion Methods
  private void assertAdminComments(PersonMergeData personMergeData) {
    assertThat(personMergeData.adminComments().date()).isEqualTo(COMMENT_DATE);
    assertThat(personMergeData.adminComments().comment()).isEqualTo(COMMENTS);
  }

  private void assertEthnicity(PersonMergeData personMergeData) {
    assertThat(personMergeData.ethnicity().asOf()).isEqualTo(ETHNICITY_AS_OF_DATE);
    assertThat(personMergeData.ethnicity().ethnicity()).isEqualTo(ETHNIC_GROUP_DESC_TXT);
    assertThat(personMergeData.ethnicity().spanishOrigin()).isEqualTo(SPANISH_ORIGIN);
    assertThat(personMergeData.ethnicity().reasonUnknown()).isNull();
  }

  private void assertSexAndBirth(PersonMergeData personMergeData) {
    assertThat(personMergeData.sexAndBirth().asOf()).isEqualTo("2025-05-27T00:00:00");
    assertThat(personMergeData.sexAndBirth().dateOfBirth()).isEqualTo("2025-05-12T00:00:00");
    assertThat(personMergeData.sexAndBirth().currentSex()).isEqualTo("Male");
    assertThat(personMergeData.sexAndBirth().sexUnknown()).isNull();
    assertThat(personMergeData.sexAndBirth().transgender()).isEqualTo("Did not ask");
    assertThat(personMergeData.sexAndBirth().additionalGender()).isEqualTo("Add Gender");
    assertThat(personMergeData.sexAndBirth().birthGender()).isEqualTo("Male");
    assertThat(personMergeData.sexAndBirth().multipleBirth()).isEqualTo("No");
    assertThat(personMergeData.sexAndBirth().birthOrder()).isEqualTo("1");
    assertThat(personMergeData.sexAndBirth().birthCity()).isEqualTo("Birth City");
    assertThat(personMergeData.sexAndBirth().birthState()).isEqualTo("Tennessee");
    assertThat(personMergeData.sexAndBirth().birthCounty()).isEqualTo("Some County");
    assertThat(personMergeData.sexAndBirth().birthCountry()).isEqualTo("United States");

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
    assertThat(personMergeData.addresses()).hasSize(2);
    assertThat(personMergeData.phoneEmails()).hasSize(2);
    assertThat(personMergeData.names()).hasSize(2);
    assertThat(personMergeData.identifications()).hasSize(1);
    assertThat(personMergeData.races()).hasSize(2);
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
  void testMapNamesEmpty() {
    String nameString = null;
    List<Name> names = mapper.mapNames(nameString);
    assertThat(names).isEmpty();
  }

  @Test
  void testMapNamesException() {
    String nameString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class, () -> mapper.mapNames(nameString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient names");
  }

  @Test
  void testMapAddressEmpty() {
    String addressString = null;
    List<Address> addresses = mapper.mapAddresses(addressString);
    assertThat(addresses).isEmpty();
  }

  @Test
  void testMapAddressException() {
    String addressString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class, () -> mapper.mapAddresses(addressString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient addresses");
  }

  @Test
  void testMapPhonesEmpty() {
    String phoneString = null;
    List<PhoneEmail> phoneEmails = mapper.mapPhones(phoneString);
    assertThat(phoneEmails).isEmpty();
  }

  @Test
  void testMapPhonesException() {
    String phoneString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class, () -> mapper.mapPhones(phoneString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient phone and email");
  }

  @Test
  void testMapIdentificationsEmpty() {
    String identificationString = null;
    List<Identification> identifications = mapper.mapIdentifiers(identificationString);
    assertThat(identifications).isEmpty();
  }

  @Test
  void testMapIdentificationsException() {
    String identificationString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class, () -> mapper.mapIdentifiers(identificationString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient identification");
  }

  @Test
  void testMapRaceEmpty() {
    String raceString = null;
    List<Race> races = mapper.mapRaces(raceString);
    assertThat(races).isEmpty();
  }

  @Test
  void testMapRaceException() {
    String raceString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class, () -> mapper.mapRaces(raceString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient race");
  }

  @Test
  void testMapEthnicityEmpty() {
    String ethnicityString = null;
    Ethnicity ethnicity = mapper.mapEthnicity(ethnicityString);
    assertThat(ethnicity.asOf()).isNull();
    assertThat(ethnicity.ethnicity()).isNull();
    assertThat(ethnicity.reasonUnknown()).isNull();
    assertThat(ethnicity.spanishOrigin()).isNull();
  }

  @Test
  void testMapEthnicityException() {
    String ethnicityString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class, () -> mapper.mapEthnicity(ethnicityString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient ethnicity");
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
}
