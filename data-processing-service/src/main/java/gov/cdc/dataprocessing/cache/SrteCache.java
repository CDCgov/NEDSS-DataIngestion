package gov.cdc.dataprocessing.cache;

import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;

import java.util.*;
import java.util.stream.Collectors;

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

    public static List<ElrXref> elrXrefsList = new ArrayList<>();

    public static List<ConditionCode> conditionCodes = new ArrayList<>();
    public static TreeMap<String, String> coInfectionConditionCode = new TreeMap<>();
    public static TreeMap<String, String> investigationFormConditionCode = new TreeMap<>();

    public static Optional<ElrXref> findRecordForElrXrefsList(String fromCodeSetNm, String fromCode, String toCodeSetNm) {
        return elrXrefsList.stream()
                .filter(x -> x.getFromCodeSetNm().equals(fromCodeSetNm))
                .filter(x -> x.getFromCode().equals(fromCode))
                .filter(x -> x.getToCodeSetNm().equals(toCodeSetNm))
                .findFirst();
    }

    public static boolean checkWhetherPAIsStdOrHiv(String paCode) {
        if (paCode == null) {
            return false;
        }
        return jurisdictionCodeMapWithNbsUid.containsKey(paCode);
    }

    public static Optional<ConditionCode> findConditionCodeByDescription(String description) {
        return conditionCodes.stream()
                .filter(Objects::nonNull) // Filter out null elements
                .filter(cc -> Objects.equals(cc.getConditionShortNm(), description))
                .findFirst();
    }

}
