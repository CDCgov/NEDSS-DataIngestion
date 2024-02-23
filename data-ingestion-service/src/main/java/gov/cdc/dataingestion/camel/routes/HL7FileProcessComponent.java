package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HL7FileProcessComponent {
    private static Logger logger = LoggerFactory.getLogger(HL7FileProcessComponent.class);
    String msgType = "HL7";
    private RawELRService rawELRService;

    @Autowired
    public HL7FileProcessComponent(RawELRService rawELRService){
        this.rawELRService=rawELRService;
    }
    @Handler
    public String process(String body) throws Exception {
        String elrId = "";
        String version="1";
        try {
            logger.debug("Calling HL7FileProcessComponent");
            String hl7Str = body;
            logger.debug("HL7 Message:{}", hl7Str);
            if (hl7Str != null && !hl7Str.trim().isEmpty()) {
                RawERLDto rawERLDto = new RawERLDto();
                rawERLDto.setType(msgType);
                rawERLDto.setValidationActive(true);
                rawERLDto.setPayload(hl7Str);
                elrId = rawELRService.submission(rawERLDto,version);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return elrId;
    }
}