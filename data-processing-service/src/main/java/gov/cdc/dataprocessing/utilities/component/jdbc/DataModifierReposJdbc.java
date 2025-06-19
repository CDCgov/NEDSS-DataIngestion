package gov.cdc.dataprocessing.utilities.component.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataModifierReposJdbc {
    private final JdbcTemplate jdbcTemplateOdse;

    public DataModifierReposJdbc(@Qualifier("odseJdbcTemplate") JdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void updatePersonNameStatus(Long personUid, Integer personSeq) {
        String sql = "UPDATE Person_name SET status_cd = 'I' WHERE person_uid = ? AND person_name_seq = ?";
        jdbcTemplateOdse.update(sql, personUid, personSeq);
    }

    public void deleteEntityIdAndSeq(Long entityUid, Integer entityIdSeq) {
        String sql = "DELETE FROM Entity_id WHERE entity_uid = ? AND entity_id_seq = ?";
        jdbcTemplateOdse.update(sql, entityUid, entityIdSeq);
    }

    public void deleteByPatientUidAndMatchStringNotLike(Long patientUid) {
        String sql = "DELETE FROM EDX_patient_match WHERE Patient_uid = ? AND match_string NOT LIKE 'LR^%'";
        jdbcTemplateOdse.update(sql, patientUid);
    }

    public void deletePersonRaceByUidAndCode(Long personUid, String raceCd) {
        String sql = "DELETE FROM Person_race WHERE person_uid = ? AND race_cd = ?";
        jdbcTemplateOdse.update(sql, personUid, raceCd);
    }


    @SuppressWarnings("java:S2077")
    public void deletePersonRaceByUid(Long personUid, List<String> raceCds) {
        String sql = "DELETE FROM Person_race WHERE person_uid = ? AND race_cd NOT IN (" +
                String.join(",", raceCds.stream().map(cd -> "?").toArray(String[]::new)) + ")";
        jdbcTemplateOdse.update(sql, prepareParameters(personUid, raceCds));
    }

    private Object[] prepareParameters(Long personUid, List<String> raceCds) {
        Object[] params = new Object[raceCds.size() + 1];
        params[0] = personUid;
        System.arraycopy(raceCds.toArray(), 0, params, 1, raceCds.size());
        return params;
    }

    public Integer updateExistingPersonEdxIndByUid(Long uid) {
        String sql = "UPDATE Person SET edx_ind = 'Y' WHERE person_uid = ?";
        return jdbcTemplateOdse.update(sql, uid);
    }

    public void deletePostalLocatorById(Long postalId) {
        String sql = "DELETE FROM Postal_locator WHERE postal_locator_uid = ?";
        jdbcTemplateOdse.update(sql, postalId);
    }

    public void deleteActRelationshipByPk(Long subjectUid, Long actUid, String typeCode) {
        String sql = "DELETE FROM Act_relationship WHERE target_act_uid = ? AND source_act_uid = ? AND type_cd = ?";
        jdbcTemplateOdse.update(sql, subjectUid, actUid, typeCode);
    }

    public void deleteLocatorById(Long entityUid, Long locatorUid) {
        String sql = "DELETE FROM Entity_locator_participation WHERE entity_uid = ? AND locator_uid = ?";
        jdbcTemplateOdse.update(sql, entityUid, locatorUid);
    }

    public void deleteParticipationByPk(Long subjectEntityUid, Long actUid, String typeCd) {
        String sql = "DELETE FROM Participation WHERE subject_entity_uid = ? AND act_uid = ? AND type_cd = ?";
        jdbcTemplateOdse.update(sql, subjectEntityUid, actUid, typeCd);
    }

    public void deleteRoleByPk(Long subjectEntityUid, String code, Long roleSeq) {
        String sql = "DELETE FROM Role WHERE subject_entity_uid = ? AND cd = ? AND role_seq = ?";
        jdbcTemplateOdse.update(sql, subjectEntityUid, code, roleSeq);
    }
}
