package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.NBSConstantUtil;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestionExtended;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsUiMetaData;
import gov.cdc.dataprocessing.repository.nbs.odse.model.question.WAQuestion;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.WAQuestionRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.lookup.LookupMappingRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsUiMetaDataRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
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
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class LookupServiceTest {

    @Mock
    private LookupMappingRepository lookupMappingRepository;
    @Mock
    private NbsUiMetaDataRepository nbsUiMetaDataRepository;
    @Mock
    private WAQuestionRepository waQuestionRepository;
    @Mock
    private ICatchingValueDpService catchingValueService;
    @InjectMocks
    private LookupService lookupService;
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
        OdseCache.toPrePopFormMapping.clear();
        OdseCache.fromPrePopFormMapping.clear();

        OdseCache.map.clear();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(lookupMappingRepository, nbsUiMetaDataRepository, waQuestionRepository, catchingValueService, authUtil);
    }

    @Test
    void testGetToPrePopFormMapping_CacheHit() throws DataProcessingException {
        String formCd = "testFormCd";
        TreeMap<Object, Object> expectedMap = new TreeMap<>();
        OdseCache.toPrePopFormMapping.put(formCd, expectedMap);

        TreeMap<Object, Object> result = lookupService.getToPrePopFormMapping(formCd);

        assertEquals(expectedMap, result);
    }


    @Test
    void testGetToPrePopFormMapping_Test2() throws DataProcessingException {
        String formCd = "TO_FORM";

        List<LookupQuestionExtended> lookQLst = new ArrayList<>();
        var lookQ = new LookupQuestionExtended();
        lookQ.setToFormCd("TO_FORM");
        lookQ.setToQuestionIdentifier("TO_QUES");
        lookQ.setFromQuestionIdentifier("FROM_QUES");
        lookQ.setFromAnswerCode("FROM_ANS");
        lookQLst.add(lookQ);
        lookQ = new LookupQuestionExtended();
        lookQ.setToFormCd("TO_FORM");
        lookQ.setToQuestionIdentifier("TO_QUES");
        lookQ.setFromQuestionIdentifier("FROM_QUES");
        lookQ.setFromAnswerCode("FROM_ANS");
        lookQLst.add(lookQ);
        lookQ = new LookupQuestionExtended();
        lookQ.setToFormCd("TO_FORM_DIFF");
        lookQ.setToQuestionIdentifier("TO_QUES");
        lookQ.setFromQuestionIdentifier("FROM_QUES");
        lookQ.setFromAnswerCode("FROM_ANS");
        lookQLst.add(lookQ);
        when(lookupMappingRepository.getLookupMappings()).thenReturn(Optional.of(lookQLst));

       var res =  lookupService.getToPrePopFormMapping(formCd);

       assertEquals(2, res.size());

    }

    @Test
    void getQuestionMap_Test() {
        OdseCache.map.put("TEST", new TreeMap<>());
        var res = lookupService.getQuestionMap();
        assertNotNull(res);
    }

    @Test
    void getQuestionMap_Test_1() {

        var questCol = new ArrayList<>();
        var nbs = new NbsQuestionMetadata();
        nbs.setInvestigationFormCd( NBSConstantUtil.INV_FORM_RVCT);
        nbs.setQuestionIdentifier("IDENTIFIER");
        questCol.add(nbs);
        when(nbsUiMetaDataRepository.findPamQuestionMetaData()).thenReturn(Optional.of(questCol));

        var res = lookupService.getQuestionMap();
        assertNotNull(res);

    }

    @Test
    void getDMBQuestionMapAfterPublish_Test() {

        var dmbLst = new ArrayList<NbsUiMetaData>();
        var dmb = new NbsUiMetaData();
        dmb.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE);
        dmb.setCodeSetNm("CODE");
        dmb.setInvestigationFormCd("FROM");
        dmb.setQuestionIdentifier("IDEN");
        dmbLst.add(dmb);
        var pamLst = new ArrayList<WAQuestion>();
        var pam = new WAQuestion();
        pam.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE);
        pam.setCodeSetNm("CODE");
        pam.setInvestigationFormCd("FORM");
        pam.setQuestionIdentifier("IDEN");
        pamLst.add(pam);
        pam = new WAQuestion();
        pam.setDataType(NEDSSConstant.NBS_QUESTION_DATATYPE_CODED_VALUE);
        pam.setCodeSetNm("CODE");
        pam.setInvestigationFormCd("FORM");
        pam.setQuestionIdentifier("IDEN");
        pamLst.add(pam);
        when(nbsUiMetaDataRepository.findDmbQuestionMetaData()).thenReturn(Optional.of(dmbLst));
        when(waQuestionRepository.findGenericQuestionMetaData()).thenReturn(Optional.of(pamLst));


        var geCodeLst = new ArrayList<CodeValueGeneral>();
        var geCode = new CodeValueGeneral();
        geCodeLst.add(geCode);
        when(catchingValueService.getGeneralCodedValue("CODE")).thenReturn(geCodeLst);

        var res = lookupService.getDMBQuestionMapAfterPublish();

        assertEquals(2, res.size());
    }

    @Test
    void fillPrePopMap_Test() {

        var lookUpLst = new ArrayList<LookupQuestionExtended>();
        var lookUp = new LookupQuestionExtended();
        lookUp.setFromFormCd("FORM");
        lookUp.setFromQuestionIdentifier("IDEN");
        lookUp.setFromAnswerCode("CODE");
        lookUpLst.add(lookUp);
        lookUp = new LookupQuestionExtended();
        lookUp.setFromFormCd("FORM");
        lookUp.setFromQuestionIdentifier("IDEN");
        lookUp.setFromAnswerCode("CODE");
        lookUpLst.add(lookUp);
        lookUp = new LookupQuestionExtended();
        lookUp.setFromFormCd("FORM_DIFF");
        lookUp.setFromQuestionIdentifier("IDEN");
        lookUp.setFromAnswerCode("CODE");

        lookUpLst.add(lookUp);
        when(lookupMappingRepository.getLookupMappings()).thenReturn(Optional.of(lookUpLst));


        lookupService.fillPrePopMap();

        verify(lookupMappingRepository, times(2)).getLookupMappings();


    }
}
