package gov.cdc.dataprocessing.service.interfaces.log;

import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface IEdxLogService {

    EdxActivityDetailLog saveEdxActivityDetailLog(EDXActivityDetailLogDto detailLogDto) throws EdxLogException;

    void saveEdxActivityLogs(EDXActivityLogDto edxActivityLogDto) throws EdxLogException;

    void updateActivityLogDT(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDto edxLabInformationDto);

    void addActivityDetailLogs(EdxLabInformationDto edxLabInformationDto, String detailedMsg);
    void addActivityDetailLogsForWDS(EdxLabInformationDto edxLabInformationDto, String detailedMsg);
}
