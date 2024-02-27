package gov.cdc.dataprocessing.service.matching;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.MsgType;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dt.EdxRuleAlgorothmManagerDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXActivityDetailLogDT;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.service.core.CheckingValueService;
import gov.cdc.dataprocessing.service.interfaces.IProviderMatchingService;
import gov.cdc.dataprocessing.service.matching.base.ProviderMatchingBaseService;
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
public class ProviderMatchingService extends ProviderMatchingBaseService implements IProviderMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderMatchingService.class);

    public ProviderMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CheckingValueService checkingValueService) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, checkingValueService);
    }
    @Transactional
    public EDXActivityDetailLogDT getMatchingProvider(PersonContainer personContainer) throws DataProcessingException {
        Long entityUid = personContainer.getThePersonDto().getPersonUid();
        Collection<EdxEntityMatchDto> coll = new ArrayList<EdxEntityMatchDto>();
        EDXActivityDetailLogDT edxActivityDetailLogDT = new EDXActivityDetailLogDT();
        String DET_MSG_ENTITY_EXISTS_SUCCESS = "Provider entity found with entity uid : ";
        String DET_MSG_ENTITY_EXISTS_FAIL_NEW = "Provider not found. New Provider created with person uid : ";
        // creating new localID DT for
        // local identifier
        EdxEntityMatchDto theEdxEntityMatchDto = null;
        String localId = null;
        int localIdhshCd = 0;
        localId = getLocalId(personContainer); // if id = 123
        if (localId != null) {
            localId = localId.toUpperCase();
            localIdhshCd = localId.hashCode();
        }
        try {
            // Try to get the matching with the match string
            EdxEntityMatchDto edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, localId);
            if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                edxActivityDetailLogDT.setRecordId("" + edxEntityMatchingDT.getEntityUid());
                edxActivityDetailLogDT.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
                edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
                edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
                return edxActivityDetailLogDT;
            }
        } catch (Exception ex) {
            logger.error("Error in geting the  matching Provider");
            throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
        }
        if (localId != null) {
            theEdxEntityMatchDto = new EdxEntityMatchDto();
            theEdxEntityMatchDto.setTypeCd(NEDSSConstant.PRV);
            theEdxEntityMatchDto.setMatchString(localId);
            theEdxEntityMatchDto.setMatchStringHashCode((long)localIdhshCd);
        }

        // Matching the Identifier (i.e. NPI)
        String identifier = null;
        int identifierHshCd = 0;
        List identifierList = null;
        identifierList = getIdentifier(personContainer);
        if (identifierList != null && !identifierList.isEmpty()) {
            for (int k = 0; k < identifierList.size(); k++) {
                identifier = (String) identifierList.get(k);
                if (identifier != null) {
                    identifier = identifier.toUpperCase();
                    identifierHshCd = identifier.hashCode();
                }
                try {
                    // Try to get the matching with the match string
                    EdxEntityMatchDto edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, identifier);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDto != null) {
                            theEdxEntityMatchDto.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personContainer.getRole() == null) {
                                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(theEdxEntityMatchDto);
                            }
                        }
                        edxActivityDetailLogDT.setRecordId("" + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
                        edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
                        edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
                        return edxActivityDetailLogDT;
                    }
                } catch (Exception ex) {
                    logger.error("Error in geting the  matching Provider");
                    throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
                }
                if (identifier != null) {
                    EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
                    edxEntityMatchDto.setTypeCd(NEDSSConstant.PRV);
                    edxEntityMatchDto.setMatchString(identifier);
                    edxEntityMatchDto.setMatchStringHashCode((long) identifierHshCd);
                    coll.add(edxEntityMatchDto);
                }

            }
        }

        // Matching with name and address with street address1 alone
        String nameAddStrSt1 = null;
        int nameAddStrSt1hshCd = 0;
        nameAddStrSt1 = nameAddressStreetOneProvider(personContainer);
        if (nameAddStrSt1 != null) {
            nameAddStrSt1 = nameAddStrSt1.toUpperCase();
            nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
            if (nameAddStrSt1 != null) {
                try {
                    // Try to get the matching with match string
                    EdxEntityMatchDto edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, nameAddStrSt1);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDto != null) {
                            theEdxEntityMatchDto.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personContainer.getRole() == null) {
                                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(theEdxEntityMatchDto);
                            }
                        }
                        edxActivityDetailLogDT.setRecordId("" + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
                        edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
                        edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
                        return edxActivityDetailLogDT;
                    }
                } catch (Exception ex) {
                    logger.error("Error in geting the  matching Provider");
                    throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
                }
            }
        }

        // Continue for name Telephone with no extension
        String nameTelePhone = null;
        int nameTelePhonehshCd = 0;
        nameTelePhone = telePhoneTxtProvider(personContainer);
        if (nameTelePhone != null) {
            nameTelePhone = nameTelePhone.toUpperCase();
            nameTelePhonehshCd = nameTelePhone.hashCode();
            if (nameTelePhone != null) {
                try {
                    // Try to get the matching with the match string
                    EdxEntityMatchDto edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, nameTelePhone);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDto != null) {
                            theEdxEntityMatchDto.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personContainer.getRole() == null) {
                                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(theEdxEntityMatchDto);
                            }
                        }
                        edxActivityDetailLogDT.setRecordId("" + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
                        edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
                        edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
                        return edxActivityDetailLogDT;
                    }
                } catch (Exception ex) {
                    logger.error("Error in geting the  matching Provider");
                    throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
                }

            }
        }

        // Create the provider in case if the provider is not there in the DB
        try {
            if (personContainer.getThePersonDto().getCd().equals(NEDSSConstant.PRV)) { // Provider
                String businessTriggerCd = NEDSSConstant.PRV_CR;
                /**
                 * TODO: PERSISTING PROVIDER HERE
                 * */
                entityUid = processingProvider(personContainer, "PROVIDER", businessTriggerCd);
            }
        } catch (Exception e) {
            logger.error("Error in getting the entity Controller or Setting the Organization" + e.getMessage());
            throw new DataProcessingException("Error in getting the entity Controller or Setting the Organization" + e.getMessage(), e);
        }


        // Create the name and address with no street 2(only street1)
        if (nameAddStrSt1 != null) {
            EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
            edxEntityMatchDto.setEntityUid(entityUid);
            edxEntityMatchDto.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDto.setMatchString(nameAddStrSt1);
            edxEntityMatchDto.setMatchStringHashCode((long)nameAddStrSt1hshCd);
            try {
                if (personContainer.getRole() == null) {
                    getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDto);
                }
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameAddStrSt1:" + nameAddStrSt1 + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }

        }

        // Create the name and address with nameTelePhone
        if (nameTelePhone != null) {
            EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();
            edxEntityMatchDto.setEntityUid(entityUid);
            edxEntityMatchDto.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDto.setMatchString(nameTelePhone);
            edxEntityMatchDto.setMatchStringHashCode((long)(nameTelePhonehshCd));
            try {
                if (personContainer.getRole() == null) {
                    getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDto);
                }
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameTelePhone:" + nameTelePhone + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        if (theEdxEntityMatchDto != null)
        {
            coll.add(theEdxEntityMatchDto);
        }
        if (coll != null) {
            Iterator<EdxEntityMatchDto> it = coll.iterator();
            while (it.hasNext()) {
                EdxEntityMatchDto edxEntityMatchDto = (EdxEntityMatchDto) it.next();
                edxEntityMatchDto.setEntityUid(entityUid);
                if (personContainer.getRole() == null) {
                    getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDto);
                }
            }
        }
        // returnung the entity Uid which is just created
        edxActivityDetailLogDT.setRecordId("" + entityUid);
        edxActivityDetailLogDT.setComment("" + DET_MSG_ENTITY_EXISTS_FAIL_NEW + edxActivityDetailLogDT.getRecordId());
        edxActivityDetailLogDT.setRecordType("" + MsgType.Provider);
        edxActivityDetailLogDT.setRecordName("PHCR_IMPORT");
        edxActivityDetailLogDT.setLogType("" + EdxRuleAlgorothmManagerDT.STATUS_VAL.Success);
        return edxActivityDetailLogDT;
    }


}
