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
@ConditionalOnProperty(name = "sftp.enabled", havingValue = "true")
public class SFTPRouteBuilder extends RouteBuilder {
    private static Logger logger = LoggerFactory.getLogger(SFTPRouteBuilder.class);
    @Value("${sftp.host}")
    private String sftpHost;
    @Value("${sftp.port}")
    private String sftpPort;
    @Value("${sftp.username}")
    private String sftpUserName;
    @Value("${sftp.password}")
    private String sftpPassword;
    @Value("${sftp.directory}")
    private String sftpDirectory;

    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String AUTO_CREATE = "autoCreate";
    private static final String USE_USER_KNOWN_HOSTS_FILE = "useUserKnownHostsFile";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String SFTP = "sftp";
    private static final String ROUTE_FILES_PROCESS_UNPROCESS="file:files/sftpProcessedUnprocessed";
    private static final String ROUTE_FILE_UNZIP_DOWNLOAD="file:files/sftpUnzipDownload";
    private static final String ROUTE_FILE_DOWNLOAD="file:files/sftpdownload";
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

        if (sftpDirectory == null || !sftpDirectory.startsWith("/")) {
            sftpDirectory = "/" + sftpDirectory;
        }

        URI sftpUriBuilder = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(22)
                .setPath(sftpDirectory)
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(PASSWORD, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(PASSIVE_MODE, TRUE)
                .addParameter(INITIAL_DELAY, "2000")
                .addParameter(DELAY, "1000")
                .addParameter(NOOP, TRUE)
                .addParameter(DELETE, TRUE)//check
                .addParameter(LOCAL_WORK_DIRECTORY, "files/download") //check
                .addParameter(RECURSIVE, FALSE)//check
                .addParameter(MAXIMUM_RECONNECT_ATTEMPTS, "5")
                .addParameter(RECONNECT_DELAY, "5000")
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE)
                .build();
        URI sftpUriProcessed = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(22)
                .setPath(sftpDirectory + "processed")
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(PASSWORD, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE)
                .build();
        URI sftpUriUnProcessed = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(22)
                .setPath(sftpDirectory + "unprocessed")
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(PASSWORD, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE)
                .build();

        String sftpServer = sftpUriBuilder.toString();
        logger.info("sftp_server URL: {}", sftpServer);
        //# for the server we want to delay 5 seconds between polling the server
        //# and keep the downloaded file as-is

        logger.info("Calling sftpRouteId");
        //Download the file from sftp server.If the file is zip, it will be downloaded into files/sftpdownload directory.
        //If it's a text file, it will be moved to the folder where the all the non-zip files are downloaded.
        from(sftpServer).routeId("sftpRouteId")
                .log("The file from sftpRouteId: ${file:name}")
                .choice()
                .when(simple("${file:name} endsWith '.zip'"))
                .log(" *****when .zip condition...The file ${file:name}")
                .to(ROUTE_FILE_DOWNLOAD)
                .otherwise()
                .log(" ****Otherwise condition for other files ...The file ${file:name} content from sftp server is ${body}")
                .to(ROUTE_FILE_UNZIP_DOWNLOAD)
                .end();
        // Unzip the downloaded file
        log.info("Calling sftpUnzipFileRouteId");
        from(ROUTE_FILE_DOWNLOAD)
                .routeId("sftpUnzipFileRouteId")
                .split(new ZipSplitter()).streaming()
                .to(ROUTE_FILE_UNZIP_DOWNLOAD)
                .end();

        //Process the files from unzipped folder
        logger.info("Calling sftpdownloadUnzip");
        from(ROUTE_FILE_UNZIP_DOWNLOAD)
                .routeId("SftpReadFromUnzipDirRouteId")
                .log(" Read from unzipped files folder ...The file ${file:name}")
                .to("seda:processfiles", "seda:movefiles")
                .end();
        from("seda:processfiles")
                .routeId("sedaProcessFilesRouteId")
                .log("from seda processfiles file: ${file:name}")
                .choice()
                .when(simple("${file:name} endsWith '.txt'"))
                .log("File processed:${file:name}")
                .to("bean:gov.cdc.dataingestion.camel.routes.HL7FileProcessComponent")
                .otherwise()
                .log("File not processed:${file:name}")
                .endChoice()
                .end();

        from("seda:movefiles")
                .routeId("sedaMoveFilesRouteId")
                .log("from seda movefiles file:${file:name} body: ${body}")
                .to(ROUTE_FILES_PROCESS_UNPROCESS)
                .end();

        from(ROUTE_FILES_PROCESS_UNPROCESS)
                .log("from files sftpProcessedUnprocessed The file ${file:name}")
                .delay(5000)
                .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmssSSS}-${file:name}"))
                .choice()
                .when(simple("${file:name} endsWith '.txt'"))
                .to(sftpUriProcessed.toString())
                .otherwise()
                .to(sftpUriUnProcessed.toString())
                .endChoice()
                .end();
    }
}