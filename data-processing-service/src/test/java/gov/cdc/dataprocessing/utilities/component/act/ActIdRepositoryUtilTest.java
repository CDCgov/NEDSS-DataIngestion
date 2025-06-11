package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActIdJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActIdRepositoryUtilTest {
    @InjectMocks
    private ActIdRepositoryUtil actIdRepositoryUtil;

    @Mock
    private ActIdJdbcRepository actIdRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetActIdCollection() {
        Long actUid = 1L;
        List<ActId> actIds = new ArrayList<>();
        ActId actId = new ActId();
        actIds.add(actId);
        when(actIdRepository.findRecordsByActUid(actUid)).thenReturn(actIds);

        Collection<ActIdDto> result = actIdRepositoryUtil.getActIdCollection(actUid);

        assertEquals(1, result.size());
        for (ActIdDto dto : result) {
            assertFalse(dto.isItNew());
            assertFalse(dto.isItDirty());
        }
        verify(actIdRepository, times(1)).findRecordsByActUid(actUid);
    }

    @Test
    void testGetActIdCollectionEmpty() {
        Long actUid = 1L;
        when(actIdRepository.findRecordsByActUid(actUid)).thenReturn(new ArrayList<>());

        Collection<ActIdDto> result = actIdRepositoryUtil.getActIdCollection(actUid);

        assertTrue(result.isEmpty());
        verify(actIdRepository, times(1)).findRecordsByActUid(actUid);
    }

    @Test
    void testInsertActIdCollection() {
        Long uid = 1L;
        Collection<ActIdDto> actIdDtoCollection = new ArrayList<>();
        ActIdDto actIdDto = new ActIdDto();
        actIdDtoCollection.add(actIdDto);

        actIdRepositoryUtil.insertActIdCollection(uid, actIdDtoCollection);

        verify(actIdRepository, times(1)).mergeActId(any(ActId.class));
        for (ActIdDto dto : actIdDtoCollection) {
            assertFalse(dto.isItDirty());
            assertFalse(dto.isItNew());
            assertFalse(dto.isItDelete());
        }
    }

    @Test
    void testInsertActIdCollectionEmpty() {
        Long uid = 1L;
        Collection<ActIdDto> actIdDtoCollection = new ArrayList<>();

        actIdRepositoryUtil.insertActIdCollection(uid, actIdDtoCollection);

        verify(actIdRepository, times(0)).mergeActId(any(ActId.class));
    }
}
