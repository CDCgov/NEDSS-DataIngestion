package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.service.implementation.jurisdiction.JurisdictionService;
import gov.cdc.dataprocessing.service.implementation.jurisdiction.ProgramAreaService;
import gov.cdc.dataprocessing.service.implementation.observation.ObservationService;
import gov.cdc.dataprocessing.service.implementation.organization.OrganizationService;
import gov.cdc.dataprocessing.service.implementation.person.PersonService;
import gov.cdc.dataprocessing.service.implementation.observation.ObservationMatchingService;
import gov.cdc.dataprocessing.service.implementation.other.UidService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.other.IUidService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerAggregationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationService;
import gov.cdc.dataprocessing.service.interfaces.person.IPersonService;
import gov.cdc.dataprocessing.service.model.PersonAggContainer;
import gov.cdc.dataprocessing.utilities.component.generic_helper.ManagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ManagerAggregationService implements IManagerAggregationService {

    ManagerUtil managerUtil;
    IOrganizationService organizationService;
    IPersonService patientService;
    IUidService uidService;
    IObservationService observationService;
    IObservationMatchingService observationMatchingService;
    IProgramAreaService programAreaService;
    IJurisdictionService jurisdictionService;


    public ManagerAggregationService(ManagerUtil managerUtil,
                                     OrganizationService organizationService,
                                     PersonService patientService,
                                     UidService uidService,
                                     ObservationService observationService,
                                     ObservationMatchingService observationMatchingService,
                                     ProgramAreaService programAreaService,
                                     JurisdictionService jurisdictionService) {
        this.managerUtil = managerUtil;
        this.organizationService = organizationService;
        this.patientService = patientService;
        this.uidService = uidService;
        this.observationService = observationService;
        this.observationMatchingService = observationMatchingService;
        this.programAreaService = programAreaService;
        this.jurisdictionService = jurisdictionService;
    }

    public void processingObservationMatching(EdxLabInformationDto edxLabInformationDto,
                                                       LabResultProxyContainer labResultProxyContainer,
                                                       Long aPersonUid) throws DataProcessingException {
        ObservationDto observationDto = observationMatchingService.checkingMatchingObservation(edxLabInformationDto);

        if(observationDto !=null){
            LabResultProxyContainer matchedlabResultProxyVO = observationService.getObservationToLabResultContainer(observationDto.getObservationUid());
            observationMatchingService.processMatchedProxyVO(labResultProxyContainer, matchedlabResultProxyVO, edxLabInformationDto );

            //TODO: CHECK THIS OUT
            aPersonUid = patientService.getMatchedPersonUID(matchedlabResultProxyVO);
            patientService.updatePersonELRUpdate(labResultProxyContainer, matchedlabResultProxyVO);

            edxLabInformationDto.setRootObserbationUid(observationDto.getObservationUid());
            if(observationDto.getProgAreaCd()!=null && observationDto.getJurisdictionCd()!=null)
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
        Collection<ObservationContainer> observationContainerCollection = labResult.getTheObservationContainerCollection();
        Collection<PersonContainer> personContainerCollection = labResult.getThePersonContainerCollection();

        observationAggregation(labResult, edxLabInformationDto, observationContainerCollection);
        patientAggregation(labResult, edxLabInformationDto, personContainerCollection);
        organizationService.processingOrganization(labResult);

    }


    public void serviceAggregationAsync(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws DataProcessingConsumerException,
            DataProcessingException {
        PersonAggContainer personAggContainer;
        OrganizationContainer organizationContainer;
        Collection<ObservationContainer> observationContainerCollection = labResult.getTheObservationContainerCollection();
        Collection<PersonContainer> personContainerCollection = labResult.getThePersonContainerCollection();

        CompletableFuture<Void> observationFuture = CompletableFuture.runAsync(() -> observationAggregation(labResult, edxLabInformationDto, observationContainerCollection));

        CompletableFuture<PersonAggContainer> patientFuture = CompletableFuture.supplyAsync(() ->
        {
            try {
                return patientAggregation(labResult, edxLabInformationDto, personContainerCollection);
            } catch (DataProcessingConsumerException | DataProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<OrganizationContainer> organizationFuture = CompletableFuture.supplyAsync(() ->
        {
            try {
               return organizationService.processingOrganization(labResult);
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

        // Get the results from CompletableFuture
        try {
            personAggContainer = patientFuture.get();
            organizationContainer = organizationFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DataProcessingException("Failed to get results", e);
        }

        // Pulling Jurisdiction and Program from OBS
        ObservationContainer orderTestVO = null;
        Collection<ObservationContainer> resultTests = new ArrayList<>();
        for (ObservationContainer obsVO : labResult
                .getTheObservationContainerCollection()) {
            String obsDomainCdSt1 = obsVO.getTheObservationDto()
                    .getObsDomainCdSt1();
            if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_RESULT_CD)) {
                resultTests.add(obsVO);
            } else if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)) {
                orderTestVO = obsVO;
            }
        }

        if(orderTestVO.getTheObservationDto().getProgAreaCd()==null)
        {
            programAreaService.getProgramArea(resultTests, orderTestVO, edxLabInformationDto.getSendingFacilityClia());
        }

        if(orderTestVO.getTheObservationDto().getJurisdictionCd()==null)
        {
            jurisdictionService.assignJurisdiction(personAggContainer.getPersonContainer(), personAggContainer.getProviderContainer(),
                    organizationContainer, orderTestVO);
        }
    }


    private void observationAggregation(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto, Collection<ObservationContainer> observationContainerCollection) {
        if (observationContainerCollection != null && !observationContainerCollection.isEmpty()) {
            for (ObservationContainer obsVO : observationContainerCollection) {
                if (obsVO.getTheObservationDto().getObservationUid() == edxLabInformationDto.getRootObserbationUid()
                        && edxLabInformationDto.getRootObserbationUid() > 0
                ) {
                    long falseUid = -1;
                    uidService.setFalseToNewForObservation(labResult, falseUid, obsVO.getTheObservationDto().getObservationUid());
                    if (obsVO.getTheActIdDtoCollection() != null) {
                        for (ActIdDto actIdDto : obsVO.getTheActIdDtoCollection()) {
                            actIdDto.setItNew(false);
                            actIdDto.setItDirty(true);
                            actIdDto.setActUid(obsVO.getTheObservationDto().getObservationUid());
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
