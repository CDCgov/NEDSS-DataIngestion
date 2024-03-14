package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class ParticipationStoredProcRepository {
    @PersistenceContext(unitName = "odseEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    public void insertParticipation(ParticipationDT participationDT) throws DataProcessingException {
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
            storedProcedure.setParameter("subject_entity_uid", participationDT.getSubjectEntityUid());
            storedProcedure.setParameter("act_uid", participationDT.getActUid());
            storedProcedure.setParameter("type_cd", participationDT.getTypeCd());
            storedProcedure.setParameter("add_reason_cd", participationDT.getAddReasonCd());
            storedProcedure.setParameter("addtime", participationDT.getAddTime());
            storedProcedure.setParameter("add_user_id", participationDT.getAddUserId());
            storedProcedure.setParameter("awareness_cd", participationDT.getAwarenessCd());
            storedProcedure.setParameter("awareness_desc_txt", participationDT.getAwarenessDescTxt());
            storedProcedure.setParameter("cd", participationDT.getCd());
            storedProcedure.setParameter("duration_amt", participationDT.getDurationAmt());
            storedProcedure.setParameter("duration_unit_cd", participationDT.getDurationUnitCd());
            storedProcedure.setParameter("from_time", participationDT.getFromTime());
            storedProcedure.setParameter("last_chg_reason_cd", participationDT.getLastChgReasonCd());
            storedProcedure.setParameter("last_chg_time", participationDT.getLastChgTime());
            storedProcedure.setParameter("last_chg_user_id", participationDT.getLastChgUserId());
            storedProcedure.setParameter("record_status_cd", participationDT.getRecordStatusCd());
            storedProcedure.setParameter("record_status_time", participationDT.getRecordStatusTime());
            storedProcedure.setParameter("role_seq", participationDT.getRoleSeq());
            storedProcedure.setParameter("status_cd", participationDT.getStatusCd());
            storedProcedure.setParameter("status_time", participationDT.getStatusTime());
            storedProcedure.setParameter("to_time", participationDT.getToTime());
            storedProcedure.setParameter("type_desc_txt", participationDT.getTypeDescTxt());
            storedProcedure.setParameter("user_affiliation_txt", participationDT.getUserAffiliationTxt());
            storedProcedure.setParameter("subject_class_cd", participationDT.getSubjectClassCd());
            storedProcedure.setParameter("act_class_cd", participationDT.getActClassCd());




            // Execute the stored procedure
            storedProcedure.execute();


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

}
