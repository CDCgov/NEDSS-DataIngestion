package gov.cdc.dataprocessing.utilities.data_extraction;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.phdc.HL7OBRType;
import gov.cdc.dataprocessing.model.phdc.HL7OBXType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.io.ByteArrayOutputStream;

public class CommonLabUtil {
    public static String getXMLElementNameForOBR(HL7OBRType hl7OBRType) throws DataProcessingException {
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

    public static String getXMLElementNameForOBX(HL7OBXType hl7OBXType) throws DataProcessingException {
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
