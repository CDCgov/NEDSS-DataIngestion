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

    public Collection<Object> getContactListForInvestigation(Long publicHealthCaseUID) throws DataProcessingException {
        Collection<Object> coll = new ArrayList<>();
        coll.addAll(getPHCContactNamedByPatientSummDTColl(publicHealthCaseUID));
        coll.addAll(getPHCPatientNamedAsContactSummDTColl(publicHealthCaseUID));
        coll.addAll(getPHCPatientOtherNamedAsContactSummDTColl(publicHealthCaseUID));
        return coll;
    }

    private Collection<Object> getPHCContactNamedByPatientSummDTColl(Long publicHealthCaseUID) throws DataProcessingException {
        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.CT_CONTACT,"VIEW", "");

        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = " AND " + dataAccessWhereClause;
            dataAccessWhereClause = dataAccessWhereClause.replaceAll("program_jurisdiction_oid", "CT_CONTACT.program_jurisdiction_oid");
            dataAccessWhereClause = dataAccessWhereClause.replaceAll("shared_ind", "CT_CONTACT.shared_ind_cd");
        }

        String dataAccessWhereClause1 = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");

        if (dataAccessWhereClause1 == null) {
            dataAccessWhereClause1 = "";
        }
        else {
            dataAccessWhereClause1 = " AND " + dataAccessWhereClause1;
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll("program_jurisdiction_oid", "contact.program_jurisdiction_oid");
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll("shared_ind", "contact.shared_ind");
        }
        Collection<Object>  PHCcTContactNameByPatientSummDTColl;
        String sql  =SELECT_PHCPAT_NAMED_BY_PATIENT_COLLECTION1 + dataAccessWhereClause1
                + SELECT_PHCPAT_NAMED_BY_PATIENT_COLLECTION3 + publicHealthCaseUID+ dataAccessWhereClause;
        PHCcTContactNameByPatientSummDTColl = getContactNamedByPatientDTColl(sql);
        return PHCcTContactNameByPatientSummDTColl;
    }

    private Collection<Object> getPHCPatientNamedAsContactSummDTColl(Long publicHealthCaseUID) throws DataProcessingException {
        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");
        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = " AND " + dataAccessWhereClause;
            dataAccessWhereClause = dataAccessWhereClause.replaceAll("program_jurisdiction_oid", "subject.program_jurisdiction_oid");
            dataAccessWhereClause = dataAccessWhereClause.replaceAll("shared_ind", "subject.shared_ind");
        }
        String dataAccessWhereClause1 = queryHelper.getDataAccessWhereClause(NBSBOLookup.CT_CONTACT, "VIEW", "");
        if (dataAccessWhereClause1 == null) {
            dataAccessWhereClause1 = "";
        }
        else {
            dataAccessWhereClause1 = " AND " + dataAccessWhereClause1;
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll("program_jurisdiction_oid", "CT_CONTACT.program_jurisdiction_oid");
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll("shared_ind", "CT_CONTACT.shared_ind_cd");
        }
        Collection<Object>  PHCcTContactNameByPatientSummDTColl;
        String sql  = SELECT_PHCPAT_NAMED_BY_CONTACT_COLLECTION +publicHealthCaseUID
                + dataAccessWhereClause + dataAccessWhereClause1;
        PHCcTContactNameByPatientSummDTColl = getPatientNamedAsContactSummDTColl(sql, false);
        return PHCcTContactNameByPatientSummDTColl;
    }


    private Collection<Object> getPHCPatientOtherNamedAsContactSummDTColl(Long publicHealthCaseUID) throws DataProcessingException {
        String dataAccessWhereClause = queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", "");
        if (dataAccessWhereClause == null) {
            dataAccessWhereClause = "";
        }
        else {
            dataAccessWhereClause = " AND " + dataAccessWhereClause;
            dataAccessWhereClause = dataAccessWhereClause.replaceAll("program_jurisdiction_oid", "subject.program_jurisdiction_oid");
            dataAccessWhereClause = dataAccessWhereClause.replaceAll("shared_ind", "subject.shared_ind");
        }
        String dataAccessWhereClause1 = queryHelper.getDataAccessWhereClause(NBSBOLookup.CT_CONTACT, "VIEW", "");

        if (dataAccessWhereClause1 == null) {
            dataAccessWhereClause1 = "";
        }
        else {
            dataAccessWhereClause1 = " AND " + dataAccessWhereClause1;
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll("program_jurisdiction_oid", "CT_CONTACT.program_jurisdiction_oid");
            dataAccessWhereClause1 = dataAccessWhereClause1.replaceAll("shared_ind", "CT_CONTACT.shared_ind_cd");
        }
        Collection<Object>  PHCcTContactNameByPatientSummDTColl;
        String sql  =SELECT_PHCPAT_OTHER_NAMED_BY_CONTACT_COLLECTION + publicHealthCaseUID
                + dataAccessWhereClause+dataAccessWhereClause1;
        PHCcTContactNameByPatientSummDTColl = getPatientNamedAsContactSummDTColl(sql, true);
        return PHCcTContactNameByPatientSummDTColl;
    }


    private  Collection<Object> getContactNamedByPatientDTColl(String sql) throws DataProcessingException {
        CTContactSummaryDto cTContactSummaryDto = new CTContactSummaryDto();
        ArrayList<CTContactSummaryDto>  cTContactNameByPatientSummDTColl ;
        ArrayList<Object>  returnCTContactNameByPatientSummDTColl  = new ArrayList<> ();
        cTContactNameByPatientSummDTColl  = new ArrayList<>(customRepository.getContactByPatientInfo(sql));
        for (CTContactSummaryDto cTContactSumyDT : cTContactNameByPatientSummDTColl) {
            cTContactSumyDT.setContactNamedByPatient(true);
            Long contactEntityUid = cTContactSumyDT.getContactEntityUid();
            var lst = personNameRepository.findByParentUid(contactEntityUid);
            Collection personNameColl = new ArrayList<>();
            if (lst.isPresent()) {
                personNameColl = lst.get();
            }

            //add the contact summary dt
            returnCTContactNameByPatientSummDTColl.add(cTContactSumyDT);

            Collection contactNameColl = new ArrayList<>();
            if (lst.isPresent()) {
                contactNameColl = lst.get();
            }

            if (contactNameColl.size() > 0) {
                for (Object o : contactNameColl) {
                    PersonNameDto personNameDT = new PersonNameDto( (PersonName) o);
                    if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                        String lastName = (personNameDT.getLastNm() == null) ? "No Last" : personNameDT.getLastNm();
                        String firstName = (personNameDT.getFirstNm() == null) ? "No First" : personNameDT.getFirstNm();
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
                if (ctOtherNameColl.size() > 0) {
                    for (PersonName name : ctOtherNameColl) {
                        PersonNameDto personNameDT = new PersonNameDto(name);
                        if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                            String lastName = (personNameDT.getLastNm() == null) ? "No Last" : personNameDT.getLastNm();
                            String firstName = (personNameDT.getFirstNm() == null) ? "No First" : personNameDT.getFirstNm();
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

            if (subjectNameColl.size() > 0) {
                for (PersonName name : subjectNameColl) {
                    PersonNameDto personNameDT = new PersonNameDto(name);
                    if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                        String lastName = (personNameDT.getLastNm() == null) ? "No Last" : personNameDT.getLastNm();
                        String firstName = (personNameDT.getFirstNm() == null) ? "No First" : personNameDT.getFirstNm();
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
                if (lst2.isPresent()) {
                    if (lst.isPresent()) {
                        contactNameColl = lst.get();
                    }
                }


                if (contactNameColl.size() > 0) {
                    for (PersonName name : contactNameColl) {
                        PersonNameDto personNameDT = new PersonNameDto(name);
                        if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                            String lastName = (personNameDT.getLastNm() == null) ? "No Last" : personNameDT.getLastNm();
                            String firstName = (personNameDT.getFirstNm() == null) ? "No First" : personNameDT.getFirstNm();
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

                if (ctOtherNameColl.size() > 0) {
                    for (PersonName name : ctOtherNameColl) {
                        PersonNameDto personNameDT = new PersonNameDto(name);
                        if (personNameDT.getNmUseCd().equalsIgnoreCase(NEDSSConstant.LEGAL_NAME)) {
                            String lastName = (personNameDT.getLastNm() == null) ? "No Last" : personNameDT.getLastNm();
                            String firstName = (personNameDT.getFirstNm() == null) ? "No First" : personNameDT.getFirstNm();
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
