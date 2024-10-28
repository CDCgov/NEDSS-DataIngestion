package gov.cdc.dataprocessing.utilities;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

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
public class DataParserForSql {

    public static  <T> T parseValue(Object value, Class<T> type) {
        if (value == null) {
            return null;
        }

        if (type == Long.class) {
            return type.cast(Long.valueOf(value.toString()));
        } else if (type == String.class) {
            return type.cast(value.toString());
        } else if (type == Timestamp.class) {
            return type.cast(Timestamp.valueOf(value.toString()));
        } else if (type == Integer.class) {
            return type.cast(Integer.valueOf(value.toString()));
        } else if (type == BigDecimal.class) {
            return type.cast(new BigDecimal(value.toString()));
        }

        return null;
    }

    public static boolean dataNotNull(Object string) {
        return string != null;
    }

    public static boolean resultValidCheck(List<Object[]> results) {
       return results != null && !results.isEmpty();
    }
}
