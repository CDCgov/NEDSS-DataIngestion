package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.cache.PropertyUtilCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;

@Component

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
