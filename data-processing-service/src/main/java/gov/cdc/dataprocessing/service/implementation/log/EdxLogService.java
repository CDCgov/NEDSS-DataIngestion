package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.EdxActivityDetailLogRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.EdxActivityLogRepository;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EdxLogService implements IEdxLogService {
    private static final Logger logger = LoggerFactory.getLogger(EdxLogService.class);

    private final EdxActivityLogRepository edxActivityLogRepository;
    private final EdxActivityDetailLogRepository edxActivityDetailLogRepository;
    public EdxLogService(EdxActivityLogRepository edxActivityLogRepository,
                         EdxActivityDetailLogRepository edxActivityDetailLogRepository) {
        this.edxActivityLogRepository=edxActivityLogRepository;
        this.edxActivityDetailLogRepository=edxActivityDetailLogRepository;
    }

    public Object processingLog() throws EdxLogException {
        try {
            return "processing log";
        } catch (Exception e) {
            throw new EdxLogException("ERROR", "Data");
        }

    }

    @Override
    public void saveEdxActivityLog(EDXActivityLogDto edxActivityLogDto) throws EdxLogException {
        EdxActivityLog edxActivityLog=new EdxActivityLog(edxActivityLogDto);
        EdxActivityLog edxActivityLogResult=edxActivityLogRepository.save(edxActivityLog);
        System.out.println("ActivityLog Id:"+edxActivityLogResult.getId());

    }
    @Override
    public void saveEdxActivityDetailLog(EDXActivityDetailLogDto detailLogDto) throws EdxLogException {
        EdxActivityDetailLog edxActivityDetailLog=new EdxActivityDetailLog(detailLogDto);
        EdxActivityDetailLog edxActivityDetailLogResult=edxActivityDetailLogRepository.save(edxActivityDetailLog);
        System.out.println("ActivityDetailLog Id:"+edxActivityDetailLogResult.getId());
    }
}
