package gov.cdc.dataingestion.ecr.cdaMapping;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.service.helper.CdaMapHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.CdaPatientMappingHelper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaMapHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import org.apache.xmlbeans.XmlException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static gov.cdc.dataingestion.ecr.cdaMapping.helper.TestDataInitiation.getTestData;

class CdaMapperPatientTest {

    @Mock
    private ICdaLookUpService cdaLookUpService;
    private ICdaMapHelper cdaMapHelper;
    @InjectMocks
    private CdaPatientMappingHelper target;

    @BeforeEach
    public void setUpEach() {
        MockitoAnnotations.openMocks(this);
        cdaMapHelper = new CdaMapHelper(cdaLookUpService);
        target = new CdaPatientMappingHelper(cdaMapHelper);
    }

    @Test
    void mapToPatientTest() throws XmlException, EcrCdaXmlException {
        EcrSelectedRecord input = getTestData();
        var doc = getPatientDocument();
        int patientComponentCounter = -1;
        String inv168 = "8675309a-7754-r2d2-c3p0-973d9f777777";

        var result = target.mapToPatient(input, doc, patientComponentCounter, inv168);

        Assertions.assertNotNull(result);
    }

    private POCDMT000040ClinicalDocument1 getPatientDocument() throws XmlException {
        String document = "<xml-fragment xmlns:urn=\"urn:hl7-org:v3\">\n" +
                "  <urn:realmCode code=\"US\"/>\n" +
                "  <urn:typeId root=\"2.16.840.1.113883.1.3\" extension=\"POCD_HD000040\"/>\n" +
                "  <urn:id root=\"2.16.840.1.113883.19\" extension=\"8675309a-7754-r2d2-c3p0-973d9f777777\" assigningAuthorityName=\"LR\"/>\n" +
                "  <urn:code code=\"55751-2\" codeSystem=\"2.16.840.1.113883.6.1\" codeSystemName=\"LOINC\" displayName=\"Public Health Case Report - PHRI\"/>\n" +
                "  <urn:title>\n" +
                "    <STRING>Public Health Case Report - Data from Legacy System to CDA</STRING>\n" +
                "  </urn:title>\n" +
                "  <urn:effectiveTime value=\"20231017175832Z\"/>\n" +
                "  <urn:confidentialityCode code=\"N\" codeSystem=\"2.16.840.1.113883.5.25\"/>\n" +
                "  <urn:setId extension=\"ONGOING_CASE\" displayable=\"true\"/>\n" +
                "  <urn:versionNumber value=\"2\"/>\n" +
                "  <urn:recordTarget>\n" +
                "    <urn:patientRole/>\n" +
                "  </urn:recordTarget>\n" +
                "</xml-fragment>";

        POCDMT000040ClinicalDocument1 doc = POCDMT000040ClinicalDocument1.Factory.parse(document);
        return doc;
    }
}
