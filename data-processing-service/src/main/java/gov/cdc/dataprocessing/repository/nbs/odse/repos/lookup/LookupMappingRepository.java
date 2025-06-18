package gov.cdc.dataprocessing.repository.nbs.odse.repos.lookup;

import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestionExtended;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface LookupMappingRepository  extends JpaRepository<LookupQuestion, Long> {
    @Query(value = "SELECT " +
            "   LOOKUP_QUESTION.*, " +
            "   LOOKUP_ANSWER.FROM_CODE_SYSTEM_CD AS fromAnsCodeSystemCd, " +
            "   LOOKUP_ANSWER.TO_CODE_SYSTEM_CD AS toAnsCodeSystemCd, " +
            "   LOOKUP_ANSWER_UID AS lookupAnswerUid, " +
            "   FROM_ANSWER_CODE AS fromAnswerCode, " +
            "   TO_ANSWER_CODE AS toAnswerCode " +
            "FROM " +
            "   LOOKUP_QUESTION " +
            "LEFT OUTER JOIN " +
            "   LOOKUP_ANSWER " +
            "ON " +
            "   LOOKUP_QUESTION.LOOKUP_QUESTION_uid = LOOKUP_ANSWER.LOOKUP_QUESTION_UID " +
            "ORDER BY " +
            "   FROM_FORM_CD, " +
            "   TO_FORM_CD", nativeQuery = true)
    Optional<List<LookupQuestionExtended>> getLookupMappings();
    // LookupMappingDto
}
