package gov.cdc.dataprocessing.repository.nbs.odse.model.phc;

import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Confirmation_method")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
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
