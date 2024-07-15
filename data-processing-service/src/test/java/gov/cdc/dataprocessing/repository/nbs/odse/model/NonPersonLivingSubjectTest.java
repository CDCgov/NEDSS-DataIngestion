package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.dto.phc.NonPersonLivingSubjectDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.NonPersonLivingSubject;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NonPersonLivingSubjectTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        NonPersonLivingSubject nonPersonLivingSubject = new NonPersonLivingSubject();

        // Assert
        assertNull(nonPersonLivingSubject.getNonPersonUid());
        assertNull(nonPersonLivingSubject.getAddReasonCd());
        assertNull(nonPersonLivingSubject.getAddTime());
        assertNull(nonPersonLivingSubject.getAddUserId());
        assertNull(nonPersonLivingSubject.getBirthSexCd());
        assertNull(nonPersonLivingSubject.getBirthOrderNbr());
        assertNull(nonPersonLivingSubject.getBirthTime());
        assertNull(nonPersonLivingSubject.getBreedCd());
        assertNull(nonPersonLivingSubject.getBreedDescTxt());
        assertNull(nonPersonLivingSubject.getCd());
        assertNull(nonPersonLivingSubject.getCdDescTxt());
        assertNull(nonPersonLivingSubject.getDeceasedIndCd());
        assertNull(nonPersonLivingSubject.getDeceasedTime());
        assertNull(nonPersonLivingSubject.getDescription());
        assertNull(nonPersonLivingSubject.getLastChgReasonCd());
        assertNull(nonPersonLivingSubject.getLastChgTime());
        assertNull(nonPersonLivingSubject.getLastChgUserId());
        assertNull(nonPersonLivingSubject.getLocalId());
        assertNull(nonPersonLivingSubject.getMultipleBirthInd());
        assertNull(nonPersonLivingSubject.getNm());
        assertNull(nonPersonLivingSubject.getRecordStatusCd());
        assertNull(nonPersonLivingSubject.getRecordStatusTime());
        assertNull(nonPersonLivingSubject.getStatusCd());
        assertNull(nonPersonLivingSubject.getStatusTime());
        assertNull(nonPersonLivingSubject.getTaxonomicClassificationCd());
        assertNull(nonPersonLivingSubject.getTaxonomicClassificationDesc());
        assertNull(nonPersonLivingSubject.getUserAffiliationTxt());
        assertNull(nonPersonLivingSubject.getVersionCtrlNbr());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long nonPersonUid = 1L;
        String addReasonCd = "reason";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String birthSexCd = "M";
        Integer birthOrderNbr = 1;
        Timestamp birthTime = new Timestamp(System.currentTimeMillis());
        String breedCd = "breed";
        String breedDescTxt = "breed description";
        String cd = "code";
        String cdDescTxt = "code description";
        String deceasedIndCd = "N";
        Timestamp deceasedTime = new Timestamp(System.currentTimeMillis());
        String description = "description";
        String lastChgReasonCd = "change reason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String multipleBirthInd = "Y";
        String nm = "name";
        String recordStatusCd = "active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "status";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String taxonomicClassificationCd = "classification";
        String taxonomicClassificationDesc = "classification description";
        String userAffiliationTxt = "affiliation";
        Integer versionCtrlNbr = 1;

        NonPersonLivingSubjectDto dto = new NonPersonLivingSubjectDto();
        dto.setNonPersonUid(nonPersonUid);
        dto.setAddReasonCd(addReasonCd);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setBirthSexCd(birthSexCd);
        dto.setBirthOrderNbr(birthOrderNbr);
        dto.setBirthTime(birthTime);
        dto.setBreedCd(breedCd);
        dto.setBreedDescTxt(breedDescTxt);
        dto.setCd(cd);
        dto.setCdDescTxt(cdDescTxt);
        dto.setDeceasedIndCd(deceasedIndCd);
        dto.setDeceasedTime(deceasedTime);
        dto.setDescription(description);
        dto.setLastChgReasonCd(lastChgReasonCd);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setLocalId(localId);
        dto.setMultipleBirthInd(multipleBirthInd);
        dto.setNm(nm);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setTaxonomicClassificationCd(taxonomicClassificationCd);
        dto.setTaxonomicClassificationDesc(taxonomicClassificationDesc);
        dto.setUserAffiliationTxt(userAffiliationTxt);
        dto.setVersionCtrlNbr(versionCtrlNbr);

        // Act
        NonPersonLivingSubject nonPersonLivingSubject = new NonPersonLivingSubject(dto);

        // Assert
        assertEquals(nonPersonUid, nonPersonLivingSubject.getNonPersonUid());
        assertEquals(addReasonCd, nonPersonLivingSubject.getAddReasonCd());
        assertNotNull(nonPersonLivingSubject.getAddTime());
        assertNotNull( nonPersonLivingSubject.getAddUserId());
        assertEquals(birthSexCd, nonPersonLivingSubject.getBirthSexCd());
        assertEquals(birthOrderNbr, nonPersonLivingSubject.getBirthOrderNbr());
        assertEquals(birthTime, nonPersonLivingSubject.getBirthTime());
        assertEquals(breedCd, nonPersonLivingSubject.getBreedCd());
        assertEquals(breedDescTxt, nonPersonLivingSubject.getBreedDescTxt());
        assertEquals(cd, nonPersonLivingSubject.getCd());
        assertEquals(cdDescTxt, nonPersonLivingSubject.getCdDescTxt());
        assertEquals(deceasedIndCd, nonPersonLivingSubject.getDeceasedIndCd());
        assertEquals(deceasedTime, nonPersonLivingSubject.getDeceasedTime());
        assertEquals(description, nonPersonLivingSubject.getDescription());
        assertEquals(lastChgReasonCd, nonPersonLivingSubject.getLastChgReasonCd());
        assertEquals(lastChgTime, nonPersonLivingSubject.getLastChgTime());
        assertEquals(lastChgUserId, nonPersonLivingSubject.getLastChgUserId());
        assertEquals(localId, nonPersonLivingSubject.getLocalId());
        assertEquals(multipleBirthInd, nonPersonLivingSubject.getMultipleBirthInd());
        assertEquals(nm, nonPersonLivingSubject.getNm());
        assertEquals(recordStatusCd, nonPersonLivingSubject.getRecordStatusCd());
        assertEquals(recordStatusTime, nonPersonLivingSubject.getRecordStatusTime());
        assertEquals(statusCd, nonPersonLivingSubject.getStatusCd());
        assertEquals(statusTime, nonPersonLivingSubject.getStatusTime());
        assertEquals(taxonomicClassificationCd, nonPersonLivingSubject.getTaxonomicClassificationCd());
        assertEquals(taxonomicClassificationDesc, nonPersonLivingSubject.getTaxonomicClassificationDesc());
        assertEquals(userAffiliationTxt, nonPersonLivingSubject.getUserAffiliationTxt());
        assertEquals(versionCtrlNbr, nonPersonLivingSubject.getVersionCtrlNbr());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        NonPersonLivingSubject nonPersonLivingSubject = new NonPersonLivingSubject();

        Long nonPersonUid = 1L;
        String addReasonCd = "reason";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String birthSexCd = "M";
        Integer birthOrderNbr = 1;
        Timestamp birthTime = new Timestamp(System.currentTimeMillis());
        String breedCd = "breed";
        String breedDescTxt = "breed description";
        String cd = "code";
        String cdDescTxt = "code description";
        String deceasedIndCd = "N";
        Timestamp deceasedTime = new Timestamp(System.currentTimeMillis());
        String description = "description";
        String lastChgReasonCd = "change reason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String multipleBirthInd = "Y";
        String nm = "name";
        String recordStatusCd = "active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "status";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String taxonomicClassificationCd = "classification";
        String taxonomicClassificationDesc = "classification description";
        String userAffiliationTxt = "affiliation";
        Integer versionCtrlNbr = 1;

        // Act
        nonPersonLivingSubject.setNonPersonUid(nonPersonUid);
        nonPersonLivingSubject.setAddReasonCd(addReasonCd);
        nonPersonLivingSubject.setAddTime(addTime);
        nonPersonLivingSubject.setAddUserId(addUserId);
        nonPersonLivingSubject.setBirthSexCd(birthSexCd);
        nonPersonLivingSubject.setBirthOrderNbr(birthOrderNbr);
        nonPersonLivingSubject.setBirthTime(birthTime);
        nonPersonLivingSubject.setBreedCd(breedCd);
        nonPersonLivingSubject.setBreedDescTxt(breedDescTxt);
        nonPersonLivingSubject.setCd(cd);
        nonPersonLivingSubject.setCdDescTxt(cdDescTxt);
        nonPersonLivingSubject.setDeceasedIndCd(deceasedIndCd);
        nonPersonLivingSubject.setDeceasedTime(deceasedTime);
        nonPersonLivingSubject.setDescription(description);
        nonPersonLivingSubject.setLastChgReasonCd(lastChgReasonCd);
        nonPersonLivingSubject.setLastChgTime(lastChgTime);
        nonPersonLivingSubject.setLastChgUserId(lastChgUserId);
        nonPersonLivingSubject.setLocalId(localId);
        nonPersonLivingSubject.setMultipleBirthInd(multipleBirthInd);
        nonPersonLivingSubject.setNm(nm);
        nonPersonLivingSubject.setRecordStatusCd(recordStatusCd);
        nonPersonLivingSubject.setRecordStatusTime(recordStatusTime);
        nonPersonLivingSubject.setStatusCd(statusCd);
        nonPersonLivingSubject.setStatusTime(statusTime);
        nonPersonLivingSubject.setTaxonomicClassificationCd(taxonomicClassificationCd);
        nonPersonLivingSubject.setTaxonomicClassificationDesc(taxonomicClassificationDesc);
        nonPersonLivingSubject.setUserAffiliationTxt(userAffiliationTxt);
        nonPersonLivingSubject.setVersionCtrlNbr(versionCtrlNbr);

        // Assert
        assertEquals(nonPersonUid, nonPersonLivingSubject.getNonPersonUid());
        assertEquals(addReasonCd, nonPersonLivingSubject.getAddReasonCd());
        assertEquals(addTime, nonPersonLivingSubject.getAddTime());
        assertEquals(addUserId, nonPersonLivingSubject.getAddUserId());
        assertEquals(birthSexCd, nonPersonLivingSubject.getBirthSexCd());
        assertEquals(birthOrderNbr, nonPersonLivingSubject.getBirthOrderNbr());
        assertEquals(birthTime, nonPersonLivingSubject.getBirthTime());
        assertEquals(breedCd, nonPersonLivingSubject.getBreedCd());
        assertEquals(breedDescTxt, nonPersonLivingSubject.getBreedDescTxt());
        assertEquals(cd, nonPersonLivingSubject.getCd());
        assertEquals(cdDescTxt, nonPersonLivingSubject.getCdDescTxt());
        assertEquals(deceasedIndCd, nonPersonLivingSubject.getDeceasedIndCd());
        assertEquals(deceasedTime, nonPersonLivingSubject.getDeceasedTime());
        assertEquals(description, nonPersonLivingSubject.getDescription());
        assertEquals(lastChgReasonCd, nonPersonLivingSubject.getLastChgReasonCd());
        assertEquals(lastChgTime, nonPersonLivingSubject.getLastChgTime());
        assertEquals(lastChgUserId, nonPersonLivingSubject.getLastChgUserId());
        assertEquals(localId, nonPersonLivingSubject.getLocalId());
        assertEquals(multipleBirthInd, nonPersonLivingSubject.getMultipleBirthInd());
        assertEquals(nm, nonPersonLivingSubject.getNm());
        assertEquals(recordStatusCd, nonPersonLivingSubject.getRecordStatusCd());
        assertEquals(recordStatusTime, nonPersonLivingSubject.getRecordStatusTime());
        assertEquals(statusCd, nonPersonLivingSubject.getStatusCd());
        assertEquals(statusTime, nonPersonLivingSubject.getStatusTime());
        assertEquals(taxonomicClassificationCd, nonPersonLivingSubject.getTaxonomicClassificationCd());
        assertEquals(taxonomicClassificationDesc, nonPersonLivingSubject.getTaxonomicClassificationDesc());
        assertEquals(userAffiliationTxt, nonPersonLivingSubject.getUserAffiliationTxt());
        assertEquals(versionCtrlNbr, nonPersonLivingSubject.getVersionCtrlNbr());
    }
}
