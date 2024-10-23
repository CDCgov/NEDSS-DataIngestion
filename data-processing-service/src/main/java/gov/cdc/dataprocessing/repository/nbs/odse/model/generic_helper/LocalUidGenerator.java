package gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Local_UID_generator")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
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
