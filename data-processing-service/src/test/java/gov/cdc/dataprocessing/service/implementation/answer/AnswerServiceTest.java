package gov.cdc.dataprocessing.service.implementation.answer;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PageContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsActJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsAnswerJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnswerServiceTest {
    @Mock
    private NbsAnswerJdbcRepository nbsAnswerRepository;
    @Mock
    private NbsActJdbcRepository nbsActEntityRepository;
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
        Mockito.reset(nbsAnswerRepository, nbsActEntityRepository, authUtil);
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
        nbs.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbs.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbs.setLastChgUserId(uid);
        return nbs;
    }


    @Test
    void getPageAnswerDTMaps_Success() {
        long uid = 10L;

        // Set up mock answers
        var nbsAnsCol = new ArrayList<NbsAnswer>();
        nbsAnsCol.add(buildNbsAnswer(11L, 1L, 0, 1));
        nbsAnsCol.add(buildNbsAnswer(12L, 1L, 1, 1));
        nbsAnsCol.add(buildNbsAnswer(13L, 1L, 1, 1));

        nbsAnsCol.add(buildNbsAnswer(13L, 1L, -1, 1));

        nbsAnsCol.add(buildNbsAnswer(-1L, -1L, -1, -1));


        when(nbsAnswerRepository.findByActUid(uid)).thenReturn(nbsAnsCol);

        var result = answerService.getPageAnswerDTMaps(uid);

        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    void testGetNbsAnswerAndAssociationResultNotPresent()  {
        Long uid = 1L;

        when(nbsActEntityRepository.getNbsActEntitiesByActUid(uid)).thenReturn(new ArrayList<>());

        PageContainer result = answerService.getNbsAnswerAndAssociation(uid);

        assertNotNull(result);
        assertTrue(result.getAnswerDTMap().isEmpty());
        assertTrue(result.getPageRepeatingAnswerDTMap().isEmpty());
        assertTrue(result.getActEntityDTCollection().isEmpty());

        verify(nbsActEntityRepository).getNbsActEntitiesByActUid(uid);
    }


    @Test
    void getNbsAnswerAndAssociation_Success()  {
        long uid = 10L;

        // getPageAnswerDTMaps 52
        var nbsAnsCol = new ArrayList<NbsAnswer>();
        nbsAnsCol.add(buildNbsAnswer(11L, 1L, 0, 1));
        nbsAnsCol.add(buildNbsAnswer(12L, 1L, 1, 1));
        nbsAnsCol.add(buildNbsAnswer(13L, 1L, 1, 1));
        nbsAnsCol.add(buildNbsAnswer(13L, 1L, -1, 1));
        nbsAnsCol.add(buildNbsAnswer(-1L, -1L, -1, -1));
        when(nbsAnswerRepository.findByActUid(uid)).thenReturn(nbsAnsCol);


        var actEntityCol = new ArrayList<NbsActEntity>();
        var actEntity = new NbsActEntity();
        actEntity.setNbsActEntityUid(11L);
        actEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        actEntity.setAddUserId(11L);
        actEntity.setEntityUid(11L);
        actEntity.setEntityVersionCtrlNbr(1);
        actEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        actEntity.setLastChgUserId(11L);
        actEntity.setRecordStatusCd("TEST");
        actEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        actEntity.setTypeCd("TEST");
        actEntity.setActUid(11L);
        actEntityCol.add(actEntity);
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(10L)).thenReturn(actEntityCol);

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
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(16L);
        nbsActEntity.setEntityUid(16L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(16L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(16L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDirty(true);
        nbsActEntity.setNbsActEntityUid(17L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(17L);
        nbsActEntity.setEntityUid(17L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(17L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(17L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDelete(true);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(18L);
        nbsActEntity.setEntityUid(18L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(18L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(18L);
        nbsActEntityCol.add(nbsActEntity);

        pageContainer.setActEntityDTCollection(nbsActEntityCol);
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(15L);


        answerService.insertPageVO(pageContainer, observationDto);

        verify(nbsActEntityRepository, times(2)).mergeNbsActEntity(any());
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
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(16L);
        nbsActEntity.setEntityUid(16L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(16L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(16L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDirty(true);
        nbsActEntity.setNbsActEntityUid(17L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(17L);
        nbsActEntity.setEntityUid(17L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(17L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(17L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDelete(true);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(18L);
        nbsActEntity.setEntityUid(18L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(18L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(18L);
        nbsActEntityCol.add(nbsActEntity);

        pageContainer.setActEntityDTCollection(nbsActEntityCol);
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(15L);

        var nbsAnswerForDeleteCol = new ArrayList<NbsAnswer>();
        var nbsAnswerForDelete = buildNbsAnswer(19L, 19L, 1, 1);
        nbsAnswerForDeleteCol.add(nbsAnswerForDelete);
        when(nbsAnswerRepository.findByActUid(15L))
                .thenReturn(nbsAnswerForDeleteCol);

        var nbsActForDeleteCol = new ArrayList<NbsActEntity>();
        var nbsActForDelete = new NbsActEntity();
        nbsActForDeleteCol.add(nbsActForDelete);
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(15L))
                .thenReturn(nbsActForDeleteCol);

        answerService.storePageAnswer(pageContainer, observationDto);

        verify(nbsActEntityRepository, times(3)).mergeNbsActEntity(any());

    }


    @Test
    void storePageAnswer_Exception()  {
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
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(16L);
        nbsActEntity.setEntityUid(16L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(16L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(16L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDirty(true);
        nbsActEntity.setNbsActEntityUid(17L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(17L);
        nbsActEntity.setEntityUid(17L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(17L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(17L);
        nbsActEntityCol.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDelete(true);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(18L);
        nbsActEntity.setEntityUid(18L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(18L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(18L);
        nbsActEntityCol.add(nbsActEntity);

        pageContainer.setActEntityDTCollection(nbsActEntityCol);
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(15L);

        var nbsAnswerForDeleteCol = new ArrayList<NbsAnswer>();
        var nbsAnswerForDelete = buildNbsAnswer(19L, 19L, 1, 1);
        nbsAnswerForDeleteCol.add(nbsAnswerForDelete);
        when(nbsAnswerRepository.findByActUid(15L))
                .thenReturn(nbsAnswerForDeleteCol);


        when(nbsActEntityRepository.getNbsActEntitiesByActUid(15L))
                .thenThrow(new RuntimeException("TEST"));


        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            answerService.storePageAnswer(pageContainer, observationDto);
        });

        assertNotNull(thrown);
    }


    @Test
    void storeActEntityDTCollectionWithPublicHealthCase_Success()  {
        Collection<NbsActEntityDto> pamDTCollection = new ArrayList<>();
        var nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItNew(true);
        nbsActEntity.setNbsActEntityUid(16L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(16L);
        nbsActEntity.setEntityUid(16L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(16L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(16L);
        pamDTCollection.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDirty(true);
        nbsActEntity.setNbsActEntityUid(17L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(17L);
        nbsActEntity.setEntityUid(17L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(17L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(17L);
        pamDTCollection.add(nbsActEntity);

        nbsActEntity = new NbsActEntityDto();
        nbsActEntity.setItDelete(true);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setNbsActEntityUid(18L);
        nbsActEntity.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setAddUserId(18L);
        nbsActEntity.setEntityUid(18L);
        nbsActEntity.setEntityVersionCtrlNbr(1);
        nbsActEntity.setLastChgTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setLastChgUserId(18L);
        nbsActEntity.setRecordStatusCd("TEST");
        nbsActEntity.setRecordStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        nbsActEntity.setTypeCd("TEST");
        nbsActEntity.setActUid(18L);
        pamDTCollection.add(nbsActEntity);

        PublicHealthCaseDto rootDTInterface = new PublicHealthCaseDto();
        rootDTInterface.setPublicHealthCaseUid(11L);

        answerService.storeActEntityDTCollectionWithPublicHealthCase(pamDTCollection, rootDTInterface);
        verify(nbsActEntityRepository, times(1)).deleteNbsEntityAct(any());
    }

    @Test
    void getNbsAnswerAndAssociation_Exception()  {

        when(nbsActEntityRepository.getNbsActEntitiesByActUid(any())).thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            answerService.getNbsAnswerAndAssociation(null);
        });

        assertEquals("TEST",thrown.getMessage());

    }

    @Test
    void getPageAnswerDTMaps_Test()  {
        Long uid = 10L;

        var ansCol = new ArrayList<NbsAnswer>();
        var ans = new NbsAnswer();
        ans.setSeqNbr(1);
        ans.setNbsQuestionUid(0L);
        ansCol.add(ans);
        ans = new NbsAnswer();
        ans.setSeqNbr(1);
        ans.setNbsQuestionUid(10L);
        ansCol.add(ans);
        when(nbsAnswerRepository.findByActUid(any())).thenReturn(ansCol);

        answerService.getPageAnswerDTMaps(uid);

        verify(nbsAnswerRepository, times(1)).findByActUid(any());
    }

    @Test
    @SuppressWarnings("java:S2699")
    void insertPageVO_Test() throws DataProcessingException {
        PageContainer pageContainer = null;
        ObservationDto rootDTInterface = new ObservationDto();
        answerService.insertPageVO(pageContainer, rootDTInterface);
    }


    @Test
    void delete_Exp() {
        ObservationDto observationDto = new ObservationDto();
        when(nbsAnswerRepository.findByActUid(any())).thenThrow(new RuntimeException());
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            answerService.delete(observationDto);
        });

        assertNotNull(thrown);
    }


    @Test
    void insertAnswerHistoryDTCollection_Test_2()  {
        var anCol1 = new ArrayList<>();
        var an1 = new NbsAnswerDto();
        anCol1.add(an1);

        var anCol = new ArrayList<>();
        anCol.add(anCol1);


        answerService.insertAnswerHistoryDTCollection(anCol);

        verify(nbsAnswerRepository, times(1)).mergeNbsAnswerHist(any());

    }


    @Test
    void testProcessingPageAnswerMapSeqNbrGreaterThanZeroCollSizeGreaterThanZero() {
        Map<Object, Object> nbsRepeatingAnswerMap = new HashMap<>();
        Map<Object, Object> nbsAnswerMap = new HashMap<>();
        Collection<NbsAnswerDto> coll = new ArrayList<>();
        coll.add(new NbsAnswerDto()); // Adding an element to coll to make its size > 0
        Long nbsQuestionUid = 1L;
        NbsAnswerDto pageAnsDT = new NbsAnswerDto();
        pageAnsDT.setNbsQuestionUid(2L);
        pageAnsDT.setSeqNbr(1);

        Long result = answerService.processingPageAnswerMap(pageAnsDT, nbsRepeatingAnswerMap, nbsQuestionUid, coll, nbsAnswerMap);

        assertEquals(2L, result);
    }

    @Test
    void testProcessingPageAnswerMapCollSizeGreaterThanZeroFinalElse() {
        Map<Object, Object> nbsRepeatingAnswerMap = new HashMap<>();
        Map<Object, Object> nbsAnswerMap = new HashMap<>();
        Collection<NbsAnswerDto> coll = new ArrayList<>();
        coll.add(new NbsAnswerDto()); // Adding an element to coll to make its size > 0
        Long nbsQuestionUid = 1L;
        NbsAnswerDto pageAnsDT = new NbsAnswerDto();
        pageAnsDT.setNbsQuestionUid(2L);

        Long result = answerService.processingPageAnswerMap(pageAnsDT, nbsRepeatingAnswerMap, nbsQuestionUid, coll, nbsAnswerMap);

        assertEquals(2L, result);
    }


    @Test
    void testInsertPageVOAnswerDTMapSizeZero() throws DataProcessingException {
        PageContainer pageContainer = new PageContainer();
        Map<Object, NbsAnswerDto> answerDTMap = new HashMap<>();
        pageContainer.setAnswerDTMap(answerDTMap);

        ObservationDto rootDTInterface = new ObservationDto();

        pageContainer.setActEntityDTCollection(new ArrayList<>());
        answerService.insertPageVO(pageContainer, rootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(any());
    }

    @Test
    void testInsertPageVORepeatingAnswerDTMapSizeZero() throws DataProcessingException {
        PageContainer pageContainer = new PageContainer();
        Map<Object, NbsAnswerDto> answerDTMap = new HashMap<>();
        answerDTMap.put("key", new NbsAnswerDto());
        pageContainer.setAnswerDTMap(answerDTMap);

        Map<Object, Object> repeatingAnswerDTMap = new HashMap<>();
        pageContainer.setPageRepeatingAnswerDTMap(repeatingAnswerDTMap);

        ObservationDto rootDTInterface = new ObservationDto();
        pageContainer.setActEntityDTCollection(new ArrayList<>());

        answerService.insertPageVO(pageContainer, rootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(any());
    }

    @Test
    void testStorePageAnswerElseCase() throws DataProcessingException {
        ObservationDto observationDto = new ObservationDto();

        // Call the method with pageContainer set to null
        answerService.storePageAnswer(null, observationDto);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(any());

    }

    @Test
    void testStoreActEntityDTCollectionWithPublicHealthCase_EmptyCollection() {
        Collection<NbsActEntityDto> pamDTCollection = new ArrayList<>();
        PublicHealthCaseDto rootDTInterface = new PublicHealthCaseDto();

        answerService.storeActEntityDTCollectionWithPublicHealthCase(pamDTCollection, rootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(anyLong());
        verify(nbsActEntityRepository, never()).mergeNbsActEntity(any(NbsActEntity.class));
    }

    @Test
    void testStoreActEntityDTCollection_EmptyCollection() {
        Collection<NbsActEntityDto> pamDTCollection = new ArrayList<>();
        ObservationDto rootDTInterface = new ObservationDto();

        answerService.storeActEntityDTCollection(pamDTCollection, rootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(anyLong());
        verify(nbsActEntityRepository, never()).mergeNbsActEntity(any(NbsActEntity.class));
    }

    @Test
    void testInsertActEntityDTCollection_EmptyCollection() {
        Collection<NbsActEntityDto> actEntityDTCollection = new ArrayList<>();

        answerService.insertActEntityDTCollection(actEntityDTCollection);

        verify(nbsActEntityRepository, never()).mergeNbsActEntity(any(NbsActEntity.class));
    }


    @Test
    void testStoreAnswerDTCollection_NullCollection()  {

        answerService.storeAnswerDTCollection(null);

        verify(nbsAnswerRepository, never()).mergeNbsAnswer(any(NbsAnswer.class));
    }

    @Test
    void testStoreAnswerDTCollection_DeleteCase()  {
        Collection<Object> answerDTColl = new ArrayList<>();
        NbsAnswerDto answerDT = mock(NbsAnswerDto.class);
        when(answerDT.isItDelete()).thenReturn(true);
        when(answerDT.getNbsAnswerUid()).thenReturn(1L);
        answerDTColl.add(answerDT);


        answerService.storeAnswerDTCollection(answerDTColl);

        verify(nbsAnswerRepository, never()).mergeNbsAnswer(any(NbsAnswer.class));
    }

    @Test
    void testDelete_AnswerCollectionNull()  {
        ObservationDto rootDTInterface = new ObservationDto();
        rootDTInterface.setObservationUid(1L);

        when(nbsAnswerRepository.findByActUid(1L)).thenReturn(new ArrayList<>());

        answerService.delete(rootDTInterface);

        verify(nbsAnswerRepository, times(1)).findByActUid(any());
    }

    @Test
    void testDelete_AnswerCollectionEmpty()  {
        ObservationDto rootDTInterface = new ObservationDto();
        rootDTInterface.setObservationUid(1L);

        when(nbsAnswerRepository.findByActUid(1L)).thenReturn(new ArrayList<>());

        answerService.delete(rootDTInterface);

        verify(nbsAnswerRepository, times(1)).findByActUid(any());

    }

    @Test
    void testDelete_ActEntityCollectionNull()  {
        ObservationDto rootDTInterface = new ObservationDto();
        rootDTInterface.setObservationUid(1L);

        when(nbsAnswerRepository.findByActUid(1L)).thenReturn(new ArrayList<>());
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(1L)).thenReturn(new ArrayList<>());

        answerService.delete(rootDTInterface);

        verify(nbsAnswerRepository, times(1)).findByActUid(any());

    }

    @Test
    void testInsertAnswerHistoryDTCollection_NullCollection()  {
        answerService.insertAnswerHistoryDTCollection(null);

        verify(nbsAnswerRepository, never()).deleteByNbsAnswerUid(any());
        verify(nbsActEntityRepository, never()).mergeNbsActEntityHist(any());
    }

    @Test
    void testInsertAnswerHistoryDTCollection_EmptyCollection()  {
        answerService.insertAnswerHistoryDTCollection(Collections.emptyList());

        verify(nbsAnswerRepository, never()).deleteByNbsAnswerUid(any());
        verify(nbsAnswerRepository, never()).mergeNbsAnswerHist(any());
    }

    @Test
    void testInsertAnswerHistoryDTCollection_InvalidObject()  {
        Collection<Object> invalidCollection = new ArrayList<>();
        invalidCollection.add("InvalidObject");

        answerService.insertAnswerHistoryDTCollection(invalidCollection);

        verify(nbsAnswerRepository, never()).deleteByNbsAnswerUid(any());
        verify(nbsAnswerRepository, never()).mergeNbsAnswerHist(any());
    }


    @Test
    void testInsertPageEntityHistoryDTCollection_NullCollection()  {
        ObservationDto oldRootDTInterface = mock(ObservationDto.class);
        answerService.insertPageEntityHistoryDTCollection(null, oldRootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(any());
        verify(nbsActEntityRepository, never()).mergeNbsActEntityHist(any());
    }

    @Test
    void testInsertPageEntityHistoryDTCollection_EmptyCollection()  {
        ObservationDto oldRootDTInterface = mock(ObservationDto.class);
        answerService.insertPageEntityHistoryDTCollection(Collections.emptyList(), oldRootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(any());
        verify(nbsActEntityRepository, never()).mergeNbsActEntityHist(any());
    }

}
