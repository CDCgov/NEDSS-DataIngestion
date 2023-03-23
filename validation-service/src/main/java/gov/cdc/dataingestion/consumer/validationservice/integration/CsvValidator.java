package gov.cdc.dataingestion.consumer.validationservice.integration;

import com.google.gson.Gson;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import gov.cdc.dataingestion.consumer.validationservice.integration.interfaces.ICsvValidator;
import gov.cdc.dataingestion.consumer.validationservice.model.MessageModel;
import gov.cdc.dataingestion.consumer.validationservice.model.enums.MessageType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvValidator implements ICsvValidator {
    String schemaPath = "./csv-schema/Patients.csv";
    Gson gson;

    public CsvValidator() {
        gson = new Gson();
    }

    public MessageModel ValidateCSVAgainstCVSSchema(String message) throws Exception {
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

        MessageModel model = new MessageModel();
        model.setRawMessage(message);
        model.setMessageType(MessageType.CSV);
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
