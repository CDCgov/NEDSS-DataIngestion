package gov.cdc.dataingestion.ecr.cdaMapping.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class TestDataInitiation {
    private static EcrSelectedRecord initTestData(EcrSelectedRecord ecrObject) throws EcrCdaXmlException {
        for(int i = 0; i < ecrObject.getMsgCases().get(0).getMsgCaseAnswers().size(); i++) {
            ecrObject.getMsgCases().get(0).getMsgCaseAnswers().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgCases().get(0).getMsgCaseAnswerRepeats().size(); i++) {
            ecrObject.getMsgCases().get(0).getMsgCaseAnswerRepeats().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgProviders().size(); i++) {
            ecrObject.getMsgProviders().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgOrganizations().size(); i++) {
            ecrObject.getMsgOrganizations().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgPlaces().size(); i++) {
            ecrObject.getMsgPlaces().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgInterviews().size(); i++) {
            ecrObject.getMsgInterviews().get(i).getMsgInterview().initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgInterviews().get(0).getMsgInterviewProviders().size(); i++) {
            ecrObject.getMsgInterviews().get(0).getMsgInterviewProviders().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgInterviews().get(0).getMsgInterviewAnswers().size(); i++) {
            ecrObject.getMsgInterviews().get(0).getMsgInterviewAnswers().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgInterviews().get(0).getMsgInterviewAnswerRepeats().size(); i++) {
            ecrObject.getMsgInterviews().get(0).getMsgInterviewAnswerRepeats().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgTreatments().size(); i++) {
            ecrObject.getMsgTreatments().get(0).getMsgTreatment().initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgTreatments().get(0).getMsgTreatmentProviders().size(); i++) {
            ecrObject.getMsgTreatments().get(0).getMsgTreatmentProviders().get(i).initDataMap();
        }
        for(int i = 0; i < ecrObject.getMsgTreatments().get(0).getMsgTreatmentOrganizations().size(); i++) {
            ecrObject.getMsgTreatments().get(0).getMsgTreatmentOrganizations().get(i).initDataMap();
        }

        return ecrObject;

    }
    public static EcrSelectedRecord getTestData() {
        String dateFormat = "yyyy-MM-dd HH:mm:ss.S";
        EcrSelectedRecord ecrObject = null;
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try (Reader reader = new InputStreamReader(JsonReader.class.getResourceAsStream("/test-data-ecr/ecr.json"), "UTF-8")) {
            ecrObject = gson.fromJson(reader, EcrSelectedRecord.class);
            ecrObject.getMsgCases().get(0).getMsgCase().initDataMap();
            ecrObject = initTestData(ecrObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ecrObject;
    }
}
