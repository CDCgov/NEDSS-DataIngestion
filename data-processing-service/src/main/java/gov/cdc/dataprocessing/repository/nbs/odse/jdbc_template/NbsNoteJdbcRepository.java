package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;


import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsNote;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.data_field.*;
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
                .addValue(RECORD_STATUS_CD_DB, note.getRecordStatusCd())
                .addValue(RECORD_STATUS_TIME_DB, note.getRecordStatusTime())
                .addValue(ADD_TIME_DB, note.getAddTime())
                .addValue(ADD_USER_ID_DB, note.getAddUserId())
                .addValue(LAST_CHG_TIME_DB, note.getLastChgTime())
                .addValue(LAST_CHG_USER_ID_DB, note.getLastChgUserId())
                .addValue("note", note.getNote())
                .addValue("private_ind_cd", note.getPrivateIndCd())
                .addValue("type_cd", note.getTypeCd());

        jdbcTemplateOdse.update(MERGE_NBS_NOTE, params);
    }
}