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

@Component

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


}
