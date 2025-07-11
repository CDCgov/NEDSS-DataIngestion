package gov.cdc.dataingestion.reportstatus.service;

import com.google.gson.Gson;
import gov.cdc.dataingestion.deadletter.repository.IElrDeadLetterRepository;
import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.odse.repository.IEdxActivityLogRepository;
import gov.cdc.dataingestion.odse.repository.IEdxActivityParentLogRepository;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLog;
import gov.cdc.dataingestion.report.repository.IRawElrRepository;
import gov.cdc.dataingestion.report.repository.model.RawElrModel;
import gov.cdc.dataingestion.reportstatus.model.DltMessageStatus;
import gov.cdc.dataingestion.reportstatus.model.MessageStatus;
import gov.cdc.dataingestion.reportstatus.model.ReportStatusIdData;
import gov.cdc.dataingestion.reportstatus.repository.IReportStatusRepository;
import gov.cdc.dataingestion.share.helper.TimeStampHelper;
import gov.cdc.dataingestion.validation.repository.IValidatedELRRepository;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.cdc.dataingestion.constant.MessageType.HL7_ELR;

@Service
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class ReportStatusService {
    private final IReportStatusRepository iReportStatusRepository;
    private final IEdxActivityParentLogRepository iEdxActivityParentLogRepository;
    private final NbsInterfaceRepository nbsInterfaceRepository;

    private final IRawElrRepository iRawELRRepository;
    private final IValidatedELRRepository iValidatedELRRepository;
    private final IElrDeadLetterRepository iElrDeadLetterRepository;
    private final IEdxActivityLogRepository iEdxActivityLogRepository;
    private static final String MSG_STATUS_SUCCESS = "COMPLETED";
    private static final String MSG_STATUS_FAILED = "FAILED";
    private static final String MSG_STATUS_PROGRESS = "IN PROGRESS";
    private static final String DLT_ORIGIN_RAW = "RAW";
    private static final String DLT_ORIGIN_VALIDATED = "VALIDATED";

    public ReportStatusService(IReportStatusRepository iReportStatusRepository,
                               IEdxActivityParentLogRepository iEdxActivityParentLogRepository, NbsInterfaceRepository nbsInterfaceRepository,
                               IRawElrRepository iRawELRRepository,
                               IValidatedELRRepository iValidatedELRRepository,
                               IElrDeadLetterRepository iElrDeadLetterRepository,
                               IEdxActivityLogRepository iEdxActivityLogRepository) {
        this.iReportStatusRepository = iReportStatusRepository;
        this.iEdxActivityParentLogRepository = iEdxActivityParentLogRepository;
        this.nbsInterfaceRepository = nbsInterfaceRepository;
        this.iRawELRRepository = iRawELRRepository;
        this.iValidatedELRRepository = iValidatedELRRepository;
        this.iElrDeadLetterRepository = iElrDeadLetterRepository;
        this.iEdxActivityLogRepository=iEdxActivityLogRepository;
    }

    @SuppressWarnings("java:S3776")
    public List<MessageStatus> getMessageStatus(String rawMessageID) {
        List<MessageStatus> msgStatusList=new ArrayList<>();

        Optional<RawElrModel> rawMessageData = iRawELRRepository.findById(rawMessageID);
        if (rawMessageData.isPresent()) {
            MessageStatus msgStatus = new MessageStatus();
            msgStatus.getRawInfo().setRawMessageId(rawMessageData.get().getId());
            msgStatus.getRawInfo().setRawCreatedBy(rawMessageData.get().getCreatedBy());
            msgStatus.getRawInfo().setRawCreatedOn(TimeStampHelper.convertTimestampToString(rawMessageData.get().getCreatedOn()));
            msgStatus.getRawInfo().setRawPipeLineStatus(MSG_STATUS_SUCCESS);

            if (rawMessageData.get().getType().equalsIgnoreCase(HL7_ELR)) {
                Optional<ValidatedELRModel> validatedMessageData = iValidatedELRRepository.findByRawId(msgStatus.getRawInfo().getRawMessageId());
                if (validatedMessageData.isPresent()) {
                    msgStatus.getValidatedInfo().setValidatedMessageId(validatedMessageData.get().getId());
                    msgStatus.getValidatedInfo().setValidatedCreatedOn(TimeStampHelper.convertTimestampToString(validatedMessageData.get().getCreatedOn()));
                    msgStatus.getValidatedInfo().setValidatedPipeLineStatus(MSG_STATUS_SUCCESS);

                    // XML
                    msgStatusList=setDiXmlTransformationInfo(msgStatus);
                }
                else {
                    setDltInfo(rawMessageID, msgStatus, DLT_ORIGIN_RAW);
                    msgStatusList.add(msgStatus);
                }
            } else {
                msgStatusList=setDiXmlTransformationInfo(msgStatus);
            }

            for(MessageStatus msgStatusNew:msgStatusList) {
                if(msgStatusNew.getNbsInfo().getNbsInterfaceStatus() !=null) {
                    EdxActivityLog edxActivityLog = iEdxActivityParentLogRepository.getParentEdxActivity(Long.valueOf(msgStatusNew.getNbsInfo().getNbsInterfaceId()));
                    List<EdxActivityDetailLog> edxActivityStatusList = iEdxActivityLogRepository.getEdxActivityLogDetailsBySourceId(Long.valueOf(msgStatusNew.getNbsInfo().getNbsInterfaceId()));
                    if(!edxActivityStatusList.isEmpty() && edxActivityLog != null) {
                        msgStatusNew.getEdxLogStatus().setEdxActivityLog(edxActivityLog);
                        Set<String> seenComments = new HashSet<>();
                        for(EdxActivityDetailLog edxActivityLogModel:edxActivityStatusList){
                            String logComment = edxActivityLogModel.getLogComment();
                            if (seenComments.add(logComment)) {
                                msgStatusNew.getEdxLogStatus().getEdxActivityDetailLogList().add(edxActivityLogModel);
                            }
                        }
                    }
                }
            }
        }
        return msgStatusList;
    }

    private List<MessageStatus> setDiXmlTransformationInfo(MessageStatus msgStatus) {
        List<MessageStatus> msgStatusList=new ArrayList<>();
        List<ReportStatusIdData> elrStatusIdList = iReportStatusRepository.findByRawMessageId(msgStatus.getRawInfo().getRawMessageId());
        if (!elrStatusIdList.isEmpty()) {
            Gson gson = new Gson();
            for (ReportStatusIdData reportStatusIdData : elrStatusIdList) {
                MessageStatus msgStatusNew = gson.fromJson(gson.toJson(msgStatus), MessageStatus.class);
                msgStatusNew.getNbsInfo().setNbsInterfaceId(reportStatusIdData.getNbsInterfaceUid());
                msgStatusNew.getNbsInfo().setNbsCreatedOn(TimeStampHelper.convertTimestampToString(reportStatusIdData.getCreatedOn()));
                msgStatusNew.getNbsInfo().setNbsInterfacePipeLineStatus(MSG_STATUS_SUCCESS);
                setNbsInfo(msgStatusNew);
                msgStatusList.add(msgStatusNew);
            }
        } else {
            if (msgStatus.getValidatedInfo().getValidatedMessageId() == null) {
                setDltInfo(msgStatus.getRawInfo().getRawMessageId(), msgStatus, DLT_ORIGIN_RAW);
            } else {
                setDltInfo(msgStatus.getValidatedInfo().getValidatedMessageId(), msgStatus, DLT_ORIGIN_VALIDATED);
            }
            msgStatusList.add(msgStatus);
        }
        return msgStatusList;
    }

    private void setNbsInfo(MessageStatus msgStatus) {
        Optional<NbsInterfaceModel> nbsInterfaceModel = nbsInterfaceRepository.findByNbsInterfaceUid(msgStatus.getNbsInfo().getNbsInterfaceId());
        if (nbsInterfaceModel.isPresent()) {
            msgStatus.getNbsInfo().setNbsInterfaceStatus(nbsInterfaceModel.get().getRecordStatusCd());
        } else {
            msgStatus.getNbsInfo().setNbsInterfacePipeLineStatus(MSG_STATUS_PROGRESS);
        }
    }

    private void setDltInfo(String id, MessageStatus msgStatus, String origin) {
        var dlt = iElrDeadLetterRepository.findById(id);
        if (dlt.isPresent()) {
            switch (origin) {
                case DLT_ORIGIN_RAW:
                    msgStatus.getRawInfo().setDltInfo(new DltMessageStatus());
                    msgStatus.getRawInfo().getDltInfo().setDltId(id);
                    msgStatus.getRawInfo().getDltInfo().setDltStatus(dlt.get().getDltStatus());
                    msgStatus.getRawInfo().getDltInfo().setDltCreatedOn(TimeStampHelper.convertTimestampToString(dlt.get().getCreatedOn()));
                    msgStatus.getRawInfo().getDltInfo().setDltOrigin(dlt.get().getErrorMessageSource());
                    msgStatus.getRawInfo().getDltInfo().setDltShortTrace(dlt.get().getErrorStackTraceShort());

                    if (dlt.get().getDltStatus().equals("ERROR")) {
                        msgStatus.getValidatedInfo().setValidatedPipeLineStatus(MSG_STATUS_FAILED);
                    }
                    break;
                case DLT_ORIGIN_VALIDATED:
                    msgStatus.getValidatedInfo().setDltInfo(new DltMessageStatus());
                    msgStatus.getValidatedInfo().getDltInfo().setDltId(id);
                    msgStatus.getValidatedInfo().getDltInfo().setDltStatus(dlt.get().getDltStatus());
                    msgStatus.getValidatedInfo().getDltInfo().setDltCreatedOn(TimeStampHelper.convertTimestampToString(dlt.get().getCreatedOn()));
                    msgStatus.getValidatedInfo().getDltInfo().setDltOrigin(dlt.get().getErrorMessageSource());
                    msgStatus.getValidatedInfo().getDltInfo().setDltShortTrace(dlt.get().getErrorStackTraceShort());

                    if (dlt.get().getDltStatus().equals("ERROR")) {
                        msgStatus.getNbsInfo().setNbsInterfacePipeLineStatus(MSG_STATUS_FAILED);
                    }
                    break;
                default:
                    break;
            }

        } else {
            setPipeLineStatus(msgStatus, origin);
        }
    }

    private void setPipeLineStatus(MessageStatus msgStatus, String origin) {
        switch (origin) {
            case DLT_ORIGIN_RAW:
                msgStatus.getValidatedInfo().setValidatedPipeLineStatus(MSG_STATUS_PROGRESS);
                break;
            case DLT_ORIGIN_VALIDATED:
                msgStatus.getNbsInfo().setNbsInterfacePipeLineStatus(MSG_STATUS_PROGRESS);
                break;
            default:
                break;
        }
    }

    public List<String> getStatusForReport(String id) {
        List<String> statusList=new ArrayList<>();

        List<ReportStatusIdData> elrStatusIdList = iReportStatusRepository.findByRawMessageId(id);
        if(elrStatusIdList.isEmpty()) {
            String status= "Provided UUID is not present in the database. Either provided an invalid UUID or the injected message failed validation.";
            statusList.add(status);
        }
        for(ReportStatusIdData reportStatusIdData: elrStatusIdList) {
            Optional<NbsInterfaceModel> nbsInterfaceModel = nbsInterfaceRepository.findByNbsInterfaceUid(reportStatusIdData.getNbsInterfaceUid());
            if(nbsInterfaceModel.isPresent()) {
                statusList.add("NBS Inerface Id:"+reportStatusIdData.getNbsInterfaceUid()+" Status:"+nbsInterfaceModel.get().getRecordStatusCd());
            }else{
                statusList.add("Couldn't find status for the requested UUID.");
            }
        }
        return statusList;
    }
}