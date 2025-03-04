package gov.cdc.dataprocessing.repository.nbs.odse.repos.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public interface ActRelationshipRepository extends JpaRepository<ActRelationship, Long> {
    @Query("SELECT data FROM ActRelationship data WHERE data.targetActUid = :uid")
    Optional<Collection<ActRelationship>> findRecordsByActUid(@Param("uid") Long uid);

    @Query("SELECT data FROM ActRelationship data WHERE data.sourceActUid = :uid")
    Optional<Collection<ActRelationship>> findRecordsBySourceId(@Param("uid") Long uid);


    /*
    String SELECT_BY_SOURCE_AND_TYPE = "SELECT target_act_uid \"TargetActUid\", source_act_uid \"SourceActUid\",
    add_reason_cd \"AddReasonCd\", add_time \"AddTime\", add_user_id \"AddUserId\", duration_amt \"DurationAmt\",
    duration_unit_cd \"DurationUnitCd\", from_time \"FromTime\", last_chg_reason_cd \"LastChgReasonCd\",
    last_chg_time \"LastChgTime\", last_chg_user_id \"LastChgUserId\", record_status_cd \"RecordStatusCd\",
    record_status_time \"RecordStatusTime\", sequence_nbr \"SequenceNbr\", status_cd \"StatusCd\",
    status_time \"StatusTime\", to_time \"ToTime\", type_cd \"TypeCd\", target_class_cd \"TargetClassCd\",
    source_class_cd \"SourceClassCd\", type_desc_txt \"TypeDescTxt\", user_affiliation_txt \"UserAffiliationTxt\"
    from Act_relationship WITH (NOLOCK)
    where source_act_uid = ? and type_cd = ?"
    *
    **/
    @Query("SELECT data FROM ActRelationship data WHERE data.sourceActUid = :uid AND data.typeCd = :type")
    Optional<Collection<ActRelationship>> loadActRelationshipBySrcIdAndTypeCode(@Param("uid") Long uid, @Param("type") String type);

    /**
     String DELETE_BY_PK = "DELETE from Act_relationship where target_act_uid = ? and source_act_uid = ? and type_cd = ?"
     * */
    @Modifying
    @Query("DELETE FROM ActRelationship data WHERE data.targetActUid = ?1 AND data.sourceActUid = ?2 AND data.typeCd = ?3")
    void deleteActRelationshipByPk(Long subjectUid, Long actUid, String typeCode);


}
