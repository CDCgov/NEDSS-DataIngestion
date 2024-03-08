package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.StoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.core.IMsgOutEStoredProcService;
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

    public void callUpdateSpecimenCollDateSP(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            storedProcRepository.updateSpecimenCollDateSP(edxLabInformationDto.getNbsInterfaceUid(), edxLabInformationDto.getRootObservationVO().getTheObservationDT().getEffectiveFromTime());
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }
}