package gov.cdc.nbs.mpidatasyncer.service.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.mpidatasyncer.constants.ProcessingQueue;
import gov.cdc.nbs.mpidatasyncer.entity.mpi.MPIPerson;
import gov.cdc.nbs.mpidatasyncer.entity.nbs.Person;
import gov.cdc.nbs.mpidatasyncer.model.MPIPatientDto;
import gov.cdc.nbs.mpidatasyncer.model.PersonBatchDto;
import gov.cdc.nbs.mpidatasyncer.repository.mpi.MpiPersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonBatchProcessorService {

  private final JmsTemplate jmsTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final MpiPersonRepository mpiPersonRepository;

  @JmsListener(destination = ProcessingQueue.PROCESS_QUEUE)
  public void convertAndSend(PersonBatchDto personBatchDto) {
    if (personBatchDto.isNew()) {
      processNewPersons(personBatchDto.batch());
    } else {
      processChangedPersons(personBatchDto.batch());
    }
  }

  private void processNewPersons(List<Person> batch) {
    log.info("Processing new persons: {}", batch.size());
    batch.forEach(person -> {
      MPIPerson mpiPerson= mpiPersonRepository.save(new MPIPerson());
     for(Person patient : person.getChildren())
      {
        String json = convertPersonToJson(patient);
        if (json != null) {
          jmsTemplate.convertAndSend(ProcessingQueue.PATIENT_INSERT_QUEUE,
              new MPIPatientDto(mpiPerson.getId(),person.getPersonUid().toString(),patient.getPersonUid().toString(), json));
        }
      }

    });
  }

  private void processChangedPersons(List<Person> batch) {
    log.info("Processing changed persons: {}", batch.size());
    batch.forEach(person -> {
      String json = convertPersonToJson(person);
      if (json != null) {
        jmsTemplate.convertAndSend(ProcessingQueue.PATIENT_UPDATE_QUEUE,
            new MPIPatientDto(null,null,person.getPersonUid().toString(), json));
      }
    });
  }

  private String convertPersonToJson(Person person) {
    try {
      return objectMapper.writeValueAsString(person);
    } catch (JsonProcessingException e) {
      log.error("Failed to convert person to JSON: {}", person, e);
    }
    return null;
  }
}
