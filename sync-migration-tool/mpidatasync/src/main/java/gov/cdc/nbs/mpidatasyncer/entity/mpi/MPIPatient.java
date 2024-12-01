package gov.cdc.nbs.mpidatasyncer.entity.mpi;

import gov.cdc.nbs.mpidatasyncer.model.MPIPatientDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;


@Entity
@Data
@Table(name = "mpi_patient")
@NoArgsConstructor
public class MPIPatient {

  public MPIPatient(MPIPatientDto mpiPatientDto) {
    this.externalPatientId = mpiPatientDto.externalPatientId();
    this.data = mpiPatientDto.jsonData();
    this.referenceId =UUID.randomUUID();
    this.personId =mpiPatientDto.mpiPersonId();
    this.externalPersonId =mpiPatientDto.externalPersonId();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "person_id", nullable = false)
  private Long personId;

  @ColumnTransformer(write = "?::json")
  @Column(name = "data", nullable = false, columnDefinition = "json")
  private String data;

  @Column(name = "external_patient_id", length = 255)
  private String externalPatientId;

  @Column(name = "external_person_id", length = 255)
  private String externalPersonId;

  @Column(name = "external_person_source", length = 100)
  private String externalPersonSource;

  @Column(name = "reference_id", nullable = false)
  private UUID referenceId;
}
