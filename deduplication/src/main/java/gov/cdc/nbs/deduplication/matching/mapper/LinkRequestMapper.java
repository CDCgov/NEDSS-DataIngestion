package gov.cdc.nbs.deduplication.matching.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

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
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String birthDate = null;
    if (request.personDto().birthTime() != null) {
      birthDate = formatter.format(request.personDto().birthTime());
    }

    String race = null;
    if (!request.races().isEmpty()) {
      race = request.races().get(0).raceCategoryCd();
    }

    return new LinkRequest(new MpiPerson(
        null,
        birthDate,
        request.personDto().currSexCd(),
        null,
        toAddresses(request.postalLocators()),
        toNames(request.names()),
        toTelecoms(request.teleLocators()),
        toSsn(request.identifications()),
        race,
        request.personDto().additionalGenderCd(),
        toDriversLicense(request.identifications())));
  }

  private List<Address> toAddresses(List<PostalLocatorDto> postalLocators) {
    return Optional.of(postalLocators)
        .orElseGet(ArrayList::new)
        .stream()
        .map(pl -> new Address(
            Stream.of(
                pl.streetAddr1(),
                pl.streetAddr2())
                .filter(Objects::nonNull)
                .toList(),
            pl.cityDescTxt(),
            pl.stateCd(),
            pl.zipCd(),
            pl.cntyCd()))
        .toList();
  }

  private List<Name> toNames(List<PersonNameDto> nameDtos) {
    return Optional.of(nameDtos)
        .orElseGet(ArrayList::new)
        .stream()
        .map(n -> new Name(
            Stream.of(
                n.firstNm(),
                n.middleNm())
                .filter(Objects::nonNull)
                .toList(),
            n.lastNm(),
            Stream.of(
                n.nmSuffix())
                .filter(Objects::nonNull)
                .toList()))
        .toList();
  }

  private List<Telecom> toTelecoms(List<TeleLocatorDto> teleLocatorDtos) {
    return Optional.of(teleLocatorDtos)
        .orElseGet(ArrayList::new)
        .stream()
        .map(n -> new Telecom(n.phoneNbrTxt()))
        .toList();
  }

  private DriversLicense toDriversLicense(List<EntityIdDto> identifications) {
    return Optional.of(identifications)
        .orElseGet(ArrayList::new)
        .stream()
        .filter(id -> "DL".equals(id.typeCd()))
        .map(id -> new DriversLicense(id.rootExtensionTxt(), id.assigningAuthorityCd()))
        .findFirst()
        .orElse(null);

  }

  private String toSsn(List<EntityIdDto> identifications) {
    return Optional.of(identifications)
        .orElseGet(ArrayList::new)
        .stream()
        .filter(id -> "SS".equals(id.typeCd()))
        .map(id -> id.rootExtensionTxt())
        .findFirst()
        .orElse(null);

  }

}
