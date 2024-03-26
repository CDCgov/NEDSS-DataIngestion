package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.phdc.HL7OBRType;
import gov.cdc.dataprocessing.model.phdc.HL7OBXType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class CommonLabUtil {
    public String getXMLElementNameForOBR(HL7OBRType hl7OBRType) throws DataProcessingException {
        try {
            JAXBContext contextObj = JAXBContext.newInstance(HL7OBRType.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshallerObj.marshal(hl7OBRType, baos);
            baos.flush();
            return baos.toString();

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    public String getXMLElementNameForOBX(HL7OBXType hl7OBXType) throws DataProcessingException {
        try {
            JAXBContext contextObj = JAXBContext.newInstance(HL7OBXType.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshallerObj.marshal(hl7OBXType, baos);
            baos.flush();
            return baos.toString();

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

}
