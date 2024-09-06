package gov.cdc.nbsDedup.service.model.action;

import gov.cdc.nbsDedup.model.dto.phc.PublicHealthCaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageActPatient {
    Long patientRevisionUid;
    Long mprUid;
    PublicHealthCaseDto phcDT;

}
