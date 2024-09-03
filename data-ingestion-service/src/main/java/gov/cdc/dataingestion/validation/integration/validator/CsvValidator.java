package gov.cdc.dataingestion.validation.integration.validator;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import gov.cdc.dataingestion.constant.enums.EnumMessageType;
import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.ICsvValidator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
public class CsvValidator implements ICsvValidator {
    String schemaPath = "./csv-schema/Patients.csv";
    Gson gson;

    public CsvValidator() {
        gson = new Gson();
    }

    public ValidatedELRModel validateCSVAgainstCVSSchema(String message) throws IOException, CsvValidationException, DiHL7Exception {
        String[] header;
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(schemaPath));
            header = reader.readNext();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        if (header == null) {
            throw new DiHL7Exception("Schema definition is missing or invalid");
        }

        int headerLength = header.length;
        List<List<String>> kafkaMsg = gson.fromJson(message, List.class);

        for (var item : kafkaMsg) {
            if (item.size() != headerLength) {
                throw new DiHL7Exception("Invalid record, one or more records do not match the schema definition");
            }
        }

        ValidatedELRModel model = new ValidatedELRModel();
        model.setRawMessage(message);
        model.setMessageType(EnumMessageType.CSV.name());
        model.setMessageVersion("NA");

        return model;
    }

}