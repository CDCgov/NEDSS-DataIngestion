package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.dto.ConfirmationMethodDto;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Confirmation_method")
@Data
public class ConfirmationMethod {
    @Id
    @Column(name = "public_health_case_uid")
    private Long publicHealthCaseUid;

    @Column(name = "confirmation_method_cd")
    private String confirmationMethodCd;

    @Column(name = "confirmation_method_desc_txt")
    private String confirmationMethodDescTxt;

    @Column(name = "confirmation_method_time")
    private Timestamp confirmationMethodTime;

    public ConfirmationMethod() {

    }

    public ConfirmationMethod(ConfirmationMethodDto confirmationMethodDto) {
        this.confirmationMethodCd = confirmationMethodDto.getConfirmationMethodCd();
        this.confirmationMethodDescTxt = confirmationMethodDto.getConfirmationMethodDescTxt();
        this.confirmationMethodTime = confirmationMethodDto.getConfirmationMethodTime();
    }
}
