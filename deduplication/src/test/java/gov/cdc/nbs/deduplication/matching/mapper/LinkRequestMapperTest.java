package gov.cdc.nbs.deduplication.matching.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.Test;

import gov.cdc.nbs.deduplication.matching.exception.MappingException;
import gov.cdc.nbs.deduplication.matching.model.LinkRequest;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Address;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.DriversLicense;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Name;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Telecom;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.EntityIdDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.PersonDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.PersonNameDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.PersonRaceDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.PostalLocatorDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.TeleLocatorDto;

class LinkRequestMapperTest {

  private LinkRequestMapper mapper = new LinkRequestMapper();

  @Test
  void shouldThrowExceptionIfNull() {
    assertThrows(MappingException.class, () -> mapper.map(null));
  }

  @Test
  void mapsDoBToProperFormat() {
    PersonMatchRequest request = new PersonMatchRequest(
        new PersonDto(new Timestamp(1734118005894L), null, null),
        null,
        null,
        null,
        null,
        null);

    LinkRequest linkRequest = mapper.map(request);

    assertThat(linkRequest.record().birth_date()).isEqualTo("2024-12-13");
  }

  @Test
  void mapsRace() {
    PersonMatchRequest request = new PersonMatchRequest(
        null,
        null,
        List.of(new PersonRaceDto("raceCategoryCd"), new PersonRaceDto("raceCategoryCd2")),
        null,
        null,
        null);

    LinkRequest linkRequest = mapper.map(request);

    assertThat(linkRequest.record().race()).isEqualTo("raceCategoryCd");
  }

  @Test
  void mapsSexAndGender() {
    PersonMatchRequest request = new PersonMatchRequest(
        new PersonDto(null, "sex", "gender"),
        null,
        null,
        null,
        null,
        null);

    LinkRequest linkRequest = mapper.map(request);

    assertThat(linkRequest.record().sex()).isEqualTo("sex");
    assertThat(linkRequest.record().gender()).isEqualTo("gender");
  }

  @Test
  void toAddress() {
    List<Address> addresses = mapper.toAddresses(List.of(
        new PostalLocatorDto(
            "street1 - 1",
            "street1 - 2",
            "city 1",
            "state 1",
            "zip 1",
            "county 1"),
        new PostalLocatorDto(
            "street2 - 1",
            "street2 - 2",
            "city 2",
            "state 2",
            "zip 2",
            "county 2")));
    assertThat(addresses).hasSize(2);
    // First
    assertThat(addresses.get(0).line()).hasSize(2);
    assertThat(addresses.get(0).line().get(0)).isEqualTo("street1 - 1");
    assertThat(addresses.get(0).line().get(1)).isEqualTo("street1 - 2");
    assertThat(addresses.get(0).city()).isEqualTo("city 1");
    assertThat(addresses.get(0).state()).isEqualTo("state 1");
    assertThat(addresses.get(0).postal_code()).isEqualTo("zip 1");
    assertThat(addresses.get(0).county()).isEqualTo("county 1");

    // Second
    assertThat(addresses.get(1).line()).hasSize(2);
    assertThat(addresses.get(1).line().get(0)).isEqualTo("street2 - 1");
    assertThat(addresses.get(1).line().get(1)).isEqualTo("street2 - 2");
    assertThat(addresses.get(1).city()).isEqualTo("city 2");
    assertThat(addresses.get(1).state()).isEqualTo("state 2");
    assertThat(addresses.get(1).postal_code()).isEqualTo("zip 2");
    assertThat(addresses.get(1).county()).isEqualTo("county 2");
  }

  @Test
  void toAddressEmptyString() {
    List<Address> addresses = mapper.toAddresses(List.of(new PostalLocatorDto(
        " ",
        "street1 - 2",
        "city 1",
        "state 1",
        "zip 1",
        "county 1")));
    assertThat(addresses).hasSize(1);
    assertThat(addresses.get(0).line()).hasSize(1);
    assertThat(addresses.get(0).line().get(0)).isEqualTo("street1 - 2");
  }

  @Test
  void toAddressEmpty() {
    List<Address> addresses = mapper.toAddresses(null);
    assertThat(addresses).isEmpty();
  }

  @Test
  void toName() {
    List<Name> names = mapper.toNames(List.of(
        new PersonNameDto(
            "first1",
            "middle1",
            "last1",
            "suffix1"),
        new PersonNameDto(
            "first2",
            "middle2",
            "last2",
            "suffix2")));

    assertThat(names).hasSize(2);
    // First
    assertThat(names.get(0).given()).hasSize(2);
    assertThat(names.get(0).given().get(0)).isEqualTo("first1");
    assertThat(names.get(0).given().get(1)).isEqualTo("middle1");
    assertThat(names.get(0).family()).isEqualTo("last1");
    assertThat(names.get(0).suffix()).hasSize(1);
    assertThat(names.get(0).suffix().get(0)).isEqualTo("suffix1");

    // Second
    assertThat(names.get(1).given()).hasSize(2);
    assertThat(names.get(1).given().get(0)).isEqualTo("first2");
    assertThat(names.get(1).given().get(1)).isEqualTo("middle2");
    assertThat(names.get(1).family()).isEqualTo("last2");
    assertThat(names.get(1).suffix()).hasSize(1);
    assertThat(names.get(1).suffix().get(0)).isEqualTo("suffix2");
  }

  @Test
  void toNameEmptyString() {
    List<Name> names = mapper.toNames(List.of(
        new PersonNameDto(
            " ",
            "middle1",
            "last1",
            "suffix1"),
        new PersonNameDto(
            "first2",
            " ",
            "last2",
            " ")));

    assertThat(names).hasSize(2);
    assertThat(names.get(0).given()).hasSize(1);
    assertThat(names.get(0).given().get(0)).isEqualTo("middle1");
    assertThat(names.get(0).suffix()).hasSize(1);
    assertThat(names.get(0).suffix().get(0)).isEqualTo("suffix1");

    assertThat(names.get(1).given()).hasSize(1);
    assertThat(names.get(1).given().get(0)).isEqualTo("first2");
    assertThat(names.get(1).suffix()).isEmpty();
  }

  @Test
  void toNameEmpty() {
    List<Name> names = mapper.toNames(null);
    assertThat(names).isEmpty();
  }

  @Test
  void toTelecom() {
    List<Telecom> telecoms = mapper.toTelecoms(List.of(
        new TeleLocatorDto("phone1"),
        new TeleLocatorDto("phone2")));

    assertThat(telecoms).hasSize(2);
    assertThat(telecoms.get(0).value()).isEqualTo("phone1");
    assertThat(telecoms.get(1).value()).isEqualTo("phone2");
  }

  @Test
  void toTelecomEmpty() {
    List<Telecom> telecoms = mapper.toTelecoms(null);
    assertThat(telecoms).isEmpty();
  }

  @Test
  void toDriversLicense() {
    DriversLicense driversLicense = mapper.toDriversLicense(List.of(
        new EntityIdDto("SS", "Social Securty Administration", "SSN"),
        new EntityIdDto("DL", "GA", "DriversLicenseNumber")));

    assertThat(driversLicense.authority()).isEqualTo("GA");
    assertThat(driversLicense.value()).isEqualTo("DriversLicenseNumber");
  }

  @Test
  void toDriversLicenseMultiple() {
    DriversLicense driversLicense = mapper.toDriversLicense(List.of(
        new EntityIdDto("DL", "GA", "DriversLicenseNumber"),
        new EntityIdDto("DL", "Social Securty Administration", "SSN")));

    assertThat(driversLicense.authority()).isEqualTo("GA");
    assertThat(driversLicense.value()).isEqualTo("DriversLicenseNumber");
  }

  @Test
  void toDriversLicenseNotFound() {
    DriversLicense driversLicense = mapper.toDriversLicense(List.of(
        new EntityIdDto("SS", "Social Securty Administration", "SSN")));
    assertThat(driversLicense).isNull();
  }

  @Test
  void toDriversLicenseEmpty() {
    DriversLicense driversLicense = mapper.toDriversLicense(null);
    assertThat(driversLicense).isNull();
  }

  @Test
  void toSsn() {
    String ssn = mapper.toSsn(List.of(
        new EntityIdDto("SS", "Social Securty Administration", "SocialSecurityNumber"),
        new EntityIdDto("DL", "GA", "DriversLicenseNumber")));

    assertThat(ssn).isEqualTo("SocialSecurityNumber");
  }

  @Test
  void toSsnMultipl() {
    String ssn = mapper.toSsn(List.of(
        new EntityIdDto("SS", "Social Securty Administration", "SocialSecurityNumber"),
        new EntityIdDto("SS", "Social Securty Administration", "AnotherSSN")));

    assertThat(ssn).isEqualTo("SocialSecurityNumber");
  }

  @Test
  void toSsnNotFound() {
    String ssn = mapper.toSsn(List.of(
        new EntityIdDto("DL", "GA", "DriversLicenseNumber"),
        new EntityIdDto("AN", "GA", "AccountNumber")));

    assertThat(ssn).isNull();
  }

  @Test
  void toSsnNull() {
    String ssn = mapper.toSsn(null);

    assertThat(ssn).isNull();
  }

}
