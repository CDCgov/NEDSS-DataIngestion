package gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public interface NbsCaseAnswerRepository  extends JpaRepository<NbsCaseAnswer, Long> {

//    private final String SELECT_NBS_ANSWER_COLLECTION = "SELECT nbs_case_answer_uid \"nbsCaseAnswerUid\", seq_nbr \"seqNbr\", add_time \"addTime\", add_user_id \"addUserId\", answer_txt \"answerTxt\", last_chg_time \"lastChgTime\", last_chg_user_id \"lastChgUserId\", nbs_question_uid \"nbsQuestionUid\", act_uid \"actUid\", nbs_question_version_ctrl_nbr \"nbsQuestionVersionCtrlNbr\", answer_large_txt \"answerLargeTxt\",nbs_table_metadata_uid \"nbsTableMetadataUid\", answer_group_seq_nbr \"answerGroupSeqNbr\" FROM "
//            + DataTables.NBS_CASE_ANSWER_TABLE	+ " where act_uid=? order by nbs_question_uid";
    @Query("SELECT data FROM NbsCaseAnswer data WHERE data.actUid = :uid")
    Optional<Collection<NbsCaseAnswer>> getNbsCaseAnswerByActUid(@Param("uid") Long uid);
}
