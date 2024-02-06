package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.kafka.consumer.KafkaEdxLogConsumer;
import gov.cdc.dataprocessing.model.classic_model.dto.EDXDocumentDT;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.service.interfaces.IDataExtractionService;
import gov.cdc.dataprocessing.utilities.LabResultHandler;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class DataExtractionService implements IDataExtractionService {
    private static final Logger logger = LoggerFactory.getLogger(DataExtractionService.class);

    public DataExtractionService () {

    }

    public LabResultProxyVO parsingDataToObject(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDT edxLabInformationDT) throws DataProcessingConsumerException, JAXBException {
        LabResultProxyVO labResultProxyVO = null;
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
            HL7LabReportType hl7LabReportType = container.getHL7LabReport();
            HL7MSHType hl7MSHType = hl7LabReportType.getHL7MSH();

            /**
             * Paring MSH Value into Object
             *  Sending Facility
             *  Organization
             * */
            labResultProxyVO = LabResultHandler.getLabResultMessage(hl7MSHType, edxLabInformationDT);

            List<HL7PATIENTRESULTType> HL7PatientResultArray = hl7LabReportType.getHL7PATIENTRESULT();
            HL7PatientResultSPMType hl7PatientResultSPMType = null;

            if(HL7PatientResultArray == null){
                edxLabInformationDT.setNoSubject(true);
                edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                logger.error("HL7CommonLabUtil.processELR error thrown as NO patient segment is found.Please check message with NBS_INTERFACE_UID:-"
                        + nbsInterfaceModel.getNbsInterfaceUid());
                throw new DataProcessingException(EdxELRConstant.NO_SUBJECT);
            }
            // ENSURE HL7 Patient Result Array only has 1 record
            else if(HL7PatientResultArray.size() > 1){
                edxLabInformationDT.setMultipleSubject(true);
                edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                logger.error("HL7CommonLabUtil.processELR error thrown as multiple patient segment is found.Please check message with NBS_INTERFACE_UID:-"
                        + nbsInterfaceModel.getNbsInterfaceUid());
                throw new DataProcessingException(EdxELRConstant.MULTIPLE_SUBJECT);
            }

            /**
             * The If Else above ensure there is only Record with Single Patient Result can move forward
             * */
            HL7PATIENTRESULTType hl7PATIENTRESULTType = HL7PatientResultArray.get(0);


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
