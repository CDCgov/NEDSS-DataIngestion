package gov.cdc.dataprocessing.model.container.model.dibbs;

import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DibbsRequestBodyDto {
  private PersonDto thePersonDto;
  private List<DibbsPersonNameDto> thePersonNameDtoCollection;
  private List<DibbsEntityLocatorParticipationDto> theEntityLocatorParticipationDtoCollection;

  public DibbsRequestBodyDto(PersonContainer personContainer){
    this.thePersonDto = new PersonDto();
    thePersonDto.setBirthTime(personContainer.getThePersonDto().getBirthTime());
//    thePersonDto.setPersonUid(personContainer.getThePersonDto().getPersonUid());
    this.thePersonNameDtoCollection = new ArrayList<>();
    for(PersonNameDto personNameDto:personContainer.thePersonNameDtoCollection){
      thePersonNameDtoCollection.add(new DibbsPersonNameDto(personNameDto.getFirstNm(),personNameDto.getLastNm()));
    }
    this.theEntityLocatorParticipationDtoCollection = new ArrayList<>();
    for(EntityLocatorParticipationDto entityLocator:personContainer.theEntityLocatorParticipationDtoCollection){
      DibbsPostalLocatorDto postalLocator = new DibbsPostalLocatorDto(entityLocator.getThePostalLocatorDto());
      theEntityLocatorParticipationDtoCollection.add(new DibbsEntityLocatorParticipationDto(postalLocator));
    }
  }
}
