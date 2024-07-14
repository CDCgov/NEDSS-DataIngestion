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
