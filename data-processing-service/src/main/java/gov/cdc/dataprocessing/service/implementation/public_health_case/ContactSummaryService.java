package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.CTConstants;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.phc.CTContactSummaryDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonNameRepository;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IContactSummaryService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gov.cdc.dataprocessing.constant.ComplexQueries.*;
import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.AND_UPPERCASE;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.*;

@Service

public class ContactSummaryService implements IContactSummaryService {

    private final QueryHelper queryHelper;
    private final PersonNameRepository personNameRepository;
    private final CustomRepository customRepository;
    private final IRetrieveSummaryService retrieveSummaryService;

    public ContactSummaryService(QueryHelper queryHelper,
                                 PersonNameRepository personNameRepository,
                                 CustomRepository customRepository, IRetrieveSummaryService retrieveSummaryService) {
        this.queryHelper = queryHelper;
        this.personNameRepository = personNameRepository;
        this.customRepository = customRepository;
        this.retrieveSummaryService = retrieveSummaryService;
    }


    @Override
    public Collection<Object> getContactListForInvestigation(Long phcUid) throws DataProcessingException {
        Collection<Object> contacts = new ArrayList<>();
        contacts.addAll(getNamedByPatientContacts(phcUid));
        contacts.addAll(getNamedAsContactContacts(phcUid));
        contacts.addAll(getOtherNamedAsContactContacts(phcUid));
        return contacts;
    }

    private Collection<Object> getNamedByPatientContacts(Long phcUid) {
        String sql = buildSql(
                SELECT_PHCPAT_NAMED_BY_PATIENT_COLLECTION1,
                SELECT_PHCPAT_NAMED_BY_PATIENT_COLLECTION3,
                phcUid,
                NBSBOLookup.CT_CONTACT, NBSBOLookup.INVESTIGATION,
                "contact.program_jurisdiction_oid", "contact.shared_ind"
        );
        return enhanceContactSummaries(customRepository.getContactByPatientInfo(sql), true);
    }

    private Collection<Object> getNamedAsContactContacts(Long phcUid) throws DataProcessingException {
        String sql = buildSql(
                SELECT_PHCPAT_NAMED_BY_CONTACT_COLLECTION,
                "",
                phcUid,
                NBSBOLookup.INVESTIGATION, NBSBOLookup.CT_CONTACT,
                "subject.program_jurisdiction_oid", "subject.shared_ind"
        );
        return enhanceNamedAsContactSummaries(customRepository.getContactByPatientInfo(sql), false);
    }

    private Collection<Object> getOtherNamedAsContactContacts(Long phcUid) throws DataProcessingException {
        String sql = buildSql(
                SELECT_PHCPAT_OTHER_NAMED_BY_CONTACT_COLLECTION,
                "",
                phcUid,
                NBSBOLookup.INVESTIGATION, NBSBOLookup.CT_CONTACT,
                "subject.program_jurisdiction_oid", "subject.shared_ind"
        );
        return enhanceNamedAsContactSummaries(customRepository.getContactByPatientInfo(sql), true);
    }

    private String buildSql(String baseSql1, String baseSql2, Long phcUid,
                            String lookup1, String lookup2, String progJusOidAlias, String sharedIndAlias) {
        String where1 = applyDataAccessReplacements(queryHelper.getDataAccessWhereClause(lookup1, "VIEW", ""), progJusOidAlias, sharedIndAlias);
        String where2 = applyDataAccessReplacements(queryHelper.getDataAccessWhereClause(lookup2, "VIEW", ""), CT_PROGRAM_JUS_OID, CT_SHARED_IND_CD);
        return baseSql1 + where1 + baseSql2 + phcUid + where2;
    }

    protected String applyDataAccessReplacements(String clause, String progJusOid, String sharedInd) {
        if (clause == null || clause.isBlank()) return "";
        return AND_UPPERCASE + clause
                .replaceAll(PROGRAM_JUS_OID, progJusOid)
                .replaceAll(SHARED_IND, sharedInd);
    }

    private Collection<Object> enhanceContactSummaries(Collection<CTContactSummaryDto> contacts, boolean namedByPatient) {
        List<Object> result = new ArrayList<>();
        for (CTContactSummaryDto dto : contacts) {
            dto.setContactNamedByPatient(namedByPatient);
            dto.setContactName(resolvePersonName(dto.getContactEntityUid()));
            dto.setOtherInfectedPatientName(resolvePersonName(dto.getThirdPartyEntityUid()));
            updateDisposition(dto);
            result.add(dto);
        }
        return result;
    }

    private Collection<Object> enhanceNamedAsContactSummaries(Collection<CTContactSummaryDto> contacts, boolean otherInfected) throws DataProcessingException {
        List<Object> result = new ArrayList<>();
        for (CTContactSummaryDto dto : contacts) {
            dto.setPatientNamedByContact(true);
            dto.setOtherNamedByPatient(otherInfected);
            dto.setAssociatedMap(retrieveSummaryService.getAssociatedDocumentList(
                    dto.getCtContactUid(), CLASS_CD_CONTACT, ACT_CLASS_CD_FOR_DOC
            ));
            dto.setSubjectName(resolvePersonName(dto.getSubjectEntityUid()));
            dto.setContactName(resolvePersonName(dto.getContactEntityUid()));
            dto.setOtherInfectedPatientName(resolvePersonName(dto.getThirdPartyEntityUid()));
            dto.setDispositionCd(dto.getSourceDispositionCd());
            result.add(dto);
        }
        return result;
    }

    protected String resolvePersonName(Long entityUid) {
        if (entityUid == null) return null;
        return personNameRepository.findByParentUid(entityUid)
                .flatMap(names -> names.stream()
                        .filter(n -> NEDSSConstant.LEGAL_NAME.equalsIgnoreCase(n.getNmUseCd()))
                        .map(n -> formatName(n.getLastNm(), n.getFirstNm()))
                        .findFirst())
                .orElse(null);
    }

    protected String formatName(String last, String first) {
        String l = (last == null) ? NO_LAST_NAME_INVESTIGATOR : last;
        String f = (first == null) ? NO_FIRST_NAME_INVESTIGATOR : first;
        return l + ", " + f;
    }

    protected void updateDisposition(CTContactSummaryDto dto) {
        String decision = dto.getContactProcessingDecisionCd();
        String disposition = dto.getDispositionCd();
        if ((CTConstants.RecordSearchClosure.equals(decision) || CTConstants.SecondaryReferral.equals(decision))
                && disposition != null) {
            if ("A".equals(disposition)) dto.setDispositionCd("Z");
            else if ("C".equals(disposition)) dto.setDispositionCd("E");
        }
    }


}
