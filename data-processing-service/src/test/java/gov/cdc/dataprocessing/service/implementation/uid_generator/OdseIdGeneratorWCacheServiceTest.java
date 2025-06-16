package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OdseIdGeneratorWCacheServiceTest {

    @Mock
    private LocalUidGeneratorRepository localUidGeneratorRepository;

    @InjectMocks
    private OdseIdGeneratorWCacheService odseIdGeneratorWCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetValidLocalUid_WithGaApplied() throws Exception {
        LocalUidGenerator classUid = new LocalUidGenerator();
        classUid.setClassNameCd("PERSON");
        classUid.setSeedValueNbr(1000L);

        LocalUidGenerator gaUid = new LocalUidGenerator();
        gaUid.setClassNameCd("GA");
        gaUid.setSeedValueNbr(2000L);

        when(localUidGeneratorRepository.findByIdForUpdate("PERSON")).thenReturn(Optional.of(classUid));
        when(localUidGeneratorRepository.findByIdForUpdate("GA")).thenReturn(Optional.of(gaUid));

        LocalUidModel result = odseIdGeneratorWCacheService.getValidLocalUid(LocalIdClass.PERSON, true);

        assertNotNull(result);
        assertEquals("PERSON", result.getPrimaryClassName());
        assertEquals("PERSON", result.getClassTypeUid().getClassNameCd());
        assertEquals("GA", result.getGaTypeUid().getClassNameCd());
        verify(localUidGeneratorRepository, times(2)).save(any(LocalUidGenerator.class));
    }

    @Test
    void testGetValidLocalUid_WithoutGaApplied() throws Exception {
        LocalUidGenerator classUid = new LocalUidGenerator();
        classUid.setClassNameCd("PERSON");
        classUid.setSeedValueNbr(1000L);

        when(localUidGeneratorRepository.findByIdForUpdate("PERSON")).thenReturn(Optional.of(classUid));

        LocalUidModel result = odseIdGeneratorWCacheService.getValidLocalUid(LocalIdClass.PERSON, false);

        assertNotNull(result);
        assertEquals("PERSON", result.getPrimaryClassName());
        assertEquals("PERSON", result.getClassTypeUid().getClassNameCd());
        assertNull(result.getGaTypeUid());
        verify(localUidGeneratorRepository).save(any(LocalUidGenerator.class));
    }

    @Test
    void testFetchLocalId_LocalUidNotFound_ThrowsException() {
        when(localUidGeneratorRepository.findByIdForUpdate("PERSON")).thenReturn(Optional.empty());

        DataProcessingException thrown = assertThrows(DataProcessingException.class,
                () -> odseIdGeneratorWCacheService.getValidLocalUid(LocalIdClass.PERSON, false));

        assertFalse(thrown.getMessage().contains("Local UID not found"));
    }

    @Test
    void testFetchLocalId_RepositoryThrowsException() {
        when(localUidGeneratorRepository.findByIdForUpdate("PERSON"))
                .thenThrow(new RuntimeException("DB error"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class,
                () -> odseIdGeneratorWCacheService.getValidLocalUid(LocalIdClass.PERSON, false));

        assertTrue(thrown.getMessage().contains("Error fetching local UID"));
    }
}
