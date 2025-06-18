package gov.cdc.dataprocessing.constant.query;

public class NbsInterfaceQuery {
    public static final String SELECT_NBS_INTERFACE_BY_UID = """
    SELECT 
        nbs_interface_uid AS nbsInterfaceUid,
        payload AS payload,
        imp_exp_ind_cd AS impExpIndCd,
        record_status_cd AS recordStatusCd,
        record_status_time AS recordStatusTime,
        add_time AS addTime,
        system_nm AS systemNm,
        doc_type_cd AS docTypeCd,
        original_payload AS originalPayload,
        original_doc_type_cd AS originalDocTypeCd,
        filler_order_nbr AS fillerOrderNbr,
        lab_clia AS labClia,
        specimen_coll_date AS specimenCollDate,
        order_test_code AS orderTestCode,
        observation_uid AS observationUid,
        original_payload_RR AS originalPayloadRR,
        original_doc_type_cd_RR AS originalDocTypeCdRR
    FROM NBS_interface
    WHERE nbs_interface_uid = :nbsInterfaceUid
""";

}
