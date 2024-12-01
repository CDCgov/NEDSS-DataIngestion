package gov.cdc.nbs.mpidatasyncer.repository.nbs;

import gov.cdc.nbs.mpidatasyncer.entity.nbs.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
  List<Person> findAllByAddTimeAfterAndAddTimeLessThanEqual(Pageable pageable, LocalDateTime lastSyncTime,
      LocalDateTime currentTime);

  List<Person> findAllByLastChgTimeAfterAndLastChgTimeLessThanEqualAndAddTimeLessThanEqual(PageRequest pageRequest,
      LocalDateTime lastChangeTime, LocalDateTime currentTime, LocalDateTime addTime);

  @Query("""
      SELECT p 
      FROM Person p 
      WHERE p.addTime <= :currentTime 
      AND p.personUid IN (
          SELECT DISTINCT p2.parent.personUid 
          FROM Person p2
          WHERE p2.parent.personUid IS NOT NULL
      ) 
      ORDER BY p.personUid ASC
            """)
  Page<Person> findAllByAddTimeLessThanEqualOrderByPersonUidAsc(LocalDateTime currentTime, Pageable pageable);

  @Query("""
          SELECT p
          FROM Person p
          WHERE p.personUid = (
              SELECT MAX(parent.personUid)
              FROM Person p2
              WHERE p2.personUid = p2.parent.personUid
              AND parent.addTime <= :currentTime )
      """)
  Person findPersonWithMaxPersonUidByAddTimeLessThanEqual(@Param("currentTime") LocalDateTime currentTime);

}
