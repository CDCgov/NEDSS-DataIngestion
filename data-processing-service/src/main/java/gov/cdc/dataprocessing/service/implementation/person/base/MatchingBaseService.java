package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class MatchingBaseService  {
    private static final Logger logger = LoggerFactory.getLogger(MatchingBaseService.class);

    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    private final EntityHelper entityHelper;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final CachingValueService cachingValueService;
    private final PrepareAssocModelHelper prepareAssocModelHelper;


    public MatchingBaseService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueService cachingValueService, PrepareAssocModelHelper prepareAssocModelHelper) {
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
        this.entityHelper = entityHelper;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.cachingValueService = cachingValueService;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
    }

    protected String getLocalId(PersonContainer personContainer) {
        String localId = null;
        if (personContainer.getLocalIdentifier() != null) {
            localId = personContainer.getLocalIdentifier();
        }
        return localId;
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    protected List<String> getIdentifier(PersonContainer personContainer) throws DataProcessingException {
        String carrot = "^";
        List<String> returnList;
        List<String> identifierList = new ArrayList<>();
        String identifier ;
        try{
            if (personContainer.getTheEntityIdDtoCollection() != null
                    && !personContainer.getTheEntityIdDtoCollection().isEmpty())
            {
                Collection<EntityIdDto> entityIdDtoColl = personContainer.getTheEntityIdDtoCollection();
                for (EntityIdDto idDto : entityIdDtoColl) {
                    identifier = null;
                    if (((idDto.getStatusCd() != null && idDto
                            .getStatusCd().equalsIgnoreCase(NEDSSConstant.STATUS_ACTIVE))
                            && idDto.getRecordStatusCd() != null
                            && (idDto.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)))
                            || (idDto.getRecordStatusCd() != null
                            && idDto.getTypeCd() != null
                            && idDto.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_SS_TYPE)
                            && (idDto.getRecordStatusCd().equalsIgnoreCase(NEDSSConstant.RECORD_STATUS_ACTIVE)))
                    ) {


                        if ((idDto.getRootExtensionTxt() != null)
                                && (idDto.getTypeCd() != null)
                                && (idDto.getAssigningAuthorityCd() != null)
                                && (idDto.getAssigningAuthorityDescTxt() != null)
                                && (idDto.getAssigningAuthorityIdType() != null)) {
                            identifier = idDto.getRootExtensionTxt()
                                    + carrot + idDto.getTypeCd() + carrot
                                    + idDto.getAssigningAuthorityCd()
                                    + carrot
                                    + idDto.getAssigningAuthorityDescTxt()
                                    + carrot + idDto.getAssigningAuthorityIdType();
                        }
                        // NOTE: Person matching doesn't seem to hit this
                        else {
                            Coded coded = new Coded();
                            coded.setCode(idDto.getAssigningAuthorityCd());
                            coded.setCodesetName(NEDSSConstant.EI_AUTH);
                            //TODO: This call out to code value general Repos and Caching the recrod
                            //var codedValueGenralList = getCachingValueService().findCodeValuesByCodeSetNmAndCode(coded.getCodesetName(), coded.getCode());

                            /*
                            NotificationSRTCodeLookupTranslationDAOImpl lookupDAO = new NotificationSRTCodeLookupTranslationDAOImpl();
                            lookupDAO.retrieveSRTCodeInfo(coded);
                            * */
                            if (idDto.getRootExtensionTxt() != null
                                    && idDto.getTypeCd() != null
                                    && coded.getCode() != null
                                    && coded.getCodeDescription() != null
                                    && coded.getCodeSystemCd() != null) {
                                identifier = idDto.getRootExtensionTxt()
                                        + carrot + idDto.getTypeCd() + carrot
                                        + coded.getCode() + carrot
                                        + coded.getCodeDescription() + carrot
                                        + coded.getCodeSystemCd();
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
            HashSet<String> hashSet = new HashSet<>(identifierList);
            returnList = new ArrayList<>(hashSet) ;
        }
        catch (Exception ex) {
            String errorMessage = "Exception while creating hashcode for patient entity IDs . ";
            logger.debug(ex.getMessage() + errorMessage);
            throw new DataProcessingException(errorMessage, ex);
        }
        return returnList;
    }
    @SuppressWarnings("java:S3776")

    protected String getNamesStr(PersonContainer personContainer) {
        String namesStr = null;
        if (personContainer.getThePersonDto() != null)
        {
            PersonDto personDto = personContainer.getThePersonDto();
            if (personDto.getCd() != null
                    && personDto.getCd().equals(NEDSSConstant.PAT))
            {
                if (personContainer.getThePersonNameDtoCollection() != null
                        && !personContainer.getThePersonNameDtoCollection().isEmpty())
                {
                    Collection<PersonNameDto> personNameDtoColl = personContainer.getThePersonNameDtoCollection();
                    Iterator<PersonNameDto> personNameIterator = personNameDtoColl.iterator();
                    Timestamp asofDate = null;
                    while (personNameIterator.hasNext())
                    {
                        PersonNameDto personNameDto = personNameIterator.next();
                        if (personNameDto.getNmUseCd() != null
                                && personNameDto.getNmUseCd().equalsIgnoreCase("L")
                                && personNameDto.getRecordStatusCd() != null
                                && personNameDto.getRecordStatusCd().equals(NEDSSConstant.RECORD_STATUS_ACTIVE))
                        {
                            // These condition check was how it originally designed in legacy
                            // The way I see it is the second conditional check would never be reached
                            if (asofDate == null || (asofDate.getTime() < personNameDto.getAsOfDate().getTime())) // NOSONAR
                            {
                                namesStr = processingPersonNameBasedOnAsOfDate(personNameDto, namesStr, asofDate); // NOSONAR
                            }
                            else if (asofDate.before(personNameDto.getAsOfDate()))
                            {
                                namesStr = processingPersonNameBasedOnAsOfDate(personNameDto, namesStr, asofDate); // NOSONAR
                            }
                        }
                    }


                }
            }
        }

        return namesStr;
    }

    protected String processingPersonNameBasedOnAsOfDate(PersonNameDto personNameDto, String namesStr, Timestamp asofDate) {
        String caret = "^";
        if ((personNameDto.getLastNm() != null)
                && (!personNameDto.getLastNm().trim().equals(""))
                && (personNameDto.getFirstNm() != null)
                && (!personNameDto.getFirstNm().trim().equals("")))
        {
            namesStr = personNameDto.getLastNm() + caret + personNameDto.getFirstNm();
            asofDate = personNameDto.getAsOfDate(); // NOSONAR
        }
        return namesStr;
    }

}
