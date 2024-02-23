package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipSplitter;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@ConditionalOnProperty(name = "sftp.enabled", havingValue = "true")
public class SFTPRouteBuilder extends RouteBuilder {
    private static Logger log = LoggerFactory.getLogger(SFTPRouteBuilder.class);
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

    @Override
    public void configure() throws Exception {
        //shutdown faster in case of in-flight messages stack up
        getContext().getShutdownStrategy().setTimeout(10);

        if (sftpDirectory == null || !sftpDirectory.startsWith("/")) {
            sftpDirectory = "/" + sftpDirectory;
        }

        URI sftpUriBuilder = new URIBuilder()
                .setScheme("sftp")
                .setHost(sftpHost)
                .setPort(22)
                .setPath(sftpDirectory)
                .addParameter("username", sftpUserName)
                .addParameter("password", sftpPassword)
                .addParameter("autoCreate", "true")
                .addParameter("passiveMode", "true")
                .addParameter("initialDelay", "2000")
                .addParameter("delay", "1000")
                .addParameter("noop", "true")
                .addParameter("delete", "true")//check
                .addParameter("localWorkDirectory", "files/download") //check
                .addParameter("recursive", "false")//check
                .addParameter("maximumReconnectAttempts", "5")
                .addParameter("reconnectDelay", "5000")
                .addParameter("useUserKnownHostsFile", "false")
//                .addParameter("excludeExt","bak,da")
//                .addParameter("includeExt","txt,TXT,zip,ZIP")
                .build();
        URI sftpUriProcessed = new URIBuilder()
                .setScheme("sftp")
                .setHost(sftpHost)
                .setPort(22)
                .setPath(sftpDirectory + "processed")
                .addParameter("username", sftpUserName)
                .addParameter("password", sftpPassword)
                .addParameter("autoCreate", "true")
                .addParameter("useUserKnownHostsFile", "false")
                //.addParameter("includeExt","txt,TXT,zip,ZIP")
                .build();
        URI sftpUriUnProcessed = new URIBuilder()
                .setScheme("sftp")
                .setHost(sftpHost)
                .setPort(22)
                .setPath(sftpDirectory + "unprocessed")
                .addParameter("username", sftpUserName)
                .addParameter("password", sftpPassword)
                .addParameter("autoCreate", "true")
                .addParameter("useUserKnownHostsFile", "false")
                //.addParameter("excludeExt","txt,TXT,zip,ZIP")
                .build();
//move
//moveFailed
//preMove
        String sftpServer = sftpUriBuilder.toString();
        log.debug("sftp_server URL:" + sftpServer);
        //# for the server we want to delay 5 seconds between polling the server
        //# and keep the downloaded file as-is

        log.debug("Calling sftpRouteId");
        //Download the file from sftp server.If the file is zip, it will be downloaded into files/sftpdownload directory.
        //If it's a text file, it will be processed.
        from(sftpServer).routeId("sftpRouteId")
                .log("The file from sftpRouteId: ${file:name}")
                .choice()
                .when(simple("${file:name} endsWith '.zip'"))
                .log(" *****when .zip condition...The file ${file:name}")
                .to("file:files/sftpdownload")
                .otherwise()
                .log(" ****Otherwise condition for other files ...The file ${file:name} content from sftp server is ${body}")
                .to("file:files/sftpUnzipDownload")
                .end();
        // Unzip the downloaded file
        log.debug("Calling sftpUnzipFileRouteId");
        from("file:files/sftpdownload")
                .routeId("sftpUnzipFileRouteId")
                .split(new ZipSplitter()).streaming()
                .to("file:files/sftpUnzipDownload")
                .end();

        //Process the files from unzipped folder
        log.debug("Calling sftpdownloadUnzip");
        from("file:files/sftpUnzipDownload")//file:files/sftpUnzipDownload?includeExt=txt
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
                .to("bean:hL7FileProcessComponent")
                .otherwise()
                .log("File not processed:${file:name}")
                .endChoice()
                .end();

        from("seda:movefiles")
                .routeId("sedaMoveFilesRouteId")
                .log("from seda movefiles file:${file:name} body: ${body}")
                .to("file:files/sftpProcessedUnprocessed")
                .end();

        from("file:files/sftpProcessedUnprocessed")
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

    @Bean
    public HL7FileProcessComponent hL7FileProcessComponent() {
        return new HL7FileProcessComponent();
    }
}