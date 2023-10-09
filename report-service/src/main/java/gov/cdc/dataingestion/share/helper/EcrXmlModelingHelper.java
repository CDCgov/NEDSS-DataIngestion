package gov.cdc.dataingestion.share.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;

import java.lang.reflect.Field;
import java.util.Map;

public class EcrXmlModelingHelper {
    public Map<String, Object> setupDataMap(Field[] fields, Map<String, Object> dataMap, Object objectInstance) throws EcrCdaXmlException {
        for (Field field : fields) {
            if (!"dataMap".equals(field.getName())) {
                field.setAccessible(true);  // make sure we can access private fields
                try {
                    // Store the field name and its value in the dataMap
                    dataMap.put(field.getName(),  field.get(objectInstance));
                } catch (IllegalAccessException e) {
                    throw new EcrCdaXmlException(e.getMessage());
                }
            }
        }

        return dataMap;
    }
}
