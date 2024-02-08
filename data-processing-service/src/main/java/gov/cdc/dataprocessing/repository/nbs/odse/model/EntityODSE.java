package gov.cdc.dataprocessing.repository.nbs.odse.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@jakarta.persistence.Entity
@Getter
@Setter
@Table(name = "Entity")
public class Entity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "entity_uid")
    private Long entityUid;

    @Column(name = "class_cd")
    private String classCd;

    // Constructors, getters, and setters
    // You can generate them using your IDE or manually as needed
}
