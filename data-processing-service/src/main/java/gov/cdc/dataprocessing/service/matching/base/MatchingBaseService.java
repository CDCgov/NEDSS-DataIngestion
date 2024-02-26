package gov.cdc.dataprocessing.service.matching.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.service.core.CheckingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.data_extraction.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.data_extraction.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Getter
@Service
public class MatchingBaseService  {
    private static final Logger logger = LoggerFactory.getLogger(MatchingBaseService.class);

    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    private final EntityHelper entityHelper;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final CheckingValueService checkingValueService;

    public MatchingBaseService(
                                EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
                               EntityHelper entityHelper,
                               PatientRepositoryUtil patientRepositoryUtil,
                               CheckingValueService checkingValueService) {
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
        this.entityHelper = entityHelper;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.checkingValueService = checkingValueService;
    }

    protected String getLocalId(PersonVO personVO) {
        String localId = null;
        if (personVO.getLocalIdentifier() != null) {
            localId = personVO.getLocalIdentifier();
        }
        return localId;
    }

    protected List<String> getIdentifier(PersonVO personVO) throws DataProcessingException {
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
                        }
                        // NOTE: Person matching doesn't seem to hit this
                        else
                        {
                            try {
                                Coded coded = new Coded();
                                coded.setCode(entityIdDT.getAssigningAuthorityCd());
                                coded.setCodesetName(NEDSSConstant.EI_AUTH);
                                //TODO: This call out to code value general Repos and Caching the recrod
                                //var codedValueGenralList = getCheckingValueService().findCodeValuesByCodeSetNmAndCode(coded.getCodesetName(), coded.getCode());
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

    protected String getNamesStr(PersonVO personVO) {
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

}
