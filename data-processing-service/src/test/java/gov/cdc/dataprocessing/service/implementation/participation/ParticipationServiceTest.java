package gov.cdc.dataprocessing.service.implementation.participation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
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
    void saveParticipationHist() throws DataProcessingException {
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
    void saveParticipation() throws DataProcessingException {
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setItNew(true);
        participationDto.setSubjectEntityUid(1L);
        participationDto.setActUid(2L);

        when(participationRepository.save(any())).thenReturn(null);

        participationService.saveParticipation(participationDto);

        verify(participationRepository, times(1)).save(any());
    }

    @Test
    void saveParticipationDelete() throws DataProcessingException {
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setItDelete(true);
        participationDto.setSubjectEntityUid(1L);
        participationDto.setActUid(2L);

        doNothing().when(dataModifierReposJdbc).deleteParticipationByPk(any(), any(), any());

        participationService.saveParticipation(participationDto);

        verify(dataModifierReposJdbc, times(1)).deleteParticipationByPk(any(), any(), any());
    }
}