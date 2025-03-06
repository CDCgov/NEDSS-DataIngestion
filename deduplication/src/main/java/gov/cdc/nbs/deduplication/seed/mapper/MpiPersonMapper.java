package gov.cdc.nbs.deduplication.seed.mapper;

import static java.util.Map.entry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Address;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Identifier;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Name;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Telecom;
import gov.cdc.nbs.deduplication.seed.model.NbsAddress;
import gov.cdc.nbs.deduplication.seed.model.NbsName;

public class MpiPersonMapper implements RowMapper<MpiPerson> {

  private static final Map<String, String> RACE_MAP = Map.ofEntries(
      entry("1002-5", "AMERICAN_INDIAN"),
      entry("2028-9", "ASIAN"),
      entry("2054-5", "BLACK"),
      entry("2076-8", "HAWAIIAN"),
      entry("2106-3", "WHITE"),
      entry("2131-1", "OTHER"),
      entry("U", "UNKNOWN"));

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  @Nullable
  public MpiPerson mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    List<Address> addresses = mapAddresses(rs.getString("address"));

    List<Name> names = mapNames(rs.getString("name"));

    List<Telecom> phones = mapPhones(rs.getString("phone"));

    List<Identifier> identifiers = mapIdentifiers(rs.getString("identifiers"));

    String race = mapRace(rs.getString("race"));

    return new MpiPerson(
        rs.getString("external_id"),
        rs.getString("person_parent_uid"),
        rs.getString("birth_date"),
        rs.getString("sex"),
        addresses,
        names,
        phones,
        race,
        identifiers);
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

  List<Address> mapAddresses(String addressString) {
    return tryParse(
        addressString,
        new TypeReference<List<NbsAddress>>() {
        })
        .orElseGet(() -> new ArrayList<>())
        .stream()
        .map(this::asAddress)
        .filter(Objects::nonNull)
        .toList();
  }

  Address asAddress(NbsAddress address) {
    if (address == null) {
      return null;
    }
    List<String> lines = new ArrayList<>();
    if (address.street() != null) {
      lines.add(address.street());
    }
    if (address.street2() != null) {
      lines.add(address.street2());
    }
    return new Address(
        lines,
        address.city(),
        address.state(),
        address.zip(),
        address.county());
  }

  List<Name> mapNames(String nameString) {
    return tryParse(
        nameString,
        new TypeReference<List<NbsName>>() {
        })
        .orElseGet(() -> new ArrayList<>())
        .stream()
        .map(this::asName)
        .filter(Objects::nonNull)
        .toList();
  }

  Name asName(NbsName nbsName) {
    if (nbsName == null) {
      return null;
    }
    // Family name is required for RL
    if (nbsName.lastNm() == null) {
      return null;
    }
    List<String> givenNames = new ArrayList<>();
    if (nbsName.firstNm() != null) {
      givenNames.add(nbsName.firstNm());
    }
    if (nbsName.middleNm() != null) {
      givenNames.add(nbsName.middleNm());
    }

    List<String> suffixes = new ArrayList<>();
    if (nbsName.nmSuffix() != null) {
      suffixes.add(nbsName.nmSuffix());
    }

    return new Name(givenNames, nbsName.lastNm(), suffixes);
  }

  List<Telecom> mapPhones(String phoneString) {
    return tryParse(
        phoneString,
        new TypeReference<List<Telecom>>() {
        }).orElseGet(() -> new ArrayList<>());
  }

  List<Identifier> mapIdentifiers(String identifierString) {
    return tryParse(
        identifierString,
        new TypeReference<List<Identifier>>() {
        })
        .orElseGet(() -> new ArrayList<>())
        .stream()
        .filter(i -> i != null && MpiPerson.Identifier.SUPPORTED_IDENTIFIERS.contains(i.type()))
        .toList();
  }

  /** Convert Race_cd to acceptable value for Record Linkage */
  String mapRace(String raceString) {
    if (raceString == null || raceString.isBlank()) {
      return null;
    }
    return RACE_MAP.get(raceString);
  }

}
