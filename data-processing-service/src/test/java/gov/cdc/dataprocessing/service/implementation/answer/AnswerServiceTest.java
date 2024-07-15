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
    AuthUtil authUtil;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        AuthUtil.setGlobalAuthUser(userInfo);
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
    void getPageAnswerDTMaps_Success() {
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
    void testGetNbsAnswerAndAssociationResultNotPresent() throws DataProcessingException {
        Long uid = 1L;

        when(nbsActEntityRepository.getNbsActEntitiesByActUid(uid)).thenReturn(Optional.empty());

        PageContainer result = answerService.getNbsAnswerAndAssociation(uid);

        assertNotNull(result);
        assertTrue(result.getAnswerDTMap().isEmpty());
        assertTrue(result.getPageRepeatingAnswerDTMap().isEmpty());
        assertTrue(result.getActEntityDTCollection().isEmpty());

        verify(nbsActEntityRepository).getNbsActEntitiesByActUid(uid);
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
    void storePageAnswer_Exception() {
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


        when(nbsActEntityRepository.getNbsActEntitiesByActUid(15L))
                .thenThrow(new RuntimeException("TEST"));


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            answerService.storePageAnswer(pageContainer, observationDto);
        });

        assertNotNull(thrown);
    }


    @Test
    void storeActEntityDTCollectionWithPublicHealthCase_Success() {
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

    @Test
    void getNbsAnswerAndAssociation_Exception() {

        when(nbsActEntityRepository.getNbsActEntitiesByActUid(any())).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            answerService.getNbsAnswerAndAssociation(null);
        });

        assertEquals("InterviewAnswerRootDAOImpl:answerCollection- could not be returned", thrown.getMessage());

    }

    @Test
    void getPageAnswerDTMaps_Test() {
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
        when(nbsAnswerRepository.getPageAnswerByActUid(any())).thenReturn(Optional.of(ansCol));

        answerService.getPageAnswerDTMaps(uid);

        verify(nbsAnswerRepository, times(1)).getPageAnswerByActUid(any());
    }

    @Test
    @SuppressWarnings("java:S2699")
    void insertPageVO_Test() throws DataProcessingException {
        PageContainer pageContainer = null;
        ObservationDto rootDTInterface = new ObservationDto();
        answerService.insertPageVO(pageContainer, rootDTInterface);
    }

    @Test
    void storeAnswerDTCollection_Test_Exp() {
        ArrayList<Object> answerDTColl = new ArrayList<>();
        ObservationDto interfaceDt = new ObservationDto();

        NbsAnswerDto an = new NbsAnswerDto();
        an.setItDirty(true);
        answerDTColl.add(an);

        when(nbsAnswerRepository.save(any())).thenThrow(new RuntimeException());


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            answerService.storeAnswerDTCollection(answerDTColl, interfaceDt);
        });

        assertNotNull(thrown);
    }

    @Test
    void delete_Exp() {
        ObservationDto observationDto = new ObservationDto();
        when(nbsAnswerRepository.getPageAnswerByActUid(any())).thenThrow(new RuntimeException());
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            answerService.delete(observationDto);
        });

        assertNotNull(thrown);
    }

    @Test
    void insertAnswerHistoryDTCollection_Test_1() {
        var anCol1 = new ArrayList<>();
        var an1 = new NbsAnswerDto();
        anCol1.add(an1);

        var anCol = new ArrayList<>();
        anCol.add(anCol1);
        when(nbsAnswerHistRepository.save(any())).thenThrow(new RuntimeException());


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            answerService.insertAnswerHistoryDTCollection(anCol);
        });

        assertNotNull(thrown);
    }


    @Test
    void insertAnswerHistoryDTCollection_Test_2() throws DataProcessingException {
        var anCol1 = new ArrayList<>();
        var an1 = new NbsAnswerDto();
        anCol1.add(an1);

        var anCol = new ArrayList<>();
        anCol.add(anCol1);


        answerService.insertAnswerHistoryDTCollection(anCol);

        verify(nbsAnswerHistRepository, times(1)).save(any());

    }

    @Test
    void insertPageEntityHistoryDTCollection_Exp() {
        ArrayList<NbsActEntityDto> nbsCaseEntityDTColl = new ArrayList<>();
        ObservationDto oldrootDTInterface = new ObservationDto();

        NbsActEntityDto entityDto = new NbsActEntityDto();
        nbsCaseEntityDTColl.add(entityDto);

        when(nbsActEntityHistRepository.save(any())).thenThrow(new RuntimeException());

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            answerService.insertPageEntityHistoryDTCollection(nbsCaseEntityDTColl, oldrootDTInterface);
        });

        assertNotNull(thrown);
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
        verify(nbsActEntityRepository, never()).save(any(NbsActEntity.class));
    }

    @Test
    void testStoreActEntityDTCollection_EmptyCollection() {
        Collection<NbsActEntityDto> pamDTCollection = new ArrayList<>();
        ObservationDto rootDTInterface = new ObservationDto();

        answerService.storeActEntityDTCollection(pamDTCollection, rootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(anyLong());
        verify(nbsActEntityRepository, never()).save(any(NbsActEntity.class));
    }

    @Test
    void testInsertActEntityDTCollection_EmptyCollection() {
        Collection<NbsActEntityDto> actEntityDTCollection = new ArrayList<>();
        ObservationDto observationDto = new ObservationDto();

        answerService.insertActEntityDTCollection(actEntityDTCollection, observationDto);

        verify(nbsActEntityRepository, never()).save(any(NbsActEntity.class));
    }


    @Test
    void testStoreAnswerDTCollection_NullCollection() throws DataProcessingException {
        ObservationDto interfaceDT = new ObservationDto();

        answerService.storeAnswerDTCollection(null, interfaceDT);

        verify(nbsAnswerRepository, never()).save(any(NbsAnswer.class));
    }

    @Test
    void testStoreAnswerDTCollection_DeleteCase() throws DataProcessingException {
        Collection<Object> answerDTColl = new ArrayList<>();
        NbsAnswerDto answerDT = mock(NbsAnswerDto.class);
        when(answerDT.isItDelete()).thenReturn(true);
        when(answerDT.getNbsAnswerUid()).thenReturn(1L);
        answerDTColl.add(answerDT);

        ObservationDto interfaceDT = new ObservationDto();

        answerService.storeAnswerDTCollection(answerDTColl, interfaceDT);

        verify(nbsAnswerRepository, never()).save(any(NbsAnswer.class));
    }

    @Test
    void testDelete_AnswerCollectionNull() throws DataProcessingException {
        ObservationDto rootDTInterface = new ObservationDto();
        rootDTInterface.setObservationUid(1L);

        when(nbsAnswerRepository.getPageAnswerByActUid(1L)).thenReturn(Optional.empty());

        answerService.delete(rootDTInterface);

        verify(nbsAnswerRepository, times(1)).getPageAnswerByActUid(any());
    }

    @Test
    void testDelete_AnswerCollectionEmpty() throws DataProcessingException {
        ObservationDto rootDTInterface = new ObservationDto();
        rootDTInterface.setObservationUid(1L);

        when(nbsAnswerRepository.getPageAnswerByActUid(1L)).thenReturn(Optional.of(new ArrayList<>()));

        answerService.delete(rootDTInterface);

        verify(nbsAnswerRepository, times(1)).getPageAnswerByActUid(any());

    }

    @Test
    void testDelete_ActEntityCollectionNull() throws DataProcessingException {
        ObservationDto rootDTInterface = new ObservationDto();
        rootDTInterface.setObservationUid(1L);

        when(nbsAnswerRepository.getPageAnswerByActUid(1L)).thenReturn(Optional.of(new ArrayList<>()));
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(1L)).thenReturn(Optional.empty());

        answerService.delete(rootDTInterface);

        verify(nbsAnswerRepository, times(1)).getPageAnswerByActUid(any());

    }

    @Test
    void testDelete_ActEntityCollectionEmpty() throws DataProcessingException {
        ObservationDto rootDTInterface = new ObservationDto();
        rootDTInterface.setObservationUid(1L);

        when(nbsAnswerRepository.getPageAnswerByActUid(1L)).thenReturn(Optional.of(new ArrayList<>()));
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(1L)).thenReturn(Optional.of(new ArrayList<>()));

        answerService.delete(rootDTInterface);

        verify(nbsAnswerRepository, times(1)).getPageAnswerByActUid(any());

    }

    @Test
    void testInsertAnswerHistoryDTCollection_NullCollection() throws DataProcessingException {
        answerService.insertAnswerHistoryDTCollection(null);

        verify(nbsAnswerRepository, never()).deleteNbsAnswer(any());
        verify(nbsAnswerHistRepository, never()).save(any());
    }

    @Test
    void testInsertAnswerHistoryDTCollection_EmptyCollection() throws DataProcessingException {
        answerService.insertAnswerHistoryDTCollection(Collections.emptyList());

        verify(nbsAnswerRepository, never()).deleteNbsAnswer(any());
        verify(nbsAnswerHistRepository, never()).save(any());
    }

    @Test
    void testInsertAnswerHistoryDTCollection_InvalidObject() throws DataProcessingException {
        Collection<Object> invalidCollection = new ArrayList<>();
        invalidCollection.add("InvalidObject");

        answerService.insertAnswerHistoryDTCollection(invalidCollection);

        verify(nbsAnswerRepository, never()).deleteNbsAnswer(any());
        verify(nbsAnswerHistRepository, never()).save(any());
    }


    @Test
    void testInsertPageEntityHistoryDTCollection_NullCollection() throws DataProcessingException {
        ObservationDto oldRootDTInterface = mock(ObservationDto.class);
        answerService.insertPageEntityHistoryDTCollection(null, oldRootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(any());
        verify(nbsActEntityHistRepository, never()).save(any());
    }

    @Test
    void testInsertPageEntityHistoryDTCollection_EmptyCollection() throws DataProcessingException {
        ObservationDto oldRootDTInterface = mock(ObservationDto.class);
        answerService.insertPageEntityHistoryDTCollection(Collections.emptyList(), oldRootDTInterface);

        verify(nbsActEntityRepository, never()).deleteNbsEntityAct(any());
        verify(nbsActEntityHistRepository, never()).save(any());
    }

}
