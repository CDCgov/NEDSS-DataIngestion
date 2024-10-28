package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.cache.PropertyUtilCache;
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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class PropertyUtil {

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
            e.printStackTrace();
        }
    }

}
