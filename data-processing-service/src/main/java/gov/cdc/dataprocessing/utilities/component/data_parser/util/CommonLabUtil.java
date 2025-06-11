package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.phdc.HL7OBRType;
import gov.cdc.dataprocessing.model.phdc.HL7OBXType;
import org.springframework.stereotype.Component;

@Component

public class CommonLabUtil {
    private final XmlMapper xmlMapper = new XmlMapper();
    public String getXMLElementNameForOBR(HL7OBRType hl7OBRType) throws DataProcessingException {
        try {
            return xmlMapper.writeValueAsString(hl7OBRType);

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public String getXMLElementNameForOBX(HL7OBXType hl7OBXType) throws DataProcessingException {
        try {
            return xmlMapper.writeValueAsString(hl7OBXType);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

}
