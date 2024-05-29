package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXEventProcessDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.EdxEventProcess;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.EdxEventProcessRepository;
import gov.cdc.dataprocessing.service.implementation.other.OdseIdGeneratorService;
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

    public void insertEventProcess(EDXEventProcessDT edxEventProcessDT) throws DataProcessingException {
        var uidObj = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.NBS_DOCUMENT);
        var uid = uidObj.getSeedValueNbr();
        var localUid = uidObj.getUidPrefixCd() + uid + uidObj.getUidSuffixCd();

        actRepositoryUtil.insertActivityId(uid, edxEventProcessDT.getDocEventTypeCd(),  NEDSSConstant.EVENT_MOOD_CODE );


        EdxEventProcess data = new EdxEventProcess(edxEventProcessDT);
        data.setNbsEventUid(uid);
        edxEventProcessRepository.save(data);
    }
}
