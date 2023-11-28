package gov.cdc.dataingestion.ecr.cdaMapping;

import gov.cdc.dataingestion.exception.DeadLetterTopicException;
import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.CdaMapper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import org.apache.xmlbeans.XmlObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static gov.cdc.dataingestion.ecr.cdaMapping.helper.TestDataInitiation.getTestData;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class CdaMapperTest {
    @Mock
    private ICdaLookUpService cdaLookUpService;

    @InjectMocks
    private CdaMapper target;

    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void transformSelectedEcrToCDAXml_Test() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();

        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");

        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_PatientMulti() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();
        var patients = input.getMsgPatients();
        var patientToDuplicate = patients.get(0);
        patients.add(patientToDuplicate);
        input.setMsgPatients(patients);
        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");

        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_PatientOnly() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();
        EcrSelectedRecord patientOnlyInput = new EcrSelectedRecord();
        patientOnlyInput.setMsgContainer(input.getMsgContainer());
        patientOnlyInput.setMsgPatients(input.getMsgPatients());
        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");

        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_CaseMulti() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();

        var cases = input.getMsgCases();
        cases.add(cases.get(0));
        input.setMsgCases(cases);

        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");

        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_OrgMulti() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();

        var cases = input.getMsgOrganizations();
        cases.add(cases.get(0));
        input.setMsgOrganizations(cases);

        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");

        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_PlaceMulti() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();

        var cases = input.getMsgPlaces();
        cases.add(cases.get(0));
        input.setMsgPlaces(cases);

        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");

        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_InterviewMulti() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();

        var cases = input.getMsgInterviews();
        cases.add(cases.get(0));
        input.setMsgInterviews(cases);

        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");

        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_TreatmentMulti() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();

        var cases = input.getMsgTreatments();
        cases.add(cases.get(0));
        input.setMsgTreatments(cases);

        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");

        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_TypePart() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();
        input.getMsgPatients().get(0).setPatAddrCommentTxt("PART");
        var patients = input.getMsgPatients();
        var patientToDuplicate = patients.get(0);
        patients.add(patientToDuplicate);
        input.setMsgPatients(patients);
        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test1");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");


        var questionLookup = new PhdcQuestionLookUpDto();
        questionLookup.setDocTypeCd("test");
        questionLookup.setDocTypeVersionTxt("test");
        questionLookup.setQuesCodeSystemCd("test");
        questionLookup.setQuesCodeSystemDescTxt("test");
        questionLookup.setDataType("PART");
        questionLookup.setQuestionIdentifier("test");
        questionLookup.setQuesDisplayName("test");
        questionLookup.setSectionNm("test");
        questionLookup.setSendingSystemCd("test");
        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchPhdcQuestionByCriteria( ""))
                .thenReturn(questionLookup);

        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        when(cdaLookUpService.fetchPhdcQuestionByCriteriaWithColumn("Question_Identifier", ""))
                .thenReturn(questionLookup);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_TypeCODED() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();
        input.getMsgPatients().get(0).setPatAddrCommentTxt("CODED");
        var patients = input.getMsgPatients();
        var patientToDuplicate = patients.get(0);
        patients.add(patientToDuplicate);
        input.setMsgPatients(patients);
        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test2");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");


        var questionLookup = new PhdcQuestionLookUpDto();
        questionLookup.setDocTypeCd("test");
        questionLookup.setDocTypeVersionTxt("test");
        questionLookup.setQuesCodeSystemCd("test");
        questionLookup.setQuesCodeSystemDescTxt("test");
        questionLookup.setDataType("CODED");
        questionLookup.setQuestionIdentifier("test");
        questionLookup.setQuesDisplayName("test");
        questionLookup.setSectionNm("test");
        questionLookup.setSendingSystemCd("test");
        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchPhdcQuestionByCriteria( ""))
                .thenReturn(questionLookup);

        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        when(cdaLookUpService.fetchPhdcQuestionByCriteriaWithColumn("Question_Identifier", ""))
                .thenReturn(questionLookup);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_ElseCase() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();
        input.getMsgPatients().get(0).setPatAddrCommentTxt("CODED");
        var patients = input.getMsgPatients();
        var patientToDuplicate = patients.get(0);
        patients.add(patientToDuplicate);
        input.setMsgPatients(patients);
        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test3");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");


        var questionLookup = new PhdcQuestionLookUpDto();
        questionLookup.setDocTypeCd("test");
        questionLookup.setDocTypeVersionTxt("test");
        questionLookup.setQuesCodeSystemCd("test");
        questionLookup.setQuesCodeSystemDescTxt("test");
        questionLookup.setDataType("ELSE");
        questionLookup.setQuestionIdentifier("test");
        questionLookup.setQuesDisplayName("test");
        questionLookup.setSectionNm("test");
        questionLookup.setSendingSystemCd("test");
        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchPhdcQuestionByCriteria( ""))
                .thenReturn(questionLookup);

        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        when(cdaLookUpService.fetchPhdcQuestionByCriteriaWithColumn("Question_Identifier", ""))
                .thenReturn(questionLookup);

        var result = target.tranformSelectedEcrToCDAXml(input);

        verify(cdaLookUpService).fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101");
        Assertions.assertNotNull(result);
    }

    @Test
    void transformSelectedEcrToCDAXml_Test_TypeNotCODED() throws EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();
        input.getMsgPatients().get(0).setPatAddrCommentTxt( "2023/04/15 10:30:45.123");
        var patients = input.getMsgPatients();
        var patientToDuplicate = patients.get(0);
        patients.add(patientToDuplicate);
        input.setMsgPatients(patients);
        var lookupDto1 = new ConstantLookUpDto();
        lookupDto1.setId("test");
        lookupDto1.setSubjectArea("test");
        lookupDto1.setQuestionDisplayName("test");
        lookupDto1.setQuestionIdentifier("test");
        lookupDto1.setSampleValue("test");
        lookupDto1.setUsage("test");


        var questionLookup = new PhdcQuestionLookUpDto();
        questionLookup.setDocTypeCd("test");
        questionLookup.setDocTypeVersionTxt("test");
        questionLookup.setQuesCodeSystemCd("test");
        questionLookup.setQuesCodeSystemDescTxt("test");
        questionLookup.setDataType("DATE");
        questionLookup.setQuestionIdentifier("test");
        questionLookup.setQuesDisplayName("test");
        questionLookup.setSectionNm("test");
        questionLookup.setSendingSystemCd("test");
        // Using the correct mock (interface) here
        when(cdaLookUpService.fetchPhdcQuestionByCriteria( ""))
                .thenReturn(questionLookup);

        when(cdaLookUpService.fetchConstantLookUpByCriteriaWithColumn("QuestionIdentifier", "CUS101"))
                .thenReturn(lookupDto1);

        when(cdaLookUpService.fetchPhdcQuestionByCriteriaWithColumn("Question_Identifier", ""))
                .thenReturn(questionLookup);

        var exception = Assertions.assertThrows(EcrCdaXmlException.class, () -> {
            target.tranformSelectedEcrToCDAXml(input);
        });
        Assertions.assertNotNull(exception);
    }


}
