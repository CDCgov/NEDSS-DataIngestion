package gov.cdc.dataprocessing.repository.nbs.msgoute.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NbsInterfaceModelTest {

    @Test
    void testGettersAndSetters() {
        NbsInterfaceModel model = new NbsInterfaceModel();

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        // Set values
        model.setNbsInterfaceUid(1);
        model.setPayload("Test Payload");
        model.setImpExpIndCd("IMP");
        model.setRecordStatusCd("Active");
        model.setRecordStatusTime(currentTime);
        model.setAddTime(currentTime);
        model.setSystemNm("Test System");
        model.setDocTypeCd("DOC123");
        model.setOriginalPayload("Original Payload");
        model.setOriginalDocTypeCd("Original Doc Type");
        model.setFillerOrderNbr("12345");
        model.setLabClia("Lab123");
        model.setSpecimenCollDate(currentTime);
        model.setOrderTestCode("Test123");
        model.setObservationUid(101);

        // Assert values
        assertEquals(1, model.getNbsInterfaceUid());
        assertEquals("Test Payload", model.getPayload());
        assertEquals("IMP", model.getImpExpIndCd());
        assertEquals("Active", model.getRecordStatusCd());
        assertEquals(currentTime, model.getRecordStatusTime());
        assertEquals(currentTime, model.getAddTime());
        assertEquals("Test System", model.getSystemNm());
        assertEquals("DOC123", model.getDocTypeCd());
        assertEquals("Original Payload", model.getOriginalPayload());
        assertEquals("Original Doc Type", model.getOriginalDocTypeCd());
        assertEquals("12345", model.getFillerOrderNbr());
        assertEquals("Lab123", model.getLabClia());
        assertEquals(currentTime, model.getSpecimenCollDate());
        assertEquals("Test123", model.getOrderTestCode());
        assertEquals(101, model.getObservationUid());
    }

    @Test
    void testNoArgsConstructor() {
        NbsInterfaceModel model = new NbsInterfaceModel();

        assertNotNull(model);
        assertNull(model.getNbsInterfaceUid());
        assertNull(model.getPayload());
        assertNull(model.getImpExpIndCd());
        assertNull(model.getRecordStatusCd());
        assertNull(model.getRecordStatusTime());
        assertNull(model.getAddTime());
        assertNull(model.getSystemNm());
        assertNull(model.getDocTypeCd());
        assertNull(model.getOriginalPayload());
        assertNull(model.getOriginalDocTypeCd());
        assertNull(model.getFillerOrderNbr());
        assertNull(model.getLabClia());
        assertNull(model.getSpecimenCollDate());
        assertNull(model.getOrderTestCode());
        assertNull(model.getObservationUid());
    }
}
