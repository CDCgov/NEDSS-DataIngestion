package gov.cdc.dataprocessing.service.implementation.answer;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PageContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.NbsActEntityHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.NbsActEntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsAnswerRepository;
import gov.cdc.dataprocessing.service.implementation.action.LabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
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

import java.sql.Time;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AnswerServiceTest {
    @Mock
    private NbsAnswerRepository nbsAnswerRepository;
    @Mock
    private NbsActEntityRepository nbsActEntityRepository;
    @Mock
    private NbsAnswerHistRepository nbsAnswerHistRepository;
    @Mock
    private NbsActEntityHistRepository nbsActEntityHistRepository;
    @InjectMocks
    private AnswerService answerService;
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
        Mockito.reset(nbsAnswerRepository, nbsActEntityRepository, nbsAnswerHistRepository, nbsActEntityHistRepository, authUtil);
    }

    private NbsAnswer buildNbsAnswer(long uid, long nbsUid, int seqNbsNum, int seqNum) {
        var nbs = new NbsAnswer();
        nbs.setNbsAnswerUid(uid);
        nbs.setActUid(uid);
        nbs.setAnswerTxt("TEST");
        nbs.setNbsQuestionUid(nbsUid);
        nbs.setNbsQuestionVersionCtrlNbr(1);
        nbs.setSeqNbr(seqNum);
        nbs.setAnswerGroupSeqNbr(seqNbsNum);
        nbs.setRecordStatusCd("TEST");
        nbs.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbs.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbs.setLastChgUserId(uid);
        return nbs;
    }


    @Test
    void getPageAnswerDTMaps_Success() throws DataProcessingException {
        long uid = 10L;

        // Set up mock answers
        var nbsAnsCol = new ArrayList<NbsAnswer>();
        nbsAnsCol.add(buildNbsAnswer(11L, 1L, 0, 1));
        nbsAnsCol.add(buildNbsAnswer(12L, 1L, 1, 1));
        nbsAnsCol.add(buildNbsAnswer(13L, 1L, 1, 1));

        nbsAnsCol.add(buildNbsAnswer(13L, 1L, -1, 1));

        nbsAnsCol.add(buildNbsAnswer(-1L, -1L, -1, -1));


        when(nbsAnswerRepository.getPageAnswerByActUid(uid)).thenReturn(Optional.of(nbsAnsCol));

        var result = answerService.getPageAnswerDTMaps(uid);

        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    void getNbsAnswerAndAssociation_Success() throws DataProcessingException {
        long uid = 10L;

        // getPageAnswerDTMaps 52
        var nbsAnsCol = new ArrayList<NbsAnswer>();
        nbsAnsCol.add(buildNbsAnswer(11L, 1L, 0, 1));
        nbsAnsCol.add(buildNbsAnswer(12L, 1L, 1, 1));
        nbsAnsCol.add(buildNbsAnswer(13L, 1L, 1, 1));
        nbsAnsCol.add(buildNbsAnswer(13L, 1L, -1, 1));
        nbsAnsCol.add(buildNbsAnswer(-1L, -1L, -1, -1));
        when(nbsAnswerRepository.getPageAnswerByActUid(uid)).thenReturn(Optional.of(nbsAnsCol));


        var actEntityCol = new ArrayList<NbsActEntity>();
        var actEntity = new NbsActEntity();
        actEntity.setNbsActEntityUid(11L);
        actEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        actEntity.setAddUserId(11L);
        actEntity.setEntityUid(11L);
        actEntity.setEntityVersionCtrlNbr(1);
        actEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        actEntity.setLastChgUserId(11L);
        actEntity.setRecordStatusCd("TEST");
        actEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        actEntity.setTypeCd("TEST");
        actEntity.setActUid(11L);
        actEntityCol.add(actEntity);
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(10L)).thenReturn(Optional.of(actEntityCol));

        var test = answerService.getNbsAnswerAndAssociation(uid);

        assertNotNull(test);
        assertEquals(1, test.getPageRepeatingAnswerDTMap().size());
        assertEquals(1, test.getActEntityDTCollection().size());
        assertEquals(2, test.getAnswerDTMap().size());
    }


    @Test
    void insertPageVo_Success_PageConNotNull() throws DataProcessingException {
        PageContainer pageContainer = new PageContainer();
        var answerMap = new HashMap<Object, NbsAnswerDto>();
        var ansDto = new NbsAnswerDto(buildNbsAnswer(11L, 11L, 1, 1));
        ansDto.setItNew(true);
        answerMap.put("1", ansDto);
        ansDto = new NbsAnswerDto(buildNbsAnswer(12L, 12L, 2, 2));
        ansDto.setItNew(false);
        ansDto.setItDirty(true);
        answerMap.put("2", ansDto);
        ansDto = new NbsAnswerDto(buildNbsAnswer(13L, 13L, 3, 3));
        ansDto.setItNew(false);
        ansDto.setItDirty(false);
        ansDto.setItDelete(true);
        answerMap.put("3", ansDto);
        pageContainer.setAnswerDTMap(answerMap);

        var pageRepeatMap = new HashMap<>();
        ansDto = new NbsAnswerDto(buildNbsAnswer(11L, 11L, 1, 1));
        ansDto.setItNew(true);
        pageRepeatMap.put("1", ansDto);
        ansDto = new NbsAnswerDto(buildNbsAnswer(12L, 12L, 2, 2));
        ansDto.setItNew(false);
        ansDto.setItDirty(true);
        pageRepeatMap.put("2", ansDto);
        ansDto = new NbsAnswerDto(buildNbsAnswer(13L, 13L, 3, 3));
        ansDto.setItNew(false);
        ansDto.setItDirty(false);
        ansDto.setItDelete(true);
        pageRepeatMap.put("3", ansDto);
        pageContainer.setPageRepeatingAnswerDTMap(pageRepeatMap);

        var nbsActEntityCol = new ArrayList<NbsActEntityDto>();
        var nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItNew(true);
        nbsActEntity.setNbsActEntityUid(16L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(16L);
        nbsActEntity.setEntityUid(16L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(16L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(16L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDirty(true);
        nbsActEntity.setNbsActEntityUid(17L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(17L);
        nbsActEntity.setEntityUid(17L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(17L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(17L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDelete(true);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(18L);
        nbsActEntity.setEntityUid(18L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(18L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(18L);
        nbsActEntityCol.add(nbsActEntity);

        pageContainer.setActEntityDTCollection(nbsActEntityCol);
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(15L);


        answerService.insertPageVO(pageContainer, observationDto);

        verify(nbsActEntityRepository, times(2)).save(any());
        verify(nbsActEntityRepository, times(1)).deleteNbsEntityAct(any());

    }

    @Test
    void storePageAnswer_Success() throws DataProcessingException {
        PageContainer pageContainer = new PageContainer();
        var answerMap = new HashMap<Object, NbsAnswerDto>();
        var ansDto = new NbsAnswerDto(buildNbsAnswer(11L, 11L, 1, 1));
        ansDto.setItNew(true);
        answerMap.put("1", ansDto);
        ansDto = new NbsAnswerDto(buildNbsAnswer(12L, 12L, 2, 2));
        ansDto.setItNew(false);
        ansDto.setItDirty(true);
        answerMap.put("2", ansDto);
        ansDto = new NbsAnswerDto(buildNbsAnswer(13L, 13L, 3, 3));
        ansDto.setItNew(false);
        ansDto.setItDirty(false);
        ansDto.setItDelete(true);
        answerMap.put("3", ansDto);
        pageContainer.setAnswerDTMap(answerMap);

        var pageRepeatMap = new HashMap<>();
        ansDto = new NbsAnswerDto(buildNbsAnswer(11L, 11L, 1, 1));
        ansDto.setItNew(true);
        pageRepeatMap.put("1", ansDto);
        ansDto = new NbsAnswerDto(buildNbsAnswer(12L, 12L, 2, 2));
        ansDto.setItNew(false);
        ansDto.setItDirty(true);
        pageRepeatMap.put("2", ansDto);
        ansDto = new NbsAnswerDto(buildNbsAnswer(13L, 13L, 3, 3));
        ansDto.setItNew(false);
        ansDto.setItDirty(false);
        ansDto.setItDelete(true);
        pageRepeatMap.put("3", ansDto);
        pageContainer.setPageRepeatingAnswerDTMap(pageRepeatMap);

        var nbsActEntityCol = new ArrayList<NbsActEntityDto>();
        var nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItNew(true);
        nbsActEntity.setNbsActEntityUid(16L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(16L);
        nbsActEntity.setEntityUid(16L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(16L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(16L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDirty(true);
        nbsActEntity.setNbsActEntityUid(17L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(17L);
        nbsActEntity.setEntityUid(17L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(17L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(17L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDelete(true);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(18L);
        nbsActEntity.setEntityUid(18L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(18L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(18L);
        nbsActEntityCol.add(nbsActEntity);

        pageContainer.setActEntityDTCollection(nbsActEntityCol);
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(15L);

        var nbsAnswerForDeleteCol = new ArrayList<NbsAnswer>();
        var nbsAnswerForDelete = buildNbsAnswer(19L, 19L, 1, 1);
        nbsAnswerForDeleteCol.add(nbsAnswerForDelete);
        when(nbsAnswerRepository.getPageAnswerByActUid(15L))
                .thenReturn(Optional.of(nbsAnswerForDeleteCol));

        var nbsActForDeleteCol = new ArrayList<NbsActEntity>();
        var nbsActForDelete = new NbsActEntity();
        nbsActForDeleteCol.add(nbsActForDelete);
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(15L))
                .thenReturn(Optional.of(nbsActForDeleteCol));

        answerService.storePageAnswer(pageContainer, observationDto);

        verify(nbsActEntityRepository, times(3)).save(any());

    }

    @Test
    void storeActEntityDTCollectionWithPublicHealthCase_Success() throws DataProcessingException {
        Collection<NbsActEntityDto> pamDTCollection = new ArrayList<>();
        var nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItNew(true);
        nbsActEntity.setNbsActEntityUid(16L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(16L);
        nbsActEntity.setEntityUid(16L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(16L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(16L);
        pamDTCollection.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDirty(true);
        nbsActEntity.setNbsActEntityUid(17L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(17L);
        nbsActEntity.setEntityUid(17L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(17L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(17L);
        pamDTCollection.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDelete(true);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setAddUserId(18L);
        nbsActEntity.setEntityUid(18L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setLastChgUserId(18L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp());
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(18L);
        pamDTCollection.add(nbsActEntity);

        PublicHealthCaseDto rootDTInterface = new PublicHealthCaseDto();
        rootDTInterface.setPublicHealthCaseUid(11L);

        answerService.storeActEntityDTCollectionWithPublicHealthCase(pamDTCollection, rootDTInterface);
        verify(nbsActEntityRepository, times(1)).deleteNbsEntityAct(any());
    }


}
