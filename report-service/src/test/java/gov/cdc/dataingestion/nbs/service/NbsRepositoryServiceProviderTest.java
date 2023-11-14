package gov.cdc.dataingestion.nbs.service;

import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
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
    void saveToNbsTest() {
        String id = "whatever";
        String xmlMsg =  testXmlData;
        HL7ParsedMessage parsedMessage = new HL7ParsedMessage();
        when(nbsInterfaceRepo.save(any(NbsInterfaceModel.class))).thenReturn(new NbsInterfaceModel());

        var saved = target.saveXmlMessage(id, xmlMsg, parsedMessage);
        Assertions.assertTrue(saved instanceof NbsInterfaceModel);
    }
}
