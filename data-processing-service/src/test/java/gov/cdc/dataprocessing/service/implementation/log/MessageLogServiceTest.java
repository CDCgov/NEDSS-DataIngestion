package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.MessageLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.MessageLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageLogServiceTest {

    @InjectMocks
    private MessageLogService messageLogService;

    @Mock
    private MessageLogRepository messageLogRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMessageLogSuccess() throws DataProcessingException {
        Collection<MessageLogDto> messageLogDtoCollection = new ArrayList<>();
        MessageLogDto messageLogDto = new MessageLogDto();
        messageLogDtoCollection.add(messageLogDto);

        messageLogService.saveMessageLog(messageLogDtoCollection);

        verify(messageLogRepository, times(1)).save(any(MessageLog.class));
    }

    @Test
    void testSaveMessageLogNullCollection() throws DataProcessingException {
        messageLogService.saveMessageLog(null);

        verify(messageLogRepository, times(0)).save(any(MessageLog.class));
    }

    @Test
    void testSaveMessageLogException() {
        Collection<MessageLogDto> messageLogDtoCollection = new ArrayList<>();
        MessageLogDto messageLogDto = new MessageLogDto();
        messageLogDtoCollection.add(messageLogDto);

        doThrow(new RuntimeException("Test Exception")).when(messageLogRepository).save(any(MessageLog.class));

        assertThrows(DataProcessingException.class, () -> {
            messageLogService.saveMessageLog(messageLogDtoCollection);
        });
    }

    @Test
    void testSaveMessageLogEmptyCollection() throws DataProcessingException {
        Collection<MessageLogDto> messageLogDtoCollection = new ArrayList<>();

        messageLogService.saveMessageLog(messageLogDtoCollection);

        verify(messageLogRepository, times(0)).save(any(MessageLog.class));
    }
}