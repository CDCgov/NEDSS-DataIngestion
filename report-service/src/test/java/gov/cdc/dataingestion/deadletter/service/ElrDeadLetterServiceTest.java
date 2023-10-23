package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.constant.enums.EnumElrDltStatus;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Testcontainers
class ElrDeadLetterServiceTest {
    @Mock
    private IElrDeadLetterRepository dltRepository;

    @Mock
    private IRawELRRepository rawELRRepository;

    @Mock
    private IValidatedELRRepository validatedELRRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private IHL7ToFHIRRepository fhirRepository;

    @InjectMocks
    private ElrDeadLetterService elrDeadLetterService;

    private String guidForTesting = "8DC5E410-4A2E-4018-8C28-A4F6AB99E802";

    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
        elrDeadLetterService = new ElrDeadLetterService(dltRepository, rawELRRepository, validatedELRRepository, kafkaProducerService, fhirRepository);
    }


    @AfterAll
    public static void tearDown() {

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
        Assertions.assertEquals("The Record Is Not Existing in Dead Letter Topic. Please Try With The Different Id.", exception.getMessage());

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

        RawERLModel rawModel = new RawERLModel();
        rawModel.setId(guidForTesting);
        rawModel.setPayload("HL7 message");

        when(dltRepository.findAllDltRecordByDltStatus(eq(EnumElrDltStatus.ERROR.name()), eq(Sort.by(Sort.Direction.DESC, "createdOn")))).thenReturn(Optional.of(listData));
        var result = elrDeadLetterService.getAllErrorDltRecord();
        assertEquals(result.get(0).getErrorMessageId(), model.getErrorMessageId());

    }

    @Test
    void testGetAllErrorDltRecord_NoDataFound() {
        var result = elrDeadLetterService.getAllErrorDltRecord();
        assertEquals(result.size(), 0);
    }

    @Test
    void testUpdateAndReprocessingMessage_RawElr_Success() throws DeadLetterTopicException {
        String primaryIdForTesting = guidForTesting;

        ElrDeadLetterModel elrDltModel = new ElrDeadLetterModel();
        elrDltModel.setErrorMessageId(primaryIdForTesting);
        elrDltModel.setDltOccurrence(1);
        elrDltModel.setErrorMessageSource("elr_raw");


        RawERLModel rawERLModel = new RawERLModel();
        rawERLModel.setPayload("HL7 message");
        rawERLModel.setId(elrDltModel.getErrorMessageId());


        when(dltRepository.findById(eq(elrDltModel.getErrorMessageId())))
                .thenReturn(Optional.of(elrDltModel));
        when(rawELRRepository.findById(eq(elrDltModel.getErrorMessageId())))
                .thenReturn(Optional.of(rawERLModel));
        when(rawELRRepository.save(eq(rawERLModel))).thenReturn(rawERLModel);
        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(elrDltModel);

        var result = elrDeadLetterService.updateAndReprocessingMessage(primaryIdForTesting, "HL7 message");

        assertEquals(result.getMessage(), "HL7 message");
        assertEquals(result.getDltOccurrence(), 1);





    }

    @Test
    void testUpdateAndReprocessingMessage_ValidatedElr_Success() throws DeadLetterTopicException {
        String primaryIdForTesting = guidForTesting;

        ElrDeadLetterModel elrDltModel = new ElrDeadLetterModel();
        elrDltModel.setErrorMessageId(primaryIdForTesting);
        elrDltModel.setDltOccurrence(1);
        elrDltModel.setErrorMessageSource("elr_validated");

        ValidatedELRModel validatedERLModel = new ValidatedELRModel();
        validatedERLModel.setRawMessage("HL7 message validated");
        validatedERLModel.setId(elrDltModel.getErrorMessageId());

        when(dltRepository.findById(eq(elrDltModel.getErrorMessageId())))
                .thenReturn(Optional.of(elrDltModel));

        when(validatedELRRepository.findById(eq(elrDltModel.getErrorMessageId())))
                .thenReturn(Optional.of(validatedERLModel));

        when(validatedELRRepository.save(any(ValidatedELRModel.class))).thenReturn(validatedERLModel);
        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(elrDltModel);

        var result = elrDeadLetterService.updateAndReprocessingMessage(primaryIdForTesting, "HL7 message");

        assertEquals(result.getMessage(), "HL7 message");
        assertEquals(result.getDltOccurrence(), 1);
    }

    @Test
    void testUpdateAndReprocessingMessage_FhirPrep_Success() throws DeadLetterTopicException {
        String primaryIdForTesting = guidForTesting;

        ElrDeadLetterModel elrDltModel = new ElrDeadLetterModel();
        elrDltModel.setErrorMessageId(primaryIdForTesting);
        elrDltModel.setDltOccurrence(1);
        elrDltModel.setErrorMessageSource("fhir_prep");


        when(dltRepository.findById(eq(elrDltModel.getErrorMessageId())))
                .thenReturn(Optional.of(elrDltModel));



        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(elrDltModel);

        var result = elrDeadLetterService.updateAndReprocessingMessage(primaryIdForTesting, "HL7 message");

        assertEquals(result.getMessage(), "HL7 message");
        assertEquals(result.getDltOccurrence(), 1);
    }

    @Test
    void testUpdateAndReprocessingMessage_XmlPrep_Success() throws DeadLetterTopicException {
        String primaryIdForTesting = guidForTesting;

        ElrDeadLetterModel elrDltModel = new ElrDeadLetterModel();
        elrDltModel.setErrorMessageId(primaryIdForTesting);
        elrDltModel.setDltOccurrence(1);
        elrDltModel.setErrorMessageSource("xml_prep");


        when(dltRepository.findById(eq(elrDltModel.getErrorMessageId())))
                .thenReturn(Optional.of(elrDltModel));

        when(dltRepository.save(any(ElrDeadLetterModel.class))).thenReturn(elrDltModel);

        var result = elrDeadLetterService.updateAndReprocessingMessage(primaryIdForTesting, "HL7 message");

        assertEquals(result.getMessage(), "HL7 message");
        assertEquals(result.getDltOccurrence(), 1);
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

}
