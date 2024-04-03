package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.model.dto.lookup.LookupMappingDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LookupMappingRepository  extends JpaRepository<LookupQuestion, Long> {
    @Query(value = "SELECT " +
            "   LOOKUP_QUESTION.LOOKUP_QUESTION_uid AS lookupQuestionUid, " +
            "   FROM_QUESTION_IDENTIFIER AS fromQuestionIdentifier, " +
            "   LOOKUP_QUESTION.FROM_CODE_SYSTEM_CD AS fromCodeSystemCd, " +
            "   FROM_DATA_TYPE AS fromDataType, " +
            "   FROM_FORM_CD AS fromFormCd, " +
            "   TO_FORM_CD AS toFormCd, " +
            "   TO_QUESTION_IDENTIFIER AS toQuestionIdentifier, " +
            "   LOOKUP_QUESTION.TO_CODE_SYSTEM_CD AS toCodeSystemCd, " +
            "   TO_DATA_TYPE AS toDataType, " +
            "   LOOKUP_ANSWER_UID AS lookupAnswerUid, " +
            "   FROM_ANSWER_CODE AS fromAnswerCode, " +
            "   LOOKUP_ANSWER.FROM_CODE_SYSTEM_CD AS fromAnsCodeSystemCd, " +
            "   TO_ANSWER_CODE AS toAnswerCode, " +
            "   LOOKUP_ANSWER.TO_CODE_SYSTEM_CD AS toAnsCodeSystemCd " +
            "FROM " +
            "   LOOKUP_QUESTION " +
            "LEFT OUTER JOIN " +
            "   LOOKUP_ANSWER " +
            "ON " +
            "   LOOKUP_QUESTION.LOOKUP_QUESTION_uid = LOOKUP_ANSWER.LOOKUP_QUESTION_UID " +
            "ORDER BY " +
            "   FROM_FORM_CD, " +
            "   TO_FORM_CD", nativeQuery = true)
    Optional<List<LookupMappingDto>> getLookupMappings();
}
