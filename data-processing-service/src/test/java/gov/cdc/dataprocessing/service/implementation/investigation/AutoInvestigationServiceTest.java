package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.lookup.PrePopMappingDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCodeWithPA;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.ConditionCodeRepository;
import gov.cdc.dataprocessing.service.implementation.auth_user.AuthUserService;
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

import java.util.*;

import static gov.cdc.dataprocessing.cache.SrteCache.jurisdictionCodeMapWithNbsUid;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AutoInvestigationServiceTest {
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

        jurisdictionCodeMapWithNbsUid.put("STATE", 1);

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
        var tree =new TreeMap<String, String>();
        tree.put("PAT", "PAT");
        when(catchingValueService.getCodedValue(any()))
                .thenReturn(tree);


        var test = (PageActProxyContainer) autoInvestigationService.transferValuesTOActProxyVO(pageActProxyContainer, pamActProxyVO,
                personVOCollection, rootObservationVO, entities, questionIdentifierMap);


        assertNotNull(test);
        assertEquals(2, test.getTheParticipationDtoCollection().size());

    }
}
