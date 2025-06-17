package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EntityHelperTest {
    @InjectMocks
    private EntityHelper entityHelper;

    @Mock
    private PrepareAssocModelHelper prepareAssocModel;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIterateELPDTForEntityLocatorParticipation() throws DataProcessingException {
        Collection<EntityLocatorParticipationDto> dtCol = new ArrayList<>();
        EntityLocatorParticipationDto elpDto = new EntityLocatorParticipationDto();
        dtCol.add(elpDto);

        when(prepareAssocModel.prepareAssocDTForEntityLocatorParticipation(any(EntityLocatorParticipationDto.class)))
                .thenReturn(elpDto);

        Collection<EntityLocatorParticipationDto> result = entityHelper.iterateELPDTForEntityLocatorParticipation(dtCol);

        assertEquals(1, result.size());
        verify(prepareAssocModel, times(1)).prepareAssocDTForEntityLocatorParticipation(any(EntityLocatorParticipationDto.class));
    }

    @Test
    void testIterateELPDTForEntityLocatorParticipation_Exp() throws DataProcessingException {
        Collection<EntityLocatorParticipationDto> dtCol = new ArrayList<>();
        EntityLocatorParticipationDto elpDto = new EntityLocatorParticipationDto();
        dtCol.add(elpDto);

        when(prepareAssocModel.prepareAssocDTForEntityLocatorParticipation(any(EntityLocatorParticipationDto.class)))
                .thenThrow(new RuntimeException("TEST"));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            entityHelper.iterateELPDTForEntityLocatorParticipation(dtCol);
        });

        assertNotNull(thrown);
    }

    @Test
    void testIterateRDT() throws DataProcessingException {
        Collection<RoleDto> dtCol = new ArrayList<>();
        RoleDto roleDto = new RoleDto();
        roleDto.setItNew(true);
        dtCol.add(roleDto);

        when(prepareAssocModel.prepareAssocDTForRole(any(RoleDto.class))).thenReturn(roleDto);

        Collection<RoleDto> result = entityHelper.iterateRDT(dtCol);

        assertEquals(1, result.size());
        verify(prepareAssocModel, times(1)).prepareAssocDTForRole(any(RoleDto.class));
    }

    @Test
    void testIterateRDT_Exp() throws DataProcessingException {
        Collection<RoleDto> dtCol = new ArrayList<>();
        RoleDto roleDto = new RoleDto();
        roleDto.setItNew(true);
        dtCol.add(roleDto);

        when(prepareAssocModel.prepareAssocDTForRole(any(RoleDto.class))).thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            entityHelper.iterateRDT(dtCol);
        });

        assertNotNull(thrown);
    }

    @Test
    void testIteratePDTForParticipation() throws DataProcessingException {
        Collection<ParticipationDto> dtCol = new ArrayList<>();
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setItNew(true);
        dtCol.add(participationDto);

        when(prepareAssocModel.prepareAssocDTForParticipation(any(ParticipationDto.class))).thenReturn(participationDto);

        Collection<ParticipationDto> result = entityHelper.iteratePDTForParticipation(dtCol);

        assertEquals(1, result.size());
        verify(prepareAssocModel, times(1)).prepareAssocDTForParticipation(any(ParticipationDto.class));
    }

    @Test
    void testIterateActivityParticipation() throws DataProcessingException {
        Collection<ActivityLocatorParticipationDto> dtCol = new ArrayList<>();
        ActivityLocatorParticipationDto alpDto = new ActivityLocatorParticipationDto();
        dtCol.add(alpDto);

        when(prepareAssocModel.prepareActivityLocatorParticipationDT(any(ActivityLocatorParticipationDto.class))).thenReturn(alpDto);

        Collection<ActivityLocatorParticipationDto> result = entityHelper.iterateActivityParticipation(dtCol);

        assertEquals(1, result.size());
        verify(prepareAssocModel, times(1)).prepareActivityLocatorParticipationDT(any(ActivityLocatorParticipationDto.class));
    }

    @Test
    void testIterateActRelationship() throws DataProcessingException {
        Collection<ActRelationshipDto> dtCol = new ArrayList<>();
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItNew(true);
        dtCol.add(actRelationshipDto);

        when(prepareAssocModel.prepareActRelationshipDT(any(ActRelationshipDto.class))).thenReturn(actRelationshipDto);

        Collection<ActRelationshipDto> result = entityHelper.iterateActRelationship(dtCol);

        assertEquals(1, result.size());
        verify(prepareAssocModel, times(1)).prepareActRelationshipDT(any(ActRelationshipDto.class));
    }

    @Test
    void testIterateALPDTActivityLocatorParticipation() throws DataProcessingException {
        Collection<ActivityLocatorParticipationDto> dtCol = new ArrayList<>();
        ActivityLocatorParticipationDto alpDto = new ActivityLocatorParticipationDto();
        dtCol.add(alpDto);

        when(prepareAssocModel.prepareAssocDTForActivityLocatorParticipation(any(ActivityLocatorParticipationDto.class))).thenReturn(alpDto);

        Collection<ActivityLocatorParticipationDto> result = entityHelper.iterateALPDTActivityLocatorParticipation(dtCol);

        assertEquals(1, result.size());
        verify(prepareAssocModel, times(1)).prepareAssocDTForActivityLocatorParticipation(any(ActivityLocatorParticipationDto.class));
    }

    @Test
    void testIterateALPDTActivityLocatorParticipation_Exp() throws DataProcessingException {
        Collection<ActivityLocatorParticipationDto> dtCol = new ArrayList<>();
        ActivityLocatorParticipationDto alpDto = new ActivityLocatorParticipationDto();
        dtCol.add(alpDto);

        when(prepareAssocModel.prepareAssocDTForActivityLocatorParticipation(any(ActivityLocatorParticipationDto.class))).thenThrow(
                new RuntimeException("TEST")
        );

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            entityHelper.iterateALPDTActivityLocatorParticipation(dtCol);
        });

        assertNotNull(thrown);
    }

    @Test
    void testIterateARDTActRelationship() throws DataProcessingException {
        Collection<ActRelationshipDto> dtCol = new ArrayList<>();
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItNew(true);
        dtCol.add(actRelationshipDto);

        when(prepareAssocModel.prepareAssocDTForActRelationship(any(ActRelationshipDto.class))).thenReturn(actRelationshipDto);

        Collection<ActRelationshipDto> result = entityHelper.iterateARDTActRelationship(dtCol);

        assertEquals(1, result.size());
        verify(prepareAssocModel, times(1)).prepareAssocDTForActRelationship(any(ActRelationshipDto.class));
    }

    @Test
    void testIterateARDTActRelationship_Exp() throws DataProcessingException {
        Collection<ActRelationshipDto> dtCol = new ArrayList<>();
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItNew(true);
        dtCol.add(actRelationshipDto);

        when(prepareAssocModel.prepareAssocDTForActRelationship(any(ActRelationshipDto.class))).thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            entityHelper.iterateARDTActRelationship(dtCol);
        });

        assertNotNull(thrown);
    }
}
