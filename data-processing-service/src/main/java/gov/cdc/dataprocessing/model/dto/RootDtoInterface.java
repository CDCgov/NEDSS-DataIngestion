package gov.cdc.dataprocessing.model.dto;

import java.sql.Timestamp;

public interface RootDtoInterface {
    /**
     * A getter for last change user id
     *
     * @return Long
     * @roseuid 3C73C11500C4
     */
    Long getLastChgUserId();

    /**
     * A setter for last change user id
     *
     * @param aLastChgUserId
     * @roseuid 3C73D82701FD
     */
    void setLastChgUserId(Long aLastChgUserId);

    /**
     * A getter for jurisdiction code
     *
     * @return String
     * @roseuid 3C73D8D1030F
     */
    String getJurisdictionCd();

    /**
     * A setter for jurisdiction code
     *
     * @param aJurisdictionCd
     * @roseuid 3C73D8E5000B
     */
    void setJurisdictionCd(String aJurisdictionCd);

    /**
     * A getter for program area code
     *
     * @return String
     * @roseuid 3C73D90A0145
     */
    String getProgAreaCd();

    /**
     * A setter for program area code
     *
     * @param aProgAreaCd
     * @roseuid 3C73D91703E2
     */
    void setProgAreaCd(String aProgAreaCd);

    /**
     * A getter for last change time
     *
     * @return java.sql.Timestamp
     * @roseuid 3C73D9C502AC
     */
    Timestamp getLastChgTime();

    /**
     * A setter for last change time
     *
     * @param aLastChgTime
     * @roseuid 3C73D9D800AB
     */
    void setLastChgTime(java.sql.Timestamp aLastChgTime);

    /**
     * A getter for local id
     *
     * @return String
     * @roseuid 3C73DA200253
     */
    String getLocalId();

    /**
     * A setter for local id
     *
     * @param aLocalId
     * @roseuid 3C73DA2C00CA
     */
    void setLocalId(String aLocalId);

    /**
     * A getter for add user id
     *
     * @return Long
     * @roseuid 3C73DA4701B9
     */
    Long getAddUserId();

    /**
     * A stter for add user id
     *
     * @param aAddUserId
     * @roseuid 3C73DA550123
     */
    void setAddUserId(Long aAddUserId);

    /**
     * A getter for last change reason code
     *
     * @return String
     * @roseuid 3C73DABD00F0
     */
    String getLastChgReasonCd();

    /**
     * A setter for last change reason code
     *
     * @param aLastChgReasonCd
     * @roseuid 3C73DAC60360
     */
    void setLastChgReasonCd(String aLastChgReasonCd);

    /**
     * A getter for record status code
     *
     * @return String
     * @roseuid 3C73DAFD023D
     */
    String getRecordStatusCd();

    /**
     * A setter for record status code
     *
     * @param aRecordStatusCd
     * @roseuid 3C73DB0C02AC
     */
    void setRecordStatusCd(String aRecordStatusCd);

    /**
     * A getter for record status time
     *
     * @return java.sql.Timestamp
     * @roseuid 3C73DB260015
     */
    Timestamp getRecordStatusTime();

    /**
     * A setter for record status time
     *
     * @param aRecordStatusTime
     * @roseuid 3C73DB35002A
     */
    void setRecordStatusTime(java.sql.Timestamp aRecordStatusTime);

    /**
     * A getter for status code
     *
     * @return String
     * @roseuid 3C73DB60004A
     */
    String getStatusCd();

    /**
     * A setter for status code
     *
     * @param aStatusCd
     * @roseuid 3C73DB6A030C
     */
    void setStatusCd(String aStatusCd);

    /**
     * A getter for status time
     *
     * @return java.sql.Timestamp
     * @roseuid 3C73DB6F0381
     */
    Timestamp getStatusTime();

    /**
     * A setter for status time
     *
     * @param aStatusTime
     * @roseuid 3C73DB74018A
     */
    void setStatusTime(java.sql.Timestamp aStatusTime);

    /**
     * Implement base to return class type - currently CLASSTYPE_ACT or
     * CLASSTYPE_ENTITY
     *
     * @return String
     * @roseuid 3C73FD5C0343
     */
    String getSuperclass();

    /**
     * A getter for uid
     *
     * @return Long
     * @roseuid 3C7407A80249
     */
    Long getUid();

    /**
     * A getter for add time
     *
     * @return java.sql.Timestamp
     * @roseuid 3C74125B0003
     */
    Timestamp getAddTime();

    /**
     * A setter for add time
     *
     * @param aAddTime
     * @roseuid 3C7412520078
     */
    void setAddTime(java.sql.Timestamp aAddTime);

    /**
     * A checker for the new flag
     *
     * @return boolean
     * @roseuid 3C7440F0021D
     */
    boolean isItNew();

    /**
     * A setter for the new flag
     *
     * @param itNew
     * @roseuid 3C7441030329
     */
    void setItNew(boolean itNew);

    /**
     * A checker for the dirty flag
     *
     * @return boolean
     * @roseuid 3C74410A00DA
     */
    boolean isItDirty();

    /**
     * A setter for the dirty flag
     *
     * @param itDirty
     * @roseuid 3C74410F02C2
     */
    void setItDirty(boolean itDirty);

    /**
     * A checker for the delete flag
     *
     * @return boolean
     * @roseuid 3C74411402B5
     */
    boolean isItDelete();

    /**
     * A setter for the delete flag
     *
     * @param itDelete
     * @roseuid 3C74412E012C
     */
    void setItDelete(boolean itDelete);

    /**
     * A getter for program jurisdiction oid
     *
     * @return Long
     * @roseuid 3CF7906002AE
     */
    Long getProgramJurisdictionOid();

    /**
     * A setter for the program jurisdiction oid
     *
     * @param aProgramJurisdictionOid
     * @roseuid 3CF7974902A7
     */
    void setProgramJurisdictionOid(Long aProgramJurisdictionOid);

    /**
     * A getter for shared indicator
     *
     * @return String
     * @roseuid 3CFBB5DA00CD
     */
    String getSharedInd();

    /**
     * A setter for shared indicator
     *
     * @param aSharedInd
     * @roseuid 3CFBB5EB01F4
     */
    void setSharedInd(String aSharedInd);

    /**
     * A getter for version control number
     *
     * @return Integer
     */
    Integer getVersionCtrlNbr();
}
