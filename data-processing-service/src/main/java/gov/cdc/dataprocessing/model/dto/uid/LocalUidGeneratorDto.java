package gov.cdc.dataprocessing.model.dto.uid;


import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
