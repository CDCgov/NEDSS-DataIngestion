package gov.cdc.dataprocessing.service.implementation.data_extraction;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
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

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j

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
    public LabResultProxyContainer parsingDataToObject(NbsInterfaceModel nbsInterfaceModel, EdxLabInformationDto edxLabInformationDto) throws JAXBException, DataProcessingException {
        int rootObsUid = 0;
        long userId = AuthUtil.authUser.getAuthUserUid();
        Timestamp time = TimeStampUtil.getCurrentTimeStamp(tz);

        edxLabInformationDto.setRootObserbationUid(--rootObsUid);
        edxLabInformationDto.setPatientUid(--rootObsUid);
        edxLabInformationDto.setNextUid(--rootObsUid);
        edxLabInformationDto.setUserId(userId);
        edxLabInformationDto.setAddTime(time);

        // Prepare EDX Document
        EDXDocumentDto edxDocumentDto = new EDXDocumentDto();
        edxDocumentDto.setAddTime(time);
        edxDocumentDto.setDocTypeCd(EdxELRConstant.ELR_DOC_TYPE_CD);
        edxDocumentDto.setPayload(nbsInterfaceModel.getPayload());
        edxDocumentDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        edxDocumentDto.setRecordStatusTime(time);
        edxDocumentDto.setNbsDocumentMetadataUid(EdxELRConstant.ELR_NBS_DOC_META_UID);
        edxDocumentDto.setItDirty(false);
        edxDocumentDto.setItNew(true);

        // Parse XML once with cached JAXBContext
        Container container = dataExtractionServiceUtility.parsingElrXmlPayload(nbsInterfaceModel.getPayload());
        HL7LabReportType hl7LabReportType = container.getHL7LabReport();
        HL7MSHType hl7MSHType = hl7LabReportType.getHL7MSH();

        LabResultProxyContainer labResultProxyContainer = labResultUtil.getLabResultMessage(hl7MSHType, edxLabInformationDto);

        List<HL7PATIENTRESULTType> patientResults = hl7LabReportType.getHL7PATIENTRESULT();
        if (patientResults == null || patientResults.isEmpty()) {
            edxLabInformationDto.setNoSubject(true);
            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
            logger.error("No patient segment found. NBS_INTERFACE_UID: {}", nbsInterfaceModel.getNbsInterfaceUid());
            throw new DataProcessingException(EdxELRConstant.NO_SUBJECT);
        }
        if (patientResults.size() > 1) {
            edxLabInformationDto.setMultipleSubject(true);
            edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
            logger.error("Multiple patient segments found. NBS_INTERFACE_UID: {}", nbsInterfaceModel.getNbsInterfaceUid());
            throw new DataProcessingException(EdxELRConstant.MULTIPLE_SUBJECT);
        }

        HL7PATIENTRESULTType patientResult = patientResults.getFirst();
        labResultProxyContainer = hl7PatientHandler.getPatientAndNextOfKin(patientResult, labResultProxyContainer, edxLabInformationDto);

        List<HL7OrderObservationType> orderObservations = patientResult.getORDEROBSERVATION();
        if (orderObservations == null || orderObservations.isEmpty()) {
            edxLabInformationDto.setOrderTestNameMissing(true);
            logger.error("No OBR segment found. NBS_INTERFACE_UID: {}", nbsInterfaceModel.getNbsInterfaceUid());
            throw new DataProcessingException(EdxELRConstant.NO_ORDTEST_NAME);
        }

        HL7PatientResultSPMType spm = null;
        for (int i = 0; i < orderObservations.size(); i++) {
            HL7OrderObservationType order = orderObservations.get(i);
            HL7OBRType observationRequest = order.getObservationRequest();

            if (order.getCommonOrder() != null) {
                orcHandler.getORCProcessing(order.getCommonOrder(), labResultProxyContainer, edxLabInformationDto);
            }
            if (order.getPatientResultOrderSPMObservation() != null) {
                spm = order.getPatientResultOrderSPMObservation();
            }

            boolean isParentInvalid = i == 0 ?
                    (observationRequest.getParent() != null || observationRequest.getParentResult() != null) :
                    (observationRequest.getParent() == null ||
                            observationRequest.getParent().getHL7FillerAssignedIdentifier() == null ||
                            observationRequest.getParent().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier() == null ||
                            observationRequest.getParent().getHL7FillerAssignedIdentifier().getHL7EntityIdentifier().isBlank() ||
                            observationRequest.getParentResult() == null ||
                            observationRequest.getParentResult().getParentObservationIdentifier() == null ||
                            (observationRequest.getParentResult().getParentObservationIdentifier().getHL7Identifier() == null &&
                                    observationRequest.getParentResult().getParentObservationIdentifier().getHL7AlternateIdentifier() == null) ||
                            (observationRequest.getParentResult().getParentObservationIdentifier().getHL7Text() == null &&
                                    observationRequest.getParentResult().getParentObservationIdentifier().getHL7AlternateText() == null) ||
                            observationRequest.getParentResult().getParentObservationValueDescriptor() == null ||
                            observationRequest.getParentResult().getParentObservationValueDescriptor().getHL7String() == null ||
                            observationRequest.getParentResult().getParentObservationValueDescriptor().getHL7String().isBlank());

            if (isParentInvalid) {
                if (i == 0) {
                    edxLabInformationDto.setOrderOBRWithParent(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    logger.error("Invalid parent section for first OBR. NBS_INTERFACE_UID: {}", nbsInterfaceModel.getNbsInterfaceUid());
                    throw new DataProcessingException(EdxELRConstant.ORDER_OBR_WITH_PARENT);
                } else {
                    edxLabInformationDto.setMultipleOBR(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_13);
                    logger.error("Invalid parent section for OBR {}. NBS_INTERFACE_UID: {}", i + 1, nbsInterfaceModel.getNbsInterfaceUid());
                    throw new DataProcessingException(EdxELRConstant.MULTIPLE_OBR);
                }
            }

            observationRequestHandler.getObservationRequest(observationRequest, spm, labResultProxyContainer, edxLabInformationDto);

            // Async DB update (fire and forget)
            try {
                Timestamp effectiveFromTime = Optional.ofNullable(edxLabInformationDto.getRootObservationContainer())
                        .map(ObservationContainer::getTheObservationDto)
                        .map(ObservationDto::getEffectiveFromTime)
                        .orElse(null);

                if (effectiveFromTime != null) {
                    CompletableFuture.runAsync(() -> {
                        try {
                            nbsInterfaceStoredProcRepository.updateSpecimenCollDateSP(edxLabInformationDto.getNbsInterfaceUid(), effectiveFromTime);
                        } catch (Exception e) {
                            logger.warn("SpecimenCollDate SP failed", e);
                        }
                    });
                }
            } catch (Exception ignored) {}

            observationResultRequestHandler.getObservationResultRequest(
                    order.getPatientResultOrderObservation().getOBSERVATION(),
                    labResultProxyContainer,
                    edxLabInformationDto
            );
        }

        labResultProxyContainer.setEDXDocumentCollection(Collections.singleton(edxDocumentDto));
        return labResultProxyContainer;
    }


}
