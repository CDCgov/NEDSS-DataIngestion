package gov.cdc.dataprocessing.cache;

import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;

import java.util.*;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class SrteCache {
    public static HashMap<String, String> loinCodeWithComponentNameMap = new HashMap<>();
    public static HashMap<String, String> labResultByDescMap = new HashMap<>();
    public static HashMap<String, String> labResultWithOrganismNameIndMap = new HashMap<>();
    public static HashMap<String, String> snomedCodeByDescMap = new HashMap<>();
    public static HashMap<String, String> loincCodesMap = new HashMap<>();
    public static HashMap<String, String> raceCodesMap = new HashMap<>();
    public static HashMap<String, String> programAreaCodesMap = new HashMap<>();
    public static HashMap<String, Integer> programAreaCodesMapWithNbsUid = new HashMap<>();

    public static HashMap<String, String> jurisdictionCodeMap = new HashMap<>();
    public static HashMap<String, Integer> jurisdictionCodeMapWithNbsUid = new HashMap<>();

    public static HashMap<String, String> codedValuesMap = new HashMap<>();

    public static HashMap<String, String> codeDescTxtMap = new HashMap<>();
    public static HashMap<String, String> countyCodeByDescMap = new HashMap<>();

    public static List<ElrXref> elrXrefsList = new ArrayList<>();

    public static List<ConditionCode> conditionCodes = new ArrayList<>();
    public static HashMap<String, String> coInfectionConditionCode = new HashMap<>();
    public static HashMap<String, String> investigationFormConditionCode = new HashMap<>();

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
