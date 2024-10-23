package gov.cdc.dataprocessing.repository.nbs.odse.model.matching;


import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "EDX_entity_match")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
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
    public EdxEntityMatch(EdxEntityMatchDto edxEntityMatchDto) {
        this.edxEntityMatchUid = edxEntityMatchDto.getEdxEntityMatchUid();
        this.entityUid = edxEntityMatchDto.getEntityUid();
        this.matchString = edxEntityMatchDto.getMatchString();
        this.typeCd = edxEntityMatchDto.getTypeCd();
        this.matchStringHashcode = edxEntityMatchDto.getMatchStringHashCode();
    }


}
