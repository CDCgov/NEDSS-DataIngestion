package gov.cdc.nbs.deduplication.algorithm.pass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.cdc.nbs.deduplication.algorithm.pass.model.BlockingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingAttributeEntry;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.MatchingMethod;
import gov.cdc.nbs.deduplication.algorithm.pass.model.ui.Algorithm.Pass;

@ExtendWith(MockitoExtension.class)
class PassControllerTest {

    @Mock
    private PassService passService;

    @InjectMocks
    private PassController controller;
    private final Pass pass = new Pass(
            null,
            "pass 1",
            "description 1",
            true,
            List.of(BlockingAttribute.ADDRESS),
            List.of(
                    new MatchingAttributeEntry(MatchingAttribute.FIRST_NAME, MatchingMethod.EXACT),
                    new MatchingAttributeEntry(MatchingAttribute.LAST_NAME, MatchingMethod.JAROWINKLER)),
            0.52,
            0.92);

    private final Algorithm algorithm = new Algorithm(List.of(pass));

    @Test
    void should_save_pass() {
        when(passService.save(pass)).thenReturn(algorithm);

        Algorithm actual = controller.save(pass);

        assertThat(actual).isEqualTo(algorithm);
        verify(passService, times(1)).save(pass);
    }

    @Test
    void should_update_pass() {
        when(passService.update(2l, pass)).thenReturn(algorithm);

        Algorithm actual = controller.update(2l, pass);

        assertThat(actual).isEqualTo(algorithm);
        verify(passService, times(1)).update(2l, pass);
    }

    @Test
    void should_delete_pass() {
        when(passService.delete(3l)).thenReturn(algorithm);

        Algorithm actual = controller.delete(3l);

        assertThat(actual).isEqualTo(algorithm);
        verify(passService, times(1)).delete(3);
    }
}
