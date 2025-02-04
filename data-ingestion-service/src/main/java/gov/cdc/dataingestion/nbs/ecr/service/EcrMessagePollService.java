package gov.cdc.dataingestion.nbs.ecr.service;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.services.EcrMsgQueryService;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class EcrMessagePollService {

    private EcrMsgQueryService ecrMsgQueryService;
    private ICdaMapper cdaMapper;
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    @Autowired
    public EcrMessagePollService(
            EcrMsgQueryService ecrMsgQueryService,
            ICdaMapper cdaMapper,
            NbsRepositoryServiceProvider nbsRepositoryServiceProvider) {
        this.ecrMsgQueryService = ecrMsgQueryService;
        this.cdaMapper = cdaMapper;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
    }

    @Scheduled(initialDelay = 1000, fixedRate = 3000)
    public void fetchMessageContainerData() throws EcrCdaXmlException {
        List<EcrSelectedRecord> records = ecrMsgQueryService.getSelectedEcrRecord();

        for (EcrSelectedRecord ecr : records) {
            if (ecr != null) {
                String xml = cdaMapper.tranformSelectedEcrToCDAXml(ecr);
                nbsRepositoryServiceProvider.saveEcrCdaXmlMessage(
                        ecr.getMsgContainer().nbsInterfaceUid().toString(),
                        ecr.getMsgContainer().dataMigrationStatus(),
                        xml);
            }
        }
    }

}
