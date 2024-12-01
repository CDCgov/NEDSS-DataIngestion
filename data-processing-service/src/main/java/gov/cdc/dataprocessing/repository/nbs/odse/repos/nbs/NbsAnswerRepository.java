package gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
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
