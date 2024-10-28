package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class EcrMsgContainerDto {
    private Integer msgContainerUid;
    private String invLocalId;
    private Integer nbsInterfaceUid;
    private String receivingSystem;
    private String ongoingCase;
    private Integer versionCtrNbr;
    private Integer dataMigrationStatus;

}
