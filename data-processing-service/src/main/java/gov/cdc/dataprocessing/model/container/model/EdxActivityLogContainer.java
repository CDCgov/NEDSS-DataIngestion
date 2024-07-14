package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class EdxActivityLogContainer {
    private EDXActivityLogDto edxActivityLogDto=new EDXActivityLogDto();
}
