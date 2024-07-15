package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.phc.ConfirmationMethodRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ConfirmationMethodRepositoryUtil {

    private final ConfirmationMethodRepository confirmationMethodRepository;

    public ConfirmationMethodRepositoryUtil(ConfirmationMethodRepository confirmationMethodRepository) {
        this.confirmationMethodRepository = confirmationMethodRepository;
    }

    public Collection<ConfirmationMethodDto> getConfirmationMethodByPhc(Long phcUid) {
        Collection<ConfirmationMethodDto> lst = new ArrayList<>();
        var res = confirmationMethodRepository.findRecordsByPhcUid(phcUid);
        if (res.isEmpty()) {
            return new ArrayList<>();
        } else {
            for (var item : res.get()) {
                ConfirmationMethodDto data = new ConfirmationMethodDto(item);
                lst.add(data);
            }
        }

        return lst;
    }


}
