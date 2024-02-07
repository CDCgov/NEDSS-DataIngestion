package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.service.interfaces.ICheckingValueService;
import gov.cdc.dataprocessing.utilities.EntityIdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class HL7PatientHandler {
    private static final Logger logger = LoggerFactory.getLogger(HL7PatientHandler.class);

    private final ICheckingValueService checkingValueService;
    private final NBSObjectConverter nbsObjectConverter;

    public HL7PatientHandler(ICheckingValueService checkingValueService, NBSObjectConverter nbsObjectConverter) {
        this.checkingValueService = checkingValueService;
        this.nbsObjectConverter = nbsObjectConverter;
    }

    /**
     * This method porcess and parse HL7 Patient Result into Object
     * - Patient Identification
     * - Patient Next of Kin
     * */
    public LabResultProxyVO getPatientAndNextOfKin(
            HL7PATIENTRESULTType hl7PatientResult,
            LabResultProxyVO labResultProxyVO,
            EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        try {
            if (hl7PatientResult != null && hl7PatientResult.getPATIENT() != null) {
                // Processing Patient Identification
                if (hl7PatientResult.getPATIENT().getPatientIdentification() != null) {
                    HL7PIDType patientInfo = hl7PatientResult.getPATIENT().getPatientIdentification();
                    getPatient(patientInfo, labResultProxyVO, edxLabInformationDT);
                }
                // Processing Next of kin
                if (hl7PatientResult.getPATIENT().getNextofKinAssociatedParties() != null) {
                    List<HL7NK1Type> hl7NK1TypeList = hl7PatientResult.getPATIENT().getNextofKinAssociatedParties();
                    // Only need the first index
                    if (!hl7NK1TypeList.isEmpty()) {
                        HL7NK1Type hl7NK1Type = hl7NK1TypeList.get(0);
                        getNextOfKinVO(hl7NK1Type, labResultProxyVO, edxLabInformationDT);
                    }
                }

            }


        } catch (Exception e) {
            logger.error("Exception thrown by HL7PatientProcessor.getPatientAndNextOfKin "+ e);
            throw new DataProcessingException("Exception thrown at HL7PatientProcessor.getPatientAndNextOfKin:"+ e.getMessage() + e);
        }
        return labResultProxyVO;
    }

    public LabResultProxyVO getPatient(HL7PIDType hl7PIDType,
                                              LabResultProxyVO labResultProxyVO,
                                              EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        try {

            edxLabInformationDT.setRole(EdxELRConstant.ELR_PATIENT_CD);
            PersonVO personVO = parseToPersonObject(labResultProxyVO, edxLabInformationDT);

            // Setting Entity ID for Patient Identifier
            for (int i = 0; i < hl7PIDType.getPatientIdentifierList().size(); i++) {
                HL7CXType hl7CXType = hl7PIDType.getPatientIdentifierList().get(i);

                // Parsing Entity Id
                EntityIdDT entityIdDT = EntityIdHandler.processEntityData(hl7CXType, personVO, null, i);


                if( entityIdDT.getAssigningAuthorityIdType() == null) {
                    entityIdDT.setAssigningAuthorityIdType(edxLabInformationDT.getUniversalIdType());
                }
                if( entityIdDT.getTypeCd()!=null &&  entityIdDT.getTypeCd().equals(EdxELRConstant.ELR_SS_TYPE)){
                    String SSNNumberinit = entityIdDT.getRootExtensionTxt().replace("-", "");
                    String SSNNumber =SSNNumberinit.replace(" ", "");
                    try {
                        if(SSNNumber.length()!=9) {
                            edxLabInformationDT.setSsnInvalid(true);
                        }
                        Integer.parseInt(SSNNumber);
                    }
                    catch (NumberFormatException e) {
                        edxLabInformationDT.setSsnInvalid(true);
                    }
                    entityIdDT = NBSObjectConverter.validateSSN(entityIdDT);
                    personVO.getThePersonDT().setSSN(entityIdDT.getRootExtensionTxt());
                }
                if(personVO.getTheEntityIdDTCollection()==null) {
                    personVO.setTheEntityIdDTCollection(new ArrayList<>());
                }
                if(entityIdDT.getEntityUid()!=null) {
                    personVO.getTheEntityIdDTCollection().add(entityIdDT);
                }
            }

            // Setup Participant for LabResult
            if (labResultProxyVO.getTheParticipationDTCollection() == null) {
                labResultProxyVO.setTheParticipationDTCollection(new ArrayList<>());
            }
            ParticipationDT participationDT = new ParticipationDT();
            participationDT.setSubjectEntityUid(personVO.getThePersonDT().getPersonUid());
            participationDT.setItNew(true);
            participationDT.setItDirty(false);
            participationDT.setCd(EdxELRConstant.ELR_PATIENT_CD);
            participationDT.setAddUserId(EdxELRConstant.ELR_ADD_USER_ID);

            participationDT.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
            participationDT.setTypeCd(EdxELRConstant.ELR_PATIENT_SUBJECT_CD);
            participationDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            participationDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);

            participationDT.setTypeDescTxt(EdxELRConstant.ELR_PATIENT_SUBJECT_DESC);
            participationDT.setActUid(edxLabInformationDT.getRootObserbationUid());
            participationDT.setActClassCd(EdxELRConstant.ELR_OBS);
            labResultProxyVO.getTheParticipationDTCollection().add(participationDT);


            // Setup Person
            personVO.getThePersonDT().setAddReasonCd(EdxELRConstant.ELR_ADD_REASON_CD);
            personVO.getThePersonDT().setCurrSexCd(hl7PIDType.getAdministrativeSex());
            personVO.getThePersonDT().setElectronicInd(ELRConstant.ELECTRONIC_IND);

            // Setup Person Sex Code
            //TODO: Call out ro ElrXref Repository to grab the data
            ElrXref elrXref = new ElrXref();
            String toCode = elrXref.getToCode();//CachedDropDowns.findToCode("ELR_LCA_SEX", personVO.getThePersonDT().getCurrSexCd(), "P_SEX");
            if (toCode != null && !toCode.trim().isEmpty()){
                personVO.getThePersonDT().setCurrSexCd(toCode.trim());
                edxLabInformationDT.setSexTranslated(true);
            }else{
                edxLabInformationDT.setSexTranslated(false);
            }

            // Setup Person Birth Time
            if (hl7PIDType.getDateTimeOfBirth() != null) {
                Timestamp timestamp = NBSObjectConverter.processHL7TSTypeForDOBWithoutTime(hl7PIDType.getDateTimeOfBirth());
                personVO.getThePersonDT().setBirthTime(timestamp);
                personVO.getThePersonDT().setBirthTimeCalc(timestamp);
            }

            // Setup Person Birth Place
            if(hl7PIDType.getBirthPlace()!=null && !hl7PIDType.getBirthPlace().trim().equals("")){
                NBSObjectConverter.setPersonBirthType(hl7PIDType.getBirthPlace(), personVO);
            }

            // Setup Person Ethnic Group
            Collection<Object> ethnicColl = new ArrayList<Object>();
            List<HL7CWEType> ethnicArray = hl7PIDType.getEthnicGroup();
            for (int j = 0; j < ethnicArray.size(); j++) {
                HL7CWEType ethnicType = ethnicArray.get(j);

                PersonEthnicGroupDT personEthnicGroupDT = NBSObjectConverter.ethnicGroupType(ethnicType, personVO);
                //TODO: Call out to ElrXrefRepositoty
                ElrXref elrXrefForEthnic = new ElrXref();
                String ethnicGroupCd = elrXrefForEthnic.getToCode(); //CachedDropDowns.findToCode("ELR_LCA_ETHN_GRP", personEthnicGroupDT.getEthnicGroupCd(), "P_ETHN_GRP");
                if(ethnicGroupCd!=null && !ethnicGroupCd.trim().equals("")){
                    personEthnicGroupDT.setEthnicGroupCd(ethnicGroupCd);
                }
                if (personEthnicGroupDT.getEthnicGroupCd() != null && !personEthnicGroupDT.getEthnicGroupCd().trim().equals(""))
                {
                    var map = checkingValueService.getCodedValues("P_ETHN_GRP");
                    if (map.containsKey(personEthnicGroupDT.getEthnicGroupCd())) {
                        edxLabInformationDT.setEthnicityCodeTranslated(false);
                    }
                }
                if (personEthnicGroupDT.getEthnicGroupCd() != null
                        && !personEthnicGroupDT.getEthnicGroupCd().trim().equals("")
                    )
                {
                    ethnicColl.add(personEthnicGroupDT);
                    personVO.getThePersonDT().setEthnicGroupInd(personEthnicGroupDT.getEthnicGroupCd());
                }
                else {
                    logger.info("Blank value recived for PID-22, Ethinicity");
                }
                personVO.setThePersonEthnicGroupDTCollection(ethnicColl);
            }

            // Setup person Martial Status
            HL7CEType maritalStatusType = hl7PIDType.getMaritalStatus();
            if(maritalStatusType!= null && maritalStatusType.getHL7Identifier()!=null){
                personVO.getThePersonDT().setMaritalStatusCd(maritalStatusType.getHL7Identifier().toUpperCase());
                personVO.getThePersonDT().setMaritalStatusDescTxt(maritalStatusType.getHL7Text());
            }

            // Setup Person Mothers
            if(hl7PIDType.getMothersIdentifier() != null){
                for(int i=0; i < hl7PIDType.getMothersIdentifier().size(); i++){
                    HL7CXType hl7CXType = hl7PIDType.getMothersIdentifier().get(i);
                    int j = i;
                    if(personVO.getTheEntityIdDTCollection()!=null ) {
                        j= personVO.getTheEntityIdDTCollection().size();
                    }
                    EntityIdDT entityIdDT = nbsObjectConverter.processEntityData(hl7CXType, personVO, EdxELRConstant.ELR_MOTHER_IDENTIFIER, j);
                    if(entityIdDT.getEntityUid() != null) {
                        personVO.getTheEntityIdDTCollection().add(entityIdDT);
                    }
                }
            }

            //Setup Person Maiden Mother Name
            if(hl7PIDType.getMothersMaidenName() != null && (hl7PIDType.getMothersMaidenName().size() > 0)){
                String surname = "";
                if(hl7PIDType.getMothersMaidenName().get(0).getHL7FamilyName()!=null) {
                    surname = hl7PIDType.getMothersMaidenName().get(0).getHL7FamilyName().getHL7Surname();
                }
                String givenName = hl7PIDType.getMothersMaidenName().get(0).getHL7GivenName();
                String motherMaidenNm = "";
                if(surname!= null) {
                    motherMaidenNm = surname;
                }
                if(givenName!= null) {
                    motherMaidenNm =  motherMaidenNm + " " + givenName;
                }
                personVO.getThePersonDT().setMothersMaidenNm(motherMaidenNm.trim());
            }

            //Setup Person Birth Order
            if(hl7PIDType.getBirthOrder()!=null && hl7PIDType.getBirthOrder().getHL7Numeric() != null) {
                //TODO: double check this
                // var val = Math.round((long) hl7PIDType.getBirthOrder().getHL7Numeric());
                personVO.getThePersonDT().setBirthOrderNbr(hl7PIDType.getBirthOrder().getHL7Numeric().intValue());
            }
            if(hl7PIDType.getMultipleBirthIndicator()!=null){
                personVO.getThePersonDT().setMultipleBirthInd(hl7PIDType.getMultipleBirthIndicator());
            }


            //Setup Person Account Number
            if(hl7PIDType.getPatientAccountNumber()!=null){
                int j = 1;
                if(personVO.getTheEntityIdDTCollection()!=null ) {
                    j= personVO.getTheEntityIdDTCollection().size();
                }
                EntityIdDT entityIdDT = nbsObjectConverter.processEntityData(hl7PIDType.getPatientAccountNumber(), personVO, EdxELRConstant.ELR_ACCOUNT_IDENTIFIER, j);
                if(entityIdDT.getEntityUid() != null) {
                    personVO.getTheEntityIdDTCollection().add(entityIdDT);
                }
            }

            //Setup Person Address
            List<HL7XADType> addressArray = hl7PIDType.getPatientAddress();
            Collection<Object> addressCollection = new ArrayList<Object>();

            if (!addressArray.isEmpty()) {
                HL7XADType addressType = addressArray.get(0);
                nbsObjectConverter.personAddressType(addressType, EdxELRConstant.ELR_PATIENT_CD, personVO);
            }
            //Setup Person Deceased Status
            personVO.getThePersonDT().setDeceasedIndCd(hl7PIDType.getPatientDeathIndicator());
            if (hl7PIDType.getPatientDeathDateAndTime() != null) {
                personVO.getThePersonDT().setDeceasedTime(NBSObjectConverter.processHL7TSType(hl7PIDType
                                .getPatientDeathDateAndTime(), EdxELRConstant.DATE_VALIDATION_PATIENT_DEATH_DATE_AND_TIME_MSG));
            }

            //Setup Person Names
            List<HL7XPNType> nameArray = hl7PIDType.getPatientName();
            for (int j = 0; j < nameArray.size(); j++) {
                HL7XPNType hl7XPNType = nameArray.get(j);
                nbsObjectConverter.mapPersonNameType(hl7XPNType,personVO);
            }

            //Setup Person Business Phone Number
            if(hl7PIDType.getPhoneNumberBusiness() != null){
                List<HL7XTNType> phoneBusinessArray = hl7PIDType.getPhoneNumberBusiness();
                if (phoneBusinessArray != null && !phoneBusinessArray.isEmpty()) {
                    HL7XTNType phoneType = phoneBusinessArray.get(0);
                    EntityLocatorParticipationDT elpDT = NBSObjectConverter.personTelePhoneType(phoneType, EdxELRConstant.ELR_PATIENT_CD, personVO);
                    elpDT.setUseCd(NEDSSConstant.WORK_PHONE);
                    addressCollection.add(elpDT);
                }
            }

            //Setup Person Home Phone Number
            if(hl7PIDType.getPhoneNumberHome()!=null ){
                List<HL7XTNType> phoneHomeArray = hl7PIDType.getPhoneNumberHome();
                if (!phoneHomeArray.isEmpty()) {
                    HL7XTNType phoneType = phoneHomeArray.get(0);
                    EntityLocatorParticipationDT elpDT = NBSObjectConverter.personTelePhoneType(phoneType, EdxELRConstant.ELR_PATIENT_CD, personVO);
                    elpDT.setUseCd(NEDSSConstant.HOME);
                    addressCollection.add(elpDT);
                }
            }

            //Setup Person Race
            if(hl7PIDType.getRace() != null){
                Collection<Object> raceColl = new ArrayList<Object>();
                List<HL7CWEType> raceArray = hl7PIDType.getRace();
                PersonRaceDT raceDT = null;
                for (int j = 0; j < raceArray.size(); j++) {
                    try {
                        HL7CWEType raceType = raceArray.get(j);
                        raceDT = NBSObjectConverter.raceType(raceType, personVO);
                        raceDT.setPersonUid(personVO.getThePersonDT().getPersonUid());
                        //TODO: Call out to ElrXrefRepositoty
                        ElrXref elrXrefForRace = new ElrXref();
                        String newRaceCat = elrXrefForRace.getToCode();//CachedDropDowns.findToCode("ELR_LCA_RACE", raceDT.getRaceCategoryCd(), "P_RACE_CAT");
                        if (newRaceCat != null && !newRaceCat.trim().equals("")) {
                            raceDT.setRaceCd(newRaceCat);
                            raceDT.setRaceCategoryCd(newRaceCat);
                        }
                        var codeMap = checkingValueService.getRaceCodes();
                        if (!codeMap.containsKey(raceDT.getRaceCd())) {
                            edxLabInformationDT.setRaceTranslated(false);
                        }
                        raceColl.add(raceDT);
                    } catch (Exception e) {
                        logger.error("Exception thrown by HL7PatientProcessor.getPatientVO  getting race information" + e);
                        throw new DataProcessingException(
                                "Exception thrown at HL7PatientProcessor.getPatientVO getting race information:" + e);
                    }// end of catch
                }
                personVO.setThePersonRaceDTCollection(raceColl);
            }

            if(labResultProxyVO.getThePersonVOCollection()==null){
                labResultProxyVO.setThePersonVOCollection(new ArrayList<PersonVO>());
            }
            labResultProxyVO.getThePersonVOCollection().add(personVO);


        } catch (Exception e) {
            logger.error("Exception thrown by HL7ORCProcessor.getPatientVO "+ e);
            throw new DataProcessingException("Exception thrown at HL7PatientProcessor.getPatientVO:"+ e.getMessage() + e);
        }

        return labResultProxyVO;
    }

    /**
     * This method process and parse data from EdxLabInformation to ParsonVO and LabResultProxyVO
     *  - Person Object
     *  - Role Object (part of Lab Result, this is a list)
     * */
    public static PersonVO parseToPersonObject(LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        PersonVO personVO = new PersonVO();
        try {
            PersonDT personDT = personVO.getThePersonDT();
            personVO.getThePersonDT().setElectronicInd(ELRConstant.ELECTRONIC_IND);

            // Check patient object ROLE: PAT, NOK, PROVIDER
            // Then parsing data to PersonVO and DTO
            if (edxLabInformationDT.getRole().equalsIgnoreCase(NEDSSConstant.PAT) ) {
                personVO.getThePersonDT().setCd(NEDSSConstant.PAT);
                personDT.setCd(EdxELRConstant.ELR_PATIENT_CD);
                personDT.setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
                personDT.setPersonUid((long)edxLabInformationDT.getPatientUid());
            } else if (edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)){
                personVO.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
                personDT.setCd(EdxELRConstant.ELR_PATIENT_CD);
                personDT.setCdDescTxt(EdxELRConstant.ELR_NOK_DESC);
                personDT.setPersonUid((long) edxLabInformationDT.getNextUid());
            } else {
                personVO.getThePersonDT().setCd(EdxELRConstant.ELR_PROVIDER_CD);
                personDT.setCd(EdxELRConstant.ELR_PROVIDER_CD);
                personDT.setCdDescTxt(EdxELRConstant.ELR_PROVIDER_DESC);
                personDT.setPersonUid((long)edxLabInformationDT.getNextUid());
            }

            personVO.setItDirty(false);
            personVO.getThePersonDT().setItNew(true);
            personVO.getThePersonDT().setItDirty(false);
            personVO.setItNew(true);

            personVO.getThePersonDT().setVersionCtrlNbr(1);
            personVO.getThePersonDT().setItNew(true);
            personVO.getThePersonDT().setLastChgTime(edxLabInformationDT.getAddTime());
            personVO.getThePersonDT().setAddTime(edxLabInformationDT.getAddTime());
            personVO.getThePersonDT().setLastChgUserId(edxLabInformationDT.getUserId());
            personVO.getThePersonDT().setAddUserId(edxLabInformationDT.getUserId());
            personVO.getThePersonDT().setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            personVO.getThePersonDT().setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            personVO.getThePersonDT().setStatusTime(personVO.getThePersonDT().getLastChgTime());

            personDT.setLastChgTime(edxLabInformationDT.getAddTime());
            personDT.setLastChgUserId(edxLabInformationDT.getUserId());
            personDT.setAsOfDateAdmin(edxLabInformationDT.getAddTime());
            personDT.setAsOfDateEthnicity(edxLabInformationDT.getAddTime());
            personDT.setAsOfDateGeneral(edxLabInformationDT.getAddTime());
            personDT.setAsOfDateMorbidity(edxLabInformationDT.getAddTime());
            personDT.setAsOfDateSex(edxLabInformationDT.getAddTime());
            personDT.setAddTime(edxLabInformationDT.getAddTime());
            personDT.setAddUserId(edxLabInformationDT.getUserId());


            // Parsing to ROLE Object
            RoleDT roleDT = new RoleDT();
            roleDT.setSubjectEntityUid(personDT.getPersonUid());
            roleDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            boolean addRole= false;

            if (edxLabInformationDT.getRole().equalsIgnoreCase(NEDSSConstant.PAT)) {
                roleDT.setCd(NEDSSConstant.PAT);
                roleDT.setCdDescTxt(EdxELRConstant.ELR_PATIENT);
                roleDT.setSubjectClassCd(EdxELRConstant.ELR_PATIENT);
                addRole= true;
            }
            else if (edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                roleDT.setSubjectClassCd(EdxELRConstant.ELR_CON);
                if(edxLabInformationDT.getRelationship()!=null) {
                    roleDT.setCd(edxLabInformationDT.getRelationship());
                }
                else {
                    roleDT.setCd(EdxELRConstant.ELR_NEXT_F_KIN_ROLE_CD);
                }
                if(edxLabInformationDT.getRelationshipDesc()!=null) {
                    roleDT.setCdDescTxt(edxLabInformationDT.getRelationshipDesc());
                }
                else {
                    roleDT.setCdDescTxt(EdxELRConstant.ELR_NEXT_F_KIN_ROLE_DESC);
                }
                roleDT.setScopingRoleSeq(1);
                roleDT.setScopingEntityUid(edxLabInformationDT.getPatientUid());
                roleDT.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                addRole= true;
            }
            else if (edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD)) {
                roleDT.setCd(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);
                roleDT.setSubjectClassCd(EdxELRConstant.ELR_PROVIDER_CD);
                roleDT.setCdDescTxt(EdxELRConstant.ELR_SPECIMEN_PROCURER_DESC);
                roleDT.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingEntityUid(edxLabInformationDT.getPatientUid());
                roleDT.setScopingRoleSeq(1);
                addRole= true;
            }
            else if (edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_PROVIDER_CD) ||
                    edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_VERIFIER_CD)||
                    edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_ASSISTANT_CD) ||
                    edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_PERFORMER_CD) ||
                    edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_ENTERER_CD)
                )
            {
                roleDT.setCd(EdxELRConstant.ELR_LAB_PROVIDER_CD);
                roleDT.setSubjectClassCd(EdxELRConstant.ELR_PROVIDER_CD);
                roleDT.setCdDescTxt(EdxELRConstant.ELR_LAB_PROVIDER_DESC);
                roleDT.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingEntityUid(edxLabInformationDT.getPatientUid());
                roleDT.setScopingRoleSeq(1);
                addRole= true;
            }
            else if (edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)) {
                roleDT.setCd(EdxELRConstant.ELR_OP_CD);
                roleDT.setSubjectClassCd(EdxELRConstant.ELR_PROVIDER_CD);
                roleDT.setCdDescTxt(EdxELRConstant.ELR_OP_DESC);
                roleDT.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingRoleSeq(1);
                roleDT.setScopingEntityUid(edxLabInformationDT.getPatientUid());
                roleDT.setScopingRoleSeq(1);
                addRole= true;
            }
            else if (edxLabInformationDT.getRole().equalsIgnoreCase(EdxELRConstant.ELR_COPY_TO_CD)) {
                roleDT.setCd(EdxELRConstant.ELR_COPY_TO_CD);
                roleDT.setSubjectClassCd(EdxELRConstant.ELR_PROV_CD);
                roleDT.setCdDescTxt(EdxELRConstant.ELR_COPY_TO_DESC);
                roleDT.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDT.setScopingRoleSeq(1);
                roleDT.setScopingEntityUid(edxLabInformationDT.getPatientUid());
                roleDT.setScopingRoleSeq(1);
                addRole= true;
            }

            roleDT.setAddUserId(EdxELRConstant.ELR_ADD_USER_ID);
            roleDT.setAddReasonCd(EdxELRConstant.ELR_ADD_REASON_CD);
            roleDT.setRoleSeq(1L);
            roleDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDT.setSubjectEntityUid(personDT.getPersonUid());
            roleDT.setItNew(true);
            roleDT.setItDirty(false);

            if(addRole){
                labResultProxyVO.getTheRoleDTCollection().add(roleDT);
            }

        } catch (Exception e) {
            logger.error("Exception thrown by HL7ORCProcessor.personVO " + e);
            throw new DataProcessingException("Exception thrown at HL7PatientProcessor.personVO:"+ e);
        }
        return personVO;
    }


    public LabResultProxyVO getNextOfKinVO(HL7NK1Type hl7NK1Type,
                                                  LabResultProxyVO labResultProxyVO,
                                                  EdxLabInformationDT edxLabInformationDT) throws DataProcessingException {
        try {
            edxLabInformationDT.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
            if(hl7NK1Type.getRelationship()!=null){
                edxLabInformationDT.setRelationship(hl7NK1Type.getRelationship().getHL7Identifier());
                String desc= checkingValueService.getCodeDescTxtForCd(edxLabInformationDT.getRelationship(), EdxELRConstant.ELR_NEXT_OF_KIN_RL_CLASS);
                if(desc!=null && desc.trim().length()>0 && hl7NK1Type.getRelationship().getHL7Text()==null) {
                    edxLabInformationDT.setRelationshipDesc(desc);
                }
                else if(hl7NK1Type.getRelationship().getHL7Text()!=null) {
                    edxLabInformationDT.setRelationshipDesc(hl7NK1Type.getRelationship().getHL7Text());
                }
            }
            PersonVO personVO = parseToPersonObject(labResultProxyVO,edxLabInformationDT);

            List<HL7XADType> addressArray = hl7NK1Type.getAddress();
            Collection<Object> addressCollection = new ArrayList<Object>();
            if (!addressArray.isEmpty()) {
                HL7XADType addressType = addressArray.get(0);
                nbsObjectConverter.personAddressType(addressType, EdxELRConstant.ELR_NEXT_OF_KIN, personVO);
            }

            List<HL7XPNType> nameArray = hl7NK1Type.getName();
            if (!nameArray.isEmpty()) {
                HL7XPNType hl7XPNType = nameArray.get(0);
                nbsObjectConverter.mapPersonNameType(hl7XPNType, personVO);
            }

            List<HL7XTNType> phoneHomeArray = hl7NK1Type.getPhoneNumber();
            if (!phoneHomeArray.isEmpty()) {
                HL7XTNType phoneType = phoneHomeArray.get(0);
                EntityLocatorParticipationDT elpDT = NBSObjectConverter.personTelePhoneType(phoneType, EdxELRConstant.ELR_NEXT_OF_KIN, personVO);
                addressCollection.add(elpDT);
            }
            labResultProxyVO.getThePersonVOCollection().add(personVO);
        } catch (Exception e) {
            logger.error("Exception thrown by HL7PatientProcessor.getNextOfKinVO "
                    + e);
            throw new DataProcessingException("Exception thrown at HL7PatientProcessor.getNextOfKinVO:"+ e);
        }
        return labResultProxyVO;
    }

}
