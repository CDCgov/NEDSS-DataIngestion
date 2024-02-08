package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "LOINC_code")
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
    private Date effectiveFromTime;

    @Column(name = "effective_to_time")
    private Date effectiveToTime;

    @Column(name = "related_class_cd")
    private String relatedClassCode;

    @Column(name = "pa_derivation_exclude_cd")
    private Character paDerivationExcludeCode;

    // Constructors, getters, and setters
}
