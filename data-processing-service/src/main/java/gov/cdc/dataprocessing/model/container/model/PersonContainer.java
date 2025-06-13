package gov.cdc.dataprocessing.model.container.model;

import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter

public class PersonContainer extends LdfBaseContainer implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(PersonContainer.class); // NOSONAR

    public PersonDto thePersonDto = new PersonDto();
    public Collection<PersonNameDto> thePersonNameDtoCollection =new ArrayList<>();
    public Collection<PersonRaceDto> thePersonRaceDtoCollection =new ArrayList<>();
    public Collection<PersonEthnicGroupDto> thePersonEthnicGroupDtoCollection =new ArrayList<>();
    public Collection<EntityLocatorParticipationDto> theEntityLocatorParticipationDtoCollection = new ArrayList<>();
    public Collection<EntityIdDto> theEntityIdDtoCollection = new ArrayList<>();


    public Collection<ParticipationDto> theParticipationDtoCollection =new ArrayList<>();
    public Collection<RoleDto> theRoleDtoCollection = new ArrayList<>();

    private String defaultJurisdictionCd;
    private boolean isExt = false;
    private boolean isMPRUpdateValid = true;
    private String localIdentifier;
    private String role;
    private String addReasonCode;


    /**
     * NEW VARIABLE
     * */

    private Boolean patientMatchedFound;
    private boolean newPersonCreated = false;


    public PersonContainer deepClone() {
        try {
            // Serialize the object
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);

            // Deserialize the object
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (PersonContainer) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
