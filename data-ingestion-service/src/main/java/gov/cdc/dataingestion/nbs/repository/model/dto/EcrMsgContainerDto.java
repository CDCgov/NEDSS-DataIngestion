package gov.cdc.dataingestion.nbs.repository.model.dto;

public record EcrMsgContainerDto(
        Integer msgContainerUid,
        String invLocalId,
        Integer nbsInterfaceUid,
        String receivingSystem,
        String ongoingCase,
        Integer versionCtrlNbr,
        Integer dataMigrationStatus) {
}
