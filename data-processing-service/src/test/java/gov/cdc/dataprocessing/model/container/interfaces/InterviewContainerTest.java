package gov.cdc.dataprocessing.model.container.interfaces;


import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.phc.InterviewDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterviewContainerTest {

    @Test
    void testGettersAndSetters() {
        InterviewContainer interviewContainer = new InterviewContainer();

        // Test inherited boolean fields from BaseContainer
        interviewContainer.setItNew(true);
        interviewContainer.setItOld(true);
        interviewContainer.setItDirty(true);
        interviewContainer.setItDelete(true);

        assertTrue(interviewContainer.isItNew());
        assertTrue(interviewContainer.isItOld());
        assertTrue(interviewContainer.isItDirty());
        assertTrue(interviewContainer.isItDelete());

        // Test inherited String field from BaseContainer
        String superClassType = "TestSuperClass";
        interviewContainer.setSuperClassType(superClassType);
        assertEquals(superClassType, interviewContainer.getSuperClassType());

        // Test inherited Collection field from BaseContainer
        Collection<Object> ldfs = new ArrayList<>();
        ldfs.add("TestObject");
        interviewContainer.setLdfs(ldfs);
        assertEquals(ldfs, interviewContainer.getLdfs());

        // Test InterviewContainer specific fields
        InterviewDto interviewDto = new InterviewDto();
        interviewContainer.setTheInterviewDto(interviewDto);
        assertEquals(interviewDto, interviewContainer.getTheInterviewDto());

        Collection<EDXEventProcessDto> edxEventProcessDtoCollection = new ArrayList<>();
        edxEventProcessDtoCollection.add(new EDXEventProcessDto());
        interviewContainer.setEdxEventProcessDtoCollection(edxEventProcessDtoCollection);
        assertEquals(edxEventProcessDtoCollection, interviewContainer.getEdxEventProcessDtoCollection());
    }
}