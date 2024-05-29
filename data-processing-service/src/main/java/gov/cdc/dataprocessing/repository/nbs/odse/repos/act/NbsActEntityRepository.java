package gov.cdc.dataprocessing.repository.nbs.odse.repos.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface NbsActEntityRepository  extends JpaRepository<NbsActEntity, Long> {
    /**
     * 	private final String SELECT_ACT_ENTITY_COLLECTION="SELECT nbs_act_entity_uid \"nbsActEntityUid\", add_time \"addTime\", add_user_id \"addUserId\", last_chg_time \"lastChgTime\", last_chg_user_id \"lastChgUserId\", act_uid \"actUid\", entity_uid \"entityUid\", type_cd \"typeCd\", entity_version_ctrl_nbr \"entityVersionCtrlNbr\"
     * 	FROM "+ DataTables.NBS_ACT_ENTITY_TABLE +" where act_uid=?";
     * */

    @Query("SELECT data FROM NbsActEntity data WHERE data.actUid = :uid")
    Optional<Collection<NbsActEntity>> getNbsActEntitiesByActUid(@Param("uid") Long uid);

    @Query("DELETE FROM NbsActEntity data WHERE data.nbsActEntityUid = :nbsActEntityUid")
    void deleteNbsEntityAct(Long nbsActEntityUid);


}
