package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.NNDActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.NNDActivityLogRepository;
import gov.cdc.dataprocessing.service.interfaces.log.INNDActivityLogService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.NND_METADATA;
import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Service
public class NNDActivityLogService implements INNDActivityLogService {
    private final NNDActivityLogRepository nndActivityLogRepository;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;

    public NNDActivityLogService(NNDActivityLogRepository nndActivityLogRepository,
                                 IOdseIdGeneratorWCacheService odseIdGeneratorService1) {
        this.nndActivityLogRepository = nndActivityLogRepository;
        this.odseIdGeneratorService = odseIdGeneratorService1;
    }

    @Transactional
    public void saveNddActivityLog(NNDActivityLogDto nndActivityLogDto) throws DataProcessingException {
        var timeStamp = getCurrentTimeStamp();
        nndActivityLogDto.setNndActivityLogSeq(1);// default to 1
        nndActivityLogDto.setRecordStatusCd("AUTO_RESEND_ERROR");
        nndActivityLogDto.setRecordStatusTime(timeStamp);
        nndActivityLogDto.setStatusCd("E");
        nndActivityLogDto.setStatusTime(timeStamp);
        long uid;

        if(nndActivityLogDto.getNndActivityLogUid() == null) {
            var id = odseIdGeneratorService.getValidLocalUid(NND_METADATA, false);
            uid = id.getClassTypeUid().getSeedValueNbr();
        } else {
            uid = nndActivityLogDto.getNndActivityLogUid();
        }

        nndActivityLogDto.setNndActivityLogUid(uid);
        var data = new NNDActivityLog(nndActivityLogDto);
        nndActivityLogRepository.save(data);

    }
}
