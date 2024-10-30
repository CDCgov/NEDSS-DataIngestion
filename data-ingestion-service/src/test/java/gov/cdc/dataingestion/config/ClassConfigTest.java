package gov.cdc.dataingestion.config;

import gov.cdc.dataingestion.validation.integration.validator.HL7v2Validator;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7v2Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class ClassConfigTest {
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    public void setUp() {
        context = new AnnotationConfigApplicationContext(ClassConfig.class);
    }

    @Test
    void hl7v2Validator_BeanIsDefined() {
        // Act
        IHL7v2Validator hl7v2Validator = context.getBean(IHL7v2Validator.class);

        // Assert
        Assertions.assertNotNull(hl7v2Validator);
        Assertions.assertEquals(HL7v2Validator.class, hl7v2Validator.getClass());
    }



}
