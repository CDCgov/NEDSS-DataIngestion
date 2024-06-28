package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.implementation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Lab_Summary_ForWorkUp_New;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Summary;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class Observation_SummaryRepositoryImpl implements Observation_SummaryRepository {

    @PersistenceContext(unitName = "odse")
    private EntityManager entityManager;

    public  String findAllActiveLabReportUidListForManage_SQL = "SELECT "+
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

    public  String SELECT_LABSUMMARY_FORWORKUPNEW =
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
