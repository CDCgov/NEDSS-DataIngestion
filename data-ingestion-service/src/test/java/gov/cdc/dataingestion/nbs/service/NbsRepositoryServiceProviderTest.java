package gov.cdc.dataingestion.nbs.service;


import gov.cdc.dataingestion.exception.XmlConversionException;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.OrderObservation;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.PatientResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_group.Specimen;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import gov.cdc.dataingestion.nbs.TestHelper;
import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.nbs.services.NbsRepositoryServiceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class NbsRepositoryServiceProviderTest {
    @Mock
    private NbsInterfaceRepository nbsInterfaceRepo;

    @InjectMocks
    private NbsRepositoryServiceProvider target;

    private String testXmlData;

    @BeforeEach
    public void setUpEach() throws IOException {
        MockitoAnnotations.openMocks(this);
        target = new NbsRepositoryServiceProvider(nbsInterfaceRepo);
        testXmlData = TestHelper.testFileReading();
    }

    @Test
    void saveToNbsTestNoOruFound() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTest() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("200603241455");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestNewFlow() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("200603241455");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, true);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }


    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestWithSpecificTimeZone() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("20210101000000+0000");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestWithFractionOfSecondAndPlusSpecificTimeZone() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("19800107000000.6081+0606");

        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestWithFractionOfSecondAndMinusSpecificTimeZone() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("19800107000000.6081-0606");

        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestWithFractionOfSecondThreeAndPlusSpecificTimeZone() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("19800107000000.608+0606");

        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestWithFractionOfSecondThreeAndMinusSpecificTimeZone() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("19800107000000.608-0606");

        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
    @Test
    void saveToNbsTestWithFractionOfSecondFiveThrowsException() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("19800107000000.60888-0606");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());


        Assertions.assertThrows(
                XmlConversionException.class, () -> {
                    target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
                }
        );
    }
    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestDateHour() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("1980010710");

        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestDateHourMinsSec() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("19800107101112");

        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
    @SuppressWarnings({"java:S5976"})
    @Test
    void saveToNbsTestWithSpecificTimeZone2() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("20210101000000-0000");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestUniversalMsgNull() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.setMessageHeader(null);
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("200603241455");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestUniversalMsgFacilityNull() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().setSendingFacility(null);
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("200603241455");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestOrderNumPatientResultEmpty() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
               parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestOrderNumPatientResultNull() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.setPatientResult(null);
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestOrderNumObservationEmpty() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestOrderNumObservationNull() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).setOrderObservation(null);
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestObrNull() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).setObservationRequest(null);
     parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestObrEntityIdentifierNull() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier(null);
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTestTestCodeNull() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier(null);
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setAlternateIdentifier("TEST");

        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }


    @Test
    void saveToNbsTestDateOnly() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("20200101");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }


    @Test
    void saveToNbsTestThrowTimeParseException() throws XmlConversionException {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        OruR1 oru = new OruR1();
        oru.getMessageHeader().getSendingFacility().setUniversalId("1");
        oru.getPatientResult().add(new PatientResult());
        oru.getPatientResult().get(0).getOrderObservation().add(new OrderObservation());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getFillerOrderNumber().setEntityIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getObservationRequest().getUniversalServiceIdentifier().setIdentifier("test");
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().add(new Specimen());
        oru.getPatientResult().get(0).getOrderObservation().get(0).getSpecimen().get(0).getSpecimen().getSpecimenCollectionDateTime().getRangeStartDateTime().setTime("AAAA");
        parsedMessage.setParsedMessage(oru);
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());


        Assertions.assertThrows(
                XmlConversionException.class, () -> {
                    target.saveXmlMessage(id, xmlMsg, parsedMessage, false);
                }
        );
    }

    @Test
    void testSaveEcrCdaXmlMessage_UpdateExistingRecord() {
        // Arrange
        String nbsInterfaceUid = "1";
        Integer dataMigrationStatus = 0;
        String xmlMsg = "<xml>message</xml>";
        NbsInterfaceModel existingModel = new NbsInterfaceModel();
        existingModel.setPayload("old payload");

        when(nbsInterfaceRepo.getNbsInterfaceByIdAndDocType(anyInt(), any()))
                .thenReturn(Optional.of(existingModel));

        // Act
        target.saveEcrCdaXmlMessage(nbsInterfaceUid, dataMigrationStatus, xmlMsg);

        // Assert
        verify(nbsInterfaceRepo, times(1)).save(existingModel);
    }

    @Test
    void testSaveEcrCdaXmlMessage_2() {
        // Arrange
        String nbsInterfaceUid = "1";
        Integer dataMigrationStatus = -1;
        String xmlMsg = "<xml>message</xml>";
        NbsInterfaceModel existingModel = new NbsInterfaceModel();
        existingModel.setPayload("old payload");

        when(nbsInterfaceRepo.getNbsInterfaceByIdAndDocType(anyInt(), any()))
                .thenReturn(Optional.empty());

        // Act
        target.saveEcrCdaXmlMessage(nbsInterfaceUid, dataMigrationStatus, xmlMsg);

        // Assert
        verify(nbsInterfaceRepo, times(1)).save(any());
    }

    @SuppressWarnings("java:S4144")
    @Test
    void testSaveEcrCdaXmlMessage_3() {
        // Arrange
        String nbsInterfaceUid = "1";
        Integer dataMigrationStatus = -1;
        String xmlMsg = "<xml>message</xml>";
        NbsInterfaceModel existingModel = new NbsInterfaceModel();
        existingModel.setPayload("old payload");

        when(nbsInterfaceRepo.getNbsInterfaceByIdAndDocType(anyInt(), any()))
                .thenReturn(Optional.empty());

        // Act
        target.saveEcrCdaXmlMessage(nbsInterfaceUid, dataMigrationStatus, xmlMsg);

        // Assert
        verify(nbsInterfaceRepo, times(1)).save(any());
    }
}
