package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.RootDtoInterface;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActivityLocatorParticipationDT;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PrepareEntityStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.PrepareEntity;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class PrepareAssocModelHelper {
    private static final Logger logger = LoggerFactory.getLogger(PrepareAssocModelHelper.class);

    private final PrepareEntityStoredProcRepository prepareEntityStoredProcRepository;

    public PrepareAssocModelHelper(PrepareEntityStoredProcRepository prepareEntityStoredProcRepository) {
        this.prepareEntityStoredProcRepository = prepareEntityStoredProcRepository;
    }

    /**
     * This method is used to populate the system attributes on the 5 association
     * tables (ActRelationship, Participation, Role, EntityLocatoryParticipation, and
     * ActLocatorParticipation).
     *
     * @param assocDTInterface
     * @return AssocDTInterface
     * @roseuid 3CD96F960027
     */
    public EntityLocatorParticipationDto prepareAssocDTForEntityLocatorParticipation(EntityLocatorParticipationDto assocDTInterface) throws DataProcessingException {
        try {
            EntityLocatorParticipationDto aDTInterface = null;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            logger.debug("AssocDTInterface.Statuscode = "+statusCd);
            logger.debug("AssocDTInterface.recStatusCd = "+recStatusCd);
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                logger.debug("RecordStatusCd is null");
                throw new DataProcessingException("RecordStatusCd -----2----"+recStatusCd+"   statusCode--------"+statusCd);
            }

            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                logger.debug("RecordStatusCd is not active or inactive");
                throw new DataProcessingException("RecordStatusCd is not active or inactive");
            }
            else
            {
                try
                {

                    logger.debug("RecordStatusCd or statusCode is not null");
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
                //TODO: Looking into this, this basically getting Permission ID
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
                logger.debug("DT Prepared");
            }
            if(!isRealDirty) {
                aDTInterface.setItDirty(false);//Re-set the flag to original value if necessary
            }
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public RoleDto prepareAssocDTForRole(RoleDto assocDTInterface) throws DataProcessingException {
        try {
            RoleDto aDTInterface = null;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            logger.debug("AssocDTInterface.Statuscode = "+statusCd);
            logger.debug("AssocDTInterface.recStatusCd = "+recStatusCd);
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                logger.debug("RecordStatusCd is null");
                throw new DataProcessingException("RecordStatusCd -----2----"+recStatusCd+"   statusCode--------"+statusCd);
            }

            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                logger.debug("RecordStatusCd is not active or inactive");
                throw new DataProcessingException("RecordStatusCd is not active or inactive");
            }
            else
            {
                try
                {

                    logger.debug("RecordStatusCd or statusCode is not null");
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
                //TODO: Looking into this, this basically getting Permission ID
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
                logger.debug("DT Prepared");
            }
            if(!isRealDirty) {
                aDTInterface.setItDirty(false);//Re-set the flag to original value if necessary
            }
            return aDTInterface;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public ParticipationDT prepareAssocDTForParticipation(ParticipationDT assocDTInterface) throws DataProcessingException {
        try {
            ParticipationDT aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                throw new DataProcessingException("RecordStatusCd -----2----"+recStatusCd+"   statusCode--------"+statusCd);
            }
            else if(
                    !(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE)
                    || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE))
            )
            {
                throw new DataProcessingException("RecordStatusCd is not active or inactive");
            }
            else
            {
                try
                {

                    logger.debug("RecordStatusCd or statusCode is not null");
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
                assocDTInterface.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
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
     *
     * @param assocDTInterface
     * @return AssocDTInterface
     * @roseuid 3CD96F960027
     */
    public ActivityLocatorParticipationDT prepareActivityLocatorParticipationDT(ActivityLocatorParticipationDT assocDTInterface) throws DataProcessingException
    {
        try {
            ActivityLocatorParticipationDT aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                throw new DataProcessingException("RecordStatusCd -----2----"+recStatusCd+"   statusCode--------"+statusCd);
            }
            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                throw new DataProcessingException("RecordStatusCd is not active or inactive");
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
                assocDTInterface.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
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

    public ActRelationshipDT prepareActRelationshipDT(ActRelationshipDT assocDTInterface) throws DataProcessingException {
        try {
            ActRelationshipDT aDTInterface;
            String recStatusCd = assocDTInterface.getRecordStatusCd();
            String statusCd = assocDTInterface.getStatusCd();
            boolean isRealDirty = assocDTInterface.isItDirty();

            if(recStatusCd == null)
            {
                throw new DataProcessingException("RecordStatusCd -----2----"+recStatusCd+"   statusCode--------"+statusCd);
            }

            else if(!(recStatusCd.equals(NEDSSConstant.RECORD_STATUS_ACTIVE) || recStatusCd.equals(NEDSSConstant.RECORD_STATUS_INACTIVE)))
            {
                throw new DataProcessingException("RecordStatusCd is not active or inactive");
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
                assocDTInterface.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
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
     * @param theRootDTInterface -- The DT to be prepared
     * @param businessObjLookupName
     * @param businessTriggerCd
     * @param tableName
     * @param moduleCd
     * @return RootDTInterface -- the prepared DT(System attribute Set)
     * @roseuid 3C7422C50093
     */
    public RootDtoInterface prepareVO(RootDtoInterface theRootDTInterface, String businessObjLookupName,
                                String businessTriggerCd, String tableName, String moduleCd) throws DataProcessingException
    {
            if(!theRootDTInterface.isItNew() && !theRootDTInterface.isItDirty() && !theRootDTInterface.isItDelete()) {
                throw new DataProcessingException("Error while calling prepareVO method in PrepareVOUtils");
            }
            if(theRootDTInterface.isItDirty() && !theRootDTInterface.isItNew())
            {
                //TODO: EVALUATE
                // CONCURRENCE CHECK
//                boolean result = dataConcurrenceCheck(theRootDTInterface, tableName);
//                if(result)
//                {
//                    logger.debug("result in prepareVOUtil is :" + result);
//                    //no concurrent dataAccess has occured, hence can continue!
//                }
//                else
//                {
//                }
                throw new DataProcessingException("NEDSSConcurrentDataException occurred in PrepareVOUtils.Person");

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
     * @param theRootDTInterface
     * @param businessObjLookupName
     * @param businessTriggerCd
     * @param tableName
     * @param moduleCd
     * @return RootDTInterface
     */
    private RootDtoInterface prepareNewActVO(RootDtoInterface theRootDTInterface, String businessObjLookupName, String businessTriggerCd, String tableName, String moduleCd)
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
                //TODO EVALUATE
                // PROGRAM AREA
                // long pajHash = ProgramAreaJurisdictionUtil.getPAJHash(progAreaCd, jurisdictionCd);
                Long aProgramJurisdictionOid = -1L;
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
            theRootDTInterface.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
            theRootDTInterface.setAddUserId(AuthUtil.authUser.getAuthUserUid());
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
     *
     * @param theRootDTInterface -- The DT that needs to be prepared
     * @param businessObjLookupName
     * @param businessTriggerCd
     * @param tableName
     * @param moduleCd
     * @return RootDTInterface -- represents the DT whose system attribute needs to be set
     * @roseuid 3C7D8D0E0172
     */
    private RootDtoInterface prepareNewEntityVO(RootDtoInterface theRootDTInterface, String businessObjLookupName,
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
                throw new DataProcessingException("NEDSSConcurrentDataException: The data has been modified by other user, please verify!");
            }

            logger.debug("recordStatusState state in prepareDirtyEntityVO = " + recordStatusState);
            logger.debug("objectStatusState state in prepareDirtyEntityVO = " + objectStatusState);
            java.util.Date dateTime = new java.util.Date();
            Timestamp systemTime = new Timestamp(dateTime.getTime());
            theRootDTInterface.setLocalId(localId);
            theRootDTInterface.setAddUserId(AuthUtil.authUser.getAuthUserUid());
            theRootDTInterface.setAddTime(systemTime);
            theRootDTInterface.setRecordStatusCd(recordStatusState);
            theRootDTInterface.setStatusCd(objectStatusState);
            theRootDTInterface.setRecordStatusTime(systemTime);
            theRootDTInterface.setStatusTime(systemTime);
            theRootDTInterface.setLastChgTime(systemTime);
            theRootDTInterface.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
            theRootDTInterface.setLastChgReasonCd(null);

            if(tableName.equals(NEDSSConstant.PATIENT) && (!businessTriggerCd.equals("PAT_NO_MERGE")))
            {
                if(theRootDTInterface instanceof PersonDto)
                {
                    ((PersonDto)theRootDTInterface).setDedupMatchInd(null);
                    ((PersonDto)theRootDTInterface).setGroupNbr(null);
                    ((PersonDto)theRootDTInterface).setGroupTime(null);
                }
            }

            if(tableName.equals(NEDSSConstant.PATIENT) && businessTriggerCd.equals("PAT_NO_MERGE"))
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
     * @param theRootDTInterface -- The DT that needs to be prepared
     * @param businessObjLookupName
     * @param businessTriggerCd
     * @param tableName
     * @param moduleCd
     * @return RootDTInterface -- represents the DT whose system attribute needs to be set
     * @roseuid 3C6BC0B70278
     */
    private RootDtoInterface prepareDirtyActVO(RootDtoInterface theRootDTInterface,
                                              String businessObjLookupName, String businessTriggerCd, String tableName,
                                              String moduleCd)
            throws DataProcessingException


    {
        try
        {
            Long uid = theRootDTInterface.getUid();
            logger.debug("prepareDirtyActVO uid = " + uid);


            logger.debug("businessTriggerCd in prepareDirtyActVO in prepateVOUtil is :"+businessTriggerCd);
            logger.debug("moduleCd in prepareDirtyActVO in prepateVOUtil is :"+moduleCd);
            logger.debug("uid in prepareDirtyActVO in prepateVOUtil is :"+uid);
            logger.debug("tableName in prepareDirtyActVO in prepateVOUtil is :"+tableName);

            PrepareEntity prepareVOUtilsHelper = prepareEntityStoredProcRepository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);
            String localId = prepareVOUtilsHelper.getLocalId();//7

            Long addUserId =prepareVOUtilsHelper.getAddUserId();//8
            Timestamp addUserTime = prepareVOUtilsHelper.getAddUserTime();//9
            String recordStatusState = prepareVOUtilsHelper.getRecordStatusState();//12
            String objectStatusState = prepareVOUtilsHelper.getObjectStatusState();//13

            if(recordStatusState==null)
            {
                throw new DataProcessingException("NEDSSConcurrentDataException: The data has been modified by other user, please verify!");
            }


            if(!(theRootDTInterface.getProgAreaCd()==null) && !(theRootDTInterface.getJurisdictionCd()==null))
            {
                String progAreaCd = theRootDTInterface.getProgAreaCd();
                String jurisdictionCd = theRootDTInterface.getJurisdictionCd();

                //TODO EVALUATE
                // PROGRAM AREA
                //long pajHash = ProgramAreaJurisdictionUtil.getPAJHash(progAreaCd, jurisdictionCd);
                Long aProgramJurisdictionOid = -1L;
                theRootDTInterface.setProgramJurisdictionOid(aProgramJurisdictionOid);
            }

            theRootDTInterface.setAddUserId(addUserId);
            theRootDTInterface.setAddTime(addUserTime);

            theRootDTInterface.setRecordStatusCd(recordStatusState);

            java.util.Date dateTime = new java.util.Date();
            Timestamp systemTime = new Timestamp(dateTime.getTime());
            theRootDTInterface.setRecordStatusTime(systemTime);

            theRootDTInterface.setLastChgTime(systemTime);
            theRootDTInterface.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
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
     * @param theRootDTInterface -- The DT that needs to be prepared
     * @param businessObjLookupName
     * @param businessTriggerCd
     * @param tableName
     * @param moduleCd
     * @return RootDTInterface -- represents the DT whose system attribute needs to be set
     * @roseuid 3C7D8D0202C0
     */
    private RootDtoInterface prepareDirtyEntityVO(RootDtoInterface theRootDTInterface,
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
                throw new DataProcessingException("NEDSSConcurrentDataException: The data has been modified by other user, please verify!");
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
            theRootDTInterface.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
            theRootDTInterface.setLastChgReasonCd(null);

            if(tableName.equals(NEDSSConstant.PATIENT) && (!businessTriggerCd.equals("PAT_NO_MERGE")))
            {
                if(theRootDTInterface instanceof PersonDto)
                {
                    ((PersonDto)theRootDTInterface).setDedupMatchInd(null);
                    ((PersonDto)theRootDTInterface).setGroupNbr(null);
                    ((PersonDto)theRootDTInterface).setGroupTime(null);
                }
            }

            if(tableName.equals(NEDSSConstant.PATIENT) && businessTriggerCd.equals("PAT_NO_MERGE"))
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



}
