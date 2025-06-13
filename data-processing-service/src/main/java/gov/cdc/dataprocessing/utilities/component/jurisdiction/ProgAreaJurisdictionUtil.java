package gov.cdc.dataprocessing.utilities.component.jurisdiction;

import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component

public class ProgAreaJurisdictionUtil {
    private final ICacheApiService cacheApiService;
    private static final Logger logger = LoggerFactory.getLogger(ProgAreaJurisdictionUtil.class); // NOSONAR

    public ProgAreaJurisdictionUtil(@Lazy ICacheApiService cacheApiService) {
        this.cacheApiService = cacheApiService;
    }

    public long getPAJHash(String programAreaCode, String jurisdictionCode)
    {
        long hashCode = 0;

        if(!((programAreaCode==null || programAreaCode.isEmpty()) || (jurisdictionCode==null || jurisdictionCode.isEmpty()))){
            try
            {
                Integer programAreaNumericID =  Integer.valueOf(cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES_WITH_NBS_UID.name(), programAreaCode));
                Integer jurisdictionNumericID = Integer.valueOf(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_WITH_NBS_UID.name(), jurisdictionCode));
                hashCode = (jurisdictionNumericID.longValue() * 100000L) + programAreaNumericID.longValue();
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
        }
        return hashCode;
    }

    /**
     * Returns a collection of Long objects representing the list of Hash Codes for
     * the given program area and jurisdiction.  This will usually be a single hash
     * code, but when the jurisdiction code is ALL, a hash code for each jurisdiction
     * in the jurisdictionMap is created.
     */
    public Collection<Object> getPAJHashList(String programAreaCode, String jurisdictionCode) throws DataProcessingException {
        ArrayList<Object>  arrayList = new ArrayList<>();
        if(jurisdictionCode.equals("ALL"))
        {
            var keySet = cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_MAP_WITH_NBS_UID_KEY_SET.name(), "");
            Set<String> jurisdictionKeys = Arrays.stream(keySet.split(", ")).collect(Collectors.toSet());
            for (String jCode : jurisdictionKeys) {
                arrayList.add(getPAJHash(programAreaCode, jCode));
            }
        }
        else
        {
            arrayList.add(getPAJHash(programAreaCode, jurisdictionCode));
        }
        return arrayList;
    }

}
