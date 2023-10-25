package gov.cdc.dataingestion.validation.integration.validator;

import gov.cdc.dataingestion.exception.DuplicateHL7FileFoundException;
import gov.cdc.dataingestion.kafka.integration.service.KafkaProducerService;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.IHL7DuplicateValidator;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Component
@Slf4j
public class HL7DuplicateValidator implements IHL7DuplicateValidator {

    private final IValidatedELRRepository iValidatedELRRepository;
    private final KafkaProducerService kafkaProducerService;
    @Value("${kafka.elr-duplicate.topic}")
    private String validatedElrDuplicateTopic = "";

    public HL7DuplicateValidator(IValidatedELRRepository iValidatedELRRepository, KafkaProducerService kafkaProducerService) {
        this.iValidatedELRRepository = iValidatedELRRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public void ValidateHL7Document(ValidatedELRModel hl7ValidatedModel) throws DuplicateHL7FileFoundException {
        String hashedString = null;
        try {
            MessageDigest digestString = MessageDigest.getInstance("SHA-256");
            byte[] encodedByteHash = digestString.digest(hl7ValidatedModel.getRawMessage().getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedByteHash) {
                String intToHexString = Integer.toHexString(0xff & b);
                if (intToHexString.length() == 1) {
                    hexString.append(0);
                }
                hexString.append(intToHexString);
            }
            hashedString = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        if (!checkForDuplicateHL7HashString(hashedString)) {
            hl7ValidatedModel.setHashedHL7String(hashedString);
        } else {
            kafkaProducerService.sendMessageAfterCheckingDuplicateHL7(hl7ValidatedModel, validatedElrDuplicateTopic, 0);
            throw new DuplicateHL7FileFoundException("HL7 document already exists in the database. " +
                    "Please check elr_duplicate table for    l'.l[];?the failed document. Record Id: " + hl7ValidatedModel.getRawId());


        }
    }

    public boolean checkForDuplicateHL7HashString(String hashedString) {
        log.debug("Generated HashString is being checked for duplicate if already present in the database");
        Optional<ValidatedELRModel> validatedELRResponseFromDatabase = iValidatedELRRepository.findByHashedHL7String(hashedString);
        if (!validatedELRResponseFromDatabase.isEmpty()) {
            if (hashedString.equals(validatedELRResponseFromDatabase.get().getHashedHL7String())) {
                log.error("Duplicate hashed string found for the HL7 message in the database. Sending details to kafka dlt topic.");
                return true;
            }
        }
        log.debug("HashString doesn't exists in the database. Moving forward to FHIR conversion.");
        return false;
    }
}