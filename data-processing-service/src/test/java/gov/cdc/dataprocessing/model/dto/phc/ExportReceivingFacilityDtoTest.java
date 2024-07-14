package gov.cdc.dataprocessing.model.dto.phc;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExportReceivingFacilityDtoTest {

    @Test
    void testGettersAndSetters() {
        ExportReceivingFacilityDto dto = new ExportReceivingFacilityDto();

        // Set values
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setReportType("ReportType");
        dto.setAddUserId(1L);
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(2L);
        dto.setExportReceivingFacilityUid(3L);
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setReceivingSystemNm("ReceivingSystemNm");
        dto.setReceivingSystemOid("ReceivingSystemOid");
        dto.setReceivingSystemShortName("ReceivingSystemShortName");
        dto.setReceivingSystemOwner("ReceivingSystemOwner");
        dto.setReceivingSystemOwnerOid("ReceivingSystemOwnerOid");
        dto.setReceivingSystemDescTxt("ReceivingSystemDescTxt");
        dto.setSendingIndCd("SendingIndCd");
        dto.setReceivingIndCd("ReceivingIndCd");
        dto.setAllowTransferIndCd("AllowTransferIndCd");
        dto.setAdminComment("AdminComment");
        dto.setSendingIndDescTxt("SendingIndDescTxt");
        dto.setReceivingIndDescTxt("ReceivingIndDescTxt");
        dto.setAllowTransferIndDescTxt("AllowTransferIndDescTxt");
        dto.setReportTypeDescTxt("ReportTypeDescTxt");
        dto.setRecordStatusCdDescTxt("RecordStatusCdDescTxt");
        dto.setJurDeriveIndCd("JurDeriveIndCd");

        // Assert values
        assertNotNull(dto.getAddTime());
        assertEquals("ReportType", dto.getReportType());
        assertEquals(1L, dto.getAddUserId());
        assertNotNull(dto.getLastChgTime());
        assertEquals(2L, dto.getLastChgUserId());
        assertEquals(3L, dto.getExportReceivingFacilityUid());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertEquals("ReceivingSystemNm", dto.getReceivingSystemNm());
        assertEquals("ReceivingSystemOid", dto.getReceivingSystemOid());
        assertEquals("ReceivingSystemShortName", dto.getReceivingSystemShortName());
        assertEquals("ReceivingSystemOwner", dto.getReceivingSystemOwner());
        assertEquals("ReceivingSystemOwnerOid", dto.getReceivingSystemOwnerOid());
        assertEquals("ReceivingSystemDescTxt", dto.getReceivingSystemDescTxt());
        assertEquals("SendingIndCd", dto.getSendingIndCd());
        assertEquals("ReceivingIndCd", dto.getReceivingIndCd());
        assertEquals("AllowTransferIndCd", dto.getAllowTransferIndCd());
        assertEquals("AdminComment", dto.getAdminComment());
        assertEquals("SendingIndDescTxt", dto.getSendingIndDescTxt());
        assertEquals("ReceivingIndDescTxt", dto.getReceivingIndDescTxt());
        assertEquals("AllowTransferIndDescTxt", dto.getAllowTransferIndDescTxt());
        assertEquals("ReportTypeDescTxt", dto.getReportTypeDescTxt());
        assertEquals("RecordStatusCdDescTxt", dto.getRecordStatusCdDescTxt());
        assertEquals("JurDeriveIndCd", dto.getJurDeriveIndCd());
    }
}
