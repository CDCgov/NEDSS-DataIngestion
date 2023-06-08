package gov.cdc.dataingestion.validation.integration.validator;
import com.google.gson.Gson;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import gov.cdc.dataingestion.validation.integration.validator.interfaces.ICsvValidator;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.constant.enums.EnumMessageType;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class CsvValidator implements ICsvValidator {
    String schemaPath = "./csv-schema/Patients.csv";
    Gson gson;

    public CsvValidator() {
        gson = new Gson();
    }

    public ValidatedELRModel ValidateCSVAgainstCVSSchema(String message) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(schemaPath));
        String[] header = reader.readNext();
        int headerLength = header.length;
        List<List<String>> kafkaMsg = gson.fromJson(message, List.class);

        for(var item : kafkaMsg ){
            if(item.size() != headerLength) {
                // do specific expcetion maybe CSV
                throw new Exception("Invalid record, one or more record does not match with schema definition");
            }
        }

        ValidatedELRModel model = new ValidatedELRModel();
        model.setRawMessage(message);
        model.setMessageType(EnumMessageType.CSV.name());
        model.setMessageVersion("NA");

        return model;
    }

    public List<List<String>> ReadLineByLine(Reader reader) throws Exception {
        List<List<String>> list = new ArrayList<>();

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();

        String[] line;
        while ((line = csvReader.readNext()) != null) {
            List record = Arrays.asList(line);
            list.add(record);
        }

        return list;
    }

}