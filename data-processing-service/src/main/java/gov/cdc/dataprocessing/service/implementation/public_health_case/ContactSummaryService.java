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

import static gov.cdc.dataprocessing.constant.ComplexQueries.*;
import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.AND_UPPERCASE;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.*;

@Service
/**
 * Do not Attempt to refactor this Class Unless you know what need to be done
 * Blindly refactoring this class will causing the significant failure with Case Notification and Mark As Review action
 * */
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

    public Collection<Object> getContactListForInvestigation(Long publicHealthCaseUID) throws DataProcessingException {
        Collection<Object> coll = new ArrayList<>();
        coll.addAll(getPHCContactNamedByPatientSummDTColl(publicHealthCaseUID));
        coll.addAll(getPHCPatientNamedAsContactSummDTColl(publicHealthCaseUID));
        coll.addAll(getPHCPatientOtherNamedAsContactSummDTColl(publicHealthCaseUID));
        return coll;
    }

    @SuppressWarnings("java:S5361")
    private Collection<Object> getPHCContactNamedByPatientSummDTColl(Long publicHealthCaseUID) {
        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.CT_CONTACT,"VIEW", "");

        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = AND_UPPERCASE + dataAccessWhereClause;
            dataAccessWhereClause = dataAccessWhereClause.replaceAll(PROGRAM_JUS_OID, CT_PROGRAM_JUS_OID);
            dataAccessWhereClause = dataAccessWhereClause.replaceAll(SHARED_IND, CT_SHARED_IND_CD);
        }

        String dataAccessWhereClause1 = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");

        if (dataAccessWhereClause1 == null) {
            dataAccessWhereClause1 = "";
        }
        else {
            dataAccessWhereClause1 = AND_UPPERCASE + dataAccessWhereClause1;
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll(PROGRAM_JUS_OID, "contact.program_jurisdiction_oid");
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll(SHARED_IND, "contact.shared_ind");
        }
        Collection<Object>  phcTContactNameByPatientSummDTColl;
        String sql  =SELECT_PHCPAT_NAMED_BY_PATIENT_COLLECTION1 + dataAccessWhereClause1
                + SELECT_PHCPAT_NAMED_BY_PATIENT_COLLECTION3 + publicHealthCaseUID+ dataAccessWhereClause;
        phcTContactNameByPatientSummDTColl = getContactNamedByPatientDTColl(sql);
        return phcTContactNameByPatientSummDTColl;
    }

    @SuppressWarnings("java:S5361")
    private Collection<Object> getPHCPatientNamedAsContactSummDTColl(Long publicHealthCaseUID) throws DataProcessingException {
        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");
        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = AND_UPPERCASE + dataAccessWhereClause;
            dataAccessWhereClause = dataAccessWhereClause.replaceAll(PROGRAM_JUS_OID, "subject.program_jurisdiction_oid");
            dataAccessWhereClause = dataAccessWhereClause.replaceAll(SHARED_IND, "subject.shared_ind");
        }
        String dataAccessWhereClause1 = queryHelper.getDataAccessWhereClause(NBSBOLookup.CT_CONTACT, "VIEW", "");
        if (dataAccessWhereClause1 == null) {
            dataAccessWhereClause1 = "";
        }
        else {
            dataAccessWhereClause1 = AND_UPPERCASE + dataAccessWhereClause1;
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll(PROGRAM_JUS_OID, CT_PROGRAM_JUS_OID);
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll(SHARED_IND, CT_SHARED_IND_CD);
        }
        Collection<Object>  phcTContactNameByPatientSummDTColl;
        String sql  = SELECT_PHCPAT_NAMED_BY_CONTACT_COLLECTION +publicHealthCaseUID
                + dataAccessWhereClause + dataAccessWhereClause1;
        phcTContactNameByPatientSummDTColl = getPatientNamedAsContactSummDTColl(sql, false);
        return phcTContactNameByPatientSummDTColl;
    }

    @SuppressWarnings("java:S5361")
    private Collection<Object> getPHCPatientOtherNamedAsContactSummDTColl(Long publicHealthCaseUID) throws DataProcessingException {
        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");
        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = AND_UPPERCASE + dataAccessWhereClause;
            dataAccessWhereClause = dataAccessWhereClause.replaceAll(PROGRAM_JUS_OID, "subject.program_jurisdiction_oid");
            dataAccessWhereClause = dataAccessWhereClause.replaceAll(SHARED_IND, "subject.shared_ind");
        }
        String dataAccessWhereClause1 = queryHelper.getDataAccessWhereClause(NBSBOLookup.CT_CONTACT, "VIEW", "");

        if (dataAccessWhereClause1 == null) {
            dataAccessWhereClause1 = "";
        }
        else {
            dataAccessWhereClause1 = AND_UPPERCASE + dataAccessWhereClause1;
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll(PROGRAM_JUS_OID, CT_PROGRAM_JUS_OID);
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll(SHARED_IND, CT_SHARED_IND_CD);
        }
        Collection<Object>  phcTContactNameByPatientSummDTColl;
        String sql  =SELECT_PHCPAT_OTHER_NAMED_BY_CONTACT_COLLECTION + publicHealthCaseUID
                + dataAccessWhereClause+dataAccessWhereClause1;
        phcTContactNameByPatientSummDTColl = getPatientNamedAsContactSummDTColl(sql, true);
        return phcTContactNameByPatientSummDTColl;
    }

    @SuppressWarnings("java:S3776")
    private  Collection<Object> getContactNamedByPatientDTColl(String sql) {
        ArrayList<CTContactSummaryDto>  cTContactNameByPatientSummDTColl ;
        ArrayList<Object>  returnCTContactNameByPatientSummDTColl  = new ArrayList<> ();
        cTContactNameByPatientSummDTColl  = new ArrayList<>(customRepository.getContactByPatientInfo(sql));
        for (CTContactSummaryDto cTContactSumyDT : cTContactNameByPatientSummDTColl) {
            cTContactSumyDT.setContactNamedByPatient(true);
            Long contactEntityUid = cTContactSumyDT.getContactEntityUid();
            var lst = personNameRepository.findByParentUid(contactEntityUid);

            //add the contact summary dt
            returnCTContactNameByPatientSummDTColl.add(cTContactSumyDT);

            Collection<PersonName> contactNameColl = new ArrayList<>();
            if (lst.isPresent()) {
                contactNameColl = lst.get();
            }

            if (!contactNameColl.isEmpty()) {
                for (PersonName o : contactNameColl) {
                    PersonNameDto personNameDT = new PersonNameDto(o);
                    if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                        String lastName = (personNameDT.getLastNm() == null) ? NO_LAST_NAME_INVESTIGATOR : personNameDT.getLastNm();
                        String firstName = (personNameDT.getFirstNm() == null) ? NO_FIRST_NAME_INVESTIGATOR : personNameDT.getFirstNm();
                        String personName = lastName + ", " + firstName;
                        cTContactSumyDT.setName(personName);
                        cTContactSumyDT.setContactName(personName);
                        break;
                    }
                } //name iter
            }//name coll not null
            //Other Infected Person is seldom present
            Long otherEntityUid = cTContactSumyDT.getThirdPartyEntityUid();
            if (otherEntityUid != null) {
                Collection<PersonName> ctOtherNameColl = new ArrayList<>();
                var lst3 = personNameRepository.findByParentUid(otherEntityUid);
                if (lst3.isPresent()) {
                    ctOtherNameColl = lst3.get();
                }
                if (!ctOtherNameColl.isEmpty()) {
                    for (PersonName name : ctOtherNameColl) {
                        PersonNameDto personNameDT = new PersonNameDto(name);
                        if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                            String lastName = (personNameDT.getLastNm() == null) ? NO_LAST_NAME_INVESTIGATOR : personNameDT.getLastNm();
                            String firstName = (personNameDT.getFirstNm() == null) ? NO_FIRST_NAME_INVESTIGATOR : personNameDT.getFirstNm();
                            String personName = lastName + ", " + firstName;
                            cTContactSumyDT.setOtherInfectedPatientName(personName);
                            break;
                        }
                    }
                }
            }
            //Business rule with convoluted logic, if contact Processing Decision is RSC or SR and the contiact's investigation disposition
            // is A, the disposition of the Contact Record will be Z.
            //If the disposition on the Contact�s existing investigation is C, the disposition of the Contact Record will be E.
            if (cTContactSumyDT.getContactProcessingDecisionCd() != null &&
                    cTContactSumyDT.getDispositionCd() != null &&
                    (cTContactSumyDT.getContactProcessingDecisionCd().equals(CTConstants.RecordSearchClosure)
                            || cTContactSumyDT.getContactProcessingDecisionCd().equals(CTConstants.SecondaryReferral)))
            {
                if (cTContactSumyDT.getDispositionCd().equals("A")) //preventative treatment
                {
                    cTContactSumyDT.setDispositionCd("Z"); //prev preventative treated
                }
                else if (cTContactSumyDT.getDispositionCd().equals("C")) //infected brought to treat
                {
                    cTContactSumyDT.setDispositionCd("E"); //prev treated
                }
            }
        } //while
        return returnCTContactNameByPatientSummDTColl;
    }

    @SuppressWarnings("java:S3776")

    private Collection<Object> getPatientNamedAsContactSummDTColl(String sql, boolean otherInfected) throws  DataProcessingException {
        ArrayList<CTContactSummaryDto>  ctNameByPatientSummDTColl;
        ArrayList<Object>  returnCTNameByPatientSummDTColl  = new ArrayList<> ();

        ctNameByPatientSummDTColl  = new ArrayList<>(customRepository.getContactByPatientInfo(sql));
        for (CTContactSummaryDto cTContactSumyDT : ctNameByPatientSummDTColl) {
            cTContactSumyDT.setContactNamedByPatient(false);
            cTContactSumyDT.setPatientNamedByContact(true);
            cTContactSumyDT.setOtherNamedByPatient(otherInfected);

            cTContactSumyDT.setAssociatedMap(retrieveSummaryService.getAssociatedDocumentList(
                    cTContactSumyDT.getCtContactUid(),
                    NEDSSConstant.CLASS_CD_CONTACT,
                    NEDSSConstant.ACT_CLASS_CD_FOR_DOC));
            //go ahead and add the summary dt into the collection
            returnCTNameByPatientSummDTColl.add(cTContactSumyDT);

            //get the subject name
            Long contactSubjectEntityUid = cTContactSumyDT.getSubjectEntityUid();


            Collection<PersonName> subjectNameColl = new ArrayList<>();

            var lst = personNameRepository.findByParentUid(contactSubjectEntityUid);
            if (lst.isPresent()) {
                subjectNameColl = lst.get();
            }

            if (!subjectNameColl.isEmpty()) {
                for (PersonName name : subjectNameColl) {
                    PersonNameDto personNameDT = new PersonNameDto(name);
                    if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                        String lastName = (personNameDT.getLastNm() == null) ? NO_LAST_NAME_INVESTIGATOR : personNameDT.getLastNm();
                        String firstName = (personNameDT.getFirstNm() == null) ? NO_FIRST_NAME_INVESTIGATOR : personNameDT.getFirstNm();
                        String personName = lastName + ", " + firstName;
                        cTContactSumyDT.setNamedBy(personName);
                        cTContactSumyDT.setSubjectName(personName);
                        break;
                    }
                }
            }

            //get the Contact Name
            Long contactEntityUid = cTContactSumyDT.getContactEntityUid();
            if (contactEntityUid != null) {
                Collection<PersonName> contactNameColl = new ArrayList<>();

                var lst2 = personNameRepository.findByParentUid(contactEntityUid);
                if (lst2.isPresent() && lst.isPresent()) {
                    contactNameColl = lst.get();
                }


                if (!contactNameColl.isEmpty()) {
                    for (PersonName name : contactNameColl) {
                        PersonNameDto personNameDT = new PersonNameDto(name);
                        if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                            String lastName = (personNameDT.getLastNm() == null) ? NO_LAST_NAME_INVESTIGATOR : personNameDT.getLastNm();
                            String firstName = (personNameDT.getFirstNm() == null) ? NO_FIRST_NAME_INVESTIGATOR : personNameDT.getFirstNm();
                            String personName = lastName + ", " + firstName;
                            cTContactSumyDT.setContactName(personName);
                            break;
                        }
                    }
                }
            } //contact Entity not null
            //Other Infected Person is seldom present
            Long otherEntityUid = cTContactSumyDT.getThirdPartyEntityUid();
            if (otherEntityUid != null) {
                Collection<PersonName> ctOtherNameColl = new ArrayList<>();
                var lst3 = personNameRepository.findByParentUid(otherEntityUid);
                if (lst3.isPresent()) {
                    ctOtherNameColl = lst3.get();
                }

                if (!ctOtherNameColl.isEmpty()) {
                    for (PersonName name : ctOtherNameColl) {
                        PersonNameDto personNameDT = new PersonNameDto(name);
                        if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                            String lastName = (personNameDT.getLastNm() == null) ? NO_LAST_NAME_INVESTIGATOR : personNameDT.getLastNm();
                            String firstName = (personNameDT.getFirstNm() == null) ? NO_FIRST_NAME_INVESTIGATOR : personNameDT.getFirstNm();
                            String personName = lastName + ", " + firstName;
                            cTContactSumyDT.setOtherInfectedPatientName(personName);
                            break;
                        }
                    }
                }
            } //other entity
            //Setting the disposition to the source patient's disposition for the section 'patient named by contacts'
            cTContactSumyDT.setDispositionCd(cTContactSumyDT.getSourceDispositionCd());

        } //has next
        return returnCTNameByPatientSummDTColl;
    }





}