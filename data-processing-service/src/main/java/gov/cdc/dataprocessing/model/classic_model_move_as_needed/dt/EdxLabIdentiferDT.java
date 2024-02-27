package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dt;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EdxLabIdentiferDT {
    private static final long serialVersionUID = 1L;
    private String identifer;
    private String subMapID;
    private Long observationUid;
    private List<String> observationValues;

}
