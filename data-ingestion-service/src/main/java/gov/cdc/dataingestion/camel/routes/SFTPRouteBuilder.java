package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@ConditionalOnProperty(name = "sftp.enabled", havingValue = "enabled")
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class SFTPRouteBuilder extends RouteBuilder {
    private static Logger logger = LoggerFactory.getLogger(SFTPRouteBuilder.class);
    @Value("${sftp.host}")
    private String sftpHost;
    @Value("${sftp.username}")
    private String sftpUserName;
    @Value("${sftp.password}")
    private String sftpPassword;

    private String sftpDirectory="/";
    private int sftpPort=22;
    private static final String USER_NAME = "username";
    private static final String CREDENTIAL_PARAMETER = "password";
    private static final String AUTO_CREATE = "autoCreate";
    private static final String USE_USER_KNOWN_HOSTS_FILE = "useUserKnownHostsFile";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String SFTP = "sftp";
    private static final String ROUTE_FILES_PROCESS_UNPROCESS="file:files/sftpProcessedUnprocessed";
    private static final String ROUTE_TEXT_FILE_DIR="file:files/sftpTextFileDir";
    private static final String ROUTE_ZIPFILE_DIR="file:files/sftpZipFileDir";
    private static final String ROUTE_SEDA_UPDATE_STATUS="seda:updateStatus";
    private static final String PASSIVE_MODE="passiveMode";
    private static final String INITIAL_DELAY="initialDelay";
    private static final String DELAY="delay";
    private static final String NOOP="noop";
    private static final String DELETE="delete";
    private static final String LOCAL_WORK_DIRECTORY="localWorkDirectory";
    private static final String RECURSIVE="recursive";
    private static final String MAXIMUM_RECONNECT_ATTEMPTS="maximumReconnectAttempts";
    private static final String RECONNECT_DELAY="reconnectDelay";

    @Override
    public void configure() throws Exception {
        //shutdown faster in case of in-flight messages stack up
        getContext().getShutdownStrategy().setTimeout(10);

        URI sftpUriBuilder = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(sftpPort)
                .setPath(sftpDirectory)
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(CREDENTIAL_PARAMETER, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(PASSIVE_MODE, TRUE)
                .addParameter(INITIAL_DELAY, "2000")
                .addParameter(DELAY, "1000")
                .addParameter(NOOP, FALSE)
                .addParameter(DELETE, TRUE)
                .addParameter(LOCAL_WORK_DIRECTORY, "files/download") //check
                .addParameter(RECURSIVE, FALSE)
                .addParameter(MAXIMUM_RECONNECT_ATTEMPTS, "5")
                .addParameter(RECONNECT_DELAY, "5000")
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE)
                .build();
        URI sftpUriProcessed = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(sftpPort)
                .setPath(sftpDirectory + "processed")
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(CREDENTIAL_PARAMETER, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE)
                .build();
        URI sftpUriUnProcessed = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(sftpPort)
                .setPath(sftpDirectory + "unprocessed")
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(CREDENTIAL_PARAMETER, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE)
                .build();

        String sftpServer = sftpUriBuilder.toString();
        logger.debug("sftp_server URL: {}", sftpServer);

        //Download the file from sftp server.If the file is zip, it will be downloaded into files/sftpZipFileDir directory.
        //If it's a text file, it will be moved to the folder files/sftpTextFileDir, where all the text files are stored temporarily.
        from(sftpServer).routeId("sftpRouteId")
                .log("The file from sftpRouteId: ${file:name}")
                .choice()
                    .when(simple("${file:name} endsWith '.zip'"))
                        .log("Sftp first route when .zip condition...The file ${file:name}")
                        .to(ROUTE_ZIPFILE_DIR)
                    .when(simple("${file:name} endsWith '.txt' && ${bodyAs(String).trim.length} != '0'")) //NOSONAR
                        .log("Sftp first route. File:${file:name}.Moving to the folder has text files.")
                        .to(ROUTE_TEXT_FILE_DIR)
                    .otherwise()
                        .log("Sftp First route, Otherwise condition for non .txt and.zip files.File name: ${file:name}")
                        .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmss}-${file:name}"))
                        .to(sftpUriUnProcessed.toString())
                .end();
        // Unzip the downloaded file
        log.debug("Calling sftpUnzipFileRouteId");
        from(ROUTE_ZIPFILE_DIR)
                .routeId("sftpUnzipFileRouteId")
                .split(new ZipSplitter()).streaming()
                .to(ROUTE_TEXT_FILE_DIR)
                .end();

        //Process the files from unzipped folder
        logger.debug("Calling sftpReadFromTextFileDirRouteId");
        from(ROUTE_TEXT_FILE_DIR)
                .routeId("sftpReadFromTextFileDirRouteId")
                    .log(" Read from text files folder ...The file ${file:name}")
                    .to("seda:processfiles", "seda:movefiles")
                .end();
        from("seda:processfiles")
                .routeId("sedaProcessFilesRouteId")
                .log("from seda processfiles file: ${file:name}")
                .choice()
                    .when(simple("${file:name} endsWith '.txt' && ${bodyAs(String).trim.length} != '0'")) //NOSONAR
                        .log("File processed:${file:name}")
                        .log("Before bean process:${bodyAs(String).trim.length}:")
                        .bean(HL7FileProcessComponent.class)
                        .log("ELR raw id: ${body}")
                        .setBody(simple("${file:name}:${body}"))
                        .to(ROUTE_SEDA_UPDATE_STATUS)
                    .otherwise()
                        .log("File not processed:${file:name}")
                .endChoice()
                .end();

        from("seda:movefiles")
                .routeId("sedaMoveFilesRouteId")
                    .log("from seda movefiles file:${file:name}")
                    .to(ROUTE_FILES_PROCESS_UNPROCESS)
                .end();

        from(ROUTE_FILES_PROCESS_UNPROCESS+"?delete=true")
                .log("From sftpProcessedUnprocessed folder. The file ${file:name}")
                .delay(5000)
                .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmssSSS}-${file:name}"))
                .choice()
                    .when(simple("${file:name} endsWith '.txt' && ${bodyAs(String).trim.length} != '0'")) //NOSONAR
                        .log("processed file:${file:name}")
                    .otherwise()
                        .to(sftpUriUnProcessed.toString())
                .endChoice()
                .end();
        //////Provide the ELR processing status in the output folder.
        from(ROUTE_SEDA_UPDATE_STATUS)
                .routeId("sedaStatusRouteId").delay(3000)
                .log("from seda updateStatus message:${body}")
                .bean(ElrProcessStatusComponent.class)
                .choice()
                    .when(simple("${bodyAs(String)} == 'Success'"))
                        .log("When success status: ${body}")
                        .setBody(simple("${body}"))
                        .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmss}-Success-${file:name}"))
                        .to(sftpUriProcessed.toString())
                    .when(simple("${bodyAs(String).startsWith('Status:')} == 'true'"))
                        .log("When failure status: ${body}")
                        .setBody(simple("${body}"))
                        .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmss}-Failure-${file:name}"))
                        .to(sftpUriProcessed.toString())
                    .otherwise()
                        .log("--calling the same seda:updateStatus----${body}")
                        .to(ROUTE_SEDA_UPDATE_STATUS)
                .endChoice()
                .end();
    }
}