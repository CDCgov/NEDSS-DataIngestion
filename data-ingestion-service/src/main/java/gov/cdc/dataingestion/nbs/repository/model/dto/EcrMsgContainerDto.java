package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class EcrMsgContainerDto {
    private Integer msgContainerUid;
    private String invLocalId;
    private Integer nbsInterfaceUid;
    private String receivingSystem;
    private String ongoingCase;
    private Integer versionCtrNbr;
    private Integer dataMigrationStatus;

}
