package gov.cdc.dataingestion.camel.routes;

import gov.cdc.dataingestion.exception.KafkaProducerException;
import gov.cdc.dataingestion.rawmessage.dto.RawElrDto;
import gov.cdc.dataingestion.rawmessage.service.RawElrService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.when;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class HL7FileProcessComponentTest {
    @Mock
    private RawElrService rawELRService;

    @InjectMocks
    private HL7FileProcessComponent hL7FileProcessComponent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(hL7FileProcessComponent, "phcrImporterVersion", "1");
    }

    @Test
    void testSaveHL7Message() throws KafkaProducerException {
        String hl7Payload = "testmessage";
        String messageType = "HL7";

        RawElrDto rawElrDto = new RawElrDto();
        rawElrDto.setType(messageType);
        rawElrDto.setPayload(hl7Payload);
        rawElrDto.setValidationActive(true);
        rawElrDto.setVersion("1");

        when(rawELRService.submission(rawElrDto)).thenReturn("OK");
        String status = hL7FileProcessComponent.process(hl7Payload);
        Assertions.assertEquals("OK",status);
    }
}