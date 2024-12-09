package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.RenderConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.lookup.PrePopMappingDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCodeWithPA;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.ConditionCodeRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestData;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AutoInvestigationServiceTest {
    @Mock
    private ConditionCodeRepository conditionCodeRepository;
    @Mock
    private ICatchingValueService catchingValueService;
    @Mock
    private ILookupService lookupService;

    @InjectMocks
    private AutoInvestigationService autoInvestigationService;
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

//        jurisdictionCodeMapWithNbsUid.put("STATE", 1);
        OdseCache.fromPrePopFormMapping.clear();
        OdseCache.dmbMap.clear();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(conditionCodeRepository, catchingValueService, lookupService, authUtil);
    }

    @Test
    void autoCreateInvestigation_Success() throws DataProcessingException {
        TestData.createObservationContainer();
        TestData.createEdxLabInformationDto(NEDSSConstant.INV_FORM_RVCT);
        ObservationContainer observationVO = TestData.observationContainer;
        EdxLabInformationDto edxLabInformationDT = TestData.edxLabInformationDto;

        // createPublicHealthCaseVO 59
        var conditionCodeWithPACol = new ArrayList<ConditionCodeWithPA>();
        var conditionCodeWithPA = new ConditionCodeWithPA();
        conditionCodeWithPA.setConditionCd("COND");
        conditionCodeWithPA.setProgAreaCd("PROG");
        conditionCodeWithPA.setStateProgAreaCode("STATE");
        conditionCodeWithPA.setConditionCodesetNm("COND");
        conditionCodeWithPACol.add(conditionCodeWithPA);
        when(conditionCodeRepository.findProgramAreaConditionCodeByConditionCode(edxLabInformationDT.getConditionCode()))
                .thenReturn(Optional.of(conditionCodeWithPACol));


        var test = autoInvestigationService.autoCreateInvestigation(observationVO, edxLabInformationDT);

        assertNotNull(test);
        verify(conditionCodeRepository, times(1)).findProgramAreaConditionCodeByConditionCode(any());

    }

    @Test
    void autoCreateInvestigation_Success2_InvestigationTypeIsNull() throws DataProcessingException {
        TestData.createObservationContainer();
        TestData.createEdxLabInformationDto(NEDSSConstant.INV_FORM_RVCT);
        ObservationContainer observationVO = TestData.observationContainer;
        EdxLabInformationDto edxLabInformationDT = TestData.edxLabInformationDto;
        edxLabInformationDT.setInvestigationType(null);



        // createPublicHealthCaseVO 59
        var conditionCodeWithPACol = new ArrayList<ConditionCodeWithPA>();
        var conditionCodeWithPA = new ConditionCodeWithPA();
        conditionCodeWithPA.setConditionCd("COND");
        conditionCodeWithPA.setProgAreaCd("PROG");
        conditionCodeWithPA.setStateProgAreaCode("STATE");
        conditionCodeWithPA.setConditionCodesetNm("COND");
        conditionCodeWithPA.setInvestigationFormCd("FORM");
        conditionCodeWithPACol.add(conditionCodeWithPA);
        when(conditionCodeRepository.findProgramAreaConditionCodeByConditionCode(edxLabInformationDT.getConditionCode()))
                .thenReturn(Optional.of(conditionCodeWithPACol));

        OdseCache.fromPrePopFormMapping = new TreeMap<>();
        var labFormTree = new TreeMap<>();
        labFormTree.put("CODE", "TEST");
        OdseCache.fromPrePopFormMapping.put(NEDSSConstant.LAB_FORM_CD, labFormTree);

        OdseCache.dmbMap = new TreeMap<>();
        var dmsMapValue = new TreeMap<>();
        var dmsMapValueValue = new NbsQuestionMetadata();
        dmsMapValueValue.setDataLocation("DATA_LOCATION");
        dmsMapValue.put("CODE", dmsMapValueValue);
        OdseCache.dmbMap.put("FORM", dmsMapValue);

        when(conditionCodeRepository.findProgramAreaConditionCode(eq(1), any()))
                .thenReturn(Optional.of(conditionCodeWithPACol));

        var toPrePopMap = new TreeMap<>();
        var toPre = new PrePopMappingDto();
        toPre.setFromAnswerCode(null);
        toPre.setFromQuestionIdentifier("CODE");
        toPre.setToQuestionIdentifier("CODE");
        toPre.setToDataType(NEDSSConstant.DATE_DATATYPE);
        toPrePopMap.put("FORM", toPre);
        when(lookupService.getToPrePopFormMapping("FORM"))
                .thenReturn(toPrePopMap);


        var test = autoInvestigationService.autoCreateInvestigation(observationVO, edxLabInformationDT);

        assertNotNull(test);
        verify(conditionCodeRepository, times(1)).findProgramAreaConditionCodeByConditionCode(any());

    }


    @Test
    void transferValuesTOActProxyVO_Success() throws DataProcessingException {
        PageActProxyContainer pageActProxyContainer = new PageActProxyContainer();
        PamProxyContainer pamActProxyVO = new PamProxyContainer();
        Collection< PersonContainer > personVOCollection = new ArrayList<>();
        ObservationContainer rootObservationVO = new ObservationContainer();
        Collection<Object> entities = new ArrayList<>();
        Map<Object, Object> questionIdentifierMap = new TreeMap<>();


        var patCol = new ArrayList<ParticipationDto>();
        var patDto = new ParticipationDto();
        patDto.setTypeCd(EdxELRConstant.ELR_AUTHOR_CD);
        patDto.setSubjectClassCd(EdxELRConstant.ELR_ORG);
        patCol.add(patDto);
        patCol = new ArrayList<ParticipationDto>();
        patDto = new ParticipationDto();
        patDto.setTypeCd(EdxELRConstant.ELR_ORDER_CD);
        patDto.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
        patCol.add(patDto);
        patCol = new ArrayList<ParticipationDto>();
        patDto = new ParticipationDto();
        patDto.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
        patDto.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
        patCol.add(patDto);
        patCol = new ArrayList<ParticipationDto>();
        patDto = new ParticipationDto();
        patDto.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
        patDto.setSubjectClassCd(EdxELRConstant.ELR_ORG);
        patDto.setCd(EdxELRConstant.ELR_OP_CD);
        patCol.add(patDto);
        patCol = new ArrayList<ParticipationDto>();
        patDto = new ParticipationDto();
        patDto.setTypeCd(EdxELRConstant.ELR_PATIENT_SUBJECT_CD);
        patCol.add(patDto);

        rootObservationVO.setTheParticipationDtoCollection(patCol);

        var phcConn = new PublicHealthCaseContainer();
        var phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(10L);
        phcDto.setAddTime(TimeStampUtil.getCurrentTimeStamp());
        phcDto.setLastChgTime(TimeStampUtil.getCurrentTimeStamp());
        phcDto.setLastChgUserId(123L);
        phcDto.setRecordStatusCd("ACTIVE");
        phcDto.setAddUserId(123L);
        phcConn.setThePublicHealthCaseDto(phcDto);
        pageActProxyContainer.setPublicHealthCaseContainer(phcConn);

        var personCon = new PersonContainer();
        var personDto = new PersonDto();
        personDto.setCd("PERSON");
        personCon.setThePersonDto(personDto);
        personVOCollection.add(personCon);

        var entityEdxRule = new EdxRuleManageDto();
        entityEdxRule.setParticipationTypeCode("PAT");
        entityEdxRule.setParticipationUid(11L);
        entityEdxRule.setParticipationClassCode("PAT");
        entities.add(entityEdxRule);

        // createActEntityObject 162
//        var tree =new HashMap<String, String>();
//        tree.put("PAT", "PAT");
//        when(catchingValueService.getCodedValue(any()))
//                .thenReturn(tree);


        var test = (PageActProxyContainer) autoInvestigationService.transferValuesTOActProxyVO(pageActProxyContainer, pamActProxyVO,
                personVOCollection, rootObservationVO, entities, questionIdentifierMap);


        assertNotNull(test);
        assertEquals(2, test.getTheParticipationDtoCollection().size());

    }

    @Test
    void transferValuesTOActProxyVO_Test_2() throws DataProcessingException {
        PageActProxyContainer pageActProxyContainer = null;
        PamProxyContainer pamActProxyVO = new PamProxyContainer();
        ArrayList<PersonContainer> personVOCollection = new ArrayList<>();
        ObservationContainer rootObservationVO = new ObservationContainer();
        ArrayList<Object> entities = new ArrayList<>();
        Map<Object, Object> questionIdentifierMap = new HashMap<>();

        var lstPat = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setTypeCd(EdxELRConstant.ELR_AUTHOR_CD);
        pat.setSubjectClassCd(EdxELRConstant.ELR_ORG);
        lstPat.add(pat);
        rootObservationVO.setTheParticipationDtoCollection(lstPat);


        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setPublicHealthCaseUid(10L);
        phcConn.setThePublicHealthCaseDto(phcDt);
        pamActProxyVO.setPublicHealthCaseContainer(phcConn);

        var perConn = new PersonContainer();
        var perDt = new PersonDto();
        perDt.setCd("PAT");
        perConn.setThePersonDto(perDt);
        personVOCollection.add(perConn);

        var edxRule = new EdxRuleManageDto();
        edxRule.setParticipationTypeCode("OrgAsReporterOfPHC");
        edxRule.setParticipationUid(11L);
        edxRule.setParticipationClassCode("Class");
        entities.add(edxRule);
        edxRule = new EdxRuleManageDto();
        edxRule.setParticipationTypeCode("PhysicianOfPHC");
        edxRule.setParticipationUid(11L);
        edxRule.setParticipationClassCode("Class");
        entities.add(edxRule);

        var res = autoInvestigationService.transferValuesTOActProxyVO(pageActProxyContainer, pamActProxyVO,
                personVOCollection,
                rootObservationVO,
                entities,
                questionIdentifierMap);

        assertNotNull(res);
    }


    @Test
    void transferValuesTOActProxyVO_Test_3() throws DataProcessingException {
        PageActProxyContainer pageActProxyContainer = null;
        PamProxyContainer pamActProxyVO = new PamProxyContainer();
        ArrayList<PersonContainer> personVOCollection = new ArrayList<>();
        ObservationContainer rootObservationVO = new ObservationContainer();
        ArrayList<Object> entities = new ArrayList<>();
        Map<Object, Object> questionIdentifierMap = new HashMap<>();
        questionIdentifierMap.put("NBS291", "TEST");

        var lstPat = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setTypeCd(EdxELRConstant.ELR_AUTHOR_CD);
        pat.setSubjectClassCd(EdxELRConstant.ELR_ORG);
        lstPat.add(pat);
        pat = new ParticipationDto();
        pat.setTypeCd(EdxELRConstant.ELR_ORDER_CD);
        pat.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
        lstPat.add(pat);
        pat = new ParticipationDto();
        pat.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
        pat.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
        lstPat.add(pat);
        pat = new ParticipationDto();
        pat.setTypeCd(EdxELRConstant.ELR_ORDERER_CD);
        pat.setSubjectClassCd(EdxELRConstant.ELR_ORG);
        pat.setCd(EdxELRConstant.ELR_OP_CD);
        lstPat.add(pat);

        rootObservationVO.setTheParticipationDtoCollection(lstPat);


        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setPublicHealthCaseUid(10L);
        phcConn.setThePublicHealthCaseDto(phcDt);
        pamActProxyVO.setPublicHealthCaseContainer(phcConn);
        var base = new BasePamContainer();
        pamActProxyVO.setPamVO(base);

        var perConn = new PersonContainer();
        var perDt = new PersonDto();
        perDt.setCd("PAT");
        perConn.setThePersonDto(perDt);
        personVOCollection.add(perConn);

        var edxRule = new EdxRuleManageDto();
        edxRule.setParticipationTypeCode("OrgAsReporterOfPHC_False");
        edxRule.setParticipationUid(11L);
        edxRule.setParticipationClassCode("Class");
        entities.add(edxRule);
        edxRule = new EdxRuleManageDto();
        edxRule.setParticipationTypeCode("PhysicianOfPHC_False");
        edxRule.setParticipationUid(11L);
        edxRule.setParticipationClassCode("Class");
        entities.add(edxRule);

        var res = autoInvestigationService.transferValuesTOActProxyVO(pageActProxyContainer, pamActProxyVO,
                personVOCollection,
                rootObservationVO,
                entities,
                questionIdentifierMap);

        assertNotNull(res);
    }

    @Test
    void populateProxyFromPrePopMapping_Test() throws DataProcessingException {
        PageActProxyContainer pageActProxyContainer = new PageActProxyContainer();
        EdxLabInformationDto edxLabInformationDT = new EdxLabInformationDto();


        var mapTree = new TreeMap<>();
        mapTree.put("OBS_1", new TreeMap<>());
        mapTree.put("CODED", new TreeMap<>());
        mapTree.put("OBS_1$CODED_2", "CODE");

        OdseCache.fromPrePopFormMapping.put(NEDSSConstant.LAB_FORM_CD, mapTree);




        var proxLab = new LabResultProxyContainer();
        var obsColl = new ArrayList<ObservationContainer>();
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setCd("OBS_1");
        var obsNumCol = new ArrayList<ObsValueNumericDto>();
        var obsNum = new ObsValueNumericDto();
        obsNum.setNumericUnitCd("ML");
        obsNum.setNumericValue1(BigDecimal.valueOf(10));
        obsNumCol.add(obsNum);
        obsConn.setTheObservationDto(obsDt);
        obsConn.setTheObsValueNumericDtoCollection(obsNumCol);
        obsColl.add(obsConn);

        obsConn = new ObservationContainer();
        obsDt = new ObservationDto();
        obsConn.setTheObservationDto(obsDt);
        var obsDateCol = new ArrayList<ObsValueDateDto>();
        var obsDate = new ObsValueDateDto();
        obsDate.setFromTime(TimeStampUtil.getCurrentTimeStamp());
        obsDateCol.add(obsDate);
        obsDt.setCd("OBS_1");
        obsConn.setTheObsValueDateDtoCollection(obsDateCol);
        obsColl.add(obsConn);

        obsConn = new ObservationContainer();
        obsDt = new ObservationDto();
        obsConn.setTheObservationDto(obsDt);
        var obsCodedCol = new ArrayList<ObsValueCodedDto>();
        var obsCoded = new ObsValueCodedDto();
        obsCoded.setCode("CODED");
        obsCodedCol.add(obsCoded);
        obsDt.setCd("OBS_1");
        obsConn.setTheObsValueCodedDtoCollection(obsCodedCol);
        obsColl.add(obsConn);

        obsConn = new ObservationContainer();
        obsDt = new ObservationDto();
        obsConn.setTheObservationDto(obsDt);
        obsCodedCol = new ArrayList<ObsValueCodedDto>();
        obsCoded = new ObsValueCodedDto();
        obsCoded.setCode("CODED_2");
        obsCodedCol.add(obsCoded);
        obsDt.setCd("OBS_1");
        obsConn.setTheObsValueCodedDtoCollection(obsCodedCol);
        obsColl.add(obsConn);

        obsConn = new ObservationContainer();
        obsDt = new ObservationDto();
        obsConn.setTheObservationDto(obsDt);
        var obsTxtCol = new ArrayList<ObsValueTxtDto>();
        var obsTxt = new ObsValueTxtDto();
        obsTxt.setTxtTypeCd(null);
        obsTxt.setValueTxt("TEST");
        obsTxtCol.add(obsTxt);
        obsDt.setCd("OBS_1");
        obsConn.setTheObsValueTxtDtoCollection(obsTxtCol);
        obsColl.add(obsConn);

        proxLab.setTheObservationContainerCollection(obsColl);

        edxLabInformationDT.setLabResultProxyContainer(proxLab);

        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setProgAreaCd("PROG");
        phcDt.setCd("CODE");
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageActProxyContainer.setPublicHealthCaseContainer(phcConn);


        List<ConditionCodeWithPA> codePaLst = new ArrayList<>();
        var condCodePa = new ConditionCodeWithPA();
        condCodePa.setConditionCd("CODE_NOT_MATCHED");
        codePaLst.add(condCodePa);
        when(conditionCodeRepository.findProgramAreaConditionCode(eq(1), any()))
                .thenReturn(Optional.of(codePaLst));

        codePaLst = new ArrayList<>();
        condCodePa = new ConditionCodeWithPA();
        condCodePa.setConditionCd("CODE");
        condCodePa.setConditionCodesetNm("SHORT");
        condCodePa.setStateProgAreaCode("PROG");
        condCodePa.setStateProgAreaCdDesc("PROG");
        condCodePa.setInvestigationFormCd("INVES");
        codePaLst.add(condCodePa);
        when(conditionCodeRepository.findProgramAreaConditionCode(eq(2), any()))
                .thenReturn(Optional.of(codePaLst));

        var dmb = new TreeMap<>();
        var nbsMeta = new NbsQuestionMetadata();
        nbsMeta.setDataLocation(RenderConstant.ANSWER_TXT);
        dmb.put("OBS_1", nbsMeta);
        OdseCache.dmbMap.put("INVES", dmb);

        var prePop = new TreeMap<>();
        var prePopDto = new PrePopMappingDto();
        prePopDto.setFromQuestionIdentifier("OBS_1");
        prePopDto.setToQuestionIdentifier("OBS_1");
        prePopDto.setFromAnswerCode("CODED_2");
        prePopDto.setToDataType(NEDSSConstant.DATE_DATATYPE);
        prePop.put("INVES", prePopDto);

        prePopDto = new PrePopMappingDto();
        prePopDto.setFromQuestionIdentifier("OBS_1");
        prePopDto.setToQuestionIdentifier("OBS_1");
        prePopDto.setFromAnswerCode("CODED_2");
        prePopDto.setToAnswerCode("CODED_2");
        prePopDto.setToDataType("BLAH");
        prePop.put("INVES_2", prePopDto);

        prePopDto = new PrePopMappingDto();
        prePopDto.setFromQuestionIdentifier("OBS_1");
        prePopDto.setToQuestionIdentifier("OBS_1");
        prePopDto.setFromAnswerCode("CODED_2");
        prePopDto.setToAnswerCode(null);
        prePopDto.setToDataType("BLAH");
        prePop.put("INVES_3", prePopDto);

        mapTree.put("QUES$ANS", "CODE");


        pageActProxyContainer.setPageVO(new BasePamContainer());

        when(lookupService.getToPrePopFormMapping("INVES"))
                .thenReturn(prePop);


        autoInvestigationService.populateProxyFromPrePopMapping(pageActProxyContainer, edxLabInformationDT);

        verify(lookupService, times(1)).getToPrePopFormMapping("INVES");
    }

}
