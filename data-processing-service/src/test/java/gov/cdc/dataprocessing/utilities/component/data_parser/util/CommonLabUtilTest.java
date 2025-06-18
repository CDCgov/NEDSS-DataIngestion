
package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.phdc.HL7OBRType;
import gov.cdc.dataprocessing.model.phdc.HL7OBXType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommonLabUtilTest {

    private XmlMapper xmlMapper;
    private CommonLabUtil commonLabUtil;

    @BeforeEach
    void setUp() {
        xmlMapper = mock(XmlMapper.class);
        commonLabUtil = new CommonLabUtil(xmlMapper);
    }

    @Test
    void testGetXMLElementNameForOBR_ThrowsException() throws JsonProcessingException {
        HL7OBRType mockOBR = new HL7OBRType();

        when(xmlMapper.writeValueAsString(mockOBR))
                .thenThrow(new JsonProcessingException("Failed to serialize OBR") {});

        DataProcessingException exception = assertThrows(DataProcessingException.class, () ->
                commonLabUtil.getXMLElementNameForOBR(mockOBR));

        assertTrue(exception.getMessage().contains("Failed to serialize OBR"));
    }

    @Test
    void testGetXMLElementNameForOBX_ThrowsException() throws JsonProcessingException {
        HL7OBXType mockOBX = new HL7OBXType();

        when(xmlMapper.writeValueAsString(mockOBX))
                .thenThrow(new JsonProcessingException("Failed to serialize OBX") {});

        DataProcessingException exception = assertThrows(DataProcessingException.class, () ->
                commonLabUtil.getXMLElementNameForOBX(mockOBX));

        assertTrue(exception.getMessage().contains("Failed to serialize OBX"));
    }

    @Test
    void testDefaultConstructor() {
        CommonLabUtil util = new CommonLabUtil();
        assertNotNull(util); // Just verifying the object is created
    }

}
