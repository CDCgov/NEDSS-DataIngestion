package gov.cdc.dataprocessing.repository.nbs.odse.model;


import jakarta.persistence.*;
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
