package gov.cdc.dataprocessing.service.core;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.service.interfaces.INokMatchingService;
import gov.cdc.dataprocessing.service.interfaces.IPatientMatchingService;
import gov.cdc.dataprocessing.service.interfaces.IPatientService;
import gov.cdc.dataprocessing.service.interfaces.IProviderMatchingService;
import gov.cdc.dataprocessing.service.matching.PatientMatchingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

@Service
@Slf4j
public class PatientService implements IPatientService {
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final IPatientMatchingService patientMatchingService;
    private final INokMatchingService nokMatchingService;
    private final IProviderMatchingService providerMatchingService;

    public PatientService(
            PatientMatchingService patientMatchingService,
            INokMatchingService nokMatchingService,
            IProviderMatchingService providerMatchingService) {

        this.patientMatchingService = patientMatchingService;
        this.nokMatchingService = nokMatchingService;
        this.providerMatchingService = providerMatchingService;
    }

    @Transactional
    public PersonContainer processingNextOfKin(LabResultProxyContainer labResultProxyContainer, PersonContainer personContainer) throws DataProcessingException {
        try {
            long falseUid = personContainer.thePersonDto.getPersonUid();
            nokMatchingService.getMatchingNextOfKin(personContainer);

            if (personContainer.getThePersonDto().getPersonUid() != null) {

                setFalseToNew(labResultProxyContainer, falseUid, personContainer.getThePersonDto().getPersonUid());
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);

            }

            return personContainer;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    @Transactional
    public PersonContainer processingPatient(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer) throws DataProcessingException {
        //TODO: Adding Logic Here
        try {
            long falseUid = personContainer.thePersonDto.getPersonUid();
            Long personUid;
            EdxPatientMatchDto edxPatientMatchFoundDT = null;

            personContainer.setRole(EdxELRConstant.ELR_PATIENT_CD);

            if(edxLabInformationDto.getPatientUid()>0){
                personUid= edxLabInformationDto.getPatientUid();
            }
            else{
                //NOTE: Mathing Patient
                //NOTE: This matching also persist patient accordingly
                //NOTE: Either new or existing patient, it will be processed within this method
                edxPatientMatchFoundDT = patientMatchingService.getMatchingPatient(personContainer);
                edxLabInformationDto.setMultipleSubjectMatch(patientMatchingService.getMultipleMatchFound());
                personUid = personContainer.getThePersonDto().getPersonUid();
            }

            if (personUid != null) {
                setFalseToNew(labResultProxyContainer, falseUid, personUid);
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);
                PersonNameDto personName = parsingPersonName(personContainer);
                String lastName = personName.getLastNm();
                String firstName = personName.getFirstNm();
                edxLabInformationDto.setEntityName(firstName + " " + lastName);
            }

            if(edxPatientMatchFoundDT!=null && !edxPatientMatchFoundDT.isMultipleMatch() && personContainer.getPatientMatchedFound()) {
                edxLabInformationDto.setPatientMatch(true);
            }
            if(personContainer.getThePersonDto().getPersonParentUid()!=null){
                edxLabInformationDto.setPersonParentUid(personContainer.getThePersonDto().getPersonParentUid());
            }

            return personContainer;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
    }

    @Transactional
    public PersonContainer processingProvider(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer, boolean orderingProviderIndicator) throws DataProcessingException {
        //TODO: Adding Logic Here
        try {
            long falseUid = personContainer.thePersonDto.getPersonUid();
            Long personUid;
            EdxPatientMatchDto edxPatientMatchFoundDT = null;


            if (personContainer.getRole() != null && personContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)) {
                orderingProviderIndicator = true;
            }

            personContainer.setRole(EdxELRConstant.ELR_PROV_CD);
            EDXActivityDetailLogDT eDXActivityDetailLogDT = new EDXActivityDetailLogDT();
            eDXActivityDetailLogDT = providerMatchingService.getMatchingProvider(personContainer);
            String personUId;
            personUId = eDXActivityDetailLogDT.getRecordId();
            if (personUId != null) {
                long uid = Long.parseLong(personUId);
                setFalseToNew(labResultProxyContainer, falseUid,uid);
                personContainer.setItNew(false);
                personContainer.setItDirty(false);
                personContainer.getThePersonDto().setItNew(false);
                personContainer.getThePersonDto().setItDirty(false);
            }
            if (orderingProviderIndicator)
            {
                return personContainer;
            }
            orderingProviderIndicator= false;

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage());
        }
        return null;
    }

    private PersonNameDto parsingPersonName(PersonContainer personContainer) throws DataProcessingException {
        Collection<PersonNameDto> personNames = personContainer.getThePersonNameDtoCollection();
        Iterator<PersonNameDto> pnIter = personNames.iterator();
        while (pnIter.hasNext()) {
            PersonNameDto personName = pnIter.next();
            if (personName.getNmUseCd().equals("L")) {
                return personName;
            }
        }
        throw new DataProcessingException("No name use code \"L\" in PersonVO");
    }

    /**
     * TODO: Evaluation needed
     * NOTE: Not sure what this for
     * */
    private void setFalseToNew(LabResultProxyContainer labResultProxyContainer, Long falseUid, Long actualUid) throws DataProcessingException {

        try {
            Iterator<ParticipationDT> participationIterator = null;
            Iterator<ActRelationshipDT> actRelationshipIterator = null;
            Iterator<RoleDto> roleIterator = null;


            ParticipationDT participationDT = null;
            ActRelationshipDT actRelationshipDT = null;
            RoleDto roleDto = null;

            Collection<ParticipationDT> participationColl = labResultProxyContainer.getTheParticipationDTCollection();
            Collection<ActRelationshipDT> actRelationShipColl = labResultProxyContainer.getTheActRelationshipDTCollection();
            Collection<RoleDto> roleColl = labResultProxyContainer.getTheRoleDtoCollection();

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
                    roleDto = (RoleDto) roleIterator.next();
                    if (roleDto.getSubjectEntityUid().compareTo(falseUid) == 0) {
                        roleDto.setSubjectEntityUid(actualUid);
                    }
                    if (roleDto.getScopingEntityUid() != null) {
                        if (roleDto.getScopingEntityUid().compareTo(falseUid) == 0) {
                            roleDto.setScopingEntityUid(actualUid);
                        }
                        logger.debug("\n\n\n(roleDT.getSubjectEntityUid() compared to falseUid)  "
                                + roleDto.getSubjectEntityUid().compareTo(
                                falseUid));
                        logger.debug("\n\n\n(roleDT.getScopingEntityUid() compared to falseUid)  "
                                + roleDto.getScopingEntityUid().compareTo(
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
