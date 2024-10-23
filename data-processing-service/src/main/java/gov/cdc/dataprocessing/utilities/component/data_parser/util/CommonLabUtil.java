package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.phdc.HL7OBRType;
import gov.cdc.dataprocessing.model.phdc.HL7OBXType;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class CommonLabUtil {
    public String getXMLElementNameForOBR(HL7OBRType hl7OBRType) throws DataProcessingException {
        try {
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(hl7OBRType, writer);
            return writer.toString();

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public String getXMLElementNameForOBX(HL7OBXType hl7OBXType) throws DataProcessingException {
        try {
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(hl7OBXType, writer);
            return writer.toString();


        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

}
