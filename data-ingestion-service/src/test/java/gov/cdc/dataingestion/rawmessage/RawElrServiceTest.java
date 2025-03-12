package gov.cdc.dataingestion.rawmessage;

import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.exception.KafkaProducerException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.rawmessage.dto.RawElrDto;
import gov.cdc.dataingestion.rawmessage.service.RawElrService;
import gov.cdc.dataingestion.report.repository.IRawElrRepository;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RawElrServiceTest {
    @Mock
    private IRawElrRepository rawELRRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private IElrDeadLetterRepository iElrDeadLetterRepository;

    @InjectMocks
    private RawElrService target;

    private String guidForTesting =  UUID.randomUUID().toString();


    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
        target = new RawElrService(rawELRRepository, kafkaProducerService, iElrDeadLetterRepository);
    }

    @Test
    void testSaveHL7_Success() throws KafkaProducerException {
        RawElrDto modelDto = new RawElrDto();
        modelDto.setPayload("test");
        modelDto.setType("HL7");
        RawElrModel model = new RawElrModel();
        model.setId("test");
        model.setVersion("1");
        model.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        model.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        model.setCreatedBy("test");
        model.setUpdatedBy("test");
        when(rawELRRepository.save(any())).thenReturn(model);
        Mockito.doNothing().when(kafkaProducerService).sendMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        var result = target.submission(modelDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test",result);
    }

    @Test
    void testSaveElrXml_Success() throws KafkaProducerException {
        RawElrDto modelDto = new RawElrDto();
        modelDto.setPayload("test");
        modelDto.setType("HL7-XML");
        RawElrModel model = new RawElrModel();
        model.setId("test");
        model.setVersion("1");
        model.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        model.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        model.setCreatedBy("test");
        model.setUpdatedBy("test");
        when(rawELRRepository.save(any())).thenReturn(model);
        Mockito.doNothing().when(kafkaProducerService).sendMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        var result = target.submission(modelDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test",result);
    }

    @Test
    void getById_Success() {
        RawElrDto modelDto = new RawElrDto();
        modelDto.setPayload("test");
        modelDto.setType("HL7");
        modelDto.setId(guidForTesting);

        RawElrModel model = new RawElrModel();
        model.setId(guidForTesting);
        model.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        model.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        model.setCreatedBy("test");
        model.setUpdatedBy("test");

        when(rawELRRepository.getById(any())).thenReturn(model);

        var result = target.getById(modelDto.getId());

        Assertions.assertNotNull(result);
    }

    @Test
    void testUpdateRawMessageAfterRetry_HL7_Success() throws KafkaProducerException {
        RawElrDto modelDto = new RawElrDto();
        modelDto.setId("test-retry");
        modelDto.setType("HL7");
        modelDto.setValidationActive(true);
        modelDto.setVersion("1");
        Mockito.doNothing().when(kafkaProducerService).sendMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        target.updateRawMessageAfterRetry(modelDto, 1);
        Mockito.verify(kafkaProducerService).sendMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(2), Mockito.any(), Mockito.any());
    }

    @Test
    void testUpdateRawMessageAfterRetry_XML_Success() throws KafkaProducerException {
        RawElrDto modelDto = new RawElrDto();
        modelDto.setId("test-retry-xml");
        modelDto.setType("HL7-XML");
        modelDto.setPayload("xml payload");
        modelDto.setVersion("1");
        Mockito.doNothing().when(kafkaProducerService).sendElrXmlMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        target.updateRawMessageAfterRetry(modelDto, 1);
        Mockito.verify(kafkaProducerService).sendElrXmlMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq(2), Mockito.any(), Mockito.any());
    }

    @Test
    void testUpdateRawMessageAfterRetry_KafkaProducerException() throws KafkaProducerException {
        RawElrDto modelDto = new RawElrDto();
        modelDto.setId("retry-fail");
        modelDto.setType("HL7");
        Mockito.doThrow(new KafkaProducerException("Retry failed")).when(kafkaProducerService).sendMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Assertions.assertThrows(KafkaProducerException.class, () -> target.updateRawMessageAfterRetry(modelDto, 1));
        Mockito.verify(iElrDeadLetterRepository).updateDltOccurrenceForRawId(Mockito.any(), Mockito.eq(2), Mockito.eq("ERROR"));
    }
}
