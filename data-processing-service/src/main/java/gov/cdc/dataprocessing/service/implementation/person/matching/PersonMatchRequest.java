package gov.cdc.dataprocessing.service.implementation.person.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;

public record PersonMatchRequest(
    PersonDto personDto,
    Collection<PersonNameDto> names,
    Collection<PersonRaceDto> races,
    Collection<PostalLocatorDto> postalLocators,
    Collection<TeleLocatorDto> teleLocators,
    Collection<EntityIdDto> identifications) {
  public PersonMatchRequest(PersonContainer personContainer) {
    this(
        personContainer.getThePersonDto(),
        personContainer.getThePersonNameDtoCollection(),
        personContainer.getThePersonRaceDtoCollection(),
        Optional.of(personContainer.getTheEntityLocatorParticipationDtoCollection())
            .orElseGet(ArrayList::new)
            .stream()
            .map(EntityLocatorParticipationDto::getThePostalLocatorDto)
            .filter(Objects::nonNull)
            .toList(),
        Optional.of(personContainer.getTheEntityLocatorParticipationDtoCollection())
            .orElseGet(ArrayList::new)
            .stream()
            .map(EntityLocatorParticipationDto::getTheTeleLocatorDto)
            .filter(Objects::nonNull)
            .toList(),
        personContainer.getTheEntityIdDtoCollection());

  }
}
