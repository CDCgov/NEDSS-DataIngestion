package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.EntityIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
/**
 * Do not Attempt to refactor this Class Unless you know what need to be done
 * Tightly coupling with Data Parser - small error can mess up the entire Pipeline
 * */
public class HL7PatientHandler {
    private static final Logger logger = LoggerFactory.getLogger(HL7PatientHandler.class);

    private final ICatchingValueDpService checkingValueService;
    private final NBSObjectConverter nbsObjectConverter;
    private final EntityIdUtil entityIdUtil;

    private final ICacheApiService cacheApiService;

    public HL7PatientHandler(ICatchingValueDpService checkingValueService,
                             NBSObjectConverter nbsObjectConverter,
                             EntityIdUtil entityIdUtil, @Lazy ICacheApiService cacheApiService) {
        this.checkingValueService = checkingValueService;
        this.nbsObjectConverter = nbsObjectConverter;
        this.entityIdUtil = entityIdUtil;
        this.cacheApiService = cacheApiService;
    }

    /**
     * This method porcess and parse HL7 Patient Result into Object
     * - Patient Identification
     * - Patient Next of Kin
     * */
    public LabResultProxyContainer getPatientAndNextOfKin(
            HL7PATIENTRESULTType hl7PatientResult,
            LabResultProxyContainer labResultProxyContainer,
            EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {

        if (hl7PatientResult == null) {
            return labResultProxyContainer;
        }

        var patient = hl7PatientResult.getPATIENT();
        if (patient == null) {
            return labResultProxyContainer;
        }

        var patientInfo = patient.getPatientIdentification();
        if (patientInfo != null) {
            getPatient(patientInfo, labResultProxyContainer, edxLabInformationDto);
        }

        var nokList = patient.getNextofKinAssociatedParties();
        if (nokList != null && !nokList.isEmpty()) {
            var nok = nokList.getFirst();
            var nokName = nok.getName();
            if (nokName != null && !nokName.isEmpty()) {
                getNextOfKinVO(nok, labResultProxyContainer, edxLabInformationDto);
            }
        }

        return labResultProxyContainer;
    }


    @SuppressWarnings({"java:S3776","java:S6541"})
    public LabResultProxyContainer getPatient(HL7PIDType hl7PIDType,
                                              LabResultProxyContainer labResultProxyContainer,
                                              EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {

        edxLabInformationDto.setRole(EdxELRConstant.ELR_PATIENT_CD);
        PersonContainer personContainer = parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

        // Setting Entity ID for Patient Identifier
        for (int i = 0; i < hl7PIDType.getPatientIdentifierList().size(); i++) {
            HL7CXType hl7CXType = hl7PIDType.getPatientIdentifierList().get(i);

            // Parsing Entity Id
            EntityIdDto entityIdDto = entityIdUtil.processEntityData(hl7CXType, personContainer, null, i);


            if( entityIdDto.getAssigningAuthorityIdType() == null) {
                entityIdDto.setAssigningAuthorityIdType(edxLabInformationDto.getUniversalIdType());
            }
            if( entityIdDto.getTypeCd()!=null &&  entityIdDto.getTypeCd().equals(EdxELRConstant.ELR_SS_TYPE)){
                String ssnNumberinit = entityIdDto.getRootExtensionTxt().replace("-", "");
                String ssnNumber =ssnNumberinit.replace(" ", "");
                try {
                    if(ssnNumber.length()!=9) {
                        edxLabInformationDto.setSsnInvalid(true);
                    }
                    Integer.parseInt(ssnNumber);
                }
                catch (NumberFormatException e) {
                    edxLabInformationDto.setSsnInvalid(true);
                }
                nbsObjectConverter.validateSSN(entityIdDto);
                personContainer.getThePersonDto().setSSN(entityIdDto.getRootExtensionTxt());
            }
            if(personContainer.getTheEntityIdDtoCollection()==null) {
                personContainer.setTheEntityIdDtoCollection(new ArrayList<>());
            }
            if(entityIdDto.getEntityUid()!=null) {
                personContainer.getTheEntityIdDtoCollection().add(entityIdDto);
            }
        }

        // Setup Participant for LabResult
        if (labResultProxyContainer.getTheParticipationDtoCollection() == null) {
            labResultProxyContainer.setTheParticipationDtoCollection(new ArrayList<>());
        }
        ParticipationDto participationDto = new ParticipationDto();
        participationDto.setSubjectEntityUid(personContainer.getThePersonDto().getPersonUid());
        participationDto.setItNew(true);
        participationDto.setItDirty(false);
        participationDto.setCd(EdxELRConstant.ELR_PATIENT_CD);
        participationDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());
        participationDto.setSubjectClassCd(EdxELRConstant.ELR_PERSON_CD);
        participationDto.setTypeCd(EdxELRConstant.ELR_PATIENT_SUBJECT_CD);
        participationDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        participationDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);

        participationDto.setTypeDescTxt(EdxELRConstant.ELR_PATIENT_SUBJECT_DESC);
        participationDto.setActUid(edxLabInformationDto.getRootObserbationUid());
        participationDto.setActClassCd(EdxELRConstant.ELR_OBS);
        labResultProxyContainer.getTheParticipationDtoCollection().add(participationDto);


        // Setup Person
        personContainer.getThePersonDto().setAddReasonCd(EdxELRConstant.ELR_ADD_REASON_CD);
        personContainer.getThePersonDto().setCurrSexCd(hl7PIDType.getAdministrativeSex());
        personContainer.getThePersonDto().setElectronicInd(ELRConstant.ELECTRONIC_IND);

        //Setup Person Lang
        var langList = hl7PIDType.getPrimaryLanguage();
        if (!langList.isEmpty()) {
            var primaryLang = langList.getFirst();
            personContainer.getThePersonDto().setPrimLangCd(primaryLang.getHL7Identifier());
            personContainer.getThePersonDto().setPrimLangDescTxt(primaryLang.getHL7Text());

            if (personContainer.getThePersonDto().getPrimLangCd().equals("ENG")) {
                personContainer.getThePersonDto().setSpeaksEnglishCd("Y");
            } else {
                personContainer.getThePersonDto().setSpeaksEnglishCd("N");
            }
        }

        // Setup Person Sex Code
        ElrXref elrXref;
        String key = "ELR_LCA_SEX_" + personContainer.getThePersonDto().getCurrSexCd() + "_P_SEX";
        ElrXref result = (ElrXref) cacheApiService.getSrteCacheObject(ObjectName.ELR_XREF.name(), key);
        if (result == null) {
            result = new ElrXref();
        }
        elrXref = result;

        String toCode = elrXref.getToCode();
        if (toCode == null && personContainer.getThePersonDto().getCurrSexCd() != null) {
            toCode = personContainer.getThePersonDto().getCurrSexCd();
        }
        if (toCode != null && !toCode.trim().isEmpty()){
            personContainer.getThePersonDto().setCurrSexCd(toCode.trim());
            edxLabInformationDto.setSexTranslated(true);
        }else{
            edxLabInformationDto.setSexTranslated(false);
        }

        // Setup Person Birth Time
        if (hl7PIDType.getDateTimeOfBirth() != null) {
            Timestamp timestamp = nbsObjectConverter.processHL7TSTypeForDOBWithoutTime(hl7PIDType.getDateTimeOfBirth());
            personContainer.getThePersonDto().setBirthTime(timestamp);
            personContainer.getThePersonDto().setBirthTimeCalc(timestamp);
        }

        // Setup Person Birth Place
        if(hl7PIDType.getBirthPlace()!=null && !hl7PIDType.getBirthPlace().trim().isEmpty()){
            nbsObjectConverter.setPersonBirthType(hl7PIDType.getBirthPlace(), personContainer);
        }

        // Setup Person Ethnic Group
        Collection<PersonEthnicGroupDto> ethnicColl = new ArrayList<>();
        List<HL7CWEType> ethnicArray = hl7PIDType.getEthnicGroup();
        for (HL7CWEType ethnicType : ethnicArray) {
            PersonEthnicGroupDto personEthnicGroupDto = nbsObjectConverter.ethnicGroupType(ethnicType, personContainer);
            ElrXref elrXrefForEthnic;
            String keyEthnic = "ELR_LCA_ETHN_GRP_" + personEthnicGroupDto.getEthnicGroupCd() + "_P_ETHN_GRP";
            ElrXref resultEthnic = (ElrXref) cacheApiService.getSrteCacheObject(ObjectName.ELR_XREF.name(), keyEthnic);
            if ( resultEthnic == null) {
                resultEthnic = new ElrXref();
            }
            elrXrefForEthnic = resultEthnic;

            String ethnicGroupCd = elrXrefForEthnic.getToCode();
            if (ethnicGroupCd != null && !ethnicGroupCd.trim().isEmpty()) {
                personEthnicGroupDto.setEthnicGroupCd(ethnicGroupCd);
            }
            if (personEthnicGroupDto.getEthnicGroupCd() != null && !personEthnicGroupDto.getEthnicGroupCd().trim().isEmpty()) {
                if (checkingValueService.checkCodedValue("P_ETHN_GRP", personEthnicGroupDto.getEthnicGroupCd())) {
                    edxLabInformationDto.setEthnicityCodeTranslated(false);
                }
            }
            if (personEthnicGroupDto.getEthnicGroupCd() != null
                    && !personEthnicGroupDto.getEthnicGroupCd().trim().isEmpty()
            ) {
                ethnicColl.add(personEthnicGroupDto);
                personContainer.getThePersonDto().setEthnicGroupInd(personEthnicGroupDto.getEthnicGroupCd());
            } else {
                logger.info("Blank value recived for PID-22, Ethinicity");
            }
            personContainer.setThePersonEthnicGroupDtoCollection(ethnicColl);
        }

        // Setup person Martial Status
        HL7CEType maritalStatusType = hl7PIDType.getMaritalStatus();
        if(maritalStatusType!= null && maritalStatusType.getHL7Identifier()!=null){
            personContainer.getThePersonDto().setMaritalStatusCd(maritalStatusType.getHL7Identifier().toUpperCase());
            personContainer.getThePersonDto().setMaritalStatusDescTxt(maritalStatusType.getHL7Text());
        }

        // Setup Person Mothers
        if(hl7PIDType.getMothersIdentifier() != null){
            for(int i=0; i < hl7PIDType.getMothersIdentifier().size(); i++){
                HL7CXType hl7CXType = hl7PIDType.getMothersIdentifier().get(i);
                int j = i;
                if(personContainer.getTheEntityIdDtoCollection()!=null ) {
                    j= personContainer.getTheEntityIdDtoCollection().size();
                }
                EntityIdDto entityIdDto = nbsObjectConverter.processEntityData(hl7CXType, personContainer, EdxELRConstant.ELR_MOTHER_IDENTIFIER, j);
                if(entityIdDto.getEntityUid() != null) {
                    personContainer.getTheEntityIdDtoCollection().add(entityIdDto);
                }
            }
        }

        //Setup Person Maiden Mother Name
        if(hl7PIDType.getMothersMaidenName() != null && (!hl7PIDType.getMothersMaidenName().isEmpty())){
            String surname = "";
            if(hl7PIDType.getMothersMaidenName().getFirst().getHL7FamilyName()!=null) {
                surname = hl7PIDType.getMothersMaidenName().getFirst().getHL7FamilyName().getHL7Surname();
            }
            String givenName = hl7PIDType.getMothersMaidenName().getFirst().getHL7GivenName();
            String motherMaidenNm = "";
            if(surname!= null) {
                motherMaidenNm = surname;
            }
            if(givenName!= null) {
                motherMaidenNm =  motherMaidenNm + " " + givenName;
            }
            personContainer.getThePersonDto().setMothersMaidenNm(motherMaidenNm.trim());
        }

        //Setup Person Birth Order
        if(hl7PIDType.getBirthOrder()!=null && hl7PIDType.getBirthOrder().getHL7Numeric() != null) {
            personContainer.getThePersonDto().setBirthOrderNbr(hl7PIDType.getBirthOrder().getHL7Numeric().intValue());
        }
        if(hl7PIDType.getMultipleBirthIndicator()!=null){
            personContainer.getThePersonDto().setMultipleBirthInd(hl7PIDType.getMultipleBirthIndicator());
        }


        //Setup Person Account Number
        if(hl7PIDType.getPatientAccountNumber()!=null){
            int j = 1;
            if(personContainer.getTheEntityIdDtoCollection()!=null ) {
                j= personContainer.getTheEntityIdDtoCollection().size();
            }
            EntityIdDto entityIdDto = nbsObjectConverter.processEntityData(hl7PIDType.getPatientAccountNumber(), personContainer, EdxELRConstant.ELR_ACCOUNT_IDENTIFIER, j);
            if(entityIdDto.getEntityUid() != null) {
                personContainer.getTheEntityIdDtoCollection().add(entityIdDto);
            }
        }

        //Setup Person Address
        List<HL7XADType> addressArray = hl7PIDType.getPatientAddress();
        Collection<Object> addressCollection = new ArrayList<>();

        if (!addressArray.isEmpty()) {
            HL7XADType addressType = addressArray.getFirst();
            nbsObjectConverter.personAddressType(addressType, EdxELRConstant.ELR_PATIENT_CD, personContainer);
        }
        //Setup Person Deceased Status
        personContainer.getThePersonDto().setDeceasedIndCd(hl7PIDType.getPatientDeathIndicator());
        if (hl7PIDType.getPatientDeathDateAndTime() != null) {
            personContainer.getThePersonDto().setDeceasedTime(nbsObjectConverter.processHL7TSType(hl7PIDType
                    .getPatientDeathDateAndTime(), EdxELRConstant.DATE_VALIDATION_PATIENT_DEATH_DATE_AND_TIME_MSG));
        }

        //Setup Person Names
        List<HL7XPNType> nameArray = hl7PIDType.getPatientName();
        for (HL7XPNType hl7XPNType : nameArray) {
            nbsObjectConverter.mapPersonNameType(hl7XPNType, personContainer);
        }

        //Setup Person Business Phone Number
        if(hl7PIDType.getPhoneNumberBusiness() != null){
            List<HL7XTNType> phoneBusinessArray = hl7PIDType.getPhoneNumberBusiness();
            if (phoneBusinessArray != null && !phoneBusinessArray.isEmpty()) {
                HL7XTNType phoneType = phoneBusinessArray.getFirst();
                EntityLocatorParticipationDto elpDT = nbsObjectConverter.personTelePhoneType(phoneType, EdxELRConstant.ELR_PATIENT_CD, personContainer);
                elpDT.setUseCd(NEDSSConstant.WORK_PHONE);
                addressCollection.add(elpDT);
            }
        }

        //Setup Person Home Phone Number
        if(hl7PIDType.getPhoneNumberHome()!=null ){
            List<HL7XTNType> phoneHomeArray = hl7PIDType.getPhoneNumberHome();
            if (!phoneHomeArray.isEmpty()) {
                HL7XTNType phoneType = phoneHomeArray.getFirst();
                EntityLocatorParticipationDto elpDT = nbsObjectConverter.personTelePhoneType(phoneType, EdxELRConstant.ELR_PATIENT_CD, personContainer);
                elpDT.setUseCd(NEDSSConstant.HOME);
                addressCollection.add(elpDT);
            }
        }

        //Setup Person Race
        if(hl7PIDType.getRace() != null){
            Collection<PersonRaceDto> raceColl = new ArrayList<>();
            List<HL7CWEType> raceArray = hl7PIDType.getRace();
            PersonRaceDto raceDT;
            for (HL7CWEType hl7CWEType : raceArray) {
                try {
                    raceDT = nbsObjectConverter.raceType(hl7CWEType, personContainer);
                    raceDT.setPersonUid(personContainer.getThePersonDto().getPersonUid());
                    ElrXref elrXrefForRace;

                    String keyRace = "ELR_LCA_RACE_" + raceDT.getRaceCategoryCd() + "_P_RACE_CAT";
                    ElrXref resultRace = (ElrXref) cacheApiService.getSrteCacheObject(ObjectName.ELR_XREF.name(), keyRace);
                    if (resultRace == null) {
                        resultRace = new ElrXref();
                    }
                    elrXrefForRace = resultRace;

                    String newRaceCat = elrXrefForRace.getToCode();
                    if (newRaceCat != null && !newRaceCat.trim().isEmpty()) {
                        raceDT.setRaceCd(newRaceCat);
                        raceDT.setRaceCategoryCd(newRaceCat);
                    }

                    if (!cacheApiService.getSrteCacheBool(ObjectName.RACE_CODES.name(), raceDT.getRaceCd())) {
                        edxLabInformationDto.setRaceTranslated(false);
                    }
                    raceColl.add(raceDT);
                } catch (Exception e) {
                    throw new DataProcessingException(e.getMessage(),e);
                }// end of catch
            }
            personContainer.setThePersonRaceDtoCollection(raceColl);
        }

        if(labResultProxyContainer.getThePersonContainerCollection()==null){
            labResultProxyContainer.setThePersonContainerCollection(new ArrayList<>());
        }
        labResultProxyContainer.getThePersonContainerCollection().add(personContainer);

        return labResultProxyContainer;
    }

    /**
     * This method process and parse data from EdxLabInformation to ParsonVO and LabResultProxyVO
     *  - Person Object
     *  - Role Object (part of Lab Result, this is a list)
     * */
    @SuppressWarnings("java:S3776")
    public PersonContainer parseToPersonObject(LabResultProxyContainer labResultProxyContainer,
                                               EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        PersonContainer personContainer = new PersonContainer();
        try {
            PersonDto personDto = personContainer.getThePersonDto();
            personContainer.getThePersonDto().setElectronicInd(ELRConstant.ELECTRONIC_IND);

            // Check patient object ROLE: PAT, NOK, PROVIDER
            // Then parsing data to PersonVO and DTO
            if (edxLabInformationDto.getRole().equalsIgnoreCase(NEDSSConstant.PAT) ) {
                personContainer.getThePersonDto().setCd(NEDSSConstant.PAT);
                personDto.setCd(EdxELRConstant.ELR_PATIENT_CD);
                personDto.setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
                personDto.setPersonUid(edxLabInformationDto.getPatientUid());
            }
            else if (edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)){
                personContainer.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
                personDto.setCd(EdxELRConstant.ELR_PATIENT_CD);
                personDto.setCdDescTxt(EdxELRConstant.ELR_NOK_DESC);
                personDto.setPersonUid((long) edxLabInformationDto.getNextUid());
            }
            else {
                personContainer.getThePersonDto().setCd(EdxELRConstant.ELR_PROVIDER_CD);
                personDto.setCd(EdxELRConstant.ELR_PROVIDER_CD);
                personDto.setCdDescTxt(EdxELRConstant.ELR_PROVIDER_DESC);
                personDto.setPersonUid((long) edxLabInformationDto.getNextUid());
            }

            personContainer.setItDirty(false);
            personContainer.getThePersonDto().setItNew(true);
            personContainer.getThePersonDto().setItDirty(false);
            personContainer.setItNew(true);

            personContainer.getThePersonDto().setVersionCtrlNbr(1);
            personContainer.getThePersonDto().setItNew(true);
            personContainer.getThePersonDto().setLastChgTime(edxLabInformationDto.getAddTime());
            personContainer.getThePersonDto().setAddTime(edxLabInformationDto.getAddTime());
            personContainer.getThePersonDto().setLastChgUserId(edxLabInformationDto.getUserId());
            personContainer.getThePersonDto().setAddUserId(edxLabInformationDto.getUserId());
            personContainer.getThePersonDto().setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            personContainer.getThePersonDto().setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            personContainer.getThePersonDto().setStatusTime(personContainer.getThePersonDto().getLastChgTime());

            personDto.setLastChgTime(edxLabInformationDto.getAddTime());
            personDto.setLastChgUserId(edxLabInformationDto.getUserId());
            personDto.setAsOfDateAdmin(edxLabInformationDto.getAddTime());
            personDto.setAsOfDateEthnicity(edxLabInformationDto.getAddTime());
            personDto.setAsOfDateGeneral(edxLabInformationDto.getAddTime());
            personDto.setAsOfDateMorbidity(edxLabInformationDto.getAddTime());
            personDto.setAsOfDateSex(edxLabInformationDto.getAddTime());
            personDto.setAddTime(edxLabInformationDto.getAddTime());
            personDto.setAddUserId(edxLabInformationDto.getUserId());


            // Parsing to ROLE Object
            RoleDto roleDto = new RoleDto();
            roleDto.setSubjectEntityUid(personDto.getPersonUid());
            roleDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
            boolean addRole= false;

            if (edxLabInformationDto.getRole().equalsIgnoreCase(NEDSSConstant.PAT)) {
                roleDto.setCd(NEDSSConstant.PAT);
                roleDto.setCdDescTxt(EdxELRConstant.ELR_PATIENT);
                roleDto.setSubjectClassCd(EdxELRConstant.ELR_PATIENT);
                addRole= true;
            }
            else if (edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                roleDto.setSubjectClassCd(EdxELRConstant.ELR_CON);
                if(edxLabInformationDto.getRelationship()!=null) {
                    roleDto.setCd(edxLabInformationDto.getRelationship());
                }
                else {
                    roleDto.setCd(EdxELRConstant.ELR_NEXT_F_KIN_ROLE_CD);
                }
                if(edxLabInformationDto.getRelationshipDesc()!=null) {
                    roleDto.setCdDescTxt(edxLabInformationDto.getRelationshipDesc());
                }
                else {
                    roleDto.setCdDescTxt(EdxELRConstant.ELR_NEXT_F_KIN_ROLE_DESC);
                }
                roleDto.setScopingRoleSeq(1);
                roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
                roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                addRole= true;
            }
            else if (edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD)) {
                roleDto.setCd(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);
                roleDto.setSubjectClassCd(EdxELRConstant.ELR_PROVIDER_CD);
                roleDto.setCdDescTxt(EdxELRConstant.ELR_SPECIMEN_PROCURER_DESC);
                roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
                roleDto.setScopingRoleSeq(1);
                addRole= true;
            }
            else if (edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_PROVIDER_CD) ||
                    edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_VERIFIER_CD)||
                    edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_ASSISTANT_CD) ||
                    edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_PERFORMER_CD) ||
                    edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_LAB_ENTERER_CD)
            )
            {
                roleDto.setCd(EdxELRConstant.ELR_LAB_PROVIDER_CD);
                roleDto.setSubjectClassCd(EdxELRConstant.ELR_PROVIDER_CD);
                roleDto.setCdDescTxt(EdxELRConstant.ELR_LAB_PROVIDER_DESC);
                roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
                roleDto.setScopingRoleSeq(1);
                addRole= true;
            }
            else if (edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)) {
                roleDto.setCd(EdxELRConstant.ELR_OP_CD);
                roleDto.setSubjectClassCd(EdxELRConstant.ELR_PROVIDER_CD);
                roleDto.setCdDescTxt(EdxELRConstant.ELR_OP_DESC);
                roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingRoleSeq(1);
                roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
                roleDto.setScopingRoleSeq(1);
                addRole= true;
            }
            else if (edxLabInformationDto.getRole().equalsIgnoreCase(EdxELRConstant.ELR_COPY_TO_CD)) {
                roleDto.setCd(EdxELRConstant.ELR_COPY_TO_CD);
                roleDto.setSubjectClassCd(EdxELRConstant.ELR_PROV_CD);
                roleDto.setCdDescTxt(EdxELRConstant.ELR_COPY_TO_DESC);
                roleDto.setScopingClassCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingRoleCd(EdxELRConstant.ELR_PATIENT_CD);
                roleDto.setScopingRoleSeq(1);
                roleDto.setScopingEntityUid(edxLabInformationDto.getPatientUid());
                roleDto.setScopingRoleSeq(1);
                addRole= true;
            }

            roleDto.setAddUserId(AuthUtil.authUser.getNedssEntryId());
            roleDto.setAddReasonCd(EdxELRConstant.ELR_ADD_REASON_CD);
            roleDto.setRoleSeq(1L);
            roleDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            roleDto.setSubjectEntityUid(personDto.getPersonUid());
            roleDto.setItNew(true);
            roleDto.setItDirty(false);

            if(addRole){
                labResultProxyContainer.getTheRoleDtoCollection().add(roleDto);
            }

        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return personContainer;
    }


    public LabResultProxyContainer getNextOfKinVO(HL7NK1Type hl7NK1Type,
                                                  LabResultProxyContainer labResultProxyContainer,
                                                  EdxLabInformationDto edxLabInformationDto) throws DataProcessingException {
        try {
            edxLabInformationDto.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
            if(hl7NK1Type.getRelationship()!=null){
                edxLabInformationDto.setRelationship(hl7NK1Type.getRelationship().getHL7Identifier());
                String desc= checkingValueService.getCodeDescTxtForCd(edxLabInformationDto.getRelationship(), EdxELRConstant.ELR_NEXT_OF_KIN_RL_CLASS);
                if(desc!=null && !desc.trim().isEmpty() && hl7NK1Type.getRelationship().getHL7Text()==null) {
                    edxLabInformationDto.setRelationshipDesc(desc);
                }
                else if(hl7NK1Type.getRelationship().getHL7Text()!=null) {
                    edxLabInformationDto.setRelationshipDesc(hl7NK1Type.getRelationship().getHL7Text());
                }
            }
            PersonContainer personContainer = parseToPersonObject(labResultProxyContainer, edxLabInformationDto);

            List<HL7XADType> addressArray = hl7NK1Type.getAddress();
            Collection<Object> addressCollection = new ArrayList<>();
            if (!addressArray.isEmpty()) {
                HL7XADType addressType = addressArray.getFirst();
                nbsObjectConverter.personAddressType(addressType, EdxELRConstant.ELR_NEXT_OF_KIN, personContainer);
            }

            List<HL7XPNType> nameArray = hl7NK1Type.getName();
            if (!nameArray.isEmpty()) {
                HL7XPNType hl7XPNType = nameArray.getFirst();
                nbsObjectConverter.mapPersonNameType(hl7XPNType, personContainer);
            }

            List<HL7XTNType> phoneHomeArray = hl7NK1Type.getPhoneNumber();
            if (!phoneHomeArray.isEmpty()) {
                HL7XTNType phoneType = phoneHomeArray.getFirst();
                EntityLocatorParticipationDto elpDT = nbsObjectConverter.personTelePhoneType(phoneType, EdxELRConstant.ELR_NEXT_OF_KIN, personContainer);
                addressCollection.add(elpDT);
            }
            labResultProxyContainer.getThePersonContainerCollection().add(personContainer);
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return labResultProxyContainer;
    }

}