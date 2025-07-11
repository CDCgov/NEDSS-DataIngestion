package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.constant.enums.EnumElrDltStatus;
import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.exception.DateValidationException;
import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import gov.cdc.dataingestion.exception.KafkaProducerException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.rawmessage.dto.RawElrDto;
import gov.cdc.dataingestion.rawmessage.service.RawElrService;
import gov.cdc.dataingestion.report.repository.IRawElrRepository;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
@Testcontainers
class ElrDeadLetterServiceTest {
    @Mock
    private IElrDeadLetterRepository dltRepository;

    @Mock
    private IRawElrRepository rawELRRepository;

    @Mock
    private IValidatedELRRepository validatedELRRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ElrDeadLetterService elrDeadLetterService;

    @Mock
    private RawElrService rawElrService;

    @Mock
    private RawElrDto rawElrDto;

    private String guidForTesting = "8DC5E410-4A2E-4018-8C28-A4F6AB99E802";

    @BeforeEach
    void setUpEach() {
        MockitoAnnotations.openMocks(this);
        elrDeadLetterService = new ElrDeadLetterService(dltRepository, rawELRRepository, validatedELRRepository, kafkaProducerService, rawElrService);
    }


    @Test
    void testGetDltRecordByIdSuccess() throws DeadLetterTopicException {
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(guidForTesting);
        model.setErrorMessageSource("elr_raw");
        model.setErrorStackTrace("Sample Error Stack Trace");
        model.setDltOccurrence(1);
        model.setDltStatus("ERROR");
        model.setCreatedBy("system");
        model.setUpdatedBy("system");


        when(dltRepository.findById(anyString())).thenReturn(Optional.of(model));

        ElrDeadLetterDto savedDto = elrDeadLetterService.getDltRecordById(guidForTesting);
        assertEquals(savedDto.getErrorMessageId(), model.getErrorMessageId());
        assertEquals(savedDto.getMessage(), model.getMessage());
    }

    @Test
    void testGetDltRecordById_NoDltRecordFound() {
        var exception = Assertions.assertThrows(DeadLetterTopicException.class, () -> {
            elrDeadLetterService.getDltRecordById(guidForTesting);
        });
        Assertions.assertEquals("The Record does not exist in elr_dlt. Please try with a different ID", exception.getMessage());

    }

    @Test
    void testGetAllErrorDltRecord_Success()  {
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(guidForTesting);
        model.setErrorMessageSource("elr_raw");
        model.setErrorStackTrace("Sample Error Stack Trace");
        model.setDltOccurrence(1);
        model.setDltStatus("ERROR");
        model.setCreatedBy("system");
        model.setUpdatedBy("system");
        List<ElrDeadLetterModel> listData = new ArrayList<>();
        listData.add(model);

        RawElrModel rawModel = new RawElrModel();
        rawModel.setId(guidForTesting);
        rawModel.setPayload("HL7 message");

        when(dltRepository.findAllDltRecordByDltStatus(EnumElrDltStatus.ERROR.name(), Sort.by(Sort.Direction.DESC, "createdOn"))).thenReturn(Optional.of(listData));
        var result = elrDeadLetterService.getAllErrorDltRecord();
        assertEquals(result.get(0).getErrorMessageId(), model.getErrorMessageId());

    }

    @Test
    void testGetAllErrorDltRecord_NoDataFound() {
        var result = elrDeadLetterService.getAllErrorDltRecord();
        assertEquals(0 ,result.size());
    }

    @Test
    void testUpdateAndReprocessingMessage_RawElr_Success() throws DeadLetterTopicException, KafkaProducerException {
        String primaryIdForTesting = guidForTesting;

        ElrDeadLetterModel elrDltModel = new ElrDeadLetterModel();
        elrDltModel.setErrorMessageId(primaryIdForTesting);
        elrDltModel.setDltOccurrence(1);
        elrDltModel.setErrorMessageSource("elr_raw");


        RawElrModel rawElrModel = new RawElrModel();
        rawElrModel.setPayload("HL7 message");
        rawElrModel.setId(elrDltModel.getErrorMessageId());
        elrDltModel.setDltStatus(EnumElrDltStatus.ERROR.name());

        when(dltRepository.findById(elrDltModel.getErrorMessageId()))
                .thenReturn(Optional.of(elrDltModel));
        when(rawELRRepository.findById(elrDltModel.getErrorMessageId()))
                .thenReturn(Optional.of(rawElrModel));
        when(rawELRRepository.save(rawElrModel)).thenReturn(rawElrModel);
        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(elrDltModel);

        var result = elrDeadLetterService.updateAndReprocessingMessage(primaryIdForTesting, "HL7 message");

        assertEquals("HL7 message", result.getMessage());
        assertEquals(1, result.getDltOccurrence());
    }

    @Test
    void testUpdateAndReprocessingMessage_ValidatedElr_Success() throws DeadLetterTopicException, KafkaProducerException {
        String primaryIdForTesting = guidForTesting;

        ElrDeadLetterModel elrDltModel = new ElrDeadLetterModel();
        elrDltModel.setErrorMessageId(primaryIdForTesting);
        elrDltModel.setDltOccurrence(1);
        elrDltModel.setErrorMessageSource("elr_validated");
        elrDltModel.setDltStatus(EnumElrDltStatus.ERROR.name());

        ValidatedELRModel validatedERLModel = new ValidatedELRModel();
        validatedERLModel.setRawMessage("HL7 message validated");
        validatedERLModel.setId(elrDltModel.getErrorMessageId());

        when(dltRepository.findById(elrDltModel.getErrorMessageId()))
                .thenReturn(Optional.of(elrDltModel));

        when(validatedELRRepository.findById(elrDltModel.getErrorMessageId()))
                .thenReturn(Optional.of(validatedERLModel));

        when(validatedELRRepository.save(any(ValidatedELRModel.class))).thenReturn(validatedERLModel);
        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(elrDltModel);

        var result = elrDeadLetterService.updateAndReprocessingMessage(primaryIdForTesting, "HL7 message");

        assertEquals( "HL7 message", result.getMessage());
        assertEquals( 1, result.getDltOccurrence());
    }

    @Test
    void testUpdateAndReprocessingMessage_FhirPrep_Success() throws DeadLetterTopicException, KafkaProducerException {
        String primaryIdForTesting = guidForTesting;

        ElrDeadLetterModel elrDltModel = new ElrDeadLetterModel();
        elrDltModel.setErrorMessageId(primaryIdForTesting);
        elrDltModel.setDltOccurrence(1);
        elrDltModel.setErrorMessageSource("fhir_prep");
        elrDltModel.setDltStatus(EnumElrDltStatus.ERROR.name());

        when(dltRepository.findById(elrDltModel.getErrorMessageId()))
                .thenReturn(Optional.of(elrDltModel));



        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(elrDltModel);

        var result = elrDeadLetterService.updateAndReprocessingMessage(primaryIdForTesting, "HL7 message");

        assertEquals("HL7 message", result.getMessage() );
        assertEquals(1, result.getDltOccurrence());
    }

    @Test
    void testUpdateAndReprocessingMessage_XmlPrep_Success() throws DeadLetterTopicException, KafkaProducerException {
        String primaryIdForTesting = guidForTesting;

        ElrDeadLetterModel elrDltModel = new ElrDeadLetterModel();
        elrDltModel.setErrorMessageId(primaryIdForTesting);
        elrDltModel.setDltOccurrence(1);
        elrDltModel.setErrorMessageSource("xml_prep");
        elrDltModel.setDltStatus(EnumElrDltStatus.ERROR.name());


        when(dltRepository.findById(elrDltModel.getErrorMessageId()))
                .thenReturn(Optional.of(elrDltModel));

        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(elrDltModel);

        var result = elrDeadLetterService.updateAndReprocessingMessage(primaryIdForTesting, "HL7 message");

        assertEquals("HL7 message", result.getMessage());
        assertEquals(1, result.getDltOccurrence());
    }


    @Test
    void testSaveDltRecord() {
        // Arrange
        ElrDeadLetterDto dto = new ElrDeadLetterDto();
        dto.setErrorMessageId("error1");
        dto.setErrorMessageSource("source1");
        dto.setErrorStackTrace("stacktrace1");
        dto.setDltOccurrence(1);
        dto.setDltStatus("status1");
        dto.setCreatedBy("creator1");
        dto.setUpdatedBy("updater1");
        dto.setMessage("test");

        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(dto.getErrorMessageId());
        model.setErrorMessageSource(dto.getErrorMessageSource());
        model.setErrorStackTrace(dto.getErrorStackTrace());
        model.setDltOccurrence(dto.getDltOccurrence());
        model.setDltStatus(dto.getDltStatus());
        model.setCreatedOn(dto.getCreatedOn());
        model.setUpdatedOn(dto.getUpdatedOn());
        model.setCreatedBy(dto.getCreatedBy());
        model.setUpdatedBy(dto.getUpdatedBy());

        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(model);

        // Act
        ElrDeadLetterDto savedDto = elrDeadLetterService.saveDltRecord(dto);

        // Assert
        assertEquals(savedDto.getErrorMessageId(), dto.getErrorMessageId());
        assertEquals(savedDto.getErrorMessageSource(), dto.getErrorMessageSource());
        assertEquals(savedDto.getErrorStackTrace(), dto.getErrorStackTrace());
        assertEquals(savedDto.getDltOccurrence(), dto.getDltOccurrence());
        assertEquals(savedDto.getDltStatus(), dto.getDltStatus());
        assertEquals(savedDto.getCreatedOn(), dto.getCreatedOn());
        assertEquals(savedDto.getUpdatedOn(), dto.getUpdatedOn());
        assertEquals(savedDto.getCreatedBy(), dto.getCreatedBy());
        assertEquals(savedDto.getUpdatedBy(), dto.getUpdatedBy());
    }

    @Test
    void testProcessFailedMessagesFromKafka_WithMessages() throws KafkaProducerException {
        List<ElrDeadLetterModel> dltMessagesList = getElrDeadLetterModels();

        when(dltRepository.getAllErrorDltRecordForKafkaError()).thenReturn(dltMessagesList);

        elrDeadLetterService.processFailedMessagesFromKafka();

        verify(dltRepository, times(2)).updateErrorStatusForRawId(anyString(), anyString());
        verify(rawElrService, times(2)).updateRawMessageAfterRetry(any(RawElrDto.class), anyInt()); //This line is correct.

        verify(dltRepository).updateErrorStatusForRawId("test1", "PROCESSED");
        verify(dltRepository).updateErrorStatusForRawId("test2", "PROCESSED");
    }

    private static @NotNull List<ElrDeadLetterModel> getElrDeadLetterModels() {
        List<ElrDeadLetterModel> dltMessagesList = new ArrayList<>();
        ElrDeadLetterModel message1 = new ElrDeadLetterModel();
        message1.setErrorMessageId("test1");
        message1.setDltStatus("KAFKA_ERROR_HL7");
        message1.setMessage("test_hl7_payload");
        dltMessagesList.add(message1);

        ElrDeadLetterModel message2 = new ElrDeadLetterModel();
        message2.setErrorMessageId("test2");
        message2.setDltStatus("KAFKA_ERROR_HL7_XML");
        message2.setMessage("test_hl7_xml_payload");
        dltMessagesList.add(message2);
        return dltMessagesList;
    }

    @Test
    void testProcessFailedMessagesFromKafka_NoMessages() throws KafkaProducerException {
        when(dltRepository.getAllErrorDltRecordForKafkaError()).thenReturn(new ArrayList<>());

        elrDeadLetterService.processFailedMessagesFromKafka();

        verify(dltRepository, times(0)).updateErrorStatusForRawId(anyString(), anyString());
        verify(rawElrService, times(0)).updateRawMessageAfterRetry(any(RawElrDto.class), anyInt());
    }

    @ParameterizedTest
    @CsvSource({
            "KAFKA_ERROR_HL7, HL7",
            "KAFKA_ERROR_HL7_XML, HL7_XML",
            "SOMEPREFIXKAFKAERROR, ''"
    })
    void testGetElrMessageType(String dltStatus, String expectedResult) {
        String result = elrDeadLetterService.getElrMessageType(dltStatus);
        assertEquals(expectedResult, result);
    }
    @Test
    void testGetDltErrorsByDate_Success() throws DateValidationException {
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(guidForTesting);
        model.setErrorMessageSource("elr_raw");
        model.setErrorStackTrace("Sample Error Stack Trace");
        model.setDltOccurrence(1);
        model.setDltStatus("ERROR");
        model.setCreatedBy("system");
        model.setUpdatedBy("system");
        List<ElrDeadLetterModel> listData = new ArrayList<>();
        listData.add(model);

        RawElrModel rawModel = new RawElrModel();
        rawModel.setId(guidForTesting);
        rawModel.setPayload("HL7 message");

        when(dltRepository.findAllDltRecordsByDate("01-12-2025 00:00:00","01-16-2025 23:59:59")).thenReturn(Optional.of(listData));
        var result = elrDeadLetterService.getDltErrorsByDate("01-12-2025","01-16-2025");
        assertEquals(result.get(0).getErrorMessageId(), model.getErrorMessageId());
    }
    @Test
    void testGetDltErrorsByDate_validationError_for_startDateRange() {
        var exception = Assertions.assertThrows(DateValidationException.class, () -> {
            elrDeadLetterService.getDltErrorsByDate("02-12-2025","01-16-2025");
        });
        assertEquals("The Start date must be earlier than or equal to the End date.", exception.getMessage());
    }
    @Test
    void testGetDltErrorsByDate_validationError_for_invalidDate() {
        var exception = Assertions.assertThrows(DateValidationException.class, () -> {
            elrDeadLetterService.getDltErrorsByDate("02-12-2025","21-16-2025");
        });
        assertTrue(exception.getMessage().contains("Date must be in MM-DD-YYYY format"));
    }
}