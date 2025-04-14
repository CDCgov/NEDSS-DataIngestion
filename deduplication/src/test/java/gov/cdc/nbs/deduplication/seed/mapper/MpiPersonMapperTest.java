package gov.cdc.nbs.deduplication.seed.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Address;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Name;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Telecom;
import gov.cdc.nbs.deduplication.seed.model.NbsAddress;
import gov.cdc.nbs.deduplication.seed.model.NbsName;

class MpiPersonMapperTest {

  MpiPersonMapper mapper = new MpiPersonMapper();

  @Test
  void testMap() throws SQLException {
    final String ADDRESS_STRING = """
                [
          {
            "street": "123 Main st",
            "city": "Atlanta",
            "state": "Georgia",
            "zip": "12345",
            "county": "Dekalb County"
          },
          {
            "street": "123 Main st",
            "city": "Atlanta",
            "state": "Georgia",
            "zip": "12345",
            "county": "Dekalb County"
          }
        ]
                """;
    final String NAME_STRING = """
        [{"lastNm":"last","firstNm":"first"},{"lastNm":"last","middleNm":"middle","firstNm":"first","nmSuffix":"ESQ"}]
        """;
    final String PHONE_STRING = """
        [{"value":"1224443"},{"value":"1234567890"}]
        """;
    final String IDENTIFIER_STRING = """
        [{"type":"DL","value":"1234567","authority":"TN"},{"type":"SS","value":"99999999999","authority":"SSA"},{"type":"BAD_TYPE","value":"1234","authority":"BAD"}]
        """;
    final String RACE_STRING = "2106-3";
    final String EXTERNAL_ID = "123";
    final String BIRTH_DATE = "1990-01-01";
    final String SEX = "M";
    ResultSet rs = Mockito.mock(ResultSet.class);
    when(rs.getString("address")).thenReturn(ADDRESS_STRING);
    when(rs.getString("name")).thenReturn(NAME_STRING);
    when(rs.getString("phone")).thenReturn(PHONE_STRING);
    when(rs.getString("identifiers")).thenReturn(IDENTIFIER_STRING);
    when(rs.getString("race")).thenReturn(RACE_STRING);
    when(rs.getString("external_id")).thenReturn(EXTERNAL_ID);
    when(rs.getString("birth_date")).thenReturn(BIRTH_DATE);
    when(rs.getString("sex")).thenReturn(SEX);

    MpiPerson person = mapper.mapRow(rs, 0);
    assertThat(person.address()).hasSize(2);
    assertThat(person.name()).hasSize(2);
    assertThat(person.telecom()).hasSize(2);
    assertThat(person.race().getFirst()).isEqualTo("WHITE");
    assertThat(person.sex()).isEqualTo(SEX);
    assertThat(person.birth_date()).isEqualTo(BIRTH_DATE);

    assertThat(person.identifiers()).hasSize(2);
    assertThat(person.identifiers().get(0).value()).isEqualTo("1234567");
    assertThat(person.identifiers().get(0).authority()).isEqualTo("TN");
    assertThat(person.identifiers().get(0).type()).isEqualTo("DL");

    assertThat(person.identifiers().get(1).value()).isEqualTo("99999999999");
    assertThat(person.identifiers().get(1).authority()).isEqualTo("SSA");
    assertThat(person.identifiers().get(1).type()).isEqualTo("SS");
  }

  @Test
  void testMapAddress() {
    final String ADDRESS_STRING = """
                [
          {
            "street": "123 Main st",
            "city": "Atlanta",
            "state": "Georgia",
            "zip": "12345",
            "county": "Dekalb County"
          },
          {
            "street": "345 Main st",
            "city": "Another City",
            "state": "Tennessee",
            "zip": "22222"
          },
          {
            "street": "123 Main st",
            "street2": "Apt 20",
            "city": "Atlanta",
            "state": "Atlanta",
            "zip": "12345"
          }
        ]
                """;
    List<Address> addresses = mapper.mapAddresses(ADDRESS_STRING);
    assertThat(addresses).hasSize(3);
    assertThat(addresses.get(0).line()).hasSize(1);
    assertThat(addresses.get(0).line().get(0)).isEqualTo("123 Main st");
    assertThat(addresses.get(1).line()).hasSize(1);
    assertThat(addresses.get(1).line().get(0)).isEqualTo("345 Main st");
    assertThat(addresses.get(2).line()).hasSize(2);
    assertThat(addresses.get(2).line().get(0)).isEqualTo("123 Main st");
    assertThat(addresses.get(2).line().get(1)).isEqualTo("Apt 20");
  }

  @Test
  void testMapAddress2() {
    final String ADDRESS_STRING = """
        """;
    List<Address> addresses = mapper.mapAddresses(ADDRESS_STRING);
    assertThat(addresses).isEmpty();
  }

  @Test
  void testMapAddress3() {
    final String ADDRESS_STRING = null;
    List<Address> addresses = mapper.mapAddresses(ADDRESS_STRING);
    assertThat(addresses).isEmpty();
  }

  @Test
  void testAddress() {
    Address address = mapper.asAddress(null);
    assertThat(address).isNull();
  }

  @Test
  void testAddress2() {
    Address address = mapper.asAddress(new NbsAddress(
        "street1",
        "street2",
        "city",
        "state",
        "zip",
        "county"));

    assertThat(address.line()).hasSize(2);
    assertThat(address.line().get(0)).isEqualTo("street1");
    assertThat(address.line().get(1)).isEqualTo("street2");
    assertThat(address.city()).isEqualTo("city");
    assertThat(address.state()).isEqualTo("state");
    assertThat(address.postal_code()).isEqualTo("zip");
    assertThat(address.county()).isEqualTo("county");
  }

  @Test
  void mapNames() {
    final String NAME_STRING = """
        [{"lastNm":"last","firstNm":"first"},{"lastNm":"last","middleNm":"middle","firstNm":"first","nmSuffix":"ESQ"}]
        """;
    List<Name> names = mapper.mapNames(NAME_STRING);
    assertThat(names).hasSize(2);
    assertThat(names.get(0).given()).hasSize(1);
    assertThat(names.get(1).given()).hasSize(2);
  }

  @Test
  void mapNames2() {
    List<Name> names = mapper.mapNames(null);
    assertThat(names).isEmpty();
  }

  @Test
  void mapNames3() {
    final String NAME_STRING = """
        """;
    List<Name> names = mapper.mapNames(NAME_STRING);
    assertThat(names).isEmpty();
  }

  @Test
  void testAsName() {
    Name name = mapper.asName(new NbsName("first", "middle", "last", "suffix"));
    assertThat(name.given()).hasSize(2);
    assertThat(name.given().get(0)).isEqualTo("first");
    assertThat(name.given().get(1)).isEqualTo("middle");
    assertThat(name.family()).isEqualTo("last");
    assertThat(name.suffix()).hasSize(1);
    assertThat(name.suffix().get(0)).isEqualTo("suffix");
  }

  @Test
  void testAsName1() {
    Name name = mapper.asName(null);
    assertThat(name).isNull();
  }

  @Test
  void testAsName2() {
    // family name is required
    Name name = mapper.asName(new NbsName("first", "middle", null, "suffix"));
    assertThat(name).isNull();
  }

  @Test
  void testAsName3() {
    Name name = mapper.asName(new NbsName("first", null, "last", "suffix"));
    assertThat(name.given()).hasSize(1);
    assertThat(name.given().get(0)).isEqualTo("first");
    assertThat(name.family()).isEqualTo("last");
    assertThat(name.suffix()).hasSize(1);
    assertThat(name.suffix().get(0)).isEqualTo("suffix");
  }

  @Test
  void testAsName4() {
    Name name = mapper.asName(new NbsName(null, "middle", "last", "suffix"));
    assertThat(name.given()).hasSize(1);
    assertThat(name.given().get(0)).isEqualTo("middle");
    assertThat(name.family()).isEqualTo("last");
    assertThat(name.suffix()).hasSize(1);
    assertThat(name.suffix().get(0)).isEqualTo("suffix");
  }

  @Test
  void testPhone() {
    final String PHONE_STRING = """
        [{"value":"1224443"},{"value":"1234567890"}]
        """;
    List<Telecom> phones = mapper.mapPhones(PHONE_STRING);
    assertThat(phones).hasSize(2);
    assertThat(phones.get(0).value()).isEqualTo("1224443");
    assertThat(phones.get(1).value()).isEqualTo("1234567890");
  }

  @Test
  void testPhone2() {
    final String PHONE_STRING = """
        """;
    List<Telecom> phones = mapper.mapPhones(PHONE_STRING);
    assertThat(phones).isEmpty();
  }

  @Test
  void testPhone3() {
    final String PHONE_STRING = null;
    List<Telecom> phones = mapper.mapPhones(PHONE_STRING);
    assertThat(phones).isEmpty();
  }

  @ParameterizedTest
  @CsvSource(value = {
      "null,''",
      " ,''",
      "banana,''",
      "1002-5,AMERICAN_INDIAN",
      "2028-9,ASIAN",
      "2054-5,BLACK",
      "2076-8,HAWAIIAN",
      "2106-3,WHITE",
      "2131-1,OTHER",
      "U,UNKNOWN" }, nullValues = { "null" })
  void testRace(String input, String expected) {
    List<String> race = mapper.mapRace(input);

    if (expected.isEmpty()) {
      assertThat(race).isEmpty();
    } else {
      assertThat(race).containsExactly(expected);
    }
  }



}
