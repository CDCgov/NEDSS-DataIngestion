package gov.cdc.dataingestion.share.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class EcrXmlModelingHelper {
    public Map<String, Object> setupDataMap(Field[] fields, Map<String, Object> dataMap, Object objectInstance) throws EcrCdaXmlException {
        for (Field field : fields) {
            if (!"dataMap".equals(field.getName())) {
                // Use Spring's ReflectionUtils to make the field accessible
                ReflectionUtils.makeAccessible(field);
                try {
                    // Store the field name and its value in the dataMap
                    dataMap.put(field.getName(), field.get(objectInstance));
                } catch (IllegalAccessException e) {
                    throw new EcrCdaXmlException(e.getMessage());
                }
            }
        }

        return dataMap;
    }
}
