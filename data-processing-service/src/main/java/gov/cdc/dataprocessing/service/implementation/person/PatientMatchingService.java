package gov.cdc.dataprocessing.service.implementation.person;


import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.person.IPatientMatchingService;
import gov.cdc.dataprocessing.service.model.person.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;


@Service
public class PatientMatchingService extends PatientMatchingBaseService implements IPatientMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(PatientMatchingService.class);


    private final DibbsMatchService dibbsMatchService;
    private final NbsMatchService nbsMatchService;

    @Autowired
    public PatientMatchingService(
        DibbsMatchService dibbsMatchService,
        NbsMatchService nbsMatchService,
        EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
        EntityHelper entityHelper,
        PatientRepositoryUtil patientRepositoryUtil,
        CachingValueService cachingValueService,
        PrepareAssocModelHelper prepareAssocModelHelper
    ) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService,
            prepareAssocModelHelper);
        this.dibbsMatchService = dibbsMatchService;
        this.nbsMatchService = nbsMatchService;
    }

    @SuppressWarnings("java:S6541")
    @Transactional
    public EdxPatientMatchDto getMatchingPatient(PersonContainer personContainer, boolean isNbs)
        throws DataProcessingException, InterruptedException {

        Long patientUid;
        EdxPatientMatchDto edxPatientMatchFoundDT = null;
        PersonId patientPersonUid = null;
        boolean matchFound = false;

        boolean newPatientCreationApplied = false;

        if (isNbs) {
            matchFound = nbsMatchService.match(personContainer);
            edxPatientMatchFoundDT = nbsMatchService.getEdxPatientMatchFoundDT();
        } else {
            matchFound = dibbsMatchService.match(personContainer);
            edxPatientMatchFoundDT = dibbsMatchService.getEdxPatientMatchFoundDT();
        }

        // NOTE: Decision, Match Not Found, Start Person Creation
        if (!matchFound) {
            if (personContainer.getTheEntityIdDtoCollection() != null) {
                //SORTING out existing EntityId
                Collection<EntityIdDto> newEntityIdDtoColl = new ArrayList<>();
                for (EntityIdDto entityIdDto : personContainer.getTheEntityIdDtoCollection()) {
                    if (entityIdDto.getTypeCd() != null && !entityIdDto.getTypeCd().equalsIgnoreCase("LR")) {
                        newEntityIdDtoColl.add(entityIdDto);
                    }
                }
                personContainer.setTheEntityIdDtoCollection(newEntityIdDtoColl);
            }
            try {
                // NOTE: IF new patient then create
                // IF existing patient, then query find it, then Get Parent Patient ID
                if (personContainer.getThePersonDto().getCd().equals(NEDSSConstant.PAT)) { // Patient
                    patientPersonUid = setAndCreateNewPerson(personContainer);
                    personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
                    personContainer.getThePersonDto().setLocalId(patientPersonUid.getLocalId());
                    personContainer.getThePersonDto().setPersonUid(patientPersonUid.getPersonId());
                    newPatientCreationApplied = true;
                }
            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
                throw new DataProcessingException(
                    "Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
            }
            personContainer.setPatientMatchedFound(false);
        } else {
            personContainer.setPatientMatchedFound(true);
        }

        //NOTE: In this flow, if new patient, revision record is still get inserted
        //NOTE: if existing pateint, revision also insrted
        try {

            /**
             * NOTE:
             * Regarding New or Existing Patient
             * This logic will do Patient Hash update and do Patient Revision update
             * */

            /**
             * 2.0 NOTE: if new patient flow, skip revision
             * otherwise: go to update existing patient
             * */

            //REVISION
            if (!newPatientCreationApplied) {
                personContainer.getThePersonDto().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
            } else {
                personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
            }

            // SetPatientRevision

            patientUid = setPatientRevision(personContainer, NEDSSConstant.PAT_CR, NEDSSConstant.PAT);
            personContainer.getThePersonDto().setPersonUid(patientUid);

            //END REVISION

            //
            //                if (!newPatientCreationApplied && personContainer.getPatientMatchedFound()) {
            //                    personContainer.getThePersonDto().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
            //                    patientPersonUid = updateExistingPerson(personContainer, NEDSSConstant.PAT_CR, personContainer.getThePersonDto().getPersonParentUid());
            //
            //                    personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
            //                    personContainer.getThePersonDto().setLocalId(patientPersonUid.getLocalId());
            //                    personContainer.getThePersonDto().setPersonUid(patientPersonUid.getPersonId());
            //                }
            //                else if (newPatientCreationApplied) {
            //                    setPersonHashCdPatient(personContainer);
            //                }
        } catch (Exception e) {
            logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
            throw new DataProcessingException(
                "Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
        }
        return edxPatientMatchFoundDT;
    }

    public boolean getMultipleMatchFound() {
        return nbsMatchService.getMultipleMatchFound();
    }

    @Transactional
    public Long updateExistingPerson(PersonContainer personContainer, String businessTriggerCd)
        throws DataProcessingException {
        return updateExistingPerson(personContainer, businessTriggerCd,
            personContainer.getThePersonDto().getPersonParentUid()).getPersonId();
    }

}











