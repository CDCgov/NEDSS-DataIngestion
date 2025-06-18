package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActLocatorParticipationJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActLocatorParticipationRepositoryUtilTest {
    @InjectMocks
    private ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil;

    @Mock
    private ActLocatorParticipationJdbcRepository actLocatorParticipationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetActLocatorParticipationCollection() {
        Long actUid = 1L;
        List<ActLocatorParticipation> actLocatorParticipations = new ArrayList<>();
        ActLocatorParticipation actLocatorParticipation = new ActLocatorParticipation();
        actLocatorParticipations.add(actLocatorParticipation);
        when(actLocatorParticipationRepository.findByActUid(actUid)).thenReturn(actLocatorParticipations);

        Collection<ActivityLocatorParticipationDto> result = actLocatorParticipationRepositoryUtil.getActLocatorParticipationCollection(actUid);

        assertEquals(1, result.size());
        for (ActivityLocatorParticipationDto dto : result) {
            assertFalse(dto.isItNew());
            assertFalse(dto.isItDirty());
        }
        verify(actLocatorParticipationRepository, times(1)).findByActUid(actUid);
    }

    @Test
    void testGetActLocatorParticipationCollectionEmpty() {
        Long actUid = 1L;
        when(actLocatorParticipationRepository.findByActUid(actUid)).thenReturn(new ArrayList<>());

        Collection<ActivityLocatorParticipationDto> result = actLocatorParticipationRepositoryUtil.getActLocatorParticipationCollection(actUid);

        assertTrue(result.isEmpty());
        verify(actLocatorParticipationRepository, times(1)).findByActUid(actUid);
    }

    @Test
    void testInsertActLocatorParticipationCollection() {
        Long uid = 1L;
        Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection = new ArrayList<>();
        ActivityLocatorParticipationDto activityLocatorParticipationDto = new ActivityLocatorParticipationDto();
        activityLocatorParticipationDtoCollection.add(activityLocatorParticipationDto);

        actLocatorParticipationRepositoryUtil.insertActLocatorParticipationCollection(uid, activityLocatorParticipationDtoCollection);

        verify(actLocatorParticipationRepository, times(1)).mergeActLocatorParticipation(any(ActLocatorParticipation.class));
        for (ActivityLocatorParticipationDto dto : activityLocatorParticipationDtoCollection) {
            assertFalse(dto.isItDirty());
            assertFalse(dto.isItNew());
            assertFalse(dto.isItDelete());
        }
    }

    @Test
    void testInsertActLocatorParticipationCollectionEmpty() {
        Long uid = 1L;
        Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection = new ArrayList<>();

        actLocatorParticipationRepositoryUtil.insertActLocatorParticipationCollection(uid, activityLocatorParticipationDtoCollection);

        verify(actLocatorParticipationRepository, times(0)).mergeActLocatorParticipation(any(ActLocatorParticipation.class));
    }
}
