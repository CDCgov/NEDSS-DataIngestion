package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
@Table(name = "Observation")
@Data
public class Observation_Question extends ObservationBase{
    private Long obsCodeUid;
    private String code;
    private String originalTxt;
    private String codeSystemDescTxt;
    private Long obsDateUid;
    private Timestamp fromTime;
    private Timestamp toTime;
    private String durationAmt;
    private String durationUnitCd;
    private Integer obsValueDateSeq;
    private Long obsNumericUid;
    private BigDecimal numericValue1;
    private BigDecimal numericValue2;
    private Integer numericScale1;
    private Integer numericScale2;
    private String numericUnitCd;
    private Integer obsValueNumericSeq;
    private Long obsTxtUid;
    private String valueTxt;
    private Integer obsValueTxtSeq;
    private Long sourceActUid;
    private Long targetActUid;
    private String typeCd;

    public Observation_Question() {

    }
}