package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.phdc.HL7CXType;
import gov.cdc.dataprocessing.model.phdc.HL7HDType;
import gov.cdc.dataprocessing.model.phdc.HL7TSType;
import gov.cdc.dataprocessing.model.phdc.HL7XADType;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class NBSObjectConverterTest {
    @Mock
    private ICatchingValueService checkingValueService;
    @Mock
    private EntityIdUtil entityIdUtil;
    @InjectMocks
    private NBSObjectConverter nbsObjectConverter;
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
        Mockito.reset(checkingValueService, entityIdUtil, authUtil);
    }

    @Test
    void mapPersonNameType_Test() throws JAXBException, DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        var hl7XPNType = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getPatientName();

        var res = nbsObjectConverter.mapPersonNameType(hl7XPNType.get(0), personContainer);
        assertNotNull(res);

    }

    @Test
    void processEntityData_Test() throws JAXBException, DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var personDt = new PersonDto();
        personContainer.setThePersonDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        // Person Acct
        // Mother identifier
        var mothderIden = new HL7CXType();
        mothderIden.setHL7IDNumber("12");
        var auth = new HL7HDType();
        mothderIden.setHL7AssigningAuthority(auth);
        xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier().add(mothderIden);

        var hl7CXType =   xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier();


        var res = nbsObjectConverter.processEntityData(hl7CXType.get(0), personContainer, EdxELRConstant.ELR_PATIENT_ALTERNATE_IND, 1);
        assertNotNull(res);

    }
    @Test
    void processEntityData_Test_2() throws JAXBException, DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var personDt = new PersonDto();
        personContainer.setThePersonDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        var mothderIden = new HL7CXType();
        mothderIden.setHL7IDNumber("12");
        var auth = new HL7HDType();
        mothderIden.setHL7AssigningAuthority(auth);
        xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier().add(mothderIden);

        var hl7CXType =   xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier();


        var res = nbsObjectConverter.processEntityData(hl7CXType.get(0), personContainer, EdxELRConstant.ELR_MOTHER_IDENTIFIER, 1);
        assertNotNull(res);

    }

    @Test
    void processEntityData_Test_3() throws JAXBException, DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var personDt = new PersonDto();
        personContainer.setThePersonDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        var mothderIden = new HL7CXType();
        mothderIden.setHL7IDNumber("12");
        var auth = new HL7HDType();
        mothderIden.setHL7AssigningAuthority(auth);
        xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier().add(mothderIden);

        var hl7CXType =   xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier();


        var res = nbsObjectConverter.processEntityData(hl7CXType.get(0), personContainer, EdxELRConstant.ELR_ACCOUNT_IDENTIFIER, 1);
        assertNotNull(res);

    }

    @Test
    void processEntityData_Test_4() throws JAXBException, DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var personDt = new PersonDto();
        personContainer.setThePersonDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        var mothderIden = new HL7CXType();
        mothderIden.setHL7IDNumber("12");
        var auth = new HL7HDType();
        mothderIden.setHL7AssigningAuthority(auth);
        xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier().add(mothderIden);
        xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier().get(0).setHL7IdentifierTypeCode(null);
        var hl7CXType =   xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getMothersIdentifier();

        when(checkingValueService.getCodeDescTxtForCd(any(),eq(EdxELRConstant.EI_TYPE))).thenReturn("CODE");

        var res = nbsObjectConverter.processEntityData(hl7CXType.get(0), personContainer, "BLAH", 1);
        assertNotNull(res);

    }

    @Test
    void personAddressType_Test() throws JAXBException {
        PersonContainer personContainer = new PersonContainer();
        var personDt = new PersonDto();
        personContainer.setThePersonDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        HL7XADType address = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getPatientAddress().get(0);

        var res = nbsObjectConverter.personAddressType(address,EdxELRConstant.ELR_OP_CD, personContainer);
        assertNotNull(res);
    }

    @Test
    void personAddressType_Test_2() throws JAXBException, DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var personDt = new PersonDto();
        personContainer.setThePersonDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        HL7XADType address = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getPatientAddress().get(0);
        address.setHL7Country(EdxELRConstant.ELR_USA_DESC);
        address.setHL7CountyParishCode("COUNTY");

        var stateCode = new StateCode();
        stateCode.setStateCd("ME");
        when(checkingValueService.findStateCodeByStateNm("ME")).thenReturn(stateCode);
        when(checkingValueService.getCountyCdByDesc("COUNTY","ME")).thenReturn("COUNTY");



        var res = nbsObjectConverter.personAddressType(address,EdxELRConstant.ELR_OP_CD, personContainer);
        assertNotNull(res);

    }

    @Test
    void personAddressType_Test_3() throws JAXBException, DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var personDt = new PersonDto();
        personContainer.setThePersonDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        HL7XADType address = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getPatientAddress().get(0);
        address.setHL7Country(EdxELRConstant.ELR_USA_DESC);
        address.setHL7CountyParishCode("COUNTY");

        var stateCode = new StateCode();
        stateCode.setStateCd("ME");
        when(checkingValueService.findStateCodeByStateNm("ME")).thenReturn(stateCode);
        when(checkingValueService.getCountyCdByDesc("COUNTY","ME")).thenReturn("COUNTY");



        var res = nbsObjectConverter.personAddressType(address,EdxELRConstant.ELR_NEXT_OF_KIN, personContainer);
        assertNotNull(res);

    }
    @Test
    void personAddressType_Test_4() throws JAXBException, DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        var personDt = new PersonDto();
        personContainer.setThePersonDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        HL7XADType address = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getPatientAddress().get(0);
        address.setHL7Country(EdxELRConstant.ELR_USA_DESC);
        address.setHL7CountyParishCode("COUNTY");

        var stateCode = new StateCode();
        stateCode.setStateCd("ME");
        when(checkingValueService.findStateCodeByStateNm("ME")).thenReturn(stateCode);
        when(checkingValueService.getCountyCdByDesc("COUNTY","ME")).thenReturn("COUNTY");



        var res = nbsObjectConverter.personAddressType(address,"BLAH", personContainer);
        assertNotNull(res);

    }

    @Test
    void organizationAddressType_1() throws JAXBException, DataProcessingException {
        OrganizationContainer personContainer = new OrganizationContainer();
        var personDt = new OrganizationDto();
        personContainer.setTheOrganizationDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        HL7XADType address = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getPatientAddress().get(0);
        address.setHL7Country(EdxELRConstant.ELR_USA_DESC);
        address.setHL7CountyParishCode("COUNTY");

        var stateCode = new StateCode();
        stateCode.setStateCd("ME");
        when(checkingValueService.findStateCodeByStateNm("ME")).thenReturn(stateCode);
        when(checkingValueService.getCountyCdByDesc("COUNTY","ME")).thenReturn("COUNTY");



        var res = nbsObjectConverter.organizationAddressType(address,EdxELRConstant.ELR_OP_CD, personContainer);
        assertNotNull(res);

    }

    @Test
    void organizationAddressType_Test_2() throws JAXBException {
        var personContainer = new OrganizationContainer();
        var personDt = new OrganizationDto();
        personContainer.setTheOrganizationDto(personDt);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        HL7XADType address = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getPATIENT().getPatientIdentification().getPatientAddress().get(0);

        var res = nbsObjectConverter.organizationAddressType(address,EdxELRConstant.ELR_OP_CD, personContainer);
        assertNotNull(res);
    }



    @SuppressWarnings("java:S5976")
    @Test
    void validateSSN_Test() {
        var enti = new EntityIdDto();
        enti.setRootExtensionTxt("123-45-6789");

        var res = nbsObjectConverter.validateSSN(enti);
        assertNotNull(res);

    }

    @SuppressWarnings("java:S5976")
    @Test
    void validateSSN_Test_2() {
        var enti = new EntityIdDto();
        enti.setRootExtensionTxt("12345");

        var res = nbsObjectConverter.validateSSN(enti);
        assertNotNull(res);

    }

    @SuppressWarnings("java:S5976")
    @Test
    void validateSSN_Test_3() {
        var enti = new EntityIdDto();
        enti.setRootExtensionTxt("123");

        var res = nbsObjectConverter.validateSSN(enti);
        assertNotNull(res);

    }

    @Test
    void processHL7TSTypeForDOBWithoutTime_Test() throws DataProcessingException {
        HL7TSType time = new HL7TSType();
        time.setYear(BigInteger.valueOf(2000));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(10));

        when(entityIdUtil.stringToStrutsTimestamp(any())).thenReturn(TimeStampUtil.getCurrentTimeStamp());

        var res = nbsObjectConverter.processHL7TSTypeForDOBWithoutTime(time);

        assertNotNull(res);
    }

    @Test
    void setPersonBirthType_Test() {
        String country = "";
        PersonContainer personContainer = new PersonContainer();
        var perDt = new PersonDto();
        personContainer.setThePersonDto(perDt);
        var res = nbsObjectConverter.setPersonBirthType(country, personContainer);

        assertNotNull(res);
    }
}
