package gov.cdc.nbs.dibbs.nbs_deduplication.model;

import lombok.Data;
import java.util.List;

@Data
public class MatchPersonRequest {
  private PersonDto thePersonDto;
  private List<PersonNameDto> thePersonNameDtoCollection;
  private List<EntityLocatorParticipationDto> theEntityLocatorParticipationDtoCollection;
}
