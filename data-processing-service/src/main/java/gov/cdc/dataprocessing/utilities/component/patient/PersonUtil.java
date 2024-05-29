package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.service.implementation.person.PatientMatchingService;
import gov.cdc.dataprocessing.service.implementation.person.ProviderMatchingService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PersonUtil {
    private final ObservationUtil observationUtil;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final PatientMatchingService patientMatchingService;
    private final ProviderMatchingService providerMatchingService;
    private final IUidService uidService;

    public PersonUtil(ObservationUtil observationUtil,
                      PatientRepositoryUtil patientRepositoryUtil,
                      PatientMatchingService patientMatchingService,
                      ProviderMatchingService providerMatchingService,
                      IUidService uidService) {
        this.observationUtil = observationUtil;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.patientMatchingService = patientMatchingService;
        this.providerMatchingService = providerMatchingService;
        this.uidService = uidService;
    }


    @Transactional
    public Long processLabPersonContainerCollection(Collection<PersonContainer> personContainerCollection, boolean morbidityApplied,
                                             BaseContainer dataContainer) throws DataProcessingException {
        if (personContainerCollection == null || personContainerCollection.isEmpty()) {
            throw new DataProcessingException("Person container collection is null");
        }

        boolean isMorbReport = morbidityApplied;


        PersonContainer personContainer;
        Long patientMprUid = null;
        ObservationDto rootDT = observationUtil.getRootObservationDto(dataContainer);

        for (PersonContainer item : personContainerCollection) {
            personContainer = item;

            if (personContainer == null) {
                continue;
            }

            //Finds out the type of person being processed and if it is a new person object,
            //and abort the processing if the parameters not provided or provided incorrectly
            String personType = personContainer.getThePersonDto().getCd();
            boolean isNewVO = personContainer.isItNew();

            if (personType == null) {
                throw new DataProcessingException("Expected a non-null person type cd for this person uid: " + personContainer.getThePersonDto().getPersonUid());
            }


            //Persists the person object
            boolean isExternal = false;
            String electronicInd = rootDT.getElectronicInd();
            if (
                electronicInd != null
                && (
                    isMorbReport
                    && electronicInd.equals(NEDSSConstant.EXTERNAL_USER_IND)
                    || electronicInd.equals(NEDSSConstant.YES)
                )
            ) {
                isExternal = true;
            }
            Long realUid;
            if (personContainer.getRole() == null) {
                realUid = setPersonForObservationFlow(personType, personContainer, isNewVO, isExternal);
            } else {
                realUid = personContainer.getThePersonDto().getPersonUid();
            }


            //If it is a new person object, updates the associations with the newly created uid
            if (isNewVO && realUid != null) {
                Long falseUid = personContainer.getThePersonDto().getPersonUid();

                if (falseUid.intValue() < 0) {
                    uidService.setFalseToNewForObservation(dataContainer, falseUid, realUid);
                    //set the realUid to person after it has been set to participation
                    //this will help for jurisdiction derivation, this is only local to this call
                    personContainer.getThePersonDto().setPersonUid(realUid);
                }
            }

            //If it is patient, return the mpr uid, assuming only one patient in this processing
            if (personType.equalsIgnoreCase(NEDSSConstant.PAT)) {
                patientMprUid = patientRepositoryUtil.findPatientParentUidByUid(realUid);
            }
        }

        return patientMprUid;

    }



    /**
     * Description: determine person is PAT or PROVIDER, then create or update based on isNEW arg
     * */
    private Long setPersonForObservationFlow(String personType, PersonContainer personVO, boolean isNew, boolean isExternal) throws DataProcessingException
    {
        try
        {
            if (personType.equalsIgnoreCase(NEDSSConstant.PAT))
            {
                return patientMatchingService.updateExistingPerson(personVO, isNew ? NEDSSConstant.PAT_CR : NEDSSConstant.PAT_EDIT);
            }
            else if (personType.equalsIgnoreCase(NEDSSConstant.PRV) && (!isNew || (isNew && isExternal)))
            {
                return providerMatchingService.setProvider(personVO, isNew ? NEDSSConstant.PRV_CR : NEDSSConstant.PRV_EDIT);
            }
            else
            {
                throw new IllegalArgumentException("Expected a valid person type: " + personType);
            }
        }
        catch (Exception rex)
        {
            throw new DataProcessingException(rex.getMessage(), rex);
        }
    }



}
