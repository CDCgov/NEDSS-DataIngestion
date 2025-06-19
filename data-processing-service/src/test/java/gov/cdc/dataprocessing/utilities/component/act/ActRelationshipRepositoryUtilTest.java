package gov.cdc.dataprocessing.utilities.component.act;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ActRelationshipJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationshipHistory;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
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

class ActRelationshipRepositoryUtilTest {
    @InjectMocks
    private ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;

    @Mock
    private ActRelationshipJdbcRepository actRelationshipRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetActRelationshipCollectionFromSourceId() {
        Long actUid = 1L;
        List<ActRelationship> actRelationships = new ArrayList<>();
        ActRelationship actRelationship = new ActRelationship();
        actRelationships.add(actRelationship);
        when(actRelationshipRepository.findBySourceActUid(actUid)).thenReturn(actRelationships);

        Collection<ActRelationshipDto> result = actRelationshipRepositoryUtil.getActRelationshipCollectionFromSourceId(actUid);

        assertEquals(1, result.size());
        for (ActRelationshipDto dto : result) {
            assertFalse(dto.isItNew());
            assertFalse(dto.isItDirty());
        }
        verify(actRelationshipRepository, times(1)).findBySourceActUid(actUid);
    }

    @Test
    void testGetActRelationshipCollectionFromSourceIdEmpty() {
        Long actUid = 1L;
        when(actRelationshipRepository.findBySourceActUid(actUid)).thenReturn(new ArrayList<>());

        Collection<ActRelationshipDto> result = actRelationshipRepositoryUtil.getActRelationshipCollectionFromSourceId(actUid);

        assertTrue(result.isEmpty());
        verify(actRelationshipRepository, times(1)).findBySourceActUid(actUid);
    }

    @Test
    void testSelectActRelationshipDTCollectionFromActUid()  {
        long actUid = 1L;
        List<ActRelationship> actRelationships = new ArrayList<>();
        ActRelationship actRelationship = new ActRelationship();
        actRelationships.add(actRelationship);
        when(actRelationshipRepository.findByTargetActUid(actUid)).thenReturn(actRelationships);

        Collection<ActRelationshipDto> result = actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(actUid);

        assertEquals(1, result.size());
        for (ActRelationshipDto dto : result) {
            assertFalse(dto.isItNew());
            assertFalse(dto.isItDirty());
        }
        verify(actRelationshipRepository, times(1)).findByTargetActUid(actUid);
    }

    @Test
    void testSelectActRelationshipDTCollectionFromActUidEmpty()  {
        long actUid = 1L;
        when(actRelationshipRepository.findByTargetActUid(actUid)).thenReturn(new ArrayList<>());

        Collection<ActRelationshipDto> result = actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(actUid);

        assertTrue(result.isEmpty());
        verify(actRelationshipRepository, times(1)).findByTargetActUid(actUid);
    }

    @Test
    void testSelectActRelationshipDTCollectionFromActUidException() {
        long actUid = 1L;
        when(actRelationshipRepository.findByTargetActUid(actUid)).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(RuntimeException.class, () -> {
            actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(actUid);
        });
    }

    @Test
    void testInsertActRelationshipHist() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();

        actRelationshipRepositoryUtil.insertActRelationshipHist(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).insertActRelationshipHistory(any(ActRelationshipHistory.class));
    }

    @Test
    void testStoreActRelationshipNew() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItNew(true);
        AuthUtil.authUser = new AuthUser();
        AuthUtil.authUser.setAuthUserUid(1L);

        actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).insertActRelationship(any(ActRelationship.class));
    }

    @Test
    void testStoreActRelationshipDelete() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDelete(true);

        actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).deleteActRelationship(any(ActRelationship.class));
    }

    @Test
    void testStoreActRelationshipDirty() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDirty(true);
        actRelationshipDto.setTargetActUid(1L);
        actRelationshipDto.setSourceActUid(2L);
        actRelationshipDto.setTypeCd("type");

        actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).updateActRelationship(any(ActRelationship.class));
    }

    @Test
    void testStoreActRelationshipNullDto() {
        assertThrows(DataProcessingException.class, () -> {
            actRelationshipRepositoryUtil.storeActRelationship(null);
        });
    }

    @Test
    void testStoreActRelationshipDirtyIncomplete() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDirty(true);

        actRelationshipRepositoryUtil.storeActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(0)).updateActRelationship(any(ActRelationship.class));
    }
}
