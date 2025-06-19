package gov.cdc.dataingestion.nbs.ecr.resolver;

import gov.cdc.dataingestion.nbs.ecr.resolver.mapper.EcrContainerMapper;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgContainerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcrMsgContainerResolverTest {

    @Mock
    private NamedParameterJdbcTemplate template;

    @InjectMocks
    private EcrMsgContainerResolver resolver;

    @Test
    void shouldQueryForCount() {
        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        List<EcrMsgContainerDto> mockedResults = new ArrayList<>();
        when(template.query(
                Mockito.anyString(),
                captor.capture(),
                Mockito.any(EcrContainerMapper.class)))
                .thenReturn(mockedResults);

        List<EcrMsgContainerDto> results = resolver.resolve(10);
        assertThat(results).isEmpty();
        assertThat(captor.getValue().getValue("count")).isEqualTo(10);
    }
}
