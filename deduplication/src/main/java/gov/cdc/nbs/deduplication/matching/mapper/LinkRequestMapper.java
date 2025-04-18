package gov.cdc.nbs.deduplication.matching.mapper;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;

import gov.cdc.nbs.deduplication.matching.exception.MappingException;
import gov.cdc.nbs.deduplication.matching.model.LinkRequest;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.EntityIdDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.PersonNameDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.PostalLocatorDto;
import gov.cdc.nbs.deduplication.matching.model.PersonMatchRequest.TeleLocatorDto;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Address;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Identifier;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Name;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson.Telecom;
import org.springframework.stereotype.Component;

@Component
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

    List<String> race = null;
    if (request.races() != null && !request.races().isEmpty()) {
      race = request.races().stream().map(PersonMatchRequest.PersonRaceDto::raceCategoryCd).toList();
    }

    String sex = null;
    if (request.personDto() != null) {
      sex = request.personDto().currSexCd();
    }

    return new LinkRequest(new MpiPerson(
        null,
        null,
        birthDate,
        sex,
        toAddresses(request.postalLocators()),
        toNames(request.names()),
        toTelecoms(request.teleLocators()),
        race,
        new ArrayList<>()));
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

  List<Identifier> toIdentifiers(List<EntityIdDto> identifications) {
    return Optional.ofNullable(identifications)
        .orElseGet(ArrayList::new)
        .stream()
        .filter(id -> id != null && MpiPerson.Identifier.SUPPORTED_IDENTIFIERS.contains(id.typeCd()))
        .map(id -> new Identifier(id.typeCd(), id.rootExtensionTxt(), id.assigningAuthorityCd()))
        .toList();

  }

}
