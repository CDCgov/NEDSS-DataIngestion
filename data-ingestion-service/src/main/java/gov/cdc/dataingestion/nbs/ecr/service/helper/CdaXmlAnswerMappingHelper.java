package gov.cdc.dataingestion.nbs.ecr.service.helper;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.ecr.model.CdaXmlAnswerMapper;
import gov.cdc.dataingestion.nbs.ecr.service.helper.interfaces.ICdaXmlAnswerMappingHelper;
import gov.cdc.dataingestion.nbs.repository.model.dao.EcrSelectedRecord;
import gov.cdc.dataingestion.nbs.repository.model.dto.EcrMsgXmlAnswerDto;
import gov.cdc.nedss.phdc.cda.POCDMT000040ClinicalDocument1;
import gov.cdc.nedss.phdc.cda.POCDMT000040Component3;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
            if (!xmlContent.isEmpty() && xmlContent != null) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(false);
                factory.setValidating(false);

                DocumentBuilder builder = factory.newDocumentBuilder();
                InputStream is = new ByteArrayInputStream(xmlContent.getBytes());
                Document document = builder.parse((is));

                XmlObject xmlData = XmlObject.Factory.parse(document.getDocumentElement());
                out.set(xmlData);
            }
            return out;
        } catch (Exception e) {
            throw new EcrCdaXmlException(e.getMessage());
        }
    }
}
