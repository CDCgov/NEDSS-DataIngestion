package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.service.interfaces.IPatientService;
import gov.cdc.dataprocessing.utilities.component.EdxPatientMatchingCriteriaUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Service
@Slf4j
public class PatientService implements IPatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final EdxPatientMatchingCriteriaUtil edxPatientMatchingCriteriaUtil;
    public PatientService(EdxPatientMatchingCriteriaUtil edxPatientMatchingCriteriaUtil) {

        this.edxPatientMatchingCriteriaUtil = edxPatientMatchingCriteriaUtil;
    }

    public Object processingPatient(LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        //TODO: Adding Logic Here
        PersonVO person = null;
        try {
            Collection<PersonVO> personVOCollection = labResultProxyVO.getThePersonVOCollection();
            if (!personVOCollection.isEmpty()) {
                Iterator<PersonVO> it = personVOCollection.iterator();
                boolean orderingProviderIndicator = false;
                while (it.hasNext()) {
                    PersonVO personVO = it.next();
                    if (personVO.getRole() != null && personVO.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                        //TODO: Logic for Matching Next of kin
                    }
                    else {
                        long falseUid = personVO.thePersonDT.getPersonUid();
                        Long personUid;
                        EdxPatientMatchDT edxPatientMatchFoundDT = null;

                        if (personVO.thePersonDT.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PATIENT_CD)) {
                            personVO.setRole(EdxELRConstant.ELR_PATIENT_CD);

                            if(edxLabInformationDT.getPatientUid()>0){
                                personUid=edxLabInformationDT.getPatientUid();
                            }
                            else{
                                edxPatientMatchFoundDT = edxPatientMatchingCriteriaUtil.getMatchingPatient(personVO);
                                edxLabInformationDT.setMultipleSubjectMatch(edxPatientMatchingCriteriaUtil.multipleMatchFound);
                                personUid = personVO.getThePersonDT().getPersonUid();
                            }

                            if (personUid != null) {
                                setFalseToNew(labResultProxyVO, falseUid, personUid);
                                personVO.setItNew(false);
                                personVO.setItDirty(false);
                                personVO.getThePersonDT().setItNew(false);
                                personVO.getThePersonDT().setItDirty(false);
                                PersonNameDT personName = getPersonNameUseCdL(personVO);
                                String lastName = personName.getLastNm();
                                String firstName = personName.getFirstNm();
                                edxLabInformationDT.setEntityName(firstName
                                        + " " + lastName);
                            }

                            if(edxPatientMatchFoundDT!=null && !edxPatientMatchFoundDT.isMultipleMatch() && personVO.getIsExistingPatient())
                                edxLabInformationDT.setPatientMatch(true);
                            if(personVO.getThePersonDT().getPersonParentUid()!=null){
                                edxLabInformationDT.setPersonParentUid(personVO.getThePersonDT().getPersonParentUid().longValue());
                            }
                            person = personVO;
                        }
                        else if (personVO.thePersonDT.getCd().equalsIgnoreCase(EdxELRConstant.ELR_PROVIDER_CD)) {
                            //TODO: Logic for Matching Provider

                        }
                    }
                }
            }
            return "processing patient";
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    public Object processingNextOfKin() throws DataProcessingConsumerException {
        //TODO: Adding Logic Here
        try {
            return "processing next of kin";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }
    }

    public Object processingProvider() throws DataProcessingConsumerException {
        //TODO: Adding Logic Here
        try {
            return "processing provider";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }
    }

    private PersonNameDT getPersonNameUseCdL(PersonVO personVO) throws DataProcessingException {
        Collection<PersonNameDT> personNames = personVO.getThePersonNameDTCollection();
        Iterator<PersonNameDT> pnIter = personNames.iterator();
        while (pnIter.hasNext()) {
            PersonNameDT personName = (PersonNameDT) pnIter.next();
            if (personName.getNmUseCd().equals("L")) {
                return personName;
            }
        }
        throw new DataProcessingException("No name use code \"L\" in PersonVO");
    }

    private void setFalseToNew(LabResultProxyVO labResultProxyVO, Long falseUid, Long actualUid) throws DataProcessingException {

        try {
            Iterator<ParticipationDT> participationIterator = null;
            Iterator<ActRelationshipDT> actRelationshipIterator = null;
            Iterator<RoleDT> roleIterator = null;


            ParticipationDT participationDT = null;
            ActRelationshipDT actRelationshipDT = null;
            RoleDT roleDT = null;

            Collection<ParticipationDT> participationColl = labResultProxyVO.getTheParticipationDTCollection();
            Collection<ActRelationshipDT> actRelationShipColl = labResultProxyVO.getTheActRelationshipDTCollection();
            Collection<RoleDT> roleColl = labResultProxyVO.getTheRoleDTCollection();

            if (participationColl != null) {
                for (participationIterator = participationColl.iterator(); participationIterator.hasNext();) {
                    participationDT = (ParticipationDT) participationIterator.next();
                    logger.debug("(participationDT.getAct() comparedTo falseUid)"
                            + (participationDT.getActUid().compareTo(falseUid)));
                    if (participationDT.getActUid().compareTo(falseUid) == 0) {
                        participationDT.setActUid(actualUid);
                    }

                    if (participationDT.getSubjectEntityUid().compareTo(falseUid) == 0) {
                        participationDT.setSubjectEntityUid(actualUid);
                    }
                }
                logger.debug("participationDT.getSubjectEntityUid()"
                        + participationDT.getSubjectEntityUid());
            }

            if (actRelationShipColl != null) {
                for (actRelationshipIterator = actRelationShipColl.iterator(); actRelationshipIterator
                        .hasNext();) {
                    actRelationshipDT = (ActRelationshipDT) actRelationshipIterator.next();

                    if (actRelationshipDT.getTargetActUid().compareTo(falseUid) == 0) {
                        actRelationshipDT.setTargetActUid(actualUid);
                    }
                    if (actRelationshipDT.getSourceActUid().compareTo(falseUid) == 0) {
                        actRelationshipDT.setSourceActUid(actualUid);
                    }
                    logger.debug("ActRelationShipDT: falseUid "
                            + falseUid.toString() + " actualUid: " + actualUid);
                }
            }

            if (roleColl != null) {
                for (roleIterator = roleColl.iterator(); roleIterator.hasNext();) {
                    roleDT = (RoleDT) roleIterator.next();
                    if (roleDT.getSubjectEntityUid().compareTo(falseUid) == 0) {
                        roleDT.setSubjectEntityUid(actualUid);
                    }
                    if (roleDT.getScopingEntityUid() != null) {
                        if (roleDT.getScopingEntityUid().compareTo(falseUid) == 0) {
                            roleDT.setScopingEntityUid(actualUid);
                        }
                        logger.debug("\n\n\n(roleDT.getSubjectEntityUid() compared to falseUid)  "
                                + roleDT.getSubjectEntityUid().compareTo(
                                falseUid));
                        logger.debug("\n\n\n(roleDT.getScopingEntityUid() compared to falseUid)  "
                                + roleDT.getScopingEntityUid().compareTo(
                                falseUid));
                    }

                }
            }

        } catch (Exception e) {
            logger.error("HL7CommonLabUtil.setFalseToNew thrown for falseUid:"
                    + falseUid + "For actualUid :" + actualUid);
            throw new DataProcessingException("HL7CommonLabUtil.setFalseToNew thrown for falseUid:" + falseUid + "For actualUid :" + actualUid);
        }
    }

}
