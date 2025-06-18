package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.EntityJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EntityRepositoryUtilTest {
    @InjectMocks
    private EntityRepositoryUtil entityRepositoryUtil;

    @Mock
    private EntityJdbcRepository entityRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPreparingEntityReposCallForPersonWithString() {
        PersonDto personDto = new PersonDto();
        Long entityId = 1L;
        String entityValue = "classCode";
        String event = "event";

        EntityODSE result = entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, entityId, entityValue, event);

        assertNotNull(result);
        assertEquals(entityId, result.getEntityUid());
        assertEquals(entityValue, result.getClassCd());
        verify(entityRepository, times(1)).createEntity(any(EntityODSE.class));
    }

    @Test
    void testPreparingEntityReposCallForPersonWithTimestamp() {
        PersonDto personDto = new PersonDto();
        Long entityId = 1L;
        java.sql.Timestamp entityValue = new java.sql.Timestamp(System.currentTimeMillis());
        String event = "event";

        EntityODSE result = entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, entityId, entityValue, event);

        assertNull(result);
        verify(entityRepository, times(0)).createEntity(any(EntityODSE.class));
    }

    @Test
    void testPreparingEntityReposCallForPersonWithSelectEvent() {
        PersonDto personDto = new PersonDto();
        Long entityId = 1L;
        String entityValue = "classCode";
        String event = NEDSSConstant.SELECT;

        EntityODSE result = entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, entityId, entityValue, event);

        assertNotNull(result);
        assertEquals(entityId, result.getEntityUid());
        assertEquals(entityValue, result.getClassCd());
        verify(entityRepository, times(1)).createEntity(any(EntityODSE.class));
    }

    @Test
    void testPreparingEntityReposCallForPersonWithSelectCountEvent() {
        PersonDto personDto = new PersonDto();
        Long entityId = 1L;
        String entityValue = "classCode";
        String event = NEDSSConstant.SELECT_COUNT;

        EntityODSE result = entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, entityId, entityValue, event);

        assertNotNull(result);
        assertEquals(entityId, result.getEntityUid());
        assertEquals(entityValue, result.getClassCd());
        verify(entityRepository, times(1)).createEntity(any(EntityODSE.class));
    }

}
