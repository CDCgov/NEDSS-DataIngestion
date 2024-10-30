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
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
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
