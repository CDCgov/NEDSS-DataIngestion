package gov.cdc.dataingestion.nbs.services.interfaces;

import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import org.springframework.stereotype.Service;

@Service
public interface IEcrMsgQueryService {
    EcrSelectedRecord GetSelectedEcrRecord();
    EcrSelectedRecord getSelectedEcrFromJson();
}
