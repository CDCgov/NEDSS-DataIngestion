package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.PersonDT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PreparingPersonUtil {
    private static final Logger logger = LoggerFactory.getLogger(PreparingPersonUtil.class);

    public PersonDT prepareVO(
            PersonDT theRootDTInterface,
            String businessObjLookupName,
            String businessTriggerCd,
            String tableName, String moduleCd ) throws DataProcessingException {
        try
        {
            if(theRootDTInterface.isItNew() == false  && theRootDTInterface.isItDirty() == false && theRootDTInterface.isItDelete() == false) {
                throw new DataProcessingException("Error while calling prepareVO method in PrepareVOUtils");
            }
            logger.debug("(Boolean.FALSE).equals(new Boolean(theRootDTInterface.tableName)?:" + tableName +":theRootDTInterface.moduleCd:" +moduleCd +":businessTriggerCd:"+businessTriggerCd);

            // TODO: This is concurrent check, back to when finish
            /*
            if(theRootDTInterface.isItDirty() && (Boolean.FALSE).equals(new Boolean(theRootDTInterface.isItNew())))
            {

                logger.debug("!test1. theRootDTInterface isItNEW?:" + !theRootDTInterface.isItNew() +":theRootDTInterface.IsItDirty:" +!theRootDTInterface.isItDirty() );
                boolean result = dataConcurrenceCheck(theRootDTInterface, tableName, nbsSecurityObj);
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
            */

            //TODO: PERSON probably wont hit these, but need to check with legacy

            /*

            if(theRootDTInterface.isItNew() && (theRootDTInterface.getSuperclass().toUpperCase().equals("ACT")))
            {
                logger.debug("new act");
                theRootDTInterface = this.prepareNewActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd, nbsSecurityObj);
            }
            else if(theRootDTInterface.isItNew() && (theRootDTInterface.getSuperclass().toUpperCase().equals("ENTITY")))
            {
                logger.debug("new entity");
                theRootDTInterface = this.prepareNewEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd, nbsSecurityObj);
            }
            else if(theRootDTInterface.isItDirty() && (theRootDTInterface.getSuperclass().toUpperCase().equals("ACT")))
            {
                logger.debug("dirty act");
                theRootDTInterface = this.prepareDirtyActVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd, nbsSecurityObj);
            }
            else if(theRootDTInterface.isItDirty() && (theRootDTInterface.getSuperclass().toUpperCase().equals("ENTITY")))
            {
                logger.debug("dirty entity");
                theRootDTInterface = this.prepareDirtyEntityVO(theRootDTInterface, businessObjLookupName, businessTriggerCd, tableName, moduleCd, nbsSecurityObj);
            }
            */

            return theRootDTInterface;
        }
        catch(Exception e)
        {
            logger.info("Exception in PrepareVOUtils.prepareVO: LocalID: " + theRootDTInterface.getLocalId() +  ", businessTriggerCd: " + businessTriggerCd + ", tableName: " + tableName + ", " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

}
