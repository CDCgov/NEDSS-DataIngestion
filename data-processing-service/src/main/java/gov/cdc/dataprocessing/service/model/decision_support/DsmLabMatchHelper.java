package gov.cdc.dataprocessing.service.model.decision_support;

import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dsma_algorithm.Algorithm;
import gov.cdc.dataprocessing.model.dsma_algorithm.CodedType;
import gov.cdc.dataprocessing.model.dsma_algorithm.ElrCriteriaType;
import gov.cdc.dataprocessing.model.dsma_algorithm.SendingSystemType;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueNumericDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueTxtDto;
import gov.cdc.dataprocessing.service.model.wds.WdsReport;
import gov.cdc.dataprocessing.service.model.wds.WdsValueCodedReport;
import gov.cdc.dataprocessing.service.model.wds.WdsValueNumericReport;
import gov.cdc.dataprocessing.service.model.wds.WdsValueTextReport;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Setter
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class DsmLabMatchHelper {
    private static final Logger logger = LoggerFactory.getLogger(DsmLabMatchHelper.class); // NOSONAR

    static final String NULL_STRING = "null";
    //hold quick access values for this Workflow Decision Support algorithm
    private Map<String,String> systemNameMap = new HashMap<String,String>(); //name-OID
    private Map<String,String> systemOidMap = new HashMap<String,String>(); //OID-name
    private Map<String,String> resultedTestCodeMap = new HashMap<String,String>(); //test code/test desc
    //these resulted test lists need to be collections and not maps because a test could repeat
    private List<TestCodedValue> resultedTestCodedValueList = new ArrayList<TestCodedValue>(); //coded values
    private List<TestNumericValue> resultedTestNumericValueList = new ArrayList<TestNumericValue>(); //numeric values
    private List<TestTextValue> resultedTestTextValueList = new ArrayList<TestTextValue>(); //text values
    private Algorithm algorithm = null;
    private Algorithm algorithmDocument = null;
    private String algorithmNm = "";
    private String andOrLogic = "";
    private Boolean algorithmIsAndLogic = false;
    private Boolean algorithmIsOrLogic = false;

    public DsmLabMatchHelper() {

    }

    /**
     * Constructor - expects algorithm payload (see DSMAlgorithm.xsd)
     * Get the values from the algorithm and populate class variables.
     * @param algorithmDocument
     */
    public DsmLabMatchHelper(Algorithm algorithmDocument) throws DataProcessingException {
        try {
            this.algorithm =algorithmDocument;
            this.algorithmDocument=algorithmDocument;
        } catch (Exception e) {
            throw new DataProcessingException("ELR to Algorithm Matching Failed: DSMLabMatchHelper.Constructor Unable to process Container Document",e);
        }
        if (algorithm.getAlgorithmName() != null) {
            algorithmNm = algorithm.getAlgorithmName();
        }
        //And or Or Algorithm
        if (algorithm.getElrAdvancedCriteria() != null && algorithm.getElrAdvancedCriteria().getAndOrLogic() != null)
            andOrLogic = algorithm.getElrAdvancedCriteria().getAndOrLogic();
        else
            andOrLogic = DecisionSupportConstants.OR_AND_OR_LOGIC; //OR
        if (andOrLogic.equals(DecisionSupportConstants.OR_AND_OR_LOGIC))
            this.algorithmIsOrLogic = true;
        else
            this.algorithmIsAndLogic = true;
        //initialize maps;
        this.systemNameMap = new HashMap<String,String>();//name-OID
        this.systemOidMap = new HashMap<String,String>(); //OID-name
        //populate receiving systems map if present
        if(algorithm.getApplyToSendingSystems()!=null ){
            try {
                SendingSystemType sendingSystemType  =algorithm.getApplyToSendingSystems();
                for(int i=0; i<sendingSystemType.getSendingSystem().size(); i++){
                    CodedType sendingSystemCodedType= sendingSystemType.getSendingSystem().get(i);
                    String receivingSystemOid = sendingSystemCodedType.getCodeSystemVersionID();
                    String receivingSystemDescTxt = sendingSystemCodedType.getCode();

                    if(receivingSystemOid!=null){
                        //criteriaBufferKey.append(spacer);
                        if (receivingSystemDescTxt != null)
                            systemOidMap.put(receivingSystemOid, receivingSystemDescTxt);
                        else
                            systemOidMap.put(receivingSystemOid, NULL_STRING);
                    }
                    if(receivingSystemDescTxt!=null){
                        if (receivingSystemOid != null)
                            systemNameMap.put(receivingSystemDescTxt,receivingSystemOid);
                        else
                            systemNameMap.put(receivingSystemDescTxt, NULL_STRING);
                    }
                } //for
            } catch (Exception e) {
                throw new DataProcessingException("ELR to Algorithm Matching Failed: DSMLabMatchHelper.Constructor Unable to process specified sending systems",e);
            }
        }

        //next populate resultedTestCodeMap
        if(algorithm.getElrAdvancedCriteria()!=null && algorithm.getElrAdvancedCriteria().getElrCriteria()!=null) {
            List<ElrCriteriaType> elrCriteriaArray = algorithm.getElrAdvancedCriteria().getElrCriteria();  //getElrCriteriaArray().
            try {
                for(ElrCriteriaType elrCriteria : elrCriteriaArray){
                    CodedType elrResultTestCriteriaType= elrCriteria.getResultedTest();
                    if(elrResultTestCriteriaType!=null && elrResultTestCriteriaType.getCode()!=null && elrResultTestCriteriaType.getCode().length()>0){
                        String code = elrResultTestCriteriaType.getCode();
                        String codeDesc = elrResultTestCriteriaType.getCodeDescTxt();
                        resultedTestCodeMap.put(code,  codeDesc);
                        if (elrCriteria.getElrCodedResultValue() != null) {
                            TestCodedValue thisCodedValue = new TestCodedValue();
                            thisCodedValue.setTestCode(code);
                            thisCodedValue.setTestCodeDesc(codeDesc);
                            thisCodedValue.setResultCode(elrCriteria.getElrCodedResultValue().getCode());
                            thisCodedValue.setResultCodeDesc(elrCriteria.getElrCodedResultValue().getCodeDescTxt());
                            resultedTestCodedValueList.add(thisCodedValue);
                        } else if (elrCriteria.getElrTextResultValue() != null) {
                            TestTextValue thisTextValue = new TestTextValue();
                            thisTextValue.setTestCode(code);
                            thisTextValue.setTestCodeDesc(codeDesc);
                            if (elrCriteria.getElrTextResultValue().getTextValue() != null)
                                thisTextValue.setTextValue(elrCriteria.getElrTextResultValue().getTextValue());
                            if (elrCriteria.getElrTextResultValue().getComparatorCode() != null) {
                                if (elrCriteria.getElrTextResultValue().getComparatorCode().getCode() != null)
                                    thisTextValue.setComparatorCode(elrCriteria.getElrTextResultValue().getComparatorCode().getCode());
                                if (elrCriteria.getElrTextResultValue().getComparatorCode().getCodeDescTxt() != null)
                                    thisTextValue.setComparatorCodeDesc(elrCriteria.getElrTextResultValue().getComparatorCode().getCodeDescTxt());
                            }
                            resultedTestTextValueList.add(thisTextValue);
                        } else if (elrCriteria.getElrNumericResultValue() != null) {
                            TestNumericValue thisNumericValue = new TestNumericValue();
                            thisNumericValue.setTestCode(code);
                            thisNumericValue.setTestCodeDesc(codeDesc);
                            //comparator
                            if (elrCriteria.getElrNumericResultValue().getComparatorCode() != null) {
                                if (elrCriteria.getElrNumericResultValue().getComparatorCode().getCode() != null)
                                    thisNumericValue.setComparatorCode(elrCriteria.getElrNumericResultValue().getComparatorCode().getCode());
                                if (elrCriteria.getElrNumericResultValue().getComparatorCode().getCodeDescTxt() != null)
                                    thisNumericValue.setComparatorCodeDesc(elrCriteria.getElrNumericResultValue().getComparatorCode().getCodeDescTxt());
                            }
                            //value1
                            if (elrCriteria.getElrNumericResultValue().getValue1() != null) {
                                try {
                                    BigDecimal algorithmNumericValue1 = new BigDecimal(elrCriteria.getElrNumericResultValue().getValue1());
                                    thisNumericValue.setValue1(algorithmNumericValue1);
                                } catch (Exception e) {
                                    logger.info(e.getMessage());
                                }
                            }
                            //separator
                            if (elrCriteria.getElrNumericResultValue().getSeperatorCode() != null)
                                thisNumericValue.setSeparatorCode(elrCriteria.getElrNumericResultValue().getSeperatorCode());
                            //value2
                            if (elrCriteria.getElrNumericResultValue().getValue2() != null)
                                try {
                                    BigDecimal algorithmNumericValue2 = new BigDecimal(elrCriteria.getElrNumericResultValue().getValue2());
                                    thisNumericValue.setValue2(algorithmNumericValue2);
                                } catch (Exception e) {
                                    logger.info(e.getMessage());
                                }
                            //units
                            if (elrCriteria.getElrNumericResultValue().getUnit() != null) {
                                if (elrCriteria.getElrNumericResultValue().getUnit().getCode() != null)
                                    thisNumericValue.setUnitCode(elrCriteria.getElrNumericResultValue().getUnit().getCode());
                                if (elrCriteria.getElrNumericResultValue().getUnit().getCodeDescTxt() != null)
                                    thisNumericValue.setUnitCodeDesc(elrCriteria.getElrNumericResultValue().getUnit().getCodeDescTxt());
                            }
                            resultedTestNumericValueList.add(thisNumericValue);
                        }
                    } //elrResultTestCriteriaType not null
                } //for
            } catch (Exception e) {
                throw new DataProcessingException("DSMMatchHelper.get criteria Exception thrown",e);
            }
        }
    } //end Constructor

    /**
     * isThisLabAMatch
     * @param resultedTestCodeColl - test codes in the incoming lab
     * @param resultedTestColl - lab results in the incoming lab
     * @param sendingFacilityClia - Lab clia
     * @param sendingFacilityName - Lab name
     * @return true if this algorithm is a match, false otherwise
     */
    public WdsReport isThisLabAMatch(Collection<String> resultedTestCodeColl,
                                     Collection<ObservationContainer> resultedTestColl, String sendingFacilityClia, String sendingFacilityName) {
        //Is this Decision Support Algorithm looking for the lab test(s) in these Lab results?
        WdsReport wdsReport = new WdsReport();
        boolean testNotMatched = testsDoNotMatch(resultedTestCodeColl, resultedTestCodeMap, andOrLogic);
        if (testNotMatched)
        {
            var report = new WdsReport();
            report.setAlgorithmMatched(false);
            return report;
        }

        //Is this Decision Support Algorithm only for certain facilities? (Not ALL)
        if (systemOidMap != null && !systemOidMap.isEmpty())
        {
            boolean nameMatched = true;
            boolean oidMatched = true;
            if (sendingFacilityClia != null
                    && !sendingFacilityClia.isEmpty()
                    && systemOidMap.containsKey(sendingFacilityClia)) {
                oidMatched = true;
            }
            else {
                oidMatched = false;
            }

            if (sendingFacilityName != null && !sendingFacilityName.isEmpty() && systemNameMap.containsKey(sendingFacilityName)) {
                nameMatched = true;
            }
            else {
                if (sendingFacilityName != null) {
                    //logger.debug("Algorithm matches test code and Sending System OID but Sending System Name of " + sendingFacilityName +"  not in list of Specified Sending Systems");
                }
                nameMatched = false;
            }
            if (oidMatched
                    || nameMatched
            ) //GST
            {
                //logger.debug("Algorithm matches either the Sending Facility Name or the Sending Facility Oid");
            }
            else
            {
                return wdsReport; //specified facility(s) do not match so this algorithm is not a match
            }
        }

        //test is in algorithm, check if value matches
        try {
            wdsReport = testIfAlgorthmMatchesLab(resultedTestColl, resultedTestCodedValueList, resultedTestTextValueList, resultedTestNumericValueList);
        }
        catch (Exception e) {
            logger.info(e.getMessage());
        }
        return wdsReport;
    }

    /**
     * See if the values specified in the Algorithm match the Lab Test
     *
     * @param resultedTestColl
     * @param testCodedValueList
     * @param testTextValueList
     * @param testNumericValueList
     * @return true if match found, false if not
     */
    private WdsReport testIfAlgorthmMatchesLab(
            Collection<ObservationContainer> resultedTestColl,
            List<TestCodedValue> testCodedValueList,
            List<TestTextValue> testTextValueList,
            List<TestNumericValue> testNumericValueList) throws DataProcessingException {
        WdsReport wdsReport = new WdsReport();
        try {
            // CODED VALUE
            var obsValueMatched = checkingObsValueMatched(resultedTestColl, testCodedValueList, wdsReport);
            if (obsValueMatched) {
                return wdsReport;
            }

            // TEXT VALUE
            var obsTextMatched = checkingObsTextMatched(
                    resultedTestColl,
                    testTextValueList,
                    wdsReport
            );

            if (obsTextMatched) {
                return wdsReport;
            }

            // NUMERIC
            var obsNumericMatched = checkingObsNumericMatched(
                    resultedTestColl,
                    testNumericValueList,
                    wdsReport
            );
            if (obsNumericMatched) {
                return wdsReport;
            }


        } catch (Exception e) {
            throw new DataProcessingException("DSMMatchHelper.isThisLabAMatch Exception thrown",e);
        }
        ////All test complete!
        if (algorithmIsOrLogic)
        {
            wdsReport.setAlgorithmMatched(false);
            return wdsReport; //nothing matched
        }
        if (algorithmIsAndLogic)
        {
            wdsReport.setAlgorithmMatched(true);
            return wdsReport; //everything matched
        }

        wdsReport.setAlgorithmMatched(false);
        return wdsReport; //failsafe
    }

    private boolean checkingObsNumericMatched(
            Collection<ObservationContainer> resultedTestColl,
            List<TestNumericValue> testNumericValueList,
            WdsReport wdsReport
    ) {
        for (TestNumericValue algorithmNumericValue : testNumericValueList)
        {
            boolean numericAlgorithmMatched = false;
            for (ObservationContainer o : resultedTestColl) {
                ObservationContainer resultObsVO = o;
                String testCode = null;
                if (resultObsVO.getTheObservationDto().getCd() != null)
                {
                    testCode = resultObsVO.getTheObservationDto().getCd();
                }
                else if (resultObsVO.getTheObservationDto().getAltCd() != null)
                {
                    testCode = resultObsVO.getTheObservationDto().getAltCd();
                }

                if (algorithmNumericValue.getTestCode().equalsIgnoreCase(testCode))
                {
                    if (resultObsVO.getTheObsValueNumericDtoCollection() != null) {
                        var numericReport = new WdsValueNumericReport();
                        for (ObsValueNumericDto obsValueNumericDT : resultObsVO.getTheObsValueNumericDtoCollection()) {
                            //check if the units match (if present)
                            if (algorithmNumericValue.getUnitCode() != null) {
                                String labUnits = obsValueNumericDT.getNumericUnitCd();
                                if (labUnits == null)
                                {
                                    continue; //no units match here
                                }
                                else if (algorithmNumericValue.getUnitCode().equals(labUnits.trim())) {
                                    //logger.debug("Algorithm units match with lab units of " +labUnits);
                                }
                                else {
                                    //logger.debug("Algorithm units of " +algorithmNumericValue.getUnitCode() +"does not match with lab units of "+labUnits);
                                    continue; //no units match here
                                }
                            }
                            //if the unlikely case the incoming lab has a comparator that is not equal, can't definitively match >,< >=, <= and <>
                            if (obsValueNumericDT.getComparatorCd1() != null && !obsValueNumericDT.getComparatorCd1().equals(NEDSSConstant.EQUAL_LOGIC)) {
                                String labComparator = obsValueNumericDT.getComparatorCd1().trim();
                                if (labComparator.equals(NEDSSConstant.LESS_THAN_LOGIC) || labComparator.equals(NEDSSConstant.LESS_THAN_OR_EQUAL_LOGIC)
                                        || labComparator.equals(NEDSSConstant.GREATER_THAN_LOGIC) || labComparator.equals(NEDSSConstant.GREATER_THAN_OR_EQUAL_LOGIC)
                                        || labComparator.equals(NEDSSConstant.NOT_EQUAL_LOGIC2))
                                {
                                    continue; //skip this result
                                }
                            }
                            boolean isTiterLab = false;
                            if (obsValueNumericDT.getSeparatorCd() != null && !obsValueNumericDT.getSeparatorCd().trim().isEmpty())
                            {
                                String labSeparator = obsValueNumericDT.getSeparatorCd().trim();
                                //can't handle separators of /, -, or +
                                if (!labSeparator.equals(NEDSSConstant.COLON)) {
                                    //logger.debug("Lab has numeric result with separator that is not a colon [ " +labSeparator + "]");
                                    continue;//skip this result
                                }
                                if (obsValueNumericDT.getNumericValue2() == null) {
                                    //logger.debug("Lab has numeric result with colon separator but no Numeric Value2?");
                                    continue;//skip this result
                                }

                                //numeric value 1 is 1 in the ratio i.e. =1:8, =1:16, =1:32
                                if (obsValueNumericDT.getNumericValue1() != null)
                                {
                                    BigDecimal bdOne = new BigDecimal(1);
                                    if (obsValueNumericDT.getNumericValue1().compareTo(bdOne) == 0) {
                                        //logger.debug("Lab has titer value");
                                        isTiterLab = true;
                                    } else {
                                        //logger.debug("Lab looks like titer but numeric value1 is [" + obsValueNumericDT.getNumericValue1() +"] ");
                                        continue;//skip this result
                                    }
                                }
                            }

                            if (algorithmNumericValue.getComparatorCode().equals(NEDSSConstant.EQUAL_LOGIC))
                            {
                                //For BigDecimal must use CompareTo and not Equals (using Equals 5.0 is not equal to 5.00, using CompareTo they are equal)

                                numericReport.setCodeType("OBS_NUMERIC_VALUE");
                                numericReport.setWdsCode(algorithmNumericValue.getValue1().toString());
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue1()!= null ? obsValueNumericDT.getNumericValue1().toString(): "");
                                numericReport.setInputCode2(obsValueNumericDT.getNumericValue2()!= null ? obsValueNumericDT.getNumericValue2().toString(): "");

                                numericReport.setOperator(NEDSSConstant.EQUAL_LOGIC);

                                if (!isTiterLab && obsValueNumericDT.getNumericValue1() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue1()) == 0) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                                else if (isTiterLab && obsValueNumericDT.getNumericValue2() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue2()) == 0) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }

                            }
                            else if (algorithmNumericValue.getComparatorCode().equals(NEDSSConstant.GREATER_THAN_LOGIC))
                            {
                                //For BigDecimal must use CompareTo and not Equals (using Equals 5.0 is not equal to 5.00, using CompareTo they are equal)
                                numericReport.setCodeType("OBS_NUMERIC_VALUE");
                                numericReport.setWdsCode(algorithmNumericValue.getValue1().toString());
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue1() != null ? obsValueNumericDT.getNumericValue1().toString() : "");
                                numericReport.setInputCode2(obsValueNumericDT.getNumericValue2() != null ? obsValueNumericDT.getNumericValue2().toString(): "");
                                numericReport.setOperator(NEDSSConstant.GREATER_THAN_LOGIC);

                                if (!isTiterLab && obsValueNumericDT.getNumericValue1() != null
                                        && obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) == 1) {
                                    numericReport.setMatchedFound(true);

                                    numericAlgorithmMatched = true;
                                }
                                else if (isTiterLab && obsValueNumericDT.getNumericValue2() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue2()) == -1) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                            }
                            else if (algorithmNumericValue.getComparatorCode().equals(NEDSSConstant.GREATER_THAN_OR_EQUAL_LOGIC))
                            {
                                numericReport.setCodeType("OBS_NUMERIC_VALUE");
                                numericReport.setWdsCode(algorithmNumericValue.getValue1().toString());
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue1() != null ? obsValueNumericDT.getNumericValue1().toString() : "");
                                numericReport.setInputCode2(obsValueNumericDT.getNumericValue2() != null ? obsValueNumericDT.getNumericValue2().toString(): "");
                                numericReport.setOperator(NEDSSConstant.GREATER_THAN_OR_EQUAL_LOGIC);

                                if (!isTiterLab && obsValueNumericDT.getNumericValue1() != null
                                        && (obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) == 0 ||
                                        obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) == 1)
                                ) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                                else if (isTiterLab && obsValueNumericDT.getNumericValue2() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue2()) == 0
                                        || obsValueNumericDT.getNumericValue2() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue2()) == -1
                                ) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                            }
                            else if (algorithmNumericValue.getComparatorCode().equals(NEDSSConstant.LESS_THAN_LOGIC))
                            {

                                numericReport.setCodeType("OBS_NUMERIC_VALUE");
                                numericReport.setWdsCode(algorithmNumericValue.getValue1().toString());
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue1() != null ? obsValueNumericDT.getNumericValue1().toString() : "");
                                numericReport.setInputCode2(obsValueNumericDT.getNumericValue2() != null ? obsValueNumericDT.getNumericValue2().toString() : "");
                                numericReport.setOperator(NEDSSConstant.LESS_THAN_LOGIC);

                                if (!isTiterLab && obsValueNumericDT.getNumericValue1() != null
                                        && (obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) == -1)
                                ) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                                else if (isTiterLab && obsValueNumericDT.getNumericValue2() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue2()) == 1
                                ) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                            }
                            else if (algorithmNumericValue.getComparatorCode().equals(NEDSSConstant.LESS_THAN_OR_EQUAL_LOGIC))
                            {

                                numericReport.setCodeType("OBS_NUMERIC_VALUE");
                                numericReport.setWdsCode(algorithmNumericValue.getValue1().toString());
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue1() != null ? obsValueNumericDT.getNumericValue1().toString() : "");
                                numericReport.setInputCode2(obsValueNumericDT.getNumericValue2() != null ? obsValueNumericDT.getNumericValue2().toString() : "");
                                numericReport.setOperator(NEDSSConstant.LESS_THAN_OR_EQUAL_LOGIC);

                                if (!isTiterLab && obsValueNumericDT.getNumericValue1() != null
                                        && (obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) == 0
                                        || obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) == -1)
                                ) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;

                                }
                                else if (isTiterLab && obsValueNumericDT.getNumericValue2() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue2()) == 0
                                        || obsValueNumericDT.getNumericValue2() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue2()) == 1
                                ) {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                            }
                            else if (algorithmNumericValue.getComparatorCode().equals(NEDSSConstant.NOT_EQUAL_LOGIC))
                            {
                                numericReport.setCodeType("OBS_NUMERIC_VALUE");
                                numericReport.setWdsCode(algorithmNumericValue.getValue1().toString());
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue1() != null ? obsValueNumericDT.getNumericValue1().toString() : "");
                                numericReport.setInputCode2(obsValueNumericDT.getNumericValue2() != null ? obsValueNumericDT.getNumericValue2().toString() : "");
                                numericReport.setOperator(NEDSSConstant.NOT_EQUAL_LOGIC);

                                if (!isTiterLab && obsValueNumericDT.getNumericValue1() != null
                                        && obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) != 0)
                                {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                                else if (isTiterLab && obsValueNumericDT.getNumericValue2() != null
                                        && algorithmNumericValue.getValue1() != null
                                        && algorithmNumericValue.getValue1().compareTo(obsValueNumericDT.getNumericValue2()) != 0)
                                {
                                    numericReport.setMatchedFound(true);
                                    numericAlgorithmMatched = true;
                                }
                            }
                            else if (algorithmNumericValue.getComparatorCode().equals(NEDSSConstant.BETWEEN_LOGIC))
                            {
                                numericReport.setCodeType("OBS_NUMERIC_VALUE");
                                numericReport.setWdsCode(algorithmNumericValue.getValue1().toString());
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue1() != null ? obsValueNumericDT.getNumericValue1().toString() : "");
                                numericReport.setInputCode2(obsValueNumericDT.getNumericValue2() != null ? obsValueNumericDT.getNumericValue2().toString() : "");
                                numericReport.setOperator(NEDSSConstant.BETWEEN_LOGIC);
                                if (obsValueNumericDT.getNumericValue1() != null
                                        && (obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) == 0
                                        || obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue1()) == 1)
                                ) {
                                    if (obsValueNumericDT.getNumericValue1() != null && algorithmNumericValue.getValue2() != null
                                            && (obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue2()) == 0
                                            || obsValueNumericDT.getNumericValue1().compareTo(algorithmNumericValue.getValue2()) == -1)
                                    ) {
                                        numericReport.setMatchedFound(true);
                                        numericAlgorithmMatched = true;
                                    }
                                }
                            }
                            else {
                                numericReport.setMatchedFound(false);
                                numericReport.setCodeType("OBS_NUMERIC_VALUE");
                                numericReport.setWdsCode(algorithmNumericValue.getValue1().toString());
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue1() != null ? obsValueNumericDT.getNumericValue1().toString() : "");
                                numericReport.setInputCode1(obsValueNumericDT.getNumericValue2() != null ? obsValueNumericDT.getNumericValue2().toString() : "");
                            }
                            wdsReport.getWdsValueNumericReportList().add(numericReport);

                            if (numericAlgorithmMatched) {
                                if (algorithmIsOrLogic)
                                {
                                    wdsReport.setAlgorithmMatched(true);
                                    return true;
                                }
                            }
                        } //next obsValueNumeric
                    } //obsValueNumericDT collection
                    if (!numericAlgorithmMatched) {
                        //logger.debug("-------Algorithm Numeric Value did NOT match and lab Obs Value Numeric-----------");
                    }
                }//code matched
            } //end resulted tests
            //if no resulted tests matched and we have AND logic
            if (algorithmIsAndLogic && !numericAlgorithmMatched) //result code did not match
            {
                wdsReport.setAlgorithmMatched(true);
                return true;
            }
        }//testNumericValueIter
        return false;
    }

    private boolean checkingObsTextMatched(
            Collection<ObservationContainer> resultedTestColl,
            List<TestTextValue> testTextValueList,
            WdsReport wdsReport
    ) {
        for (TestTextValue algorithmTextValue : testTextValueList)
        {
            boolean textAlgorithmMatched = false;
            for (ObservationContainer o : resultedTestColl) {
                ObservationContainer resultObsVO = o;
                String testCode = null;
                if (resultObsVO.getTheObservationDto().getCd() != null)
                {
                    testCode = resultObsVO.getTheObservationDto().getCd();
                }
                else if (resultObsVO.getTheObservationDto().getAltCd() != null)
                {
                    testCode = resultObsVO.getTheObservationDto().getAltCd();
                }

                if (algorithmTextValue.getTestCode().equalsIgnoreCase(testCode))
                {
                    if (resultObsVO.getTheObsValueTxtDtoCollection() != null) {
                        for (ObsValueTxtDto obsValueTxtDT : resultObsVO.getTheObsValueTxtDtoCollection())
                        {
                            var wdsValueText = new WdsValueTextReport();
                            if (obsValueTxtDT.getTxtTypeCd() == null
                                    || obsValueTxtDT.getTxtTypeCd().trim().equals("")
                                    || obsValueTxtDT.getTxtTypeCd().equalsIgnoreCase("O"))
                            {//NBSCentral #11984: to avoid comparing with the notes
                                wdsValueText.setMatchedFound(true);
                                if (algorithmTextValue.getComparatorCode().equals(NEDSSConstant.EQUAL_LOGIC))
                                {
                                    if (obsValueTxtDT.getValueTxt() != null && obsValueTxtDT.getValueTxt().equals(algorithmTextValue.getTextValue()))
                                    {
                                        textAlgorithmMatched = true;
                                    }
                                }
                                else if (algorithmTextValue.getComparatorCode().equals(NEDSSConstant.CONTAINS_LOGIC))
                                {
                                    if (obsValueTxtDT.getValueTxt() != null && obsValueTxtDT.getValueTxt().contains(algorithmTextValue.getTextValue()))
                                    {
                                        textAlgorithmMatched = true;
                                    }
                                }
                                else if (algorithmTextValue.getComparatorCode().equals(NEDSSConstant.STARTS_WITH_LOGIC))
                                {
                                    if (obsValueTxtDT.getValueTxt() != null && obsValueTxtDT.getValueTxt().startsWith(algorithmTextValue.getTextValue()))
                                    {
                                        textAlgorithmMatched = true;
                                    }
                                }
                                else if (algorithmTextValue.getComparatorCode().equals(NEDSSConstant.NOT_EQUAL_LOGIC))
                                {
                                    if (obsValueTxtDT.getValueTxt() != null && obsValueTxtDT.getValueTxt().compareTo(algorithmTextValue.getTextValue()) != 0)
                                    {
                                        textAlgorithmMatched = true;
                                    }
                                }
                                else if (algorithmTextValue.getComparatorCode().equals(NEDSSConstant.NOTNULL_LOGIC))
                                {
                                    if (obsValueTxtDT.getValueTxt() != null && (obsValueTxtDT.getValueTxt().length() > 0))
                                    {
                                        textAlgorithmMatched = true;
                                    }
                                }
                                else
                                {
                                    wdsValueText.setMatchedFound(false);
                                }
                                wdsValueText.setInputCode(obsValueTxtDT.getValueTxt());
                                wdsValueText.setWdsCode(algorithmTextValue.getTextValue());
                                wdsValueText.setCodeType("OBS_VALUE_TEXT");
                                wdsReport.getWdsValueTextReportList().add(wdsValueText);
                            }
                        } //subObs has next
                        if (textAlgorithmMatched) {
                            if (algorithmIsOrLogic)
                            {
                                wdsReport.setAlgorithmMatched(true);
                                return true;
                            }
                        }

                        if (algorithmIsAndLogic
                                && !textAlgorithmMatched) //result code did not match
                        {
                            wdsReport.setAlgorithmMatched(false);
                            return true;
                        }
                    } //obsValueTxt present
                }//test code matches
            } //next lab test

        } //next text algorithm result

        return false;
    }
    private boolean checkingObsValueMatched(
            Collection<ObservationContainer> resultedTestColl,
            List<TestCodedValue> testCodedValueList,
            WdsReport wdsReport
    ) {
        for (TestCodedValue algorithmCodedValue : testCodedValueList)
        {
            boolean textAlgorithmMatched = false;
            for (ObservationContainer o : resultedTestColl)
            {
                ObservationContainer resultObsVO = o;
                String testCode = null;
                if (resultObsVO.getTheObservationDto().getCd() != null)
                {
                    testCode = resultObsVO.getTheObservationDto().getCd();
                }
                else if (resultObsVO.getTheObservationDto().getAltCd() != null)
                {
                    testCode = resultObsVO.getTheObservationDto().getAltCd();
                }
                else
                {
                    continue; //no test code?
                }

                if (algorithmCodedValue.getTestCode().equalsIgnoreCase(testCode))
                {
                    if (resultObsVO.getTheObsValueCodedDtoCollection() != null) {
                        for (ObsValueCodedDto obsValueCodedDT : resultObsVO.getTheObsValueCodedDtoCollection()) {
                            //Test code match?
                            if (obsValueCodedDT.getCode() != null
                                    && algorithmCodedValue.getResultCode().equalsIgnoreCase(obsValueCodedDT.getCode())) {
                                textAlgorithmMatched = true;
                                if (algorithmIsOrLogic)
                                {
                                    var valueCoded = new WdsValueCodedReport();
                                    valueCoded.setCodeType("OBS_VALUE_CODED");
                                    valueCoded.setInputCode(obsValueCodedDT.getCode());
                                    valueCoded.setWdsCode(algorithmCodedValue.getResultCode());
                                    valueCoded.setMatchedFound(true);
                                    wdsReport.setWdsValueCodedReport(valueCoded);
                                    wdsReport.setAlgorithmMatched(true);
                                    return true;
                                }
                            }
                            else if (algorithmIsAndLogic) //result code did not match
                            {
                                var valueCoded = new WdsValueCodedReport();
                                valueCoded.setCodeType("OBS_VALUE_CODED");
                                valueCoded.setInputCode(obsValueCodedDT.getCode());
                                valueCoded.setWdsCode(algorithmCodedValue.getResultCode());
                                valueCoded.setMatchedFound(false);
                                wdsReport.setWdsValueCodedReport(valueCoded);
                                wdsReport.setAlgorithmMatched(false);
                                return true;
                            }
                        } //subObs has next
                    } //obsValueCoded present
                }//code matches
            } //next lab test

        } //while algorithm TestCodedValue has next

        return false;
    }

    /**
     * Note negative logic - testsDoNotMatch
     * Quickly rule out labs that don't have the tests the algorithm seeks.
     * If it is an OR, one must be there. If an AND, all must be there.
     * @param resultedTestCodeColl - resulted test codes in lab
     * @param resultedTestCodeMap2 - resulted test codes in Algorithm
     * @param andOrLogic2 - algorithm and/or logic
     * @return boolean true if this algorithm is not a match
     */
    private boolean testsDoNotMatch(Collection<String> resultedTestCodeColl,
                                    Map<String, String> resultedTestCodeMap2, String andOrLogic2) {
        for (String resultedTest : resultedTestCodeMap2.keySet())
        {
            if (resultedTestCodeColl.contains(resultedTest)) {
                if (andOrLogic2.equals(DecisionSupportConstants.OR_AND_OR_LOGIC)) //OR
                {
                    return false;
                }
            } else {
                if (andOrLogic2.equals(DecisionSupportConstants.AND_AND_OR_LOGIC)) //AND
                {
                    return true;
                }
            }
        }
        return false;
    }


}
