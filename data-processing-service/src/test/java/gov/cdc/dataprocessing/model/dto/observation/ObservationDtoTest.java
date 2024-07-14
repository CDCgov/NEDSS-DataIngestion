package gov.cdc.dataprocessing.model.dto.observation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ObservationDtoTest {
    @Test
    void testGetAndSet() {
        ObservationDto entity = new ObservationDto();

        entity.setSearchResultOT("OT result");
        entity.setSearchResultRT("RT result");
        entity.setCdSystemCdOT("OT system code");
        entity.setCdSystemCdRT("RT system code");
        entity.setHiddenCd("hidden code");
        entity.setCodedResultCd("coded result");
        entity.setOrganismCd("organism code");
        entity.setSusceptabilityVal("susceptibility value");
        entity.setResultedMethodCd("resulted method");
        entity.setDrugNameCd("drug name");
        entity.setInterpretiveFlagCd("interpretive flag");

        assertEquals("OT result", entity.getSearchResultOT());
        assertEquals("RT result", entity.getSearchResultRT());
        assertEquals("OT system code", entity.getCdSystemCdOT());
        assertEquals("RT system code", entity.getCdSystemCdRT());
        assertEquals("hidden code", entity.getHiddenCd());
        assertEquals("coded result", entity.getCodedResultCd());
        assertEquals("organism code", entity.getOrganismCd());
        assertEquals("susceptibility value", entity.getSusceptabilityVal());
        assertEquals("resulted method", entity.getResultedMethodCd());
        assertEquals("drug name", entity.getDrugNameCd());
        assertEquals("interpretive flag", entity.getInterpretiveFlagCd());

        assertNotNull(entity.getSuperclass());
    }
}
