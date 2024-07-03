package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationshipHistory;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipHistoryRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
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

class ActRelationshipRepositoryUtilTest {
    @InjectMocks
    private ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;

    @Mock
    private ActRelationshipRepository actRelationshipRepository;

    @Mock
    private ActRelationshipHistoryRepository actRelationshipHistoryRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetActRelationshipCollectionFromSourceId() {
        Long actUid = 1L;
        List<ActRelationship> actRelationships = new ArrayList<>();
        ActRelationship actRelationship = new ActRelationship();
        actRelationships.add(actRelationship);
        when(actRelationshipRepository.findRecordsBySourceId(actUid)).thenReturn(Optional.of(actRelationships));

        Collection<ActRelationshipDto> result = actRelationshipRepositoryUtil.getActRelationshipCollectionFromSourceId(actUid);

        assertEquals(1, result.size());
        for (ActRelationshipDto dto : result) {
            assertFalse(dto.isItNew());
            assertFalse(dto.isItDirty());
        }
        verify(actRelationshipRepository, times(1)).findRecordsBySourceId(actUid);
    }

    @Test
    public void testGetActRelationshipCollectionFromSourceIdEmpty() {
        Long actUid = 1L;
        when(actRelationshipRepository.findRecordsBySourceId(actUid)).thenReturn(Optional.empty());

        Collection<ActRelationshipDto> result = actRelationshipRepositoryUtil.getActRelationshipCollectionFromSourceId(actUid);

        assertTrue(result.isEmpty());
        verify(actRelationshipRepository, times(1)).findRecordsBySourceId(actUid);
    }

    @Test
    public void testSelectActRelationshipDTCollectionFromActUid() throws DataProcessingException {
        long actUid = 1L;
        List<ActRelationship> actRelationships = new ArrayList<>();
        ActRelationship actRelationship = new ActRelationship();
        actRelationships.add(actRelationship);
        when(actRelationshipRepository.findRecordsByActUid(actUid)).thenReturn(Optional.of(actRelationships));

        Collection<ActRelationshipDto> result = actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(actUid);

        assertEquals(1, result.size());
        for (ActRelationshipDto dto : result) {
            assertFalse(dto.isItNew());
            assertFalse(dto.isItDirty());
        }
        verify(actRelationshipRepository, times(1)).findRecordsByActUid(actUid);
    }

    @Test
    public void testSelectActRelationshipDTCollectionFromActUidEmpty() throws DataProcessingException {
        long actUid = 1L;
        when(actRelationshipRepository.findRecordsByActUid(actUid)).thenReturn(Optional.empty());

        Collection<ActRelationshipDto> result = actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(actUid);

        assertTrue(result.isEmpty());
        verify(actRelationshipRepository, times(1)).findRecordsByActUid(actUid);
    }

    @Test
    public void testSelectActRelationshipDTCollectionFromActUidException() {
        long actUid = 1L;
        when(actRelationshipRepository.findRecordsByActUid(actUid)).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(DataProcessingException.class, () -> {
            actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(actUid);
        });
    }

    @Test
    public void testInsertActRelationshipHist() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();

        actRelationshipRepositoryUtil.insertActRelationshipHist(actRelationshipDto);

        verify(actRelationshipHistoryRepository, times(1)).save(any(ActRelationshipHistory.class));
    }

    @Test
    public void testStoreActRelationshipNew() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItNew(true);
        AuthUtil.authUser = new AuthUser();
        AuthUtil.authUser.setAuthUserUid(1L);

        actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).save(any(ActRelationship.class));
    }

    @Test
    public void testStoreActRelationshipDelete() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDelete(true);

        actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).delete(any(ActRelationship.class));
    }

    @Test
    public void testStoreActRelationshipDirty() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDirty(true);
        actRelationshipDto.setTargetActUid(1L);
        actRelationshipDto.setSourceActUid(2L);
        actRelationshipDto.setTypeCd("type");

        actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).save(any(ActRelationship.class));
    }

    @Test
    public void testStoreActRelationshipNullDto() {
        assertThrows(DataProcessingException.class, () -> {
            actRelationshipRepositoryUtil.storeActRelationship(null);
        });
    }

    @Test
    public void testStoreActRelationshipDirtyIncomplete() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDirty(true);

        actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(0)).save(any(ActRelationship.class));
    }
}
