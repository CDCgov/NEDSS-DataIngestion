package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "LOINC_code")
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class LOINCCode implements Serializable {
    private static final long serialVersionUID = 1L; // Generated serial version UID

    @Id
    @Column(name = "loinc_cd")
    private String loincCode;

    @Column(name = "component_name")
    private String componentName;

    @Column(name = "property")
    private String property;

    @Column(name = "time_aspect")
    private String timeAspect;

    @Column(name = "system_cd")
    private String systemCode;

    @Column(name = "scale_type")
    private String scaleType;

    @Column(name = "method_type")
    private String methodType;

    @Column(name = "display_nm")
    private String displayName;

    @Column(name = "nbs_uid")
    private Long nbsUid;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "related_class_cd")
    private String relatedClassCode;

    @Column(name = "pa_derivation_exclude_cd")
    private String paDerivationExcludeCode;

    // Constructors, getters, and setters
}
