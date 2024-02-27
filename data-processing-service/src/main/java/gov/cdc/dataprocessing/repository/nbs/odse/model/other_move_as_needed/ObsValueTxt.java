package gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Obs_value_txt")
public class ObsValueTxt {

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

    @Id
    @Column(name = "obs_value_txt_seq")
    private Short obsValueTxtSeq;

    @Column(name = "data_subtype_cd")
    private String dataSubtypeCd;

    @Column(name = "encoding_type_cd")
    private String encodingTypeCd;

    @Column(name = "txt_type_cd")
    private String txtTypeCd;

    @Lob
    @Column(name = "value_image_txt")
    private byte[] valueImageTxt;

    @Column(name = "value_txt", length = 2000)
    private String valueTxt;

    // Relationships if needed
}
