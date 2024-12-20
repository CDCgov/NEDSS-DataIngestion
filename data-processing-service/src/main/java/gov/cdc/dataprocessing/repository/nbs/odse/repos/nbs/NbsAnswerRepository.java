package gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
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
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public interface NbsAnswerRepository  extends JpaRepository<NbsAnswer, Long> {
    /**
     *
     * String SELECT_NBS_ANSWER_COLLECTION = "SELECT nbs_answer_uid \"nbsAnswerUid\", seq_nbr \"seqNbr\",
     * answer_txt \"answerTxt\", ca.last_chg_time \"lastChgTime\", ca.last_chg_user_id \"lastChgUserId\",
     * ca.nbs_question_uid \"nbsQuestionUid\", act_uid \"actUid\", nbs_question_version_ctrl_nbr \"nbsQuestionVersionCtrlNbr\",
     * record_status_cd \"recordStatusCd\", record_status_time \"recordStatusTime\", answer_group_seq_nbr \"answerGroupSeqNbr\"
     * FROM nbs_answer ca where ca.act_uid = ? ORDER BY nbs_question_uid"
     * */
    @Query("SELECT data FROM NbsAnswer data WHERE data.actUid = :uid")
    Optional<Collection<NbsAnswer>> getPageAnswerByActUid(@Param("uid") Long uid);


    /**
     * String DELETE_NBS_ANSWER = "DELETE FROM nbs_answer WHERE nbs_answer_uid= ?"
     */
    @Query("DELETE FROM NbsAnswer data WHERE data.nbsAnswerUid = :nbsAnswerUid")
    void deleteNbsAnswer(Long nbsAnswerUid);


}
