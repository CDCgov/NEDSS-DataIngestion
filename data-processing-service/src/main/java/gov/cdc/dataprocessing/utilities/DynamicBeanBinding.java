package gov.cdc.dataprocessing.utilities;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class DynamicBeanBinding {
    private static Map<Object, Object> beanMethodMap = new HashMap<>();



    /**
     * populateBean populates the metadata relevant colNm to the Bean Object and
     * returns
     */
    public static void populateBean(Object bean, String colNm, String colVal)
            throws Exception {

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
                throw new Exception(e);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static String getSetterName(String columnName) throws Exception {
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
            throw new Exception(e);
        }
    }


    @SuppressWarnings("unchecked")
    private static Map<Object, Object> getMethods(Class<?> beanClass)
            throws Exception {
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
            throw new Exception(e);
        }
    }

}
