package gov.cdc.dataprocessing.service.implementation.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.StoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.stored_proc.IMsgOutEStoredProcService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MsgOutEStoredProcService implements IMsgOutEStoredProcService {
    private static final Logger logger = LoggerFactory.getLogger(MsgOutEStoredProcService.class);

    private final StoredProcRepository storedProcRepository;

    public MsgOutEStoredProcService(StoredProcRepository storedProcRepository) {
        this.storedProcRepository = storedProcRepository;
    }

    @Transactional
    public void callUpdateSpecimenCollDateSP(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            storedProcRepository.updateSpecimenCollDateSP(edxLabInformationDto.getNbsInterfaceUid(),
                    edxLabInformationDto.getRootObservationContainer().getTheObservationDto().getEffectiveFromTime()
            );
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }
}
