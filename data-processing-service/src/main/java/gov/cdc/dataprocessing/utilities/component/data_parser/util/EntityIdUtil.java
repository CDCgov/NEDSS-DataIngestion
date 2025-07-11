package gov.cdc.dataprocessing.utilities.component.data_parser.util;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.phdc.HL7CXType;
import gov.cdc.dataprocessing.model.phdc.HL7DTType;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component

public class EntityIdUtil {
    private static final Logger logger = LoggerFactory.getLogger(EntityIdUtil.class);
    private final ICatchingValueDpService catchingValueService;

    public EntityIdUtil(ICatchingValueDpService catchingValueService) {
        this.catchingValueService = catchingValueService;
    }

    /**
     * This method process then parse data from Person into EntityId Object
     * */
    @SuppressWarnings("java:S3776")
    public EntityIdDto processEntityData(HL7CXType hl7CXType, PersonContainer personContainer, String indicator, int index) throws DataProcessingException {
        EntityIdDto entityIdDto = new EntityIdDto();
        if (hl7CXType != null) {
            entityIdDto.setEntityUid(personContainer.getThePersonDto().getPersonUid());
            entityIdDto.setAddTime(personContainer.getThePersonDto().getAddTime());
            entityIdDto.setEntityIdSeq(index + 1);
            entityIdDto.setRootExtensionTxt(hl7CXType.getHL7IDNumber());
            
            if(hl7CXType.getHL7AssigningAuthority() != null){
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
            }
            else if (indicator != null && indicator.equals(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER)) {
                entityIdDto.setTypeCd(EdxELRConstant.ELR_ACCOUNT_IDENTIFIER);
                entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_ACCOUNT_DESC);
            }
            else if (hl7CXType.getHL7IdentifierTypeCode() == null || hl7CXType.getHL7IdentifierTypeCode().trim().isEmpty()) {
                entityIdDto.setTypeCd(EdxELRConstant.ELR_PERSON_TYPE);
                entityIdDto.setTypeDescTxt(EdxELRConstant.ELR_PERSON_TYPE_DESC);

                String typeCode = catchingValueService.getCodeDescTxtForCd(entityIdDto.getTypeCd(), EdxELRConstant.EI_TYPE);

                if (typeCode == null || typeCode.trim().isEmpty()) {
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
            entityIdDto.setEffectiveFromTime(processHL7DTType(hl7CXType.getHL7EffectiveDate(), EdxELRConstant.DATE_VALIDATION_PID_PATIENT_IDENTIFIER_EFFECTIVE_DATE_TIME_MSG));
            entityIdDto.setValidFromTime(entityIdDto.getEffectiveFromTime());
            entityIdDto.setEffectiveToTime(processHL7DTType(hl7CXType.getHL7ExpirationDate(), EdxELRConstant.DATE_VALIDATION_PID_PATIENT_IDENTIFIER_EXPIRATION_DATE_TIME_MSG));
            entityIdDto.setValidToTime(entityIdDto.getEffectiveToTime());
            entityIdDto.setItNew(true);
            entityIdDto.setItDirty(false);

        }
        return entityIdDto;
    }

    public Timestamp processHL7DTType(HL7DTType time, String itemDescription) throws DataProcessingException {
        Timestamp toTimestamp = null;

        int year = -1;
        int month = -1;
        int date = -1;
        String toTime = "";
        if (time != null) {
            if (time.getYear() != null)
                year = time.getYear().intValue();
            if (time.getMonth() != null)
                month = time.getMonth().intValue();
            if (time.getDay() != null)
                date = time.getDay().intValue();

            if (year >= 0 && month >= 0 && date >= 0) {
                toTime = month + "/" + date + "/" + year;
                logger.debug("  in processHL7DTType: Date string is: {}", toTime);
                toTimestamp = stringToStrutsTimestamp(toTime);
            }
            if (toTimestamp == null) {
                throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7DTType: Timestamp value is Null");
            }
            if (isDateNotOkForDatabase(toTimestamp)) {
                throw new DataProcessingException("Hl7ToNBSObjectConverter.processHL7DTType " +itemDescription +toTime + EdxELRConstant.DATE_INVALID_FOR_DATABASE);
            }
        }

        return toTimestamp;
    }

    public Timestamp stringToStrutsTimestamp(String strTime) {
        try {
            if (strTime != null && !strTime.trim().isEmpty()) {
                return TimeStampUtil.convertStringToTimestamp(strTime);
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            logger.error("string could not be parsed into time");
            return null;
        }
    }

    /**
     * The earliest date that can be stored in SQL is Jan 1st, 1753 and the latest is Dec 31st, 9999
     * Check the date so we don't get a SQL error.
     */
    public boolean isDateNotOkForDatabase (Timestamp dateVal) {
        if (dateVal == null)
            return false;
        String earliestDate = "1753-01-01";
        String latestDate = "9999-12-31";
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date earliestDateAcceptable = dateFormat.parse(earliestDate);
            if (dateVal.before(earliestDateAcceptable))
            {
                return true;
            }
            Date lastAcceptableDate = dateFormat.parse(latestDate);
            if (dateVal.after(lastAcceptableDate))
            {
                return true;
            }
        }catch(Exception ex){//this generic but you can control another types of exception
            logger.error("Unexpected exception in checkDateForDatabase() {}", ex.getMessage());
        }
        return false;
    }

}
