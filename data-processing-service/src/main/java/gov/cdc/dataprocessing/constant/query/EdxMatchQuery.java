package gov.cdc.dataprocessing.constant.query;

public class EdxMatchQuery {
    public static final String MERGE_EDX_PATIENT_MATCH = """
MERGE INTO EDX_patient_match AS target
USING (
    SELECT
        :patientUid AS patient_uid,
        :typeCd AS type_cd,
        :matchStringHashcode AS match_string_hashcode
) AS source
ON target.patient_uid = source.patient_uid
   AND target.type_cd = source.type_cd
   AND target.match_string_hashcode = source.match_string_hashcode
WHEN MATCHED THEN
    UPDATE SET match_string = :matchString
WHEN NOT MATCHED THEN
    INSERT (
        patient_uid,
        match_string,
        type_cd,
        match_string_hashcode
    )
    VALUES (
        :patientUid,
        :matchString,
        :typeCd,
        :matchStringHashcode
    );
""";

    public static final String MERGE_EDX_ENTITY_MATCH = """
MERGE INTO EDX_entity_match AS target
USING (
    SELECT
        :entityUid AS entity_uid,
        :typeCd AS type_cd,
        :matchStringHashcode AS match_string_hashcode
) AS source
ON target.entity_uid = source.entity_uid
   AND target.type_cd = source.type_cd
   AND target.match_string_hashcode = source.match_string_hashcode
WHEN MATCHED THEN
    UPDATE SET match_string = :matchString
WHEN NOT MATCHED THEN
    INSERT (
        entity_uid,
        match_string,
        type_cd,
        match_string_hashcode
    )
    VALUES (
        :entityUid,
        :matchString,
        :typeCd,
        :matchStringHashcode
    );
""";

}
