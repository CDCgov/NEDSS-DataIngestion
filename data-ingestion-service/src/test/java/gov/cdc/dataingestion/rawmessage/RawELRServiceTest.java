package gov.cdc.dataingestion.rawmessage;

import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.rawmessage.dto.RawERLDto;
import gov.cdc.dataingestion.rawmessage.service.RawELRService;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
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
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class RawELRServiceTest {
    @Mock
    private IRawELRRepository rawELRRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private RawELRService target;

    private String guidForTesting =  UUID.randomUUID().toString();


    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
        target = new RawELRService(rawELRRepository, kafkaProducerService);
    }



    @Test
    void testSaveHL7_Success() {
        RawERLDto modelDto = new RawERLDto();
        modelDto.setPayload("test");
        modelDto.setType("HL7");
        RawERLModel model = new RawERLModel();
        model.setId("test");
        model.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        model.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        model.setCreatedBy("test");
        model.setUpdatedBy("test");
        when(rawELRRepository.save(any())).thenReturn(model);
        Mockito.doNothing().when(kafkaProducerService).sendMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        var result = target.submission(modelDto, "1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test",result);

    }

    @Test
    void testSaveElrXml_Success() {
        RawERLDto modelDto = new RawERLDto();
        modelDto.setPayload("test");
        modelDto.setType("HL7-XML");
        RawERLModel model = new RawERLModel();
        model.setId("test");
        model.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        model.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        model.setCreatedBy("test");
        model.setUpdatedBy("test");
        when(rawELRRepository.save(any())).thenReturn(model);
        Mockito.doNothing().when(kafkaProducerService).sendMessageFromController(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        var result = target.submission(modelDto, "1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test",result);

    }

    @Test
    void getById_Success() {
        RawERLDto modelDto = new RawERLDto();
        modelDto.setPayload("test");
        modelDto.setType("HL7");
        modelDto.setId(guidForTesting);

        RawERLModel model = new RawERLModel();
        model.setId(guidForTesting);
        model.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        model.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        model.setCreatedBy("test");
        model.setUpdatedBy("test");

        when(rawELRRepository.getById(any())).thenReturn(model);

        var result = target.getById(modelDto.getId());

        Assertions.assertNotNull(result);
    }
}
