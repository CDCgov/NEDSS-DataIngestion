package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.EdxEventProcessJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxEventProcess;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.utilities.component.act.ActRepositoryUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component

public class EdxEventProcessRepositoryUtil {
    private final EdxEventProcessJdbcRepository edxEventProcessJdbcRepository;
    private final ActRepositoryUtil actRepositoryUtil;
    private final UidPoolManager uidPoolManager;
    public EdxEventProcessRepositoryUtil(
                                         EdxEventProcessJdbcRepository edxEventProcessJdbcRepository,
                                         ActRepositoryUtil actRepositoryUtil,
                                         @Lazy UidPoolManager uidPoolManager) {
        this.edxEventProcessJdbcRepository = edxEventProcessJdbcRepository;
        this.actRepositoryUtil = actRepositoryUtil;
        this.uidPoolManager = uidPoolManager;
    }

    public void insertEventProcess(EDXEventProcessDto edxEventProcessDto) throws DataProcessingException {
        var uidObj = uidPoolManager.getNextUid(LocalIdClass.NBS_DOCUMENT, false);
        var uid = uidObj.getClassTypeUid().getSeedValueNbr();

        actRepositoryUtil.insertActivityId(uid, edxEventProcessDto.getDocEventTypeCd(),  NEDSSConstant.EVENT_MOOD_CODE );


        EdxEventProcess data = new EdxEventProcess(edxEventProcessDto);
        data.setNbsEventUid(uid);
        edxEventProcessJdbcRepository.mergeEdxEventProcess(data);
    }
}
