package gov.cdc.dataprocessing.service.implementation.data_extraction;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc.NbsInterfaceStoredProcRepository;
import gov.cdc.dataprocessing.service.interfaces.data_extraction.IDataExtractionService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.HL7PatientHandler;
import gov.cdc.dataprocessing.utilities.component.data_parser.ORCHandler;
import gov.cdc.dataprocessing.utilities.component.data_parser.ObservationRequestHandler;
import gov.cdc.dataprocessing.utilities.component.data_parser.ObservationResultRequestHandler;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.LabResultUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class DataExtractionService implements IDataExtractionService {
    private static final Logger logger = LoggerFactory.getLogger(DataExtractionService.class);

    private final HL7PatientHandler hl7PatientHandler;
    private final ObservationRequestHandler observationRequestHandler;
    private final ObservationResultRequestHandler observationResultRequestHandler;
    private final ORCHandler orcHandler;
    private final LabResultUtil labResultUtil;
    private final NbsInterfaceStoredProcRepository nbsInterfaceStoredProcRepository;
    @Value("${service.timezone}")
    private String tz = "UTC";

    private final DataExtractionServiceUtility dataExtractionServiceUtility;
    public DataExtractionService (
            HL7PatientHandler hl7PatientHandler,
            ObservationRequestHandler observationRequestHandler,
            ObservationResultRequestHandler observationResultRequestHandler,
            ORCHandler orcHandler,
            DataExtractionServiceUtility dataExtractionServiceUtility,
            LabResultUtil labResultUtil, NbsInterfaceStoredProcRepository nbsInterfaceStoredProcRepository) {
        this.hl7PatientHandler = hl7PatientHandler;
        this.dataExtractionServiceUtility = dataExtractionServiceUtility;
        this.observationRequestHandler = observationRequestHandler;
        this.observationResultRequestHandler = observationResultRequestHandler;
        this.orcHandler = orcHandler;
        this.labResultUtil = labResultUtil;
        this.nbsInterfaceStoredProcRepository = nbsInterfaceStoredProcRepository;
    }

    @SuppressWarnings("java:S3776")
    @Transactional
    public LabResultProxyContainer parsingDataToObject(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDto edxLabInformationDto) throws JAXBException, DataProcessingException {

        LabResultProxyContainer labResultProxyContainer;
        int rootObsUid = 0;
        long userId = AuthUtil.authUser.getAuthUserUid();
        var time = TimeStampUtil.getCurrentTimeStamp(tz);
            edxLabInformationDto.setRootObserbationUid(--rootObsUid);
            edxLabInformationDto.setPatientUid(--rootObsUid);
            edxLabInformationDto.setNextUid(--rootObsUid);
            edxLabInformationDto.setUserId(userId);
            edxLabInformationDto.setAddTime(time);

            // Set Collection of EDXDocumentDto
            Collection<EDXDocumentDto> collectionXmlDoc = new ArrayList<>();
            EDXDocumentDto edxDocumentDto = new EDXDocumentDto();
            edxDocumentDto.setAddTime(time);
            edxDocumentDto.setDocTypeCd(EdxELRConstant.ELR_DOC_TYPE_CD);
            edxDocumentDto.setPayload(nbsInterfaceModel.getPayload());
            edxDocumentDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            edxDocumentDto.setRecordStatusTime(time);
            edxDocumentDto.setNbsDocumentMetadataUid(EdxELRConstant.ELR_NBS_DOC_META_UID);
            edxDocumentDto.setItDirty(false);
            edxDocumentDto.setItNew(true);
            collectionXmlDoc.add(edxDocumentDto);

            Container container = dataExtractionServiceUtility.parsingElrXmlPayload(nbsInterfaceModel.getPayload());
            HL7LabReportType hl7LabReportType = container.getHL7LabReport();
            HL7MSHType hl7MSHType = hl7LabReportType.getHL7MSH();


            labResultProxyContainer = labResultUtil.getLabResultMessage(hl7MSHType, edxLabInformationDto);
            List<HL7PATIENTRESULTType> HL7PatientResultArray = hl7LabReportType.getHL7PATIENTRESULT(); // NOSONAR
            HL7PatientResultSPMType hl7PatientResultSPMType = null;

            if(HL7PatientResultArray == null || HL7PatientResultArray.isEmpty()){
                edxLabInformationDto.setNoSubject(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                logger.error("HL7CommonLabUtil.processELR error thrown as NO patient segment is found.Please check message with NBS_INTERFACE_UID:-{}", nbsInterfaceModel.getNbsInterfaceUid());
                throw new DataProcessingException(EdxELRConstant.NO_SUBJECT);
            }
            // ENSURE HL7 Patient Result Array only has 1 record
            else if(HL7PatientResultArray.size() > 1){
                edxLabInformationDto.setMultipleSubject(true);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                logger.error("HL7CommonLabUtil.processELR error thrown as multiple patient segment is found.Please check message with NBS_INTERFACE_UID:-{}", nbsInterfaceModel.getNbsInterfaceUid());
                throw new DataProcessingException(EdxELRConstant.MULTIPLE_SUBJECT);
            }

            /**
             * The If Else above ensure there is only Record with Single Patient Result can move forward
             * */
            HL7PATIENTRESULTType hl7PATIENTRESULTType = HL7PatientResultArray.get(0);
            labResultProxyContainer = hl7PatientHandler.getPatientAndNextOfKin(hl7PATIENTRESULTType, labResultProxyContainer, edxLabInformationDto);

            List<HL7OrderObservationType> hl7OrderObservationArray = hl7PATIENTRESULTType.getORDEROBSERVATION();

            if(hl7OrderObservationArray==null || hl7OrderObservationArray.isEmpty()){
                edxLabInformationDto.setOrderTestNameMissing(true);
                logger.error("HL7CommonLabUtil.processELR error thrown as NO OBR segment is found.Please check message with NBS_INTERFACE_UID:-{}", nbsInterfaceModel.getNbsInterfaceUid());
                throw new DataProcessingException(EdxELRConstant.NO_ORDTEST_NAME);
            }

            for (int j = 0; j < hl7OrderObservationArray.size(); j++) {
                HL7OrderObservationType hl7OrderObservationType = hl7OrderObservationArray.get(j);
                if (hl7OrderObservationType.getCommonOrder() != null) {
                    orcHandler.getORCProcessing(hl7OrderObservationType.getCommonOrder(), labResultProxyContainer, edxLabInformationDto);
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
                    edxLabInformationDto.setOrderOBRWithParent(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    logger.error("HL7CommonLabUtil.processELR error thrown as either OBR26 is null OR OBR 29 is \"NOT NULL\" for the first OBR section.Please check message with NBS_INTERFACE_UID:-{}", nbsInterfaceModel.getNbsInterfaceUid());
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

                    edxLabInformationDto.setMultipleOBR(true);
                    logger.error("HL7CommonLabUtil.processELR error thrown as either OBR26 is null OR OBR 29 is null for the OBR {} .Please check message with NBS_INTERFACE_UID:-{}",(j+1), nbsInterfaceModel.getNbsInterfaceUid());
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    throw new DataProcessingException(EdxELRConstant.MULTIPLE_OBR);
                }

                observationRequestHandler.getObservationRequest(hl7OrderObservationType.getObservationRequest(), hl7PatientResultSPMType, labResultProxyContainer, edxLabInformationDto);

                if(
                    edxLabInformationDto.getRootObservationContainer()!=null
                    && edxLabInformationDto.getRootObservationContainer().getTheObservationDto()!=null
                    && edxLabInformationDto.getRootObservationContainer().getTheObservationDto().getEffectiveFromTime()!=null
                )
                {
                    nbsInterfaceStoredProcRepository.updateSpecimenCollDateSP(edxLabInformationDto.getNbsInterfaceUid(), edxLabInformationDto.getRootObservationContainer().getTheObservationDto().getEffectiveFromTime());
                }

                observationResultRequestHandler.getObservationResultRequest(hl7OrderObservationType.getPatientResultOrderObservation().getOBSERVATION(), labResultProxyContainer, edxLabInformationDto);

            }
            labResultProxyContainer.setEDXDocumentCollection(collectionXmlDoc);

            return labResultProxyContainer;

    }


}
