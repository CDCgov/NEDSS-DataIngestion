package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
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
public class ExportReceivingFacilityDto extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private Timestamp addTime;
    private String reportType;
    private Long addUserId;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private Long exportReceivingFacilityUid;
    private String recordStatusCd;
    private String receivingSystemNm;
    private String  receivingSystemOid;
    private String receivingSystemShortName;
    private String receivingSystemOwner;
    private String receivingSystemOwnerOid;
    private String receivingSystemDescTxt;
    private String sendingIndCd;
    private String receivingIndCd;
    private String allowTransferIndCd;
    private String adminComment;

    private String sendingIndDescTxt;
    private String receivingIndDescTxt;
    private String allowTransferIndDescTxt;
    private String reportTypeDescTxt;

    private String recordStatusCdDescTxt;

    private String jurDeriveIndCd;
}
