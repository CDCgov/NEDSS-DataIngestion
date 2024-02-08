package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import gov.cdc.dataprocessing.model.classic_model.dto.EntityIdDT;
import gov.cdc.dataprocessing.model.classic_model.dto.PersonDT;
import gov.cdc.dataprocessing.model.classic_model.dto.PersonNameDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.service.CheckingValueService;
import gov.cdc.dataprocessing.utilities.model.Coded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Component
public class EdxPatientMatchingCriteriaUtil {

    private static final Logger logger = LoggerFactory.getLogger(EdxPatientMatchingCriteriaUtil.class);
    
    private final CheckingValueService checkingValueService;
    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;

    public EdxPatientMatchingCriteriaUtil(CheckingValueService checkingValueService, EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil) {
        this.checkingValueService = checkingValueService;
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
    }


    // getting local Id string from person VO
    public String getLocalId(PersonVO personVO) {
        String localId = null;
        if (personVO.getLocalIdentifier() != null) {
            localId = personVO.getLocalIdentifier();
        }
        return localId;
    }

    public List<String> getIdentifier(PersonVO personVO) throws DataProcessingException {
        String carrot = "^";
        List<String> returnList =new ArrayList<String>();
        List<String> identifierList = new ArrayList<String>();
        String identifier = null;
        try{
            if (personVO.getTheEntityIdDTCollection() != null
                    && personVO.getTheEntityIdDTCollection().size() > 0) {
                Collection<EntityIdDT> entityIdDTColl = personVO.getTheEntityIdDTCollection();
                Iterator<EntityIdDT> entityIdIterator = entityIdDTColl.iterator();
                while (entityIdIterator.hasNext()) {
                    identifier= null;
                    EntityIdDT entityIdDT = entityIdIterator.next();
                    if (((entityIdDT.getStatusCd() != null && entityIdDT
                            .getStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_ACTIVE))
                            && entityIdDT.getRecordStatusCd() != null
                            && (entityIdDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)))
                            || (entityIdDT.getRecordStatusCd() != null
                            && entityIdDT.getTypeCd()!=null
                            && entityIdDT.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_SS_TYPE)
                            && (entityIdDT.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)))
                    ) {


                        if ((entityIdDT.getRootExtensionTxt() != null)
                                && (entityIdDT.getTypeCd() != null)
                                && (entityIdDT.getAssigningAuthorityCd() != null)
                                && (entityIdDT.getAssigningAuthorityDescTxt() !=null)
                                && (entityIdDT.getAssigningAuthorityIdType() != null)) {
                            identifier = entityIdDT.getRootExtensionTxt()
                                    + carrot + entityIdDT.getTypeCd() + carrot
                                    + entityIdDT.getAssigningAuthorityCd()
                                    + carrot
                                    + entityIdDT.getAssigningAuthorityDescTxt()
                                    + carrot + entityIdDT.getAssigningAuthorityIdType();
                        }else {
                            try {
                                Coded coded = new Coded();
                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
                                coded.setCodesetName(NEDSSConstant.EI_AUTH);

                                //TODO: This call out to code value general Repos and Caching the recrod
//                                NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
//                                lookupDAO.retrieveSRTCodeInfo(coded);

                                var codedValueGenralList = checkingValueService.findCodeValuesByCodeSetNmAndCode(coded.getCodesetName(), coded.getCode());

                                if (entityIdDT.getRootExtensionTxt() != null
                                        && entityIdDT.getTypeCd() != null
                                        && coded.getCode()!=null
                                        && coded.getCodeDescription()!=null
                                        && coded.getCodeSystemCd()!=null){
                                    identifier = entityIdDT.getRootExtensionTxt()
                                            + carrot + entityIdDT.getTypeCd() + carrot
                                            + coded.getCode() + carrot
                                            + coded.getCodeDescription() + carrot
                                            + coded.getCodeSystemCd();
                                }
                            }catch (Exception ex) {
                                String errorMessage = "The assigning authority " + entityIdDT.getAssigningAuthorityCd() + " does not exists in the system. ";
                                logger.debug(ex.getMessage() + errorMessage);
                            }
                        }

                        if (identifier != null) {
                            if (getNamesStr(personVO) != null) {
                                identifier = identifier + carrot + getNamesStr(personVO);
                                identifierList.add(identifier);
                            }
                        }

                    }
                }
            }
            HashSet<String> hashSet = new HashSet<String>(identifierList);
            returnList = new ArrayList<String>(hashSet) ;
        }
        catch (Exception ex) {
            String errorMessage = "Exception while creating hashcode for patient entity IDs . ";
            logger.debug(ex.getMessage() + errorMessage);
            throw new DataProcessingException(errorMessage, ex);
        }
        return returnList;
    }

    public String getNamesStr(PersonVO personVO) {
        String namesStr = null;
        String carrot = "^";
        if (personVO.getThePersonDT() != null) {
            PersonDT personDT = personVO.getThePersonDT();
            if (personDT.getCd() != null
                    && personDT.getCd().equals(NEDSSConstant.PAT)) {
                if (personVO.getThePersonNameDTCollection() != null
                        && personVO.getThePersonNameDTCollection().size() > 0) {
                    Collection<PersonNameDT> personNameDTColl = personVO.getThePersonNameDTCollection();
                    Iterator<PersonNameDT> personNameIterator = personNameDTColl.iterator();
                    Timestamp asofDate = null;
                    while (personNameIterator.hasNext()) {
                        PersonNameDT personNameDT = (PersonNameDT) personNameIterator
                                .next();
                        if (personNameDT.getNmUseCd() != null
                                && personNameDT.getNmUseCd().equalsIgnoreCase(
                                "L")
                                && personNameDT.getRecordStatusCd() != null
                                && personNameDT.getRecordStatusCd().equals(
                                NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                            if (asofDate == null
                                    || (asofDate.getTime() < personNameDT
                                    .getAsOfDate().getTime())) {
                                if ((personNameDT.getLastNm() != null)
                                        && (!personNameDT.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDT.getFirstNm() != null)
                                        && (!personNameDT.getFirstNm().trim()
                                        .equals(""))) {
                                    namesStr = personNameDT.getLastNm()
                                            + carrot
                                            + personNameDT.getFirstNm();
                                    asofDate = personNameDT.getAsOfDate();

                                }
                            } else if (asofDate.before(personNameDT
                                    .getAsOfDate())) {
                                if ((personNameDT.getLastNm() != null)
                                        && (!personNameDT.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDT.getFirstNm() != null)
                                        && (!personNameDT.getFirstNm().trim()
                                        .equals(""))) {
                                    namesStr = personNameDT.getLastNm()
                                            + carrot
                                            + personNameDT.getFirstNm();
                                    asofDate = personNameDT.getAsOfDate();
                                }
                            }
                        }
                    }
                }
            }
        }

        return namesStr;
    }

    public String getLNmFnmDobCurSexStr(PersonVO personVO) {
        String namedobcursexStr = null;
        String carrot = "^";
        if (personVO.getThePersonDT() != null) {
            PersonDT personDT = personVO.getThePersonDT();
            if (personDT.getCd() != null
                    && personDT.getCd().equals(NEDSSConstant.PAT)) {
                if (personVO.getThePersonNameDTCollection() != null
                        && personVO.getThePersonNameDTCollection().size() > 0) {
                    Collection<PersonNameDT> personNameDTColl = personVO
                            .getThePersonNameDTCollection();
                    Iterator personNameIterator = personNameDTColl.iterator();
                    Timestamp asofDate = null;
                    while (personNameIterator.hasNext()) {
                        PersonNameDT personNameDT = (PersonNameDT) personNameIterator
                                .next();
                        if (personNameDT.getNmUseCd() == null)
                        {
                            String Message = "personNameDT.getNmUseCd() is null";
                            logger.debug(Message);
                        }
                        if (personNameDT.getNmUseCd() != null
                                && personNameDT.getNmUseCd().equalsIgnoreCase("L")
                                && personNameDT.getRecordStatusCd() != null
                                && personNameDT.getRecordStatusCd().equals(
                                NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                            if (asofDate == null
                                    || (asofDate.getTime() < personNameDT
                                    .getAsOfDate().getTime())) {
                                if ((personNameDT.getLastNm() != null)
                                        && (!personNameDT.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDT.getFirstNm() != null)
                                        && (!personNameDT.getFirstNm().trim()
                                        .equals(""))
                                        && (personDT.getBirthTime() != null)
                                        && (personDT.getCurrSexCd() != null)
                                        && (!personDT.getCurrSexCd().trim()
                                        .equals(""))) {
                                    namedobcursexStr = personNameDT.getLastNm()
                                            + carrot
                                            + personNameDT.getFirstNm()
                                            + carrot + personDT.getBirthTime()
                                            + carrot + personDT.getCurrSexCd();
                                    asofDate = personNameDT.getAsOfDate();
                                }
                            } else if (asofDate.before(personNameDT
                                    .getAsOfDate())) {
                                if ((personNameDT.getLastNm() != null)
                                        && (!personNameDT.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDT.getFirstNm() != null)
                                        && (!personNameDT.getFirstNm().trim()
                                        .equals(""))
                                        && (personDT.getBirthTime() != null)
                                        && (personDT.getCurrSexCd() != null)
                                        && (!personDT.getCurrSexCd().trim()
                                        .equals(""))) {
                                    namedobcursexStr = personNameDT.getLastNm()
                                            + carrot
                                            + personNameDT.getFirstNm()
                                            + carrot + personDT.getBirthTime()
                                            + carrot + personDT.getCurrSexCd();
                                    asofDate = personNameDT.getAsOfDate();

                                }

                            }

                        }
                    }
                }
            }
        }
        return namedobcursexStr;
    }

    public void setPatientToEntityMatch(PersonVO personVO) throws DataProcessingException {

        Long patientUid = personVO.getThePersonDT().getPersonUid();
        EdxPatientMatchDT edxPatientMatchDT = new EdxPatientMatchDT();
        String patientRole = personVO.getRole();
        String cdDescTxt = personVO.thePersonDT.getCdDescTxt();
        // Matching with IDValue,IDType Code,Assigning authority,Legal Last name
        // ,Legal First name-Identifier
        // if (patientRole == null ||
        // patientRole.equalsIgnoreCase(EdxELRConstants.ELR_PATIENT_ROLE_CD)||
        // patientRole.equalsIgnoreCase("")) {
        if (cdDescTxt == null || cdDescTxt.equalsIgnoreCase("") || !cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
            String identifierStr = null;
            int identifierStrhshCd = 0;
            List identifierStrList = getIdentifier(personVO);
            if (identifierStrList != null && !identifierStrList.isEmpty()) {
                for (int k = 0; k < identifierStrList.size(); k++) {
                    identifierStr = (String) identifierStrList.get(k);
                    if (identifierStr != null) {
                        identifierStr = identifierStr.toUpperCase();
                        identifierStrhshCd = identifierStr.hashCode();
                    }

                    if (identifierStr != null) {
                        edxPatientMatchDT = new EdxPatientMatchDT();
                        edxPatientMatchDT.setPatientUid(patientUid);
                        edxPatientMatchDT.setTypeCd(NEDSSConstant.PAT);
                        edxPatientMatchDT.setMatchString(identifierStr);
                        edxPatientMatchDT.setMatchStringHashCode((long) identifierStrhshCd);
                        try {
                            edxPatientMatchRepositoryUtil.setEdxPatientMatchDT(edxPatientMatchDT);
                        } catch (Exception e) {
                            logger.error("Error in creating the setEdxPatientMatchDT with identifierStr:"
                                    + identifierStr + " " + e.getMessage());
                            throw new DataProcessingException(e.getMessage(), e);
                        }

                    }
                }
            }

            // Matching with last name ,first name ,date of birth and current
            // sex

            String namesdobcursexStr = null;
            int namesdobcursexStrhshCd = 0;
            namesdobcursexStr = getLNmFnmDobCurSexStr(personVO);
            if (namesdobcursexStr != null) {
                namesdobcursexStr = namesdobcursexStr.toUpperCase();
                namesdobcursexStrhshCd = namesdobcursexStr.hashCode();
            }

            if (namesdobcursexStr != null) {
                edxPatientMatchDT = new EdxPatientMatchDT();
                edxPatientMatchDT.setPatientUid(patientUid);
                edxPatientMatchDT.setTypeCd(NEDSSConstant.PAT);
                edxPatientMatchDT.setMatchString(namesdobcursexStr);
                edxPatientMatchDT.setMatchStringHashCode((long) namesdobcursexStrhshCd);
                try {
                    edxPatientMatchRepositoryUtil.setEdxPatientMatchDT(edxPatientMatchDT);
                } catch (Exception e) {
                    logger.error("Error in creating the setEdxPatientMatchDT with namesdobcursexStr:" + namesdobcursexStr + " " + e.getMessage());
                    throw new DataProcessingException(e.getMessage(), e);
                }

            }
        }
        // else if
        // (patientRole.equalsIgnoreCase(EdxELRConstants.ELR_NEXT_F_KIN_ROLE_CD))
        // {
        if (cdDescTxt != null && cdDescTxt.equalsIgnoreCase(EdxELRConstant.ELR_NOK_DESC)) {
           //TODO: Next of KIN Code

        }// end of method
    }

}
