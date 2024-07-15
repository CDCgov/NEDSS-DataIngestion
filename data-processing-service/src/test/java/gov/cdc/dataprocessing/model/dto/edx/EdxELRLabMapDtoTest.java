package gov.cdc.dataprocessing.model.dto.edx;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EdxELRLabMapDtoTest {

    @Test
    void testGettersAndSetters() {
        EdxELRLabMapDto dto = new EdxELRLabMapDto();

        Long subjectEntityUid = 1L;
        String roleCd = "roleCd";
        String roleCdDescTxt = "roleCdDescTxt";
        Integer eoleSeq = 1;
        String roleSubjectClassCd = "roleSubjectClassCd";
        Long participationEntityUid = 2L;
        Long participationActUid = 3L;
        String participationActClassCd = "participationActClassCd";
        String participationCd = "participationCd";
        Integer participationRoleSeq = 2;
        String participationSubjectClassCd = "participationSubjectClassCd";
        String participationSubjectEntityCd = "participationSubjectEntityCd";
        String participationTypeCd = "participationTypeCd";
        String participationTypeDescTxt = "participationTypeDescTxt";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long entityUid = 4L;
        String entityCd = "entityCd";
        String entityCdDescTxt = "entityCdDescTxt";
        String entityStandardIndustryClassCd = "entityStandardIndustryClassCd";
        String entityStandardIndustryDescTxt = "entityStandardIndustryDescTxt";
        String entityDisplayNm = "entityDisplayNm";
        String entityElectronicInd = "entityElectronicInd";
        Timestamp asOfDate = new Timestamp(System.currentTimeMillis());
        Long rootObservationUid = 5L;
        String entityIdRootExtensionTxt = "entityIdRootExtensionTxt";
        String entityIdAssigningAuthorityCd = "entityIdAssigningAuthorityCd";
        String entityIdAssigningAuthorityDescTxt = "entityIdAssigningAuthorityDescTxt";
        String entityIdTypeCd = "entityIdTypeCd";
        String entityIdTypeDescTxt = "entityIdTypeDescTxt";

        dto.setSubjectEntityUid(subjectEntityUid);
        dto.setRoleCd(roleCd);
        dto.setRoleCdDescTxt(roleCdDescTxt);
        dto.setEoleSeq(eoleSeq);
        dto.setRoleSubjectClassCd(roleSubjectClassCd);
        dto.setParticipationEntityUid(participationEntityUid);
        dto.setParticipationActUid(participationActUid);
        dto.setParticipationActClassCd(participationActClassCd);
        dto.setParticipationCd(participationCd);
        dto.setParticipationRoleSeq(participationRoleSeq);
        dto.setParticipationSubjectClassCd(participationSubjectClassCd);
        dto.setParticipationSubjectEntityCd(participationSubjectEntityCd);
        dto.setParticipationTypeCd(participationTypeCd);
        dto.setParticipationTypeDescTxt(participationTypeDescTxt);
        dto.setAddTime(addTime);
        dto.setEntityUid(entityUid);
        dto.setEntityCd(entityCd);
        dto.setEntityCdDescTxt(entityCdDescTxt);
        dto.setEntityStandardIndustryClassCd(entityStandardIndustryClassCd);
        dto.setEntityStandardIndustryDescTxt(entityStandardIndustryDescTxt);
        dto.setEntityDisplayNm(entityDisplayNm);
        dto.setEntityElectronicInd(entityElectronicInd);
        dto.setAsOfDate(asOfDate);
        dto.setRootObservationUid(rootObservationUid);
        dto.setEntityIdRootExtensionTxt(entityIdRootExtensionTxt);
        dto.setEntityIdAssigningAuthorityCd(entityIdAssigningAuthorityCd);
        dto.setEntityIdAssigningAuthorityDescTxt(entityIdAssigningAuthorityDescTxt);
        dto.setEntityIdTypeCd(entityIdTypeCd);
        dto.setEntityIdTypeDescTxt(entityIdTypeDescTxt);

        assertEquals(subjectEntityUid, dto.getSubjectEntityUid());
        assertEquals(roleCd, dto.getRoleCd());
        assertEquals(roleCdDescTxt, dto.getRoleCdDescTxt());
        assertEquals(eoleSeq, dto.getEoleSeq());
        assertEquals(roleSubjectClassCd, dto.getRoleSubjectClassCd());
        assertEquals(participationEntityUid, dto.getParticipationEntityUid());
        assertEquals(participationActUid, dto.getParticipationActUid());
        assertEquals(participationActClassCd, dto.getParticipationActClassCd());
        assertEquals(participationCd, dto.getParticipationCd());
        assertEquals(participationRoleSeq, dto.getParticipationRoleSeq());
        assertEquals(participationSubjectClassCd, dto.getParticipationSubjectClassCd());
        assertEquals(participationSubjectEntityCd, dto.getParticipationSubjectEntityCd());
        assertEquals(participationTypeCd, dto.getParticipationTypeCd());
        assertEquals(participationTypeDescTxt, dto.getParticipationTypeDescTxt());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(entityUid, dto.getEntityUid());
        assertEquals(entityCd, dto.getEntityCd());
        assertEquals(entityCdDescTxt, dto.getEntityCdDescTxt());
        assertEquals(entityStandardIndustryClassCd, dto.getEntityStandardIndustryClassCd());
        assertEquals(entityStandardIndustryDescTxt, dto.getEntityStandardIndustryDescTxt());
        assertEquals(entityDisplayNm, dto.getEntityDisplayNm());
        assertEquals(entityElectronicInd, dto.getEntityElectronicInd());
        assertEquals(asOfDate, dto.getAsOfDate());
        assertEquals(rootObservationUid, dto.getRootObservationUid());
        assertEquals(entityIdRootExtensionTxt, dto.getEntityIdRootExtensionTxt());
        assertEquals(entityIdAssigningAuthorityCd, dto.getEntityIdAssigningAuthorityCd());
        assertEquals(entityIdAssigningAuthorityDescTxt, dto.getEntityIdAssigningAuthorityDescTxt());
        assertEquals(entityIdTypeCd, dto.getEntityIdTypeCd());
        assertEquals(entityIdTypeDescTxt, dto.getEntityIdTypeDescTxt());
    }
}
