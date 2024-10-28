package gov.cdc.dataprocessing.repository.nbs.odse.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@jakarta.persistence.Entity
@Getter
@Setter
@Table(name = "Entity")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
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
public class EntityODSE implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "entity_uid")
    private Long entityUid;

    @Column(name = "class_cd")
    private String classCd;

    // Constructors, getters, and setters
    // You can generate them using your IDE or manually as needed
}
