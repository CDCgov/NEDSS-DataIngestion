package gov.cdc.srtedataservice.repository.nbs.odse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Local_UID_generator")
public class LocalUidGenerator {

    @Id
    @Column(name = "class_name_cd")
    private String classNameCd;

    @Column(name = "type_cd")
    private String typeCd;

    @Column(name = "UID_prefix_cd")
    private String uidPrefixCd;

    @Column(name = "UID_suffix_CD")
    private String uidSuffixCd;

    @Column(name = "seed_value_nbr")
    private Long seedValueNbr;

}
