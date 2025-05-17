package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestDataReader;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.EntityIdUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class HL7PatientHandlerTest {
    @Mock
    private ICatchingValueDpService checkingValueService;
    @Mock
    private NBSObjectConverter nbsObjectConverter;
    @Mock
    private EntityIdUtil entityIdUtil;
    @InjectMocks
    private HL7PatientHandler hl7PatientHandler;
    @Mock
    private ICacheApiService cacheApiService;
    @Mock
    AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(checkingValueService, nbsObjectConverter, entityIdUtil, authUtil);
    }


    @Test
    void getPatientAndNextOfKin_Test() throws DataProcessingException, JAXBException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();

        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        var hl7PatientResult = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0);

        var enId = new EntityIdDto();
        enId.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        enId.setRootExtensionTxt("123-23-4567");
        enId.setEntityUid(10L);
        when(entityIdUtil.processEntityData(any(), any(), eq(null), eq(0))).thenReturn(enId);

        enId = new EntityIdDto();
        enId.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        enId.setRootExtensionTxt("123-23-456711");
        enId.setEntityUid(10L);
        when(entityIdUtil.processEntityData(any(), any(), eq(null), eq(1))).thenReturn(enId);

        var lang = new HL7CEType();
        lang.setHL7Identifier("ENG");
        lang.setHL7Text("ENG");
        hl7PatientResult.getPATIENT().getPatientIdentification().getPrimaryLanguage().add(lang);


        var exref = new ElrXref();
        exref.setFromCodeSetNm("ELR_LCA_SEX");
        exref.setFromCode("F");
        exref.setToCodeSetNm("P_SEX");
        exref.setToCode("TO_CODE");

        when(nbsObjectConverter.processHL7TSTypeForDOBWithoutTime(any())).thenReturn(TimeStampUtil.getCurrentTimeStamp("UTC"));

        hl7PatientResult.getPATIENT().getPatientIdentification().setBirthPlace("US");

        var ethnic = new HL7CWEType();
        hl7PatientResult.getPATIENT().getPatientIdentification().getEthnicGroup().add(ethnic);

        var perEth = new PersonEthnicGroupDto();
        perEth.setEthnicGroupCd("CODE");
        when(nbsObjectConverter.ethnicGroupType(any(), any())).thenReturn(perEth);
        exref = new ElrXref();
        exref.setFromCodeSetNm("ELR_LCA_ETHN_GRP");
        exref.setFromCode("CODE");
        exref.setToCodeSetNm("P_ETHN_GRP");
        exref.setToCode("TO_CODE");

//        var map = new HashMap<String, String>();
//        map.put("TO_CODE", "CODE");
//        when(checkingValueService.getCodedValues(any(), any())).thenReturn(map);

        var martial = new HL7CEType();
        martial.setHL7Identifier("MARTIAL");
        martial.setHL7Text("MARTIAL");
        hl7PatientResult.getPATIENT().getPatientIdentification().setMaritalStatus(martial);

        var mothderIden = new HL7CXType();
        hl7PatientResult.getPATIENT().getPatientIdentification().getMothersIdentifier().add(mothderIden);
        enId = new EntityIdDto();
        enId.setTypeCd(EdxELRConstant.ELR_MOTHER_IDENTIFIER);
        enId.setEntityUid(10L);
        when(nbsObjectConverter.processEntityData(any(), any(), eq(EdxELRConstant.ELR_MOTHER_IDENTIFIER), eq(2))).thenReturn(enId);

        var motherName = new HL7XPNType();
        var famName = new HL7FNType();
        famName.setHL7Surname("TEST");
        motherName.setHL7FamilyName(famName);
        motherName.setHL7GivenName("TEST");
        hl7PatientResult.getPATIENT().getPatientIdentification().getMothersMaidenName().add(motherName);

        var birthOrder =new HL7NMType();
        birthOrder.setHL7Numeric(BigInteger.ONE);
        hl7PatientResult.getPATIENT().getPatientIdentification().setBirthOrder(birthOrder);

        hl7PatientResult.getPATIENT().getPatientIdentification().setMultipleBirthIndicator("TEST");

        var patientAcct = new HL7CXType();
        hl7PatientResult.getPATIENT().getPatientIdentification().setPatientAccountNumber(patientAcct);
        enId = new EntityIdDto();
        enId.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        enId.setRootExtensionTxt("123-23-4567");
        enId.setEntityUid(10L);
        when(nbsObjectConverter.processEntityData(any(), any(), eq(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER), eq(3))).thenReturn(enId);

        var deathTime = new HL7TSType();
        hl7PatientResult.getPATIENT().getPatientIdentification().setPatientDeathDateAndTime(deathTime);

        var bunessPhone = new HL7XTNType();
        hl7PatientResult.getPATIENT().getPatientIdentification().getPhoneNumberBusiness().add(bunessPhone);

        var homePone = new HL7XTNType();
        hl7PatientResult.getPATIENT().getPatientIdentification().getPhoneNumberHome().add(homePone);

        var entiyLoca = new EntityLocatorParticipationDto();
        when(nbsObjectConverter.personTelePhoneType(any(),eq(EdxELRConstant.ELR_PATIENT_CD), any())).thenReturn(entiyLoca);

        var race = new PersonRaceDto();
        race.setRaceCategoryCd("CODE");
        when(nbsObjectConverter.raceType(any(), any())).thenReturn(race);
        exref = new ElrXref();
        exref.setFromCodeSetNm("ELR_LCA_RACE");
        exref.setFromCode("CODE");
        exref.setToCodeSetNm("P_RACE_CAT");
        exref.setToCode("TO_CODE");
//        SrteCache.elrXrefsList.add(exref);
//        SrteCache.raceCodesMap.put("TO_CODE", "TO_CODE");

        // NOK
        when(checkingValueService.getCodeDescTxtForCd(any(),eq( EdxELRConstant.ELR_NEXT_OF_KIN_RL_CLASS))).thenReturn("NOK");

        when(cacheApiService.getSrteCacheObject(any(), any())).thenReturn("{}");

        var res = hl7PatientHandler.getPatientAndNextOfKin(hl7PatientResult, labResultProxyContainer, edxLabInformationDto);

        assertNotNull(test);
        assertEquals(2, res.getThePersonContainerCollection().size());
    }

    @Test
    void parseToPersonObject_Role_Check_1() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();


        edxLabInformationDto.setRole(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);

        var res =  hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        assertNotNull(res);
    }

    @Test
    void parseToPersonObject_Role_Check_2() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();


        edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_PROVIDER_CD);

        var res =  hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        assertNotNull(res);
    }

    @Test
    void parseToPersonObject_Role_Check_3() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();


        edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_VERIFIER_CD);

        var res =  hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        assertNotNull(res);
    }

    @Test
    void parseToPersonObject_Role_Check_4() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();


        edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_ASSISTANT_CD);

        var res =  hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        assertNotNull(res);
    }

    @Test
    void parseToPersonObject_Role_Check_5() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();


        edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_PERFORMER_CD);

        var res =  hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        assertNotNull(res);
    }

    @Test
    void parseToPersonObject_Role_Check_6() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();


        edxLabInformationDto.setRole(EdxELRConstant.ELR_LAB_ENTERER_CD);

        var res =  hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        assertNotNull(res);
    }

    @Test
    void parseToPersonObject_Role_Check_7() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();


        edxLabInformationDto.setRole(EdxELRConstant.ELR_OP_CD);

        var res =  hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        assertNotNull(res);
    }

    @Test
    void parseToPersonObject_Role_Check_8() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();


        edxLabInformationDto.setRole(EdxELRConstant.ELR_COPY_TO_CD);

        var res =  hl7PatientHandler.parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        assertNotNull(res);
    }
}
