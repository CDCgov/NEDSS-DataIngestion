package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.phdc.HL7CXType;
import gov.cdc.dataprocessing.model.phdc.HL7DTType;
import gov.cdc.dataprocessing.model.phdc.HL7HDType;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EntityIdUtilTest {
    @Mock
    AuthUtil authUtil;
    @Mock
    private ICatchingValueService catchingValueService;
    @InjectMocks
    private EntityIdUtil entityIdUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        AuthUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtil, catchingValueService);
    }

    @Test
    void processEntityData_Test() throws DataProcessingException {
        HL7CXType hl7CXType = new HL7CXType();
        PersonContainer personContainer = new PersonContainer();
        String indicator = EdxELRConstant.ELR_PATIENT_ALTERNATE_IND;
        int index = 1;

        personContainer.getThePersonDto().setPersonUid(10L);
        personContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7HDType assignAuth = new HL7HDType();
        assignAuth.setHL7UniversalID("TEST");
        assignAuth.setHL7NamespaceID("TEST");
        assignAuth.setHL7UniversalIDType("TEST");

        hl7CXType.setHL7AssigningAuthority(assignAuth);
        hl7CXType.setHL7IdentifierTypeCode("TEST");

        var time = new HL7DTType();
        time.setYear(BigInteger.valueOf(2020));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(30));
        hl7CXType.setHL7EffectiveDate(time);
        hl7CXType.setHL7ExpirationDate(time);

        var res = entityIdUtil.processEntityData(hl7CXType, personContainer, indicator, index);
        assertNotNull(res);
        assertEquals("TEST", res.getAssigningAuthorityCd());
    }

    @Test
    void processEntityData_Test_2() throws DataProcessingException {
        HL7CXType hl7CXType = new HL7CXType();
        PersonContainer personContainer = new PersonContainer();
        String indicator = EdxELRConstant.ELR_MOTHER_IDENTIFIER;
        int index = 1;

        personContainer.getThePersonDto().setPersonUid(10L);
        personContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7HDType assignAuth = new HL7HDType();
        assignAuth.setHL7UniversalID("TEST");
        assignAuth.setHL7NamespaceID("TEST");
        assignAuth.setHL7UniversalIDType("TEST");

        hl7CXType.setHL7AssigningAuthority(assignAuth);
        hl7CXType.setHL7IdentifierTypeCode("TEST");

        var time = new HL7DTType();
        time.setYear(BigInteger.valueOf(2020));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(30));
        hl7CXType.setHL7EffectiveDate(time);
        hl7CXType.setHL7ExpirationDate(time);

        var res = entityIdUtil.processEntityData(hl7CXType, personContainer, indicator, index);
        assertNotNull(res);
        assertEquals("TEST", res.getAssigningAuthorityCd());
    }

    @Test
    void processEntityData_Test_3() throws DataProcessingException {
        HL7CXType hl7CXType = new HL7CXType();
        PersonContainer personContainer = new PersonContainer();
        String indicator = EdxELRConstant.ELR_ACCOUNT_IDENTIFIER;
        int index = 1;

        personContainer.getThePersonDto().setPersonUid(10L);
        personContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7HDType assignAuth = new HL7HDType();
        assignAuth.setHL7UniversalID("TEST");
        assignAuth.setHL7NamespaceID("TEST");
        assignAuth.setHL7UniversalIDType("TEST");

        hl7CXType.setHL7AssigningAuthority(assignAuth);
        hl7CXType.setHL7IdentifierTypeCode("TEST");

        var time = new HL7DTType();
        time.setYear(BigInteger.valueOf(2020));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(30));
        hl7CXType.setHL7EffectiveDate(time);
        hl7CXType.setHL7ExpirationDate(time);

        var res = entityIdUtil.processEntityData(hl7CXType, personContainer, indicator, index);
        assertNotNull(res);
        assertEquals("TEST", res.getAssigningAuthorityCd());
    }

    @Test
    void processEntityData_Test_4() throws DataProcessingException {
        HL7CXType hl7CXType = new HL7CXType();
        PersonContainer personContainer = new PersonContainer();
        String indicator = "BLAH";
        int index = 1;

        personContainer.getThePersonDto().setPersonUid(10L);
        personContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7HDType assignAuth = new HL7HDType();
        assignAuth.setHL7UniversalID("TEST");
        assignAuth.setHL7NamespaceID("TEST");
        assignAuth.setHL7UniversalIDType("TEST");

        hl7CXType.setHL7AssigningAuthority(assignAuth);
        hl7CXType.setHL7IdentifierTypeCode("TEST");

        var time = new HL7DTType();
        time.setYear(BigInteger.valueOf(2020));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(30));
        hl7CXType.setHL7EffectiveDate(time);
        hl7CXType.setHL7ExpirationDate(time);

        var res = entityIdUtil.processEntityData(hl7CXType, personContainer, indicator, index);
        assertNotNull(res);
        assertEquals("TEST", res.getAssigningAuthorityCd());
    }

    @Test
    void processEntityData_Test_5() throws DataProcessingException {
        HL7CXType hl7CXType = new HL7CXType();
        PersonContainer personContainer = new PersonContainer();
        String indicator = "BLAH";
        int index = 1;

        personContainer.getThePersonDto().setPersonUid(10L);
        personContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7HDType assignAuth = new HL7HDType();
        assignAuth.setHL7UniversalID("TEST");
        assignAuth.setHL7NamespaceID("TEST");
        assignAuth.setHL7UniversalIDType("TEST");

        hl7CXType.setHL7AssigningAuthority(assignAuth);
        hl7CXType.setHL7IdentifierTypeCode(null);

        var time = new HL7DTType();
        time.setYear(BigInteger.valueOf(2020));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(30));
        hl7CXType.setHL7EffectiveDate(time);
        hl7CXType.setHL7ExpirationDate(time);

        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("CODE");


        var res = entityIdUtil.processEntityData(hl7CXType, personContainer, indicator, index);
        assertNotNull(res);
        assertEquals("TEST", res.getAssigningAuthorityCd());
    }

    @Test
    void processEntityData_Test_6() throws DataProcessingException {
        HL7CXType hl7CXType = new HL7CXType();
        PersonContainer personContainer = new PersonContainer();
        String indicator = "BLAH";
        int index = 1;

        personContainer.getThePersonDto().setPersonUid(10L);
        personContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7HDType assignAuth = new HL7HDType();
        assignAuth.setHL7UniversalID("TEST");
        assignAuth.setHL7NamespaceID("TEST");
        assignAuth.setHL7UniversalIDType("TEST");

        hl7CXType.setHL7AssigningAuthority(assignAuth);
        hl7CXType.setHL7IdentifierTypeCode(null);

        var time = new HL7DTType();
        time.setYear(BigInteger.valueOf(2020));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(30));
        hl7CXType.setHL7EffectiveDate(time);
        hl7CXType.setHL7ExpirationDate(time);

        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("");


        var res = entityIdUtil.processEntityData(hl7CXType, personContainer, indicator, index);
        assertNotNull(res);
        assertEquals("TEST", res.getAssigningAuthorityCd());
    }


    @Test
    void processEntityData_Test_Exp_1() {
        HL7CXType hl7CXType = new HL7CXType();
        PersonContainer personContainer = new PersonContainer();
        String indicator = EdxELRConstant.ELR_PATIENT_ALTERNATE_IND;
        int index = 1;

        personContainer.getThePersonDto().setPersonUid(10L);
        personContainer.getThePersonDto().setAddTime(TimeStampUtil.getCurrentTimeStamp());

        HL7HDType assignAuth = new HL7HDType();
        assignAuth.setHL7UniversalID("TEST");
        assignAuth.setHL7NamespaceID("TEST");
        assignAuth.setHL7UniversalIDType("TEST");

        hl7CXType.setHL7AssigningAuthority(assignAuth);
        hl7CXType.setHL7IdentifierTypeCode("TEST");

        var time = new HL7DTType();
        time.setYear(BigInteger.valueOf(20200));
        time.setMonth(BigInteger.valueOf(12));
        time.setDay(BigInteger.valueOf(30));
        hl7CXType.setHL7EffectiveDate(time);
        hl7CXType.setHL7ExpirationDate(time);


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            entityIdUtil.processEntityData(hl7CXType, personContainer, indicator, index);
        });
        assertNotNull(thrown);

    }

    @Test
    void stringToStrutsTimestamp_Test_1() {
        String time = null;

        var res = entityIdUtil.stringToStrutsTimestamp(time);
        assertNull(res);
    }

    @Test
    void stringToStrutsTimestamp_Test_2() {
        String time = "NULL";

        var res = entityIdUtil.stringToStrutsTimestamp(time);
        assertNull(res);
    }

    @Test
    void isDateNotOkForDatabase_Test_1() {
        Timestamp timestamp = null;
        var res = entityIdUtil.isDateNotOkForDatabase(timestamp);

        assertFalse(res);
    }

    @Test
    void isDateNotOkForDatabase_Test_2() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("1700-01-01");
        Timestamp timestamp = new Timestamp(date.getTime());

        // Act
        boolean result = entityIdUtil.isDateNotOkForDatabase(timestamp);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsDateNotOkForDatabase_ExceptionHandling() throws Exception {
        // Arrange
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat mockDateFormat = spy(new SimpleDateFormat("yyyy-MM-dd"));
        doThrow(new RuntimeException("Test Exception")).when(mockDateFormat).parse(anyString());


        // Act
        boolean result = entityIdUtil.isDateNotOkForDatabase(timestamp);

        // Assert
        assertFalse(result);
    }
}
