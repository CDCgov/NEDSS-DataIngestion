package gov.cdc.dataprocessing.model.dto.uid;


import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class LocalUidGeneratorDto {
    private String classNameCd;

    private String typeCd;

    private String uidPrefixCd;

    private String uidSuffixCd;

    private Long seedValueNbr;

    private int counter;
    private int usedCounter;


    public LocalUidGeneratorDto() {

    }

    public LocalUidGeneratorDto(LocalUidGenerator localUidGenerator) {
        this.classNameCd = localUidGenerator.getClassNameCd();
        this.typeCd = localUidGenerator.getTypeCd();
        this.uidPrefixCd = localUidGenerator.getUidPrefixCd();
        this.uidSuffixCd = localUidGenerator.getUidSuffixCd();
        this.seedValueNbr = localUidGenerator.getSeedValueNbr();
    }
}
