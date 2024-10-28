package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Act")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class Act {
    @Id
    @Column(name = "act_uid")
    private Long actUid;

    @Column(name = "class_cd")
    private String classCode;

    @Column(name = "mood_cd")
    private String moodCode;

    public Act() {

    }
}
