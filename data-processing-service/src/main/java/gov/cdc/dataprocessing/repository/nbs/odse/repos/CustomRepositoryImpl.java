package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.phc.CTContactSummaryDto;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PublicHealthCaseStoredProcRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import static gov.cdc.dataprocessing.constant.ComplexQueries.*;

@Repository
public class CustomRepositoryImpl implements CustomRepository {
    @PersistenceContext(unitName = "odse")
    private EntityManager entityManager;
    private final PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository;

    public CustomRepositoryImpl(PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository) {
        this.publicHealthCaseStoredProcRepository = publicHealthCaseStoredProcRepository;
    }

    public List<StateDefinedFieldDataDto> getLdfCollection(Long busObjectUid, String conditionCode, String theQuery) {
        Query query = entityManager.createNativeQuery(theQuery);
        query.setParameter("businessObjUid", busObjectUid);
        query.setParameter("conditionCd", conditionCode);
        List<StateDefinedFieldDataDto> lst = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                StateDefinedFieldDataDto container = new StateDefinedFieldDataDto();
                int i = 0;
                container.setLdfUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                container.setBusinessObjNm(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setAddTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setBusinessObjUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setLastChgTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setLdfValue(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setVersionCtrlNbr(dataNotNull(item[++i]) ? Integer.valueOf(item[i].toString()): null);
                lst.add(container);
            }
        }
        return lst;
    }

    public Map<Object, Object> getAssociatedDocumentList(Long uid, String targetClassCd, String sourceClassCd, String theQuery) {
        Map<Object, Object> map= new HashMap<> ();
        Query query = entityManager.createNativeQuery(theQuery);
        query.setParameter("TargetActUid", uid);
        query.setParameter("SourceClassCd", sourceClassCd);
        query.setParameter("TargetClassCd", targetClassCd);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                map.put(item[0].toString(), Long.valueOf(item[1].toString()));
            }
        }
        return map;
    }
    public Map<String, EDXEventProcessDto>getEDXEventProcessMapByCaseId(Long publicHealthCaseUid) {
        String docQuery = " SELECT"
                + " edx_event_process_uid  \"eDXEventProcessUid\", "
                + " nbs_document_uid  \"nbsDocumentUid\", "
                + " nbs_event_uid  \"nbsEventUid\", "
                + " source_event_id \"sourceEventId\", "
                + " doc_event_type_cd \"docEventTypeCd\", "
                + " edx_event_process.add_user_id \"addUserId\", " + " edx_event_process.add_time \"addTime\", "
                + " parsed_ind \"parsedInd\" "
                + " FROM edx_event_process, act_relationship "
                + " where edx_event_process.nbs_event_uid=act_relationship.source_act_uid "
                + " and act_relationship.target_act_uid = :TargetActUid order by nbs_document_uid";

        Map<String, EDXEventProcessDto> map= new HashMap<> ();
        Query query = entityManager.createNativeQuery(docQuery);
        query.setParameter("TargetActUid", publicHealthCaseUid);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                EDXEventProcessDto container = new EDXEventProcessDto();
                int i = 0;
                container.setEDXEventProcessUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                container.setNbsDocumentUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setNbsEventUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);

                Long sourceEventId = Long.valueOf(item[++i].toString());
                container.setSourceEventId(dataNotNull(item[i]) ? String.valueOf(item[i].toString()): null);

                container.setDocEventTypeCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setAddUserId(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setAddTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setParsedInd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);


                map.put(sourceEventId.toString(), container);
            }
        }
        return map;
    }

    public Map<Object, Object> retrieveDocumentSummaryVOForInv(Long publicHealthUID) {
        Map<Object,Object> map= new HashMap<Object,Object> ();
        Query query = entityManager.createNativeQuery(DOCUMENT_FOR_A_PHC);
        query.setParameter("PhcUid", publicHealthUID);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                DocumentSummaryContainer container = new DocumentSummaryContainer();
                int i = 0;
                Long phcUid =  Long.valueOf(item[i].toString());
                Long getNbsDocumentUid = Long.valueOf(item[++i].toString());
                container.setNbsDocumentUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);

                container.setDocType(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCdDescTxt(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setAddTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setLocalId(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCdDescTxt(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);


                map.put(getNbsDocumentUid, container);
            }
        }
        return map;
    }

    public List<NotificationSummaryContainer> retrieveNotificationSummaryListForInvestigation(Long publicHealthUID, String theQuery) {
        List<NotificationSummaryContainer> map= new ArrayList<> ();
        Query query = entityManager.createNativeQuery(theQuery);
        query.setParameter("PhcUid", publicHealthUID);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                NotificationSummaryContainer container = new NotificationSummaryContainer();
                int i = 0;
                container.setNotificationUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                container.setCdNotif(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setAddTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setRptSentTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setRecordStatusTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setJurisdictionCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setProgramJurisdictionOid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setCaseClassCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setAutoResendInd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCaseClassCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setLocalId(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setTxt(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setRecordStatusCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setIsHistory(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setNndInd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setRecipient(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                map.add(container);
            }
        }
        return map;
    }

    public Map<Object, Object> retrieveTreatmentSummaryVOForInv(Long publicHealthUID, String theQuery) {
        Map<Object,Object> map= new HashMap<Object,Object> ();
        Query query = entityManager.createNativeQuery(theQuery);
        query.setParameter("PhcUid", publicHealthUID);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                TreatmentContainer container = new TreatmentContainer();
                int i = 0;
                container.setPhcUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                Long treatmentId = Long.valueOf(item[++i].toString());
                container.setTreatmentUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                container.setTreatmentNameCode(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCustomTreatmentNameCode(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setActivityFromTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setLocalId(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                map.put(treatmentId, container);
            }
        }
        return map;
    }

    public Map<Object,Object>  getAssociatedInvList(Long uid,String sourceClassCd, String theQuery) {
        Map<Object,Object> assocoiatedInvMap= new HashMap<Object,Object> ();
        Query query = entityManager.createNativeQuery(theQuery);
        query.setParameter("ClassCd", sourceClassCd);
        query.setParameter("ActUid",uid);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                assocoiatedInvMap.put(item[0].toString(), item[1].toString());
                if (sourceClassCd.equalsIgnoreCase(NEDSSConstant.CLASS_CD_OBS)) {
                    if (dataNotNull(item[2])) {
                        assocoiatedInvMap.put(item[0].toString() + "-" + item[1].toString(), item[2].toString());
                    }
                }
            }
        }

        return assocoiatedInvMap;
    }

    public ArrayList<ResultedTestSummaryContainer> getSusceptibilityResultedTestSummary(String typeCode, Long observationUid) {
        String theSelect = SELECT_LABSUSCEPTIBILITES_REFLEXTEST_SUMMARY_FORWORKUP_SQLSERVER;
        Query query = entityManager.createNativeQuery(theSelect);
        query.setParameter("TypeCode", typeCode);
        query.setParameter("TargetActUid",observationUid);

        ArrayList<ResultedTestSummaryContainer> lst = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
                int i = 0;
                container.setObservationUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                container.setCtrlCdUserDefined1(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setSourceActUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setLocalId(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setResultedTest(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setResultedTestCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCdSystemCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCodedResultValue(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setOrganismName(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setNumericResultCompare(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setHighRange(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setLowRange(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setNumericResultSeperator(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setNumericResultValue1(dataNotNull(item[++i]) ? BigDecimal.valueOf(Long.parseLong(item[i].toString())): null);
                container.setNumericResultValue2(dataNotNull(item[++i]) ? BigDecimal.valueOf(Long.parseLong(item[i].toString())): null);
                container.setNumericScale1(dataNotNull(item[++i]) ? Integer.valueOf(item[i].toString()): null);
                container.setNumericScale2(dataNotNull(item[++i]) ? Integer.valueOf(item[i].toString()): null);
                container.setNumericResultUnits(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);

                lst.add(container);
            }
        }
        return lst;
    }

    public ArrayList<UidSummaryContainer> getSusceptibilityUidSummary(ResultedTestSummaryContainer RVO, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm, String typeCode, Long observationUid)
    {
        String theSelect = GET_SOURCE_ACT_UID_FOR_SUSCEPTIBILITES_SQL;
        Query query = entityManager.createNativeQuery(theSelect);
        query.setParameter("TypeCode", typeCode);
        query.setParameter("TargetActUid",observationUid);
        ArrayList<UidSummaryContainer> lst = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                UidSummaryContainer uid = new UidSummaryContainer();
                uid.setUid(dataNotNull(item[0]) ? Long.valueOf(item[0].toString()): null);
                lst.add(uid);
            }
        }
        return lst;
    }

    public ArrayList<ResultedTestSummaryContainer> getTestAndSusceptibilities(String typeCode, Long observationUid, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm) {
        String theSelect = SELECT_LABRESULTED_REFLEXTEST_SUMMARY_FORWORKUP_SQL;
        Query query = entityManager.createNativeQuery(theSelect);
        query.setParameter("TypeCode", typeCode);
        query.setParameter("TargetActUid",observationUid);
        ArrayList<ResultedTestSummaryContainer> lst = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
                int i = 0;
                container.setObservationUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                container.setCtrlCdUserDefined1(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setSourceActUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setLocalId(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setResultedTest(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCdSystemCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setResultedTestStatusCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCodedResultValue(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setOrganismName(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setOrganismCodeSystemCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setHighRange(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setLowRange(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setNumericResultCompare(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setNumericResultSeperator(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setNumericResultValue1(dataNotNull(item[++i]) ? BigDecimal.valueOf(Long.parseLong(item[i].toString())): null);
                container.setNumericResultValue2(dataNotNull(item[++i]) ? BigDecimal.valueOf(Long.parseLong(item[i].toString())): null);
                container.setNumericScale1(dataNotNull(item[++i]) ? Integer.valueOf(item[i].toString()): null);
                container.setNumericScale2(dataNotNull(item[++i]) ? Integer.valueOf(item[i].toString()): null);
                container.setNumericResultUnits(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setTextResultValue(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                lst.add(container);
            }
        }
        return lst;
    }

    public ProviderDataForPrintContainer getOrderingPersonPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) {
        String theSelect = "select phone_nbr_txt \"phoneNbrTxt\", extension_txt \"extensionTxt\" from TELE_locator with (nolock) where TELE_locator_uid in ("
                +" select locator_uid from Entity_locator_participation with (nolock) where entity_uid= "+ organizationUid + " and cd='O' and class_cd='TELE')  ";

        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                providerDataForPrintVO.setProviderPhone(item[0].toString());
                providerDataForPrintVO.setProviderPhoneExtension(item[1].toString());
                break;
            }
        }
        return providerDataForPrintVO;
    }
    public ProviderDataForPrintContainer getOrderingPersonAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {
        String theSelect = "select street_addr1 \"streetAddr1\", city_desc_txt \"cityDescTxt\", state_cd \"stateCd\", zip_cd \"zipCd\" from Postal_locator with (nolock) where postal_locator_uid in ("
                + "select locator_uid from Entity_locator_participation with (nolock) where entity_uid in ("+organizationUid+")and cd='O' and class_cd='PST')";

        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                providerDataForPrintVO.setProviderStreetAddress1(item[0].toString());
                providerDataForPrintVO.setProviderCity(item[1].toString());
                providerDataForPrintVO.setProviderState(item[2].toString());
                providerDataForPrintVO.setProviderZip(item[3].toString());
                break;
            }
        }
        return providerDataForPrintVO;
    }
    public ProviderDataForPrintContainer getOrderingFacilityPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {
        String theSelect = "select phone_nbr_txt \"phoneNbrTxt\", extension_txt \"extensionTxt\" from TELE_locator with (nolock) where TELE_locator_uid in ("
                +" select locator_uid from Entity_locator_participation with (nolock) where entity_uid= "+ organizationUid +"  and cd='PH' and class_cd='TELE')  ";

        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                providerDataForPrintVO.setFacilityPhone(item[0].toString());
                providerDataForPrintVO.setFacilityPhoneExtension(item[1].toString());
                break;
            }
        }
        return providerDataForPrintVO;
    }
    public ProviderDataForPrintContainer getOrderingFacilityAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {

        String theSelect = "select street_addr1 \"streetAddr1\", street_addr2 \"streetAddr2\", city_desc_txt \"cityDescTxt\", "
                + "state_cd \"stateCd\", zip_cd \"zipCd\" from Postal_locator with (nolock) where postal_locator_uid in ("
                + "select locator_uid from Entity_locator_participation with (nolock) where entity_uid in ("+ organizationUid+ ")"
                +  "and cd='O' and class_cd='PST')";
        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                providerDataForPrintVO.setFacilityAddress1(item[0].toString());
                providerDataForPrintVO.setFacilityAddress2(item[1].toString());
                providerDataForPrintVO.setFacilityCity(item[2].toString());
                providerDataForPrintVO.setFacilityState(item[3].toString());
                providerDataForPrintVO.setFacilityZip(item[4].toString());
                break;
            }
        }
        return providerDataForPrintVO;
    }

    public String getSpecimanSource(Long materialUid) {
        String vals = null;
        String theSelect = "SELECT cd \"specimenSource\" "
                + "FROM material with (nolock) " + " WHERE material_uid = " + materialUid;
        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                vals = (item[0].toString());
            }
        }
        return vals;
    }

    public  String getReportingFacilityName(Long organizationUid) {

        String vals = null;
        String theSelect = "SELECT organization_name.nm_txt \"nmTxt\" "
                + "FROM organization_name with (nolock) " + " WHERE organization_uid = " + organizationUid;
        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                vals = (item[0].toString());
            }
        }
        return vals;
    }

    public ArrayList<Object>  getActIdDetails(Long observationUID) {
        ArrayList<Object> vals= new ArrayList<Object> ();
        String theSelect = "SELECT Act_id.root_extension_txt \"rootExtTxt\" " +
                "FROM Act_id with (nolock) " +
                "WHERE Act_id.Act_uid = " + observationUID;

        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                vals.add(item[0].toString());
            }
        }
        return vals;
    }
    public  ArrayList<Object>  getProviderInfo(Long observationUID,String partTypeCd) {
        ArrayList<Object> vals = new ArrayList<Object> ();
        //per Pete Varnell - use optimizer hint on select
        String ORD_PROVIDER_MSQL = "SELECT ";
        String ORD_PROVIDER = "person_name.person_uid \"providerUid\",person_name.last_nm \"lastNm\", person_name.nm_degree \"degree\", " +
                "person_name.first_nm \"firstNm\" , person_name.nm_prefix \"prefix\" ,  person_name.nm_suffix \"suffix\" " +
                "FROM person_name with (nolock) , observation with (nolock), " +
                "participation with (nolock) WHERE person_name.person_uid = participation.subject_entity_uid " +
                "AND participation.act_uid = observation.observation_uid " +
                "AND participation.type_cd = '" + partTypeCd + "'" + " AND participation.subject_class_cd='PSN' " +
                "AND observation.observation_uid = "  + observationUID;

        var theSelect = ORD_PROVIDER_MSQL + ORD_PROVIDER;
        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                vals.add(item[0].toString());
                vals.add(item[1].toString());
                vals.add(item[2].toString());
                vals.add(item[3].toString());
                vals.add(item[4].toString());
                vals.add(Long.valueOf(item[5].toString()));
            }
        }

        return vals;
    }

    public ArrayList<Object> getPatientPersonInfo(Long observationUID) {
        ArrayList<Object> vals= new ArrayList<Object> ();
        String theSelect = "SELECT person_name.last_nm \"lastNm\", " +
                "person_name.first_nm \"firstNm\", observation.ctrl_cd_display_form \"ctrlCdDisplayForm\", " +
                "person.person_parent_uid \"personParentUid\" " +
                "FROM person with (nolock) , person_name with (nolock) , observation with (nolock) , participation with (nolock) " +
                "WHERE person.person_uid = participation.subject_entity_uid " +
                "AND participation.act_uid = observation.observation_uid " +
                "AND participation.type_cd = \'PATSBJ\' " +
                "AND person.person_uid = person_name.person_uid " +
                "AND person_name.nm_use_cd = \'L\' " +
                "AND observation.observation_uid = "  + observationUID;
        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                vals.add(item[0].toString());
                vals.add(item[1].toString());
                vals.add(item[2].toString());
                vals.add(Long.valueOf(item[3].toString()));
            }
        }
        return vals;
    }

    public  Map<Object,Object> getLabParticipations(Long observationUID) {
        String QUICK_FIND_PATIENT_MSQL = "SELECT ";
        String QUICK_FIND_PATIENT =  "participation.subject_class_cd \"classCd\", "
                + "participation.type_cd \"typeCd\", "
                + "participation.subject_entity_uid \"subjectEntityUid\" "
                + "from observation with (nolock), participation with (nolock)"
                + "WHERE participation.act_uid = observation.observation_uid "
                + "AND participation.type_cd in(\'"
                + NEDSSConstant.PAR111_TYP_CD + "\',\'"
                + NEDSSConstant.PAR104_TYP_CD + "\',\'"
                + NEDSSConstant.PAR110_TYP_CD + "\',\'"
                + NEDSSConstant.PAR101_TYP_CD + "\')"
                + "AND observation.observation_uid = " + observationUID.toString();
        String theSelect=  QUICK_FIND_PATIENT_MSQL+QUICK_FIND_PATIENT;

        Map<Object,Object> vals = new HashMap<Object,Object>();
        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                String classCd =  item[0].toString();
                String typeCd = item[1].toString();
                Long subjectEntityUid = Long.valueOf(item[2].toString());
                if(classCd.equalsIgnoreCase(NEDSSConstant.CLASS_CD_PSN) && typeCd.equalsIgnoreCase( NEDSSConstant.PAR101_TYP_CD))
                {
                    continue;
                }
                vals.put(typeCd, subjectEntityUid);
            }
        }

        return vals;
    }

    public Collection<CTContactSummaryDto> getContactByPatientInfo(String queryString) {
        Query query = entityManager.createNativeQuery(queryString);

        Collection<CTContactSummaryDto> ctContactSummaryDtoCollection = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                CTContactSummaryDto contact = new CTContactSummaryDto();
                contact.setNamedOnDate(dataNotNull(item[0]) ? Timestamp.valueOf(item[0].toString()) : null );
                contact.setCtContactUid(dataNotNull(item[1]) ? Long.valueOf(item[1].toString()) : null );
                contact.setLocalId(dataNotNull(item[2]) ? String.valueOf(item[2].toString()) : null );
                contact.setSubjectEntityUid(dataNotNull(item[3]) ? Long.valueOf(item[3].toString()) : null );
                contact.setContactEntityUid(dataNotNull(item[4]) ? Long.valueOf(item[4].toString()) : null );
                contact.setPriorityCd(dataNotNull(item[5]) ? String.valueOf(item[5].toString()) : null );
                contact.setDispositionCd(dataNotNull(item[6]) ? String.valueOf(item[6].toString()) : null );
                contact.setProgAreaCd(dataNotNull(item[7]) ? String.valueOf(item[7].toString()) : null );
                contact.setNamedDuringInterviewUid(dataNotNull(item[8]) ? Long.valueOf(item[8].toString()) : null );
                contact.setContactReferralBasisCd(dataNotNull(item[9]) ? String.valueOf(item[9].toString()) : null );
                contact.setThirdPartyEntityUid(dataNotNull(item[10]) ? Long.valueOf(item[10].toString()) : null );
                contact.setThirdPartyEntityPhcUid(dataNotNull(item[11]) ? Long.valueOf(item[11].toString()) : null );
                contact.setContactProcessingDecision(dataNotNull(item[12]) ? String.valueOf(item[12].toString()) : null );
                contact.setSourceDispositionCd(dataNotNull(item[13]) ? String.valueOf(item[13].toString()) : null );
                contact.setSourceConditionCd(dataNotNull(item[14]) ? String.valueOf(item[14].toString()) : null );
                contact.setSourceCurrentSexCd(dataNotNull(item[15]) ? String.valueOf(item[15].toString()) : null );
                contact.setSourceInterviewStatusCd(dataNotNull(item[15]) ? String.valueOf(item[15].toString()) : null );
                contact.setSubjectEntityPhcUid(dataNotNull(item[16]) ? Long.valueOf(item[16].toString()) : null );
                contact.setInterviewDate(dataNotNull(item[17]) ? Timestamp.valueOf(item[17].toString()) : null );
                contact.setCreateDate(dataNotNull(item[18]) ? Timestamp.valueOf(item[18].toString()) : null );
                contact.setSubjectPhcLocalId(dataNotNull(item[19]) ? String.valueOf(item[19].toString()) : null );
                contact.setContactMprUid(dataNotNull(item[20]) ? Long.valueOf(item[20].toString()) : null );
                contact.setSubjectPhcCd(dataNotNull(item[21]) ? String.valueOf(item[21].toString()) : null );
                contact.setSubjectMprUid(dataNotNull(item[22]) ? Long.valueOf(item[22].toString()) : null );
                ctContactSummaryDtoCollection.add(contact);
            }
        }
        return ctContactSummaryDtoCollection;
    }

    public NbsDocumentContainer getNbsDocument(Long nbsUid) throws DataProcessingException {
        Query query = entityManager.createNativeQuery(GET_NBS_DOCUMENT);

        NBSDocumentDto container = new NBSDocumentDto();
        NbsDocumentContainer nbsDocumentVO = new NbsDocumentContainer();
        query.setParameter("NbsUid", nbsUid);

        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                int i = 0;
                container.setNbsDocumentUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                container.setLocalId(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setDocTypeCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setJurisdictionCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setProgAreaCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setDocStatusCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setAddTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setTxt(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setVersionCtrlNbr(dataNotNull(item[++i]) ? Integer.valueOf(item[i].toString()): null);
                container.setDocPurposeCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setCdDescTxt(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setSendingFacilityNm(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setAddUserId(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setRecordStatusCd(dataNotNull(item[++i]) ? String.valueOf(Long.parseLong(item[i].toString())): null);
                container.setProcessingDecisionCd(dataNotNull(item[++i]) ? String.valueOf(Long.parseLong(item[i].toString())): null);
                container.setProcessingDecisiontxt(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setExternalVersionCtrlNbr(dataNotNull(item[++i]) ? Integer.valueOf(item[i].toString()): null);
                container.setCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);


                container.setPayLoadTxt(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setPhdcDocDerivedTxt(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setPayloadViewIndCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);
                container.setNbsDocumentMetadataUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setRecordStatusTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);
                container.setProgramJurisdictionOid(dataNotNull(item[++i]) ? Long.valueOf(Long.parseLong(item[i].toString())): null);
                container.setSharedInd(dataNotNull(item[++i]) ? String.valueOf(Long.parseLong(item[i].toString())): null);
                container.setLastChgUserId(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setNbsInterfaceUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                container.setDocEventTypeCd(dataNotNull(item[++i]) ? String.valueOf(item[i].toString()): null);

                container.setEffectiveTime(dataNotNull(item[++i]) ? Timestamp.valueOf(item[i].toString()): null);


                PersonContainer personVO = new PersonContainer();
                PersonDto personDT = new PersonDto();
                personDT.setPersonUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                personDT.setPersonParentUid(dataNotNull(item[++i]) ? Long.valueOf(item[i].toString()): null);
                personVO.setThePersonDto(personDT);
                nbsDocumentVO.setPatientVO(personVO);
                nbsDocumentVO.setNbsDocumentDT(container);
                nbsDocumentVO.setEDXEventProcessDTMap(publicHealthCaseStoredProcRepository.getEDXEventProcessMap(container.getNbsDocumentUid()));
            }
        }
        return nbsDocumentVO;
    }


    public ArrayList<Object> getInvListForCoInfectionId(Long mprUid,String coInfectionId) throws DataProcessingException {
        ArrayList<Object> coinfectionInvList = new ArrayList<>();

        Query query = entityManager.createNativeQuery(COINFECTION_INV_LIST_FOR_GIVEN_COINFECTION_ID_SQL);

        query.setParameter("CoInfect", coInfectionId);
        query.setParameter("PersonUid", mprUid);


        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var item : results) {
                int i = 0;
                CoinfectionSummaryContainer container = new CoinfectionSummaryContainer();
                container.setPublicHealthCaseUid(dataNotNull(item[i]) ? Long.valueOf(item[i].toString()): null);
                container.setConditionCd(dataNotNull(item[i++]) ? String.valueOf(item[i].toString()): null);
                coinfectionInvList.add(container);
            }
        }
        return coinfectionInvList;
    }



    private boolean dataNotNull(Object string) {
        return string != null;
    }

}
