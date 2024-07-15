package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxLabIdentiferDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.CommonLabUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.HL7SpecimenUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ObservationRequestHandlerTest {
    @Mock
    AuthUtil authUtil;
    @Mock
    private ICatchingValueService checkingValueService;
    @Mock
    private CommonLabUtil commonLabUtil;
    @Mock
    private NBSObjectConverter nbsObjectConverter;
    @Mock
    private HL7SpecimenUtil hl7SpecimenUtil;
    @Mock
    private HL7PatientHandler hl7PatientHandler;
    @InjectMocks
    private ObservationRequestHandler observationRequestHandler;
    private LabResultProxyContainer labResultProxyContainer;
    private EdxLabInformationDto edxLabInformationDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        AuthUtil.setGlobalAuthUser(userInfo);

        labResultProxyContainer = new LabResultProxyContainer();

        var orgConCol = new ArrayList<OrganizationContainer>();
        var orgCon = new OrganizationContainer();
        orgCon.setRole(EdxELRConstant.ELR_SENDING_FACILITY_CD);
        var entiCol = new ArrayList<EntityIdDto>();
        var enti = new EntityIdDto();
        enti.setTypeCd(EdxELRConstant.ELR_FACILITY_CD);
        entiCol.add(enti);
        orgCon.setTheEntityIdDtoCollection(entiCol);
        orgConCol.add(orgCon);
        labResultProxyContainer.setTheOrganizationContainerCollection(orgConCol);
        labResultProxyContainer.setTheObservationContainerCollection(new ArrayList<>());
        labResultProxyContainer.setThePersonContainerCollection(new ArrayList<>());


        edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setRootObserbationUid(10L);
        edxLabInformationDto.setNextUid(1);
        edxLabInformationDto.setMessageControlID("TEST");
        edxLabInformationDto.setOrderEffectiveDate(TimeStampUtil.getCurrentTimeStamp());
        edxLabInformationDto.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(checkingValueService, commonLabUtil, nbsObjectConverter, hl7SpecimenUtil, hl7PatientHandler, authUtil);
    }

    @Test
    void getObservationRequest_Test() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn("TEST");
        HL7SIType si = new HL7SIType();
        si.setHL7SequenceID("1");
        when(hl7OBRType.getSetIDOBR()).thenReturn(si); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        HL7EIType ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(ei); //CASE
        when(hl7OBRType.getParent()).thenReturn(null); //CASE
        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(cwe); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        var name = new HL7CNNType();
        name.setHL7IDNumber("TEST");
        nd.setHL7Name(name);
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);


        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("TEST");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        var result = observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        assertNotNull(result);
    }


    @Test
    void getObservationRequest_Test_2() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn("TEST");
        HL7SIType si = new HL7SIType();
        si.setHL7SequenceID("1");
        when(hl7OBRType.getSetIDOBR()).thenReturn(si); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        HL7EIType ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(ei); //CASE

        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(cwe); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        var name = new HL7CNNType();
        name.setHL7IDNumber("TEST");
        nd.setHL7Name(name);
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);

        HL7PRLType prl = new HL7PRLType();
        HL7CEType ce = new HL7CEType();
        ce.setHL7Identifier("TEST");
        ce.setHL7AlternateIdentifier("TEST");
        prl.setParentObservationIdentifier(ce);
        prl.setParentObservationSubidentifier("TEST");
        HL7TXType tx = new HL7TXType();
        tx.setHL7String("TEST");
        prl.setParentObservationValueDescriptor(tx);

        when(hl7OBRType.getParentResult()).thenReturn(prl);
        HL7EIPType eip = new HL7EIPType();
        eip.setHL7FillerAssignedIdentifier(ei);
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getParent()).thenReturn(eip); //CASE


        edxLabInformationDto.setFillerNumber("TEST");

        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("TEST");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        edxLabInformationDto.setEdxSusLabDTMap(null);
        var edxCol = new ArrayList<EdxLabIdentiferDto>();
        var edx = new EdxLabIdentiferDto();
        edx.setIdentifer("TEST");
        edx.setSubMapID("TEST");
        edx.setObservationValues(new ArrayList<>(Arrays.asList("TEST1", "TEST")));
        edx.setObservationUid(10L);
        edxCol.add(edx);
        edxLabInformationDto.setEdxLabIdentiferDTColl(edxCol);
        var sus = new HashMap<>();
        sus.put(10L, 10L);
        edxLabInformationDto.setEdxSusLabDTMap(sus);


        var result = observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        assertNotNull(result);


    }

    @SuppressWarnings("java:S5976")
    @Test
    void getObservationRequest_Test_exp_1() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn("TEST");
        HL7SIType si = new HL7SIType();
        si.setHL7SequenceID("1");
        when(hl7OBRType.getSetIDOBR()).thenReturn(si); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        HL7EIType ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(ei); //CASE
        HL7EIPType eip = new HL7EIPType();
        when(hl7OBRType.getParent()).thenReturn(eip); //CASE
        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(cwe); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);


        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("TEST");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        });
        assertNotNull(thrown);

    }

    @SuppressWarnings("java:S5976")
    @Test
    void getObservationRequest_Test_exp_2() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn("TEST");
        HL7SIType si = new HL7SIType();
        si.setHL7SequenceID("1");
        when(hl7OBRType.getSetIDOBR()).thenReturn(si); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        HL7EIType ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(ei); //CASE
        HL7EIPType eip = new HL7EIPType();
        when(hl7OBRType.getParent()).thenReturn(eip); //CASE
        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(cwe); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);


        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        });
        assertNotNull(thrown);

    }

    @SuppressWarnings("java:S5976")
    @Test
    void getObservationRequest_Test_exp_3() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn(null);
        HL7SIType si = new HL7SIType();
        si.setHL7SequenceID("1");
        when(hl7OBRType.getSetIDOBR()).thenReturn(si); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        HL7EIType ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(ei); //CASE
        HL7EIPType eip = new HL7EIPType();
        when(hl7OBRType.getParent()).thenReturn(eip); //CASE
        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(cwe); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);


        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("TEST");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        });
        assertNotNull(thrown);

    }


    @Test
    void getObservationRequest_Test_exp_4() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn("TEST");
        HL7SIType si = new HL7SIType();
        si.setHL7SequenceID("1");
        when(hl7OBRType.getSetIDOBR()).thenReturn(si); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(null); //CASE
        when(hl7OBRType.getParent()).thenReturn(null); //CASE
        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(cwe); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        var name = new HL7CNNType();
        name.setHL7IDNumber("TEST");
        nd.setHL7Name(name);
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);


        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("TEST");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        });
        assertNotNull(thrown);
    }

    @Test
    void getObservationRequest_Test_exp_5() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn("TEST");
        HL7SIType si = new HL7SIType();
        si.setHL7SequenceID("1");
        when(hl7OBRType.getSetIDOBR()).thenReturn(si); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        HL7EIType ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(ei); //CASE
        when(hl7OBRType.getParent()).thenReturn(null); //CASE
        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(null); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        var name = new HL7CNNType();
        name.setHL7IDNumber("TEST");
        nd.setHL7Name(name);
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);


        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("TEST");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        });
        assertNotNull(thrown);
    }


    @Test
    void getObservationRequest_Test_Coverage_1() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn("TEST");
        HL7SIType si = new HL7SIType();
        si.setHL7SequenceID("2");
        when(hl7OBRType.getSetIDOBR()).thenReturn(si); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier("TEST");
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        HL7EIType ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(ei); //CASE
        when(hl7OBRType.getParent()).thenReturn(null); //CASE
        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(cwe); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        var name = new HL7CNNType();
        name.setHL7IDNumber("TEST");
        nd.setHL7Name(name);
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);


        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("TEST");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        var result = observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        assertNotNull(result);
    }

    @Test
    void getObservationRequest_Test_Coverage_2() throws DataProcessingException {
        HL7OBRType hl7OBRType = Mockito.mock(HL7OBRType.class);
        HL7PatientResultSPMType hl7PatientResultSPMType = Mockito.mock(HL7PatientResultSPMType.class);


        when(hl7OBRType.getResultStatus()).thenReturn("TEST");

        when(hl7OBRType.getSetIDOBR()).thenReturn(null); // CASES
        HL7CWEType cwe = new HL7CWEType();
        cwe.setHL7Identifier(null);
        cwe.setHL7NameofCodingSystem(EdxELRConstant.ELR_LOINC_CD);
        cwe.setHL7AlternateIdentifier("TEST");
        cwe.setHL7AlternateText("TEST");
        cwe.setHL7Text("TEST");
        when(hl7OBRType.getDangerCode()).thenReturn(cwe);
        HL7EIType ei = new HL7EIType();
        ei.setHL7EntityIdentifier("TEST");
        when(hl7OBRType.getFillerOrderNumber()).thenReturn(ei); //CASE
        when(hl7OBRType.getParent()).thenReturn(null); //CASE
        when(hl7OBRType.getUniversalServiceIdentifier()).thenReturn(cwe); //CASE
        when(hl7OBRType.getResultsRptStatusChngDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationDateTime()).thenReturn(new HL7TSType());
        when(hl7OBRType.getObservationEndDateTime()).thenReturn(new HL7TSType());

        var cweLst = new ArrayList<HL7CWEType>();
        cweLst.add(cwe);
        when(hl7OBRType.getReasonforStudy()).thenReturn(cweLst);
        var cnLst = new ArrayList<HL7XCNType>();
        var cn = new HL7XCNType();
        cnLst.add(cn);
        when(hl7OBRType.getResultCopiesTo()).thenReturn(cnLst);

        when(hl7OBRType.getCollectorIdentifier()).thenReturn(cnLst);
        when(hl7OBRType.getOrderingProvider()).thenReturn(cnLst);

        var ndLst = new ArrayList<HL7NDLType>();
        var nd = new HL7NDLType();
        var name = new HL7CNNType();
        name.setHL7IDNumber("TEST");
        nd.setHL7Name(name);
        ndLst.add(nd);
        when(hl7OBRType.getAssistantResultInterpreter()).thenReturn(ndLst);
        when(hl7OBRType.getTechnician()).thenReturn(ndLst);
        when(hl7OBRType.getTranscriptionist()).thenReturn(ndLst);


        when(checkingValueService.findToCode(eq("ELR_LCA_STATUS"), any(), eq("ACT_OBJ_ST"))).thenReturn("TEST");


        var perCon = new PersonContainer();
        when(hl7PatientHandler.parseToPersonObject(any(), any())).thenReturn(perCon);


        var proCon = new PersonContainer();
        edxLabInformationDto.setOrderingProviderVO(proCon);
        var result = observationRequestHandler.getObservationRequest(hl7OBRType, hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);
        assertNotNull(result);
    }


}
