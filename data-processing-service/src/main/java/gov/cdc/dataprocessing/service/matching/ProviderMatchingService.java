package gov.cdc.dataprocessing.service.matching;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.MsgType;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxRuleAlgorothmManagerDT;
import gov.cdc.dataprocessing.model.classic_model.dto.EDXActivityDetailLogDT;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxEntityMatchDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
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
    public EDXActivityDetailLogDT getMatchingProvider(PersonVO personVO) throws DataProcessingException {
        Long entityUid = personVO.getThePersonDT().getPersonUid();
        Collection<EdxEntityMatchDT> coll = new ArrayList<EdxEntityMatchDT>();
        EDXActivityDetailLogDT edxActivityDetailLogDT = new EDXActivityDetailLogDT();
        String DET_MSG_ENTITY_EXISTS_SUCCESS = "Provider entity found with entity uid : ";
        String DET_MSG_ENTITY_EXISTS_FAIL_NEW = "Provider not found. New Provider created with person uid : ";
        // creating new localID DT for
        // local identifier
        EdxEntityMatchDT theEdxEntityMatchDT = null;
        String localId = null;
        int localIdhshCd = 0;
        localId = getLocalId(personVO); // if id = 123
        if (localId != null) {
            localId = localId.toUpperCase();
            localIdhshCd = localId.hashCode();
        }
        try {
            // Try to get the matching with the match string
            EdxEntityMatchDT edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, localId);
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
            theEdxEntityMatchDT = new EdxEntityMatchDT();
            theEdxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            theEdxEntityMatchDT.setMatchString(localId);
            theEdxEntityMatchDT.setMatchStringHashCode((long)localIdhshCd);
        }

        // Matching the Identifier (i.e. NPI)
        String identifier = null;
        int identifierHshCd = 0;
        List identifierList = null;
        identifierList = getIdentifier(personVO);
        if (identifierList != null && !identifierList.isEmpty()) {
            for (int k = 0; k < identifierList.size(); k++) {
                identifier = (String) identifierList.get(k);
                if (identifier != null) {
                    identifier = identifier.toUpperCase();
                    identifierHshCd = identifier.hashCode();
                }
                try {
                    // Try to get the matching with the match string
                    EdxEntityMatchDT edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, identifier);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDT != null) {
                            theEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personVO.getRole() == null) {
                                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(theEdxEntityMatchDT);
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
                    EdxEntityMatchDT edxEntityMatchDT = new EdxEntityMatchDT();
                    edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
                    edxEntityMatchDT.setMatchString(identifier);
                    edxEntityMatchDT.setMatchStringHashCode((long) identifierHshCd);
                    coll.add(edxEntityMatchDT);
                }

            }
        }

        // Matching with name and address with street address1 alone
        String nameAddStrSt1 = null;
        int nameAddStrSt1hshCd = 0;
        nameAddStrSt1 = nameAddressStreetOneProvider(personVO);
        if (nameAddStrSt1 != null) {
            nameAddStrSt1 = nameAddStrSt1.toUpperCase();
            nameAddStrSt1hshCd = nameAddStrSt1.hashCode();
            if (nameAddStrSt1 != null) {
                try {
                    // Try to get the matching with match string
                    EdxEntityMatchDT edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, nameAddStrSt1);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDT != null) {
                            theEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personVO.getRole() == null) {
                                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(theEdxEntityMatchDT);
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
        nameTelePhone = telePhoneTxtProvider(personVO);
        if (nameTelePhone != null) {
            nameTelePhone = nameTelePhone.toUpperCase();
            nameTelePhonehshCd = nameTelePhone.hashCode();
            if (nameTelePhone != null) {
                try {
                    // Try to get the matching with the match string
                    EdxEntityMatchDT edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, nameTelePhone);
                    if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                        if (theEdxEntityMatchDT != null) {
                            theEdxEntityMatchDT.setEntityUid(edxEntityMatchingDT.getEntityUid());
                            if (personVO.getRole() == null) {
                                getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(theEdxEntityMatchDT);
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
            if (personVO.getThePersonDT().getCd().equals(NEDSSConstant.PRV)) { // Provider
                String businessTriggerCd = NEDSSConstant.PRV_CR;
                /**
                 * TODO: PERSISTING PROVIDER HERE
                 * */
                entityUid = processingProvider(personVO, "PROVIDER", businessTriggerCd);
            }
        } catch (Exception e) {
            logger.error("Error in getting the entity Controller or Setting the Organization" + e.getMessage());
            throw new DataProcessingException("Error in getting the entity Controller or Setting the Organization" + e.getMessage(), e);
        }


        // Create the name and address with no street 2(only street1)
        if (nameAddStrSt1 != null) {
            EdxEntityMatchDT edxEntityMatchDT = new EdxEntityMatchDT();
            edxEntityMatchDT.setEntityUid(entityUid);
            edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDT.setMatchString(nameAddStrSt1);
            edxEntityMatchDT.setMatchStringHashCode((long)nameAddStrSt1hshCd);
            try {
                if (personVO.getRole() == null) {
                    getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDT);
                }
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameAddStrSt1:" + nameAddStrSt1 + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }

        }

        // Create the name and address with nameTelePhone
        if (nameTelePhone != null) {
            EdxEntityMatchDT edxEntityMatchDT = new EdxEntityMatchDT();
            edxEntityMatchDT.setEntityUid(entityUid);
            edxEntityMatchDT.setTypeCd(NEDSSConstant.PRV);
            edxEntityMatchDT.setMatchString(nameTelePhone);
            edxEntityMatchDT.setMatchStringHashCode((long)(nameTelePhonehshCd));
            try {
                if (personVO.getRole() == null) {
                    getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDT);
                }
            } catch (Exception e) {
                logger.error("Error in creating the EdxEntityMatchDT with nameTelePhone:" + nameTelePhone + " " + e.getMessage());
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        if (theEdxEntityMatchDT != null)
        {
            coll.add(theEdxEntityMatchDT);
        }
        if (coll != null) {
            Iterator<EdxEntityMatchDT> it = coll.iterator();
            while (it.hasNext()) {
                EdxEntityMatchDT edxEntityMatchDT = (EdxEntityMatchDT) it.next();
                edxEntityMatchDT.setEntityUid(entityUid);
                if (personVO.getRole() == null) {
                    getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDT);
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
