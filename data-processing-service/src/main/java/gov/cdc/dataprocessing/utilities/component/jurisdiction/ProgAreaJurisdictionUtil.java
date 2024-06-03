package gov.cdc.dataprocessing.utilities.component.jurisdiction;

import gov.cdc.dataprocessing.cache.SrteCache;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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
