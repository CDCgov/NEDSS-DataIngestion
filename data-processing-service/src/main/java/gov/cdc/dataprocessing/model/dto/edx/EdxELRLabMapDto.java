package gov.cdc.dataprocessing.model.dto.edx;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
public class EdxELRLabMapDto {
    private Long subjectEntityUid;
    private String roleCd;
    private String roleCdDescTxt;
    private Integer eoleSeq;
    private String roleSubjectClassCd;
    private Long participationEntityUid;
    private Long participationActUid;
    private String participationActClassCd;
    private String participationCd;
    private Integer participationRoleSeq;
    private String participationSubjectClassCd;
    private String participationSubjectEntityCd;
    private String participationTypeCd;
    private String participationTypeDescTxt;
    private Timestamp addTime;
    private Long entityUid;
    private String entityCd;
    private String entityCdDescTxt;
    private String entityStandardIndustryClassCd;
    private String entityStandardIndustryDescTxt;
    private String entityDisplayNm;
    private String entityElectronicInd;
    private Timestamp asOfDate;
    private Long rootObservationUid;
    private String entityIdRootExtensionTxt;
    private String entityIdAssigningAuthorityCd;
    private String entityIdAssigningAuthorityDescTxt;
    private String entityIdTypeCd;
    private String entityIdTypeDescTxt;
}
