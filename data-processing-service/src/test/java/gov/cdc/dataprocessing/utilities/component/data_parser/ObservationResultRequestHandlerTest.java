package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestDataReader;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.CommonLabUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ObservationResultRequestHandlerTest {
    @Mock
    private ICatchingValueService checkingValueService;
    @Mock
    private NBSObjectConverter nbsObjectConverter;
    @Mock
    private CommonLabUtil commonLabUtil;

    @InjectMocks
    private ObservationResultRequestHandler observationResultRequestHandler;

    private List<HL7OBSERVATIONType> result;
    private EdxLabInformationDto edxLabInformationDt;
    @Mock
    AuthUtil authUtil;

    @BeforeEach
    void setUp() throws JAXBException {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);

        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);
        result = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getORDEROBSERVATION().get(0).getPatientResultOrderObservation().getOBSERVATION();

        edxLabInformationDt = new EdxLabInformationDto();
        edxLabInformationDt.setNextUid(10);
        edxLabInformationDt.setParentObsInd(true);
        var susMap = new HashMap<>();
        edxLabInformationDt.setEdxSusLabDTMap(susMap);
        edxLabInformationDt.setMessageControlID("TEST");
        edxLabInformationDt.setSendingFacilityClia("TEST");
        edxLabInformationDt.setSendingFacilityName("TEST");
        edxLabInformationDt.setFillerNumber("TEST");
        edxLabInformationDt.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        edxLabInformationDt.setParentObservationUid(10L);
        edxLabInformationDt.setRootObserbationUid(10L);
        edxLabInformationDt.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        edxLabInformationDt.setUserId(10L);
        edxLabInformationDt.setPatientUid(10L);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(checkingValueService, nbsObjectConverter, commonLabUtil, authUtil);
    }


    @Test
    void getObservationResultRequest_Test() throws DataProcessingException {
        List<HL7OBSERVATIONType> observation = result;
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();

        observation.get(0).getObservationResult().setReferencesRange("1^2^3");
        var res = observationResultRequestHandler.getObservationResultRequest(observation, labResultProxyContainer, edxLabInformationDt);

        assertNotNull(res);
        assertEquals(1, res.getTheObservationContainerCollection().size());
        assertEquals(1, res.getTheRoleDtoCollection().size());
        assertEquals(1, res.getTheParticipationDtoCollection().size());
        assertEquals(1, res.getTheActRelationshipDtoCollection().size());
        assertEquals(1, res.getTheOrganizationContainerCollection().size());

    }

    @Test
    void setEquipments_Test() {
        List<HL7EIType> equipmentIdType = new ArrayList<>();
        ObservationDto observationDto = new ObservationDto();
        Collection<ActIdDto> actIdDtoColl = new ArrayList<>();

        var ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        ei.setHL7UniversalID("TEST");
        ei.setHL7UniversalIDType("TEST");
        equipmentIdType.add(ei);

        observationDto.setObservationUid(10L);

        var res= observationResultRequestHandler.setEquipments(equipmentIdType, observationDto, actIdDtoColl);

        assertEquals(1, res.size());
    }

    @Test
    void processingAbnormalFlag_Test() throws DataProcessingException {
        List<HL7CWEType> abnormalFlag = new ArrayList<>();
        ObservationDto observationDto = new ObservationDto();
        ObservationContainer observationContainer = new ObservationContainer();

        var cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7Text("TEST");
        abnormalFlag.add(cwe);

        observationDto.setObservationUid(10L);
        observationContainer.getTheObservationDto().setObservationUid(10L);
        observationContainer.setTheObservationInterpDtoCollection(new ArrayList<>());

        when(checkingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("TEST");
        var res= observationResultRequestHandler.processingAbnormalFlag(abnormalFlag, observationDto, observationContainer);
        assertNotNull(res);
        assertEquals(1, res.getTheObservationInterpDtoCollection().size());
    }

    @Test
    void processingAbnormalFlag_Test_2() throws DataProcessingException {
        List<HL7CWEType> abnormalFlag = new ArrayList<>();
        ObservationDto observationDto = new ObservationDto();
        ObservationContainer observationContainer = new ObservationContainer();

        var cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7Text("TEST");
        abnormalFlag.add(cwe);

        observationDto.setObservationUid(10L);
        observationContainer.getTheObservationDto().setObservationUid(10L);
        observationContainer.setTheObservationInterpDtoCollection(new ArrayList<>());

        when(checkingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("");
        var res= observationResultRequestHandler.processingAbnormalFlag(abnormalFlag, observationDto, observationContainer);
        assertNotNull(res);
        assertEquals(1, res.getTheObservationInterpDtoCollection().size());
    }

    @Test
    void processingReferringRange_Test() {
        ObservationContainer observationContainer = new ObservationContainer();
        HL7OBXType type = result.get(0).getObservationResult();
        type.setReferencesRange("1");

        var res= observationResultRequestHandler.processingReferringRange(type, observationContainer);
        assertNotNull(res);

    }

    @Test
    void processingReferringRange_Test_2() {
        ObservationContainer observationContainer = new ObservationContainer();
        HL7OBXType type = result.get(0).getObservationResult();
        type.setReferencesRange("1^2^3^4^5");

        var res= observationResultRequestHandler.processingReferringRange(type, observationContainer);
        assertNotNull(res);

    }

    @Test
    void processingObservationMethod_Test() throws DataProcessingException {
        List<HL7CEType> methodArray = new ArrayList<>();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        ObservationContainer observationContainer = new ObservationContainer();

        HL7CEType method = new HL7CEType();
        method.setHL7Identifier("TEST");
        method.setHL7Text("TEST");
        methodArray.add(method);

        method = new HL7CEType();
        method.setHL7Identifier("TEST_1");
        method.setHL7Text("TEST_1");
        methodArray.add(method);

        when(checkingValueService.getCodeDescTxtForCd(any(), eq("TEST"))).thenReturn("TEST");


        var res= observationResultRequestHandler.processingObservationMethod(methodArray,edxLabInformationDto ,observationContainer);
        assertNotNull(res);
        assertEquals("TEST**TEST_1", res.getTheObservationDto().getMethodCd());
    }

    @SuppressWarnings("java:S2699")
    @Test
    void formatValue_Test() throws DataProcessingException {
        String text = "TEST^TEST^TEST";
        HL7OBXType hl7OBXType = new HL7OBXType();
        ObservationContainer observationContainer = new ObservationContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String elementName = "";

        hl7OBXType.setValueType(EdxELRConstant.ELR_CODED_WITH_EXC_CD);
        var unit = new HL7CEType();
        hl7OBXType.setUnits(unit);

        observationResultRequestHandler.formatValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void formatValue_Test_len_2() throws DataProcessingException {
        String text = "TE^TE";
        HL7OBXType hl7OBXType = new HL7OBXType();
        ObservationContainer observationContainer = new ObservationContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String elementName = "";

        hl7OBXType.setValueType(EdxELRConstant.ELR_CODED_WITH_EXC_CD);
        var unit = new HL7CEType();
        hl7OBXType.setUnits(unit);

        observationResultRequestHandler.formatValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void formatValue_Test_len_6() throws DataProcessingException {
        String text = "TE^TE^TE^TE^TE^TE^TE^TE";
        HL7OBXType hl7OBXType = new HL7OBXType();
        ObservationContainer observationContainer = new ObservationContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String elementName = "";

        hl7OBXType.setValueType(EdxELRConstant.ELR_CODED_WITH_EXC_CD);
        var unit = new HL7CEType();
        hl7OBXType.setUnits(unit);

        observationResultRequestHandler.formatValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);
    }

    @Test
    void formatValue_Test_txt_empty()  {
        String text = "";
        HL7OBXType hl7OBXType = new HL7OBXType();
        ObservationContainer observationContainer = new ObservationContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String elementName = "";

        hl7OBXType.setValueType(EdxELRConstant.ELR_CODED_WITH_EXC_CD);
        var unit = new HL7CEType();
        hl7OBXType.setUnits(unit);



        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationResultRequestHandler.formatValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);
        });
        assertNotNull(thrown);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void formValue_Numeric_Test_1() throws DataProcessingException {
        String text = "100";
        HL7OBXType hl7OBXType = new HL7OBXType();
        ObservationContainer observationContainer = new ObservationContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String elementName = "";
        hl7OBXType.setValueType(EdxELRConstant.ELR_NUMERIC_CD);
        var unit = new HL7CEType();
        unit.setHL7Identifier("mL");
        hl7OBXType.setUnits(unit);

        observationContainer.getTheObservationDto().setObservationUid(10L);

        observationResultRequestHandler.formatValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);

    }
    @SuppressWarnings("java:S2699")
    @Test
    void formValue_Txt_Test_1() throws DataProcessingException {
        String text = "adwada^adwadd";
        HL7OBXType hl7OBXType = new HL7OBXType();
        ObservationContainer observationContainer = new ObservationContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String elementName = "";
        hl7OBXType.setValueType(EdxELRConstant.ELR_TEXT_DT);
        var unit = new HL7CEType();
        unit.setHL7Identifier("mL");
        hl7OBXType.setUnits(unit);

        observationContainer.getTheObservationDto().setObservationUid(10L);

        observationResultRequestHandler.formatValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);

    }



    @Test
    void formatValue_Test_exp_un_support()  {
        String text = "";
        HL7OBXType hl7OBXType = new HL7OBXType();
        ObservationContainer observationContainer = new ObservationContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        String elementName = "";

        hl7OBXType.setValueType("BLAH");
        var unit = new HL7CEType();
        hl7OBXType.setUnits(unit);



        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationResultRequestHandler.formatValue(text, hl7OBXType, observationContainer, edxLabInformationDto, elementName);
        });
        assertNotNull(thrown);
    }


    @Test
    void getObservationResultRequest_Test_Exp() {
        List<HL7OBSERVATIONType> observation = result;
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();

        observation.get(0).getObservationResult().setReferencesRange("1^2^3");

        edxLabInformationDt.setParentObsInd(false);
        observation.get(0).getObservationResult().setObservationIdentifier(null);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationResultRequestHandler.getObservationResultRequest(observation, labResultProxyContainer, edxLabInformationDt);
        });

        assertNotNull(thrown);
    }
}
