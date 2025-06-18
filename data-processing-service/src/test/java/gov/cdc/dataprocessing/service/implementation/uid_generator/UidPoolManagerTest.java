package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UidPoolManagerTest {

    @Mock
    private LocalUidGeneratorRepository localUidGeneratorRepository;

    @InjectMocks
    private UidPoolManager uidPoolManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        uidPoolManager = new UidPoolManager(localUidGeneratorRepository);
        Queue<LocalUidModel> queue = new ConcurrentLinkedQueue<>();
        var localUid = new LocalUidModel();
        queue.add(localUid);
        uidPoolManager.uidPools.put("PERSON", queue);
    }

    @Test
    void testInitializePools() throws Exception {
        when(localUidGeneratorRepository.reserveBatchAndGetStartSeed(anyString(), anyInt()))
                .thenReturn(buildSeed());

        uidPoolManager.initializePools();
        for (LocalIdClass idClass : LocalIdClass.values()) {
            assertNotNull(uidPoolManager.getNextUid(idClass, false));
        }
    }

    @Test
    void testGetNextUidWithoutPreload() throws Exception {
        when(localUidGeneratorRepository.reserveBatchAndGetStartSeed(anyString(), anyInt()))
                .thenReturn(buildSeed());

        LocalUidModel uid = uidPoolManager.getNextUid(LocalIdClass.PERSON, false);
        assertNotNull(uid);
    }


    private LocalUidGenerator buildSeed() {
        LocalUidGenerator dto = new LocalUidGenerator();
        dto.setSeedValueNbr(1000L);
        dto.setClassNameCd("PAT");
        dto.setUidPrefixCd("P");
        dto.setUidSuffixCd("X");
        return dto;
    }
}