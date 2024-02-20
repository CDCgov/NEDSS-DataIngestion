package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.classic_model.dto.EdxEntityMatchDT;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EDX_entity_match")
@Data
public class EdxEntityMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edx_entity_match_uid")
    private Long edxEntityMatchUid;

    @Column(name = "entity_UID")
    private Long entityUid;

    @Column(name = "match_string")
    private String matchString;

    @Column(name = "type_cd")
    private String typeCd;

    @Column(name = "match_string_hashcode")
    private Long matchStringHashcode;

    public EdxEntityMatch() {

    }
    public EdxEntityMatch(EdxEntityMatchDT edxEntityMatchDT) {
        this.edxEntityMatchUid = edxEntityMatchDT.getEdxEntityMatchUid();
        this.entityUid = edxEntityMatchDT.getEntityUid();
        this.matchString = edxEntityMatchDT.getMatchString();
        this.typeCd = edxEntityMatchDT.getTypeCd();
        this.matchStringHashcode = edxEntityMatchDT.getMatchStringHashCode();
    }


}
