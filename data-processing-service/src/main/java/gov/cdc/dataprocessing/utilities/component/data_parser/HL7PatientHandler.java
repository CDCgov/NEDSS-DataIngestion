package gov.cdc.dataprocessing.utilities.component.data_parser;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
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
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.data_parser.util.EntityIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static gov.cdc.dataprocessing.cache.SrteCache.findRecordForElrXrefsList;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class HL7PatientHandler {
    private static final Logger logger = LoggerFactory.getLogger(HL7PatientHandler.class);

    private final ICatchingValueService checkingValueService;
    private final NBSObjectConverter nbsObjectConverter;
    private final EntityIdUtil entityIdUtil;


    public HL7PatientHandler(ICatchingValueService checkingValueService,
                             NBSObjectConverter nbsObjectConverter,
                             EntityIdUtil entityIdUtil) {
        this.checkingValueService = checkingValueService;
        this.nbsObjectConverter = nbsObjectConverter;
        this.entityIdUtil = entityIdUtil;
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
            if (hl7PatientResult != null && hl7PatientResult.getPATIENT() != null) {
                // Processing Patient Identification
                if (hl7PatientResult.getPATIENT().getPatientIdentification() != null) {
                    HL7PIDType patientInfo = hl7PatientResult.getPATIENT().getPatientIdentification();
                    getPatient(patientInfo, labResultProxyContainer, edxLabInformationDto);
                }
                // Processing Next of kin
                if (hl7PatientResult.getPATIENT().getNextofKinAssociatedParties() != null) {
                    List<HL7NK1Type> hl7NK1TypeList = hl7PatientResult.getPATIENT().getNextofKinAssociatedParties();
                    // Only need the first index
                    if (!hl7NK1TypeList.isEmpty()) {
                        HL7NK1Type hl7NK1Type = hl7NK1TypeList.get(0);
                        if (hl7NK1Type.getName() != null && !hl7NK1Type.getName().isEmpty()) {
                            getNextOfKinVO(hl7NK1Type, labResultProxyContainer, edxLabInformationDto);
                        }
                    }
                }
            }
        return labResultProxyContainer;
    }
    @SuppressWarnings("java:S3776")
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
                    String SSNNumberinit = entityIdDto.getRootExtensionTxt().replace("-", "");
                    String SSNNumber =SSNNumberinit.replace(" ", "");
                    try {
                        if(SSNNumber.length()!=9) {
                            edxLabInformationDto.setSsnInvalid(true);
                        }
                        Integer.parseInt(SSNNumber);
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
            //participationDto.setAddUserId(EdxELRConstant.ELR_ADD_USER_ID);
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
                var primaryLang = langList.get(0);
                personContainer.getThePersonDto().setPrimLangCd(primaryLang.getHL7Identifier());
                personContainer.getThePersonDto().setPrimLangDescTxt(primaryLang.getHL7Text());

                if (personContainer.getThePersonDto().getPrimLangCd().equals("ENG")) {
                    personContainer.getThePersonDto().setSpeaksEnglishCd("Y");
                } else {
                    personContainer.getThePersonDto().setSpeaksEnglishCd("N");
                }
            }

            // Setup Person Sex Code
            ElrXref elrXref = new ElrXref();
            var result = findRecordForElrXrefsList("ELR_LCA_SEX", personContainer.getThePersonDto().getCurrSexCd(), "P_SEX");

            if (result.isPresent()) {
                elrXref = result.get();
            }
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
            if(hl7PIDType.getBirthPlace()!=null && !hl7PIDType.getBirthPlace().trim().equals("")){
                nbsObjectConverter.setPersonBirthType(hl7PIDType.getBirthPlace(), personContainer);
            }

            // Setup Person Ethnic Group
            Collection<PersonEthnicGroupDto> ethnicColl = new ArrayList<>();
            List<HL7CWEType> ethnicArray = hl7PIDType.getEthnicGroup();
            for (HL7CWEType ethnicType : ethnicArray) {
                PersonEthnicGroupDto personEthnicGroupDto = nbsObjectConverter.ethnicGroupType(ethnicType, personContainer);
                ElrXref elrXrefForEthnic = new ElrXref();
                var res = findRecordForElrXrefsList("ELR_LCA_ETHN_GRP",personEthnicGroupDto.getEthnicGroupCd(), "P_ETHN_GRP");
                if (res.isPresent()) {
                    elrXrefForEthnic = res.get();
                }
                String ethnicGroupCd = elrXrefForEthnic.getToCode();
                if (ethnicGroupCd != null && !ethnicGroupCd.trim().equals("")) {
                    personEthnicGroupDto.setEthnicGroupCd(ethnicGroupCd);
                }
                if (personEthnicGroupDto.getEthnicGroupCd() != null && !personEthnicGroupDto.getEthnicGroupCd().trim().equals("")) {
                    var map = checkingValueService.getCodedValues("P_ETHN_GRP", personEthnicGroupDto.getEthnicGroupCd());
                    if (map.containsKey(personEthnicGroupDto.getEthnicGroupCd())) {
                        edxLabInformationDto.setEthnicityCodeTranslated(false);
                    }
                }
                if (personEthnicGroupDto.getEthnicGroupCd() != null
                        && !personEthnicGroupDto.getEthnicGroupCd().trim().equals("")
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
                HL7XADType addressType = addressArray.get(0);
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
                    HL7XTNType phoneType = phoneBusinessArray.get(0);
                    EntityLocatorParticipationDto elpDT = nbsObjectConverter.personTelePhoneType(phoneType, EdxELRConstant.ELR_PATIENT_CD, personContainer);
                    elpDT.setUseCd(NEDSSConstant.WORK_PHONE);
                    addressCollection.add(elpDT);
                }
            }

            //Setup Person Home Phone Number
            if(hl7PIDType.getPhoneNumberHome()!=null ){
                List<HL7XTNType> phoneHomeArray = hl7PIDType.getPhoneNumberHome();
                if (!phoneHomeArray.isEmpty()) {
                    HL7XTNType phoneType = phoneHomeArray.get(0);
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
                        ElrXref elrXrefForRace = new ElrXref();
                        var res = findRecordForElrXrefsList("ELR_LCA_RACE", raceDT.getRaceCategoryCd(), "P_RACE_CAT");
                        if (res.isPresent()) {
                            elrXrefForRace = res.get();
                        }
                        String newRaceCat = elrXrefForRace.getToCode();
                        if (newRaceCat != null && !newRaceCat.trim().equals("")) {
                            raceDT.setRaceCd(newRaceCat);
                            raceDT.setRaceCategoryCd(newRaceCat);
                        }
                        var codeMap = SrteCache.raceCodesMap;
                        if (!codeMap.containsKey(raceDT.getRaceCd())) {
                            edxLabInformationDto.setRaceTranslated(false);
                        }
                        raceColl.add(raceDT);
                    } catch (Exception e) {
                        logger.error("Exception thrown by HL7PatientProcessor.getPatientVO  getting race information" + e);
                        throw new DataProcessingException(
                                "Exception thrown at HL7PatientProcessor.getPatientVO getting race information:" + e);
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
            logger.error("Exception thrown by HL7ORCProcessor.personVO " + e);
            throw new DataProcessingException("Exception thrown at HL7PatientProcessor.personVO:"+ e);
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
                if(desc!=null && desc.trim().length()>0 && hl7NK1Type.getRelationship().getHL7Text()==null) {
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
                HL7XADType addressType = addressArray.get(0);
                nbsObjectConverter.personAddressType(addressType, EdxELRConstant.ELR_NEXT_OF_KIN, personContainer);
            }

            List<HL7XPNType> nameArray = hl7NK1Type.getName();
            if (!nameArray.isEmpty()) {
                HL7XPNType hl7XPNType = nameArray.get(0);
                nbsObjectConverter.mapPersonNameType(hl7XPNType, personContainer);
            }

            List<HL7XTNType> phoneHomeArray = hl7NK1Type.getPhoneNumber();
            if (!phoneHomeArray.isEmpty()) {
                HL7XTNType phoneType = phoneHomeArray.get(0);
                EntityLocatorParticipationDto elpDT = nbsObjectConverter.personTelePhoneType(phoneType, EdxELRConstant.ELR_NEXT_OF_KIN, personContainer);
                addressCollection.add(elpDT);
            }
            labResultProxyContainer.getThePersonContainerCollection().add(personContainer);
        } catch (Exception e) {
            logger.error("Exception thrown by HL7PatientProcessor.getNextOfKinVO "
                    + e);
            throw new DataProcessingException("Exception thrown at HL7PatientProcessor.getNextOfKinVO:"+ e);
        }
        return labResultProxyContainer;
    }

}
