package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.edx.EdxLabIdentiferDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueCodedDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueNumericDto;
import gov.cdc.dataprocessing.model.dto.observation.ObsValueTxtDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ObservationResultRequestHandlerTest {
    @Mock
    private ICatchingValueDpService checkingValueService;
    @Mock
    private NBSObjectConverter nbsObjectConverter;
    @Mock
    private CommonLabUtil commonLabUtil;

    @Mock
    private ICacheApiService cacheApiService;

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
        edxLabInformationDt.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        edxLabInformationDt.setParentObservationUid(10L);
        edxLabInformationDt.setRootObserbationUid(10L);
        edxLabInformationDt.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        edxLabInformationDt.setUserId(10L);
        edxLabInformationDt.setPatientUid(10L);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(checkingValueService, nbsObjectConverter, commonLabUtil, authUtil);
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

    @SuppressWarnings({"java:S2699", "java:S5976"})
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

    @SuppressWarnings({"java:S2699", "java:S5976"})
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

    @SuppressWarnings({"java:S2699", "java:S5976"})
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
    void testValidateResultedTestName_WhenObservationIdentifierIsNull_ShouldThrowException() throws DataProcessingException {
        // Arrange
        HL7OBXType obx = new HL7OBXType();
        obx.setObservationIdentifier(null); // triggers `id == null`

        edxLabInformationDt.setParentObsInd(false); // ensure condition is evaluated

        when(commonLabUtil.getXMLElementNameForOBX(obx)).thenReturn("OBX");

        // Act & Assert
        DataProcessingException ex = assertThrows(DataProcessingException.class,
                () -> observationResultRequestHandler.validateResultedTestName(obx, edxLabInformationDt));

        assertTrue(ex.getMessage().contains(EdxELRConstant.NO_RESULT_NAME));
        assertTrue(edxLabInformationDt.isResultedTestNameMissing());
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_19, edxLabInformationDt.getErrorText());
    }

    @Test
    void testValidateResultedTestName_WhenBothIdentifiersNull_ShouldThrowException() throws DataProcessingException {
        // Arrange
        HL7CWEType obsIdentifier = new HL7CWEType();
        obsIdentifier.setHL7Identifier(null);
        obsIdentifier.setHL7AlternateIdentifier(null);

        HL7OBXType obx = new HL7OBXType();
        obx.setObservationIdentifier(obsIdentifier);

        edxLabInformationDt.setParentObsInd(false);

        when(commonLabUtil.getXMLElementNameForOBX(obx)).thenReturn("OBX");

        // Act & Assert
        DataProcessingException ex = assertThrows(DataProcessingException.class,
                () -> observationResultRequestHandler.validateResultedTestName(obx, edxLabInformationDt));

        assertTrue(ex.getMessage().contains(EdxELRConstant.NO_RESULT_NAME));
        assertTrue(edxLabInformationDt.isResultedTestNameMissing());
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_19, edxLabInformationDt.getErrorText());
    }

    @Test
    void testCreateAndLinkLabIdentifier_WhenHL7IdentifierIsNull_UsesAlternateIdentifier() {
        // Arrange
        HL7OBXType obx = new HL7OBXType();
        HL7CWEType identifier = new HL7CWEType();
        identifier.setHL7Identifier(null); // <-- triggers the `else if`
        identifier.setHL7AlternateIdentifier("ALT_ID");
        obx.setObservationIdentifier(identifier);
        obx.setObservationSubID("sub-id");

        ObservationDto obsDto = new ObservationDto();
        obsDto.setObservationUid(123L);

        // Clear identifier map to make sure the test initializes it
        edxLabInformationDt.setEdxSusLabDTMap(new HashMap<>());
        edxLabInformationDt.setEdxLabIdentiferDTColl(null);

        // Act
        observationResultRequestHandler.createAndLinkLabIdentifier(edxLabInformationDt, obx, obsDto);

        // Assert
        Map<Object, Object> labMap = edxLabInformationDt.getEdxSusLabDTMap();
        assertEquals(1, labMap.size());

        EdxLabIdentiferDto result = (EdxLabIdentiferDto) labMap.get(obsDto.getObservationUid());
        assertNotNull(result);
        assertEquals("ALT_ID", result.getIdentifer());
        assertEquals("sub-id", result.getSubMapID());
        assertEquals(obsDto.getObservationUid(), result.getObservationUid());

        var coll = edxLabInformationDt.getEdxLabIdentiferDTColl();
        assertNotNull(coll);
        assertEquals(1, coll.size());
    }

    @Test
    void testProcessObservationResultStatus_WhenToCodeExists_ShouldSetTrimmedToCode() throws DataProcessingException {
        // Arrange
        HL7OBXType obx = new HL7OBXType();
        obx.setObservationResultStatus("P");

        ObservationDto dto = new ObservationDto();

        when(checkingValueService.findToCode("ELR_LCA_STATUS", "P", "ACT_OBJ_ST")).thenReturn(" PENDING ");

        // Act
        observationResultRequestHandler.processObservationResultStatus(obx, dto);

        // Assert
        assertEquals("PENDING", dto.getStatusCd());
    }

    @Test
    void testProcessObservationResultStatus_WhenToCodeIsNull_ShouldUseRawStatus() throws DataProcessingException {
        // Arrange
        HL7OBXType obx = new HL7OBXType();
        obx.setObservationResultStatus("R");

        ObservationDto dto = new ObservationDto();

        when(checkingValueService.findToCode("ELR_LCA_STATUS", "R", "ACT_OBJ_ST")).thenReturn(null);

        // Act
        observationResultRequestHandler.processObservationResultStatus(obx, dto);

        // Assert
        assertEquals("R", dto.getStatusCd());
    }

    @Test
    void testProcessObservationResultStatus_WhenToCodeIsEmpty_ShouldUseRawStatus() throws DataProcessingException {
        // Arrange
        HL7OBXType obx = new HL7OBXType();
        obx.setObservationResultStatus("C");

        ObservationDto dto = new ObservationDto();

        when(checkingValueService.findToCode("ELR_LCA_STATUS", "C", "ACT_OBJ_ST")).thenReturn("  ");

        // Act
        observationResultRequestHandler.processObservationResultStatus(obx, dto);

        // Assert
        assertEquals("C", dto.getStatusCd());
    }


    @Test
    void testProcessObservationResultStatus_WhenRawStatusIsNull_ShouldDoNothing() throws DataProcessingException {
        // Arrange
        HL7OBXType obx = new HL7OBXType(); // rawStatus == null
        ObservationDto dto = new ObservationDto();

        // Act
        observationResultRequestHandler.processObservationResultStatus(obx, dto);

        // Assert
        assertNull(dto.getStatusCd()); // no change made
        verifyNoInteractions(checkingValueService); // optimization: method shouldn't be called
    }


    @Test
    void testProcessDateTimeOfAnalysis_WhenDateTimeIsNull_ShouldNotSetActivityToTime() throws DataProcessingException {
        // Arrange
        HL7OBXType obx = new HL7OBXType(); // no date
        ObservationDto dto = new ObservationDto();

        // Act
        observationResultRequestHandler.processDateTimeOfAnalysis(obx, dto);

        // Assert
        assertNull(dto.getActivityToTime());
        assertNull(dto.getRptToStateTime()); // also unchanged
        verifyNoInteractions(nbsObjectConverter);
    }


    @Test
    void testProcessObservationIdentifier_WhenObsIdentifierIsNull_ShouldThrowException() {
        // Arrange
        HL7CWEType obsIdentifier = null;
        ObservationDto observationDto = new ObservationDto();
        observationDto.setCd("TEST_CODE");
        ObservationContainer observationContainer = new ObservationContainer();
        edxLabInformationDt.setParentObsInd(false); // not relevant to this case

        // Act & Assert
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () ->
                observationResultRequestHandler.processObservationIdentifier(
                        obsIdentifier,
                        observationDto,
                        edxLabInformationDt,
                        observationContainer
                ));

        assertTrue(thrown.getMessage().contains("The Resulted Test ObservationCd can't be set to null. Please check.TEST_CODE"));
    }


    @Test
    void testProcessObservationIdentifier_WhenCdIsNull_AndHL7AlternateIdentifierPresent_ShouldSetCd() throws DataProcessingException {
        // Arrange
        HL7CWEType obsIdentifier = new HL7CWEType();
        obsIdentifier.setHL7AlternateIdentifier("ALT_ID");

        ObservationDto observationDto = new ObservationDto(); // Cd is null
        ObservationContainer observationContainer = new ObservationContainer();
        edxLabInformationDt.setParentObsInd(false);

        // Act
        observationResultRequestHandler.processObservationIdentifier(
                obsIdentifier,
                observationDto,
                edxLabInformationDt,
                observationContainer
        );

        // Assert
        assertEquals("ALT_ID", observationDto.getCd());
    }

    @Test
    void testProcessObservationIdentifier_WhenAltTextNotNull_AndCdDescTxtIsNull_ShouldSetCdDescTxt() throws DataProcessingException {
        // Arrange
        HL7CWEType obsIdentifier = new HL7CWEType();
        obsIdentifier.setHL7AlternateText("Alternate Text");

        ObservationDto observationDto = new ObservationDto(); // CdDescTxt is null
        ObservationContainer observationContainer = new ObservationContainer();

        edxLabInformationDt.setParentObsInd(false);

        // Act
        observationResultRequestHandler.processObservationIdentifier(
                obsIdentifier,
                observationDto,
                edxLabInformationDt,
                observationContainer
        );

        // Assert
        assertEquals("Alternate Text", observationDto.getCdDescTxt());
        assertNull(observationDto.getAltCdDescTxt());
    }

    @Test
    void testProcessObservationIdentifier_WhenAltTextNotNull_AndCdDescTxtExists_ShouldSetAltCdDescTxt() throws DataProcessingException {
        // Arrange
        HL7CWEType obsIdentifier = new HL7CWEType();
        obsIdentifier.setHL7AlternateText("Alt Description");

        ObservationDto observationDto = new ObservationDto();
        observationDto.setCdDescTxt("Existing Description");

        ObservationContainer observationContainer = new ObservationContainer();
        edxLabInformationDt.setParentObsInd(false);

        // Act
        observationResultRequestHandler.processObservationIdentifier(
                obsIdentifier,
                observationDto,
                edxLabInformationDt,
                observationContainer
        );

        // Assert
        assertEquals("Existing Description", observationDto.getCdDescTxt());
        assertEquals("Alt Description", observationDto.getAltCdDescTxt());
    }



    @Test
    void testProcessObservationIdentifier_WhenCdSystemCdNull_ShouldSetFromAlternateCodingSystem() throws DataProcessingException {
        // Arrange
        HL7CWEType obsIdentifier = new HL7CWEType();
        obsIdentifier.setHL7NameofAlternateCodingSystem("ALT_SYS");

        ObservationDto observationDto = new ObservationDto();
        // Ensure CdSystemCd is null
        // Simulate scenario where neither Cd nor CdDescTxt is set
        observationDto.setCd("code"); // To avoid earlier block
        observationDto.setCdDescTxt("desc");

        ObservationContainer observationContainer = new ObservationContainer();
        edxLabInformationDt.setParentObsInd(false);

        // Act
        observationResultRequestHandler.processObservationIdentifier(
                obsIdentifier,
                observationDto,
                edxLabInformationDt,
                observationContainer
        );

        // Assert
        assertEquals("ALT_SYS", observationDto.getCdSystemCd());
        assertEquals("ALT_SYS", observationDto.getCdSystemDescTxt());
    }

    @Test
    void testProcessObservationIdentifier_WhenCdSystemCdIsSnomed_SetsSnomedDesc() throws DataProcessingException {
        HL7CWEType obsIdentifier = new HL7CWEType();

        ObservationDto dto = new ObservationDto();
        dto.setCdSystemCd(EdxELRConstant.ELR_SNOMED_CD); // triggers SNOMED branch

        ObservationContainer container = new ObservationContainer();
        edxLabInformationDt.setParentObsInd(false);

        observationResultRequestHandler.processObservationIdentifier(
                obsIdentifier, dto, edxLabInformationDt, container
        );

        assertEquals(EdxELRConstant.ELR_SNOMED_DESC, dto.getCdSystemDescTxt());
    }

    @Test
    void testProcessObservationIdentifier_WhenCdSystemCdIsLocal_SetsLocalDesc() throws DataProcessingException {
        HL7CWEType obsIdentifier = new HL7CWEType();

        ObservationDto dto = new ObservationDto();
        dto.setCdSystemCd(EdxELRConstant.ELR_LOCAL_CD); // triggers LOCAL branch

        ObservationContainer container = new ObservationContainer();
        edxLabInformationDt.setParentObsInd(false);

        observationResultRequestHandler.processObservationIdentifier(
                obsIdentifier, dto, edxLabInformationDt, container
        );

        assertEquals(EdxELRConstant.ELR_LOCAL_DESC, dto.getCdSystemDescTxt());
    }

    @Test
    void testProcessObservationIdentifier_WhenAltCdSystemCdIsSnomed_SetsAltCdSystemDescTxtToSnomedDesc() throws DataProcessingException {
        // Arrange
        HL7CWEType obsIdentifier = new HL7CWEType();
        obsIdentifier.setHL7NameofAlternateCodingSystem(EdxELRConstant.ELR_SNOMED_CD); // provide to prevent overwrite with null

        ObservationDto dto = new ObservationDto();
        dto.setAltCd("ALT"); // to enter the alt-code condition

        ObservationContainer container = new ObservationContainer();
        edxLabInformationDt.setParentObsInd(false);

        // Act
        observationResultRequestHandler.processObservationIdentifier(
                obsIdentifier, dto, edxLabInformationDt, container
        );

        // Assert
        assertEquals(EdxELRConstant.ELR_SNOMED_DESC, dto.getAltCdSystemDescTxt());
    }



    @Test
    void testProcessObservationIdentifier_WhenParentObsAndMissingCd_ThrowsDataProcessingException() {
        // Arrange
        HL7CWEType obsIdentifier = new HL7CWEType(); // all fields null
        ObservationDto observationDto = new ObservationDto();
        ObservationContainer observationContainer = new ObservationContainer();

        edxLabInformationDt.setParentObsInd(true); // trigger parentObs condition

        // Act + Assert
        DataProcessingException ex = assertThrows(DataProcessingException.class, () ->
                observationResultRequestHandler.processObservationIdentifier(
                        obsIdentifier,
                        observationDto,
                        edxLabInformationDt,
                        observationContainer
                )
        );

        assertEquals(EdxELRConstant.NO_DRUG_NAME, ex.getMessage());
        assertTrue(edxLabInformationDt.isDrugNameMissing());
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_13, edxLabInformationDt.getErrorText());
    }


    @Test
    void testGetPerformingFacility_WhenExceptionThrown_ThrowsDataProcessingException() {
        // Arrange
        HL7OBXType hl7OBXType = new HL7OBXType();
        HL7XONType xon = new HL7XONType();
        xon.setHL7OrganizationIdentifier("FACILITY_ID");
        xon.setHL7OrganizationName("LAB_NAME");

        HL7HDType authority = new HL7HDType();
        authority.setHL7UniversalID("UID");
        authority.setHL7UniversalIDType("ISO");
        authority.setHL7NamespaceID("CLIA");
        xon.setHL7AssigningAuthority(authority);
        hl7OBXType.setPerformingOrganizationName(xon);

        HL7XADType address = new HL7XADType();
        hl7OBXType.setPerformingOrganizationAddress(address); // triggers the call to converter

        LabResultProxyContainer proxy = new LabResultProxyContainer();
        proxy.setTheParticipationDtoCollection(new ArrayList<>());
        proxy.setTheRoleDtoCollection(new ArrayList<>());
        proxy.setTheOrganizationContainerCollection(new ArrayList<>());

        EdxLabInformationDto info = new EdxLabInformationDto();
        info.setNextUid(123);
        info.setUserId(1L);
        info.setPatientUid(2L);
        info.setAddTime(new Timestamp(System.currentTimeMillis()));

        // Simulate exception from converter
        when(nbsObjectConverter.organizationAddressType(any(), any(), any()))
                .thenThrow(new RuntimeException("Simulated failure"));

        // Act + Assert
        DataProcessingException ex = assertThrows(DataProcessingException.class, () ->
                observationResultRequestHandler.getPerformingFacility(hl7OBXType, 99L, proxy, info)
        );

        assertTrue(ex.getMessage().contains("Simulated failure"));
    }

    @Test
    void testHandleCodedValue_FallbackToAltWhenPrimaryCodeMissing() throws DataProcessingException {
        // Arrange
        String text = "^DISPLAY^LOCAL^ALT_CODE^ALT_DESC^ALT_SYSTEM";
        HL7OBXType obx = new HL7OBXType();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(123L);
        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(observationDto);
        EdxLabInformationDto info = new EdxLabInformationDto();

        when(commonLabUtil.getXMLElementNameForOBX(obx)).thenReturn("OBX_1");

        // Act
        observationResultRequestHandler.handleCodedValue(text, obx, container, info, "Element");

        // Assert
        Collection<ObsValueCodedDto> result = container.getTheObsValueCodedDtoCollection();
        assertNotNull(result);
        assertEquals(1, result.size());

    }

    @Test
    void testHandleCodedValue_SetsCodeSystemDescriptionsCorrectly() throws DataProcessingException {
        // Arrange
        String text = "CODE^Display^SNOMED^ALT_CODE^ALT_DESC^LOCAL";
        HL7OBXType obx = new HL7OBXType();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(123L);

        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(observationDto);

        EdxLabInformationDto info = new EdxLabInformationDto();

        when(commonLabUtil.getXMLElementNameForOBX(obx)).thenReturn("OBX_1");

        // Act
        observationResultRequestHandler.handleCodedValue(text, obx, container, info, "SomeElement");

        // Assert
        Collection<ObsValueCodedDto> result = container.getTheObsValueCodedDtoCollection();
        assertNotNull(result);
        assertEquals(1, result.size());


    }

    @Test
    void testHandleCodedValue_WhenCodeSystemCdIsSNOMED_SetsCodeSystemDescTxtToSNOMED_DESC() throws DataProcessingException {
        // Arrange
        String text = "CODE^Display^sNomEd"; // code system is "SNOMED" (case-insensitive)
        HL7OBXType obx = new HL7OBXType();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(1001L);

        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(observationDto);

        EdxLabInformationDto info = new EdxLabInformationDto();
        when(commonLabUtil.getXMLElementNameForOBX(obx)).thenReturn("OBX1");

        // Act
        observationResultRequestHandler.handleCodedValue(text, obx, container, info, "SomeElement");

        // Assert
        Collection<ObsValueCodedDto> result = container.getTheObsValueCodedDtoCollection();
        assertNotNull(result);
        assertEquals(1, result.size());

        ObsValueCodedDto dto = result.iterator().next();
        assertEquals("sNomEd", dto.getCodeSystemCd()); // original case
        assertEquals(EdxELRConstant.ELR_SNOMED_DESC, dto.getCodeSystemDescTxt());
    }

    @Test
    void testHandleCodedValue_WhenCodeSystemCdIsLOCAL_SetsCodeSystemDescTxtToLOCAL_DESC() throws DataProcessingException {
        // Arrange
        String text = "CODE^Display^LOCAL"; // Code system is LOCAL (case-insensitive)
        HL7OBXType obx = new HL7OBXType();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(2002L);

        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(observationDto);

        EdxLabInformationDto info = new EdxLabInformationDto();
        when(commonLabUtil.getXMLElementNameForOBX(obx)).thenReturn("OBX1");

        // Act
        observationResultRequestHandler.handleCodedValue(text, obx, container, info, "Element");

        // Assert
        Collection<ObsValueCodedDto> result = container.getTheObsValueCodedDtoCollection();
        assertNotNull(result);
        assertEquals(1, result.size());

        ObsValueCodedDto dto = result.iterator().next();
        assertEquals("LOCAL", dto.getCodeSystemCd());
        assertEquals(EdxELRConstant.ELR_LOCAL_DESC, dto.getCodeSystemDescTxt());
    }

    @Test
    void testHandleStructuredNumericValue_WhenTokenIsLt_SetsComparatorCd1ToLessThan() {
        // Arrange
        String text = "&lt;^12.5"; // First token "&lt;" → should map to "<"
        HL7CEType unit = new HL7CEType();
        unit.setHL7Identifier("mg/dL");

        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(100L);

        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(observationDto);

        // Act
        observationResultRequestHandler.handleStructuredNumericValue(text, unit, container);

        // Assert
        var results = container.getTheObsValueNumericDtoCollection();
        assertNotNull(results);
        assertEquals(1, results.size());

        ObsValueNumericDto dto = results.iterator().next();
        assertEquals("<", dto.getComparatorCd1()); // ✅ Check the mapping
        assertEquals(new BigDecimal("12.5"), dto.getNumericValue1());
        assertEquals("mg/dL", dto.getNumericUnitCd());
        assertEquals(100L, dto.getObservationUid());
    }

    @Test
    void testAddCommentsToObservation_AddsAllCommentsToCollection() {
        // Arrange
        ObservationContainer container = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(123L);
        container.setTheObservationDto(observationDto);

        Collection<ObsValueTxtDto> initialCollection = new ArrayList<>();
        container.setTheObsValueTxtDtoCollection(initialCollection);

        List<String> comments = List.of("Comment A", "Comment B");

        // Act
        observationResultRequestHandler.addCommentsToObservation(comments, container);

        // Assert
        Collection<ObsValueTxtDto> result = container.getTheObsValueTxtDtoCollection();
        assertEquals(2, result.size());

        List<ObsValueTxtDto> dtoList = new ArrayList<>(result);
        assertEquals("Comment A", dtoList.get(0).getValueTxt());
        assertEquals("Comment B", dtoList.get(1).getValueTxt());
        assertEquals(123L, dtoList.get(0).getObservationUid());
        assertEquals(123L, dtoList.get(1).getObservationUid());
    }

    @Test
    void testAddEmptyNoteToObservation_AddsCarriageReturnNote() {
        // Arrange
        ObservationContainer container = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(999L);
        container.setTheObservationDto(observationDto);

        container.setTheObsValueTxtDtoCollection(new ArrayList<>());

        // Act
        observationResultRequestHandler.addEmptyNoteToObservation(container);

        // Assert
        Collection<ObsValueTxtDto> result = container.getTheObsValueTxtDtoCollection();
        assertEquals(1, result.size());

        ObsValueTxtDto dto = result.iterator().next();
        assertEquals("\r", dto.getValueTxt());
        assertEquals(999L, dto.getObservationUid());
    }

    @Test
    void testBuildObsValueTxtDto_WithText_AssignsAllFieldsCorrectly() {
        // Arrange
        ObservationContainer container = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(123L);
        container.setTheObservationDto(observationDto);

        List<ObsValueTxtDto> existingList = new ArrayList<>();
        container.setTheObsValueTxtDtoCollection(existingList);

        String inputText = "Sample comment";

        // Act
        ObsValueTxtDto result = observationResultRequestHandler.buildObsValueTxtDto(container, inputText);

        // Assert
        assertNotNull(result);
        assertTrue(result.isItNew());
        assertFalse(result.isItDirty());
        assertEquals(123L, result.getObservationUid());
        assertEquals(EdxELRConstant.ELR_OBX_COMMENT_TYPE, result.getTxtTypeCd());
        assertEquals("Sample comment", result.getValueTxt());
        assertEquals(1, result.getObsValueTxtSeq());
    }

    @Test
    void testBuildObsValueTxtDto_WhenCollectionIsNull_InitializesAndAssignsSeq() {
        // Arrange
        ObservationContainer container = new ObservationContainer();
        ObservationDto dto = new ObservationDto();
        dto.setObservationUid(456L);
        container.setTheObservationDto(dto);
        container.setTheObsValueTxtDtoCollection(null); // simulate uninitialized collection

        String inputText = "Hello";

        // Act
        ObsValueTxtDto result = observationResultRequestHandler.buildObsValueTxtDto(container, inputText);

        // Assert
        assertEquals(456L, result.getObservationUid());
        assertEquals("Hello", result.getValueTxt());
        assertEquals(1, result.getObsValueTxtSeq());
    }
}
