package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActIdDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.service.implementation.matching.ObservationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.core.*;
import gov.cdc.dataprocessing.service.interfaces.matching.IObservationMatchingService;
import gov.cdc.dataprocessing.service.model.PersonAggContainer;
import gov.cdc.dataprocessing.utilities.component.ManagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ManagerAggregationService implements IManagerAggregationService {

    ManagerUtil managerUtil;
    IOrganizationService organizationService;
    IPatientService patientService;
    IUidService uidService;
    IObservationService observationService;
    IObservationMatchingService observationMatchingService;

    public ManagerAggregationService(ManagerUtil managerUtil,
                                     OrganizationService organizationService,
                                     PatientService patientService,
                                     UidService uidService,
                                     ObservationService observationService,
                                     ObservationMatchingService observationMatchingService) {
        this.managerUtil = managerUtil;
        this.organizationService = organizationService;
        this.patientService = patientService;
        this.uidService = uidService;
        this.observationService = observationService;
        this.observationMatchingService = observationMatchingService;
    }

    public void processingObservationMatching(EdxLabInformationDto edxLabInformationDto,
                                                       LabResultProxyContainer labResultProxyContainer,
                                                       Long aPersonUid) throws DataProcessingException {
        ObservationDT observationDT = observationMatchingService.checkingMatchingObservation(edxLabInformationDto);

        if(observationDT!=null){
            LabResultProxyContainer matchedlabResultProxyVO = observationService.getObservationToLabResultContainer(observationDT.getObservationUid());
            observationMatchingService.processMatchedProxyVO(labResultProxyContainer, matchedlabResultProxyVO, edxLabInformationDto );

            //TODO: CHECK THIS OUT
            aPersonUid = patientService.getMatchedPersonUID(matchedlabResultProxyVO);
            patientService.updatePersonELRUpdate(labResultProxyContainer, matchedlabResultProxyVO);

            edxLabInformationDto.setRootObserbationUid(observationDT.getObservationUid());
            if(observationDT.getProgAreaCd()!=null && observationDT.getJurisdictionCd()!=null)
            {
                edxLabInformationDto.setLabIsUpdateDRRQ(true);
            }
            else
            {
                edxLabInformationDto.setLabIsUpdateDRSA(true);
            }
            edxLabInformationDto.setPatientMatch(true);
        }
        else {
            edxLabInformationDto.setLabIsCreate(true);
        }
    }

    public void serviceAggregation(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws DataProcessingConsumerException,
            DataProcessingException {
        Collection<ObservationVO> observationVOCollection = labResult.getTheObservationVOCollection();
        Collection<PersonContainer> personContainerCollection = labResult.getThePersonContainerCollection();

        observationAggregation(labResult, edxLabInformationDto, observationVOCollection);
        patientAggregation(labResult, edxLabInformationDto, personContainerCollection);
        organizationService.processingOrganization(labResult);

    }


    public void serviceAggregationAsync(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws DataProcessingConsumerException,
            DataProcessingException {
        Collection<ObservationVO> observationVOCollection = labResult.getTheObservationVOCollection();
        Collection<PersonContainer> personContainerCollection = labResult.getThePersonContainerCollection();

        CompletableFuture<Void> observationFuture = CompletableFuture.runAsync(() ->
                observationAggregation(labResult, edxLabInformationDto, observationVOCollection));

        CompletableFuture<Void> patientFuture = CompletableFuture.runAsync(() ->
        {
            try {
                patientAggregation(labResult, edxLabInformationDto, personContainerCollection);
            } catch (DataProcessingConsumerException | DataProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<Void> organizationFuture = CompletableFuture.runAsync(() ->
        {
            try {
                organizationService.processingOrganization(labResult);
            } catch (DataProcessingConsumerException e) {
                throw new RuntimeException(e);
            }
        });

        // Wait for all tasks to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(observationFuture, patientFuture, organizationFuture);

        try {
            allFutures.get(); // Wait for all tasks to complete
        } catch (InterruptedException | ExecutionException e) {
            throw new DataProcessingException("Failed to execute tasks", e);
        }
    }


    private void observationAggregation(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto, Collection<ObservationVO> observationVOCollection) {
        if (observationVOCollection != null && !observationVOCollection.isEmpty()) {
            for (ObservationVO obsVO : observationVOCollection) {
                if (obsVO.getTheObservationDT().getObservationUid() == edxLabInformationDto.getRootObserbationUid()
                        && edxLabInformationDto.getRootObserbationUid() > 0
                ) {
                    long falseUid = -1;
                    uidService.setFalseToNewForObservation(labResult, falseUid, obsVO.getTheObservationDT().getObservationUid());
                    if (obsVO.getTheActIdDtoCollection() != null) {
                        for (ActIdDto actIdDto : obsVO.getTheActIdDtoCollection()) {
                            actIdDto.setItNew(false);
                            actIdDto.setItDirty(true);
                            actIdDto.setActUid(obsVO.getTheObservationDT().getObservationUid());
                        }
                    }
                    break;
                }
            }
        }


    }

    private PersonAggContainer patientAggregation(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto,
                                                  Collection<PersonContainer>  personContainerCollection) throws DataProcessingConsumerException, DataProcessingException {

        PersonAggContainer container = new PersonAggContainer();
        PersonContainer personContainerObj = null;
        PersonContainer providerVOObj = null;
        if (personContainerCollection != null && !personContainerCollection.isEmpty() ) {
            Iterator<PersonContainer> it = personContainerCollection.iterator();
            boolean orderingProviderIndicator = false;

            while (it.hasNext()) {
                PersonContainer personContainer = it.next();
                if (personContainer.getRole() != null && personContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                    patientService.processingNextOfKin(labResultProxyContainer, personContainer);

                }
                else {
                    if (personContainer.thePersonDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_CD)) {
                        personContainerObj =  patientService.processingPatient(labResultProxyContainer, edxLabInformationDto, personContainer);
                    }
                    else if (personContainer.thePersonDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PROVIDER_CD)) {
                        var prv = patientService.processingProvider(labResultProxyContainer, edxLabInformationDto, personContainer, orderingProviderIndicator);
                        if (prv != null) {
                            providerVOObj = prv;
                        }
                    }
                }
            }
        }

        container.setPersonContainer(personContainerObj);
        container.setProviderContainer(providerVOObj);
        return container;
    }
}
