package gov.cdc.dataprocessing.service.implementation.uid_generator;

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
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class OdseIdGeneratorService implements IOdseIdGeneratorService {
//    private final LocalUidGeneratorRepository localUidGeneratorRepository;

    public OdseIdGeneratorService() {
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
