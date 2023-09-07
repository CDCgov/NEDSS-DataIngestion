package gov.cdc.dataingestion.nbs.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface EcrMsgQueryRepository {
     void FetchMsgContainerForApplicableEcr();
}
