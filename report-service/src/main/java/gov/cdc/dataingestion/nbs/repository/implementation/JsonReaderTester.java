package gov.cdc.dataingestion.nbs.repository.implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

// THIS TEST CLASS
public class JsonReaderTester {
    private JsonReaderTester() {
        // this is test class
    }
    private static String dateFormat = "yyyy-MM-dd HH:mm:ss.S";
    public static EcrMsgContainerDto loadContainer() {
        Gson gson = new Gson();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgContainer.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgContainerDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static EcrMsgPatientDto loadPatient() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgPatient.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgPatientDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    //region ECR CASE
    public static EcrMsgCaseDto loadCase() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgCase.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgCaseDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static List<EcrMsgCaseParticipantDto> loadCasePar() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgCasePaticipant.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgCaseParticipantDto>>() {}.getType();
            List<EcrMsgCaseParticipantDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static List<EcrMsgCaseAnswerDto> loadCaseAnswer() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgCaseAnswer.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgCaseAnswerDto>>() {}.getType();
            List<EcrMsgCaseAnswerDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static List<EcrMsgCaseAnswerDto> loadCaseAnswerRepeat() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgCaseAnswerRepeat.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgCaseAnswerDto>>() {}.getType();
            List<EcrMsgCaseAnswerDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }
    //endregion

    public static List<EcrMsgProviderDto> loadProvider() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgProvider.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgProviderDto>>() {}.getType();
            List<EcrMsgProviderDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static EcrMsgOrganizationDto loadOrg() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgOrg.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgOrganizationDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    //region INTERVIEW
    public static EcrMsgInterviewDto loadInterview() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgInterview.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgInterviewDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static List<EcrMsgCaseAnswerDto> loadInterviewAnswer() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgInterviewAnswer.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgCaseAnswerDto>>() {}.getType();
            List<EcrMsgCaseAnswerDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static List<EcrMsgCaseAnswerDto> loadInterviewAnswerRepeat() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgInterviewAnswerRepeat.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgCaseAnswerDto>>() {}.getType();
            List<EcrMsgCaseAnswerDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static List<EcrMsgInterviewProviderDto> loadInterviewProvider() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgInterviewProvider.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgInterviewProviderDto>>() {}.getType();
            List<EcrMsgInterviewProviderDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }
    //endregion

    public static EcrMsgPlaceDto loadPlace() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgPlace.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgPlaceDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    //region TREATMENT
    public static EcrMsgTreatmentDto loadTreatment() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgTreatment.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgTreatmentDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static List<EcrMsgTreatmentOrganizationDto> loadTreatmentOrg() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgTreatmentOrg.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgTreatmentOrganizationDto>>() {}.getType();
            List<EcrMsgTreatmentOrganizationDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }

    public static List<EcrMsgTreatmentProviderDto> loadTreatmentProvider() {
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgTreatmentProvider.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            // This is the line where we provide Gson with the type information.
            Type listType = new TypeToken<List<EcrMsgTreatmentProviderDto>>() {}.getType();
            List<EcrMsgTreatmentProviderDto> res = gson.fromJson(reader, listType);
            return res;
        } catch (Exception e) {
            e.printStackTrace(); // NOSONAR
        }
        return null; // NOSONAR
    }
    //endregion

}
