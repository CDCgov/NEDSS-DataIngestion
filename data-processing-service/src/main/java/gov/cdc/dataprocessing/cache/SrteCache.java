package gov.cdc.dataprocessing.cache;

import java.util.TreeMap;

public class SrteCache {
    public static TreeMap<String, String> loincCodesMap = new TreeMap<>();
    public static TreeMap<String, String> raceCodesMap = new TreeMap<>();
    public static TreeMap<String, String> programAreaCodesMap = new TreeMap<>();
    public static TreeMap<String, Integer> programAreaCodesMapWithNbsUid = new TreeMap<>();

    public static TreeMap<String, String> jurisdictionCodeMap = new TreeMap<>();
    public static TreeMap<String, Integer> jurisdictionCodeMapWithNbsUid = new TreeMap<>();

    public static TreeMap<String, String> codedValuesMap = new TreeMap<>();

    public static TreeMap<String, String> codeDescTxtMap = new TreeMap<>();
    public static TreeMap<String, String> countyCodeByDescMap = new TreeMap<>();


}
