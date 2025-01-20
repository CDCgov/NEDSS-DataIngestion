package gov.cdc.nbs.deduplication.matching.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;

import gov.cdc.nbs.deduplication.matching.exception.MappingException;
import gov.cdc.nbs.deduplication.matching.model.LinkRequest;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.EntityIdDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.PersonNameDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.PostalLocatorDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.TeleLocatorDto;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Address;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.DriversLicense;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Name;
import gov.cdc.nbs.deduplication.seed.model.SeedRequest.Telecom;

public class LinkRequestMapper {

  public LinkRequest map(PersonMatchRequest request) {
    if (request == null) {
      throw new MappingException("Match request cannot be null");
    }
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String birthDate = null;
    if (request.personDto() != null && request.personDto().birthTime() != null) {
      birthDate = formatter.format(request.personDto().birthTime());
    }

    String race = null;
    if (request.races() != null && !request.races().isEmpty()) {
      race = request.races().get(0).raceCategoryCd();
    }

    String sex = null;
    if (request.personDto() != null) {
      sex = request.personDto().currSexCd();
    }

    String gender = null;
    if (request.personDto() != null) {
      gender = request.personDto().additionalGenderCd();
    }

    return new LinkRequest(new MpiPerson(
        null,
        null,
        birthDate,
        sex,
        null,
        toAddresses(request.postalLocators()),
        toNames(request.names()),
        toTelecoms(request.teleLocators()),
        toSsn(request.identifications()),
        race,
        gender,
        toDriversLicense(request.identifications())));
  }

  List<Address> toAddresses(List<PostalLocatorDto> postalLocators) {
    return Optional.ofNullable(postalLocators)
        .orElseGet(ArrayList::new)
        .stream()
        .map(pl -> new Address(
            Stream.of(
                    pl.streetAddr1(),
                    pl.streetAddr2())
                .filter(Strings::isNotBlank)
                .toList(),
            pl.cityDescTxt(),
            pl.stateCd(),
            pl.zipCd(),
            pl.cntyCd()))
        .filter(Objects::nonNull)
        .toList();
  }

  List<Name> toNames(List<PersonNameDto> nameDtos) {
    return Optional.ofNullable(nameDtos)
        .orElseGet(ArrayList::new)
        .stream()
        .map(n -> new Name(
            Stream.of(
                    n.firstNm(),
                    n.middleNm())
                .filter(Strings::isNotBlank)
                .toList(),
            n.lastNm(),
            Stream.of(
                    n.nmSuffix())
                .filter(Strings::isNotBlank)
                .toList()))
        .toList();
  }

  List<Telecom> toTelecoms(List<TeleLocatorDto> teleLocatorDtos) {
    return Optional.ofNullable(teleLocatorDtos)
        .orElseGet(ArrayList::new)
        .stream()
        .map(n -> new Telecom(n.phoneNbrTxt()))
        .toList();
  }

  DriversLicense toDriversLicense(List<EntityIdDto> identifications) {
    return Optional.ofNullable(identifications)
        .orElseGet(ArrayList::new)
        .stream()
        .filter(id -> "DL".equals(id.typeCd()))
        .map(id -> new DriversLicense(id.rootExtensionTxt(), id.assigningAuthorityCd()))
        .findFirst()
        .orElse(null);

  }

  String toSsn(List<EntityIdDto> identifications) {
    return Optional.ofNullable(identifications)
        .orElseGet(ArrayList::new)
        .stream()
        .filter(id -> "SS".equals(id.typeCd()))
        .map(id -> id.rootExtensionTxt())
        .findFirst()
        .orElse(null);

  }

}
