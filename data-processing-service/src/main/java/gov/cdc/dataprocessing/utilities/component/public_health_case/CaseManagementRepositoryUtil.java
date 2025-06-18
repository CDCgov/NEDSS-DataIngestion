package gov.cdc.dataprocessing.utilities.component.public_health_case;


import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.CaseManagementJdbcRepository;
import org.springframework.stereotype.Component;

@Component


public class CaseManagementRepositoryUtil {
    private final CaseManagementJdbcRepository caseManagementJdbcRepository;

    public CaseManagementRepositoryUtil(
                                        CaseManagementJdbcRepository caseManagementJdbcRepository) {
        this.caseManagementJdbcRepository = caseManagementJdbcRepository;
    }

    public CaseManagementDto getCaseManagementPhc(Long phcUid) {
        var res = caseManagementJdbcRepository.findByPublicHealthCaseUid(phcUid);
        if (res == null) {
            return null;
        }
        else
        {
            return new CaseManagementDto(res);
        }
    }
}
