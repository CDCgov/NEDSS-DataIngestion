package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc.ObservationMatchStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationMatchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.LOG_SENT_MESSAGE;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.LAB_REPORT_STR;

@Service

public class ObservationMatchingService implements IObservationMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(ObservationMatchingService.class);

    private  final ObservationMatchStoredProcRepository observationMatchStoredProcRepository;
    private final ObservationRepository observationRepository;

    public ObservationMatchingService(ObservationMatchStoredProcRepository observationMatchStoredProcRepository,
                                      ObservationRepository observationRepository) {
        this.observationMatchStoredProcRepository = observationMatchStoredProcRepository;
        this.observationRepository = observationRepository;
    }

    @SuppressWarnings("java:S3776")
    public ObservationDto checkingMatchingObservation(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        String fillerNumber;

        ObservationContainer observationContainer = edxLabInformationDto.getRootObservationContainer();
        fillerNumber = edxLabInformationDto.getFillerNumber();

        ObservationDto obsDT;

        if (edxLabInformationDto.getRootObservationContainer() != null) {
            obsDT = matchingObservation(edxLabInformationDto);
        } else {
            edxLabInformationDto.setObservationMatch(false);
            logger.error("Error!! masterObsVO not available for fillerNbr: {}", edxLabInformationDto.getFillerNumber());
            return null;
        }

        if (obsDT != null) // find a match is it a correction?
        {
            String msgStatus = observationContainer.getTheObservationDto().getStatusCd();
            String odsStatus = "N";
            if (msgStatus == null || odsStatus == null) //NOSONAR
            {
                edxLabInformationDto.setObservationMatch(false);
                logger.error("Error!! null status cd: msgInObs status= {} odsObs status= {}", msgStatus, odsStatus);
                return null;
            }
            if (
                    (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_NEW)
                            && (msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_NEW)
                            || msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
                            || msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED))
                    )
                            || (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
                            && (msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
                            || msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)
                    )
                    )
                            || (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)
                            && msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)
                    )
            ) {
                if (obsDT.getActivityToTime() != null && obsDT.getActivityToTime().after(edxLabInformationDto.getRootObservationContainer().getTheObservationDto().getActivityToTime())) {
                    edxLabInformationDto.setActivityTimeOutOfSequence(true);
                    edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                    edxLabInformationDto.setLocalId(obsDT.getLocalId());
                    throw new DataProcessingException("An Observation Lab test match was found for Accession # " + fillerNumber + ", but the activity time is out of sequence.");
                }
                // MATCHED
                edxLabInformationDto.setObservationMatch(true);
                return obsDT;
            }
            else if (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)
                    && msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
            ) {
                edxLabInformationDto.setFinalPostCorrected(true);
                edxLabInformationDto.setLocalId(obsDT.getLocalId());
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                throw new DataProcessingException(LAB_REPORT_STR + obsDT.getLocalId() + " was not updated. Final report with Accession # " + fillerNumber + " was sent after a corrected report was received.");
            }
            else if (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED)
                    && msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_NEW)
            ) {
                edxLabInformationDto.setPreliminaryPostFinal(true);
                edxLabInformationDto.setLocalId(obsDT.getLocalId());
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                throw new DataProcessingException(LAB_REPORT_STR + obsDT.getLocalId() + " was not updated. Preliminary report with Accession # " + fillerNumber + " was sent after a final report was received.");
            }
            else if (odsStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED)
                    && msgStatus.equals(EdxELRConstant.ELR_OBS_STATUS_CD_NEW)
            ) {
                edxLabInformationDto.setPreliminaryPostCorrected(true);
                edxLabInformationDto.setLocalId(obsDT.getLocalId());
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                throw new DataProcessingException(LAB_REPORT_STR + obsDT.getLocalId() + " was not updated. Preliminary report with Accession # " + fillerNumber + LOG_SENT_MESSAGE);
            }
            else {
                edxLabInformationDto.setFinalPostCorrected(true);
                edxLabInformationDto.setLocalId(obsDT.getLocalId());
                logger.error(" Error!! Invalid status combination: msgInObs status= {} odsObs status= {}", msgStatus, odsStatus);
                edxLabInformationDto.setErrorText(EdxELRConstant.ELR_MASTER_LOG_ID_14);
                throw new DataProcessingException(LAB_REPORT_STR + obsDT.getLocalId() + " was not updated. Final report with Accession # " + fillerNumber + LOG_SENT_MESSAGE);
            }
        }

        edxLabInformationDto.setObservationMatch(false);
        return null;
    }

    @SuppressWarnings("java:S3776")
    public void processMatchedProxyVO(LabResultProxyContainer labResultProxyVO,
                                      LabResultProxyContainer matchedlabResultProxyVO,
                                      EdxLabInformationDto edxLabInformationDT) {
        Long matchedObservationUid =null;
        ObservationDto matchedObservationDto = null;
        Collection<ObservationContainer> observationCollection = matchedlabResultProxyVO.getTheObservationContainerCollection();
        Iterator<ObservationContainer> it = observationCollection.iterator();
        Collection<ActRelationshipDto> updatedARCollection = new ArrayList<>();
        Collection<ParticipationDto> updatedPartCollection = new ArrayList<>();
        Collection<RoleDto> updatedRoleCollection=new ArrayList<>();

        while (it.hasNext()) {
            ObservationContainer observationContainer = it.next();
            String obsDomainCdSt1 = observationContainer.getTheObservationDto().getObsDomainCdSt1();

            if (obsDomainCdSt1 != null
                && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)
            ) {

                matchedObservationDto = observationContainer.getTheObservationDto();

                //update the order status
                if(edxLabInformationDT.getRootObservationContainer()!=null && edxLabInformationDT.getRootObservationContainer().getTheObservationDto()!=null)
                {
                    observationContainer.getTheObservationDto().setStatusCd(edxLabInformationDT.getRootObservationContainer().getTheObservationDto().getStatusCd());
                }
                observationContainer.setItDirty(true);
                observationContainer.setItNew(false);
                matchedObservationUid= observationContainer.getTheObservationDto().getObservationUid();

            }
            else{
                if(observationContainer.getTheObservationDto().getCtrlCdDisplayForm()!=null
                    && (
                        observationContainer.getTheObservationDto().getCd().equalsIgnoreCase(EdxELRConstant.ELR_LAB_CD)
                        || observationContainer.getTheObservationDto().getCtrlCdDisplayForm().equalsIgnoreCase(EdxELRConstant.ELR_LAB_COMMENT)
                        )
                ){
                    observationContainer.setItDirty(true);
                    continue;
                }
                else
                {
                    observationContainer.setItDelete(true);
                }

                if(labResultProxyVO.getTheObservationContainerCollection()==null){
                    labResultProxyVO.setTheObservationContainerCollection(new ArrayList<>());
                }
                labResultProxyVO.getTheObservationContainerCollection().add(observationContainer);
            }

            for (ActRelationshipDto actRelationshipDto : observationContainer.getTheActRelationshipDtoCollection()) {
                if (actRelationshipDto.getTypeCd() != null
                        && actRelationshipDto.getTypeCd().equals(NEDSSConstant.LAB_REPORT)
                        && actRelationshipDto.getTargetClassCd() != null
                        && actRelationshipDto.getTargetClassCd().equals(NEDSSConstant.CASE)
                ) {
                    edxLabInformationDT.setOriginalAssociatedPHCUid(actRelationshipDto.getTargetActUid());
                }
                if (actRelationshipDto.getTypeCd() != null && actRelationshipDto.getTypeCd().equals(EdxELRConstant.ELR_AR_LAB_COMMENT)) {
                    updatedARCollection.add(actRelationshipDto);
                }
                else {
                    actRelationshipDto.setItDelete(true);
                    updatedARCollection.add(actRelationshipDto);
                }
            }

            for (ParticipationDto participationDto : observationContainer.getTheParticipationDtoCollection()) {
                participationDto.setItDelete(true);
                participationDto.setItDirty(false);
                participationDto.setItNew(false);
                if (participationDto.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_AUTHOR_CD)
                        && participationDto.getCd().equalsIgnoreCase(EdxELRConstant.ELR_SENDING_FACILITY_CD)) {
                    labResultProxyVO.setSendingFacilityUid(participationDto.getSubjectEntityUid());
                }
                updatedPartCollection.add(participationDto);
            }
        }

        updatedARCollection.addAll(labResultProxyVO.getTheActRelationshipDtoCollection());
        labResultProxyVO.setTheActRelationshipDtoCollection(updatedARCollection);
        updatedPartCollection.addAll(labResultProxyVO.getTheParticipationDtoCollection());
        labResultProxyVO.setTheParticipationDtoCollection(updatedPartCollection);

        Collection<RoleDto> rolecoll = new ArrayList<>();
        Long patientUid;
        Collection<PersonContainer> coll = matchedlabResultProxyVO.getThePersonContainerCollection();

        if(coll!=null){
            for (PersonContainer personVO : coll) {
                if (personVO.getThePersonDto() != null
                        && personVO.getThePersonDto().getCdDescTxt() != null
                        && personVO.getThePersonDto().getCdDescTxt().equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)
                )
                {
                    patientUid = personVO.getThePersonDto().getPersonUid();
                    edxLabInformationDT.setPatientUid(patientUid);
                }
                rolecoll.addAll(personVO.getTheRoleDtoCollection());
            }
        }

        Collection<OrganizationContainer> orgColl = matchedlabResultProxyVO.getTheOrganizationContainerCollection();
        if(orgColl!=null){
            for (OrganizationContainer organizationContainer : orgColl) {
                rolecoll.addAll(organizationContainer.getTheRoleDTCollection());
            }
        }

        Collection<MaterialContainer> matColl = matchedlabResultProxyVO.getTheMaterialContainerCollection();
        if(matColl!=null){
            for (MaterialContainer materialContainer : matColl) {
                rolecoll.addAll(materialContainer.getTheRoleDTCollection());
            }
        }

        for (RoleDto roleDT : rolecoll) {
            roleDT.setItDelete(true);
            roleDT.setItDirty(false);
            roleDT.setItNew(false);
            updatedRoleCollection.add(roleDT);
        }
        updatedRoleCollection.addAll(labResultProxyVO.getTheRoleDtoCollection());
        labResultProxyVO.setTheRoleDtoCollection(updatedRoleCollection);

        if(labResultProxyVO.getTheObservationContainerCollection() != null){
            for (ObservationContainer obsVO : labResultProxyVO.getTheObservationContainerCollection()) {
                String obsDomainCdSt1 = obsVO.getTheObservationDto().getObsDomainCdSt1();
                if (obsDomainCdSt1 != null
                    && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)
                        && matchedObservationDto != null
                )
                {
                    obsVO.getTheObservationDto().setObservationUid(matchedObservationUid);
                    obsVO.getTheObservationDto().setVersionCtrlNbr(matchedObservationDto.getVersionCtrlNbr());
                    obsVO.getTheObservationDto().setProgAreaCd(matchedObservationDto.getProgAreaCd());
                    obsVO.getTheObservationDto().setJurisdictionCd(matchedObservationDto.getJurisdictionCd());
                    obsVO.getTheObservationDto().setSharedInd(matchedObservationDto.getSharedInd());
                    obsVO.getTheObservationDto().setLocalId(matchedObservationDto.getLocalId());
                    obsVO.getTheObservationDto().setItDirty(true);
                    obsVO.getTheObservationDto().setItNew(false);

                    obsVO.setItNew(false);
                    obsVO.setItDirty(true);
                    labResultProxyVO.setItDirty(true);
                    labResultProxyVO.setItNew(false);
                    break;
                }

            }
        }

        if(labResultProxyVO.getTheActRelationshipDtoCollection()!=null)
        {
            for (ActRelationshipDto actRelationshipDto : labResultProxyVO.getTheActRelationshipDtoCollection()) {
                if (actRelationshipDto.getTargetActUid().compareTo(edxLabInformationDT.getRootObserbationUid()) == 0
                        && (!actRelationshipDto.getTypeCd().equals(EdxELRConstant.ELR_SUPPORT_CD)
                        && !actRelationshipDto.getTypeCd().equals(EdxELRConstant.ELR_REFER_CD)
                        && !actRelationshipDto.getTypeCd().equals(EdxELRConstant.ELR_COMP_CD)
                        )
                )  {
                    actRelationshipDto.setTargetActUid(matchedObservationUid);
                    break;
                }
            }
        }
    }

    private ObservationDto matchingObservation(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        Long observationUid = observationMatchStoredProcRepository.getMatchedObservation(edxLabInformationDto);
        if (observationUid == null) {
            return null;
        }
        else
        {
            var result = observationRepository.findById(observationUid);
            if (result.isEmpty()) {
                return null;
            }
            else
            {
                ObservationDto observationDto = new ObservationDto(result.get());
                observationDto.setItNew(false);
                observationDto.setItDirty(false);
                observationDto.setItDelete(false);

                return observationDto;
            }
        }
    }
}
