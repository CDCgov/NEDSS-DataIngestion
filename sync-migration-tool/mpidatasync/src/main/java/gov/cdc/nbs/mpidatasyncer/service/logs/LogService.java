package gov.cdc.nbs.mpidatasyncer.service.logs;

import gov.cdc.nbs.mpidatasyncer.entity.syncer.Log;
import gov.cdc.nbs.mpidatasyncer.enums.LogLevel;
import gov.cdc.nbs.mpidatasyncer.repository.syncer.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

  private final LogRepository logRepository;

  public List<String>  fetchLogs(LocalDateTime startTime, LocalDateTime endTime) {
    AtomicInteger successCount = new AtomicInteger();
    List<String> messages = new ArrayList<>();
    logRepository.findByTimestampBetween(startTime, endTime).forEach(l -> {
      if (LogLevel.SUCCESS.getStatus().equals(l.getLevel())) {
        successCount.getAndIncrement();
      }
      messages.add(l.getTimestamp() + " - " + l.getLevel() + " - " + l.getMessage());
    });
    return messages;
  }

  private void addLog(LogLevel logLevel, String message) {
    logRepository.save(new Log(logLevel, message));
  }

  public void logSuccess(String message) {
    addLog(LogLevel.SUCCESS, message);
    log.info(message);
  }

  public void logError(String message, Exception e) {
    addLog(LogLevel.ERROR, message);
    log.error(message, e);
  }

  public void logWarn(String message) {
    addLog(LogLevel.WARN, message);
    log.warn(message);
  }
  public void logInfo(String message) {
    addLog(LogLevel.INFO, message);
    log.info(message);
  }

}
