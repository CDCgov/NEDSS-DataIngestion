package gov.cdc.dataprocessing.utilities.component;


import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.CaseManagementDT;
import gov.cdc.dataprocessing.model.dto.ConfirmationMethodDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CaseManagementRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.ConfirmationMethodRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component

public class CaseManagementRepositoryUtil {
    private final CaseManagementRepository caseManagementRepository;

    public CaseManagementRepositoryUtil(CaseManagementRepository caseManagementRepository) {
        this.caseManagementRepository = caseManagementRepository;
    }

    public CaseManagementDT getCaseManagementPhc(Long phcUid) {
        Collection<CaseManagementDT> lst = new ArrayList<>();
        var res = caseManagementRepository.findRecordsByPhcUid(phcUid);
        if (res.isEmpty()) {
            return null;
        } else {
            for(var item : res.get()) {
                //NOTE: THIS SHOULD ONLY RETURN A SINGLE DATA NOT A COLLECTION
                CaseManagementDT data = new CaseManagementDT(item);
                return data;
            }
        }

        return new CaseManagementDT();
    }
}
