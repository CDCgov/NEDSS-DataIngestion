package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.service.implementation.other.CachingValueService;
import gov.cdc.dataprocessing.service.interfaces.person.INokMatchingService;
import gov.cdc.dataprocessing.service.implementation.person.base.NokMatchingBaseService;
import gov.cdc.dataprocessing.service.model.person.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
public class NokMatchingService  extends NokMatchingBaseService implements INokMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(NokMatchingService.class);

    public NokMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueService cachingValueService,
            PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService, prepareAssocModelHelper);
    }
    @Transactional
    public EdxPatientMatchDto getMatchingNextOfKin(PersonContainer personContainer) throws DataProcessingException {
        Long patientUid = personContainer.getThePersonDto().getPersonUid();
        EdxPatientMatchDto edxPatientFoundDT = null;
        EdxPatientMatchDto edxPatientMatchFoundDT = null;
        PersonId patientPersonUid = null;
        boolean matchFound = false;
        boolean newPersonCreationApplied = false;

        String nameAddStrSt1 = null;
        int nameAddStrSt1hshCd = 0;
        List nameAddressStreetOneStrList = nameAddressStreetOneNOK(personContainer);

        if (nameAddressStreetOneStrList != null && !nameAddressStreetOneStrList.isEmpty()) {
            for (int k = 0; k < nameAddressStreetOneStrList.size(); k++) {
                nameAddStrSt1 = (String) nameAddressStreetOneStrList.get(k);
                if (nameAddStrSt1 != null) {
                    nameAddStrSt1 = nameAddStrSt1.toUpperCase();
                    nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
                    try {
                        if (nameAddStrSt1 != null) {
                            edxPatientFoundDT = new EdxPatientMatchDto();
                            edxPatientFoundDT.setPatientUid(patientUid);
                            edxPatientFoundDT.setTypeCd(NEDSSConstant.NOK);
                            edxPatientFoundDT.setMatchString(nameAddStrSt1);
                            edxPatientFoundDT.setMatchStringHashCode((long)(nameAddStrSt1hshCd));
                        }
                        // Try to get the Next of Kin matching with the match string
                        edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(edxPatientFoundDT.getTypeCd(), nameAddStrSt1);
                        if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
                            matchFound = false;
                        } else {
                            matchFound = true;
                        }
                    } catch (Exception ex) {
                        logger.error("Error in geting the  matching Next of Kin");
                        throw new DataProcessingException("Error in geting the  matching Next of Kin" + ex.getMessage(), ex);
                    }
                }
            }
        }

        if (!matchFound) {
            String nameTelePhone = null;
            int nameTelePhonehshCd = 0;
            List nameTelePhoneStrList = telePhoneTxtNOK(personContainer);
            if (nameTelePhoneStrList != null && !nameTelePhoneStrList.isEmpty()) {
                for (int k = 0; k < nameTelePhoneStrList.size(); k++) {
                    nameTelePhone = (String) nameTelePhoneStrList.get(k);
                    if (nameTelePhone != null) {
                        nameTelePhone = nameTelePhone.toUpperCase();
                        nameTelePhonehshCd = nameTelePhone.hashCode();
                        try {
                            if (nameTelePhone != null) {
                                edxPatientFoundDT = new EdxPatientMatchDto();
                                edxPatientFoundDT.setPatientUid(patientUid);
                                edxPatientFoundDT.setTypeCd(NEDSSConstant.NOK);
                                edxPatientFoundDT.setMatchString(nameTelePhone);
                                edxPatientFoundDT.setMatchStringHashCode((long)(nameTelePhonehshCd));
                            }
                            // Try to get the matching with the match string
                            edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(edxPatientFoundDT.getTypeCd(), nameTelePhone);
                            if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
                                matchFound = false;
                            } else {
                                matchFound = true;
                            }
                        } catch (Exception ex) {
                            logger.error("Error in geting the  matching Patient");
                            throw new DataProcessingException("Error in geting the  matching Patient" + ex.getMessage(), ex);
                        }
                    }
                }
            }
        }


        // NEW NOK
        if (!matchFound) {
            if (personContainer.getTheEntityIdDtoCollection() != null) {
                Collection<EntityIdDto> newEntityIdDtoColl = new ArrayList<>();
                Iterator<EntityIdDto> iter = personContainer.getTheEntityIdDtoCollection().iterator();
                while (iter.hasNext()) {
                    EntityIdDto entityIdDto = (EntityIdDto) iter.next();
                    if (entityIdDto.getTypeCd() != null && !entityIdDto.getTypeCd().equalsIgnoreCase("LR")) {
                        newEntityIdDtoColl.add(entityIdDto);
                    }
                }
                personContainer.setTheEntityIdDtoCollection(newEntityIdDtoColl);
            }
            try {
                if (personContainer.getThePersonDto().getCd().equals(NEDSSConstant.PAT)) { // Patient
                    patientPersonUid = setAndCreateNewPerson(personContainer);
                    personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
                    personContainer.getThePersonDto().setLocalId(patientPersonUid.getLocalId());
                    personContainer.getThePersonDto().setPersonUid(patientPersonUid.getPersonId());

                    newPersonCreationApplied = true;

                }
            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
                throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
            }
            personContainer.setPatientMatchedFound(false);
        }
        else {
            personContainer.setPatientMatchedFound(true);
        }

        try {

//            if (!newPersonCreationApplied) {
//                personContainer.getThePersonDto().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
//            }
//            else {
//                personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
//            }
//
//            patientUid = setPatientRevision(personContainer, NEDSSConstant.PAT_CR);
//            personContainer.getThePersonDto().setPersonUid(patientUid);


            if (!newPersonCreationApplied) {
                // patientRepositoryUtil.updateExistingPerson(personVO);
                personContainer.getThePersonDto().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
                patientPersonUid = updateExistingPerson(personContainer, NEDSSConstant.PAT_CR, personContainer.getThePersonDto().getPersonParentUid());
                personContainer.getThePersonDto().setPersonParentUid(patientPersonUid.getPersonParentId());
                personContainer.getThePersonDto().setLocalId(patientPersonUid.getLocalId());
                personContainer.getThePersonDto().setPersonUid(patientPersonUid.getPersonId());
            }
            else if (newPersonCreationApplied) {
                setPersonHashCdNok(personContainer);
            }
        } catch (Exception e) {
            logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
            throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
        }
        return edxPatientMatchFoundDT;
    }
}
