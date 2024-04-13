package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PublicHealthCaseService implements IPublicHealthCaseService {
    private static final Logger logger = LoggerFactory.getLogger(PublicHealthCaseService.class);
    public PublicHealthCaseService() {

    }

    public Object processingPublicHealthCase() throws DataProcessingConsumerException {
        try {
            return "processing public health case";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }

    }

    public Object processingAutoInvestigation() throws DataProcessingConsumerException {
        try {
            return "processing auto investigation";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }

    }
}
