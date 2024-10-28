package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.PrepareEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.jurisdiction.ProgAreaJurisdictionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.*;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PAT_NO_MERGER;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class PrepareAssocModelHelper {
    private static final Logger logger = LoggerFactory.getLogger(PrepareAssocModelHelper.class);

    private final PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;
    private final ProgAreaJurisdictionUtil progAreaJurisdictionUtil;
    private final ConcurrentCheck concurrentCheck;

    public PrepareAssocModelHelper(PrepareEntityStoredProcRepository prepareEntityStoredProcRepository,
                                   ProgAreaJurisdictionUtil progAreaJurisdictionUtil,
                                   ConcurrentCheck concurrentCheck) {
        this.prepareEntityStoredProcRepository = prepareEntityStoredProcRepository;
        this.progAreaJurisdictionUtil = progAreaJurisdictionUtil;
        this.concurrentCheck = concurrentCheck;
    }

    /**
     * This method is used to populate the system attributes on the 5 association
     * tables (ActRelationship, Participation, Role, EntityLocatoryParticipation, and
     * ActLocatorParticipation).
     */
    public EntityLocatorParticipationDto prepareAssocDTForEntityLocatorParticipation(EntityLocatorParticipationDto assocDTInterface) throws DataProcessingException {
        try {
            EntityLocatorParticipationDto aDTInterface ;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            logger.debug("AssocDTInterface.Statuscode = "+statusCd);
            logger.debug("AssocDTInterface.recStatusCd = "+recStatusCd);
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                logger.debug(LOG_RECORD_STATUS_NULL);
                throw new DataProcessingException(LOG_RECORD_2+recStatusCd+LOG_RECORD+statusCd);
            }

            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                logger.debug(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
                throw new DataProcessingException(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
            }
            else
            {
                    logger.debug(LOG_RECORD_STATUS_CD_NOT_NULL);
                    assocDTInterface.setAddUserId(null);
                    assocDTInterface.setAddTime(null);
                    java.util.Date dateTime = new java.util.Date();
                    Timestamp systemTime = new Timestamp(dateTime.getTime());
                    assocDTInterface.setRecordStatusTime(systemTime);
                    assocDTInterface.setStatusTime(systemTime);
                    assocDTInterface.setLastChgTime(systemTime);

                assocDTInterface.setLastChgReasonCd(null);
                aDTInterface = assocDTInterface;
                logger.debug(LOG_DT_PREPARED);
            }
            if(!isRealDirty) {
                aDTInterface.setItDirty(false);
            }
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public ActRelationshipDto prepareAssocDTForActRelationship(ActRelationshipDto assocDTInterface) throws DataProcessingException
    {
        try {
            ActRelationshipDto aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();
            if(recStatusCd == null)
            {
                logger.debug(LOG_RECORD_STATUS_NULL);
                throw new DataProcessingException(LOG_RECORD_2+recStatusCd+LOG_RECORD+statusCd);
            }

            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                logger.debug(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
                throw new DataProcessingException(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
            }
            else
            {
                try
                {

                    logger.debug(LOG_RECORD_STATUS_CD_NOT_NULL);
                    assocDTInterface.setAddUserId(null);
                    assocDTInterface.setAddTime(null);
                    java.util.Date dateTime = new java.util.Date();
                    Timestamp systemTime = new Timestamp(dateTime.getTime());
                    assocDTInterface.setRecordStatusTime(systemTime);
                    assocDTInterface.setStatusTime(systemTime);
                    assocDTInterface.setLastChgTime(systemTime);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                assocDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());

                assocDTInterface.setLastChgReasonCd(null);
                aDTInterface = assocDTInterface;
                logger.debug(LOG_DT_PREPARED);
            }
            if(!isRealDirty) aDTInterface.setItDirty(false);//Re-set the flag to original value if necessary
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    public RoleDto prepareAssocDTForRole(RoleDto assocDTInterface) throws DataProcessingException {
        try {
            RoleDto aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                logger.debug(LOG_RECORD_STATUS_NULL);
                throw new DataProcessingException(LOG_RECORD_2+recStatusCd+LOG_RECORD+statusCd);
            }

            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                logger.debug(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
                throw new DataProcessingException(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
            }
            else
            {
                try
                {

                    logger.debug(LOG_RECORD_STATUS_CD_NOT_NULL);
                    assocDTInterface.setAddUserId(null);
                    assocDTInterface.setAddTime(null);
                    java.util.Date dateTime = new java.util.Date();
                    Timestamp systemTime = new Timestamp(dateTime.getTime());
                    assocDTInterface.setRecordStatusTime(systemTime);
                    assocDTInterface.setStatusTime(systemTime);
                    assocDTInterface.setLastChgTime(systemTime);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
//                if(!nbsSecurityObj.getEntryID().equals(""))
//                {
//                    logger.debug("nbsSecurityObj.getEntryID() = " + nbsSecurityObj.getEntryID());
//                    assocDTInterface.setLastChgUserId(new Long(nbsSecurityObj.getEntryID()));
//                }
//                else
//                {
//                    logger.debug("nbsSecurityObj.getEntryID() is NULL ");
//                    throw new NEDSSSystemException("nbsSecurityObj.getEntryID() is NULL ");
//                }
                assocDTInterface.setLastChgReasonCd(null);
                aDTInterface = assocDTInterface;
                logger.debug(LOG_DT_PREPARED);
            }
            if(!isRealDirty) {
                aDTInterface.setItDirty(false);//Re-set the flag to original value if necessary
            }
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public ParticipationDto prepareAssocDTForParticipation(ParticipationDto assocDTInterface) throws DataProcessingException {
        try {
            ParticipationDto aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                throw new DataProcessingException(LOG_RECORD_2+recStatusCd+LOG_RECORD+statusCd);
            }
            else if(
                    !(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)
                    || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE))
            )
            {
                throw new DataProcessingException(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
            }
            else
            {
                try
                {

                    logger.debug(LOG_RECORD_STATUS_CD_NOT_NULL);
                    assocDTInterface.setAddUserId(null);
                    assocDTInterface.setAddTime(null);
                    java.util.Date dateTime = new java.util.Date();
                    Timestamp systemTime = new Timestamp(dateTime.getTime());
                    assocDTInterface.setRecordStatusTime(systemTime);
                    assocDTInterface.setStatusTime(systemTime);
                    assocDTInterface.setLastChgTime(systemTime);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                assocDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                assocDTInterface.setLastChgReasonCd(null);
                aDTInterface = assocDTInterface;
            }
            if(!isRealDirty) {
                aDTInterface.setItDirty(false);//Re-set the flag to original value if necessary
            }
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    /**
     * This method is used to populate the system attributes on the 5 association
     * tables (ActRelationship, Participation, Role, EntityLocatoryParticipation, and
     * ActLocatorParticipation).
     */
    public ActivityLocatorParticipationDto prepareActivityLocatorParticipationDT(ActivityLocatorParticipationDto assocDTInterface) throws DataProcessingException
    {
        try {
            ActivityLocatorParticipationDto aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                throw new DataProcessingException(LOG_RECORD_2+recStatusCd+LOG_RECORD+statusCd);
            }
            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                throw new DataProcessingException(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
            }
            else
            {
                assocDTInterface.setAddUserId(null);
                assocDTInterface.setAddTime(null);
                java.util.Date dateTime = new java.util.Date();
                Timestamp systemTime = new Timestamp(dateTime.getTime());
                assocDTInterface.setRecordStatusTime(systemTime);
                assocDTInterface.setStatusTime(systemTime);
                assocDTInterface.setLastChgTime(systemTime);
                assocDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                assocDTInterface.setLastChgReasonCd(null);
                aDTInterface = assocDTInterface;
            }
            if(!isRealDirty) {
                aDTInterface.setItDirty(false);
            }
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public ActRelationshipDto prepareActRelationshipDT(ActRelationshipDto assocDTInterface) throws DataProcessingException {
        try {
            ActRelationshipDto aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                throw new DataProcessingException(LOG_RECORD_2 + recStatusCd+LOG_RECORD+statusCd);
            }

            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                throw new DataProcessingException(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
            }

            else
            {
                assocDTInterface.setAddUserId(null);
                assocDTInterface.setAddTime(null);
                java.util.Date dateTime = new java.util.Date();
                Timestamp systemTime = new Timestamp(dateTime.getTime());
                assocDTInterface.setRecordStatusTime(systemTime);
                assocDTInterface.setStatusTime(systemTime);
                assocDTInterface.setLastChgTime(systemTime);
                assocDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                assocDTInterface.setLastChgReasonCd(null);
                aDTInterface = assocDTInterface;
            }
            if(!isRealDirty) {
                aDTInterface.setItDirty(false);//Re-set the flag to original value if necessary
            }
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * This method is used to prepare Dirty Acts,Dirty Entities,New Acts And New Entities depending
     * you want to edit,delete or create records
     */
    public RootDtoInterface prepareVO(RootDtoInterface theRootDTInterface, String businessObjLookupName,
                                String businessTriggerCd, String tableName, String moduleCd,
                                      Integer existingVersion) throws DataProcessingException
    {
            if(!theRootDTInterface.isItNew() && !theRootDTInterface.isItDirty() && !theRootDTInterface.isItDelete()) {
                throw new DataProcessingException("Error while calling prepareVO method in PrepareVOUtils");
            }
            if(theRootDTInterface.isItDirty() && !theRootDTInterface.isItNew())
            {
                // CONCURRENCE CHECK
                boolean result = concurrentCheck.dataConcurrenceCheck(theRootDTInterface, tableName, existingVersion);
                if(result)
                {
                    logger.debug("result in prepareVOUtil is :" + result);
                    //no concurrent dataAccess has occured, hence can continue!
                }
                else
                {
                    throw new DataProcessingException("NEDSSConcurrentDataException occurred in PrepareVOUtils.Person");
                }

            }

            if(theRootDTInterface.isItNew() && (theRootDTInterface.getSuperclass().equalsIgnoreCase(NEDSSConstant.CLASSTYPE_ACT)))
            {
                logger.debug("new act");
                theRootDTInterface = prepareNewActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
            }
            else if(theRootDTInterface.isItNew() && (theRootDTInterface.getSuperclass().equalsIgnoreCase(NEDSSConstant.CLASSTYPE_ENTITY)))
            {
                logger.debug("new entity");
                theRootDTInterface = prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
            }
            else if(theRootDTInterface.isItDirty() && (theRootDTInterface.getSuperclass().equalsIgnoreCase(NEDSSConstant.CLASSTYPE_ACT)))
            {
                logger.debug("dirty act");
                theRootDTInterface = prepareDirtyActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
            }
            else if(theRootDTInterface.isItDirty() && (theRootDTInterface.getSuperclass().equalsIgnoreCase(NEDSSConstant.CLASSTYPE_ENTITY)))
            {
                logger.debug("dirty entity");
                theRootDTInterface = prepareDirtyEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
            }
            return theRootDTInterface;
    }

    /**
     * This method prepares the Act value object if it is New(Create)
     * and check null for record Status State and set the System attributes in the rootDTInterface
     */
    protected RootDtoInterface prepareNewActVO(RootDtoInterface theRootDTInterface, String businessObjLookupName, String businessTriggerCd, String tableName, String moduleCd)
            throws DataProcessingException
    {
        try
        {
            Long uid = theRootDTInterface.getUid();
            logger.debug("prepareNewActVO uid = " + uid);

            PrepareEntity prepareVOUtilsHelper = prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);
            String localId = prepareVOUtilsHelper.getLocalId();//7
            Long addUserId =prepareVOUtilsHelper.getAddUserId();//8
            Timestamp addUserTime = prepareVOUtilsHelper.getAddUserTime();//9
            String recordStatusState = prepareVOUtilsHelper.getRecordStatusState();//12
            String objectStatusState = prepareVOUtilsHelper.getObjectStatusState();//13
            if(recordStatusState==null)
            {
                throw new DataProcessingException("prepareNewActVO - recordStatusState = " + recordStatusState + "- objectStatusState = " + objectStatusState);
            }

            if(!(theRootDTInterface.getProgAreaCd()==null) && !(theRootDTInterface.getJurisdictionCd()==null))
            {
                String progAreaCd = theRootDTInterface.getProgAreaCd();
                String jurisdictionCd = theRootDTInterface.getJurisdictionCd();
                long pajHash = progAreaJurisdictionUtil.getPAJHash(progAreaCd, jurisdictionCd);
                Long aProgramJurisdictionOid = pajHash;
                logger.debug("aProgramJurisdictionOid is : " + aProgramJurisdictionOid);
                theRootDTInterface.setProgramJurisdictionOid(aProgramJurisdictionOid);
                logger.debug("aProgramJurisdictionOid from obj  is : " + theRootDTInterface.getProgramJurisdictionOid());

            }

            theRootDTInterface.setLocalId(null);

            theRootDTInterface.setRecordStatusCd(recordStatusState);

            java.util.Date dateTime = new java.util.Date();
            Timestamp systemTime = new Timestamp(dateTime.getTime());
            theRootDTInterface.setRecordStatusTime(systemTime);
            theRootDTInterface.setLastChgTime(systemTime);
            theRootDTInterface.setAddTime(systemTime);
            theRootDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
            theRootDTInterface.setAddUserId(AuthUtil.authUser.getNedssEntryId());
            theRootDTInterface.setLastChgReasonCd(null);

            return theRootDTInterface;
        }
        catch(Exception e)
        {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }



    /**
     * This method prepares the Entity value object if it is New(Create)
     */
    protected RootDtoInterface prepareNewEntityVO(RootDtoInterface theRootDTInterface, String businessObjLookupName,
                                                String businessTriggerCd, String tableName, String moduleCd)
            throws DataProcessingException
    {
        try
        {
            logger.debug("prepareNewEntityVO uid = " + theRootDTInterface.getUid());
            Long uid = theRootDTInterface.getUid();
            logger.debug("prepareDirtyEntityVO uid = " + uid);

            PrepareEntity prepareVOUtilsHelper = prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);
            String localId = prepareVOUtilsHelper.getLocalId();//7
            Long addUserId =prepareVOUtilsHelper.getAddUserId();//8
            Timestamp addUserTime = prepareVOUtilsHelper.getAddUserTime();//9
            String recordStatusState = prepareVOUtilsHelper.getRecordStatusState();//12
            String objectStatusState = prepareVOUtilsHelper.getObjectStatusState();//13
            //We decided to set the status_cd and status_time also for entities 08/01/2005
            if(recordStatusState==null ||objectStatusState==null)
            {
                throw new DataProcessingException(LOG_RECORD_MODIFIED_BY_OTHER_USER);
            }

            logger.debug("recordStatusState state in prepareDirtyEntityVO = " + recordStatusState);
            logger.debug("objectStatusState state in prepareDirtyEntityVO = " + objectStatusState);
            java.util.Date dateTime = new java.util.Date();
            Timestamp systemTime = new Timestamp(dateTime.getTime());
            theRootDTInterface.setLocalId(localId);
            theRootDTInterface.setAddUserId(AuthUtil.authUser.getNedssEntryId());
            theRootDTInterface.setAddTime(systemTime);
            theRootDTInterface.setRecordStatusCd(recordStatusState);
            theRootDTInterface.setStatusCd(objectStatusState);
            theRootDTInterface.setRecordStatusTime(systemTime);
            theRootDTInterface.setStatusTime(systemTime);
            theRootDTInterface.setLastChgTime(systemTime);
            theRootDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
            theRootDTInterface.setLastChgReasonCd(null);

            if(tableName.equals(NEDSSConstant.PATIENT) && (!businessTriggerCd.equals(PAT_NO_MERGER)))
            {
                if(theRootDTInterface instanceof PersonDto)
                {
                    ((PersonDto)theRootDTInterface).setDedupMatchInd(null);
                    ((PersonDto)theRootDTInterface).setGroupNbr(null);
                    ((PersonDto)theRootDTInterface).setGroupTime(null);
                }
            }

            if(tableName.equals(NEDSSConstant.PATIENT) && businessTriggerCd.equals(PAT_NO_MERGER))
            {
                if(theRootDTInterface instanceof PersonDto)
                {
                    ((PersonDto)theRootDTInterface).setGroupNbr(null);
                    ((PersonDto)theRootDTInterface).setGroupTime(null);
                }
            }

            return theRootDTInterface;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * This method prepares the Act value object if it is Dirty(Edit,update or Delete)
     * and check null for record Status State and set the System attribures in the rootDTInterface
     */
    protected RootDtoInterface prepareDirtyActVO(RootDtoInterface theRootDTInterface,
                                              String businessObjLookupName, String businessTriggerCd, String tableName,
                                              String moduleCd)
            throws DataProcessingException


    {
        try
        {
            Long uid = theRootDTInterface.getUid();

            PrepareEntity prepareVOUtilsHelper = prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);
            String localId = prepareVOUtilsHelper.getLocalId();//7

            Long addUserId =prepareVOUtilsHelper.getAddUserId();//8
            Timestamp addUserTime = prepareVOUtilsHelper.getAddUserTime();//9
            String recordStatusState = prepareVOUtilsHelper.getRecordStatusState();//12
            String objectStatusState = prepareVOUtilsHelper.getObjectStatusState();//13

            if(recordStatusState==null)
            {
                throw new DataProcessingException(LOG_RECORD_MODIFIED_BY_OTHER_USER);
            }


            if(!(theRootDTInterface.getProgAreaCd()==null) && !(theRootDTInterface.getJurisdictionCd()==null))
            {
                String progAreaCd = theRootDTInterface.getProgAreaCd();
                String jurisdictionCd = theRootDTInterface.getJurisdictionCd();

                long pajHash = progAreaJurisdictionUtil.getPAJHash(progAreaCd, jurisdictionCd);
                Long aProgramJurisdictionOid = pajHash;
                theRootDTInterface.setProgramJurisdictionOid(aProgramJurisdictionOid);
            }

            theRootDTInterface.setAddUserId(addUserId);
            theRootDTInterface.setAddTime(addUserTime);

            theRootDTInterface.setRecordStatusCd(recordStatusState);

            java.util.Date dateTime = new java.util.Date();
            Timestamp systemTime = new Timestamp(dateTime.getTime());
            theRootDTInterface.setRecordStatusTime(systemTime);

            theRootDTInterface.setLastChgTime(systemTime);
            theRootDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
            theRootDTInterface.setLastChgReasonCd(null);
            return theRootDTInterface;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


    /**
     * This method prepares the Entity value object if it is Dirty(Edit,update or Delete)
     * and check null for record Status State and set the System attribures in the rootDTInterface
     */
    protected RootDtoInterface prepareDirtyEntityVO(RootDtoInterface theRootDTInterface,
                                                 String businessObjLookupName, String businessTriggerCd,
                                                 String tableName, String moduleCd)
            throws DataProcessingException
    {
        try
        {

            Long uid = theRootDTInterface.getUid();

            PrepareEntity prepareVOUtilsHelper = prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);
            String localId = prepareVOUtilsHelper.getLocalId();//7
            Long addUserId =prepareVOUtilsHelper.getAddUserId();//8
            Timestamp addUserTime = prepareVOUtilsHelper.getAddUserTime();//9
            String recordStatusState = prepareVOUtilsHelper.getRecordStatusState();//12
            String objectStatusState = prepareVOUtilsHelper.getObjectStatusState();//13
            if(recordStatusState==null ||objectStatusState==null)
            {
                throw new DataProcessingException(LOG_RECORD_MODIFIED_BY_OTHER_USER);
            }

            java.util.Date dateTime = new java.util.Date();
            Timestamp systemTime = new Timestamp(dateTime.getTime());
            theRootDTInterface.setLocalId(localId);
            theRootDTInterface.setAddUserId(addUserId);
            theRootDTInterface.setAddTime(addUserTime);
            theRootDTInterface.setRecordStatusCd(recordStatusState);
            theRootDTInterface.setStatusCd(objectStatusState);
            theRootDTInterface.setRecordStatusTime(systemTime);
            theRootDTInterface.setStatusTime(systemTime);
            theRootDTInterface.setLastChgTime(systemTime);
            theRootDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
            theRootDTInterface.setLastChgReasonCd(null);

            if(tableName.equals(NEDSSConstant.PATIENT) && (!businessTriggerCd.equals(PAT_NO_MERGER)))
            {
                if(theRootDTInterface instanceof PersonDto)
                {
                    ((PersonDto)theRootDTInterface).setDedupMatchInd(null);
                    ((PersonDto)theRootDTInterface).setGroupNbr(null);
                    ((PersonDto)theRootDTInterface).setGroupTime(null);
                }
            }

            if(tableName.equals(NEDSSConstant.PATIENT) && businessTriggerCd.equals(PAT_NO_MERGER))
            {
                if(theRootDTInterface instanceof PersonDto)
                {
                    ((PersonDto)theRootDTInterface).setGroupNbr(null);
                    ((PersonDto)theRootDTInterface).setGroupTime(null);
                }
            }

            return theRootDTInterface;
        }
        catch(Exception e)
        {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public ActivityLocatorParticipationDto prepareAssocDTForActivityLocatorParticipation(ActivityLocatorParticipationDto assocDTInterface)
            throws DataProcessingException {
        try {
            ActivityLocatorParticipationDto aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                logger.debug(LOG_RECORD_STATUS_NULL);
                throw new DataProcessingException(LOG_RECORD_2+recStatusCd+LOG_RECORD+statusCd);
            }

            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                logger.debug(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
                throw new DataProcessingException(LOG_RECORD_STATUS_CD_NOT_ACTIVE);
            }

            else
            {
                try
                {

                    logger.debug(LOG_RECORD_STATUS_CD_NOT_NULL);
                    assocDTInterface.setAddUserId(null);
                    assocDTInterface.setAddTime(null);
                    java.util.Date dateTime = new java.util.Date();
                    Timestamp systemTime = new Timestamp(dateTime.getTime());
                    assocDTInterface.setRecordStatusTime(systemTime);
                    assocDTInterface.setStatusTime(systemTime);
                    assocDTInterface.setLastChgTime(systemTime);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                assocDTInterface.setLastChgUserId(AuthUtil.authUser.getNedssEntryId());
                assocDTInterface.setLastChgReasonCd(null);
                aDTInterface = assocDTInterface;
                logger.debug(LOG_DT_PREPARED);
            }
            if(!isRealDirty) aDTInterface.setItDirty(false);//Re-set the flag to original value if necessary
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }


}
