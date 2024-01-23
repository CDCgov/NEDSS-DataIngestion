package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.IPatientService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PatientService implements IPatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    public PatientService() {

    }

    public String processingPatient() throws DataProcessingConsumerException {
        //TODO: Adding Logic Here
        try {
            return "processing patient";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }
    }

    public String processingNextOfKin() throws DataProcessingConsumerException {
        //TODO: Adding Logic Here
        try {
            return "processing next of kin";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }
    }

    public String processingProvider() throws DataProcessingConsumerException {
        //TODO: Adding Logic Here
        try {
            return "processing provider";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }
    }

}
