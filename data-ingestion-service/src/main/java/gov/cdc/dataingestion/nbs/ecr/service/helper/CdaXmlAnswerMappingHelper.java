package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaXmlAnswerMapper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaXmlAnswerMappingHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgXmlAnswerDto;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import gov.cdc.nedss.phdc.cda.POCDMT000040Component3;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class CdaXmlAnswerMappingHelper implements ICdaXmlAnswerMappingHelper {
    public CdaXmlAnswerMapper mapToXmlAnswerTop(EcrSelectedRecord input,
                                                POCDMT000040ClinicalDocument1 clinicalDocument,
                                                int componentCounter) throws EcrCdaXmlException {


        CdaXmlAnswerMapper mapper = new CdaXmlAnswerMapper();
        if(input.getMsgXmlAnswers() != null && !input.getMsgXmlAnswers().isEmpty()) {
            for(int i = 0; i < input.getMsgXmlAnswers().size(); i++) {
                componentCounter++;
                int c = 0;
                if (clinicalDocument.getComponent().getStructuredBody().getComponentArray().length == 0) {
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                } else {
                    c = clinicalDocument.getComponent().getStructuredBody().getComponentArray().length;
                    clinicalDocument.getComponent().getStructuredBody().addNewComponent();
                }
                POCDMT000040Component3 out = clinicalDocument.getComponent().getStructuredBody().getComponentArray(c);
                var mappedData = mapToExtendedData(input.getMsgXmlAnswers().get(i), out);
                clinicalDocument.getComponent().getStructuredBody().setComponentArray(c, mappedData);
            }
        }
        mapper.setClinicalDocument(clinicalDocument);
        mapper.setComponentCounter(componentCounter);
        return mapper;


    }

    private POCDMT000040Component3 mapToExtendedData(EcrMsgXmlAnswerDto in, POCDMT000040Component3 out) throws EcrCdaXmlException {
        try {
            String xmlContent = in.getAnswerXmlTxt();
            if (xmlContent != null && !xmlContent.isEmpty()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(false);
                factory.setValidating(false);

                // OWASP recommended XXE prevention measures
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
                factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

                // Additional OWASP recommendations for secure XML processing
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                factory.setXIncludeAware(false);
                factory.setExpandEntityReferences(false);

                if(xmlContent.contains("sdt:") && xmlContent.contains("xsi:")) {
                    String wrappedXmlContent = "<wrapper xmlns:sdt=\"urn:hl7-org:sdtc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + xmlContent + "</wrapper>";

                    XmlOptions options = new XmlOptions();
                    options.setCompileNoValidation();

                    XmlObject xmlData = XmlObject.Factory.parse(wrappedXmlContent, options);
                    XmlObject parsedXmlData = extractSection(xmlData);
                    out.set(parsedXmlData);
                }
                else if(xmlContent.contains("sdt:")) {
                    String wrappedXmlContent = "<wrapper xmlns:sdt=\"urn:hl7-org:sdtc\">" + xmlContent + "</wrapper>";

                    XmlOptions options = new XmlOptions();
                    options.setCompileNoValidation();

                    XmlObject xmlData = XmlObject.Factory.parse(wrappedXmlContent, options);
                    XmlObject parsedXmlData = extractSection(xmlData);
                    out.set(parsedXmlData);
                }
                else {
                    String wrappedXmlContent = "<wrapper xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + xmlContent + "</wrapper>";
                    XmlOptions options = new XmlOptions();
                    options.setCompileNoValidation();

                    XmlObject xmlData = XmlObject.Factory.parse(wrappedXmlContent, options);
                    XmlObject parsedXmlData = extractSection(xmlData);
                    out.set(parsedXmlData);
                }
            }
            return out;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }

    public XmlObject extractSection(XmlObject xmlObject) {
        XmlCursor cursor = xmlObject.newCursor();

        cursor.toStartDoc();
        cursor.selectPath("./*");
        if (cursor.toFirstChild()) {
            cursor.selectPath("./wrapper");
            XmlObject section = cursor.getObject().copy();
            cursor.dispose();
            return section;
        }
        cursor.dispose();
        return null;
    }

}

