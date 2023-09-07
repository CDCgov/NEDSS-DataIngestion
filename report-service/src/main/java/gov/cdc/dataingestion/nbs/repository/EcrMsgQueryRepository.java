package gov.cdc.dataingestion.nbs.repository;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgContainerDto;
import org.springframework.stereotype.Repository;

@Repository
public interface EcrMsgQueryRepository {
     EcrMsgContainerDto FetchMsgContainerForApplicableEcr();
}
