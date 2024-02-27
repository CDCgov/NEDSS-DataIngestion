package gov.cdc.dataprocessing.service.core;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.repository.nbs.odse.locator.LocalUidGeneratorRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.LocalUidGenerator;
import gov.cdc.dataprocessing.service.interfaces.IOdseIdGeneratorService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OdseIdGeneratorService implements IOdseIdGeneratorService {
    private static final Logger logger = LoggerFactory.getLogger(OdseIdGeneratorService.class);
    private final LocalUidGeneratorRepository localUidGeneratorRepository;

    public OdseIdGeneratorService(LocalUidGeneratorRepository localUidGeneratorRepository) {
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }

    @Transactional
    public LocalUidGenerator getLocalIdAndUpdateSeed(LocalIdClass localIdClass) {
        Optional<LocalUidGenerator> localUidOpt = this.localUidGeneratorRepository.findByIdForUpdate(localIdClass.name());
        LocalUidGenerator localId = null;
        if (localUidOpt.isPresent()) {
            localId = localUidOpt.get();
        }

        if (localId != null) {
            long seed = localId.getSeedValueNbr();
            seed++;
            LocalUidGenerator newLocalId = new LocalUidGenerator();
            newLocalId.setUidSuffixCd(localId.getUidSuffixCd());
            newLocalId.setUidPrefixCd(localId.getUidPrefixCd());
            newLocalId.setTypeCd(localId.getTypeCd());
            newLocalId.setClassNameCd(localId.getClassNameCd());
            newLocalId.setSeedValueNbr(seed);
            this.localUidGeneratorRepository.save(newLocalId);
        }
        return localId;
    }
}
