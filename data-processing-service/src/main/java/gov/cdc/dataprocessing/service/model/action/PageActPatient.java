package gov.cdc.dataprocessing.service.model.action;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.PublicHealthCaseDT;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageActPatient {
    Long patientRevisionUid;
    Long mprUid;
    PublicHealthCaseDT phcDT;

}
