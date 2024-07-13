package gov.cdc.dataprocessing.model.dto.edx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EDXEventProcessCaseSummaryDtoTest {

    @Test
    void testGettersAndSetters() {
        EDXEventProcessCaseSummaryDto dto = new EDXEventProcessCaseSummaryDto();

        String conditionCd = "conditionCd";
        Long personParentUid = 12345L;
        Long personUid = 67890L;
        String personLocalId = "personLocalId";

        dto.setConditionCd(conditionCd);
        dto.setPersonParentUid(personParentUid);
        dto.setPersonUid(personUid);
        dto.setPersonLocalId(personLocalId);

        assertEquals(conditionCd, dto.getConditionCd());
        assertEquals(personParentUid, dto.getPersonParentUid());
        assertEquals(personUid, dto.getPersonUid());
        assertEquals(personLocalId, dto.getPersonLocalId());
    }
}
