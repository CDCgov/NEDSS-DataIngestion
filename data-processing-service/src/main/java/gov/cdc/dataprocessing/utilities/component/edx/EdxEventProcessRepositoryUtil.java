package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxEventProcess;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxEventProcessRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.OdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.component.act.ActRepositoryUtil;
import org.springframework.stereotype.Component;

@Component
public class EdxEventProcessRepositoryUtil {
    private final EdxEventProcessRepository edxEventProcessRepository;
    private final ActRepositoryUtil actRepositoryUtil;
    private final OdseIdGeneratorService odseIdGeneratorService;

    public EdxEventProcessRepositoryUtil(EdxEventProcessRepository edxEventProcessRepository,
                                         ActRepositoryUtil actRepositoryUtil,
                                         OdseIdGeneratorService odseIdGeneratorService) {
        this.edxEventProcessRepository = edxEventProcessRepository;
        this.actRepositoryUtil = actRepositoryUtil;
        this.odseIdGeneratorService = odseIdGeneratorService;
    }

    public void insertEventProcess(EDXEventProcessDto edxEventProcessDto) throws DataProcessingException {
        var uidObj = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.NBS_DOCUMENT);
        var uid = uidObj.getSeedValueNbr();

        actRepositoryUtil.insertActivityId(uid, edxEventProcessDto.getDocEventTypeCd(), NEDSSConstant.EVENT_MOOD_CODE);


        EdxEventProcess data = new EdxEventProcess(edxEventProcessDto);
        data.setNbsEventUid(uid);
        edxEventProcessRepository.save(data);
    }
}
