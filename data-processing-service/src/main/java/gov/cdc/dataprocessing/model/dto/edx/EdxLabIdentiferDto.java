package gov.cdc.dataprocessing.model.dto.edx;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@SuppressWarnings("all")
public class EdxLabIdentiferDto {
    private static final long serialVersionUID = 1L;
    private String identifer;
    private String subMapID;
    private Long observationUid;
    private List<String> observationValues;

}
