package gov.cdc.dataingestion.nbs.repository.implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gov.cdc.dataingestion.nbs.repository.model.dto.*;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonReaderTester {
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
            e.printStackTrace();
        }
        return null;
    }

    public static EcrMsgOrganizationDto loadOrg() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.S").create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgOrg.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgOrganizationDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<EcrMsgProviderDto> loadProvider() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.S").create();
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
            e.printStackTrace();
        }
        return null;
    }

    public static List<EcrMsgCaseParticipantDto> loadCasePar() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.S").create();
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
            e.printStackTrace();
        }
        return null;
    }

    public static EcrMsgPatientDto loadPatient() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.S").create();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgPatient.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgPatientDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static EcrMsgCaseDto loadCase() {
        Gson gson = new Gson();
        try {
            var in =  JsonReaderTester.class.getResourceAsStream("/testData/ecrMsgCase.json");
            InputStreamReader reader = new InputStreamReader(
                    in,
                    StandardCharsets.UTF_8);
            var res =  gson.fromJson(reader, EcrMsgCaseDto.class);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
