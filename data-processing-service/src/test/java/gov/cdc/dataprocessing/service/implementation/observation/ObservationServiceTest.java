package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.service.implementation.organization.OrganizationService;
import gov.cdc.dataprocessing.service.interfaces.act.IActRelationshipService;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.log.IMessageLogService;
import gov.cdc.dataprocessing.service.interfaces.log.INNDActivityLogService;
import gov.cdc.dataprocessing.service.interfaces.material.IMaterialService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IEdxDocumentService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationCodeService;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.paticipation.IParticipationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.role.IRoleService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PersonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObservationServiceTest {
    @Mock
    private INNDActivityLogService nndActivityLogService;
    @Mock
    private IMessageLogService messageLogService;
    @Mock
    private ObservationRepositoryUtil observationRepositoryUtil;
    @Mock
    private INotificationService notificationService;
    @Mock
    private IMaterialService materialService;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private IRoleService roleService;
    @Mock
    private IActRelationshipService actRelationshipService;
    @Mock
    private IEdxDocumentService edxDocumentService;
    @Mock
    private IAnswerService answerService;
    @Mock
    private IParticipationService participationService;
    @Mock
    private ObservationRepository observationRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private IJurisdictionService jurisdictionService;
    @Mock
    private OrganizationRepositoryUtil organizationRepositoryUtil;
    @Mock
    private IObservationCodeService observationCodeService;
    @Mock
    private ObservationUtil observationUtil;
    @Mock
    private PersonUtil personUtil;
    @Mock
    private IProgramAreaService programAreaService;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private IUidService uidService;

    @Mock
    private IInvestigationService investigationService;

    @InjectMocks
    private ObservationService observationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() {
        Mockito.reset(nndActivityLogService, messageLogService, observationRepositoryUtil, notificationService,
                materialService, patientRepositoryUtil, roleService, actRelationshipService, edxDocumentService,
                answerService, participationService, observationRepository, personRepository, jurisdictionService,
                organizationRepositoryUtil, observationCodeService, observationUtil, personUtil, programAreaService,
                prepareAssocModelHelper, uidService, investigationService);
    }

    @Test
    void getObservationToLabResultContainer_ObservationUidIsNull_ShouldThrowException() {
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationService.getObservationToLabResultContainer(null);
        });

        assertEquals("HL7CommonLabUtil.getLabResultToProxy observationUid is null", thrown.getMessage());
    }

    @Test
    void getObservationToLabResultContainer_ReturnData() {

    }

}
