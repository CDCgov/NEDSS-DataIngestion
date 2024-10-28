package gov.cdc.dataprocessing.utilities.component.nbs;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.NbsDocumentContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocumentHist;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsDocumentHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsDocumentRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NbsDocumentRepositoryUtilTest {
    @Mock
    private CustomRepository customRepository;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private ParticipationRepositoryUtil participationRepositoryUtil;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private NbsDocumentRepository nbsDocumentRepository;

    @Mock
    private NbsDocumentHistRepository nbsDocumentHistRepository;
    @InjectMocks
    private NbsDocumentRepositoryUtil nbsDocumentRepositoryUtil;
    @Mock
    AuthUtil authUtil;

    @Mock
    private NBSDocumentDto nbsDocumentDto;


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
        Mockito.reset(customRepository, patientRepositoryUtil,participationRepositoryUtil, prepareAssocModelHelper,
                nbsDocumentRepository,nbsDocumentHistRepository,authUtil);
    }

    @Test
    void getNBSDocumentWithoutActRelationship_Test() throws DataProcessingException {
        Long uid = 10L;
        when(customRepository.getNbsDocument(uid)).thenReturn(new NbsDocumentContainer());
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(new PersonContainer());
        when(participationRepositoryUtil.getParticipation(any(), any())).thenReturn(new ParticipationDto());

        var res = nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(uid);

        assertNotNull(res);
    }

    @Test
    void getNBSDocumentWithoutActRelationship_Test_2() throws DataProcessingException {
        Long uid = 10L;
        when(customRepository.getNbsDocument(uid)).thenReturn(new NbsDocumentContainer());
        when(patientRepositoryUtil.loadPerson(any())).thenReturn(new PersonContainer());
        when(participationRepositoryUtil.getParticipation(any(), any())).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(uid);
        });
        assertNotNull(thrown);
    }

    @Test
    void updateDocumentWithOutthePatient_Test() throws DataProcessingException {
        NbsDocumentContainer doc = new NbsDocumentContainer();
        doc.getNbsDocumentDT().setNbsDocumentMetadataUid(10L);
        doc.getNbsDocumentDT().setRecordStatusCd( NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE);
        doc.setFromSecurityQueue(true);
        when(customRepository.getNbsDocument(any())).thenReturn(new NbsDocumentContainer());

        var docDto = new NBSDocumentDto();
        docDto.setDocPayload("TEST");
        docDto.setPhdcDocDerived("TEST");
        when(prepareAssocModelHelper.prepareVO(any(), any(), any(), any(), any(), any())).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            nbsDocumentRepositoryUtil.updateDocumentWithOutthePatient(doc);
        });
        assertNotNull(thrown);

    }


    @Test
    void testInsertNBSDocumentHist() {
        // Act
        nbsDocumentRepositoryUtil.insertNBSDocumentHist(nbsDocumentDto);

        // Assert
        verify(nbsDocumentHistRepository).save(any(NbsDocumentHist.class));
    }
}
