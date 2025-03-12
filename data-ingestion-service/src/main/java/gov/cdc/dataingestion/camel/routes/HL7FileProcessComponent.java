package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.rawmessage.dto.RawElrDto;
import gov.cdc.dataingestion.rawmessage.service.RawElrService;
import org.apache.camel.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class HL7FileProcessComponent {
    private static Logger logger = LoggerFactory.getLogger(HL7FileProcessComponent.class);
    String msgType = "HL7";
    private RawElrService rawELRService;

    @Autowired
    public HL7FileProcessComponent(RawElrService rawELRService){
        this.rawELRService=rawELRService;
    }
    @Handler
    public String process(String body) {
        String elrId = "";
        String version="1";
        try {
            logger.debug("Calling HL7FileProcessComponent");
            String hl7Str = body;
            logger.debug("HL7 Message:{}", hl7Str);
            if (hl7Str != null && !hl7Str.trim().isEmpty()) {
                RawElrDto rawElrDto = new RawElrDto();
                rawElrDto.setType(msgType);
                rawElrDto.setValidationActive(true);
                rawElrDto.setPayload(hl7Str);
                rawElrDto.setVersion(version);
                elrId = rawELRService.submission(rawElrDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return elrId;
    }
}