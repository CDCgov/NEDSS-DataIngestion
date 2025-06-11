package gov.cdc.srtedataservice.utilities;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


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
