package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.service.implementation.person.PatientMatchingService;
import gov.cdc.dataprocessing.service.implementation.person.ProviderMatchingService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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

    @SuppressWarnings("java:S3776")

    public Long processLabPersonContainerCollection(Collection<PersonContainer> personContainerCollection, boolean morbidityApplied,
                                                    BaseContainer dataContainer) throws DataProcessingException {
        if (personContainerCollection == null || personContainerCollection.isEmpty()) {
            throw new DataProcessingException("Person container collection is null");
        }


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
                    morbidityApplied
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
    @SuppressWarnings("java:S2589")
    private Long setPersonForObservationFlow(String personType, PersonContainer personVO, boolean isNew, boolean isExternal) throws DataProcessingException
    {
            if (personType.equalsIgnoreCase(NEDSSConstant.PAT))
            {
                return patientMatchingService.updateExistingPerson(personVO, isNew ? NEDSSConstant.PAT_CR : NEDSSConstant.PAT_EDIT);
            }
            else if (personType.equalsIgnoreCase(NEDSSConstant.PRV) && (!isNew || isExternal))
            {
                return providerMatchingService.setProvider(personVO, isNew ? NEDSSConstant.PRV_CR : NEDSSConstant.PRV_EDIT);
            }
            else
            {
                throw new IllegalArgumentException("Expected a valid person type: " + personType);
            }
    }



}
