package gov.cdc.dataprocessing.service.implementation.other;

import gov.cdc.dataprocessing.model.phdc.Container;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import java.io.StringReader;

@Service
public class DataExtractionServiceUtility {
    public Container parsingElrXmlPayload(String xmlPayload) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(Container.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xmlPayload);
        Container result = (Container) unmarshaller.unmarshal(reader);
        return result;
    }
}
