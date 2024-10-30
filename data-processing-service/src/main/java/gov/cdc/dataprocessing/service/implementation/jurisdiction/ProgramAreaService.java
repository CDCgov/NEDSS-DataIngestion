package gov.cdc.dataprocessing.service.implementation.jurisdiction;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ProgramAreaCode;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.ProgramAreaCodeRepository;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ISrteCodeObsService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.ERROR;

@Service
@Slf4j
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class ProgramAreaService implements IProgramAreaService {
    boolean programAreaDerivationExcludeFlag;

    private final ISrteCodeObsService srteCodeObsService;
    private final ProgramAreaCodeRepository programAreaCodeRepository;
    private final IObservationCodeService observationCodeService;

    public ProgramAreaService(
            ISrteCodeObsService srteCodeObsService,
            ProgramAreaCodeRepository programAreaCodeRepository,
            IObservationCodeService observationCodeService) {

        this.srteCodeObsService = srteCodeObsService;
        this.programAreaCodeRepository = programAreaCodeRepository;
        this.observationCodeService = observationCodeService;
    }


    public List<ProgramAreaCode> getAllProgramAreaCode() {
        var progCodeRes = programAreaCodeRepository.findAll();
        if (!progCodeRes.isEmpty()) {
            return progCodeRes;
        } else {
            return new ArrayList<>();
        }
    }

    /***
     * Description: given observation request  (root observation) and an List of observation result.
     * This method conditionally the valid program area associated with CLIA code.
     * CLIA code assoc with MSH-4.2.1 and PID-3.4.2.
     * This method set program area to Observation request (root observation) after it found valid code in observation results
     * */
    public void getProgramArea(Collection<ObservationContainer> observationResults, ObservationContainer observationRequest, String clia) throws DataProcessingException
    {
        String programAreaCode = null;
        if (clia == null || clia.trim().equals(""))
        {
            clia = NEDSSConstant.DEFAULT;
        }

        Map<String, String> paResults = null;
        if (observationRequest.getTheObservationDto().getElectronicInd().equals(NEDSSConstant.ELECTRONIC_IND_ELR)) {
            if (!observationResults.isEmpty())
            {
                // Program Area happening here
                paResults = getProgramAreaHelper(
                        clia,
                        observationResults,
                        observationRequest.getTheObservationDto().getElectronicInd()
                );
            }

            if (paResults != null && paResults.containsKey(ELRConstant.PROGRAM_AREA_HASHMAP_KEY))
            {
                programAreaCode = paResults.get(ELRConstant.PROGRAM_AREA_HASHMAP_KEY);
                observationRequest.getTheObservationDto().setProgAreaCd(programAreaCode);
            }
            else
            {
                observationRequest.getTheObservationDto().setProgAreaCd(null);
            }
        }

        if (paResults != null && paResults.containsKey(ERROR))
        {
            observationRequest.getTheObservationDto().setProgAreaCd(null);
        }
        else
        {
            observationRequest.getTheObservationDto().setProgAreaCd(programAreaCode);
        }
    }


    /**
     * Description: method getting program area given CLIA and Observation Requests
     * */
    @SuppressWarnings({"java:S3776","java:S1149"})
    private HashMap<String, String> getProgramAreaHelper(String reportingLabCLIA,
                                                         Collection<ObservationContainer> observationResults,
                                                         String electronicInd) throws DataProcessingException {

        HashMap<String, String> returnMap = new HashMap<>();
        if (reportingLabCLIA == null)
        {
            returnMap.put(ERROR, NEDSSConstant.REPORTING_LAB_CLIA_NULL);
            return returnMap;
        }

        Iterator<ObservationContainer> obsResults = observationResults.iterator();
        Hashtable<String, String> paHTBL = new Hashtable<>();

        //iterator through each resultTest
        while (obsResults.hasNext())
        {
            ObservationContainer obsResult = obsResults.next();
            ObservationDto obsDt = obsResult.getTheObservationDto();

            String obsDomainCdSt1 = obsDt.getObsDomainCdSt1();
            String obsDTCode = obsDt.getCd();
            boolean found = false;

            //Set exclude flag to false - if any of the components - Lab Result (SNOMED or Local) or Lab Test (LOINC or
            //Local) is excluded, this flag will be set so as not to fail the derivation for this resulted test.
            programAreaDerivationExcludeFlag = false;

            // make sure you are dealing with a resulted test here.
            if (
                    obsDomainCdSt1 != null &&
                            obsDomainCdSt1.equals(ELRConstant.ELR_OBSERVATION_RESULT) &&
                            obsDTCode != null &&
                            !obsDTCode.equals(NEDSSConstant.ACT114_TYP_CD)
            )
            {
                // Retrieve PAs using Lab Result --> SNOMED code mapping
                // If ELR, use actual CLIA - if manual use "DEFAULT" as CLIA
                String progAreaCd;
                if ( electronicInd.equals(NEDSSConstant.ELECTRONIC_IND_ELR) )
                {
                    progAreaCd = srteCodeObsService.getPAFromSNOMEDCodes(reportingLabCLIA, obsResult.getTheObsValueCodedDtoCollection());
                }
                //NOTE: this wont happen in ELR flow
                else
                {
                    progAreaCd = srteCodeObsService.getPAFromSNOMEDCodes(NEDSSConstant.DEFAULT, obsResult.getTheObsValueCodedDtoCollection());
                }


                // If PA returned, check to see if it is the same one as before.
                if (progAreaCd != null)
                {
                    found = true;
                    paHTBL.put(progAreaCd.trim(), progAreaCd.trim());
                    if (paHTBL.size() != 1)
                    {
                        break;
                    }

                }

                // Retrieve PAs using Resulted Test --> LOINC mapping
                if (!found)
                {
                    progAreaCd = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, obsResult);
                    // If PA returned, check to see if it is the same one as before.
                    if (progAreaCd != null)
                    {
                        found = true;
                        paHTBL.put(progAreaCd.trim(), progAreaCd.trim());
                        if (paHTBL.size() != 1)
                        {
                            break;
                        }
                    }
                }

                // Retrieve PAs using Local Result Code to PA mapping
                if (!found)
                {
                    progAreaCd = srteCodeObsService.getPAFromLocalResultCode(reportingLabCLIA, obsResult.getTheObsValueCodedDtoCollection());
                    // If PA returned, check to see if it is the same one as before.
                    if (progAreaCd != null)
                    {
                        found = true;
                        paHTBL.put(progAreaCd.trim(), progAreaCd.trim());
                        if (paHTBL.size() != 1)
                        {
                            break;
                        }
                    }
                }

                // Retrieve PAs using Local Result Code to PA mapping
                if (!found)
                {
                    progAreaCd = srteCodeObsService.getPAFromLocalTestCode(reportingLabCLIA, obsResult);
                    // If PA returned, check to see if it is the same one as before.
                    if (progAreaCd != null)
                    {
                        found = true;
                        paHTBL.put(progAreaCd.trim(), progAreaCd.trim());
                        if (paHTBL.size() != 1)
                        {
                            break;
                        }
                    }
                }

                //If we haven't found a PA and the no components were excluded based on the exclude flag,
                //clear the PA hashtable which will fail the derivation
                if (!found && !programAreaDerivationExcludeFlag)
                {
                    paHTBL.clear();
                    break;
                }
            }
        } //end of while

        if(paHTBL.size() == 0)
        {
            returnMap.put(ERROR, ELRConstant.PROGRAM_ASSIGN_2);
        }
        else if (paHTBL.size() == 1)
        {
            returnMap.put(ELRConstant.PROGRAM_AREA_HASHMAP_KEY, paHTBL.keys().nextElement());
        }
        else
        {
            returnMap.put(ERROR, ELRConstant.PROGRAM_ASSIGN_1);
        }
        return returnMap;
    } //end of getProgramArea

    @SuppressWarnings("java:S3776")
    public String deriveProgramAreaCd(LabResultProxyContainer labResultProxyVO, ObservationContainer orderTest) throws DataProcessingException {
        //Gathering the result tests
        Collection<ObservationContainer>  resultTests = new ArrayList<> ();
        for (ObservationContainer obsVO : labResultProxyVO.getTheObservationContainerCollection()) {
            String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();
            if (obsDomainCdSt1 != null &&
                    obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD)) {
                resultTests.add(obsVO);
            }
        }

        //Get the reporting lab clia
        String reportingLabCLIA = "";
        if(labResultProxyVO.getLabClia()!=null && labResultProxyVO.isManualLab())
        {
            reportingLabCLIA =labResultProxyVO.getLabClia();
        }
        else
        {
            reportingLabCLIA = observationCodeService.getReportingLabCLIA(labResultProxyVO);
        }

        if(reportingLabCLIA == null || reportingLabCLIA.trim().equals(""))
        {
            reportingLabCLIA = NEDSSConstant.DEFAULT;
        }

        //Get program area
        if(!orderTest.getTheObservationDto().getElectronicInd().equals(NEDSSConstant.ELECTRONIC_IND_ELR)){
            Map<Object, Object> paResults = null;
            if (resultTests.size() > 0)
            {
                paResults = srteCodeObsService.getProgramArea(reportingLabCLIA, resultTests, orderTest.getTheObservationDto().getElectronicInd());
            }


            //set program area for order test
            if (paResults != null &&
                    paResults.containsKey(ELRConstant.PROGRAM_AREA_HASHMAP_KEY))
            {
                orderTest.getTheObservationDto().setProgAreaCd( (String) paResults.get(
                        ELRConstant.PROGRAM_AREA_HASHMAP_KEY));
            }
            else
            {
                orderTest.getTheObservationDto().setProgAreaCd(null);
            }

            if (paResults != null && paResults.containsKey(ERROR))
            {
                return (String) paResults.get(ERROR);
            }
            else
            {
                return null;
            }
        }
        return null;
    }







}