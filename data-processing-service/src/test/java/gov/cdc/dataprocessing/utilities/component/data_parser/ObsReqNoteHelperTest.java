package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.phdc.HL7NTEType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ObsReqNoteHelperTest {

    private ObservationContainer observationContainer;
    private HL7NTEType hl7NTEType;

    @BeforeEach
    void setUp() {
        observationContainer = mock(ObservationContainer.class);
        hl7NTEType = mock(HL7NTEType.class);
    }

    @Test
    void testGetObsReqNotes_WithComments() throws DataProcessingException {
        List<String> comments = new ArrayList<>();
        comments.add("Test Comment 1");
        comments.add("Test Comment 2");

        when(hl7NTEType.getHL7Comment()).thenReturn(comments);
        when(observationContainer.getTheObsValueTxtDtoCollection()).thenReturn(new ArrayList<>());
        when(observationContainer.getTheObservationDto()).thenReturn(new ObservationDto());

        List<HL7NTEType> noteArray = new ArrayList<>();
        noteArray.add(hl7NTEType);

        ObservationContainer result = ObsReqNoteHelper.getObsReqNotes(noteArray, observationContainer);

        assertNotNull(result);
        assertEquals(2, result.getTheObsValueTxtDtoCollection().size());
        verify(observationContainer, times(7)).getTheObsValueTxtDtoCollection();
    }

    @Test
    void testGetObsReqNotes_WithoutComments() throws DataProcessingException {
        when(hl7NTEType.getHL7Comment()).thenReturn(new ArrayList<>());
        when(observationContainer.getTheObsValueTxtDtoCollection()).thenReturn(new ArrayList<>());
        when(observationContainer.getTheObservationDto()).thenReturn(new ObservationDto());

        List<HL7NTEType> noteArray = new ArrayList<>();
        noteArray.add(hl7NTEType);

        ObservationContainer result = ObsReqNoteHelper.getObsReqNotes(noteArray, observationContainer);

        assertNotNull(result);
        assertEquals(1, result.getTheObsValueTxtDtoCollection().size());
        verify(observationContainer, times(4)).getTheObsValueTxtDtoCollection();
    }

    @Test
    void testGetObsReqNotes_Exception() {
        List<HL7NTEType> noteArray = new ArrayList<>();
        noteArray.add(hl7NTEType);

        try {
            doThrow(new RuntimeException("Test Exception")).when(hl7NTEType).getHL7Comment();
            ObsReqNoteHelper.getObsReqNotes(noteArray, observationContainer);
        } catch (DataProcessingException e) {
            assertEquals("Exception thrown at ObservationResultRequest.getObsReqNotes:Test Exception", e.getMessage());
        }
    }
}
