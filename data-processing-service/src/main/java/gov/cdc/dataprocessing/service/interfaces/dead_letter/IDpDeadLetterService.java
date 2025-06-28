package gov.cdc.dataprocessing.service.interfaces.dead_letter;

import gov.cdc.dataprocessing.model.dto.dead_letter.RtiDltDto;

import java.util.List;

public interface IDpDeadLetterService {
    List<RtiDltDto> findDltRecords(Long interfaceUid);
    void saveRtiDlt(Exception exception, Long nbsInterfaceUid, String payload, String step, String status, String id);
}
