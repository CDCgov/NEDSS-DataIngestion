package gov.cdc.nbsDedup.matchservice;


import gov.cdc.nbsDedup.constant.elr.EdxELRConstant;
import gov.cdc.nbsDedup.constant.elr.NEDSSConstant;
import gov.cdc.nbsDedup.exception.DataProcessingException;
import gov.cdc.nbsDedup.model.container.model.PersonContainer;
import gov.cdc.nbsDedup.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.nbsDedup.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.nbsDedup.service.implementation.person.base.cache.CachingValueService;
import gov.cdc.nbsDedup.utilities.component.entity.EntityHelper;
import gov.cdc.nbsDedup.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.nbsDedup.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.nbsDedup.utilities.component.patient.PatientRepositoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NbsMatchService extends PatientMatchingBaseService {


  private static final Logger logger = LoggerFactory.getLogger(NbsMatchService.class);
  private boolean multipleMatchFound = false;
  private EdxPatientMatchDto edxPatientMatchFoundDT = null;



  public NbsMatchService(EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil, EntityHelper entityHelper,
      PatientRepositoryUtil patientRepositoryUtil, CachingValueService cachingValueService,
      PrepareAssocModelHelper prepareAssocModelHelper) {
    super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueService,
        prepareAssocModelHelper);
  }


  public boolean match(PersonContainer personContainer) throws DataProcessingException {
    Long patientUid = personContainer.getThePersonDto().getPersonUid();
    String cd = personContainer.getThePersonDto().getCd();
    String patientRole = personContainer.getRole();
    EdxPatientMatchDto edxPatientFoundDT;
    boolean matchFound = false;

    if (patientRole == null || patientRole.isEmpty() || patientRole.equalsIgnoreCase(
        EdxELRConstant.ELR_PATIENT_ROLE_CD)) {
      EdxPatientMatchDto localIdHashCode;
      String localId;
      int localIdhshCd = 0;
      localId = getLocalId(personContainer);
      if (localId != null) {
        localId = localId.toUpperCase();
        localIdhshCd = localId.hashCode();
      }
      //NOTE: Matching Start here
      try {
        // Try to get the matching with the match string
        //	(was hash code but hash code had dups on rare occasions)
        edxPatientMatchFoundDT = getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, localId);
        if (edxPatientMatchFoundDT != null && edxPatientMatchFoundDT.isMultipleMatch()) {
          multipleMatchFound = true;
          matchFound = false;
        } else if (edxPatientMatchFoundDT != null && edxPatientMatchFoundDT.getPatientUid() != null) {
          matchFound = true;
        }
      } catch (Exception ex) {
        logger.error("Error in geting the  matching Patient");
        throw new DataProcessingException("Error in geting the  matching Patient" + ex.getMessage(), ex);
      }

      if (localId != null) {
        localIdHashCode = new EdxPatientMatchDto();
        localIdHashCode.setTypeCd(NEDSSConstant.PAT);
        localIdHashCode.setMatchString(localId);
        localIdHashCode.setMatchStringHashCode((long) localIdhshCd);
      }

      // NOTE: Matching by Identifier
      if (!matchFound) {
        String IdentifierStr;
        int identifierStrhshCd = 0;

        List<String> identifierStrList = getIdentifier(personContainer);
        if (identifierStrList != null && !identifierStrList.isEmpty()) {
          for (String s : identifierStrList) {
            IdentifierStr = s;
            if (IdentifierStr != null) {
              IdentifierStr = IdentifierStr.toUpperCase();
              identifierStrhshCd = IdentifierStr.hashCode();
            }

            if (IdentifierStr != null) {
              edxPatientFoundDT = new EdxPatientMatchDto();
              edxPatientFoundDT.setTypeCd(NEDSSConstant.PAT);
              edxPatientFoundDT.setMatchString(IdentifierStr);
              edxPatientFoundDT.setMatchStringHashCode((long) identifierStrhshCd);
              // Try to get the matching with the hash code
              edxPatientMatchFoundDT =
                  getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, IdentifierStr);

              if (edxPatientMatchFoundDT.isMultipleMatch()) {
                matchFound = false;
                multipleMatchFound = true;
              } else if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
                matchFound = false;
              } else {
                matchFound = true;
                break;
              }
            }
          }
        }
      }

      // NOTE: Matching with last name ,first name ,date of birth and current sex
      if (!matchFound) {
        String namesdobcursexStr;
        int namesdobcursexStrhshCd;
        namesdobcursexStr = getLNmFnmDobCurSexStr(personContainer);
        if (namesdobcursexStr != null) {
          namesdobcursexStr = namesdobcursexStr.toUpperCase();
          namesdobcursexStrhshCd = namesdobcursexStr.hashCode();
          try {
            if (namesdobcursexStr != null) {
              edxPatientFoundDT = new EdxPatientMatchDto();
              edxPatientFoundDT.setPatientUid(patientUid);
              edxPatientFoundDT.setTypeCd(NEDSSConstant.PAT);
              edxPatientFoundDT.setMatchString(namesdobcursexStr);
              edxPatientFoundDT.setMatchStringHashCode((long) namesdobcursexStrhshCd);
            }
            edxPatientMatchFoundDT =
                getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString(cd, namesdobcursexStr);
            if (edxPatientMatchFoundDT.isMultipleMatch()) {
              multipleMatchFound = true;
              matchFound = false;
            } else if (edxPatientMatchFoundDT.getPatientUid() == null || (edxPatientMatchFoundDT.getPatientUid() != null && edxPatientMatchFoundDT.getPatientUid() <= 0)) {
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
    return matchFound;
  }

  public boolean getMultipleMatchFound() {
    return multipleMatchFound;
  }

  public EdxPatientMatchDto getEdxPatientMatchFoundDT() {
    return edxPatientMatchFoundDT;
  }
}
