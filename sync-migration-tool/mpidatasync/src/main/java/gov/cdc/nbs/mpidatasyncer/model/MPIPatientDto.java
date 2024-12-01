package gov.cdc.nbs.mpidatasyncer.model;



import java.io.Serializable;

public record MPIPatientDto(Long mpiPersonId,String externalPersonId,String externalPatientId, String jsonData) implements Serializable {
  private static final long serialVersionUID = 1L;
}
