package gov.cdc.dataprocessing.utilities.component.edx;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxEventProcess;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxEventProcessRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.component.act.ActRepositoryUtil;
import org.springframework.stereotype.Component;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class EdxEventProcessRepositoryUtil {
    private final EdxEventProcessRepository edxEventProcessRepository;
    private final ActRepositoryUtil actRepositoryUtil;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;

    public EdxEventProcessRepositoryUtil(EdxEventProcessRepository edxEventProcessRepository,
                                         ActRepositoryUtil actRepositoryUtil,
                                         IOdseIdGeneratorWCacheService odseIdGeneratorService1) {
        this.edxEventProcessRepository = edxEventProcessRepository;
        this.actRepositoryUtil = actRepositoryUtil;
        this.odseIdGeneratorService = odseIdGeneratorService1;
    }

    public void insertEventProcess(EDXEventProcessDto edxEventProcessDto) throws DataProcessingException {
        var uidObj = odseIdGeneratorService.getValidLocalUid(LocalIdClass.NBS_DOCUMENT, false);
        var uid = uidObj.getClassTypeUid().getSeedValueNbr();

        actRepositoryUtil.insertActivityId(uid, edxEventProcessDto.getDocEventTypeCd(),  NEDSSConstant.EVENT_MOOD_CODE );


        EdxEventProcess data = new EdxEventProcess(edxEventProcessDto);
        data.setNbsEventUid(uid);
        edxEventProcessRepository.save(data);
    }
}
