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
