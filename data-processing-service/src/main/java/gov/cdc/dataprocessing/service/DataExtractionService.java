package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.model.classic_model.dto.EDXDocumentDT;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.phdc.Container;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.service.interfaces.IDataExtractionService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Service
@Slf4j
public class DataExtractionService implements IDataExtractionService {
    public DataExtractionService () {

    }

    public LabResultProxyVO parsingDataToObject(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDT edxLabInformationDT) throws DataProcessingConsumerException, JAXBException {
        LabResultProxyVO labResultProxyVO = new LabResultProxyVO();
        int rootObsUid = 0;
        Long userId = 123L;
        Timestamp time = new Timestamp(new Date().getTime());
        try {

            edxLabInformationDT.setRootObserbationUid(rootObsUid);
            edxLabInformationDT.setPatientUid(rootObsUid);
            edxLabInformationDT.setNextUid(rootObsUid);
            edxLabInformationDT.setUserId(userId);
            edxLabInformationDT.setAddTime(time);

            // Set Collection of EDXDocumentDT
            Collection<EDXDocumentDT> collectionXmlDoc = new ArrayList<EDXDocumentDT>();
            EDXDocumentDT edxDocumentDT = new EDXDocumentDT();
            edxDocumentDT.setAddTime(time);
            edxDocumentDT.setDocTypeCd(EdxELRConstant.ELR_DOC_TYPE_CD);
            edxDocumentDT.setPayload(nbsInterfaceModel.getPayload());
            edxDocumentDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            edxDocumentDT.setRecordStatusTime(time);
            edxDocumentDT.setNbsDocumentMetadataUid(EdxELRConstant.ELR_NBS_DOC_META_UID);
            edxDocumentDT.setItDirty(false);
            edxDocumentDT.setItNew(true);
            collectionXmlDoc.add(edxDocumentDT);

            Container container = parsingElrXmlPayload(nbsInterfaceModel.getPayload());

            return labResultProxyVO;
        }catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), "Data");
        }
    }

    public Container parsingElrXmlPayload(String xmlPayload) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(Container.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xmlPayload);
        Container obj = (Container) unmarshaller.unmarshal(reader);
        return obj;
    }
}
