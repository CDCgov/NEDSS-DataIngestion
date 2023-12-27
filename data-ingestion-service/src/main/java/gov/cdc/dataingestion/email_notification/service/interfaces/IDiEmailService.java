package gov.cdc.dataingestion.email_notification.service.interfaces;

import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;

public interface IDiEmailService {
     void sendDltEmailNotification(ElrDeadLetterModel dlt);
}
