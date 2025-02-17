package gov.cdc.dataprocessing.utilities.component.nbs_interface;

import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class NbsInterfaceReposUtilJdbc {
    private final JdbcTemplate jdbcTemplateMsgoute;
    private static final String FIND_BY_NBS_INTERFACE_UID = """
        SELECT 
            nbs_interface_uid, payload, imp_exp_ind_cd, record_status_cd, record_status_time, 
            add_time, system_nm, doc_type_cd, original_payload, original_doc_type_cd, 
            filler_order_nbr, lab_clia, specimen_coll_date, order_test_code, 
            observation_uid, original_payload_RR, original_doc_type_cd_RR
        FROM NBS_interface
        WHERE nbs_interface_uid = ?
    """;

    public NbsInterfaceReposUtilJdbc(@Qualifier("msgouteJdbcTemplate") JdbcTemplate jdbcTemplateMsgoute) {
        this.jdbcTemplateMsgoute = jdbcTemplateMsgoute;
    }


    public Optional<NbsInterfaceModel> findByNbsInterfaceUid(Integer id) {
        return jdbcTemplateMsgoute.query(
                FIND_BY_NBS_INTERFACE_UID,
                new Object[]{id},
                (ResultSet rs) -> rs.next() ? Optional.of(mapRowToNbsInterfaceModel(rs)) : Optional.empty()
        );
    }

    private NbsInterfaceModel mapRowToNbsInterfaceModel(ResultSet rs) throws SQLException {
        NbsInterfaceModel model = new NbsInterfaceModel();
        model.setNbsInterfaceUid(rs.getInt("nbs_interface_uid"));
        model.setPayload(rs.getString("payload"));
        model.setImpExpIndCd(rs.getString("imp_exp_ind_cd"));
        model.setRecordStatusCd(rs.getString("record_status_cd"));
        model.setRecordStatusTime(rs.getTimestamp("record_status_time"));
        model.setAddTime(rs.getTimestamp("add_time"));
        model.setSystemNm(rs.getString("system_nm"));
        model.setDocTypeCd(rs.getString("doc_type_cd"));
        model.setOriginalPayload(rs.getString("original_payload"));
        model.setOriginalDocTypeCd(rs.getString("original_doc_type_cd"));
        model.setFillerOrderNbr(rs.getString("filler_order_nbr"));
        model.setLabClia(rs.getString("lab_clia"));
        model.setSpecimenCollDate(rs.getTimestamp("specimen_coll_date"));
        model.setOrderTestCode(rs.getString("order_test_code"));
        model.setObservationUid(rs.getObject("observation_uid", Integer.class));
        model.setOriginalPayloadRR(rs.getString("original_payload_RR"));
        model.setOriginalDocTypeCdRR(rs.getString("original_doc_type_cd_RR"));
        return model;
    }
}
