package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.service.interfaces.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static gov.cdc.dataprocessing.constant.ManagerEvent.EVENT_ELR;
@Service
@Slf4j
public class ManagerService implements IManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    private final IObservationService observationService;
    private final IPatientService patientService;
    private final IOrganizationService organizationService;
    private final IProgramAreaJurisdictionService programAreaJurisdictionService;
    private final ILabProcessingService labProcessingService;

    private final IPublicHealthCaseService publicHealthCaseService;
    private final IEdxLogService edxLogService;

    private final IHandleLabService handleLabService;

    public ManagerService(IObservationService observationService,
                          IPatientService patientService,
                          IOrganizationService organizationService,
                          IProgramAreaJurisdictionService programAreaJurisdictionService,
                          ILabProcessingService labProcessingService,
                          IPublicHealthCaseService publicHealthCaseService,
                          IEdxLogService edxLogService, IHandleLabService handleLabService) {
        this.observationService = observationService;
        this.patientService = patientService;
        this.organizationService = organizationService;
        this.programAreaJurisdictionService = programAreaJurisdictionService;
        this.labProcessingService = labProcessingService;
        this.publicHealthCaseService = publicHealthCaseService;
        this.edxLogService = edxLogService;
        this.handleLabService = handleLabService;
    }

    public Object processDistribution(String eventType, String data) throws DataProcessingConsumerException {
        //TODO: determine which flow the data will be going through
        Object result = new Object();
        switch (eventType) {
            case EVENT_ELR:
                result = processingELR(data);
                break;
            default:
                break;
        }
        return result;
    }

    public Object processingHealthCase(String data) throws DataProcessingConsumerException {
        //TODO logic to for Health Case Flow
        Object result = new Object();
        try {
            //TODO: Public Health Case
            var healthCase = publicHealthCaseService.processingPublicHealthCase();

            //TODO: Auto Investigation
            var autoInvestigation = publicHealthCaseService.processingAutoInvestigation();
            return result;
        } catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), result);
        }
    }

    public Object processingHandleLab(String data)  throws DataProcessingConsumerException {
        //TODO logic to for Health Case Flow
        Object result = new Object();
        try {
            //TODO: Handling Lab Code
            boolean isLabReviewed = true;
            boolean isActExist = true;
            if (isLabReviewed) {
                result = handleLabService.processingReviewedLab();
            }
            else {
                if (isActExist) {
                    result = handleLabService.processingNonReviewLabWithAct();
                }
                else {
                    result = handleLabService.processingNonReviewLabWithoutAct();
                }
            }

            //TODO: Auto Investigation
            return result;
        } catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), result);
        }
    }

    public void processingEdxLog(String data) throws EdxLogException {
        edxLogService.processingLog();
    }



    private Object processingELR(String data) throws DataProcessingConsumerException {
        //TODO logic to execute data here
        Object result = new Object();
        try {
            //TODO: OBSERVATION
            var observation = observationService.processingObservation();

            //TODO: PATIENT
            var patient = patientService.processingPatient();
            var nextOfKin = patientService.processingNextOfKin();
            var provider = patientService.processingProvider();

            //TODO: ORGANIZATION
            var organization = organizationService.processingOrganization();

            //TODO: PROGRAM AREA
            var programArea = programAreaJurisdictionService.processingProgramArea();
            var jurisdiction = programAreaJurisdictionService.processingJurisdiction();

            //TODO: LAB PROCESSING
            var labProcessing = labProcessingService.processingLabResult();

            //TODO: Producing msg for Next Step
           // kafkaManagerProducer.sendData(healthCaseTopic, data);
            return result;
        } catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), result);
        }
    }
}
