package gov.cdc.nbs.mpidatasyncer.entity.mpi;

import jakarta.persistence.*;
import lombok.Data;


import java.util.UUID;

@Entity
@Table(name = "mpi_person")
@Data
public class MPIPerson {

  public MPIPerson() {
    referenceId = UUID.randomUUID();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "reference_id", nullable = false)
  private UUID referenceId;


}

