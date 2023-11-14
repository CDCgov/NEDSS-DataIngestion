package gov.cdc.dataingestion.nbs.service;

import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.OrderObservation;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.PatientResult;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageGroup.Specimen;
import gov.cdc.dataingestion.hl7.helper.model.hl7.messageType.OruR1;
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
import org.springframework.security.core.parameters.P;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class NbsRepositoryServiceProviderTest {
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
    void saveToNbsTestNoOruFound() {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }

    @Test
    void saveToNbsTest() {
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

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
}
