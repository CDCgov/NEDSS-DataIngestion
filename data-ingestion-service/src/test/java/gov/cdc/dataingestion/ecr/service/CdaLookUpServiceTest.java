package gov.cdc.dataingestion.ecr.service;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.IEcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import gov.cdc.dataingestion.nbs.services.CdaLookUpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
class CdaLookUpServiceTest {
    @Mock
    private IEcrLookUpRepository ecrLookUpRepository;
    @InjectMocks
    private CdaLookUpService target;

    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
        target = new CdaLookUpService(ecrLookUpRepository);
    }

    @Test
    void fetchConstantLookUpByCriteriaWithColumn_Test() throws EcrCdaXmlException {
        String column = "test";
        String value = "test";
        ConstantLookUpDto dto = new ConstantLookUpDto();
        dto.setId("test");
        dto.setSubjectArea("test");
        dto.setQuestionIdentifier("test");
        dto.setQuestionDisplayName("test");
        dto.setSampleValue("test");
        dto.setUsage("test");
        when(ecrLookUpRepository.fetchConstantLookUpByCriteriaWithColumn(
                column, value
        )).thenReturn(dto);

        var result = target.fetchConstantLookUpByCriteriaWithColumn(column, value);

        Assertions.assertEquals(dto.getId(), result.getId());
        Assertions.assertEquals(dto.getSubjectArea(), result.getSubjectArea());
        Assertions.assertEquals(dto.getQuestionIdentifier(), result.getQuestionIdentifier());
        Assertions.assertEquals(dto.getQuestionDisplayName(), result.getQuestionDisplayName());
        Assertions.assertEquals(dto.getSampleValue(), result.getSampleValue());
        Assertions.assertEquals(dto.getUsage(), result.getUsage());
    }

    @Test
    void fetchPhdcAnswerByCriteriaForTranslationCode_Test() throws EcrCdaXmlException {
        String column = "test";
        String value = "test";
        PhdcAnswerLookUpDto dto = new PhdcAnswerLookUpDto();
        dto.setAnsFromCode("test");
        dto.setAnsFromCodeSystemCd("test");
        dto.setAnsFromCodeSystemDescTxt("test");
        dto.setAnsFromDisplayNm("test");
        dto.setAnsToCode("test");
        dto.setAnsToCodeSystemCd("test");
        dto.setAnsToCodeSystemDescTxt("test");
        dto.setAnsToDisplayNm("test");
        dto.setCodeTranslationRequired("test");
        dto.setDocTypeCd("test");
        dto.setDocTypeVersionTxt("test");
        dto.setQuesCodeSystemCd("test");
        dto.setQuestionIdentifier("test");
        dto.setSendingSystemCd("test");

        when(ecrLookUpRepository.fetchPhdcAnswerByCriteriaForTranslationCode(
                column, value
        )).thenReturn(dto);

        var result = target.fetchPhdcAnswerByCriteriaForTranslationCode(column, value);

        Assertions.assertEquals(dto.getAnsFromCode(), result.getAnsFromCode());
        Assertions.assertEquals(dto.getAnsFromCodeSystemCd(), result.getAnsFromCodeSystemCd());
        Assertions.assertEquals(dto.getAnsFromCodeSystemDescTxt(), result.getAnsFromCodeSystemDescTxt());
        Assertions.assertEquals(dto.getAnsFromDisplayNm(), result.getAnsFromDisplayNm());
        Assertions.assertEquals(dto.getAnsToCode(), result.getAnsToCode());
        Assertions.assertEquals(dto.getAnsToCodeSystemCd(), result.getAnsToCodeSystemCd());
        Assertions.assertEquals(dto.getAnsToCodeSystemDescTxt(), result.getAnsToCodeSystemDescTxt());
        Assertions.assertEquals(dto.getAnsToDisplayNm(), result.getAnsToDisplayNm());
        Assertions.assertEquals(dto.getCodeTranslationRequired(), result.getCodeTranslationRequired());
        Assertions.assertEquals(dto.getDocTypeCd(), result.getDocTypeCd());
        Assertions.assertEquals(dto.getDocTypeVersionTxt(), result.getDocTypeVersionTxt());
        Assertions.assertEquals(dto.getQuesCodeSystemCd(), result.getQuesCodeSystemCd());
        Assertions.assertEquals(dto.getQuestionIdentifier(), result.getQuestionIdentifier());
        Assertions.assertEquals(dto.getSendingSystemCd(), result.getSendingSystemCd());

    }

    @Test
    void fetchPhdcQuestionByCriteria_Test() throws EcrCdaXmlException {
        String value = "test";
        PhdcQuestionLookUpDto dto = new PhdcQuestionLookUpDto();
        dto.setDocTypeCd("test");
        dto.setDocTypeVersionTxt("test");
        dto.setQuesCodeSystemCd("test");
        dto.setQuesCodeSystemDescTxt("test");
        dto.setDataType("test");
        dto.setQuestionIdentifier("test");
        dto.setQuesDisplayName("test");
        dto.setSectionNm("test");
        dto.setSendingSystemCd("test");

        when(ecrLookUpRepository.fetchPhdcQuestionByCriteria(
                value
        )).thenReturn(dto);
        var result = target.fetchPhdcQuestionByCriteria(value);

        Assertions.assertEquals(dto.getDocTypeCd(), result.getDocTypeCd());
        Assertions.assertEquals(dto.getDocTypeVersionTxt(), result.getDocTypeVersionTxt());
        Assertions.assertEquals(dto.getQuesCodeSystemCd(), result.getQuesCodeSystemCd());
        Assertions.assertEquals(dto.getQuesCodeSystemDescTxt(), result.getQuesCodeSystemDescTxt());
        Assertions.assertEquals(dto.getDataType(), result.getDataType());
        Assertions.assertEquals(dto.getQuestionIdentifier(), result.getQuestionIdentifier());
        Assertions.assertEquals(dto.getQuesDisplayName(), result.getQuesDisplayName());
        Assertions.assertEquals(dto.getSectionNm(), result.getSectionNm());
        Assertions.assertEquals(dto.getSendingSystemCd(), result.getSendingSystemCd());

    }

    @Test
    void fetchPhdcQuestionByCriteriaWithColumn_Test() throws EcrCdaXmlException {
        String value = "test";
        String column = "test";
        PhdcQuestionLookUpDto dto = new PhdcQuestionLookUpDto();
        dto.setDocTypeCd("test");
        dto.setDocTypeVersionTxt("test");
        dto.setQuesCodeSystemCd("test");
        dto.setQuesCodeSystemDescTxt("test");
        dto.setDataType("test");
        dto.setQuestionIdentifier("test");
        dto.setQuesDisplayName("test");
        dto.setSectionNm("test");
        dto.setSendingSystemCd("test");

        when(ecrLookUpRepository.fetchPhdcQuestionByCriteriaWithColumn(
                column, value
        )).thenReturn(dto);
        var result = target.fetchPhdcQuestionByCriteriaWithColumn(column, value);

        Assertions.assertEquals(dto.getDocTypeCd(), result.getDocTypeCd());
        Assertions.assertEquals(dto.getDocTypeVersionTxt(), result.getDocTypeVersionTxt());
        Assertions.assertEquals(dto.getQuesCodeSystemCd(), result.getQuesCodeSystemCd());
        Assertions.assertEquals(dto.getQuesCodeSystemDescTxt(), result.getQuesCodeSystemDescTxt());
        Assertions.assertEquals(dto.getDataType(), result.getDataType());
        Assertions.assertEquals(dto.getQuestionIdentifier(), result.getQuestionIdentifier());
        Assertions.assertEquals(dto.getQuesDisplayName(), result.getQuesDisplayName());
        Assertions.assertEquals(dto.getSectionNm(), result.getSectionNm());
        Assertions.assertEquals(dto.getSendingSystemCd(), result.getSendingSystemCd());

    }

    @Test
    void fetchQuestionIdentifierMapByCriteriaByCriteria_Test() throws EcrCdaXmlException {
        String value = "test";
        String column = "test";
        QuestionIdentifierMapDto dto = new QuestionIdentifierMapDto();
        dto.setColumnNm("test");
        dto.setQuestionIdentifier("test");
        dto.setDynamicQuestionIdentifier("test");

        when(ecrLookUpRepository.fetchQuestionIdentifierMapByCriteriaByCriteria(
                column, value
        )).thenReturn(dto);
        var result = target.fetchQuestionIdentifierMapByCriteriaByCriteria(column, value);

        Assertions.assertEquals(dto.getColumnNm(), result.getColumnNm());
        Assertions.assertEquals(dto.getQuestionIdentifier(), result.getQuestionIdentifier());
        Assertions.assertEquals(dto.getDynamicQuestionIdentifier(), result.getDynamicQuestionIdentifier());

    }
}
