package gov.cdc.srtedataservice.cache_model;

import gov.cdc.srtedataservice.repository.nbs.srte.model.ConditionCode;
import gov.cdc.srtedataservice.repository.nbs.srte.model.ElrXref;

import java.util.*;

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
public class SrteCache {
    // usage: SrteCache.loinCodeWithComponentNameMap.get(resVO.getResultedTestCd());
    public static HashMap<String, String> loinCodeWithComponentNameMap = new HashMap<>();
    // usage: SrteCache.labResultByDescMap.get(susVO.getCodedResultValue());
    public static HashMap<String, String> labResultByDescMap = new HashMap<>();
    //usage: SrteCache.labResultWithOrganismNameIndMap.get(resVO.getCodedResultValue());
    public static HashMap<String, String> labResultWithOrganismNameIndMap = new HashMap<>();
    //usage: SrteCache.snomedCodeByDescMap.get(resVO.getCodedResultValue());
    public static HashMap<String, String> snomedCodeByDescMap = new HashMap<>();
    // usage: aOELOINCs.containsKey(observationDto.getCd())
    public static HashMap<String, String> loincCodesMap = new HashMap<>();
    // usage: codeMap.containsKey(raceDT.getRaceCd())
    public static HashMap<String, String> raceCodesMap = new HashMap<>();
    // usage: SrteCache.programAreaCodesMap.get(labVO.getProgramArea());
    public static HashMap<String, String> programAreaCodesMap = new HashMap<>();
    // usage: SrteCache.programAreaCodesMapWithNbsUid.get(programAreaCode);
    public static HashMap<String, Integer> programAreaCodesMapWithNbsUid = new HashMap<>();
    // usage: SrteCache.jurisdictionCodeMap.get(labVO.getJurisdiction());
    public static HashMap<String, String> jurisdictionCodeMap = new HashMap<>();
    // usage: SrteCache.jurisdictionCodeMapWithNbsUid.get(jurisdictionCode);
    public static HashMap<String, Integer> jurisdictionCodeMapWithNbsUid = new HashMap<>();

    public static HashMap<String, String> codedValuesMap = new HashMap<>();

    public static HashMap<String, String> codeDescTxtMap = new HashMap<>();
    public static HashMap<String, String> countyCodeByDescMap = new HashMap<>();

    public static List<ElrXref> elrXrefsList = new ArrayList<>();

    public static List<ConditionCode> conditionCodes = new ArrayList<>();
    // usage: (SrteCache.coInfectionConditionCode.containsKey(edxLabInformationDT.getConditionCode()));
    public static HashMap<String, String> coInfectionConditionCode = new HashMap<>();
    // usage: condAndFormCdTreeMap.get(phcDT.getCd());
    // (SrteCache.investigationFormConditionCode.containsKey(conditionCode))
    public static HashMap<String, String> investigationFormConditionCode = new HashMap<>();

    public static ElrXref findRecordForElrXrefsList(String fromCodeSetNm, String fromCode, String toCodeSetNm) {
        var option = elrXrefsList.stream()
                .filter(x -> x.getFromCodeSetNm().equals(fromCodeSetNm))
                .filter(x -> x.getFromCode().equals(fromCode))
                .filter(x -> x.getToCodeSetNm().equals(toCodeSetNm))
                .findFirst();
        return option.orElse(null);
    }

    public static boolean checkWhetherPAIsStdOrHiv(String paCode) {
        if (paCode == null) {
            return false;
        }
        return jurisdictionCodeMapWithNbsUid.containsKey(paCode);
    }

    public static ConditionCode findConditionCodeByDescription(String description) {
        var option = conditionCodes.stream()
                .filter(Objects::nonNull) // Filter out null elements
                .filter(cc -> Objects.equals(cc.getConditionShortNm(), description))
                .findFirst();
        return option.orElse(null);
    }

}