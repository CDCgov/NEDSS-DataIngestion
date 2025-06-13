package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.constant.data_field.*;

@Repository

public class ParticipationStoredProcRepository {
    private static final Logger logger = LoggerFactory.getLogger(ParticipationStoredProcRepository.class); // NOSONAR

    @PersistenceContext(unitName = "odseEntityManagerFactory") // Specify the persistence unit name
    private EntityManager entityManager;

    @Transactional
    public void insertParticipation(ParticipationDto participationDto) {
        try {

            StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery("addParticipation_sp");

            // Register the parameters
            storedProcedure.registerStoredProcedureParameter(SUBJECT_ENTITY_UID_DB, Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(ACT_UID_DB, Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("type_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(ADD_REASON_CD_DB, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("addtime", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(ADD_USER_ID_DB, Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("awareness_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("awareness_desc_txt", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("duration_amt", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("duration_unit_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("from_time", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(LAST_CHG_REASON_CD_DB, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(LAST_CHG_TIME_DB, Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(LAST_CHG_USER_ID_DB, Long.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(RECORD_STATUS_CD_DB, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(RECORD_STATUS_TIME_DB, Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("role_seq", Integer.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(STATUS_CD_DB, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(STATUS_TIME_DB, Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("to_time", Timestamp.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("type_desc_txt", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter(USER_AFFILIATION_TXT_DB, String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("subject_class_cd", String.class, ParameterMode.IN);
            storedProcedure.registerStoredProcedureParameter("act_class_cd", String.class, ParameterMode.IN);


            // Set the parameter values
            storedProcedure.setParameter(SUBJECT_ENTITY_UID_DB, participationDto.getSubjectEntityUid());
            storedProcedure.setParameter(ACT_UID_DB, participationDto.getActUid());
            storedProcedure.setParameter("type_cd", participationDto.getTypeCd());
            storedProcedure.setParameter(ADD_REASON_CD_DB, participationDto.getAddReasonCd());
            storedProcedure.setParameter("addtime", participationDto.getAddTime());
            storedProcedure.setParameter(ADD_USER_ID_DB, participationDto.getAddUserId());
            storedProcedure.setParameter("awareness_cd", participationDto.getAwarenessCd());
            storedProcedure.setParameter("awareness_desc_txt", participationDto.getAwarenessDescTxt());
            storedProcedure.setParameter("cd", participationDto.getCd());
            storedProcedure.setParameter("duration_amt", participationDto.getDurationAmt());
            storedProcedure.setParameter("duration_unit_cd", participationDto.getDurationUnitCd());
            storedProcedure.setParameter("from_time", participationDto.getFromTime());
            storedProcedure.setParameter(LAST_CHG_REASON_CD_DB, participationDto.getLastChgReasonCd());
            storedProcedure.setParameter(LAST_CHG_TIME_DB, participationDto.getLastChgTime());
            storedProcedure.setParameter(LAST_CHG_USER_ID_DB, participationDto.getLastChgUserId());
            storedProcedure.setParameter(RECORD_STATUS_CD_DB, participationDto.getRecordStatusCd());
            storedProcedure.setParameter(RECORD_STATUS_TIME_DB, participationDto.getRecordStatusTime());
            storedProcedure.setParameter("role_seq", participationDto.getRoleSeq());
            storedProcedure.setParameter(STATUS_CD_DB, participationDto.getStatusCd());
            storedProcedure.setParameter(STATUS_TIME_DB, participationDto.getStatusTime());
            storedProcedure.setParameter("to_time", participationDto.getToTime());
            storedProcedure.setParameter("type_desc_txt", participationDto.getTypeDescTxt());
            storedProcedure.setParameter(USER_AFFILIATION_TXT_DB, participationDto.getUserAffiliationTxt());
            storedProcedure.setParameter("subject_class_cd", participationDto.getSubjectClassCd());
            storedProcedure.setParameter("act_class_cd", participationDto.getActClassCd());




            // Execute the stored procedure
            storedProcedure.execute();


        } catch (Exception e) {
            logger.info(e.getMessage());
        }

    }

}
