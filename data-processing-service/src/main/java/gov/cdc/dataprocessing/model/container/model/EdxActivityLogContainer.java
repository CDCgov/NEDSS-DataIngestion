package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdxActivityLogContainer {
    private EDXActivityLogDto edxActivityLogDto=new EDXActivityLogDto();
}
