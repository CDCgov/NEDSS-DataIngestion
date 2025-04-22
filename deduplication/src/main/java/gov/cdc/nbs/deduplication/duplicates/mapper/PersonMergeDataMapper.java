package gov.cdc.nbs.deduplication.duplicates.mapper;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData.Address;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData.Identifier;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData.Name;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData.Race;
import gov.cdc.nbs.deduplication.duplicates.model.PersonMergeData.Telecom;



public class PersonMergeDataMapper implements RowMapper<PersonMergeData> {

  private static final Map<String, String> RACE_MAP = Map.ofEntries(
      Map.entry("1002-5", "AMERICAN_INDIAN"),
      Map.entry("2028-9", "ASIAN"),
      Map.entry("2054-5", "BLACK"),
      Map.entry("2076-8", "HAWAIIAN"),
      Map.entry("2106-3", "WHITE"),
      Map.entry("2131-1", "OTHER"),
      Map.entry("U", "UNKNOWN")
  );

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  @Nullable
  public PersonMergeData mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
    String commentDate = rs.getString("comment_date");
    String comments = rs.getString("admin_comments");
    List<Address> addresses = mapAddresses(rs.getString("address"));
    List<Telecom> phones = mapPhones(rs.getString("phone"));
    List<Name> names = mapNames(rs.getString("name"));
    List<Identifier> identifiers = mapIdentifiers(rs.getString("identifiers"));
    List<Race> races = mapRaces(rs.getString("race"));
    return new PersonMergeData(
        commentDate,
        comments,
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

  // Map Addresses
  List<Address> mapAddresses(String addressString) {
    return tryParse(addressString, new TypeReference<List<Map<String, Object>>>() {
    })
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::asAddress)
        .filter(Objects::nonNull)
        .toList();
  }

  Address asAddress(Map<String, Object> addressMap) {
    if (addressMap == null) {
      return null;
    }
    String type = String.valueOf(addressMap.get("code_short_desc_txt"));
    String comments = String.valueOf(addressMap.get("address_comments"));
    String id = String.valueOf(addressMap.get("Id"));
    String asOfDate = String.valueOf(addressMap.get("as_of_date_address"));
    String useCode = String.valueOf(addressMap.get("use_cd"));
    String address1 = String.valueOf(addressMap.get("address1"));
    String address2 = String.valueOf(addressMap.get("address2"));
    String city = String.valueOf(addressMap.get("city"));
    String state = String.valueOf(addressMap.get("state"));
    String postalCode = String.valueOf(addressMap.get("zip"));
    String county = String.valueOf(addressMap.get("county"));
    String censusTract = String.valueOf(addressMap.get("census"));
    String country = String.valueOf(addressMap.get("country"));
    List<String> lines = new ArrayList<>();
    if (address1 != null && !address1.isEmpty()) {
      lines.add(address1);
    }
    if (address2 != null && !address2.isEmpty()) {
      lines.add(address2);
    }
    return new Address(
        id,
        asOfDate,
        useCode,
        lines,
        city,
        state,
        postalCode,
        county,
        censusTract,
        country,
        type,
        comments);
  }

  // Map Phones
  List<Telecom> mapPhones(String phoneString) {
    return tryParse(phoneString, new TypeReference<List<Map<String, Object>>>() {
    })
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::asTelecom)
        .filter(Objects::nonNull)
        .toList();
  }

  Telecom asTelecom(Map<String, Object> phoneMap) {
    if (phoneMap == null) {
      return null;
    }
    String type = String.valueOf(phoneMap.get("Type"));
    String comments = String.valueOf(phoneMap.get("telecom_comments"));
    String id = String.valueOf(phoneMap.get("Id"));
    String asOfDate = String.valueOf(phoneMap.get("as_of_date_telecom"));
    String useCode = String.valueOf(phoneMap.get("use_cd"));
    String countryCode = String.valueOf(phoneMap.get("country_code"));
    String phoneNumber = String.valueOf(phoneMap.get("phone_number"));
    String extension = String.valueOf(phoneMap.get("extension"));
    String email = String.valueOf(phoneMap.get("email"));
    String url = String.valueOf(phoneMap.get("url"));
    return new Telecom(
        id,
        asOfDate,
        useCode,
        countryCode,
        phoneNumber,
        extension,
        email,
        url,
        type,
        comments);
  }

  // Map Names
  List<Name> mapNames(String nameString) {
    return tryParse(nameString, new TypeReference<List<Map<String, Object>>>() {
    })
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::asName)
        .filter(Objects::nonNull)
        .toList();
  }

  Name asName(Map<String, Object> nameMap) {
    if (nameMap == null) {
      return null;
    }
    String personUid = String.valueOf(nameMap.get("personUid"));
    String id = String.valueOf(nameMap.get("Id"));
    String type = String.valueOf(nameMap.get("type"));
    String asOfDate = String.valueOf(nameMap.get("as_of_date_name"));
    String first = String.valueOf(nameMap.get("first"));
    String middle = String.valueOf(nameMap.get("middle"));
    String last = String.valueOf(nameMap.get("last"));
    String secondFamily = String.valueOf(nameMap.get("second_last"));
    String prefix = String.valueOf(nameMap.get("prefix"));
    String suffix = String.valueOf(nameMap.get("suffix"));
    String degree = String.valueOf(nameMap.get("degree"));
    List<String> givenNames = new ArrayList<>();
    if (first != null && !first.isEmpty()) {
      givenNames.add(first);
    }
    if (middle != null && !middle.isEmpty()) {
      givenNames.add(middle);
    }
    return new Name(
        personUid,
        id,
        asOfDate,
        givenNames,
        last,
        secondFamily,
        prefix,
        suffix,
        degree,
        type);
  }

  // Map Identifiers
  List<Identifier> mapIdentifiers(String identifierString) {
    return tryParse(identifierString, new TypeReference<List<Map<String, Object>>>() {
    })
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::asIdentifier)
        .filter(Objects::nonNull)
        .filter(i -> Identifier.SUPPORTED_IDENTIFIERS.contains(i.authority()))
        .toList();
  }

  Identifier asIdentifier(Map<String, Object> identifierMap) {
    if (identifierMap == null) {
      return null;
    }
    String type = String.valueOf(identifierMap.get("type"));
    String id = String.valueOf(identifierMap.get("Id"));
    String asOfDate = String.valueOf(identifierMap.get("as_of_date_identifier"));
    String value = String.valueOf(identifierMap.get("value"));
    String authority = String.valueOf(identifierMap.get("assigning_authority"));
    return new Identifier(
        id,
        asOfDate,
        value,
        authority,
        type);
  }

  // Map Races
  List<Race> mapRaces(String raceString) {
    return tryParse(raceString, new TypeReference<List<Map<String, Object>>>() {
    })
        .orElseGet(Collections::emptyList)
        .stream()
        .map(this::asRace)
        .filter(Objects::nonNull)
        .toList();
  }

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
