package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

class HL7FileProcessComponentTest {
    @Mock
    private RawELRService rawELRService;

    @InjectMocks
    private HL7FileProcessComponent hL7FileProcessComponent;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveHL7Message() throws Exception {
        String hl7Payload = "testmessage";
        String messageType = "HL7";

        RawERLDto rawERLDto = new RawERLDto();
        rawERLDto.setType(messageType);
        rawERLDto.setPayload(hl7Payload);
        rawERLDto.setValidationActive(true);

        when(rawELRService.submission(rawERLDto,"1")).thenReturn("OK");
        String status = hL7FileProcessComponent.process(hl7Payload);
        Assertions.assertEquals("OK",status);
    }
}