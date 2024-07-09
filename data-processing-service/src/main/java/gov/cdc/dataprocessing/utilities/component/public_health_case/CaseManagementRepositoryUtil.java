package gov.cdc.dataprocessing.utilities.component.public_health_case;


import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.phc.CaseManagementRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component

public class CaseManagementRepositoryUtil {
    private final CaseManagementRepository caseManagementRepository;

    public CaseManagementRepositoryUtil(CaseManagementRepository caseManagementRepository) {
        this.caseManagementRepository = caseManagementRepository;
    }

    public CaseManagementDto getCaseManagementPhc(Long phcUid) {
        Collection<CaseManagementDto> lst = new ArrayList<>();
        var res = caseManagementRepository.findRecordsByPhcUid(phcUid);
        if (res.isEmpty()) {
            return null;
        } else {
            var item = res.get().stream().findFirst();
            if (item.isPresent()) {
                return new CaseManagementDto(item.get());
            }
        }

        return new CaseManagementDto();
    }
}
