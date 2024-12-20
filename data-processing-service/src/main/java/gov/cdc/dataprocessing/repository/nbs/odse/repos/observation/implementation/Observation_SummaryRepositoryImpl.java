package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.implementation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Lab_Summary_ForWorkUp_New;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Summary;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
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
public class Observation_SummaryRepositoryImpl implements Observation_SummaryRepository // NOSONAR
{

    @PersistenceContext(unitName = "odse")
    private EntityManager entityManager;

    public  String findAllActiveLabReportUidListForManage_SQL = "SELECT "+ // NOSONAR
            "ar.source_act_uid \"uid\", "+
            "ISNULL(ar.from_time,obs.add_time ) \"addTime\", " +
            "ar.add_reason_cd \"addReasonCd\" "+
            "FROM observation obs with (nolock), Act_Relationship ar with (nolock) "+
            "WHERE ar.target_class_cd = '" + NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE + "' " +
            "AND ar.source_class_cd = '" + NEDSSConstant.OBSERVATION_CLASS_CODE + "' " +
            "AND ar.type_cd = '" + NEDSSConstant.LAB_REPORT + "' " +
            "AND ar.record_status_cd = '" + NEDSSConstant.RECORD_STATUS_ACTIVE + "' " +
            "AND ar.target_act_uid = :targetActUid " +
            "AND ar.source_act_uid = obs.observation_uid ";

    public static final String SELECT_LABSUMMARY_FORWORKUPNEW =
            "SELECT participation.act_uid \"uid\", " +
                    "OBS.* " +
                    "FROM observation OBS, person, " +
                    "participation " +
                    "WHERE " +
                    "OBS.observation_uid=participation.act_uid " +
                    "AND participation.subject_entity_uid=person.person_uid " +
                    "AND participation.type_cd='PATSBJ' " +
                    "AND Participation.act_class_cd = 'OBS' " +
                    "AND Participation.subject_class_cd = 'PSN'   " +
                    "AND Participation.record_status_cd = 'ACTIVE' " +
                    "AND person_parent_uid = :personParentUid";


    @Override
    public Collection<Observation_Summary> findAllActiveLabReportUidListForManage(Long investigationUid, String whereClause) {
        var sql = findAllActiveLabReportUidListForManage_SQL + whereClause;
        var lst = new ArrayList<Observation_Summary>();

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("targetActUid", investigationUid);
        List<Object[]> results = query.getResultList();

        if (results != null && !results.isEmpty()) {
            for(var result : results) {
                Observation_Summary container = new Observation_Summary();
                container.setUid((Long) result[0]);
                container.setAddTime((Timestamp) result[1]);
                container.setAddReasonCd((String) result[2]);
            }
        }
        return lst;
    }

    @Override
    public Optional<Collection<Observation_Lab_Summary_ForWorkUp_New>> findLabSummaryForWorkupNew(Long personParentUid, String whereClause) {
        var sql = SELECT_LABSUMMARY_FORWORKUPNEW + whereClause;
        var res =  entityManager.createQuery(sql, Observation_Lab_Summary_ForWorkUp_New.class)
                .setParameter("personParentUid", personParentUid)
                .getResultList();
        return Optional.ofNullable(res);
    }
}
