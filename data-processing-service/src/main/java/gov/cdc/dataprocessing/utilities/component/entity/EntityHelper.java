package gov.cdc.dataprocessing.utilities.component.entity;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EntityLocatorParticipationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model.dto.RoleDT;
import gov.cdc.dataprocessing.utilities.component.PrepareAssocModelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Component
public class EntityHelper {
    private static final Logger logger = LoggerFactory.getLogger(EntityHelper.class);
    private  final PrepareAssocModelHelper prepareAssocModel;

    public EntityHelper(PrepareAssocModelHelper prepareAssocModel) {
        this.prepareAssocModel = prepareAssocModel;
    }

    /**
     * This private method is used to populate the system attributes on the
     * EntityLocatoryParticipation Collection<Object>
     *
     * @param dtCol
     *            Collection<Object>
     *            NBSSecurityObj object
     * @return Collection<Object> EntityLocatoryParticipation collection object
     *         populated with system attributes
     */
    public Collection<EntityLocatorParticipationDT> iterateELPDTForEntityLocatorParticipation(Collection<EntityLocatorParticipationDT> dtCol) throws DataProcessingException {
        Collection<EntityLocatorParticipationDT> retCol = new ArrayList<>();
        Collection<EntityLocatorParticipationDT> collection = new ArrayList<>();
        Iterator<EntityLocatorParticipationDT> anIterator = null;
        collection = dtCol;
        logger.debug("Collection<Object> size before iteration in iterateELPDT " + collection.size());
        try {
            for (anIterator = collection.iterator(); anIterator.hasNext(); ) {
                EntityLocatorParticipationDT elpDT = (EntityLocatorParticipationDT) anIterator.next();
                EntityLocatorParticipationDT assocDTInterface = elpDT;
                logger.debug("Iterating EntityLocatorParticipationDT");
                elpDT = (EntityLocatorParticipationDT) prepareAssocModel.prepareAssocDTForEntityLocatorParticipation(assocDTInterface);
                logger.debug("Came back from PrepareVOUtils");
                retCol.add(elpDT);
            }
        } catch (Exception e) {
            logger.error("EntityControllerEJB.iterateELPDT: " + e.getMessage(), e);
            throw new DataProcessingException(e.getMessage(), e);
        }
        logger.debug("Collection<Object> size after iteration in iterateELPDT " + retCol.size());
        return retCol;

    }


    /**
     * This private method is used to populate the system attributes on the
     * RoleDT Collection<Object>
     *
     * @param dtCol
     *            Collection<Object>
     *            NBSSecurityObj object
     * @return Collection<Object> RoleDT collection object populated with system
     *         attributes
     */
    public Collection<RoleDT> iterateRDT(Collection<RoleDT> dtCol) throws DataProcessingException {
        Collection<RoleDT> retCol = new ArrayList<>();
        Collection<RoleDT> collection = new ArrayList<>();
        Iterator<RoleDT> anIterator = null;
        collection = dtCol;
        logger.debug("Collection<Object> size before iteration in iterateRDT " + collection.size());
        if (collection != null) {
            try {
                for (anIterator = collection.iterator(); anIterator.hasNext();) {
                    RoleDT rDT = (RoleDT) anIterator.next();
                    if (rDT.isItDirty() || rDT.isItNew() || rDT.isItDelete()) {
                        logger.debug("EntityController:rdT.IsItDelete"
                                + rDT.isItDelete() + "rdt.IsItNew:"
                                + rDT.isItNew() + "rdt.IsItDirty:"
                                + rDT.isItDirty());
                        RoleDT assocDTInterface = rDT;
                        rDT = (RoleDT) prepareAssocModel.prepareAssocDTForRole(assocDTInterface);
                        retCol.add(rDT);
                    }
                }
            } catch (Exception e) {
                logger.error("EntityControllerEJB.iterateRDT: " + e.getMessage(), e);
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        logger.debug("Collection<Object> size after iteration in iterateRDT " + retCol.size());
        return retCol;

    }



    /**
     * This private method is used to populate the system attributes on the
     * ParticipationDT Collection<Object>
     *
     * @param dtCol
     *            Collection<Object>
     * @param securityObj
     *            NBSSecurityObj object
     * @return Collection<Object> ParticipationDT collection object populated
     *         with system attributes
     */

    public Collection<ParticipationDT> iteratePDTForParticipation(Collection<ParticipationDT> dtCol) throws DataProcessingException {
        Collection<ParticipationDT> retCol = new ArrayList<>();
        Collection<ParticipationDT> collection = new ArrayList<>();
        Iterator<ParticipationDT> anIterator = null;
        collection = dtCol;
        logger.debug("Collection<Object> size before iteration in iteratePDT " + collection.size());
        if (collection != null) {
            try {
                for (anIterator = collection.iterator(); anIterator.hasNext();) {
                    ParticipationDT pDT = (ParticipationDT) anIterator.next();
                    if (pDT.isItDirty() || pDT.isItNew() || pDT.isItDelete()) {
                        logger.debug("EntityController:pdT.IsItDelete"
                                + pDT.isItDelete() + "pdt.IsItNew:"
                                + pDT.isItNew() + "pdt.IsItDirty:"
                                + pDT.isItDirty());
                        ParticipationDT assocDTInterface = pDT;
                        pDT = prepareAssocModel.prepareAssocDTForParticipation(assocDTInterface);
                        retCol.add(pDT);
                    }
                }
            } catch (Exception e) {
                logger.error("EntityControllerEJB.iteratePDT: " + e.getMessage(), e);
                throw new DataProcessingException(e.getMessage(), e);
            }
        }
        logger.debug("Collection<Object> size after iteration in iteratePDT "
                + retCol.size());
        return retCol;
    }

}
