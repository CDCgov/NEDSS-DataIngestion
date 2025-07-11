package gov.cdc.dataprocessing.repository.nbs.odse.repos.dsm;

import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository


@SuppressWarnings("java:S125")
public interface DsmAlgorithmRepository  extends JpaRepository<DsmAlgorithm, Long> {

//            	SELECT_DSM_ALGORITHM_LIST ="SELECT dsm_algorithm_uid \"dsmAlgorithmUid\",  algorithm_nm \"algorithmNm\", event_type \"eventType\", condition_list \"conditionList\", frequency \"frequency\" , apply_to \"applyTo\", sending_system_list \"sendingSystemList\", reporting_system_list \"reportingSystemList\", event_action \"eventAction\","
//                	+" algorithm_payload \"algorithmPayload\", admin_comment \"adminComment\", status_cd \"statusCd\", status_time \"statusTime\", last_chg_user_id \"lastChgUserId\", last_chg_time \"lastChgTime\" "
//                    	+" FROM DSM_algorithm where status_cd='A'";
    @Query("SELECT pn FROM DsmAlgorithm pn WHERE pn.statusCd = :statusCd")
    Optional<Collection<DsmAlgorithm>> findDsmAlgorithmByStatusCode(@Param("statusCd") String statusCode);

}
