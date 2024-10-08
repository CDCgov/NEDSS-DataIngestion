package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.MsgType;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.person.base.ProviderMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.person.IProviderMatchingService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ProviderMatchingService extends ProviderMatchingBaseService implements IProviderMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderMatchingService.class);

    public ProviderMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueService cachingValueService,
            PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService, prepareAssocModelHelper);
    }
    @Transactional
    public EDXActivityDetailLogDto getMatchingProvider(PersonContainer personContainer) throws DataProcessingException {
        Long entityUid = personContainer.getThePersonDto().getPersonUid();
        Collection<EdxEntityMatchDto> coll = new ArrayList<>();
        EDXActivityDetailLogDto edxActivityDetailLogDto = new EDXActivityDetailLogDto();
        String DET_MSG_ENTITY_EXISTS_SUCCESS = "Provider entity found with entity uid : ";
        String DET_MSG_ENTITY_EXISTS_FAIL_NEW = "Provider not found. New Provider created with person uid : ";
        // creating new localID DT for
        // local identifier
        EdxEntityMatchDto theEdxEntityMatchDto = null;
        String localId;
        int localIdhshCd = 0;
        localId = getLocalId(personContainer); // if id = 123
        if (localId != null) {
            localId = localId.toUpperCase();
            localIdhshCd = localId.hashCode();
            try {
                // Try to get the matching with the match string
                EdxEntityMatchDto edxEntityMatchingDT = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, localId);
                if (edxEntityMatchingDT != null && edxEntityMatchingDT.getEntityUid() != null) {
                    edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                    edxActivityDetailLogDto.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                    edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Provider));
                    edxActivityDetailLogDto.setRecordName("PHCR_IMPORT");
                    edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                    return edxActivityDetailLogDto;
                }
            } catch (Exception ex) {
                logger.error("Error in geting the  matching Provider");
                throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
            }
        }

        if (localId != null) {
            theEdxEntityMatchDto = new EdxEntityMatchDto();
            theEdxEntityMatchDto.setTypeCd(NEDSSConstant.PRV);
            theEdxEntityMatchDto.setMatchString(localId);
            theEdxEntityMatchDto.setMatchStringHashCode((long)localIdhshCd);
        }

        // Matching the Identifier (i.e. NPI)
        String identifier;
        int identifierHshCd = 0;
        List identifierList ;
        identifierList = getIdentifier(personContainer);
        if (identifierList != null && !identifierList.isEmpty()) {
            for (Object o : identifierList) {
                identifier = (String) o;
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
                        edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                        edxActivityDetailLogDto.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Provider));
                        edxActivityDetailLogDto.setRecordName("PHCR_IMPORT");
                        edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                        return edxActivityDetailLogDto;
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
        String nameAddStrSt1;
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
                        edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                        edxActivityDetailLogDto.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                        edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Provider));
                        edxActivityDetailLogDto.setRecordName("PHCR_IMPORT");
                        edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                        return edxActivityDetailLogDto;
                    }
                } catch (Exception ex) {
                    logger.error("Error in geting the  matching Provider");
                    throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
                }
            }
        }

        // Continue for name Telephone with no extension
        String nameTelePhone;
        int nameTelePhonehshCd = 0;
        nameTelePhone = telePhoneTxtProvider(personContainer);
        if (nameTelePhone != null) {
            nameTelePhone = nameTelePhone.toUpperCase();
            nameTelePhonehshCd = nameTelePhone.hashCode();
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
                    edxActivityDetailLogDto.setRecordId(String.valueOf(edxEntityMatchingDT.getEntityUid()));
                    edxActivityDetailLogDto.setComment(DET_MSG_ENTITY_EXISTS_SUCCESS + edxEntityMatchingDT.getEntityUid());
                    edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Provider));
                    edxActivityDetailLogDto.setRecordName("PHCR_IMPORT");
                    edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
                    return edxActivityDetailLogDto;
                }
            } catch (Exception ex) {
                logger.error("Error in geting the  matching Provider");
                throw new DataProcessingException("Error in geting the  matching Provider" + ex.getMessage(), ex);
            }

        }

        // Create the provider in case if the provider is not there in the DB
        try {
            if (personContainer.getThePersonDto().getCd().equals(NEDSSConstant.PRV)) { // Provider
                String businessTriggerCd = NEDSSConstant.PRV_CR;
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
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        if (theEdxEntityMatchDto != null)
        {
            coll.add(theEdxEntityMatchDto);
        }
        if (coll != null) {
            for (EdxEntityMatchDto edxEntityMatchDto : coll) {
                edxEntityMatchDto.setEntityUid(entityUid);
                if (personContainer.getRole() == null) {
                    getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(edxEntityMatchDto);
                }
            }
        }
        // returnung the entity Uid which is just created
        edxActivityDetailLogDto.setRecordId(String.valueOf(entityUid));
        edxActivityDetailLogDto.setComment(DET_MSG_ENTITY_EXISTS_FAIL_NEW + edxActivityDetailLogDto.getRecordId());
        edxActivityDetailLogDto.setRecordType(String.valueOf(MsgType.Provider));
        edxActivityDetailLogDto.setRecordName("PHCR_IMPORT");
        edxActivityDetailLogDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
        return edxActivityDetailLogDto;
    }


    public Long setProvider(PersonContainer personContainer, String businessTriggerCd) throws DataProcessingException {
       return processingProvider(personContainer,  "",  businessTriggerCd) ;
    }

}
