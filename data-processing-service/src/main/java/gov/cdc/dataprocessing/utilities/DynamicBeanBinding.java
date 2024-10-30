package gov.cdc.dataprocessing.utilities;

import gov.cdc.dataprocessing.exception.DataProcessingException;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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
public class DynamicBeanBinding {
    private static Map<Object, Object> beanMethodMap = new HashMap<>();



    /**
     * populateBean populates the metadata relevant colNm to the Bean Object and
     * returns
     */
    public static void populateBean(Object bean, String colNm, String colVal)
            throws DataProcessingException {

        try {

            //final SimpleDateFormat DATE_STORE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

            String methodName = getSetterName(colNm);
            Map<Object, Object> methodMap = getMethods(bean.getClass());

            Method method = (Method) methodMap.get(methodName);
            if(method==null){
                return;
            }
            Object[] parmTypes = method.getParameterTypes();
            String pType = ((Class<?>) parmTypes[0]).getName();
            Object[] arg = { "" };
            Object[] nullArg = null;

            if (colVal!=null && !colVal.equals("")) {
                if (pType.equalsIgnoreCase("java.sql.Timestamp")) {

                    Timestamp ts = new Timestamp(new SimpleDateFormat("MM/dd/yyyy")
                            .parse(colVal).getTime());
                    arg[0] = ts;

                } else if (pType.equalsIgnoreCase("java.lang.String")) {
                    arg[0] = colVal;

                } else if (pType.equalsIgnoreCase("java.lang.Long")) {
                    arg[0] = Long.valueOf(colVal);


                } else if (pType.equalsIgnoreCase("java.lang.Integer")) {
                    arg[0] = Integer.valueOf(colVal);

                } else if (pType.equalsIgnoreCase("java.math.BigDecimal")) {
                    arg[0] = BigDecimal.valueOf(Long.parseLong(colVal));

                } else if (pType.equalsIgnoreCase("boolean")) {
                    arg[0] = colVal;
                }
            }else {
                arg[0] = nullArg;
            }
            try {
                if(colVal==null) {
                    Object[] nullargs = { null };
                    method.invoke(bean, nullargs);
                }else
                    method.invoke(bean, arg);
//                logger.debug("Successfully called methodName for bean " + bean
//                        + " with value " + colVal);
            } catch (Exception e) {
                throw new DataProcessingException(e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    private static String getSetterName(String columnName) throws DataProcessingException {
        try {
            StringBuilder sb = new StringBuilder("set");
            StringTokenizer st = new StringTokenizer(columnName, "_");
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                s = s.substring(0, 1).toUpperCase()
                        + s.substring(1).toLowerCase();
                sb.append(s);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    @SuppressWarnings("unchecked")
    private static Map<Object, Object> getMethods(Class<?> beanClass)
            throws DataProcessingException {
        try {
            if (beanMethodMap.get(beanClass) == null) {
                Method[] gettingMethods = beanClass.getMethods();
                Map<Object, Object> resultMap = new HashMap<>();
                for (Method gettingMethod : gettingMethods) {
                    Method method =  gettingMethod;
                    String methodName = method.getName();
                    Object[] parmTypes = method.getParameterTypes();
                    if (methodName.startsWith("set") && parmTypes.length == 1)
                        resultMap.put(methodName, method);
                }
                beanMethodMap.put(beanClass, resultMap);
            }
            return (Map<Object, Object>) beanMethodMap.get(beanClass);
        } catch (SecurityException e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

}
