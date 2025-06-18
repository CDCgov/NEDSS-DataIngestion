package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ConfirmationMethodJdbcRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component

public class ConfirmationMethodRepositoryUtil {

    private final ConfirmationMethodJdbcRepository confirmationMethodJdbcRepository;

    public ConfirmationMethodRepositoryUtil(
                                            ConfirmationMethodJdbcRepository confirmationMethodJdbcRepository) {
        this.confirmationMethodJdbcRepository = confirmationMethodJdbcRepository;
    }

    public Collection<ConfirmationMethodDto> getConfirmationMethodByPhc(Long phcUid) {
        Collection<ConfirmationMethodDto> lst = new ArrayList<>();
        var res = confirmationMethodJdbcRepository.findByPublicHealthCaseUid(phcUid);
        if (res.isEmpty()) {
            return new ArrayList<>();
        } else {
            for(var item : res) {
                ConfirmationMethodDto data = new ConfirmationMethodDto(item);
                lst.add(data);
            }
        }

        return lst;
    }


}
