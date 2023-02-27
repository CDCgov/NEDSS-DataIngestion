package com.cdceq.nbsadapter.routes;

import  org.springframework.beans.factory.annotation.Autowired;
import 	org.springframework.beans.factory.annotation.Value;
import  org.springframework.stereotype.Component;

import  org.apache.camel.builder.RouteBuilder;

import	lombok.NoArgsConstructor;

import  org.slf4j.Logger;
import  org.slf4j.LoggerFactory;

import  com.cdceq.nbsadapter.exceptions.ValidationException;
import	com.cdceq.nbsadapter.processors.Hl7ToXmlTransformer;
import	com.cdceq.nbsadapter.processors.XmlDataPersister;
import 	com.cdceq.nbsadapter.processors.Hl7DataPersister;

import	com.vault.utils.VaultValuesResolver;

@Component
@NoArgsConstructor
public class LegacyHl7RouteBuilder extends RouteBuilder {
    private static final Logger logger = LoggerFactory.getLogger(LegacyHl7RouteBuilder.class);

	@Value("${reportstream.hl7filesdirurl}")
	private String vaultHl7FilesDirectory;

	@Value("${kafka.outbound.hl7messagesendpoint}")
	private String 	vaultHl7MessagesEndpoint;

	@Value("${kafka.outbound.xmlmessagesendpoint}")
	private String 	vaultXmlMessagesEndpoint;

	@Autowired
	private Hl7ToXmlTransformer hl7ToXmlTransformer;

	@Autowired
	private XmlDataPersister xmlDataPersister;

	@Autowired
	private Hl7DataPersister hl7DataPersister;

    @Override
    public void configure() {
		String hl7FilesDirectoryUrl = VaultValuesResolver.getVaultKeyValue(vaultHl7FilesDirectory);
		String hl7MsgsEndpoint = VaultValuesResolver.getVaultKeyValue(vaultHl7MessagesEndpoint);
		String xmlMsgsEndpoint = VaultValuesResolver.getVaultKeyValue(vaultXmlMessagesEndpoint);

		logger.info("Report stream hl7 files directory = {}", hl7FilesDirectoryUrl);

        onException(ValidationException.class)
        .log("Observed validation exception")
        .markRollbackOnly()
        .useOriginalMessage()
        .logStackTrace(true)        
        .end();

        onException(Exception.class)
        .log("Observed exception")
        .markRollbackOnly()
        .useOriginalMessage()
        .logStackTrace(true)
        .end();

		from(hl7FilesDirectoryUrl)
		.routeId("FilesConsumer.Hl7.Route")
		.setHeader("HL7DATASOURCE", constant("FileAPI"))
		.to("seda:hl7_convert_to_xml", "seda:hl7_send_to_kafka", "seda:hl7_persist_to_mongo_db")
		.end();

		from("seda:process_hl7_payload")
		.routeId("RestAPI.Hl7.Route")
		.log("Hl7: ${body}")
		.setHeader("HL7DATASOURCE", constant("RestAPI"))
		.to("seda:hl7_send_to_kafka", "seda:hl7_persist_to_mongo_db")
		.end();

		from("seda:hl7_send_to_kafka")
		.to(hl7MsgsEndpoint)
		.log("Dispatched to kafka hl7 messages topic")
		.end();

		from("seda:hl7_persist_to_mongo_db")
		.process(hl7DataPersister)
		.log("Persisted hl7 message to mongo db")
		.end();

		from("seda:hl7_convert_to_xml")
		.log("Processing file ${headers.CamelFileName} from file system")
		.process(hl7ToXmlTransformer)
		.to("seda:process_xml_payload")
		.end();

		// entry point from internal or controller
		from("seda:process_xml_payload")
		.log("Xml: ${body}")
		.to("seda:xml_persist_to_nbs_sqlserver_db", "seda:xml_send_to_kafka")
		.end();

		from("seda:xml_persist_to_nbs_sqlserver_db")
		.process(xmlDataPersister)
		.log("Processed file ${headers.CamelFileName}, persisted as xml message to sql server database")
		.end();

		from("seda:xml_send_to_kafka")
		.to(xmlMsgsEndpoint)
		.log("Dispatched to kafka xml messages topic")
		.end();
    }
}