package gov.cdc.dataingestion.rti;

import com.google.gson.Gson;
import gov.cdc.dataingestion.rti.interfaces.IRtiLogService;
import gov.cdc.dataingestion.rti.model.RtiLogStackDto;
import gov.cdc.dataingestion.rti.repository.RtiLogRepository;
import gov.cdc.dataingestion.rti.repository.model.RtiLog;
import org.springframework.stereotype.Service;

import static gov.cdc.dataingestion.share.helper.TimeStampHelper.getCurrentTimeStamp;

@Service
public class RtiLogService implements IRtiLogService {
    private final Gson gson = new Gson();
    private final RtiLogRepository rtiLogRepository;

    public RtiLogService(RtiLogRepository rtiLogRepository) {
        this.rtiLogRepository = rtiLogRepository;
    }

    public void persistingRtiLog(String message) {
        RtiLogStackDto dto = gson.fromJson(message, RtiLogStackDto.class);
        RtiLog log = new RtiLog(dto);
        log.setCreatedOn(getCurrentTimeStamp());
        rtiLogRepository.save(log);
    }
}
