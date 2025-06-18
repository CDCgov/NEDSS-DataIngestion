package gov.cdc.dataprocessing.constant.query;

public class ConfirmationMethodQuery {
    public static final String SELECT_CONFIRMATION_METHOD_BY_UID = """
    SELECT
        public_health_case_uid AS publicHealthCaseUid,
        confirmation_method_cd AS confirmationMethodCd,
        confirmation_method_desc_txt AS confirmationMethodDescTxt,
        confirmation_method_time AS confirmationMethodTime
    FROM Confirmation_method
    WHERE public_health_case_uid = :uid
""";

    public static final String MERGE_CONFIRMATION_METHOD = """
            MERGE INTO Confirmation_method AS target
            USING (VALUES (:publicHealthCaseUid, :confirmationMethodCd, :confirmationMethodDescTxt, :confirmationMethodTime)) AS source
                   (public_health_case_uid, confirmation_method_cd, confirmation_method_desc_txt, confirmation_method_time)
            ON target.public_health_case_uid = source.public_health_case_uid
            WHEN MATCHED THEN
                UPDATE SET
                    confirmation_method_cd = source.confirmation_method_cd,
                    confirmation_method_desc_txt = source.confirmation_method_desc_txt,
                    confirmation_method_time = source.confirmation_method_time
            WHEN NOT MATCHED THEN
                INSERT (
                    public_health_case_uid,
                    confirmation_method_cd,
                    confirmation_method_desc_txt,
                    confirmation_method_time
                )
                VALUES (
                    source.public_health_case_uid,
                    source.confirmation_method_cd,
                    source.confirmation_method_desc_txt,
                    source.confirmation_method_time
                );
            
            """;
}
