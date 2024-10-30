package gov.cdc.dataprocessing.repository.nbs.odse.repos.lookup;

import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestionExtended;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
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
