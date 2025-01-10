package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.phdc.HL7MSHType;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestDataReader;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class LabResultUtilTest {
    @InjectMocks
    private LabResultUtil labResultUtil;

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
        Mockito.reset(authUtil);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void getLabResultMessage_Test() throws JAXBException {
        HL7MSHType hl7MSHType;
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();

        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);
        hl7MSHType = xmlConn.getHL7LabReport().getHL7MSH();
        edxLabInformationDto.setNextUid(10);
        edxLabInformationDto.setDangerCode("TEST");
        edxLabInformationDto.setUniversalIdType("TEST");
        edxLabInformationDto.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        edxLabInformationDto.setFillerNumber("TEST");
        edxLabInformationDto.setSendingFacilityClia("TEST");
        edxLabInformationDto.setSendingFacilityName("TEST");
        edxLabInformationDto.setPatientUid(10L);
        edxLabInformationDto.setRootObserbationUid(10L);

        labResultUtil.getLabResultMessage(hl7MSHType, edxLabInformationDto);


    }
}
