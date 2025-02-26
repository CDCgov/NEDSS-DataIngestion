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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
//    private Boolean isExistingPatient;
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
