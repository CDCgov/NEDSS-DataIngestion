package gov.cdc.dataprocessing.service.implementation.act;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActRelationshipServiceTest {
    @Mock
    private ActRelationshipRepository actRelationshipRepository;
    @Mock
    private DataModifierReposJdbc dataModifierReposJdbc;
    @InjectMocks
    private ActRelationshipService actRelationshipService;
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
        Mockito.reset(dataModifierReposJdbc, actRelationshipRepository, authUtil);
    }

    @Test
    void loadActRelationshipBySrcIdAndTypeCode_Success() {
        long uid = 10L;
        String type = "type";

        var actCol =  new ArrayList<ActRelationship>();
        var act = new ActRelationship();
        actCol.add(act);
        when(actRelationshipRepository.loadActRelationshipBySrcIdAndTypeCode(10L, type))
                .thenReturn(Optional.of(actCol));

        var test = actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(uid, type);

        assertNotNull(test);
        assertEquals(1, test.size());
    }

    @Test
    void loadActRelationshipBySrcIdAndTypeCode_Success_2() {
        long uid = 10L;
        String type = "type";


        when(actRelationshipRepository.loadActRelationshipBySrcIdAndTypeCode(10L, type))
                .thenReturn(Optional.empty());

        var test = actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(uid, type);

        assertNotNull(test);
    }

    @Test
    void saveActRelationship_Test_1() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDirty(true);

        actRelationshipService.saveActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(0)).save(any());
    }

    @Test
    void saveActRelationship_Test_2() throws DataProcessingException {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();

        actRelationshipService.saveActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(0)).save(any());
    }



    @Test
    void saveActRelationship_Success_New() throws DataProcessingException {
        var dto = new ActRelationshipDto();
        dto.setAddReasonCd("TEST");
        dto.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setAddUserId(1L);
        dto.setDurationAmt("5");
        dto.setDurationUnitCd("days");
        dto.setFromTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setLastChgReasonCd("Updated");
        dto.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setLastChgUserId(2L);
        dto.setRecordStatusCd("Active");
        dto.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setSequenceNbr(1);
        dto.setStatusCd("Completed");
        dto.setStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setToTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setUserAffiliationTxt("Affiliation");
        dto.setSourceActUid(100L);
        dto.setTypeDescTxt("Description");
        dto.setTargetActUid(101L);
        dto.setSourceClassCd("ClassA");
        dto.setTargetClassCd("ClassB");
        dto.setTypeCd("Type1");
        dto.setItNew(true);

        actRelationshipService.saveActRelationship(dto);

        verify(actRelationshipRepository, times(1)).save(any());

    }

    @Test
    void saveActRelationship_Success_Dirty() throws DataProcessingException {
        var dto = new ActRelationshipDto();
        dto.setAddReasonCd("TEST");
        dto.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setAddUserId(1L);
        dto.setDurationAmt("5");
        dto.setDurationUnitCd("days");
        dto.setFromTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setLastChgReasonCd("Updated");
        dto.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setLastChgUserId(2L);
        dto.setRecordStatusCd("Active");
        dto.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setSequenceNbr(1);
        dto.setStatusCd("Completed");
        dto.setStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setToTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setUserAffiliationTxt("Affiliation");
        dto.setSourceActUid(100L);
        dto.setTypeDescTxt("Description");
        dto.setTargetActUid(101L);
        dto.setSourceClassCd("ClassA");
        dto.setTargetClassCd("ClassB");
        dto.setTypeCd("Type1");
        dto.setItNew(false);
        dto.setItDirty(true);

        actRelationshipService.saveActRelationship(dto);

        verify(actRelationshipRepository, times(1)).save(any());

    }

    @Test
    void saveActRelationship_Success_Delete() throws DataProcessingException {
        var dto = new ActRelationshipDto();
        dto.setAddReasonCd("TEST");
        dto.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setAddUserId(1L);
        dto.setDurationAmt("5");
        dto.setDurationUnitCd("days");
        dto.setFromTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setLastChgReasonCd("Updated");
        dto.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setLastChgUserId(2L);
        dto.setRecordStatusCd("Active");
        dto.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setSequenceNbr(1);
        dto.setStatusCd("Completed");
        dto.setStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setToTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        dto.setUserAffiliationTxt("Affiliation");
        dto.setSourceActUid(100L);
        dto.setTypeDescTxt("Description");
        dto.setTargetActUid(101L);
        dto.setSourceClassCd("ClassA");
        dto.setTargetClassCd("ClassB");
        dto.setTypeCd("Type1");
        dto.setItNew(false);
        dto.setItDirty(false);
        dto.setItDelete(true);

        actRelationshipService.saveActRelationship(dto);

        verify(dataModifierReposJdbc, times(1)).deleteActRelationshipByPk(any(), any(), any());

    }

    @Test
    void saveActRelationship_Exception()  {
        ActRelationshipDto dto = null;



        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            actRelationshipService.saveActRelationship(dto);
        });

        assertEquals("Act Relationship is null", thrown.getMessage());

    }
}
