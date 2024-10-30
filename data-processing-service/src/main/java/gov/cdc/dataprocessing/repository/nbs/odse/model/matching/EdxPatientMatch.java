package gov.cdc.dataprocessing.repository.nbs.odse.model.matching;

import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "EDX_patient_match")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public class EdxPatientMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EDX_patient_match_uid")
    private Long edxPatientMatchUid;

    @Column(name = "Patient_uid")
    private Long patientUid;

    @Column(name = "match_string")
    private String matchString;

    @Column(name = "type_cd")
    private String typeCd;

    @Column(name = "match_string_hashcode")
    private Long matchStringHashcode;

    public EdxPatientMatch() {

    }
    public EdxPatientMatch(EdxPatientMatchDto dto) {
        this.patientUid = dto.getPatientUid();
        this.matchString = dto.getMatchString();
        this.typeCd = dto.getTypeCd();
        this.matchStringHashcode = dto.getMatchStringHashCode();
    }
}
