package gov.cdc.nbs.mpidatasyncer.service.activemq;


import gov.cdc.nbs.mpidatasyncer.constants.ProcessingQueue;
import gov.cdc.nbs.mpidatasyncer.entity.nbs.Person;
import gov.cdc.nbs.mpidatasyncer.model.LinkerSeedRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonBatchProcessorServiceAPI {

  private final JmsTemplate jmsTemplate;

  @JmsListener(destination = ProcessingQueue.PROCESS_QUEUE_API)
  private void processNewPersonsAPI(List<Person> batch) {
    batch.forEach(person -> {
      List<LinkerSeedRequest.Record> records = new ArrayList<>();
      for (Person patient : person.getChildren()) {
        LinkerSeedRequest.Record patientRecord = personToRecord(patient);
        records.add(patientRecord);
      }
      LinkerSeedRequest.Cluster cluster =
          new LinkerSeedRequest.Cluster(records, person.getPersonUid().toString());
      jmsTemplate.convertAndSend(ProcessingQueue.PATIENT_INSERT_QUEUE_API, Arrays.asList(cluster));
    });
  }


  public LinkerSeedRequest.Record personToRecord(Person person) {
    String externalId = person.getPersonUid().toString();
    String birthDate = person.getBirthTime() != null ? person.getBirthTime() : "";
    String mrn = person.getMedicaidNum() != null ? person.getMedicaidNum() : "";
    String ssn = person.getSsn() != null ? person.getSsn() : "";
    String race = person.getRaceCd() != null ? person.getRaceCd() : "";

    String sex = person.getCurrSexCd() != null ? person.getCurrSexCd() : "";
    String gender = person.getPreferredGenderCd() != null ? person.getPreferredGenderCd() : "";

    List<LinkerSeedRequest.Address> address = new ArrayList<>();
    address.add(new LinkerSeedRequest.Address(
        person.getHmStreetAddr1() != null ? person.getHmStreetAddr1() : ""));


    List<LinkerSeedRequest.Name> name = new ArrayList<>();
    name.add(new LinkerSeedRequest.Name(
        List.of(person.getFirstNm() != null ? person.getFirstNm() : ""),
        person.getLastNm() != null ? person.getLastNm() : ""
    ));


    List<LinkerSeedRequest.Telecom> telecom = new ArrayList<>();
    telecom.add(
        new LinkerSeedRequest.Telecom(person.getHmPhoneNbr() != null ? person.getHmPhoneNbr() : ""));


    LinkerSeedRequest.DriversLicense driversLicense = null;
    driversLicense = new LinkerSeedRequest.DriversLicense(
        person.getDlNum() != null ? person.getDlNum() : "",
        person.getDlStateCd() != null ? person.getDlStateCd() : ""
    );

    return new LinkerSeedRequest.Record(
        externalId,
        birthDate,
        sex,
        mrn,
        address,
        name,
        telecom,
        ssn,
        race,
        gender,
        driversLicense
    );
  }



}
