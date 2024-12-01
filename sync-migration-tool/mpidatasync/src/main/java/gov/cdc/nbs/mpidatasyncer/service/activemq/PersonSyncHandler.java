package gov.cdc.nbs.mpidatasyncer.service.activemq;


import gov.cdc.nbs.mpidatasyncer.constants.ProcessingQueue;
import gov.cdc.nbs.mpidatasyncer.entity.nbs.Person;
import gov.cdc.nbs.mpidatasyncer.entity.syncer.SyncMetadata;
import gov.cdc.nbs.mpidatasyncer.helper.MigrationHelper;
import gov.cdc.nbs.mpidatasyncer.model.PersonBatchDto;
import gov.cdc.nbs.mpidatasyncer.repository.nbs.PersonRepository;
import gov.cdc.nbs.mpidatasyncer.repository.syncer.SyncMetadataRepository;
import gov.cdc.nbs.mpidatasyncer.service.PersonService;
import gov.cdc.nbs.mpidatasyncer.service.logs.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonSyncHandler {

  private final SyncMetadataRepository syncMetadataRepository;
  private final PersonRepository nbsPersonRepository;
  private final JmsTemplate jmsTemplate;
  private final LogService logService;
  private final MigrationHelper migrationStateHelper;
  private final PersonService personService;


  @Value("${batch.size}")
  private int batchSize;

  @Value("${use.linker.api}")
  private boolean useLinkerApi;


  public void migratePersonData() {
    if (useLinkerApi) {
      migrateToLinkerApi();
    } else {
      migrateToDatabase();
    }
  }

  private void migrateToLinkerApi() {
    logService.logInfo("Started migration to the linker seed API.");
    LocalDateTime currentTime = LocalDateTime.now();
    updateLastSyncTime(currentTime);
    migrationStateHelper.setMaxPersonUid(personService.findPersonWithMaxPersonUidByAddTimeLessThanEqual(currentTime));
    int page = 0;
    Page<Person> batch;
    do {
      batch = nbsPersonRepository.findAllByAddTimeLessThanEqualOrderByPersonUidAsc(currentTime,
          PageRequest.of(page, batchSize));
      if (!batch.isEmpty()) {
        jmsTemplate.convertAndSend(ProcessingQueue.PROCESS_QUEUE_API,
            batch.getContent());
      }
      page++;
    } while (!batch.isEmpty());
  }

  private void migrateToDatabase() {
    logService.logInfo("Started migration to database.");
    LocalDateTime currentTime = LocalDateTime.now();
    updateLastSyncTime(currentTime);
    migrationStateHelper.setMaxPersonUid(personService.findPersonWithMaxPersonUidByAddTimeLessThanEqual(currentTime));
    int page = 0;
    Page<Person> batch;
    do {
      batch = nbsPersonRepository.findAllByAddTimeLessThanEqualOrderByPersonUidAsc(currentTime,
          PageRequest.of(page, batchSize));
      if (!batch.isEmpty()) {
        jmsTemplate.convertAndSend(ProcessingQueue.PROCESS_QUEUE,
            new PersonBatchDto(true, batch.getContent()));
      }
      page++;
    } while (!batch.isEmpty());
  }

  public void syncRecords() {
    LocalDateTime lastSyncTime = getLastSyncTime();
    LocalDateTime currentTime = LocalDateTime.now();
    updateLastSyncTime(currentTime);
    if (lastSyncTime != null) {
      syncNewRecords(lastSyncTime, currentTime);
      syncChangedRecords(lastSyncTime, currentTime);
    } else {
      logService.logWarn("Sync using ActiveMQ can not run without lastSyncTime value.");
    }
  }

  private void syncNewRecords(LocalDateTime lastSyncTime, LocalDateTime currentTime) {
    int page = 0;
    List<Person> batch;
    do {
      batch = nbsPersonRepository.findAllByAddTimeAfterAndAddTimeLessThanEqual(PageRequest.of(page, batchSize),
          lastSyncTime, currentTime);
      log.info("Start sync new person batch");
      if (page == 0 && batch.isEmpty()) {
        log.info("There is no new data to sync");
      }
      if (!batch.isEmpty()) {
        jmsTemplate.convertAndSend(ProcessingQueue.PROCESS_QUEUE, new PersonBatchDto(true, batch));
      }
      log.info("End sync new person batch");
      page++;
    } while (!batch.isEmpty());
  }

  private void syncChangedRecords(LocalDateTime lastSyncTime, LocalDateTime currentTime) {
    int page = 0;
    List<Person> batch;
    do {
      batch = nbsPersonRepository.
          findAllByLastChgTimeAfterAndLastChgTimeLessThanEqualAndAddTimeLessThanEqual(PageRequest.of(page, batchSize),
              lastSyncTime, currentTime, lastSyncTime);
      log.info("Start sync changed person Process");
      if (page == 0 && batch.isEmpty()) {
        log.info("There is no changed data to sync");
      }
      if (!batch.isEmpty()) {
        jmsTemplate.convertAndSend(ProcessingQueue.PROCESS_QUEUE, new PersonBatchDto(false, batch));
      }
      log.info("End sync changed person Process");
      page++;
    } while (!batch.isEmpty());
  }

  private LocalDateTime getLastSyncTime() {
    return syncMetadataRepository.findFirstByOrderByIdAsc()
        .map(SyncMetadata::getLastSyncTime)
        .orElse(null);
  }

  private void updateLastSyncTime(LocalDateTime initialSyncTime) {
    SyncMetadata existingSyncMetadata = syncMetadataRepository.findFirstByOrderByIdAsc().orElse(null);
    if (existingSyncMetadata != null) {
      existingSyncMetadata.setLastSyncTime(initialSyncTime);
      syncMetadataRepository.save(existingSyncMetadata);
    } else {
      syncMetadataRepository.save(new SyncMetadata(initialSyncTime));
    }
  }


}
