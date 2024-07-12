package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.phc.CTContactSummaryDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PublicHealthCaseStoredProcRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomRepositoryImplTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository;

    @InjectMocks
    private CustomRepositoryImpl customRepositoryImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customRepositoryImpl.entityManager = entityManager;
    }
    @Test
    void getLdfCollection_shouldReturnListOfStateDefinedFieldDataDto_whenResultsAreNotEmpty() {
        // Arrange
        Long busObjectUid = 1L;
        String conditionCode = "A01";
        String theQuery = "SELECT * FROM table WHERE businessObjUid = :businessObjUid AND conditionCd = :conditionCd";
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1L, "TestObject", "2021-01-01 10:00:00", 1L, "2021-01-02 10:00:00", "Value", 1});
        when(entityManager.createNativeQuery(theQuery)).thenReturn(mockQuery);
        when(mockQuery.setParameter("businessObjUid", busObjectUid)).thenReturn(mockQuery);
        when(mockQuery.setParameter("conditionCd", conditionCode)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        List<StateDefinedFieldDataDto> result = customRepositoryImpl.getLdfCollection(busObjectUid, conditionCode, theQuery);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        StateDefinedFieldDataDto dto = result.get(0);
        assertEquals(1L, dto.getLdfUid());
        assertEquals("TestObject", dto.getBusinessObjNm());
        assertEquals(Timestamp.valueOf("2021-01-01 10:00:00"), dto.getAddTime());
        assertEquals(1L, dto.getBusinessObjUid());
        assertEquals(Timestamp.valueOf("2021-01-02 10:00:00"), dto.getLastChgTime());
        assertEquals("Value", dto.getLdfValue());
        assertEquals(1, dto.getVersionCtrlNbr());

        verify(entityManager, times(1)).createNativeQuery(theQuery);
        verify(mockQuery, times(1)).setParameter("businessObjUid", busObjectUid);
        verify(mockQuery, times(1)).setParameter("conditionCd", conditionCode);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getLdfCollection_shouldReturnEmptyList_whenResultsAreEmpty() {
        // Arrange
        Long busObjectUid = 1L;
        String conditionCode = "A01";
        String theQuery = "SELECT * FROM table WHERE businessObjUid = :businessObjUid AND conditionCd = :conditionCd";
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = new ArrayList<>();
        when(entityManager.createNativeQuery(theQuery)).thenReturn(mockQuery);
        when(mockQuery.setParameter("businessObjUid", busObjectUid)).thenReturn(mockQuery);
        when(mockQuery.setParameter("conditionCd", conditionCode)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        List<StateDefinedFieldDataDto> result = customRepositoryImpl.getLdfCollection(busObjectUid, conditionCode, theQuery);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).createNativeQuery(theQuery);
        verify(mockQuery, times(1)).setParameter("businessObjUid", busObjectUid);
        verify(mockQuery, times(1)).setParameter("conditionCd", conditionCode);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getAssociatedDocumentList_shouldReturnMapOfDocuments_whenResultsAreNotEmpty() {
        // Arrange
        Long uid = 1L;
        String targetClassCd = "TARGET";
        String sourceClassCd = "SOURCE";
        String theQuery = "SELECT * FROM documents WHERE TargetActUid = :TargetActUid AND SourceClassCd = :SourceClassCd AND TargetClassCd = :TargetClassCd";
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = List.of(
                new Object[]{"doc1", "123"},
                new Object[]{"doc2", "456"}
        );
        when(entityManager.createNativeQuery(theQuery)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", uid)).thenReturn(mockQuery);
        when(mockQuery.setParameter("SourceClassCd", sourceClassCd)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetClassCd", targetClassCd)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.getAssociatedDocumentList(uid, targetClassCd, sourceClassCd, theQuery);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(123L, result.get("doc1"));
        assertEquals(456L, result.get("doc2"));

        verify(entityManager, times(1)).createNativeQuery(theQuery);
        verify(mockQuery, times(1)).setParameter("TargetActUid", uid);
        verify(mockQuery, times(1)).setParameter("SourceClassCd", sourceClassCd);
        verify(mockQuery, times(1)).setParameter("TargetClassCd", targetClassCd);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getAssociatedDocumentList_shouldReturnEmptyMap_whenResultsAreEmpty() {
        // Arrange
        Long uid = 1L;
        String targetClassCd = "TARGET";
        String sourceClassCd = "SOURCE";
        String theQuery = "SELECT * FROM documents WHERE TargetActUid = :TargetActUid AND SourceClassCd = :SourceClassCd AND TargetClassCd = :TargetClassCd";
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = List.of();
        when(entityManager.createNativeQuery(theQuery)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", uid)).thenReturn(mockQuery);
        when(mockQuery.setParameter("SourceClassCd", sourceClassCd)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetClassCd", targetClassCd)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.getAssociatedDocumentList(uid, targetClassCd, sourceClassCd, theQuery);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).createNativeQuery(theQuery);
        verify(mockQuery, times(1)).setParameter("TargetActUid", uid);
        verify(mockQuery, times(1)).setParameter("SourceClassCd", sourceClassCd);
        verify(mockQuery, times(1)).setParameter("TargetClassCd", targetClassCd);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getEDXEventProcessMapByCaseId_shouldReturnMapOfEDXEventProcessDto_whenResultsAreNotEmpty() {
        // Arrange
        Long publicHealthCaseUid = 1L;
        String docQuery = " SELECT"
                + " edx_event_process_uid  \"eDXEventProcessUid\", "
                + " nbs_document_uid  \"nbsDocumentUid\", "
                + " nbs_event_uid  \"nbsEventUid\", "
                + " source_event_id \"sourceEventId\", "
                + " doc_event_type_cd \"docEventTypeCd\", "
                + " edx_event_process.add_user_id \"addUserId\", " + " edx_event_process.add_time \"addTime\", "
                + " parsed_ind \"parsedInd\" "
                + " FROM edx_event_process, act_relationship "
                + " where edx_event_process.nbs_event_uid=act_relationship.source_act_uid "
                + " and act_relationship.target_act_uid = :TargetActUid order by nbs_document_uid";

        Query mockQuery = mock(Query.class);


        var objList = new Object[]{1L, 2L, 3L, 4L, "TYPE1", 5L, "2021-01-01 10:00:00", "YES"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);

        when(entityManager.createNativeQuery(docQuery)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", publicHealthCaseUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<String, EDXEventProcessDto> result = customRepositoryImpl.getEDXEventProcessMapByCaseId(publicHealthCaseUid);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        EDXEventProcessDto dto = result.get("4");
        assertNotNull(dto);
        assertEquals(1L, dto.getEDXEventProcessUid());
        assertEquals(2L, dto.getNbsDocumentUid());
        assertEquals(3L, dto.getNbsEventUid());
        assertEquals("4", dto.getSourceEventId());
        assertEquals("TYPE1", dto.getDocEventTypeCd());
        assertEquals(5L, dto.getAddUserId());
        assertEquals(Timestamp.valueOf("2021-01-01 10:00:00"), dto.getAddTime());
        assertEquals("YES", dto.getParsedInd());

        verify(entityManager, times(1)).createNativeQuery(docQuery);
        verify(mockQuery, times(1)).setParameter("TargetActUid", publicHealthCaseUid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getEDXEventProcessMapByCaseId_shouldReturnEmptyMap_whenResultsAreEmpty() {
        // Arrange
        Long publicHealthCaseUid = 1L;
        String docQuery = " SELECT"
                + " edx_event_process_uid  \"eDXEventProcessUid\", "
                + " nbs_document_uid  \"nbsDocumentUid\", "
                + " nbs_event_uid  \"nbsEventUid\", "
                + " source_event_id \"sourceEventId\", "
                + " doc_event_type_cd \"docEventTypeCd\", "
                + " edx_event_process.add_user_id \"addUserId\", " + " edx_event_process.add_time \"addTime\", "
                + " parsed_ind \"parsedInd\" "
                + " FROM edx_event_process, act_relationship "
                + " where edx_event_process.nbs_event_uid=act_relationship.source_act_uid "
                + " and act_relationship.target_act_uid = :TargetActUid order by nbs_document_uid";

        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = List.of();
        when(entityManager.createNativeQuery(docQuery)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", publicHealthCaseUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<String, EDXEventProcessDto> result = customRepositoryImpl.getEDXEventProcessMapByCaseId(publicHealthCaseUid);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).createNativeQuery(docQuery);
        verify(mockQuery, times(1)).setParameter("TargetActUid", publicHealthCaseUid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void retrieveDocumentSummaryVOForInv_shouldReturnMapOfDocumentSummaryContainer_whenResultsAreNotEmpty() {
        Long publicHealthUID = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{1L, 2L, 3L, "2021-01-01 10:00:00", "2021-01-01 10:00:00", "2021-01-01 10:00:00", "LocalId", "Description"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("PhcUid", publicHealthUID)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.retrieveDocumentSummaryVOForInv(publicHealthUID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        DocumentSummaryContainer container = (DocumentSummaryContainer) result.get(2L);
        assertNotNull(container);
        assertEquals(2L, container.getNbsDocumentUid());
        assertEquals("3", container.getDocType());

        verify(mockQuery, times(1)).setParameter("PhcUid", publicHealthUID);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void retrieveDocumentSummaryVOForInv_shouldReturnEmptyMap_whenResultsAreEmpty() {
        // Arrange
        Long publicHealthUID = 1L;
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = List.of();
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("PhcUid", publicHealthUID)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.retrieveDocumentSummaryVOForInv(publicHealthUID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(mockQuery, times(1)).setParameter("PhcUid", publicHealthUID);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void retrieveNotificationSummaryListForInvestigation_shouldReturnListOfNotificationSummaryContainer_whenResultsAreNotEmpty() {
        // Arrange
        Long publicHealthUID = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{1L, "NotifCd", "2021-01-01 10:00:00.0", "2021-01-01 10:00:00.0",
                "2021-01-01 10:00:00.0", "Cd", "JurisdictionCd", 2L, "CaseClassCd",
                "AutoResendInd", "CaseClassCd", "LocalId", "Txt", "RecordStatusCd",
                "IsHistory", "NndInd", "Recipient"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("PhcUid", publicHealthUID)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        List<NotificationSummaryContainer> result = customRepositoryImpl.retrieveNotificationSummaryListForInvestigation(publicHealthUID, any());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        NotificationSummaryContainer container = result.get(0);
        assertNotNull(container);
        assertEquals(1L, container.getNotificationUid());
        assertEquals("NotifCd", container.getCdNotif());
        assertEquals("Cd", container.getCd());


        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("PhcUid", publicHealthUID);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void retrieveNotificationSummaryListForInvestigation_shouldReturnEmptyList_whenResultsAreEmpty() {
        // Arrange
        Long publicHealthUID = 1L;
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = new ArrayList<>();
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("PhcUid", publicHealthUID)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        List<NotificationSummaryContainer> result = customRepositoryImpl.retrieveNotificationSummaryListForInvestigation(publicHealthUID, any());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("PhcUid", publicHealthUID);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void retrieveTreatmentSummaryVOForInv_shouldReturnMapOfTreatmentContainer_whenResultsAreNotEmpty() {
        // Arrange
        Long publicHealthUID = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{1L, 2L, 3L, "2021-01-01 10:00:00.0", "2021-01-01 10:00:00.0",
                "2021-01-01 10:00:00.0", "LocalId"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("PhcUid", publicHealthUID)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.retrieveTreatmentSummaryVOForInv(publicHealthUID, any());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        TreatmentContainer container = (TreatmentContainer) result.get(2L);
        assertNotNull(container);
        assertEquals(1L, container.getPhcUid());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("PhcUid", publicHealthUID);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void retrieveTreatmentSummaryVOForInv_shouldReturnEmptyMap_whenResultsAreEmpty() {
        // Arrange
        Long publicHealthUID = 1L;
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = new ArrayList<>();
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("PhcUid", publicHealthUID)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.retrieveTreatmentSummaryVOForInv(publicHealthUID, any());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("PhcUid", publicHealthUID);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getAssociatedInvList_shouldReturnMap_whenResultsAreNotEmptyAndSourceClassCdIsObs() {
        // Arrange
        Long uid = 1L;
        String sourceClassCd = NEDSSConstant.CLASS_CD_OBS;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"Item1", "Item2", "Item3"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("ClassCd", sourceClassCd)).thenReturn(mockQuery);
        when(mockQuery.setParameter("ActUid", uid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.getAssociatedInvList(uid, sourceClassCd, any());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Item2", result.get("Item1"));
        assertEquals("Item3", result.get("Item1-Item2"));

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("ClassCd", sourceClassCd);
        verify(mockQuery, times(1)).setParameter("ActUid", uid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getAssociatedInvList_shouldReturnMap_whenResultsAreNotEmptyAndSourceClassCdIsNotObs() {
        // Arrange
        Long uid = 1L;
        String sourceClassCd = "OtherClassCd";
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"Item1", "Item2", null};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("ClassCd", sourceClassCd)).thenReturn(mockQuery);
        when(mockQuery.setParameter("ActUid", uid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.getAssociatedInvList(uid, sourceClassCd, any());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Item2", result.get("Item1"));

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("ClassCd", sourceClassCd);
        verify(mockQuery, times(1)).setParameter("ActUid", uid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getAssociatedInvList_shouldReturnEmptyMap_whenResultsAreEmpty() {
        // Arrange
        Long uid = 1L;
        String sourceClassCd = NEDSSConstant.CLASS_CD_OBS;
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = new ArrayList<>();
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("ClassCd", sourceClassCd)).thenReturn(mockQuery);
        when(mockQuery.setParameter("ActUid", uid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.getAssociatedInvList(uid, sourceClassCd, any());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("ClassCd", sourceClassCd);
        verify(mockQuery, times(1)).setParameter("ActUid", uid);
        verify(mockQuery, times(1)).getResultList();
    }


    @Test
    void getSusceptibilityResultedTestSummary_shouldReturnListOfResultedTestSummaryContainer_whenResultsAreNotEmpty() {
        // Arrange
        String typeCode = "TypeCodeExample";
        Long observationUid = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{1L, "UserDefined1", 2L, "LocalId", "Test", "TestCd", "SystemCd",
                "ResultValue", "OrganismName", "Compare", "HighRange", "LowRange",
                "Seperator", "123", "456", "1", "2", "Units"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("TypeCode", typeCode)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", observationUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<ResultedTestSummaryContainer> result = customRepositoryImpl.getSusceptibilityResultedTestSummary(typeCode, observationUid);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ResultedTestSummaryContainer container = result.get(0);
        assertNotNull(container);
        assertEquals(1L, container.getObservationUid());
        assertEquals("UserDefined1", container.getCtrlCdUserDefined1());
        assertEquals(2L, container.getSourceActUid());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("TypeCode", typeCode);
        verify(mockQuery, times(1)).setParameter("TargetActUid", observationUid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getSusceptibilityResultedTestSummary_shouldReturnEmptyList_whenResultsAreEmpty() {
        // Arrange
        String typeCode = "TypeCodeExample";
        Long observationUid = 1L;
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = new ArrayList<>();
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("TypeCode", typeCode)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", observationUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<ResultedTestSummaryContainer> result = customRepositoryImpl.getSusceptibilityResultedTestSummary(typeCode, observationUid);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("TypeCode", typeCode);
        verify(mockQuery, times(1)).setParameter("TargetActUid", observationUid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getSusceptibilityUidSummary_shouldReturnListOfUidSummaryContainer_whenResultsAreNotEmpty() {
        // Arrange
        ResultedTestSummaryContainer rvo = new ResultedTestSummaryContainer();
        LabReportSummaryContainer labRepEvent = new LabReportSummaryContainer();
        LabReportSummaryContainer labRepSumm = new LabReportSummaryContainer();
        String typeCode = "TypeCodeExample";
        Long observationUid = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{1L};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("TypeCode", typeCode)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", observationUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<UidSummaryContainer> result = customRepositoryImpl.getSusceptibilityUidSummary(rvo, labRepEvent, labRepSumm, typeCode, observationUid);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        UidSummaryContainer uid = result.get(0);
        assertNotNull(uid);
        assertEquals(1L, uid.getUid());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("TypeCode", typeCode);
        verify(mockQuery, times(1)).setParameter("TargetActUid", observationUid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getSusceptibilityUidSummary_shouldReturnEmptyList_whenResultsAreEmpty() {
        // Arrange
        ResultedTestSummaryContainer rvo = new ResultedTestSummaryContainer();
        LabReportSummaryContainer labRepEvent = new LabReportSummaryContainer();
        LabReportSummaryContainer labRepSumm = new LabReportSummaryContainer();
        String typeCode = "TypeCodeExample";
        Long observationUid = 1L;
        Query mockQuery = mock(Query.class);
        List<Object[]> mockResults = new ArrayList<>();
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("TypeCode", typeCode)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", observationUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<UidSummaryContainer> result = customRepositoryImpl.getSusceptibilityUidSummary(rvo, labRepEvent, labRepSumm, typeCode, observationUid);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("TypeCode", typeCode);
        verify(mockQuery, times(1)).setParameter("TargetActUid", observationUid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getTestAndSusceptibilities_shouldReturnListOfResultedTestSummaryContainer_whenResultsAreNotEmpty() {
        // Arrange
        String typeCode = "TypeCodeExample";
        Long observationUid = 1L;
        LabReportSummaryContainer labRepEvent = new LabReportSummaryContainer();
        LabReportSummaryContainer labRepSumm = new LabReportSummaryContainer();
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{1L, "UserDefined1", 2L, "LocalId", "Test", "SystemCd", "StatusCd", "ResultValue",
                "OrganismName", "OrganismCodeSystemCd", "HighRange", "LowRange", "Compare",
                "Seperator", "123", "456", "1", "2", "Units", "TextResult"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("TypeCode", typeCode)).thenReturn(mockQuery);
        when(mockQuery.setParameter("TargetActUid", observationUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<ResultedTestSummaryContainer> result = customRepositoryImpl.getTestAndSusceptibilities(typeCode, observationUid, labRepEvent, labRepSumm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ResultedTestSummaryContainer container = result.get(0);
        assertNotNull(container);
        assertEquals(1L, container.getObservationUid());
        assertEquals("UserDefined1", container.getCtrlCdUserDefined1());
        assertEquals(2L, container.getSourceActUid());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("TypeCode", typeCode);
        verify(mockQuery, times(1)).setParameter("TargetActUid", observationUid);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getOrderingPersonPhone_shouldSetProviderPhoneAndExtension_whenResultsAreNotEmpty() {
        // Arrange
        Long organizationUid = 1L;
        ProviderDataForPrintContainer providerDataForPrintVO = new ProviderDataForPrintContainer();
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"123-456-7890", "123"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(1)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ProviderDataForPrintContainer result = customRepositoryImpl.getOrderingPersonPhone(providerDataForPrintVO, organizationUid);

        // Assert
        assertNotNull(result);
        assertEquals("123-456-7890", result.getProviderPhone());
        assertEquals("123", result.getProviderPhoneExtension());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setMaxResults(1);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getOrderingPersonAddress_shouldSetProviderAddress_whenResultsAreNotEmpty() {
        // Arrange
        Long organizationUid = 1L;
        ProviderDataForPrintContainer providerDataForPrintVO = new ProviderDataForPrintContainer();
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"123 Main St", "Springfield", "IL", "62704"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(1)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ProviderDataForPrintContainer result = customRepositoryImpl.getOrderingPersonAddress(providerDataForPrintVO, organizationUid);

        // Assert
        assertNotNull(result);
        assertEquals("123 Main St", result.getProviderStreetAddress1());
        assertEquals("Springfield", result.getProviderCity());
        assertEquals("IL", result.getProviderState());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setMaxResults(1);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getOrderingFacilityPhone_shouldSetFacilityPhoneAndExtension_whenResultsAreNotEmpty() {
        // Arrange
        Long organizationUid = 1L;
        ProviderDataForPrintContainer providerDataForPrintVO = new ProviderDataForPrintContainer();
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"123-456-7890", "123"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(1)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ProviderDataForPrintContainer result = customRepositoryImpl.getOrderingFacilityPhone(providerDataForPrintVO, organizationUid);

        // Assert
        assertNotNull(result);
        assertEquals("123-456-7890", result.getFacilityPhone());
        assertEquals("123", result.getFacilityPhoneExtension());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setMaxResults(1);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getOrderingFacilityAddress_shouldSetFacilityAddress_whenResultsAreNotEmpty() {
        // Arrange
        Long organizationUid = 1L;
        ProviderDataForPrintContainer providerDataForPrintVO = new ProviderDataForPrintContainer();
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"123 Main St", "Suite 100", "Springfield", "IL", "62704"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(1)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ProviderDataForPrintContainer result = customRepositoryImpl.getOrderingFacilityAddress(providerDataForPrintVO, organizationUid);

        // Assert
        assertNotNull(result);
        assertEquals("123 Main St", result.getFacilityAddress1());
        assertEquals("Suite 100", result.getFacilityAddress2());
        assertEquals("Springfield", result.getFacilityCity());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setMaxResults(1);
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getSpecimanSource_shouldReturnSpecimenSource_whenResultsAreNotEmpty() {
        // Arrange
        Long materialUid = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"SpecimenSource"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        String result = customRepositoryImpl.getSpecimanSource(materialUid);

        // Assert
        assertNotNull(result);
        assertEquals("SpecimenSource", result);

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).getResultList();
    }


    @Test
    void getReportingFacilityName_shouldReturnFacilityName_whenResultsAreNotEmpty() {
        // Arrange
        Long organizationUid = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"FacilityName"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        String result = customRepositoryImpl.getReportingFacilityName(organizationUid);

        // Assert
        assertNotNull(result);
        assertEquals("FacilityName", result);

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getActIdDetails_shouldReturnListOfActIdDetails_whenResultsAreNotEmpty() {
        // Arrange
        Long observationUID = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"RootExtTxt"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<Object> result = customRepositoryImpl.getActIdDetails(observationUID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("RootExtTxt", result.get(0));

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getProviderInfo_shouldReturnListOfProviderInfo_whenResultsAreNotEmpty() {
        // Arrange
        Long observationUID = 1L;
        String partTypeCd = "PartTypeCdExample";
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"ProviderUid", "LastNm", "Degree", "FirstNm", "Prefix", 1L};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<Object> result = customRepositoryImpl.getProviderInfo(observationUID, partTypeCd);

        // Assert
        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals("ProviderUid", result.get(0));
        assertEquals("LastNm", result.get(1));
        assertEquals("Degree", result.get(2));

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getPatientPersonInfo_shouldReturnListOfPatientPersonInfo_whenResultsAreNotEmpty() {
        // Arrange
        Long observationUID = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"LastNm", "FirstNm", "CtrlCdDisplayForm", 123L};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<Object> result = customRepositoryImpl.getPatientPersonInfo(observationUID);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("LastNm", result.get(0));
        assertEquals("FirstNm", result.get(1));
        assertEquals("CtrlCdDisplayForm", result.get(2));
        assertEquals(123L, result.get(3));

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).getResultList();
    }


    @Test
    void getLabParticipations_shouldReturnMapOfLabParticipations_whenResultsAreNotEmpty() {
        // Arrange
        Long observationUID = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"classCd", "typeCd", 123L};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Map<Object, Object> result = customRepositoryImpl.getLabParticipations(observationUID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123L, result.get("typeCd"));

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).getResultList();
    }


    @Test
    void getContactByPatientInfo_shouldReturnCollectionOfCTContactSummaryDto_whenResultsAreNotEmpty() {
        // Arrange
        String queryString = "SELECT * FROM contacts WHERE patient_id = 1";
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{"2021-01-01 10:00:00", 1L, "LocalId", 2L, 3L, "PriorityCd", "DispositionCd", "ProgAreaCd", 4L, "ContactReferralBasisCd", 5L, 6L, "ContactProcessingDecision", "SourceDispositionCd", "SourceConditionCd", "SourceCurrentSexCd", "1",1L, "2021-01-02 10:00:00", "2021-01-03 10:00:00", "1", 8L, "1", 9L};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        Collection<CTContactSummaryDto> result = customRepositoryImpl.getContactByPatientInfo(queryString);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CTContactSummaryDto contact = result.iterator().next();
        assertNotNull(contact);
        assertEquals(Timestamp.valueOf("2021-01-01 10:00:00"), contact.getNamedOnDate());
        assertEquals(1L, contact.getCtContactUid());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).getResultList();
    }

    @Test
    void getNbsDocument_shouldReturnNbsDocumentContainer_whenResultsAreNotEmpty() throws DataProcessingException {
        // Arrange
        Long nbsUid = 1L;
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{1L, "LocalId", "DocTypeCd", "JurisdictionCd", "ProgAreaCd", "DocStatusCd", "2021-01-01 10:00:00", "Txt", 1, "DocPurposeCd", "CdDescTxt", "SendingFacilityNm", 1L, "1", "1", "ProcessingDecisiontxt", 1, "Cd", "PayLoadTxt", "PhdcDocDerivedTxt", "PayloadViewIndCd", 1L, "2021-01-01 10:00:00", 1L, "1", 1L, 1L, "DocEventTypeCd", "2021-01-01 10:00:00", 1L, 2L};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("NbsUid", nbsUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);
        when(publicHealthCaseStoredProcRepository.getEDXEventProcessMap(any())).thenReturn(new HashMap<>());

        // Act
        NbsDocumentContainer result = customRepositoryImpl.getNbsDocument(nbsUid);

        // Assert
        assertNotNull(result);
        NBSDocumentDto container = result.getNbsDocumentDT();
        assertNotNull(container);
        assertEquals(1L, container.getNbsDocumentUid());
        assertEquals("LocalId", container.getLocalId());
        assertEquals("DocTypeCd", container.getDocTypeCd());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("NbsUid", nbsUid);
        verify(mockQuery, times(1)).getResultList();
        verify(publicHealthCaseStoredProcRepository, times(1)).getEDXEventProcessMap(any());
    }

    @Test
    void getInvListForCoInfectionId_shouldReturnListOfCoinfectionSummaryContainer_whenResultsAreNotEmpty()  {
        // Arrange
        Long mprUid = 1L;
        String coInfectionId = "CoInfectionIdExample";
        Query mockQuery = mock(Query.class);
        var objList = new Object[]{1L, "ConditionCd"};
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(objList);
        when(entityManager.createNativeQuery(any())).thenReturn(mockQuery);
        when(mockQuery.setParameter("CoInfect", coInfectionId)).thenReturn(mockQuery);
        when(mockQuery.setParameter("PersonUid", mprUid)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockResults);

        // Act
        ArrayList<Object> result = customRepositoryImpl.getInvListForCoInfectionId(mprUid, coInfectionId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        CoinfectionSummaryContainer container = (CoinfectionSummaryContainer) result.get(0);
        assertNotNull(container);
        assertEquals(1L, container.getPublicHealthCaseUid());
        assertEquals("ConditionCd", container.getConditionCd());

        verify(entityManager, times(1)).createNativeQuery(any());
        verify(mockQuery, times(1)).setParameter("CoInfect", coInfectionId);
        verify(mockQuery, times(1)).setParameter("PersonUid", mprUid);
        verify(mockQuery, times(1)).getResultList();
    }



}
