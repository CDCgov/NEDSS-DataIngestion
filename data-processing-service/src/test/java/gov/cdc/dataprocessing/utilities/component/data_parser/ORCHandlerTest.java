package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.phdc.HL7ORCType;
import gov.cdc.dataprocessing.model.phdc.HL7TSType;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ORCHandlerTest {
    @Mock
    private NBSObjectConverter nbsObjectConverter;
    @InjectMocks
    private ORCHandler orcHandler;

    private PersonContainer personContainer;
    private HL7ORCType commonOrder;
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

        var perDt = new PersonDto();
        personContainer = new PersonContainer();
        personContainer.setThePersonDto(perDt);

        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);

        commonOrder = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getORDEROBSERVATION().get(0).getCommonOrder();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(nbsObjectConverter, authUtil);
    }

    @Test
    void getORCProcessing_Test() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();

        labResultProxyContainer.setThePersonContainerCollection(new ArrayList<>());
        edxLabInformationDto.setNextUid(10);
        edxLabInformationDto.setUserId(10L);
        edxLabInformationDto.setRootObserbationUid(10L);

        when(nbsObjectConverter.orgTelePhoneType(any(), any(),any())).thenReturn(new EntityLocatorParticipationDto());

        commonOrder.setOrderEffectiveDateTime(new HL7TSType());

        when(nbsObjectConverter.processHL7TSType(any(), any())).thenReturn(TimeStampUtil.getCurrentTimeStamp());


        orcHandler.getORCProcessing(commonOrder, labResultProxyContainer, edxLabInformationDto);

        verify(nbsObjectConverter, times(1)).processHL7TSType(any(), any());
    }

    @Test
    void getORCProcessing_Test_exp_1() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();

        labResultProxyContainer.setThePersonContainerCollection(new ArrayList<>());
        edxLabInformationDto.setNextUid(10);
        edxLabInformationDto.setUserId(10L);
        edxLabInformationDto.setRootObserbationUid(10L);

        when(nbsObjectConverter.orgTelePhoneType(any(), any(),any())).thenReturn(new EntityLocatorParticipationDto());

        commonOrder.setOrderEffectiveDateTime(new HL7TSType());

        when(nbsObjectConverter.processHL7TSType(any(), any())).thenThrow(new RuntimeException("TEST"));




        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            orcHandler.getORCProcessing(commonOrder, labResultProxyContainer, edxLabInformationDto);
        });

        assertNotNull(thrown);
    }

    @Test
    void getORCProcessing_Test_exp_2()  {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();

        labResultProxyContainer.setThePersonContainerCollection(new ArrayList<>());
        edxLabInformationDto.setNextUid(10);
        edxLabInformationDto.setUserId(10L);
        edxLabInformationDto.setRootObserbationUid(10L);

        when(nbsObjectConverter.orgTelePhoneType(any(), any(),any())).thenThrow(new RuntimeException("TEST"));

        commonOrder.setOrderEffectiveDateTime(new HL7TSType());



        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            orcHandler.getORCProcessing(commonOrder, labResultProxyContainer, edxLabInformationDto);
        });

        assertNotNull(thrown);
    }

    @Test
    void getORCProcessing_exp_3() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();

        labResultProxyContainer.setThePersonContainerCollection(new ArrayList<>());
        edxLabInformationDto.setNextUid(10);
        edxLabInformationDto.setUserId(10L);
        edxLabInformationDto.setRootObserbationUid(10L);

        when(nbsObjectConverter.orgTelePhoneType(any(), any(),any())).thenReturn(new EntityLocatorParticipationDto());
        when(nbsObjectConverter.personAddressType(any(), any(),any())).thenThrow(new RuntimeException("TEST"));

        commonOrder.setOrderEffectiveDateTime(new HL7TSType());

        when(nbsObjectConverter.processHL7TSType(any(), any())).thenReturn(TimeStampUtil.getCurrentTimeStamp());



        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            orcHandler.getORCProcessing(commonOrder, labResultProxyContainer, edxLabInformationDto);
        });
        assertNotNull(thrown);
    }



}
