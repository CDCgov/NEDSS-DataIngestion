package gov.cdc.nbsDedup.model.container.model;

import gov.cdc.nbsDedup.model.dto.log.EDXActivityLogDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdxActivityLogContainer {
    private EDXActivityLogDto edxActivityLogDto=new EDXActivityLogDto();
}
