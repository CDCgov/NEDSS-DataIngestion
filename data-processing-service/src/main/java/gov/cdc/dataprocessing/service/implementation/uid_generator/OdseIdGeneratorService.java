package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorService;
import org.springframework.stereotype.Service;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class OdseIdGeneratorService implements IOdseIdGeneratorService {
    private final LocalUidGeneratorRepository localUidGeneratorRepository;

    public OdseIdGeneratorService(LocalUidGeneratorRepository localUidGeneratorRepository) {
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }
//
//    @Transactional
//    public LocalUidGenerator getLocalIdAndUpdateSeed(LocalIdClass localIdClass) throws DataProcessingException {
//        try {
//            Optional<LocalUidGenerator> localUidOpt = this.localUidGeneratorRepository.findById(localIdClass.name());
//            LocalUidGenerator localId = null;
//            if (localUidOpt.isPresent()) {
//                localId = localUidOpt.get();
//            }
//
//            if (localId != null) {
//                long seed = localId.getSeedValueNbr();
//                seed++;
//                LocalUidGenerator newLocalId = new LocalUidGenerator();
//                newLocalId.setUidSuffixCd(localId.getUidSuffixCd());
//                newLocalId.setUidPrefixCd(localId.getUidPrefixCd());
//                newLocalId.setTypeCd(localId.getTypeCd());
//                newLocalId.setClassNameCd(localId.getClassNameCd());
//                newLocalId.setSeedValueNbr(seed);
//                this.localUidGeneratorRepository.save(newLocalId);
//            }
//            return localId;
//        } catch (Exception e) {
//            throw new DataProcessingException(e.getMessage(), e);
//        }
//
//    }
}
