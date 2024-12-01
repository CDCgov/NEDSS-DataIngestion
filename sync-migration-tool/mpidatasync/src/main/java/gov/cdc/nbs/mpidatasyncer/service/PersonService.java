package gov.cdc.nbs.mpidatasyncer.service;

import gov.cdc.nbs.mpidatasyncer.entity.nbs.Person;
import gov.cdc.nbs.mpidatasyncer.repository.nbs.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PersonService {

  private final PersonRepository personRepository;

  public Long findPersonWithMaxPersonUidByAddTimeLessThanEqual(LocalDateTime currentTime){
    Person maxPerson =personRepository.findPersonWithMaxPersonUidByAddTimeLessThanEqual(currentTime);
    Person maxPatient = maxPerson.getChildren().getLast();//max patient id
    return maxPatient.getPersonUid();
  }
}
