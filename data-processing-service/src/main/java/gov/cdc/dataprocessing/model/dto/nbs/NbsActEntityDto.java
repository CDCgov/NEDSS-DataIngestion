package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class NbsActEntityDto extends BaseContainer {

    private static final long serialVersionUID = 1L;

    private Long nbsActEntityUid;
    private Timestamp addTime;
    private Long addUserId;
    private Long entityUid;
    private Integer entityVersionCtrlNbr;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String  typeCd;
    private Long actUid;


    public NbsActEntityDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }
    public NbsActEntityDto(NbsActEntity nbsActEntity) {
        this.nbsActEntityUid = nbsActEntity.getNbsActEntityUid();
        this.addTime = nbsActEntity.getAddTime();
        this.addUserId = nbsActEntity.getAddUserId();
        this.entityUid = nbsActEntity.getEntityUid();
        this.entityVersionCtrlNbr = nbsActEntity.getEntityVersionCtrlNbr();
        this.lastChgTime = nbsActEntity.getLastChgTime();
        this.lastChgUserId = nbsActEntity.getLastChgUserId();
        this.recordStatusCd = nbsActEntity.getRecordStatusCd();
        this.recordStatusTime = nbsActEntity.getRecordStatusTime();
        this.typeCd = nbsActEntity.getTypeCd();
        this.actUid = nbsActEntity.getActUid();
    }


}
