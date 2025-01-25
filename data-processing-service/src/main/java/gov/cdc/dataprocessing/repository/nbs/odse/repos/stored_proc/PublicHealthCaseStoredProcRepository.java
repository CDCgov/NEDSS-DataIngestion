package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class PublicHealthCaseStoredProcRepository {

    @PersistenceContext(unitName = "odseEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    public Collection<PublicHealthCaseDto> associatedPublicHealthCaseForMprForCondCd(Long mprUid, String conditionCode) throws DataProcessingException {
        Collection<PublicHealthCaseDto> models = new ArrayList<>();
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("ASSO_PHC_FOR_MPR_COND_SP");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("conditionCd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("mprUID", Long.class, ParameterMode.IN);


            // Set the parameter values
            storedProcedure.setParameter("conditionCd", conditionCode);
            storedProcedure.setParameter("mprUID", mprUid);

            // Execute the stored procedure
            storedProcedure.execute();
            List<Object[]> results = storedProcedure.getResultList();
            for (Object[] result : results) {
                PublicHealthCaseDto model = new PublicHealthCaseDto();
                model.setPublicHealthCaseUid((Long) result[0]);
                model.setActivityDurationAmt((String) result[1]);
                model.setActivityDurationUnitCd((String) result[2]);
                model.setActivityFromTime((Timestamp) result[3]);
                model.setActivityToTime((Timestamp) result[4]);
                model.setAddReasonCd((String)  result[5]);
                model.setAddTime((Timestamp) result[6]);
                model.setAddUserId((Long)  result[7]);
                model.setCaseClassCd((String)  result[8]);
                model.setCd((String)  result[9]);
                model.setCdDescTxt((String)  result[10]);
                model.setCdSystemCd((String)  result[11]);
                model.setCdSystemDescTxt((String)  result[12]);
                model.setConfidentialityCd((String)  result[13]);
                model.setConfidentialityDescTxt((String)  result[14]);
                model.setDetectionMethodCd((String)  result[15]);
                model.setDetectionMethodDescTxt((String) result[16]);
                model.setDiseaseImportedCd((String) result[17]);
                model.setDiseaseImportedDescTxt((String) result[18]);
                model.setEffectiveDurationAmt((String) result[19]);
                model.setEffectiveDurationUnitCd((String) result[20]);
                model.setEffectiveFromTime((Timestamp) result[21]);
                model.setEffectiveToTime((Timestamp) result[22]);
                model.setGroupCaseCnt(((Short) result[23]).intValue());
                model.setInvestigationStatusCd((String) result[24]);
                model.setJurisdictionCd((String) result[25]);
                model.setLastChgReasonCd((String) result[26]);
                model.setLastChgTime((Timestamp) result[27]);
                model.setLastChgUserId((Long) result[28]);
                model.setLocalId((String) result[29]);
                model.setMmwrWeek((String) result[30]);
                model.setMmwrYear((String) result[31]);
                model.setOutbreakName((String) result[32]);
                model.setOutbreakFromTime((Timestamp) result[33]);
                model.setOutbreakInd((String) result[34]);
                model.setOutbreakToTime((Timestamp) result[35]);
                model.setOutcomeCd((String) result[36]);
                model.setPatientGroupId((Long) result[37]);
                model.setProgAreaCd((String) result[38]);
                model.setRecordStatusCd((String) result[39]);
                model.setRecordStatusTime((Timestamp) result[40]);
                model.setRepeatNbr((Integer) result[41]);
                model.setRptCntyCd((String) result[42]);
                model.setStatusCd( ((Character) result[43]).toString());
                model.setStatusTime((Timestamp) result[44]);
                model.setTransmissionModeCd((String) result[45]);
                model.setTransmissionModeDescTxt((String) result[46]);
                model.setTxt((String) result[47]);
                model.setUserAffiliationTxt((String) result[48]);
                model.setPatAgeAtOnset((String) result[49]);
                model.setPatAgeAtOnsetUnitCd((String) result[50]);
                model.setRptFormCmpltTime((Timestamp) result[51]);
                model.setRptSourceCd((String) result[52]);
                model.setRptSourceCdDescTxt((String) result[53]);
                model.setRptToCountyTime((Timestamp) result[54]);
                model.setRptToStateTime((Timestamp) result[55]);
                model.setDiagnosisTime((Timestamp) result[56]);
                model.setProgramJurisdictionOid((Long) result[57]);
                model.setSharedInd(((Character) result[58]).toString());
                model.setVersionCtrlNbr(((Short) result[59]).intValue());
                model.setCaseTypeCd(((Character) result[60]).toString());
                model.setInvestigatorAssignedTime((Timestamp) result[61]);
                model.setHospitalizedIndCd((String) result[62]);
                model.setHospitalizedAdminTime((Timestamp) result[63]);
                model.setHospitalizedDischargeTime((Timestamp) result[64]);
                model.setHospitalizedDurationAmt((BigDecimal) result[65]);
                model.setPregnantIndCd((String) result[66]);
                model.setDayCareIndCd((String) result[67]);
                model.setFoodHandlerIndCd((String) result[68]);
                model.setImportedCountryCd((String) result[69]);
                model.setImportedStateCd((String) result[70]);
                model.setImportedCityDescTxt((String) result[71]);
                model.setImportedCountyCd((String) result[72]);
                model.setDeceasedTime((Timestamp) result[73]);
                model.setCountIntervalCd((String) result[74]);
                model.setPriorityCd((String) result[75]);
                model.setInfectiousFromDate((Timestamp) result[76]);
                model.setInfectiousToDate((Timestamp) result[77]);
                model.setContactInvStatus((String) result[78]);
                model.setContactInvTxt((String) result[79]);
                model.setReferralBasisCd((String) result[80]);
                model.setCurrProcessStateCd((String) result[81]);
                model.setInvPriorityCd((String) result[82]);
                model.setCoinfectionId((String) result[83]);
                model.setAssociatedSpecimenCollDate((Timestamp) result[84]);
                model.setConfirmationMethodCd((String) result[85]);
                model.setConfirmationMethodTime((Timestamp) result[86]);

                models.add(model);
            }
            return models;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }


    public   Map<String, EDXEventProcessDto> getEDXEventProcessMap(Long nbsDocumentUid) throws DataProcessingException {
        Map<String, EDXEventProcessDto> eventProcessMap = new HashMap<>();
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("GETEDXEVENTPROCESSBYDOCID_SP");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("nbsDocumentUid", Long.class, ParameterMode.IN);


            // Set the parameter values

            // Execute the stored procedure
            storedProcedure.execute();
            List<Object[]> results = storedProcedure.getResultList();
            for (Object[] rs : results) {
                EDXEventProcessDto edxEventProcessDto = new EDXEventProcessDto();
                edxEventProcessDto.setEDXEventProcessUid((Long) rs[1]);
                edxEventProcessDto.setNbsDocumentUid((Long) rs[2]);
                edxEventProcessDto.setNbsEventUid((Long) rs[3]);
                edxEventProcessDto.setSourceEventId((String) rs[4]);
                edxEventProcessDto.setDocEventTypeCd((String) rs[5]);
                edxEventProcessDto.setAddUserId((Long) rs[6]);
                edxEventProcessDto.setAddTime((Timestamp) rs[7]);
                edxEventProcessDto.setParsedInd((String) rs[8]);
                eventProcessMap.put(edxEventProcessDto.getSourceEventId(), edxEventProcessDto);

            }
            return eventProcessMap;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }

    }

}
