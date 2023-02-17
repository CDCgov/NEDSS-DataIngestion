    package gov.cdc.dataingestion.report.integration.service.convert;

    import ca.uhn.hl7v2.HL7Exception;
    import ca.uhn.hl7v2.model.Message;
    import ca.uhn.hl7v2.model.v251.segment.MSH;
    import ca.uhn.hl7v2.util.Hl7InputStreamMessageIterator;
    import gov.cdc.dataingestion.report.model.ReportInfo;
    import io.github.linuxforhealth.fhir.FHIRContext;
    import io.github.linuxforhealth.hl7.ConverterOptions;
    import io.github.linuxforhealth.hl7.message.HL7MessageEngine;
    import io.github.linuxforhealth.hl7.message.HL7MessageModel;
    import io.github.linuxforhealth.hl7.resource.ResourceReader;
    import lombok.extern.slf4j.Slf4j;
    import org.hl7.fhir.r4.model.Bundle;
    import org.springframework.stereotype.Service;
    import java.io.ByteArrayInputStream;
    import java.nio.charset.StandardCharsets;
    import java.util.ArrayList;

    /**
     * Convert HL7 to FHIR.
     */
    @Service
    @Slf4j
    public class ConvertToFhirService implements IConvertToFhirService {

        /**
         * convertCsvToHl7Service
         */
        public ConvertCsvToHl7Service convertCsvToHl7Service;

        /**
         * Constructor.
         * @param convertCsvToHl7Service  convertCsvToHl7Service
         */
        public ConvertToFhirService(
                final ConvertCsvToHl7Service convertCsvToHl7Service) {
            this.convertCsvToHl7Service = convertCsvToHl7Service;
        }

        /**
         * Converts hl7 to fhir.
         * @param report report details.
         * @return fhir format.
         */
        @Override
        public String execute(final ReportInfo report) {

            ArrayList<Message> hl7messages = new ArrayList<>();
            try {
                var convertedMessage =  this.convertCsvToHl7Service
                        .execute(report.getPayload());
                var streamIterator  = new Hl7InputStreamMessageIterator(
                        new ByteArrayInputStream(
                                convertedMessage.getBytes(
                                        StandardCharsets.UTF_8)));
                streamIterator.forEachRemaining(
                        hl7messages::add
                );
                log.info("hl7messages: {}", hl7messages);
                Message message = hl7messages.get(0);
                log.info("message: {}", message);

                var  messageModel = getHL7MessageModel(hl7messages.get(0));
                HL7MessageEngine messageEngine = this.getMessageEngine();

                Bundle bundle = messageModel
                        .convert(hl7messages.get(0), messageEngine);
                log.info("bundle: {}", bundle);
                return bundle.toString();
            } catch (Exception e) {
                log.info("Unable to convert to HL7:", e);
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Hl7 message model from the message header.
         * @param hl7Message hl7 message
         * @return Hl7 message model.
         */
        private HL7MessageModel getHL7MessageModel(final Message hl7Message)
                throws HL7Exception {
            var messageTemplateType = getMessageTemplateType(hl7Message);
            return ResourceReader.getInstance()
                    .getMessageTemplates().get(messageTemplateType);
        }

        /**
         * Obtain the message type for a given HL7 [message].
         * @return the message type
         */
        String getMessageTemplateType(final Message message)
                throws HL7Exception {
            MSH header = (MSH) message.get("MSH");
            return header.getMessageType().getMsg1_MessageCode().getValue()
                    +
                    "_"
                    +
                    header.getMessageType().getMsg2_TriggerEvent().getValue();
        }

        /**
         * Build a HL7MessageEngine for
         * converting HL7 -> FHIR with provided [options].
         * @return the message engine
         */
        HL7MessageEngine getMessageEngine() {
            ConverterOptions finalOptions = new ConverterOptions.Builder()
                    .withBundleType(Bundle.BundleType.MESSAGE)
                    .withPrettyPrint().build();

            var context = new FHIRContext(
                    finalOptions.isPrettyPrint(),
                    finalOptions.isValidateResource(),
                    finalOptions.getProperties(),
                    finalOptions.getZoneIdText()
            );

            return new HL7MessageEngine(context, finalOptions.getBundleType());
        }
    }