package gov.cdc.dataprocessing.service.implementation.log;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.model.container.EdxActivityLogContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.EdxActivityDetailLogRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.EdxActivityLogRepository;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.model.EdxActivityLogMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Service
@Slf4j
public class EdxLogService implements IEdxLogService {

    private static final Logger logger = LoggerFactory.getLogger(EdxLogService.class);

    private final EdxActivityLogRepository edxActivityLogRepository;
    private final EdxActivityDetailLogRepository edxActivityDetailLogRepository;
    private final KafkaManagerProducer kafkaManagerProducer;

    private final EdxActivityLogMapper edxActivityLogMapper;

    public EdxLogService(EdxActivityLogRepository edxActivityLogRepository,
                         EdxActivityDetailLogRepository edxActivityDetailLogRepository,
                         KafkaManagerProducer kafkaManagerProducer,
                         EdxActivityLogMapper edxActivityLogMapper) {
        this.edxActivityLogRepository = edxActivityLogRepository;
        this.edxActivityDetailLogRepository = edxActivityDetailLogRepository;
        this.kafkaManagerProducer = kafkaManagerProducer;
        this.edxActivityLogMapper = edxActivityLogMapper;
    }

    public Object processingLog() throws EdxLogException {
        try {
            return "processing log";
        } catch (Exception e) {
            throw new EdxLogException("ERROR", "Data");
        }
    }

    @Transactional
    @Override
    public EdxActivityLog saveEdxActivityLog(EDXActivityLogDto edxActivityLogDto) throws EdxLogException {
        EdxActivityLog edxActivityLog = new EdxActivityLog(edxActivityLogDto);
        EdxActivityLog edxActivityLogResult = edxActivityLogRepository.save(edxActivityLog);
        System.out.println("ActivityLog Id:" + edxActivityLogResult.getId());
        return edxActivityLogResult;
    }

    @Transactional
    @Override
    public EdxActivityDetailLog saveEdxActivityDetailLog(EDXActivityDetailLogDto detailLogDto) throws EdxLogException {
        EdxActivityDetailLog edxActivityDetailLog = new EdxActivityDetailLog(detailLogDto);
        EdxActivityDetailLog edxActivityDetailLogResult = edxActivityDetailLogRepository.save(edxActivityDetailLog);
        System.out.println("ActivityDetailLog Id:" + edxActivityDetailLogResult.getId());
        return edxActivityDetailLogResult;
    }
    @Transactional
    public void saveEdxActivityLogs(String logMessageJson) throws EdxLogException {
        Gson gson = new Gson();
        EDXActivityLogDto edxActivityLogDto = gson.fromJson(logMessageJson, EDXActivityLogDto.class);
        EdxActivityLog edxActivityLog = edxActivityLogMapper.map(edxActivityLogDto);
        EdxActivityLog edxActivityLogResult = edxActivityLogRepository.save(edxActivityLog);
        System.out.println("ActivityLog Id:" + edxActivityLogResult.getId());

//        EdxActivityDetailLog edxActivityDetailLog = new EdxActivityDetailLog(edxActivityLogContainer.getEdxActivityDetailLogDto());
//        edxActivityDetailLog.setEdxActivityLogUid(edxActivityLogResult.getId());
//        EdxActivityDetailLog edxActivityDetailLogResult = edxActivityDetailLogRepository.save(edxActivityDetailLog);
//        System.out.println("ActivityDetailLog Id:" + edxActivityDetailLogResult.getId());
    }

    public void testKafkaproduceLogMessage() {
        EdxActivityLogContainer edxActivityLogContainer = new EdxActivityLogContainer();
        //Activity Log
        EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();
        edxActivityLogDto.setSourceUid(12345678L);
        edxActivityLogDto.setTargetUid(12345L);
        edxActivityLogDto.setTargetUid(4567L);
        edxActivityLogDto.setDocType("test doc type1");
        edxActivityLogDto.setRecordStatusCd("Test status cd1");
        edxActivityLogDto.setRecordStatusTime(getCurrentTimeStamp());
        edxActivityLogDto.setExceptionTxt("test exception1");
        edxActivityLogDto.setImpExpIndCd("I");
        edxActivityLogDto.setSourceTypeCd("INT");
        edxActivityLogDto.setSourceUid(6789L);
        edxActivityLogDto.setTargetTypeCd("LAB");
        edxActivityLogDto.setBusinessObjLocalId("TESTBO1231");
        edxActivityLogDto.setDocName("DOC NAME1");
        edxActivityLogDto.setSrcName("TSTSRCNM1");
        edxActivityLogDto.setAlgorithmAction("TSTALGACT1");
        edxActivityLogDto.setAlgorithmName("TSTALGNM1");
        edxActivityLogDto.setMessageId("TSTMSGID1231");
        edxActivityLogDto.setEntityNm("TEST Entity name1");
        edxActivityLogDto.setAccessionNbr("TST ACC 501");

        //Activity Detail Log
        EDXActivityDetailLogDto detailLogDto = new EDXActivityDetailLogDto();
        //detailLogDto.setEdxActivityDetailLogUid(12345678L);
        //detailLogDto.setEdxActivityLogUid(72276L);
        detailLogDto.setRecordId("RC2341");
        detailLogDto.setRecordType("TEST Record Type1");
        detailLogDto.setRecordName("TEST_Record Name1");
        detailLogDto.setLogType("TEST Log Type1");
        detailLogDto.setComment("TEST Comment text12331");

        edxActivityLogContainer.setEdxActivityLogDto(edxActivityLogDto);
//        edxActivityLogContainer.setEdxActivityDetailLogDto(detailLogDto);

        Gson gson = new Gson();
        String activityLogJsonString = gson.toJson(edxActivityLogContainer);
        System.out.println("--json string to topic edx activity log---:" + activityLogJsonString);
        kafkaManagerProducer.sendDataEdxActivityLog(activityLogJsonString);
    }
}
