package gov.cdc.dataprocessing.utilities.component.data_extraction;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.*;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.locator.TeleLocatorDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.model.phdc.*;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;
import gov.cdc.dataprocessing.service.interfaces.ICheckingValueService;
import gov.cdc.dataprocessing.utilities.data_extraction.EntityIdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Component
public class NBSObjectConverter {
    private static final Logger logger = LoggerFactory.getLogger(NBSObjectConverter.class);

    private final ICheckingValueService checkingValueService;

    public NBSObjectConverter(ICheckingValueService checkingValueService) {
        this.checkingValueService = checkingValueService;
    }

    public PersonContainer mapPersonNameType(HL7XPNType hl7XPNType, PersonContainer personContainer) throws DataProcessingException {
        PersonNameDto personNameDto = new PersonNameDto();
        HL7FNType hl7FamilyName = hl7XPNType.getHL7FamilyName();
        /** Optional maxOccurs="1 */
        if(hl7FamilyName!=null){
            personNameDto.setLastNm(hl7FamilyName.getHL7Surname());
            personNameDto.setLastNm2(hl7FamilyName.getHL7OwnSurname());
        }
        /** length"194 */
        personNameDto.setFirstNm(hl7XPNType.getHL7GivenName());
        /** Optional maxOccurs="1 */
        /** length"30 */
        String hl7SecondAndFurtherGivenNamesOrInitialsThereof = hl7XPNType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof();
        /** Optional maxOccurs="1 */
        /** length"30 */
        personNameDto.setMiddleNm(hl7SecondAndFurtherGivenNamesOrInitialsThereof);
        String hl7Suffix = hl7XPNType.getHL7Suffix();
        /** Optional maxOccurs="1 */
        /** length"20 */
        personNameDto.setNmSuffix(hl7Suffix);

        String hl7Prefix = hl7XPNType.getHL7Prefix();
        /** Optional maxOccurs="1 */
        /** length"20 */
        personNameDto.setNmPrefix(hl7Prefix);

        /** Optional maxOccurs="1 */
        /** length"6 */
        String hl7NameTypeCode = hl7XPNType.getHL7NameTypeCode();
        personNameDto.setNmUseCd(Objects.requireNonNullElse(hl7NameTypeCode, EdxELRConstant.ELR_LEGAL_NAME));

        String toCode = checkingValueService.findToCode("ELR_LCA_NM_USE", personNameDto.getNmUseCd(), "P_NM_USE");
        if(toCode!=null && !toCode.isEmpty()){
            personNameDto.setNmUseCd(toCode);
        }
        /** length"1 */
        HL7TSType hl7EffectiveDate = hl7XPNType.getHL7EffectiveDate();
        /** Optional maxOccurs="1 */
        /** length"26 */
        Timestamp timestamp = processHL7TSType(hl7EffectiveDate, EdxELRConstant.DATE_VALIDATION_PERSON_NAME_FROM_TIME_MSG);
        personNameDto.setFromTime(timestamp);
        HL7TSType hl7ExpirationDate = hl7XPNType.getHL7ExpirationDate();
        /** Optional maxOccurs="1 */
        /** length"26 */
        Timestamp toTimestamp = processHL7TSType(hl7ExpirationDate, EdxELRConstant.DATE_VALIDATION_PERSON_NAME_TO_TIME_MSG);
        personNameDto.setToTime(toTimestamp);
        /** Optional maxOccurs="1 */
        /** length"199 */
        personNameDto.setAddTime(personContainer.getThePersonDto().getAddTime());
        personNameDto.setAddReasonCd(EdxELRConstant.ADD_REASON_CD);
        personNameDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        personNameDto.setAsOfDate(personContainer.getThePersonDto().getLastChgTime());
        personNameDto.setAddUserId(personContainer.getThePersonDto().getAddUserId());
        personNameDto.setItNew(true);
        personNameDto.setItDirty(false);
        personNameDto.setLastChgTime(personContainer.getThePersonDto().getLastChgTime());
        personNameDto.setLastChgUserId(personContainer.getThePersonDto()
                .getLastChgUserId());
        int seq = 0;
        if (personContainer.getThePersonNameDtoCollection() == null) {
            personContainer.setThePersonNameDtoCollection(new ArrayList<>());
        } else {
            seq = personContainer.getThePersonNameDtoCollection().size();
        }
        personNameDto.setPersonNameSeq(seq + 1);

        personContainer.getThePersonNameDtoCollection().add(personNameDto);

        if (personNameDto.getNmUseCd()!=null && personNameDto.getNmUseCd().equals(EdxELRConstant.ELR_LEGAL_NAME)) {
            personContainer.getThePersonDto().setLastNm(personNameDto.getLastNm());
            personContainer.getThePersonDto().setFirstNm(personNameDto.getFirstNm());
            personContainer.getThePersonDto().setNmPrefix(personNameDto.getNmPrefix());
            personContainer.getThePersonDto().setNmSuffix(personNameDto.getNmSuffix());
            personContainer.getThePersonDto().setMiddleNm(personNameDto.getMiddleNm());
        }
        return personContainer;
    }

    public EntityIdDto processEntityData(HL7CXType hl7CXType, PersonContainer personContainer, String indicator, int j) throws DataProcessingException {
        EntityIdDto entityIdDto = new EntityIdDto();
        if (hl7CXType != null ) {
            entityIdDto.setEntityUid(personContainer.getThePersonDto().getPersonUid());
            entityIdDto.setAddTime(personContainer.getThePersonDto().getAddTime());
            entityIdDto.setEntityIdSeq(j + 1);
            entityIdDto.setRootExtensionTxt(hl7CXType.getHL7IDNumber());
            if(hl7CXType.getHL7AssigningAuthority()!=null){
                entityIdDto.setAssigningAuthorityCd(hl7CXType.getHL7AssigningAuthority().getHL7UniversalID());
                entityIdDto.setAssigningAuthorityDescTxt(hl7CXType.getHL7AssigningAuthority().getHL7NamespaceID());
                entityIdDto.setAssigningAuthorityIdType(hl7CXType.getHL7AssigningAuthority().getHL7UniversalIDType());
            }
            if (indicator != null && indicator.equals(EdxELRConstant.ELR_PATIENT_ALTERNATE_IND)) {
                entityIdDto.setTypeCd(EdxELRConstant.ELR_PATIENT_ALTERNATE_TYPE);
                entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_PATIENT_ALTERNATE_DESC);
            }
            else if (indicator != null && indicator.equals(EdxELRConstant.ELR_MOTHER_IDENTIFIER)) {
                entityIdDto.setTypeCd(EdxELRConstant.ELR_MOTHER_IDENTIFIER);
                entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_MOTHER_IDENTIFIER);
            } else if (indicator != null && indicator.equals(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER)) {
                entityIdDto.setTypeCd(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER);
                entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_ACCOUNT_DESC);
            }
            else if (hl7CXType.getHL7IdentifierTypeCode() == null || hl7CXType.getHL7IdentifierTypeCode().trim().equals("")) {
                entityIdDto.setTypeCd(EdxELRConstant.ELR_PERSON_TYPE);
                entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_PERSON_TYPE_DESC);
                String typeCode = checkingValueService.getCodeDescTxtForCd(entityIdDto.getTypeCd(), EdxELRConstant.EI_TYPE);
                if (typeCode == null || typeCode.trim().equals("")) {
                    entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_CLIA_DESC);
                } else {
                    entityIdDto.setTypeDescTxt(typeCode);
                }
            } else {
                entityIdDto.setTypeCd(hl7CXType.getHL7IdentifierTypeCode());
            }

            entityIdDto.setAddUserId(personContainer.getThePersonDto().getAddUserId());
            entityIdDto.setLastChgUserId(personContainer.getThePersonDto().getLastChgUserId());
            entityIdDto.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDto.setStatusTime(personContainer.getThePersonDto().getAddTime());
            entityIdDto.setRecordStatusTime(personContainer.getThePersonDto().getAddTime());
            entityIdDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
            entityIdDto.setAsOfDate(personContainer.getThePersonDto().getAddTime());
            entityIdDto.setEffectiveFromTime(EntityIdHandler.processHL7DTType(hl7CXType.getHL7EffectiveDate(), EdxELRConstant.DATE_VALIDATION_PID_PATIENT_IDENTIFIER_EFFECTIVE_DATE_TIME_MSG));
            entityIdDto.setValidFromTime(entityIdDto.getEffectiveFromTime());
            entityIdDto.setEffectiveToTime(EntityIdHandler.processHL7DTType(hl7CXType.getHL7ExpirationDate(), EdxELRConstant.DATE_VALIDATION_PID_PATIENT_IDENTIFIER_EXPIRATION_DATE_TIME_MSG));
            entityIdDto.setValidToTime(entityIdDto.getEffectiveToTime());
            entityIdDto.setItNew(true);
            entityIdDto.setItDirty(false);
        }
        return entityIdDto;
    }

    /**
     * Parsing Entity Address into Object
     * */
    public EntityLocatorParticipationDto addressType(HL7XADType hl7XADType, String role)   {

        EntityLocatorParticipationDto elp = new EntityLocatorParticipationDto();
        try {
            elp.setItNew(true);
            elp.setItDirty(false);
            elp.setAddTime(new Timestamp(new Date().getTime()));
            elp.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
            elp.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);

            String addressType = hl7XADType.getHL7AddressType();
            /** Optional maxOccurs="1 */
            /** length"3 */

            if (role.equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)) {
                elp.setClassCd(EdxELRConstant.ELR_POSTAL_CD);
                elp.setUseCd(EdxELRConstant.ELR_WORKPLACE_CD);
                elp.setCd(EdxELRConstant.ELR_OFFICE_CD);
                elp.setCdDescTxt(EdxELRConstant.ELR_OFFICE_DESC);
            } else if (role.equalsIgnoreCase(EdxELRConstant.ELR_NEXT_OF_KIN)) {
                elp.setClassCd(EdxELRConstant.ELR_POSTAL_CD);
                elp.setUseCd(EdxELRConstant.ELR_USE_EMERGENCY_CONTACT_CD);
                elp.setCd(EdxELRConstant.ELR_HOUSE_CD);
                elp.setCdDescTxt(EdxELRConstant.ELR_HOUSE_DESC);
            } else {
                elp.setCd(Objects.requireNonNullElse(addressType, EdxELRConstant.ELR_HOUSE_CD));
                elp.setClassCd(NEDSSConstant.POSTAL);
                elp.setUseCd(NEDSSConstant.HOME);
            }

            PostalLocatorDto pl = new PostalLocatorDto();
            pl.setItNew(true);
            pl.setItDirty(false);
            pl.setAddTime(new Timestamp(new Date().getTime()));
            pl.setRecordStatusTime(new Timestamp(new Date().getTime()));

            pl.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
            HL7SADType HL7StreetAddress = hl7XADType.getHL7StreetAddress();
            /** Optional maxOccurs="1 */
            /** length"184 */
            if(HL7StreetAddress!=null){
                pl = nbsStreetAddressType(HL7StreetAddress, pl);
            }
            if(hl7XADType.getHL7OtherDesignation()!=null && (pl.getStreetAddr2()==null || pl.getStreetAddr2().trim().equalsIgnoreCase(""))) {
                pl.setStreetAddr2(hl7XADType.getHL7OtherDesignation());
            }

            String city = hl7XADType.getHL7City();
            /** Optional maxOccurs="1 */
            /** length"50 */
            pl.setCityDescTxt(city);
            String stateOrProvince = hl7XADType.getHL7StateOrProvince();
            /** Optional maxOccurs="1 */
            /** length"50 */

            String state="";
            if(stateOrProvince!=null) {
                state= translateStateCd(stateOrProvince);
            }
            pl.setStateCd(state);
            String zip = hl7XADType.getHL7ZipOrPostalCode();
            /** Optional maxOccurs="1 */
            /** length"12 */

            pl.setZipCd(formatZip(zip));
            String country = hl7XADType.getHL7Country();
            if(country!=null && country.equalsIgnoreCase(EdxELRConstant.ELR_USA_DESC))
            {
                pl.setCntryCd(EdxELRConstant.ELR_USA_CD);
            }
            else
            {
                pl.setCntryCd(country);
            }
            String countyParishCode = hl7XADType.getHL7CountyParishCode();

            /** Optional maxOccurs="1 */
            /** length"20 */
            String cnty = checkingValueService.getCountyCdByDesc(countyParishCode,pl.getStateCd());
            if(cnty==null) {
                pl.setCntyCd(countyParishCode);
            }
            else {
                pl.setCntyCd(cnty);
            }
            String HL7CensusTract = hl7XADType.getHL7CensusTract();
            /** Optional maxOccurs="1 */
            /** length"20 */
            pl.setCensusTrackCd(HL7CensusTract);

            elp.setThePostalLocatorDto(pl);
        } catch (Exception e) {
            logger.error("Hl7ToNBSObjectConverter. Error thrown: "+ e);
        }
        return elp;
    }

    private PostalLocatorDto nbsStreetAddressType(HL7SADType hl7SADType, PostalLocatorDto pl) {

        String streetOrMailingAddress = hl7SADType.getHL7StreetOrMailingAddress();
        /** Optional maxOccurs="1 */
        /** length"120 */
        pl.setStreetAddr1(streetOrMailingAddress);
        String streetName = hl7SADType.getHL7StreetName();
        /** Optional maxOccurs="1 */
        /** length"50 */
        String dwellingNumber = hl7SADType.getHL7DwellingNumber();
        /** Optional maxOccurs="1 */
        /** length"12 */

        if(dwellingNumber==null) {
            dwellingNumber="";
        }
        if(streetName==null) {
            streetName="";
        }
        pl.setStreetAddr2(dwellingNumber + " " + streetName);
        return pl;
    }

    private String translateStateCd(String msgInStateCd) {
        if(msgInStateCd != null && !msgInStateCd.trim().isEmpty())
        {
            //TODO: Call out to State Code Repository here
            StateCode stateCode = checkingValueService.findStateCodeByStateNm(msgInStateCd);
            return stateCode.getStateCd();
        }
        else
        {
            return null;
        }
    }

    private String formatZip(String zip) {
        if (zip != null) {
            zip =zip.trim();
            // for zip code like: 12,123,1234,12345
            if (zip.length() <= 5) {
                return zip;
            }
            // for zip code like: 123456789
            else if (zip.length() == 9 && zip.indexOf("-") == -1) {
                zip = zip.substring(0, 5) + "-" + zip.substring(5, 9);
                // for zip code like: 123456,1234567890: Will ignore 12345-6789
            }
            else if (zip.length() > 5 && zip.indexOf("-") == -1) {
                zip = zip.substring(0, 5);
            }
        }// end of if
        return zip;
    }

    public EntityLocatorParticipationDto personAddressType(HL7XADType hl7XADType, String role, PersonContainer personContainer) {
        EntityLocatorParticipationDto elp = addressType(hl7XADType, role);
        elp.setEntityUid(personContainer.getThePersonDto().getPersonUid());
        elp.setAddUserId(personContainer.getThePersonDto().getAddUserId());
        elp.setAsOfDate(personContainer.getThePersonDto().getLastChgTime());
        if (elp.getThePostalLocatorDto() == null) {
            elp.setThePostalLocatorDto(new PostalLocatorDto());
        }
        elp.getThePostalLocatorDto().setAddUserId(personContainer.getThePersonDto().getAddUserId());
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(elp);
        return elp;
    }

    public EntityLocatorParticipationDto organizationAddressType(HL7XADType hl7XADType, String role, OrganizationVO organizationVO) {
        EntityLocatorParticipationDto elp = addressType(hl7XADType, role);
        elp.setEntityUid(organizationVO.getTheOrganizationDT().getOrganizationUid());
        elp.setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
        elp.setAsOfDate(organizationVO.getTheOrganizationDT().getLastChgTime());
        if (elp.getThePostalLocatorDto() == null) {
            elp.setThePostalLocatorDto(new PostalLocatorDto());
        }
        elp.getThePostalLocatorDto().setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
        organizationVO.getTheEntityLocatorParticipationDtoCollection().add(elp);
        return elp;
    }

    public static EntityIdDto validateSSN(EntityIdDto entityIdDto) {
        String ssn = entityIdDto.getRootExtensionTxt();
        if(ssn != null && !ssn.equals("") && !ssn.equals(" ")) {
            ssn =ssn.trim();
            if (ssn.length() > 3) {
                String newSSN = ssn.substring(0, 3);
                newSSN = newSSN + "-";
                if (ssn.length() > 5) {
                    newSSN = newSSN + ssn.replace("-", "").substring(3, 5) + "-";
                    newSSN = newSSN + ssn.replace("-", "").substring(5, (ssn.replace("-", "").length()));
                    ssn = newSSN;
                    entityIdDto.setRootExtensionTxt(ssn);
                }
                else {
                    newSSN = newSSN + ssn.replace("-", "").substring(3, ssn.length()) + "- ";
                    ssn = newSSN;
                    entityIdDto.setRootExtensionTxt(ssn);
                }
            }
            else {
                ssn = ssn + "- - ";
                entityIdDto.setRootExtensionTxt(ssn);
            }
        }//end of if
        return entityIdDto;
    }//end of while

    public static Timestamp processHL7TSTypeForDOBWithoutTime(HL7TSType time) throws DataProcessingException {
        Timestamp toTimestamp = null;
        String toTime = "";

        try {
            int year = -1;
            int month = -1;
            int date = -1;
            if (time != null) {
                if (time.getYear() != null) {
                    year = time.getYear().intValue();
                }
                if (time.getMonth() != null) {
                    month = time.getMonth().intValue();
                }
                if (time.getDay() != null) {
                    date = time.getDay().intValue();
                }
                if (year >= 0 && month >= 0 && date >= 0) {
                    toTime = month + "/" + date + "/" + year;
                    logger.debug("  in processHL7TSTypeForDOBWithoutTime: Date string is: " +toTime);
                    toTimestamp = EntityIdHandler.stringToStrutsTimestamp(toTime); //if can't process returns null
                }
            }
        } catch (Exception e) {
            logger.error("Hl7ToNBSObjectConverter.processHL7TSTypeForDOBWithoutTime failed as the date format is not right. Please check.!"+ toTime);
            throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7TSTypeForDOBWithoutTime failed as the date format is not right."+
                    EdxELRConstant.DATE_VALIDATION_PID_PATIENT_BIRTH_DATE_NO_TIME_MSG+toTime+"<--");
        }

        if (EntityIdHandler.isDateNotOkForDatabase(toTimestamp)) {
            throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7TSTypeForDOBWithoutTime " +EdxELRConstant.DATE_VALIDATION_PID_PATIENT_BIRTH_DATE_NO_TIME_MSG +toTime + EdxELRConstant.DATE_INVALID_FOR_DATABASE);
        }
        return toTimestamp;
    }

    public static EntityLocatorParticipationDto setPersonBirthType(String countryOfBirth, PersonContainer personContainer) {
        EntityLocatorParticipationDto elp = new EntityLocatorParticipationDto();

        elp.setItNew(true);
        elp.setItDirty(false);
        elp.setAddTime(new Timestamp(new Date().getTime()));
        elp.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        elp.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        elp.setClassCd(EdxELRConstant.ELR_TELE_CD);
        elp.setUseCd(NEDSSConstant.HOME);
        elp.setCd(EdxELRConstant.ELR_PHONE_CD);
        elp.setCdDescTxt(EdxELRConstant.ELR_PHONE_DESC);
        elp.setClassCd("PST") ;
        elp.setUseCd("BIR");
        elp.setCd("F");
        elp.setAddUserId(personContainer.getThePersonDto().getAddUserId());
        elp.setEntityUid(personContainer.getThePersonDto().getPersonUid());
        elp.setAsOfDate(personContainer.getThePersonDto().getLastChgTime());

        PostalLocatorDto pl = new PostalLocatorDto();
        pl.setItNew(true);
        pl.setItDirty(false);
        pl.setAddTime(new Timestamp(new Date().getTime()));
        pl.setRecordStatusTime(new Timestamp(new Date().getTime()));
        pl.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        pl.setCntryCd(countryOfBirth);
        pl.setAddUserId(personContainer.getThePersonDto().getAddUserId());
        elp.setThePostalLocatorDto(pl);
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(elp);
        return elp;
    }

    public static PersonEthnicGroupDto ethnicGroupType(HL7CWEType hl7CWEType,
                                                       PersonContainer personContainer) {
        PersonEthnicGroupDto ethnicGroupDT = new PersonEthnicGroupDto();
        ethnicGroupDT.setItNew(true);
        ethnicGroupDT.setItDirty(false);
        ethnicGroupDT.setAddReasonCd("Add");
        ethnicGroupDT.setRecordStatusCd(NEDSSConstant.ACTIVE);
        ethnicGroupDT.setPersonUid(personContainer.getThePersonDto().getPersonUid());
        ethnicGroupDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        ethnicGroupDT.setEthnicGroupCd(hl7CWEType.getHL7Identifier());
        ethnicGroupDT.setEthnicGroupDescTxt(hl7CWEType.getHL7Text());
        personContainer.getThePersonDto().setEthnicGroupInd(ethnicGroupDT.getEthnicGroupCd());

        return ethnicGroupDT;
    }

    public static Timestamp processHL7TSType(HL7TSType time, String itemDescription) throws DataProcessingException {
        String timeStr = "";
        try {
            Timestamp toTimestamp = null;
            java.util.Date date2;
            int year = -1;
            int month = -1;
            int day = -1;
            int hourOfDay = 0;
            int minute = 0;
            int second = 0;
            if (time != null) {
                if (time.getYear() != null)
                    year = time.getYear().intValue();
                if (time.getMonth() != null)
                    month = time.getMonth().intValue();
                if (time.getHours() != null)
                    hourOfDay = time.getHours().intValue();
                if (time.getDay() != null)
                    day = time.getDay().intValue();
                if (time.getMinutes() != null)
                    minute = time.getMinutes().intValue();
                if (time.getSeconds() != null)
                    second = time.getSeconds().intValue();


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                timeStr = year+"-"+month+"-"+day+" "+hourOfDay+":"+minute+":"+second;
                logger.debug("  in processHL7TSType: Date string is: " +timeStr);
                date2 = sdf.parse(timeStr);
                toTimestamp = new java.sql.Timestamp(date2.getTime());
                if (EntityIdHandler.isDateNotOkForDatabase(toTimestamp)) {
                    throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7TSType " +itemDescription +timeStr + EdxELRConstant.DATE_INVALID_FOR_DATABASE);
                }
            }
            return toTimestamp;
        } catch (Exception e) {
            logger.error("Hl7ToNBSObjectConverter.processHL7TSType failed as the date format is not right. Please check.!"+ timeStr);
            throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7TSType failed as the date format is not right. "+ itemDescription+timeStr);
        }
    }


    public static EntityLocatorParticipationDto personTelePhoneType(
            HL7XTNType hl7XTNType, String role, PersonContainer personContainer) {
        EntityLocatorParticipationDto elp = telePhoneType(hl7XTNType, role);
        elp.setAddUserId(personContainer.getThePersonDto().getAddUserId());
        elp.setEntityUid(personContainer.getThePersonDto().getPersonUid());
        elp.setAsOfDate(personContainer.getThePersonDto().getLastChgTime());
        if (elp.getTheTeleLocatorDto() == null) {
            elp.setTheTeleLocatorDto(new TeleLocatorDto());
        }
        elp.getTheTeleLocatorDto().setAddUserId(personContainer.getThePersonDto().getAddUserId());
        if (personContainer.getTheEntityLocatorParticipationDtoCollection() == null) {
            personContainer.setTheEntityLocatorParticipationDtoCollection(new ArrayList<>());
        }
        personContainer.getTheEntityLocatorParticipationDtoCollection().add(elp);
        return elp;
    }

    public static PersonRaceDto raceType(HL7CWEType hl7CEType, PersonContainer personContainer) {
        PersonRaceDto raceDT = new PersonRaceDto();
        raceDT.setItNew(true);
        raceDT.setItDelete(false);
        raceDT.setItDirty(false);
        raceDT.setAddTime(new Timestamp(new Date().getTime()));
        raceDT.setAddUserId(personContainer.getThePersonDto().getAddUserId());

        if (hl7CEType.getHL7Identifier() != null) {
            raceDT.setRaceCd(hl7CEType.getHL7Identifier());
            raceDT.setRaceDescTxt(hl7CEType.getHL7Text());
            raceDT.setRaceCategoryCd(hl7CEType.getHL7Identifier());
        } else {
            raceDT.setRaceCd(hl7CEType.getHL7AlternateIdentifier());
            raceDT.setRaceDescTxt(hl7CEType.getHL7AlternateText());
            raceDT.setRaceCategoryCd(hl7CEType.getHL7AlternateIdentifier());
        }



        raceDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        raceDT.setAsOfDate(personContainer.getThePersonDto().getAddTime());
        return raceDT;
    }

    public static EntityLocatorParticipationDto telePhoneType(
            HL7XTNType hl7XTNType, String role) {
        EntityLocatorParticipationDto elp = new EntityLocatorParticipationDto();
        TeleLocatorDto teleDT = new TeleLocatorDto();

        elp.setItNew(true);
        elp.setItDirty(false);
        elp.setAddTime(new Timestamp(new Date().getTime()));
        elp.setCd(NEDSSConstant.PHONE);

        elp.setClassCd(EdxELRConstant.ELR_TELE_CD);
        elp.setUseCd(NEDSSConstant.HOME);
        elp.setCd(EdxELRConstant.ELR_PHONE_CD);
        elp.setCdDescTxt(EdxELRConstant.ELR_PHONE_DESC);

        elp.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        elp.setStatusCd(NEDSSConstant.STATUS_ACTIVE);
        teleDT.setItNew(true);
        teleDT.setItDirty(false);
        teleDT.setAddTime(new Timestamp(new Date().getTime()));
        teleDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        elp.setTheTeleLocatorDto(teleDT);

        elp.setClassCd(NEDSSConstant.TELE);

        String emailAddress = hl7XTNType.getHL7EmailAddress();
        /** Optional maxOccurs="1 */
        /** length"199 */
        teleDT.setEmailAddress(emailAddress);
        String areaCode = "";
        String number = "";
        HL7NMType hl7AreaCityCode = hl7XTNType.getHL7AreaCityCode();
        HL7NMType hl7LocalNumber = hl7XTNType.getHL7LocalNumber();

        /** Optional maxOccurs="1 */
        /** length"5 */
        boolean incorrectLength;

        ArrayList<String> areaAndNumber = new ArrayList<String>();
        incorrectLength = checkIfAreaCodeMoreThan3Digits(areaAndNumber, hl7AreaCityCode);
        if(!incorrectLength)
            incorrectLength = checkIfNumberMoreThan10Digits(areaAndNumber, hl7LocalNumber);

        if(!incorrectLength){

            if (hl7AreaCityCode != null) {
                areaCode = String.valueOf(hl7AreaCityCode.getHL7Numeric().intValue());
            }
            hl7LocalNumber = hl7XTNType.getHL7LocalNumber();
            /** Optional maxOccurs="1 */
            /** length"9 */
            /*If the float is too long, like 10 digits, the float format would be somethign line 11.1F, and trying to convert it to String, in some cases, the precision
             * is not great, and the number changes. That is the reason I am treating the local number as String. NBSCentral defect related is #2758*/
            if (hl7LocalNumber != null) {

//                String localNumberString = hl7LocalNumber.toString();
//                int begin = localNumberString.indexOf(">");
//                if(begin!=-1){
//                    String subString1 = localNumberString.substring(begin+1);
//                    int end = subString1.indexOf("<");
//                    if(end!=-1)
//                        number = subString1.substring(0,end);
//                }
                //number = (String.format ("%.0f", hl7LocalNumber.getHL7Numeric()));

                number = hl7LocalNumber.getHL7Numeric().toString();

            }
        }
        else{
            areaCode = areaAndNumber.get(0);
            number = areaAndNumber.get(1);
        }

        if(areaCode!=null && areaCode.equalsIgnoreCase("0"))
            areaCode = "";

        String phoneNbrTxt = areaCode + number;

        String formattedPhoneNumber = formatPhoneNbr(phoneNbrTxt);
        teleDT.setPhoneNbrTxt(formattedPhoneNumber);

        HL7NMType extension = hl7XTNType.getHL7Extension();
        teleDT.setExtensionTxt(String.valueOf(extension.getHL7Numeric()));
        /** Optional maxOccurs="1 */
        /** length"5 */

        // teleDT.setExtensionTxt(extension.getHL7Numeric().getValue1()+"");
        String anyText = hl7XTNType.getHL7AnyText();
        /** Optional maxOccurs="1 */
        /** length"199 */
        teleDT.setUserAffiliationTxt(anyText);
        return elp;
    }

    public static boolean  checkIfNumberMoreThan10Digits(ArrayList<String> areaAndNumber,  HL7NMType HL7Type){


        boolean incorrectLength = false;
        String areaCode, number;

        if (HL7Type != null) {
            String areaCodeString = HL7Type.getHL7Numeric().toString();

            if(areaCodeString.length()>10){//Phone number more than 10 digits
                int length = areaCodeString.length();
                incorrectLength= true;

                areaCode = areaCodeString.substring(0,length-10);
                number = areaCodeString.substring(length-10);


                areaAndNumber.add(areaCode);
                areaAndNumber.add(number);

            }

            //if the phone number contains the area code, it seems to work fine because everything goes to the number and no math.round function is used, so the issue were the wrong number is created does not happen
        }


        return incorrectLength;

    }
    public static String formatPhoneNbr(String phoneNbrTxt) {
        // Format numeric number into telephone format
        // eg, 1234567 -> 123-4567, 1234567890 -> 123-456-7890
        String newFormatedNbr = "";
        if (phoneNbrTxt != null) {
            // String phoneNbr = dt.getPhoneNbrTxt();
            phoneNbrTxt =phoneNbrTxt.trim();
            int nbrSize = phoneNbrTxt.length();

            if (nbrSize > 4) { // Add first dash
                newFormatedNbr = "-"
                        + phoneNbrTxt.substring(nbrSize - 4, nbrSize);
                if (nbrSize > 7) { // Add a second dash
                    newFormatedNbr = phoneNbrTxt.substring(0, nbrSize - 7)
                            + "-"
                            + phoneNbrTxt.substring(nbrSize - 7, nbrSize - 4)
                            + newFormatedNbr;
                } else {
                    String remainder = phoneNbrTxt.substring(0, nbrSize - 4);
                    newFormatedNbr = remainder + newFormatedNbr;
                }

            } else {
                newFormatedNbr = phoneNbrTxt;
            }
        }// end of if
        return newFormatedNbr;
    }// End of formatPhoneNbr

    public static boolean checkIfAreaCodeMoreThan3Digits(ArrayList<String> areaAndNumber, HL7NMType HL7Type){

        boolean incorrectLength = false;
        String areaCode, number;
        if (HL7Type != null) {

            String areaCodeString =HL7Type.getHL7Numeric().toString();

            if(areaCodeString.length()>3){//Area code more than 3 digits
                incorrectLength= true;
                areaCode = areaCodeString.substring(0,3);
                number = areaCodeString.substring(3);

                areaAndNumber.add(areaCode);
                areaAndNumber.add(number);


            }

            //if the phone number contains the area code, it seems to work fine because everything goes to the number and no math.round function is used, so the issue were the wrong number is created does not happen
        }

        return incorrectLength;
    }

    public static ParticipationDT defaultParticipationDT(ParticipationDT participationDT, EdxLabInformationDto edxLabInformationDto) {
        participationDT.setAddTime(edxLabInformationDto.getAddTime());
        participationDT.setLastChgTime(edxLabInformationDto.getAddTime());
        participationDT.setAddUserId(edxLabInformationDto.getUserId());
        participationDT.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
        participationDT.setAddTime(edxLabInformationDto.getAddTime());
        participationDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        participationDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        participationDT.setItDirty(false);
        participationDT.setItNew(true);

        return participationDT;
    }

    public static EntityLocatorParticipationDto orgTelePhoneType(HL7XTNType hl7XTNType, String role, OrganizationVO organizationVO) {
        EntityLocatorParticipationDto elp = telePhoneType(hl7XTNType, role);
        elp.setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
        elp.setEntityUid(organizationVO.getTheOrganizationDT().getOrganizationUid());
        elp.setAsOfDate(organizationVO.getTheOrganizationDT().getLastChgTime());
        if (elp.getTheTeleLocatorDto() == null) {
            elp.setTheTeleLocatorDto(new TeleLocatorDto());
        }
        elp.getTheTeleLocatorDto().setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
        return elp;

    }

    public static PersonContainer processCNNPersonName(HL7CNNType hl7CNNType,
                                                       PersonContainer personContainer) {
        PersonNameDto personNameDto = new PersonNameDto();
        String lastName = hl7CNNType.getHL7FamilyName();
        personNameDto.setLastNm(lastName);
        String firstName = hl7CNNType.getHL7GivenName();
        personNameDto.setFirstNm(firstName);
        String middleName = hl7CNNType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof();
        personNameDto.setMiddleNm(middleName);
        String suffix = hl7CNNType.getHL7Suffix();
        personNameDto.setNmSuffix(suffix);
        String prefix = hl7CNNType.getHL7Prefix();
        personNameDto.setNmPrefix(prefix);
        String degree = hl7CNNType.getHL7Degree();
        personNameDto.setNmDegree(degree);
        personNameDto.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
        personNameDto.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        personNameDto.setPersonNameSeq(1);
        personNameDto.setAddTime(personContainer.getThePersonDto().getAddTime());
        personNameDto.setAddReasonCd(EdxELRConstant.ADD_REASON_CD);
        personNameDto.setAsOfDate(personContainer.getThePersonDto().getLastChgTime());
        personNameDto.setAddUserId(personContainer.getThePersonDto().getAddUserId());
        personNameDto.setItNew(true);
        personNameDto.setItDirty(false);
        personNameDto.setLastChgTime(personContainer.getThePersonDto().getLastChgTime());
        personNameDto.setLastChgUserId(personContainer.getThePersonDto().getLastChgUserId());
        int seq = 0;
        if (personContainer.getThePersonNameDtoCollection().size() > 0) {
            seq = personContainer.getThePersonNameDtoCollection().size();
        }
        personNameDto.setPersonNameSeq(seq + 1);

        personContainer.getThePersonNameDtoCollection().add(personNameDto);
        personDtToPersonVO(personNameDto, personContainer);
        return personContainer;
    }
    public static PersonContainer personDtToPersonVO(PersonNameDto personNameDto,
                                                     PersonContainer personContainer) {
        personContainer.getThePersonDto().setLastNm(personNameDto.getLastNm());
        personContainer.getThePersonDto().setFirstNm(personNameDto.getFirstNm());
        personContainer.getThePersonDto().setNmPrefix(personNameDto.getNmPrefix());
        personContainer.getThePersonDto().setNmSuffix(personNameDto.getNmSuffix());

        return personContainer;
    }
    public static Timestamp processHL7TSTypeWithMillis(HL7TSType time, String itemDescription) throws DataProcessingException {
        String dateStr = "";
        try {
            Timestamp toTimestamp = null;

            int year = -1;
            int month = -1;
            int day = -1;
            int hourOfDay = 0;
            int minute = 0;
            int second = 0;
            int millis = 0;
            if (time != null) {
                if (time.getYear() != null)
                    year = time.getYear().intValue();
                if (time.getMonth() != null)
                    month = time.getMonth().intValue();
                if (time.getHours() != null)
                    hourOfDay = time.getHours().intValue();
                if (time.getDay() != null)
                    day = time.getDay().intValue();
                if (time.getMinutes() != null)
                    minute = time.getMinutes().intValue();
                if (time.getSeconds() != null)
                    second = time.getSeconds().intValue();
                if (time.getMillis() != null)
                    millis = time.getMillis().intValue();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                java.util.Date date2;
                dateStr = year+"-"+month+"-"+day+" "+hourOfDay+":"+minute+":"+second+"."+millis;
                logger.debug("  in processHL7TSTypeWithMillis: Date string is: " +dateStr);
                date2 = sdf.parse(dateStr);
                toTimestamp = new java.sql.Timestamp(date2.getTime());
                if (EntityIdHandler.isDateNotOkForDatabase(toTimestamp)) {
                    throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7TSTypeWithMillis " +itemDescription + date2
                            + EdxELRConstant.DATE_INVALID_FOR_DATABASE);
                }
            }
            return toTimestamp;
        } catch (Exception e) {
            logger.error("Hl7ToNBSObjectConverter.processHL7TSTypeWithMillis failed as the date format is not right. Please check.!"+ dateStr);
            throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7TSTypeWithMillis failed as the date format is not right."+ itemDescription+dateStr);
        }
    }

}
