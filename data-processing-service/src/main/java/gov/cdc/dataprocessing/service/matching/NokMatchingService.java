package gov.cdc.dataprocessing.service.matching;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.service.core.CheckingValueService;
import gov.cdc.dataprocessing.service.interfaces.INokMatchingService;
import gov.cdc.dataprocessing.service.matching.base.NokMatchingBaseService;
import gov.cdc.dataprocessing.service.model.PersonId;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
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
            CheckingValueService checkingValueService) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, checkingValueService);
    }
    @Transactional
    public EdxPatientMatchDT getMatchingNextOfKin(PersonVO personVO) throws DataProcessingException {
        Long patientUid = personVO.getThePersonDT().getPersonUid();
        EdxPatientMatchDT edxPatientFoundDT = null;
        EdxPatientMatchDT edxPatientMatchFoundDT = null;
        PersonId patientPersonUid = null;
        boolean matchFound = false;
        boolean newPersonCreationApplied = false;

        String nameAddStrSt1 = null;
        int nameAddStrSt1hshCd = 0;
        List nameAddressStreetOneStrList = nameAddressStreetOneNOK(personVO);

        if (nameAddressStreetOneStrList != null && !nameAddressStreetOneStrList.isEmpty()) {
            for (int k = 0; k < nameAddressStreetOneStrList.size(); k++) {
                nameAddStrSt1 = (String) nameAddressStreetOneStrList.get(k);
                if (nameAddStrSt1 != null) {
                    nameAddStrSt1 = nameAddStrSt1.toUpperCase();
                    nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
                    try {
                        if (nameAddStrSt1 != null) {
                            edxPatientFoundDT = new EdxPatientMatchDT();
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
            List nameTelePhoneStrList = telePhoneTxtNOK(personVO);
            if (nameTelePhoneStrList != null && !nameTelePhoneStrList.isEmpty()) {
                for (int k = 0; k < nameTelePhoneStrList.size(); k++) {
                    nameTelePhone = (String) nameTelePhoneStrList.get(k);
                    if (nameTelePhone != null) {
                        nameTelePhone = nameTelePhone.toUpperCase();
                        nameTelePhonehshCd = nameTelePhone.hashCode();
                        try {
                            if (nameTelePhone != null) {
                                edxPatientFoundDT = new EdxPatientMatchDT();
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
            if (personVO.getTheEntityIdDTCollection() != null) {
                Collection<EntityIdDT> newEntityIdDTColl = new ArrayList<>();
                Iterator<EntityIdDT> iter = personVO.getTheEntityIdDTCollection().iterator();
                while (iter.hasNext()) {
                    EntityIdDT entityIdDT = (EntityIdDT) iter.next();
                    if (entityIdDT.getTypeCd() != null && !entityIdDT.getTypeCd().equalsIgnoreCase("LR")) {
                        newEntityIdDTColl.add(entityIdDT);
                    }
                }
                personVO.setTheEntityIdDTCollection(newEntityIdDTColl);
            }
            try {
                if (personVO.getThePersonDT().getCd().equals(NEDSSConstant.PAT)) { // Patient
                    patientPersonUid = setAndCreateNewPerson(personVO);
                    personVO.getThePersonDT().setPersonParentUid(patientPersonUid.getPersonParentId());
                    personVO.getThePersonDT().setLocalId(patientPersonUid.getLocalId());
                    personVO.getThePersonDT().setPersonUid(patientPersonUid.getPersonId());

                    newPersonCreationApplied = true;

                }
            } catch (Exception e) {
                logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
                throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
            }
            personVO.setPatientMatchedFound(false);
        }
        else {
            personVO.setPatientMatchedFound(true);
        }

        try {
            if (!newPersonCreationApplied) {
                // patientRepositoryUtil.updateExistingPerson(personVO);
                personVO.getThePersonDT().setPersonParentUid(edxPatientMatchFoundDT.getPatientUid());
                patientPersonUid = updateExistingPerson(personVO, NEDSSConstant.PAT_CR, personVO.getThePersonDT().getPersonParentUid());
                personVO.getThePersonDT().setPersonParentUid(patientPersonUid.getPersonParentId());
                personVO.getThePersonDT().setLocalId(patientPersonUid.getLocalId());
                personVO.getThePersonDT().setPersonUid(patientPersonUid.getPersonId());
            }
            else if (newPersonCreationApplied) {
                setPersonHashCdNok(personVO);
            }
        } catch (Exception e) {
            logger.error("Error in getting the entity Controller or Setting the Patient" + e.getMessage());
            throw new DataProcessingException("Error in getting the entity Controller or Setting the Patient" + e.getMessage(), e);
        }
        return edxPatientMatchFoundDT;
    }
}
