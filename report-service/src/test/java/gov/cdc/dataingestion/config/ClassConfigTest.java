package gov.cdc.dataingestion.config;
import gov.cdc.dataingestion.conversion.integration.HL7ToFHIRConversion;
import gov.cdc.dataingestion.conversion.integration.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.validation.integration.validator.CsvValidator;
import gov.cdc.dataingestion.validation.integration.validator.HL7v2Validator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.ICsvValidator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ClassConfigTest {
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    public void setUp() {
        context = new AnnotationConfigApplicationContext(ClassConfig.class);
    }

    @Test
    public void hl7v2Validator_BeanIsDefined() {
        // Act
        IHL7v2Validator hl7v2Validator = context.getBean(IHL7v2Validator.class);

        // Assert
        Assertions.assertNotNull(hl7v2Validator);
        Assertions.assertEquals(HL7v2Validator.class, hl7v2Validator.getClass());
    }

    @Test
    public void csvValidator_BeanIsDefined() {
        // Act
        ICsvValidator csvValidator = context.getBean(ICsvValidator.class);

        // Assert
        Assertions.assertNotNull(csvValidator);
        Assertions.assertEquals(CsvValidator.class, csvValidator.getClass());
    }

    @Test
    public void hl7ToFHIRConversion_BeanIsDefined() {
        // Act
        IHL7ToFHIRConversion hl7ToFHIRConversion = context.getBean(IHL7ToFHIRConversion.class);

        // Assert
        Assertions.assertNotNull(hl7ToFHIRConversion);
        Assertions.assertEquals(HL7ToFHIRConversion.class, hl7ToFHIRConversion.getClass());
    }
}
