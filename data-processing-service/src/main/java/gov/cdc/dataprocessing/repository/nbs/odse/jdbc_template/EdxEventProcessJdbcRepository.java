package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxEventProcess;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.data_field.ADD_TIME_DB;
import static gov.cdc.dataprocessing.constant.data_field.ADD_USER_ID_DB;
import static gov.cdc.dataprocessing.constant.query.EdxEventProcessQuery.MERGE_EDX_EVENT;

@Component
public class EdxEventProcessJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public EdxEventProcessJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate")  NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void mergeEdxEventProcess(EdxEventProcess edx) {
        var params = new org.springframework.jdbc.core.namedparam.MapSqlParameterSource()
                .addValue("edx_event_process_uid", edx.getEdxEventProcessUid())
                .addValue("nbs_document_uid", edx.getNbsDocumentUid())
                .addValue("nbs_event_uid", edx.getNbsEventUid())
                .addValue("source_event_id", edx.getSourceEventId())
                .addValue("doc_event_type_cd", edx.getDocEventTypeCd())
                .addValue(ADD_USER_ID_DB, edx.getAddUserId())
                .addValue(ADD_TIME_DB, edx.getAddTime())
                .addValue("parsed_ind", edx.getParsedInd())
                .addValue("edx_document_uid", edx.getEdxDocumentUid());

        jdbcTemplateOdse.update(MERGE_EDX_EVENT, params);
    }

}
