package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.core.IProgramAreaJurisdictionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProgramAreaJurisdictionService implements IProgramAreaJurisdictionService {
    private static final Logger logger = LoggerFactory.getLogger(ProgramAreaJurisdictionService.class);
    public ProgramAreaJurisdictionService() {

    }

    public Object processingProgramArea() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing program area";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }

    }

    public Object processingJurisdiction() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing jurisdiction";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }

    }
}
