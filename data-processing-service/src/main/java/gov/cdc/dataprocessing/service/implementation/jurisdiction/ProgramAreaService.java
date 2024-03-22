package gov.cdc.dataprocessing.service.implementation.jurisdiction;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ProgramAreaCode;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.ProgramAreaCodeRepository;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationCodeService;
import gov.cdc.dataprocessing.service.interfaces.other.ISrteCodeObsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ProgramAreaService implements IProgramAreaService {
    private static final Logger logger = LoggerFactory.getLogger(ProgramAreaService.class);
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

    public Object processingProgramArea() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing program area";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }

    }

    public Object processingJurisdiction() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing jurisdiction";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }

    }

    public List<ProgramAreaCode> getAllProgramAreaCode() {
        var progCodeRes = programAreaCodeRepository.findAll();
        if (!progCodeRes.isEmpty()) {
            return progCodeRes;
        } else {
            return new ArrayList<>();
        }
    }

    public void getProgramArea(Collection<ObservationContainer> resultTests, ObservationContainer orderTest, String clia) throws DataProcessingException
    {
        String programAreaCode = null;
        if (clia == null || clia.trim().equals(""))
        {
            clia = NEDSSConstant.DEFAULT;
        }

        Map<String, String> paResults = null;
        if (orderTest.getTheObservationDto().getElectronicInd()
                .equals(NEDSSConstant.ELECTRONIC_IND_ELR)) {
            if (resultTests.size() > 0)
            {
                paResults = getProgramAreaHelper(clia, resultTests, orderTest.getTheObservationDto().getElectronicInd());
            }

            if (paResults != null && paResults.containsKey(ELRConstant.PROGRAM_AREA_HASHMAP_KEY))
            {
                programAreaCode = paResults.get(ELRConstant.PROGRAM_AREA_HASHMAP_KEY);
                orderTest.getTheObservationDto().setProgAreaCd(programAreaCode);
            }
            else 
            {
                orderTest.getTheObservationDto().setProgAreaCd(null);
            }
        }
        if (paResults != null && paResults.containsKey("ERROR")) 
        {
            orderTest.getTheObservationDto().setProgAreaCd(null);
        } 
        else 
        {
            orderTest.getTheObservationDto().setProgAreaCd(programAreaCode);
        }
    }


    private HashMap<String, String> getProgramAreaHelper(String reportingLabCLIA,
                                                  Collection<ObservationContainer> observationVOCollection,
                                                  String electronicInd) throws DataProcessingException {

        HashMap<String, String> returnMap = new HashMap<>();
        if (reportingLabCLIA == null)
        {
            returnMap.put(NEDSSConstant.ERROR, NEDSSConstant.REPORTING_LAB_CLIA_NULL);
            return returnMap;
        }

        Iterator<ObservationContainer> obsIt = observationVOCollection.iterator();
        Hashtable<String, String> paHTBL = new Hashtable<>();

        //iterator through each resultTest
        while (obsIt.hasNext())
        {
            ObservationContainer obsVO = obsIt.next();
            ObservationDto obsDt = obsVO.getTheObservationDto();

            String obsDomainCdSt1 = obsDt.getObsDomainCdSt1();
            String obsDTCode = obsDt.getCd();
            boolean found = false;

            //Set exclude flag to false - if any of the components - Lab Result (SNOMED or Local) or Lab Test (LOINC or
            //Local) is excluded, this flag will be set so as not to fail the derivation for this resulted test.
            programAreaDerivationExcludeFlag = false;

            // make sure you are dealing with a resulted test here.
            if ( (obsDomainCdSt1 != null) &&
                    obsDomainCdSt1.equals(ELRConstant.ELR_OBSERVATION_RESULT) &&
                    (obsDTCode != null) &&
                    (!obsDTCode.equals(NEDSSConstant.ACT114_TYP_CD)))
            {
                // Retrieve PAs using Lab Result --> SNOMED code mapping
                // If ELR, use actual CLIA - if manual use "DEFAULT" as CLIA
                String progAreaCd;
                if ( electronicInd.equals(NEDSSConstant.ELECTRONIC_IND_ELR) )
                {
                    progAreaCd = srteCodeObsService.getPAFromSNOMEDCodes(reportingLabCLIA, obsVO.getTheObsValueCodedDtoCollection());
                }
                else
                {
                    progAreaCd = srteCodeObsService.getPAFromSNOMEDCodes(NEDSSConstant.DEFAULT, obsVO.getTheObsValueCodedDtoCollection());
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
                    progAreaCd = srteCodeObsService.getPAFromLOINCCode(reportingLabCLIA, obsVO);
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
                    progAreaCd = srteCodeObsService.getPAFromLocalResultCode(reportingLabCLIA, obsVO.getTheObsValueCodedDtoCollection());
                    // If PA returned, check to see if it is the same one as before.
                    if (progAreaCd != null)
                    {
                        found = true;
                        //System.out.println("Found!" + progAreaCd);
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
                    progAreaCd = srteCodeObsService.getPAFromLocalTestCode(reportingLabCLIA, obsVO);
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
            returnMap.put(NEDSSConstant.ERROR, ELRConstant.PROGRAM_ASSIGN_2);
        }
        else if (paHTBL.size() == 1)
        {
            returnMap.put(ELRConstant.PROGRAM_AREA_HASHMAP_KEY, paHTBL.keys().nextElement().toString());
        }
        else
        {
            returnMap.put(NEDSSConstant.ERROR, ELRConstant.PROGRAM_ASSIGN_1);
        }
        return returnMap;
    } //end of getProgramArea

    public String deriveProgramAreaCd(LabResultProxyContainer labResultProxyVO, ObservationContainer orderTest) throws DataProcessingException {
            //Gathering the result tests
            Collection<ObservationContainer>  resultTests = new ArrayList<> ();
            for (Iterator<ObservationContainer> it = labResultProxyVO.getTheObservationContainerCollection().
                    iterator(); it.hasNext(); )
            {
                ObservationContainer obsVO = (ObservationContainer) it.next();

                String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();
                if (obsDomainCdSt1 != null &&
                        obsDomainCdSt1.equalsIgnoreCase(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD))
                {
                    resultTests.add(obsVO);
                }
            }

            //Get the reporting lab clia
            String reportingLabCLIA = "";
            if(labResultProxyVO.getLabClia()!=null && labResultProxyVO.isManualLab())
                reportingLabCLIA =labResultProxyVO.getLabClia();
            else
                reportingLabCLIA = observationCodeService.getReportingLabCLIA(labResultProxyVO);

            if(reportingLabCLIA == null || reportingLabCLIA.trim().equals(""))
                reportingLabCLIA = NEDSSConstant.DEFAULT;

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


                //Return errors if any
                if (paResults != null &&
                        paResults.containsKey("ERROR"))
                {
                    return (String) paResults.get("ERROR");
                }
                else
                {
                    return null;
                }
            }
            return null;
    }







}
