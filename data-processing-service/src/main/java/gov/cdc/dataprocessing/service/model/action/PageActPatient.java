package gov.cdc.dataprocessing.service.model.action;

import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public class PageActPatient {
    Long patientRevisionUid;
    Long mprUid;
    PublicHealthCaseDto phcDT;

}
