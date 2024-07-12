package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.phdc.HL7OBRType;
import gov.cdc.dataprocessing.model.phdc.HL7OBXType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

@Component
public class CommonLabUtil {
    public String getXMLElementNameForOBR(HL7OBRType hl7OBRType) throws DataProcessingException {
        try {
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(hl7OBRType, writer);
            return writer.toString();

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    public String getXMLElementNameForOBX(HL7OBXType hl7OBXType) throws DataProcessingException {
        try {
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(hl7OBXType, writer);
            return writer.toString();


        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

}
