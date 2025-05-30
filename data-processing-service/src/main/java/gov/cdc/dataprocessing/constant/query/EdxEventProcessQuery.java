package gov.cdc.dataprocessing.constant.query;

public class EdxEventProcessQuery {
    public static String MERGE_EDX_EVENT = """
            MERGE INTO EDX_event_process AS target
            USING (VALUES
                (:edx_event_process_uid, :nbs_document_uid, :nbs_event_uid, :source_event_id,
                 :doc_event_type_cd, :add_user_id, :add_time, :parsed_ind, :edx_document_uid)
            ) AS source (
                edx_event_process_uid, nbs_document_uid, nbs_event_uid, source_event_id,
                doc_event_type_cd, add_user_id, add_time, parsed_ind, edx_document_uid
            )
            ON target.edx_event_process_uid = source.edx_event_process_uid
            
            WHEN MATCHED THEN
                UPDATE SET
                    nbs_document_uid = source.nbs_document_uid,
                    nbs_event_uid = source.nbs_event_uid,
                    source_event_id = source.source_event_id,
                    doc_event_type_cd = source.doc_event_type_cd,
                    add_user_id = source.add_user_id,
                    add_time = source.add_time,
                    parsed_ind = source.parsed_ind,
                    edx_document_uid = source.edx_document_uid
            
            WHEN NOT MATCHED THEN
                INSERT (
                    edx_event_process_uid, nbs_document_uid, nbs_event_uid, source_event_id,
                    doc_event_type_cd, add_user_id, add_time, parsed_ind, edx_document_uid
                )
                VALUES (
                    source.edx_event_process_uid, source.nbs_document_uid, source.nbs_event_uid, source.source_event_id,
                    source.doc_event_type_cd, source.add_user_id, source.add_time, source.parsed_ind, source.edx_document_uid
                );
            """;
}
