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
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class NbsRepositoryServiceProviderTest {
    @Mock
    private NbsInterfaceRepository nbsInterfaceRepo;

    @InjectMocks
    private NbsRepositoryServiceProvider target;

    private String testXmlData;

    @BeforeEach
    void setUpEach() throws IOException {
        MockitoAnnotations.openMocks(this);
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
    void saveToNbsTestWithFractionOfSecondFiveThrowsException()  {
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
    void saveToNbsTestThrowTimeParseException() {
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
    void testSaveElrXmlMessage() throws Exception {
        String messageId = "12345";
        String xmlMsg =  testXmlData;
        boolean dataProcessingApplied = true;
        NbsInterfaceModel savedItem = new NbsInterfaceModel();
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(savedItem);

        NbsInterfaceModel result = target.saveElrXmlMessage(messageId, xmlMsg, dataProcessingApplied);

        assertNotNull(result);
        assertTrue(result instanceof NbsInterfaceModel);
        verify(nbsInterfaceRepo, times(1)).save(any(NbsInterfaceModel.class));
    }

    @Test
    void testSaveElrXmlMessage_InvalidXml_ThrowsException() {
        String messageId = "12345";
        String invalidXml = "<Container><InvalidXml></Container>";
        boolean dataProcessingApplied = true;

        assertThrows(XmlConversionException.class, () -> {
            target.saveElrXmlMessage(messageId, invalidXml, dataProcessingApplied);
        });
    }

    @Test
    void testSaveElrXmlMessage_NullOrderCode() throws XmlConversionException {
        String messageId = "12345";
        String xmlMsg = "<Container></Container>";
        boolean dataProcessingApplied = true;
        NbsInterfaceModel savedItem = new NbsInterfaceModel();
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(savedItem);

        NbsInterfaceModel result = target.saveElrXmlMessage(messageId, xmlMsg, dataProcessingApplied);

        assertNotNull(result);
        assertNull(result.getOrderTestCode());
    }

    @Test
    void testSavingElrXmlNbsInterfaceModelHelper_ValidXml() throws Exception {
        String xmlMsg =  testXmlData;
        NbsInterfaceModel item = new NbsInterfaceModel();

        NbsInterfaceModel result = target.savingElrXmlNbsInterfaceModelHelper(xmlMsg, item);

        assertNotNull(result);
        assertEquals("123", result.getLabClia());
    }

    @Test
    void testGetSpecimenCollectionDateStr_ValidDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String xmlMsg = "<Container><HL7LabReport><HL7PATIENT_RESULT><ORDER_OBSERVATION>" +
                "<PatientResultOrderSPMObservation><SPECIMEN><SPECIMEN>" +
                "<SpecimenCollectionDateTime><HL7RangeStartDateTime>" +
                "<year>2024</year><month>11</month><day>18</day>" +
                "<hours>08</hours><minutes>30</minutes>" +
                "</HL7RangeStartDateTime></SpecimenCollectionDateTime>" +
                "</SPECIMEN></SPECIMEN></PatientResultOrderSPMObservation></ORDER_OBSERVATION></HL7PATIENT_RESULT></HL7LabReport></Container>";
        Document doc = builder.parse(new InputSource(new StringReader(xmlMsg)));

        String result = target.getSpecimenCollectionDateStr(doc);

        assertEquals("202411180830", result);
    }

    @Test
    void testGetPaddedNodeValue_SingleDigit() throws Exception {
        String xmlMsg = "<Container><year>9</year></Container>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlMsg)));

        String result = target.getPaddedNodeValue(doc, "/Container/year");

        assertEquals("09", result);
    }

    @Test
    void testGetPaddedNodeValue_NoValue() throws Exception {
        String xmlMsg = "<Container></Container>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlMsg)));

        String result = target.getPaddedNodeValue(doc, "/Container/year");

        assertNull(result);
    }

    @Test
    void testGetNodeValue_ValidNode() throws Exception {
        String xmlMsg =  testXmlData;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlMsg)));

        String result = target.getNodeValue(doc, "/Container/HL7LabReport/HL7MSH/SendingFacility/HL7UniversalID");

        assertEquals("123", result);
    }

    @Test
    void testGetNodeValue_InvalidNode() throws Exception {
        String xmlMsg = "<Container></Container>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlMsg)));

        String result = target.getNodeValue(doc, "/Container/HL7UniversalID");

        assertNull(result);
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
