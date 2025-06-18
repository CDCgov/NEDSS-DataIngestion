package gov.cdc.dataprocessing.service.implementation.data_extraction;

import gov.cdc.dataprocessing.model.phdc.Container;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service

public class DataExtractionServiceUtility {
    private static final JAXBContext ELR_CONTEXT;

    static {
        try {
            ELR_CONTEXT = JAXBContext.newInstance(Container.class);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError("Failed to initialize JAXBContext for Container: " + e.getMessage());
        }
    }

    public Container parsingElrXmlPayload(String xmlPayload) throws JAXBException {
        Unmarshaller unmarshaller = ELR_CONTEXT.createUnmarshaller();
        try (StringReader reader = new StringReader(xmlPayload)) {
            return (Container) unmarshaller.unmarshal(reader);
        }
    }
}
