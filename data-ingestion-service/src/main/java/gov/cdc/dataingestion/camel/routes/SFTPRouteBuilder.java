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
import java.util.HashSet;
import java.util.Set;

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
    private static final Logger logger = LoggerFactory.getLogger(SFTPRouteBuilder.class);
    @Value("${sftp.host}")
    private String sftpHost;
    @Value("${sftp.username}")
    private String sftpUserName;
    @Value("${sftp.password}")
    private String sftpPassword;
    @Value("${sftp.elr-file-extns}")
    private String hl7FileExtns;
    @Value("${sftp.filepaths}")
    private String sftpFilePaths;

    private static final int SFTP_PORT=22;
    private static final String USER_NAME = "username";
    private static final String CREDENTIAL_PARAMETER = "password";
    private static final String AUTO_CREATE = "autoCreate";
    private static final String USE_USER_KNOWN_HOSTS_FILE = "useUserKnownHostsFile";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String SFTP = "sftp";
    private static final String PASSIVE_MODE="passiveMode";
    private static final String INITIAL_DELAY="initialDelay";
    private static final String DELAY="delay";
    private static final String NOOP="noop";
    private static final String DELETE="delete";
    private static final String LOCAL_WORK_DIRECTORY="localWorkDirectory";
    private static final String RECURSIVE="recursive";
    private static final String MAXIMUM_RECONNECT_ATTEMPTS="maximumReconnectAttempts";
    private static final String RECONNECT_DELAY="reconnectDelay";
    private static final String VAR_VALID_FILE_EXTNS="validFileExtns";
    @Override
    public void configure() throws Exception {
        //shutdown faster in case of in-flight messages stack up
        getContext().getShutdownStrategy().setTimeout(10);

        URIBuilder sftpUriBuilder = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(SFTP_PORT)
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(CREDENTIAL_PARAMETER, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(PASSIVE_MODE, TRUE)
                .addParameter(INITIAL_DELAY, "2000")
                .addParameter(DELAY, "2000")
                .addParameter(NOOP, FALSE)
                .addParameter(DELETE, TRUE)
                .addParameter(LOCAL_WORK_DIRECTORY, "files/download") //check
                .addParameter(RECURSIVE, FALSE)
                .addParameter(MAXIMUM_RECONNECT_ATTEMPTS, "5")
                .addParameter(RECONNECT_DELAY, "5000")
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE);

        URIBuilder sftpProcessed = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(SFTP_PORT)
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(CREDENTIAL_PARAMETER, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE);

        URIBuilder sftpUnProcessed = new URIBuilder()
                .setScheme(SFTP)
                .setHost(sftpHost)
                .setPort(SFTP_PORT)
                .addParameter(USER_NAME, sftpUserName)
                .addParameter(CREDENTIAL_PARAMETER, sftpPassword)
                .addParameter(AUTO_CREATE, TRUE)
                .addParameter(USE_USER_KNOWN_HOSTS_FILE, FALSE);

        String validFileExtns=getValidFileExtns(hl7FileExtns);
        logger.debug("HL7 Valid File Extns: {}", validFileExtns);
        String fileTypeValidationCondition="${file:name.ext.single} in ${variable.validFileExtns} && ${bodyAs(String).trim.length} != '0'";
        //Multiple sftp folders:"/,/ELRFiles,/ELRFiles/lab-1,/ELRFiles/lab-2"
        String[] ftpPaths=sftpFilePaths.split(",");
        int i=0;
        for(String path:ftpPaths){
            i=i+1;
            String sftpPath=path.trim();
            //Main SFTP PATH - Consumer
            sftpUriBuilder.setPath(sftpPath);
            URI sftpUri=sftpUriBuilder.build();
            String sftpServer = sftpUri.toString();
            logger.debug("sftp_server URL: {}", sftpServer);

            //producer routes for processed status folders
            String sftpProcessedUri=sftpProcessed.setPath(sftpPath + "/processed").build().toString();
            String sftpUnProcessedUri=sftpUnProcessed.setPath(sftpPath + "/unprocessed").build().toString();

            String routeTextFileDir="file:files/tempTextFileDir"+i;
            String routeZipfileDir="file:files/tempZipFileDir"+i;
            String routeMoveToUnprocessed="file:files/tempUnProcessedFiles"+i;
            String routeProcessingStatus="seda:updateStatus_"+i;

            //Download the file from sftp server.If the file is zip, it will be moved into files/tempZipFileDir directory.
            //If it's a text file, it will be moved to the folder files/tempTextFileDir, where all the files are temporarily stored.
            from(sftpServer).routeId("sftpRouteId_"+i)
                    .log("The file from sftpRouteId: ${file:name} and file extn: ${file:name.ext.single}")
                    .setVariable(VAR_VALID_FILE_EXTNS).constant(validFileExtns)
                    .choice()
                        .when(simple("${file:name} endsWith '.zip'"))
                            .log("Sftp first route when .zip condition...The file ${file:name}")
                            .to(routeZipfileDir)
                        .when(simple(fileTypeValidationCondition))
                            .log("Sftp first route. File:${file:name}.Moving to the folder that has text files.")
                            .to(routeTextFileDir)
                        .otherwise()
                            .log("Sftp First route, Otherwise-Unsupported file formats. File name: ${file:name}")
                            .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmss}-${file:name}"))
                        .to(sftpUnProcessedUri)
                    .end();
            // Unzip the downloaded file
            from(routeZipfileDir)
                    .routeId("sftpUnzipFileRouteId_"+i)
                    .split(new ZipSplitter()).streaming()
                    .to(routeTextFileDir)
                    .end();

            //Process the files from unzipped folder
            from(routeTextFileDir+"?delete=true")
                    .routeId("sftpReadFromTextFileDirRouteId_"+i)
                        .log("Read from a folder that has files extracted from a zip file.The file ${file:name}")
                        .to("seda:processfiles_"+i, "seda:movefiles_"+i)
                    .end();

            from("seda:processfiles_"+i)
                    .routeId("sedaProcessFilesRouteId_"+i)
                    .log("from seda processfiles file: ${file:name}")
                    .setVariable(VAR_VALID_FILE_EXTNS).constant(validFileExtns)
                    .choice()
                        .when(simple(fileTypeValidationCondition))//NOSONAR
                            .log("File processed:${file:name}")
                            .log("Before bean process:${bodyAs(String).trim.length}:")
                            .bean(HL7FileProcessComponent.class)
                            .log("ELR raw id: ${body}")
                            .setBody(simple("${file:name}:${body}"))
                            .to(routeProcessingStatus)
                        .otherwise()
                        .log("File not processed:${file:name}")
                    .endChoice()
                    .end();

            from("seda:movefiles_"+i)
                    .routeId("sedaMoveFilesRouteId_"+i)
                        .log("from seda movefiles file:${file:name}")
                        .to(routeMoveToUnprocessed)
                    .end();

            from(routeMoveToUnprocessed+"?delete=true").routeId("moveToUnProcessRouteId_"+i)
                    .log("From tempUnProcessedFiles folder. The file ${file:name}")
                    .setVariable(VAR_VALID_FILE_EXTNS).constant(validFileExtns)
                    .delay(5000)
                    .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmssSSS}-${file:name}"))
                    .choice()
                        .when(simple(fileTypeValidationCondition)) //NOSONAR
                            .log("processed file:${file:name}")
                        .otherwise()
                            .to(sftpUnProcessedUri)
                    .endChoice()
                    .end();
            //Provide the ELR processing status in the output folder.
            from(routeProcessingStatus)
                    .routeId("sedaStatusRouteId_"+i).delay(5000)
                    .bean(ElrProcessStatusComponent.class)
                    .choice()
                        .when(simple("${bodyAs(String).startsWith('Status: Success')} == 'true'"))
                            .log("When the status is available, create a file in the processed folder")
                            .setBody(simple("${body}"))
                            .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmss}-success-${file:name}"))
                            .to(sftpProcessedUri)
                        .when(simple("${bodyAs(String).startsWith('Status: Failure')} == 'true' || ${bodyAs(String).startsWith('Status: Validation Error')} == 'true'"))
                            .log("When the status is available, create a file in the processed folder")
                            .setBody(simple("${body}"))
                            .setHeader(Exchange.FILE_NAME, simple("${date:now:yyyyMMddHHmmss}-failure-${file:name}"))
                            .to(sftpProcessedUri)
                        .otherwise()
                            .log("Calling the same route until it finds the status.seda:updateStatus----${body}")
                            .to(routeProcessingStatus)
                    .endChoice()
                    .end();
        }
    }

    /**
     * Make a list of file extensions with lowercase and uppercase.And remove the '.' from the file extn.
     * @param envFileExtns ex: txt,.hl7
     * @return ex:txt,TXT,hl7,HL7
     */
    private String getValidFileExtns(String envFileExtns) {
        String fileExtns="";
        Set<String> fileExtnsSet = new HashSet<>();
        if (envFileExtns != null) {
            String[] extns =envFileExtns.split(",");
            for (String envFileExtn : extns) {
                if(envFileExtn.startsWith(".")){
                    envFileExtn=envFileExtn.trim().substring(1);
                }
                fileExtnsSet.add(envFileExtn.trim().toLowerCase());
                fileExtnsSet.add(envFileExtn.trim().toUpperCase());
            }
            fileExtns= String.join(",", fileExtnsSet);
        }
        return fileExtns;
    }
}