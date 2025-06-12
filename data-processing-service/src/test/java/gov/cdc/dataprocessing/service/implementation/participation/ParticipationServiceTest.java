package gov.cdc.dataprocessing.service.implementation.participation;

import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationHistRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParticipationServiceTest {

    @InjectMocks
    ParticipationService participationService;

    @Mock
    ParticipationRepository participationRepository;
    @Mock
    private DataModifierReposJdbc dataModifierReposJdbc;

    @Mock
    ParticipationHistRepository participationHistRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findPatientMprUidByObservationUid() {
        List<Long> nums = new ArrayList<>();
        nums.add(1L);

        when(participationRepository.findPatientMprUidByObservationUid(any(), any(), any())).thenReturn(Optional.of(nums));

        Long result = participationService.findPatientMprUidByObservationUid("classcode", "typecode", 1L);
        assertEquals(1L, result);
    }

    @Test
    void saveParticipationHist()  {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);

        when(participationHistRepository.findVerNumberByKey(any(), any(), any())).thenReturn(Optional.of(numbers));

        when(participationHistRepository.save(any())).thenReturn(null);

        ParticipationDto participationDto = new ParticipationDto();

        participationService.saveParticipationHist(participationDto);

        verify(participationHistRepository, times(1)).save(any());
    }

    @Test
    void saveParticipation() {
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setItNew(true);
        participationDto.setSubjectEntityUid(1L);
        participationDto.setActUid(2L);

        when(participationRepository.save(any())).thenReturn(null);

        participationService.saveParticipation(participationDto);

        verify(participationRepository, times(1)).save(any());
    }

    @Test
    void saveParticipationDelete() {
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setItDelete(true);
        participationDto.setSubjectEntityUid(1L);
        participationDto.setActUid(2L);

        doNothing().when(dataModifierReposJdbc).deleteParticipationByPk(any(), any(), any());

        participationService.saveParticipation(participationDto);

        verify(dataModifierReposJdbc, times(1)).deleteParticipationByPk(any(), any(), any());
    }

    @Test
    void testSaveParticipationByBatch() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectEntityUid(1L);
        dto.setActUid(2L);
        List<ParticipationDto> toSave = List.of(dto);

        participationService.saveParticipationByBatch(toSave);

        verify(participationRepository).saveAll(anyList());
    }

    @Test
    void testSaveParticipationByBatchWithEmptyList() {
        participationService.saveParticipationByBatch(Collections.emptyList());
        verify(participationRepository, never()).saveAll(anyList());
    }

    @Test
    void testSaveParticipationHistBatch() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectEntityUid(1L);
        dto.setActUid(2L);
        dto.setTypeCd("type");
        when(participationHistRepository.findVerNumberByKey(1L, 2L, "type"))
                .thenReturn(Optional.of(List.of(1, 2)));

        participationService.saveParticipationHistBatch(List.of(dto));

        verify(participationHistRepository).saveAll(anyList());
    }

    @Test
    void testSaveParticipationHistBatchWithEmptyList() {
        participationService.saveParticipationHistBatch(Collections.emptyList());
        verify(participationHistRepository, never()).saveAll(anyList());
    }
}