package gov.cdc.nbs.deduplication.duplicates.mapper;

import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PersonMergeDataMapperTest {

  private final PersonMergeDataMapper mapper = new PersonMergeDataMapper();
  private static final String COMMENT_DATE = "2023-10-01";
  private static final String COMMENTS = "Admin comments here";
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
               "assigning_authority": "TN"
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
  void testMapRow() throws SQLException {
    ResultSet rs = Mockito.mock(ResultSet.class);

    when(rs.getString("comment_date")).thenReturn(COMMENT_DATE);
    when(rs.getString("admin_comments")).thenReturn(COMMENTS);
    when(rs.getString("address")).thenReturn(ADDRESS_STRING);
    when(rs.getString("phone")).thenReturn(PHONE_STRING);
    when(rs.getString("name")).thenReturn(NAME_STRING);
    when(rs.getString("identifiers")).thenReturn(IDENTIFIER_STRING);
    when(rs.getString("race")).thenReturn(RACE_STRING);

    // Perform mapping
    PersonMergeData personMergeData = mapper.mapRow(rs, 0);

    // Assertions
    assertThat(personMergeData.asOfDate()).isEqualTo(COMMENT_DATE);
    assertThat(personMergeData.comments()).isEqualTo(COMMENTS);
    assertThat(personMergeData.address()).hasSize(2);
    assertThat(personMergeData.telecom()).hasSize(2);
    assertThat(personMergeData.name()).hasSize(2);
    assertThat(personMergeData.identifiers()).hasSize(1);
    assertThat(personMergeData.race()).hasSize(2);

    assertThat(personMergeData.address().getFirst().city()).isEqualTo("Atlanta");
    assertThat(personMergeData.telecom().getFirst().phoneNumber()).isEqualTo("1234567890");
    assertThat(personMergeData.name().getFirst().given().getFirst()).contains("John");
    assertThat(personMergeData.identifiers().getFirst().value()).isEqualTo("1234567");
    assertThat(personMergeData.race().getFirst().category()).isEqualTo("WHITE");
    assertThat(personMergeData.race().get(1).category()).isEqualTo("BLACK");
  }

  @Test
  void testMapAddresses() {
    List<Address> addresses = mapper.mapAddresses(ADDRESS_STRING);
    assertThat(addresses).hasSize(2);
    assertThat(addresses.getFirst().city()).isEqualTo("Atlanta");
    assertThat(addresses.get(1).city()).isEqualTo("Nashville");
  }

  @Test
  void testMapAddressesEmptyString() {
    final String addressString = "";
    List<PersonMergeData.Address> addresses = mapper.mapAddresses(addressString);
    assertThat(addresses).isEmpty();
  }

  @Test
  void testMapAddressesNull() {
    List<PersonMergeData.Address> addresses = mapper.mapAddresses(null);
    assertThat(addresses).isEmpty();
  }

  @Test
  void testAsAddress() {
    Map<String, Object> addressMap = Map.of(
        "address1", "123 Main st",
        "city", "Atlanta",
        "state", "Georgia",
        "zip", "12345"
    );
    Address address = mapper.asAddress(addressMap);
    assertThat(address).isNotNull();
    assertThat(address.city()).isEqualTo("Atlanta");
    assertThat(address.state()).isEqualTo("Georgia");
    assertThat(address.postalCode()).isEqualTo("12345");
  }

  @Test
  void testAsAddressNull() {
    Address address = mapper.asAddress(null);
    assertThat(address).isNull();
  }

  @Test
  void testMapPhones() {
    List<Telecom> phones = mapper.mapPhones(PHONE_STRING);
    assertThat(phones).hasSize(2);
    assertThat(phones.getFirst().phoneNumber()).isEqualTo("1234567890");
    assertThat(phones.get(1).phoneNumber()).isEqualTo("9876543210");
  }

  @Test
  void testMapPhonesEmptyString() {
    final String phoneString = "";
    List<Telecom> phones = mapper.mapPhones(phoneString);
    assertThat(phones).isEmpty();
  }

  @Test
  void testMapPhonesNull() {
    List<Telecom> phones = mapper.mapPhones(null);
    assertThat(phones).isEmpty();
  }

  @Test
  void testAsTelecom() {
    Map<String, Object> phoneMap = Map.of(
        "phone_number", "1234567890",
        "type", "HOME"
    );
    Telecom telecom = mapper.asTelecom(phoneMap);
    assertThat(telecom).isNotNull();
    assertThat(telecom.phoneNumber()).isEqualTo("1234567890");
  }

  @Test
  void testAsTelecomNull() {
    Telecom telecom = mapper.asTelecom(null);
    assertThat(telecom).isNull();
  }

  @Test
  void testMapNames() {
    List<Name> names = mapper.mapNames(NAME_STRING);
    assertThat(names).hasSize(2);
    assertThat(names.getFirst().given().getFirst()).contains("John");
    assertThat(names.get(1).given().getFirst()).contains("Jane");
  }

  @Test
  void testMapNamesEmptyString() {
    final String nameString = "";
    List<Name> names = mapper.mapNames(nameString);
    assertThat(names).isEmpty();
  }

  @Test
  void testMapNamesNull() {
    List<Name> names = mapper.mapNames(null);
    assertThat(names).isEmpty();
  }

  @Test
  void testAsName() {
    Map<String, Object> nameMap = Map.of(
        "first", "John",
        "last", "Doe"
    );
    Name name = mapper.asName(nameMap);
    assertThat(name).isNotNull();
    assertThat(name.given().getFirst()).contains("John");
    assertThat(name.family()).isEqualTo("Doe");
  }

  @Test
  void testAsNameNull() {
    Name name = mapper.asName(null);
    assertThat(name).isNull();
  }

  @Test
  void testMapIdentifiers() {
    List<Identifier> identifiers = mapper.mapIdentifiers(IDENTIFIER_STRING);
    assertThat(identifiers).hasSize(1);
    assertThat(identifiers.getFirst().value()).isEqualTo("1234567");
    assertThat(identifiers.getFirst().asOfDate()).isEqualTo("2023-01-01");
  }

  @Test
  void testMapIdentifiersUnsupportedAuthority() {
    final String identifierString = """
        [
            {"value": "1234567", "authority": "BAD", "type": "DL"}
        ]
        """;
    List<Identifier> identifiers = mapper.mapIdentifiers(identifierString);
    assertThat(identifiers).isEmpty();
  }

  @Test
  void testMapIdentifiersEmptyString() {
    final String identifierString = "";
    List<Identifier> identifiers = mapper.mapIdentifiers(identifierString);
    assertThat(identifiers).isEmpty();
  }

  @Test
  void testMapIdentifiersNull() {
    List<Identifier> identifiers = mapper.mapIdentifiers(null);
    assertThat(identifiers).isEmpty();
  }

  @Test
  void testMapRaces() {
    List<Race> races = mapper.mapRaces(RACE_STRING);
    assertThat(races).hasSize(2);
    assertThat(races.getFirst().category()).isEqualTo("WHITE");
    assertThat(races.get(1).category()).isEqualTo("BLACK");
  }

  @Test
  void testMapRacesUnknownCategory() {
    final String raceString = """
        [
            {"race_category_cd": "UNKNOWN_CATEGORY"}
        ]
        """;
    List<Race> races = mapper.mapRaces(raceString);
    assertThat(races).isEmpty();
  }

  @Test
  void testMapRacesEmptyString() {
    final String raceString = "";
    List<Race> races = mapper.mapRaces(raceString);
    assertThat(races).isEmpty();
  }

  @Test
  void testMapRacesNull() {
    List<PersonMergeData.Race> races = mapper.mapRaces(null);
    assertThat(races).isEmpty();
  }
}
