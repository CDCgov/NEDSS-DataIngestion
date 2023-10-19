package gov.cdc.dataingestion.ecr.cdaMapping;

import gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapStringHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

class CdaMapStringHelperTest {
    @InjectMocks
    private CdaMapStringHelper target;

    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStringsBeforePipe_Test() {
        List<String> inputList = new ArrayList<>();
        inputList.add("test");
        inputList.add("test");
        var result = target.getStringsBeforePipe("test|test");

        Assertions.assertEquals(inputList.get(0), result.get(0));
    }

    @Test
    void getStringsBeforeCaret_Test() {
        List<String> inputList = new ArrayList<>();
        inputList.add("test");
        inputList.add("test");
        var result = target.getStringsBeforeCaret("test^test");

        Assertions.assertEquals(inputList.get(0), result.get(0));
    }
}
