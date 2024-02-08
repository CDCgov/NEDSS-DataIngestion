package gov.cdc.dataprocessing.utilities;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EntityIdDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import gov.cdc.dataprocessing.model.phdc.HL7CXType;
import gov.cdc.dataprocessing.model.phdc.HL7DTType;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EntityIdHandler {
    private static final Logger logger = LoggerFactory.getLogger(EntityIdHandler.class);

    /**
     * This method process then parse data from Person into EntityId Object
     * */
    public static EntityIdDT processEntityData(HL7CXType hl7CXType, PersonVO personVO, String indicator, int index) throws DataProcessingException {
        EntityIdDT entityIdDT = new EntityIdDT();
        if (hl7CXType != null) {
            entityIdDT.setEntityUid(personVO.getThePersonDT().getPersonUid());
            entityIdDT.setAddTime(personVO.getThePersonDT().getAddTime());
            entityIdDT.setEntityIdSeq(index + 1);
            entityIdDT.setRootExtensionTxt(hl7CXType.getHL7IDNumber());
            
            if(hl7CXType.getHL7AssigningAuthority() != null){
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
            }
            else if (indicator != null && indicator.equals(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER)) {
                entityIdDT.setTypeCd(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER);
                entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_ACCOUNT_DESC);
            }
            else if (hl7CXType.getHL7IdentifierTypeCode() == null || hl7CXType.getHL7IdentifierTypeCode().trim().isEmpty()) {
                entityIdDT.setTypeCd(EdxELRConstant.ELR_PERSON_TYPE);
                entityIdDT.setTypeDescTxt(EdxELRConstant.ELR_PERSON_TYPE_DESC);

                /**
                 * TODO: Need to call out ro CodeValueGeneral Repository to grab the data
                 * */
                CodeValueGeneral codeValueGeneralModel = new CodeValueGeneral();
                codeValueGeneralModel.setCode("DUMMY");
                String typeCode =  codeValueGeneralModel.getCode();// CachedDropDowns.getCodeDescTxtForCd(entityIdDT.getTypeCd(), EdxELRConstant.EI_TYPE);

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
            entityIdDT.setEffectiveFromTime(processHL7DTType(hl7CXType.getHL7EffectiveDate(), EdxELRConstant.DATE_VALIDATION_PID_PATIENT_IDENTIFIER_EFFECTIVE_DATE_TIME_MSG));
            entityIdDT.setValidFromTime(entityIdDT.getEffectiveFromTime());
            entityIdDT.setEffectiveToTime(processHL7DTType(hl7CXType.getHL7ExpirationDate(), EdxELRConstant.DATE_VALIDATION_PID_PATIENT_IDENTIFIER_EXPIRATION_DATE_TIME_MSG));
            entityIdDT.setValidToTime(entityIdDT.getEffectiveToTime());
            entityIdDT.setItNew(true);
            entityIdDT.setItDirty(false);

        }
        return entityIdDT;
    }

    public static Timestamp processHL7DTType(HL7DTType time, String itemDescription) throws DataProcessingException {
        Timestamp toTimestamp = null;

        int year = -1;
        int month = -1;
        int date = -1;
        String toTime = "";
        if (time != null) {
            try {
                if (time.getYear() != null)
                    year = time.getYear().intValue();
                if (time.getMonth() != null)
                    month = time.getMonth().intValue();
                if (time.getDay() != null)
                    date = time.getDay().intValue();

                if (year >= 0 && month >= 0 && date >= 0) {
                    toTime = month + "/" + date + "/" + year;
                    logger.debug("  in processHL7DTType: Date string is: " +toTime);
                    toTimestamp = stringToStrutsTimestamp(toTime);
                }
            } catch (Exception e) {
                logger.error("Hl7ToNBSObjectConverter.processHL7DTType failed as the date format is not right. Please check.!"+toTime);
                throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7DTType failed as the date format is not right." +itemDescription +toTime);
            }
            if (isDateNotOkForDatabase(toTimestamp)) {
                throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7DTType " +itemDescription +toTime + EdxELRConstant.DATE_INVALID_FOR_DATABASE);
            }

        }

        return toTimestamp;
    }

    public static Timestamp stringToStrutsTimestamp(String strTime) {
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
        Date t;
        try {
            if (strTime != null && strTime.trim().length() > 0) {
                t = formatter.parse(strTime);
                logger.debug(String.valueOf(t));
                Timestamp ts = new Timestamp(t.getTime());
                return ts;
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            logger.info("string could not be parsed into time");
            return null;
        }
    }

    /**
     * The earliest date that can be stored in SQL is Jan 1st, 1753 and the latest is Dec 31st, 9999
     * Check the date so we don't get a SQL error.
     * @param dateVal
     * @return true if invalid date
     */
    public static boolean isDateNotOkForDatabase (Timestamp dateVal) {
        if (dateVal == null)
            return false;
        String earliestDate = "1753-01-01";
        String latestDate = "9999-12-31";
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date earliestDateAcceptable = dateFormat.parse(earliestDate);
            if (dateVal.before(earliestDateAcceptable))
                return true;
            Date lastAcceptableDate = dateFormat.parse(latestDate);
            if (dateVal.after(lastAcceptableDate))
                return true;
        }catch(Exception ex){//this generic but you can control another types of exception
            logger.error("Unexpected exception in checkDateForDatabase() " + ex.getMessage());
        }
        return false;
    }

}
