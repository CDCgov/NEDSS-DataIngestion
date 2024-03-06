package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class PrepareAssocModelHelper {
    private static final Logger logger = LoggerFactory.getLogger(PrepareAssocModelHelper.class);


    //TODO: EVALUATE
    /**
     * This method is used to prepare Dirty Acts,Dirty Entities,New Acts And New Entities depending
     * you want to edit,delete or create records
     * @param theRootDTInterface -- The DT to be prepared
     * @param businessObjLookupName
     * @param businessTriggerCd
     * @param tableName
     * @param moduleCd
     * @param securityObj
     * @return RootDTInterface -- the prepared DT(System attribute Set)
     * @throws NEDSSSystemException
     * @throws NEDSSConcurrentDataException
     * @roseuid 3C7422C50093
     */
    public AbstractVO prepareVO(
            AbstractVO theRootDTInterface, String businessObjLookupName,
            String businessTriggerCd, String tableName,
            String moduleCd) throws DataProcessingException {
        try
        {
            if(!theRootDTInterface.isItNew() && !theRootDTInterface.isItDirty() && !theRootDTInterface.isItDelete()) {
                throw new DataProcessingException("Error while calling prepareVO method in PrepareVOUtils");
            }
            //Boolean testNewForRootDTInterface = theRootDTInterface.isItNew();
//            if(theRootDTInterface.isItDirty() && (Boolean.FALSE).equals(theRootDTInterface.isItNew()))
//            {
//                boolean result = dataConcurrenceCheck(theRootDTInterface, tableName, nbsSecurityObj);
//                if(result)
//                {
//                    //no concurrent dataAccess has occured, hence can continue!
//                }
//                else
//                {
//                    throw new DataProcessingException("NEDSSConcurrentDataException occurred in PrepareVOUtils.Person");
//                }
//            }

//            if(theRootDTInterface.isItNew() && (theRootDTInterface.getSuperclass().toUpperCase().equals("ACT")))
//            {
//                theRootDTInterface = this.prepareNewActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
//            }
//            else if(theRootDTInterface.isItNew() && (theRootDTInterface.getSuperclass().toUpperCase().equals("ENTITY")))
//            {
//                theRootDTInterface = this.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
//            }
//            else if(theRootDTInterface.isItDirty() && (theRootDTInterface.getSuperclass().toUpperCase().equals("ACT")))
//            {
//                theRootDTInterface = this.prepareDirtyActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
//            }
//            else if(theRootDTInterface.isItDirty() && (theRootDTInterface.getSuperclass().toUpperCase().equals("ENTITY")))
//            {
//                theRootDTInterface = this.prepareDirtyEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd);
//            }
            return theRootDTInterface;
        }
        catch(Exception ex)
        {
            throw new DataProcessingException(ex.getMessage(), ex);
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
            // TODO Auto-generated catch block
            logger.error("Exception in PrepareVOUtils.prepareAssocDT: RecordStatusCd: " + assocDTInterface.getRecordStatusCd() +  ", " + e.getMessage(), e);
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
            // TODO Auto-generated catch block
            logger.error("Exception in PrepareVOUtils.prepareAssocDT: RecordStatusCd: " + assocDTInterface.getRecordStatusCd() +  ", " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    public ParticipationDT prepareAssocDTForParticipation(ParticipationDT assocDTInterface) throws DataProcessingException {
        try {
            ParticipationDT aDTInterface = null;
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
            // TODO Auto-generated catch block
            logger.error("Exception in PrepareVOUtils.prepareAssocDT: RecordStatusCd: " + assocDTInterface.getRecordStatusCd() +  ", " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

}
