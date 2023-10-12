package gov.cdc.dataingestion.nbs.repository.model.dao;

import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgOrganizationDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgTreatmentDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgProviderDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EcrSelectedTreatment {
    private EcrMsgTreatmentDto msgTreatment;
    private List<EcrMsgProviderDto> msgTreatmentProviders = new ArrayList<>();
    private List<EcrMsgOrganizationDto> msgTreatmentOrganizations = new ArrayList<>();
}
