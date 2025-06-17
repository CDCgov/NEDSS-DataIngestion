package gov.cdc.dataprocessing.service.implementation.stored_proc;

import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.StoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.stored_proc.IMsgOutEStoredProcService;
import org.springframework.stereotype.Service;

@Service

public class MsgOutEStoredProcService implements IMsgOutEStoredProcService {

    private final StoredProcRepository storedProcRepository;

    public MsgOutEStoredProcService(StoredProcRepository storedProcRepository) {
        this.storedProcRepository = storedProcRepository;
    }

    public void callUpdateSpecimenCollDateSP(EdxLabInformationDto edxLabInformationDto) {
        storedProcRepository.updateSpecimenCollDateSP(edxLabInformationDto.getNbsInterfaceUid(),
                edxLabInformationDto.getRootObservationContainer().getTheObservationDto().getEffectiveFromTime()
        );
    }
}
