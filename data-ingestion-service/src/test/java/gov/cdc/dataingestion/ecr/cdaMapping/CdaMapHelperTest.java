package gov.cdc.dataingestion.ecr.cdaMapping;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapHelper;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
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
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
class CdaMapHelperTest {
    @Mock
    private ICdaLookUpService cdaLookUpService;
    @InjectMocks
    private CdaMapHelper target;


    @BeforeEach
    void setUpEach() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void mapToTsTypeTestElseCase() throws EcrCdaXmlException {
        String time = "2023/04/15 10:30:45.123";
        var result = target.mapToTsType(time);
        Assertions.assertNotNull(result);
    }

    @Test
    void mapToQuestionIdTestIdentifierNotNull() throws EcrCdaXmlException {
        var lookup = new PhdcAnswerLookUpDto();
        lookup.setAnsToCode("");
        lookup.setAnsToCodeSystemCd("test");
        lookup.setAnsFromCodeSystemDescTxt("test");
        lookup.setAnsFromDisplayNm("test");
        lookup.setAnsToCodeSystemCd("test");
        lookup.setAnsToCodeSystemDescTxt("test");
        lookup.setAnsToDisplayNm("Test");
        String data ="test";
        when(cdaLookUpService.fetchPhdcAnswerByCriteriaForTranslationCode(
                "test", data))
                .thenReturn(lookup);

        var result = target.mapToCodedAnswer("test", "test");
        Assertions.assertEquals("test", result.getCode());

    }
}
