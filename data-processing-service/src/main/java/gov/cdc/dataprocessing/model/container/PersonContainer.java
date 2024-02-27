package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.LdfBaseVO;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
public class PersonContainer extends LdfBaseVO implements Serializable {
    public PersonDto thePersonDto = new PersonDto();
    public Collection<PersonNameDto> thePersonNameDtoCollection =new ArrayList<>();
    public Collection<PersonRaceDto> thePersonRaceDtoCollection =new ArrayList<>();
    public Collection<PersonEthnicGroupDto> thePersonEthnicGroupDtoCollection =new ArrayList<>();
    public Collection<EntityLocatorParticipationDto> theEntityLocatorParticipationDtoCollection = new ArrayList<>();
    public Collection<EntityIdDto> theEntityIdDtoCollection = new ArrayList<>();


    public Collection<ParticipationDT> theParticipationDTCollection=new ArrayList<>();
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
            e.printStackTrace();
            return null;
        }
    }

}
