package gov.cdc.dataprocessing.service.core;

import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.service.interfaces.IEdxLogService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EdxLogService implements IEdxLogService {
    private static final Logger logger = LoggerFactory.getLogger(EdxLogService.class);

    public EdxLogService() {

    }

    public Object processingLog() throws EdxLogException {
        //TODO: Adding logic here
        try {
            return "processing log";
        } catch (Exception e) {
            throw new EdxLogException("ERROR", "Data");
        }

    }

}
