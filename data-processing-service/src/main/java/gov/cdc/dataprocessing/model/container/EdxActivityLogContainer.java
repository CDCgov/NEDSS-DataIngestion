package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdxActivityLogContainer {
    private EDXActivityLogDto edxActivityLogDto=new EDXActivityLogDto();
    private EDXActivityDetailLogDto edxActivityDetailLogDto=new EDXActivityDetailLogDto();
}
