package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.dto.observation.ObsValueTxtDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ObsValueTxtId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Data
@Entity
@Table(name = "Obs_value_txt")
@IdClass(ObsValueTxtId.class)
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class ObsValueTxt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

    @Id
    @Column(name = "obs_value_txt_seq")
    private Integer obsValueTxtSeq;

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
    public ObsValueTxt(ObsValueTxtDto obsValueTxtDto) {
        this.observationUid = obsValueTxtDto.getObservationUid();
        this.obsValueTxtSeq = obsValueTxtDto.getObsValueTxtSeq();
        this.dataSubtypeCd = obsValueTxtDto.getDataSubtypeCd();
        this.encodingTypeCd = obsValueTxtDto.getEncodingTypeCd();
        this.txtTypeCd = obsValueTxtDto.getTxtTypeCd();
        this.valueImageTxt = obsValueTxtDto.getValueImageTxt();
        this.valueTxt = obsValueTxtDto.getValueTxt();
    }

    public ObsValueTxt() {
        
    }
}
