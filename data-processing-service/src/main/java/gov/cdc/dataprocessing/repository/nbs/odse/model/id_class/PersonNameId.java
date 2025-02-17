package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class PersonNameId implements Serializable {
    private Long personUid;

    private Integer personNameSeq;

    public PersonNameId() {}

    public PersonNameId(Long personUid, Integer personNameSeq) {
        this.personUid = personUid;
        this.personNameSeq = personNameSeq;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonNameId that = (PersonNameId) o;
        return Objects.equals(personUid, that.personUid) &&
                Objects.equals(personNameSeq, that.personNameSeq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personUid, personNameSeq);
    }


}
