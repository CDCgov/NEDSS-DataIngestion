package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.phdc.*;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
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

    private PersonContainer perContainer;
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

        var perDt = new PersonDto();
        perContainer = new PersonContainer();
        perContainer.setThePersonDto(perDt);
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
    void processHL7TSTypeForDOBWithoutTime_Test_Exp()  {
        HL7TSType time = new HL7TSType();
        time.setYear(BigInteger.valueOf(2000));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(10));

        when(entityIdUtil.stringToStrutsTimestamp(any())).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            nbsObjectConverter.processHL7TSTypeForDOBWithoutTime(time);
        });

        assertNotNull(thrown);
    }

    @Test
    void processHL7TSTypeForDOBWithoutTime_Exp_2() {
        HL7TSType time = new HL7TSType();
        time.setYear(BigInteger.valueOf(2000));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(10));

        when(entityIdUtil.stringToStrutsTimestamp(any())).thenReturn(TimeStampUtil.getCurrentTimeStamp());
        when(entityIdUtil.isDateNotOkForDatabase(any())).thenReturn(true);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            nbsObjectConverter.processHL7TSTypeForDOBWithoutTime(time);
        });
        assertNotNull(thrown);
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

    @Test
    void ethnicGroupType_Test() {
        HL7CWEType hl7CWEType = new HL7CWEType();
        perContainer.getThePersonDto().setPersonUid(10L);

        hl7CWEType.setHL7Identifier("TEST");
        hl7CWEType.setHL7Text("TEST");

        var res = nbsObjectConverter.ethnicGroupType(hl7CWEType, perContainer);

        assertNotNull(res);


    }


    @Test
    void processHL7TSType_Test() throws DataProcessingException {
        HL7TSType time = new HL7TSType();
        String itemDescription = "TEST";

        time.setYear(BigInteger.valueOf(2022));
        time.setMonth(BigInteger.valueOf(12));
        time.setHours(BigInteger.valueOf(10));
        time.setDay(BigInteger.valueOf(10));
        time.setMinutes(BigInteger.valueOf(10));
        time.setSeconds(BigInteger.valueOf(10));

        var res = nbsObjectConverter.processHL7TSType(time, itemDescription);
        assertNotNull(res);

    }

    @Test
    void processHL7TSType_Test_Exp_1()  {
        HL7TSType time = new HL7TSType();
        String itemDescription = "TEST";

        time.setYear(BigInteger.valueOf(2022));
        time.setMonth(BigInteger.valueOf(12));
        time.setHours(BigInteger.valueOf(10));
        time.setDay(BigInteger.valueOf(10));
        time.setMinutes(BigInteger.valueOf(10));
        time.setSeconds(BigInteger.valueOf(10));

        when(entityIdUtil.isDateNotOkForDatabase(any())).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            nbsObjectConverter.processHL7TSType(time, itemDescription);
        });
        assertNotNull(thrown);

    }


    @Test
    void processHL7TSType_Test_Exp_2()  {
        HL7TSType time = new HL7TSType();
        String itemDescription = "TEST";

        time.setYear(BigInteger.valueOf(2022));
        time.setMonth(BigInteger.valueOf(12));
        time.setHours(BigInteger.valueOf(10));
        time.setDay(BigInteger.valueOf(10));
        time.setMinutes(BigInteger.valueOf(10));
        time.setSeconds(BigInteger.valueOf(10));

        when(entityIdUtil.isDateNotOkForDatabase(any())).thenReturn(true);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            nbsObjectConverter.processHL7TSType(time, itemDescription);
        });
        assertNotNull(thrown);

    }


    @Test
    void personTelePhoneType_Test() {
        // Mocking dependencies
        HL7XTNType hl7XTNType = Mockito.mock(HL7XTNType.class);
        PersonContainer personContainer = Mockito.mock(PersonContainer.class);
        PersonDto personDTO = Mockito.mock(PersonDto.class);

        // Setting up test data
        String role = "someRole";
        Long addUserId = 123L;
        Long personUid = 456L;

        // Setting up personContainer mock
        when(personContainer.getThePersonDto()).thenReturn(personDTO);
        when(personDTO.getAddUserId()).thenReturn(addUserId);
        when(personDTO.getPersonUid()).thenReturn(personUid);
        when(personDTO.getLastChgTime()).thenReturn(TimeStampUtil.getCurrentTimeStamp());
        when(personContainer.getTheEntityLocatorParticipationDtoCollection()).thenReturn(new ArrayList<>());

        when(hl7XTNType.getHL7EmailAddress()).thenReturn("test@mail.com");

        var cityCode = new HL7NMType();
        cityCode.setHL7Numeric(BigInteger.valueOf(1000));
        when(hl7XTNType.getHL7AreaCityCode()).thenReturn(cityCode);
        when(hl7XTNType.getHL7LocalNumber()).thenReturn(cityCode);
        when(hl7XTNType.getHL7Extension()).thenReturn(cityCode);
        when(hl7XTNType.getHL7AnyText()).thenReturn("TEST");


        // Invoking the method to be tested
        var result = nbsObjectConverter.personTelePhoneType(hl7XTNType, role, personContainer);

        // Asserting the results
        assertNotNull(result);
        assertEquals(addUserId, result.getAddUserId());
        assertEquals(personUid, result.getEntityUid());
    }

    @Test
    void raceType_Test() {
        perContainer.getThePersonDto().setAddUserId(12L);
        perContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7CWEType hl7CWEType = Mockito.mock(HL7CWEType.class);
        when(hl7CWEType.getHL7Identifier()).thenReturn("TEST");
        when(hl7CWEType.getHL7Text()).thenReturn("TEST");
        var result = nbsObjectConverter.raceType(hl7CWEType, perContainer);
        assertNotNull(result);


    }

    @Test
    void raceType_Test_2() {
        perContainer.getThePersonDto().setAddUserId(12L);
        perContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7CWEType hl7CWEType = Mockito.mock(HL7CWEType.class);
        when(hl7CWEType.getHL7Identifier()).thenReturn(null);
        when(hl7CWEType.getHL7AlternateIdentifier()).thenReturn("TEST");
        when(hl7CWEType.getHL7AlternateText()).thenReturn("TEST");
        var result = nbsObjectConverter.raceType(hl7CWEType, perContainer);
        assertNotNull(result);


    }

    @Test
    void checkIfNumberMoreThan10Digits_Test() {
        var areaAndNumber = new ArrayList<String>();
        HL7NMType hl7NMType = Mockito.mock(HL7NMType.class);
        when(hl7NMType.getHL7Numeric()).thenReturn(BigInteger.valueOf(100000000000L));
        var result = nbsObjectConverter.checkIfNumberMoreThan10Digits(areaAndNumber, hl7NMType);
        assertTrue(result);

    }

    @Test
    void formatPhoneNbr_Test() {
        String phoneNbrTxt = "123456789";
        var result = nbsObjectConverter.formatPhoneNbr(phoneNbrTxt);
        assertNotNull(result);


    }

    @Test
    void formatPhoneNbr_Test_LenLessThanSeven() {
        String phoneNbrTxt = "123456";
        var result = nbsObjectConverter.formatPhoneNbr(phoneNbrTxt);
        assertNotNull(result);


    }

    @Test
    void defaultParticipationDT_Test() {
        var part = new ParticipationDto();
        var edx = new EdxLabInformationDto();
        edx.setAddTime(TimeStampUtil.getCurrentTimeStamp());

        var result = nbsObjectConverter.defaultParticipationDT(part, edx);
        assertNotNull(result);

    }

    @Test
    void orgTelePhoneType_Test() {
        HL7XTNType hl7XTNType = Mockito.mock(HL7XTNType.class);
        when(hl7XTNType.getHL7EmailAddress()).thenReturn("test@mail.com");
        var cityCode = new HL7NMType();
        cityCode.setHL7Numeric(BigInteger.valueOf(1000));
        when(hl7XTNType.getHL7AreaCityCode()).thenReturn(cityCode);
        when(hl7XTNType.getHL7LocalNumber()).thenReturn(cityCode);
        when(hl7XTNType.getHL7Extension()).thenReturn(cityCode);
        when(hl7XTNType.getHL7AnyText()).thenReturn("TEST");

        String role = "TEST";
        OrganizationContainer organizationContainer = new OrganizationContainer();
        organizationContainer.setTheOrganizationDto(new OrganizationDto());


        var result = nbsObjectConverter.orgTelePhoneType(hl7XTNType, role, organizationContainer);
        assertNotNull(result);

    }

    @Test
    void processCNNPersonName_Test() {
        HL7CNNType hl7CNNType =  Mockito.mock(HL7CNNType.class);
        when(hl7CNNType.getHL7FamilyName()).thenReturn("TEST");
        when(hl7CNNType.getHL7GivenName()).thenReturn("TEST");
        when(hl7CNNType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof()).thenReturn("TEST");
        when(hl7CNNType.getHL7Suffix()).thenReturn("TEST");
        when(hl7CNNType.getHL7Prefix()).thenReturn("TEST");
        when(hl7CNNType.getHL7Degree()).thenReturn("TEST");


        var perNamCol = new ArrayList<PersonNameDto>();
        var perNam = new PersonNameDto();
        perNamCol.add(perNam);
        perContainer.setThePersonNameDtoCollection(perNamCol);

        var result = nbsObjectConverter.processCNNPersonName(hl7CNNType, perContainer);
        assertNotNull(result);
    }

    @Test
    void processHL7TSTypeWithMillis_Test() throws DataProcessingException {
        HL7TSType time = new HL7TSType();
        String itemDescription = "TEST";

        time.setYear(BigInteger.valueOf(2022));
        time.setMonth(BigInteger.valueOf(12));
        time.setHours(BigInteger.valueOf(10));
        time.setDay(BigInteger.valueOf(10));
        time.setMinutes(BigInteger.valueOf(10));
        time.setSeconds(BigInteger.valueOf(10));
        time.setMillis(BigInteger.valueOf(10));


        var res = nbsObjectConverter.processHL7TSTypeWithMillis(time, itemDescription);
        assertNotNull(res);

    }

    @Test
    void processHL7TSTypeWithMillis_Exp_1() {
        HL7TSType time = new HL7TSType();
        String itemDescription = "TEST";

        time.setYear(BigInteger.valueOf(2022));
        time.setMonth(BigInteger.valueOf(12));
        time.setHours(BigInteger.valueOf(10));
        time.setDay(BigInteger.valueOf(10));
        time.setMinutes(BigInteger.valueOf(10));
        time.setSeconds(BigInteger.valueOf(10));
        time.setMillis(BigInteger.valueOf(10));

        when(entityIdUtil.isDateNotOkForDatabase(any())).thenReturn(true);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            nbsObjectConverter.processHL7TSTypeWithMillis(time, itemDescription);
        });

        assertNotNull(thrown);

    }


    @Test
    void processHL7TSTypeWithMillis_Exp_2() {
        HL7TSType time = new HL7TSType();
        String itemDescription = "TEST";

        time.setYear(BigInteger.valueOf(2022));
        time.setMonth(BigInteger.valueOf(12));
        time.setHours(BigInteger.valueOf(10));
        time.setDay(BigInteger.valueOf(10));
        time.setMinutes(BigInteger.valueOf(10));
        time.setSeconds(BigInteger.valueOf(10));
        time.setMillis(BigInteger.valueOf(10));

        when(entityIdUtil.isDateNotOkForDatabase(any())).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            nbsObjectConverter.processHL7TSTypeWithMillis(time, itemDescription);
        });

        assertNotNull(thrown);

    }
}
