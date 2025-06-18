package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxDocumentRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class EdxDocumentServiceTest {
    @Mock
    private EdxDocumentRepository edxDocumentRepository;
    @InjectMocks
    private EdxDocumentService edxDocumentService;
    @Mock
    AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(edxDocumentRepository, authUtil);
    }

    @Test
    void selectEdxDocumentCollectionByActUid_Success() {
        long uid = 10L;
        var edxCol = new ArrayList<EdxDocument>();
        var edx = new EdxDocument();
        edxCol.add(edx);
        when(edxDocumentRepository.selectEdxDocumentCollectionByActUid(10L)).thenReturn(Optional.of(edxCol));

        var test = edxDocumentService.selectEdxDocumentCollectionByActUid(uid);

        assertEquals(1, test.size());

    }

    @Test
    void saveEdxDocument_Success() {
        var edxDto = new EDXDocumentDto();
        edxDto.setEDXDocumentUid(1L);
        edxDto.setActUid(1L);
        edxDto.setPayload("TEST");
        edxDto.setRecordStatusCd("A");
        edxDto.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        edxDto.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        edxDto.setDocTypeCd("TEST");
        edxDto.setNbsDocumentMetadataUid(1L);
        edxDto.setOriginalPayload("TEST");
        edxDto.setOriginalDocTypeCd("TEST");
        edxDto.setEdxDocumentParentUid(1L);

        var edx = new EdxDocument(edxDto);
        edx.setId(1L);
        when(edxDocumentRepository.save(any(EdxDocument.class))).thenReturn(edx);

        edxDocumentService.saveEdxDocument(edxDto);

        verify(edxDocumentRepository, times(1)).save(any(EdxDocument.class));
    }

    @Test
    void selectEdxDocumentCollectionByActUid_EmptyResult() {
        long uid = 10L;
        when(edxDocumentRepository.selectEdxDocumentCollectionByActUid(10L)).thenReturn(Optional.empty());

        var result = edxDocumentService.selectEdxDocumentCollectionByActUid(uid);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void selectEdxDocumentCollectionByActUid_NullUid() {
        var result = edxDocumentService.selectEdxDocumentCollectionByActUid(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void saveEdxDocument_WithNullFields() {
        var edxDto = new EDXDocumentDto();
        var edx = new EdxDocument(edxDto);
        edx.setId(1L);
        when(edxDocumentRepository.save(any(EdxDocument.class))).thenReturn(edx);

        var result = edxDocumentService.saveEdxDocument(edxDto);

        assertNotNull(result);
        verify(edxDocumentRepository, times(1)).save(any(EdxDocument.class));
    }

    @Test
    void saveEdxDocumentBatch_EmptyList() {
        var result = edxDocumentService.saveEdxDocumentBatch(new ArrayList<>());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void saveEdxDocumentBatch_Success() {
        var dtos = new ArrayList<EDXDocumentDto>();
        var dto = new EDXDocumentDto();
        dto.setEDXDocumentUid(1L);
        dto.setActUid(1L);
        dto.setPayload("TEST");
        dto.setRecordStatusCd("A");
        dto.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setDocTypeCd("TEST");
        dto.setNbsDocumentMetadataUid(1L);
        dto.setOriginalPayload("TEST");
        dto.setOriginalDocTypeCd("TEST");
        dto.setEdxDocumentParentUid(1L);
        dtos.add(dto);

        var edx = new EdxDocument(dto);
        edx.setId(1L);
        when(edxDocumentRepository.saveAll(anyList())).thenReturn(List.of(edx));

        var result = edxDocumentService.saveEdxDocumentBatch(dtos);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(edxDocumentRepository, times(1)).saveAll(anyList());
    }

}
