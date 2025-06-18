package gov.cdc.dataprocessing.cache;

import java.util.Map;
import java.util.TreeMap;

public class OdseCache {
    public static Map<Object,Object> fromPrePopFormMapping = new TreeMap<>();
    public static Map<Object,Object> toPrePopFormMapping = new TreeMap<>();
    public static Map<Object,Object> dmbMap = new TreeMap<>();
    public static Map<Object,Object> map = new TreeMap<>();


    public static TreeMap<Object,Object> DMB_QUESTION_MAP = new TreeMap<>();
    public static String GUEST_LIST_HASHED_PA_J = "";
    public static String OWNER_LIST_HASHED_PA_J = "";

}
