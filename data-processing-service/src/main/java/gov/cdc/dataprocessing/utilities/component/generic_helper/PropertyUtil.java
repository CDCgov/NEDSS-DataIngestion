package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.cache.PropertyUtilCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;

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
public class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class); // NOSONAR

    @Value("${nbs.data.hiv_program_areas}")
    private String hivProgArea = "";

    public boolean isHIVProgramArea(String pa) {
        if(!hivProgArea.isEmpty() && PropertyUtilCache.cachedHivList.isEmpty()){
            cachedHivProgramArea();
        }

        if(pa==null){
            return false;
        }
        else return !PropertyUtilCache.cachedHivList.isEmpty() && PropertyUtilCache.cachedHivList.contains(pa.toUpperCase());
    }


    private void cachedHivProgramArea(){
        try {
            String delim=",";
            String line= hivProgArea;
            StringTokenizer tokens = new StringTokenizer(line, delim, true);
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if (!delim.equals(token)) {
                    PropertyUtilCache.cachedHivList.add(token.toUpperCase().trim());
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }

}
