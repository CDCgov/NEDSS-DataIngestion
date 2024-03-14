package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
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
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;

    public NbsActEntityDto() {

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
