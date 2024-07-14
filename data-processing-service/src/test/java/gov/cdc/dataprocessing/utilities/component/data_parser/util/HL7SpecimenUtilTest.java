package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.phdc.HL7PatientResultSPMType;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestDataReader;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.NBSObjectConverter;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HL7SpecimenUtilTest {
    @Mock
    AuthUtil authUtil;
    @Mock
    private NBSObjectConverter nbsObjectConverter;
    @InjectMocks
    private HL7SpecimenUtil hl7SpecimenUtil;

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
        Mockito.reset(authUtil, nbsObjectConverter);
    }

    @Test
    void process251Specimen_Test() throws JAXBException, DataProcessingException {
        HL7PatientResultSPMType hL7PatientResultSPMType;
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto observationDto = new ObservationDto();
        PersonContainer collectorVO = new PersonContainer();
        collectorVO.getThePersonDto().setPersonUid(10L);
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setNextUid(10);
        edxLabInformationDto.setDangerCode("TEST");
        edxLabInformationDto.setUniversalIdType("TEST");
        edxLabInformationDto.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        edxLabInformationDto.setFillerNumber("TEST");
        edxLabInformationDto.setSendingFacilityClia("TEST");
        edxLabInformationDto.setSendingFacilityName("TEST");
        edxLabInformationDto.setPatientUid(10L);
        edxLabInformationDto.setRootObserbationUid(10L);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);
        hL7PatientResultSPMType = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getORDEROBSERVATION().get(0).getPatientResultOrderSPMObservation();


        hl7SpecimenUtil.process251Specimen(hL7PatientResultSPMType, labResultProxyContainer, observationDto, collectorVO, edxLabInformationDto);

        verify(nbsObjectConverter, times(1)).defaultParticipationDT(any(), any());
    }

    @Test
    void process251Specimen_Test_Exp_1() throws JAXBException {
        HL7PatientResultSPMType hL7PatientResultSPMType;
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto observationDto = new ObservationDto();
        PersonContainer collectorVO = new PersonContainer();
        collectorVO.getThePersonDto().setPersonUid(10L);
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setNextUid(10);
        edxLabInformationDto.setDangerCode("TEST");
        edxLabInformationDto.setUniversalIdType("TEST");
        edxLabInformationDto.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        edxLabInformationDto.setFillerNumber("TEST");
        edxLabInformationDto.setSendingFacilityClia("TEST");
        edxLabInformationDto.setSendingFacilityName("TEST");
        edxLabInformationDto.setPatientUid(10L);
        edxLabInformationDto.setRootObserbationUid(10L);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);
        hL7PatientResultSPMType = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getORDEROBSERVATION().get(0).getPatientResultOrderSPMObservation();

        when(nbsObjectConverter.defaultParticipationDT(any(), any())).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            hl7SpecimenUtil.process251Specimen(hL7PatientResultSPMType, labResultProxyContainer, observationDto, collectorVO, edxLabInformationDto);
        });

        assertNotNull(thrown);

    }

    @Test
    void process251Specimen_Test_Exp_2() throws JAXBException, DataProcessingException {
        HL7PatientResultSPMType hL7PatientResultSPMType;
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationDto observationDto = new ObservationDto();
        PersonContainer collectorVO = new PersonContainer();
        collectorVO.getThePersonDto().setPersonUid(10L);
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setNextUid(10);
        edxLabInformationDto.setDangerCode("TEST");
        edxLabInformationDto.setUniversalIdType("TEST");
        edxLabInformationDto.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        edxLabInformationDto.setFillerNumber("TEST");
        edxLabInformationDto.setSendingFacilityClia("TEST");
        edxLabInformationDto.setSendingFacilityName("TEST");
        edxLabInformationDto.setPatientUid(10L);
        edxLabInformationDto.setRootObserbationUid(10L);
        var test = new TestDataReader();
        var xmlData = test.readDataFromXmlPath("/xml_payload/payload_1.xml");
        var xmlConn = test.convertXmlStrToContainer(xmlData);
        hL7PatientResultSPMType = xmlConn.getHL7LabReport().getHL7PATIENTRESULT().get(0).getORDEROBSERVATION().get(0).getPatientResultOrderSPMObservation();

        when(nbsObjectConverter.processHL7TSTypeWithMillis(any(), any())).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            hl7SpecimenUtil.process251Specimen(hL7PatientResultSPMType, labResultProxyContainer, observationDto, collectorVO, edxLabInformationDto);
        });

        assertNotNull(thrown);

    }
}
