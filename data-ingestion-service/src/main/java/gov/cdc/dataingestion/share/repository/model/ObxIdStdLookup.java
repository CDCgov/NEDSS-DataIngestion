package gov.cdc.dataingestion.share.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "obx_id_std_lookup")
public class ObxIdStdLookup {
    @Id
    @Column(name = "uid", nullable = false)
    private Long id;

    @Column(name = "OBX_VALUE_TYPE_ID")
    private String obxValueTypeId;

    @Column(name = "OBX_VALUE_TYPE_DESC")
    private String obxValueTypeDesc;

}
