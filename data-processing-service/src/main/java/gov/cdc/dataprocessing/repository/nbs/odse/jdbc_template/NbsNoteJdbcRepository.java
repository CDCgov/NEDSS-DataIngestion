package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;


import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsNote;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.query.NbsNoteQuery.MERGE_NBS_NOTE;


@Component
public class NbsNoteJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NbsNoteJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void mergeNbsNote(NbsNote note) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nbs_note_uid", note.getNbsNoteUid())
                .addValue("note_parent_uid", note.getNoteParentUid())
                .addValue("record_status_cd", note.getRecordStatusCd())
                .addValue("record_status_time", note.getRecordStatusTime())
                .addValue("add_time", note.getAddTime())
                .addValue("add_user_id", note.getAddUserId())
                .addValue("last_chg_time", note.getLastChgTime())
                .addValue("last_chg_user_id", note.getLastChgUserId())
                .addValue("note", note.getNote())
                .addValue("private_ind_cd", note.getPrivateIndCd())
                .addValue("type_cd", note.getTypeCd());

        jdbcTemplateOdse.update(MERGE_NBS_NOTE, params);
    }
}