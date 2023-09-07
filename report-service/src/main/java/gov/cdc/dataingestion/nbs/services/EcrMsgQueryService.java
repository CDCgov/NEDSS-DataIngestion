package gov.cdc.dataingestion.nbs.services;

import gov.cdc.dataingestion.nbs.repository.EcrMsgQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcrMsgQueryService {
    private EcrMsgQueryRepository ecrMsgQueryRepository;

    @Autowired
    public EcrMsgQueryService(EcrMsgQueryRepository ecrMsgQueryRepository) {
        this.ecrMsgQueryRepository = ecrMsgQueryRepository;
    }

    public void test() {
        var msgContainer = this.ecrMsgQueryRepository.FetchMsgContainerForApplicableEcr();

        // Do the update LAST

        var a = msgContainer;
    }
}
