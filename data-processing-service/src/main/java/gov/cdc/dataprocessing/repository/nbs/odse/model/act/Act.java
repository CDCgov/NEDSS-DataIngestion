package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Act")
@Data
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
