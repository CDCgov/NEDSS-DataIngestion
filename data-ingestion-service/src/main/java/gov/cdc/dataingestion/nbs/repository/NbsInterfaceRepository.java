package gov.cdc.dataingestion.nbs.repository;

import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NbsInterfaceRepository extends JpaRepository<NbsInterfaceModel, Integer> {
    @Query(value = "SELECT * FROM NBS_interface WHERE nbs_interface_uid = :nbsInterfaceUid AND doc_type_cd = :docTypeCd", nativeQuery = true)
    Optional<NbsInterfaceModel> getNbsInterfaceByIdAndDocType(@Param("nbsInterfaceUid") Integer id, @Param("docTypeCd") String docType);
    Optional<NbsInterfaceModel> findByNbsInterfaceUid(Integer id);

    // Temporarily pulling first 100 values (takes more time to pull everything due to payload size),
    // will need to change once the UI is built as it needs to be refreshed in real time
    @Query(value = "SELECT TOP 100 nbs_interface_uid, payload, imp_exp_ind_cd, record_status_cd, record_status_time, add_time, system_nm, \n" +
            "CASE WHEN doc_type_cd = '11648804' THEN 'ELR' WHEN doc_type_cd = 'PHC236' THEN 'ECR' ELSE doc_type_cd END AS doc_type_cd, " +
            "original_payload, original_doc_type_cd, filler_order_nbr, lab_clia, specimen_coll_date, order_test_code, OBSERVATION_UID, " +
            "original_payload_RR, original_doc_type_cd_RR FROM NBS_interface", nativeQuery = true)
    List<NbsInterfaceModel> findAll();

}
