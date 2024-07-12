package gov.cdc.dataingestion.validation.integration.validator;

import gov.cdc.dataingestion.validation.integration.validator.interfaces.ICsvValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CsvValidatorTest {
    private ICsvValidator target;

    @BeforeEach
    public void setUp() {
        target = new CsvValidator();
    }

    @Test
    void ValidateCSVAgainstCVSSchema_ValidCSV() throws Exception {
        // Arrange
        String message = "[[\"value1\", \"value2\", \"value3\", \"value4\", \"value5\", \"value6\", \"value7\", \"value8\"]]";

        // Act
        var result = target.validateCSVAgainstCVSSchema(message);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals("CSV", result.getMessageType());
        Assertions.assertEquals("NA", result.getMessageVersion());
    }

    @Test
    void ValidateCSVAgainstCVSSchema_InValidCSV() {
        // Arrange
        String message = "[[\"value1\", \"value2\", \"value3\", \"value4\", \"value5\", \"value8\"]]";

        // Act
        Exception exception = Assertions.assertThrows(
                Exception.class, () -> {
                    target.validateCSVAgainstCVSSchema(message);
                }
        );

        // Assert
        String expectedMessage = "Invalid record, one or more record does not match with schema definition";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }
}
