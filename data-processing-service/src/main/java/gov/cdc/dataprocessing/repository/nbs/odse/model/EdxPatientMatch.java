package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Table(name = "EDX_patient_match")
@Data
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
}
