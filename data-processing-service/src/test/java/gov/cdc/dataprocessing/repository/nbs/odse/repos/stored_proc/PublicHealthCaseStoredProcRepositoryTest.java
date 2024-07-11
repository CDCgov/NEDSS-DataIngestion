package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
class PublicHealthCaseStoredProcRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private PublicHealthCaseStoredProcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAssociatedPublicHealthCaseForMprForCondCd() throws DataProcessingException {
        Long mprUid = 1L;
        String conditionCode = "testCondition";

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Long.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(storedProcedureQuery);

        List<Object[]> mockResultList = new ArrayList<>();
        mockResultList.add(new Object[]{
                1L, "durationAmt", "durationUnitCd", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), "addReasonCd", new Timestamp(System.currentTimeMillis()),
                2L, "caseClassCd", "cd", "cdDescTxt", "cdSystemCd", "cdSystemDescTxt", "confidentialityCd",
                "confidentialityDescTxt", "detectionMethodCd", "detectionMethodDescTxt", "diseaseImportedCd",
                "diseaseImportedDescTxt", "effectiveDurationAmt", "effectiveDurationUnitCd", new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), (short) 3, "investigationStatusCd", "jurisdictionCd",
                "lastChgReasonCd", new Timestamp(System.currentTimeMillis()), 3L, "localId", "mmwrWeek",
                "mmwrYear", "outbreakName", new Timestamp(System.currentTimeMillis()), "outbreakInd",
                new Timestamp(System.currentTimeMillis()), "outcomeCd", 4L, "progAreaCd", "recordStatusCd",
                new Timestamp(System.currentTimeMillis()), 5, "rptCntyCd", 'A', new Timestamp(System.currentTimeMillis()),
                "transmissionModeCd", "transmissionModeDescTxt", "txt", "userAffiliationTxt", "patAgeAtOnset",
                "patAgeAtOnsetUnitCd", new Timestamp(System.currentTimeMillis()), "rptSourceCd", "rptSourceCdDescTxt",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                6L, 'B', (short) 7, 'C', new Timestamp(System.currentTimeMillis()), "hospitalizedIndCd",
                new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                new BigDecimal("123.45"), "pregnantIndCd", "dayCareIndCd", "foodHandlerIndCd", "importedCountryCd",
                "importedStateCd", "importedCityDescTxt", "importedCountyCd", new Timestamp(System.currentTimeMillis()),
                "countIntervalCd", "priorityCd", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
                "contactInvStatus", "contactInvTxt", "referralBasisCd", "currProcessStateCd", "invPriorityCd",
                "coinfectionId", new Timestamp(System.currentTimeMillis()), "confirmationMethodCd", new Timestamp(System.currentTimeMillis())
        });

        when(storedProcedureQuery.getResultList()).thenReturn(mockResultList);

        Collection<PublicHealthCaseDto> result = repository.associatedPublicHealthCaseForMprForCondCd(mprUid, conditionCode);

        assertEquals(1, result.size());
        PublicHealthCaseDto dto = result.iterator().next();
        assertEquals(1L, dto.getPublicHealthCaseUid());
        assertEquals("durationAmt", dto.getActivityDurationAmt());
        // Add more assertions as needed
    }

    @Test
    void testAssociatedPublicHealthCaseForMprForCondCd_ThrowsException() {
        Long mprUid = 1L;
        String conditionCode = "testCondition";

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.associatedPublicHealthCaseForMprForCondCd(mprUid, conditionCode));
    }

    @Test
    void testGetEDXEventProcessMap() throws DataProcessingException {
        Long nbsDocumentUid = 1L;

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Long.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(storedProcedureQuery);

        List<Object[]> mockResultList = new ArrayList<>();
        mockResultList.add(new Object[]{
                0L, 1L, 2L, 3L, "sourceEventId", "docEventTypeCd", 4L, new Timestamp(System.currentTimeMillis()), "parsedInd"
        });

        when(storedProcedureQuery.getResultList()).thenReturn(mockResultList);

        Map<String, EDXEventProcessDto> result = repository.getEDXEventProcessMap(nbsDocumentUid);

        assertEquals(1, result.size());
        EDXEventProcessDto dto = result.get("sourceEventId");
        assertEquals(1L, dto.getEDXEventProcessUid());
        assertEquals(2L, dto.getNbsDocumentUid());
        assertEquals(3L, dto.getNbsEventUid());
        // Add more assertions as needed
    }

    @Test
    void testGetEDXEventProcessMap_ThrowsException() {
        Long nbsDocumentUid = 1L;

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.getEDXEventProcessMap(nbsDocumentUid));
    }
}
