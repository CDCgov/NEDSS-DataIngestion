package gov.cdc.dataprocessing.utilities.component.data_extraction;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.*;
import gov.cdc.dataprocessing.model.classic_model.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
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

    public PersonVO mapPersonNameType(HL7XPNType hl7XPNType, PersonVO personVO) throws DataProcessingException {
        PersonNameDT personNameDT = new PersonNameDT();
        HL7FNType hl7FamilyName = hl7XPNType.getHL7FamilyName();
        /** Optional maxOccurs="1 */
        if(hl7FamilyName!=null){
            personNameDT.setLastNm(hl7FamilyName.getHL7Surname());
            personNameDT.setLastNm2(hl7FamilyName.getHL7OwnSurname());
        }
        /** length"194 */
        personNameDT.setFirstNm(hl7XPNType.getHL7GivenName());
        /** Optional maxOccurs="1 */
        /** length"30 */
        String hl7SecondAndFurtherGivenNamesOrInitialsThereof = hl7XPNType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof();
        /** Optional maxOccurs="1 */
        /** length"30 */
        personNameDT.setMiddleNm(hl7SecondAndFurtherGivenNamesOrInitialsThereof);
        String hl7Suffix = hl7XPNType.getHL7Suffix();
        /** Optional maxOccurs="1 */
        /** length"20 */
        personNameDT.setNmSuffix(hl7Suffix);

        String hl7Prefix = hl7XPNType.getHL7Prefix();
        /** Optional maxOccurs="1 */
        /** length"20 */
        personNameDT.setNmPrefix(hl7Prefix);

        /** Optional maxOccurs="1 */
        /** length"6 */
        String hl7NameTypeCode = hl7XPNType.getHL7NameTypeCode();
        personNameDT.setNmUseCd(Objects.requireNonNullElse(hl7NameTypeCode, EdxELRConstant.ELR_LEGAL_NAME));

        String toCode = checkingValueService.findToCode("ELR_LCA_NM_USE", personNameDT.getNmUseCd(), "P_NM_USE");
        if(toCode!=null && !toCode.isEmpty()){
            personNameDT.setNmUseCd(toCode);
        }
        /** length"1 */
        HL7TSType hl7EffectiveDate = hl7XPNType.getHL7EffectiveDate();
        /** Optional maxOccurs="1 */
        /** length"26 */
        Timestamp timestamp = processHL7TSType(hl7EffectiveDate, EdxELRConstant.DATE_VALIDATION_PERSON_NAME_FROM_TIME_MSG);
        personNameDT.setFromTime(timestamp);
        HL7TSType hl7ExpirationDate = hl7XPNType.getHL7ExpirationDate();
        /** Optional maxOccurs="1 */
        /** length"26 */
        Timestamp toTimestamp = processHL7TSType(hl7ExpirationDate, EdxELRConstant.DATE_VALIDATION_PERSON_NAME_TO_TIME_MSG);
        personNameDT.setToTime(toTimestamp);
        /** Optional maxOccurs="1 */
        /** length"199 */
        personNameDT.setAddTime(personVO.getThePersonDT().getAddTime());
        personNameDT.setAddReasonCd(EdxELRConstant.ADD_REASON_CD);
        personNameDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        personNameDT.setAsOfDate(personVO.getThePersonDT().getLastChgTime());
        personNameDT.setAddUserId(personVO.getThePersonDT().getAddUserId());
        personNameDT.setItNew(true);
        personNameDT.setItDirty(false);
        personNameDT.setLastChgTime(personVO.getThePersonDT().getLastChgTime());
        personNameDT.setLastChgUserId(personVO.getThePersonDT()
                .getLastChgUserId());
        int seq = 0;
        if (personVO.getThePersonNameDTCollection() == null) {
            personVO.setThePersonNameDTCollection(new ArrayList<>());
        } else {
            seq = personVO.getThePersonNameDTCollection().size();
        }
        personNameDT.setPersonNameSeq(seq + 1);

        personVO.getThePersonNameDTCollection().add(personNameDT);

        if (personNameDT.getNmUseCd()!=null && personNameDT.getNmUseCd().equals(EdxELRConstant.ELR_LEGAL_NAME)) {
            personVO.getThePersonDT().setLastNm(personNameDT.getLastNm());
            personVO.getThePersonDT().setFirstNm(personNameDT.getFirstNm());
            personVO.getThePersonDT().setNmPrefix(personNameDT.getNmPrefix());
            personVO.getThePersonDT().setNmSuffix(personNameDT.getNmSuffix());
            personVO.getThePersonDT().setMiddleNm(personNameDT.getMiddleNm());
        }
        return personVO;
    }

    public EntityIdDT processEntityData(HL7CXType hl7CXType, PersonVO personVO, String indicator, int j) throws DataProcessingException {
        EntityIdDT entityIdDT = new EntityIdDT();
        if (hl7CXType != null ) {
            entityIdDT.setEntityUid(personVO.getThePersonDT().getPersonUid());
            entityIdDT.setAddTime(personVO.getThePersonDT().getAddTime());
            entityIdDT.setEntityIdSeq(j + 1);
            entityIdDT.setRootExtensionTxt(hl7CXType.getHL7IDNumber());
            if(hl7CXType.getHL7AssigningAuthority()!=null){
                entityIdDT.setAssigningAuthorityCd(hl7CXType.getHL7AssigningAuthority().getHL7UniversalID());
                entityIdDT.setAssigningAuthorityDescTxt(hl7CXType.getHL7AssigningAuthority().getHL7NamespaceID());
                entityIdDT.setAssigningAuthorityIdType(hl7CXType.getHL7AssigningAuthority().getHL7UniversalIDType());
            }
            if (indicator != null && indicator.equals(EdxELRConstant.ELR_PATIENT_ALTERNATE_IND)) {
                entityIdDT.setTypeCd(EdxELRConstant.ELR_PATIENT_ALTERNATE_TYPE);
                entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_PATIENT_ALTERNATE_DESC);
            }
            else if (indicator != null && indicator.equals(EdxELRConstant.ELR_MOTHER_IDENTIFIER)) {
                entityIdDT.setTypeCd(EdxELRConstant.ELR_MOTHER_IDENTIFIER);
                entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_MOTHER_IDENTIFIER);
            } else if (indicator != null && indicator.equals(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER)) {
                entityIdDT.setTypeCd(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER);
                entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_ACCOUNT_DESC);
            }
            else if (hl7CXType.getHL7IdentifierTypeCode() == null || hl7CXType.getHL7IdentifierTypeCode().trim().equals("")) {
                entityIdDT.setTypeCd(EdxELRConstant.ELR_PERSON_TYPE);
                entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_PERSON_TYPE_DESC);
                String typeCode = checkingValueService.getCodeDescTxtForCd(entityIdDT.getTypeCd(), EdxELRConstant.EI_TYPE);
                if (typeCode == null || typeCode.trim().equals("")) {
                    entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_CLIA_DESC);
                } else {
                    entityIdDT.setTypeDescTxt(typeCode);
                }
            } else {
                entityIdDT.setTypeCd(hl7CXType.getHL7IdentifierTypeCode());
            }

            entityIdDT.setAddUserId(personVO.getThePersonDT().getAddUserId());
            entityIdDT.setLastChgUserId(personVO.getThePersonDT().getLastChgUserId());
            entityIdDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
            entityIdDT.setStatusTime(personVO.getThePersonDT().getAddTime());
            entityIdDT.setRecordStatusTime(personVO.getThePersonDT().getAddTime());
            entityIdDT.setRecordStatusCd(NEDSSConstant.ACTIVE);
            entityIdDT.setAsOfDate(personVO.getThePersonDT().getAddTime());
            entityIdDT.setEffectiveFromTime(EntityIdHandler.processHL7DTType(hl7CXType.getHL7EffectiveDate(), EdxELRConstant.DATE_VALIDATION_PID_PATIENT_IDENTIFIER_EFFECTIVE_DATE_TIME_MSG));
            entityIdDT.setValidFromTime(entityIdDT.getEffectiveFromTime());
            entityIdDT.setEffectiveToTime(EntityIdHandler.processHL7DTType(hl7CXType.getHL7ExpirationDate(), EdxELRConstant.DATE_VALIDATION_PID_PATIENT_IDENTIFIER_EXPIRATION_DATE_TIME_MSG));
            entityIdDT.setValidToTime(entityIdDT.getEffectiveToTime());
            entityIdDT.setItNew(true);
            entityIdDT.setItDirty(false);
        }
        return entityIdDT;
    }

    /**
     * Parsing Entity Address into Object
     * */
    public EntityLocatorParticipationDT addressType(HL7XADType hl7XADType, String role)   {

        EntityLocatorParticipationDT elp = new EntityLocatorParticipationDT();
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

            PostalLocatorDT pl = new PostalLocatorDT();
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

            elp.setThePostalLocatorDT(pl);
        } catch (Exception e) {
            logger.error("Hl7ToNBSObjectConverter. Error thrown: "+ e);
        }
        return elp;
    }

    private PostalLocatorDT nbsStreetAddressType(HL7SADType hl7SADType, PostalLocatorDT pl) {

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

    public EntityLocatorParticipationDT personAddressType(HL7XADType hl7XADType, String role, PersonVO personVO) {
        EntityLocatorParticipationDT elp = addressType(hl7XADType, role);
        elp.setEntityUid(personVO.getThePersonDT().getPersonUid());
        elp.setAddUserId(personVO.getThePersonDT().getAddUserId());
        elp.setAsOfDate(personVO.getThePersonDT().getLastChgTime());
        if (elp.getThePostalLocatorDT() == null) {
            elp.setThePostalLocatorDT(new PostalLocatorDT());
        }
        elp.getThePostalLocatorDT().setAddUserId(personVO.getThePersonDT().getAddUserId());
        personVO.getTheEntityLocatorParticipationDTCollection().add(elp);
        return elp;
    }

    public EntityLocatorParticipationDT organizationAddressType(HL7XADType hl7XADType, String role, OrganizationVO organizationVO) {
        EntityLocatorParticipationDT elp = addressType(hl7XADType, role);
        elp.setEntityUid(organizationVO.getTheOrganizationDT().getOrganizationUid());
        elp.setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
        elp.setAsOfDate(organizationVO.getTheOrganizationDT().getLastChgTime());
        if (elp.getThePostalLocatorDT() == null) {
            elp.setThePostalLocatorDT(new PostalLocatorDT());
        }
        elp.getThePostalLocatorDT().setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
        organizationVO.getTheEntityLocatorParticipationDTCollection().add(elp);
        return elp;
    }

    public static EntityIdDT validateSSN(EntityIdDT entityIdDt) {
        String ssn = entityIdDt.getRootExtensionTxt();
        if(ssn != null && !ssn.equals("") && !ssn.equals(" ")) {
            ssn =ssn.trim();
            if (ssn.length() > 3) {
                String newSSN = ssn.substring(0, 3);
                newSSN = newSSN + "-";
                if (ssn.length() > 5) {
                    newSSN = newSSN + ssn.replace("-", "").substring(3, 5) + "-";
                    newSSN = newSSN + ssn.replace("-", "").substring(5, (ssn.replace("-", "").length()));
                    ssn = newSSN;
                    entityIdDt.setRootExtensionTxt(ssn);
                }
                else {
                    newSSN = newSSN + ssn.replace("-", "").substring(3, ssn.length()) + "- ";
                    ssn = newSSN;
                    entityIdDt.setRootExtensionTxt(ssn);
                }
            }
            else {
                ssn = ssn + "- - ";
                entityIdDt.setRootExtensionTxt(ssn);
            }
        }//end of if
        return entityIdDt;
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

    public static EntityLocatorParticipationDT setPersonBirthType(String countryOfBirth, PersonVO personVO) {
        EntityLocatorParticipationDT elp = new EntityLocatorParticipationDT();

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
        elp.setAddUserId(personVO.getThePersonDT().getAddUserId());
        elp.setEntityUid(personVO.getThePersonDT().getPersonUid());
        elp.setAsOfDate(personVO.getThePersonDT().getLastChgTime());

        PostalLocatorDT pl = new PostalLocatorDT();
        pl.setItNew(true);
        pl.setItDirty(false);
        pl.setAddTime(new Timestamp(new Date().getTime()));
        pl.setRecordStatusTime(new Timestamp(new Date().getTime()));
        pl.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        pl.setCntryCd(countryOfBirth);
        pl.setAddUserId(personVO.getThePersonDT().getAddUserId());
        elp.setThePostalLocatorDT(pl);
        personVO.getTheEntityLocatorParticipationDTCollection().add(elp);
        return elp;
    }

    public static PersonEthnicGroupDT ethnicGroupType(HL7CWEType hl7CWEType,
                                               PersonVO personVO) {
        PersonEthnicGroupDT ethnicGroupDT = new PersonEthnicGroupDT();
        ethnicGroupDT.setItNew(true);
        ethnicGroupDT.setItDirty(false);
        ethnicGroupDT.setAddReasonCd("Add");
        ethnicGroupDT.setRecordStatusCd(NEDSSConstant.ACTIVE);
        ethnicGroupDT.setPersonUid(personVO.getThePersonDT().getPersonUid());
        ethnicGroupDT.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        ethnicGroupDT.setEthnicGroupCd(hl7CWEType.getHL7Identifier());
        ethnicGroupDT.setEthnicGroupDescTxt(hl7CWEType.getHL7Text());
        personVO.getThePersonDT().setEthnicGroupInd(ethnicGroupDT.getEthnicGroupCd());

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


    public static EntityLocatorParticipationDT personTelePhoneType(
            HL7XTNType hl7XTNType, String role, PersonVO personVO) {
        EntityLocatorParticipationDT elp = telePhoneType(hl7XTNType, role);
        elp.setAddUserId(personVO.getThePersonDT().getAddUserId());
        elp.setEntityUid(personVO.getThePersonDT().getPersonUid());
        elp.setAsOfDate(personVO.getThePersonDT().getLastChgTime());
        if (elp.getTheTeleLocatorDT() == null) {
            elp.setTheTeleLocatorDT(new TeleLocatorDT());
        }
        elp.getTheTeleLocatorDT().setAddUserId(personVO.getThePersonDT().getAddUserId());
        if (personVO.getTheEntityLocatorParticipationDTCollection() == null) {
            personVO.setTheEntityLocatorParticipationDTCollection(new ArrayList<>());
        }
        personVO.getTheEntityLocatorParticipationDTCollection().add(elp);
        return elp;
    }

    public static  PersonRaceDT raceType(HL7CWEType hl7CEType, PersonVO personVO) {
        PersonRaceDT raceDT = new PersonRaceDT();
        raceDT.setItNew(true);
        raceDT.setItDelete(false);
        raceDT.setItDirty(false);
        raceDT.setAddTime(new Timestamp(new Date().getTime()));
        raceDT.setAddUserId(personVO.getThePersonDT().getAddUserId());

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
        raceDT.setAsOfDate(personVO.getThePersonDT().getAddTime());
        return raceDT;
    }

    public static EntityLocatorParticipationDT telePhoneType(
            HL7XTNType hl7XTNType, String role) {
        EntityLocatorParticipationDT elp = new EntityLocatorParticipationDT();
        TeleLocatorDT teleDT = new TeleLocatorDT();

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
        elp.setTheTeleLocatorDT(teleDT);

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

    public static ParticipationDT defaultParticipationDT(ParticipationDT participationDT, EdxLabInformationDT edxLabInformationDT) {
        participationDT.setAddTime(edxLabInformationDT.getAddTime());
        participationDT.setLastChgTime(edxLabInformationDT.getAddTime());
        participationDT.setAddUserId(edxLabInformationDT.getUserId());
        participationDT.setAddReasonCd(EdxELRConstant.ELR_ROLE_REASON);
        participationDT.setAddTime(edxLabInformationDT.getAddTime());
        participationDT.setStatusCd(EdxELRConstant.ELR_ACTIVE_CD);
        participationDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        participationDT.setItDirty(false);
        participationDT.setItNew(true);

        return participationDT;
    }

    public static EntityLocatorParticipationDT orgTelePhoneType(HL7XTNType hl7XTNType, String role, OrganizationVO organizationVO) {
        EntityLocatorParticipationDT elp = telePhoneType(hl7XTNType, role);
        elp.setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
        elp.setEntityUid(organizationVO.getTheOrganizationDT().getOrganizationUid());
        elp.setAsOfDate(organizationVO.getTheOrganizationDT().getLastChgTime());
        if (elp.getTheTeleLocatorDT() == null) {
            elp.setTheTeleLocatorDT(new TeleLocatorDT());
        }
        elp.getTheTeleLocatorDT().setAddUserId(organizationVO.getTheOrganizationDT().getAddUserId());
        return elp;

    }

    public static PersonVO processCNNPersonName(HL7CNNType hl7CNNType,
                                         PersonVO personVO) {
        PersonNameDT personNameDT = new PersonNameDT();
        String lastName = hl7CNNType.getHL7FamilyName();
        personNameDT.setLastNm(lastName);
        String firstName = hl7CNNType.getHL7GivenName();
        personNameDT.setFirstNm(firstName);
        String middleName = hl7CNNType.getHL7SecondAndFurtherGivenNamesOrInitialsThereof();
        personNameDT.setMiddleNm(middleName);
        String suffix = hl7CNNType.getHL7Suffix();
        personNameDT.setNmSuffix(suffix);
        String prefix = hl7CNNType.getHL7Prefix();
        personNameDT.setNmPrefix(prefix);
        String degree = hl7CNNType.getHL7Degree();
        personNameDT.setNmDegree(degree);
        personNameDT.setNmUseCd(EdxELRConstant.ELR_LEGAL_NAME);
        personNameDT.setRecordStatusCd(EdxELRConstant.ELR_ACTIVE);
        personNameDT.setPersonNameSeq(1);
        personNameDT.setAddTime(personVO.getThePersonDT().getAddTime());
        personNameDT.setAddReasonCd(EdxELRConstant.ADD_REASON_CD);
        personNameDT.setAsOfDate(personVO.getThePersonDT().getLastChgTime());
        personNameDT.setAddUserId(personVO.getThePersonDT().getAddUserId());
        personNameDT.setItNew(true);
        personNameDT.setItDirty(false);
        personNameDT.setLastChgTime(personVO.getThePersonDT().getLastChgTime());
        personNameDT.setLastChgUserId(personVO.getThePersonDT().getLastChgUserId());
        int seq = 0;
        if (personVO.getThePersonNameDTCollection().size() > 0) {
            seq = personVO.getThePersonNameDTCollection().size();
        }
        personNameDT.setPersonNameSeq(seq + 1);

        personVO.getThePersonNameDTCollection().add(personNameDT);
        personDtToPersonVO(personNameDT, personVO);
        return personVO;
    }
    public static PersonVO personDtToPersonVO(PersonNameDT personNameDT,
                                       PersonVO personVO) {
        personVO.getThePersonDT().setLastNm(personNameDT.getLastNm());
        personVO.getThePersonDT().setFirstNm(personNameDT.getFirstNm());
        personVO.getThePersonDT().setNmPrefix(personNameDT.getNmPrefix());
        personVO.getThePersonDT().setNmSuffix(personNameDT.getNmSuffix());

        return personVO;
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
