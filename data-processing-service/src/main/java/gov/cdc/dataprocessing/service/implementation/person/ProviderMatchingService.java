package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.MsgType;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueDpDpService;
import gov.cdc.dataprocessing.service.implementation.person.base.ProviderMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.person.IProviderMatchingService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PHCR_IMPORT_SRT;

@Service

public class ProviderMatchingService extends ProviderMatchingBaseService implements IProviderMatchingService {

    public ProviderMatchingService(
            EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil,
            EntityHelper entityHelper,
            PatientRepositoryUtil patientRepositoryUtil,
            CachingValueDpDpService cachingValueDpService,
            PrepareAssocModelHelper prepareAssocModelHelper) {
        super(edxPatientMatchRepositoryUtil, entityHelper, patientRepositoryUtil, cachingValueDpService, prepareAssocModelHelper);
    }

    @SuppressWarnings("java:S2259")
    public EDXActivityDetailLogDto getMatchingProvider(PersonContainer personContainer) throws DataProcessingException {
        Long entityUid = personContainer.getThePersonDto().getPersonUid();
        List<EdxEntityMatchDto> matchesToPersist = new ArrayList<>();
        EDXActivityDetailLogDto logDto = createLogDtoTemplate();

        String localId = normalize(getLocalId(personContainer));
        if (attemptMatch(localId, personContainer, matchesToPersist, logDto)) return logDto;

        List<String> identifiers = getIdentifier(personContainer);
        if (identifiers != null) {
            for (String identifier : identifiers) {
                if (attemptMatch(normalize(identifier), personContainer, matchesToPersist, logDto)) return logDto;
            }
        }

        String nameAddr1 = normalize(nameAddressStreetOneProvider(personContainer));
        if (attemptMatch(nameAddr1, personContainer, matchesToPersist, logDto)) return logDto;

        String phone = normalize(telePhoneTxtProvider(personContainer));
        if (attemptMatch(phone, personContainer, matchesToPersist, logDto)) return logDto;

        // Create provider if not found
        if (NEDSSConstant.PRV.equals(personContainer.getThePersonDto().getCd())) {
            entityUid = processingProvider(personContainer, "PROVIDER", NEDSSConstant.PRV_CR);
        }

        persistMatchIfNotNull(nameAddr1, nameAddr1.hashCode(), entityUid, personContainer);
        persistMatchIfNotNull(phone, phone.hashCode(), entityUid, personContainer);

        for (EdxEntityMatchDto dto : matchesToPersist) {
            dto.setEntityUid(entityUid);
            persistIfNoRole(dto, personContainer);
        }

        return buildLogDto(logDto, entityUid, "Provider not found. New Provider created with person uid : ");
    }

    boolean attemptMatch(String matchString, PersonContainer container, List<EdxEntityMatchDto> matches, EDXActivityDetailLogDto logDto) throws DataProcessingException {
        if (matchString == null) return false;

        EdxEntityMatchDto matchedDto = getEdxPatientMatchRepositoryUtil().getEdxEntityMatchOnMatchString(NEDSSConstant.PRV, matchString);
        if (matchedDto != null && matchedDto.getEntityUid() != null) {
            logMatch(logDto, matchedDto.getEntityUid());
            if (container.getRole() == null) {
                persistIfNoRole(createMatchDto(matchString, matchString.hashCode()), container, matchedDto.getEntityUid());
            }
            return true;
        }

        matches.add(createMatchDto(matchString, matchString.hashCode()));
        return false;
    }

    protected void persistMatchIfNotNull(String matchString, int hashCode, Long entityUid, PersonContainer container)  {
        if (matchString != null) {
            EdxEntityMatchDto dto = createMatchDto(matchString, hashCode);
            dto.setEntityUid(entityUid);
            persistIfNoRole(dto, container);
        }
    }

    protected void persistIfNoRole(EdxEntityMatchDto dto, PersonContainer container)  {
        if (container.getRole() == null) {
            getEdxPatientMatchRepositoryUtil().saveEdxEntityMatch(dto);
        }
    }

    protected void persistIfNoRole(EdxEntityMatchDto dto, PersonContainer container, Long entityUid)  {
        dto.setEntityUid(entityUid);
        persistIfNoRole(dto, container);
    }

    private EdxEntityMatchDto createMatchDto(String matchString, int hashCode) {
        EdxEntityMatchDto dto = new EdxEntityMatchDto();
        dto.setTypeCd(NEDSSConstant.PRV);
        dto.setMatchString(matchString);
        dto.setMatchStringHashCode((long) hashCode);
        return dto;
    }

    private void logMatch(EDXActivityDetailLogDto logDto, Long entityUid) {
        logDto.setRecordId(String.valueOf(entityUid));
        logDto.setComment("Provider entity found with entity uid : " + entityUid);
        logDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
    }

    private EDXActivityDetailLogDto buildLogDto(EDXActivityDetailLogDto logDto, Long entityUid, String message) {
        logDto.setRecordId(String.valueOf(entityUid));
        logDto.setComment(message + entityUid);
        logDto.setLogType(String.valueOf(EdxRuleAlgorothmManagerDto.STATUS_VAL.Success));
        return logDto;
    }

    private EDXActivityDetailLogDto createLogDtoTemplate() {
        EDXActivityDetailLogDto dto = new EDXActivityDetailLogDto();
        dto.setRecordType(String.valueOf(MsgType.Provider));
        dto.setRecordName(PHCR_IMPORT_SRT);
        return dto;
    }

    private String normalize(String input) {
        return (input != null) ? input.toUpperCase() : null;
    }


    public Long setProvider(PersonContainer personContainer, String businessTriggerCd) throws DataProcessingException {
       return processingProvider(personContainer,  "",  businessTriggerCd) ;
    }

}
