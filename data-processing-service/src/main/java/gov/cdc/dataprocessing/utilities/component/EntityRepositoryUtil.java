package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.model.classic_model.dto.PersonDT;
import gov.cdc.dataprocessing.repository.nbs.odse.EntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(EntityRepositoryUtil.class);
    private final EntityRepository entityRepository;


    public EntityRepositoryUtil(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public Object preparingEntityReposCallForPerson(PersonDT personDT, List<Object> arrayList, String event) {
        for(int i = 0; i < arrayList.size(); i++) {
            Entity entity;
            if (arrayList.get(i).getClass().toString().equals("class java.lang.String")) {
                entity = new Entity();
            } else {
                if (arrayList.get(i).getClass().toString().equals("class java.sql.Timestamp")) {
                    //TODO: Will get back to this
                }
                else {

                }
            }
        }
    }
}
