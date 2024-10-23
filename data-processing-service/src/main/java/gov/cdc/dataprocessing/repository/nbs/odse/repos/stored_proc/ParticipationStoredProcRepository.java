package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class ParticipationStoredProcRepository {
    @PersistenceContext(unitName = "odseEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    public void insertParticipation(ParticipationDto participationDto) {
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("addParticipation_sp");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter("subject_entity_uid", Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("act_uid", Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("type_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("add_reason_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("addtime", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("add_user_id", Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("awareness_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("awareness_desc_txt", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("duration_amt", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("duration_unit_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("from_time", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("last_chg_reason_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("last_chg_time", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("last_chg_user_id", Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("record_status_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("record_status_time", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("role_seq", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("status_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("status_time", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("to_time", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("type_desc_txt", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("user_affiliation_txt", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("subject_class_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("act_class_cd", String.class, ParameterMode.IN);


            // Set the parameter values
            storedProcedure.setParameter("subject_entity_uid", participationDto.getSubjectEntityUid());
            storedProcedure.setParameter("act_uid", participationDto.getActUid());
            storedProcedure.setParameter("type_cd", participationDto.getTypeCd());
            storedProcedure.setParameter("add_reason_cd", participationDto.getAddReasonCd());
            storedProcedure.setParameter("addtime", participationDto.getAddTime());
            storedProcedure.setParameter("add_user_id", participationDto.getAddUserId());
            storedProcedure.setParameter("awareness_cd", participationDto.getAwarenessCd());
            storedProcedure.setParameter("awareness_desc_txt", participationDto.getAwarenessDescTxt());
            storedProcedure.setParameter("cd", participationDto.getCd());
            storedProcedure.setParameter("duration_amt", participationDto.getDurationAmt());
            storedProcedure.setParameter("duration_unit_cd", participationDto.getDurationUnitCd());
            storedProcedure.setParameter("from_time", participationDto.getFromTime());
            storedProcedure.setParameter("last_chg_reason_cd", participationDto.getLastChgReasonCd());
            storedProcedure.setParameter("last_chg_time", participationDto.getLastChgTime());
            storedProcedure.setParameter("last_chg_user_id", participationDto.getLastChgUserId());
            storedProcedure.setParameter("record_status_cd", participationDto.getRecordStatusCd());
            storedProcedure.setParameter("record_status_time", participationDto.getRecordStatusTime());
            storedProcedure.setParameter("role_seq", participationDto.getRoleSeq());
            storedProcedure.setParameter("status_cd", participationDto.getStatusCd());
            storedProcedure.setParameter("status_time", participationDto.getStatusTime());
            storedProcedure.setParameter("to_time", participationDto.getToTime());
            storedProcedure.setParameter("type_desc_txt", participationDto.getTypeDescTxt());
            storedProcedure.setParameter("user_affiliation_txt", participationDto.getUserAffiliationTxt());
            storedProcedure.setParameter("subject_class_cd", participationDto.getSubjectClassCd());
            storedProcedure.setParameter("act_class_cd", participationDto.getActClassCd());




            // Execute the stored procedure
            storedProcedure.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
