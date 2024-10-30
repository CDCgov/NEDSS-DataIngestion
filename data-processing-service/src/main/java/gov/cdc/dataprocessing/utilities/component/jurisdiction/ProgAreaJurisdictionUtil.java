package gov.cdc.dataprocessing.utilities.component.jurisdiction;

import gov.cdc.dataprocessing.cache.SrteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Component
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
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class ProgAreaJurisdictionUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProgAreaJurisdictionUtil.class); // NOSONAR

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
    public Collection<Object> getPAJHashList(String programAreaCode, String jurisdictionCode)
    {
        ArrayList<Object>  arrayList = new ArrayList<>();
        if(jurisdictionCode.equals("ALL"))
        {
            //get key set
            Set<String> jurisdictionKeys = SrteCache.jurisdictionCodeMapWithNbsUid.keySet();
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
