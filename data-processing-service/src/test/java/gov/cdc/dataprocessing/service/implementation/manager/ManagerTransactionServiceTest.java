package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.exception.DataProcessingDBException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ManagerTransactionServiceTest {

    private ManagerService managerService;
    private ManagerTransactionService transactionService;

    @BeforeEach
    void setUp() {
        managerService = mock(ManagerService.class);
        transactionService = new ManagerTransactionService(managerService);
    }

    @Test
    void testProcessWithTransactionSeparation_WhenProcessingReturnsNull_SkipsHandling() throws Exception {
        when(managerService.processingELR(123, false)).thenReturn(null);

        transactionService.processWithTransactionSeparation(123, false);

        verify(managerService, times(1)).processingELR(123, false);
        verify(managerService, never()).handlingWdsAndLab(any(), anyBoolean());
    }

    @Test
    void testProcessWithTransactionSeparation_WhenProcessingReturnsValid_CallsHandling() throws Exception {
        PublicHealthCaseFlowContainer container = new PublicHealthCaseFlowContainer();
        when(managerService.processingELR(123, false)).thenReturn(container);

        transactionService.processWithTransactionSeparation(123, false);

        verify(managerService).processingELR(123, false);
        verify(managerService).handlingWdsAndLab(container, false);
    }

    @Test
    void testProcessWithTransactionSeparation_WhenProcessingThrows_ExceptionPropagated() throws Exception {
        when(managerService.processingELR(123, false)).thenThrow(new DataProcessingDBException("DB error"));

        assertThrows(DataProcessingDBException.class,
                () -> transactionService.processWithTransactionSeparation(123, false));

        verify(managerService).processingELR(123, false);
        verify(managerService, never()).handlingWdsAndLab(any(), anyBoolean());
    }

    @Test
    void testProcessWithTransactionSeparation_WhenHandlingThrows_ExceptionPropagated() throws Exception {
        PublicHealthCaseFlowContainer container = new PublicHealthCaseFlowContainer();
        when(managerService.processingELR(123, false)).thenReturn(container);
        doThrow(new EdxLogException("Handling failed", container)).when(managerService).handlingWdsAndLab(container, false);

        assertThrows(EdxLogException.class,
                () -> transactionService.processWithTransactionSeparation(123, false));

        verify(managerService).processingELR(123, false);
        verify(managerService).handlingWdsAndLab(container, false);
    }
}
