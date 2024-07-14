package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.phdc.HL7OBRType;
import gov.cdc.dataprocessing.model.phdc.HL7OBXType;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommonLabUtilTest {


    @Mock
    AuthUtil authUtil;
    @InjectMocks
    private CommonLabUtil commonLabUtil;
    private PersonContainer personContainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        AuthUtil.setGlobalAuthUser(userInfo);

        var perDt = new PersonDto();
        personContainer = new PersonContainer();
        personContainer.setThePersonDto(perDt);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(authUtil);
    }


    @Test
    void getXMLElementNameForOBR_TEST() {

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            commonLabUtil.getXMLElementNameForOBR(null);
        });

        assertNotNull(thrown);

    }

    @Test
    void getXMLElementNameForOBX_TEST() {

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            commonLabUtil.getXMLElementNameForOBX(null);
        });

        assertNotNull(thrown);

    }

    @Test
    void getXMLElementNameForOBR_TEST_1() throws DataProcessingException {

        HL7OBRType hl7OBRType = new HL7OBRType();

        var res = commonLabUtil.getXMLElementNameForOBR(hl7OBRType);

        assertNotNull(res);
    }

    @Test
    void getXMLElementNameForOBX_TEST_2() throws DataProcessingException {

        HL7OBXType hl7OBRType = new HL7OBXType();

        var res = commonLabUtil.getXMLElementNameForOBX(hl7OBRType);


        assertNotNull(res);

    }

}
