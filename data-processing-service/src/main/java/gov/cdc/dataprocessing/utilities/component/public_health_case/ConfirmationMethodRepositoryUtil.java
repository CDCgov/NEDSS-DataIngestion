package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.phc.ConfirmationMethodRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
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
            for(var item : res.get()) {
                ConfirmationMethodDto data = new ConfirmationMethodDto(item);
                lst.add(data);
            }
        }

        return lst;
    }


}
