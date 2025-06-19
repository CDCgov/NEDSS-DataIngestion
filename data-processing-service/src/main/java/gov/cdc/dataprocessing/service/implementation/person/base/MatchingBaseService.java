package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueDpDpService;
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

public class MatchingBaseService  {
    private static final Logger logger = LoggerFactory.getLogger(MatchingBaseService.class);

    private final EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    private final EntityHelper entityHelper;
    private final PatientRepositoryUtil patientRepositoryUtil;
    private final CachingValueDpDpService cachingValueDpService;
    private final PrepareAssocModelHelper prepareAssocModelHelper;


    public MatchingBaseService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueDpDpService cachingValueDpService, PrepareAssocModelHelper prepareAssocModelHelper) {
        this.edxPatientMatchRepositoryUtil = edxPatientMatchRepositoryUtil;
        this.entityHelper = entityHelper;
        this.patientRepositoryUtil = patientRepositoryUtil;
        this.cachingValueDpService = cachingValueDpService;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
    }

    public String getLocalId(PersonContainer personContainer) {
        String localId = null;
        if (personContainer.getLocalIdentifier() != null) {
            localId = personContainer.getLocalIdentifier();
        }
        return localId;
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    public List<String> getIdentifier(PersonContainer personContainer) throws DataProcessingException {
        String carrot = "^";
        List<String> returnList;
        List<String> identifierList = new ArrayList<>();
        StringBuilder identifier = new StringBuilder();
        try{
            if (personContainer.getTheEntityIdDtoCollection() != null
                    && !personContainer.getTheEntityIdDtoCollection().isEmpty())
            {
                Collection<EntityIdDto> entityIdDtoColl = personContainer.getTheEntityIdDtoCollection();
                for (EntityIdDto idDto : entityIdDtoColl) {
                    identifier.setLength(0);
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
                            identifier.append(idDto.getRootExtensionTxt())
                                    .append(carrot)
                                    .append(idDto.getTypeCd())
                                    .append(carrot)
                                    .append(idDto.getAssigningAuthorityCd())
                                    .append(carrot)
                                    .append(idDto.getAssigningAuthorityDescTxt())
                                    .append(carrot)
                                    .append(idDto.getAssigningAuthorityIdType());
                        }
                        // NOTE: Person matching doesn't seem to hit this
                        else {
                            Coded coded = new Coded();
                            coded.setCode(idDto.getAssigningAuthorityCd());
                            coded.setCodesetName(NEDSSConstant.EI_AUTH);
                            coded.setCodeDescription("UKN");
                            coded.setCodeSystemCd("UKN");

                            if (idDto.getRootExtensionTxt() != null
                                    && idDto.getTypeCd() != null
                                    && coded.getCode() != null
                                    && coded.getCodeDescription() != null
                                    && coded.getCodeSystemCd() != null) {
                                identifier.append(idDto.getRootExtensionTxt())
                                        .append(carrot)
                                        .append(idDto.getTypeCd())
                                        .append(carrot)
                                        .append(coded.getCode())
                                        .append(carrot)
                                        .append(coded.getCodeDescription())
                                        .append(carrot)
                                        .append(coded.getCodeSystemCd());
                            }
                        }

                        if (!identifier.isEmpty() && getNamesStr(personContainer) != null) {
                            identifier.append(carrot).append(getNamesStr(personContainer));
                            identifierList.add(identifier.toString());
                        }

                    }
                }
            }
            HashSet<String> hashSet = new HashSet<>(identifierList);
            returnList = new ArrayList<>(hashSet) ;
        }
        catch (Exception ex) {
            throw new DataProcessingException(ex.getMessage(), ex);
        }
        return returnList;
    }
    @SuppressWarnings({"java:S3776", "java:S1066", "java:S1871"})

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
                            namesStr = processingPersonNameBasedOnAsOfDate(personNameDto, namesStr, asofDate); // NOSONAR
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
                && (!personNameDto.getLastNm().trim().isEmpty())
                && (personNameDto.getFirstNm() != null)
                && (!personNameDto.getFirstNm().trim().isEmpty()))
        {
            namesStr = personNameDto.getLastNm() + caret + personNameDto.getFirstNm();
            asofDate = personNameDto.getAsOfDate(); // NOSONAR
        }
        return namesStr;
    }

}
