package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.service.interfaces.person.IPersonService;
import gov.cdc.dataprocessing.service.model.person.PersonAggContainer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class ManagerUtil {

    private final IPersonService patientService;

    public ManagerUtil(IPersonService patientService) {
        this.patientService = patientService;
    }

    /**
     * Description: Assign person Uid to the Observation, This happen on the matched observation flow
     * */
    public void setPersonUIDOnUpdate(Long aPersonUid, LabResultProxyContainer labResultProxyVO) {
        Collection<PersonContainer> personCollection = labResultProxyVO.getThePersonContainerCollection();
        if(personCollection!=null){
            for (PersonContainer personVO : personCollection) {
                String perDomainCdStr = personVO.getThePersonDto().getCdDescTxt();
                if (perDomainCdStr != null && perDomainCdStr.equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)) {
                    personVO.setItDirty(true);
                    personVO.setItNew(false);
                    personVO.getThePersonDto().setPersonUid(aPersonUid);
                    personVO.getThePersonDto().setItDirty(true);
                    personVO.getThePersonDto().setItNew(false);
                    personVO.setRole(null);
                }
            }
        }
    }

    /**
     * Original name: getOrderedTest
     * */
    public ObservationContainer getObservationWithOrderDomainCode(LabResultProxyContainer labResultProxyVO) {
        for (ObservationContainer obsVO : labResultProxyVO.getTheObservationContainerCollection()) {
            String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();
            if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)) {
                return obsVO;
            }
        }
        return null;
    }


    @SuppressWarnings("java:S3776")
    public PersonAggContainer patientAggregation(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws DataProcessingConsumerException, DataProcessingException {

        PersonAggContainer container = new PersonAggContainer();
        PersonContainer personContainerObj = null;
        PersonContainer providerVOObj = null;
        if (labResult.getThePersonContainerCollection() != null && !labResult.getThePersonContainerCollection().isEmpty() ) {
            Iterator<PersonContainer> it = labResult.getThePersonContainerCollection().iterator();
            boolean orderingProviderIndicator = false;

            while (it.hasNext()) {
                PersonContainer personContainer = it.next();
                if (personContainer.getRole() != null && personContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                    patientService.processingNextOfKin(labResult, personContainer);

                }
                else {
                    if (personContainer.thePersonDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_CD)) {
                        personContainerObj =  patientService.processingPatient(labResult, edxLabInformationDto, personContainer);
                    }
                    else if (personContainer.thePersonDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PROVIDER_CD)) {
                        var prv = patientService.processingProvider(labResult, edxLabInformationDto, personContainer, orderingProviderIndicator);
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


    /**
     * This wont work in this Transactional architecture
     * As we update the person and its assoc tables serveral time, so we must keep the Transactional as synchronous flow
     * */
    @SuppressWarnings("java:S3776")

    public PersonAggContainer personAggregationAsync(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        PersonAggContainer container = new PersonAggContainer();
        CompletableFuture<PersonContainer> patientFuture = null;
        CompletableFuture<PersonContainer> providerFuture = null;
        CompletableFuture<Void> nextOfKinFuture = null;

        if (labResult.getThePersonContainerCollection() != null && !labResult.getThePersonContainerCollection().isEmpty()) {
            for (PersonContainer personContainer : labResult.getThePersonContainerCollection()) {
                // Expecting multiple NOK
                // NOK info wont be return
                if (personContainer.getRole() != null && personContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                    if (nextOfKinFuture == null) {
                        nextOfKinFuture = CompletableFuture.runAsync(() -> {
                            try {
                                patientService.processingNextOfKin(labResult, personContainer);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        nextOfKinFuture = nextOfKinFuture.thenRunAsync(() -> {
                            try {
                                patientService.processingNextOfKin(labResult, personContainer);
                            } catch (DataProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
                // Expecting single patient
                // patient uid is needed in return
                else if (personContainer.thePersonDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_CD)) {
                    // Asynchronously process Patient
                    if (patientFuture == null) {
                        patientFuture = CompletableFuture.supplyAsync(() -> {
                            try {
                                return patientService.processingPatient(labResult, edxLabInformationDto, personContainer);
                            } catch (DataProcessingConsumerException | DataProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                }
                // Expecting single provider
                // provider uid is needed in return
                else if (personContainer.thePersonDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PROVIDER_CD) && providerFuture == null) {
                    // Asynchronously process Provider
                    providerFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            return patientService.processingProvider(labResult, edxLabInformationDto, personContainer, false);
                        } catch (DataProcessingConsumerException | DataProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                nextOfKinFuture != null ? nextOfKinFuture : CompletableFuture.completedFuture(null),
                patientFuture != null ? patientFuture : CompletableFuture.completedFuture(null),
                providerFuture != null ? providerFuture : CompletableFuture.completedFuture(null)
        );

        try {
            allFutures.get(); // Wait for all futures to complete
            if (patientFuture != null) {
                container.setPersonContainer(patientFuture.get()); // Set patient
            }
            if (providerFuture != null) {
                container.setPersonContainer(providerFuture.get());
            }
            // You can similarly set provider or other information if needed here
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DataProcessingException("Thread was interrupted", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException && cause.getCause() instanceof DataProcessingException) {
                throw (DataProcessingException) cause.getCause();
            } else {
                throw new DataProcessingException("Error processing lab results", e);
            }
        }


        return container;
    }




}
