package gov.cdc.dataprocessing.constant.query;

public class ActQuery {
    public static final String INSERT_SQL_ACT = """
            INSERT INTO Act (
                act_uid,
                class_cd,
                mood_cd
            ) VALUES (
                :act_uid,
                :class_cd,
                :mood_cd
            );
            
            """;

    public static final String UPDATE_SQL_ACT = """
            UPDATE Act SET
                class_cd = :class_cd,
                mood_cd = :mood_cd
            WHERE act_uid = :act_uid;
            
            """;

    public static final String MERGE_SQL_ACT = """
            MERGE INTO Act AS target
            USING (SELECT :act_uid AS act_uid) AS source
            ON target.act_uid = source.act_uid
            WHEN MATCHED THEN
                UPDATE SET
                    class_cd = :class_cd,
                    mood_cd = :mood_cd
            WHEN NOT MATCHED THEN
                INSERT (act_uid, class_cd, mood_cd)
                VALUES (:act_uid, :class_cd, :mood_cd);
            
            """;
}
