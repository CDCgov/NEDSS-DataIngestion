package gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "state_defined_field_data")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class StateDefinedFieldData {

    @Id
    @Column(name = "ldf_uid")
    private Long ldfUid;

    @Column(name = "business_object_uid")
    private Long businessObjectUid;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "business_object_nm", nullable = false)
    private String businessObjectName;

    @Column(name = "last_chg_time")
    private Date lastChangeTime;

    @Column(name = "ldf_value", length = 2000)
    private String ldfValue;

    @Column(name = "version_ctrl_nbr")
    private Short versionControlNumber;

    // Constructors, getters, and setters (if needed)
}
