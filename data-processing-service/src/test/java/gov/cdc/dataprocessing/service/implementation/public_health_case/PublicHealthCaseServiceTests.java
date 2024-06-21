package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PublicHealthCaseServiceTests {
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    @InjectMocks
    private PublicHealthCaseService publicHealthCaseService;
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
        Mockito.reset(entityHelper, publicHealthCaseRepositoryUtil, authUtil);
    }


    @Test
    void setPublicHealthCase_Success() throws DataProcessingException {
        var phcConn = new PublicHealthCaseContainer();
        var actLocCol = new ArrayList<ActivityLocatorParticipationDto>();
        var actLoc = new ActivityLocatorParticipationDto();
        actLocCol.add(actLoc);
        phcConn.setTheActivityLocatorParticipationDTCollection(actLocCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        phcConn.setTheActRelationshipDTCollection(actReCol);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        phcConn.setTheParticipationDTCollection(patCol);

        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        phcConn.setItNew(true);

        when(entityHelper.iterateALPDTActivityLocatorParticipation(any())).thenReturn(new ArrayList<>());

        publicHealthCaseService.setPublicHealthCase(phcConn);

        verify(publicHealthCaseRepositoryUtil, times(1)).create(any());

    }

    @Test
    void setPublicHealthCase_Success_Update() throws DataProcessingException {
        var phcConn = new PublicHealthCaseContainer();
        var actLocCol = new ArrayList<ActivityLocatorParticipationDto>();
        var actLoc = new ActivityLocatorParticipationDto();
        actLocCol.add(actLoc);
        phcConn.setTheActivityLocatorParticipationDTCollection(actLocCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        phcConn.setTheActRelationshipDTCollection(actReCol);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        phcConn.setTheParticipationDTCollection(patCol);

        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        phcConn.setItNew(false);

        when(entityHelper.iterateALPDTActivityLocatorParticipation(any())).thenReturn(new ArrayList<>());

        publicHealthCaseService.setPublicHealthCase(phcConn);

        verify(publicHealthCaseRepositoryUtil, times(1)).update(any());

    }


    @Test
    void setPublicHealthCase_Exception() throws DataProcessingException {
        var phcConn = new PublicHealthCaseContainer();
        var actLocCol = new ArrayList<ActivityLocatorParticipationDto>();
        var actLoc = new ActivityLocatorParticipationDto();
        actLocCol.add(actLoc);
        phcConn.setTheActivityLocatorParticipationDTCollection(actLocCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        phcConn.setTheActRelationshipDTCollection(actReCol);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        phcConn.setTheParticipationDTCollection(patCol);

        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        phcConn.setItNew(true);

        when(entityHelper.iterateALPDTActivityLocatorParticipation(any())).thenThrow(
                new RuntimeException("TEST")
        );


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            publicHealthCaseService.setPublicHealthCase(phcConn);
        });


        assertNotNull(thrown);
        assertEquals("TEST", thrown.getMessage());
    }

}
