package gov.cdc.dataprocessing.repository.nbs.odse.repos.participation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ParticipationId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
    @Query("SELECT data FROM Participation data WHERE data.actUid = :uid")
    Collection<Participation> findRecordsById(@Param("uid") Long uid);

    @Query("SELECT data FROM Participation data WHERE data.subjectEntityUid = :parentUid")
    Optional<List<Participation>> findByParentUid(@Param("parentUid") Long parentUid);

    @Query("SELECT data.subjectEntityUid FROM Participation data WHERE data.subjectClassCode = :classCode AND data.typeCode = :type AND data.actUid = :actUid")
    Optional<List<Long>> findPatientMprUidByObservationUid(@Param("classCode") String classCode,
                                                           @Param("type") String typeCode,
                                                           @Param("actUid") Long actUid);

    @Query("DELETE FROM Participation data WHERE data.subjectEntityUid = :subjectUid AND data.actUid = :actUid AND data.typeCode = :typeCode")
    void deleteParticipationByPk(Long subjectUid, Long actUid, String typeCode);

    Optional<List<Participation>> findBySubjectEntityUidAndActUid(Long subjectEntityUid,Long actUid);

    @Query("SELECT data FROM Participation data WHERE data.subjectEntityUid = :subjectEntityUid")
    Optional<List<Participation>> findBySubjectEntityUid(@Param("subjectEntityUid")  Long subjectEntityUid);
}

