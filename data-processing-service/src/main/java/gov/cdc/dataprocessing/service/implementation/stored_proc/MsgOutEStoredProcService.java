package gov.cdc.dataprocessing.service.implementation.stored_proc;

import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.StoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.stored_proc.IMsgOutEStoredProcService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class MsgOutEStoredProcService implements IMsgOutEStoredProcService {

    private final StoredProcRepository storedProcRepository;

    public MsgOutEStoredProcService(StoredProcRepository storedProcRepository) {
        this.storedProcRepository = storedProcRepository;
    }

    @Transactional
    public void callUpdateSpecimenCollDateSP(EdxLabInformationDto edxLabInformationDto) {
        storedProcRepository.updateSpecimenCollDateSP(edxLabInformationDto.getNbsInterfaceUid(),
                edxLabInformationDto.getRootObservationContainer().getTheObservationDto().getEffectiveFromTime()
        );
    }
}
