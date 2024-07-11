package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
class ParticipationStoredProcRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private ParticipationStoredProcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInsertParticipation()  {
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setSubjectEntityUid(1L);
        participationDto.setActUid(2L);
        participationDto.setTypeCd("typeCd");
        participationDto.setAddReasonCd("addReasonCd");
        participationDto.setAddTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setAddUserId(3L);
        participationDto.setAwarenessCd("awarenessCd");
        participationDto.setAwarenessDescTxt("awarenessDescTxt");
        participationDto.setCd("cd");
        participationDto.setDurationAmt("durationAmt");
        participationDto.setDurationUnitCd("durationUnitCd");
        participationDto.setFromTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setLastChgReasonCd("lastChgReasonCd");
        participationDto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setLastChgUserId(4L);
        participationDto.setRecordStatusCd("recordStatusCd");
        participationDto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setRoleSeq(5L);
        participationDto.setStatusCd("statusCd");
        participationDto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setToTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setTypeDescTxt("typeDescTxt");
        participationDto.setUserAffiliationTxt("userAffiliationTxt");
        participationDto.setSubjectClassCd("subjectClassCd");
        participationDto.setActClassCd("actClassCd");

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Long.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Timestamp.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Integer.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(storedProcedureQuery);

        repository.insertParticipation(participationDto);

        verify(entityManager).createStoredProcedureQuery("addParticipation_sp");
        verify(storedProcedureQuery).registerStoredProcedureParameter("subject_entity_uid", Long.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("act_uid", Long.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("type_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("add_reason_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("addtime", Timestamp.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("add_user_id", Long.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("awareness_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("awareness_desc_txt", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("duration_amt", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("duration_unit_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("from_time", Timestamp.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("last_chg_reason_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("last_chg_time", Timestamp.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("last_chg_user_id", Long.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("record_status_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("record_status_time", Timestamp.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("role_seq", Integer.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("status_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("status_time", Timestamp.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("to_time", Timestamp.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("type_desc_txt", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("user_affiliation_txt", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("subject_class_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("act_class_cd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).setParameter("subject_entity_uid", participationDto.getSubjectEntityUid());
        verify(storedProcedureQuery).setParameter("act_uid", participationDto.getActUid());
        verify(storedProcedureQuery).setParameter("type_cd", participationDto.getTypeCd());
        verify(storedProcedureQuery).setParameter("add_reason_cd", participationDto.getAddReasonCd());
        verify(storedProcedureQuery).setParameter("addtime", participationDto.getAddTime());
        verify(storedProcedureQuery).setParameter("add_user_id", participationDto.getAddUserId());
        verify(storedProcedureQuery).setParameter("awareness_cd", participationDto.getAwarenessCd());
        verify(storedProcedureQuery).setParameter("awareness_desc_txt", participationDto.getAwarenessDescTxt());
        verify(storedProcedureQuery).setParameter("cd", participationDto.getCd());
        verify(storedProcedureQuery).setParameter("duration_amt", participationDto.getDurationAmt());
        verify(storedProcedureQuery).setParameter("duration_unit_cd", participationDto.getDurationUnitCd());
        verify(storedProcedureQuery).setParameter("from_time", participationDto.getFromTime());
        verify(storedProcedureQuery).setParameter("last_chg_reason_cd", participationDto.getLastChgReasonCd());
        verify(storedProcedureQuery).setParameter("last_chg_time", participationDto.getLastChgTime());
        verify(storedProcedureQuery).setParameter("last_chg_user_id", participationDto.getLastChgUserId());
        verify(storedProcedureQuery).setParameter("record_status_cd", participationDto.getRecordStatusCd());
        verify(storedProcedureQuery).setParameter("record_status_time", participationDto.getRecordStatusTime());
        verify(storedProcedureQuery).setParameter("role_seq", participationDto.getRoleSeq());
        verify(storedProcedureQuery).setParameter("status_cd", participationDto.getStatusCd());
        verify(storedProcedureQuery).setParameter("status_time", participationDto.getStatusTime());
        verify(storedProcedureQuery).setParameter("to_time", participationDto.getToTime());
        verify(storedProcedureQuery).setParameter("type_desc_txt", participationDto.getTypeDescTxt());
        verify(storedProcedureQuery).setParameter("user_affiliation_txt", participationDto.getUserAffiliationTxt());
        verify(storedProcedureQuery).setParameter("subject_class_cd", participationDto.getSubjectClassCd());
        verify(storedProcedureQuery).setParameter("act_class_cd", participationDto.getActClassCd());
        verify(storedProcedureQuery).execute();
    }

    @Test
    void testInsertParticipation_ThrowsException() {
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setSubjectEntityUid(1L);
        participationDto.setActUid(2L);
        participationDto.setTypeCd("typeCd");
        participationDto.setAddReasonCd("addReasonCd");
        participationDto.setAddTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setAddUserId(3L);
        participationDto.setAwarenessCd("awarenessCd");
        participationDto.setAwarenessDescTxt("awarenessDescTxt");
        participationDto.setCd("cd");
        participationDto.setDurationAmt("durationAmt");
        participationDto.setDurationUnitCd("durationUnitCd");
        participationDto.setFromTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setLastChgReasonCd("lastChgReasonCd");
        participationDto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setLastChgUserId(4L);
        participationDto.setRecordStatusCd("recordStatusCd");
        participationDto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setRoleSeq(5L);
        participationDto.setStatusCd("statusCd");
        participationDto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setToTime(new Timestamp(System.currentTimeMillis()));
        participationDto.setTypeDescTxt("typeDescTxt");
        participationDto.setUserAffiliationTxt("userAffiliationTxt");
        participationDto.setSubjectClassCd("subjectClassCd");
        participationDto.setActClassCd("actClassCd");

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        repository.insertParticipation(participationDto);
    }
}
