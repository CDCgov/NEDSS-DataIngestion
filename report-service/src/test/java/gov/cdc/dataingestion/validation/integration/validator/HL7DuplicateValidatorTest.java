package gov.cdc.dataingestion.validation.integration.validator;

import gov.cdc.dataingestion.exception.DuplicateHL7FileFoundException;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HL7DuplicateValidatorTest {

    HL7DuplicateValidator hl7DuplicateValidator;
    IValidatedELRRepository iValidatedELRRepositoryMock;

    @BeforeEach
    void setUp() {
        iValidatedELRRepositoryMock = mock(IValidatedELRRepository.class);
        hl7DuplicateValidator = new HL7DuplicateValidator(iValidatedELRRepositoryMock);
    }

    @Test
    void validateHL7DocumentNoDuplicate() throws DuplicateHL7FileFoundException {
        ValidatedELRModel validatedELRModel = getValidatedELRModel();

        Mockito.when(iValidatedELRRepositoryMock.findByHashedHL7String(any()))
                .thenReturn(Optional.empty());
        boolean isHL7Duplicate = hl7DuplicateValidator.ValidateHL7Document(validatedELRModel);

        assertEquals(isHL7Duplicate, false);
        assertNotNull(validatedELRModel.getHashedHL7String());
        verify(iValidatedELRRepositoryMock).findByHashedHL7String(any());
    }

    @Test
    void validateHL7DocumentDuplicate() {
        String hashedString = "843588fcbfbdca29f9807f81455bbd3ae6935dae6152bcac6851e9568c885c66";
        ValidatedELRModel validatedELRModel = getValidatedELRModel();

        Mockito.when(iValidatedELRRepositoryMock.findByHashedHL7String(hashedString))
                .thenReturn(Optional.of(validatedELRModel));
        validatedELRModel.setHashedHL7String(hashedString);

        assertThrows(DuplicateHL7FileFoundException.class, () -> hl7DuplicateValidator.ValidateHL7Document(validatedELRModel));
        verify(iValidatedELRRepositoryMock).findByHashedHL7String(hashedString);
    }

    @Test
    void checkDuplicateHL7Exists() {
        String hashedString = "843588fcbfbdca29f9807f81455bbd3ae6935dae6152bcac6851e9568c885c66";
        ValidatedELRModel validatedELRModel = getValidatedELRModel();
        validatedELRModel.setHashedHL7String(hashedString);

        Mockito.when(iValidatedELRRepositoryMock.findByHashedHL7String(hashedString))
                .thenReturn(Optional.of(validatedELRModel));
        boolean result = hl7DuplicateValidator.checkForDuplicateHL7HashString(hashedString);

        assertTrue(result);
        verify(iValidatedELRRepositoryMock).findByHashedHL7String(hashedString);
    }

    @Test
    void checkDuplicateHL7NotExists() {
        Mockito.when(iValidatedELRRepositoryMock.findByHashedHL7String(any()))
                .thenReturn(Optional.empty());
        boolean result = hl7DuplicateValidator.checkForDuplicateHL7HashString(any());

        assertFalse(result);
        verify(iValidatedELRRepositoryMock).findByHashedHL7String(any());
    }

    @NotNull
    private static ValidatedELRModel getValidatedELRModel() {
        String data = "MSH|^~\\&|ULTRA|TML|OLIS|OLIS|200905011130||ORU^R01|20169838-v25|T|2.5\r"
                + "PID|||7005728^^^TML^MR||TEST^RACHEL^DIAMOND||19310313|F|||200 ANYWHERE ST^^TORONTO^ON^M6G 2T9||(416)888-8888||||||1014071185^KR\r"
                + "PV1|1||OLIS||||OLIST^BLAKE^DONALD^THOR^^^^^921379^^^^OLIST\r"
                + "ORC|RE||T09-100442-RET-0^^OLIS_Site_ID^ISO|||||||||OLIST^BLAKE^DONALD^THOR^^^^L^921379\r"
                + "OBR|0||T09-100442-RET-0^^OLIS_Site_ID^ISO|RET^RETICULOCYTE COUNT^HL79901 literal|||200905011106|||||||200905011106||OLIST^BLAKE^DONALD^THOR^^^^L^921379||7870279|7870279|T09-100442|MOHLTC|200905011130||B7|F||1^^^200905011106^^R\r"
                + "OBX|1|ST|||TestValue";

        ValidatedELRModel validatedELRModel = new ValidatedELRModel();
        validatedELRModel.setRawId("test_uuid");
        validatedELRModel.setRawMessage(data);
        validatedELRModel.setMessageType("HL7");
        return validatedELRModel;
    }
}