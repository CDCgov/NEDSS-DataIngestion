package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonContainerTest {

    @Test
    void testGettersAndSetters() {
        PersonContainer personContainer = new PersonContainer();

        PersonDto personDto = new PersonDto();
        Collection<PersonNameDto> personNameDtoCollection = new ArrayList<>();
        Collection<PersonRaceDto> personRaceDtoCollection = new ArrayList<>();
        Collection<PersonEthnicGroupDto> personEthnicGroupDtoCollection = new ArrayList<>();
        Collection<EntityLocatorParticipationDto> entityLocatorParticipationDtoCollection = new ArrayList<>();
        Collection<EntityIdDto> entityIdDtoCollection = new ArrayList<>();
        Collection<ParticipationDto> participationDtoCollection = new ArrayList<>();
        Collection<RoleDto> roleDtoCollection = new ArrayList<>();

        personContainer.setThePersonDto(personDto);
        personContainer.setThePersonNameDtoCollection(personNameDtoCollection);
        personContainer.setThePersonRaceDtoCollection(personRaceDtoCollection);
        personContainer.setThePersonEthnicGroupDtoCollection(personEthnicGroupDtoCollection);
        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorParticipationDtoCollection);
        personContainer.setTheEntityIdDtoCollection(entityIdDtoCollection);
        personContainer.setTheParticipationDtoCollection(participationDtoCollection);
        personContainer.setTheRoleDtoCollection(roleDtoCollection);

        personContainer.setDefaultJurisdictionCd("testJurisdiction");
        personContainer.setExt(true);
        personContainer.setMPRUpdateValid(false);
        personContainer.setLocalIdentifier("testLocalIdentifier");
        personContainer.setRole("testRole");
        personContainer.setAddReasonCode("testAddReasonCode");
        personContainer.setPatientMatchedFound(true);

        assertEquals(personDto, personContainer.getThePersonDto());
        assertEquals(personNameDtoCollection, personContainer.getThePersonNameDtoCollection());
        assertEquals(personRaceDtoCollection, personContainer.getThePersonRaceDtoCollection());
        assertEquals(personEthnicGroupDtoCollection, personContainer.getThePersonEthnicGroupDtoCollection());
        assertEquals(entityLocatorParticipationDtoCollection, personContainer.getTheEntityLocatorParticipationDtoCollection());
        assertEquals(entityIdDtoCollection, personContainer.getTheEntityIdDtoCollection());
        assertEquals(participationDtoCollection, personContainer.getTheParticipationDtoCollection());
        assertEquals(roleDtoCollection, personContainer.getTheRoleDtoCollection());

        assertEquals("testJurisdiction", personContainer.getDefaultJurisdictionCd());
        assertEquals(true, personContainer.isExt());
        assertEquals(false, personContainer.isMPRUpdateValid());
        assertEquals("testLocalIdentifier", personContainer.getLocalIdentifier());
        assertEquals("testRole", personContainer.getRole());
        assertEquals("testAddReasonCode", personContainer.getAddReasonCode());
        assertEquals(true, personContainer.getPatientMatchedFound());
    }
}
