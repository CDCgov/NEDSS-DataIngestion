package gov.cdc.dataingestion.deadletter.service;

import gov.cdc.dataingestion.deadletter.model.ElrDeadLetterDto;
import gov.cdc.dataingestion.deadletter.model.ElrDltStatus;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import gov.cdc.dataingestion.report.repository.IRawELRRepository;
import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.conversion.repository.IHL7ToFHIRRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class ElrDeadLetterServiceTest {

    private static final DockerImageName taggedImageName = DockerImageName.parse("mcr.microsoft.com/azure-sql-edge")
            .withTag("latest")
            .asCompatibleSubstituteFor("mcr.microsoft.com/mssql/server");

    @Container
    public MSSQLServerContainer mssqlserver  =
            new MSSQLServerContainer<>(taggedImageName)
                    .acceptLicense()
                    .withInitScript("sql-script/test-script.sql")
            ;
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


    private ElrDeadLetterService elrDeadLetterService;

    private String guidForTesting = "";
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        elrDeadLetterService = new ElrDeadLetterService(dltRepository, rawELRRepository, validatedELRRepository, kafkaProducerService, fhirRepository);

    }

    @AfterEach
    public void tearDown() {
        mssqlserver.stop();
    }

    private void initialDataInsertionAndSelection(String dltSourceMessage) {
        //region CONNECTION
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(mssqlserver.getJdbcUrl(), mssqlserver.getUsername(), mssqlserver.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //endregion

        //region INITIAL INSERTION - insert dlt data to test container db
        String sqlInsert = "INSERT INTO [NBS_DataIngest].[dbo].[elr_dlt] (" +
                "error_message_id, error_message_source, error_stack_trace, dlt_status, dlt_occurrence, " +
                "created_by, updated_by, created_on, updated_on" +
                ") VALUES (" +
                "NEWID(), '" + dltSourceMessage +"', " + "'Sample Error Stack Trace', 'ERROR', 1, " +
                "'system', 'system', GETDATE(), NULL" +
                ");";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //endregion -  -,

        //region INITIAL SELECTION - unique id from dlt for container testing
        String sqlSelect = "SELECT TOP 1 * FROM [NBS_DataIngest].[dbo].[elr_dlt];";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlSelect);

            // Iterate over the ResultSet to get the first record
            while(rs.next()) {
                guidForTesting = rs.getString("error_message_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //endregion

        //region STEP INSERTION - dependent tables, insertion for elr_raw, elr_fhir, and elr_validated
        String rawElrQuery = "INSERT INTO [NBS_DataIngest].[dbo].[elr_raw] (" +
                "id, message_type, payload, created_by, updated_by, created_on, updated_on" +
                ") VALUES (" +
                "'" + guidForTesting +"', " + "'Sample Message Type', 'Sample Payload', 'system', 'system', GETDATE(), NULL" +
                ");";

        String fhirElrQuery = "INSERT INTO [NBS_DataIngest].[dbo].[elr_fhir] (" +
                "id, fhir_message, raw_message_id, created_by, updated_by, created_on, updated_on" +
                ") VALUES (" +
                "'" + guidForTesting +"', " +"'Sample FHIR Message', 'Sample Raw Message ID', 'system', 'system', GETDATE(), NULL" +
                ");";

        String elrValidateQuery = "INSERT INTO [NBS_DataIngest].[dbo].[elr_validated] (" +
                "id, raw_message_id, message_type, message_version, validated_message, hashed_hl7_string, " +
                "created_by, updated_by, created_on, updated_on" +
                ") VALUES (" +
                "'" + guidForTesting +"', " + "'Sample Raw Message ID', 'Sample Message Type', 'Sample Message Version', 'Sample Validated Message', " +
                "NULL, 'system', 'system', GETDATE(), NULL" +
                ");";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(rawElrQuery);
            stmt.execute(fhirElrQuery);
            stmt.execute(elrValidateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //endregion
    }

    @Test
    void testGetDltRecordByIdSuccess() throws DeadLetterTopicException {

        initialDataInsertionAndSelection("elr_raw");
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(guidForTesting);
        model.setErrorMessageSource("elr_raw");
        model.setErrorStackTrace("Sample Error Stack Trace");
        model.setDltOccurrence(1);
        model.setDltStatus("ERROR");
        model.setCreatedBy("system");
        model.setUpdatedBy("system");

        RawERLModel rawModel = new RawERLModel();
        rawModel.setId(guidForTesting);
        rawModel.setPayload("HL7 message");

        when(dltRepository.findById(anyString())).thenReturn(Optional.of(model));
        when(rawELRRepository.findById(anyString())).thenReturn(Optional.of(rawModel));

        ElrDeadLetterDto savedDto = elrDeadLetterService.getDltRecordById(guidForTesting);
        assertEquals(savedDto.getErrorMessageId(), model.getErrorMessageId());
        assertEquals(savedDto.getErrorMessage(), rawModel.getPayload());
    }

    @Test
    void testGetDltRecordById_RawTableNotFound() {

        initialDataInsertionAndSelection("elr_raw");
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(guidForTesting);
        model.setErrorMessageSource("elr_raw");
        model.setErrorStackTrace("Sample Error Stack Trace");
        model.setDltOccurrence(1);
        model.setDltStatus("ERROR");
        model.setCreatedBy("system");
        model.setUpdatedBy("system");


        when(dltRepository.findById(anyString())).thenReturn(Optional.of(model));

        var exception = Assertions.assertThrows(DeadLetterTopicException.class, () -> {
            elrDeadLetterService.getDltRecordById(guidForTesting);
        });
        Assertions.assertEquals("DLT record, but parent table record not found", exception.getMessage());

    }

    @Test
    void testGetDltRecordById_RawTableNonRecognizeTopic() {

        initialDataInsertionAndSelection("elr_raw");
        ElrDeadLetterModel model = new ElrDeadLetterModel();
        model.setErrorMessageId(guidForTesting);
        model.setErrorMessageSource("unknown");
        model.setErrorStackTrace("Sample Error Stack Trace");
        model.setDltOccurrence(1);
        model.setDltStatus("ERROR");
        model.setCreatedBy("system");
        model.setUpdatedBy("system");

        when(dltRepository.findById(anyString())).thenReturn(Optional.of(model));

        var exception = Assertions.assertThrows(DeadLetterTopicException.class, () -> {
            elrDeadLetterService.getDltRecordById(guidForTesting);
        });
        Assertions.assertEquals("Unsupported Topic", exception.getMessage());

    }

    @Test
    void testGetDltRecordById_NoDltRecordFound() {
        initialDataInsertionAndSelection("elr_raw");
        var exception = Assertions.assertThrows(DeadLetterTopicException.class, () -> {
            elrDeadLetterService.getDltRecordById(guidForTesting);
        });
        Assertions.assertEquals("Dead Letter Record Is Null", exception.getMessage());

    }

    @Test
    void testGetAllErrorDltRecord_Success() throws DeadLetterTopicException {
        initialDataInsertionAndSelection("elr_raw");
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

        when(dltRepository.findAllDltRecordByDltStatus(eq(ElrDltStatus.ERROR.name()), eq(Sort.by(Sort.Direction.DESC, "createdOn")))).thenReturn(Optional.of(listData));
        when(rawELRRepository.findById(anyString())).thenReturn(Optional.of(rawModel));
        var result = elrDeadLetterService.getAllErrorDltRecord();
        assertEquals(result.get(0).getErrorMessageId(), model.getErrorMessageId());
        assertEquals(result.get(0).getErrorMessage(), rawModel.getPayload());

    }

    @Test
    void testGetAllErrorDltRecord_NoDataFound() throws DeadLetterTopicException {
        var result = elrDeadLetterService.getAllErrorDltRecord();
        assertEquals(result.size(), 0);
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
