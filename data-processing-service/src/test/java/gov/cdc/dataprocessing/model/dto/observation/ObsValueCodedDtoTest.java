package gov.cdc.dataprocessing.model.dto.observation;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ObsValueCodedDtoTest {
    @Test
    void testGetAndSet() {
        ObsValueCodedDto entity = new ObsValueCodedDto();
        Collection<Object> obsValueCodedModDTCollection = new HashSet<>();
        obsValueCodedModDTCollection.add("testValue");

        entity.setTheObsValueCodedModDTCollection(obsValueCodedModDTCollection);
        entity.setSearchResultRT("RT result");
        entity.setCdSystemCdRT("RT system code");
        entity.setHiddenCd("hidden code");

        assertEquals(obsValueCodedModDTCollection, entity.getTheObsValueCodedModDTCollection());
        assertEquals("RT result", entity.getSearchResultRT());
        assertEquals("RT system code", entity.getCdSystemCdRT());
        assertEquals("hidden code", entity.getHiddenCd());
    }

    @Test
    void testGetAndSet2() {
        ObsValueNumericDto entity = new ObsValueNumericDto();
        entity.setNumericValue("TEST");
        assertNotNull(entity.getNumericValue());
    }
}
