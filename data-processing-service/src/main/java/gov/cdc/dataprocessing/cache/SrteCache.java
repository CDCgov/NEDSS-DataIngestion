package gov.cdc.dataprocessing.cache;

import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
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

    public static Optional<ElrXref> findRecordForElrXrefsList(String fromCodeSetNm, String fromCode, String toCodeSetNm) {
        return elrXrefsList.stream()
                .filter(x -> x.getFromCodeSetNm().equals(fromCodeSetNm))
                .filter(x -> x.getFromCode().equals(fromCode))
                .filter(x -> x.getToCodeSetNm().equals(toCodeSetNm))
                .findFirst();
    }
}
