package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.classic_model.dto.PersonDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.repository.nbs.odse.PersonRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PatientRepositoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(PatientRepositoryUtil.class);
    private final PersonRepository personRepository;
    private final EntityRepositoryUtil entityRepositoryUtil;

    public PatientRepositoryUtil(
            PersonRepository personRepository,
            EntityRepositoryUtil entityRepositoryUtil) {
        this.personRepository = personRepository;
        this.entityRepositoryUtil = entityRepositoryUtil;
    }

    public Person createPerson(PersonVO personVO) {
        //TODO: Implement unique id generator here
        Long personUid = 212121L;
        String localUid = "Unique Id here";
        ArrayList<Object>  arrayList = new ArrayList<>();
        PersonDT personDT = personVO.getThePersonDT();

        if(personDT.getLocalId() == null || personDT.getLocalId().trim().length() == 0) {
            personDT.setLocalId(localUid);
        }

        if(personDT.getPersonParentUid() == null) {
            personDT.setPersonParentUid(personUid);
        }

        // set new person uid in entity table
        personDT.setPersonUid(personUid);

        arrayList.add(personUid);
        arrayList.add(NEDSSConstant.PERSON);



        //TODO: Create Entitty


        //TODO: Create Person
        Person person = new Person(personDT);

        //TODO: Create Person Name
        //TODO: Create Person Race
        //TODO: Create Person Ethnic
        //TODO: Create EntityID
        //TODO: Create Entity Locator Participation
        //TODO: Create Role
    }
}
