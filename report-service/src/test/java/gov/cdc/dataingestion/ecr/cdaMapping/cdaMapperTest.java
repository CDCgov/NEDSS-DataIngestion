package gov.cdc.dataingestion.ecr.cdaMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import gov.cdc.dataingestion.deadletter.service.ElrDeadLetterService;
import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.CdaMapper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.*;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.*;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.services.CdaLookUpService;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStreamReader;
import java.io.Reader;

public class cdaMapperTest {
    @Mock
    private ICdaLookUpService cdaLookUpService;

    @InjectMocks
    private CdaMapper target;

    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
        target = new CdaMapper(cdaLookUpService);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test() throws EcrCdaXmlException {
        EcrSelectedRecord input = new EcrSelectedRecord();
        String dateFormat = "yyyy-MM-dd HH:mm:ss.S";

        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        try (Reader reader = new InputStreamReader(JsonReader.class.getResourceAsStream("/test-data-ecr/ecr.json"), "UTF-8")) {
            EcrSelectedRecord ecrObject = gson.fromJson(reader, EcrSelectedRecord.class);
            ecrObject.getMsgCases().get(0).getMsgCase().initDataMap();
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


            var result = target.tranformSelectedEcrToCDAXml(ecrObject);

            Assertions.assertNotNull(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
