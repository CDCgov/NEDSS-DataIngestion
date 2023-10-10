package gov.cdc.dataingestion.nbs.repository.model.dao;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgTreatmentDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgTreatmentOrganizationDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgTreatmentProviderDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EcrSelectedTreatment {
    private EcrMsgTreatmentDto MsgTreatment;
    private List<EcrMsgTreatmentProviderDto> MsgTreatmentProviders = new ArrayList<>();
    private List<EcrMsgTreatmentOrganizationDto> MsgTreatmentOrganizations = new ArrayList<>();
}
