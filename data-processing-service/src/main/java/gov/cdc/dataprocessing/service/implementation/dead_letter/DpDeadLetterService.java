package gov.cdc.dataprocessing.service.implementation.dead_letter;

import gov.cdc.dataprocessing.config.ServicePropertiesProvider;
import gov.cdc.dataprocessing.model.dto.dead_letter.RtiDltDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.RtiDlt;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.RtiDltJdbcRepository;
import gov.cdc.dataprocessing.service.interfaces.dead_letter.IDpDeadLetterService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static gov.cdc.dataprocessing.utilities.StringUtils.getRootStackTraceAsString;

@Service
public class DpDeadLetterService implements IDpDeadLetterService {
    private final RtiDltJdbcRepository rtiDltJdbcRepository;


    public DpDeadLetterService(
                               RtiDltJdbcRepository rtiDltJdbcRepository) {
        this.rtiDltJdbcRepository = rtiDltJdbcRepository;
    }

    public List<RtiDltDto> findDltRecords(Long interfaceUid) {
        List<RtiDlt> domainList = new ArrayList<>();
        if (interfaceUid == null) {
            domainList = rtiDltJdbcRepository.findByUnSuccessStatus();
        }
        else {
            domainList = rtiDltJdbcRepository.findByNbsInterfaceId(interfaceUid);
        }

        List<RtiDltDto> dtoList = new ArrayList<>();
        for (RtiDlt domain : domainList) {
            RtiDltDto dto = new RtiDltDto(domain);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public void saveRtiDlt(Exception exception, Long nbsInterfaceUid, String payload, String step, String status, String id) {
        RtiDlt rtiDlt = new RtiDlt();
        if (id != null) {
            rtiDlt.setId(id);
        }
        rtiDlt.setNbsInterfaceId(nbsInterfaceUid);
        rtiDlt.setOrigin(step);
        rtiDlt.setStatus(status);
        rtiDlt.setPayload(payload);
        rtiDlt.setStackTrace(getRootStackTraceAsString(exception));
        rtiDltJdbcRepository.upsert(rtiDlt);
    }

}
