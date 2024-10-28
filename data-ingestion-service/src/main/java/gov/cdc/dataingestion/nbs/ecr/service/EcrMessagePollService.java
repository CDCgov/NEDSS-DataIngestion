package gov.cdc.dataingestion.nbs.ecr.service;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.interfaces.ICdaMapper;
import gov.cdc.dataingestion.nbs.services.EcrMsgQueryService;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableScheduling
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class EcrMessagePollService {

    private EcrMsgQueryService ecrMsgQueryService;
    private ICdaMapper cdaMapper;
    private NbsRepositoryServiceProvider nbsRepositoryServiceProvider;

    @Autowired
    public EcrMessagePollService(EcrMsgQueryService ecrMsgQueryService, ICdaMapper cdaMapper, NbsRepositoryServiceProvider nbsRepositoryServiceProvider) {
        this.ecrMsgQueryService = ecrMsgQueryService;
        this.cdaMapper = cdaMapper;
        this.nbsRepositoryServiceProvider = nbsRepositoryServiceProvider;
    }

    @Scheduled(initialDelay = 1000, fixedRate = 3000)
    public void fetchMessageContainerData() throws EcrCdaXmlException {
        var result = ecrMsgQueryService.getSelectedEcrRecord();
        if (result != null) {
            var xmlResult = this.cdaMapper.tranformSelectedEcrToCDAXml(result);
            nbsRepositoryServiceProvider.saveEcrCdaXmlMessage(result.getMsgContainer().getNbsInterfaceUid().toString()
                    , result.getMsgContainer().getDataMigrationStatus(), xmlResult);
        }

    }


}
