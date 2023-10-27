package gov.cdc.dataingestion.registration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDTO {
    private String username;
    private String password;
}
