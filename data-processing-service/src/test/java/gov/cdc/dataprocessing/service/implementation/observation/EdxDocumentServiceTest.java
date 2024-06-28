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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EdxDocumentServiceTest {
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
        edxDto.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        edxDto.setAddTime(TimeStampUtil.getCurrentTimeStamp());
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

}
