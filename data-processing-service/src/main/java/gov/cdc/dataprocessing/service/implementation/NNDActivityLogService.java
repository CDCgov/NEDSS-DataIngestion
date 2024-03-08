package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NNDActivityLogDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.NNDActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.NNDActivityLogRepository;
import gov.cdc.dataprocessing.service.implementation.core.OdseIdGeneratorService;
import gov.cdc.dataprocessing.service.interfaces.INNDActivityLogService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.NND_METADATA;
import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Service
public class NNDActivityLogService implements INNDActivityLogService {
    private static final Logger logger = LoggerFactory.getLogger(NNDActivityLogService.class);

    private final NNDActivityLogRepository nndActivityLogRepository;
    private final OdseIdGeneratorService odseIdGeneratorService;

    public NNDActivityLogService(NNDActivityLogRepository nndActivityLogRepository,
                                 OdseIdGeneratorService odseIdGeneratorService) {
        this.nndActivityLogRepository = nndActivityLogRepository;
        this.odseIdGeneratorService = odseIdGeneratorService;
    }

    @Transactional
    public void saveNddActivityLog(NNDActivityLogDT nndActivityLogDT) {
        var timeStamp = getCurrentTimeStamp();
        nndActivityLogDT.setNndActivityLogSeq(1);// default to 1
        nndActivityLogDT.setRecordStatusCd("AUTO_RESEND_ERROR");
        nndActivityLogDT.setRecordStatusTime(timeStamp);
        nndActivityLogDT.setStatusCd("E");
        nndActivityLogDT.setStatusTime(timeStamp);
        long uid = 0;

        if(nndActivityLogDT.getNndActivityLogUid() == null) {
            var id = odseIdGeneratorService.getLocalIdAndUpdateSeed(NND_METADATA);
            uid = id.getSeedValueNbr();
        } else {
            uid = nndActivityLogDT.getNndActivityLogUid();
        }

        nndActivityLogDT.setNndActivityLogUid(uid);
        var data = new NNDActivityLog(nndActivityLogDT);
        nndActivityLogRepository.save(data);

    }
}
