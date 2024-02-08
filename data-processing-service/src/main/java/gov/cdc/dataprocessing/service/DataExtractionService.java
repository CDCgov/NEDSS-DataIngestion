package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.EDXDocumentDT;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.service.interfaces.IDataExtractionService;
import gov.cdc.dataprocessing.service.interfaces.IMsgOutEStoredProcService;
import gov.cdc.dataprocessing.utilities.LabResultHandler;
import gov.cdc.dataprocessing.utilities.component.HL7PatientHandler;
import gov.cdc.dataprocessing.utilities.component.ORCHandler;
import gov.cdc.dataprocessing.utilities.component.ObservationRequestHandler;
import gov.cdc.dataprocessing.utilities.component.ObservationResultRequestHandler;
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

    private final HL7PatientHandler hl7PatientHandler;
    private final ObservationRequestHandler observationRequestHandler;
    private final ObservationResultRequestHandler observationResultRequestHandler;
    private final IMsgOutEStoredProcService msgOutEStoredProcService;
    private final ORCHandler orcHandler;
    public DataExtractionService (
            HL7PatientHandler hl7PatientHandler,
            ObservationRequestHandler observationRequestHandler,
            ObservationResultRequestHandler observationResultRequestHandler,
            IMsgOutEStoredProcService msgOutEStoredProcService, ORCHandler orcHandler) {
        this.hl7PatientHandler = hl7PatientHandler;
        this.observationRequestHandler = observationRequestHandler;
        this.observationResultRequestHandler = observationResultRequestHandler;
        this.msgOutEStoredProcService = msgOutEStoredProcService;
        this.orcHandler = orcHandler;
    }

    public LabResultProxyVO parsingDataToObject(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDT edxLabInformationDT) throws DataProcessingConsumerException, JAXBException, DataProcessingException {

        LabResultProxyVO labResultProxyVO;
        int rootObsUid = 0;
        long userId = 123L;
        Timestamp time = new Timestamp(new Date().getTime());
        try {

            edxLabInformationDT.setRootObserbationUid(--rootObsUid);
            edxLabInformationDT.setPatientUid(--rootObsUid);
            edxLabInformationDT.setNextUid(--rootObsUid);
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
                logger.error("HL7CommonLabUtil.processELR error thrown as NO patient segment is found.Please check message with NBS_INTERFACE_UID:-" + nbsInterfaceModel.getNbsInterfaceUid());
                throw new DataProcessingException(EdxELRConstant.NO_SUBJECT);
            }
            // ENSURE HL7 Patient Result Array only has 1 record
            else if(HL7PatientResultArray.size() > 1){
                edxLabInformationDT.setMultipleSubject(true);
                edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                logger.error("HL7CommonLabUtil.processELR error thrown as multiple patient segment is found.Please check message with NBS_INTERFACE_UID:-" + nbsInterfaceModel.getNbsInterfaceUid());
                throw new DataProcessingException(EdxELRConstant.MULTIPLE_SUBJECT);
            }

            /**
             * The If Else above ensure there is only Record with Single Patient Result can move forward
             * */
            HL7PATIENTRESULTType hl7PATIENTRESULTType = HL7PatientResultArray.get(0);
            labResultProxyVO = hl7PatientHandler.getPatientAndNextOfKin(hl7PATIENTRESULTType, labResultProxyVO, edxLabInformationDT);

            List<HL7OrderObservationType> hl7OrderObservationArray = hl7PATIENTRESULTType.getORDEROBSERVATION();

            if(hl7OrderObservationArray==null){
                edxLabInformationDT.setOrderTestNameMissing(true);
                logger.error("HL7CommonLabUtil.processELR error thrown as NO OBR segment is found.Please check message with NBS_INTERFACE_UID:-"+ nbsInterfaceModel.getNbsInterfaceUid());
                throw new DataProcessingException(EdxELRConstant.NO_ORDTEST_NAME);
            }

            for (int j = 0; j < hl7OrderObservationArray.size(); j++) {
                HL7OrderObservationType hl7OrderObservationType = hl7OrderObservationArray.get(j);
                if (hl7OrderObservationType.getCommonOrder() != null) {
                    orcHandler.getORCProcessing(hl7OrderObservationType.getCommonOrder(), labResultProxyVO, edxLabInformationDT);
                }
                
                if (hl7OrderObservationType.getPatientResultOrderSPMObservation() != null) {
                    hl7PatientResultSPMType = hl7OrderObservationType.getPatientResultOrderSPMObservation();
                }

                if(
                    j==0 && 
                    (hl7OrderObservationType.getObservationRequest().getParent() != null
                    ||hl7OrderObservationType.getObservationRequest().getParentResult() != null)
                )
                {
                    edxLabInformationDT.setOrderOBRWithParent(true);
                    edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    logger.error("HL7CommonLabUtil.processELR error thrown as either OBR26 is null OR OBR 29 is \"NOT NULL\" for the first OBR section.Please check message with NBS_INTERFACE_UID:-"+ nbsInterfaceModel.getNbsInterfaceUid());
                    throw new DataProcessingException(EdxELRConstant.ORDER_OBR_WITH_PARENT);

                }
                else if(
                        j>0 && (hl7OrderObservationType.getObservationRequest().getParent()==null
                        || hl7OrderObservationType.getObservationRequest().getParentResult()==null
                        || hl7OrderObservationType.getObservationRequest().getParentResult().getParentObservationValueDescriptor()== null
                        || hl7OrderObservationType.getObservationRequest().getParentResult().getParentObservationValueDescriptor().getHL7String() == null
                        || hl7OrderObservationType.getObservationRequest().getParentResult().getParentObservationValueDescriptor().getHL7String().trim().equals("")
                        || hl7OrderObservationType.getObservationRequest().getParent().getHL7FillerAssignedIdentifier()==null
                        || hl7OrderObservationType.getObservationRequest().getParent().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier()==null
                        || hl7OrderObservationType.getObservationRequest().getParent().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier().trim().equals("")
                        || hl7OrderObservationType.getObservationRequest().getParentResult().getParentObservationIdentifier()==null
                        || (hl7OrderObservationType.getObservationRequest().getParentResult().getParentObservationIdentifier().getHL7Identifier()==null
                        && hl7OrderObservationType.getObservationRequest().getParentResult().getParentObservationIdentifier().getHL7AlternateIdentifier()==null)
                        || (hl7OrderObservationType.getObservationRequest().getParentResult().getParentObservationIdentifier().getHL7Text()==null
                        && hl7OrderObservationType.getObservationRequest().getParentResult().getParentObservationIdentifier().getHL7AlternateText()==null))
                )
                {

                    edxLabInformationDT.setMultipleOBR(true);
                    logger.error("HL7CommonLabUtil.processELR error thrown as either OBR26 is null OR OBR 29 is null for the OBR "+(j+1)+".Please check message with NBS_INTERFACE_UID:-"+ nbsInterfaceModel.getNbsInterfaceUid());
                    edxLabInformationDT.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.MULTIPLE_OBR);
                }

                observationRequestHandler.getObservationRequest(hl7OrderObservationType.getObservationRequest(), hl7PatientResultSPMType, labResultProxyVO, edxLabInformationDT);

                if(
                    edxLabInformationDT.getRootObservationVO()!=null
                    && edxLabInformationDT.getRootObservationVO().getTheObservationDT()!=null
                    && edxLabInformationDT.getRootObservationVO().getTheObservationDT().getEffectiveFromTime()!=null
                )
                {
                    //TODO: LOGIC TO UPDATE   nbsInterfaceDAOImpl.updateNBSInterfaceRecord(edxLabInformationDT);
                    System.out.println("UPDATE");
                    //msgOutEStoredProcService.callUpdateSpecimenCollDateSP(edxLabInformationDT);
                }

                //TODO: Check this one
                observationResultRequestHandler.getObservationResultRequest(hl7OrderObservationType.getPatientResultOrderObservation().getOBSERVATION(),
                        labResultProxyVO, edxLabInformationDT);

            }
            labResultProxyVO.setEDXDocumentCollection(collectionXmlDoc);

            return labResultProxyVO;
        }catch (Exception e) {
            logger.error("HL7CommonLabUtil.processELR Exception thrown while parsing XML document. Please checkPlease check message with NBS_INTERFACE_UID:-"+ nbsInterfaceModel.getNbsInterfaceUid(), e);
            throw new DataProcessingException("Exception thrown at HL7CommonLabUtil.processELR:" + e.getMessage());
        }
    }

    public Container parsingElrXmlPayload(String xmlPayload) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(Container.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xmlPayload);
        return (Container) unmarshaller.unmarshal(reader);
    }
}
