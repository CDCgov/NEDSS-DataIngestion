package gov.cdc.dataprocessing.service.implementation.person.matching;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;

class PersonMatchRequestTest {

  @Test
  void mapsPersonContainerDto() {
    Timestamp now = Timestamp.from(Instant.now());
    PersonDto personDto = new PersonDto();
    personDto.setBirthTime(now);
    personDto.setCurrSexCd("currSex");
    personDto.setAdditionalGenderCd("gender");

    PersonContainer container = new PersonContainer();
    container.setThePersonDto(personDto);
    PersonMatchRequest request = new PersonMatchRequest(container);
    assertThat(request).isNotNull();
    assertThat(request.personDto()).isEqualTo(personDto);
  }

  @Test
  void mapsPersonNameDto() {
    PersonNameDto name1 = new PersonNameDto();
    name1.setFirstNm("first1");
    name1.setLastNm("last1");
    name1.setNmSuffix("suffix1");

    PersonNameDto name2 = new PersonNameDto();
    name2.setFirstNm("first2");
    name2.setLastNm("last2");
    name2.setNmSuffix("suffix2");
    List<PersonNameDto> names = List.of(name1, name2);

    PersonContainer container = new PersonContainer();
    container.setThePersonNameDtoCollection(names);
    PersonMatchRequest request = new PersonMatchRequest(container);
    assertThat(request.names()).hasSize(2);
    assertThat(request.names()).isEqualTo(names);
  }

  @Test
  void mapsPersonRaceDto() {
    PersonRaceDto race1 = new PersonRaceDto();
    race1.setRaceCategoryCd("raceCd1");

    PersonRaceDto race2 = new PersonRaceDto();
    race2.setRaceCategoryCd("raceCd2");

    List<PersonRaceDto> races = List.of(race1, race2);

    PersonContainer container = new PersonContainer();
    container.setThePersonRaceDtoCollection(races);
    PersonMatchRequest request = new PersonMatchRequest(container);
    assertThat(request.races()).hasSize(2);
    assertThat(request.races()).isEqualTo(races);
  }

  @Test
  void mapsPersonAddressDto() {
    PostalLocatorDto address1 = new PostalLocatorDto();
    address1.setStreetAddr1("street1");
    address1.setStreetAddr2("street2");
    address1.setCityCd("city");
    address1.setCityDescTxt("cityDesc");
    address1.setStateCd("stateCd");
    address1.setZipCd("zip");

    PostalLocatorDto address2 = new PostalLocatorDto();
    address2.setStreetAddr1("street3");
    address2.setStreetAddr2("street4");
    address2.setCityCd("city2");
    address2.setCityDescTxt("cityDesc2");
    address2.setStateCd("stateCd2");
    address2.setZipCd("zip2");

    TeleLocatorDto phone1 = new TeleLocatorDto();
    phone1.setPhoneNbrTxt("phone1");

    EntityLocatorParticipationDto elp1 = new EntityLocatorParticipationDto();
    elp1.setThePostalLocatorDto(address1);

    EntityLocatorParticipationDto elp2 = new EntityLocatorParticipationDto();
    elp2.setThePostalLocatorDto(address2);

    EntityLocatorParticipationDto elp3 = new EntityLocatorParticipationDto();
    elp3.setTheTeleLocatorDto(phone1);

    EntityLocatorParticipationDto elp4 = new EntityLocatorParticipationDto();
    elp3.setTheTeleLocatorDto(null);

    List<EntityLocatorParticipationDto> postalDtos = List.of(elp1, elp2, elp3, elp4);

    PersonContainer container = new PersonContainer();
    container.setTheEntityLocatorParticipationDtoCollection(postalDtos);

    PersonMatchRequest request = new PersonMatchRequest(container);
    assertThat(request.postalLocators()).hasSize(2);

    PostalLocatorDto dto1 = List.copyOf(request.postalLocators()).get(0);
    assertThat(dto1.getStreetAddr1()).isEqualTo("street1");
    assertThat(dto1.getStreetAddr2()).isEqualTo("street2");
    assertThat(dto1.getCityCd()).isEqualTo("city");
    assertThat(dto1.getCityDescTxt()).isEqualTo("cityDesc");
    assertThat(dto1.getStateCd()).isEqualTo("stateCd");
    assertThat(dto1.getZipCd()).isEqualTo("zip");

    PostalLocatorDto dto2 = List.copyOf(request.postalLocators()).get(1);
    assertThat(dto2.getStreetAddr1()).isEqualTo("street3");
    assertThat(dto2.getStreetAddr2()).isEqualTo("street4");
    assertThat(dto2.getCityCd()).isEqualTo("city2");
    assertThat(dto2.getCityDescTxt()).isEqualTo("cityDesc2");
    assertThat(dto2.getStateCd()).isEqualTo("stateCd2");
    assertThat(dto2.getZipCd()).isEqualTo("zip2");
  }

  @Test
  void mapsPersonTeleDto() {
    TeleLocatorDto phone1 = new TeleLocatorDto();
    phone1.setPhoneNbrTxt("phone1");

    TeleLocatorDto phone2 = new TeleLocatorDto();
    phone2.setPhoneNbrTxt("phone2");

    PostalLocatorDto address1 = new PostalLocatorDto();
    address1.setStreetAddr1("street1");
    address1.setStreetAddr2("street2");
    address1.setCityCd("city");
    address1.setCityDescTxt("cityDesc");
    address1.setStateCd("stateCd");
    address1.setZipCd("zip");

    EntityLocatorParticipationDto elp1 = new EntityLocatorParticipationDto();
    elp1.setTheTeleLocatorDto(phone1);

    EntityLocatorParticipationDto elp2 = new EntityLocatorParticipationDto();
    elp2.setTheTeleLocatorDto(phone2);

    EntityLocatorParticipationDto elp3 = new EntityLocatorParticipationDto();
    elp2.setThePostalLocatorDto(address1);

    List<EntityLocatorParticipationDto> phoneDtos = List.of(elp1, elp2, elp3);

    PersonContainer container = new PersonContainer();
    container.setTheEntityLocatorParticipationDtoCollection(phoneDtos);

    PersonMatchRequest request = new PersonMatchRequest(container);

    assertThat(request.teleLocators()).hasSize(2);
    TeleLocatorDto dto1 = List.copyOf(request.teleLocators()).get(0);
    assertThat(dto1.getPhoneNbrTxt()).isEqualTo("phone1");

    TeleLocatorDto dto2 = List.copyOf(request.teleLocators()).get(1);
    assertThat(dto2.getPhoneNbrTxt()).isEqualTo("phone2");

  }

  @Test
  void mapsEntityIds() {
    EntityIdDto id1 = new EntityIdDto();
    id1.setAssigningAuthorityCd("GA");
    id1.setRootExtensionTxt("value 1");
    id1.setTypeCd("DL");

    EntityIdDto id2 = new EntityIdDto();
    id2.setAssigningAuthorityCd("GA2");
    id2.setRootExtensionTxt("value 2");
    id2.setTypeCd("SS");

    List<EntityIdDto> entityIds = List.of(id1, id2);
    PersonContainer container = new PersonContainer();
    container.setTheEntityIdDtoCollection(entityIds);

    PersonMatchRequest request = new PersonMatchRequest(container);

    assertThat(request.identifications()).hasSize(2);
    EntityIdDto firstId = List.copyOf(request.identifications()).get(0);
    assertThat(firstId.getAssigningAuthorityCd()).isEqualTo("GA");
    assertThat(firstId.getRootExtensionTxt()).isEqualTo("value 1");
    assertThat(firstId.getTypeCd()).isEqualTo("DL");

    EntityIdDto secondId = List.copyOf(request.identifications()).get(1);
    assertThat(secondId.getAssigningAuthorityCd()).isEqualTo("GA2");
    assertThat(secondId.getRootExtensionTxt()).isEqualTo("value 2");
    assertThat(secondId.getTypeCd()).isEqualTo("SS");
  }
}
