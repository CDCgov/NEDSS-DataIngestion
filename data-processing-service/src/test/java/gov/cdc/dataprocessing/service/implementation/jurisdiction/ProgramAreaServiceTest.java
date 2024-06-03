package gov.cdc.dataprocessing.service.implementation.jurisdiction;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ProgramAreaCode;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.ProgramAreaCodeRepository;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationCodeService;
import gov.cdc.dataprocessing.service.interfaces.other.ISrteCodeObsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProgramAreaServiceTest {
    @Mock
    private ISrteCodeObsService srteCodeObsService;
    @Mock
    private ProgramAreaCodeRepository programAreaCodeRepository;
    @Mock
    private IObservationCodeService observationCodeService;
    @InjectMocks
    private ProgramAreaService programAreaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(srteCodeObsService);
        Mockito.reset(programAreaCodeRepository);
        Mockito.reset(observationCodeService);
    }

    @Test
    void getAllProgramAreaCode() {
        ProgramAreaCode programAreaCode= getProgramAreaCode();
        when(programAreaCodeRepository.findAll()).thenReturn(List.of(programAreaCode));
        List<ProgramAreaCode> programAreaCodeList = programAreaService.getAllProgramAreaCode();
        assertEquals(1, programAreaCodeList.size());
    }
    @Test
    void getAllProgramAreaCode_empty_list() {
        List<ProgramAreaCode> programAreaCodeList=new ArrayList<>();
        when(programAreaCodeRepository.findAll()).thenReturn(programAreaCodeList);
        List<ProgramAreaCode> programAreaCodeListResult = programAreaService.getAllProgramAreaCode();
        assertEquals(0, programAreaCodeListResult.size());
    }

    @Test
    void getProgramArea_SNOMED() throws DataProcessingException {
        Collection<ObservationContainer> observationResults=new ArrayList<>();
        //1
        ObservationContainer observationContainer1=new ObservationContainer();
        ObservationDto obsDt1=new ObservationDto();
        obsDt1.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt1.setCd("TEST_CD1");
        observationContainer1.setTheObservationDto(obsDt1);
        //2
        ObservationContainer observationContainer2=new ObservationContainer();
        ObservationDto obsDt2=new ObservationDto();
        obsDt2.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt2.setCd("TEST_CD2");
        observationContainer2.setTheObservationDto(obsDt2);

        observationResults.add(observationContainer1);
        observationResults.add(observationContainer2);

        ObservationContainer observationRequest=new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setElectronicInd(NEDSSConstant.ELECTRONIC_IND_ELR);
        observationRequest.setTheObservationDto(observationDto);
        //
        when(srteCodeObsService.getPAFromSNOMEDCodes(any(),any())).thenReturn("TEST_PA");
        //Call test method
        programAreaService.getProgramArea(observationResults,observationRequest,"TEST");
        verify(srteCodeObsService,times(2)).getPAFromSNOMEDCodes(any(), any());
    }
    @Test
    void getProgramArea_LOINC() throws DataProcessingException {
        Collection<ObservationContainer> observationResults=new ArrayList<>();
        //1
        ObservationContainer observationContainer1=new ObservationContainer();
        ObservationDto obsDt1=new ObservationDto();
        obsDt1.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt1.setCd("TEST_CD1");
        observationContainer1.setTheObservationDto(obsDt1);
        //2
        ObservationContainer observationContainer2=new ObservationContainer();
        ObservationDto obsDt2=new ObservationDto();
        obsDt2.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt2.setCd("TEST_CD2");
        observationContainer2.setTheObservationDto(obsDt2);

        observationResults.add(observationContainer1);
        observationResults.add(observationContainer2);

        ObservationContainer observationRequest=new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setElectronicInd(NEDSSConstant.ELECTRONIC_IND_ELR);
        observationRequest.setTheObservationDto(observationDto);
        //
        when(srteCodeObsService.getPAFromSNOMEDCodes(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLOINCCode(any(),any())).thenReturn("TEST_PA");

        //Call test method
        programAreaService.getProgramArea(observationResults,observationRequest,"TEST");
        verify(srteCodeObsService,times(2)).getPAFromLOINCCode(any(), any());
    }
    @Test
    void getProgramArea_LocalResult() throws DataProcessingException {
        Collection<ObservationContainer> observationResults=new ArrayList<>();
        //1
        ObservationContainer observationContainer1=new ObservationContainer();
        ObservationDto obsDt1=new ObservationDto();
        obsDt1.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt1.setCd("TEST_CD1");
        observationContainer1.setTheObservationDto(obsDt1);
        //2
        ObservationContainer observationContainer2=new ObservationContainer();
        ObservationDto obsDt2=new ObservationDto();
        obsDt2.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt2.setCd("TEST_CD2");
        observationContainer2.setTheObservationDto(obsDt2);

        observationResults.add(observationContainer1);
        observationResults.add(observationContainer2);

        ObservationContainer observationRequest=new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setElectronicInd(NEDSSConstant.ELECTRONIC_IND_ELR);
        observationRequest.setTheObservationDto(observationDto);
        //
        when(srteCodeObsService.getPAFromSNOMEDCodes(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLOINCCode(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLocalResultCode(any(),any())).thenReturn("TEST_PA");
        //Call test method
        programAreaService.getProgramArea(observationResults,observationRequest,"TEST");
        verify(srteCodeObsService,times(2)).getPAFromLocalResultCode(any(), any());
    }
    @Test
    void getProgramArea_LocalTestCode() throws DataProcessingException {
        Collection<ObservationContainer> observationResults=new ArrayList<>();
        //1
        ObservationContainer observationContainer1=new ObservationContainer();
        ObservationDto obsDt1=new ObservationDto();
        obsDt1.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt1.setCd("TEST_CD1");
        observationContainer1.setTheObservationDto(obsDt1);

        observationResults.add(observationContainer1);
        //observationRequest
        ObservationContainer observationRequest=new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setElectronicInd(NEDSSConstant.ELECTRONIC_IND_ELR);
        observationRequest.setTheObservationDto(observationDto);
        //
        when(srteCodeObsService.getPAFromSNOMEDCodes(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLOINCCode(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLocalResultCode(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLocalTestCode(any(),any())).thenReturn("TEST_PA");

        //Call test method
        programAreaService.getProgramArea(observationResults,observationRequest,"TEST");
        verify(srteCodeObsService,times(1)).getPAFromLocalTestCode(any(), any());
    }
    @Test
    void getProgramArea_empty_PA() throws DataProcessingException {
        Collection<ObservationContainer> observationResults=new ArrayList<>();
        //1
        ObservationContainer observationContainer1=new ObservationContainer();
        ObservationDto obsDt1=new ObservationDto();
        obsDt1.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt1.setCd("TEST_CD1");
        observationContainer1.setTheObservationDto(obsDt1);

        observationResults.add(observationContainer1);
        //observationRequest
        ObservationContainer observationRequest=new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setElectronicInd(NEDSSConstant.ELECTRONIC_IND_ELR);
        observationRequest.setTheObservationDto(observationDto);
        //
        when(srteCodeObsService.getPAFromSNOMEDCodes(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLOINCCode(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLocalResultCode(any(),any())).thenReturn(null);
        when(srteCodeObsService.getPAFromLocalTestCode(any(),any())).thenReturn(null);

        //Call test method
        programAreaService.getProgramArea(observationResults,observationRequest,"TEST");
        verify(srteCodeObsService,times(1)).getPAFromLocalTestCode(any(), any());
    }
    @Test
    void getProgramArea_multi_PA() throws DataProcessingException {
        Collection<ObservationContainer> observationResults=new ArrayList<>();
        //1
        ObservationContainer observationContainer1=new ObservationContainer();
        ObservationDto obsDt1=new ObservationDto();
        obsDt1.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt1.setCd("TEST_CD1");
        observationContainer1.setTheObservationDto(obsDt1);

        ObsValueCodedDto obsValueCodedDto=new ObsValueCodedDto();
        obsValueCodedDto.setCode("TEST_CODE");
        Collection<ObsValueCodedDto> obsValueCodedDtoCol=new ArrayList<>();
        obsValueCodedDtoCol.add(obsValueCodedDto);
        observationContainer1.setTheObsValueCodedDtoCollection(obsValueCodedDtoCol);

        //2
        ObservationContainer observationContainer2=new ObservationContainer();
        ObservationDto obsDt2=new ObservationDto();
        obsDt2.setObsDomainCdSt1(ELRConstant.ELR_OBSERVATION_RESULT);
        obsDt2.setCd("TEST_CD2");
        observationContainer2.setTheObservationDto(obsDt2);

        observationResults.add(observationContainer1);
        observationResults.add(observationContainer2);

        ObservationContainer observationRequest=new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setElectronicInd(NEDSSConstant.ELECTRONIC_IND_ELR);
        observationRequest.setTheObservationDto(observationDto);
        //
        when(srteCodeObsService.getPAFromSNOMEDCodes(any(),eq(null))).thenReturn("TEST_PA1");
        when(srteCodeObsService.getPAFromSNOMEDCodes(any(),eq(observationContainer1.getTheObsValueCodedDtoCollection()))).thenReturn("TEST_PA2");
        //Call test method
        programAreaService.getProgramArea(observationResults,observationRequest,"TEST");
        verify(srteCodeObsService,times(2)).getPAFromSNOMEDCodes(any(), any());
    }
    @Test
    void deriveProgramAreaCd() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO=new LabResultProxyContainer();
        Collection<ObservationContainer> obsContainerList = new ArrayList<>();
        ObservationContainer obsVO1 =new ObservationContainer();
        obsVO1.getTheObservationDto().setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsContainerList.add(obsVO1);
        labResultProxyVO.setTheObservationContainerCollection(obsContainerList);

        labResultProxyVO.setLabClia("TEST123");
        labResultProxyVO.setManualLab(true);
        //
        ObservationContainer orderTest=new ObservationContainer();
        orderTest.getTheObservationDto().setElectronicInd(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);

        //
        HashMap<Object, Object> paResults=new HashMap<>();
        paResults.put(ELRConstant.PROGRAM_AREA_HASHMAP_KEY, "TEST_PA");
        paResults.put("ERROR", "ERROR_PA");

        when(srteCodeObsService.getProgramArea(any(), any(), any())).thenReturn(paResults);

        String result= programAreaService.deriveProgramAreaCd(labResultProxyVO,orderTest);
        assertEquals("ERROR_PA",result);
        verify(srteCodeObsService).getProgramArea(any(), any(), any());
    }
    @Test
    void deriveProgramAreaCd_labClia_null() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO=new LabResultProxyContainer();
        Collection<ObservationContainer> obsContainerList = new ArrayList<>();
        ObservationContainer obsVO1 =new ObservationContainer();
        obsVO1.getTheObservationDto().setObsDomainCdSt1(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        obsContainerList.add(obsVO1);
        labResultProxyVO.setTheObservationContainerCollection(obsContainerList);

        labResultProxyVO.setLabClia(null);
        labResultProxyVO.setManualLab(false);
        //
        ObservationContainer orderTest=new ObservationContainer();
        orderTest.getTheObservationDto().setElectronicInd(NEDSSConstant.RESULTED_TEST_OBS_DOMAIN_CD);
        //
        when(observationCodeService.getReportingLabCLIA(any())).thenReturn(null);
        when(srteCodeObsService.getProgramArea(any(), any(), any())).thenReturn(null);
        //call test method
        String result= programAreaService.deriveProgramAreaCd(labResultProxyVO,orderTest);
        assertNull(result);
        verify(srteCodeObsService,times(1)).getProgramArea(any(), any(), any());
    }

    private ProgramAreaCode getProgramAreaCode() {
        ProgramAreaCode programAreaCode = new ProgramAreaCode();
        programAreaCode.setProgAreaCd("ARBO");
        programAreaCode.setProgAreaDescTxt("ARBO");
        programAreaCode.setNbsUid(1);
        programAreaCode.setStatusCd("A");
        programAreaCode.setStatusTime(new Timestamp(System.currentTimeMillis()));
        programAreaCode.setCodeSetNm("S_PROGRA_C");
        programAreaCode.setCodeSeq(1);
        return programAreaCode;
    }
}