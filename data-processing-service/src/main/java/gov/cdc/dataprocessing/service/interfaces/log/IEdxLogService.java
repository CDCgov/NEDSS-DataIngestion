package gov.cdc.dataprocessing.service.interfaces.log;

import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;

public interface IEdxLogService {

    EdxActivityDetailLog saveEdxActivityDetailLog(EDXActivityDetailLogDto detailLogDto) throws EdxLogException;

    void saveEdxActivityLogs(EDXActivityLogDto edxActivityLogDto) throws EdxLogException;

    void updateActivityLogDT(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDto edxLabInformationDto);

    void addActivityDetailLogs(EdxLabInformationDto edxLabInformationDto, String detailedMsg);
    void addActivityDetailLogsForWDS(EdxLabInformationDto edxLabInformationDto, String detailedMsg);
}
