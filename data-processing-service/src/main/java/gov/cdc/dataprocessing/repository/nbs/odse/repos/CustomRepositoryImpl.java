package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.CTContactSummaryDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PublicHealthCaseStoredProcRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static gov.cdc.dataprocessing.constant.ComplexQueries.*;
import static gov.cdc.dataprocessing.utilities.DataParserForSql.*;

@Repository
public class CustomRepositoryImpl implements CustomRepository {
    @PersistenceContext(unitName = "odse")
    protected EntityManager entityManager;
    private static final String TARGET_ACT_UID = "TargetActUid";
    private static final String SOURCE_CLASS_CODE = "SourceClassCd";
    private static final String TARGET_CLASS_CODE = "TargetClassCd";
    private static final String PHC_UID = "PhcUid";
    private static final String TYPE_CODE = "TypeCode";

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
        if (resultValidCheck(results)) {
            for(var item : results) {
                StateDefinedFieldDataDto container = new StateDefinedFieldDataDto();
                int i = 0;
                container.setLdfUid(parseValue(item[i], Long.class));
                container.setBusinessObjNm(parseValue(item[++i], String.class));
                container.setAddTime(parseValue(item[++i], Timestamp.class));
                container.setBusinessObjUid(parseValue(item[++i], Long.class));
                container.setLastChgTime(parseValue(item[++i], Timestamp.class));
                container.setLdfValue(parseValue(item[++i], String.class));
                container.setVersionCtrlNbr(parseValue(item[++i], Integer.class));
                lst.add(container);
            }
        }
        return lst;
    }

    public Map<Object, Object> getAssociatedDocumentList(Long uid, String targetClassCd, String sourceClassCd, String theQuery) {
        Map<Object, Object> map= new HashMap<> ();
        Query query = entityManager.createNativeQuery(theQuery);
        query.setParameter(TARGET_ACT_UID, uid);
        query.setParameter(SOURCE_CLASS_CODE, sourceClassCd);
        query.setParameter(TARGET_CLASS_CODE, targetClassCd);
        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
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
        query.setParameter(TARGET_ACT_UID, publicHealthCaseUid);
        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                EDXEventProcessDto container = new EDXEventProcessDto();
                int i = 0;
                container.setEDXEventProcessUid(parseValue(item[i], Long.class));
                container.setNbsDocumentUid(parseValue(item[++i], Long.class));
                container.setNbsEventUid(parseValue(item[++i], Long.class));

                long sourceEventId = Long.parseLong(item[++i].toString());
                container.setSourceEventId(parseValue(item[i], String.class));

                container.setDocEventTypeCd(parseValue(item[++i], String.class));
                container.setAddUserId(parseValue(item[++i], Long.class));
                container.setAddTime(parseValue(item[++i], Timestamp.class));
                container.setParsedInd(parseValue(item[++i], String.class));

                map.put(Long.toString(sourceEventId), container);
            }
        }
        return map;
    }


    public Map<Object, Object> retrieveDocumentSummaryVOForInv(Long publicHealthUID) {
        Map<Object,Object> map= new HashMap<Object,Object> ();
        Query query = entityManager.createNativeQuery(DOCUMENT_FOR_A_PHC);
        query.setParameter(PHC_UID, publicHealthUID);
        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                DocumentSummaryContainer container = new DocumentSummaryContainer();
                int i = 0;
                Long getNbsDocumentUid = parseValue(item[++i], Long.class);
                container.setNbsDocumentUid(getNbsDocumentUid);

                container.setDocType(parseValue(item[++i], String.class));
                container.setCdDescTxt(parseValue(item[++i], String.class));
                container.setAddTime(parseValue(item[++i], Timestamp.class));
                container.setLocalId(parseValue(item[++i], String.class));
                container.setCdDescTxt(parseValue(item[++i], String.class));

                map.put(getNbsDocumentUid, container);
            }
        }
        return map;
    }

    public List<NotificationSummaryContainer> retrieveNotificationSummaryListForInvestigation(Long publicHealthUID, String theQuery) {
        List<NotificationSummaryContainer> map= new ArrayList<> ();
        Query query = entityManager.createNativeQuery(theQuery);
        query.setParameter(PHC_UID, publicHealthUID);
        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                NotificationSummaryContainer container = new NotificationSummaryContainer();
                int i = 0;
                container.setNotificationUid(parseValue(item[i], Long.class));
                container.setCdNotif(parseValue(item[++i], String.class));
                container.setAddTime(parseValue(item[++i], Timestamp.class));
                container.setRptSentTime(parseValue(item[++i], Timestamp.class));
                container.setRecordStatusTime(parseValue(item[++i], Timestamp.class));
                container.setCd(parseValue(item[++i], String.class));
                container.setJurisdictionCd(parseValue(item[++i], String.class));
                container.setProgramJurisdictionOid(parseValue(item[++i], Long.class));
                container.setCaseClassCd(parseValue(item[++i], String.class));
                container.setAutoResendInd(parseValue(item[++i], String.class));
                container.setCaseClassCd(parseValue(item[++i], String.class));
                container.setLocalId(parseValue(item[++i], String.class));
                container.setTxt(parseValue(item[++i], String.class));
                container.setRecordStatusCd(parseValue(item[++i], String.class));
                container.setIsHistory(parseValue(item[++i], String.class));
                container.setNndInd(parseValue(item[++i], String.class));
                container.setRecipient(parseValue(item[++i], String.class));
                map.add(container);
            }
        }
        return map;
    }

    public Map<Object, Object> retrieveTreatmentSummaryVOForInv(Long publicHealthUID, String theQuery) {
        Map<Object,Object> map= new HashMap<Object,Object> ();
        Query query = entityManager.createNativeQuery(theQuery);
        query.setParameter(PHC_UID, publicHealthUID);
        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                TreatmentContainer container = new TreatmentContainer();
                int i = 0;
                container.setPhcUid(parseValue(item[i], Long.class));
                Long treatmentId = parseValue(item[++i], Long.class);
                container.setTreatmentUid(treatmentId);
                container.setTreatmentNameCode(parseValue(item[++i], String.class));
                container.setCustomTreatmentNameCode(parseValue(item[++i], String.class));
                container.setActivityFromTime(parseValue(item[++i], Timestamp.class));
                container.setLocalId(parseValue(item[++i], String.class));
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
        if (resultValidCheck(results)) {
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
        query.setParameter(TYPE_CODE, typeCode);
        query.setParameter(TARGET_ACT_UID,observationUid);

        ArrayList<ResultedTestSummaryContainer> lst = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
                int i = 0;
                container.setObservationUid(parseValue(item[i], Long.class));
                container.setCtrlCdUserDefined1(parseValue(item[++i], String.class));
                container.setSourceActUid(parseValue(item[++i], Long.class));
                container.setLocalId(parseValue(item[++i], String.class));
                container.setResultedTest(parseValue(item[++i], String.class));
                container.setResultedTestCd(parseValue(item[++i], String.class));
                container.setCdSystemCd(parseValue(item[++i], String.class));
                container.setCodedResultValue(parseValue(item[++i], String.class));
                container.setOrganismName(parseValue(item[++i], String.class));
                container.setNumericResultCompare(parseValue(item[++i], String.class));
                container.setHighRange(parseValue(item[++i], String.class));
                container.setLowRange(parseValue(item[++i], String.class));
                container.setNumericResultSeperator(parseValue(item[++i], String.class));
                container.setNumericResultValue1(parseValue(item[++i], BigDecimal.class));
                container.setNumericResultValue2(parseValue(item[++i], BigDecimal.class));
                container.setNumericScale1(parseValue(item[++i], Integer.class));
                container.setNumericScale2(parseValue(item[++i], Integer.class));
                container.setNumericResultUnits(parseValue(item[++i], String.class));
                lst.add(container);
            }
        }
        return lst;
    }

    public ArrayList<UidSummaryContainer> getSusceptibilityUidSummary(ResultedTestSummaryContainer RVO, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm, String typeCode, Long observationUid)
    {
        String theSelect = GET_SOURCE_ACT_UID_FOR_SUSCEPTIBILITES_SQL;
        Query query = entityManager.createNativeQuery(theSelect);
        query.setParameter(TYPE_CODE, typeCode);
        query.setParameter(TARGET_ACT_UID,observationUid);
        ArrayList<UidSummaryContainer> lst = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                UidSummaryContainer uid = new UidSummaryContainer();
                uid.setUid(parseValue(item[0], Long.class));
                lst.add(uid);
            }
        }
        return lst;
    }

    public ArrayList<ResultedTestSummaryContainer> getTestAndSusceptibilities(String typeCode, Long observationUid, LabReportSummaryContainer labRepEvent, LabReportSummaryContainer labRepSumm) {
        String theSelect = SELECT_LABRESULTED_REFLEXTEST_SUMMARY_FORWORKUP_SQL;
        Query query = entityManager.createNativeQuery(theSelect);
        query.setParameter(TYPE_CODE, typeCode);
        query.setParameter(TARGET_ACT_UID,observationUid);
        ArrayList<ResultedTestSummaryContainer> lst = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
                int i = 0;
                container.setObservationUid(parseValue(item[i], Long.class));
                container.setCtrlCdUserDefined1(parseValue(item[++i], String.class));
                container.setSourceActUid(parseValue(item[++i], Long.class));
                container.setLocalId(parseValue(item[++i], String.class));
                container.setResultedTest(parseValue(item[++i], String.class));
                container.setCdSystemCd(parseValue(item[++i], String.class));
                container.setResultedTestStatusCd(parseValue(item[++i], String.class));
                container.setCodedResultValue(parseValue(item[++i], String.class));
                container.setOrganismName(parseValue(item[++i], String.class));
                container.setOrganismCodeSystemCd(parseValue(item[++i], String.class));
                container.setHighRange(parseValue(item[++i], String.class));
                container.setLowRange(parseValue(item[++i], String.class));
                container.setNumericResultCompare(parseValue(item[++i], String.class));
                container.setNumericResultSeperator(parseValue(item[++i], String.class));
                container.setNumericResultValue1(parseValue(item[++i], BigDecimal.class));
                container.setNumericResultValue2(parseValue(item[++i], BigDecimal.class));
                container.setNumericScale1(parseValue(item[++i], Integer.class));
                container.setNumericScale2(parseValue(item[++i], Integer.class));
                container.setNumericResultUnits(parseValue(item[++i], String.class));
                container.setTextResultValue(parseValue(item[++i], String.class));
                lst.add(container);
            }
        }
        return lst;
    }

    public ProviderDataForPrintContainer getOrderingPersonPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid) {
        String theSelect = "select phone_nbr_txt \"phoneNbrTxt\", extension_txt \"extensionTxt\" from TELE_locator with (nolock) where TELE_locator_uid in ("
                +" select locator_uid from Entity_locator_participation with (nolock) where entity_uid= "+ organizationUid + " and cd='O' and class_cd='TELE')  ";

        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.setMaxResults(1).getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                providerDataForPrintVO.setProviderPhone(item[0].toString());
                providerDataForPrintVO.setProviderPhoneExtension(item[1].toString());
            }
        }
        return providerDataForPrintVO;
    }
    public ProviderDataForPrintContainer getOrderingPersonAddress(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {
        String theSelect = "select street_addr1 \"streetAddr1\", city_desc_txt \"cityDescTxt\", state_cd \"stateCd\", zip_cd \"zipCd\" from Postal_locator with (nolock) where postal_locator_uid in ("
                + "select locator_uid from Entity_locator_participation with (nolock) where entity_uid in ("+organizationUid+")and cd='O' and class_cd='PST')";

        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.setMaxResults(1).getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                providerDataForPrintVO.setProviderStreetAddress1(item[0].toString());
                providerDataForPrintVO.setProviderCity(item[1].toString());
                providerDataForPrintVO.setProviderState(item[2].toString());
                providerDataForPrintVO.setProviderZip(item[3].toString());
            }
        }
        return providerDataForPrintVO;
    }
    public ProviderDataForPrintContainer getOrderingFacilityPhone(ProviderDataForPrintContainer providerDataForPrintVO, Long organizationUid)
    {
        String theSelect = "select phone_nbr_txt \"phoneNbrTxt\", extension_txt \"extensionTxt\" from TELE_locator with (nolock) where TELE_locator_uid in ("
                +" select locator_uid from Entity_locator_participation with (nolock) where entity_uid= "+ organizationUid +"  and cd='PH' and class_cd='TELE')  ";

        Query query = entityManager.createNativeQuery(theSelect);
        List<Object[]> results = query.setMaxResults(1).getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                providerDataForPrintVO.setFacilityPhone(item[0].toString());
                providerDataForPrintVO.setFacilityPhoneExtension(item[1].toString());
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
        List<Object[]> results = query.setMaxResults(1).getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                providerDataForPrintVO.setFacilityAddress1(item[0].toString());
                providerDataForPrintVO.setFacilityAddress2(item[1].toString());
                providerDataForPrintVO.setFacilityCity(item[2].toString());
                providerDataForPrintVO.setFacilityState(item[3].toString());
                providerDataForPrintVO.setFacilityZip(item[4].toString());
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
        if (resultValidCheck(results)) {
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
        if (resultValidCheck(results)) {
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
        if (resultValidCheck(results)) {
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
        if (resultValidCheck(results)) {
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
        if (resultValidCheck(results)) {
            for(var item : results) {
                vals.add(item[0].toString());
                vals.add(item[1].toString());
                vals.add(item[2].toString());
                vals.add(Long.valueOf(item[3].toString()));
            }
        }
        return vals;
    }

    @SuppressWarnings("java:S1192")
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
        if (resultValidCheck(results)) {
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
        if (resultValidCheck(results)) {
            for(var item : results) {
                CTContactSummaryDto contact = new CTContactSummaryDto();
                int i = 0;
                contact.setNamedOnDate(parseValue(item[i], Timestamp.class));
                contact.setCtContactUid(parseValue(item[++i], Long.class));
                contact.setLocalId(parseValue(item[++i], String.class));
                contact.setSubjectEntityUid(parseValue(item[++i], Long.class));
                contact.setContactEntityUid(parseValue(item[++i], Long.class));
                contact.setPriorityCd(parseValue(item[++i], String.class));
                contact.setDispositionCd(parseValue(item[++i], String.class));
                contact.setProgAreaCd(parseValue(item[++i], String.class));
                contact.setNamedDuringInterviewUid(parseValue(item[++i], Long.class));
                contact.setContactReferralBasisCd(parseValue(item[++i], String.class));
                contact.setThirdPartyEntityUid(parseValue(item[++i], Long.class));
                contact.setThirdPartyEntityPhcUid(parseValue(item[++i], Long.class));
                contact.setContactProcessingDecision(parseValue(item[++i], String.class));
                contact.setSourceDispositionCd(parseValue(item[++i], String.class));
                contact.setSourceConditionCd(parseValue(item[++i], String.class));
                contact.setSourceCurrentSexCd(parseValue(item[++i], String.class));
                contact.setSourceInterviewStatusCd(parseValue(item[++i], String.class));
                contact.setSubjectEntityPhcUid(parseValue(item[++i], Long.class));
                contact.setInterviewDate(parseValue(item[++i], Timestamp.class));
                contact.setCreateDate(parseValue(item[++i], Timestamp.class));
                contact.setSubjectPhcLocalId(parseValue(item[++i], String.class));
                contact.setContactMprUid(parseValue(item[++i], Long.class));
                contact.setSubjectPhcCd(parseValue(item[++i], String.class));
                contact.setSubjectMprUid(parseValue(item[++i], Long.class));
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
        if (resultValidCheck(results)) {
            for(var item : results) {
                int i = 0;
                container.setNbsDocumentUid(parseValue(item[i], Long.class));
                container.setLocalId(parseValue(item[++i], String.class));
                container.setDocTypeCd(parseValue(item[++i], String.class));
                container.setJurisdictionCd(parseValue(item[++i], String.class));
                container.setProgAreaCd(parseValue(item[++i], String.class));
                container.setDocStatusCd(parseValue(item[++i], String.class));
                container.setAddTime(parseValue(item[++i], Timestamp.class));
                container.setTxt(parseValue(item[++i], String.class));
                container.setVersionCtrlNbr(parseValue(item[++i], Integer.class));
                container.setDocPurposeCd(parseValue(item[++i], String.class));
                container.setCdDescTxt(parseValue(item[++i], String.class));
                container.setSendingFacilityNm(parseValue(item[++i], String.class));
                container.setAddUserId(parseValue(item[++i], Long.class));
                container.setRecordStatusCd(parseValue(item[++i], String.class));
                container.setProcessingDecisionCd(parseValue(item[++i], String.class));
                container.setProcessingDecisiontxt(parseValue(item[++i], String.class));
                container.setExternalVersionCtrlNbr(parseValue(item[++i], Integer.class));
                container.setCd(parseValue(item[++i], String.class));

                container.setPayLoadTxt(parseValue(item[++i], String.class));
                container.setPhdcDocDerivedTxt(parseValue(item[++i], String.class));
                container.setPayloadViewIndCd(parseValue(item[++i], String.class));
                container.setNbsDocumentMetadataUid(parseValue(item[++i], Long.class));
                container.setRecordStatusTime(parseValue(item[++i], Timestamp.class));
                container.setProgramJurisdictionOid(parseValue(item[++i], Long.class));
                container.setSharedInd(parseValue(item[++i], String.class));
                container.setLastChgUserId(parseValue(item[++i], Long.class));
                container.setNbsInterfaceUid(parseValue(item[++i], Long.class));
                container.setDocEventTypeCd(parseValue(item[++i], String.class));
                container.setEffectiveTime(parseValue(item[++i], Timestamp.class));

                PersonContainer personVO = new PersonContainer();
                PersonDto personDT = new PersonDto();
                personDT.setPersonUid(parseValue(item[++i], Long.class));
                personDT.setPersonParentUid(parseValue(item[++i], Long.class));
                personVO.setThePersonDto(personDT);
                nbsDocumentVO.setPatientVO(personVO);
                nbsDocumentVO.setNbsDocumentDT(container);
                nbsDocumentVO.setEDXEventProcessDTMap(publicHealthCaseStoredProcRepository.getEDXEventProcessMap(container.getNbsDocumentUid()));
            }
        }
        return nbsDocumentVO;
    }


    public ArrayList<Object> getInvListForCoInfectionId(Long mprUid,String coInfectionId) {
        ArrayList<Object> coinfectionInvList = new ArrayList<>();

        Query query = entityManager.createNativeQuery(COINFECTION_INV_LIST_FOR_GIVEN_COINFECTION_ID_SQL);

        query.setParameter("CoInfect", coInfectionId);
        query.setParameter("PersonUid", mprUid);


        List<Object[]> results = query.getResultList();
        if (resultValidCheck(results)) {
            for(var item : results) {
                int i = 0;
                CoinfectionSummaryContainer container = new CoinfectionSummaryContainer();
                container.setPublicHealthCaseUid(parseValue(item[i], Long.class));
                container.setConditionCd(parseValue(item[++i], String.class));
                coinfectionInvList.add(container);
            }
        }
        return coinfectionInvList;
    }





}
