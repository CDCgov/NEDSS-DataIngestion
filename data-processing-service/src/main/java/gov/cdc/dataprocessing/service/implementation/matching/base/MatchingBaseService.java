package gov.cdc.dataprocessing.service.implementation.matching.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.service.implementation.core.CheckingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
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

    protected String getLocalId(PersonContainer personContainer) {
        String localId = null;
        if (personContainer.getLocalIdentifier() != null) {
            localId = personContainer.getLocalIdentifier();
        }
        return localId;
    }

    protected List<String> getIdentifier(PersonContainer personContainer) throws DataProcessingException {
        String carrot = "^";
        List<String> returnList =new ArrayList<String>();
        List<String> identifierList = new ArrayList<String>();
        String identifier = null;
        try{
            if (personContainer.getTheEntityIdDtoCollection() != null
                    && personContainer.getTheEntityIdDtoCollection().size() > 0) {
                Collection<EntityIdDto> entityIdDtoColl = personContainer.getTheEntityIdDtoCollection();
                Iterator<EntityIdDto> entityIdIterator = entityIdDtoColl.iterator();
                while (entityIdIterator.hasNext()) {
                    identifier= null;
                    EntityIdDto entityIdDto = entityIdIterator.next();
                    if (((entityIdDto.getStatusCd() != null && entityIdDto
                            .getStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_ACTIVE))
                            && entityIdDto.getRecordStatusCd() != null
                            && (entityIdDto.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)))
                            || (entityIdDto.getRecordStatusCd() != null
                            && entityIdDto.getTypeCd()!=null
                            && entityIdDto.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_SS_TYPE)
                            && (entityIdDto.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)))
                    ) {


                        if ((entityIdDto.getRootExtensionTxt() != null)
                                && (entityIdDto.getTypeCd() != null)
                                && (entityIdDto.getAssigningAuthorityCd() != null)
                                && (entityIdDto.getAssigningAuthorityDescTxt() !=null)
                                && (entityIdDto.getAssigningAuthorityIdType() != null)) {
                            identifier = entityIdDto.getRootExtensionTxt()
                                    + carrot + entityIdDto.getTypeCd() + carrot
                                    + entityIdDto.getAssigningAuthorityCd()
                                    + carrot
                                    + entityIdDto.getAssigningAuthorityDescTxt()
                                    + carrot + entityIdDto.getAssigningAuthorityIdType();
                        }
                        // NOTE: Person matching doesn't seem to hit this
                        else
                        {
                            try {
                                Coded coded = new Coded();
                                coded.setCode(entityIdDto.getAssigningAuthorityCd());
                                coded.setCodesetName(NEDSSConstant.EI_AUTH);
                                //TODO: This call out to code value general Repos and Caching the recrod
                                //var codedValueGenralList = getCheckingValueService().findCodeValuesByCodeSetNmAndCode(coded.getCodesetName(), coded.getCode());
                                if (entityIdDto.getRootExtensionTxt() != null
                                        && entityIdDto.getTypeCd() != null
                                        && coded.getCode()!=null
                                        && coded.getCodeDescription()!=null
                                        && coded.getCodeSystemCd()!=null){
                                    identifier = entityIdDto.getRootExtensionTxt()
                                            + carrot + entityIdDto.getTypeCd() + carrot
                                            + coded.getCode() + carrot
                                            + coded.getCodeDescription() + carrot
                                            + coded.getCodeSystemCd();
                                }
                            }catch (Exception ex) {
                                String errorMessage = "The assigning authority " + entityIdDto.getAssigningAuthorityCd() + " does not exists in the system. ";
                                logger.debug(ex.getMessage() + errorMessage);
                            }
                        }

                        if (identifier != null) {
                            if (getNamesStr(personContainer) != null) {
                                identifier = identifier + carrot + getNamesStr(personContainer);
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

    protected String getNamesStr(PersonContainer personContainer) {
        String namesStr = null;
        String carrot = "^";
        if (personContainer.getThePersonDto() != null) {
            PersonDto personDto = personContainer.getThePersonDto();
            if (personDto.getCd() != null
                    && personDto.getCd().equals(NEDSSConstant.PAT)) {
                if (personContainer.getThePersonNameDtoCollection() != null
                        && personContainer.getThePersonNameDtoCollection().size() > 0) {
                    Collection<PersonNameDto> personNameDtoColl = personContainer.getThePersonNameDtoCollection();
                    Iterator<PersonNameDto> personNameIterator = personNameDtoColl.iterator();
                    Timestamp asofDate = null;
                    while (personNameIterator.hasNext()) {
                        PersonNameDto personNameDto = (PersonNameDto) personNameIterator
                                .next();
                        if (personNameDto.getNmUseCd() != null
                                && personNameDto.getNmUseCd().equalsIgnoreCase(
                                "L")
                                && personNameDto.getRecordStatusCd() != null
                                && personNameDto.getRecordStatusCd().equals(
                                NEDSSConstant.RECORD_STATUS_ACTIVE)) {
                            if (asofDate == null
                                    || (asofDate.getTime() < personNameDto
                                    .getAsOfDate().getTime())) {
                                if ((personNameDto.getLastNm() != null)
                                        && (!personNameDto.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDto.getFirstNm() != null)
                                        && (!personNameDto.getFirstNm().trim()
                                        .equals(""))) {
                                    namesStr = personNameDto.getLastNm()
                                            + carrot
                                            + personNameDto.getFirstNm();
                                    asofDate = personNameDto.getAsOfDate();

                                }
                            } else if (asofDate.before(personNameDto
                                    .getAsOfDate())) {
                                if ((personNameDto.getLastNm() != null)
                                        && (!personNameDto.getLastNm().trim()
                                        .equals(""))
                                        && (personNameDto.getFirstNm() != null)
                                        && (!personNameDto.getFirstNm().trim()
                                        .equals(""))) {
                                    namesStr = personNameDto.getLastNm()
                                            + carrot
                                            + personNameDto.getFirstNm();
                                    asofDate = personNameDto.getAsOfDate();
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
