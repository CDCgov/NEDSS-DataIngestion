package gov.cdc.dataingestion.rawmessage.controller;

import gov.cdc.dataingestion.custommetrics.CustomMetricsBuilder;
import gov.cdc.dataingestion.exception.KafkaProducerException;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.rawmessage.dto.RawElrDto;
import gov.cdc.dataingestion.rawmessage.service.RawElrService;
import gov.cdc.dataingestion.validation.services.interfaces.IHL7Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static gov.cdc.dataingestion.constant.MessageType.HL7_ELR;
import static gov.cdc.dataingestion.constant.MessageType.XML_ELR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class ElrReportsControllerMockTest {
    @Mock
    private RawElrService rawELRService;

    @Mock
    private CustomMetricsBuilder customMetricsBuilder;

    @Mock
    private IHL7Service hl7Service;

    @InjectMocks
    private ElrReportsController elrReportsController;

    @BeforeEach
    void setUp() {
        // Initializes the mocks and injects them into the controller
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_HL7_ELR_Type() throws KafkaProducerException {
        String payload = "HL7 message";
        String type = HL7_ELR;
        String version = "1";
        String expectedResponse = "Submission successful";

        when(rawELRService.submissionElr(any(RawElrDto.class))).thenReturn(expectedResponse);

        ResponseEntity<String> response = elrReportsController.save(payload, type, version,"");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(customMetricsBuilder).incrementMessagesProcessed();
    }

    @Test
    void testSave_XML_ELR_Type() throws KafkaProducerException {
        String payload = "XML message";
        String type = XML_ELR;
        String version = "1";
        String expectedResponse = "Submission successful";

        when(rawELRService.submissionElrXml(any(RawElrDto.class))).thenReturn(expectedResponse);

        ResponseEntity<String> response = elrReportsController.save(payload, type, version,"");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(customMetricsBuilder).incrementMessagesProcessed();
    }

    @Test
    void testSave_Invalid_Type() {
        String payload = "Invalid message";
        String type = "INVALID_TYPE";
        String version = "1";

        assertThrows(ResponseStatusException.class, () ->
                elrReportsController.save(payload, type, version,""));
    }

    @Test
    void testSave_Missing_Type() {
        String payload = "Message without type";
        String type = ""; // Empty type
        String version = "1";

        assertThrows(ResponseStatusException.class, () ->
                elrReportsController.save(payload, type, version,""));
    }

    @Test
    void testHl7Validator_ValidPayload() throws DiHL7Exception {
        String payload = "Valid HL7 message";
        String expectedResponse = "Valid";

        when(hl7Service.hl7Validator(payload)).thenReturn(expectedResponse);

        ResponseEntity<String> response = elrReportsController.hl7Validator(payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void testHl7Validator_InvalidPayload() throws DiHL7Exception {
        String payload = "Invalid HL7 message";

        when(hl7Service.hl7Validator(payload)).thenThrow(new DiHL7Exception("Invalid HL7 message"));

        assertThrows(DiHL7Exception.class, () -> elrReportsController.hl7Validator(payload));
    }
}
