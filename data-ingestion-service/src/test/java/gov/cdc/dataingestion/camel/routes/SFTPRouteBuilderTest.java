package gov.cdc.dataingestion.camel.routes;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;

import static org.apache.camel.builder.AdviceWith.adviceWith;

class SFTPRouteBuilderTest extends CamelTestSupport {

    @MockBean
    private HL7FileProcessComponent hL7FileProcessComponent;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SFTPRouteBuilder();
    }

    @Test
    void testMockEndpoints() throws Exception {
        RouteDefinition sftpRoute = context.getRouteDefinition("sftpRouteId");
        RouteDefinition routeSftpUnzipFile = context.getRouteDefinition("sftpUnzipFileRouteId");
        RouteDefinition routeSftpReadFromUnzipDir = context.getRouteDefinition("SftpReadFromUnzipDirRouteId");
        RouteDefinition routeSedaProcessFiles = context.getRouteDefinition("sedaProcessFilesRouteId");
        adviceWith(
                sftpRoute,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:fromSftpRoute");
                        weaveByToUri("file:files/sftpUnzipDownload").replace().to("mock:result");

                    }
                });
        adviceWith(
                routeSftpUnzipFile,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:sftpUnzipFileRoute");
                        weaveByToUri("file:files/sftpUnzipDownload").replace().to("mock:sftpUnzipFileResult");
                    }
                });
        adviceWith(
                routeSftpReadFromUnzipDir,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:sftpReadFromUnzipDirRoute");
                        weaveByToUri("seda:processfiles").replace().to("mock:sftpReadFromUnzippedFilesResult");
                    }
                });
        adviceWith(
                routeSedaProcessFiles,
                context,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("seda:sedaProcessFilesRoute");
                        weaveAddLast().to("mock:processBodyResult");

                    }
                });
        context.start();

        //For sftpRouteId
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBody("direct:fromSftpRoute", new File("src/test/resources/sftpfiles/HL7file-test.txt"));
        mock.assertIsSatisfied();

        //For sftpUnzipFileRouteId
        MockEndpoint mockSftpUnzipFile = getMockEndpoint("mock:sftpUnzipFileResult");
        mockSftpUnzipFile.expectedMinimumMessageCount(4);
        template.sendBody("direct:sftpUnzipFileRoute", new File("src/test/resources/sftpfiles/hl7testdata.zip"));
        mockSftpUnzipFile.assertIsSatisfied();

        MockEndpoint mockSftpReadFromUnzippedFiles = getMockEndpoint("mock:sftpReadFromUnzippedFilesResult");
        mockSftpReadFromUnzippedFiles.expectedMessageCount(1);
        template.sendBody("direct:sftpReadFromUnzipDirRoute", new File("src/test/resources/sftpfiles/HL7file-test.txt"));
        mockSftpReadFromUnzippedFiles.assertIsSatisfied();

        MockEndpoint mockProcessBody = getMockEndpoint("mock:processBodyResult");
        mockProcessBody.expectedMessageCount(2);
        template.sendBody("seda:sedaProcessFilesRoute", new File("src/test/resources/sftpfiles/HL7file-test1.txt"));
        template.sendBody("seda:sedaProcessFilesRoute", new File("src/test/resources/sftpfiles/HL7file-test.txt"));
        mockProcessBody.assertIsSatisfied();

    }
}