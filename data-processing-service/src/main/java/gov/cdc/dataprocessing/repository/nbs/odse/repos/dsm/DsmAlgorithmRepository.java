package gov.cdc.dataprocessing.repository.nbs.odse.repos.dsm;

import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public interface DsmAlgorithmRepository  extends JpaRepository<DsmAlgorithm, Long> {

//            	SELECT_DSM_ALGORITHM_LIST ="SELECT dsm_algorithm_uid \"dsmAlgorithmUid\",  algorithm_nm \"algorithmNm\", event_type \"eventType\", condition_list \"conditionList\", frequency \"frequency\" , apply_to \"applyTo\", sending_system_list \"sendingSystemList\", reporting_system_list \"reportingSystemList\", event_action \"eventAction\","
//                	+" algorithm_payload \"algorithmPayload\", admin_comment \"adminComment\", status_cd \"statusCd\", status_time \"statusTime\", last_chg_user_id \"lastChgUserId\", last_chg_time \"lastChgTime\" "
//                    	+" FROM DSM_algorithm where status_cd='A'";
    @Query("SELECT pn FROM DsmAlgorithm pn WHERE pn.statusCd = :statusCd")
    Optional<Collection<DsmAlgorithm>> findDsmAlgorithmByStatusCode(@Param("statusCd") String statusCode);

}
