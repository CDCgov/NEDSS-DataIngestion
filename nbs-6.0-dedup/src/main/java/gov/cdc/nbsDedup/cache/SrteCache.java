package gov.cdc.nbsDedup.cache;


import java.util.*;

public class SrteCache {
    public static TreeMap<String, Integer> programAreaCodesMapWithNbsUid = new TreeMap<>();
    public static TreeMap<String, Integer> jurisdictionCodeMapWithNbsUid = new TreeMap<>();
    public static TreeMap<String, String> codedValuesMap = new TreeMap<>();
    public static TreeMap<String, String> codeDescTxtMap = new TreeMap<>();
    public static TreeMap<String, String> countyCodeByDescMap = new TreeMap<>();
}
