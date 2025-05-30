package gov.cdc.dataprocessing.constant.query;

public class OrganizationNameQuery {
    // INSERT
    public static final String INSERT_ORGANIZATION_NAME = """
INSERT INTO Organization_name (
    organization_uid, organization_name_seq, nm_txt, nm_use_cd, record_status_cd, default_nm_ind
) VALUES (
    :organizationUid, :organizationNameSeq, :nameText, :nameUseCode, :recordStatusCode, :defaultNameIndicator
)
""";

    // UPDATE
    public static final String UPDATE_ORGANIZATION_NAME = """
UPDATE Organization_name SET
    nm_txt = :nameText,
    nm_use_cd = :nameUseCode,
    record_status_cd = :recordStatusCode,
    default_nm_ind = :defaultNameIndicator
WHERE organization_uid = :organizationUid
  AND organization_name_seq = :organizationNameSeq
""";

    // SELECT BY ORGANIZATION_UID
    public static final String SELECT_ORGANIZATION_NAMES_BY_ORG_UID = """
SELECT
    organization_uid         AS organizationUid,
    organization_name_seq    AS organizationNameSeq,
    nm_txt                   AS nameText,
    nm_use_cd                AS nameUseCode,
    record_status_cd         AS recordStatusCode,
    default_nm_ind           AS defaultNameIndicator
FROM Organization_name
WHERE organization_uid = :organizationUid
""";

}
