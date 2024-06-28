package gov.cdc.dataprocessing.service.implementation.observation;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationCodeService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ISrteCodeObsService;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PropertyUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ObservationCodeService implements IObservationCodeService {

    private final ISrteCodeObsService srteCodeObsService;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;
    private final ObservationUtil observationUtil;

    public ObservationCodeService(ISrteCodeObsService srteCodeObsService,
                                  OrganizationRepositoryUtil organizationRepositoryUtil,
                                  ObservationUtil observationUtil
    ) {
        this.srteCodeObsService = srteCodeObsService;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
        this.observationUtil = observationUtil;
    }


    public String getReportingLabCLIA(BaseContainer proxy) throws DataProcessingException {
        Collection<ParticipationDto>  partColl = null;
        if (proxy instanceof LabResultProxyContainer)
        {
            partColl = ( (LabResultProxyContainer) proxy).getTheParticipationDtoCollection();
        }
//            if (proxy instanceof MorbidityProxyVO)
//            {
//                partColl = ( (MorbidityProxyVO) proxy).getTheParticipationDTCollection();
//            }

        //Get the reporting lab
        Long reportingLabUid = observationUtil.getUid(partColl, null,
                NEDSSConstant.ENTITY_UID_LIST_TYPE,
                NEDSSConstant.ORGANIZATION,
                NEDSSConstant.PAR111_TYP_CD,
                NEDSSConstant.PART_ACT_CLASS_CD,
                NEDSSConstant.RECORD_STATUS_ACTIVE);

        OrganizationContainer reportingLabVO = null;

        if (reportingLabUid != null)
        {
            reportingLabVO = organizationRepositoryUtil.loadObject(reportingLabUid, null);
        }


        //Get the CLIA
        String reportingLabCLIA = null;

        if(reportingLabVO != null)
        {
            Collection<EntityIdDto>  entityIdColl = reportingLabVO.getTheEntityIdDtoCollection();

            if (entityIdColl != null && entityIdColl.size() > 0) {
                for (EntityIdDto idDT : entityIdColl) {
                    if (idDT == null) {
                        continue;
                    }

                    String authoCd = idDT.getAssigningAuthorityCd();
                    String idTypeCd = idDT.getTypeCd();
                    if (authoCd != null && idTypeCd != null &&
                            authoCd.equalsIgnoreCase(NEDSSConstant.REPORTING_LAB_CLIA) &&
                            idTypeCd.equalsIgnoreCase(NEDSSConstant.REPORTING_LAB_FI_TYPE)) { //civil00011659
                        reportingLabCLIA = idDT.getRootExtensionTxt();
                        break;
                    }
                }
            }
        }
        return reportingLabCLIA;
    }



    /**
     * UNREACHABLE METHOD for the current flow; only reached if the payload is not ELR
     * deriveTheConditionCodeList - used by Associate to Investigations
     *    when associating an STD lab to a closed investigation.
     *    Condition list determines the Processing Decision to show.
     */
    public ArrayList<String> deriveTheConditionCodeList(LabResultProxyContainer labResultProxyVO,
                                                        ObservationContainer orderTest) throws DataProcessingException {

        ArrayList<String> derivedConditionList = new ArrayList<>();

        //if this is not an STD Program Area - we can skip this overhead
        //TODO: CACHING
//        String programAreaCd = orderTest.getTheObservationDto().getProgAreaCd();
//        if ((programAreaCd == null) || (!propertyUtil.isStdOrHivProgramArea(programAreaCd))) {
//            return derivedConditionList;
//        }

        // Get the result tests
        Collection<ObservationContainer> resultTests = new ArrayList<>();
        for (ObservationContainer obsVO : labResultProxyVO.getTheObservationContainerCollection()) {
            String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();
            if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD)) {
                resultTests.add(obsVO);
            }
        }

        // Get the reporting lab clia
        String reportingLabCLIA = "";
        if (labResultProxyVO.getLabClia() != null && labResultProxyVO.isManualLab()){
            reportingLabCLIA = labResultProxyVO.getLabClia();
        }
        else {
            if (orderTest.getTheParticipationDtoCollection() != null) {
                reportingLabCLIA = getReportingLabCLIAId(orderTest.getTheParticipationDtoCollection());
            }
        }
        if (reportingLabCLIA == null || reportingLabCLIA.trim().equals(""))
        {
            reportingLabCLIA = NEDSSConstant.DEFAULT;
        }

        // If there are resulted tests, call obs processor for the list of
        // associated conditions
        // found in the various lab test SRT tables
        if (resultTests.size() > 0) {
            derivedConditionList = getDerivedConditionList(reportingLabCLIA, resultTests, orderTest.getTheObservationDto().getElectronicInd());
        }

        return derivedConditionList;
    }

    private ArrayList<String> getDerivedConditionList(String reportingLabCLIA,
                                                      Collection<ObservationContainer> observationContainerCollection,
                                                      String electronicInd) throws DataProcessingException {
        int noConditionFoundForResultedTestCount = 0;
        ArrayList<String> returnList =  new ArrayList<> ();

        // iterator through each resultTest
        for (ObservationContainer observationContainer : observationContainerCollection) {
            ArrayList<String> resultedTestConditionList;
            ObservationDto obsDt = observationContainer.getTheObservationDto();

            String obsDomainCdSt1 = obsDt.getObsDomainCdSt1();
            String obsDTCode = obsDt.getCd();

            // make sure you are dealing with a resulted test here.
            if (obsDomainCdSt1 != null
                    && obsDomainCdSt1.equals(ELRConstant.ELR_OBSERVATION_RESULT)
                    && obsDTCode != null
                    && !obsDTCode.equals(NEDSSConstant.ACT114_TYP_CD)
            ) {

                // Retrieve Condition List using SNM Lab Result --> SNOMED code mapping
                // If ELR, use actual CLIA - if manual use "DEFAULT" as CLIA
                if (electronicInd.equals(NEDSSConstant.ELECTRONIC_IND_ELR)) {
                    resultedTestConditionList = getConditionsFromSNOMEDCodes(reportingLabCLIA, observationContainer.getTheObsValueCodedDtoCollection());
                } else {
                    resultedTestConditionList = getConditionsFromSNOMEDCodes(NEDSSConstant.DEFAULT, observationContainer.getTheObsValueCodedDtoCollection());
                }

                // if no conditions found - try LN to retrieve Condition using Resulted Test --> LOINC mapping
                if (resultedTestConditionList.isEmpty()) {
                    String loincCondition = getConditionForLOINCCode(reportingLabCLIA, observationContainer);
                    if (loincCondition != null
                            && !loincCondition.isEmpty()
                    ) {
                        resultedTestConditionList.add(loincCondition);
                    }
                }

                // none - try LR to retrieve default Condition using Local Result Code to condition mapping
                if (resultedTestConditionList.isEmpty()) {
                    String localResultDefaultConditionCd = getConditionCodeForLocalResultCode(reportingLabCLIA, observationContainer.getTheObsValueCodedDtoCollection());
                    if (localResultDefaultConditionCd != null
                            && !localResultDefaultConditionCd.isEmpty()
                    ) {
                        resultedTestConditionList.add(localResultDefaultConditionCd);
                    }
                }
                // none - try LT to retrieve default Condition using Local Test Code to condition mapping
                if (resultedTestConditionList.isEmpty()) {
                    String localTestDefaultConditionCd = getConditionCodeForLocalTestCode(reportingLabCLIA, observationContainer);
                    if (localTestDefaultConditionCd != null
                            && !localTestDefaultConditionCd.isEmpty()
                    ) {
                        resultedTestConditionList.add(localTestDefaultConditionCd);
                    }
                }
                // none - see if default condition code exists for the resulted lab test
                if (resultedTestConditionList.isEmpty()) {
                    String defaultLabTestConditionCd = getDefaultConditionForLabTestCode(obsDTCode, reportingLabCLIA);
                    if (defaultLabTestConditionCd != null
                            && !defaultLabTestConditionCd.isEmpty()
                    ) {
                        resultedTestConditionList.add(defaultLabTestConditionCd);
                    }
                }
                if (resultedTestConditionList.isEmpty()) {
                    noConditionFoundForResultedTestCount = noConditionFoundForResultedTestCount + 1;
                }
                //if we found conditions add them to the return list
                if (!resultedTestConditionList.isEmpty()) {
                    Set<String> hashset = new HashSet<>();
                    hashset.addAll(returnList);
                    hashset.addAll(resultedTestConditionList);
                    //get rid of dups..
                    returnList = new ArrayList<>(hashset);
                }
            }
        }
        //if we couldn't derive a condition for a test, return no conditions
        if (noConditionFoundForResultedTestCount > 0)
        {
            returnList.clear(); //incomplete list - return empty list
        }

        return returnList;
    } // end of ConditionList

    private String getReportingLabCLIAId(Collection<ParticipationDto> partColl) throws DataProcessingException {
        // Get the reporting lab
        Long reportingLabUid = observationUtil.getUid(
                partColl,
                null,
                NEDSSConstant.ENTITY_UID_LIST_TYPE,
                NEDSSConstant.ORGANIZATION, NEDSSConstant.PAR111_TYP_CD,
                NEDSSConstant.PART_ACT_CLASS_CD,
                NEDSSConstant.RECORD_STATUS_ACTIVE);

        OrganizationContainer reportingLabVO = null;
        try {
            if (reportingLabUid != null) {
                reportingLabVO = organizationRepositoryUtil.loadObject(reportingLabUid, null);
            }
        } catch (Exception rex) {
            throw new DataProcessingException("Error while retriving reporting organization vo, its uid is: " + reportingLabUid, rex);
        }

        // Get the CLIA
        String reportingLabCLIA = null;

        if (reportingLabVO != null) {

            Collection<EntityIdDto> entityIdColl = reportingLabVO.getTheEntityIdDtoCollection();

            if (entityIdColl != null && entityIdColl.size() > 0) {
                for (EntityIdDto idDT : entityIdColl) {
                    String authoCd = idDT.getAssigningAuthorityCd();
                    String idTypeCd = idDT.getTypeCd();
                    if (authoCd == null || idTypeCd == null) {
                        continue;
                    }
                    if (authoCd.trim().contains(NEDSSConstant.REPORTING_LAB_CLIA)
                            && idTypeCd.trim().equalsIgnoreCase(NEDSSConstant.REPORTING_LAB_FI_TYPE)
                    ) {
                        reportingLabCLIA = idDT.getRootExtensionTxt();
                        break;
                    }
                }
            }
        }
        return reportingLabCLIA;
    }

    /**
     * Returns a List of Condition Codes associated with the passed Snomed codes.
     *
     * @param reportingLabCLIA : String
     * @param obsValueCodedDtoColl : Collection
     * @return ArrayList<string>
     */
    // AK - 7/25/04
    private ArrayList<String> getConditionsFromSNOMEDCodes(String reportingLabCLIA, Collection<ObsValueCodedDto> obsValueCodedDtoColl) throws DataProcessingException {

        ArrayList<String> snomedConditionList = new ArrayList<>();

        if (obsValueCodedDtoColl != null) {
            for (ObsValueCodedDto obsValueCodedDto : obsValueCodedDtoColl) {
                String snomedCd = "";
                String conditionCd = "";
                String codeSystemCd = obsValueCodedDto.getCodeSystemCd();

                if (codeSystemCd == null || codeSystemCd.trim().equals("")) {
                    continue;
                }

                String obsCode = obsValueCodedDto.getCode();
                if (obsCode == null || obsCode.trim().equals("")) {
                    continue;
                }

                /* If the code is not a Snomed code, try to get the snomed code.
                 * Check if ObsValueCodedDto.codeSystemCd='L' and CLIA for
                 * Reporting Lab is available, find the Snomed code for
                 * ObsValueCodedDto.code(Local Result to Snomed lookup)
                 */
                if (!codeSystemCd.equals(ELRConstant.ELR_SNOMED_CD)) {
                    Map<String, Object> snomedMap = srteCodeObsService.getSnomed(obsValueCodedDto.getCode(), ELRConstant.TYPE, reportingLabCLIA);

                    if (snomedMap.containsKey("COUNT") && (Integer) snomedMap.get("COUNT") == 1) {
                        snomedCd = (String) snomedMap.get("LOINC");
                    } else {
                        continue;
                    }
                }

                /*
                 * If already coded using SNOMED code, just add it to the return
                 * array. check if ObsValueCodedDto.codeSystemCd="SNM", use
                 * ObsValueCodedDto.code for Snomed
                 */
                else if (codeSystemCd.equals("SNM")) {
                    snomedCd = obsCode;
                }

                //if these is a Snomed code, see if we can get a corresponding condition for it
                try {
                    conditionCd = srteCodeObsService.getConditionForSnomedCode(snomedCd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (conditionCd != null && !conditionCd.isEmpty()) {
                    snomedConditionList.add(conditionCd);
                }
            } // end of while has next
        } // end if collection not null
        return snomedConditionList;
    }

    private String getConditionForLOINCCode(String reportingLabCLIA, ObservationContainer resultTestVO) throws DataProcessingException {

        String loincCd = "";
        ObservationDto obsDt = resultTestVO.getTheObservationDto();
        if (obsDt == null || reportingLabCLIA == null)
        {
            return null;
        }

        String cdSystemCd = obsDt.getCdSystemCd();
        if (cdSystemCd == null || cdSystemCd.trim().equals(""))
        {
            return null;
        }

        String obsCode = obsDt.getCd();
        if (obsCode == null || obsCode.trim().equals(""))
        {
            return null;
        }

        if (cdSystemCd.equals(ELRConstant.ELR_OBSERVATION_LOINC)) {
            loincCd = obsCode;
        }
        else
        {
            Map<String, Object> snomedMap =  srteCodeObsService.getSnomed(obsCode, "LT", reportingLabCLIA);

            if(snomedMap.containsKey("COUNT") && (Integer) snomedMap.get("COUNT") == 1) {
                loincCd = (String) snomedMap.get("LOINC");
            }
        }

        // If we have resolved the LOINC code, try to derive the condition
        if (loincCd == null || loincCd.isEmpty())
        {
            return loincCd;
        }

        String conditionCd = "";
        try {
            conditionCd = srteCodeObsService.getConditionForLoincCode(loincCd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (conditionCd);

    }

    /**
     * Gets the default condition for a Local Result code.
     * If we find that it maps to more than one condition code, return nothing.
     * @param reportingLabCLIA : String
     * @param obsValueCodedDtoColl: Collection
     * @return conditionCd : String
     */
    private String getConditionCodeForLocalResultCode(String reportingLabCLIA, Collection<ObsValueCodedDto> obsValueCodedDtoColl) {
        String conditionCd = "";
        HashMap<String, String> conditionMap = new HashMap<>();
        if (obsValueCodedDtoColl == null || reportingLabCLIA == null)
        {
            return null;
        }

        for (ObsValueCodedDto obsValueCodedDto : obsValueCodedDtoColl) {
            String code = obsValueCodedDto.getCode();
            //String codeSystemCd = obsValueCodedDto.getCodeSystemCd();
            if (code != null) {
                String defaultCondition = srteCodeObsService.getDefaultConditionForLocalResultCode(code, reportingLabCLIA);
                if (defaultCondition != null && !defaultCondition.isEmpty()) {
                    conditionCd = defaultCondition;
                    conditionMap.put(defaultCondition, code);
                }
            }
        }
        if (conditionMap.size() > 1 || conditionMap.isEmpty())
        {
            return("");
        }
        else {
            return(conditionCd);
        }
    }

    /**
     * Gets the default condition for the Local Test code.
     * @param resultTestVO : Collection
     * @param reportingLabCLIA : String
     * @return conditionCd : String
     */
    private String getConditionCodeForLocalTestCode(String reportingLabCLIA, ObservationContainer resultTestVO) {

        //edit checks
        if (reportingLabCLIA == null || resultTestVO == null)
        {
            return null;
        }
        ObservationDto obsDt = resultTestVO.getTheObservationDto();
        if (obsDt.getCd() == null || obsDt.getCd().equals("") || obsDt.getCd().equals(" ") || obsDt.getCdSystemCd() == null)
        {
            return null;
        }

        String testCd = obsDt.getCd();
        return (srteCodeObsService.getDefaultConditionForLocalResultCode(testCd, reportingLabCLIA));
    }

    /**
     * Gets the default condition for the Lab Test code.
     * @return conditionCd : String
     */
    private String getDefaultConditionForLabTestCode(String labTestCd, String reportingLabCLIA) {
        String conditionCd = srteCodeObsService.getDefaultConditionForLabTest(labTestCd, reportingLabCLIA );
        //see if the DEFAULT is set for the lab test if still not found..
        if ((conditionCd == null || conditionCd.isEmpty()) && !reportingLabCLIA.equals(NEDSSConstant.DEFAULT))
        {
            conditionCd = srteCodeObsService.getDefaultConditionForLabTest(labTestCd, NEDSSConstant.DEFAULT);
        }
        return(conditionCd);
    }





}
