package gov.cdc.nbs.deduplication.batch.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gov.cdc.nbs.deduplication.batch.model.PersonMergeData;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Address;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Ethnicity;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.GeneralPatientInformation;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Identification;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Mortality;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Name;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.PhoneEmail;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.Race;
import gov.cdc.nbs.deduplication.batch.model.PersonMergeData.SexAndBirth;

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

  private static final String MORTALITY_STRING = """
      {
      "asOf": "2025-05-27T00:00:00",
      "dateOfDeath": "2025-05-11T00:00:00",
      "deathCity": "Death city",
      "deceased": "Yes",
      "deathState": "Texas",
      "deathCounty": "Anderson County",
      "deathCountry": "Afghanistan"
      }
      """;
  private static final String GENERAL_INFO_STRING = """
        {
        "asOf": "2025-05-27T00:00:00",
        "maritalStatus": "Annulled",
        "mothersMaidenName": "MotherMaiden",
        "numberOfAdultsInResidence": 2,
        "numberOfChildrenInResidence": 0,
        "primaryOccupation": "Mining",
        "educationLevel": "10th grade",
        "primaryLanguage": "Eastern Frisian",
        "speaksEnglish": "Yes",
        "stateHivCaseId": "123"
      }
      """;

  private static final String INVESTIGATION_STRING = """
      [
        {
          "id": "CAS10001000GA01",
          "startDate": "2025-06-05T00:00:00",
          "condition": "2019 Novel Coronavirus"
        },
        {
          "id": "CAS10001001GA01",
          "startDate": null,
          "condition": "Cholera"
        }
      ]
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
    when(rs.getString("mortality")).thenReturn(MORTALITY_STRING);
  }

  private void mockGeneralPatientInformationFields(ResultSet rs) throws SQLException {
    when(rs.getString("general")).thenReturn(GENERAL_INFO_STRING);
  }

  private void mockInvestigationsField(ResultSet rs) throws SQLException {
    when(rs.getString("investigations")).thenReturn(INVESTIGATION_STRING);
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
    assertThat(personMergeData.mortality().asOf()).isEqualTo("2025-05-27T00:00:00");
    assertThat(personMergeData.mortality().deceased()).isEqualTo("Yes");
    assertThat(personMergeData.mortality().dateOfDeath()).isEqualTo("2025-05-11T00:00:00");
    assertThat(personMergeData.mortality().deathCity()).isEqualTo("Death city");
    assertThat(personMergeData.mortality().deathState()).isEqualTo("Texas");
    assertThat(personMergeData.mortality().deathCounty()).isEqualTo("Anderson County");
    assertThat(personMergeData.mortality().deathCountry()).isEqualTo("Afghanistan");
  }

  private void assertGeneralPatientInformation(PersonMergeData personMergeData) {
    assertThat(personMergeData.general().asOf()).isEqualTo("2025-05-27T00:00:00");
    assertThat(personMergeData.general().maritalStatus()).isEqualTo("Annulled");
    assertThat(personMergeData.general().mothersMaidenName()).isEqualTo("MotherMaiden");
    assertThat(personMergeData.general().numberOfAdultsInResidence()).isEqualTo("2");
    assertThat(personMergeData.general().numberOfChildrenInResidence()).isEqualTo("0");
    assertThat(personMergeData.general().primaryOccupation()).isEqualTo("Mining");
    assertThat(personMergeData.general().educationLevel()).isEqualTo("10th grade");
    assertThat(personMergeData.general().primaryLanguage()).isEqualTo("Eastern Frisian");
    assertThat(personMergeData.general().speaksEnglish()).isEqualTo("Yes");
    assertThat(personMergeData.general().stateHivCaseId()).isEqualTo("123");
  }

  private void assertInvestigations(PersonMergeData personMergeData) {
    assertThat(personMergeData.investigations()).hasSize(2);
    assertThat(personMergeData.investigations().getFirst().id()).isEqualTo("CAS10001000GA01");
    assertThat(personMergeData.investigations().getFirst().startDate()).isEqualTo("2025-06-05T00:00:00");
    assertThat(personMergeData.investigations().getFirst().condition()).isEqualTo("2019 Novel Coronavirus");

    assertThat(personMergeData.investigations().get(1).id()).isEqualTo("CAS10001001GA01");
    assertThat(personMergeData.investigations().get(1).startDate()).isNull();
    assertThat(personMergeData.investigations().get(1).condition()).isEqualTo("Cholera");
  }

  private void assertNestedFields(PersonMergeData personMergeData) {
    assertThat(personMergeData.addresses()).hasSize(2);
    assertThat(personMergeData.phoneEmails()).hasSize(2);
    assertThat(personMergeData.names()).hasSize(2);
    assertThat(personMergeData.identifications()).hasSize(1);
    assertThat(personMergeData.races()).hasSize(2);
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
  void testMapSexAndBirthEmpty() {
    String sexAndBirthString = null;
    SexAndBirth sexAndBirth = mapper.mapSexAndBirth(sexAndBirthString);
    assertThat(sexAndBirth.asOf()).isNull();
    assertThat(sexAndBirth.dateOfBirth()).isNull();
    assertThat(sexAndBirth.currentSex()).isNull();
    assertThat(sexAndBirth.sexUnknown()).isNull();
    assertThat(sexAndBirth.transgender()).isNull();
    assertThat(sexAndBirth.additionalGender()).isNull();
    assertThat(sexAndBirth.birthGender()).isNull();
    assertThat(sexAndBirth.multipleBirth()).isNull();
    assertThat(sexAndBirth.birthOrder()).isNull();
    assertThat(sexAndBirth.birthCity()).isNull();
    assertThat(sexAndBirth.birthState()).isNull();
    assertThat(sexAndBirth.birthCounty()).isNull();
  }

  @Test
  void testMapSexAndBirthException() {
    String sexAndBirthString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class, () -> mapper.mapSexAndBirth(sexAndBirthString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient sex and birth");
  }

  @Test
  void testMapMortalityEmpty() {
    String mortalityString = null;
    Mortality mortality = mapper.mapMortality(mortalityString);
    assertThat(mortality.asOf()).isNull();
    assertThat(mortality.deceased()).isNull();
    assertThat(mortality.dateOfDeath()).isNull();
    assertThat(mortality.deathCity()).isNull();
    assertThat(mortality.deathState()).isNull();
    assertThat(mortality.deathCounty()).isNull();
    assertThat(mortality.deathCountry()).isNull();
  }

  @Test
  void testMapMortalityException() {
    String mortalityString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class, () -> mapper.mapMortality(mortalityString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient mortality");
  }

  @Test
  void testMapGeneralEmpty() {
    String generalInfoString = null;
    GeneralPatientInformation generalInfo = mapper.mapGeneralPatientInformation(generalInfoString);
    assertThat(generalInfo.asOf()).isNull();
    assertThat(generalInfo.asOf()).isNull();
    assertThat(generalInfo.maritalStatus()).isNull();
    assertThat(generalInfo.mothersMaidenName()).isNull();
    assertThat(generalInfo.numberOfAdultsInResidence()).isNull();
    assertThat(generalInfo.numberOfChildrenInResidence()).isNull();
    assertThat(generalInfo.primaryOccupation()).isNull();
    assertThat(generalInfo.educationLevel()).isNull();
    assertThat(generalInfo.primaryLanguage()).isNull();
    assertThat(generalInfo.speaksEnglish()).isNull();
    assertThat(generalInfo.stateHivCaseId()).isNull();
  }

  @Test
  void testMapGeneralException() {
    String generalInfoString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class,
        () -> mapper.mapGeneralPatientInformation(generalInfoString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient general information");
  }

  @Test
  void testMapInvestigationsNull() {
    List<PersonMergeData.Investigation> investigations = mapper.mapInvestigations(null);
    assertThat(investigations).isEmpty();
  }

  @Test
  void testMapInvestigationException() {
    String investigationString = "asdf";
    PersonMapException ex = assertThrows(PersonMapException.class,
        () -> mapper.mapInvestigations(investigationString));
    assertThat(ex.getMessage()).isEqualTo("Failed to parse patient investigations");
  }
}
