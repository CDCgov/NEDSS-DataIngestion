package gov.cdc.dataprocessing.utilities.component.jurisdiction;

import gov.cdc.dataprocessing.cache.SrteCache;
import org.springframework.stereotype.Component;

@Component
public class ProgAreaJurisdictionUtil {
    public long getPAJHash(String programAreaCode, String jurisdictionCode)
    {
        long hashCode = 0;

        if(!((programAreaCode==null || programAreaCode.isEmpty()) || (jurisdictionCode==null || jurisdictionCode.isEmpty()))){
            try
            {
                Integer programAreaNumericID = SrteCache.programAreaCodesMapWithNbsUid.get(programAreaCode);
                Integer jurisdictionNumericID = SrteCache.jurisdictionCodeMapWithNbsUid.get(jurisdictionCode);
                hashCode = (jurisdictionNumericID.longValue() * 100000L) + programAreaNumericID.longValue();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return hashCode;
    }
}
