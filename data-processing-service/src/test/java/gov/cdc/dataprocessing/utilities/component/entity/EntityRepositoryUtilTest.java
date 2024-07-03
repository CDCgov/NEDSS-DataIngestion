package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.entity.EntityRepository;
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
    private EntityRepository entityRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPreparingEntityReposCallForPersonWithString() {
        PersonDto personDto = new PersonDto();
        Long entityId = 1L;
        String entityValue = "classCode";
        String event = "event";

        EntityODSE result = entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, entityId, entityValue, event);

        assertNotNull(result);
        assertEquals(entityId, result.getEntityUid());
        assertEquals(entityValue, result.getClassCd());
        verify(entityRepository, times(1)).save(any(EntityODSE.class));
    }

    @Test
    public void testPreparingEntityReposCallForPersonWithTimestamp() {
        PersonDto personDto = new PersonDto();
        Long entityId = 1L;
        java.sql.Timestamp entityValue = new java.sql.Timestamp(System.currentTimeMillis());
        String event = "event";

        EntityODSE result = entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, entityId, entityValue, event);

        assertNull(result);
        verify(entityRepository, times(0)).save(any(EntityODSE.class));
    }

    @Test
    public void testPreparingEntityReposCallForPersonWithSelectEvent() {
        PersonDto personDto = new PersonDto();
        Long entityId = 1L;
        String entityValue = "classCode";
        String event = NEDSSConstant.SELECT;

        EntityODSE result = entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, entityId, entityValue, event);

        assertNotNull(result);
        assertEquals(entityId, result.getEntityUid());
        assertEquals(entityValue, result.getClassCd());
        verify(entityRepository, times(1)).save(any(EntityODSE.class));
    }

    @Test
    public void testPreparingEntityReposCallForPersonWithSelectCountEvent() {
        PersonDto personDto = new PersonDto();
        Long entityId = 1L;
        String entityValue = "classCode";
        String event = NEDSSConstant.SELECT_COUNT;

        EntityODSE result = entityRepositoryUtil.preparingEntityReposCallForPerson(personDto, entityId, entityValue, event);

        assertNotNull(result);
        assertEquals(entityId, result.getEntityUid());
        assertEquals(entityValue, result.getClassCd());
        verify(entityRepository, times(1)).save(any(EntityODSE.class));
    }

}
