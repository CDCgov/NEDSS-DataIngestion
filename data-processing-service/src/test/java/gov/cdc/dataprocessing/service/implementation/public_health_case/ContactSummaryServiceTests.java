package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.CTConstants;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.phc.CTContactSummaryDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonNameRepository;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
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
import static org.mockito.Mockito.when;

class ContactSummaryServiceTests {
    @Mock
    private QueryHelper queryHelper;
    @Mock
    private PersonNameRepository personNameRepository;
    @Mock
    private CustomRepository customRepository;
    @Mock
    private IRetrieveSummaryService retrieveSummaryService;
    
    @InjectMocks
    private ContactSummaryService contactSummaryService;
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
        Mockito.reset(queryHelper, personNameRepository, customRepository, retrieveSummaryService, authUtil);
    }

    @Test
    void getContactListForInvestigation_Success() throws DataProcessingException {
        long phcUid = 10L;

        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.CT_CONTACT,"VIEW", "")).thenReturn("BLAH");
        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "")).thenReturn("BLAH");

        var contactSumCol = new ArrayList<CTContactSummaryDto>();
        var contactSum = new CTContactSummaryDto();
        contactSum.setContactEntityUid(11L);
        contactSum.setThirdPartyEntityUid(12L);
        contactSum.setContactProcessingDecisionCd(CTConstants.RecordSearchClosure);
        contactSum.setDispositionCd("A");
        contactSumCol.add(contactSum);
        when(customRepository.getContactByPatientInfo(any())).thenReturn(contactSumCol);
        var personNameLst = new ArrayList<PersonName>();
        var personName = new PersonName();
        personName.setLastNm("TEST");
        personName.setFirstNm("TEST");
        personName.setNmUseCd(NEDSSConstant.LEGAL_NAME);
        personNameLst.add(personName);
        when(personNameRepository.findByParentUid(11L)).thenReturn(Optional.of(personNameLst));
        when(personNameRepository.findByParentUid(12L)).thenReturn(Optional.of(personNameLst));

        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "")).thenReturn("BLAH");
        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.CT_CONTACT, "VIEW", "")).thenReturn("BLAH");


        var contactSumCol2 = new ArrayList<CTContactSummaryDto>();
        var contactSum2 = new CTContactSummaryDto();
        contactSum2.setContactEntityUid(13L);
        contactSum2.setThirdPartyEntityUid(14L);
        contactSum2.setCtContactUid(15L);
        contactSum2.setSubjectEntityUid(16L);
        contactSum2.setContactProcessingDecisionCd(CTConstants.RecordSearchClosure);
        contactSum2.setDispositionCd("A");
        contactSumCol2.add(contactSum2);

        when(customRepository.getContactByPatientInfo(any())).thenReturn(contactSumCol2);
        when(personNameRepository.findByParentUid(16L)).thenReturn(Optional.of(personNameLst));
        when(personNameRepository.findByParentUid(13L)).thenReturn(Optional.of(personNameLst));
        when(personNameRepository.findByParentUid(14L)).thenReturn(Optional.of(personNameLst));


        var test = contactSummaryService.getContactListForInvestigation(phcUid);

        assertNotNull(test);
        assertEquals(3, test.size());

    }
}
