package gov.cdc.dataprocessing.service.implementation.matching;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.MaterialVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.ObservationMatchStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.service.interfaces.matching.IObservationMatchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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


    public void processMatchedProxyVO(LabResultProxyContainer labResultProxyVO,
                                      LabResultProxyContainer matchedlabResultProxyVO,
                                      EdxLabInformationDto edxLabInformationDT) {
        Long matchedObservationUid =null;
        ObservationDT matchedObservationDT = null;
        Collection<ObservationVO> observationCollection = matchedlabResultProxyVO.getTheObservationVOCollection();
        Iterator<ObservationVO> it = observationCollection.iterator();
        Collection<ActRelationshipDT> updatedARCollection = new ArrayList<>();
        Collection<ParticipationDT> updatedPartCollection = new ArrayList<>();
        Collection<RoleDto> updatedRoleCollection=new ArrayList<>();

        while (it.hasNext()) {
            ObservationVO observationVO = it.next();
            String obsDomainCdSt1 = observationVO.getTheObservationDT().getObsDomainCdSt1();

            if (obsDomainCdSt1 != null && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)) {

                matchedObservationDT =observationVO.getTheObservationDT();

                observationVO.getTheObservationDT().getRecordStatusCd();
                observationVO.getTheObservationDT().getRecordStatusTime();
                //update the order status
                if(edxLabInformationDT.getRootObservationVO()!=null && edxLabInformationDT.getRootObservationVO().getTheObservationDT()!=null)
                {
                    observationVO.getTheObservationDT().setStatusCd(edxLabInformationDT.getRootObservationVO().getTheObservationDT().getStatusCd());
                }
                observationVO.setItDirty(true);
                observationVO.setItNew(false);
                matchedObservationUid=observationVO.getTheObservationDT().getObservationUid();

            }
            else{
                if(observationVO.getTheObservationDT().getCtrlCdDisplayForm()!=null
                        && (observationVO.getTheObservationDT().getCd().equalsIgnoreCase(EdxELRConstant.ELR_LAB_CD)
                            || observationVO.getTheObservationDT().getCtrlCdDisplayForm().equalsIgnoreCase(EdxELRConstant.ELR_LAB_COMMENT)
                            )
                ){
                    observationVO.setItDirty(true);
                    continue;
                }
                else
                {
                    observationVO.setItDelete(true);
                }

                if(labResultProxyVO.getTheObservationVOCollection()==null){
                    labResultProxyVO.setTheObservationVOCollection(new ArrayList<>());
                }
                labResultProxyVO.getTheObservationVOCollection().add(observationVO);
            }

            for (ActRelationshipDT actRelationshipDT : observationVO.getTheActRelationshipDTCollection()) {
                if (actRelationshipDT.getTypeCd() != null
                        && actRelationshipDT.getTypeCd().equals(NEDSSConstant.LAB_REPORT)
                        && actRelationshipDT.getTargetClassCd() != null
                        && actRelationshipDT.getTargetClassCd().equals(NEDSSConstant.CASE)
                ) {
                    edxLabInformationDT.setOriginalAssociatedPHCUid(actRelationshipDT.getTargetActUid());
                }
                if (actRelationshipDT.getTypeCd().equals(EdxELRConstant.ELR_AR_LAB_COMMENT)) {
                    updatedARCollection.add(actRelationshipDT);
                }
                else {
                    actRelationshipDT.setItDelete(true);
                    updatedARCollection.add(actRelationshipDT);
                }
            }

            for (ParticipationDT participationDT : observationVO.getTheParticipationDTCollection()) {
                participationDT.setItDelete(true);
                participationDT.setItDirty(false);
                participationDT.setItNew(false);
                if (participationDT.getTypeCd().equalsIgnoreCase(EdxELRConstant.ELR_AUTHOR_CD)
                        && participationDT.getCd().equalsIgnoreCase(EdxELRConstant.ELR_SENDING_FACILITY_CD)) {
                    labResultProxyVO.setSendingFacilityUid(participationDT.getSubjectEntityUid());
                }
                updatedPartCollection.add(participationDT);
            }
        }

        updatedARCollection.addAll(labResultProxyVO.getTheActRelationshipDTCollection());
        labResultProxyVO.setTheActRelationshipDTCollection(updatedARCollection);
        updatedPartCollection.addAll(labResultProxyVO.getTheParticipationDTCollection());
        labResultProxyVO.setTheParticipationDTCollection(updatedPartCollection);


        Collection<RoleDto> rolecoll = new ArrayList<>();
        Long patientUid = null;
        Collection<PersonContainer> coll = matchedlabResultProxyVO.getThePersonContainerCollection();

        if(coll!=null){
            for (PersonContainer personVO : coll) {
                if (personVO.getThePersonDto() != null && personVO.getThePersonDto().getCdDescTxt() != null
                        && personVO.getThePersonDto().getCdDescTxt().equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_DESC)
                )
                {
                    patientUid = personVO.getThePersonDto().getPersonUid();
                    edxLabInformationDT.setPatientUid(patientUid);
                }
                rolecoll.addAll(personVO.getTheRoleDtoCollection());
            }
        }

        Collection<OrganizationVO> orgColl = matchedlabResultProxyVO.getTheOrganizationVOCollection();
        if(orgColl!=null){
            for (OrganizationVO organizationVO : orgColl) {
                rolecoll.addAll(organizationVO.getTheRoleDTCollection());
            }
        }

        Collection<MaterialVO> matColl = matchedlabResultProxyVO.getTheMaterialVOCollection();
        if(matColl!=null){
            for (MaterialVO materialVO : matColl) {
                rolecoll.addAll(materialVO.getTheRoleDTCollection());
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

        if(labResultProxyVO.getTheObservationVOCollection() != null){
            for (ObservationVO obsVO : labResultProxyVO.getTheObservationVOCollection()) {
                String obsDomainCdSt1 = obsVO.getTheObservationDT().getObsDomainCdSt1();
                if (obsDomainCdSt1 != null
                        && obsDomainCdSt1.equalsIgnoreCase(EdxELRConstant.ELR_ORDER_CD)
                )
                {
                    obsVO.getTheObservationDT().setObservationUid(matchedObservationUid);
                    obsVO.getTheObservationDT().setVersionCtrlNbr(matchedObservationDT.getVersionCtrlNbr());
                    obsVO.getTheObservationDT().setProgAreaCd(matchedObservationDT.getProgAreaCd());
                    obsVO.getTheObservationDT().setJurisdictionCd(matchedObservationDT.getJurisdictionCd());
                    obsVO.getTheObservationDT().setSharedInd(matchedObservationDT.getSharedInd());
                    obsVO.getTheObservationDT().setLocalId(matchedObservationDT.getLocalId());
                    obsVO.getTheObservationDT().setItDirty(true);
                    obsVO.getTheObservationDT().setItNew(false);

                    obsVO.setItNew(false);
                    obsVO.setItDirty(true);
                    labResultProxyVO.setItDirty(true);
                    labResultProxyVO.setItNew(false);
                    break;
                }

            }
        }

        if(labResultProxyVO.getTheActRelationshipDTCollection()!=null)
        {
            for (ActRelationshipDT actRelationshipDT : labResultProxyVO.getTheActRelationshipDTCollection()) {
                if (actRelationshipDT.getTargetActUid().compareTo(edxLabInformationDT.getRootObserbationUid()) == 0
                        && (!actRelationshipDT.getTypeCd().equals(EdxELRConstant.ELR_SUPPORT_CD)
                                || !actRelationshipDT.getTypeCd().equals(EdxELRConstant.ELR_REFER_CD)
                                || !actRelationshipDT.getTypeCd().equals(EdxELRConstant.ELR_COMP_CD)
                            )
                ) {
                    actRelationshipDT.setTargetActUid(matchedObservationUid);
                    break;
                }
            }
        }
    }


    public ObservationDT matchingObservation(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
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
                ObservationDT observationDT = new ObservationDT(result.get());
                observationDT.setItNew(false);
                observationDT.setItDirty(false);
                observationDT.setItDelete(false);

                return observationDT;
            }
        }
    }
}
