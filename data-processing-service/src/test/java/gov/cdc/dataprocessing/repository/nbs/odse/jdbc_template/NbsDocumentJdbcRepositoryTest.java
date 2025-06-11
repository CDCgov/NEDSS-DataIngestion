package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocumentHist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.constant.query.NbsDocumentQuery.MERGE_NBS_DOC;
import static gov.cdc.dataprocessing.constant.query.NbsDocumentQuery.MERGE_NBS_DOC_HIST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NbsDocumentJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private NbsDocumentJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMergeNbsDocument_shouldCallUpdate() {
        NbsDocument doc = new NbsDocument();
        doc.setNbsDocumentUid(1L);
        doc.setDocPayload("payload");
        doc.setDocTypeCd("type");
        doc.setLocalId("locId");
        doc.setRecordStatusCd("A");
        doc.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        doc.setAddUserId(123L);
        doc.setAddTime(new Timestamp(System.currentTimeMillis()));
        doc.setProgAreaCd("prog");
        doc.setJurisdictionCd("jur");
        doc.setTxt("text");
        doc.setProgramJurisdictionOid(999L);
        doc.setSharedInd("Y");
        doc.setVersionCtrlNbr(1);
        doc.setCd("cd");
        doc.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        doc.setLastChgUserId(321L);
        doc.setDocPurposeCd("purpose");
        doc.setDocStatusCd("status");
        doc.setCdDescTxt("desc");
        doc.setSendingFacilityNm("facility");
        doc.setNbsInterfaceUid(888L);
        doc.setSendingAppEventId("eventId");
        doc.setSendingAppPatientId("patientId");
        doc.setPhdcDocDerived("Y");
        doc.setPayloadViewIndCd("1");
        doc.setExternalVersionCtrlNbr(2);
        doc.setProcessingDecisionTxt("decision");
        doc.setProcessingDecisionCd("procCd");

        repository.mergeNbsDocument(doc);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_NBS_DOC), any(MapSqlParameterSource.class));
    }

    @Test
    void testMergeNbsDocumentHist_shouldCallUpdate() {
        NbsDocumentHist hist = new NbsDocumentHist();
        hist.setNbsDocumentHistUid(10L);
        hist.setDocPayload("payload");
        hist.setDocTypeCd("type");
        hist.setLocalId("locId");
        hist.setRecordStatusCd("A");
        hist.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        hist.setAddUserId(123L);
        hist.setAddTime(new Timestamp(System.currentTimeMillis()));
        hist.setProgAreaCd("prog");
        hist.setJurisdictionCd("jur");
        hist.setTxt("text");
        hist.setProgramJurisdictionOid(999L);
        hist.setSharedInd("Y");
        hist.setVersionCtrlNbr(1);
        hist.setCd("cd");
        hist.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        hist.setLastChgUserId(321L);
        hist.setDocPurposeCd("purpose");
        hist.setDocStatusCd("status");
        hist.setCdDescTxt("desc");
        hist.setSendingFacilityNm("facility");
        hist.setNbsInterfaceUid(888L);
        hist.setSendingAppEventId("eventId");
        hist.setSendingAppPatientId("patientId");
        hist.setNbsDocumentUid(1L);
        hist.setPhdcDocDerived("Y");
        hist.setPayloadViewIndCd("1");
        hist.setNbsDocumentMetadataUid(555L);
        hist.setExternalVersionCtrlNbr(2);
        hist.setProcessingDecisionTxt("decision");
        hist.setProcessingDecisionCd("procCd");

        repository.mergeNbsDocumentHist(hist);

        verify(jdbcTemplateOdse, times(1)).update(eq(MERGE_NBS_DOC_HIST), any(MapSqlParameterSource.class));
    }
}