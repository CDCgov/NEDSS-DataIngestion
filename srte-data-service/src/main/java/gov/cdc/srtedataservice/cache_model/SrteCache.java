package gov.cdc.srtedataservice.cache_model;

import gov.cdc.srtedataservice.repository.nbs.srte.model.ConditionCode;
import gov.cdc.srtedataservice.repository.nbs.srte.model.ElrXref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
@SuppressWarnings({"java:S125", "java:S1118", "java:S1104", "java:S1319", "java:S1444", "java:S2386"})
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
